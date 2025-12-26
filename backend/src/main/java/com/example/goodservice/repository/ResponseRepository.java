package com.example.goodservice.repository;

import com.example.goodservice.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByNeedId(Long needId);
    List<Response> findByUserId(Long responderId);
    long countByNeedId(Long needId);
}
