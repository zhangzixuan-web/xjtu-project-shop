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
      <!-- 在你的 el-table 中找到“图片”列，然后用下面的代码替换 -->

      <el-table-column label="图片" width="120" align="center">
        <template slot-scope="scope">
          <!--
            1. v-if 判断：确保在 scope.row.imgs 存在，并且是一个有内容的数组时，才渲染图片组件。
            这是防止因为数据为空导致页面报错的关键。
          -->
          <el-image
              v-if="scope.row.imgs && Array.isArray(scope.row.imgs) && scope.row.imgs.length > 0"
              style="width: 80px; height: 80px"
              :src="scope.row.imgs[0]"
              :preview-src-list="scope.row.imgs"
              fit="cover">

            <!-- 加载失败时的占位内容 -->
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline" style="font-size: 30px;"></i>
            </div>
            <!-- 加载中的占位内容 -->
            <div slot="placeholder" class="image-slot">
              加载中<span class="dot">...</span>
            </div>

          </el-image>

          <!--
            2. v-else：如果上面的 v-if 不满足（即没有图片数据），则显示一个友好的提示。
          -->
          <div v-else class="no-image">
            <span>暂无图片</span>
          </div>

        </template>
      </el-table-column>
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

          <!-- 自定义样式的图片上传 -->
          <el-form-item label="商品图片">
            <el-upload
                :action="'http://localhost:9999/api/files/upload'"
                list-type="picture-card"
                :on-success="handleAvatarSuccess"
                :on-remove="handleRemove"
                :file-list="fileList">
              <i class="el-icon-plus"></i>
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
      // 【修改1】: form 和 fileList 应该是独立的，form 用于提交，fileList 用于显示
      form: {},
      fileList: [], // 用于控制 el-upload 的文件列表显示
      dialogFormVisible: false,
      multipleSelection: [],
      categories: []
    }
  },
  created() {
    this.load();

    request.get("/api/category").then(res => {
      this.categories = res.data;
    });
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
        this.tableData = res.data.records;
        this.total = res.data.total;
      });
    },

    // 【修改2】: 保存商品（新增或修改），逻辑重构
    save() {
      // 1. 复制一份 form 数据，避免污染原始数据
      const dataToSubmit = { ...this.form };

      // 2. 检查 imgs 是否为数组，如果不是则初始化为空数组（增加代码健壮性）
      if (!Array.isArray(dataToSubmit.imgs)) {
        dataToSubmit.imgs = [];
      }

      // 3. 将 imgs 数组转换为 JSON 字符串，以匹配后端 String 类型的字段
      dataToSubmit.imgs = JSON.stringify(dataToSubmit.imgs);

      console.log("最终提交给后端的数据:", dataToSubmit);

      // 4. 根据是否有 id 来判断是新增还是更新
      // 你的后端接口设计是同一个 POST /api/merchant/goods，由后端判断，所以前端无需区分
      request.post("/api/merchant/goods", dataToSubmit).then(res => {
        if (res.code === '0') {
          this.$message.success("保存成功");
          this.dialogFormVisible = false;
          this.load();
        } else {
          this.$message.error(res.msg || "保存失败");
        }
      });
    },

    // 【修改3】: 打开新增弹窗，必须正确初始化 form
    handleAdd() {
      this.dialogFormVisible = true;
      // 初始化 form 对象，为所有字段提供默认值，特别是 imgs 必须是空数组！
      this.form = {
        name: '',
        description: '',
        price: 0.00,
        discount: 1.0,
        store: 0,
        categoryId: null,
        imgs: [] // <<-- 这是成功的关键！
      };
      // 同时清空 el-upload 的显示列表
      this.fileList = [];
    },

    // 【修改4】: 打开编辑弹窗，需要处理 imgs 字符串到数组的转换
    handleEdit(row) {
      // 深拷贝，防止修改弹窗内数据时，表格数据也跟着变
      this.form = JSON.parse(JSON.stringify(row));

      // 将后端返回的 imgs JSON 字符串解析成数组，用于数据绑定
      try {
        if (typeof this.form.imgs === 'string' && this.form.imgs) {
          this.form.imgs = JSON.parse(this.form.imgs);
        } else if (!Array.isArray(this.form.imgs)) {
          this.form.imgs = [];
        }
      } catch (e) {
        console.error("解析图片数据失败", e);
        this.form.imgs = [];
      }

      // 根据解析出的 imgs 数组，生成 el-upload 需要的 fileList
      this.fileList = this.form.imgs.map(url => ({
        name: url.substring(url.lastIndexOf('/') + 1), // 从URL中提取文件名
        url: url,
        response: { data: url } // 模拟上传成功后的 response，方便删除
      }));

      this.dialogFormVisible = true;
    },

    // 删除商品
    del(id) {
      this.$confirm('此操作将永久删除该商品, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete("/api/merchant/goods/" + id).then(res => {
          if (res.code === '0') {
            this.$message.success("删除成功");
            this.load();
          } else {
            this.$message.error(res.msg || "删除失败");
          }
        });
      });
    },

    // 【修改5】: 图片上传成功回调，逻辑完全重写
    handleAvatarSuccess(res, file, fileList) {
      if (res.code === '0' && res.data) {
        // res.data 应该是完整的 URL, e.g., "http://localhost:9999/files/123456.jpg"
        console.log("图片上传成功，URL:", res.data);

        // 如果 form.imgs 还不是数组，先初始化它
        if (!Array.isArray(this.form.imgs)) {
          this.form.imgs = [];
        }

        // 将新的 URL 添加到要提交的数据中
        this.form.imgs.push(res.data);

        // 更新 fileList 以正确显示 el-upload 的状态
        this.fileList = fileList;

        console.log("更新后 this.form.imgs:", this.form.imgs);
      } else {
        this.$message.error(res.msg || "图片上传失败");
        // 上传失败时，从 fileList 中移除这个失败的文件
        const failedFileIndex = fileList.findIndex(f => f.uid === file.uid);
        if (failedFileIndex > -1) {
          fileList.splice(failedFileIndex, 1);
        }
        this.fileList = fileList;
      }
    },

    // 【新增方法】: 处理图片移除
    handleRemove(file, fileList) {
      // 从 this.form.imgs 数组中移除对应的 URL
      const urlToRemove = file.response ? file.response.data : file.url;
      if (urlToRemove) {
        const index = this.form.imgs.indexOf(urlToRemove);
        if (index !== -1) {
          this.form.imgs.splice(index, 1);
        }
      }
      // 更新 el-upload 的显示列表
      this.fileList = fileList;
      console.log("移除后 this.form.imgs:", this.form.imgs);
    },

    // --- 以下是你的其他方法，保持不变 ---
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    delBatch() {
      // ...
    },
    reset() {
      this.name = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
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