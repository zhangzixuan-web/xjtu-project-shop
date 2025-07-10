<template>
  <div>
    <div style="margin-top: 10px">
      <el-row>
        <el-col :span="24">
          <el-card>
            <div style="padding: 10px 0">
              <!-- 搜索输入框和按钮 -->
              <div style="margin-bottom: 20px">
                <el-input placeholder="输入商品名称搜索" style="width: 70%" v-model="searchText"></el-input>
                <el-button style="margin-left: 5px" @click="loadTable">搜索</el-button>
              </div>
              <!-- 搜索结果展示 -->
              <el-row :gutter="10">
                <el-col :span="15" v-for="item in tableData" :key="item.id" style="margin-bottom: 10px; text-align: center">
                  <!-- 商品卡片 -->
                  <div style="border: 1px solid #ccc; padding: 10px; cursor: pointer" @click="goodsDetail(item.id)">
                    <el-image :src="item.imgs[0]" style="width: 100%; height: 150px;" fit="contain"></el-image>
                    <!-- 商品名称，使用 Tooltip 防止过长显示不全 -->
                    <div style="padding: 5px; height: 20px; text-align: center; font-size: 14px; overflow: hidden">
                      <el-tooltip :content="item.name" placement="bottom" effect="light">
                        <span class="item-title" style="text-align: center">{{ item.name }}</span>
                      </el-tooltip>
                    </div>
                    <!-- 商品价格 -->
                    <div style="padding: 5px; text-align: center; color: red; font-size: 14px">
                      ￥ {{ item.price }}
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 分页 -->
            <div style="margin-top: 20px; text-align: center;">
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

          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "Search",
  data() {
    return {
      searchText: '',  // 搜索关键词
      tableData: [],   // 搜索结果数据
      user: {},
      pageNum: 1,      // 当前页码
      pageSize: 8,     // 每页显示条数
      total: 0         // 总条数
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    // 从路由参数中获取搜索关键词
    this.searchText = this.$route.query.searchText || ''
    // 加载初始搜索结果
    this.loadTable()
  },
  methods: {
    // 跳转到商品详情页
    goodsDetail(id) {
      this.$router.push({path: '/front/goods', query: {id: id}})
    },
    // 加载搜索结果数据
    loadTable() {
      API.get("/api/goods/page", {
        params: {
          name: this.searchText,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        this.tableData = res.data.records;
        this.total = res.data.total;

        // 安全地处理商品图片
        this.tableData.forEach(item => {
          if (!item.imgs) {
            item.imgs = [''];
          } else if (typeof item.imgs === 'string') {
            try {
              item.imgs = JSON.parse(item.imgs);
            } catch (e) {
              item.imgs = [''];
            }
          }
        });
      })
    },
    // 处理每页显示条数变化
    handleSizeChange(pageSize) { // 注意：这里参数应该是 pageSize
      this.pageSize = pageSize;
      this.loadTable();
    },
    // 处理页码变化
    handleCurrentChange(pageNum) { // 注意：这里参数应该是 pageNum
      this.pageNum = pageNum;
      this.loadTable();
    }
  },
};
</script>

<style scoped>
.el-row {
  display: flex;
  justify-content: center;
}

.item-title {
  display: block;
  text-align: center;
}

.el-image {
  display: block;
  margin: 0 auto;
}
</style>
