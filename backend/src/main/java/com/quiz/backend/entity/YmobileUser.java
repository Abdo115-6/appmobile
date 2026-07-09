package com.quiz.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "YMOBILE")
public class YmobileUser {

    @Id
    @Column(name = "ROWID")
    private Long rowid;

    @Column(name = "YLOGIN_0")
    private String ylogin0;

    @Column(name = "YPASS_0")
    private String ypass0;

    @Column(name = "YID_0")
    private String yid0;

    @Column(name = "YROLE_0")
    private String yrole0;

    @Column(name = "CREDATTIM_0")
    private LocalDateTime credattim0;

    @Column(name = "UPDDATTIM_0")
    private LocalDateTime upddattim0;

    @Column(name = "CREUSR_0")
    private String creusr0;

    @Column(name = "UPDUSR_0")
    private String updusr0;

    public YmobileUser() {}

    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
    public String getYlogin0() { return ylogin0; }
    public void setYlogin0(String ylogin0) { this.ylogin0 = ylogin0; }
    public String getYpass0() { return ypass0; }
    public void setYpass0(String ypass0) { this.ypass0 = ypass0; }
    public String getYid0() { return yid0; }
    public void setYid0(String yid0) { this.yid0 = yid0; }
    public String getYrole0() { return yrole0; }
    public void setYrole0(String yrole0) { this.yrole0 = yrole0; }
    public LocalDateTime getCredattim0() { return credattim0; }
    public void setCredattim0(LocalDateTime credattim0) { this.credattim0 = credattim0; }
    public LocalDateTime getUpddattim0() { return upddattim0; }
    public void setUpddattim0(LocalDateTime upddattim0) { this.upddattim0 = upddattim0; }
    public String getCreusr0() { return creusr0; }
    public void setCreusr0(String creusr0) { this.creusr0 = creusr0; }
    public String getUpdusr0() { return updusr0; }
    public void setUpdusr0(String updusr0) { this.updusr0 = updusr0; }
}
