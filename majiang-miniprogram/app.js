const {
    dev,
    prod
} = require('./config')
App({
    globalData: {
        baseURL: __wxConfig.envVersion === 'develop' ? dev.baseUrl : prod.baseUrl,
        userInfo: null,
        userId: null,
        openid: ''
    },

    onLaunch() {
        const userId = wx.getStorageSync('userId');
        const openid = wx.getStorageSync('openid');
        if (userId) this.globalData.userId = userId;
        if (openid) this.globalData.openid = openid;
    }
});