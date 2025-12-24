import { defineStore } from "pinia";
import { ref } from "vue";

const KEY = "gs_needs";

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

export const useNeedsStore = defineStore("needs", () => {
  const items = ref<any[]>(read());

  function save() {
    write(items.value);
  }

  function add(payload: any) {
    const item = {
      id: "n-" + Date.now(),
      title: payload.title,
      description: payload.description,
      region: payload.region || "",
      ownerId: payload.ownerId || null,
      status: "open",
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
    if (filter.region) list = list.filter((x) => x.region === filter.region);
    if (filter.ownerId) list = list.filter((x) => x.ownerId === filter.ownerId);
    const total = list.length;
    const start = (page - 1) * pageSize;
    const data = list.slice(start, start + pageSize);
    return { total, data };
  }

  return { items, add, update, remove, query };
});
