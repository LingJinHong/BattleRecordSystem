const request = require('../utils/request');

function createGame(data) {
  return request({ url: '/api/games', method: 'POST', data });
}

function getGameList() {
  return request({ url: '/api/games/list' });
}

function getGameDetail(id) {
  return request({ url: `/api/games/${id}` });
}

function settleGame(gameId, data) {
  return request({ url: `/api/games/${gameId}/settlement`, method: 'POST', data });
}

module.exports = {
  createGame,
  getGameList,
  getGameDetail,
  settleGame
};
