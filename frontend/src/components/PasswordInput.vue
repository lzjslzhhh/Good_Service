<template>
  <div>
    <input type="password" v-model="local" @input="onInput" />
    <div class="tips" v-if="message">{{ message }}</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from "vue";

export default defineComponent({
  name: "PasswordInput",
  props: { modelValue: { type: String, default: "" } },
  emits: ["update:modelValue"],
  setup(props, { emit }) {
    const local = ref(props.modelValue);
    const message = ref("");

    watch(
      () => props.modelValue,
      (v) => (local.value = v)
    );

    function onInput() {
      const p = local.value;
      if (!p || p.length < 6) message.value = "密码长度至少6位";
      else if ((p.match(/\d/g) || []).length < 2)
        message.value = "密码必须包含至少两个数字";
      else if (p === p.toLowerCase()) message.value = "不能全部为小写";
      else if (p === p.toUpperCase()) message.value = "不能全部为大写";
      else message.value = "";
      emit("update:modelValue", local.value);
    }

    return { local, onInput, message };
  },
});
</script>

<style scoped>
.tips {
  color: #c00;
  font-size: 12px;
  margin-top: 6px;
}
</style>
