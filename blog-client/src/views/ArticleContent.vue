<template>
  <content-box class="article-box">
    <div class="article-container" slot="content">
      <!--文章标题-->
      <h1 class="article-title">{{ article.title }}</h1>
      <!--文章信息-->
      <article-info class="article-info" :article="article"></article-info>
      <!--文章概述-->
      <p class="article-desc">{{ article.summary }}</p>
      <!--文章内容-->
      <article-page-content
        :article="article"
        id="src-toc"
        ref="src-toc"
      ></article-page-content>
      <!--打赏功能-->
      <!-- <article-reward></article-reward> -->
      <!--文章版权声明-->
      <article-copyright></article-copyright>
      <!--文章标签-->
      <tag-wall class="article-tag-wall" :tagList="article.tags"></tag-wall>
      <!--上一页/下一页-->

     <!--  <div class="article-context" >
        <div class="article-previous" @click="handleToArticle()" v-if="true">
          <span class="iconfont">&#xe581;</span>
          <span class="title-ellipsis">JAVA非对称加密算法RSA</span>
         根据article.id计算上一篇文章 -->
      <!--  </div>
        <div class="article-next" @click="handleToArticle()" v-if="true">
          <span class="title-ellipsis">Spring Cloud微服务权限系统搭建教程</span>
          根据article.id计算下一篇文章 
          <span class="iconfont">&#xe580;</span>
        </div>
      </div>-->
    </div>
    <!--添加评论系统-->
    <div class="article-common" slot="common">
      <valine-common></valine-common>
    </div>
  </content-box>
</template>

<script type="text/ecmascript-6">
import ContentBox from "components/content/ContentBox";
import ArticleInfo from "components/content/ArticleInfo";
import ArticlePageContent from "components/content/article/ArticlePageContent";
import ArticleReward from "components/content/article/ArticleReward";
import ArticleCopyright from "components/content/article/ArticleCopyright";
import TagWall from "components/content/TagWall";
import ValineCommon from "components/common/ValineCommon";

import tocbot from "tocbot";
import { makeIds } from "common/js/utils";
import Vue from "vue";
import marked from "marked";

export default {
  name: "ArticleContent",
  components: {
    "content-box": ContentBox,
    "article-info": ArticleInfo,
    "article-page-content": ArticlePageContent,
    "article-reward": ArticleReward,
    "article-copyright": ArticleCopyright,
    "tag-wall": TagWall,
    "valine-common": ValineCommon,
  },
  data() {
    return {
      tocbotControl: undefined,
      article: {},
    };
  },
  created() {
    // 发送ajax请求获取文章信息
    this.getBlog();
  },
  watch: {
      article: function() {
        this.$nextTick(function(){
        this.wait();
        this.imgshow();
        })
      }
    },
  beforeDestroy() {
    if (this.tocbotControl !== undefined) {
      this.tocbotControl.destroy();
    }
  },
  methods: {
    handleToArticle() {
      console.log("跳转到相应文章");
    },
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

    },
    imgshow(){
        var imgs= document.getElementsByTagName("img")
        imgs.forEach((img) =>{
          var style = document.createElement("style");
          style.type = "text/css";
          try{
          　　style.appendChild(document.createTextNode(
            "img{ background-size: contain|cover;width:100%;height:auto;} "));
          }catch(ex){
          　　style.styleSheet.cssText = "img{ background-size: contain|cover;width:100%;height:auto;} ";//针对IE
          }
          img.appendChild(style);
          console.log(img)
        })
    },

    // 根据当前currentid获取对应文章
    getBlog() {
      const _this = this;
      const id = this.$route.params.id;
      this.$axios.get(`/blog/article/${id}`).then((res) => {
        _this.article = res.data.data;
        _this.article.content = marked(_this.article.content);
      });
    },

    wait() {
      makeIds(document.querySelector("#src-toc"));
      console.log("等待结束");
      this.highlightCode();
      this.tocbotControl = tocbot.init({
        tocSelector: "#dest-toc", // ArticlePageToc的id,在ArticleSideBar.vue中设置
        contentSelector: "#src-toc", // ArticlePageContent的id,在ArticleContent.vue中设置
        headingSelector: "h1, h2, h3, h4",
        hasInnerContainers: true,
        linkClass: "toc-link",
        activeLinkClass: "is-active-link",
        listClass: "toc-list",
        isCollapsedClass: "is-collapsed",
        collapsibleClass: "is-collapsible",
        listItemClass: "toc-list-item",
        collapseDepth: 0,
        scrollSmooth: true,
        scrollSmoothDuration: 420,
        headingsOffset: 1,
        throttleTimeout: 50,
        positionFixedClass: "is-position-fixed",
        fixedSidebarOffset: "auto",
        includeHtml: true,
        onClick: false,
      });
      tocbot.refresh();
    },
  },
};
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
@import '~common/stylus/index.styl';

.article-container, .article-common {
  display: block;
  padding: 1rem 2rem 1rem 2rem;
  margin: 0 10px 10px 10px;
  border-radius: 6px;
  background-color: $color-content-background;
  font-weight: 400;
  color: $color-article-font;

  .article-title {
    margin: 1rem 0 0;
    font-size: 1.15rem;
    font-weight: 400;
  }

  .article-info {
    margin: 5px 0;
  }

  .article-desc {
    padding: 5px 10px 5px 10px;
    margin: 0.5rem 0 1rem;
    line-height: 1.6rem;
    text-indent: 2em;
    background-color: #F7F7FC;
    border-left: 3px solid $color-nav;
    text-align: justify;
  }

  .article-tag-wall {
    margin: 1.5rem 0;
  }

  .article-context {
    position: relative;
    margin: 1.5rem 0 2.3rem 0;
    border-top: 1px solid #f5f5f5;
    font-weight: 400;

    .article-previous, display block {
      position: absolute;
      left: 0;
      float: left;
      padding-top: 0.8rem;

      &:hover {
        cursor: pointer;
        color: $color-on-hover;
      }

      .iconfont {
        float: left;
        margin-right: 5px;
        font-size: 0.75rem;
      }

      .title-ellipsis {
        display: inline-block;
        width: 20rem; // 长度超过20rem显示...
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .article-next {
      display: inline-block;
      position: absolute;
      right: 0;
      float: right;
      padding-top: 0.8rem;
      text-align: right;

      &:hover {
        cursor: pointer;
        color: $color-on-hover;
      }

      .iconfont {
        float: right;
        margin-left: 5px;
        font-size: 0.75rem;
      }

      .title-ellipsis {
        display: inline-block;
        width: 20rem; // 长度超过20rem显示...
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

.article-common {
  margin-bottom: $footer-height-pageContent;
}

@media screen and (max-width: $size-xxl) {
  .title-ellipsis {
    width: 16rem !important;
  }
}

@media screen and (max-width: $size-xl) {
  .title-ellipsis {
    width: 13rem !important;
  }
}

@media screen and (max-width: $size-lg) {
  .title-ellipsis {
    width: 11rem !important;
  }
}

@media screen and (max-width: $size-md) {
  .title-ellipsis {
    width: 8rem !important;
  }
}

@media screen and (max-width: $size-sm) {
  .article-container {
    padding: 1rem 1rem 1rem 1rem !important;
  }

  .article-common {
    margin: 0 0 $footer-height-pageContent 0 !important;
    padding: 1rem 1rem 1rem 1rem !important;
  }

  >>>.content-container, .common-container {
    width: 100% !important;
  }
}
</style>
