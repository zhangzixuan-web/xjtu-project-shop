<template>
  <div>
    <!-- 搜索和新增区域 -->
    <div style="padding: 5px 0">
      <el-input v-model="text" @keyup.enter.native="load" style="width: 200px"> <i slot="prefix" class="el-input__icon el-icon-search"></i></el-input>
      <el-button @click="add" type="primary" size="mini" style="margin: 10px">新增</el-button>
    </div>

    <!-- 商品数据表格 -->
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="100" sortable> </el-table-column>
      <el-table-column prop="name" label="商品名称" width="150"></el-table-column>
      <el-table-column label="图片" width="200px">
        <template slot-scope="scope">
          <el-image style="width: 100px; height: 100px" :src="scope.row.imgs[0]" :preview-src-list="[scope.row.imgs[0]]"></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类"></el-table-column>
      <el-table-column prop="description" label="商品描述" width="300"></el-table-column>
      <el-table-column prop="no" label="商品编号"></el-table-column>
      <el-table-column prop="price" label="原价"></el-table-column>
      <el-table-column prop="discount" label="折扣"></el-table-column>
      <el-table-column prop="store" label="库存"></el-table-column>
      <el-table-column label="是否推荐">
        <template slot-scope="scope">
          <span v-if="scope.row.recommend">是</span>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column prop="praise" label="点赞数"></el-table-column>
      <el-table-column prop="sales" label="销量"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>

      <!-- 操作列 -->
      <el-table-column
          fixed="right"
          label="操作"
          width="200">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" circle  @click="edit(scope.row)"></el-button>
          <el-popconfirm
              @confirm="del(scope.row.id)"
              title="确定删除？"
          >
            <el-button type="danger" icon="el-icon-delete" circle slot="reference" style="margin-left: 10px"></el-button>
          </el-popconfirm>
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

    <!-- 弹窗   -->
    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%"
               :close-on-click-modal="false">
      <el-form :model="entity">
        <el-form-item label="商品名称" label-width="150px">
          <el-input v-model="entity.name" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" label-width="150px">
          <el-input type="textarea" v-model="entity.description" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="商品编号" label-width="150px">
          <el-input v-model="entity.no" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="原价" label-width="150px">
          <el-input v-model="entity.price" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="折扣" label-width="150px">
          <el-input v-model="entity.discount" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="库存" label-width="150px">
          <el-input v-model="entity.store" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="分类" label-width="150px">
          <el-select v-model="entity.categoryId" placeholder="请选择" style="width: 80%">
            <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否推荐" label-width="150px">
          <el-switch
              v-model="entity.recommend"
              active-color="#13ce66"
              inactive-color="#ccc">
          </el-switch>
        </el-form-item>
        <!-- 图片上传 -->
        <el-form-item label="图片" label-width="150px">
          <el-upload action="http://localhost:9999/files/upload" multiple :on-success="handleSuccess" ref="upload">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
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
const url = "/api/goods/"

export default {
  name: "Goods",
  data() {
    return {
      fileList: [],   // 已上传文件列表
      options: [],    // 分类选项
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
    this.user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
    this.load()
  },
  methods: {
    // 文件上传成功钩子
    handleSuccess(res) {
      // res.data 包含文件标识，拼接成可访问的 URL
      let url = "http://localhost:9999/files/" + res.data;
      this.fileList.push(url);
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
    // 加载商品列表和分类信息
    load() {
       API.get(url + "/page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: this.text
          }
       }).then(res => {
          this.tableData = res.data.records || [];
          this.total = res.data.total;

         this.tableData.forEach(item => {
           // 处理表格图片显示，将imgs字段从JSON字符串转为数组
           if (!item.imgs) {
             item.imgs = ['']; // 提供一个默认空图片，避免控制台报错
           } else {
             item.imgs = JSON.parse(item.imgs)
           }
         })
       })

      // 加载分类数据
      API.get("/api/category").then(res => {
        this.options = res.data;
      });
    },
    // 打开新增弹窗
    add() {
      this.entity = {};
      this.fileList = [];
      // 清空已上传文件列表
      if(this.$refs['upload']) {
        this.$refs['upload'].clearFiles();
      }
      this.dialogFormVisible = true;
    },
    // 打开编辑弹窗
    edit(obj) {
      console.log(this.$refs)
      this.entity = JSON.parse(JSON.stringify(obj))
      this.fileList = []
      this.dialogFormVisible = true
      this.$nextTick(() => {
        if(this.$refs['upload']) {
          this.$refs['upload'].clearFiles();
        }
      });
    },
    // 保存（新增或修改）
    save() {
      // 如果有新上传的文件，将其更新到entity
      if (this.fileList.length) {
        this.entity.imgs = JSON.stringify(this.fileList)
      }
      if (typeof this.entity.imgs === 'object') {
        this.entity.imgs = JSON.stringify(this.entity.imgs)
      }
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
    // 删除
    del(id) {
      API.delete(url + id).then(res => {
        this.$message({
          type: "success",
          message: "操作成功"
        })
        this.load()
      })
    }
  },
};
</script>

<style scoped>
</style>
