<template>
  <div class="drawer-bg" v-show="show">
    <div class="drawer-blank" @click="onHide()"></div>
    <div class="drawer-body">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { watch } from 'vue';
  const props = withDefaults(defineProps<{ show: boolean; onShow?: Function }>(), {
    show: false
  });
  const emit = defineEmits(['hide']);

  watch(
    () => props.show,
    (v) => {
      if (v && props.onShow) {
        props.onShow();
      }
    }
  );

  const onHide = () => {
    emit('hide', false);
  };
</script>
