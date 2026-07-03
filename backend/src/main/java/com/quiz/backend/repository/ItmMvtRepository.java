package com.quiz.backend.repository;

import com.quiz.backend.entity.ItmMvt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItmMvtRepository extends JpaRepository<ItmMvt, Long> {
    List<ItmMvt> findByItmref0(String itmref0);

    @Query("SELECT m FROM ItmMvt m WHERE m.itmref0 = :itmref0 AND m.avcbasqty0 > 0 AND m.stofcy0 IN :sites")
    List<ItmMvt> findByItmref0AndSites(@Param("itmref0") String itmref0, @Param("sites") List<String> sites);

    List<ItmMvt> findBystofcy0(String stofcy0);
}
