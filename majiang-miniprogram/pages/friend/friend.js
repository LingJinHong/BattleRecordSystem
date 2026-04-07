const friendApi = require('../../utils/api/friend');

Page({
  data: {
    friendList: [],
    friendUserIdInput: ''
  },

  onShow() {
    this.loadFriends();
  },

  async loadFriends() {
    const userId = getApp().globalData.userId || wx.getStorageSync('userId') || '';
    try {
      const data = await friendApi.getFriendList(userId);
      this.setData({ friendList: data || [] });
    } catch (e) {}
  },

  onInput(e) {
    this.setData({ friendUserIdInput: e.detail.value });
  },

  async onAddFriend() {
    const userId = getApp().globalData.userId || wx.getStorageSync('userId') || '';
    const friendUserId = Number(this.data.friendUserIdInput);
    if (!friendUserId) {
      return wx.showToast({ title: '请输入好友ID', icon: 'none' });
    }
    try {
      await friendApi.addFriend({ userId, friendUserId });
      wx.showToast({ title: '添加成功', icon: 'success' });
      this.setData({ friendUserIdInput: '' });
      this.loadFriends();
    } catch (e) {}
  }
});
