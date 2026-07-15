package com.quiz.backend.dto;

public class ArticleResponse {
    private Long id;
    private String nom;
    private Integer quantiteALouer;
    private String ref;
    private Integer coefficient;

    public ArticleResponse() {}

    public ArticleResponse(Long id, String nom, Integer quantiteALouer, String ref, Integer coefficient) {
        this.id = id;
        this.nom = nom;
        this.quantiteALouer = quantiteALouer;
        this.ref = ref;
        this.coefficient = coefficient;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public void setQuantiteALouer(Integer quantiteALouer) { this.quantiteALouer = quantiteALouer; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }
}
