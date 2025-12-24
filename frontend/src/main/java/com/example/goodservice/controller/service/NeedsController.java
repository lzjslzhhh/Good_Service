package com.example.goodservice.controller.service;

import com.example.goodservice.common.Result;
import com.example.goodservice.vo.service.NeedItemVO;
import org.springframework.web.bind.annotation.*;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/needs")
public class NeedsController {

    // ================= 公共需求列表 =================
    @GetMapping("/public")
    public Result<List<NeedItemVO>> getPublicNeeds(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        // TODO: 查询数据库，根据分页和筛选条件返回数据
        return Result.success(mockData(page, pageSize));
    }

    private List<NeedItemVO> mockData(int page, int pageSize) {
        return List.of(
                new NeedItemVO(1L, "修水管", "管道维修", "北京市", "2025-12-01", "Pending"),
                new NeedItemVO(2L, "助老服务", "助老服务", "上海市", "2025-12-05", "Responded")
        );
    }

    // ================= 我发布的需求 =================
    @GetMapping("/my")
    public Result<List<NeedItemVO>> getMyPublishedNeeds(
            @RequestParam int page,
            @RequestParam int pageSize) {

        // TODO: 根据当前登录用户查询发布的需求
        return Result.success(mockMyNeeds(page, pageSize));
    }

    // ================= 我提交的响应列表 =================
    @GetMapping("/my-responses")
    public Result<List<Map<String, Object>>> getMyResponses(
            @RequestParam int page,
            @RequestParam int pageSize) {
        // TODO: 根据当前登录用户查询
        return Result.success(mockMyResponses(page, pageSize));
    }

    private List<Map<String, Object>> mockMyResponses(int page, int pageSize) {
        return List.of(
                Map.of(
                        "id", 1,
                        "needId", 1,
                        "needTitle", "修水管",
                        "serviceContent", "修水管服务",
                        "price", 200,
                        "status", "Submitted",
                        "mediaFiles", List.of("pic1.jpg", "pic2.mp4")
                ),
                Map.of(
                        "id", 2,
                        "needId", 2,
                        "needTitle", "助老服务",
                        "serviceContent", "助老陪伴",
                        "price", 150,
                        "status", "Accepted",
                        "mediaFiles", List.of()
                )
        );
    }

    private List<NeedItemVO> mockMyNeeds(int page, int pageSize) {
        return List.of(
                new NeedItemVO(1L, "修水管", "管道维修", "北京市", "2025-12-01", "Pending"),
                new NeedItemVO(2L, "助老服务", "助老服务", "上海市", "2025-12-05", "Responded")
        );
    }

    // ================= 响应列表 =================
    @GetMapping("/{needId}/responses")
    public Result<List<Map<String, Object>>> getResponsesByNeedId(@PathVariable Long needId) {
        return Result.success(mockResponses(needId));
    }

    private List<Map<String, Object>> mockResponses(Long needId) {
        return List.of(
                Map.of(
                        "id", 1,
                        "responderName", "张三",
                        "responderIntro", "有5年经验",
                        "serviceContent", "修水管服务",
                        "price", 200,
                        "status", "Submitted"
                ),
                Map.of(
                        "id", 2,
                        "responderName", "李四",
                        "responderIntro", "专业助老",
                        "serviceContent", "助老服务",
                        "price", 150,
                        "status", "Accepted"
                )
        );
    }

    // ================= 提交响应 =================
    @PostMapping("/responses")
    public Result<Void> submitResponse(@RequestBody Map<String, Object> dto) {
        // dto 包含 needId, content, price, mediaFiles
        // TODO: 保存响应到数据库
        return Result.success(null);
    }

    // ================= 修改响应 =================
    @PutMapping("/responses/{responseId}")
    public Result<Void> updateResponse(@PathVariable Long responseId,
                                       @RequestBody Map<String, Object> dto) {
        // TODO: 更新数据库
        return Result.success(null);
    }

    // ================= 撤销响应 =================
    @DeleteMapping("/responses/{responseId}")
    public Result<Void> deleteResponse(@PathVariable Long responseId) {
        // TODO: 删除或标记为撤销
        return Result.success(null);
    }



    // ================= 响应审核 =================
    @PostMapping("/responses/{responseId}/audit")
    public Result<Void> auditResponse(@PathVariable Long responseId, @RequestBody Map<String, String> body) {
        String action = body.get("action"); // accept / reject
        // TODO: 更新数据库状态
        return Result.success(null);
    }

    // ================= 发布/修改需求 =================
    @PostMapping
    public Result<Void> publishNeed(@RequestBody Map<String, Object> dto) {
        // TODO: 保存需求及文件信息
        return Result.success(null);
    }

    // ================= 统计分析 =================
    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getStats(
            @RequestParam String startMonth,  // YYYY-MM
            @RequestParam String endMonth,    // YYYY-MM
            @RequestParam(required = false) String region) {

        // TODO: 查询数据库，按月份和地域统计
        // 返回 List<Map>，每条 Map 含: month, region, publishCount, successCount
        return Result.success(mockStats(startMonth, endMonth, region));
    }

    private List<Map<String, Object>> mockStats(String startMonth, String endMonth, String region) {
        List<Map<String, Object>> list = new ArrayList<>();
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01");
        while (!start.isAfter(end)) {
            list.add(Map.of(
                    "month", start.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    "region", region != null && !region.isEmpty() ? region : "全区",
                    "publishCount", (int)(Math.random() * 200 + 50),
                    "successCount", (int)(Math.random() * 100 + 20)
            ));
            start = start.plusMonths(1);
        }
        return list;
    }

    // ================= 获取单条需求/响应详情 =================
    @GetMapping("/{id}/detail")
    public Result<Map<String, Object>> getDetail(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "need") String type) {

        // type=need 获取需求详情，type=response 获取响应详情
        Map<String, Object> data;
        if ("need".equals(type)) {
            data = Map.of(
                    "id", id,
                    "title", "急需家庭保洁",
                    "description", "这是一个非常详细的描述文本，说明了具体的需求内容。",
                    "status", "Pending",
                    "userId", 88,
                    "createTime", "2023-11-21 10:00:00"
            );
        } else {
            data = Map.of(
                    "id", id,
                    "title", "承接家庭保洁服务",
                    "description", "这是一个非常详细的描述文本，说明了服务响应内容。",
                    "status", "Pending",
                    "userId", 66,
                    "createTime", "2023-11-20 15:00:00"
            );
        }

        return Result.success(data);
    }


    // ================= 文件上传 =================
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // TODO: 保存文件，返回文件名或 URL
        return Result.success(Map.of("name", file.getOriginalFilename()));
    }

}


