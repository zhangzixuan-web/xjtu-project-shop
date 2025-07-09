<template>
  <div>
    <!-- 标签页 -->
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待支付" name="待支付"></el-tab-pane>
      <el-tab-pane label="待发货" name="待发货"></el-tab-pane>
      <el-tab-pane label="待收货" name="待收货"></el-tab-pane>
      <el-tab-pane label="已完成" name="已完成"></el-tab-pane>
    </el-tabs>

    <!-- 条件渲染：如果有数据，则显示表格 -->
    <div v-if="tableData && tableData.length">

      <!-- 订单主表格 -->
      <el-table :data="tableData" stripe style="width: 100%">

        <!-- 展开行，用于显示订单内的商品列表 -->
        <el-table-column type="expand">
          <template slot-scope="props">
            <h4>订单商品详情</h4>
            <!-- 这是一个内嵌的表格，显示该订单的所有商品 -->
            <el-table :data="props.row.carts" :show-header="false">
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
              <el-table-column prop="goods.name" label="商品名称" width="200" align="center"></el-table-column>
              <el-table-column prop="goods.realPrice" label="单价" width="150" align="center"></el-table-column>
              <el-table-column prop="count" label="数量" width="180" align="center"></el-table-column>
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

    <!-- 条件渲染：如果没数据，则显示空状态提示 -->
    <div v-else>
      <el-empty description="暂无订单数据"></el-empty>
    </div>

    <!-- 分页组件，放在表格和空状态的外部 -->
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
    <!-- 【正确版本】物流信息对话框 -->
    <el-dialog title="物流详情" :visible.sync="logisticsVisible" width="60%">

      <!-- 如果有物流数据，就显示地图和时间线 -->
      <div v-if="logisticsData && logisticsData.length > 0">

        <!-- 把我们的地图组件放在这里 -->
        <LogisticsMap :tracking-data="logisticsData" />



      </div>

      <!-- 如果没有物流数据，就显示“暂无信息” -->
      <el-empty v-else description="暂无物流信息"></el-empty>

    </el-dialog>

  </div>
</template>

<script>
import API from "@/utils/request";
import LogisticsMap from '@/components/LogisticsMap.vue'; // 【核心】引入我们自己的地图组件

export default {
  name: "Order", // 给组件一个名字，好习惯
  components: {
    LogisticsMap // 【核心】在这里注册地图组件
  },
  data() {
    return {
      user: {},
      pageNum: 1,
      pageSize: 10,
      total: 0,
      activeName: 'all',
      tableData: [],
      // payData: [], // 这个字段似乎没被使用，可以考虑删除
      commentData: [],
      entity: {},
      state: 'all',
      dialogFormVisible: false, // 这是评价弹窗的

      // --- 物流功能相关的data ---
      logisticsVisible: false, // 控制物流弹窗的显示/隐藏
      logisticsData: []      // 存放物流轨迹数据
    }
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    if (!this.user.id) {
      this.$message({
        type: 'warning',
        message: '请登录！'
      })
      // 如果未登录，可以跳转到登录页
      // this.$router.push('/login');
      return;
    }
    this.load();
  },
  methods: {
    // 【核心】查看物流的方法
    async showLogistics(orderId) {
      try {
        const res = await API.get("/api/order/" + orderId + "/tracking");
        this.logisticsData = res; // 直接赋值，因为axios已经帮我们解析了
      } catch (e) {
        console.error("获取物流信息失败:", e);
        this.logisticsData = []; // 清空数据
        this.$message.error('获取物流信息失败，请稍后重试');
      }
      this.logisticsVisible = true; // 打开弹窗
    },

    // --- 下面是你原来就有的方法，保持不变 ---
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
    handleClick(tab) {
      this.state = tab.name;
      this.pageNum = 1; // 切换tab时，重置页码到第一页
      this.load();
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
      // 切换tab时，'all' 应该被转换为空字符串发送给后端
      const queryState = this.state === 'all' ? '' : this.state;

      API.get("/api/order/page/front", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          state: queryState
        }
      }).then(res => {
        this.tableData = res.data.records;
        this.total = res.data.total; // 更新总条数，用于分页

        this.commentData = []; // 每次加载前清空评价数据

        this.tableData.forEach(v => {
          // 这里有一个潜在的bug，如果v.carts是null或空字符串，JSON.parse会报错
          try {
            v.carts = JSON.parse(v.carts);
          } catch(e) {
            v.carts = []; // 解析失败则置为空数组
          }

          v.carts.forEach(item => {
            if (!item.goods.imgs) {
              item.goods.imgs = ['']
            } else {
              try {
                item.goods.imgs = JSON.parse(item.goods.imgs)
              } catch(e) {
                item.goods.imgs = [''];
              }
            }

            // '已完成' 状态的数据才加入待评价列表
            if (v.state === '已完成') {
              item.goods.orderNo = v.orderNo;
              this.commentData.push(item.goods);
            }
          })
        })
      })
    },
    pay(id) {
      API.put("/api/order/pay/" + id).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '付款成功'
          })
          this.load()
        } else {
          this.$message({
            type: 'error',
            message: res.msg
          })
        }
      })
    },
    cancel(id) {
      API.put("/api/order", {id: id, state: '已取消'}).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.load()
        }
      })
    },
    del(id) {
      API.delete("/api/order/" + id).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.load()
        }
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
