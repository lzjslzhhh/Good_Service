import { defineStore } from "pinia";
import { ref } from "vue";

const KEY = "gs_responses";

function read() {
  try {
    const raw = localStorage.getItem(KEY);
    return raw ? JSON.parse(raw) : [];
  } catch (e) {
    return [];
  }
}

function write(v: any[]) {
  localStorage.setItem(KEY, JSON.stringify(v));
}

export const useResponsesStore = defineStore("responses", () => {
  const items = ref<any[]>(read());

  function save() {
    write(items.value);
  }

  function add(payload: any) {
    const item = {
      id: "r-" + Date.now(),
      needId: payload.needId,
      responderId: payload.responderId || null,
      content: payload.content,
      status: payload.status || "pending",
      createdAt: new Date().toISOString(),
    };
    items.value.unshift(item);
    save();
    return item;
  }

  function update(id: string, payload: any) {
    const idx = items.value.findIndex((i) => i.id === id);
    if (idx === -1) return null;
    items.value[idx] = { ...items.value[idx], ...payload };
    save();
    return items.value[idx];
  }

  function remove(id: string) {
    const idx = items.value.findIndex((i) => i.id === id);
    if (idx === -1) return false;
    items.value.splice(idx, 1);
    save();
    return true;
  }

  function query(page = 1, pageSize = 10, filter: any = {}) {
    let list = items.value.slice();
    if (filter.needId) list = list.filter((x) => x.needId === filter.needId);
    if (filter.responderId)
      list = list.filter((x) => x.responderId === filter.responderId);
    const total = list.length;
    const start = (page - 1) * pageSize;
    const data = list.slice(start, start + pageSize);
    return { total, data };
  }

  return { items, add, update, remove, query };
});
