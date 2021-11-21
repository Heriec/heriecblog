<template>
  <div class="desk-side-bar">
    <!--侧边栏主要内容-->
    <div class="side-bar-sticky">
      <div class="author-header">
        <div class="author-avatar">
          <img src="~assets/img/blogImage.jpg" />
        </div>
        <div class="author-pseudonym">{{ $t("homeNav.pseudonym") }}</div>
        <div class="author-motto">{{ $t("homeNav.motto") }}</div>
      </div>
      <div class="blog-search">
        <iv-input
          search
          :placeholder="$t('homeNav.searchPlaceholder')"
          v-model="keyWord"
          clearable
          @on-enter="search"
        ></iv-input>
      </div>
      <div class="side-bar-nav-menu">
        <div class="menu-item-home">
          <a class="menu-item-choose" href="/"
            ><span class="iconfont">&#xe501;</span>{{ $t("homeNav.home") }}</a
          >
        </div>
        <div class="menu-item-gallery">
          <a class="menu-item-choose" href="#"
            ><span class="iconfont">&#xe891;</span
            >{{ $t("homeNav.gallery") }}</a
          >
        </div>
        <div class="menu-item-mix">
          <a class="menu-item-timeline" href="/timeline"
            ><span class="iconfont">&#xe7e3;</span
            >{{ $t("homeNav.timeline") }}</a
          >
          <span class="separator"></span>
          <a class="menu-item-tags" href="/tags"
            ><span class="iconfont">&#xe613;</span>{{ $t("homeNav.tags") }}</a
          >
          <br /><br/>
          <a class="menu-item-moments" href="/moment"
            ><span class="iconfont">
              <svg
                t="1637161406956"
                class="icon"
                viewBox="0 0 1024 1024"
                version="1.1"
                xmlns="http://www.w3.org/2000/svg"
                p-id="536"
                width="16"
                height="16"
              >
                <path
                  d="M884.96128 1014.67136 135.8848 1014.67136c-68.95104 0-124.84608-55.93088-124.84608-124.92288L11.03872 140.17024c0-68.99712 56.70912-130.8416 125.66016-130.8416l754.00192 0c68.95104 0 125.66528 56.67328 125.66528 125.66528l0 754.00704C1016.3712 957.99808 953.9072 1014.67136 884.96128 1014.67136zM953.53856 134.99904c0-34.49344-28.35456-62.83264-62.83264-62.83264L136.69888 72.1664c-34.47296 0-63.24224 33.5104-63.24224 68.00896l0 749.57312c0 34.49344 27.95008 62.464 62.42304 62.464l749.07648 0c34.47296 0 68.5824-28.70784 68.5824-63.2064L953.53856 134.99904zM765.04576 700.49792 262.36928 700.49792c-17.35168 0-31.42144-14.06464-31.42144-31.4112 0-17.35168 14.06976-31.41632 31.42144-31.41632l502.67136 0c17.34656 0 31.4112 14.06464 31.4112 31.41632C796.45184 686.43328 782.3872 700.49792 765.04576 700.49792zM765.04576 512.00512 262.36928 512.00512c-17.35168 0-31.42144-14.06976-31.42144-31.42144 0-17.35168 14.06976-31.42144 31.42144-31.42144l502.67136 0c17.34656 0 31.4112 14.06976 31.4112 31.42144C796.45184 497.93536 782.3872 512.00512 765.04576 512.00512zM765.04576 323.5072 262.36928 323.5072c-17.35168 0-31.42144-14.06976-31.42144-31.42144 0-17.35168 14.06976-31.41632 31.42144-31.41632l502.67136 0c17.34656 0 31.4112 14.06464 31.4112 31.41632C796.45184 309.43744 782.3872 323.5072 765.04576 323.5072z"
                  p-id="537"
                ></path>
              </svg> </span
            >{{ $t("homeNav.moment") }}</a
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  name: "DeskSideBar",
  data() {
    return {
      keyWord: "",
    };
  },
  mounted() {},
  methods: {
    search() {
      if (this.keyWord === "" || this.keyWord.length < 1) {
        this.$Message.warning({
          content: this.$t("warningMessage.nullForSearch"),
        });
      } else {
        this.$Message.success({
          content: this.$t("successMessage.successForSearch"),
        });
      }
    },
  },
};
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
.desk-side-bar {
  position: relative;
  width: 100%;

  .side-bar-sticky {
    padding: 24px 10px;
    text-align: center;

    .author-header {
      padding: 1rem;
      margin: 0.4rem;
      border-radius: 6px;
      background-color: $color-content-background;

      .author-avatar {
        display: block;
        text-align: center;

        img {
          display: inline-block;
          vertical-align: top;
          padding: 4px;
          width: 100%;
          max-width: 100px;
          border-radius: 50%;
          border: 1px solid $color-border;
        }
      }

      .author-pseudonym {
        margin: 0.8rem 0 0 0;
        line-height: 1.8rem;
        height: 1.8rem;
        white-space: nowrap;
        font-size: 1.3rem !important;
        font-weight: 400;
        color: $color-font-content;
      }

      .author-motto {
        margin: 0.5rem 0;
        font-weight: 300;
        color: $color-font-blog-desc;
      }
    }

    .blog-search {
      padding: 0.8rem 0;
      margin: 1rem 0.4rem;

      >>>.ivu-input { // 穿透进iViewUI组件更改样式
        border-color: $color-on-hover;
      }
    }

    .side-bar-nav-menu {
      padding: 0.5rem 0;
      margin: 3rem 0.8em 0.8rem 0.8rem;
      font-weight: normal;

      .menu-item-choose {
        display: inline-block;
        color: $color-font-content;
        text-decoration: none;

        &:hover {
          color: $color-on-hover;
          cursor: pointer;
        }
      }

      span {
        font-size: 1.05rem;
        margin: 0 5px;
      }

      .menu-item-mix {
        margin: 3rem 0 1rem 0;

        .menu-item-timeline {
          display: inline-block;
          color: $color-font-content;
          text-decoration: none;

          &:hover {
            color: $color-on-hover;
            cursor: pointer;
          }
        }

        .separator {
          margin: 0 6px 0 10px;
          border-right: 1px solid $color-gradually-gray-71;
        }

        .menu-item-tags {
          display: inline-block;
          color: $color-font-content;
          text-decoration: none;

          &:hover {
            color: $color-on-hover;
            cursor: pointer;
          }
        }
      }
    }
  }
}
</style>
