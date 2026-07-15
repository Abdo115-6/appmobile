package com.quiz.backend.repository;

import com.quiz.backend.entity.SprcList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SprcListRepository extends JpaRepository<SprcList, Long> {

    @Query(value = "SELECT TOP 1 PRI_0 FROM MALLZELLIJ.SPRICLIST WHERE PLI_0 = :pli0 AND PLICRD_0 = :plicrd0 AND PLICRI2_0 = :itmref", nativeQuery = true)
    BigDecimal findPriceByArticle(@Param("pli0") String pli0, @Param("plicrd0") String plicrd0, @Param("itmref") String itmref);


}