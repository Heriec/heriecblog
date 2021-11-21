<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">用户注册</div>
            <el-form :model="registerForm" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="registerForm.username" placeholder="用户名">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="nick">
                    <el-input v-model="registerForm.nick" placeholder="昵称">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>

                <el-form-item prop="password">
                    <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter.native="login">
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>

                <el-form-item prop="newpassword">
                <el-input type="password" placeholder="确认密码" v-model="registerForm.newpassword" @keyup.enter.native="login">
                    <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                </el-input>
                </el-form-item>

                </el-form-item>
            <el-form-item prop="captcha">
                <el-input class="verify_css" placeholder="请输入4位验证码" v-model="registerForm.captcha" @keyup.enter.native="submitForm('ruleForm')"></el-input>
                <!--关键 ↓-->
                <div class="v-container" id="v_container"></div>
                    
            </el-form-item>
                <div class="login-btn">
                <el-button type="primary" @click="toRegister()">注册</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
import CryptoJS from 'crypto-js/crypto-js'
import { GVerify } from '../../api/verifyCode';
import Validator from 'vue-validator';



export default {
    
    data: function () {
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
            } else if (value !== this.registerForm.password) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        };
        var validatePass3 = (rule, value, callback) => {
            var _this = this;
            // 获取验证码
            var verifyFlag = _this.verifyCode.validate(value)
            console.log(verifyFlag)
            if (!verifyFlag) {
                callback(new Error('验证码错误!'));
            } else {
                callback();
            }
        };
        return {
            registerForm: {
                username: '',
                password: '',
                newpassword:'',
                nick:'',
                captcha:''
            },
            rules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                nick: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' },
                    { validator: validatePass, trigger: 'blur' }
                ],
                newpassword: [
                    { required: true, message: '请再输入你的密码', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' },
                    { validator: validatePass2, trigger: 'blur', required: true }
                ],
                captcha: [
                    { required: true, message: '验证码不能为空', trigger: 'blur' },
                    { min: 4, max: 4, message: '验证码为4位', trigger: 'blur' },
                    { validator: validatePass3, trigger: 'blur', required: true }
                ]
            },
            
        };
        
    },
    mounted () {
        this.verifyCode = new GVerify('v_container')
    },
    methods: {
        Encrypt(word) {
            const key = CryptoJS.enc.Utf8.parse('1234567812345678') // 十六位十六进制数作为密钥
            const iv = CryptoJS.enc.Utf8.parse("1234567812345678"); //十六位十六进制数作为密钥偏移量
            var srcs = CryptoJS.enc.Utf8.parse(word);
            var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
            return encrypted.toString();
        },
        toRegister(){
            const _this = this;
            var form = {
                username:'',
                password:'',
                nick:'',
            }
            // 这是element ui的验证表单格式的固定写法
            this.$refs.login.validate((valid) => {
                if (valid) {
                    form.username=_this.registerForm.username;
                    form.password=this.Encrypt(this.registerForm.password);
                    form.nick=_this.registerForm.nick;
                    // 发送请求
                    this.$axios.post('/admin/register', form).then((res) => {
                        _this.$message.success('注册成功！');
                        this.$router.push({ path: `/login` });
                    });
                } else {
                    this.$message({
                        showClose: true,
                        message: '注册失败，请找管理员',
                        type: 'error'
                    });
                    // this.$nextTick(()=>{
                    //     this.$refs['registerForm'].resetFields()
                    // })                 
                    return false;
                }
            });
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
.verify_css {
 width: 45%;
}
.v-container{
    position:absolute;
    right:3px;
    bottom:-12px;
     
}
</style>