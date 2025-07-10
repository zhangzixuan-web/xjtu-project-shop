<template>
  <div>
    <!-- 操作区域 -->
    <div style="padding: 5px 0">
      <el-button @click="add" type="primary" size="mini" style="margin: 10px 0">新增</el-button>
    </div>

    <!-- 公告数据表格 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="100"> </el-table-column>
      <el-table-column prop="title" label="标题"> </el-table-column>
      <el-table-column prop="content" label="内容"> </el-table-column>
      <el-table-column prop="time" label="发布时间"> </el-table-column>

      <!-- 操作列 -->
      <el-table-column
          fixed="right"
          label="操作"
          width="200">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" circle  @click="edit(scope.row)"></el-button>
          <el-popconfirm
              @confirm="del(scope.row.id)"
              title="确定删除？"
          >
            <el-button type="danger" icon="el-icon-delete" circle slot="reference" style="margin-left: 10px"></el-button>
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
    <el-dialog title="公告信息" :visible.sync="dialogFormVisible" width="40%"
               :close-on-click-modal="false">
      <el-form :model="entity" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="entity.title" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" :rows="5" v-model="entity.content" autocomplete="off" style="width: 80%"></el-input>
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
const url = "/api/notice/"

export default {
  name: "Notice",
  data() {
    return {
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      entity: {},
      dialogFormVisible: false,
      total: 0
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
    // 加载公告列表
    load() {
       API.get(url + "/page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: '' // 该参数在后端未使用，但保留以保持接口一致性
          }
       }).then(res => {
          this.tableData = res.data.records || [];
          this.total = res.data.total;
       });
    },
    // 打开新增弹窗
    add() {
      this.entity = {};
      this.dialogFormVisible = true;
    },
    // 打开编辑弹窗
    edit(obj) {
      this.entity = JSON.parse(JSON.stringify(obj));
      this.dialogFormVisible = true;
    },
    // 保存（新增或修改）
    save() {
      const apiCall = this.entity.id ? API.put(url, this.entity) : API.post(url, this.entity);
      apiCall.then(res => {
        if (res.code === '0') {
          this.$message.success("操作成功");
          this.dialogFormVisible = false;
          this.load();
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 删除公告
    del(id) {
      API.delete(url + id).then(res => {
        this.$message({
          type: "success",
          message: "操作成功"
        })
        this.load()
      })
    }
  },
};
</script>

<style scoped>
</style>
