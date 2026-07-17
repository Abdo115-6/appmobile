package com.quiz.backend.dto;

import java.util.List;

public class DevisConfirmRequest {
    private String site;
    private String clientCode;
    private String clientName;
    private String creusr0;
    private String mobileKey;
    private List<DevisRequest> articles;

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getCreusr0() { return creusr0; }
    public void setCreusr0(String creusr0) { this.creusr0 = creusr0; }
    public String getMobileKey() { return mobileKey; }
    public void setMobileKey(String mobileKey) { this.mobileKey = mobileKey; }
    public List<DevisRequest> getArticles() { return articles; }
    public void setArticles(List<DevisRequest> articles) { this.articles = articles; }
}
