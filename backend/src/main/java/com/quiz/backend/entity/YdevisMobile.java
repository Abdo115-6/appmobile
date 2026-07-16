package com.quiz.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "YDEVISMOBILE")
public class YdevisMobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROWID")
    private Long rowid;

    @Column(name = "UPDTICK_0")
    private Integer updtick0;

    @Column(name = "YID_0", length = 10)
    private String yid0;

    @Column(name = "YNUM_0", length = 20)
    private String ynum0;

    @Column(name = "YCLIENT_0", length = 15)
    private String yclient0;

    @Column(name = "YSITE_0", length = 5)
    private String ysite0;

    @Column(name = "YARTICLE_0", length = 24)
    private String yarticle0;

    @Column(name = "YQTY_0")
    private BigDecimal yqty0;

    @Column(name = "YPRICE_0")
    private BigDecimal yprice0;

    @Column(name = "YCOEFF_0")
    private BigDecimal ycoeff0;

    @Column(name = "YCARTON_0")
    private Integer ycarton0;

    @Column(name = "YBPCNUM_0", length = 20)
    private String ybpcnum0;

    @Column(name = "YBPCNAM_0", length = 100)
    private String ybpcnam0;

    @Column(name = "YITMREF_0", length = 30)
    private String yitmref0;

    @Column(name = "YITMDES_0", length = 200)
    private String yitmdes0;

    @Column(name = "CREDATTIM_0")
    private LocalDateTime credattim0;

    @Column(name = "UPDDATTIM_0")
    private LocalDateTime upddattim0;

    @Column(name = "AUUID_0")
    private byte[] auuid0;

    @Column(name = "CREUSR_0", length = 20)
    private String creusr0;

    @Column(name = "UPDUSR_0", length = 20)
    private String updusr0;

    public YdevisMobile() {}

    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
    public Integer getUpdtick0() { return updtick0; }
    public void setUpdtick0(Integer updtick0) { this.updtick0 = updtick0; }
    public String getYid0() { return yid0; }
    public void setYid0(String yid0) { this.yid0 = yid0; }
    public String getYnum0() { return ynum0; }
    public void setYnum0(String ynum0) { this.ynum0 = ynum0; }
    public String getYclient0() { return yclient0; }
    public void setYclient0(String yclient0) { this.yclient0 = yclient0; }
    public String getYsite0() { return ysite0; }
    public void setYsite0(String ysite0) { this.ysite0 = ysite0; }
    public String getYarticle0() { return yarticle0; }
    public void setYarticle0(String yarticle0) { this.yarticle0 = yarticle0; }
    public BigDecimal getYqty0() { return yqty0; }
    public void setYqty0(BigDecimal yqty0) { this.yqty0 = yqty0; }
    public BigDecimal getYprice0() { return yprice0; }
    public void setYprice0(BigDecimal yprice0) { this.yprice0 = yprice0; }
    public BigDecimal getYcoeff0() { return ycoeff0; }
    public void setYcoeff0(BigDecimal ycoeff0) { this.ycoeff0 = ycoeff0; }
    public Integer getYcarton0() { return ycarton0; }
    public void setYcarton0(Integer ycarton0) { this.ycarton0 = ycarton0; }
    public String getYbpcnum0() { return ybpcnum0; }
    public void setYbpcnum0(String ybpcnum0) { this.ybpcnum0 = ybpcnum0; }
    public String getYbpcnam0() { return ybpcnam0; }
    public void setYbpcnam0(String ybpcnam0) { this.ybpcnam0 = ybpcnam0; }
    public String getYitmref0() { return yitmref0; }
    public void setYitmref0(String yitmref0) { this.yitmref0 = yitmref0; }
    public String getYitmdes0() { return yitmdes0; }
    public void setYitmdes0(String yitmdes0) { this.yitmdes0 = yitmdes0; }
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
