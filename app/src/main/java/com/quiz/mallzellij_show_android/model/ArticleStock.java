package com.quiz.mallzellij_show_android.model;

import java.math.BigDecimal;

public class ArticleStock {
    private Long siteId;
    private String siteName;
    private Integer quantity;
    private Integer quantiteALouer;
    private Integer prix;

    public Long getSiteId() { return siteId; }
    public String getSiteName() { return siteName; }
    public Integer getQuantity() { return quantity; }
    public Integer getQuantiteALouer() { return quantiteALouer; }
    public Integer getPrix() { return prix; }
}
