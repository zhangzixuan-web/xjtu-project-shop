<template>
  <!-- 整体布局容器 -->
  <div class="bakery-layout">
    <!-- 网站头部区域 -->
    <div class="bakery-header">
      <div class="bakery-container">
        <!-- 使用 ElementUI 的 Row 和 Col 进行布局 -->
        <el-row type="flex" align="middle">
          <!-- Logo -->
          <el-col :span="4">
            <div class="bakery-logo"><a href="/front/home">Sweet<span>Delight</span></a></div>
          </el-col>
          <!-- 导航菜单 -->
          <el-col :span="8">
            <el-menu class="bakery-nav" :default-active="$route.path" @select="handleMenuSelect" mode="horizontal">
              <el-menu-item index="/front/home">首页</el-menu-item>
              <el-menu-item index="/front/cart">购物车</el-menu-item>
              <el-menu-item index="/front/order">我的订单</el-menu-item>
              <el-menu-item index="/front/collect">收藏夹</el-menu-item>
              <el-menu-item index="/front/person">个人中心</el-menu-item>
              <el-menu-item index="/front/address">收货地址</el-menu-item>
            </el-menu>
          </el-col>
          <!-- 搜索框 -->
          <el-col :span="14" class="search-col">
            <!-- 仅在非搜索页面显示搜索框 -->
            <div v-if="!$route.path.includes('/search')" class="search-box">
              <el-input
                  style="max-width: 250px; width: 100%; margin-right: 10px;"
                  placeholder="请输入商品名称搜索商品"
                  v-model="searchText"
              ></el-input>
              <el-button class="bakery-btn" @click="search">搜索</el-button>
            </div>
            <!-- 在搜索页面留出空白占位 -->
            <div v-else>
              <div style="width: 100px;height: 20px"></div>
            </div>
          </el-col>
          <!-- 用户信息和登录/注册按钮 -->
          <el-col :span="5">
            <!-- 如果用户已登录，显示欢迎信息和下拉菜单 -->
            <div style="text-align: right; padding-right: 10px" v-if="user.id">
              <el-dropdown style="float: right;" @command="handleCommand">
                <span class="el-dropdown-link" style="cursor: pointer">
                  欢迎你！{{ user.username }}<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="person">个人信息</el-dropdown-item>
                  <el-dropdown-item command="loginOut">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
            <!-- 如果用户未登录，显示登录和注册按钮 -->
            <div style="text-align: right; padding-right: 10px" v-else>
              <el-button class="bakery-btn-outline" @click="$router.replace('/login')">登录</el-button>
              <el-button class="bakery-btn" @click="$router.replace('/register')">注册</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 主体内容区域 -->
    <div class="main-content-wrapper">
      <div class="bakery-container">
        <!-- 路由出口，子页面将在此处渲染 -->
        <router-view />
      </div>
    </div>

    <!-- 网站底部区域 -->
    <div class="bakery-footer">
      <div class="bakery-container">
        <el-row :gutter="20">
          <!-- 联系我们信息 -->
          <el-col :span="8">
            <div class="bakery-footer-title">联系我们</div>
            <div class="bakery-footer-link">
              <i class="el-icon-phone"></i> +971 (66) 8767 8798
            </div>
            <div class="bakery-footer-link">
              <i class="el-icon-message"></i> sweetdelight@yourmail.com
            </div>
            <div class="bakery-footer-link">
              <i class="el-icon-location"></i> 53128 Sydney Rd Southern, Australia(OA), 55284
            </div>
          </el-col>
          <!-- 快速链接 -->
          <el-col :span="8">
            <div class="bakery-footer-title">快速链接</div>
            <div class="bakery-footer-link" @click="$router.push('/front/home')">首页</div>
            <div class="bakery-footer-link" @click="$router.push('/front/cart')">购物车</div>
            <div class="bakery-footer-link" @click="$router.push('/front/order')">我的订单</div>
            <div class="bakery-footer-link" @click="$router.push('/front/collect')">收藏夹</div>
          </el-col>
          <!-- 关于我们信息 -->
          <el-col :span="8">
            <div class="bakery-footer-title">关于我们</div>
            <div class="bakery-footer-link">我们的故事</div>
            <div class="bakery-footer-link">烘焙工艺</div>
            <div class="bakery-footer-link">加入我们</div>
          </el-col>
        </el-row>
        <!-- 底部版权信息 -->
        <div class="bakery-footer-bottom">
          © Copyright 2024 SweetDelight
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "Front",
  data() {
    return {
      searchText: '', // 搜索框输入的内容
      user: {}        // 当前登录的用户信息
    }
  },
  created() {
    // 组件创建时，从 sessionStorage 中获取用户信息
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
  },
  methods: {
    // 搜索功能
    search() {
      // 跳转到搜索页面，并通过 query 参数传递搜索关键词
      this.$router.replace({path: '/front/search', query: {searchText: this.searchText}})
    },
    // 处理下拉菜单命令
    handleCommand(command) {
      if (command === 'person') {
        // 跳转到个人信息页面
        this.$router.push('/front/person');
      }
      if (command === 'loginOut') {
        // 退出登录，清除 sessionStorage 中的用户信息并跳转到登录页
        sessionStorage.removeItem("user")
        this.$router.replace('/login');
      }
    },
    // 处理导航菜单选择
    handleMenuSelect(index) {
      // 跳转到选中的菜单项对应的路由
      this.$router.push(index);
    },
  }
}
</script>

<style scoped>
/* 整体布局样式 */
.bakery-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 主内容区域包裹器，用于垂直方向的弹性布局 */
.main-content-wrapper {
  flex: 1; /* 使主内容区域填充可用的垂直空间 */
  padding: 20px 0; /* 在内容卡片上下添加一些空间 */
}

/* 内容容器，用于水平居中 */
.bakery-container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* Logo 链接样式 */
.bakery-logo a {
  text-decoration: none;
  color: var(--secondary-color);
}

/* 下拉菜单链接样式 */
.el-dropdown-link {
  color: var(--secondary-color);
  font-weight: 500;
}

/* 搜索框列样式 */
.search-col {
  padding-left: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 搜索框容器样式 */
.search-box {
  height: 40px;
  padding: 5px 0;
  display: flex;
  align-items: center;
}
</style>