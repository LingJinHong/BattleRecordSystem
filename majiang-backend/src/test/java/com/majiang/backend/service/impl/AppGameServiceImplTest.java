package com.majiang.backend.service.impl;

import com.majiang.backend.common.ApiException;
import com.majiang.backend.dto.GameTransferDTO;
import com.majiang.backend.dto.SettleAppGameRequest;
import com.majiang.backend.entity.AppGame;
import com.majiang.backend.entity.AppUserBattle;
import com.majiang.backend.mapper.AppGameMapper;
import com.majiang.backend.mapper.AppGamePlayerMapper;
import com.majiang.backend.mapper.AppUserBattleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppGameServiceImplTest {

    @Mock
    private AppGameMapper gameMapper;

    @Mock
    private AppGamePlayerMapper gamePlayerMapper;

    @Mock
    private AppUserBattleMapper battleMapper;

    @InjectMocks
    private AppGameServiceImpl appGameService;

    private AppGame mockGame;

    @BeforeEach
    void setUp() {
        mockGame = new AppGame();
        mockGame.setId(100L);
        mockGame.setTeamId(10L);
        mockGame.setGameTime(LocalDateTime.of(2026, 4, 1, 20, 0));
    }

    @Test
    void settleGame_shouldReturnTransfersAndInsertBattles() {
        when(gameMapper.selectById(100L)).thenReturn(mockGame);

        SettleAppGameRequest request = new SettleAppGameRequest();
        request.setGameId(100L);
        request.setVenueFee(50);

        SettleAppGameRequest.PlayerAmountDTO p1 = new SettleAppGameRequest.PlayerAmountDTO();
        p1.setUserId(1L);
        p1.setAmount(new BigDecimal("100"));

        SettleAppGameRequest.PlayerAmountDTO p2 = new SettleAppGameRequest.PlayerAmountDTO();
        p2.setUserId(2L);
        p2.setAmount(new BigDecimal("-60"));

        SettleAppGameRequest.PlayerAmountDTO p3 = new SettleAppGameRequest.PlayerAmountDTO();
        p3.setUserId(3L);
        p3.setAmount(new BigDecimal("-40"));

        request.setResults(List.of(p1, p2, p3));

        List<GameTransferDTO> transfers = appGameService.settleGame(request);

        assertEquals(2, transfers.size());
        assertEquals(2L, transfers.get(0).getFromUserId());
        assertEquals(1L, transfers.get(0).getToUserId());
        assertEquals(new BigDecimal("60"), transfers.get(0).getAmount());

        assertEquals(3L, transfers.get(1).getFromUserId());
        assertEquals(1L, transfers.get(1).getToUserId());
        assertEquals(new BigDecimal("40"), transfers.get(1).getAmount());

        verify(battleMapper, times(3)).insert(any(AppUserBattle.class));
    }

    @Test
    void settleGame_shouldThrowWhenSumNotZero() {
        SettleAppGameRequest request = new SettleAppGameRequest();
        request.setGameId(100L);

        SettleAppGameRequest.PlayerAmountDTO p1 = new SettleAppGameRequest.PlayerAmountDTO();
        p1.setUserId(1L);
        p1.setAmount(new BigDecimal("100"));

        SettleAppGameRequest.PlayerAmountDTO p2 = new SettleAppGameRequest.PlayerAmountDTO();
        p2.setUserId(2L);
        p2.setAmount(new BigDecimal("-10"));

        request.setResults(List.of(p1, p2));

        assertThrows(ApiException.class, () -> appGameService.settleGame(request));
    }

    @Test
    void settleGame_shouldThrowWhenGameNotFound() {
        when(gameMapper.selectById(999L)).thenReturn(null);

        SettleAppGameRequest request = new SettleAppGameRequest();
        request.setGameId(999L);

        SettleAppGameRequest.PlayerAmountDTO p1 = new SettleAppGameRequest.PlayerAmountDTO();
        p1.setUserId(1L);
        p1.setAmount(new BigDecimal("50"));

        SettleAppGameRequest.PlayerAmountDTO p2 = new SettleAppGameRequest.PlayerAmountDTO();
        p2.setUserId(2L);
        p2.setAmount(new BigDecimal("-50"));

        request.setResults(List.of(p1, p2));

        assertThrows(ApiException.class, () -> appGameService.settleGame(request));
    }
}
