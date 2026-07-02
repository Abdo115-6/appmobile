package com.quiz.backend.repository;

import com.quiz.backend.entity.ItmMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItmMasterRepository extends JpaRepository<ItmMaster, Long> {
    List<ItmMaster> findByItmdes10ContainingIgnoreCase(String itmdes10);
}
