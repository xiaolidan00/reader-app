<template>
  <Drawer :show="isMenu" @hide="onHide">
    <div class="nav-title" v-if="bookItem">{{ bookItem.name }}</div>
    <div class="nav-container" ref="navRef">
      <div
        :class="['nav-item', currentChapter == idx ? 'active' : '']"
        v-for="(item, idx) in chapterList"
        :key="idx"
        @click="onChapterItem(idx)"
      >
        {{ item.title }}
      </div>
    </div>
  </Drawer>
</template>

<script setup lang="ts">
  import Drawer from "./Drawer.vue";
  import {currentChapter, bookItem, chapterList} from "../config.ts";
  import {watch, nextTick, useTemplateRef} from "vue";
  const navRef = useTemplateRef("navRef");
  const props = defineProps({
    isMenu: Boolean
  });
  const emit = defineEmits(["update:isMenu", "item"]);
  const onHide = () => {
    emit("update:isMenu", false);
  };
  const onChapterItem = (idx: number) => {
    emit("item", idx);
    emit("update:isMenu", false);
  };
  watch(
    () => props.isMenu,
    async (v) => {
      await nextTick();
      if (v) {
        navRef.value?.scrollTo(0, (currentChapter.value - 1) * 40);
      }
    }
  );
</script>

<style scoped lang="scss">
  .nav-container {
    height: calc(100% - 60px);
    overflow-y: auto;
    overflow-x: hidden;
    border-top: solid 1px var(--border);
  }
  .nav-title {
    font-weight: bold;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
  }
  .nav-item {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    overflow: hidden;
    padding: 0 10px;
    text-overflow: ellipsis;
    white-space: nowrap;
    text-overflow: ellipsis;
    &:not(:last-child) {
      border-bottom: solid 1px var(--border);
    }
    &.active {
      color: dodgerblue;
    }
  }
</style>
