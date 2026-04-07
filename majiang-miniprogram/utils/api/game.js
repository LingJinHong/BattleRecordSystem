import request from '../request';
export const gameApi = {
    getGameDetail: (data) => request.get(`/api/games/${data}`),
    createGame: (data) => request.post('/api/games', data),
    getGameList: (data) => request.get('/api/games/list', data),
    settleGame: (data) => request.post(`/api/games/${data.gameId}/settlement`, data),
}