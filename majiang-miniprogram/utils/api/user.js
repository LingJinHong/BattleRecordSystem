const request = require('../request');

function getOpenidByCode(code) {
  return request({ url: `/api/user-data/by-code?code=${code}` });
}

function createUserData(data) {
  return request({ url: '/api/user-data', method: 'POST', data });
}

function getUserById(id) {
  return request({ url: `/api/user-data/${id}` });
}

function getUserByOpenid(openid) {
  return request({ url: `/api/user-data/openid/${openid}` });
}

function updateUser(id, data) {
  return request({ url: `/api/user-data/${id}`, method: 'PUT', data });
}

module.exports = {
  getOpenidByCode,
  createUserData,
  getUserById,
  getUserByOpenid,
  updateUser
};
