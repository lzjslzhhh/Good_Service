package com.example.goodservice.repository;

import com.example.goodservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContainingOrPhoneContaining(String username, String phone, Pageable pageable);
}
