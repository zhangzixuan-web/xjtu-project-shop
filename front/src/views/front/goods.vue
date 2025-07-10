<template>
  <div class="bakery-product-detail">
    <div style="margin-top: 20px">
      <!-- 商品主要信息区域 -->
      <el-row :gutter="30">
        <!-- 左侧：商品图片展示 -->
        <el-col :span="8">
          <div class="product-gallery">
            <!-- 主图 -->
            <div class="main-image">
              <el-image :src="mainImg"></el-image>
            </div>
            <!-- 缩略图列表 -->
            <div class="thumbnail-container">
              <div class="thumbnail-item" v-for="item in goods.imgs" :key="item" @mouseover="show(item)">
                <el-image style="width: 60px; height: 60px;" fit="contain" :src="item"></el-image>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧：商品详细信息和操作 -->
        <el-col :span="16">
          <div class="product-info">
            <!-- 收藏和点赞按钮 -->
            <div class="product-actions">
              <div class="action-button" @click="collect">
                <img :src="collectActiveIcon" alt="收藏" class="action-icon">
                <span class="action-text">收藏</span>
              </div>
              <div class="action-button" @click="praise">
                <img :src="praiseActiveIcon" alt="点赞" class="action-icon">
                <span class="action-text">{{ goods.praise }}</span>
              </div>
            </div>
            <!-- 商品标题和描述 -->
            <h1 class="product-title">{{ goods.name }}</h1>
            <div class="product-description">{{ goods.description }}</div>
            <div class="product-meta">
              <span class="meta-label">上架时间</span>
              <span class="meta-value">{{ goods.createTime }}</span>
            </div>

            <!-- 价格和折扣信息 -->
            <div class="product-price">
              <span class="price-label">抢购价</span>
              <span class="current-price">￥{{ goods.realPrice }}</span>
              <span class="discount-tag" v-if="goods.discount < 1">({{ (goods.discount * 10).toFixed(1) }}折)</span>
            </div>
            <div class="original-price" v-if="goods.discount < 1">
              原价：￥{{ goods.price }}
            </div>
            <!-- 库存 -->
            <div class="product-stock">
              库存：{{ goods.store }}
            </div>
            <!-- 服务承诺 -->
            <div class="product-service">
              <span class="service-label">服务</span>
              <span class="service-value">
                由"SweetDelight"发货，并提供售后服务。 23:55前完成下单，预计明天可送达
              </span>
            </div>

            <!-- 购买操作：数量选择、加入购物车、立即购买 -->
            <div class="product-actions-buy">
              <el-input-number v-model="num" :min="1" :max="10" label="购买数量" class="quantity-input"></el-input-number>
              <el-button class="add-to-cart-btn" @click="addCart">
                <img :src="cartIcon" class="cart-icon">
                <span>加入购物车</span>
              </el-button>
              <el-button class="buy-now-btn" @click="buyNow">立即购买</el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 商品评论区 -->
      <div class="product-comments">
        <h2 class="bakery-section-title">商品评论</h2>
        
        <!-- 遍历显示评论列表 -->
        <div class="comment-item" v-for="item in messages">
          <div class="comment-avatar">
            <el-image :src="item.avatar" class="avatar-img"></el-image>
          </div>
          <div class="comment-content">
            <div class="comment-username">{{ item.username }}</div>
            <div class="comment-text">
              {{ item.content }}
              <!-- 如果评论是当前用户发表的，显示删除按钮 -->
              <el-button type="text" size="mini" class="delete-btn" @click="del(item.id)" v-if="item.username === user.username">删除</el-button>
            </div>
            <!-- 显示父级评论（回复） -->
            <div class="comment-reply" v-if="item.parentMessage">{{ item.username }}：{{ item.parentMessage.content }}</div>
            <div class="comment-meta">
              <span>{{ item.time }}</span>
            </div>
          </div>
        </div>

        <!-- 回复弹窗 (功能未完全展示) -->
        <el-dialog title="回复信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
          <el-form :model="entity">
            <el-form-item label="内容" label-width="100px">
              <el-input v-model="entity.reply" autocomplete="off" type="textarea" :rows="3"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="reply">确 定</el-button>
          </div>
        </el-dialog>
      </div>

      <!-- 相关商品推荐区域 (静态展示) -->
      <div class="related-products">
        <h2 class="bakery-section-title">相关商品</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="(item, index) in 4" :key="index">
            <div class="bakery-card">
              <div class="bakery-card-img-container">
                <el-image :src="goods.imgs[0]" class="bakery-card-img" fit="contain"></el-image>
              </div>
              <div class="bakery-card-title">相关商品 {{ index + 1 }}</div>
              <div class="bakery-card-price">
                <span class="current-price">￥{{ (goods.price * 0.9).toFixed(2) }}</span>
                <span class="original-price">￥{{ goods.price }}</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "Goods",
  data() {
    return {
      messages: [],                      // 商品评论列表
      dialogFormVisible: false,          // 控制回复弹窗的显示
      entity: {},                        // 存储当前操作的实体（如回复）
      mainImg: '',                       // 当前显示的主图 URL
      cartIcon: require('../../assets/购物车-23.png'), // 购物车图标
      praiseActiveIcon: require("../../assets/点赞-激活.png"), // 点赞激活状态图标
      collectActiveIcon: require("../../assets/收藏-激活.png"), // 收藏激活状态图标
      num: 1,                            // 购买数量
      id: 1,                             // 当前商品 ID
      user: {},                          // 当前登录的用户信息
      goods: {imgs: []},                 // 商品详情数据
      praiseFlag: false                  // 防止重复点赞的标志
    };
  },
  created() {
    // 从 sessionStorage 获取用户信息
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    // 从路由查询参数中获取商品 ID
    this.id = this.$route.query.id
    // 加载商品数据
    this.load()
  },
  methods: {
    // 收藏商品
    collect() {
      API.post("/api/collect", {
        goodsName: this.goods.name,
        goodsImg: this.goods.imgs[0],
        goodsId: this.goods.id,
        userId: this.user.id
      }).then(res => {
        if (res.code === '0') {
          this.$message.success("收藏成功");
        } else {
          this.$message.error(res.msg);
        }
        this.load(); // 重新加载数据以更新状态
      })
    },
    // 点赞商品
    praise() {
      // 如果已经点过赞，则提示并返回
      if (this.praiseFlag) {
        this.$message.warning("您已点过赞");
        return
      }
      this.praiseFlag = true; // 设置点赞标志，防止重复点击
      this.entity = JSON.parse(JSON.stringify(this.goods))
      this.entity.praise += 1;
      this.entity.imgs = null; // 更新时不需要传递图片信息
      API.put("/api/goods", this.entity).then(res => {
        if (res.code === '0') {
          this.$message.success("点赞成功");
        } else {
          this.$message.error(res.msg);
        }
        this.load(); // 重新加载数据以更新点赞数
      })
    },
    // 加载商品评论
    loadMessage() {
      API.get("/api/message/foreign/" + this.goods.id).then(res => {
        this.messages = res.data;
      })
    },
    // 立即购买
    buyNow() {
      // 检查库存
      if ((this.goods.store - this.num) < 0) {
        this.$message.warning('商品库存不足！');
        return
      }
      let cart = []
      cart.push({count: this.num, goods: this.goods, goodsId: this.goods.id})
      // 将要购买的商品存入 Vuex store，然后跳转到预订单页面
      this.$store.commit("setCarts", cart)
      this.$router.replace("/front/preOrder")
    },
    // 鼠标悬停时切换主图
    show(item) {
      this.mainImg = item
    },
    // 加入购物车
    addCart() {
      // 检查是否登录
      if (!this.user.id) {
        this.$message.warning('请先登录！');
        return
      }
      // 检查库存
      if ((this.goods.store - this.num) < 0) {
        this.$message.warning('商品库存不足！');
        return
      }

      API.post("/api/cart", {
        userId: this.user.id,
        goodsId: this.goods.id,
        count: this.num
      }).then(res => {
        if (res.code === '0') {
          this.$message.success("加入购物车成功");
        } else {
          this.$message.error(res.msg);
        }
      })
    },
    // 加载商品详情
    load() {
      API.get("/api/goods/" + this.id).then(res => {
        this.goods = res.data
        if (this.goods.imgs) {
          this.goods.imgs = JSON.parse(this.goods.imgs)
          this.mainImg = this.goods.imgs[0]
        }
        this.loadMessage()
      })
    },
    // 删除评论
    del(id) {
      API.delete("/api/message/" + id).then(res => {
        if (res.code === '0') {
          this.$message.success("删除成功");
        } else {
          this.$message.error(res.msg);
        }
        this.loadMessage()
      })
    },
    // 回复评论
    reReply(id) {
      this.dialogFormVisible = true
      this.entity = {parentId: id, foreignId: this.goods.id, username: this.user.username, avatar: this.user.avatar}
    },
    reply() {
      if (!this.entity.reply) {
        this.$message.warning("请填写内容");
        return;
      }
      this.entity.content = this.entity.reply
      API.post("/api/message", this.entity).then(res => {
        if (res.code === '0') {
          this.$message.success("回复成功");
          this.dialogFormVisible = false
          this.entity = {}
          this.loadMessage()
        } else {
          this.$message.error(res.msg);
        }
      })
    },
    cancel() {
      this.dialogFormVisible = false
      this.entity = {}
    }
  },
};
</script>

<style scoped>
.bakery-product-detail {
  margin-bottom: 60px;
}

.product-gallery {
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  padding: 20px;
}

.main-image {
  margin-bottom: 20px;
}

.thumbnail-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.thumbnail-item {
  cursor: pointer;
  border: 1px solid #eee;
  border-radius: var(--border-radius);
  overflow: hidden;
  transition: transform 0.3s;
}

.thumbnail-item:hover {
  transform: scale(1.05);
  border-color: var(--primary-color);
}

.product-info {
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  padding: 30px;
}

.product-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.action-button {
  display: flex;
  align-items: center;
  margin-left: 20px;
  cursor: pointer;
}

.action-icon {
  width: 18px;
  height: 18px;
}

.action-text {
  margin-left: 5px;
  color: var(--secondary-color);
  font-size: 14px;
}

.product-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--secondary-color);
  margin-bottom: 15px;
}

.product-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.product-meta {
  margin-bottom: 15px;
  color: #999;
}

.meta-label {
  margin-right: 10px;
}

.meta-value {
  color: #666;
}

.product-price {
  margin: 25px 0 10px;
  display: flex;
  align-items: center;
}

.price-label {
  color: #999;
  margin-right: 10px;
}

.current-price {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color);
  margin-right: 10px;
}

.discount-tag {
  background-color: var(--primary-color);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  margin-bottom: 10px;
}

.product-stock {
  color: #666;
  margin-bottom: 15px;
}

.product-service {
  background-color: rgba(240, 154, 89, 0.1);
  padding: 15px;
  border-radius: var(--border-radius);
  margin: 20px 0;
}

.service-label {
  color: #999;
  margin-right: 10px;
}

.service-value {
  color: #666;
}

.product-actions-buy {
  margin-top: 30px;
  display: flex;
  align-items: center;
}

.quantity-input {
  margin-right: 15px;
}

.add-to-cart-btn {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 12px 20px;
  position: relative;
  margin-right: 15px;
}

.cart-icon {
  width: 20px;
  margin-right: 5px;
  vertical-align: middle;
}

.buy-now-btn {
  background-color: var(--secondary-color);
  color: white;
  border: none;
  padding: 12px 25px;
}

.product-comments {
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  padding: 30px;
  margin-top: 30px;
}

.comment-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.comment-avatar {
  flex: 0 0 60px;
  margin-right: 20px;
}

.avatar-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
}

.comment-content {
  flex: 1;
}

.comment-username {
  font-weight: 600;
  color: var(--secondary-color);
  margin-bottom: 10px;
}

.comment-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.delete-btn {
  color: var(--primary-color);
}

.comment-reply {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: var(--border-radius);
  margin-bottom: 10px;
  color: #666;
}

.comment-meta {
  color: #999;
  font-size: 12px;
}

.related-products {
  margin-top: 40px;
}
</style>
