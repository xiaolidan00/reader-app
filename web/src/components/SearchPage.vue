<template>
  <Drawer :show="isSearch" @hide="onHide">
    <div class="search-box">
      <div class="search">
        <input placeholder="搜索关键词" type="text" v-model="state.searchKey" @change="onSearch()" />
        <i class="search-icon" @click="onSearch()"></i>
      </div>
    </div>

    <div class="empty" v-show="state.searchKey && state.searchResult.length == 0">暂无搜索结果</div>
    <div class="search-list" ref="searchList">
      <div @click="onSearhItem(item)" v-for="(item, i) in state.searchResult" :key="i" v-html="item.content"></div>
    </div>
  </Drawer>
</template>

<script setup lang="ts">
  import {reactive, nextTick, ref} from "vue";
  import {PageNum, LineNum} from "../data";
  import {chapterList, setHighlight} from "../config.ts";
  import Drawer from "./Drawer.vue";
  import type {SearchItemType} from "../@types";
  const searchList = ref<HTMLDivElement>();
  defineProps({
    isSearch: Boolean
  });
  const emit = defineEmits(["update:isSearch", "item"]);

  type StateType = {
    searchResult: SearchItemType[];
    searchKey: string;
  };
  const state = reactive<StateType>({
    searchKey: "",
    searchResult: []
  });

  const onHide = () => {
    emit("update:isSearch", false);
  };
  let searchLen = 0;

  const onSearch = async () => {
    const list: SearchItemType[] = [];
    if (state.searchKey) {
      const s = state.searchKey + "";
      chapterList.value.forEach((it, c) => {
        const t = Math.ceil(it.title.length / LineNum);

        it.content.forEach((item, idx) => {
          const start = item.indexOf(s);

          if (start >= 0) {
            list.push({content: item, chapter: c, start, index: Math.floor((idx + t) / PageNum)});
          }
        });
      });
    }
    state.searchResult = list;
    await nextTick();
    if (state.searchKey) {
      searchLen = state.searchKey.length;
      const ch = searchList.value!.children;
      for (let i = 0; i < list.length; i++) {
        const textNode = ch[i].firstChild!;
        const item = list[i];
        setHighlight(item.start, textNode, searchLen);
      }
    }
  };
  const onSearhItem = (item: SearchItemType) => {
    emit("item", {item, searchLen, searchKey: state.searchKey});
    emit("update:isSearch", false);
  };
</script>

<style scoped lang="scss">
  .empty {
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .search-list {
    height: calc(100% - 50px);
    padding: 0px 10px 10px 10px;
    overflow: hidden auto;

    > div {
      height: 40px;
      display: flex;
      align-items: center;
      &:not(:last-child) {
        border-bottom: solid 1px var(--border);
      }
    }
  }
</style>
