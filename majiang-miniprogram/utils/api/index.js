// 导入封装好的请求
import request from '../request'


// 用户模块接口
export const userApi = {
    // 登录
    login: (data) => request.get(`/api/user-data/by-code?code=${data}`),
    getOpenId: (data) => request.get(`/api/user-data/by-openid?openid=${data}`),
    addUser: (data) => request.post('/api/user-data', data),
    // 获取用户信息
    getUserInfo: () => request.get('/user/info'),
    // 修改用户信息
    updateUser: (data) => request.put('/user/info', data)
}


// 商品模块接口
export const goodsApi = {
    // 获取商品列表
    getGoodsList: (data) => request.get('/goods/list', data),
    // 获取商品详情
    getGoodsDetail: (id) => request.get(`/goods/detail/${id}`)
}