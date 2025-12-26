// backend/src/main/java/com/example/goodservice/service/ResponseService.java
package com.example.goodservice.service;

import com.example.goodservice.entity.Need;
import com.example.goodservice.entity.Response;
import com.example.goodservice.repository.NeedRepository;
import com.example.goodservice.repository.ResponseRepository;
import com.example.goodservice.repository.UserRepository;
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
                vo.setStatus(response.getStatus());
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
    @Autowired
    private UserRepository userRepository;
    public List<com.example.goodservice.vo.service.ResponseVO> getResponsesByNeedId(Long needId) {
        List<Response> responses = responseRepository.findByNeedId(needId);

        return responses.stream().map(response -> {
            com.example.goodservice.vo.service.ResponseVO vo = new com.example.goodservice.vo.service.ResponseVO();
            vo.setId(response.getResponseId());
            vo.setNeedId(response.getNeedId());
            vo.setServiceContent(response.getContent());
            vo.setStatus(response.getStatus());

            // 响应者信息（user）
            if (response.getUserId() != null) {
                userRepository.findById(response.getUserId()).ifPresent(u -> {
                    vo.setResponderName(u.getRealName() != null ? u.getRealName() : u.getUsername());
                    vo.setResponderIntro(u.getProfile());
                });
            }

            // 需求标题
            needRepository.findById(response.getNeedId()).ifPresent(n -> vo.setNeedTitle(n.getTitle()));

            return vo;
        }).collect(java.util.stream.Collectors.toList());
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