<template>
  <div v-if="visible" class="modal-backdrop">
    <div class="modal">
      <h3>{{ title }}</h3>
      <div class="body"><slot /></div>
      <div class="actions">
        <button @click="$emit('cancel')">取消</button>
        <button @click="$emit('confirm')">确认</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  props: {
    visible: { type: Boolean, default: false },
    title: { type: String, default: "确认" },
  },
  emits: ["confirm", "cancel"],
});
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal {
  background: #fff;
  padding: 16px;
  border-radius: 6px;
  min-width: 320px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
}
</style>
