package com.me.mdm.webclient.zohoCharts;

import java.util.Hashtable;
import com.adventnet.i18n.I18N;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.webclient.graphs.data.GraphDataProducer;
import org.json.simple.JSONArray;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartJSONGenerator;

public class ZohoChartJSONGeneratorImpl extends com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartJSONGeneratorImpl implements ZohoChartJSONGenerator
{
    private static Logger logger;
    
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
            this.parameterMap = new HashMap();
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
            final ZohoChartPropertiesImpl chartPropObj = new ZohoChartPropertiesImpl();
            this.zohoChartProps = chartPropObj.getDefaultChartProperties();
        }
        catch (final Exception e) {
            ZohoChartJSONGeneratorImpl.logger.log(Level.SEVERE, "Exception in ZohoChartJSONGeneratorImpl-initialiseZohoChartJSONGenerator", e);
        }
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-initialiseZohoChartJSONGenerator");
    }
    
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
            final JSONObject pieInnerLabelObj = new JSONObject();
            final JSONObject dataLabelObj = new JSONObject();
            final JSONObject pieObj = new JSONObject();
            final JSONObject barObj = new JSONObject();
            final JSONObject plotOptionObj = new JSONObject();
            final JSONObject plotObj = new JSONObject();
            final JSONObject yGridObj = new JSONObject();
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
            yAxesObj.put((Object)"label", (Object)yAxisLabel);
            yGridObj.put((Object)"color", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxesGridColor"));
            yGridObj.put((Object)"strokeWidth", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxesGridStrokeWidth"));
            yAxesObj.put((Object)"grid", (Object)yGridObj);
            tickLabel = new JSONObject();
            eventObj = new JSONObject();
            eventObj.put((Object)"cursor", ((Hashtable<K, Object>)this.zohoChartProps).get("yAxisLabelCursorType"));
            tickLabel.put((Object)"events", (Object)eventObj);
            yAxesObj.put((Object)"ticklabel", (Object)tickLabel);
            yAxesArr.add((Object)yAxesObj);
            axesObj.put((Object)"xaxis", (Object)xAxesObj);
            axesObj.put((Object)"yaxis", (Object)yAxesArr);
            chartObj.put((Object)"axes", (Object)axesObj);
            pieInnerLabelObj.put((Object)"show", ((Hashtable<K, Object>)this.zohoChartProps).get("plotPieInnerLableShow"));
            pieInnerLabelObj.put((Object)"showAs", ((Hashtable<K, Object>)this.zohoChartProps).get("plotPieInnerLableShowAs"));
            dataLabelObj.put((Object)"type", ((Hashtable<K, Object>)this.zohoChartProps).get("plotPieDataLableType"));
            dataLabelObj.put((Object)"innerLabel", (Object)pieInnerLabelObj);
            dataLabelObj.put((Object)"showAs", ((Hashtable<K, Object>)this.zohoChartProps).get("plotPieDataLableShowAs"));
            pieObj.put((Object)"datalabels", (Object)dataLabelObj);
            pieObj.put((Object)"maxRadius", ((Hashtable<K, Object>)this.zohoChartProps).get("plotPieMaxRadius"));
            pieObj.put((Object)"innerRadius", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotPieInnerRadius"));
            barObj.put((Object)"maxBandWidth", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotMaxBandwidth"));
            plotOptionObj.put((Object)"bar", (Object)barObj);
            plotOptionObj.put((Object)"pie", (Object)pieObj);
            plotObj.put((Object)"plotoptions", (Object)plotOptionObj);
            chartObj.put((Object)"plot", (Object)plotObj);
            chartData.put((Object)"chart", (Object)chartObj);
            chartData.put((Object)"legend", (Object)this.constructLegendJSON(this.chartName));
        }
        catch (final Exception e) {
            ZohoChartJSONGeneratorImpl.logger.log(Level.SEVERE, "Exception in ZohoChartJSONGeneratorImpl-constructChartData", e);
        }
        ZohoChartJSONGeneratorImpl.logger.log(Level.FINE, "End of ZohoChartJSONGeneratorImpl-constructChartData", chartData);
        return chartData;
    }
    
    static {
        ZohoChartJSONGeneratorImpl.logger = Logger.getLogger(ZohoChartJSONGeneratorImpl.class.getName());
    }
}
