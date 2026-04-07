const request = require('../utils/request');

function getBattleList(params = {}) {
  const query = Object.keys(params)
    .filter((k) => params[k] !== undefined && params[k] !== '')
    .map((k) => `${k}=${encodeURIComponent(params[k])}`)
    .join('&');
  return request({ url: `/api/user-battle${query ? `?${query}` : ''}` });
}

function getBattleDetail(id) {
  return request({ url: `/api/user-battle/${id}` });
}

module.exports = {
  getBattleList,
  getBattleDetail
};
