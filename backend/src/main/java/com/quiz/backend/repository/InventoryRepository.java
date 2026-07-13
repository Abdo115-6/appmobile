package com.quiz.backend.repository;

import com.quiz.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByYitmref0(String yitmref0);
    List<Inventory> findByYdepot0(String ydepot0);
    List<Inventory> findByYequipe0(String yequipe0);
    List<Inventory> findByYzone0(String yzone0);

    @Query(value = "SELECT * FROM MALLZELLIJ.YINV WHERE " +
            "(:depot IS NULL OR YDEPOT_0 = :depot) AND " +
            "(:equipe IS NULL OR YEQUIPE_0 = :equipe) AND " +
            "(:zone IS NULL OR YZONE_0 = :zone) " +
            "ORDER BY CREDATTIM_0 DESC", nativeQuery = true)
    List<Inventory> findFiltered(@Param("depot") String depot,
                                  @Param("equipe") String equipe,
                                  @Param("zone") String zone);
}
