package com.quiz.backend.repository;

import com.quiz.backend.entity.ArticleStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleStockRepository extends JpaRepository<ArticleStock, Long> {
    List<ArticleStock> findByArticleId(Long articleId);
}
