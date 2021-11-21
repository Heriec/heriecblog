<template>
  <content-box>
    <div class="article-timeline-container" slot="content">
      <!--文章时间线内容-->
      <div class="article-timeline-item">
        <time-line :itemList="momentTimelineList" :temp="temp"></time-line>
      </div>
      <!--页码-->
      <div class="article-timeline-page-container">
        <iv-page
          class-name="article-timeline-pagination"
          :total="pagination.total"
          :current="pagination.currentPage"
          :pageSize="pagination.pageSize"  
          @on-change="handleCurrentChange">
        </iv-page>
      </div>
    </div>
  </content-box>
</template>

<script type="text/ecmascript-6">
import ContentBox from 'components/content/ContentBox'
import HashMap from 'common/js/HashMap'
import TimeLine from 'components/content/MomentTimeLine'

export default {
  name: 'ArticleTimeLine',
  components: {
    'content-box': ContentBox,
    'time-line': TimeLine
  },
  data () {
    return {
      temp: undefined,
      pagination: {
        total: 0,
        currentPage: 1,
        pageSize: 100
      },
      momentTimelineList: []
    }
  },
  created () {
    this.temp = new HashMap()
    this.findPage()
  },
  methods: {
    checkPage () {
      return this.momentTimelineList.length > this.pagination.pageSize
    },
    handleCurrentChange (currentPage) {
      this.pagination.currentPage = currentPage
      this.findPage()
    },
    findPage () {
      const _this = this;
      // 发送Ajax请求，提交分页相关参数，在赋值之前先清空temp
      this.$axios.post('/moment/MomentTimeLine').then(res => {
        _this.momentTimelineList = res.data.data;
      })
    },
  }
}
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
  @import '~common/stylus/index.styl'
  .article-timeline-container
    .article-timeline-item
      overflow hidden
      padding 1rem 1rem 1rem 1rem
      margin 0 auto
      margin-bottom $footer-height-pageContent
      border-radius 6px
      background-color $color-content-background
    .article-timeline-page-container
      padding 20px 0 80px 0
      height 1.5rem
      line-height 1.5rem
      text-align center
      .article-timeline-pagination
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
