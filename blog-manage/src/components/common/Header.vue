<template>
    <div class="header">
        <!-- 折叠按钮 -->
        <!-- 侧边栏是否折叠，通过 -->
        <div class="collapse-btn" @click="collapseChage">
            <i v-if="!collapse" class="el-icon-remove-outline"></i>
            <i v-else class="el-icon-circle-plus-outline"></i>
        </div>
        <div class="logo">Blog后台管理系统</div>
        <div class="myPower"></div>
        <div class="header-right">
            <div class="header-user-con">
                <!-- 动态和私信 -->
                <!-- <div class="user-advice">
                    <el-badge :value="1" class="item" type="success">
                        <el-button size="small">动态</el-button>
                    </el-badge>
                    <el-badge :value="1" class="item" type="success">
                        <el-button size="small">私信</el-button>
                    </el-badge>
                </div> -->

                <!-- 全屏显示 -->
                <div class="btn-fullscreen" @click="handleFullScreen">
                    <!-- tooltip文字提示 -->
                    <el-tooltip effect="dark" :content="fullscreen ? `取消全屏` : `全屏`" placement="bottom">
                        <i class="el-icon-rank"></i>
                    </el-tooltip>
                </div>

                <!-- 用户头像 -->
                <div class="user-avator">
                    <img :src="user.avatar" />
                </div>
                <!-- 用户名下拉菜单 -->
                <el-dropdown class="user-name" trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        {{ user.username }}
                        <!-- <i class="el-icon-caret-bottom"></i> -->
                        <i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <a target="_blank" @click="PersonalCenter">
                            <el-dropdown-item>个人中心</el-dropdown-item>
                        </a>
                        <a target="_blank" @click="openDialog">
                            <el-dropdown-item>修改密码</el-dropdown-item>
                        </a>
                        <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>

        <!-- 个人中心 -->
        <el-dialog title="个人中心" :visible.sync="PersonalCenterFormVisible" >            
            <el-form :model="personalCenterForm" :rules="rules2" ref="personalCenterForm">
                <el-form-item label="头像" :label-width="formLabelWidth">
                    <el-upload
                        action=""
                        class="avatar-uploader"
                        :before-upload="onBeforeUploadImage"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess">
                        <!-- :before-upload="beforeAvatarUpload" -->
                        <img v-if="personalCenterForm.imageUrl" :src="personalCenterForm.imageUrl" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
                <el-form-item label="用户名" :label-width="formLabelWidth">
                    <el-input v-model="personalCenterForm.username" autocomplete="off" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item label="昵称" :label-width="formLabelWidth" prop="nick">
                    <el-input v-model="personalCenterForm.nick" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="自我介绍" :label-width="formLabelWidth">
                    <el-input v-model="personalCenterForm.introduction" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="PersonalCenterFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="toChangePersonalCenter()">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 修改密码的地方 -->
        <el-dialog title="修改密码" :visible.sync="dialogFormVisible" width="40%">
            <el-form :model="modifypasswordForm" :rules="rules" ref="modifypasswordForm">
                <el-form-item label="旧密码" :label-width="formLabelWidth" prop="notNull">
                    <el-input v-model="modifypasswordForm.oldpassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="新密码" :label-width="formLabelWidth" prop="newpassword">
                    <el-input v-model="modifypasswordForm.newpassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" :label-width="formLabelWidth" prop="checkpassword">
                    <el-input v-model="modifypasswordForm.checkpassword" type="password"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="toChangePwd()">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import CryptoJS from 'crypto-js/crypto-js'
import bus from '../common/bus';
import Validator from 'vue-validator';
export default {
    data() {
       var validatePass = (rule, value, callback) => {
            const reg=/^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]{1,50}$/;
            if (value === '') {
                callback(new Error('请输入密码'));
            } else {
                if (!reg.test(value)) {
                    callback(new Error('请输入密码包含字母和数字'));
                }
                callback();
            }
        };
        var validatePass2 = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'));
            } else if (value !== this.modifypasswordForm.newpassword) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        };
        var validatePass3 = (rule, value, callback) => {
            console.log(value)
            console.log(this.personalCenterForm.nick)
            if (value === '') {
                callback(new Error('昵称不能为空！'));
            }  else {
                callback();
            }
        };
        return {
            dialogFormVisible: false,
            PersonalCenterFormVisible : false,
            collapse: false,
            fullscreen: false,
            user: {
                userid: '',
                username: '',
                avatar: ''
            },
            personalCenterForm:{
                username:'',
                nick:'',
                introduction:'',
                imageUrl: ''
                
            },
            modifypasswordForm: {
                oldpassword: '',
                newpassword: '',
                checkpassword:''
            },
            rules: {
                newpassword: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' },
                    { validator: validatePass, trigger: 'blur' }
                ],
                checkpassword: [
                    { required: true, message: '请确认密码', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' },
                    { validator: validatePass2, trigger: 'blur', required: true }
                ],
                
            },
            rules2: {
                nick:[
                    { validator: validatePass3, trigger: 'blur', required: true }
                ]
            },
            formLabelWidth: '100px',
            imageUrl: '',
            pictureVisible:false
        };
    },
    computed: {},
    methods: {
        handleAvatarSuccess(res, file) {
            this.personalCenterForm.imageUrl = URL.createObjectURL(file.raw);
        },
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'image/jpeg';
            const isLt2M = file.size / 1024 / 1024 < 2;

            if (!isJPG) {
            this.$message.error('上传头像图片只能是 JPG 格式!');
            }
            if (!isLt2M) {
            this.$message.error('上传头像图片大小不能超过 2MB!');
            }
            return isJPG && isLt2M;
        },
        onBeforeUploadImage(file) {
            const isIMAGE = file.type === 'image/jpeg' || 'image/jpg' || 'image/png';
            const isLt1M = file.size / 1024 / 1024 < 10;
            if (!isIMAGE) {
                this.$message.error('上传文件只能是图片格式!');
            }
            if (!isLt1M) {
                this.$message.error('上传文件大小不能超过 10MB!');
            }
            let formdata = new FormData();
          formdata.append('image', file);
 
          this.$axios.post("/admin/upload/uploadImg",formdata
            ).then(res => {
                if (res.data.code == 200) {
                    this.$message.success(res.data.msg);
                    this.personalCenterForm.imageUrl= res.data.data;

                } else {
                    this.$message.error(res.data.msg);
                }
          })
        },

        // 注销方法
        handleCommand(command) {
            if (command == 'logout') {
            const _this = this;
            this.$axios
                .get('admin/logout', {
                    // headers: {
                    //     "Authorization":localStorage.getItem('token')
                    // }
                })
                .then((res) => {
                    _this.$message.success('退出成功');
                    _this.$store.commit('REMOVE_INFO');
                    _this.$router.push('/login');
                });
            }
        },
        // 侧边栏折叠
        collapseChage() {
            this.collapse = !this.collapse;
            // 提交侧边栏当前折叠的状态
            // 这个没搞懂
            bus.$emit('collapse', this.collapse);
        },
        // 全屏控制函数
        handleFullScreen() {
            let element = document.documentElement;
            if (this.fullscreen) {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitCancelFullScreen) {
                    document.webkitCancelFullScreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            } else {
                if (element.requestFullscreen) {
                    element.requestFullscreen();
                } else if (element.webkitRequestFullScreen) {
                    element.webkitRequestFullScreen();
                } else if (element.mozRequestFullScreen) {
                    element.mozRequestFullScreen();
                } else if (element.msRequestFullscreen) {
                    // IE11
                    element.msRequestFullscreen();
                }
            }
            this.fullscreen = !this.fullscreen;
        },
        openDialog() {
            this.dialogFormVisible = true;
        },
        PersonalCenter(){
            this.PersonalCenterFormVisible = true;
            this.personalCenterForm.username = this.$store.getters.getUser.username;
            this.personalCenterForm.nick = this.$store.getters.getUser.nick;
            this.personalCenterForm.introduction = this.$store.getters.getUser.introduction;
            this.personalCenterForm.imageUrl = this.$store.getters.getUser.avatar;
        },
        Encrypt(word) {
            const key = CryptoJS.enc.Utf8.parse('1234567812345678') // 十六位十六进制数作为密钥
            const iv = CryptoJS.enc.Utf8.parse("1234567812345678"); //十六位十六进制数作为密钥偏移量
            var srcs = CryptoJS.enc.Utf8.parse(word);
            var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
            return encrypted.toString();
        },
        //修改个人中心
        toChangePersonalCenter(){
            const _this = this;

            const params = {
                id: _this.user.userid,
                nick: _this.personalCenterForm.nick,
                introduction: _this.personalCenterForm.introduction,
                avatar: _this.personalCenterForm.imageUrl
            };
            this.$refs.personalCenterForm.validate((valid) => {
                if (valid) {
                    _this.$axios
                        .post('/admin/user/changePersonalCenter', params)
                        .then((res) => {
                            if (res.data.code == 200) {
                                this.$message.success(res.data.msg);
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        })
                        .catch((err) => {
                        });
                } else {
                    return false;
                }
            });
            this.PersonalCenterFormVisible = false;
        },
        //修改密码
        toChangePwd() {
            const _this = this;
            const id = this.user.userid;
            const username = _this.user.username;
            const oldpassword = this.Encrypt(_this.modifypasswordForm.oldpassword);
            const newpassword = this.Encrypt(_this.modifypasswordForm.newpassword);
            const params = {
                id: id,
                username: username,
                oldPassword: oldpassword,
                newPassword: newpassword
            };
            this.$refs.modifypasswordForm.validate((valid) => {
                if (valid) {
                    _this.$axios
                        .post('/admin/user/changePassword', params)
                        .then((res) => {
                            if (res.data.code == 200) {
                                this.$message.success(res.data.msg);
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        })
                        .catch((err) => {
                        });
                } else {
                    return false;
                }
            });
            this.dialogFormVisible = false;
        }
    },
    // 如果屏幕小，自动折叠左侧
    mounted() {
        if (document.body.clientWidth < 1500) {
            this.collapseChage();
        }
    },
    created() {
        if (this.$store.getters.getUser.username) {
            this.user.userid = this.$store.getters.getUser.id;
            this.user.username = this.$store.getters.getUser.username;
            this.user.avatar = this.$store.getters.getUser.avatar;
        }
    },
    advice2() {
        console.log(aaa);
        this.$message.error('尚未开发');
    }
};
</script>
<style>
.header {
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 70px;
    font-size: 22px;
    color: #fff;
}
.collapse-btn {
    float: left;
    padding: 0 21px;
    cursor: pointer;
    line-height: 70px;
}
.header .logo {
    float: left;
    width: 250px;
    line-height: 70px;
}
.header-right {
    float: right;
    padding-right: 50px;
}
.header-user-con {
    display: flex;
    height: 70px;
    align-items: center;
}
.btn-fullscreen {
    transform: rotate(45deg);
    margin-right: 5px;
    font-size: 24px;
}
.btn-bell,
.btn-fullscreen {
    position: relative;
    width: 30px;
    height: 30px;
    text-align: center;
    border-radius: 15px;
    cursor: pointer;
}
.btn-bell-badge {
    position: absolute;
    right: 0;
    top: -2px;
    width: 8px;
    height: 8px;
    border-radius: 4px;
    background: #f56c6c;
    color: #fff;
}
.btn-bell .el-icon-bell {
    color: #fff;
}
.user-name {
    margin-left: 10px;
}
.user-avator {
    margin-left: 20px;
}
.user-avator img {
    display: block;
    width: 40px;
    height: 40px;
    border-radius: 50%;
}
.el-dropdown-link {
    color: #fff;
    cursor: pointer;
}
.el-dropdown-menu__item {
    text-align: center;
}

.myPower {
    line-height: 70px;
    float: left;
    color: goldenrod;
}

.user-advice {
    padding: 20px;
    float: right;
    /* line-height: 70px; */
}

.item {
    /* margin-top: 10px; */
    margin-right: 20px;
}



.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 160px;
    height: 160px;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 60px;
    height: 100px;
    border-radius: 50%;
    line-height: 78px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
