import request from '../request'
export const battleApi = {
    getBattleList: (data) => request.get(`/api/user-battle${data ? `?${data}` : ''}`),
    getBattleDetail: (data) => request.get(`/api/user-battle/${data}`),
}