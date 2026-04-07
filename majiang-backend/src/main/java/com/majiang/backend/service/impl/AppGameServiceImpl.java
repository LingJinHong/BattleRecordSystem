package com.majiang.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResultCode;
import com.majiang.backend.dto.CreateAppGameRequest;
import com.majiang.backend.dto.GameTransferDTO;
import com.majiang.backend.dto.SettleAppGameRequest;
import com.majiang.backend.entity.AppGame;
import com.majiang.backend.entity.AppGamePlayer;
import com.majiang.backend.entity.AppUserBattle;
import com.majiang.backend.mapper.AppGameMapper;
import com.majiang.backend.mapper.AppGamePlayerMapper;
import com.majiang.backend.mapper.AppUserBattleMapper;
import com.majiang.backend.service.AppGameService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppGameServiceImpl implements AppGameService {

    private final AppGameMapper gameMapper;
    private final AppGamePlayerMapper gamePlayerMapper;
    private final AppUserBattleMapper battleMapper;

    @Override
    @Transactional
    public AppGame createGame(CreateAppGameRequest dto, Long userId) {
        if (dto.getTeamId() == null) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "teamId不能为空");
        }
        if (dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "对局玩家不能为空");
        }

        AppGame game = new AppGame();
        game.setTeamId(dto.getTeamId());
        game.setCreatorId(userId);
        game.setGameTime(LocalDateTime.now());

        gameMapper.insert(game);
        for (Long gameUserId : dto.getUserIds()) {
            AppGamePlayer gp = new AppGamePlayer();
            gp.setGameId(game.getId());
            gp.setUserId(gameUserId);
            gamePlayerMapper.insert(gp);
        }

        return game;
    }

    @Override
    public AppGame getGameById(Long id) {
        if (id == null) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "id不能为空");
        }
        AppGame game = gameMapper.selectById(id);
        if (game == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "对局不存在");
        }
        return game;
    }

    @Override
    public List<AppGame> listGames() {
        return gameMapper.selectList(new QueryWrapper<AppGame>().orderByDesc("id"));
    }

    @Override
    @Transactional
    public List<GameTransferDTO> settleGame(SettleAppGameRequest dto) {
        if (dto.getGameId() == null) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "gameId不能为空");
        }
        List<SettleAppGameRequest.PlayerAmountDTO> list = dto.getResults();
        if (list == null || list.isEmpty()) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "结算结果不能为空");
        }

        BigDecimal sum = list.stream()
                .map(SettleAppGameRequest.PlayerAmountDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(BigDecimal.ZERO) != 0) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "金额总和必须为0");
        }

        AppGame game = gameMapper.selectById(dto.getGameId());
        if (game == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "游戏不存在");
        }

        Long settledCount = battleMapper.selectCount(
                new LambdaQueryWrapper<AppUserBattle>()
                        .eq(AppUserBattle::getGameId, dto.getGameId())
        );
        if (settledCount != null && settledCount > 0) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "该对局已结算，请勿重复结算");
        }

        List<Player> winners = new ArrayList<>();
        List<Player> losers = new ArrayList<>();

        for (SettleAppGameRequest.PlayerAmountDTO item : list) {
            if (item.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                winners.add(new Player(item.getUserId(), item.getAmount()));
            } else if (item.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                losers.add(new Player(item.getUserId(), item.getAmount()));
            }
        }

        winners.sort((a, b) -> b.amount.compareTo(a.amount));
        losers.sort((a, b) -> a.amount.compareTo(b.amount));

        int i = 0;
        int j = 0;
        List<GameTransferDTO> transfers = new ArrayList<>();

        while (i < losers.size() && j < winners.size()) {
            Player loser = losers.get(i);
            Player winner = winners.get(j);

            BigDecimal pay = loser.amount.abs().min(winner.amount);
            transfers.add(new GameTransferDTO(loser.userId, winner.userId, pay));

            loser.amount = loser.amount.add(pay);
            winner.amount = winner.amount.subtract(pay);

            if (loser.amount.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            if (winner.amount.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        Long teamId = game.getTeamId();
        LocalDateTime now = LocalDateTime.now();
        for (SettleAppGameRequest.PlayerAmountDTO item : list) {
            AppUserBattle battle = new AppUserBattle();
            battle.setUserId(item.getUserId());
            battle.setIncomeExpenseType(item.getAmount().compareTo(BigDecimal.ZERO) > 0 ? "income" : "expense");
            battle.setScore(item.getAmount().intValue());
            battle.setTeamId(teamId);
            battle.setGameId(dto.getGameId());
            battle.setMatchDate(game.getGameTime().toLocalDate());
            battle.setCreateTime(now);
            battle.setVenueFee(dto.getVenueFee());
            battleMapper.insert(battle);
        }

        return transfers;
    }

    @Data
    @AllArgsConstructor
    static class Player {
        Long userId;
        BigDecimal amount;
    }
}
