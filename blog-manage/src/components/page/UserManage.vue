<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item> <i class="el-icon-lx-addressbook"></i> 用户管理 </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-input v-model="query.username" placeholder="用户名" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch(1)">搜索</el-button>
                <el-button type="primary" icon="el-icon-refresh" @click="refreshData">重置</el-button>
            </div>
            <el-table :data="users" border class="table" header-cell-class-name="table-header">
                <el-table-column prop="id" label="ID" width="55" align="center"></el-table-column>
                <el-table-column prop="username" label="用户名"></el-table-column>
                <!-- <el-table-column prop="nick" label="昵称"></el-table-column> -->

                <!-- <el-table-column label="评论内容" width="200">
                    <template slot-scope="scope">{{scope.row.content}}</template>
                </el-table-column> -->
                <el-table-column label="用户头像" align="center">
                    <template slot-scope="scope">
                        <el-image class="table-td-thumb" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"></el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="introduction" label="用户简介"></el-table-column>

                <el-table-column prop="role" label="用户角色"></el-table-column>

                <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 编辑弹出框 -->
            <el-dialog title="编辑" :visible.sync="editVisible" width="30%">
                <el-form ref="userform" :model="userform" label-width="70px">
                    <el-form-item label="ID">
                        <el-input v-model="userform.id" :disabled="true" :hidden="userform.id"></el-input>
                    </el-form-item>
                    <el-form-item label="用户名">
                        <el-input v-model="userform.username" :disabled="true"></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="newpassword">
                    <el-input v-model="userform.newPassword" type="password"></el-input>
                    </el-form-item>
                    <el-form-item label="用户角色">
                        <el-input v-model="userform.newRole"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="clearUserform()">取 消</el-button>
                    <el-button type="primary" @click="saveEdit">确 定</el-button>
                </span>
            </el-dialog>

            <!-- 分页条 -->
            <div class="pagination">
                <el-pagination
                    @current-change="getData"
                    :current-page="currentPage"
                    :page-count="total"
                    layout="prev, pager, next"
                    background
                    hide-on-single-page
                ></el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
import CryptoJS from 'crypto-js/crypto-js'
export default {
    name: 'users',
    inject: ['reload'],
    data() {
        return {
            tmp: 1,
            editVisible: false,
            userform: {
                id:null,
                username: '',
                newPassword: '',
                newRole:''
            }
                
            ,
            momentList: [],
            fileList: '',
            isAdd: true,
            query: {
                username: ''
            },
            users: [],
            // comments: [],
            currentPage: 1,
            pageSize: 6,
            total: 0
        };
    },
    mounted() {
        this.getData(1);
    },
    methods: {
        Encrypt(word) {
            const key = CryptoJS.enc.Utf8.parse('1234567812345678') // 十六位十六进制数作为密钥
            const iv = CryptoJS.enc.Utf8.parse("1234567812345678"); //十六位十六进制数作为密钥偏移量
            var srcs = CryptoJS.enc.Utf8.parse(word);
            var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
            return encrypted.toString();
        },
        getData(currentPage) {
            if (this.tmp == 1) this.getUser(currentPage);
            else this.handleSearch(currentPage);
        },
        getUser(currentPage) {
            this.tmp = 1;
            const _this = this;
            this.$axios
                .get('/admin/user/list', {
                    params: {
                        currentPage: currentPage
                    }
                })
                .then((res) => {
                    _this.users = res.data.data.records;
                    _this.currentPage = res.data.data.current;
                    _this.total = res.data.data.pages;
                });
        },
        // 触发搜索按钮
        handleSearch(currentPage) {
            this.tmp = 2;
            const _this = this;
            this.$axios
                .get('/admin/user/queryUsername/' + _this.query.username, {
                    params: {
                        currentPage: currentPage
                    }
                })
                .then((res) => {
                    if (res.data.code == 200) {
                        _this.users = res.data.data.records;
                        _this.currentPage = res.data.data.current;
                        _this.total = res.data.data.pages;
                        this.$message.success(res.data.msg);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                });
        },
        // 删除操作
        handleDelete(index) {
            // 二次确认删除
            this.$confirm('确定要删除吗？', '提示', {
                type: 'warning'
            })
                .then(() => {
                    this.$axios
                        .delete('/admin/user/delete' + '?id=' + index)
                        .then((res) => {
                            if (res.data.code == 200) {
                                this.$message.success(res.data.msg);
                                this.refreshData2(this.tmp);
                                //this.reload();
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        })
                        .catch((err) => {
                            this.$message.error('不要再试了哦，没有权限');
                        });
                })
                .catch(() => {});
        },
        // 编辑操作
        handleEdit(index, row) {
            this.userform.id = row.id;
            this.userform.username = row.username;
            this.userform.newRole = row.role;
            this.isAdd = false;
            this.editVisible = true;
        },
        refreshData() {
            this.tmp = 1;
            this.getData(1);
        },
        refreshData2(op) {
            this.tmp = op;
            if (this.users.length == 1) {
                this.currentPage--;
            }
            this.getData(this.currentPage);
        },

        // 保存编辑
        saveEdit() {
            const _this = this;
            //修改标签     

            const form= {
                id: this.userform.id,
                username: this.userform.username,
                newPassword: this.Encrypt(this.userform.newPassword),
                newRole: this.userform.newRole
            }
            console.log(form)
            if (!_this.isAdd) {
                this.$axios
                    .post('/admin/user/update', form)
                    .then((res) => {   
                        if (res.data.code == 200) {
                            this.$message.success(res.data.msg);
                            this.refreshData2(this.tmp);
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    })
                    .catch((err) => {
                    });
            }
            this.clearUserform()
        },
        clearUserform(){
            this.editVisible = false;

            const values = {
                id:'',
                username:'',
                newPassword:'',
                newRole:''
                }
            this.userform=values;
        }

    },
    
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-select {
    width: 120px;
}

.handle-input {
    width: 300px;
    display: inline-block;
}
.table {
    width: 100%;
    font-size: 14px;
}
.red {
    color: #ff0000;
}
.mr10 {
    margin-right: 10px;
}
.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}
/*上传图片*/
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}
.avatar-uploader .el-upload:hover {
    border-color: #409eff;
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
.el-table >>> .warning-row {
    background: oldlace;
}

.el-table .success-row {
    background: #f0f9eb;
}
</style>
