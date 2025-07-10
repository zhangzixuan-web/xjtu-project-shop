import Vue from 'vue'
import Vuex from 'vuex'

// 全局使用 Vuex
Vue.use(Vuex)

// 定义全局状态
const state = {
    // 购物车列表，用于存放用户加入购物车的商品
    carts: [],
    // 一个类型标识，具体用途根据业务场景确定
    type: 0
}

// 定义同步修改 state 的方法
const mutations = {
    /**
     * 设置购物车列表
     * @param {Object} state - 当前的 state 对象
     * @param {Array} carts - 新的购物车数据
     */
    setCarts (state, carts) {
        state.carts = carts
    },
    /**
     * 设置类型标识
     * @param {Object} state - 当前的 state 对象
     * @param {number} type - 新的类型值
     */
    setType (state, type) {
        state.type = type
    }
}

// 创建 store 实例
const store = new Vuex.Store({
    state,
    mutations
})

// 导出 store 实例
export default store
