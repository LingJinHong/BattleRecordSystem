const teamApi = require('../../api/team');
const friendApi = require('../../api/friend');
const gameApi = require('../../api/game');

Page({
  data: {
    teamList: [],
    userList: [],
    selectedTeamId: '',
    selectedUserIds: []
  },

  onLoad() {
    this.initData();
  },

  async initData() {
    const userId = getApp().globalData.userId || wx.getStorageSync('userId') || '';
    try {
      const [teamRes, friendRes] = await Promise.all([
        teamApi.getTeamList({ page: 1, size: 100, userId }),
        friendApi.getFriendList(userId)
      ]);
      this.setData({
        teamList: teamRes.records || teamRes.list || teamRes || [],
        userList: friendRes || []
      });
    } catch (e) {}
  },

  onTeamChange(e) {
    const idx = Number(e.detail.value);
    const team = this.data.teamList[idx] || {};
    this.setData({ selectedTeamId: team.id || '' });
  },

  toggleUser(e) {
    const id = e.currentTarget.dataset.id;
    let { selectedUserIds } = this.data;
    if (selectedUserIds.includes(id)) {
      selectedUserIds = selectedUserIds.filter((v) => v !== id);
    } else {
      selectedUserIds = selectedUserIds.concat(id);
    }
    this.setData({ selectedUserIds });
  },

  async onSubmit() {
    const app = getApp();
    const userId = app.globalData.userId || wx.getStorageSync('userId');
    const { selectedTeamId, selectedUserIds } = this.data;
    if (!selectedTeamId) {
      return wx.showToast({ title: '请选择小队', icon: 'none' });
    }
    if (!selectedUserIds.length) {
      return wx.showToast({ title: '请选择玩家', icon: 'none' });
    }

    try {
      const data = await gameApi.createGame({
        teamId: selectedTeamId,
        userId,
        userIds: selectedUserIds
      });
      wx.showToast({ title: '创建成功', icon: 'success' });
      wx.navigateTo({ url: `/pages/game-detail/game-detail?id=${data.id || ''}` });
    } catch (e) {}
  }
});
