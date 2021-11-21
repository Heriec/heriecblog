<template>
  <content-box>
    <div class="tag-container" slot="content">
      <div class="tag-item" v-for="tag in tagList" :key="tag.id">
        <div class="tag-group" @click="timelineByTag(tag.id,tag.name)">
          <span class="iconfont">&#xe655;</span>{{tag.name}}
        </div>
      </div>
    </div>
  </content-box>
</template>

<script type="text/ecmascript-6">
import ContentBox from 'components/content/ContentBox'
export default {
  name: 'TagList',
  components: {
    'content-box': ContentBox
  },
  data () {
    return {
      tagList: [
        
      ]
    }
  },
  created() {
    this.getTagList()
  },
  methods: {
      timelineByTag (id,name) {
      this.$router.push({
        path: '/tag',query:{id: id,name: name}
      })
      //this.$router.push({ path: `/tag/${id}`})
    },
    getTagList () {
      const _this = this;
      this.$axios.post('/tagList').then(res => {
        _this.tagList = res.data.data;
      })
    },
    }
}
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
  .tag-container
    padding 1.5rem 0 1.5rem 3rem
    border-radius 6px
    background-color $color-content-background
    .tag-item
      display inline-block
      margin 8px 0 8px 0
      width 20%
      .tag-group
        display inline-block
        opacity 0.75
        font-weight 400
        &:hover
          color $color-on-hover
          cursor pointer
        span
          margin-right 5px
          color $color-on-hover
  @media screen and (min-width: $size-xxl)
    .tag-item
      width 20% !important
  @media screen and (max-width: $size-xl)
    .tag-item
      width 25% !important
  @media screen and (max-width: $size-lg)
    .tag-item
      width 33% !important
  @media screen and (max-width: $size-md)
    .tag-item
      width 50% !important
  @media screen and (max-width: $size-sm)
    .tag-container
      margin-bottom 50px
</style>
