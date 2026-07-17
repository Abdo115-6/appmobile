package com.quiz.backend.repository;

import com.quiz.backend.entity.ItmMvt;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItmMvtRepository extends JpaRepository<ItmMvt, Long> {
    List<ItmMvt> findByItmref0(String itmref0);

    @Query("SELECT m FROM ItmMvt m WHERE m.itmref0 = :itmref0 AND m.physto0 > 0 AND m.stofcy0 IN :sites")
    List<ItmMvt> findByItmref0AndSites(@Param("itmref0") String itmref0, @Param("sites") List<String> sites);

    List<ItmMvt> findBystofcy0(String stofcy0);

    @Query("SELECT SUM(m.phyall0) FROM ItmMvt m WHERE m.itmref0 = :itmref0 AND m.stofcy0 IN :sites AND m.phyall0 > 0")
    BigDecimal sumPhyall0ByItmref0AndSites(@Param("itmref0") String itmref0, @Param("sites") List<String> sites);

    @Query("SELECT SUM(m.physto0) FROM ItmMvt m WHERE m.itmref0 = :itmref0 AND m.stofcy0 IN :sites AND m.physto0 > 0")
    BigDecimal sumPhysto0ByItmref0AndSites(@Param("itmref0") String itmref0, @Param("sites") List<String> sites);

    @Query("SELECT m.itmref0, SUM(m.phyall0) FROM ItmMvt m WHERE m.stofcy0 IN :sites AND m.phyall0 > 0 GROUP BY m.itmref0")
    List<Object[]> sumPhyall0GroupedByItmref0(@Param("sites") List<String> sites);

    @Query("SELECT m.itmref0, SUM(m.physto0), SUM(m.phyall0) FROM ItmMvt m WHERE m.stofcy0 IN :sites AND m.physto0 > 0 GROUP BY m.itmref0")
    List<Object[]> sumStocksGroupedByItmref0(@Param("sites") List<String> sites);
}
