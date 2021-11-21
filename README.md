# heriec个人博客搭建

## 技术栈

- 前端：`Vue`+`elementUI`+`IViewUI`+`MavonEditor`
- 后端：`SpringBoot`+`MyBati-Plus`+`MySQL`+`Redis`+`Shiro`+`JWT`+其他工具

简单介绍一下3个文件夹的内容

>  blog-client是放前台博客展示页面的vue项目

![image-20211121163847740](https://gitee.com/heriec/drawing-bed/raw/master/img/202111211638919.png)

> blog-manage是博客的管理页面

![image-20211121164338497](https://gitee.com/heriec/drawing-bed/raw/master/img/202111211643669.png)

![image-20211121164300334](https://gitee.com/heriec/drawing-bed/raw/master/img/202111211643519.png)

![image-20211121171333668](https://gitee.com/heriec/drawing-bed/raw/master/img/202111211713833.png)

> blog-master是对前台和后台的所有代码

理论上不应该放在一起的一个项目里面的，但是吧项目经历不成熟，所以就这样先写完，后面可能还会加功能，比如前台的搜索，还有照片墙，但是七牛云的云空间还要买域名，可能这个就算了。

## 博客项目启动步骤

1. 克隆项目到本地
2. 启动本地redis和mysql
3. 安装前端前台client项目相关依赖，使用`npm install`命令
4. 安装前端博客管理manage项目相关依赖，使用`npm install`命令
5. 启动后端springBoot系统
6. 自行修改数据库和redis的配置
7. 在`blog-client`模块下执行命令`npm run serve`
8. 在`blog-manage`模块下执行命令`npm run serve`

## 前台

关于前台没什么好说的，vue我也不熟，大体样子也是找模板的，里面想加功能都直接去elementUI里直接找框架的

最重要的属于是ajax请求你要会写，这样基本接受请求能给前台传值回来

还有一个重要的就是对于接收到的文章转换为html展示在页面上的话我用了marked.js进行操作，但是我个人感觉不是很理想，有的代码块是数据库的语言他没有给我的代码高亮，我也不太清楚，也没心思搞了。上代码吧。

### Marked对markdown文本进行高亮

```javascript
highlightCode() {
      const srcToc = document.querySelector("#src-toc");
      const blocks = srcToc.querySelectorAll("pre code");
      blocks.forEach((block) => {
        this.$hljs.highlightBlock(block);
        // 去前后空格并添加行号
        block.innerHTML =
          "<ul><li>" +
          block.innerHTML
            .replace(/(^\s*)|(\s*$)/g, "")
            .replace(/\n/g, "\n</li><li>") +
          "\n</li></ul>";
      });
```

### html图片渲染

在转换为html后，这个图片没有给css渲染，导致图片不显示在容器中，我就直接对dom进行操作

在前端处理图片时没有对图片处理样式导致的图片的位置和尺寸都发生了变化，如何操作对应的数据呢

直接上js代码

```javascript
imgshow(){
        var imgs= document.getElementsByTagName("img")//获取全局图片信息
        imgs.forEach((img) =>{
          var style = document.createElement("style");//创建style标签
          style.type = "text/css";
          try{
          　　style.appendChild(document.createTextNode(
            "img{ background-size: contain|cover;width:100%;height:auto;} "));//动态变化图片的大小符合容器
          }catch(ex){
          　　style.styleSheet.cssText = "img{ background-size: contain|cover;width:100%;height:auto;} ";//针对IE
          }
          img.appendChild(style);
          console.log(img)
        })
    },
```

调用

```javascript
makeIds(document.querySelector("#src-toc"));
```

### 生成目录

```javascript
export function makeIds (srcToc) {
  var headings = srcToc.querySelectorAll('h1, h2, h3, h4, h5, h6')
  var headingMap = {}
  Array.prototype.forEach.call(headings, function (heading) {
    var id = heading.id ? heading.id : heading.textContent.trim().toLowerCase()
      .split(' ').join('-').replace(/[!@#$%^&*():]/ig, '').replace(/\//ig, '-')
    headingMap[id] = !isNaN(headingMap[id]) ? ++headingMap[id] : 0
    if (headingMap[id]) {
      heading.id = id + '-' + headingMap[id]
    } else {
      heading.id = id
    }
  })
}
```

在生成目录的时候，发现有时候会生成目录，有时候不生成，后来发现是前端在得到数据之前就操作了dom，所以导致没有渲染到dom，后来找了很多种方法，比如延迟渲染，本来以为很好用，但是在用学校校园网的时候发现，网一卡就不行了，后来换了一种方法this.$nextTick(function()这个完美适用

```javascript
watch: {
      article: function() {
        this.$nextTick(function(){
        this.wait();
        this.imgshow();
        })
      }
    },
```

前台这边没什么好说的了，其他都不是重点，毕竟我没系统学习vue，就想拿vue练手，全栈太累

## 后台

后台的话我对模板没有什么特别改动，只有加功能，其他的模块做的都很好

#### 验证码生成

这个js文件直接生成图片验证码，还可以判断验证码是否正确

```javascript
function GVerify (options) { // 创建一个图形验证码对象，接收options对象为参数
 this.options = { // 默认options参数值
  id: '', // 容器Id
  canvasId: 'verifyCanvas', // canvas的ID
  width: '80', // 默认canvas宽度
  height: '30', // 默认canvas高度
  type: 'number', // 图形验证码默认类型blend:数字字母混合类型、number:纯数字、letter:纯字母
  code: ''
 }

 if (Object.prototype.toString.call(options) === '[object Object]') { // 判断传入参数类型
  for (var i in options) { // 根据传入的参数，修改默认参数值
   this.options[i] = options[i]
  }
 } else {
  this.options.id = options
 }

 this.options.numArr = '0,1,2,3,4,5,6,7,8,9'.split(',')
 this.options.letterArr = getAllLetter()

 this._init()
 this.refresh()
}

GVerify.prototype = {
 /** 版本号**/
 version: '1.0.0',

 /** 初始化方法**/
 _init: function () {
  var con = document.getElementById(this.options.id)
  var canvas = document.createElement('canvas')
  // this.options.width = con.offsetWidth > 0 ? con.offsetWidth : '30'
  // this.options.height = con.offsetHeight > 0 ? con.offsetHeight : '30'
  this.options.width = '130'
  this.options.height = '35'
  canvas.id = this.options.canvasId
  canvas.width = this.options.width
  canvas.height = this.options.height
  canvas.style.cursor = 'pointer'
  canvas.innerHTML = '您的浏览器版本不支持canvas'
  con.appendChild(canvas)
  var parent = this
  canvas.onclick = function () {
   parent.refresh()
  }
 },

 /** 生成验证码**/
 refresh: function () {
  var canvas = document.getElementById(this.options.canvasId)
  if (canvas.getContext) {
   var ctx = canvas.getContext('2d')
  }
  ctx.textBaseline = 'middle'

  ctx.fillStyle = randomColor(180, 240)
  ctx.fillRect(0, 0, this.options.width, this.options.height)

  if (this.options.type === 'blend') { // 判断验证码类型
   var txtArr = this.options.numArr.concat(this.options.letterArr)
  } else if (this.options.type === 'number') {
   var txtArr = this.options.numArr
  } else {
   var txtArr = this.options.letterArr
  }

  for (var i = 1; i <= 4; i++) {
   var txt = txtArr[randomNum(0, txtArr.length)]
   this.options.code += txt
   ctx.font = randomNum(this.options.height / 2, this.options.height) + 'px SimHei' // 随机生成字体大小
   ctx.fillStyle = randomColor(50, 160) // 随机生成字体颜色
   ctx.shadowOffsetX = randomNum(-3, 3)
   ctx.shadowOffsetY = randomNum(-3, 3)
   ctx.shadowBlur = randomNum(-3, 3)
   ctx.shadowColor = 'rgba(0, 0, 0, 0.3)'
   var x = this.options.width / 5 * i
   var y = this.options.height / 2
   var deg = randomNum(-30, 30)
   /** 设置旋转角度和坐标原点**/
   ctx.translate(x, y)
   ctx.rotate(deg * Math.PI / 180)
   ctx.fillText(txt, 0, 0)
   /** 恢复旋转角度和坐标原点**/
   ctx.rotate(-deg * Math.PI / 180)
   ctx.translate(-x, -y)
  }
  /** 绘制干扰线**/
  for (var i = 0; i < 4; i++) {
   ctx.strokeStyle = randomColor(40, 180)
   ctx.beginPath()
   ctx.moveTo(randomNum(0, this.options.width), randomNum(0, this.options.height))
   ctx.lineTo(randomNum(0, this.options.width), randomNum(0, this.options.height))
   ctx.stroke()
  }
  /** 绘制干扰点**/
  for (var i = 0; i < this.options.width / 4; i++) {
   ctx.fillStyle = randomColor(0, 255)
   ctx.beginPath()
   ctx.arc(randomNum(0, this.options.width), randomNum(0, this.options.height), 1, 0, 2 * Math.PI)
   ctx.fill()
  }
 },

 /** 验证验证码**/
 validate: function (code) {
  var code = code.toLowerCase()
  var v_code = this.options.code.toLowerCase()
  if (code == v_code) {
   return true
  } else {
   return false
  }
 }
}
/** 生成字母数组**/
function getAllLetter () {
 var letterStr = 'a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z'
 return letterStr.split(',')
}
/** 生成一个随机数**/
function randomNum (min, max) {
 return Math.floor(Math.random() * (max - min) + min)
}
/** 生成一个随机色**/
function randomColor (min, max) {
 var r = randomNum(min, max)
 var g = randomNum(min, max)
 var b = randomNum(min, max)
 return 'rgb(' + r + ',' + g + ',' + b + ')'
}

export {
 GVerify
}
```

使用

```javascript
import { GVerify } from '../../api/verifyCode';
```

在vue中的组件

```vue
<el-form-item prop="captcha">
                <el-input class="verify_css" placeholder="请输入4位验证码" v-model="registerForm.captcha" @keyup.enter.native="submitForm('ruleForm')"></el-input>
                <!--关键 ↓-->
                <div class="v-container" id="v_container"></div>
                    
            </el-form-item>
```

在vue中mounted

```javascript
mounted () {
        this.verifyCode = new GVerify('v_container')
    },
```

Vue中判断输入（正则表达式）

```vue
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
```

js部分

```javascript
data: function () {
        var validatePass = (rule, value, callback) => {
            const reg=/^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]{1,50}$/;//正则表达式，必须包括数字和字母
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
```

组件中的prop添rules下面的第一层数组名字，rules下面的第一层数组要与输入框的v-mode对应，



非要说的话只有显示分页和搜索分页怎么区分，我就在js里面加了一个中间值来判断，但我不知道更好的解决方法是什么，我觉得加中间值这种方法不是很好，但是我又没什么更好的方法，代码不优美，就这样吧。会写请求就行

## 后端

这是重点，我主开发后端，但是吧，前端花了我不少时间，但总不至于说是浪费时间

### SpringBoot

现在后端大部分都是用户SpringBoot写的项目，所以我就用SpringBoot练手了，我前面写过Spring+jsp所以算有点基础

后端三层架构：dao+service+controller没有什么问题，application配置文件一配，直接就能run了

### MyBati-Plus

学完Mybatis后，Mybatis的sql语句我是写累了，xml文件确实写了不少，这次新学了MP，就用他来偷懒了，但是我没有用代码生成器，我是自己梳理一遍的，只能说MPyyds！！！

### MySQL

后端数据库首选，不介绍，好用，上数据库代码,这里是结构，自己搞数据

```sql
/*
SQLyog Ultimate v10.00 Beta1
MySQL - 5.5.62-log : Database - heriecblog
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`heriecblog` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `heriecblog`;

/*Table structure for table `article` */

DROP TABLE IF EXISTS `article`;

CREATE TABLE `article` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '文章主键',
  `title` varchar(80) NOT NULL COMMENT '文章标题',
  `summary` varchar(255) NOT NULL DEFAULT '' COMMENT '文章摘要',
  `content` longtext NOT NULL COMMENT '文章内容',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近编辑时间',
  `read_num` int(11) NOT NULL DEFAULT '0' COMMENT '阅读量',
  `like_num` int(11) NOT NULL DEFAULT '0' COMMENT '点赞量',
  `top` tinyint(1) DEFAULT '0' COMMENT '是否置顶：0 不置定 1 置定',
  `article_status` tinyint(1) DEFAULT '0' COMMENT '博客状态：0草稿 1发布',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章';

/*Table structure for table `article_category_relation` */

DROP TABLE IF EXISTS `article_category_relation`;

CREATE TABLE `article_category_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `article_id` int(11) NOT NULL COMMENT '文章id',
  `category_id` int(11) NOT NULL COMMENT '分类id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='分类——文章关系表';

/*Table structure for table `article_comment` */

DROP TABLE IF EXISTS `article_comment`;

CREATE TABLE `article_comment` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '评论主键',
  `content` varchar(300) COLLATE utf8_unicode_ci NOT NULL COMMENT '评论内容',
  `blog_id` int(11) NOT NULL,
  `nick_name` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '评论人昵称',
  `avatar` varchar(255) COLLATE utf8_unicode_ci DEFAULT 'https://www.datealive.top/wp-content/themes/kratos/static/images/avatar/104b754c6da34af2852493395c2bcf53!400x400.jpeg' COMMENT '评论人头像',
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '评论人邮箱',
  `reply_nick_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '回复人昵称',
  `pid` int(20) DEFAULT '0' COMMENT '父评论id，0为没有父评论',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `site_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '评论者站点',
  `is_check` tinyint(1) DEFAULT '0' COMMENT '是否审核，0为未审核',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='文章评论';

/*Table structure for table `article_tag_relation` */

DROP TABLE IF EXISTS `article_tag_relation`;

CREATE TABLE `article_tag_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `article_id` int(50) NOT NULL COMMENT '文章id',
  `tag_id` int(50) NOT NULL COMMENT '标签id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签——文章关系表';

/*Table structure for table `category` */

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类序号',
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '分类名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='分类';

/*Table structure for table `friends_link` */

DROP TABLE IF EXISTS `friends_link`;

CREATE TABLE `friends_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '友链名',
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'url',
  `info` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '站点描述',
  `avatar` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '头像路径',
  `is_check` int(11) DEFAULT '0' COMMENT '0 未审核， 1为审核',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='友链表';

/*Table structure for table `moment` */

DROP TABLE IF EXISTS `moment`;

CREATE TABLE `moment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `content` longtext COLLATE utf8_unicode_ci NOT NULL COMMENT '动态内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `like_num` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '默认公开，0不公开',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='动态表';

/*Table structure for table `site_setting` */

DROP TABLE IF EXISTS `site_setting`;

CREATE TABLE `site_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `type` int(11) NOT NULL COMMENT '设置类型，1基础设置 2页脚信息 3.资料卡',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '设置项名字',
  `value` longtext COLLATE utf8_unicode_ci COMMENT '设置项value',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='站点设置表';

/*Table structure for table `tag` */

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签序号',
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '标签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文章';

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `username` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '密码',
  `nick` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '头像路径',
  `introduction` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '介绍',
  `role` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员还是用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

/*Table structure for table `visit_count` */

DROP TABLE IF EXISTS `visit_count`;

CREATE TABLE `visit_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uv` int(255) unsigned NOT NULL,
  `pv` int(255) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```

### Redis

也是新学的技术栈，就用了，用来做阅览量和点赞量，感受到了非关系型数据库的好处了

阅览量就通过ip增加，再通过定时任务来存到数据库中

```java
public VisitCount staticUP(String route, HttpServletRequest request) {
    String realIp = ipUtils.getRealIp(request);

    // 先统计访客量
    String key="blog:UV";
    String key2="blog:PV";
    boolean b = redisUtils.hasKey(key);
    boolean b2=redisUtils.hasKey(key2);
    // 没访问过
    if(b==false||b2==false){
        VisitCount visitCount = visitCountMapper.selectById(1);
        // redis中添加值
        redisUtils.set("blog:UV",String.valueOf(visitCount.getUv()));
        redisUtils.set("blog:PV",String.valueOf(visitCount.getPv()));


    }

    redisUtils.sSet("blog:ip",realIp);
    redisUtils.sSet("blog:ip:route",realIp+":"+route);

    String s = (String) redisUtils.get("blog:UV");
    int uv = Integer.parseInt(s)+(int)redisUtils.sGetSetSize("blog:ip");
    s = (String) redisUtils.get("blog:PV");
    int pv = Integer.parseInt(s)+(int)redisUtils.sGetSetSize("blog:ip:route");
    return new VisitCount(1,uv,pv);


}
```

其他的可以自己到项目中看

### Shiro

安全这块不太懂，只能说代码用上了，不知道为什么，安全这块确实复杂点

最重要的还是配置文件吧，别人都给你写好了

```java
import com.heriec.blogmaster.shiro.JwtFilter;
import com.heriec.blogmaster.shiro.UserRealm;
import com.heriec.blogmaster.utils.JwtUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Autowired
    private JwtFilter jwtFilter;


    /**
     * 这个SessionManager为securityManager添加了bean
     * @param redisSessionDAO
     * @return
     */
    @Bean
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO){
        DefaultSessionManager sessionManager = new DefaultSessionManager();
//        sessionManager.setSessionDAO(redisSessionDAO);
        return sessionManager;

    }
    /**
     * DefaultWebSecurityManager
     * @param userRealm
     * @param sessionManager
     * @return
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);
        //关闭shiro自带的session，详情见文档
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    /**
     * 这里的ShiroFilterChainDefinition编写是为了下面的ShiroFilterFactoryBean所使用，其实屁都不懂，后期再学，哭死
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/**", "authc"); // 默认的过滤器，表示所有资源都需要通过过滤器
        filterMap.put("/**", "jwt"); // 默认的过滤器，表示所有资源都需要通过过滤器
//        filterMap.put("/admin/login","anon");
        chainDefinition.addPathDefinitions(filterMap);
        return chainDefinition;
    }


    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", jwtFilter);
        shiroFilter.setFilters(filters);
        Map<String, String> filterMap = shiroFilterChainDefinition.getFilterChainMap();
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilterFactoryBean");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }
//************************************以下看不懂*******************************************
    /**
     * 加下面两个方法才是注销，估计是用aop实现的注销logout
     */
    /**
     * 如果userPrefix和proxyTargetClass都为false会导致 aop和shiro权限注解不兼容 资源报错404
     * 因此两个属性至少需要其中一个属性为true才可以
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * 开启aop注解支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
```

### JWT

jwt配置自定义

```java
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Data
@Component
//绑定配置文件
@ConfigurationProperties(prefix = "blog.jwt")
public class JwtUtils {

    private String secret;
    private long expire;
    private String header;

    /**
     * 生成jwt token
     */
    public String generateToken(Integer userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 10000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId+"")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            log.debug("------------------------输入token错误", e);
            return null;
        }
    }

    /**
     * token是否过期
     * @return  true：过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}
```

### Swagger

直接上配置文件

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket createRestApiGroup1(Environment environment){
        // 添加 head 参数配置 start
        ParameterBuilder token = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        token.name("Authorization").description("token 信息").modelRef(new ModelRef("String"))
                .parameterType("header").required(false).build();
        pars.add(token.build());

         //设置要显示的Swagger环境
        Profiles profiles = Profiles.of("dev", "prod");
         //获取项目的环境
        boolean isDevAndTest = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .enable(isDevAndTest)
                .select().apis(RequestHandlerSelectors.basePackage("com.heriec.blogmaster"))
                .build().groupName("test1")
                // 注意一下 globalOperationParameters 这行配置
                .globalOperationParameters(pars);
    }
    // 配置网站相关信息
    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact=new Contact("heriec","hh.com","2821188630@qq.com");
        return new ApiInfoBuilder()
                .title("个人博客API文档")
                .description("为个人博客开发努力！")
                .termsOfServiceUrl("http://blog.heriec.com")
                .version("v1.0")
                .contact(contact)
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

}
```

## 其他	

### 文件上传

我用的是七牛云

工具类

```java
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 七牛云上传文件工具类
 */
public class QiniuUtils {

    // 设置需要操作的账号的AK和SK             （AK和SK均在七牛云中获得，以下会说明）
    private static final String ACCESS_KEY = "*******";
    private static final String SECRET_KEY = "*******";
    private static final String AGREEMENT = "http://";

    // 要上传的空间                       （刚刚新建空间的名称）
    private static final String bucketname = "heriecblog";

    // 密钥
    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    //新建空间时，七牛云分配出的域名 （自己可在万网购买域名解析后，绑定到加速域名）
    private static final String DOMAIN = "xxx.com";

    public static String getUpToken() {
        return auth.uploadToken(bucketname, null, 3600, new StringMap().put("insertOnly", 1));
    }

    //base64方式上传
    public static String put64image(byte[] base64, String key) throws Exception {
        String file64 = Base64.encodeToString(base64, 0);
        Integer len = base64.length;

        //华北空间使用 upload-z1.qiniu.com，华南空间使用 upload-z2.qiniu.com，北美空间使用 upload-na0.qiniu.com
        String url = "http://upload-z2.qiniu.com/putb64/" + len + "/key/" + UrlSafeBase64.encodeToString(key);

        RequestBody rb = RequestBody.create(null, file64);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Authorization", "UpToken " + getUpToken())
                .post(rb).build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = client.newCall(request).execute();
        System.out.println(response);
        //返回图片地址   https://q5jhgxz4q.bkt.clouddn.com/812bbd78-62d3-44bc-836c-9ee27ba4866a
        //用此地址可在网页中访问到上传的图片
        return AGREEMENT+DOMAIN + "/"+key;
    }

    /**
     * @param key 图片的文件名
     * @Explain 删除空间中的图片
     */
    public static void delete(String key) {
        BucketManager bucketManager = new BucketManager(auth);
        try {
            bucketManager.delete(bucketname, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

controller

```java
@RestController
@Api("文件上传控制器")
@Slf4j
@RequestMapping("/admin/upload")
public class FileUploadController {

    @RequiresAuthentication
    @PostMapping("/uploadImg")
    public Result insert(@RequestPart("image") MultipartFile file){

        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        try {
            byte[] bytes = file.getBytes();
            String imageName = UUID.randomUUID().toString();
            try {
                //使用base64方式上传到七牛云
                String url = QiniuUtils.put64image(bytes, imageName);
                log.info("上传地址为----：" + url);
                return Result.success("上传成功！",url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return Result.fail("上传图片异常");
        }
        return Result.fail("上传图片异常");
    }
}
```

### 密码加密

前端传密码要加密传到后端，后端再解密后盐值加密存到数据库

> 用AES加密密钥

记得秘钥要相同

前端

先vue导入

```
npm install crypto-js --save
```

再在导入使用

```javascript
import CryptoJS from 'crypto-js'
```

对应函数

```javascript
//前端加密
Encrypt(word) {
    const key = CryptoJS.enc.Utf8.parse('1234567812345678') // 十六位十六进制数作为密钥
    const iv = CryptoJS.enc.Utf8.parse("1234567812345678"); //十六位十六进制数作为密钥偏移量
    var srcs = CryptoJS.enc.Utf8.parse(word);
    var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return encrypted.toString();
},
    //前端解密   
Decrypt(word){  
    const key = CryptoJS.enc.Utf8.parse('1234567812345678') // 十六位十六进制数作为密钥
    const iv = CryptoJS.enc.Utf8.parse("1234567812345678"); //十六位十六进制数作为密钥偏移量
    var decrypt = CryptoJS.AES.decrypt(word, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
        return CryptoJS.enc.Utf8.stringify(decrypt).toString();
    }
```

后端

```java
package com.heriec.blogmaster.utils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

public class AESUtils {
    //密钥 (需要前端和后端保持一致)
    private static final String KEY = "1234567812345678";
    /**
     * aes解密
     * @param encrypt   内容
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String encrypt) {
        try {
            return aesDecrypt(encrypt, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * aes加密
     * @param content
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String content) {
        try {
            return aesEncrypt(content, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }


    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }


    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }


    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }
}
```

这里之前一直会报`Invalid AES key length: 17 bytes`错误，我一直找不到，后来就把Base64的包换了

换成了这个

```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.10</version>
</dependency>
```

也报了错误，但是我复制了别人一模一样的key值，又好了，不晓得为什么

这上面的加密属于CBC模式，其他的我没学了可以看这篇文章[使用crypto.js前端加密后端解密 - 血翼残飞 - 博客园 (cnblogs.com)](https://www.cnblogs.com/xueyicanfei/p/11926173.html)

### Nginx部署

这里我就不写了，毕竟我不怎么会