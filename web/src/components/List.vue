<script setup lang="ts">
  import {reactive, onBeforeUnmount, computed, onMounted} from "vue";
  import {selectBook, dataList, bookItem, loading, listSearchKey} from "../config.ts";
  import {BookType} from "../@types";
  import {cloneDeep} from "lodash-es";
  import AndroidController from "../controllers/AndroidController.ts";
  import {EventBus} from "../utils/EventEmitter.ts";
  const detailSet: Array<{name: string; prop: keyof BookType; idx?: boolean}> = [
    {name: "文件路径", prop: "path"},
    {name: "共有章节", prop: "total"},
    {name: "当前章节", prop: "chapter", idx: true},
    {name: "共有字数", prop: "num"},
    {name: "文件大小", prop: "size"}
    // { name: '最近阅读', prop: 'readTime'  }
  ];
  let orginMap: {[n: string]: boolean} = {};
  type StateType = {
    isEdit: boolean;
    checkMap: {[n: string]: boolean};
    isAll: boolean;

    isDetail: boolean;
  };
  const state = reactive<StateType>({
    isEdit: false,
    checkMap: {},
    isAll: false,

    isDetail: false
  });
  const showDataList = computed(() => {
    const list = dataList.value;
    if (listSearchKey.value) {
      return list.filter((it) => it.name.indexOf(listSearchKey.value) >= 0);
    }
    return list;
  });
  const openTxt = () => {
    AndroidController.openTxt();
  };
  const onRightItem = (item: BookType) => {
    state.isDetail = true;
    bookItem.value = item;
  };
  const onReadTxt = (item: BookType) => {
    if (state.isEdit) {
      onCheckItem(item);
    } else {
      selectBook.value = item.id;
      bookItem.value = item;
    }
  };
  const onDelTxt = (type: "record" | "file") => {
    const ids: string[] = [];
    for (const k in state.checkMap) {
      if (state.checkMap[k]) {
        ids.push(k);
      }
    }
    if (ids.length) {
      console.log("delTxt", ids);
      AndroidController.delTxt(ids, type);
    }

    state.checkMap = {};
    state.isEdit = false;
  };
  const onDelOneTxt = (type: "record" | "file") => {
    window.Android.delTxt([bookItem.value!.id], type);
    state.checkMap = {};
    state.isDetail = false;
    state.isEdit = false;
  };
  const onAll = () => {
    state.isAll = !state.isAll;
    if (state.isAll) {
      for (let k in state.checkMap) {
        state.checkMap[k] = true;
      }
    } else {
      state.checkMap = {};
    }
  };
  const onCheckItem = (item: BookType) => {
    state.checkMap[item.id] = !state.checkMap[item.id];
    console.log(state.checkMap);
    let count = 0;
    for (let k in state.checkMap) {
      if (state.checkMap[k]) count++;
    }
    if (count === Object.keys(state.checkMap).length) {
      state.isAll = true;
    } else if (count === 0) {
      state.isAll = false;
    }
  };

  let isLock = false;
  const refreshTxt = (data: BookType[]) => {
    if (isLock) return;
    isLock = true;
    console.log("refreshTxt", data);
    dataList.value = data;
    localStorage.setItem("BOOK_LIST", JSON.stringify(data));
    orginMap = {};
    data.forEach((a: BookType) => {
      orginMap[a.id] = false;
    });
    state.checkMap = cloneDeep(orginMap);
    loading.value = false;
    setTimeout(() => {
      isLock = false;
    }, 1000);
  };
  // EventBus.on("listTxt", refreshTxt);
  // window.Android.send("currentPage", "list");
  const onDragOver = (ev: DragEvent) => {
    ev.preventDefault();
  };

  const onBatch = () => {
    state.isEdit = !state.isEdit;
    state.checkMap = cloneDeep(orginMap);
  };
  const getTitle = (t: string) => {
    return t.replace(/[,，！!、]/g, "").substring(0, 25);
  };
  onMounted(() => {
    EventBus.on("listTxt", refreshTxt);
  });
  onBeforeUnmount(() => {
    EventBus.off("listTxt", refreshTxt);
  });
</script>

<template>
  <div class="search-box">
    <div class="search">
      <input placeholder="搜索关键词" type="text" v-model="listSearchKey" />
      <i class="close-icon" @click="listSearchKey = ''" v-show="listSearchKey"></i>
    </div>
    <!-- <i class="more-icon"></i> -->
  </div>
  <div class="tool-bar">
    <button @click="openTxt()">导入TXT</button>
    <i v-if="state.isEdit" :class="['check', state.isAll ? 'active' : '']" @click="onAll()"></i>
    <button :class="[state.isEdit ? 'active' : '']" @click="onBatch">批量操作</button>
    <button v-if="state.isEdit" @click="onDelTxt('record')">删除记录</button>
    <button v-if="state.isEdit" @click="onDelTxt('file')">删除文件</button>
  </div>
  <div class="book-list">
    <div class="book-item" v-for="item in showDataList" :key="item.name">
      <div class="book-top">
        <i
          v-if="state.isEdit"
          :class="['check', state.checkMap[item.id] ? 'active' : '']"
          @click.self="onCheckItem(item)"
        ></i>
      </div>

      <div class="book-cover" @click.self="onReadTxt(item)">
        {{ getTitle(item.name) }}
      </div>
      <div class="book-detail" @click="onRightItem(item)">
        <span>{{ item.chapter + 1 }}/{{ item.total }} </span>
        <i class="more-icon"></i>
      </div>
    </div>
  </div>
  <div class="dialog-bg" v-if="state.isDetail && bookItem">
    <div class="blank" @click="state.isDetail = false"></div>
    <div class="dialog-body">
      <div class="chapter-title">{{ bookItem.name }}</div>
      <div>
        <table class="detail-table">
          <tr v-for="(item, idx) in detailSet" :key="idx">
            <td>{{ item.name }}</td>
            <td>{{ item.idx ? (bookItem[item.prop] as number) + 1 : bookItem[item.prop] }}</td>
          </tr>
        </table>

        <div class="bottom-action">
          <span @click="onDelOneTxt('record')">删除记录</span>
          <span @click="onDelOneTxt('file')">删除文件</span>
        </div>
      </div>
    </div>
  </div>
</template>
