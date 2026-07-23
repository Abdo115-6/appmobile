package com.quiz.backend.repository;

import com.quiz.backend.entity.YdevisMobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface YdevisMobileRepository extends JpaRepository<YdevisMobile, Long> {
    @Query(value = "SELECT MAX(CAST(SUBSTRING(YMOBKEY_0, 4, 7) AS INT)) FROM MALLZELLIJ.YDEVISMOBILE WHERE YMOBKEY_0 LIKE 'MOB%'", nativeQuery = true)
    Integer findMaxYmobkey0Num();
}
