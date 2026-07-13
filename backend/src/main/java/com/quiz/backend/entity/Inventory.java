package com.quiz.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "YINV")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROWID")
    private Long rowid;

    @Column(name = "UPDTICK_0")
    private Integer updtick0;

    @Column(name = "YNUM_0")
    private String ynum0;

    @Column(name = "YDEPOT_0")
    private String ydepot0;

    @Column(name = "YEQUIPE_0")
    private String yequipe0;

    @Column(name = "YZONE_0")
    private String yzone0;

    @Column(name = "YITMREF_0")
    private String yitmref0;

    @Column(name = "YQTYPLT_0")
    private BigDecimal yqtyplt0;

    @Column(name = "YQTYCRT_0")
    private BigDecimal yqtycrt0;

    @Column(name = "YQTYMTR_0")
    private BigDecimal yqtymtr0;

    @Column(name = "CREDATTIM_0")
    private LocalDateTime credattim0;

    @Column(name = "UPDDATTIM_0")
    private LocalDateTime upddattim0;

    @Column(name = "AUUID_0")
    private byte[] auuid0;

    @Column(name = "CREUSR_0")
    private String creusr0;

    @Column(name = "UPDUSR_0")
    private String updusr0;

    public Inventory() {}

    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
    public Integer getUpdtick0() { return updtick0; }
    public void setUpdtick0(Integer updtick0) { this.updtick0 = updtick0; }
    public String getYnum0() { return ynum0; }
    public void setYnum0(String ynum0) { this.ynum0 = ynum0; }
    public String getYdepot0() { return ydepot0; }
    public void setYdepot0(String ydepot0) { this.ydepot0 = ydepot0; }
    public String getYequipe0() { return yequipe0; }
    public void setYequipe0(String yequipe0) { this.yequipe0 = yequipe0; }
    public String getYzone0() { return yzone0; }
    public void setYzone0(String yzone0) { this.yzone0 = yzone0; }
    public String getYitmref0() { return yitmref0; }
    public void setYitmref0(String yitmref0) { this.yitmref0 = yitmref0; }
    public BigDecimal getYqtyplt0() { return yqtyplt0; }
    public void setYqtyplt0(BigDecimal yqtyplt0) { this.yqtyplt0 = yqtyplt0; }
    public BigDecimal getYqtycrt0() { return yqtycrt0; }
    public void setYqtycrt0(BigDecimal yqtycrt0) { this.yqtycrt0 = yqtycrt0; }
    public BigDecimal getYqtymtr0() { return yqtymtr0; }
    public void setYqtymtr0(BigDecimal yqtymtr0) { this.yqtymtr0 = yqtymtr0; }
    public LocalDateTime getCredattim0() { return credattim0; }
    public void setCredattim0(LocalDateTime credattim0) { this.credattim0 = credattim0; }
    public LocalDateTime getUpddattim0() { return upddattim0; }
    public void setUpddattim0(LocalDateTime upddattim0) { this.upddattim0 = upddattim0; }
    public byte[] getAuuid0() { return auuid0; }
    public void setAuuid0(byte[] auuid0) { this.auuid0 = auuid0; }
    public String getCreusr0() { return creusr0; }
    public void setCreusr0(String creusr0) { this.creusr0 = creusr0; }
    public String getUpdusr0() { return updusr0; }
    public void setUpdusr0(String updusr0) { this.updusr0 = updusr0; }
}
