package com.quiz.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "SPRICLIST")
public class SprcList {

    @Id
    @Column(name = "ROWID")
    private Long rowid;

    @Column(name = "ITMREF_0")
    private String itmref0;

    @Column(name = "PLI_0")
    private String pli0;

    @Column(name = "PLICRD_0")
    private String plicrd0;

    @Column(name = "PRI_0")
    private BigDecimal pri0;

    public SprcList() {}

    public Long getRowid() { return rowid; }
    public void setRowid(Long rowid) { this.rowid = rowid; }
    public String getItmref0() { return itmref0; }
    public void setItmref0(String itmref0) { this.itmref0 = itmref0; }
    public String getPli0() { return pli0; }
    public void setPli0(String pli0) { this.pli0 = pli0; }
    public String getPlicrd0() { return plicrd0; }
    public void setPlicrd0(String plicrd0) { this.plicrd0 = plicrd0; }
    public BigDecimal getPri0() { return pri0; }
    public void setPri0(BigDecimal pri0) { this.pri0 = pri0; }
}
