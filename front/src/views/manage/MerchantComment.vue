<template>
  <div>
    <div style="padding: 5px 0">
      <el-input v-model="text" @keyup.enter.native="load" style="width: 200px" placeholder="输入评论内容查询">
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </div>

    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
      <el-table-column prop="goodsName" label="商品名称"></el-table-column>
      <el-table-column prop="content" label="评论内容" width="300"></el-table-column>
      <el-table-column prop="username" label="评论人"></el-table-column>
      <el-table-column prop="time" label="评论时间"></el-table-column>
      <el-table-column fixed="right" label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="reply(scope.row.id)">回复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 10px">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="回复评论" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="内容">
          <el-input type="textarea" v-model="replyForm.content" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import API from '../../utils/request'
const url = "/api/merchant/comment/"

export default {
  name: "MerchantComment",
  data() {
    return {
      text: '',
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogFormVisible: false,
      replyForm: {
        content: '',
        parentId: null,
        goodsId: null,
        replyTo: null,
      },
    };
  },
  created() {
    this.load()
  },
  methods: {
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
    },
    reply(parentId) {
      const originalComment = this.tableData.find(c => c.id === parentId);
      this.replyForm = {
        content: '',
        parentId: parentId,
        goodsId: originalComment.goodsId,
        replyTo: originalComment.userId
      };
      this.dialogFormVisible = true;
    },
    submitReply() {
      API.post("/api/comment", this.replyForm).then(res => {
        if (res.code === '0') {
          this.$message.success("回复成功");
          this.dialogFormVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      })
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
};
</script>

<style scoped>
</style> 