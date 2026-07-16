package com.quiz.backend.repository;

import com.quiz.backend.entity.BpCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpCustomerRepository extends JpaRepository<BpCustomer, String> {

    @Query("SELECT c FROM BpCustomer c WHERE c.bpcnum0 LIKE %:q%")
    List<BpCustomer> search(@Param("q") String q);
}
