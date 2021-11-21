<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">Heriec博客后台管理系统</div>
            <el-form :model="loginForm" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" placeholder="username">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="password" v-model="loginForm.password" @keyup.enter.native="login">
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="login">登录</el-button>
                </div>
                <div class="login-btn">
                <el-button type="primary" @click="register">注册</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
import CryptoJS from 'crypto-js/crypto-js'
import bus from '../common/bus';

export default {
    data: function () {
        return {
            loginForm: {
                username: 'admin',
                password: '123'
            },
            rules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
            }
        };
    },
    methods: {
        Encrypt(word) {
            const key = CryptoJS.enc.Utf8.parse('1234567812345678') // 十六位十六进制数作为密钥
            const iv = CryptoJS.enc.Utf8.parse("1234567812345678"); //十六位十六进制数作为密钥偏移量
            var srcs = CryptoJS.enc.Utf8.parse(word);
            var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
            return encrypted.toString();
        },
        login() {
            const _this = this;
            var form = {
                username:'',
                password:''
            }
            // 这是element ui的验证表单格式的固定写法
            this.$refs.login.validate((valid) => {
                if (valid) {
                    //_this.loginForm.password=this.Encrypt(this.loginForm.password);
                    form.username=_this.loginForm.username;
                    form.password=this.Encrypt(this.loginForm.password);
                    // 发送请求
                    this.$axios.post('/admin/login', form).then((res) => {
                        // 请求后端,如果返回为400,会被axios自动拦截,那里有拦截方法
                        _this.$message.success('登录成功');
                        const token = res.headers['authorization'];
                        _this.$store.commit('SET_TOKEN', token);
                        _this.$store.commit('SET_USERINFO', res.data.data);
                        _this.$router.push('/');
                    });
                } else {
                    // this.$message.error('请输入账号和密码');
                    // console.log('error submit!!');
                    // return false;
                    // element ui的消息提示写法
                    this.$message({
                        showClose: true,
                        message: '请输入用户名和密码',
                        type: 'error'
                    });
                    return false;
                }
            });
        },
        register(){
            const _this = this;
            //this.$router.replace('/login','/register')
            this.$router.push({ path: `/register` });
        }
    }
};
</script>

<style scoped>
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background-image: url(../../assets/img/b-login.jpg);
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 22px;
    color: rgb(31, 27, 27);
    border-bottom: 1px solid rgb(10, 245, 30);
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: rgba(255, 255, 255, 0.5);
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
</style>