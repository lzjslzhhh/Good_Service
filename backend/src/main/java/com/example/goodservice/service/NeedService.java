package com.example.goodservice.service;

import com.example.goodservice.entity.Need;
import com.example.goodservice.repository.NeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NeedService {

    @Autowired
    private NeedRepository needRepository;

    /**
     * 发布需求
     */
    public void publishNeed(Need need) {
        need.setStatus(0);
        need.setCreateTime(LocalDateTime.now());
        need.setUpdateTime(LocalDateTime.now());
        needRepository.save(need);
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
    public List<Need> listNeedsByUser(Long userId) {
        return needRepository.findByUserId(userId);
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
