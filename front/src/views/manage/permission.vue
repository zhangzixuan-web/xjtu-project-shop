<template>
  <div>
    <!-- 搜索和新增区域 -->
    <div style="padding: 5px 0">
      <el-input v-model="text" @keyup.enter.native="load" style="width: 200px"> <i slot="prefix" class="el-input__icon el-icon-search"></i></el-input>
      <el-button @click="add" type="primary" size="mini" style="margin: 10px">新增</el-button>
    </div>

    <!-- 权限（菜单）数据表格 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="path" label="页面路径"></el-table-column>
      <el-table-column prop="icon" label="图标"></el-table-column>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog title="菜单信息" :visible.sync="dialogFormVisible" width="30%"
               :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form :model="entity">
        <el-form-item label="名称" label-width="150px">
          <el-input v-model="entity.name" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="描述" label-width="150px">
          <el-input v-model="entity.description" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="页面路径" label-width="150px">
          <el-input v-model="entity.path" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="图标" label-width="150px">
          <el-input v-model="entity.icon" autocomplete="off" style="width: 80%"></el-input>
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
import {resetRouter} from "@/router";
import {setMenu} from "@/utils/manage";

const url = "/api/permission/"

export default {
  name: "Permission",
  data() {
    return {
      text: '',
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      entity: {},
      dialogFormVisible: false
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    load() {
      API.get(url + "/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.text
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total
      })
    },
    add() {
      this.entity = {}
      this.dialogFormVisible = true
    },
    edit(obj) {
      this.entity = JSON.parse(JSON.stringify(obj))
      this.dialogFormVisible = true
    },
    save() {
      const apiCall = this.entity.id ? API.put(url, this.entity) : API.post(url, this.entity);
      apiCall.then(res => {
        if (res.code === '0') {
          this.$message.success("操作成功");
          this.dialogFormVisible = false;
          this.load();
          this.updateUserAndMenu(); // 更新菜单
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    del(id) {
      API.delete(url + id).then(res => {
        if (res.code === '0') {
          this.$message.success("操作成功");
          this.load();
          this.updateUserAndMenu(); // 更新菜单
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 更新用户权限和菜单
    updateUserAndMenu() {
      // 重新请求用户最新信息，包括权限
      API.get("/api/user/" + this.user.id).then(res => {
        if (res.code === '0') {
          const newUser = res.data;
          // 为了保持登录状态，需要保留 token
          newUser.token = this.user.token;
          sessionStorage.setItem("user", JSON.stringify(newUser)); // 更新缓存
          this.user = newUser;
          
          // 重置路由并设置新菜单
          resetRouter(JSON.parse(JSON.stringify(newUser.permission || [])));
          // setMenu 已经被弃用，通过触发事件来让布局组件更新菜单
          this.$emit('call');
        }
      });
    }
  },
};
</script>

<style scoped>
</style>
