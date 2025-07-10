<template>
  <!-- 登录页面容器 -->
  <div class="bakery-login-container">
    <!-- 登录卡片 -->
    <div class="bakery-login-card">
      <!-- 左侧内容：Logo 和图片 -->
      <div class="bakery-login-left">
        <div class="bakery-login-logo">
          <h1>Sweet<span>Delight</span></h1>
          <p>欢迎来到美味烘焙世界</p>
        </div>
        <div class="bakery-login-image">
          <img src="@/assets/sign in1.jpg" alt="Bakery Products">
        </div>
      </div>
      
      <!-- 右侧内容：登录表单 -->
      <div class="bakery-login-right">
        <h2 class="bakery-login-title">登录账号</h2>
        <p class="bakery-login-subtitle">请输入您的账号信息</p>
        
        <div class="bakery-login-form">
          <!-- 用户名输入 -->
          <div class="bakery-form-group">
            <label>用户名</label>
            <input type="text" class="bakery-input" placeholder="请输入用户名" v-model="user.username">
          </div>
          <!-- 密码输入 -->
          <div class="bakery-form-group">
            <label>密码</label>
            <input type="password" class="bakery-input" placeholder="请输入密码" v-model="user.password">
          </div>
          
          <!-- 验证码区域，登录失败3次后显示 -->
          <transition name="fade">
            <div class="bakery-form-group captcha-group" v-if="count >= 3">
              <label>验证码</label>
              <div class="captcha-wrapper">
                <input type="text" class="bakery-input captcha-input" placeholder="请输入验证码" v-model="code" @keyup.enter="login">
                <!-- 验证码组件，点击可刷新 -->
                <div class="captcha-img" @click="refreshCode" title="点击刷新">
                  <Identify :identifyCode="identifyCode"></Identify>
                </div>
              </div>
            </div>
          </transition>
          
          <!-- 表单操作：记住我和忘记密码 -->
          <div class="bakery-form-actions">
            <div class="remember-me">
              <input type="checkbox" id="remember">
              <label for="remember">记住我</label>
            </div>
            <div class="forgot-password">
              <span @click="preReset">忘记密码?</span>
            </div>
          </div>
          
          <!-- 登录按钮 -->
          <button class="bakery-btn bakery-login-btn" @click="login">登录</button>
          
          <!-- 注册链接 -->
          <div class="bakery-login-footer">
            <p>还没有账号? <span class="bakery-link" @click="goRegister">立即注册</span></p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 忘记密码弹窗 -->
    <el-dialog title="重置密码" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false" append-to-body>
      <el-form :model="reset">
        <el-form-item label="用户名" label-width="100px">
          <el-input v-model="reset.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机号" label-width="100px">
          <el-input v-model="reset.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" label-width="100px">
          <el-input v-model="reset.password" autocomplete="off" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 导入封装的 API 请求工具
import API from "@/utils/request";
// 导入路由重置方法
import { resetRouter } from "@/router";
// 导入验证码组件
import Identify from "@/components/common/Identify";

export default {
  name: "Login",
  components: { Identify },
  data() {
    return {
      user: { username: '', password: '' }, // 用户输入的登录信息
      count: 0,                           // 记录登录失败次数
      code: '',                             // 用户输入的验证码
      identifyCode: '',                   // 生成的正确验证码
      identifyCodes: '3456789ABCDEFGHGKMNPQRSTUVWXY', // 生成验证码的字符集
      dialogFormVisible: false,             // 控制忘记密码弹窗的显示
      reset: {}                             // 忘记密码表单的数据
    };
  },
  methods: {
    // 登录方法
    login() {
      // 表单基础校验
      if (!this.user.username || !this.user.password) {
        this.$message.warning("请输入用户名和密码");
        return;
      }
      // 如果需要验证码，则校验验证码
      if (this.count >= 3 && this.code.toLowerCase() !== this.identifyCode.toLowerCase()) {
        this.$message.error("验证码错误");
        return;
      }
      // 发送登录请求
      API.post("/api/user/login", this.user).then(res => {
        if (res.code === '0') {
          // 登录成功
          this.$message.success("登录成功");
          // 将用户信息存入 sessionStorage
          sessionStorage.setItem("user", JSON.stringify(res.data));
          // 重置路由以加载动态路由
          resetRouter(res.data.permission);
 
          // 根据用户角色跳转到不同页面
          if (res.data.role && res.data.role.includes(3)) { // 商家
            this.$router.replace("/manage/merchantHome");
          } else if (res.data.role && res.data.role.includes(1)) { // 管理员
            this.$router.replace("/manage/home");
          } else { // 普通用户
            this.$router.replace("/front/home");
          }
        } else {
          // 登录失败
          this.$message.error(res.msg);
          this.count++;
          // 失败超过3次时刷新验证码
          if (this.count >= 3) this.refreshCode();
        }
      });
    },
    // 跳转到注册页面
    goRegister() {
      this.$router.push('/register');
    },
    // 显示重置密码弹窗
    preReset() {
      this.reset = {};
      this.dialogFormVisible = true;
    },
    // 保存重置的密码
    save() {
      // 表单校验
      if (!this.reset.username || !this.reset.phone || !this.reset.password) {
        this.$message.warning("请填写完整信息");
        return;
      }
      // 发送重置密码请求
      API.post("/api/user/reset", this.reset).then(res => {
        if (res.code === '0') {
          this.$message.success("密码重置成功");
          this.dialogFormVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 刷新验证码
    refreshCode() {
      this.identifyCode = '';
      this.makeCode(4);
    },
    // 生成指定长度的验证码
    makeCode(l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode += this.identifyCodes[Math.floor(Math.random() * this.identifyCodes.length)];
      }
    }
  },
  mounted() {
    // 组件挂载时，生成初始验证码
    this.refreshCode();
  }
}
</script>

<style scoped>
.bakery-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-color: var(--background-color);
}

.bakery-login-card {
  display: flex;
  width: 900px;
  min-height: 550px;
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  overflow: hidden;
}

.bakery-login-left {
  flex: 1;
  background-color: var(--accent-color);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
}

.bakery-login-logo {
  text-align: center;
  margin-bottom: 30px;
  z-index: 2;
}

.bakery-login-logo h1 {
  font-size: 36px;
  font-weight: 800;
  color: var(--secondary-color);
  margin-bottom: 10px;
}

.bakery-login-logo h1 span {
  color: var(--primary-color);
}

.bakery-login-logo p {
  color: var(--secondary-color);
  font-size: 16px;
}

.bakery-login-image {
  width: 80%;
  z-index: 1;
  text-align: center;
}

.bakery-login-image img {
  width: 100%;
  border-radius: var(--border-radius);
  object-fit: cover;
  height: 300px;
  box-shadow: var(--box-shadow);
}

.bakery-login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.bakery-login-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--secondary-color);
  margin-bottom: 10px;
}

.bakery-login-subtitle {
  color: #666;
  margin-bottom: 30px;
}

.bakery-login-form {
  flex: 1;
}

.bakery-form-group {
  margin-bottom: 20px;
}

.bakery-form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--secondary-color);
  font-weight: 500;
}

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

.captcha-group {
  margin-top: 20px;
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  width: 120px;
  height: 45px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  overflow: hidden;
}

.bakery-form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.forgot-password span {
  color: var(--primary-color);
  cursor: pointer;
}

.bakery-login-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
}

.bakery-login-footer {
  text-align: center;
  color: #666;
}

.bakery-link {
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>