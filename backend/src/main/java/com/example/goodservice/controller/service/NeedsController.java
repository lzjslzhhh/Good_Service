package com.example.goodservice.controller.service;

import com.example.goodservice.common.Result;
import com.example.goodservice.entity.Need;
import com.example.goodservice.service.NeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/need")
class NeedsController {

    @Autowired
    private NeedService needService;

    /**
     * 发布需求
     */
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Need need) {
        needService.publishNeed(need);
        return Result.success("需求发布成功");
    }

    /**
     * 查询所有需求
     */
    @GetMapping("/list")
    public Result<List<Need>> listAll() {
        return Result.success(needService.listAllNeeds());
    }

    /**
     * 查询我的需求
     */
    @GetMapping("/my")
    public Result<List<Need>> listMyNeeds(@RequestParam Long userId) {
        return Result.success(needService.listNeedsByUser(userId));
    }

    /**
     * 查询需求详情
     */
    @GetMapping("/{needId}")
    public Result<Need> detail(@PathVariable Long needId) {
        return Result.success(needService.getNeedById(needId));
    }

    /**
     * 更新需求状态
     */
    @PutMapping("/{needId}/status")
    public Result<String> updateStatus(@PathVariable Long needId,
                                       @RequestParam Integer status) {
        needService.updateNeedStatus(needId, status);
        return Result.success("状态更新成功");
    }

    /**
     * 删除需求
     */
    @DeleteMapping("/{needId}")
    public Result<String> delete(@PathVariable Long needId) {
        needService.deleteNeed(needId);
        return Result.success("需求删除成功");
    }
}
