#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generate mock data for Good_service MySQL DB.
Usage:
  python generate_mock_data.py
Adjust configuration values below as needed.
"""

import random, pymysql, sys
from datetime import datetime, timedelta

# ---- 配置（如需修改请在此处修改） ----
DB = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "Lz2734678150",
    "db": "goodservice",
    "charset": "utf8mb4",
}
NUM_USERS = 50
TOTAL_NEEDS = 600
START_MONTH = "2025-01"   # inclusive YYYY-MM
END_MONTH = "2025-12"     # inclusive YYYY-MM
REGIONS = [
    ("朝阳区","北京市","北京市"),
    ("海淀区","北京市","北京市"),
    ("浦东新区","上海市","上海市"),
    ("黄浦区","上海市","上海市"),
    ("天河区","广州市","广东省"),
    ("南山区","深圳市","广东省")
]
SERVICE_TYPES = ["管道维修","助老服务","保洁服务","就诊服务","营养餐服务","定期接送服务","其他"]
# --------------------------------------

def connect():
    return pymysql.connect(
        host=DB["host"],
        port=DB["port"],
        user=DB["user"],
        password=DB["password"],
        db=DB["db"],
        charset=DB["charset"],
        cursorclass=pymysql.cursors.DictCursor,
        autocommit=False
    )

def month_range(start_ym, end_ym):
    sy, sm = map(int, start_ym.split("-"))
    ey, em = map(int, end_ym.split("-"))
    months = []
    y, m = sy, sm
    while (y < ey) or (y == ey and m <= em):
        months.append(f"{y:04d}-{m:02d}")
        m += 1
        if m > 12:
            m = 1
            y += 1
    return months

def rand_datetime_for_month(ym):
    year, month = map(int, ym.split("-"))
    day = random.randint(1, 25)
    hour = random.randint(0,23)
    minute = random.randint(0,59)
    second = random.randint(0,59)
    return datetime(year, month, day, hour, minute, second)

def insert_regions(conn):
    with conn.cursor() as cur:
        cur.execute("SELECT COUNT(*) cnt FROM region")
        cnt = cur.fetchone()["cnt"]
        if cnt > 0:
            print("region 表已有数据，跳过插入 region。")
            cur.execute("SELECT region_id, region_name FROM region")
            rows = cur.fetchall()
            return {r["region_name"]: r["region_id"] for r in rows}
        ids = {}
        for name, city, prov in REGIONS:
            cur.execute(
                "INSERT INTO region (region_name, city, province) VALUES (%s,%s,%s)",
                (name, city, prov)
            )
        conn.commit()
        cur.execute("SELECT region_id, region_name FROM region")
        rows = cur.fetchall()
        for r in rows: ids[r["region_name"]] = r["region_id"]
        print(f"插入 {len(ids)} 个 region")
        return ids

def insert_users(conn, n):
    users = []
    with conn.cursor() as cur:
        cur.execute("SELECT COUNT(*) cnt FROM `user`")
        cnt = cur.fetchone()["cnt"]
        if cnt > 0:
            print("user 表已有数据，读取并使用现有用户（不会重复插入）")
            cur.execute("SELECT id FROM `user`")
            return [r["id"] for r in cur.fetchall()]
        for i in range(1, n+1):
            username = f"user{i:03d}"
            phone = f"138{random.randint(10000000,99999999)}"
            realName = f"姓名{i:03d}"
            profile = f"我是{username}的简介"
            # 密码用明文 'password'（用于数据填充；登录不是必须）
            cur.execute(
                "INSERT INTO `user` (username,password,user_type,real_name,phone,profile,register_time,update_time) "
                "VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
                (username, "password", 0, realName, phone, profile, datetime.now(), datetime.now())
            )
            users.append(cur.lastrowid)
        conn.commit()
        print(f"插入 {len(users)} 个 user")
        return users

def insert_needs(conn, users, region_ids, months, total_needs):
    needs = []  # list of dict: need_id, user_id, region_id, create_time
    with conn.cursor() as cur:
        for i in range(total_needs):
            ym = random.choice(months)
            create_time = rand_datetime_for_month(ym)
            update_time = create_time + timedelta(hours=random.randint(0,72))
            user_id = random.choice(users)
            region_name = random.choice(list(region_ids.keys()))
            region_id = region_ids[region_name]
            service_type = random.choice(SERVICE_TYPES)
            title = f"{service_type} - {random.randint(1000,9999)}"
            description = f"自动生成描述，需求编号 {i+1}"
            status = random.choices([0, -1], weights=[0.92, 0.08])[0]  # 多数为已发布
            cur.execute(
                "INSERT INTO need (user_id, region_id, service_type, title, description, status, create_time, update_time) "
                "VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
                (user_id, region_id, service_type, title, description, status, create_time, update_time)
            )
            nid = cur.lastrowid
            needs.append({"need_id": nid, "user_id": user_id, "region_id": region_id, "create_time": create_time, "ym": ym})
            if (i+1) % 100 == 0:
                conn.commit()
        conn.commit()
    print(f"插入 {len(needs)} 条 need")
    return needs

def insert_responses(conn, needs, users):
    total = 0
    inserted = 0
    with conn.cursor() as cur:
        for nd in needs:
            need_id = nd["need_id"]
            owner = nd["user_id"]
            base_time = nd["create_time"]
            num = random.choices([0,1,2,3,4], weights=[0.15,0.45,0.2,0.12,0.08])[0]
            total += num
            for k in range(num):
                # 选响应者不能是发布者
                responder = random.choice(users)
                if responder == owner:
                    # 选择不同用户
                    candidate = random.choice(users)
                    if candidate != owner:
                        responder = candidate
                create_time = base_time + timedelta(days=random.randint(0,10), hours=random.randint(0,23))
                update_time = create_time + timedelta(hours=random.randint(0,48))
                # response.status: 0 pending, 1 accepted (成功), 2 rejected, 3 canceled
                status = random.choices([0,1,2,3], weights=[0.55,0.25,0.15,0.05])[0]
                content = f"自动响应内容，need {need_id} - {k+1}"
                image_url = None
                cur.execute(
                    "INSERT INTO response (need_id, user_id, content, image_url, status, create_time, update_time) "
                    "VALUES (%s,%s,%s,%s,%s,%s,%s)",
                    (need_id, responder, content, image_url, status, create_time, update_time)
                )
                inserted += 1
            if inserted % 200 == 0:
                conn.commit()
        conn.commit()
    print(f"为 {len(needs)} 个需求生成 {inserted} 条 response（含 status=1 的若干）")
    return inserted

def main():
    months = month_range(START_MONTH, END_MONTH)
    print("months:", months)
    conn = connect()
    try:
        region_ids = insert_regions(conn)
        users = insert_users(conn, NUM_USERS)
        needs = insert_needs(conn, users, region_ids, months, TOTAL_NEEDS)
        resp_count = insert_responses(conn, needs, users)
        print("完成：")
        print(f"  users: {len(users)}")
        print(f"  needs: {len(needs)}")
        print(f"  responses: {resp_count}")
    except Exception as e:
        conn.rollback()
        print("出错，回滚。错误：", e)
        raise
    finally:
        conn.close()

if __name__ == "__main__":
    main()