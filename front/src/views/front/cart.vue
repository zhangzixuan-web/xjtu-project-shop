<template>
  <div style="margin-top: 10px">
    <!-- 购物车商品列表 -->
    <el-table :data="tableData" stripe style="width: 100%">
      <!-- 商品图片 -->
      <el-table-column label="商品图片" width="150" align="center">
        <template slot-scope="scope">
          <!-- 点击图片可跳转到商品详情页 -->
          <a :href="['/front/goods?id=' + scope.row.goods.id]">
            <el-image :src="scope.row.goods.imgs[0]" style="width: 100px; height: 100px;" fit="contain"></el-image>
          </a>
        </template>
      </el-table-column>

      <!-- 商品名称 -->
      <el-table-column prop="goods.name" label="商品名称" width="200" align="center"></el-table-column>

      <!-- 商品单价 -->
      <el-table-column prop="goods.realPrice" label="商品单价" width="150" align="center"></el-table-column>

      <!-- 购买数量 -->
      <el-table-column prop="count" label="购买数量" width="180" align="center">
        <template slot-scope="scope">
          <!-- 数量增减器，改变时会触发 changeCount 方法 -->
          <el-input-number v-model="scope.row.count" :min="1" :max="10" label="购买数量" style="width: 100px" @change="changeCount(scope.row)"></el-input-number>
        </template>
      </el-table-column>

      <!-- 加入时间 -->
      <el-table-column prop="createTime" label="加入时间" width="200" align="center"></el-table-column>

      <!-- 操作 -->
      <el-table-column fixed="right" label="操作" width="200" align="center">
        <template slot-scope="scope">
          <!-- 删除按钮 -->
          <el-button type="danger" icon="el-icon-delete" circle @click="del(scope.row.id)"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 底部结算栏 -->
    <div style="margin-top: 10px">
      <div style="display: flex; background-color: white; padding: 10px">
        <div style="flex: 1; padding-left: 20px; color: #999; font-size: 12px">
          <div>共 <span style="color: #ffa500; font-size: 20px">{{ count }}</span> 件商品</div>
          <div style="color: orange; padding-top: 5px">立即下单，享超值优惠！</div>
        </div>
        <div style="flex: 1; color: #ffa500; text-align: right">
          <div>
            <span>总价：</span>
            <span>￥ {{ totalPrice }}</span>
            <el-button style="background-color: #ffa500; color: white; margin-left: 20px" @click="submitOrder">去结算</el-button>
          </div>
          <div style="text-align: right; color: #999; font-size: 12px; margin-right: 85px">
            优惠： - ￥{{ discount }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "FrontCart",
  data() {
    return {
      user: {},         // 当前登录用户信息
      count: 0,         // 购物车商品总数
      tableData: [],    // 购物车商品列表数据
      totalPrice: 0,    // 总价
      discount: 0       // 优惠金额
    }
  },
  created() {
    // 获取用户信息，未登录则提示
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    if (!this.user.id) { // 注意：这里判断 this.user.id 更严谨
      this.$message.warning('请登录！');
      return;
    }
    // 加载购物车数据
    this.load()
  },
  methods: {
    // 提交订单
    submitOrder() {
      // 检查购物车是否为空
      if (!this.tableData.length) {
        this.$message.warning('您还未选择商品');
        return
      }
      // 将当前购物车数据存入 Vuex store，用于在预订单页面展示
      this.$store.commit('setCarts', this.tableData)
      // 设置一个类型标识，可能用于区分是“立即购买”还是“购物车结算”
      this.$store.commit('setType', 1)
      // 跳转到预订单页面
      this.$router.replace("/front/preOrder")
    },
    // 加载购物车数据
    load() {
      API.get("/api/cart").then(res => {
        this.tableData = res.data.list
        if (this.tableData) {
          // 计算商品总数
          this.count = this.tableData.length > 0 ? this.tableData.map(v => v.count).reduce((a, b) => a + b) : 0;
          // 处理商品图片，将其从 JSON 字符串解析为数组
          this.tableData.forEach(item => {
            if (!item.goods.imgs) {
              item.goods.imgs = ['']
            } else {
              item.goods.imgs = JSON.parse(item.goods.imgs)
            }
          })
          // 更新总价和优惠金额
          this.totalPrice = res.data.totalPrice
          this.discount = res.data.discount
        }
      })
    },
    // 修改商品数量
    changeCount(obj) {
      let param = JSON.parse(JSON.stringify(obj))
      delete param.goods  // 移除不需要传给后端的 goods 对象
      API.put("/api/cart/", param).then(res => {
        if (res.code === '0') {
          // 成功后重新加载购物车数据以更新总价等信息
          this.load()
        }
      })
    },
    // 从购物车删除商品
    del(id) {
      API.delete("/api/cart/" + id).then(res => {
        if (res.code === '0') {
          this.$message.success('操作成功');
          // 成功后重新加载购物车数据
          this.load()
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
