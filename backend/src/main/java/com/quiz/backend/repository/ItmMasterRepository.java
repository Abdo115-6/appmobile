package com.quiz.backend.repository;

import com.quiz.backend.entity.ItmMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItmMasterRepository extends JpaRepository<ItmMaster, Long> {
    List<ItmMaster> findByItmdes10ContainingIgnoreCase(String itmdes10);

    @Query("SELECT i FROM ItmMaster i WHERE TRIM(i.itmref0) = :ref")
    Optional<ItmMaster> findByItmref0(String ref);

    @Query(value = "SELECT * FROM MALLZELLIJ.ITMMASTER WHERE REPLACE(ITMREF_0, ' ', '') LIKE %:ref%", nativeQuery = true)
    Optional<ItmMaster> findByItmref0Like(String ref);
}
