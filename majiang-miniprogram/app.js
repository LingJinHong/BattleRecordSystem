App({
  globalData: {
    baseURL: 'http://localhost:8080',
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
