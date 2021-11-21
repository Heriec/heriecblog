<template>
  <div class="article-info">
    <div class="edit-time-group"><span class="iconfont">&#xe503;</span>{{article.publishTime}}</div>
    <span class="left-separator">|</span>
    <div class="update-time-group"><span class="iconfont">&#xe50a;</span>{{article.updateTime}}</div>
    <div class="read-num-group"><span class="iconfont">&#xe63f;</span>{{article.readNum}}</div>
    <span class="right-separator">|</span>
    <div class="like-num-group" @click="handleToLikeNum"><span class="iconfont">&#xe504;</span>{{article.likeNum}}</div>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  name: 'ArticleInfo',
  data(){
    return{
      status:1
    }
  },
  props: ['article'],
  methods: {
    ifLike(aid) {
      const _this = this;
      
    },
    handleToLikeNum () {
      const _this=this;
      this.$axios.get("/blog/ifLike?aid=" + this.article.id).then(res => {
        if(!res.data.data){
          _this.article.likeNum--;
          _this.status=1;
        }
        else {
          _this.article.likeNum++;
          _this.status=0;
        }
      })
      .catch((err) => {
      });
    }
  }
}
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
  .article-info
    overflow hidden
    font-weight 400
    opacity 0.6
    &>div
      &:hover
        color $color-on-hover
        cursor pointer
      .iconfont
        margin-right 5px
    .edit-time-group
      float left
      margin 5px 10px 5px 0
    .left-separator
      float left
      margin 5px 0
    .update-time-group
      float left
      margin 5px 0 5px 10px
    .like-num-group
      float right
      margin 5px 10px 5px 0
      &:hover
        color red
    .right-separator
      float right
      margin 5px 0
    .read-num-group
      float right
      margin 5px 0 5px 10px
  @media screen and (max-width: $size-md)
    .read-num-group, .right-separator, .like-num-group
      display none !important
</style>
