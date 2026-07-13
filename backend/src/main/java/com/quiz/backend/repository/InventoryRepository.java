package com.quiz.backend.repository;

import com.quiz.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByYitmref0(String yitmref0);
    List<Inventory> findByYdepot0(String ydepot0);
    List<Inventory> findByYequipe0(String yequipe0);
    List<Inventory> findByYzone0(String yzone0);
}
