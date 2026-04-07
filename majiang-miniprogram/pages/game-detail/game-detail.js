const gameApi = require('../../api/game');

Page({
  data: {
    detail: null,
    players: []
  },

  async onLoad(query) {
    await this.loadDetail(query.id);
  },

  async loadDetail(id) {
    try {
      const data = await gameApi.getGameDetail(id);
      const players = data.results || data.players || [];
      this.setData({ detail: data, players });
    } catch (e) {}
  }
});
