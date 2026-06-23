package com.quiz.backend.controller;

import com.quiz.backend.dto.ArticleResponse;
import com.quiz.backend.dto.ArticleStockResponse;
import com.quiz.backend.entity.ArticleStock;
import com.quiz.backend.repository.ArticleRepository;
import com.quiz.backend.repository.ArticleStockRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final ArticleStockRepository articleStockRepository;

    public ArticleController(ArticleRepository articleRepository, ArticleStockRepository articleStockRepository) {
        this.articleRepository = articleRepository;
        this.articleStockRepository = articleStockRepository;
    }

    @GetMapping
    public List<ArticleResponse> getAll() {
        return articleRepository.findAll().stream()
                .map(a -> new ArticleResponse(a.getId(), a.getNom()))
                .toList();
    }

    @GetMapping("/search")
    public List<ArticleResponse> search(@RequestParam String q) {
        return articleRepository.findByNomContainingIgnoreCase(q).stream()
                .map(a -> new ArticleResponse(a.getId(), a.getNom()))
                .toList();
    }

    @GetMapping("/{articleId}/stocks")
    public List<ArticleStockResponse> getStocks(@PathVariable Long articleId) {
        return articleStockRepository.findByArticleId(articleId).stream()
                .map(as -> new ArticleStockResponse(as.getSite().getId(), as.getSite().getNom(), as.getQuantite()))
                .toList();
    }
}
