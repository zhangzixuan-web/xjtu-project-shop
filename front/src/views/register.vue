<template>
  <!-- 注册页面容器 -->
  <div class="bakery-register-container">
    <!-- 注册卡片 -->
    <div class="bakery-register-card">
      <!-- 左侧内容：Logo 和图片 -->
      <div class="bakery-register-left">
        <div class="bakery-register-logo">
          <h1>Sweet<span>Delight</span></h1>
          <p>创建您的烘焙之旅</p>
        </div>
        <div class="bakery-register-image">
          <img src="@/assets/轮播-2.jpg" alt="Bakery Products">
        </div>
      </div>
      
      <!-- 右侧内容：注册表单 -->
      <div class="bakery-register-right">
        <h2 class="bakery-register-title">创建账号</h2>
        <p class="bakery-register-subtitle">请填写以下信息完成注册</p>
        
        <div class="bakery-register-form">
          <!-- 用户名输入 -->
          <div class="bakery-form-group">
            <label>用户名</label>
            <input type="text" class="bakery-input" placeholder="请输入用户名" v-model="user.username">
          </div>
          
          <!-- 手机号输入 -->
          <div class="bakery-form-group">
            <label>手机号</label>
            <input type="tel" class="bakery-input" placeholder="请输入手机号" v-model="user.phone">
          </div>
          
          <!-- 密码输入 -->
          <div class="bakery-form-group">
            <label>密码</label>
            <input type="password" class="bakery-input" placeholder="请输入密码" v-model="user.password">
          </div>
          
          <!-- 确认密码输入 -->
          <div class="bakery-form-group">
            <label>确认密码</label>
            <input type="password" class="bakery-input" placeholder="请确认密码" v-model="user.confirm">
          </div>
          
          <!-- 注册按钮 -->
          <button class="bakery-btn bakery-register-btn" @click="register">注册</button>
          
          <!-- 登录链接 -->
          <div class="bakery-register-footer">
            <p>已有账号? <span class="bakery-link" @click="$router.push('/login')">立即登录</span></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "Register",
  data() {
    return {
      user: {} // 存储用户输入的注册信息
    }
  },
  methods: {
    // 注册方法
    register() {
      // 对输入进行一系列校验
      if (!this.user.username) {
        this.$message.error("请输入用户名");
        return;
      }
      if (!this.user.phone) {
        this.$message.error("请输入手机号");
        return;
      }
      // 简单的手机号格式校验
      if (!/^1[3456789]\d{9}$/.test(this.user.phone)) {
        this.$message.error("请输入有效的手机号");
        return;
      }
      if (!this.user.password) {
        this.$message.error("请输入密码");
        return;
      }
      if (this.user.password.length < 6) {
        this.$message.warning("密码长度不能少于6位");
        return;
      }
      if (!this.user.confirm) {
        this.$message.error("请确认密码");
        return;
      }
      if (this.user.password !== this.user.confirm) {
        this.$message.error("两次输入的密码不一致");
        return;
      }

      // 发送注册请求到后端
      API.post("/api/user/register", this.user).then(res => {
        if (res.code === '0') {
          // 注册成功后提示用户并延迟跳转到登录页
          this.$message.success("注册成功！即将跳转到登录页...");
          setTimeout(() => {
            this.$router.replace("/login");
          }, 1500);
        } else {
          // 如果注册失败，显示后端返回的错误信息
          this.$message.error(res.msg);
        }
      })
    }
  }
}
</script>

<style scoped>
/* 注册页面容器样式 */
.bakery-register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-color: var(--background-color);
}

/* 注册卡片样式 */
.bakery-register-card {
  display: flex;
  width: 900px;
  min-height: 600px;
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  overflow: hidden;
}

/* 左侧内容区域样式 */
.bakery-register-left {
  flex: 1;
  background-color: var(--accent-color);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
}

/* Logo 样式 */
.bakery-register-logo {
  text-align: center;
  margin-bottom: 30px;
  z-index: 2;
}

.bakery-register-logo h1 {
  font-size: 36px;
  font-weight: 800;
  color: var(--secondary-color);
  margin-bottom: 10px;
}

.bakery-register-logo h1 span {
  color: var(--primary-color);
}

.bakery-register-logo p {
  color: var(--secondary-color);
  font-size: 16px;
}

/* 图片区域样式 */
.bakery-register-image {
  width: 80%;
  z-index: 1;
  text-align: center;
}

.bakery-register-image img {
  width: 100%;
  border-radius: var(--border-radius);
  object-fit: cover;
  height: 300px;
  box-shadow: var(--box-shadow);
}

/* 右侧表单区域样式 */
.bakery-register-right {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.bakery-register-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--secondary-color);
  margin-bottom: 10px;
}

.bakery-register-subtitle {
  color: #666;
  margin-bottom: 30px;
}

.bakery-register-form {
  flex: 1;
}

/* 表单组样式 */
.bakery-form-group {
  margin-bottom: 20px;
}

.bakery-form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--secondary-color);
  font-weight: 500;
}

/* 输入框样式 */
.bakery-input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  font-size: 16px;
  transition: border-color 0.3s;
}

.bakery-input:focus {
  border-color: var(--primary-color);
  outline: none;
}

/* 注册按钮样式 */
.bakery-register-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0;
}

/* 底部链接样式 */
.bakery-register-footer {
  text-align: center;
  color: #666;
}

.bakery-link {
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
}

/* 响应式布局：小屏幕下隐藏左侧图片 */
@media (max-width: 768px) {
  .bakery-register-card {
    flex-direction: column;
    width: 100%;
  }
  
  .bakery-register-left {
    display: none;
  }
}
</style>