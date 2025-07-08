<template>
  <div style="margin-top: 10px">
    <!-- 全部 -->
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="全部订单" name="all">
        <el-table :data="tableData" stripe style="width: 100%">
          <!-- 商品图片列，增加宽度并居中对齐 -->
          <el-table-column label="商品图片" width="150" align="center" header-align="center">
            <template slot-scope="scope">
              <a :href="['/front/goods?id=' + scope.row.goods.id]">
                <el-image :src="scope.row.goods.imgs[0]" style="width: 100px; height: 100px;" fit="contain"></el-image>
              </a>
            </template>
          </el-table-column>

          <!-- 商品名称列，增加宽度并居中对齐 -->
          <el-table-column prop="goods.name" label="商品名称" width="200" align="center" header-align="center"></el-table-column>

          <!-- 商品单价列，增加宽度并居中对齐 -->
          <el-table-column prop="goods.realPrice" label="商品单价" width="150" align="center" header-align="center"></el-table-column>

          <!-- 购买数量列，增加宽度并居中对齐 -->
          <el-table-column prop="count" label="购买数量" width="180" align="center" header-align="center">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.count" :min="1" :max="10" label="购买数量" style="width: 100px" @change="changeCount(scope.row)"></el-input-number>
            </template>
          </el-table-column>

          <!-- 加入时间列，增加宽度并居中对齐 -->
          <el-table-column prop="createTime" label="加入时间" width="200" align="center" header-align="center"></el-table-column>

          <!-- 操作列，增加宽度并居中对齐 -->
          <el-table-column fixed="right" label="操作" width="200" align="center" header-align="center">
            <template slot-scope="scope">
              <el-button type="danger" icon="el-icon-delete" circle @click="del(scope.row.id)"></el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 10px">
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
      </el-tab-pane>

      <!-- 待付款 -->
      <el-tab-pane label="待付款" name="待付款">
        <el-table :data="tableData" stripe style="width: 100%">
          <!-- 商品图片列，增加宽度并居中对齐 -->
          <el-table-column label="商品图片" width="150" align="center" header-align="center">
            <template slot-scope="scope">
              <a :href="['/front/goods?id=' + scope.row.goods.id]">
                <el-image :src="scope.row.goods.imgs[0]" style="width: 100px; height: 100px;" fit="contain"></el-image>
              </a>
            </template>
          </el-table-column>

          <!-- 商品名称列，增加宽度并居中对齐 -->
          <el-table-column prop="goods.name" label="商品名称" width="200" align="center" header-align="center"></el-table-column>

          <!-- 商品单价列，增加宽度并居中对齐 -->
          <el-table-column prop="goods.realPrice" label="商品单价" width="150" align="center" header-align="center"></el-table-column>

          <!-- 购买数量列，增加宽度并居中对齐 -->
          <el-table-column prop="count" label="购买数量" width="180" align="center" header-align="center">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.count" :min="1" :max="10" label="购买数量" style="width: 100px" @change="changeCount(scope.row)"></el-input-number>
            </template>
          </el-table-column>

          <!-- 操作列，增加宽度并居中对齐 -->
          <el-table-column fixed="right" label="操作" width="200" align="center" header-align="center">
            <template slot-scope="scope">
              <el-button type="primary" @click="pay(scope.row.id)" v-if="scope.row.state === '待付款'">付款</el-button>
              <el-button type="danger" @click="cancel(scope.row.id)" v-if="scope.row.state === '待付款'">取消</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 10px">
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
      </el-tab-pane>

      <!-- 待收货 -->
      <el-tab-pane label="待收货" name="待收货">
        <el-table :data="tableData" stripe style="width: 100%">
          <el-table-column label="商品图片" width="150" align="center" header-align="center">
            <template slot-scope="scope">
              <a :href="['/front/goods?id=' + scope.row.goods.id]">
                <el-image :src="scope.row.goods.imgs[0]" style="width: 100px; height: 100px;" fit="contain"></el-image>
              </a>
            </template>
          </el-table-column>

          <el-table-column label="商品名称" width="200" align="center" header-align="center"></el-table-column>

          <el-table-column label="商品单价" width="150" align="center" header-align="center"></el-table-column>

          <el-table-column label="操作" fixed="right" width="200" align="center" header-align="center">
            <template slot-scope="scope">
              <el-button type="primary" @click="confirm(scope.row.id)" v-if="scope.row.state === '待收货'">确认收货</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 10px">
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
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "cart",
  data() {
    return {
      user: {},
      pageNum: 1,
      pageSize: 10,
      total: 0,
      activeName: 'all',
      tableData: [],
      payData: [],
      commentData: [],
      entity: {},
      state: 'all',
      dialogFormVisible: false,
      logisticsVisible: false,
      logisticsData: []
    }
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    if (!this.user.id) {
      this.$message({
        type: 'warning',
        message: '请登录！'
      })
      return
    }
    this.load()
  },
  methods: {
    preComment(id) {
      this.entity = {}
      this.entity.foreignId = id
      this.dialogFormVisible = true
    },
    comment() {
      if (!this.entity.content) {
        this.$message({
          type: 'warning',
          message: '请填写内容'
        })
        return
      }
      API.post("/api/message", this.entity).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '评价成功'
          })
          this.dialogFormVisible = false
          this.load()
        }
      })
    },
    confirm(id) {
      API.put("/api/order", {id: id, state: '已完成'}).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.load()
        }
      })
    },
    load() {
      API.get("/api/order/page/front", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          state: this.state
        }
      }).then(res => {
        this.tableData = res.data.records
      })
    }
  }
}
</script>

<style scoped>
.el-table-column {
  text-align: center;  /* 将所有列内容居中 */
}

.el-table__header {
  text-align: center;  /* 将表头内容居中 */
}

.el-table__body {
  text-align: center;  /* 将表格主体内容居中 */
}
</style>
