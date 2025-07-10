<template>
  <div>
    <!-- 收藏列表表格 -->
    <el-table :data="tableData" border stripe style="width: 100%; margin-top: 10px"
              header-align="center">
      <!-- 商品名称列 -->
      <el-table-column label="商品名称" width="250" align="center">
        <template slot-scope="scope">
          <!-- 点击名称跳转到商品详情页 -->
          <a :href="['/front/goods?id=' + scope.row.goodsId]">{{ scope.row.goodsName }}</a>
        </template>
      </el-table-column>

      <!-- 商品图片列 -->
      <el-table-column prop="goodsImg" label="商品图片" width="200" align="center">
        <template slot-scope="scope">
          <el-image :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"
                    style="width: 100%; height: 100px" fit="contain"></el-image>
        </template>
      </el-table-column>

      <!-- 收藏时间列 -->
      <el-table-column prop="createTime" label="收藏时间" width="250" align="center">
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column fixed="right" label="操作" width="250" align="center">
        <template slot-scope="scope">
          <!-- 取消收藏按钮，使用 Popconfirm 进行二次确认 -->
          <el-popconfirm @confirm="del(scope.row.id)" title="确定取消收藏吗？">
            <el-button type="danger" slot="reference" style="margin-left: 10px">取消收藏</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div style="margin-top: 10px; background-color: white; padding: 10px 0;">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[2, 5, 10]"
          layout="prev, pager, next"
          :total="total"
      >
      </el-pagination>
    </div>

    <!-- 弹窗 (注：此弹窗在收藏夹页面通常不会被触发，可能是开发时遗留) -->
    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form :model="entity">
        <el-form-item label="商品名称" label-width="150px">
          <el-input v-model="entity.goodsName" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="商品图片" label-width="150px">
          <el-input v-model="entity.goodsImg" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="商品id" label-width="150px">
          <el-input v-model="entity.goodsId" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="用户id" label-width="150px">
          <el-input v-model="entity.userId" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="收藏时间" label-width="150px">
          <el-date-picker style="width: 80%" v-model="entity.createTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '../../utils/request'
const url = "/api/collect/"

export default {
  name: "Collect",
  data() {
    return {
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      entity: {},
      total: 0,
      dialogFormVisible: false
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
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
    // 加载收藏列表数据
    load() {
      API.get(url + "/page/front", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total
      })
    },
    // 打开新增弹窗 (在此页面逻辑中通常不使用)
    add() {
      this.entity = {}
      this.dialogFormVisible = true
    },
    // 打开编辑弹窗 (在此页面逻辑中通常不使用)
    edit(obj) {
      this.entity = JSON.parse(JSON.stringify(obj))
      this.dialogFormVisible = true
    },
    // 保存 (在此页面逻辑中通常不使用)
    save() {
      const apiCall = this.entity.id ? API.put(url, this.entity) : API.post(url, this.entity);
      apiCall.then(res => {
        if (res.code === '0') {
          this.$message.success("操作成功");
          this.load();
          this.dialogFormVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 取消收藏
    del(id) {
      API.delete(url + id).then(res => {
        if(res.code === '0') {
            this.$message.success("操作成功");
            this.load(); // 重新加载列表
        } else {
            this.$message.error(res.msg);
        }
      })
    }
  },
};
</script>

<style scoped>
/* 样式可以保持不变或根据需要调整 */
</style>
