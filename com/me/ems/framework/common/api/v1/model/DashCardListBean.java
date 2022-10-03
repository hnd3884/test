package com.me.ems.framework.common.api.v1.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashCardBean;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("dashboard")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashCardListBean
{
    private String dashboardName;
    private Object meta;
    @JsonProperty("cards")
    private List<DashCardBean> cardBeans;
    
    public DashCardListBean() {
    }
    
    public DashCardListBean(final String dashboardName, final List<DashCardBean> cardBeans, final Object meta) {
        this.dashboardName = dashboardName;
        this.cardBeans = cardBeans;
        this.meta = meta;
    }
    
    public String getDashboardName() {
        return this.dashboardName;
    }
    
    public void setDashboardName(final String dashboardName) {
        this.dashboardName = dashboardName;
    }
    
    public List<DashCardBean> getCardBeans() {
        return this.cardBeans;
    }
    
    @JsonDeserialize(as = List.class)
    public void setCardBeans(final List<DashCardBean> cardBeans) {
        this.cardBeans = cardBeans;
    }
    
    public Object getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Object meta) {
        this.meta = meta;
    }
}
