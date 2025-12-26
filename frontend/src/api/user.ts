import request from "@/utils/request";
import type { User } from "@/types";

export const login = (payload: { username: string; password: string }) => {
  return request.post("/api/auth/login", payload).then((r: any) => {
    // 处理字段映射
    const userData = r.data;
    return {
      id: userData.id,
      username: userData.username,
      phone: userData.phone,
      profile: userData.profile,
      role: userData.userType === 1 ? 'admin' : 'user', // 映射userType到role
      token: userData.token
    } as User;
  });
};

export const register = (payload: {
  username: string;
  profile?: string;
  password: string;
}) => {
  return request.post("/api/auth/register", payload).then((r: any) => r.data);
};


export const getCurrentUser = () => {
  return request.get("/api/auth/profile").then((r: any) => {
    const userData = r.data;
    return {
      id: userData.id,
      username: userData.username,
      phone: userData.phone,
      intro: userData.profile, // 后端profile字段对应前端intro
      role: userData.userType === 1 ? 'admin' : 'user',
      token: userData.token
    } as User;
  });
};


export const updateProfile = (payload: {
  phone?: string;
  profile?: string;
  password?: string;
}) => {

  const requestData = {
    phone: payload.phone,
    profile: payload.profile, 
    password: payload.password
  };
  
  return request.put("/api/auth/profile", requestData).then((r: any) => r.data);
};