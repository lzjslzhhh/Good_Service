export type ServiceCategory = 
  | '管道维修' 
  | '助老服务' 
  | '保洁服务' 
  | '就诊服务' 
  | '营养餐服务' 
  | '定期接送服务' 
  | '其他';

export type Role = 'user' | 'admin';
// 需求状态
export type NeedStatus = 'Pending' | 'Responded' | 'Completed'; // 待响应 | 已响应 | 已完成

// 响应状态
export type ResponseStatus = 'Submitted' | 'Accepted' | 'Rejected'; // 已提交 | 已接受 | 已拒绝

// 需求单结构 (对应“我需要”)
export interface NeedItem {
  id: number;
  title: string;
  category: ServiceCategory;
  description: string;
  mediaFiles?: string[]; // 模拟上传的图片/视频文件名
  region: string;
  status: NeedStatus;
  userId: number; // 添加发布者ID字段
  publishTime: string;
  publisherId: number;
  responseCount: number; // 收到多少个响应
}

// 响应单结构 (对应“我服务”)
export interface ResponseItem {
  id: number;
  needId: number;
  responderId: number;
  responderName: string; // 响应者姓名
  responderIntro?: string; // 响应者简介
  serviceContent: string;
  mediaFiles?: string[]; // 响应者上传的证明文件
  price: number;
  status: ResponseStatus;
  createTime: string;
}
// 统计数据结构
export interface StatItem {
  month: string;
  region: string;
  publishCount: number; // 累计发布需求数
  successCount: number; // 累计响应成功数
}
export interface User {
  id: number;
  username: string;
  phone?: string;
  profile?: string;
  role: Role; // 核心字段
  token?: string;
}

export interface ServiceItem {
  id: number;
  title: string;
  description: string;
  region: string;
  status: 'pending' | 'active' | 'completed';
  createTime: string;
  userId: number;
}

export interface PageResult<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}