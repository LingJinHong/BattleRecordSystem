const request = require('../utils/request');

function addFriend(data) {
  return request({ url: '/api/friend-user/add', method: 'POST', data });
}

function getFriendList(userId) {
  return request({ url: `/api/friend-user/by-user?userId=${userId}` });
}

module.exports = {
  addFriend,
  getFriendList
};
