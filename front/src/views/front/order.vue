<template>
  <div>
    <!-- 订单状态筛选标签页 -->
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待支付" name="待支付"></el-tab-pane>
      <el-tab-pane label="待发货" name="待发货"></el-tab-pane>
      <el-tab-pane label="待收货" name="待收货"></el-tab-pane>
      <el-tab-pane label="已完成" name="已完成"></el-tab-pane>
    </el-tabs>

    <!-- 如果有订单数据，则显示表格 -->
    <div v-if="tableData && tableData.length">

      <!-- 订单列表主表格 -->
      <el-table :data="tableData" stripe style="width: 100%">

        <!-- 可展开行，用于显示订单内的商品详情 -->
        <el-table-column type="expand">
          <template slot-scope="props">
            <h4>订单商品详情</h4>
            <!-- 内嵌表格，显示该订单的所有商品 -->
            <el-table :data="props.row.carts" :show-header="false">
              <!-- 商品图片 -->
              <el-table-column width="150" align="center">
                <template slot-scope="scope">
                  <a :href="['/front/goods?id=' + scope.row.goods.id]">
                    <el-image v-if="scope.row.goods.imgs && scope.row.goods.imgs.length"
                              :src="scope.row.goods.imgs[0]"
                              style="width: 80px; height: 80px;"
                              fit="contain"></el-image>
                  </a>
                </template>
              </el-table-column>
              <!-- 商品名称、单价、数量 -->
              <el-table-column prop="goods.name" label="商品名称" width="200" align="center"></el-table-column>
              <el-table-column prop="goods.realPrice" label="单价" width="150" align="center"></el-table-column>
              <el-table-column prop="count" label="数量" width="180" align="center"></el-table-column>
              <!-- 操作：去评价 -->
              <el-table-column label="操作" align="center">
                <template slot-scope="scope" v-if="props.row.state === '已完成'">
                  <el-button type="primary" size="mini" @click="preComment(scope.row.goods.id)">去评价</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>

        <!-- 主表格的列定义 -->
        <el-table-column prop="orderNo" label="订单编号" width="220" align="center"></el-table-column>
        <el-table-column prop="totalPrice" label="订单总价" width="150" align="center"></el-table-column>
        <el-table-column prop="state" label="订单状态" width="150" align="center"></el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="200" align="center"></el-table-column>

        <!-- 主表格的操作列 -->
        <el-table-column fixed="right" label="操作" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.state === '待支付'" type="warning" size="mini">去支付</el-button>
            <el-button v-if="scope.row.state === '待收货' || scope.row.state === '已完成'"
                       type="primary"
                       size="mini"
                       @click="showLogistics(scope.row.id)">
              查看物流
            </el-button>
            <el-button v-if="scope.row.state === '待收货'"
                       type="success"
                       size="mini"
                       @click="confirm(scope.row.id)">
              确认收货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

    </div>

    <!-- 如果没有数据，显示空状态提示 -->
    <div v-else>
      <el-empty description="暂无订单数据"></el-empty>
    </div>

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

    <!-- 评价弹窗 -->
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

    <!-- 物流信息弹窗 -->
    <el-dialog title="物流详情" :visible.sync="logisticsVisible" width="60%">
      <!-- 如果有物流数据，则显示地图和时间线 -->
      <div v-if="logisticsData && logisticsData.length > 0">
        <!-- 物流地图组件 -->
        <LogisticsMap :tracking-data="logisticsData" />
      </div>
      <!-- 如果没有物流数据，显示空状态 -->
      <el-empty v-else description="暂无物流信息"></el-empty>
    </el-dialog>

  </div>
</template>

<script>
import API from "@/utils/request";
import LogisticsMap from '@/components/LogisticsMap.vue'; // 引入自定义的物流地图组件

export default {
  name: "Order",
  components: {
    LogisticsMap // 注册物流地图组件
  },
  data() {
    return {
      user: {},
      pageNum: 1,
      pageSize: 10,
      total: 0,
      activeName: 'all', // 当前选中的 Tab 名称
      tableData: [],     // 订单列表数据
      commentData: [],   // 评价数据（似乎未被充分使用）
      entity: {},        // 用于存储评价内容的实体
      state: 'all',      // 当前筛选的订单状态
      dialogFormVisible: false, // 评价弹窗的显示状态
      logisticsVisible: false, // 物流弹窗的显示状态
      logisticsData: []      // 物流轨迹数据
    }
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    if (!this.user.id) {
      this.$message.warning('请登录！');
      return;
    }
    this.load();
  },
  methods: {
    // 查看物流的方法
    async showLogistics(orderId) {
      try {
        const res = await API.get("/api/order/" + orderId + "/tracking");
        this.logisticsData = res; // 假设 res.data 是物流数据数组
      } catch (e) {
        console.error("获取物流信息失败:", e);
        this.logisticsData = []; // 出错时清空数据
        this.$message.error('获取物流信息失败，请稍后重试');
      }
      this.logisticsVisible = true; // 打开弹窗
    },

    // 准备评价，打开弹窗
    preComment(id) {
      this.entity = {}
      this.entity.foreignId = id // 设置评价关联的商品ID
      this.dialogFormVisible = true
    },
    // 提交评价
    comment() {
      if (!this.entity.content) {
        this.$message.warning('请填写内容');
        return
      }
      API.post("/api/message", this.entity).then(res => {
        if (res.code === '0') {
          this.$message.success('评价成功');
          this.dialogFormVisible = false
          this.load() // 重新加载订单，可能会更新评价状态
        }
      })
    },
    // 确认收货
    confirm(id) {
      API.put("/api/order", {id: id, state: '已完成'}).then(res => {
        if (res.code === '0') {
          this.$message.success('操作成功');
          this.load() // 重新加载订单列表
        }
      })
    },
    // 点击 Tab 切换订单状态
    handleClick(tab) {
      this.state = tab.name;
      this.pageNum = 1; // 切换状态时重置到第一页
      this.load();
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
    // 加载订单数据
    load() {
      // 当 state 为 'all' 时，传给后端空字符串，表示查询全部
      const queryState = this.state === 'all' ? '' : this.state;

      API.get("/api/order/page/front", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          state: queryState
        }
      }).then(res => {
        this.tableData = res.data.records;
        this.total = res.data.total;

        this.commentData = []; // 每次加载前清空

        this.tableData.forEach(v => {
          // 安全地解析购物车 JSON 字符串
          try {
            if (v.carts && typeof v.carts === 'string') {
              v.carts = JSON.parse(v.carts);
              v.carts.forEach(c => {
                if (c.goods.imgs && typeof c.goods.imgs === 'string') {
                  c.goods.imgs = JSON.parse(c.goods.imgs);
                }
              });
            }
          } catch (e) {
            console.error("解析订单中的 carts 数据失败:", e);
            v.carts = []; // 解析失败则置为空数组，防止页面渲染错误
          }
        });
      })
    }
  }
}
</script>
<style scoped>
/* 增加一些样式来美化内嵌表格 */
.el-table__expanded-cell {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
