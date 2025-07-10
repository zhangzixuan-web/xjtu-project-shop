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
          <!-- 【修改 1】: 使用 ref 替代 id -->
          <div v-if="chartData.length" ref="pieChart" :style="{ width: '100%', height: '400px' }"></div>
          <!-- 无数据时显示 -->
          <div v-else class="empty-chart">暂无数据</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import API from '../../utils/request'
// 引入 ECharts
import * as echarts from 'echarts';

export default {
  name: "MerchantHome",
  data() {
    return {
      user: {},
      stats: {
        totalSales: 0,
        totalRevenue: 0
      },
      chartData: [],
      pieChart: null, // 【修改 2】: 用于存储 ECharts 实例
    };
  },
  mounted() {
    this.user = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))
        : {};
    this.loadStats();
    this.loadAndDrawPieChart(); // 修改了方法名以更清晰地反映其作用
  },
  // 【修改 5】: 在组件销毁前，清理图表和事件监听，防止内存泄漏
  beforeDestroy() {
    if (this.pieChart) {
      this.pieChart.dispose(); // 销毁实例
      this.pieChart = null;
    }
    window.removeEventListener("resize", this.handleChartResize);
  },
  methods: {
    // 【修改 3】: 将加载数据和绘制图表合并到一个方法中
    loadAndDrawPieChart() {
      API.get('/api/merchant/stats/category').then(res => {
        if (res.code === '0' && res.data && res.data.length) {
          this.chartData = res.data;

          // 【修改 4】: 使用 this.$nextTick 确保 DOM 更新后再初始化 ECharts
          this.$nextTick(() => {
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
                label: { show: false, position: 'center' },
                emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
                labelLine: { show: false },
                data: this.chartData
              }]
            };

            // 如果已有实例，先销毁，防止重复渲染
            if (this.pieChart) {
              this.pieChart.dispose();
            }

            // 使用 ref 获取 DOM 元素并初始化 ECharts
            this.pieChart = echarts.init(this.$refs.pieChart);
            this.pieChart.setOption(option);

            // 添加窗口大小变化的监听
            window.addEventListener("resize", this.handleChartResize);
          });
        } else {
          // 如果没有数据，确保清空图表和数据
          this.chartData = [];
          if (this.pieChart) {
            this.pieChart.dispose();
            this.pieChart = null;
          }
        }
      });
    },
    // 加载商家的统计数据（总销量、总销售额）
    loadStats() {
      API.get('/api/merchant/stats').then(res => {
        if (res.code === '0') {
          this.stats = res.data;
        }
      })
    },
    // 将 resize 回调独立成一个方法，方便添加和移除
    handleChartResize() {
      if (this.pieChart) {
        this.pieChart.resize();
      }
    }
  },
};
</script>

<style scoped>
/* 样式部分无需修改，保持原样 */
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