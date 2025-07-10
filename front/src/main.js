// 导入 Vue 核心库
import Vue from 'vue'
// 导入根组件 App
import App from './App.vue'
// 导入路由配置
import router from './router'
// 导入 Vuex 状态管理
import store from './store'
// 导入 ElementUI 组件库及其样式
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
// 导入自定义的全局样式
import './style/base.css';
import './style/bakery-theme.css';

// 全局引入 ECharts
import * as echarts from 'echarts';
// 将 ECharts 挂载到 Vue 的原型上，方便在所有组件中使用
Vue.prototype.$echarts = echarts

// 关闭 Vue 的生产提示
Vue.config.productionTip = false

// 全局注册 ElementUI, 并设置默认尺寸为 'small'
Vue.use(ElementUI, { size: 'small' });

// 创建 Vue 根实例
new Vue({
  // 注入路由
  router,
  // 注入 store
  store,
  // 渲染根组件 App
  render: h => h(App)
}).$mount('#app') // 挂载到 DOM 的 #app 元素上
