<template>
  <div class="merchant-home-container">
    <!-- 欢迎卡片 -->
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-content">
        <img src="https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png" alt="avatar" class="avatar" />
        <div class="welcome-text">
          <h2 class="greeting">你好, 商家 {{ user.username }}!</h2>
          <p class="info-text">欢迎来到您的专属工作台。在这里，您可以轻松管理您的商品和订单。</p>
        </div>
      </div>
    </el-card>

    <!-- 商家数据统计行 -->
    <el-row :gutter="20" class="stats-row">
      <!-- 总销量 -->
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #ffe4b5 0%, #ffc680 100%);">
          <div class="stat-content">
            <i class="el-icon-sold-out stat-icon"></i>
            <div class="stat-text">
              <div class="stat-value">{{ stats.totalSales }}</div>
              <div class="stat-label">总销量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 总销售额 -->
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #ffd59e 0%, #ffb770 100%);">
          <div class="stat-content">
            <i class="el-icon-money stat-icon"></i>
            <div class="stat-text">
              <div class="stat-value">¥ {{ stats.totalRevenue ? stats.totalRevenue.toFixed(2) : '0.00' }}</div>
              <div class="stat-label">总销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类销量图表行 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>分类商品销量统计</span>
          </div>
          <!-- ECharts 饼图容器 -->
          <div v-if="chartData.length" id="category-pie" :style="{ width: '100%', height: '400px' }"></div>
          <!-- 无数据时显示 -->
          <div v-else class="empty-chart">暂无数据</div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import API from '../../utils/request'

export default {
  name: "MerchantHome",
  data() {
    return {
      user: {},
      stats: {
        totalSales: 0,
        totalRevenue: 0
      },
      chartData: []
    };
  },
  mounted() {
    this.user = sessionStorage.getItem("user")
      ? JSON.parse(sessionStorage.getItem("user"))
      : {};
    this.loadStats();
    this.drawPieChart();
  },
  methods: {
    // 绘制分类销量饼图
    drawPieChart() {
      // 商家专用接口
      API.get('/api/merchant/stats/category').then(res => {
        if (res.code === '0' && res.data.length) {
          this.chartData = res.data;
          const option = {
            tooltip: { trigger: 'item', formatter: '{b}<br/>销量: {c} ({d}%)' },
            legend: { top: '5%', left: 'center' },
            color: ['#ff6f00', '#ff8f00', '#ffb300', '#ffd54f', '#fff59d'],
            series: [{
              name: '分类销量',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '20',
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: this.chartData
            }]
          };
          const chart = this.$echarts.init(document.getElementById('category-pie'));
          chart.setOption(option);
          window.addEventListener("resize", () => {
            chart.resize();
          });
        }
      });
    },
    // 加载商家的统计数据（总销量、总销售额）
    loadStats() {
      // 从商家专用接口获取统计数据
      API.get('/api/merchant/stats').then(res => {
        if (res.code === '0') {
          this.stats = res.data;
        }
      })
    },
  },
};
</script>

<style scoped>
/* 定义 CSS 变量以方便主题管理 */
:root {
  --primary-bg: #fff8ec; /* 主背景色 */
  --card-bg: #fffdf9;    /* 卡片背景色 */
  --accent: #ff9b3c;      /* 主题强调色 */
  --accent-dark: #d87d29; /* 深色强调色 */
  --text-primary: #5c3a1d; /* 主要文字颜色 */
  --text-secondary: #8c6e54; /* 次要文字颜色 */
  --border-light: #f2e2c9; /* 边框颜色 */
}
.merchant-home-container {
  padding: 20px;
  background: var(--primary-bg);
}
.welcome-card {
  margin-bottom: 20px;
  background: var(--card-bg);
  border: none;
}
.welcome-content {
  display: flex;
  align-items: center;
}
.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  margin-right: 20px;
}
.greeting {
  font-size: 20px;
  color: var(--accent-dark);
  margin: 0 0 8px;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  border: none;
  color: var(--text-primary);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}
.stat-content {
  display: flex;
  align-items: center;
}
.stat-icon {
  font-size: 48px;
  margin-right: 20px;
  opacity: 0.9;
}
.stat-text {
  text-align: right;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
}
.stat-label {
  font-size: 14px;
}
.chart-card {
  background: var(--card-bg);
  border: none;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
  color: var(--accent-dark);
}
.info-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}
/* 图表为空时的占位符样式 */
.empty-chart {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  color: var(--text-secondary);
  font-size: 16px;
}
</style> 