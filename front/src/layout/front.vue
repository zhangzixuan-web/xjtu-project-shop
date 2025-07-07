<template>
  <div class="bakery-layout">
    <!--    头部-->
    <div class="bakery-header">
      <div class="bakery-container">
        <el-row type="flex" align="middle">
          <el-col :span="4">
            <div class="bakery-logo"><a href="/front/home">Sweet<span>Delight</span></a></div>
          </el-col>
          <el-col :span="10">
            <el-menu class="bakery-nav" :default-active="$route.path" @select="handleMenuSelect" mode="horizontal">
              <el-menu-item index="/front/home">
                首页
              </el-menu-item>
              <el-menu-item index="/front/cart">
                购物车
              </el-menu-item>
              <el-menu-item index="/front/order">
                我的订单
              </el-menu-item>
              <el-menu-item index="/front/collect">
                收藏夹
              </el-menu-item>
              <el-menu-item index="/front/person">
                个人中心
              </el-menu-item>
              <el-menu-item index="/front/address">
                收货地址
              </el-menu-item>
            </el-menu>
          </el-col>
          <el-col :span="6">
            <div v-if="!$route.path.includes('/search')" class="search-box">
              <el-input style="width: 80%" placeholder="请输入商品名称搜索商品" v-model="searchText"></el-input>
              <el-button class="bakery-btn" style="margin-left: 5px" @click="search">搜索</el-button>
            </div>
            <div v-else>
              <div style="width: 100px;height: 20px"></div>
            </div>
          </el-col>
          <el-col :span="4">
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
            <div style="text-align: right; padding-right: 10px" v-else>
              <el-button class="bakery-btn-outline" @click="$router.replace('/login')">登录</el-button>
              <el-button class="bakery-btn" @click="$router.replace('/register')">注册</el-button>
            </div>

          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 主体 -->
    <div class="bakery-container">
      <router-view />
    </div>

    <!--    底部-->
    <div class="bakery-footer">
      <div class="bakery-container">
        <el-row :gutter="20">
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
          <el-col :span="8">
            <div class="bakery-footer-title">快速链接</div>
            <div class="bakery-footer-link" @click="$router.push('/front/home')">首页</div>
            <div class="bakery-footer-link" @click="$router.push('/front/cart')">购物车</div>
            <div class="bakery-footer-link" @click="$router.push('/front/order')">我的订单</div>
            <div class="bakery-footer-link" @click="$router.push('/front/collect')">收藏夹</div>
          </el-col>
          <el-col :span="8">
            <div class="bakery-footer-title">关于我们</div>
            <div class="bakery-footer-link">我们的故事</div>
            <div class="bakery-footer-link">烘焙工艺</div>
            <div class="bakery-footer-link">加入我们</div>
          </el-col>
        </el-row>
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
      searchText: '',
      user: {}
    }
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
  },
  methods: {
    search() {
      this.$router.replace({path: '/front/search', query: {searchText: this.searchText}})
    },
    handleCommand(command) {
      if (command === 'person') {
        this.$router.push('/front/person');
      }
      if (command === 'loginOut') {
        sessionStorage.removeItem("user")
        this.$router.replace('/login');

      }
    },
    handleMenuSelect(index) {
      console.log(index)
      this.$router.push(index);
    },
  }
}
</script>

<style scoped>
.bakery-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.bakery-container {
  flex: 1;
}

.bakery-logo a {
  text-decoration: none;
  color: var(--secondary-color);
}

.el-dropdown-link {
  color: var(--secondary-color);
  font-weight: 500;
}

/* 修复搜索框样式 */
.search-box {
  height: 40px;
  padding: 5px 0;
}
</style>
