import request from "@/utils/request";
import type { NeedItem, ResponseItem, PageResult, StatItem } from "@/types";

// helper: 归一化后端返回为 PageResult<T>
const toPageResult = <T>(
  resBody: any,
  params?: { page?: number; pageSize?: number }
): PageResult<T> => {
  const payload =
    resBody && resBody.data !== undefined ? resBody.data : resBody;
  // Case: backend already returns PageResult-like object
  if (payload && Array.isArray(payload.list)) {
    return {
      list: payload.list as T[],
      total:
        typeof payload.total === "number" ? payload.total : payload.list.length,
      page: typeof payload.page === "number" ? payload.page : params?.page || 1,
      pageSize:
        typeof payload.pageSize === "number"
          ? payload.pageSize
          : params?.pageSize || payload.list.length,
    };
  }

  // Case: payload itself is an array
  if (Array.isArray(payload)) {
    return {
      list: payload as T[],
      total: payload.length,
      page: params?.page || 1,
      pageSize: params?.pageSize || payload.length,
    };
  }

  // Fallback: empty
  return {
    list: [],
    total: 0,
    page: params?.page || 1,
    pageSize: params?.pageSize || 10,
  };
};

// ==========================================
// 1. 公共/市场 API (所有用户可见)
// ==========================================

// 获取“好服务”大厅列表 (支持筛选、分页)
export const getPublicNeedsList = async (params: {
  page: number;
  pageSize: number;
  category?: string;
  keyword?: string;
}) => {
  const res: any = await request.get("/needs/public", { params });
  return toPageResult<NeedItem>(res, params);
};

// ==========================================
// 2. “我需要” API (我是发布者)
// ==========================================

export const getMyPublishedNeeds = async (params: {
  page: number;
  pageSize: number;
}) => {
  const res: any = await request.get("/needs/my", { params });
  return toPageResult<NeedItem>(res, params);
};

// 获取某个需求的具体响应列表 (用于发布者审核)
export const getResponsesByNeedId = async (needId: number) => {
  const res: any = await request.get(`/needs/${needId}/responses`);
  return res.data as ResponseItem[];
};

// 发布者审核响应 (接受/拒绝)
export const auditResponse = async (
  responseId: number,
  action: "accept" | "reject"
) => {
  return request
    .post(`/needs/responses/${responseId}/audit`, { action })
    .then((r: any) => r.data);
};

// 发布/修改需求
export const publishNeed = (data: any) =>
  request.post("/needs", data).then((r: any) => r.data);

// ==========================================
// 3. “我服务” API (我是响应者)
// ==========================================

export const getMyResponses = async (params?: {
  page?: number;
  pageSize?: number;
}) => {
  const res: any = await request.get("/needs/my-responses", { params });
  return res.data as any[];
};

// 提交新的响应
export const submitResponse = (data: any) =>
  request.post("/needs/responses", data).then((r: any) => r.data);

// 修改我的响应 (仅限未接受状态)
export const updateResponse = (responseIdOrData: number | any, data?: any) => {
  // 支持两种用法：updateResponse(id, data) 或 updateResponse({ id, ...fields })
  if (typeof responseIdOrData === "number") {
    return request
      .put(`/needs/responses/${responseIdOrData}`, data)
      .then((r: any) => r.data);
  }
  const payload = responseIdOrData || {};
  const id = payload.id || payload.responseId;
  if (!id) return Promise.reject(new Error("missing response id"));
  return request
    .put(`/needs/responses/${id}`, payload)
    .then((r: any) => r.data);
};

// 撤销/删除我的响应
export const deleteResponse = (responseId: number) =>
  request.delete(`/needs/responses/${responseId}`).then((r: any) => r.data);

// ==========================================
// 4. 通用/系统 API
// ==========================================

// 文件上传
export const uploadFile = (file: File) => {
  const fd = new FormData();
  fd.append("file", file);
  return request
    .post("/needs/upload", fd, {
      headers: { "Content-Type": "multipart/form-data" },
    })
    .then((r: any) => r.data);
};

// ==========================================
// 5. 统计分析（需求侧）
// ==========================================

export const getNeedStats = (params: {
  startMonth: string;
  endMonth: string;
  region?: string;
}) => {
  return request
    .get("/needs/stats", { params })
    .then((r: any) => r.data as StatItem[]);
};

// 兼容：管理员统计（如果需要）
// （管理员统计请使用 `frontend/src/api/admin.ts` 中的接口）

// 获取单条需求或响应详情
export const getNeedDetail = async (id: number, type?: "need" | "response") => {
  const params = type ? { type } : undefined;
  const res: any = await request.get(`/needs/${id}/detail`, { params });
  return res.data as any;
};
