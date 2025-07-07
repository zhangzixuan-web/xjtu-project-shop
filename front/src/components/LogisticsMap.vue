<template>
  <div>
    <!-- 地图容器 -->
    <div id="map-container" style="width: 100%; height: 400px; border: 1px solid #ccc;"></div>

    <!-- 使用 Element UI 或 Ant Design Vue 的 Timeline 组件效果更佳 -->
    <div style="margin-top: 20px;">
      <h3>物流跟踪</h3>
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
/* global AMap */ // 告诉 linter AMap 是一个全局变量

export default {
  name: 'LogisticsMap',
  props: {
    trackingData: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      map: null
    };
  },
  mounted() {
    this.initMap();
  },
  methods: {
    initMap() {
      // 确保数据有效且 DOM 已准备好
      if (!this.trackingData || this.trackingData.length === 0) {
        return;
      }

      this.map = new AMap.Map('map-container', {
        zoom: 5,
        center: [this.trackingData[0].longitude, this.trackingData[0].latitude]
      });

      const path = this.trackingData.map(step => [step.longitude, step.latitude]);

      // 绘制轨迹线
      const polyline = new AMap.Polyline({
        path: path,
        strokeColor: "#ff9233",
        strokeWeight: 6,
        strokeOpacity: 0.8
      });
      this.map.add(polyline);

      // ===================================================

      // 1. 创建起点标记 (用一个自定义的“仓库”样式)
      const startMarker = new AMap.Marker({
        position: path[0],
        // 使用 content 自定义HTML内容
        content: `<div class="marker-start-point"></div>`,
        // offset 用来校准图标的中心点，让尖角对准坐标
        offset: new AMap.Pixel(-15, -30),
        map: this.map
      });

      // 2. 创建终点/当前位置标记 (换成我们的小货车图标)
      const endMarker = new AMap.Marker({
        position: path[path.length - 1],
        // 直接使用 icon 属性，传入图片的URL
        icon: 'https://webapi.amap.com/images/car.png',
        offset: new AMap.Pixel(-13, -26), // 根据图片大小微调偏移
        map: this.map
      });

      // 3. （可选）如果你想把中间的途经点也改了
      for (let i = 1; i < path.length - 1; i++) {
        const marker = new AMap.Marker({
          position: path[i],
          content: `<div class="marker-via-point"></div>`, // 一个小的途经点样式
          offset: new AMap.Pixel(-5, -5),
          map: this.map,
        });
      }


      // 地图自适应视野
      this.map.setFitView();
    }
  }
}
</script>

<style>
/* 【新增】把这些样式加到 <style> 标签里 */

/* 自定义起点图标的样式 */
/* 我们用CSS画一个简单的水滴形状 */
.marker-start-point {
  width: 30px;
  height: 30px;
  background-color: #ffcc61; /* 绿色 */
  border-radius: 50% 50% 50% 0;
  transform: rotate(-45deg);
  border: 2px solid white;
  box-shadow: 0 0 10px rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
/* 在水滴里加一个"起"字 */
.marker-start-point::after {
  content: '起';
  color: white;
  font-weight: bold;
  transform: rotate(45deg); /* 把字转正 */
  font-size: 14px;
}


/* 自定义途经点的样式 */
/* 一个简单的蓝色小圆点 */
.marker-via-point {
  width: 10px;
  height: 10px;
  background-color: #fb6311; /* 蓝色 */
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 5px rgba(0,0,0,0.4);
}
</style>