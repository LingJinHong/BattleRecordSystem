const request = require('../utils/request');

function createTeam(data) {
  return request({ url: '/api/user-team', method: 'POST', data });
}

function getTeamList(params = {}) {
  const query = Object.keys(params)
    .filter((k) => params[k] !== undefined && params[k] !== '')
    .map((k) => `${k}=${encodeURIComponent(params[k])}`)
    .join('&');
  return request({ url: `/api/user-team${query ? `?${query}` : ''}` });
}

function getTeamUsers(teamId) {
  return request({ url: `/api/team-user/by-team?teamId=${teamId}` });
}

function addTeamUser(data) {
  return request({ url: '/api/team-user/add', method: 'POST', data });
}

module.exports = {
  createTeam,
  getTeamList,
  getTeamUsers,
  addTeamUser
};
