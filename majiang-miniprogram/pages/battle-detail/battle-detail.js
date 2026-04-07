const battleApi = require('../../api/battle');

Page({
  data: {
    detail: null
  },

  async onLoad(query) {
    await this.loadDetail(query.id);
  },

  async loadDetail(id) {
    try {
      const detail = await battleApi.getBattleDetail(id);
      this.setData({ detail });
    } catch (e) {}
  }
});
