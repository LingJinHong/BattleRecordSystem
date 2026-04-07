package com.majiang.backend.service;

import com.majiang.backend.dto.CreateAppGameRequest;
import com.majiang.backend.dto.GameTransferDTO;
import com.majiang.backend.dto.SettleAppGameRequest;
import com.majiang.backend.entity.AppGame;

import java.util.List;

public interface AppGameService {
    AppGame createGame(CreateAppGameRequest dto, Long userId);
    List<GameTransferDTO> settleGame(SettleAppGameRequest dto);
    AppGame getGameById(Long id);
    List<AppGame> listGames();
}
