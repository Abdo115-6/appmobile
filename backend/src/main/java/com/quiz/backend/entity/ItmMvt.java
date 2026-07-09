package com.quiz.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ITMMVT")
public class ItmMvt {

    @Id
    @Column(name = "ROWID")
    private Long rowid;

    @Column(name = "ITMREF_0")
    private String itmref0;

    @Column(name = "STOFCY_0")
    private String stofcy0;

    @Column(name = "PHYSTO_0")
    private BigDecimal physto0;

    @Column(name = "CTLSTO_0")
    private BigDecimal ctlsto0;

    @Column(name = "REJSTO_0")
    private BigDecimal rejsto0;

    @Column(name = "AVC_0")
    private BigDecimal avc0;

    @Column(name = "AVCBASQTY_0")
    private BigDecimal avcbasqty0;

    @Column(name = "CREDAT_0")
    private LocalDate credat0;

    @Column(name = "CREUSR_0")
    private String creusr0;

    @Column(name = "UPDDAT_0")
    private LocalDate upddat0;

    @Column(name = "UPDUSR_0")
    private String updusr0;

    @Column(name = "CREDATTIM_0")
    private LocalDateTime credattim0;

    @Column(name = "UPDDATTIM_0")
    private LocalDateTime upddattim0;

    @Column(name = "PHYALL_0")
    private BigDecimal phyall0;

    @Column(name = "YAVCTTC_0")
    private BigDecimal yavcttc0;

    @Column(name = "YPMPDEM_0")
    private BigDecimal ypmpdem0;

    @Column(name = "YQTYDEM_0")
    private BigDecimal yqtydem0;

    @Column(name = "YDATE_0")
    private LocalDate ydate0;

    public ItmMvt() {}

    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
    public String getItmref0() { return itmref0; }
    public void setItmref0(String itmref0) { this.itmref0 = itmref0; }
    public String getStofcy0() { return stofcy0; }
    public void setStofcy0(String stofcy0) { this.stofcy0 = stofcy0; }
    public BigDecimal getPhysto0() { return physto0; }
    public void setPhysto0(BigDecimal physto0) { this.physto0 = physto0; }
    public BigDecimal getCtlsto0() { return ctlsto0; }
    public void setCtlsto0(BigDecimal ctlsto0) { this.ctlsto0 = ctlsto0; }
    public BigDecimal getRejsto0() { return rejsto0; }
    public void setRejsto0(BigDecimal rejsto0) { this.rejsto0 = rejsto0; }
    public BigDecimal getAvc0() { return avc0; }
    public void setAvc0(BigDecimal avc0) { this.avc0 = avc0; }
    public BigDecimal getAvcbasqty0() { return avcbasqty0; }
    public void setAvcbasqty0(BigDecimal avcbasqty0) { this.avcbasqty0 = avcbasqty0; }
    public LocalDate getCredat0() { return credat0; }
    public void setCredat0(LocalDate credat0) { this.credat0 = credat0; }
    public String getCreusr0() { return creusr0; }
    public void setCreusr0(String creusr0) { this.creusr0 = creusr0; }
    public LocalDate getUpddat0() { return upddat0; }
    public void setUpddat0(LocalDate upddat0) { this.upddat0 = upddat0; }

    public String getUpdusr0() { return updusr0; }
    public void setUpdusr0(String updusr0) { this.updusr0 = updusr0; }
    public LocalDateTime getCredattim0() { return credattim0; }
    public void setCredattim0(LocalDateTime credattim0) { this.credattim0 = credattim0; }
    public LocalDateTime getUpddattim0() { return upddattim0; }
    public void setUpddattim0(LocalDateTime upddattim0) { this.upddattim0 = upddattim0; }
    public BigDecimal getPhyall0() { return phyall0; }
    public void setPhyall0(BigDecimal phyall0) { this.phyall0 = phyall0; }
    public BigDecimal getYavcttc0() { return yavcttc0; }
    public void setYavcttc0(BigDecimal yavcttc0) { this.yavcttc0 = yavcttc0; }
    public BigDecimal getYpmpdem0() { return ypmpdem0; }
    public void setYpmpdem0(BigDecimal ypmpdem0) { this.ypmpdem0 = ypmpdem0; }
    public BigDecimal getYqtydem0() { return yqtydem0; }
    public void setYqtydem0(BigDecimal yqtydem0) { this.yqtydem0 = yqtydem0; }
    public LocalDate getYdate0() { return ydate0; }
    public void setYdate0(LocalDate ydate0) { this.ydate0 = ydate0; }
}
