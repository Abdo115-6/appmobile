package com.quiz.backend.dto;

import java.math.BigDecimal;

public class ArticleStockResponse {
    private Long siteId;
    private String siteName;
    private Integer quantity;
    private Integer quantiteALouer;
    private BigDecimal prix;
    private BigDecimal prixPromo;
    private BigDecimal prixPrevendor;

    public ArticleStockResponse() {}

    public ArticleStockResponse(Long siteId, String siteName, Integer quantity, Integer quantiteALouer, BigDecimal prix, BigDecimal prixPromo, BigDecimal prixPrevendor) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.quantity = quantity;
        this.quantiteALouer = quantiteALouer;
        this.prix = prix;
        this.prixPromo = prixPromo;
        this.prixPrevendor = prixPrevendor;
    }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public void setQuantiteALouer(Integer quantiteALouer) { this.quantiteALouer = quantiteALouer; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public BigDecimal getPrixPromo() { return prixPromo; }
    public void setPrixPromo(BigDecimal prixPromo) { this.prixPromo = prixPromo; }
    public BigDecimal getPrixPrevendor() { return prixPrevendor; }
    public void setPrixPrevendor(BigDecimal prixPrevendor) { this.prixPrevendor = prixPrevendor; }
}
