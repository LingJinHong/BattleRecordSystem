import {
    gameApi
} from '../../utils/api/game'
import moment from 'moment-mini'
Page({
    data: {
        detail: null,
        players: [],
        baseURL: getApp().globalData.baseURL
    },

    async onLoad(query) {
        await this.loadDetail(query.id);
    },

    async loadDetail(id) {
        try {
            const res = await gameApi.getGameDetail(id);
            res.data.game.gameTime = moment(res.data.game.gameTime).format('YYYY-MM-DD HH:mm:ss');
            const players = res.data.players || [];
            this.setData({
                detail: res.data.game,
                players
            });
        } catch (e) {}
    }
});