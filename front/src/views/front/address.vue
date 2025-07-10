<template>
  <div>
    <!-- "新增"按钮 -->
    <div style="padding: 5px 0; display: flex; justify-content: flex-start;">
      <el-button @click="add" type="primary" size="mini" style="margin: 10px">新增</el-button>
    </div>

    <!-- 地址列表表格 -->
    <el-table :data="tableData" border stripe style="width: 100%; text-align: center;">
      <!-- 列定义 -->
      <el-table-column prop="id" label="ID" width="150" sortable align="center" header-align="center"></el-table-column>
      <el-table-column prop="linkUser" label="联系人" width="200" align="center" header-align="center"></el-table-column>
      <el-table-column prop="linkAddress" label="联系地址" width="250" align="center" header-align="center"></el-table-column>
      <el-table-column prop="linkPhone" label="联系电话" width="200" align="center" header-align="center"></el-table-column>
      <el-table-column prop="userId" label="关联用户id" width="200" align="center" header-align="center"></el-table-column>

      <!-- 操作列 -->
      <el-table-column fixed="right" label="操作" width="250" align="center" header-align="center">
        <template slot-scope="scope">
          <div style="display: flex; justify-content: center; gap: 10px;">
            <!-- 修改按钮 -->
            <el-button type="primary" icon="el-icon-edit" circle @click="edit(scope.row)"></el-button>
            <!-- 删除按钮，使用 Popconfirm 进行二次确认 -->
            <el-popconfirm
                @confirm="del(scope.row.id)"
                title="确定删除？"
            >
              <el-button type="danger" icon="el-icon-delete" circle slot="reference"></el-button>
            </el-popconfirm>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div style="margin-top: 10px; text-align: center;">
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

    <!-- 新增/编辑地址信息的弹窗 -->
    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form :model="entity">
        <el-form-item label="联系人" label-width="100px">
          <el-input v-model="entity.linkUser" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="联系地址" label-width="100px">
          <el-input v-model="entity.linkAddress" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" label-width="100px">
          <el-input v-model="entity.linkPhone" autocomplete="off" style="width: 80%"></el-input>
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
const url = "/api/address/"

export default {
  name: "Address",
  data() {
    return {
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      entity: {},
      total: 0,
      dialogFormVisible: false,
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
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
    // 加载地址列表数据
    load() {
      if (!this.user.id) {
        this.$message.warning("请登录")
        return
      }
      // 请求当前用户的地址列表（分页）
      API.get(url + "/page/front", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total
      })
    },
    // 打开新增弹窗
    add() {
      if (!this.user.id) {
        this.$message.warning("请登录")
        return
      }
      // 初始化 entity 对象，并关联当前用户
      this.entity = { userId: this.user.id }
      this.dialogFormVisible = true
    },
    // 打开编辑弹窗
    edit(obj) {
      // 深拷贝选中的地址信息到 entity
      this.entity = JSON.parse(JSON.stringify(obj))
      // 确保 userId 正确
      this.entity.userId = this.user.id
      this.dialogFormVisible = true
    },
    // 保存地址（新增或修改）
    save() {
      // 根据 entity 是否有 id 来判断是新增还是修改
      if (!this.entity.id) { // 新增
        API.post(url, this.entity).then(res => {
          if (res.code === '0') {
            this.$message.success("操作成功");
          } else {
            this.$message.error(res.msg);
          }
          this.load(); // 重新加载列表
          this.dialogFormVisible = false;
        })
      } else { // 修改
        API.put(url, this.entity).then(res => {
          if (res.code === '0') {
            this.$message.success("操作成功");
          } else {
            this.$message.error(res.msg);
          }
          this.load(); // 重新加载列表
          this.dialogFormVisible = false;
        })
      }
    },
    // 删除地址
    del(id) {
      API.delete(url + id).then(res => {
        // 这里假设删除总能成功，可以根据后端返回值进行更精细的判断
        this.$message.success("操作成功");
        this.load(); // 重新加载列表
      })
    }
  },
};
</script>

<style scoped>
</style>
