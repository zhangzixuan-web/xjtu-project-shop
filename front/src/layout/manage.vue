<template>
  <div class="app-container">
    <header>
      后台管理
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
    <div class="main-content">
      <el-menu class="side-menu" :default-active="$route.path" @select="handleMenuSelect">
        <el-menu-item :index="item.path" v-for="item in user.permission" :key="item.path">
          <template #title>
            <i :class="['el-icon-' + item.icon]"></i>
            <span>{{ item.name }}</span>
          </template>
        </el-menu-item>
      </el-menu>

      <router-view class="main-container" :key="Math.random()" @call="getMenu" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'Manage',
  data() {
    return {
      user: {},
    }
  },
  created() {
    this.getMenu()
  },
  methods: {
    getMenu() {
      this.user = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : {}
      this.user.permission.forEach(item => {
        item.path = '/manage' + item.path
      })
    },
    handleCommand(command) {
      if (command === 'person') this.$router.push('/manage/person')
      if (command === 'loginOut') {
        sessionStorage.removeItem('user')
        this.$router.replace('/login')
      }
    },
    handleMenuSelect(index) {
      this.$router.push(index)
    },
  },
}
</script>

<style scoped>
/* 调色盘 */
:root {
  --primary-bg: #fff8ec;
  --card-bg: #fffdf9;
  --accent: #ff9b3c;
  --accent-dark: #d87d29;
  --text-primary: #5c3a1d;
  --text-secondary: #8c6e54;
  --border-light: #f2e2c9;
}

.app-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--primary-bg);
  color: var(--text-primary);
  font-family: "PingFang SC", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

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

.el-dropdown-link {
  color: var(--accent-dark);
}

.main-content {
  flex: 1;
  display: flex;
  height: calc(100% - 52px);
}

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

.main-container {
  flex: 1;
  margin-left: 3px;
  padding: 16px;
  overflow-y: auto;
  background: var(--card-bg);
  border-left: none;
}
</style>