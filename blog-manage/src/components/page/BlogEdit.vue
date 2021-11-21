<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item> <i class="el-icon-lx-calendar"></i> 博客 </el-breadcrumb-item>
                <el-breadcrumb-item>编写文章</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <!-- 和article进行双向绑定 -->
            <el-form ref="form" :model="article" label-width="80px">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="文章标题">
                            <el-input v-model="article.title" style="width: 400px"></el-input>
                        </el-form-item>    
                        <el-form-item label="内容摘要">
                            <el-input v-model="article.summary" type="textarea" :rows="2" style="width: 400px"></el-input>
                        </el-form-item>
                        <el-form-item label="发布时间" style="width: 400px">
                            <el-col :span="11">
                                <el-date-picker
                                    type="datetime"
                                    value-format="yyyy-MM-dd HH:mm:ss"
                                    placeholder="选择日期"
                                    v-model="article.publishTime"
                                    style="width: 100%"
                                ></el-date-picker>
                            </el-col>
                        </el-form-item>
                        <el-form-item label="是否置顶">
                            <el-switch v-model="article.top" :active-value="1" :inactive-value="0" active-color="#13ce66"></el-switch>
                        </el-form-item>
                        <el-form-item label="文章标签">
                            <el-select v-model="article.tags" multiple placeholder="+New Tag" size="mini">
                                <el-option v-for="item in tagsList" :key="item.id" :label="item.name" :value="item.name"> </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="文章分类">
                            <el-select v-model="article.categories" multiple placeholder="+New category" size="mini">
                                <el-option v-for="item in categorysList" :key="item.id" :label="item.name" :value="item.name"> </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <mavon-editor
                    v-model="article.content"
                    ref="md"
                    @imgAdd="$imgAdd"
                    @change="change"
                    @imgDel="$imgDel"
                    style="min-height: 600px"
                    :ishljs="true"
                    codeStyle="atom-one-dark"
                />
                <el-form-item size="large">
                    <el-button class="editor-btn" type="primary" @click="release">发布文章</el-button>
                    <el-button class="editor-btn" type="primary" @click="saveblog">保存草稿</el-button>
                    <el-button class="editor-btn" type="info" @click="$router.back(-1)">取消</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';
var token = localStorage.getItem('token'); // 要保证取到
export default {
    name: 'markdown',
    data: function () {
        return {
            configs: {},
            // content: '',
            img_file:[],
            article: {
                id: null,
                title: '',
                summary: '',
                content: '',
                publishTime: '',
                updateTime: '',
                readNum: 0,
                likeNum: 0,
                pictureUrl: '',
                articleStatus: 0,
                top: 0,
                categories: [],
                tags: []
            },

            html: '',
            categorysList: [],
            tagsList: [],
            logo: ''
        };
    },
    components: {
        mavonEditor
    },
    watch: {
        $route: function () {
            if (this.$route.query.blog_id >= 0) {
                this.getBlog();
            }else{
                this.article={};
            }
            // 获取所有标签和分类信息
            this.getTagsList();
            this.getCategorysList();
        }
    },
    created: function() {
        this.html = this.article.content;
        // 跳转到这个路由页面的时候，判断是否有路径变量blog_id,如果有,查询该id的文章并展示在页面上
        // 这个方法的设计是为了从文章列表的编辑跳转时直接导入文章信息
        // console.log(this.$route.query.blog_id);
          if (this.$route.query.blog_id >= 0) {
             this.getBlog();
             // location.reload();
         }
        // 获取所有标签和分类信息
        this.getTagsList();
        this.getCategorysList();
    },
    methods: {
        getBlog() {
            const _this = this;
            const id = this.$route.query.blog_id;
            // 这里改成发两次请求比较好，第一次请求查询detail，第二次请求查询list
            this.$axios.get(`/admin/article/${id}`).then((res) => {
                if (res.data.code == 200){
                    _this.article = res.data.data;
                    this.fileList.pictureUrl=_this.article.pictureUrl;
                }
                else{
                    console.log('11111 :>> ', 11111);
                }
                //console.log(_this.article);
                // _this.content = res.data.data.blog_content;
                // _this.tagsList = res.data.data.tags;
                // _this.categorys = res.data.data.cateGory;
            });
        },
        getTagsList() {
            const _this = this;
            this.$axios.get('/admin/tag/list').then((res) => {
                _this.tagsList = res.data.data;
                // _this.categorys = res.data.data.cateGory;
            });
        },
        getCategorysList() {
            const _this = this;
            this.$axios.get('/admin/category/list').then((res) => {
                _this.categorysList = res.data.data;
                // _this.categorys = res.data.data.cateGory;
            });
        },
        change(value, render) {
            const _this = this;
            // // render 为 markdown 解析后的结果
            // //  this.blogDetail.blog_content=render;
            _this.html = render;

            console.log(render);
        },
        // markdown中的图片上传
        // 将图片上传到服务器，返回地址替换到md中
        $imgAdd(pos, $file) {
            // 第一步.将图片上传到服务器.
          this.img_file[pos] = $file;
          let formdata = new FormData();
          formdata.append('image', $file);
 
          this.$axios.post("/admin/upload/uploadImg",formdata
            ).then(res => {
                if (res.data.code == 200) {
                    this.$message.success(res.data.msg);
                    this.$refs.md.$img2Url(pos, res.data.data);

                } else {
                    this.$message.error(res.data.msg);
                }
          })
        },
         $imgDel(pos) {
            delete this.img_file[pos];
        },
        change(value, render) {
            const _this = this;
            // // render 为 markdown 解析后的结果
            // //  this.blogDetail.blog_content=render;
            _this.html = render;

            //console.log(render);
        },
        // 发布文章
        release(file) {
            const _this = this;
            // _this.blogDetail.tags = _this.tagsList;
            // _this.blogDetail.blog_content = _this.content;
            // _this.blogDetail.cateGory = _this.categorys;
            _this.article.articleStatus = 1;
            // _this.article.content = _this.html;
             console.log(_this.article.pictureUrl);
            this.$axios
                .post('/admin/article/saveBlog', _this.article)
                .then((res) => {
                    if (res.data.code == 200) {
                        this.$message.success(res.data.msg);
                        // _this.$router.push({ path: '/admin/articles' })  
                    } else {
                        this.$message.error(res.data.msg);
                    }
                })
                .catch((err) => {
                    this.$message.error('不要再试了哦，没有权限');
                });
            //this.UploadImage();
        },
        // 存为草稿
        saveblog() {
            const _this = this;
            // _this.blogDetail.tags = _this.tagsList;
            // _this.blogDetail.blog_content = _this.content;
            // _this.blogDetail.cateGory = _this.categorys;
            _this.article.articleStatus = 0;
            this.$axios
                .post('/admin/article/saveBlog', _this.article
                // , {
                //     headers: {
                //         Authorization: localStorage.getItem('token')
                //     }
                // }
                )
                .then((res) => {
                    if (res.data.code == 200) {
                        this.$message.success(res.data.msg);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                })
                .catch((err) => {
                    this.$message.error('不要再试了哦，没有权限');
                });
        },
    }
};
</script>
<style scoped>
.editor-btn {
    margin-top: 20px;
}
.el-tag + .el-tag {
    margin-left: 10px;
}
.button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
}
.input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
}
</style>