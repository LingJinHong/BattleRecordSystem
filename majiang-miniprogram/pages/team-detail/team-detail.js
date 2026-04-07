const teamApi = require('../../utils/api/team');

Page({
  data: {
    id: '',
    members: [],
    addUserIdInput: ''
  },

  onLoad(query) {
    this.setData({ id: query.id });
    this.loadMembers();
  },

  async loadMembers() {
    try {
      const members = await teamApi.getTeamUsers(this.data.id);
      this.setData({ members: members || [] });
    } catch (e) {}
  },

  onInput(e) {
    this.setData({ addUserIdInput: e.detail.value });
  },

  async onAddMember() {
    const userId = Number(this.data.addUserIdInput);
    if (!userId) return wx.showToast({ title: '请输入用户ID', icon: 'none' });
    try {
      await teamApi.addTeamUser({ teamId: Number(this.data.id), userId });
      wx.showToast({ title: '添加成功', icon: 'success' });
      this.setData({ addUserIdInput: '' });
      this.loadMembers();
    } catch (e) {}
  }
});
