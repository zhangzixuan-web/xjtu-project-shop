<template>
  <div class="home-container">
    <!-- 欢迎语 -->
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-content">
        <img src="https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png" alt="avatar" class="avatar">
        <div class="welcome-text">
          <h2 class="greeting">你好, {{ user.username }}! 祝你开始高效的一天！</h2>
          <p class="weather-info">今日晴朗，20°C - 25°C，适合处理工作和享受阳光。</p>
        </div>
      </div>
    </el-card>

    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(to right, #6dd5ed, #2193b0);">
          <div class="stat-content">
            <i class="el-icon-user stat-icon"></i>
            <div class="stat-text">
              <div class="stat-value">1,280</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(to right, #ffafbd, #ffc3a0);">
          <div class="stat-content">
            <i class="el-icon-s-goods stat-icon"></i>
            <div class="stat-text">
              <div class="stat-value">¥ 120,560</div>
              <div class="stat-label">总销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(to right, #a1c4fd, #c2e9fb);">
          <div class="stat-content">
            <i class="el-icon-s-order stat-icon"></i>
            <div class="stat-text">
              <div class="stat-value">3,150</div>
              <div class="stat-label">订单总量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(to right, #d4fc79, #96e6a1);">
          <div class="stat-content">
            <i class="el-icon-chat-line-round stat-icon"></i>
            <div class="stat-text">
              <div class="stat-value">99+</div>
              <div class="stat-label">待处理消息</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主内容区 -->
    <el-row :gutter="20">
      <!-- 左侧图表 -->
      <el-col :span="14">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>分类商品销量统计</span>
            <el-button style="float: right; padding: 3px 0" type="text">查看更多</el-button>
          </div>
          <div id="pie" :style="{width: '100%', height: '400px'}"></div>
        </el-card>
      </el-col>

      <!-- 右侧公告栏 -->
      <el-col :span="10">
        <el-card shadow="never" class="notice-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-message-solid"></i> 系统公告</span>
          </div>
          <el-collapse v-model="active" accordion class="custom-collapse">
            <el-collapse-item v-for="(item, index) in notices" :key="item.id" :name="index">
              <template slot="title">
                <div class="collapse-title">
                  <span class="title-text">{{ item.title }}</span>
                  <span class="title-time">{{ item.time }}</span>
                </div>
              </template>
              <div class="collapse-content">{{ item.content }}</div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import API from '../../utils/request'

export default {
  name: "Home",
  data() {
    return {
      notices: [],
      active: 0,
      user: {},
    };
  },
  mounted() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : { username: 'Admin' };
    this.loadNotices();
    this.drawPieChart();
  },
  methods: {
    loadNotices() {
      // 为了演示，如果没有真实数据，提供一些模拟数据
      API.get("/api/notice/").then((res) => {
        if (res.data && res.data.length > 0) {
          this.notices = res.data;
        } else {
          this.notices = [
            {id: 1, title: '系统V2.0版本上线通知', time: '2023-10-27', content: '新版本已于今日凌晨发布，优化了性能并修复了已知bug，欢迎体验。'},
            {id: 2, title: '国庆节放假安排', time: '2023-09-28', content: '根据国家规定，10月1日至7日放假调休，共7天。祝大家节日快乐！'},
            {id: 3, title: '服务器维护公告', time: '2023-09-15', content: '为提升服务质量，我们将于本周日凌晨2点至4点进行服务器维护，届时系统将暂停服务。'},
          ]
        }
      }).catch(() => {
        // API请求失败时的备用数据
        this.notices = [
          {id: 1, title: '系统V2.0版本上线通知', time: '2023-10-27', content: '新版本已于今日凌晨发布，优化了性能并修复了已知bug，欢迎体验。'},
          {id: 2, title: '国庆节放假安排', time: '2023-09-28', content: '根据国家规定，10月1日至7日放假调休，共7天。祝大家节日快乐！'},
          {id: 3, title: '服务器维护公告', time: '2023-09-15', content: '为提升服务质量，我们将于本周日凌晨2点至4点进行服务器维护，届时系统将暂停服务。'},
        ];
      });
    },
    drawPieChart() {
      API.get("/api/goods").then(res => {
        let totalSales = 0;
        const salesByCategory = new Map();

        // 聚合数据
        if (res.data && res.data.length > 0) {
          res.data.forEach(item => {
            const sales = item.sales || 0;
            totalSales += sales;
            if (salesByCategory.has(item.categoryName)) {
              salesByCategory.set(item.categoryName, salesByCategory.get(item.categoryName) + sales);
            } else {
              salesByCategory.set(item.categoryName, sales);
            }
          });
        }

        const chartData = Array.from(salesByCategory, ([name, value]) => ({ name, value }));

        // 如果没有真实数据，则使用模拟数据
        if(chartData.length === 0) {
          Object.assign(this, {
            chartData: [{name: '电子产品', value: 1590}, {name: '日用百货', value: 1048}, {name: '服装鞋帽', value: 735}, {name: '图书音像', value: 580}],
            totalSales: 3953
          });
        }

        const pieOption = {
          // 美化后的配置
          tooltip: {
            trigger: 'item',
            formatter: '{b} <br/>销量: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'right',
            top: 'center',
            icon: 'circle'
          },
          series: [
            {
              name: '销量',
              type: 'pie',
              radius: ['50%', '70%'],
              center: ['40%', '50%'], // 将饼图向左移动，给图例留出空间
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
                  fontSize: '24',
                  fontWeight: 'bold'
                },
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              labelLine: {
                show: false
              },
              data: chartData
            }
          ],
          // 定义一组柔和的颜色
          color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452'],
          // 在图表中心显示总数
          graphic: {
            type: 'text',
            left: '39%',
            top: '45%',
            style: {
              text: `总销量\n${totalSales.toLocaleString()}`,
              textAlign: 'center',
              fill: '#333',
              fontSize: 18
            }
          }
        };

        let myChart = this.$echarts.init(document.getElementById('pie'));
        myChart.setOption(pieOption);
        window.addEventListener('resize', () => myChart.resize());
      });
    }
  },
};
</script>

<style scoped>
.home-container {
  padding: 20px;
  background-color: #f4f7f6;
}
.welcome-card {
  margin-bottom: 20px;
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
  color: #333;
  margin: 0 0 10px;
}
.weather-info {
  font-size: 14px;
  color: #888;
  margin: 0;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  border: none;
  color: #fff;
}
.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stat-icon {
  font-size: 48px;
  opacity: 0.8;
}
.stat-text {
  text-align: right;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
}
.stat-label {
  font-size: 14px;
}

.chart-card, .notice-card {
  border: none;
  height: 100%;
}

.card-header {
  font-size: 16px;
  font-weight: 500;
}

/* 自定义折叠面板样式 */
.custom-collapse {
  border: none;
}
/* 使用 ::v-deep 或 >>> 穿透scoped样式 */
::v-deep .el-collapse-item {
  margin-bottom: 10px;
}
::v-deep .el-collapse-item__header {
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}
::v-deep .el-collapse-item__header:hover {
  background-color: #f5f7fa;
}
::v-deep .el-collapse-item__wrap {
  border: none;
}
::v-deep .el-collapse-item__content {
  padding: 15px 20px;
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-top: none;
  border-radius: 0 0 4px 4px;
}
.collapse-title {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title-text {
  font-weight: 500;
  color: #333;
}
.title-time {
  font-size: 13px;
  color: #999;
}
.collapse-content {
  color: #666;
  line-height: 1.6;
}
</style>