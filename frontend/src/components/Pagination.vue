<template>
  <div class="pagination" v-if="total > pageSize">
    <button :disabled="page <= 1" @click="$emit('update:page', page - 1)">
      上一页
    </button>
    <span>第 {{ page }} 页 / 共 {{ pages }} 页</span>
    <button :disabled="page >= pages" @click="$emit('update:page', page + 1)">
      下一页
    </button>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "Pagination",
  props: {
    total: { type: Number, required: true },
    page: { type: Number, required: true },
    pageSize: { type: Number, required: true },
  },
  emits: ["update:page"],
  setup(props) {
    const pages = computed(() =>
      Math.max(1, Math.ceil(props.total / props.pageSize))
    );
    return { pages };
  },
});
</script>

<style scoped>
.pagination {
  display: flex;
  gap: 8px;
  align-items: center;
}
button:disabled {
  opacity: 0.5;
}
</style>
