package com.example.goodservice.service;

import com.example.goodservice.common.PageResult;
import com.example.goodservice.entity.Need;
import com.example.goodservice.repository.NeedRepository;
import com.example.goodservice.repository.RegionRepository;
import com.example.goodservice.repository.ResponseRepository;
import com.example.goodservice.vo.service.MyNeedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NeedService {

    @Autowired
    private NeedRepository needRepository;
    @Autowired  // 添加这行
    private RegionRepository regionRepository;
    public PageResult<Need> getNeedsWithFilter(int page, int pageSize, String serviceType, String keyword) {
        List<Need> allEntities = needRepository.findAll();

        // 手动筛选逻辑
        List<Need> filtered = allEntities.stream()
            .filter(need -> serviceType == null || serviceType.isEmpty() ||
                (need.getServiceType() != null && need.getServiceType().equals(serviceType)))
            .filter(need -> {
                if (keyword == null || keyword.isEmpty()) return true;
                String title = need.getTitle() == null ? "" : need.getTitle();
                String desc = need.getDescription() == null ? "" : need.getDescription();
                return title.contains(keyword) || desc.contains(keyword);
            })
            .collect(Collectors.toList());

        // 手动分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        List<Need> pagedList = filtered.subList(start, end);

        return new PageResult<>(pagedList, filtered.size(), page, pageSize);
    } /**
     * 发布需求
     */
    public void publishNeed(Need need) {
        need.setStatus(0);
        need.setCreateTime(LocalDateTime.now());
        need.setUpdateTime(LocalDateTime.now());
        needRepository.save(need);
    }
    /**
     * 修改需求
     */
    public void updateNeed(Need need) {
        Need existingNeed = getNeedById(need.getNeedId());
        existingNeed.setTitle(need.getTitle());
        existingNeed.setServiceType(need.getServiceType());
        existingNeed.setDescription(need.getDescription());
        existingNeed.setRegionId(need.getRegionId());
        existingNeed.setUpdateTime(LocalDateTime.now());
        needRepository.save(existingNeed);
    }
    /**
     * 查询所有需求
     */
    public List<Need> listAllNeeds() {
        return needRepository.findAll();
    }

    /**
     * 查询某个用户的需求
     */
    @Autowired
    private ResponseRepository responseRepository; // 需要先创建

    public List<MyNeedVO> listNeedsByUser(Long userId) {
        List<Need> needs = needRepository.findByUserId(userId);
        return needs.stream().map(need -> {
            MyNeedVO vo = new MyNeedVO();
            vo.setNeedId(need.getNeedId());
            vo.setUserId(need.getUserId());
            vo.setRegionId(need.getRegionId());
            vo.setTitle(need.getTitle());
            vo.setServiceType(need.getServiceType());
            vo.setStatus(need.getStatus());

            // 计算响应数量
            Long count = responseRepository.countByNeedId(need.getNeedId());
            vo.setResponseCount(count);

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询需求详情
     */
    public Need getNeedById(Long needId) {
        return needRepository.findById(needId)
                .orElseThrow(() -> new RuntimeException("需求不存在"));
    }

    /**
     * 更新需求状态
     */
    public void updateNeedStatus(Long needId, Integer status) {
        Need need = getNeedById(needId);
        need.setStatus(status);
        need.setUpdateTime(LocalDateTime.now());
        needRepository.save(need);
    }

    /**
     * 删除需求
     */
    public void deleteNeed(Long needId) {
        needRepository.deleteById(needId);
    }
}
