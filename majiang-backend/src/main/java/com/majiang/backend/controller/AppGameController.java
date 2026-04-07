package com.majiang.backend.controller;

import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.dto.CreateAppGameRequest;
import com.majiang.backend.dto.GameTransferDTO;
import com.majiang.backend.dto.SettleAppGameRequest;
import com.majiang.backend.entity.AppGame;
import com.majiang.backend.service.AppGameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/games", "/api/game"})
@RequiredArgsConstructor
public class AppGameController {

    private final AppGameService appGameService;

    /**
     * 创建一局新对局。
     * 兼容路由：POST /api/games 和 POST /api/game/create
     */
    @PostMapping({"", "/create"})
    public ApiResponse<AppGame> create(@RequestBody @Valid CreateAppGameRequest dto) {
        AppGame game = appGameService.createGame(dto, dto.getUserId());
        return ApiResponse.ok(game);
    }

    @GetMapping("/{id}")
    public ApiResponse<AppGame> getById(@PathVariable Long id) {
        return ApiResponse.ok(appGameService.getGameById(id));
    }

    @GetMapping("/list")
    public ApiResponse<List<AppGame>> list() {
        return ApiResponse.ok(appGameService.listGames());
    }

    /**
     * 结算一局对局并返回转账明细。
     * 推荐路由：POST /api/games/{gameId}/settlement
     * 兼容路由：POST /api/game/settle
     */
    @PostMapping({"/{gameId}/settlement", "/settle"})
    public ApiResponse<List<GameTransferDTO>> settle(
            @PathVariable(required = false) Long gameId,
            @RequestBody @Valid SettleAppGameRequest dto
    ) {
        if (gameId != null && dto.getGameId() == null) {
            dto.setGameId(gameId);
        }
        List<GameTransferDTO> transfers = appGameService.settleGame(dto);
        return ApiResponse.ok(transfers);
    }
}
