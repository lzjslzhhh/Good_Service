import request from "@/utils/request";
import type { NeedItem, ResponseItem, PageResult, StatItem } from "@/types";


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


export const getPublicNeedsList = async (params: {
  page: number;
  pageSize: number;
  serviceType?: string;
  keyword?: string;
}) => {
  const res: any = await request.get("/api/need/list", { params });
  return toPageResult<NeedItem>(res, params);
};


export const getMyPublishedNeeds = async (params: {
  page: number;
  pageSize: number;
  userId: number;
}) => {
  const res: any = await request.get("/api/need/my", { params });
  return toPageResult<NeedItem>(res, params);
};


export const getResponsesByNeedId = async (needId: number) => {
  const res: any = await request.get(`/api/need/${needId}/responses`);
  return res.data as ResponseItem[];
};


export const auditResponse = async (
  responseId: number,
  action: "accept" | "reject"
) => {
  return request
    .post(`/api/need/responses/${responseId}/audit`, { action })
    .then((r: any) => r.data);
};


export const publishNeed = (data: any) =>
  request.post("/api/need/publish", data).then((r: any) => r.data);

export const updateNeed = (data: any) =>
  request.put(`/api/need/${data.id}`, data).then((r: any) => r.data);

export const deleteNeed = (id: number, userId: number) =>
  request.delete(`/api/need/${id}?userId=${userId}`).then((r: any) => r.data);

export const getMyResponses = async (params?: {
  page?: number;
  pageSize?: number;
}) => {
  const res: any = await request.get("/api/need/my-responses", { params });
  return res.data as any[];
};


export const submitResponse = (data: any) =>
  request.post("/api/need/responses", data).then((r: any) => r.data);


export const updateResponse = (responseIdOrData: number | any, data?: any) => {

  if (typeof responseIdOrData === "number") {
    return request
      .put(`/api/need/responses/${responseIdOrData}`, data)
      .then((r: any) => r.data);
  }
  const payload = responseIdOrData || {};
  const id = payload.id || payload.responseId;
  if (!id) return Promise.reject(new Error("missing response id"));
  return request
    .put(`/api/need/responses/${id}`, payload)
    .then((r: any) => r.data);
};


export const deleteResponse = (responseId: number) =>
  request.delete(`/api/need/responses/${responseId}`).then((r: any) => r.data);


export const uploadFile = (file: File) => {
  const fd = new FormData();
  fd.append("file", file);
  return request
    .post("/api/need/upload", fd, {
      headers: { "Content-Type": "multipart/form-data" },
    })
    .then((r: any) => r.data);
};


export const getNeedStats = (params: {
  startMonth: string;
  endMonth: string;
  region?: string;
}) => {
  return request
    .get("/api/need/stats", { params })
    .then((r: any) => r.data as StatItem[]);
};


export const getNeedDetail = async (id: number, type?: "need" | "response") => {
  const params = type ? { type } : undefined;
  const res: any = await request.get(`/api/need/${id}/detail`, { params });
  return res.data as any;
};
