import request from "@/utils/request";
import type { StatItem } from "@/types";

export const getNeedStats = (params: {
  startMonth: string;
  endMonth: string;
  region?: string;
}) => {
  return request
    .get("/needs/stats", { params })
    .then((r: any) => r.data as StatItem[]);
};

// 管理员月度统计接口位于 `frontend/src/api/admin.ts` 的 `getMonthlyStats`
