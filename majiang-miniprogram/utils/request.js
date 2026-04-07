const app = getApp();

function request({ url, method = 'GET', data = {}, header = {} }) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${app.globalData.baseURL}${url}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...header
      },
      success(res) {
        const { code, msg, data: payload } = res.data || {};
        if (code === 0) {
          resolve(payload);
        } else {
          wx.showToast({ title: msg || '请求失败', icon: 'none' });
          reject(res.data || {});
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常', icon: 'none' });
        reject(err);
      }
    });
  });
}

module.exports = request;
