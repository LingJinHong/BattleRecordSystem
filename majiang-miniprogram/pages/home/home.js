import {
    battleApi
} from '../../utils/api/battle';
const teamApi = require('../../utils/api/team');

Page({
    data: {
        filters: {
            startTime: '',
            endTime: '',
            teamId: '',
            incomeExpenseType: ''
        },
        teamList: [],
        typeOptions: [{
                label: '全部',
                value: ''
            },
            {
                label: '收入',
                value: 1
            },
            {
                label: '支出',
                value: 2
            }
        ],
        summary: {
            totalAmount: 0,
            totalCount: 0
        },
        battleList: []
    },

    onShow() {
        this.initData();
    },

    async initData() {
        await Promise.all([this.loadTeams(), this.loadBattles()]);
    },

    async loadTeams() {
        const userId = getApp().globalData.userId || wx.getStorageSync('userId') || '';
        try {
            const data = await teamApi.getTeamList({
                page: 1,
                size: 100,
                userId
            });
            const list = data.records || data.list || data || [];
            this.setData({
                teamList: list
            });
        } catch (e) {}
    },

    async loadBattles() {
        const userId = getApp().globalData.userId || wx.getStorageSync('userInfo').id || '';
        const {
            filters
        } = this.data;
        try {
            const params = {
                userId,
                page: 1,
                size: 100,
                teamId: filters.teamId,
                incomeExpenseType: filters.incomeExpenseType,
                startTime: filters.startTime,
                endTime: filters.endTime
            };
            const data = Object.keys(params)
                .filter((k) => params[k] !== undefined && params[k] !== '')
                .map((k) => `${k}=${encodeURIComponent(params[k])}`)
                .join('&');

            const res = await battleApi.getBattleList(data);
            const list = res.data.records || [];
            const totalAmount = list.reduce((sum, item) => sum + Number(item.score), 0);
            this.setData({
                battleList: list,
                summary: {
                    totalAmount,
                    totalCount: list.length
                }
            });
        } catch (e) {}
    },

    onStartDateChange(e) {
        this.setData({
            'filters.startTime': e.detail.value
        });
    },

    onEndDateChange(e) {
        this.setData({
            'filters.endTime': e.detail.value
        });
    },

    onTeamChange(e) {
        const idx = Number(e.detail.value);
        const team = this.data.teamList[idx] || {};
        this.setData({
            'filters.teamId': team.id || ''
        });
    },

    onTypeChange(e) {
        const idx = Number(e.detail.value);
        const item = this.data.typeOptions[idx] || {
            value: ''
        };
        this.setData({
            'filters.incomeExpenseType': item.value
        });
    },

    onSearch() {
        this.loadBattles();
    },

    goBattle(e) {
        const item = e.currentTarget.dataset.item;
        if (item.gameId) {
            wx.navigateTo({
                url: `/pages/game-detail/game-detail?id=${item.gameId}`
            });
        } else {
            wx.navigateTo({
                url: `/pages/battle-detail/battle-detail?id=${item.id}`
            });
        }
    }
});