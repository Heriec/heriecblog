<template>
  <content-box>
    <div class="tag-timeline-container" slot="content">
      <!--指定标题类文章时间线内容-->
      <div class="tag-timeline-item">
        <time-line :itemList="tagTimelineList.articleTimelineList" :temp="temp" @linkToArticle="handleToArticle">
          <div class="tag-name" slot="title">{{tagTimelineList.name}}</div>
        </time-line>
      </div>
      <!--页码
      <div class="tag-timeline-page-container">
        <iv-page
          class-name="tag-timeline-pagination"
          :total="pagination.total"
          :current="pagination.currentPage"
          :pageSize="pagination.pageSize"  >
          @on-change="handleCurrentChange"
        </iv-page>
      </div>-->
    </div>
  </content-box>
</template>

<script type="text/ecmascript-6">
import ContentBox from 'components/content/ContentBox'
import HashMap from 'common/js/HashMap'
import TimeLine from 'components/content/TimeLine'

export default {
  name: 'TagTimeLine',
  components: {
    'content-box': ContentBox,
    'time-line': TimeLine
  },
  data () {
    return {
      temp: undefined,
      // pagination: {
      //   total: 100,
      //   currentPage: 1,
      //   pageSize: 100
      // },
      tagTimelineList: {
        name: 'Java',
        articleTimelineList: []
      }
    }
  },
  created () {
    this.temp = new HashMap()
    this.findPage()
  },
  methods: {
    findPage () {
      // 发送Ajax请求，提交分页相关参数，在赋值之前先清空temp
      const _this = this;
      this.tagTimelineList.name=this.$route.query.name
      this.$axios.get('/tag',{
        params: {
          id: this.$route.query.id
          }
        }).then((res) => {
        _this.tagTimelineList.articleTimelineList = res.data.data;
      });
    },
    handleToArticle (id) {
      this.$router.push({ path: `/blog/article/${id}` });
    }
  }
}
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
  @import '~common/stylus/index.styl'
  .tag-timeline-container
    .tag-timeline-item
      overflow hidden
      padding 1rem 1rem 1rem 1rem
      margin 0 auto
      margin-bottom $footer-height-pageContent
      border-radius 6px
      background-color $color-content-background
      .tag-name
        margin 10px 0 10px 20px
        color $color-on-hover
        font-size 1.6rem
        font-weight bold
        text-align center
    .tag-timeline-page-container
      padding 20px 0 80px 0
      height 1.5rem
      line-height 1.5rem
      text-align center
      .tag-timeline-pagination
        >>>.ivu-page-item
          width 20px !important
          min-width 20px !important
          border none !important
          border-radius 0 !important
          background none !important
        >>>.ivu-page-item-active
          border none !important
          background-color $color-on-hover !important
        >>>a
          margin 0 !important
          color #515a6e
          font-family $body-font !important
        >>>.ivu-page-prev
          border none !important
          border-radius 0 !important
          background none !important
        >>>.ivu-page-next
          border none !important
          border-radius 0 !important
          background none !important
</style>
