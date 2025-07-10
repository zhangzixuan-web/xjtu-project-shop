<template>
  <div style="margin-top: 10px">
    <div style="background-color: white; padding: 10px">
      <!-- 收货地址选择区域 -->
      <div style="padding: 10px; margin-bottom: 20px; border-bottom: 1px solid #eee">
        <div style="font-size: 20px; border-bottom: 2px solid dodgerblue; padding-bottom: 10px; margin-bottom: 20px">
          收货地址
        </div>
        <!-- 遍历显示用户的收货地址 -->
        <div style="padding: 10px 0; color: #555" v-for="item in addressData" :key="item.id">
          <!-- 使用 el-radio 实现地址单选 -->
          <el-radio v-model="chooseAddress" :label="item" border>{{ item.linkUser }}</el-radio>
          <span>{{ item.linkUser }}</span>
          <span style="margin-left: 20px">{{ item.linkPhone }}</span>
          <span style="margin-left: 20px">{{ item.linkAddress }}</span>
        </div>
      </div>

      <!-- 商品确认区域 -->
      <el-table :data="carts" stripe style="width: 100%" :show-header="false">
        <!-- 商品图片 -->
        <el-table-column label="商品图片" width="150">
          <template slot-scope="scope">
            <el-image :src="scope.row.goods.imgs[0]" style="width: 100px; height: 100px;" fit="contain"></el-image>
          </template>
        </el-table-column>
        <!-- 商品名称 -->
        <el-table-column prop="goods.name" label="商品名称"></el-table-column>
        <!-- 价格和数量 -->
        <el-table-column label="价格">
          <template slot-scope="scope">
            <span v-html="scope.row.goods.realPrice + ' X ' + scope.row.count "></span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 订单总价及提交按钮 -->
      <div style="margin-top: 10px">
        <div style="background-color: white; padding: 10px">
          <div style="color: red; text-align: right">
            <div>
              <span>总价：</span>
              <span>￥ {{ totalPrice }}</span>
            </div>
            <div style="text-align: right; color: #999; font-size: 12px; padding: 5px 0">
              运费： ￥ 0
            </div>
            <div style="text-align: right; color: #999; font-size: 12px;">
              优惠： - ￥{{ discount }}
            </div>
            <div style="padding: 10px 0">
              <el-button style="background-color: red; color: white; width: 100px" @click="submitOrder">提交订单</el-button>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "preOrder", // 组件名应为 PascalCase
  data() {
    return {
      chooseAddress: null, // 用户选择的收货地址
      user: {},            // 当前登录用户信息
      addressData: [],     // 用户的收货地址列表
      carts: [],           // 待结算的商品列表
      totalPrice: 0,       // 订单总价
      discount: 0          // 优惠金额
    }
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    if (!this.user.id) { // 检查用户ID更可靠
      this.$message.warning('请登录！');
      return;
    }
    this.load();
  },
  methods: {
    // 提交订单
    submitOrder() {
      // 检查是否已选择收货地址
      if (!this.chooseAddress) {
        this.$message.warning('请选择收货地址！');
        return;
      }

      // 调用后端接口创建订单
      API.post("/api/order", {
        totalPrice: this.totalPrice,
        linkUser: this.chooseAddress.linkUser,
        linkPhone: this.chooseAddress.linkPhone,
        linkAddress: this.chooseAddress.linkAddress,
        carts: JSON.stringify(this.carts), // 将商品列表序列化为 JSON 字符串
        type: this.$store.state.type // 携带订单类型（立即购买/购物车结算）
      }).then(res => {
        if (res.code === '0') {
          this.$message.success('提交成功！');
          // 成功后跳转到订单列表页
          this.$router.replace("/front/order");
        } else {
          this.$message.error(res.msg);
        }
      })
    },
    // 加载页面所需数据
    load() {
      // 从 Vuex store 中获取待结算的商品
      this.carts = this.$store.state.carts;

      // 向后端发送预订单请求，获取计算后的总价和优惠
      API.post("/api/order/pre", {carts: JSON.stringify(this.carts)}).then(res => {
        this.carts = res.data.list;
        this.totalPrice = res.data.totalPrice;
        this.discount = res.data.discount;

        // 安全地处理商品图片
        this.carts.forEach(item => {
          if (!item.goods.imgs) {
            item.goods.imgs = [''];
          } else if (typeof item.goods.imgs === 'string') {
            try {
              item.goods.imgs = JSON.parse(item.goods.imgs);
            } catch (e) {
              item.goods.imgs = [''];
            }
          }
        });
      });

      // 获取当前用户的所有收货地址
      API.get("/api/address").then(res => {
        this.addressData = res.data;
      });
    },
  }
}
</script>

<style scoped>

</style>
