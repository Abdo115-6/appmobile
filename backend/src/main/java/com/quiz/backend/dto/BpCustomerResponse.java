package com.quiz.backend.dto;

public class BpCustomerResponse {
    private String code;
    private String name;
    private String shortName;

    public BpCustomerResponse() {}

    public BpCustomerResponse(String code, String name, String shortName) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
}
