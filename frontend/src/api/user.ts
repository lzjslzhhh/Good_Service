import request from "@/utils/request";
import type { User } from "@/types";

export const login = (payload: { username: string; password: string }) => {
  return request.post("http://localhost:8080/auth/login", payload).then((r: any) => r.data as User);
};

export const register = (payload: {
  username: string;
  password: string;
  phone?: string;
}) => {
  return request.post("/auth/register", payload).then((r: any) => r.data);
};

export const getProfile = () =>
  request.get("/admin/me").then((r: any) => r.data as User);
