package com.me.devicemanagement.framework.webclient.graphs;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.graphs.util.GraphUtil;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.webclient.zohoCharts.DefaultChartThemeGenerator;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartJSONGenerator;
import java.util.Properties;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphBean
{
    String name;
    String title;
    String xaxisLabel;
    String yaxisLabel;
    String graphType;
    List graphColors;
    String encodedGraphData;
    String encodedUserTheme;
    LinkedList<GraphEntry> graphEntries;
    
    public LinkedList<GraphEntry> getGraphEntries() {
        return this.graphEntries;
    }
    
    public void setGraphColors(final ArrayList colors) {
        this.graphColors = colors;
    }
    
    public List getColorArroay() {
        final List<String> colorArray = new ArrayList<String>();
        for (final GraphEntry entry : this.graphEntries) {
            if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                final String colorValue = "\"#" + entry.getColor() + "\"";
                colorArray.add(colorValue);
            }
        }
        return this.graphColors = colorArray;
    }
    
    public void setGraphEntries(final LinkedList<GraphEntry> graphEntries) {
        this.graphEntries = graphEntries;
    }
    
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
    
    public String getEncodedUserTheme() {
        return this.encodedUserTheme;
    }
    
    public void setEncodedUserTheme(final String encodedUserTheme) {
        this.encodedUserTheme = encodedUserTheme;
    }
    
    public String getEncodedGraphData() {
        return this.encodedGraphData;
    }
    
    public void setEncodedGraphData(final String encodedGraphData) {
        this.encodedGraphData = encodedGraphData;
    }
    
    public void setGraphValues(final String name, final String xmlName, final String generatorClass, String chartGenerator, final String params) throws Exception {
        if (this.graphType == null || this.graphType.trim().equals("")) {
            this.graphType = "bar";
        }
        if (this.graphType.toLowerCase().contains("pie")) {
            this.graphType = "pie";
        }
        final Properties chartProps = new Properties();
        ((Hashtable<String, String>)chartProps).put("chartName", name);
        ((Hashtable<String, String>)chartProps).put("xmlFileName", xmlName);
        ((Hashtable<String, String>)chartProps).put("generatorClass", generatorClass);
        ((Hashtable<String, String>)chartProps).put("graphType", this.graphType);
        if (params != null && !params.equalsIgnoreCase("")) {
            ((Hashtable<String, String>)chartProps).put("params", params);
        }
        chartGenerator = ((chartGenerator == null || chartGenerator.trim().equals("")) ? "com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartJSONGeneratorImpl" : chartGenerator);
        final ZohoChartJSONGenerator zohoChartJSONGenerator = (ZohoChartJSONGenerator)Class.forName(chartGenerator).newInstance();
        zohoChartJSONGenerator.initialiseZohoChartJSONGenerator(chartProps);
        final JSONObject chartData = zohoChartJSONGenerator.constructChartData();
        this.encodedGraphData = Encoder.convertToNewBase(chartData.toString());
        this.title = ((this.title == null || this.title.trim().equals("")) ? zohoChartJSONGenerator.getChartTitle() : this.title);
        if (this.encodedUserTheme == null || this.encodedUserTheme.trim().equals("")) {
            final DefaultChartThemeGenerator defaultChartThemeGenerator = new DefaultChartThemeGenerator();
            final JSONObject chartTheme = defaultChartThemeGenerator.constructChartData();
            this.setEncodedUserTheme(Encoder.convertToNewBase(chartTheme.toString()));
        }
        this.setName(name);
    }
    
    public void setGraphValues(final String name, final String xmlName, final String generatorClass, final String params) throws Exception {
        GraphUtil.getInstance().populateGraphBean(this, name, xmlName, generatorClass, params);
    }
    
    @Override
    public String toString() {
        return "GraphBean{name=" + this.name + ", title=" + this.title + ", xaxisLabel=" + this.xaxisLabel + ", yaxisLabel=" + this.yaxisLabel + ", graphEntries=" + this.graphEntries + '}';
    }
}
