<template>
  <div class="mobile-side-bar">
    <div class="header-inner">
      <!--移动端横幅Banner-->
      <div class="site-brand-wrapper">
        <div class="site-nav-toggle" @click="show = !show">
          <span class="iconfont">&#xe660;</span>
        </div>
        <div class="site-title">
          <span class="blog-pseudonym">{{ $t("homeNav.pseudonym") }}</span>
        </div>
      </div>
      <!--移动端菜单栏-->
      <div class="site-nav" v-show="show">
        <ul class="menu">
          <li class="menu-item-home">
            <a href="/"
              ><span class="iconfont">&#xe501;</span>{{ $t("homeNav.home") }}</a
            >
          </li>
          <li class="menu-item-tags">
            <a href="/tags"
              ><span class="iconfont">&#xe613;</span>{{ $t("homeNav.tags") }}</a
            >
          </li>
          <li class="menu-item-timeline">
            <a href="/timeline"
              ><span class="iconfont">&#xe7e3;</span
              >{{ $t("homeNav.timeline") }}</a
            >
          </li>
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
          <li class="menu-item-search">
            <span class="iconfont">&#xe505;</span>
            <input
              class="mobile-search"
              :placeholder="$t('homeNav.searchPlaceholder')"
              v-model="keyWord"
            />
            <div class="site-language">
              <iv-switch
                class="mobile-switch"
                size="large"
                @on-change="changeLanguage"
                true-color="forestgreen"
              >
                <span slot="open">{{ $t("language.chinese") }}</span>
                <span slot="close">{{ $t("language.english") }}</span>
              </iv-switch>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  name: "MobileSideBar",
  data() {
    return {
      show: false,
      keyWord: "",
    };
  },
  methods: {
    changeLanguage(flag) {
      if (flag === false) {
        this.$i18n.locale = "en";
        localStorage.setItem("language", "en");
      } else {
        this.$i18n.locale = "zh";
        localStorage.setItem("language", "zh");
      }
    },
  },
};
</script>

<style lang="stylus" type="text/stylus" rel="stylesheet/stylus" scoped>
.mobile-side-bar {
  background-color: $color-header-mobile;

  .header-inner {
    overflow: hidden;
    width: auto;
    margin: 0 auto 20px auto;
    padding-top: 10px;

    .site-brand-wrapper {
      padding: 0 10px;
      font: normal 700 1.4rem $body-font;
      text-align: center;
      line-height: 2.2rem;
      height: 2.2rem;
      color: white;

      .site-nav-toggle {
        float: left;
        padding: 0 5px;

        &:hover {
          cursor: pointer;
        }
      }

      .site-title {
        font-weight: 400;
      }
    }
  }

  .site-nav {
    border-top: 1px solid $color-border;
    color: white;

    ul li {
      margin: 5px 0 5px 0;
      padding: 5px 15px;
      list-style: none;
      line-height: 24;
      font: normal 400 16px $body-font;

      &:first-of-type {
        margin-top: 0;
        padding-top: 8px;
      }

      &>a {
        text-decoration: none;
        color: white;

        span {
          margin-right: 10px;
        }
      }

      &:hover {
        color: $color-on-hover;
        cursor: pointer;
        background-color: $color-gradually-gray-11;
      }
    }

    .menu-item-search {
      margin-bottom: 0;
      padding-bottom: 7px;

      span {
        position: relative;
        top: 2px;
        margin-right: 10px;
        font-size: 18px;
      }

      .mobile-search {
        margin: 0;
        outline: none;
        width: 65%;
        height: 24px;
        border: none;
        border-bottom: 1px solid $color-border;
        background-color: $color-page-background;

        &:hover {
          border-bottom: 1px solid $color-border-hover;
        }
      }

      .site-language {
        position: relative;
        float: right;
        margin: 1px 0;

        .mobile-switch span {
          position: absolute;
          top: 2px;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
