package com.quiz.backend.repository;

import com.quiz.backend.entity.ItmMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItmMasterRepository extends JpaRepository<ItmMaster, Long> {
    List<ItmMaster> findByItmdes10ContainingIgnoreCase(String itmdes10);
    // Optional<ItmMaster> findByEancod0(String eancod0);
    Optional<ItmMaster> findByItmref0(String itmref0);
}
