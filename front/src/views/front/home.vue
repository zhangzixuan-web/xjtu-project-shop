<template>
  <div>
    <!-- Hero Banner with Video -->
    <div class="bakery-hero" style="margin-top: 20px">
      <el-row type="flex" align="middle" style="height: 100%;">
        <el-col :span="10" class="hero-text-content">
          <div style="font-size: 20px; color: var(--primary-color); margin-bottom: 15px">Watch Our Story</div>
          <h1 class="bakery-hero-title">The Art of<br>Baking.</h1>
          <p class="hero-subtitle">
            Discover the passion and dedication that goes into every one of our creations.
          </p>
          <el-button class="bakery-btn shop-now-btn">Explore Products <i class="el-icon-arrow-right"></i></el-button>
        </el-col>
        <el-col :span="14" class="hero-video-container">
          <video class="hero-video" autoplay loop muted playsinline>
            <source src="@/assets/video.mp4" type="video/mp4">
            Your browser does not support the video tag.
          </video>
        </el-col>
      </el-row>
    </div>

    <!-- Category Icons -->
    <div style="margin: 40px 0">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(category, index) in categoryIcons" :key="index">
          <div class="category-icon-box" @click="getGoodsByCategory(categories[index] ? categories[index].id : 0)">
            <div class="category-icon">
              <img :src="category.icon" alt="Category Icon" class="category-icon-img">
            </div>
            <div class="category-name">{{ category.name }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Special Bakery Products -->
    <div style="margin: 60px 0">
      <div class="section-label">LATEST PRODUCTS</div>
      <h2 class="bakery-section-title">Special Bakery Products</h2>
      
      <el-row :gutter="20">
        <el-col :span="6" v-for="item in tableData" :key="item.id">
          <div class="bakery-card" @click="goodsDetail(item.id)">
            <div class="bakery-card-img-container">
              <el-image :src="item.imgs[0]" class="bakery-card-img" fit="contain"></el-image>
            </div>
            <div class="bakery-card-title">
              <el-tooltip :content="item.name" placement="bottom" effect="light">
                <span>{{ item.name }}</span>
              </el-tooltip>
            </div>
            <div class="bakery-card-price">
              <span class="current-price">￥{{ item.price }}</span>
              <span class="original-price">￥{{ (item.price * 1.25).toFixed(2) }}</span>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Pagination -->
      <div style="text-align: center; margin-top: 20px">
        <el-pagination
            small
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-size="pageSize"
            :page-sizes="[4, 8, 12]"
            layout="prev, pager, next"
            :total="total"
        >
        </el-pagination>
      </div>
    </div>

    <!-- Discount Carousel Section -->
    <div class="discount-carousel-section">
        <el-carousel height="400px" indicator-position="outside" arrow="always">
            <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
              <img :src="item.src" alt="Promotional Image" class="discount-carousel-image">
            </el-carousel-item>
        </el-carousel>
    </div>
    
    <!-- Discount Banner -->
    <div class="discount-banner">
      <el-row type="flex" align="middle">
        <el-col :span="8">
          <div class="discount-image">
            <div class="discount-badge"> 80% Off</div>
            <img src="@/assets/discount1.jpg" alt="Discount" style="width: 100%; border-radius: var(--border-radius)">
          </div>
        </el-col>
        <el-col :span="16">
          <div style="padding-left: 40px">
            <div class="offer-text">Offer For Customer.</div>
            <h2 class="discount-title">All Products <span>80%</span> Discount.</h2>
            <el-button class="bakery-btn shop-now-btn">Shop Now <i class="el-icon-arrow-right"></i></el-button>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Featured Products -->
    <div style="margin: 60px 0">
      <div class="section-label">FEATURED ITEMS</div>
      <h2 class="bakery-section-title">Featured Products</h2>
      
      <div class="featured-products-slider">
        <el-row :gutter="20">
          <el-col :span="4" v-for="item in recommend.slice(0, 6)" :key="item.id">
            <div class="bakery-card" @click="goodsDetail(item.id)">
              <div class="bakery-card-img-container">
                <el-image :src="item.imgs[0]" class="bakery-card-img" fit="contain"></el-image>
              </div>
              <div class="bakery-card-title">{{ item.name }}</div>
              <div class="bakery-card-price">
                <span class="current-price">￥{{ item.price }}</span>
                <span class="original-price">￥{{ (item.price * 1.25).toFixed(2) }}</span>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="slider-controls">
          <i class="el-icon-arrow-left slider-arrow"></i>
          <i class="el-icon-arrow-right slider-arrow"></i>
        </div>
      </div>
    </div>

    <!-- Blog Posts -->
    <div style="margin: 60px 0">
      <div class="section-label">LATEST BLOG POST</div>
      <h2 class="bakery-section-title">Latest News & Article</h2>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="blog-card">
            <div class="blog-image">
              <img src="@/assets/bg1.jpg" alt="Blog Post">
            </div>
            <div class="blog-date">FEBRUARY 28, 2023</div>
            <h3 class="blog-title">Exploring the World of Bakery Items</h3>
            <el-button class="blog-read-more">READ MORE</el-button>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="blog-card">
            <div class="blog-image">
              <img src="@/assets/bg2.jpg" alt="Blog Post">
            </div>
            <div class="blog-date">FEBRUARY 6, 2023</div>
            <h3 class="blog-title">The Rise of Artisanal Bread A Trend Worth Trying</h3>
            <el-button class="blog-read-more">READ MORE</el-button>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="blog-card">
            <div class="blog-image">
              <img src="@/assets/bg3.jpg" alt="Blog Post">
            </div>
            <div class="blog-date">FEBRUARY 6, 2023</div>
            <h3 class="blog-title">A Behind the Scenes Look at the Kitchen</h3>
            <el-button class="blog-read-more">READ MORE</el-button>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Reservation Form -->
    <div style="margin: 60px 0">
      <div class="section-label">BOOK RESERVATION</div>
      <h2 class="bakery-section-title">Book Our Reservation</h2>
      
      <div class="reservation-form">
        <el-form>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item>
                <el-input placeholder="Full Name"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <el-input placeholder="Phone Number"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <el-input placeholder="Email Address"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item>
                <el-input type="textarea" rows="4" placeholder="Additional Message"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" style="text-align: center">
              <el-button class="bakery-btn reservation-btn">Book Reservation</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import API from "@/utils/request";

const url = "/api/video/"

export default {
  name: "Home",
  data() {
    return {
      sales: [],
      recommend: [],
      tableData: [],
      activeIndex: 0,
      imgList: [],
      user: {},
      categories: [],
      pageNum: 1,
      pageSize: 8,
      total: 0,
      // 轮播图数据
      carouselImages: [
        { src: require('@/assets/轮播-1.jpg') },
        { src: require('@/assets/轮播-2.jpg') },
        { src: require('@/assets/轮播-3.jpg') }
      ],
      // 分类图标数据
      categoryIcons: [
        { name: '面包', icon: require('@/assets/icon1.png') },
        { name: '蛋糕', icon: require('@/assets/icon2.jpg') },
        { name: '甜点', icon: require('@/assets/icon3.png') },
        { name: '饼干', icon: require('@/assets/icon4.png') }
      ]
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
    getCategoryIcon(index) {
      const icons = ['el-icon-pie-chart', 'el-icon-ice-cream', 'el-icon-dessert', 'el-icon-food'];
      return icons[index % icons.length];
    },
    goodsDetail(id) {
      this.$router.replace({path: '/front/goods', query: {id: id}})
    },
    getGoodsByCategory(id) {
      if (!id && this.categories.length > 0) {
        id = this.categories[0].id;
      }
      this.activeIndex = id
      this.loadTable(id)
    },
    load() {
      API.get("/api/banner").then(res => {
        this.imgList = res.data
      })

      API.get("/api/goods/recommend").then(res => {
        this.recommend = res.data

        this.recommend.forEach(item => {
          // 处理下表格的图片显示
          if (!item.imgs) {
            item.imgs = ['']
          } else {
            item.imgs = JSON.parse(item.imgs)
          }
        })
      })

      API.get("/api/goods/sales").then(res => {
        this.sales = res.data

        this.sales.forEach(item => {
          // 处理下表格的图片显示
          if (!item.imgs) {
            item.imgs = ['']
          } else {
            item.imgs = JSON.parse(item.imgs)
          }
        })
      })

      API.get("/api/category").then(res => {
        this.categories = res.data
        if (this.categories && this.categories.length > 0) {
          this.activeIndex = this.categories[0].id
          this.loadTable(this.categories[0].id)
        }
      })
    },
    loadTable(categoryId) {
      API.get("/api/goods/byCategory/" + categoryId, {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total

        this.tableData.forEach(item => {
          // 处理下表格的图片显示
          if (!item.imgs) {
            item.imgs = ['']
          } else {
            item.imgs = JSON.parse(item.imgs)
          }
        })
      })
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.loadTable(this.categories[0].id)
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.loadTable(this.categories[0].id)
    },
  },
};
</script>

<style scoped>
.active {
  color: var(--primary-color) !important;
}

.category-item {
  padding: 5px 0;
  border-bottom: 1px dashed #ccc;
  color: #666;
  cursor: pointer;
}

.line1 {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-title {
  cursor: pointer;
}
.item-title:hover {
  color: var(--primary-color);
}

/* Hero Section with Video */
.bakery-hero {
  background-color: var(--accent-color);
  border-radius: var(--border-radius);
  overflow: hidden;
  position: relative;
  margin-bottom: 20px;
  height: 420px;
  padding: 20px;
}

.hero-text-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 40px;
  height: 100%;
}

.bakery-hero-title {
  font-size: 56px;
  font-weight: 800;
  color: var(--secondary-color);
  margin: 0 0 20px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 16px;
  color: var(--text-color);
  margin-bottom: 30px;
  max-width: 80%;
}

.shop-now-btn {
  padding: 12px 24px;
  font-size: 16px;
  align-self: flex-start;
}

.hero-video-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-video {
  width: 100%;
  height: 380px;
  border-radius: var(--border-radius);
  object-fit: cover; /* 确保视频填满容器 */
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

/* Discount Carousel Section */
.discount-carousel-section {
    margin: 60px 0;
    border-radius: var(--border-radius);
    overflow: hidden;
    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

.discount-carousel-image {
    width: 100%;
    height: 400px;
    object-fit: cover;
}

/* 分类图标样式 (简化为单层圆形) */
.category-icon-box {
  background-color: white;
  border-radius: 50%;
  box-shadow: var(--box-shadow);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  width: 180px;
  height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.category-icon-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.category-icon {
  /* This element is now just a container, no background or specific size needed */
  margin-bottom: 15px; /* Adjust spacing between icon and text */
}

.category-icon-img {
  width: 80px;  /* Increased icon size */
  height: 80px; /* Increased icon size */
  object-fit: contain;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--secondary-color);
}

.section-label {
  display: inline-block;
  background-color: rgba(240, 154, 89, 0.1);
  color: var(--primary-color);
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 10px;
}

.bakery-section-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--secondary-color);
  margin: 0 0 30px;
}

.bakery-card {
  background-color: white;
  border-radius: var(--border-radius);
  overflow: hidden;
  box-shadow: var(--box-shadow);
  margin-bottom: 30px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.bakery-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.bakery-card-img-container {
  padding: 20px;
  background-color: #fff;
  text-align: center;
}

.bakery-card-img {
  height: 150px;
  object-fit: contain;
}

.bakery-card-title {
  padding: 0 15px;
  font-size: 18px;
  font-weight: 600;
  color: var(--secondary-color);
  text-align: center;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bakery-card-price {
  padding: 0 15px 15px;
  text-align: center;
}

.current-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color);
  margin-right: 10px;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.discount-banner {
  background-color: var(--accent-color);
  border-radius: var(--border-radius);
  padding: 30px;
  margin: 60px 0;
}

.discount-image {
  position: relative;
}

.discount-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: var(--primary-color);
  color: white;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  z-index: 1;
}

.offer-text {
  font-size: 24px;
  color: var(--secondary-color);
  margin-bottom: 10px;
}

.discount-title {
  font-size: 48px;
  font-weight: 800;
  color: var(--secondary-color);
  margin-bottom: 30px;
}

.discount-title span {
  color: var(--primary-color);
}

.featured-products-slider {
  position: relative;
  padding: 0 30px;
}

.slider-controls {
  position: absolute;
  top: -70px;
  right: 0;
}

.slider-arrow {
  font-size: 24px;
  color: var(--secondary-color);
  cursor: pointer;
  margin-left: 15px;
  background-color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--box-shadow);
  transition: all 0.3s;
}

.slider-arrow:hover {
  background-color: var(--primary-color);
  color: white;
}

.blog-card {
  background-color: var(--background-color);
  border-radius: var(--border-radius);
  overflow: hidden;
  padding: 15px;
  margin-bottom: 30px;
}

.blog-image {
  border-radius: var(--border-radius);
  overflow: hidden;
  margin-bottom: 15px;
}

.blog-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s;
}

.blog-card:hover .blog-image img {
  transform: scale(1.05);
}

.blog-date {
  font-size: 14px;
  color: var(--primary-color);
  margin-bottom: 10px;
}

.blog-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--secondary-color);
  margin-bottom: 15px;
  line-height: 1.4;
}

.blog-read-more {
  background-color: transparent;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 600;
}

.blog-read-more:hover {
  background-color: var(--primary-color);
  color: white;
}

.reservation-form {
  background-color: white;
  border-radius: var(--border-radius);
  padding: 30px;
  box-shadow: var(--box-shadow);
  margin-top: 30px;
}

.reservation-btn {
  padding: 12px 30px;
  font-size: 16px;
  margin-top: 15px;
}

/* 修复hero图片位置 */
.hero-image-container {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.hero-image {
  max-width: 100%;
  height: auto;
  border-radius: var(--border-radius);
  object-fit: cover;
  object-position: center;
  box-shadow: var(--box-shadow);
}
</style>
