package com.quiz.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ITMMASTER")
public class ItmMaster {

    @Id
    @Column(name = "ROWID")
    private Long rowid;

    @Column(name = "ITMREF_0")
    private String itmref0;

    @Column(name = "ITMDES1_0")
    private String itmdes10;

    @Column(name = "ITMDES2_0")
    private String itmdes20;

    @Column(name = "ITMDES3_0")
    private String itmdes30;

    @Column(name = "EANCOD_0")
    private String eancod0;

    @Column(name = "ITMSTA_0")
    private Integer itmsta0;

    @Column(name = "TCLCOD_0")
    private String tclcod0;

    @Column(name = "STU_0")
    private String stu0;

    @Column(name = "SAU_0")
    private String sau0;

    @Column(name = "PUU_0")
    private String puu0;

    @Column(name = "STOMGTCOD_0")
    private Integer stomgtcod0;

    @Column(name = "LOTMGTCOD_0")
    private Integer lotmgtcod0;

    @Column(name = "SERMGTCOD_0")
    private Integer sermgtcod0;

    @Column(name = "ITMWEI_0")
    private BigDecimal itmwei0;

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

    @Column(name = "YDIM_0")
    private String ydim0;

    @Column(name = "YCOUL_0")
    private String ycoul0;

    @Column(name = "XCOUT_0")
    private BigDecimal xcout0;

    @Column(name = "PCUSTUCOE_0")
    private BigDecimal pcustucoe0;

    @Column(name = "YFIN_0")
    private Integer yfin0;

    @Column(name = "YNEANCE_0")
    private String yneance0;

    @Column(name = "YNEANCE_1")
    private String yneance1;

    @Column(name = "YNEANCE_2")
    private String yneance2;

    @Column(name = "YCALIBRE_0")
    private String ycalibre0;

    @Column(name = "YCALIBRE_1")
    private String ycalibre1;

    @Column(name = "YCALIBRE_2")
    private String ycalibre2;

    @Column(name = "YDELISTER_0")
    private Integer ydelister0;

    @Column(name = "YNVCOLL_0")
    private Integer ynvcoll0;

    public ItmMaster() {}

    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
    public String getItmref0() { return itmref0; }
    public void setItmref0(String itmref0) { this.itmref0 = itmref0; }
    public String getItmdes10() { return itmdes10; }
    public void setItmdes10(String itmdes10) { this.itmdes10 = itmdes10; }
    public String getItmdes20() { return itmdes20; }
    public void setItmdes20(String itmdes20) { this.itmdes20 = itmdes20; }
    public String getItmdes30() { return itmdes30; }
    public void setItmdes30(String itmdes30) { this.itmdes30 = itmdes30; }
    public String getEancod0() { return eancod0; }
    public void setEancod0(String eancod0) { this.eancod0 = eancod0; }
    public Integer getItmsta0() { return itmsta0; }
    public void setItmsta0(Integer itmsta0) { this.itmsta0 = itmsta0; }
    public String getTclcod0() { return tclcod0; }
    public void setTclcod0(String tclcod0) { this.tclcod0 = tclcod0; }
    public String getStu0() { return stu0; }
    public void setStu0(String stu0) { this.stu0 = stu0; }
    public String getSau0() { return sau0; }
    public void setSau0(String sau0) { this.sau0 = sau0; }
    public String getPuu0() { return puu0; }
    public void setPuu0(String puu0) { this.puu0 = puu0; }
    public Integer getStomgtcod0() { return stomgtcod0; }
    public void setStomgtcod0(Integer stomgtcod0) { this.stomgtcod0 = stomgtcod0; }
    public Integer getLotmgtcod0() { return lotmgtcod0; }
    public void setLotmgtcod0(Integer lotmgtcod0) { this.lotmgtcod0 = lotmgtcod0; }
    public Integer getSermgtcod0() { return sermgtcod0; }
    public void setSermgtcod0(Integer sermgtcod0) { this.sermgtcod0 = sermgtcod0; }
    public BigDecimal getItmwei0() { return itmwei0; }
    public void setItmwei0(BigDecimal itmwei0) { this.itmwei0 = itmwei0; }
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
    public String getYdim0() { return ydim0; }
    public void setYdim0(String ydim0) { this.ydim0 = ydim0; }
    public String getYcoul0() { return ycoul0; }
    public void setYcoul0(String ycoul0) { this.ycoul0 = ycoul0; }
    public BigDecimal getXcout0() { return xcout0; }
    public void setXcout0(BigDecimal xcout0) { this.xcout0 = xcout0; }
    public BigDecimal getPcustucoe0() { return pcustucoe0; }
    public void setPcustucoe0(BigDecimal pcustucoe0) { this.pcustucoe0 = pcustucoe0; }
    public Integer getYfin0() { return yfin0; }
    public void setYfin0(Integer yfin0) { this.yfin0 = yfin0; }
    public String getYneance0() { return yneance0; }
    public void setYneance0(String yneance0) { this.yneance0 = yneance0; }
    public String getYneance1() { return yneance1; }
    public void setYneance1(String yneance1) { this.yneance1 = yneance1; }
    public String getYneance2() { return yneance2; }
    public void setYneance2(String yneance2) { this.yneance2 = yneance2; }
    public String getYcalibre0() { return ycalibre0; }
    public void setYcalibre0(String ycalibre0) { this.ycalibre0 = ycalibre0; }
    public String getYcalibre1() { return ycalibre1; }
    public void setYcalibre1(String ycalibre1) { this.ycalibre1 = ycalibre1; }
    public String getYcalibre2() { return ycalibre2; }
    public void setYcalibre2(String ycalibre2) { this.ycalibre2 = ycalibre2; }
    public Integer getYdelister0() { return ydelister0; }
    public void setYdelister0(Integer ydelister0) { this.ydelister0 = ydelister0; }
    public Integer getYnvcoll0() { return ynvcoll0; }
    public void setYnvcoll0(Integer ynvcoll0) { this.ynvcoll0 = ynvcoll0; }
}
