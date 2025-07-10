<template>
  <div>
    <!-- 搜索和重置区域 -->
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 新增和批量删除区域 -->
    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <!-- 商品数据表格 -->
    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column label="图片"><template slot-scope="scope"><el-image style="width: 100px; height: 100px" :src="scope.row.img" :preview-src-list="[scope.row.img]"></el-image></template></el-table-column>
      <el-table-column prop="price" label="价格"></el-table-column>
      <el-table-column prop="discount" label="折扣"></el-table-column>
      <el-table-column prop="sales" label="销量"></el-table-column>
      <el-table-column prop="createTime" label="上架时间"></el-table-column>
      <el-table-column prop="categoryName" label="分类"></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作"  width="200" align="center">
        <template slot-scope="scope">
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog title="商品信息" :visible.sync="dialogFormVisible" width="30%" >
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <!-- 自定义样式的图片上传 -->
          <el-upload
              class="avatar-uploader"
              action="http://localhost:9999/files/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="form.img" :src="form.img" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="折扣">
          <el-input v-model="form.discount" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select clearable v-model="form.categoryId" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="推荐">
          <el-switch v-model="form.recommend" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
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
import request from "@/utils/request";

export default {
  name: "MerchantGoods",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      categories: []
    }
  },
  created() {
    this.load()

    // 加载分类信息，用于弹窗中的下拉选择
    request.get("/api/category").then(res => {
      this.categories = res.data
    })
  },
  methods: {
    // 加载商品列表
    load() {
      request.get("/api/merchant/goods/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    // 保存商品（新增或修改）
    save() {
      // 使用商家专用的接口
      request.post("/api/merchant/goods", this.form).then(res => {
        if (res.code === '0') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    // 打开新增弹窗
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
    },
    // 打开编辑弹窗
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    // 删除商品
    del(id) {
      request.delete("/api/merchant/goods/" + id).then(res => {
        if (res.code === '0') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    // 处理表格多选变化
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    // 批量删除
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)
      // 注意！批量删除也需要调用商家接口，但我们后端没做，这里暂时注释掉
      // 如果需要，后端也需要增加 /merchant/del/batch 接口
      this.$message.warning("此功能暂未开放")
      // request.post("/api/merchant/goods/del/batch", ids).then(res => {
      //   if (res.code === '0') {
      //     this.$message.success("批量删除成功")
      //     this.load()
      //   } else {
      //     this.$message.error("批量删除失败")
      //   }
      // })
    },
    // 重置搜索条件
    reset() {
      this.name = ""
      this.load()
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
    // 图片上传成功回调
    handleAvatarSuccess(res) {
      this.form.img = "http://localhost:9999/files/" + res.data
    }
  }
}
</script>

<style>
/* 自定义表格头部背景色 */
.headerBg {
  background: #eee!important;
}
/* 自定义图片上传组件样式 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 