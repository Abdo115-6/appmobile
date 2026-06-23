package com.quiz.backend.dto;

public class ArticleStockResponse {
    private Long siteId;
    private String siteName;
    private Integer quantity;

    public ArticleStockResponse() {}

    public ArticleStockResponse(Long siteId, String siteName, Integer quantity) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.quantity = quantity;
    }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
