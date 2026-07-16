package com.quiz.backend.dto;

import java.math.BigDecimal;

public class ArticleResponse {
    private Long id;
    private String nom;
    private Integer quantiteALouer;
    private String ref;
    private BigDecimal coefficient;
    private String sau;

    public ArticleResponse() {}

    public ArticleResponse(Long id, String nom, Integer quantiteALouer, String ref, BigDecimal coefficient, String sau) {
        this.id = id;
        this.nom = nom;
        this.quantiteALouer = quantiteALouer;
        this.ref = ref;
        this.coefficient = coefficient;
        this.sau = sau;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public void setQuantiteALouer(Integer quantiteALouer) { this.quantiteALouer = quantiteALouer; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public String getSau() { return sau; }
    public void setSau(String sau) { this.sau = sau; }
}
