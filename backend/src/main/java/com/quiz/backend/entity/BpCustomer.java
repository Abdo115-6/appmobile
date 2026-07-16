package com.quiz.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BPCUSTOMER")
public class BpCustomer {

    @Id
    @Column(name = "BPCNUM_0")
    private String bpcnum0;

    @Column(name = "BPCNAM_0")
    private String bpcnam0;

    @Column(name = "BPCSHO_0")
    private String bpcsho0;

    @Column(name = "BPCSTA_0")
    private Integer bpcsta0;

    @Column(name = "ROWID")
    private Long rowid;

    public BpCustomer() {}

    public String getBpcnum0() { return bpcnum0; }
    public void setBpcnum0(String bpcnum0) { this.bpcnum0 = bpcnum0; }
    public String getBpcnam0() { return bpcnam0; }
    public void setBpcnam0(String bpcnam0) { this.bpcnam0 = bpcnam0; }
    public String getBpcsho0() { return bpcsho0; }
    public void setBpcsho0(String bpcsho0) { this.bpcsho0 = bpcsho0; }
    public Integer getBpcsta0() { return bpcsta0; }
    public void setBpcsta0(Integer bpcsta0) { this.bpcsta0 = bpcsta0; }
    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
}
