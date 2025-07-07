<template>
  <div class="bakery-product-detail">
    <div style="margin-top: 20px">
      <!--      商品-->
      <el-row :gutter="30">
        <!--    图片-->
        <el-col :span="8">
          <div class="product-gallery">
            <div class="main-image">
              <el-image :src="mainImg"></el-image>
            </div>
            <div class="thumbnail-container">
              <div class="thumbnail-item" v-for="(item, index) in goods.imgs" :key="item" @mouseover="show(item)">
                <el-image style="width: 60px; height: 60px;" fit="contain" :src="item"></el-image>
              </div>
            </div>
          </div>
        </el-col>

        <!--    商品细节-->
        <el-col :span="16">
          <div class="product-info">
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
            <h1 class="product-title">{{ goods.name }}</h1>
            <div class="product-description">{{ goods.description }}</div>
            <div class="product-meta">
              <span class="meta-label">上架时间</span>
              <span class="meta-value">{{ goods.createTime }}</span>
            </div>

            <div class="product-price">
              <span class="price-label">抢购价</span>
              <span class="current-price">￥{{ goods.realPrice }}</span>
              <span class="discount-tag" v-if="goods.discount < 1">({{ (goods.discount * 10).toFixed(1) }}折)</span>
            </div>

            <div class="original-price" v-if="goods.discount < 1">
              原价：￥{{ goods.price }}
            </div>
            <div class="product-stock">
              库存：{{ goods.store }}
            </div>

            <div class="product-service">
              <span class="service-label">服务</span>
              <span class="service-value">
                由"SweetDelight"发货，并提供售后服务。 23:55前完成下单，预计明天可送达
              </span>
            </div>

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

      <!--      评论-->
      <div class="product-comments">
        <h2 class="bakery-section-title">商品评论</h2>
        
        <div class="comment-item" v-for="item in messages">
          <div class="comment-avatar">
            <el-image :src="item.avatar" class="avatar-img"></el-image>
          </div>
          <div class="comment-content">
            <div class="comment-username">{{ item.username }}</div>
            <div class="comment-text">
              {{ item.content }}
              <el-button type="text" size="mini" class="delete-btn" @click="del(item.id)" v-if="item.username === user.username">删除</el-button>
            </div>
            <div class="comment-reply" v-if="item.parentMessage">{{ item.username }}：{{ item.parentMessage.content }}</div>
            <div class="comment-meta">
              <span>{{ item.time }}</span>
            </div>
          </div>
        </div>

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

      <!-- Related Products -->
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

const url = "/api/video/"

export default {
  name: "Goods",
  data() {
    return {
      messages: [],
      dialogFormVisible: false,
      entity: {},
      address: '',
      mainImg: '',
      cartIcon: require('../../assets/购物车-23.png'),
      praiseActiveIcon: require("../../assets/点赞-激活.png"),
      collectActiveIcon: require("../../assets/收藏-激活.png"),
      num: 1,
      id: 1,
      user: {},
      goods: {imgs: []},
      praiseFlag: false
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.id = this.$route.query.id
    this.load()
  },
  methods: {
    collect() {
      API.post("/api/collect", {
        goodsName: this.goods.name,
        goodsImg: this.goods.imgs[0],
        goodsId: this.goods.id,
        userId: this.user.id
      }).then(res => {
        if (res.code === '0') {
          this.$message({
            message: "收藏成功",
            type: "success"
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
        this.load();
      })
    },
    praise() {
      if (this.praiseFlag) {
        this.$message({
          message: "您已点过赞",
          type: "warning"
        });
        return
      }
      this.praiseFlag = true
      this.entity = JSON.parse(JSON.stringify(this.goods))
      this.entity.praise += 1
      this.entity.imgs = null
      API.put("/api/goods", this.entity).then(res => {
        if (res.code === '0') {
          this.$message({
            message: "点赞成功",
            type: "success"
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
        this.load();
      })
    },
    loadMessage() {
      API.get("/api/message/foreign/" + this.goods.id).then(res => {
        this.messages = res.data;
      })
    },
    buyNow() {
      if ((this.goods.store - this.num) < 0) {
        this.$message({
          type: 'warning',
          message: '商品库存不足！'
        })
        return
      }
      let cart = []
      cart.push({count: this.num, goods: this.goods, goodsId: this.goods.id})
      this.$store.commit("setCarts", cart)
      this.$router.replace("/front/preOrder")
    },
    // ... existing methods
    show(item) {
      this.mainImg = item
    },
    addCart() {
      if (!this.user.id) {
        this.$message({
          type: 'warning',
          message: '请先登录！'
        })
        return
      }
      if ((this.goods.store - this.num) < 0) {
        this.$message({
          type: 'warning',
          message: '商品库存不足！'
        })
        return
      }

      API.post("/api/cart", {
        userId: this.user.id,
        goodsId: this.goods.id,
        count: this.num
      }).then(res => {
        if (res.code === '0') {
          this.$message({
            message: "添加成功",
            type: "success"
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
      })
    },
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
    del(id) {
      API.delete("/api/message/" + id).then(res => {
        if (res.code === '0') {
          this.$message({
            message: "删除成功",
            type: "success"
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
        this.loadMessage()
      })
    },
    reReply(id) {
      this.dialogFormVisible = true
      this.entity = {parentId: id, foreignId: this.goods.id, username: this.user.username, avatar: this.user.avatar}
    },
    reply() {
      if (!this.entity.reply) {
        this.$message({
          message: "请填写内容",
          type: "warning"
        });
        return;
      }
      this.entity.content = this.entity.reply
      API.post("/api/message", this.entity).then(res => {
        if (res.code === '0') {
          this.$message({
            message: "回复成功",
            type: "success"
          });
          this.dialogFormVisible = false
          this.entity = {}
          this.loadMessage()
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
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
