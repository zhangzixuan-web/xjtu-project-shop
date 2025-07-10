<template>
  <!-- 整个后台应用的容器 -->
  <div class="app-container">
    <!-- 顶部 header -->
    <header>
      后台管理
      <!-- 用户信息及下拉菜单 -->
      <el-dropdown style="float: right;" @command="handleCommand">
        <span class="el-dropdown-link" style="cursor: pointer">
          欢迎你！{{ user.username }}<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="person">个人信息</el-dropdown-item>
          <el-dropdown-item command="loginOut">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </header>
    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 侧边导航菜单 -->
      <el-menu class="side-menu" :default-active="$route.path" @select="handleMenuSelect">
        <!-- 根据用户权限动态渲染菜单项 -->
        <el-menu-item :index="item.path" v-for="item in user.permission" :key="item.path">
          <template #title>
            <i :class="['el-icon-' + item.icon]"></i>
            <span>{{ item.name }}</span>
          </template>
        </el-menu-item>
      </el-menu>

      <!-- 路由出口，用于显示具体的功能页面 -->
      <!-- :key="Math.random()" 强制组件在路由变化时重新渲染，@call 用于子组件调用父组件方法 -->
      <router-view class="main-container" :key="Math.random()" @call="getMenu" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'Manage',
  data() {
    return {
      user: {}, // 当前登录的用户信息，包含权限列表
    }
  },
  created() {
    // 组件创建时，初始化菜单
    this.getMenu()
  },
  methods: {
    // 获取并设置菜单
    getMenu() {
      // 从 sessionStorage 获取用户信息
      this.user = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : {}
      // 判断当前用户是否是商家 (roleId 为 3)
      const isMerchant = this.user.role && this.user.role.includes(3);

      if (isMerchant) {
        // 如果是商家，硬编码商家的菜单项
        this.user.permission = [
          { path: '/manage/merchantHome', name: '首页', icon: 's-home' },
          { path: '/manage/merchantGoods', name: '商品管理', icon: 's-goods' },
          { path: '/manage/merchantOrder', name: '订单管理', icon: 's-order' },
          { path: '/manage/merchantComment', name: '评论管理', icon: 'chat-line-square' }
        ];
      } else {
        // 如果是管理员或其他角色，处理从后端获取的权限列表，为其路径加上 '/manage' 前缀
        this.user.permission.forEach(item => {
          item.path = '/manage' + item.path
        })
      }
    },
    // 处理下拉菜单命令
    handleCommand(command) {
      if (command === 'person') this.$router.push('/manage/person')
      if (command === 'loginOut') {
        // 退出登录
        sessionStorage.removeItem('user')
        this.$router.replace('/login')
      }
    },
    // 处理侧边菜单选择
    handleMenuSelect(index) {
      // 跳转到对应页面
      this.$router.push(index)
    },
  },
}
</script>

<style scoped>
/* 调色板变量 */
:root {
  --primary-bg: #fff8ec;
  --card-bg: #fffdf9;
  --accent: #ff9b3c;
  --accent-dark: #d87d29;
  --text-primary: #5c3a1d;
  --text-secondary: #8c6e54;
  --border-light: #f2e2c9;
}

/* 应用容器样式 */
.app-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--primary-bg);
  color: var(--text-primary);
  font-family: "PingFang SC", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

/* 头部样式 */
header {
  margin-bottom: 3px;
  line-height: 52px;
  padding: 0 20px;
  font-size: 18px;
  font-weight: 600;
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-light);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.03);
  color: var(--accent-dark);
}

/* 下拉菜单链接样式 */
.el-dropdown-link {
  color: var(--accent-dark);
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  display: flex;
  height: calc(100% - 52px);
}

/* 侧边菜单样式 */
.side-menu {
  width: 200px;
  background: var(--card-bg);
  border-right: 1px solid var(--border-light);
}

.el-menu {
  border-right: none;
}

.el-menu-item {
  color: var(--text-secondary);
}

.el-menu-item.is-active {
  background: rgba(255, 155, 60, 0.08) !important;
  color: var(--accent-dark) !important;
}

/* 主内容容器（路由视图）样式 */
.main-container {
  flex: 1;
  margin-left: 3px;
  padding: 16px;
  overflow-y: auto;
  background: var(--card-bg);
  border-left: none;
}
</style>