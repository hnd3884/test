package com.me.ems.framework.common.api.v1.model;

public class Graph
{
    private String name;
    private String title;
    private String xaxisLabel;
    private String yaxisLabel;
    private String graphType;
    private String encodedGraphData;
    private String encodedUserTheme;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getXaxisLabel() {
        return this.xaxisLabel;
    }
    
    public void setXaxisLabel(final String xaxisLabel) {
        this.xaxisLabel = xaxisLabel;
    }
    
    public String getYaxisLabel() {
        return this.yaxisLabel;
    }
    
    public void setYaxisLabel(final String yaxisLabel) {
        this.yaxisLabel = yaxisLabel;
    }
    
    public String getGraphType() {
        return this.graphType;
    }
    
    public void setGraphType(final String graphType) {
        this.graphType = graphType;
    }
    
    public String getEncodedGraphData() {
        return this.encodedGraphData;
    }
    
    public void setEncodedGraphData(final String encodedGraphData) {
        this.encodedGraphData = encodedGraphData;
    }
    
    public String getEncodedUserTheme() {
        return this.encodedUserTheme;
    }
    
    public void setEncodedUserTheme(final String encodedUserTheme) {
        this.encodedUserTheme = encodedUserTheme;
    }
}
