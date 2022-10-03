package com.me.devicemanagement.framework.webclient.zohoCharts;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import com.me.devicemanagement.framework.webclient.graphs.GraphEntry;
import com.adventnet.i18n.I18N;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import org.json.simple.JSONArray;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.graphs.data.GraphDataProducer;

public class ZohoChartJSONGeneratorImpl implements ZohoChartJSONGenerator
{
    protected String chartName;
    protected String xmlFileName;
    protected String generatorClass;
    protected String graphType;
    protected GraphDataProducer graphDataProducer;
    protected HashMap<String, String> parameterMap;
    protected JSONArray colourArray;
    protected Properties zohoChartProps;
    private static Logger logger;
    
    @Override
    public void initialiseZohoChartJSONGenerator(final Properties chartProps) {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-initialiseZohoChartJSONGenerator");
        try {
            final String chartName = ((Hashtable<K, String>)chartProps).get("chartName");
            final String xmlFileName = ((Hashtable<K, String>)chartProps).get("xmlFileName");
            final String generatorClass = ((Hashtable<K, String>)chartProps).get("generatorClass");
            String chartType = ((Hashtable<K, String>)chartProps).get("graphType");
            final String params = ((Hashtable<K, String>)chartProps).get("params");
            this.chartName = chartName;
            this.xmlFileName = xmlFileName;
            this.generatorClass = generatorClass;
            this.parameterMap = new HashMap<String, String>();
            this.colourArray = new JSONArray();
            if (chartType == null) {
                chartType = "bar";
            }
            this.graphType = chartType;
            this.graphDataProducer = (GraphDataProducer)Class.forName(generatorClass).newInstance();
            if (params != null && !params.equalsIgnoreCase("")) {
                for (final String parameter : params.split(";")) {
                    final String[] pair = parameter.split("=");
                    final String value = pair[1];
                    this.parameterMap.put(pair[0], value);
                }
            }
            this.zohoChartProps = ApiFactoryProvider.getZohoChartProps().getDefaultChartProperties();
        }
        catch (final Exception e) {
            ZohoChartJSONGeneratorImpl.logger.log(Level.SEVERE, "Exception in ZohoChartJSONGeneratorImpl-initialiseZohoChartJSONGenerator", e);
        }
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-initialiseZohoChartJSONGenerator");
    }
    
    @Override
    public JSONObject constructChartData() {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-constructShartData");
        final JSONObject chartData = new JSONObject();
        try {
            this.parameterMap.put("XML_NAME", this.xmlFileName);
            final HashMap<String, String> chartProps = this.graphDataProducer.getChartProps(this.chartName, this.parameterMap);
            final String xAxisLabelName = I18N.getMsg((String)chartProps.get("xaxislabel"), new Object[0]);
            final String yAxisLabelName = I18N.getMsg((String)chartProps.get("yaxislabel"), new Object[0]);
            chartData.put((Object)"canvas", (Object)this.constructCanvasData());
            chartData.put((Object)"seriesdata", (Object)this.constructSeriesData());
            chartData.put((Object)"metadata", (Object)this.constructMetaData(xAxisLabelName, yAxisLabelName));
            final JSONObject chartObj = new JSONObject();
            final JSONObject axesObj = new JSONObject();
            JSONObject eventObj = new JSONObject();
            final JSONObject xAxesObj = new JSONObject();
            JSONObject tickLabel = new JSONObject();
            final JSONObject yAxesObj = new JSONObject();
            final JSONObject xAxisLabel = new JSONObject();
            final JSONObject yAxisLabel = new JSONObject();
            final JSONArray yAxesArr = new JSONArray();
            final JSONObject gridObj = new JSONObject();
            xAxisLabel.put((Object)"text", (Object)xAxisLabelName);
            xAxisLabel.put((Object)"fontSize", ((Hashtable<K, Object>)this.zohoChartProps).get("xAxisLabelFontSize"));
            xAxisLabel.put((Object)"marginTop", ((Hashtable<K, Object>)this.zohoChartProps).get("xAxisLabelMarginTop"));
            eventObj.put((Object)"click", ((Hashtable<K, Object>)this.zohoChartProps).get("xAxisLabelClick"));
            eventObj.put((Object)"doubleclick", ((Hashtable<K, Object>)this.zohoChartProps).get("xAxisLabelClick"));
            yAxisLabel.put((Object)"text", (Object)yAxisLabelName);
            yAxisLabel.put((Object)"fontSize", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxisLabelFontSize"));
            yAxisLabel.put((Object)"marginRight", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxisLabelMarginRight"));
            xAxesObj.put((Object)"label", (Object)xAxisLabel);
            tickLabel.put((Object)"alignMode", ((Hashtable<K, Object>)this.zohoChartProps).get("xAxisRotation"));
            tickLabel.put((Object)"fontSize", ((Hashtable<K, Object>)this.zohoChartProps).get("TickLabelFontSize"));
            tickLabel.put((Object)"events", (Object)eventObj);
            xAxesObj.put((Object)"ticklabel", (Object)tickLabel);
            gridObj.put((Object)"color", ((Hashtable<K, Object>)this.zohoChartProps).get("yGridColor"));
            yAxesObj.put((Object)"grid", (Object)gridObj);
            yAxesObj.put((Object)"label", (Object)yAxisLabel);
            tickLabel = new JSONObject();
            eventObj = new JSONObject();
            eventObj.put((Object)"cursor", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxisLabelCursorType"));
            tickLabel.put((Object)"events", (Object)eventObj);
            yAxesObj.put((Object)"ticklabel", (Object)tickLabel);
            yAxesArr.add((Object)yAxesObj);
            axesObj.put((Object)"xaxis", (Object)xAxesObj);
            axesObj.put((Object)"yaxis", (Object)yAxesArr);
            chartObj.put((Object)"axes", (Object)axesObj);
            chartData.put((Object)"chart", (Object)chartObj);
            chartData.put((Object)"legend", (Object)this.constructLegendJSON(this.chartName));
        }
        catch (final Exception e) {
            ZohoChartJSONGeneratorImpl.logger.log(Level.SEVERE, "Exception in ZohoChartJSONGeneratorImpl-constructChartData", e);
        }
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructChartData", chartData);
        return chartData;
    }
    
    public JSONObject constructMetaData(final String xAxisLabel, final String yAxisLabel) {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-constructMetaData");
        final JSONObject metaData = new JSONObject();
        final JSONArray columns = new JSONArray();
        final JSONObject xAxisColumn = new JSONObject();
        final JSONObject yAxisColumn = new JSONObject();
        final JSONObject yAxisDataType = new JSONObject();
        final JSONObject yAxisDataFormat = new JSONObject();
        final JSONObject axes = new JSONObject();
        final JSONArray yAxesIndex = new JSONArray();
        final JSONArray xAxesIndex = new JSONArray();
        final JSONArray yAxesIndex2 = new JSONArray();
        final JSONArray toolTip = new JSONArray();
        yAxesIndex2.add((Object)1);
        yAxesIndex.add((Object)yAxesIndex2);
        xAxesIndex.add((Object)0);
        toolTip.add((Object)0);
        toolTip.add((Object)1);
        axes.put((Object)"x", (Object)xAxesIndex);
        axes.put((Object)"y", (Object)yAxesIndex);
        axes.put((Object)"tooltip", (Object)toolTip);
        xAxisColumn.put((Object)"dataindex", (Object)0);
        xAxisColumn.put((Object)"columnname", (Object)xAxisLabel);
        xAxisColumn.put((Object)"datatype", ((Hashtable<K, Object>)this.zohoChartProps).get("xAxisDataType"));
        yAxisColumn.put((Object)"dataindex", (Object)1);
        yAxisColumn.put((Object)"columnname", (Object)yAxisLabel);
        yAxisColumn.put((Object)"datatype", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxisDataType"));
        yAxisDataFormat.put((Object)"thousandSeperator", ((Hashtable<K, Object>)this.zohoChartProps).get("yDataTypeThousandSeparator"));
        yAxisDataFormat.put((Object)"decimalPlaces", ((Hashtable<K, Object>)this.zohoChartProps).get("yDataTypeDecimal"));
        yAxisDataFormat.put((Object)"signEnabled", ((Hashtable<K, Object>)this.zohoChartProps).get("yDataTypeSignEnabled"));
        yAxisDataFormat.put((Object)"prefix", ((Hashtable<K, Object>)this.zohoChartProps).get("yDataTypePrefix"));
        yAxisDataType.put((Object)"format", (Object)yAxisDataFormat);
        yAxisDataType.put((Object)"subfunction", ((Hashtable<K, Object>)this.zohoChartProps).get("subfunction"));
        yAxisColumn.put((Object)"numeric", (Object)yAxisDataType);
        columns.add((Object)xAxisColumn);
        columns.add((Object)yAxisColumn);
        metaData.put((Object)"columns", (Object)columns);
        metaData.put((Object)"axes", (Object)axes);
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructMetaData", metaData);
        return metaData;
    }
    
    public JSONObject constructSeriesData() {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-constructSeriesData");
        final JSONObject seriesData = new JSONObject();
        final JSONArray chartData = new JSONArray();
        final JSONArray yAxisColOrder = new JSONArray();
        final JSONArray data = new JSONArray();
        final JSONObject chartDataObj = new JSONObject();
        yAxisColOrder.add((Object)0);
        yAxisColOrder.add((Object)0);
        chartDataObj.put((Object)"type", (Object)this.graphType);
        chartDataObj.put((Object)"yaxiscolumnorder", (Object)yAxisColOrder);
        data.add((Object)this.constructChartValues());
        chartDataObj.put((Object)"data", (Object)data);
        chartData.add((Object)chartDataObj);
        seriesData.put((Object)"chartdata", (Object)chartData);
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructSeriesData", seriesData);
        return seriesData;
    }
    
    public JSONObject constructCanvasData() {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-constructCanvasData");
        final JSONObject canvasData = new JSONObject();
        canvasData.put((Object)"theme", ((Hashtable<K, Object>)this.zohoChartProps).get("ThemeName"));
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructCanvasData", canvasData);
        return canvasData;
    }
    
    public JSONArray constructChartValues() {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-constructChartValues");
        final JSONArray chartValueArray = new JSONArray();
        try {
            final LinkedList<GraphEntry> graphValues = this.graphDataProducer.getGraphColumnsWithValues(this.chartName, this.parameterMap);
            if (graphValues != null) {
                for (final GraphEntry graphEntry : graphValues) {
                    final JSONArray data = new JSONArray();
                    data.add((Object)graphEntry.getLabel());
                    data.add((Object)graphEntry.getValue());
                    data.add((Object)graphEntry.getActionLink());
                    chartValueArray.add((Object)data);
                    this.colourArray.add((Object)("#" + graphEntry.getColor()));
                }
            }
        }
        catch (final Exception e) {
            ZohoChartJSONGeneratorImpl.logger.log(Level.SEVERE, "Exception in ZohoChartJSONGeneratorImpl-constructChartValues", e);
        }
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructChartValues", chartValueArray);
        return chartValueArray;
    }
    
    public JSONObject constructLegendJSON(final String chartName) {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-constructLegendJSON");
        final JSONObject legend = new JSONObject();
        final JSONObject colourPallete = new JSONObject();
        final JSONObject options = new JSONObject();
        options.put((Object)"multicolor", ((Hashtable<K, Object>)this.zohoChartProps).get("flat-ui-colors"));
        colourPallete.put((Object)"type", ((Hashtable<K, Object>)this.zohoChartProps).get("multicolor"));
        colourPallete.put((Object)"options", (Object)options);
        legend.put((Object)"colorPallete", (Object)colourPallete);
        legend.put((Object)"colors", (Object)this.colourArray);
        legend.put((Object)"textPadding", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendPadding"));
        legend.put((Object)"fontSize", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendFontSize"));
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructChartValues", legend);
        return legend;
    }
    
    @Override
    public String getChartTitle() {
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "In ZohoChartJSONGeneratorImpl-getChartTitle");
        String title = "";
        try {
            title = I18N.getMsg("dc.som.graphTitle", new Object[0]);
            this.parameterMap.put("XML_NAME", this.xmlFileName);
            final HashMap chartProps = this.graphDataProducer.getChartProps(this.chartName, this.parameterMap);
            title = I18N.getMsg((String)chartProps.get("title"), new Object[0]);
        }
        catch (final Exception e) {
            ZohoChartJSONGeneratorImpl.logger.log(Level.SEVERE, "Exception in ZohoChartJSONGeneratorImpl-getChartTitle", e);
        }
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-getChartTitle", title);
        return title;
    }
    
    static {
        ZohoChartJSONGeneratorImpl.logger = Logger.getLogger(ZohoChartJSONGeneratorImpl.class.getName());
    }
}
