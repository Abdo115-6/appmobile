package com.quiz.backend.repository;

import com.quiz.backend.entity.ItmMvt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItmMvtRepository extends JpaRepository<ItmMvt, Long> {
    List<ItmMvt> findByItmref0(String itmref0);
}
