package com.quiz.backend.repository;

import com.quiz.backend.entity.YdevisMobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface YdevisMobileRepository extends JpaRepository<YdevisMobile, Long> {
}
