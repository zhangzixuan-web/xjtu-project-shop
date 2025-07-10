<template>
  <div>
    <!-- 地图容器 -->
    <div id="map-container" style="width: 100%; height: 400px; border: 1px solid #ccc;"></div>

    <!-- 物流时间线 -->
    <!-- 提示：为了更好的UI效果和可维护性，建议使用成熟的UI库（如Element UI）的Timeline组件 -->
    <div style="margin-top: 20px;">
      <h3>物流跟踪</h3>
      <!-- 手动实现的简易时间线 -->
      <ul>
        <li v-for="(step, index) in trackingData" :key="index" style="list-style: none; margin-bottom: 10px; padding-left: 20px; border-left: 2px solid #e4e7ed; position: relative;">
          <div style="position: absolute; left: -8px; top: 0; width: 14px; height: 14px; background: #909399; border-radius: 50%;"></div>
          <p style="margin: 0;"><strong>{{ step.time }}</strong></p>
          <p style="margin: 0; color: #606266;">{{ step.context }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
/* global AMap */ // 告诉 linter AMap 是一个已在外部（如index.html）引入的全局变量

export default {
  name: 'LogisticsMap',
  props: {
    // 从父组件接收物流跟踪数据
    trackingData: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      map: null // 地图实例
    };
  },
  mounted() {
    this.initMap();
  },
  methods: {
    initMap() {
      // 确保数据有效且 DOM 已准备好
      if (!this.trackingData || this.trackingData.length === 0) {
        console.warn("无有效的物流数据来初始化地图。");
        return;
      }

      // 初始化高德地图
      this.map = new AMap.Map('map-container', {
        zoom: 5,
        // 使用第一个数据点作为地图中心
        center: [this.trackingData[0].longitude, this.trackingData[0].latitude]
      });

      // 提取经纬度数组用于绘制轨迹
      const path = this.trackingData.map(step => [step.longitude, step.latitude]);

      // 绘制轨迹线
      const polyline = new AMap.Polyline({
        path: path,
        strokeColor: "#ff9233", // 线条颜色
        strokeWeight: 6,         // 线条宽度
        strokeOpacity: 0.8       // 线条透明度
      });
      this.map.add(polyline);

      // --- 自定义标记点 ---

      // 1. 创建起点标记 (使用自定义的“水滴”样式)
      const startMarker = new AMap.Marker({
        position: path[0],
        content: `<div class="marker-start-point"></div>`, // 使用 content 自定义HTML内容
        offset: new AMap.Pixel(-15, -30), // 校准图标的中心点
        map: this.map
      });

      // 2. 创建终点/当前位置标记 (使用高德提供的小货车图标)
      const endMarker = new AMap.Marker({
        position: path[path.length - 1],
        icon: 'https://webapi.amap.com/images/car.png', // 使用 icon 属性传入图片URL
        offset: new AMap.Pixel(-13, -26), // 根据图片大小微调偏移
        map: this.map
      });

      // 3. (可选) 为中间的途经点添加标记
      for (let i = 1; i < path.length - 1; i++) {
        new AMap.Marker({
          position: path[i],
          content: `<div class="marker-via-point"></div>`, // 一个小的途经点样式
          offset: new AMap.Pixel(-5, -5),
          map: this.map,
        });
      }

      // 地图自适应视野，确保所有标记和轨迹线都在可视范围内
      this.map.setFitView();
    }
  }
}
</script>

<style>
/* 自定义起点图标的样式：一个带“起”字的水滴形状 */
.marker-start-point {
  width: 30px;
  height: 30px;
  background-color: #ffcc61; /* 主题色 */
  border-radius: 50% 50% 50% 0;
  transform: rotate(-45deg);
  border: 2px solid white;
  box-shadow: 0 0 10px rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
/* 在水滴里添加"起"字，并转正 */
.marker-start-point::after {
  content: '起';
  color: white;
  font-weight: bold;
  transform: rotate(45deg);
  font-size: 14px;
}

/* 自定义途经点的样式：一个简单的橙色小圆点 */
.marker-via-point {
  width: 10px;
  height: 10px;
  background-color: #fb6311;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 5px rgba(0,0,0,0.4);
}
</style>