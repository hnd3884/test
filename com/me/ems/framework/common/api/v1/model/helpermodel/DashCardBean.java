package com.me.ems.framework.common.api.v1.model.helpermodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashCardBean
{
    private Integer displayOrder;
    private String cardTitle;
    private Object cardData;
    private String cardType;
    private Object meta;
    private String dashboardId;
    private Long cardId;
    private Integer minHeight;
    private Integer minWidth;
    private Integer maxHeight;
    private Integer maxWidth;
    private Boolean lock;
    private Boolean noMove;
    private Boolean noResize;
    private Integer height;
    private Integer width;
    private Integer xPos;
    private Integer yPos;
    private String viewName;
    
    public String getViewName() {
        return this.viewName;
    }
    
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }
    
    public Integer getHeight() {
        return this.height;
    }
    
    public void setHeight(final Integer height) {
        this.height = height;
    }
    
    public Integer getWidth() {
        return this.width;
    }
    
    public void setWidth(final Integer width) {
        this.width = width;
    }
    
    public Integer getxPos() {
        return this.xPos;
    }
    
    public void setxPos(final Integer xPos) {
        this.xPos = xPos;
    }
    
    public Integer getyPos() {
        return this.yPos;
    }
    
    public void setyPos(final Integer yPos) {
        this.yPos = yPos;
    }
    
    public Integer getMinHeight() {
        return this.minHeight;
    }
    
    public void setMinHeight(final Integer minHeight) {
        this.minHeight = minHeight;
    }
    
    public Integer getMinWidth() {
        return this.minWidth;
    }
    
    public void setMinWidth(final Integer minWidth) {
        this.minWidth = minWidth;
    }
    
    public Integer getMaxHeight() {
        return this.maxHeight;
    }
    
    public void setMaxHeight(final Integer maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    public Integer getMaxWidth() {
        return this.maxWidth;
    }
    
    public void setMaxWidth(final Integer maxWidth) {
        this.maxWidth = maxWidth;
    }
    
    public Boolean getLock() {
        return this.lock;
    }
    
    public void setLock(final Boolean lock) {
        this.lock = lock;
    }
    
    public Boolean getNoMove() {
        return this.noMove;
    }
    
    public void setNoMove(final Boolean noMove) {
        this.noMove = noMove;
    }
    
    public Boolean getNoResize() {
        return this.noResize;
    }
    
    public void setNoResize(final Boolean noResize) {
        this.noResize = noResize;
    }
    
    public Long getCardId() {
        return this.cardId;
    }
    
    public void setCardId(final Long cardId) {
        this.cardId = cardId;
    }
    
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }
    
    public void setDisplayOrder(final Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public String getCardTitle() {
        return this.cardTitle;
    }
    
    public void setCardTitle(final String cardTitle) {
        this.cardTitle = cardTitle;
    }
    
    public Object getCardData() {
        return this.cardData;
    }
    
    public void setCardData(final Object cardData) {
        this.cardData = cardData;
    }
    
    public String getCardType() {
        return this.cardType;
    }
    
    public void setCardType(final String cardType) {
        this.cardType = cardType;
    }
    
    public String getDashboardId() {
        return this.dashboardId;
    }
    
    public void setDashboardId(final String dashboardId) {
        this.dashboardId = dashboardId;
    }
    
    public Object getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Object meta) {
        this.meta = meta;
    }
}
