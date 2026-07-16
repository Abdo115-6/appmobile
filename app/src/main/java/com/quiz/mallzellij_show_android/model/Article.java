package com.quiz.mallzellij_show_android.model;

import java.math.BigDecimal;

public class Article {
    private Long id;
    private String nom;
    private Integer quantiteALouer;
    private String ref;
    private BigDecimal coefficient;
    private String sau;

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public String getRef() { return ref; }
    public BigDecimal getCoefficient() { return coefficient; }
    public String getSau() { return sau; }
}
