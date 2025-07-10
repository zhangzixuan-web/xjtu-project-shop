<template>
  <div>
    <el-card style="margin-top: 10px">
      <!-- 使用折叠面板展示公告，accordion 属性表示每次只能展开一个面板 -->
      <el-collapse v-model="activeNames" :accordion="true">
        <!-- 遍历公告数据 -->
        <el-collapse-item :name="item.id" v-for="item in tableData" :key="item.id">
          <!-- 自定义标题 -->
          <template slot="title">
            <div style="color: #666; font-size: 16px">
              <span>{{ item.title }}</span>
              <span style="margin-left: 20px">{{ item.time }}</span>
            </div>
          </template>
          <!-- 公告内容 -->
          <div style="padding: 10px">{{ item.content }}</div>
        </el-collapse-item>
      </el-collapse>

      <!-- 分页组件 -->
      <div style="margin-top: 10px">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-size="pageSize"
            :page-sizes="[5, 10, 15]"
            layout="prev, pager, next"
            :total="total"
        >
        </el-pagination>
      </div>
    </el-card>

  </div>
</template>

<script>
import API from "@/utils/request";

export default {
  name: "FrontNotice",
  data() {
    return {
      activeNames: 1,  // 当前激活的折叠面板的 name，默认为1
      tableData: [],   // 公告列表数据
      total: 0,        // 总条数
      pageNum: 1,      // 当前页码
      pageSize: 10     // 每页显示条数
    }
  },
  created() {
    this.load()
  },
  methods: {
    // 处理每页显示条数变化
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    // 处理页码变化
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    // 加载公告数据
    load() {
      API.get( "/api/notice/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: '' // name 参数似乎是多余的，可以考虑移除
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total
        // 如果有数据，默认展开第一条
        if (this.tableData.length > 0) {
          this.activeNames = this.tableData[0].id
        }
      })
    }
  }
}
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>
