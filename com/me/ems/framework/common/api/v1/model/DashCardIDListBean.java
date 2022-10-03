package com.me.ems.framework.common.api.v1.model;

import java.util.ArrayList;
import java.util.List;

public class DashCardIDListBean
{
    private String dashboardName;
    private Long dashboardId;
    private List<Long> cardIds;
    
    public DashCardIDListBean() {
        this.dashboardId = -1L;
        this.dashboardName = "";
        this.cardIds = new ArrayList<Long>();
    }
    
    public DashCardIDListBean(final Long dashboardId, final String dashboardName, final ArrayList<Long> cardIds) {
        this.dashboardId = dashboardId;
        this.dashboardName = dashboardName;
        this.cardIds = cardIds;
    }
    
    public String getDashboardName() {
        return this.dashboardName;
    }
    
    public void setDashboardName(final String dashboardName) {
        this.dashboardName = dashboardName;
    }
    
    public Long getDashboardId() {
        return this.dashboardId;
    }
    
    public void setDashboardId(final Long dashboardId) {
        this.dashboardId = dashboardId;
    }
    
    public List<Long> getCardIds() {
        return this.cardIds;
    }
    
    public void setCardIds(final List<Long> cardIds) {
        this.cardIds = cardIds;
    }
}
