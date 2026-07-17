package com.quiz.mallzellij_show_android.model;

import java.math.BigDecimal;

public class DevisRequest {
    private String site;
    private String clientCode;
    private String clientName;
    private String articleRef;
    private String articleName;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal coefficient;
    private Integer cartons;
    private String creusr0;
    private String mobileKey;

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getArticleRef() { return articleRef; }
    public void setArticleRef(String articleRef) { this.articleRef = articleRef; }
    public String getArticleName() { return articleName; }
    public void setArticleName(String articleName) { this.articleName = articleName; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public Integer getCartons() { return cartons; }
    public void setCartons(Integer cartons) { this.cartons = cartons; }
    public String getCreusr0() { return creusr0; }
    public void setCreusr0(String creusr0) { this.creusr0 = creusr0; }
    public String getMobileKey() { return mobileKey; }
    public void setMobileKey(String mobileKey) { this.mobileKey = mobileKey; }
}
