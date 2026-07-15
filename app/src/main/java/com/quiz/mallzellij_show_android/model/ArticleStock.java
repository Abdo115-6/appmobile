package com.quiz.mallzellij_show_android.model;

import java.math.BigDecimal;

public class ArticleStock {
    private Long siteId;
    private String siteName;
    private Integer quantity;
    private Integer quantiteALouer;
    private Integer prix;
    private BigDecimal prixPromo;
    private BigDecimal prixPrevendor;

    public Long getSiteId() { return siteId; }
    public String getSiteName() { return siteName; }
    public Integer getQuantity() { return quantity; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public Integer getPrix() { return prix; }
    public BigDecimal getPrixPromo() { return prixPromo; }
    public BigDecimal getPrixPrevendor() { return prixPrevendor; }
}
