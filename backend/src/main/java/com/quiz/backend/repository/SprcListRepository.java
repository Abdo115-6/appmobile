package com.quiz.backend.repository;

import com.quiz.backend.entity.SprcList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SprcListRepository extends JpaRepository<SprcList, Long> {

    @Query(value = "SELECT TOP 1 PRI_0 FROM MALLZELLIJ.SPRICLIST WHERE PLI_0 = 'T11' AND PLICRD_0 = 'SPL26-0001'", nativeQuery = true)
    BigDecimal findPrice();
}
