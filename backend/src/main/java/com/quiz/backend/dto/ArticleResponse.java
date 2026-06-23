package com.quiz.backend.dto;

public class ArticleResponse {
    private Long id;
    private String nom;

    public ArticleResponse() {}

    public ArticleResponse(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
