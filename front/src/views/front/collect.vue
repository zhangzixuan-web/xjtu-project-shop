<template>
  <div>
    <el-table :data="tableData" border stripe style="width: 100%; margin-top: 10px"
              header-align="center"> <!-- 表头文字居中 -->
      <el-table-column label="商品名称" width="250" align="center"> <!-- 内容居中 -->
        <template slot-scope="scope">
          <a :href="['/front/goods?id=' + scope.row.goodsId]">{{ scope.row.goodsName }}</a>
        </template>
      </el-table-column>

      <el-table-column prop="goodsImg" label="商品图片" width="200" align="center"> <!-- 内容居中 -->
        <template slot-scope="scope">
          <el-image :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"
                    style="width: 100%; height: 100px" fit="contain"></el-image>
        </template>
      </el-table-column>

      <el-table-column prop="createTime" label="收藏时间" width="250" align="center"> <!-- 内容居中 -->
      </el-table-column>

      <el-table-column fixed="right" label="操作" width="250" align="center"> <!-- 内容居中 -->
        <template slot-scope="scope">
          <el-popconfirm @confirm="del(scope.row.id)" title="确定取消收藏吗？">
            <el-button type="danger" slot="reference" style="margin-left: 10px">取消收藏</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 10px; background-color: white; padding: 10px 0;">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[2, 5, 10]"
          layout="prev, pager, next"
          :total="total"
      >
      </el-pagination>
    </div>

    <!-- 弹窗 -->
    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form :model="entity">
        <el-form-item label="商品名称" label-width="150px">
          <el-input v-model="entity.goodsName" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="商品图片" label-width="150px">
          <el-input v-model="entity.goodsImg" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="商品id" label-width="150px">
          <el-input v-model="entity.goodsId" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="用户id" label-width="150px">
          <el-input v-model="entity.userId" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="收藏时间" label-width="150px">
          <el-date-picker style="width: 80%" v-model="entity.createTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间"></el-date-picker>
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
const url = "/api/collect/"

export default {
  name: "Collect",
  data() {
    return {
      fileList: [],
      options: [],
      text: '',
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      entity: {},
      total: 0,
      dialogFormVisible: false
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.$emit('user', this.user);
    this.load()
  },
  methods: {
    fileSuccessUpload(res) {
      this.entity.file = "http://localhost:9999/files/" + res.data;
      this.fileList = [res.data]
      console.log(res)
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    load() {
      API.get(url + "/page/front", {
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
      this.fileList = []
      this.dialogFormVisible = true
    },
    edit(obj) {
      this.entity = JSON.parse(JSON.stringify(obj))
      this.fileList = []
      this.dialogFormVisible = true
    },
    save() {
      if (!this.entity.id) {
        API.post(url, this.entity).then(res => {
          if (res.code === '0') {
            this.$message({
              type: "success",
              message: "操作成功"
            })
          } else {
            this.$message({
              type: "error",
              message: res.msg
            })
          }
          this.load()
          this.dialogFormVisible = false
        })
      } else {
        API.put(url, this.entity).then(res => {
          if (res.code === '0') {
            this.$message({
              type: "success",
              message: "操作成功"
            })
          } else {
            this.$message({
              type: "error",
              message: res.msg
            })
          }
          this.load()
          this.dialogFormVisible = false
        })
      }
    },
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
.el-table-column {
  text-align: center; /* 将所有列内容居中 */
}

.el-table__header {
  text-align: center; /* 将表头内容居中 */
}

.el-table__body {
  text-align: center; /* 将表格主体内容居中 */
}
</style>
