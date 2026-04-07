const gameApi = require('../../utils/api/game');

Page({
  data: {
    gameList: []
  },

  onShow() {
    this.loadGames();
  },

  async loadGames() {
    try {
      const data = await gameApi.getGameList();
      this.setData({ gameList: data || [] });
    } catch (e) {}
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/game-create/game-create' });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/game-detail/game-detail?id=${id}` });
  },

  goSettle(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/game-settle/game-settle?id=${id}` });
  }
});
