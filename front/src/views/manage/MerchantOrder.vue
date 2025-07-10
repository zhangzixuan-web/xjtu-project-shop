<template>
  <div>
    <!-- 搜索区域 -->
    <div style="padding: 5px 0">
      <el-input v-model="text" @keyup.enter.native="load" style="width: 200px"> <i slot="prefix" class="el-input__icon el-icon-search"></i></el-input>
    </div>

    <!-- 商家订单数据表格 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="100" sortable> </el-table-column>
      <el-table-column prop="orderNo" label="订单编号"></el-table-column>
      <el-table-column prop="totalPrice" label="总价"></el-table-column>
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
          <el-button type="primary" @click="ship(scope.row)" v-if="scope.row.state === '待发货'">发货</el-button>
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

    <!-- 发货信息弹窗 -->
    <el-dialog title="发货信息" :visible.sync="dialogFormVisible" width="30%"
               :close-on-click-modal="false">
      <el-form :model="entity" label-width="100px">
        <el-form-item label="快递公司">
          <el-input v-model="entity.shippingCompany" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="entity.shippingNumber" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmShip">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '../../utils/request'
// 商家订单专用接口
const url = "/api/merchant/order/"

export default {
  name: "MerchantOrder",
  data() {
    return {
      text: '',
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      entity: {}, // 用于暂存发货信息
      total: 0,
      dialogFormVisible: false
    };
  },
  created() {
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
    // 打开填写发货信息的弹窗
    ship(row) {
      this.entity = JSON.parse(JSON.stringify(row));
      // 初始化快递信息字段，防止旧数据残留
      this.entity.shippingCompany = '';
      this.entity.shippingNumber = '';
      this.dialogFormVisible = true;
    },
    // 确认发货，提交快递信息
    confirmShip() {
      API.post(url + this.entity.id + '/ship', {
        company: this.entity.shippingCompany,
        number: this.entity.shippingNumber
      }).then(res => {
        if (res.code === '0') {
          this.$message.success("发货成功")
          this.dialogFormVisible = false;
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
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
    // 加载商家自己的订单列表
    load() {
       API.get(url + "page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: this.text
          }
       }).then(res => {
          this.tableData = res.data.records || []
          this.total = res.data.total
       })
    }
  },
};
</script>

<style scoped>
</style> 