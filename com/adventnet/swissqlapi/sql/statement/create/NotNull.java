package com.adventnet.swissqlapi.sql.statement.create;

public class NotNull
{
    private String nullStatus;
    private String identity;
    private String start;
    private String with;
    private String increment;
    private String by;
    private String noForCache;
    private String cache;
    private String cacheSize;
    private String maxValueOrNoMaxValue;
    private String minValueOrNoMinValue;
    private String cycleOrNoCycle;
    private String orderOrNoOrder;
    private String cacheOrNoCache;
    
    public void setNullStatus(final String nullStatus) {
        this.nullStatus = nullStatus;
    }
    
    public void setIdentity(final String id) {
        this.identity = id;
    }
    
    public void setStart(final String start) {
        this.start = start;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setIncrement(final String increment) {
        this.increment = increment;
    }
    
    public void setBy(final String by) {
        this.by = by;
    }
    
    public void setNoForCache(final String noForCache) {
        this.noForCache = noForCache;
    }
    
    public void setCache(final String cache) {
        this.cache = cache;
    }
    
    public void setCacheSize(final String cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public void setMaxValueOrNoMaxValue(final String maxValueOrNoMaxValue) {
        this.maxValueOrNoMaxValue = maxValueOrNoMaxValue;
    }
    
    public void setMinValueOrNoMinValue(final String minValueOrNoMinValue) {
        this.minValueOrNoMinValue = minValueOrNoMinValue;
    }
    
    public void setCycleOrNoCycle(final String cycleOrNoCycle) {
        this.cycleOrNoCycle = cycleOrNoCycle;
    }
    
    public void setCacheOrNoCache(final String cacheOrNoCache) {
        this.cacheOrNoCache = cacheOrNoCache;
    }
    
    public void setOrderOrNoOrder(final String orderOrNoOrder) {
        this.orderOrNoOrder = orderOrNoOrder;
    }
    
    public String getIdentity() {
        return this.identity;
    }
    
    public String getNullStatus() {
        return this.nullStatus;
    }
    
    public String getStart() {
        return this.start;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public String getIncrement() {
        return this.increment;
    }
    
    public String getBy() {
        return this.by;
    }
    
    public String getNoForCache() {
        return this.noForCache;
    }
    
    public String getCache() {
        return this.cache;
    }
    
    public String getCacheSize() {
        return this.cacheSize;
    }
    
    public String getMaxValueOrNoMaxValue() {
        return this.maxValueOrNoMaxValue;
    }
    
    public String getMinValueOrNoMinValue() {
        return this.minValueOrNoMinValue;
    }
    
    public String getCycleOrNoCycle() {
        return this.cycleOrNoCycle;
    }
    
    public String getCacheOrNoCache() {
        return this.cacheOrNoCache;
    }
    
    public String getOrderOrNoOrder() {
        return this.orderOrNoOrder;
    }
}
