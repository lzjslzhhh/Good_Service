import request from "@/utils/request";
import type { StatItem } from "@/types";

export const getNeedStats = (params: {
  startMonth: string;
  endMonth: string;
  region?: string;
}) => {
  return request
    .get("/api/need/stats", { params })
    .then((r: any) => r.data as StatItem[]);
};

