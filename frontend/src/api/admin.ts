import type { User, PageResult, StatItem } from "@/types";
import request from "@/utils/request";

// 1. 获取用户列表 (分页 + 搜索)
export const getUserList = (params: {
  page: number;
  pageSize: number;
  keyword?: string;
}) => {
  return request
    .get("/api/admin/user/list", { params })
    .then((res: any) => res.data as PageResult<User>);
};

// 2. 获取当前用户信息
export const getCurrentUser = () => {
  return request.get("/api/admin/me").then((res: any) => res.data as User);
};

// 3. 更新当前用户信息
export const updateCurrentUser = (payload: Record<string, any>) => {
  return request
    .put("/api/admin/me", payload)
    .then((res: any) => res.data as void);
};

// 4. 管理员修改指定用户
export const updateUser = (id: number, payload: Record<string, any>) => {
  return request
    .put(`/api/admin/users/${id}`, payload)
    .then((res: any) => res.data as void);
};

// 5. 获取统计数据 (按月趋势)
export const getMonthlyStats = (params: {
  start?: string;
  end?: string;
  region?: string;
}) => {
  return request
    .get("/api/admin/stats/monthly", { params })
    .then((res: any) => res.data as StatItem[]);
};
