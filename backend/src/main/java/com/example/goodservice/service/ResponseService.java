// backend/src/main/java/com/example/goodservice/service/ResponseService.java
package com.example.goodservice.service;

import com.example.goodservice.entity.Need;
import com.example.goodservice.entity.Response;
import com.example.goodservice.repository.NeedRepository;
import com.example.goodservice.repository.ResponseRepository;
import com.example.goodservice.vo.service.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponseService {
    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private NeedRepository needRepository;

    public List<ResponseVO> getMyResponsesWithDetails(Long userId) {
        List<Response> responses = responseRepository.findByUserId(userId);
        return responses.stream().map(response -> {
            ResponseVO vo = new ResponseVO();
            vo.setId(response.getResponseId());
            vo.setNeedId(response.getNeedId());

            // 查询关联的需求信息
            Need need = needRepository.findById(response.getNeedId()).orElse(null);
            if (need != null) {
                vo.setNeedTitle(need.getTitle());
                vo.setServiceContent(response.getContent());
                vo.setStatus(Long.valueOf(response.getStatus()));
            }

            // 映射其他字段...
            return vo;
        }).collect(Collectors.toList());
    }

    public void updateResponse(Response response) {
        Response existingResponse = responseRepository.findById(response.getResponseId())
                .orElseThrow(() -> new RuntimeException("响应不存在"));

        // 只能更新特定字段，比如内容和更新时间
        existingResponse.setContent(response.getContent());
        existingResponse.setUpdateTime(LocalDateTime.now());
        responseRepository.save(existingResponse);
    }

    public void deleteResponse(Long responseId) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("响应不存在"));
        responseRepository.delete(response);
    }
    public Response submitResponse(Response response) {
        Need need = needRepository.findById(response.getNeedId())
                .orElseThrow(() -> new RuntimeException("需求不存在"));

        if (need.getUserId().equals(response.getUserId())) {
            throw new RuntimeException("不能响应自己发布的需求");
        }
        response.setStatus(0);
        response.setCreateTime(LocalDateTime.now());
        response.setUpdateTime(LocalDateTime.now());
        return responseRepository.save(response);
    }

    public List<Response> getResponsesByNeedId(Long needId) {
        return responseRepository.findByNeedId(needId);
    }

    public List<Response> getMyResponses(Long userId) {
        return responseRepository.findByUserId(userId);
    }

    public void auditResponse(Long responseId, Integer status) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("响应不存在"));

        response.setStatus(status);
        responseRepository.save(response);
    }
}