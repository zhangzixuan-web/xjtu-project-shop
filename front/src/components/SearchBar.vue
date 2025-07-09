<template>
  <div ref="searchWrapper" class="search-container">
    <transition name="fade">
      <div v-if="isExpanded" class="search-bar-expanded">
        <el-input
            v-model="searchText"
            placeholder="搜索商品"
            class="search-input"
            size="medium"
            @keyup.enter.native="onSearch"
        />
        <i class="el-icon-close close-icon" @click="collapseSearch"></i>
      </div>
    </transition>

    <transition name="scale">
      <div v-if="!isExpanded" class="search-ball" @click.stop="expandSearch">
        <i class="el-icon-search"></i>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: "SearchBar",
  data() {
    return {
      isExpanded: false,
      searchText: ""
    };
  },
  mounted() {
    document.addEventListener("click", this.handleClickOutside);
  },
  beforeDestroy() {
    document.removeEventListener("click", this.handleClickOutside);
  },
  methods: {
    expandSearch() {
      this.isExpanded = true;
      this.$nextTick(() => {
        const input = this.$el.querySelector("input");
        if (input && typeof input.focus === "function") {
          input.focus();
        }
      });
    },
    collapseSearch() {
      this.isExpanded = false;
      this.searchText = "";
    },
    onSearch() {
      const keyword = this.searchText.trim();
      if (keyword) {
        this.$router.push({
          path: "/front/search",
          query: { keyword }
        });
        this.collapseSearch();
      }
    },
    handleClickOutside(e) {
      // 点击搜索按钮时防止误触
      if (this.isExpanded && this.$refs.searchWrapper && !this.$refs.searchWrapper.contains(e.target)) {
        this.collapseSearch();
      }
    }
  }
};
</script>

<style scoped>
.search-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

.search-ball {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--box-shadow);
  transition: transform 0.2s;
}

.search-ball:hover {
  transform: scale(1.1);
}

.search-bar-expanded {
  display: flex;
  align-items: center;
  background-color: white;
  border-radius: 24px;
  padding: 5px 10px;
  box-shadow: var(--box-shadow);
  width: 260px;
}

.search-input {
  flex: 1;
}

.close-icon {
  color: #999;
  margin-left: 10px;
  cursor: pointer;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: transform 0.2s ease;
}
.scale-enter,
.scale-leave-to {
  transform: scale(0);
}
</style>
