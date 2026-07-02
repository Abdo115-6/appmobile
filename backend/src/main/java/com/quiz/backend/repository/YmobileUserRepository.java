package com.quiz.backend.repository;

import com.quiz.backend.entity.YmobileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YmobileUserRepository extends JpaRepository<YmobileUser, Long> {
    Optional<YmobileUser> findByYlogin0(String ylogin0);
    boolean existsByYlogin0(String ylogin0);
}
