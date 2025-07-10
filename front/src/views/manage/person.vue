<template>
  <div>
    <el-row>
      <el-col :span="8">
        <el-card>
          <!-- 卡片头部 -->
          <div style="padding: 10px 0; border-bottom: 1px solid #ccc; display: flex">
            <div style="flex: 1;font-size: 18px">个人信息</div>
            <div style="flex: 1; text-align: right">
              <el-button @click="edit">修改</el-button>
            </div>
          </div>
          <!-- 信息展示区域 -->
          <div style="padding: 10px 0; margin-top: 20px">
            <div>
              <!-- 头像上传 -->
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

            <!-- 用户信息 -->
            <div style="padding: 10px 0; display: flex">
              <div style="flex: 1">用户名：</div>
              <span style="flex: 5">{{ userInfo.username }}</span>
            </div>
            <div style="padding: 10px 0; display: flex">
              <div style="flex: 1">邮箱：</div>
              <div style="flex: 5">{{ userInfo.email }}</div>
            </div>
            <div style="padding: 10px 0; display: flex">
              <div style="flex: 1">电话：</div>
              <div style="flex: 5">{{ userInfo.phone }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑个人信息弹窗 -->
    <el-dialog title="个人信息" :visible.sync="dialogFormVisible" width="30%"
               :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form :model="entity">
        <el-form-item label="用户名" label-width="100px">
          <el-input v-model="entity.username" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" label-width="100px">
          <el-input v-model="entity.email" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="电话" label-width="100px">
          <el-input v-model="entity.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="密码" label-width="100px">
          <!-- 允许用户在此修改密码 -->
          <el-input show-password v-model="entity.password" autocomplete="off" style="width: 80%"></el-input>
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
import API from '../../utils/request'
const url = "/api/user/"

export default {
  name: "Person", // 组件名应为 Person 以匹配文件名
  data() {
    return {
      user: {},      // 当前登录用户，主要用于获取id
      userInfo: {},  // 从后端获取的完整用户信息，用于展示
      entity: {},    // 弹窗表单绑定的对象
      dialogFormVisible: false,
      uploadUrl: 'http://localhost:9999/files/upload', // 头像上传地址
      imageUrl: '',  // 上传后或加载的头像URL
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
    // 加载当前用户的详细信息
    load() {
      API.get(url + this.user.id).then(res => {
        this.userInfo = res.data || {};
        // 从返回数据构造完整的头像URL
        if (res.data && res.data.avatar) {
          this.imageUrl = "http://localhost:9999/files/" + res.data.avatar;
        }
      });
    },
    // 保存信息修改
    save() {
      API.put(url, this.entity).then(res => {
        if (res.code === '0') {
          this.$message.success("修改成功");
          this.dialogFormVisible = false;
          this.load();
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 打开编辑弹窗
    edit() {
      // 深拷贝一份用户信息到表单对象，避免直接修改展示数据
      this.entity = JSON.parse(JSON.stringify(this.userInfo));
      this.dialogFormVisible = true;
    },
    // 头像上传成功回调
    handleAvatarSuccess(res) {
      this.imageUrl = "http://localhost:9999/files/" + res.data;
      this.entity = JSON.parse(JSON.stringify(this.userInfo));
      this.entity.avatar = res.data; // 更新实体中的头像信息
      this.save(); // 立即保存头像更新
    },
    // 头像上传前校验
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
</style>
