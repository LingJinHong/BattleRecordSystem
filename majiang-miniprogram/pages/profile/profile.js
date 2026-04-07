const userApi = require('../../utils/api/user');
const app = getApp();

Page({
    data: {
        user: null,
        isLoading: false
    },

    onLoad() {
        // 页面加载优先读缓存，避免重复授权
        const cachedUser = wx.getStorageSync('userInfo');
        const cachedOpenid = wx.getStorageSync('openid');
        if (cachedUser && cachedOpenid) {
            this.setData({
                user: {
                    ...cachedUser,
                    openid: cachedOpenid
                }
            });
            this.syncUserInfo(cachedOpenid); // 静默更新
        }
    },

    // ✅ 唯一入口：用户点击按钮直接触发，保证是tap手势
    async onGetProfile() {
        if (this.data.isLoading) return;
        this.setData({
            isLoading: true
        });
        wx.showLoading({
            title: '登录中...',
            mask: true
        });

        try {
            // 1. 【关键】第一步：直接调用wx.getUserProfile，在tap同步栈中
            const profileRes = await this.wxGetUserProfile();
            console.log('profileRes', profileRes);
            const {
                nickName,
                avatarUrl
            } = profileRes.userInfo;
            console.log('[授权] 用户信息获取成功:', nickName);

            // 2. 第二步：wx.login获取code（在授权后调用，不影响tap校验）
            const loginRes = await this.wxLogin();
            console.log('[登录] code获取成功:', loginRes.code);

            // 3. 第三步：后端换取openid
            const openidRes = await userApi.getOpenidByCode(loginRes.code);
            const openid = openidRes;
            console.log('[登录] openid获取成功:', openid);

            // 4. 第四步：查询/创建用户
            const userRes = await userApi.getUserByOpenid(openid);
            let userInfo = userRes;
            console.log('userInfo', userInfo);
            if (!userInfo || userInfo.length == 0) {
                // 用户不存在，创建新用户
                const createParam = {
                    openid: openid,
                    nickName: nickName,
                    avatarUrl: avatarUrl,
                    // 补充后端需要的其他必填字段（如果有）
                    title: nickName,
                    content: nickName
                };
                const createRes = await userApi.createUserData(createParam);
                userInfo = createRes;
                userInfo.avatarUrl = app.globalData.baseURL + createRes.avatarUrl;
                // 缓存用户信息
                wx.setStorageSync('userInfo', userInfo);
                wx.setStorageSync('openid', openid);
                app.globalData.userInfo = userInfo;
            }

            // 5. 更新页面数据
            this.setData({
                user: {
                    ...userInfo,
                    openid
                }
            });

            wx.showToast({
                title: '登录成功',
                icon: 'success'
            });

        } catch (err) {
            console.error('[登录] 流程异常:', err);
            this.handleLoginError(err);
        } finally {
            this.setData({
                isLoading: false
            });
            wx.hideLoading();
        }
    },

    // ✅ 封装wx.getUserProfile，确保在tap中调用
    wxGetUserProfile() {
        return new Promise((resolve, reject) => {
            wx.getUserProfile({
                desc: '用于完善个人资料',
                success: resolve,
                fail: (err) => {
                    if (err.errMsg.includes('fail auth deny')) {
                        reject(new Error('用户拒绝授权'));
                    } else {
                        reject(err);
                    }
                }
            });
        });
    },

    // 封装wx.login
    wxLogin() {
        return new Promise((resolve, reject) => {
            wx.login({
                success: res => res.code ? resolve(res) : reject(new Error('获取code失败')),
                fail: reject
            });
        });
    },

    // 静默同步用户信息
    async syncUserInfo(openid) {
        try {
            const userRes = await userApi.getUserByOpenid(openid);
            if (userRes.data) {
                this.setData({
                    user: {
                        ...userRes.data,
                        openid
                    }
                });
                wx.setStorageSync('userInfo', userRes.data);
            }
        } catch (err) {
            console.error('[同步] 用户信息同步失败:', err);
        }
    },

    // 统一错误处理
    handleLoginError(err) {
        const errMsg = err.message || err.errMsg || '登录失败，请稍后重试';

        if (errMsg.includes('用户拒绝授权')) {
            wx.showModal({
                title: '提示',
                content: '您已拒绝授权，无法使用个人中心功能，是否重新授权？',
                success: (res) => res.confirm && this.onGetProfile()
            });
        } else if (errMsg.includes('invalid appid')) {
            wx.showToast({
                title: '服务器繁忙，请稍后重试',
                icon: 'none'
            });
        } else {
            wx.showToast({
                title: errMsg,
                icon: 'none'
            });
        }
    }
});