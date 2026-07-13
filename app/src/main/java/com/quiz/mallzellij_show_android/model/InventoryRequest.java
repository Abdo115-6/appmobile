package com.quiz.mallzellij_show_android.model;

import java.math.BigDecimal;

public class InventoryRequest {
    private String ynum0;
    private String ydepot0;
    private String yequipe0;
    private String yzone0;
    private String yitmref0;
    private BigDecimal yqtyplt0;
    private BigDecimal yqtycrt0;
    private BigDecimal yqtymtr0;
    private String creusr0;

    public InventoryRequest(String ynum0, String ydepot0, String yequipe0, String yzone0,
                            String yitmref0, BigDecimal yqtyplt0, BigDecimal yqtycrt0,
                            BigDecimal yqtymtr0, String creusr0) {
        this.ynum0 = ynum0;
        this.ydepot0 = ydepot0;
        this.yequipe0 = yequipe0;
        this.yzone0 = yzone0;
        this.yitmref0 = yitmref0;
        this.yqtyplt0 = yqtyplt0;
        this.yqtycrt0 = yqtycrt0;
        this.yqtymtr0 = yqtymtr0;
        this.creusr0 = creusr0;
    }
}
