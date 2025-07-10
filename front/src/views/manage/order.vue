<template>
  <div>
    <!-- 搜索区域 -->
    <div style="padding: 5px 0">
      <el-input v-model="text" @keyup.enter.native="load" style="width: 200px">
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </div>

    <!-- 订单数据表格 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="100" sortable> </el-table-column>
      <el-table-column prop="orderNo" label="订单编号"></el-table-column>
      <el-table-column prop="totalPrice" label="总价"></el-table-column>
      <el-table-column prop="userId" label="下单人id"></el-table-column>
      <el-table-column prop="linkUser" label="联系人"></el-table-column>
      <el-table-column prop="linkPhone" label="联系电话"></el-table-column>
      <el-table-column prop="linkAddress" label="送货地址"></el-table-column>
      <el-table-column prop="state" label="状态"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>

      <!-- 操作列 -->
      <el-table-column
          fixed="right"
          label="操作"
          width="200">
        <template slot-scope="scope">
          <!-- 仅在订单状态为'待发货'时显示发货按钮 -->
          <el-button type="primary" @click="out(scope.row)" v-if="scope.row.state === '待发货'">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 10px">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[2, 5, 10]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
      >
      </el-pagination>
    </div>

    <!-- 弹窗 -->
    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form :model="entity">
        <el-form-item label="订单编号" label-width="150px">
          <el-input v-model="entity.orderNo" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="总价" label-width="150px">
          <el-input v-model="entity.totalPrice" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="下单人id" label-width="150px">
          <el-input v-model="entity.userId" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="联系人" label-width="150px">
          <el-input v-model="entity.linkUser" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" label-width="150px">
          <el-input v-model="entity.linkPhone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="送货地址" label-width="150px">
          <el-input v-model="entity.linkAddress" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="状态" label-width="150px">
          <el-input v-model="entity.state" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="创建时间" label-width="150px">
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
const url = "/api/order/"

export default {
  name: "Order",
  data() {
    return {
      text: '',
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      entity: {},
      total: 0,
      dialogFormVisible: false
    };
  },
  created() {
    this.load()
  },
  methods: {
    // 发货操作
    out(obj) {
      this.entity = JSON.parse(JSON.stringify(obj))
      this.entity.state = '待收货'
      this.save()
    },
    // 处理每页显示条数变化
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.load();
    },
    // 处理页码变化
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.load();
    },
    // 加载订单列表
    load() {
      API.get(url + "/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.text
        }
      }).then(res => {
        this.tableData = res.data.records || [];
        this.total = res.data.total || 0;
      });
    },
    save() {
      if (!this.entity.id) {
        API.post(url, this.entity).then(res => {
          if (res.code === '0') {
            this.$message({
              type: "success",
              message: "操作成功"
            })
          } else {
            this.$message({
              type: "error",
              message: res.msg
            })
          }
          this.load()
          this.dialogFormVisible = false
        })
      } else {
        API.put(url, this.entity).then(res => {
          if (res.code === '0') {
            this.$message({
              type: "success",
              message: "操作成功"
            })
          } else {
            this.$message({
              type: "error",
              message: res.msg
            })
          }
          this.load()
          this.dialogFormVisible = false
        })
      }
    },
    del(id) {
      API.delete(url + id).then(res => {
        this.$message({
          type: "success",
          message: "操作成功"
        })
        this.load()
      })
    }
  }
};
</script>
