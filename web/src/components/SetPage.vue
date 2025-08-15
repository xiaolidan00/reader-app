<template>
  <Drawer :show="isSet" @hide="onHide" :onShow="onShow">
    <div class="chapter-set">
      <table>
        <tbody>
          <!-- <tr>
          <td>字体大小</td>
          <td><input type="range" v-model="state.fontSize" @change="onFontSize" /></td>
        </tr> -->
          <tr>
            <td>字体颜色</td>
            <td><input type="color" v-model="state.fontColor" @change="onFontColor" /></td>
          </tr>
          <tr>
            <td>背景颜色</td>
            <td><input type="color" v-model="state.bg" @change="onBg" /></td>
          </tr>
          <tr>
            <td>章节匹配</td>
            <td>
              <select v-model="state.regexType" @change="onRegex">
                <option :value="-1">自动识别</option>
                <option v-for="(item, idx) in chapterRegex" :key="idx" :value="idx">
                  {{ item.name }}
                </option>
                <option :value="-2">自定义</option>
              </select>
            </td>
          </tr>
          <tr>
            <td>正则表达式</td>
            <td>
              <input v-model="state.regex" :disabled="state.regexType != -2" @change="onRegex" type="text" />
            </td>
          </tr>

          <tr>
            <td>导出部分章节</td>
            <td>
              <input type="number" class="chapter-input" v-model="state.startChapter" />~
              <input type="number" class="chapter-input" v-model="state.endChapter" />

              <button class="export-chapter" @click="onExportChapter">导出</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </Drawer>
</template>

<script setup lang="ts">
  import Drawer from "./Drawer.vue";
  import {chapterRegex} from "../data";
  import {bookItem, selectBook} from "../config.ts";
  import {reactive} from "vue";
  import {onMounted} from "vue";
  import AndroidController from "../controllers/AndroidController.ts";

  const state = reactive({
    regexType: bookItem.value!.regexType ?? -1,
    fontSize: Number(localStorage.getItem("fontSize")) || 18,
    fontColor: localStorage.getItem("fontColor") || "#505050",
    bg: localStorage.getItem("bg") || "#faebd7",
    regex: bookItem.value!.regex || "",
    startChapter: 1,
    endChapter: 100
  });
  withDefaults(
    defineProps<{
      isSet: boolean;
    }>(),
    {isSet: false, total: 0}
  );
  const emit = defineEmits(["update:isSet", "exportTxt"]);
  const onHide = () => {
    emit("update:isSet", false);
  };
  //   const onFontSize = () => {
  //     localStorage.setItem('fontSize', state.fontSize + '');
  //     document.documentElement.style.setProperty('--fontSize', state.fontSize + 'px');
  //   };
  const onRegex = () => {
    bookItem.value!.regexType = state.regexType;
    if (state.regexType >= 0) {
      state.regex = chapterRegex[state.regexType].value;
    }

    AndroidController.chapterRegex(selectBook.value, state.regex, state.regexType);
  };
  const onFontColor = () => {
    localStorage.setItem("fontColor", state.fontColor);
    document.documentElement.style.setProperty("--font", state.fontColor);
  };
  const onBg = () => {
    localStorage.setItem("bg", state.bg);
    document.documentElement.style.setProperty("--bg", state.bg);
  };
  const onShow = () => {
    state.startChapter = 1;
    state.endChapter = bookItem.value!.total;
  };
  const onExportChapter = () => {
    emit("exportTxt", {start: state.startChapter, end: state.endChapter});
  };
  onMounted(() => {
    document.documentElement.style.setProperty("--font", state.fontColor);
    document.documentElement.style.setProperty("--bg", state.bg);
    // document.documentElement.style.setProperty('--fontSize', state.fontSize + 'px');
  });
</script>

<style lang="scss" scoped>
  .chapter-set {
    line-height: 40px;
    padding: 20px;
    select,
    input[type="text"],
    input[type="color"],
    .chapter-input {
      background-color: rgba(255, 255, 255, 0.3);
      border: none;
      border-radius: 4px;
      width: 100%;
      height: 40px;
      outline: none;
      padding: 0 10px;
    }
    .export-chapter {
      margin-left: 10px;
      padding: 0 20px;
      height: 40px;
      background-color: dodgerblue;
      border-radius: 4px;
      border: none;
      cursor: pointer;
      color: white;
      &:hover {
        filter: brightness(120%);
      }
    }
    .chapter-input {
      width: 100px !important;
    }
    input[type="text"]:disabled {
      background-color: rgba(0, 0, 0, 0.1);
    }
    table {
      width: 100%;

      tr:not(:last-child) td {
        border-bottom: solid 1px var(--border);
      }
    }
  }
</style>
