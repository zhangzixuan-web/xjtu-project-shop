<template>
  <div class="home-container">
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-content">
        <img src="https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png" alt="avatar" class="avatar" />
        <div class="welcome-text">
          <h2 class="greeting">你好, {{ user.username }}! 祝你开始高效的一天！</h2>
          <p class="weather-info">今日晴朗，20°C - 25°C，适合处理工作和享受阳光。</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #ffe4b5 0%, #ffc680 100%);">
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
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #ffd59e 0%, #ffb770 100%);">
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
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #ffcc9a 0%, #ffad66 100%);">
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
        <el-card shadow="hover" class="stat-card" style="background: linear-gradient(135deg, #ffcb8e 0%, #ffa64d 100%);">
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

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>分类商品销量统计</span>
            <el-button style="float: right; padding: 3px 0" type="text">查看更多</el-button>
          </div>
          <div id="pie" :style="{ width: '100%', height: '400px' }"></div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="never" class="notice-card">
          <template #header>
            <span><i class="el-icon-message-solid"></i> 系统公告</span>
          </template>
          <el-collapse v-model="active" accordion class="custom-collapse">
            <el-collapse-item v-for="(item, index) in notices" :key="item.id" :name="index">
              <template #title>
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
  name: 'Home',
  data() {
    return {
      notices: [],
      active: 0,
      user: {},
    }
  },
  mounted() {
    this.user = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : { username: 'Admin' }
    this.loadNotices()
    this.drawPieChart()
  },
  methods: {
    loadNotices() {
      API.get('/api/notice/').then(res => {
        this.notices = res.data && res.data.length ? res.data : [
          { id: 1, title: '系统V2.0版本上线通知', time: '2023-10-27', content: '新版本已于今日凌晨发布，优化了性能并修复了已知bug，欢迎体验。' },
          { id: 2, title: '国庆节放假安排', time: '2023-09-28', content: '根据国家规定，10月1日至7日放假调休，共7天。祝大家节日快乐！' },
          { id: 3, title: '服务器维护公告', time: '2023-09-15', content: '为提升服务质量，我们将于本周日凌晨2点至4点进行服务器维护，届时系统将暂停服务。' },
        ]
      }).catch(() => {
        this.notices = [
          { id: 1, title: '系统V2.0版本上线通知', time: '2023-10-27', content: '新版本已于今日凌晨发布，优化了性能并修复了已知bug，欢迎体验。' },
          { id: 2, title: '国庆节放假安排', time: '2023-09-28', content: '根据国家规定，10月1日至7日放假调休，共7天。祝大家节日快乐！' },
          { id: 3, title: '服务器维护公告', time: '2023-09-15', content: '为提升服务质量，我们将于本周日凌晨2点至4点进行服务器维护，届时系统将暂停服务。' },
        ]
      })
    },
    drawPieChart() {
      API.get('/api/goods').then(res => {
        let total = 0
        const map = new Map()
        res.data && res.data.forEach(i => {
          const s = i.sales || 0
          total += s
          map.set(i.categoryName, (map.get(i.categoryName) || 0) + s)
        })
        const chartData = map.size ? Array.from(map, ([name, value]) => ({ name, value })) : [
          { name: '面包', value: 11 },
          { name: '蛋糕', value: 5 },
          { name: '饮品', value: 3 },
        ]
        total = total || 19
        const option = {
          tooltip: { trigger: 'item', formatter: '{b}<br/>销量: {c} ({d}%)' },
          legend: { orient: 'vertical', left: 'right', top: 'center' },
          color: ['#ff6f00', '#ff8f00', '#ffb300', '#ffd54f', '#fff59d'],
          series: [{
            type: 'pie',
            radius: ['50%', '70%'],
            center: ['40%', '52%'],
            data: chartData,
            label: { show: false },
            labelLine: { show: false },
            itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          }],
          graphic: {
            type: 'text',
            left: '39%',
            top: '46%',
            style: { text: `总销量\n${total}`, textAlign: 'center', fill: '#5c3a1d', fontSize: 18 },
          },
        }
        const chart = this.$echarts.init(document.getElementById('pie'))
        chart.setOption(option)
        window.addEventListener('resize', () => chart.resize())
      })
    },
  },
}
</script>

<style scoped>
.home-container {
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
.weather-info {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  border: none;
  color: #5c3a1d;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}
.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stat-icon {
  font-size: 46px;
  opacity: 0.9;
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
  background: var(--card-bg);
  border: none;
  height: 100%;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: var(--accent-dark);
}

/* Collapse */
.custom-collapse {
  border: none;
}
::v-deep .el-collapse-item__header {
  background: #fff9f0;
  border: 1px solid var(--border-light);
  border-radius: 4px;
}
::v-deep .el-collapse-item__header:hover {
  background: #fff6e6;
}
::v-deep .el-collapse-item__wrap {
  border: none;
}
::v-deep .el-collapse-item__content {
  padding: 16px 20px;
  background: #ffffff;
  border: 1px solid var(--border-light);
  border-top: none;
  border-radius: 0 0 4px 4px;
}
.collapse-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title-text {
  font-weight: 500;
  color: var(--accent-dark);
}
.title-time {
  font-size: 13px;
  color: var(--text-secondary);
}
.collapse-content {
  color: var(--text-secondary);
  line-height: 1.6;
}
</style>
