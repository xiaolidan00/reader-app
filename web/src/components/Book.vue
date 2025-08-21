<template>
  <div class="book-detail">
    <div class="nav-back">
      <i class="back-icon" @click="onBack()"></i>
      <span class="title">{{ state.title }}</span>
      {{ currentChapter + 1 }} /{{ chapterList.length }}
    </div>

    <div class="book-container" id="bookContainer" @mousedown.stop="onDown" @mouseup.stop="onPageAction">
      <div class="chapter-title" v-if="chapterList.length && currentIndex == 0 && chapterList[currentChapter]">
        {{ chapterList[currentChapter].title }}
      </div>
      <div class="book-content" id="contenTxt" v-html="state.showContent"></div>
    </div>
    <div class="book-bottom">
      <i class="book-icon" title="章节目录" @click="state.isMenu = true"></i>
      <i class="listen-icon" title="听书" @click="state.isListen = true"></i>
      <i class="search-icon" title="搜索关键词" @click="state.isSearch = true"></i>
      <i class="setting-icon" title="读书设置" @click="state.isSet = true"></i>
      <i class="save-icon" @click="onSaveTxt()" title="另存为带章节目录TXT"></i>
    </div>
  </div>
  <ChapterPage v-model:is-menu="state.isMenu" @item="onChapterItem"></ChapterPage>
  <ListenPage
    ref="listenRef"
    v-model:is-listen="state.isListen"
    :total="state.total"
    @pre="preChapter"
    @next="nextChapter"
    @nextPage="nextPage"
    @index="changeIndex"
  ></ListenPage>
  <SearchPage v-model:is-search="state.isSearch" @item="onSearchItem"></SearchPage>
  <SetPage v-model:is-set="state.isSet" @exportTxt="onSaveTxt"></SetPage>
</template>

<script setup lang="ts">
  import {ref, reactive, nextTick, onBeforeUnmount, onMounted} from "vue";
  import type {ChapterType, SearchItemType} from "../@types";
  import SearchPage from "./SearchPage.vue";
  import ChapterPage from "./ChapterPage.vue";
  import ListenPage from "./ListenPage.vue";
  import SetPage from "./SetPage.vue";
  import {PageNum, LineNum} from "../data";
  import {selectBook, loading, bookItem, chapterList, setHighlight, currentChapter, currentIndex} from "../config.ts";
  import AndroidController from "../controllers/AndroidController.ts";

  //<InstanceType<typeof ListenPage>>
  const listenRef = ref();
  loading.value = true;

  type StateType = {
    title: string;
    isMenu: boolean;
    isListen: boolean;
    total: number;
    showContent: string;
    detail: string[];
    isSearch: boolean;
    isSet: boolean;
  };
  const state = reactive<StateType>({
    title: bookItem.value!.name,
    isMenu: false,
    detail: [],
    showContent: "",
    total: 0,
    isListen: false,
    isSearch: false,
    isSet: false
  });
  const updateBook = () => {
    AndroidController.readedTxt(
      selectBook.value + "",
      currentChapter.value,
      currentIndex.value,
      chapterList.value.length
    );
  };

  const onSearchItem = async ({
    item,
    searchLen,
    searchKey
  }: {
    item: SearchItemType;
    searchLen: number;
    searchKey: string;
  }) => {
    currentIndex.value = item.index;
    currentChapter.value = item.chapter;
    onChapter(item.chapter, 2);

    await nextTick();
    const contenTxt = document.getElementById("contenTxt")!;
    const textNode = contenTxt.firstChild!;
    setHighlight(textNode.textContent!.indexOf(searchKey), textNode, searchLen);
  };
  const onBack = () => {
    updateBook();
    selectBook.value = "";
    bookItem.value = undefined;
    state.isMenu = false;
    state.isListen = false;
    state.isSearch = false;
  };

  const preChapter = () => {
    if (currentChapter.value - 1 >= 0) {
      onChapter(currentChapter.value - 1, 0);
    }
  };
  const nextPage = () => {
    if (currentIndex.value + 1 < state.total) {
      currentIndex.value++;
      changeIndex();
    } else if (currentChapter.value + 1 < chapterList.value.length) {
      onChapter(currentChapter.value + 1, 0);
    }
  };
  const prePage = () => {
    if (currentIndex.value - 1 >= 0) {
      currentIndex.value--;
      changeIndex();
    } else if (currentChapter.value - 1 >= 0) {
      onChapter(currentChapter.value - 1, 1);
    }
  };
  let isMove = 0;
  const onDown = (event: MouseEvent) => {
    isMove = event.pageX;
  };

  const onPageAction = (event: MouseEvent) => {
    const x = event.pageX;
    if (Math.abs(isMove - x) <= 10) {
      const w = window.innerWidth;
      if (x >= 0 && x <= w * 0.5) {
        prePage();
      } else {
        nextPage();
      }
      // else {
      //   state.isMenu = true;
      // }
    }
    isMove = 0;
  };
  const changeIndex = () => {
    getPage();

    listenRef.value!.onSpeak();
  };
  const nextChapter = () => {
    if (currentChapter.value < chapterList.value.length) {
      onChapter(currentChapter.value + 1, 0);
    }
  };
  let titleLine = 0;
  const getPage = () => {
    const a = currentIndex.value * PageNum - titleLine;
    const b = (currentIndex.value + 1) * PageNum - titleLine;
    state.showContent = state.detail.slice(a < 0 ? 0 : a, b).join("");
  };

  const onChapter = (i: number, type: 0 | 1 | 2) => {
    if (type !== 2) currentChapter.value = i;
    if (currentChapter.value > chapterList.value.length) {
      currentChapter.value = chapterList.value.length - 1;
    }
    state.isMenu = false;
    const t: ChapterType = chapterList.value[currentChapter.value];
    titleLine = Math.ceil(t.title.length / LineNum);

    state.detail = t.content;
    state.total = Math.ceil((t.content.length + titleLine) / PageNum);
    if (type === 1) {
      currentIndex.value = state.total - 1;
    } else if (type === 2) {
      if (currentIndex.value >= 0 && currentIndex.value < state.total) {
      } else {
        currentIndex.value = 0;
      }
    } else {
      currentIndex.value = 0;
    }
    changeIndex();
  };
  const onChapterItem = (idx: number) => {
    onChapter(idx, 0);
  };

  let isFirst = true;
  const onReadTxt = (_event: any, data: ChapterType[]) => {
    if (isFirst) {
      currentChapter.value = bookItem.value!.chapter;
      currentIndex.value = bookItem.value!.index;
      isFirst = false;
    }
    console.log("readTxt");
    if (data.length) {
      chapterList.value = data;
    } else {
      alert("章节解析失败");
      loading.value = false;
      return;
    }

    onChapter(currentChapter.value, 2);
    loading.value = false;
  };
  const onSaveTxt = (op?: {start: number; end: number}) => {
    const start = op?.start || 1;
    const end = op?.end || chapterList.value.length;
    const fileName = bookItem.value!.name + `（带章节目录）${op ? start + "-" + end : ""}.txt`;
    let txt = "";
    const t0 = chapterList.value[0];
    txt += t0.title + "\n";
    txt += t0.content.join("") + "\n";
    for (let i = start; i < end; i++) {
      const it = chapterList.value[i];
      let t = it.title;
      if (/\s*第\s*[0-9]+\s*章/.test(t)) {
        t = t.replace(/\s*第\s*[0-9]+\s*章/g, "");
      }
      txt += `第${i}章 ` + t + "\n";
      txt += it.content.join("") + "\n";
    }

    const file = new File([txt], fileName, {type: "text/plain"});
    const dom = document.createElement("a");
    dom.download = fileName;
    dom.href = window.URL.createObjectURL(file);
    document.body.appendChild(dom);
    dom.click();
  };
  updateBook();
  window.Android.on("readTxt", onReadTxt);

  const backList = () => {
    loading.value = false;
    console.log("TXT DELETE");
    selectBook.value = "";
    alert("TXT已删除，不可阅读！");
  };
  window.Android.on("backList", backList);
  const closeBook = () => {
    AndroidController.closedBook(
      selectBook.value + "",
      currentChapter.value,
      currentIndex.value,
      chapterList.value.length
    );
  };
  window.Android.on("closeBook", closeBook);
  const onKeyPress = (ev: KeyboardEvent) => {
    switch (ev.code) {
      case "ArrowDown":
      case "ArrowLeft":
        prePage();
        break;
      case "ArrowUp":
      case "ArrowRight":
        nextPage();
        break;
    }
  };
  onMounted(() => {
    AndroidController.readTxt(bookItem.value!.path);
    window.addEventListener("keyup", onKeyPress);
  });
  onBeforeUnmount(() => {
    window.Android.off("readTxt", onReadTxt);
    window.Android.off("closeBook", closeBook);
    window.Android.off("backList", backList);
    window.removeEventListener("keyup", onKeyPress);
  });
</script>

<style scoped lang="scss">
  .book-detail {
    height: 100%;
    display: flex;
    background-color: var(--bg);
    flex-direction: column;
  }

  .book-container {
    height: calc(100% - 90px);
    text-align: left;
    line-height: 1.5;
    font-size: var(--fontSize);
  }
  .book-content {
    height: 100%;

    padding: 10px;
    overflow: hidden;
    white-space: pre-wrap;
  }
  .book-bottom {
    border-top: solid 1px rgba(0, 0, 0, 0.1);
    height: 40px;
    display: flex;
    align-items: center;
    padding: 0 10px;
    i {
      cursor: pointer;
      height: 20px;
      width: 20px;
      &:not(:last-child) {
        margin-right: 20px;
      }
    }
  }

  .nav-back {
    border-bottom: solid 1px rgba(0, 0, 0, 0.1);
    height: 50px;
    padding: 0 10px;
    display: flex;
    align-items: center;
    font-size: 16px;
    .title {
      display: inline-block;
      width: calc(100% - 100px);
      overflow: hidden;

      white-space: nowrap;

      font-weight: bold;
      text-align: center;
    }
    .back-icon {
      height: 20px;
      width: 20px;
      cursor: pointer;
    }
  }
</style>
