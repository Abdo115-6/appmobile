package com.quiz.backend.dto;

import java.math.BigDecimal;

public class ArticleResponse {
    private Long id;
    private String nom;
    private Integer quantiteALouer;
    private String ref;
    private BigDecimal coefficient;
    private String sau;
    private int physicalStock;
    private int availableStock;

    public ArticleResponse() {}

    public ArticleResponse(Long id, String nom, Integer quantiteALouer, String ref,
                           BigDecimal coefficient, String sau,
                           int physicalStock, int availableStock) {
        this.id = id;
        this.nom = nom;
        this.quantiteALouer = quantiteALouer;
        this.ref = ref;
        this.coefficient = coefficient;
        this.sau = sau;
        this.physicalStock = physicalStock;
        this.availableStock = availableStock;
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
    public int getPhysicalStock() { return physicalStock; }
    public void setPhysicalStock(int physicalStock) { this.physicalStock = physicalStock; }
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
}
