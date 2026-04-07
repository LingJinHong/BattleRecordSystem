const teamApi = require('../../api/team');

Page({
  data: {
    teamList: [],
    teamNameInput: ''
  },

  onShow() {
    this.loadTeams();
  },

  async loadTeams() {
    const userId = getApp().globalData.userId || wx.getStorageSync('userId') || '';
    try {
      const data = await teamApi.getTeamList({ page: 1, size: 100, userId });
      this.setData({ teamList: data.records || data.list || data || [] });
    } catch (e) {}
  },

  onInput(e) {
    this.setData({ teamNameInput: e.detail.value });
  },

  async onCreateTeam() {
    const userId = getApp().globalData.userId || wx.getStorageSync('userId') || '';
    const teamName = (this.data.teamNameInput || '').trim();
    if (!teamName) {
      return wx.showToast({ title: '请输入小队名称', icon: 'none' });
    }
    try {
      await teamApi.createTeam({ userId, teamName });
      wx.showToast({ title: '创建成功', icon: 'success' });
      this.setData({ teamNameInput: '' });
      this.loadTeams();
    } catch (e) {}
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/team-detail/team-detail?id=${id}` });
  }
});
