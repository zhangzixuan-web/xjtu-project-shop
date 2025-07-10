<template>
  <div style="margin-top: 10px">
    <el-row>
      <el-col :span="24">
        <el-card>
          <!-- 卡片头部：标题和操作按钮 -->
          <div style="padding: 10px 0; border-bottom: 1px solid #ccc; display: flex">
            <div style="flex: 1;font-size: 20px">个人信息</div>
            <div style="flex: 1; text-align: right">
              <el-button @click="recharge">充值</el-button>
              <el-button @click="edit">修改</el-button>
            </div>
          </div>
          <!-- 个人信息展示区域 -->
          <el-row>
            <el-col :span="10" :offset="10">
              <div style="padding: 10px 0; margin-top: 20px">
                <!-- 头像上传组件 -->
                <div style="padding-left: 40px">
                  <el-upload
                      class="avatar-uploader"
                      :action='uploadUrl'
                      :show-file-list="false"
                      :on-success="handleAvatarSuccess"
                      :before-upload="beforeAvatarUpload">
                    <img v-if="imageUrl" :src="imageUrl" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                  </el-upload>
                </div>
                <!-- 个人信息表单 -->
                <el-form label-width="100px">
                  <el-form-item label="用户名：" >
                    <div>{{ userInfo.username }}</div>
                  </el-form-item>
                  <el-form-item label="昵称：">
                    <div>{{ userInfo.nickName }}</div>
                  </el-form-item>
                  <el-form-item label="邮箱：">
                    <div>{{ userInfo.email }}</div>
                  </el-form-item>
                  <el-form-item label="电话：">
                    <div>{{ userInfo.phone }}</div>
                  </el-form-item>
                  <el-form-item label="地址：">
                    <div>{{ userInfo.address }}</div>
                  </el-form-item>
                  <el-form-item label="年龄：">
                    <div>{{ userInfo.age }}</div>
                  </el-form-item>
                  <el-form-item label="账户余额：">
                    <div>￥ {{ userInfo.account }}</div>
                  </el-form-item>
                </el-form>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改个人信息弹窗 -->
    <el-dialog title="个人信息" :visible.sync="dialogFormVisible" width="30%"
               :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form :model="entity">
        <el-form-item label="昵称" label-width="100px">
          <el-input v-model="entity.nickName" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" label-width="100px">
          <el-input v-model="entity.email" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="电话" label-width="100px">
          <el-input v-model="entity.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="地址" label-width="100px">
          <el-input v-model="entity.address" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="年龄" label-width="100px">
          <el-input v-model="entity.age" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="密码" label-width="100px">
          <el-input show-password v-model="entity.password" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 充值弹窗 -->
    <el-dialog title="充值" :visible.sync="vis" width="30%"
               :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form>
        <el-form-item label="金额" label-width="100px">
          <el-input v-model="money" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="vis = false">取 消</el-button>
        <el-button type="primary" @click="doRecharge">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '../../utils/request'

const url = "/api/user/"

export default {
  name: "Person", // 组件名应为 Person
  data() {
    return {
      user: {},              // 从 sessionStorage 获取的基础用户信息
      userInfo: {},          // 从后端获取的详细用户信息
      entity: {},            // 用于编辑时绑定的数据对象
      dialogFormVisible: false, // 控制编辑弹窗的显示
      vis: false,            // 控制充值弹窗的显示
      uploadUrl: 'http://localhost:9999/files/upload', // 头像上传地址
      imageUrl: '',          // 上传后或加载的头像URL
      money: 0               // 充值金额
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
    // 执行充值
    doRecharge() {
      API.put(url + "account/" + this.money).then(res => {
        if (res.code === '0') {
          this.$message.success("充值成功");
          this.load(); // 成功后重新加载用户信息以更新余额
        } else {
          this.$message.error(res.msg);
        }
        this.vis = false; // 关闭弹窗
      })
    },
    // 打开充值弹窗
    recharge() {
      this.vis = true;
      this.money = 0;
    },
    // 加载用户详细信息
    load() {
      API.get(url + this.user.id).then(res => {
        this.userInfo = res.data || {};
        // 拼接完整的头像 URL
        this.imageUrl = "http://localhost:9999/files/" + res.data.avatar;
      })
    },
    // 保存修改后的信息
    save() {
      API.put(url, this.entity).then(res => {
        if (res.code === '0') {
          this.$message.success("修改成功");
          this.dialogFormVisible = false;
          this.load(); // 重新加载以显示更新后的信息
        } else {
          this.$message.error(res.msg);
        }
      })
    },
    // 打开编辑弹窗
    edit() {
      // 深拷贝一份 userInfo，避免在弹窗中修改时直接影响页面显示
      this.entity = JSON.parse(JSON.stringify(this.userInfo));
      this.dialogFormVisible = true;
    },
    // 头像上传成功后的回调
    handleAvatarSuccess(res) {
      this.imageUrl = res.data; // 更新显示的头像
      this.entity = JSON.parse(JSON.stringify(this.userInfo));
      this.entity.avatar = res.data; // 更新待保存数据中的头像
      this.save(); // 调用保存方法，将新的头像信息更新到后端
    },
    // 上传前的校验
    beforeAvatarUpload(file) {
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isLt2M;
    }
  },
};
</script>

<style scoped>
/* 头像上传组件样式 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}

.el-form-item {
  margin-bottom: 10px;
}
</style>