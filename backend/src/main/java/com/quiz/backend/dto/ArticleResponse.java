package com.quiz.backend.dto;

public class ArticleResponse {
    private Long id;
    private String nom;
    private Integer quantiteALouer;

    public ArticleResponse() {}

    public ArticleResponse(Long id, String nom, Integer quantiteALouer) {
        this.id = id;
        this.nom = nom;
        this.quantiteALouer = quantiteALouer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public void setQuantiteALouer(Integer quantiteALouer) { this.quantiteALouer = quantiteALouer; }
}
