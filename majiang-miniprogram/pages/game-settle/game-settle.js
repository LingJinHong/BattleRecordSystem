const gameApi = require('../../utils/api/game');

Page({
  data: {
    gameId: '',
    players: [],
    venueFee: 0,
    totalAmount: 0
  },

  async onLoad(query) {
    const gameId = query.id;
    this.setData({ gameId });
    await this.loadGameDetail(gameId);
  },

  async loadGameDetail(gameId) {
    try {
      const data = await gameApi.getGameDetail(gameId);
      const users = data.players || data.userInfos || data.users || [];
      this.setData({
        players: users.map((u) => ({
          userId: u.userId || u.id,
          name: u.userName || u.title || `用户${u.userId || u.id}`,
          amount: ''
        }))
      });
    } catch (e) {}
  },

  onAmountInput(e) {
    const idx = Number(e.currentTarget.dataset.idx);
    const value = e.detail.value;
    const key = `players[${idx}].amount`;
    this.setData({ [key]: value }, () => {
      this.calculateTotal();
    });
  },

  onVenueFeeInput(e) {
    this.setData({ venueFee: Number(e.detail.value || 0) });
  },

  calculateTotal() {
    const totalAmount = this.data.players.reduce((sum, p) => sum + Number(p.amount || 0), 0);
    this.setData({ totalAmount });
  },

  async onSubmit() {
    const { gameId, players, totalAmount, venueFee } = this.data;
    if (Number(totalAmount) !== 0) {
      return wx.showToast({ title: '总和必须等于 0', icon: 'none' });
    }

    const results = players.map((p) => ({ userId: p.userId, amount: Number(p.amount || 0) }));
    try {
      await gameApi.settleGame(gameId, { gameId: Number(gameId), venueFee: Number(venueFee || 0), results });
      wx.showToast({ title: '结算成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 500);
    } catch (e) {}
  }
});
