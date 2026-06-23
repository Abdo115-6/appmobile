package com.quiz.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "article_stock")
public class ArticleStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false)
    private Integer quantite;

    public ArticleStock() {}

    public ArticleStock(Article article, Site site, Integer quantite) {
        this.article = article;
        this.site = site;
        this.quantite = quantite;

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}
