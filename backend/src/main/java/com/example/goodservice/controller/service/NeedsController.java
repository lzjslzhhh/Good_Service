package com.example.goodservice.controller.service;

import com.example.goodservice.common.PageResult;
import com.example.goodservice.common.Result;
import com.example.goodservice.dto.need.NeedRequestDTO;
import com.example.goodservice.entity.Need;
import com.example.goodservice.entity.Region;
import com.example.goodservice.entity.Response;
import com.example.goodservice.repository.NeedRepository;
import com.example.goodservice.repository.ResponseRepository;
import com.example.goodservice.service.NeedService;
import com.example.goodservice.service.ResponseService;
import com.example.goodservice.vo.service.MyNeedVO;
import com.example.goodservice.vo.service.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.goodservice.repository.RegionRepository;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/need")
class NeedsController {

    @Autowired
    private NeedService needService;

    @Autowired
    private RegionRepository regionRepository;
    /**
     * 发布需求
     */
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody NeedRequestDTO request) {
        Region region = regionRepository.findByRegionName(request.getRegion())
                .orElseThrow(() -> new RuntimeException("地区不存在"));
        Need need = new Need();
        need.setServiceType(request.getServiceType());
        need.setTitle(request.getTitle());
        need.setDescription(request.getDescription());
        need.setUserId(request.getUserId());
        need.setRegionId(region.getRegionId());
        needService.publishNeed(need);
        return Result.success("需求发布成功");
    }
    /**
     * 修改需求
     */
    @PutMapping("/{needId}")
    public Result<String> updateNeed(@PathVariable Long needId, @RequestBody Need need) {
        need.setNeedId(needId); // 确保路径参数和请求体一致
        needService.updateNeed(need);
        return Result.success("需求修改成功");
    }
    /**
     * 查询所有需求
     */
    @GetMapping("/list")
    public Result<PageResult<Need>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String keyword) {

        PageResult<Need> result = needService.getNeedsWithFilter(page, pageSize, serviceType, keyword);
        return Result.success(result);
    }
    /**
     * 查询我的需求
     */
    @GetMapping("/my")
    public Result<List<MyNeedVO>> listMyNeeds(@RequestParam Long userId) {
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
    public Result<String> delete(@PathVariable Long needId, @RequestParam Long userId) {
        // 验证是用户自己的需求
        Need need = needService.getNeedById(needId);
        if (!need.getUserId().equals(userId)) {
            return Result.error("只能删除自己发布的需求");
        }

        // 验证没有响应
        long responseCount = responseRepository.countByNeedId(needId);
        if (responseCount > 0) {
            return Result.error("已有响应的需求不能删除");
        }

        needService.deleteNeed(needId);
        return Result.success("需求删除成功");
    }

    @Autowired
    private ResponseService responseService;

    @PostMapping("/responses")
    public Result<String> submitResponse(@RequestBody Response response) {
        responseService.submitResponse(response);
        return Result.success("响应提交成功");
    }

    @GetMapping("/{needId}/responses")
    public Result<List<Response>> getResponses(@PathVariable Long needId) {
        return Result.success(responseService.getResponsesByNeedId(needId));
    }
    @PutMapping("/responses/{responseId}")
    public Result<String> updateResponse(@PathVariable Long responseId, @RequestBody Response response) {
        response.setResponseId(responseId);
        responseService.updateResponse(response);
        return Result.success("响应修改成功");
    }
    // 删除响应
    @DeleteMapping("/responses/{responseId}")
    public Result<String> deleteResponse(@PathVariable Long responseId) {
        responseService.deleteResponse(responseId);
        return Result.success("响应删除成功");
    }
    @GetMapping("/my-responses")
    public Result<List<ResponseVO>> getMyResponses(@RequestParam Long responderId) {
        List<ResponseVO> responseVOs = responseService.getMyResponsesWithDetails(responderId);
        return Result.success(responseVOs);
    }

    @PostMapping("/responses/{responseId}/audit")
    public Result<String> auditResponse(
            @PathVariable Long responseId,
            @RequestParam Integer status) {
        responseService.auditResponse(responseId, status);
        return Result.success("审核操作完成");
    }

    // 在NeedService.java中添加
    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private NeedRepository needRepository;

    public List<MyNeedVO> listNeedsByUser(Long userId) {
        List<Need> needs = needRepository.findByUserId(userId);
        return needs.stream().map(need -> {
            MyNeedVO vo = new MyNeedVO();
            // ...其他字段设置...
            vo.setResponseCount(responseRepository.countByNeedId(need.getNeedId()));
            return vo;
        }).collect(Collectors.toList());
    }
}
