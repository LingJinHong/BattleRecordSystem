// 基础配置
const {
    dev,
    prod
} = require('../config')
const BASE_URL = __wxConfig.envVersion === 'develop' ? dev.baseUrl : prod.baseUrl;
const TIMEOUT = 10000; // 超时时间10秒

// 请求封装
const request = (options) => {
    // 1. 拼接完整请求地址
    const url = `${BASE_URL}${options.url}`

    // 2. 请求头（自动携带token）
    const header = {
        'Content-Type': 'application/json',
        ...options.header
    }
    // 从本地缓存获取token并添加到请求头
    const token = wx.getStorageSync('token')
    if (token) {
        header.Authorization = `Bearer ${token}`
    }

    // 3. 加载提示（默认开启，可传参关闭）
    if (options.loading !== false) {
        wx.showLoading({
            title: '加载中...',
            mask: true
        })
    }

    // 4. 返回Promise
    return new Promise((resolve, reject) => {
        wx.request({
            url,
            method: options.method || 'GET',
            data: options.data || {},
            header,
            timeout: TIMEOUT,

            // 响应成功
            success: (res) => {
                // 关闭加载
                if (options.loading !== false) wx.hideLoading()

                const {
                    statusCode,
                    data
                } = res
                // HTTP状态码判断
                if (statusCode >= 200 && statusCode < 300) {
                    // 后端自定义业务状态码（根据你的接口修改）
                    if (data.code == 0) {
                        resolve(data)
                    } else {
                        // 业务错误统一提示
                        wx.showToast({
                            title: data.msg || '请求失败',
                            icon: 'none'
                        })
                        reject(data)
                    }
                } else {
                    // HTTP错误处理
                    wx.showToast({
                        title: res,
                        icon: 'none'
                    })
                    handleHttpError(statusCode)
                    reject(res)
                }
            },

            // 请求失败（网络错误/超时）
            fail: (err) => {
                if (options.loading !== false) wx.hideLoading()
                wx.showToast({
                    title: '网络异常，请重试',
                    icon: 'none'
                })
                reject(err)
            }
        })
    })
}

// HTTP状态码统一处理
const handleHttpError = (statusCode) => {
    let msg = '请求失败'
    switch (statusCode) {
        case 400:
            msg = '请求参数错误'
            break
        case 401:
            msg = '登录已过期，请重新登录'
            // 自动跳转到登录页
            wx.removeStorageSync('token')
            //   wx.navigateTo({ url: '/pages/login/login' })
            break
        case 403:
            msg = '无权限访问'
            break
        case 404:
            msg = '请求地址不存在'
            break
        case 500:
            msg = '服务器异常'
            break
    }
    wx.showToast({
        title: msg,
        icon: 'none'
    })
}

// 导出常用请求方法
export default {
    request,
    get: (url, data = {}, options = {}) => {
        return request({
            url,
            method: 'GET',
            data,
            ...options
        })
    },
    post: (url, data = {}, options = {}) => {
        return request({
            url,
            method: 'POST',
            data,
            ...options
        })
    },
    put: (url, data = {}, options = {}) => {
        return request({
            url,
            method: 'PUT',
            data,
            ...options
        })
    },
    delete: (url, data = {}, options = {}) => {
        return request({
            url,
            method: 'DELETE',
            data,
            ...options
        })
    }
}