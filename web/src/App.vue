<script setup lang="ts">
  import {computed} from "vue";
  import List from "./components/List.vue";
  import Book from "./components/Book.vue";

  import {EventBus} from "./utils/EventEmitter";
  import {selectBook, loading} from "./config";
  import VConsole from "vconsole";
  const vConsole = new VConsole();
  const currentPart = computed(() => {
    return selectBook.value ? Book : List;
  });

  window.AndroidResult = (eventName: string, result: any) => {
    EventBus.emit(eventName, result);
    console.log("🚀 ~ App.vue ~ result:", eventName, result);
  };
</script>

<template>
  <component :is="currentPart"></component>
  <div class="loading" v-show="loading"><i class="loading-icon"></i></div>
</template>

<style lang="scss" scoped>
  .loading {
    position: fixed;
    z-index: 9;
    top: 0;
    left: 0;
    height: 100%;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    > .loading-icon {
      height: 48px;
      width: 48px;
    }
  }
</style>
