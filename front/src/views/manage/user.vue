<template>
  <div>
    <!-- 搜索和新增区域 -->
    <div style="padding: 5px 0">
      <el-input v-model="text" @keyup.enter.native="load" style="width: 200px" placeholder="请输入名称"> <i slot="prefix" class="el-input__icon el-icon-search"></i></el-input>
      <el-button @click="add" type="primary" size="mini" style="margin: 10px">新增</el-button>
    </div>

    <!-- 用户数据表格 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phone" label="电话"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
      <!-- 角色分配列 -->
      <el-table-column label="角色">
        <template slot-scope="scope">
          <!-- 下拉多选框，用于为用户分配角色 -->
          <el-select v-model="scope.row.role" value-key="id" multiple placeholder="请选择" @change="changeRole(scope.row)">
            <el-option
                v-for="item in options"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column
          fixed="right"
          label="操作"
          width="200">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" circle @click="edit(scope.row)"></el-button>
          <el-popconfirm
              @confirm="del(scope.row.id)"
              title="确定删除？"
          >
            <el-button type="danger" icon="el-icon-delete" circle slot="reference"
                       style="margin-left: 10px"></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 10px">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[2, 5, 10]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
      >
      </el-pagination>
    </div>

    <!-- 新增/编辑用户信息的弹窗 -->
    <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%"
               :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form :model="entity">
        <el-form-item label="用户名" label-width="150px">
          <el-input v-model="entity.username" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="昵称" label-width="150px">
          <el-input v-model="entity.nickName" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" label-width="150px">
          <el-input v-model="entity.email" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="电话" label-width="150px">
          <el-input v-model="entity.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="地址" label-width="150px">
          <el-input v-model="entity.address" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="年龄" label-width="150px">
          <el-input v-model="entity.age" autocomplete="off" style="width: 80%"></el-input>
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
  name: "User",
  data() {
    return {
      options: [],      // 角色列表，用于下拉框
      text: '',         // 搜索文本
      tableData: [],    // 用户数据
      pageNum: 1,
      pageSize: 10,
      total: 0,
      entity: {},       // 弹窗表单绑定的对象
      dialogFormVisible: false
    };
  },
  created() {
    this.load();
  },
  methods: {
    // 当用户角色发生变化时
    changeRole(row) {
      this.entity = JSON.parse(JSON.stringify(row));
      // 直接调用 save 方法保存更改
      this.save();
    },
    // 处理每页显示条数变化
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    // 处理页码变化
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    // 加载用户列表和角色列表
    load() {
      // 获取用户分页数据
      API.get(url + "/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.text
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total
      });

      // 获取所有角色数据，用于下拉框
      API.get("/api/role").then(res => {
        this.options = res.data
      });
    },
    // 打开新增弹窗
    add() {
      this.entity = {}
      this.dialogFormVisible = true
    },
    // 打开编辑弹窗
    edit(obj) {
      this.entity = JSON.parse(JSON.stringify(obj))
      this.dialogFormVisible = true
    },
    // 保存（新增或修改）
    save() {
      // 根据有无id判断是新增还是修改
      const apiCall = this.entity.id ? API.put(url, this.entity) : API.post(url, this.entity);
      apiCall.then(res => {
        if (res.code === '0') {
          this.$message.success("操作成功");
          this.load();
          this.dialogFormVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 删除用户
    del(id) {
      API.delete(url + id).then(res => {
        if (res.code === '0') {
          this.$message.success("操作成功");
          this.load();
        } else {
          this.$message.error(res.msg);
        }
      })
    }
  },
};
</script>

<style scoped>
</style>
