<template>
  <div style="margin-top: 10px">
    <!-- 全部 -->
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待支付" name="待支付"></el-tab-pane>
      <el-tab-pane label="待发货" name="待发货"></el-tab-pane>
      <el-tab-pane label="待收货" name="待收货"></el-tab-pane>
      <el-tab-pane label="已完成" name="已完成"></el-tab-pane>
    </el-tabs>

    <!-- 订单主表格 -->
    <el-table :data="tableData" stripe style="width: 100%" v-if="tableData && tableData.length">

      <!-- 展开行，用于显示订单内的商品列表 -->
      <el-table-column type="expand">
        <template slot-scope="props">
          <h4>订单商品详情</h4>
          <!-- 这是一个内嵌的表格，显示该订单的所有商品 -->
          <el-table :data="props.row.carts" :show-header="false">
            <el-table-column width="150" align="center">
              <template slot-scope="scope">
                <a :href="['/front/goods?id=' + scope.row.goods.id]">
                  <!-- 增加 v-if 判断，确保 imgs 数组存在且有内容 -->
                  <el-image v-if="scope.row.goods.imgs && scope.row.goods.imgs.length"
                            :src="scope.row.goods.imgs[0]"
                            style="width: 80px; height: 80px;"
                            fit="contain"></el-image>
                </a>
              </template>
            </el-table-column>
            <el-table-column prop="goods.name" label="商品名称" width="200" align="center"></el-table-column>
            <el-table-column prop="goods.realPrice" label="单价" width="150" align="center"></el-table-column>
            <el-table-column prop="count" label="数量" width="180" align="center"></el-table-column>
            <!-- 评价按钮只在“已完成”状态下显示 -->
            <el-table-column label="操作" align="center">
              <template slot-scope="scope" v-if="props.row.state === '已完成'">
                <el-button type="primary" size="mini" @click="preComment(scope.row.goods.id)">去评价</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>

      <!-- 主表格的列 -->
      <el-table-column prop="orderNo" label="订单编号" width="220" align="center"></el-table-column>
      <el-table-column prop="totalPrice" label="订单总价" width="150" align="center"></el-table-column>
      <el-table-column prop="state" label="订单状态" width="150" align="center"></el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="200" align="center"></el-table-column>

      <el-table-column fixed="right" label="操作" align="center">
        <template slot-scope="scope">
          <!-- 根据订单状态显示不同按钮 -->
          <el-button v-if="scope.row.state === '待支付'" type="warning" size="mini">去支付</el-button>
          <el-button v-if="scope.row.state === '待收货'" type="success" size="mini" @click="confirm(scope.row.id)">确认收货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 数据为空时的提示 -->
    <el-empty v-else description="暂无订单数据"></el-empty>

    <!-- 分页组件 -->
    <div style="margin-top: 20px; text-align: center;">
      <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 评价弹窗 (你的代码里应该有，这里我补充一个基本结构) -->
    <el-dialog title="商品评价" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="entity" label-width="80px">
        <el-form-item label="评价内容">
          <el-input v-model="entity.content" autocomplete="off" type="textarea" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="comment">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  // name 属性在组件中应该是唯一的，并且遵循 PascalCase 或 kebab-case 规范
  name: "Cart",

  data() {
    return {
      user: {},
      pageNum: 1,
      pageSize: 10,
      total: 0,
      activeName: 'all', // 用于 el-tabs 的 v-model
      tableData: [],
      payData: [],
      commentData: [],
      entity: {},
      state: 'all',      // 用于 API 请求的状态参数
      dialogFormVisible: false,
      logisticsVisible: false,
      logisticsData: []
    };
  },

  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {};
    if (!this.user.id) {
      this.$message({
        type: 'warning',
        message: '请登录！'
      });
      return;
    }
    this.load();
  },

  methods: {
    // --- 你原有的方法 ---
    preComment(id) {
      this.entity = {};
      this.entity.foreignId = id;
      this.dialogFormVisible = true;
    },

    comment() {
      if (!this.entity.content) {
        this.$message({
          type: 'warning',
          message: '请填写内容'
        });
        return;
      }
      API.post("/api/message", this.entity).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '评价成功'
          });
          this.dialogFormVisible = false;
          this.load();
        }
      });
    },

    confirm(id) {
      API.put("/api/order", {id: id, state: '已完成'}).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '操作成功'
          });
          this.load();
        }
      });
    },

    // 这是你主要的加载数据方法，保留它
    load() {
      let state = this.state === 'all' ? '' : this.state;
      this.commentData = [];
      API.get("/api/order/page/front", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          state: state
        }
      }).then(res => {
        // 增加一个安全检查，防止 res.data 为空时报错
        if (res.data) {
          this.tableData = res.data.records;
          this.total = res.data.total;

          this.tableData.forEach(v => {
            // 增加一个检查，确保 v.carts 存在且为有效 JSON 字符串
            if (v.carts && typeof v.carts === 'string') {
              v.carts = JSON.parse(v.carts);
            } else {
              v.carts = []; // 如果数据有问题，给一个空数组，避免后续 forEach 报错
            }

            v.carts.forEach(item => {
              if (!item.goods.imgs) {
                item.goods.imgs = [''];
              } else {
                item.goods.imgs = JSON.parse(item.goods.imgs);
              }

              if (this.state === '已完成') {
                item.goods.orderNo = v.orderNo;
                this.commentData.push(item.goods);
              }
            });
          });
        }
      });
    },

    // --- 新增的、之前缺失的方法 ---

    // 这个方法处理 el-tabs 的点击事件
    handleClick(tab) {
      console.log('标签页切换:', tab.name);
      this.state = tab.name; // 将当前状态设置为点击的标签页的 name
      this.pageNum = 1;      // 切换标签页后，重置到第一页
      this.load();           // 重新加载数据
    },

    // 这个方法处理 el-pagination 的每页条数变化
    handleSizeChange(newPageSize) {
      console.log(`每页显示条数变为: ${newPageSize}`);
      this.pageSize = newPageSize;
      this.load(); // 重新加载数据
    },

    // 这个方法处理 el-pagination 的页码变化
    handleCurrentChange(newPageNum) {
      console.log(`当前页码变为: ${newPageNum}`);
      this.pageNum = newPageNum;
      this.load(); // 重新加载数据
    }
  }
};
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
