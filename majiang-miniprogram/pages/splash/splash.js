import {
    userApi
} from '../../utils/api/index';
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        avatarUrl: '',
        localAvatarUrl: "",
        nickName: '',
        userInfo: {},
        timer: null,
        showAuthModal: false, // 控制自定义弹窗显示
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        // ==============================================
        // 第一步：判断是否已经登录（看缓存里有没有token）
        // ==============================================
        // const token = wx.getStorageSync('userInfo')
        const userInfo = wx.getStorageSync('userInfo')

        if (userInfo) {
            // ✅ 已登录：直接跳首页，不显示开屏页！
            wx.reLaunch({
                url: '/pages/home/home'
            })
            return
        }
    },
    onInputNick(e) {
        this.setData({
            nickName: e.detail.value
        })
    },
    // 获取微信头像
    onChooseAvatar(e) {
        this.setData({
            avatarUrl: e.detail.avatarUrl
        })
    },
    // 提交登录
    submit() {
        const {
            avatarUrl,
            nickName
        } = this.data

        if (!avatarUrl) {
            wx.showToast({
                title: '请选择头像',
                icon: 'none'
            })
            return
        }
        if (!nickName) {
            wx.showToast({
                title: '请输入昵称',
                icon: 'none'
            })
            return
        }

        // 保存用户信息（本地缓存）
        wx.setStorageSync('userInfo', {
            avatarUrl,
            nickName
        })

        // 登录成功 → 跳首页
        wx.reLaunch({
            url: '/pages/index/index',
            success: () => {
                wx.showToast({
                    title: '登录成功'
                })
            }
        })
    },
    async goLogin(code) {
        try {
            const res = await userApi.login(code)
            this.goOpenId(res.data)
        } catch (err) {
            console.log('请求失败', err)
        }
    },
    async goOpenId(openId) {
        try {
            const res = await userApi.getOpenId(openId);
            wx.setStorageSync('userInfo', res.data[0])
            // 2. 调用wx.uploadFile上传到你的后端接口
            wx.uploadFile({
                url: app.globalData.baseURL + '/api/user/upload-avatar', // 后端上传接口
                filePath: this.data.avatarUrl,
                name: 'avatar', // 后端接收的文件参数名
                formData: {
                    openid: openId // 携带用户标识，绑定头像
                },
                success: (uploadRes) => {
                    // 3. 上传成功，拿到后端返回的公网头像URL
                    const localAvatarUrl = JSON.parse(uploadRes.data).data;
                    // 后续用这个公网URL展示/存储
                    this.setData({
                        avatarUrl: getApp().globalData.baseURL + `${localAvatarUrl}`
                    })
                    wx.reLaunch({
                        url: '/pages/home/home',
                    })
                    if (!res.data.length) {
                        this.addUser(openId)
                    }

                },
                fail: (err) => {
                    console.error('上传失败：', err);
                }
            });

        } catch (err) {
            console.log('请求失败', err)
        }
    },
    //新用户
    async addUser(openId) {
        let data = {
            openid: openId,
            title: this.data.nickName,
            content: this.data.nickName,
            avatarUrl: this.data.avatarUrl,
        }
        try {
            const res = await userApi.addUser(data);
            wx.setStorageSync('userInfo', res.data)
            wx.reLaunch({
                url: 'page/index/index',
            })
        } catch (err) {
            console.log('请求失败', err)
        }
    },

    confirmAuth() {
        if (!this.data.avatarUrl) {
            wx.showToast({
                title: '请选择微信头像',
                icon: 'none'
            });
            return
        }
        if (!this.data.nickName) {
            wx.showToast({
                title: '请选择微信名称',
                icon: 'none'
            });
            return
        }
        const that = this;
        wx.login({
            success(res) {
                if (res.code) {
                    //发起网络请求
                    that.goLogin(res.code);
                } else {
                    console.log('登录失败！' + res.errMsg)
                }
            }
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {
        clearInterval(this.data.timer)
    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})