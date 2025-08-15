<template>
  <Drawer :show="isListen" @hide="onHide">
    <div class="listen">
      <div class="title" v-if="chapterList.length && chapterList[currentChapter]">
        {{ chapterList[currentChapter].title }}
      </div>
      <div class="progress">
        <input type="range" v-model="currentIndex" @click="changeIndex()" :min="0" :max="total - 1" />
      </div>

      <div class="control">
        <span @click="preChapter()">
          <i class="pre-icon"></i>
        </span>
        <span @click="onPlay()">
          <i :class="[state.isPlay ? 'stop-icon' : 'play-icon']"></i>
        </span>
        <span @click="nextChapter()">
          <i class="next-icon"></i>
        </span>
      </div>
      <div class="speed">
        <span
          :class="[state.speed == item.value ? 'active' : '']"
          v-for="item in speeds"
          :key="item.name"
          @click="onSpeed(item.value)"
          >{{ item.name }}</span
        >
      </div>
      <div class="voice">
        <div
          :class="[state.voice == i ? 'active' : '']"
          @click="selectVoice(i)"
          v-for="(item, i) in state.voices"
          :key="i"
        >
          {{ item }}
        </div>
      </div>
    </div>
  </Drawer>
</template>

<script setup lang="ts">
  import Drawer from "./Drawer.vue";
  import {currentChapter, chapterList, currentIndex} from "../config.ts";
  import {reactive, onMounted, onBeforeUnmount, watch, nextTick} from "vue";
  const emit = defineEmits(["update:isListen", "index", "pre", "next", "nextPage"]);
  let voiceList: any = [];
  const speeds = [
    {name: "0.5X", value: 0.5},
    {name: "1.0X", value: 1},
    {name: "1.2X", value: 1.2},
    {name: "1.5X", value: 1.5},
    {name: "1.8X", value: 1.8},
    {name: "2.0X", value: 2}
  ];
  type StateType = {
    voice: number;
    voices: string[];
    isPlay: boolean;
    speed: number;
  };
  const state = reactive<StateType>({
    voice: Number(localStorage.getItem("voice")) || 0,
    voices: [],
    speed: Number(localStorage.getItem("speed")) || 1,
    isPlay: false
  });
  const props = withDefaults(
    defineProps<{
      isListen: boolean;
      total: number;
    }>(),
    {isListen: false, total: 0}
  );
  watch(
    () => props.isListen,
    (v: boolean) => {
      if (v) {
        if (voiceList.length == 0) {
          alert("暂无TTS语音播报员，请在安装后使用");
          state.isPlay = false;
          emit("update:isListen", false);
          return;
        }
      }
    },
    {immediate: true}
  );

  const onHide = () => {
    emit("update:isListen", false);
  };
  const changeIndex = () => {
    emit("index", currentIndex.value);
  };
  const preChapter = () => {
    emit("pre");
  };
  const nextChapter = () => {
    emit("next");
  };
  const stopPlay = () => {
    state.isPlay = false;
    speechSynthesis.pause();
  };
  const selectVoice = (i: number) => {
    state.voice = i;
    localStorage.setItem("voice", i + "");
    if (state.isPlay) onSpeak();
  };
  const onSpeed = (i: number) => {
    state.speed = i;
    localStorage.setItem("speed", i + "");
    if (state.isPlay) onSpeak();
  };
  const onPlay = () => {
    state.isPlay = !state.isPlay;
    onSpeak();
  };
  const voiceSet: {txt: string; utterance?: SpeechSynthesisUtterance} = {
    txt: ""
  };

  const onSpeak = async () => {
    await nextTick();

    if (state.isPlay) {
      const contenTxt = document.getElementById("bookContainer")!;
      const str = contenTxt.innerText;
      if (voiceSet.txt != str) {
        speechSynthesis.cancel();
        const t = new SpeechSynthesisUtterance(str.replace(/[\_\-\+=\*]+/g, ""));
        t.voice = voiceList[state.voice];
        t.rate = state.speed;
        t.volume = 100;
        speechSynthesis.speak(t);
        voiceSet.txt = str;
        voiceSet.utterance = t;
        t.onend = () => {
          emit("nextPage");
        };
      } else if (voiceSet.utterance) {
        speechSynthesis.resume();
      }
    } else {
      speechSynthesis.pause();
    }
  };
  onMounted(() => {
    navigator.mediaDevices.addEventListener("devicechange", stopPlay);
    setTimeout(() => {
      voiceList = speechSynthesis.getVoices() || [];

      state.voices = voiceList.map((it: any) => it.name);
    }, 1000);
  });
  onBeforeUnmount(() => {
    speechSynthesis.cancel();
    navigator.mediaDevices.removeEventListener("devicechange", stopPlay);
  });
  defineExpose({
    onSpeak
  });
</script>

<style scoped lang="scss">
  .listen {
    height: 100%;

    .voice {
      padding: 20px;
      line-height: 30px;
      max-height: 200px;
      overflow: hidden auto;

      > div {
        white-space: nowrap;
        &:not(:last-child) {
          border-bottom: solid 1px var(--border);
        }
        &.active {
          color: dodgerblue;
          font-weight: bold;
        }
      }
    }
    .speed {
      display: flex;
      padding: 20px;
      line-height: 24px;
      text-align: center;
      > span {
        flex: 1;

        display: inline-block;
        &.active {
          color: dodgerblue;
          font-weight: bold;
        }
      }
    }
    .title {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      height: 100px;
      line-height: 100px;
      text-align: center;
      font-weight: bold;
      font-size: 18px;
    }
    .progress {
      padding: 20px;
    }
    .listen {
      height: 90%;
      background-color: var(--bg);
    }

    .control {
      padding: 20px 100px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      > span {
        height: 50px;
        width: 50px;

        border-radius: 50%;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        background-color: gray;
        &:nth-child(2) {
          height: 80px;
          width: 80px;
        }
      }
      i {
        height: 30px;
        width: 30px;
      }
    }
  }
</style>
