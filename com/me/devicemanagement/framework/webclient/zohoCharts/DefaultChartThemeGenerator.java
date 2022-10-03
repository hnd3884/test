package com.me.devicemanagement.framework.webclient.zohoCharts;

import java.util.Hashtable;
import org.json.simple.JSONArray;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class DefaultChartThemeGenerator
{
    private static Logger logger;
    private Properties zohoChartProps;
    
    public DefaultChartThemeGenerator() {
        this.zohoChartProps = ApiFactoryProvider.getZohoChartProps().getDefaultChartProperties();
    }
    
    public JSONObject constructChartData() {
        DefaultChartThemeGenerator.logger.log(Level.FINE, "In DefaultChartThemeGenerator-constructChartData");
        final JSONObject chartData = new JSONObject();
        chartData.put((Object)"canvas", (Object)this.constructCanvasData());
        final JSONObject chartObj = new JSONObject();
        final JSONObject toolTip = new JSONObject();
        chartObj.put((Object)"plot", (Object)this.constructPlotJSON());
        chartObj.put((Object)"marginTop", ((Hashtable<K, Object>)this.zohoChartProps).get("ChartMarginTop"));
        chartData.put((Object)"chart", (Object)chartObj);
        chartData.put((Object)"notes", (Object)this.constructNotesJSON());
        chartData.put((Object)"legend", (Object)this.constructLegendJSON());
        toolTip.put((Object)"backgroundColor", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipBackground"));
        toolTip.put((Object)"borderColor", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipBorder"));
        toolTip.put((Object)"shadow", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipShadow"));
        toolTip.put((Object)"fontColor", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipFontColour"));
        toolTip.put((Object)"fontSize", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipFontSize"));
        toolTip.put((Object)"lineHeight", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipLineHeight"));
        toolTip.put((Object)"opacity", ((Hashtable<K, Object>)this.zohoChartProps).get("ToolTipOpacity"));
        chartData.put((Object)"tooltip", (Object)toolTip);
        final JSONObject noDataHandler = new JSONObject();
        noDataHandler.put((Object)"htmlEl", ((Hashtable<K, Object>)this.zohoChartProps).get("nodataHandlerHTMLEL"));
        noDataHandler.put((Object)"x", ((Hashtable<K, Object>)this.zohoChartProps).get("nodataHandlerHTMLEL_X"));
        noDataHandler.put((Object)"y", ((Hashtable<K, Object>)this.zohoChartProps).get("nodataHandlerHTMLEL_Y"));
        chartData.put((Object)"noDataHandler", (Object)noDataHandler);
        DefaultChartThemeGenerator.logger.log(Level.FINE, "End of DefaultChartThemeGenerator-constructChartData", chartData);
        return chartData;
    }
    
    public JSONObject constructCanvasData() {
        DefaultChartThemeGenerator.logger.log(Level.FINE, "In DefaultChartThemeGenerator-constructCanvasData");
        final JSONObject canvasData = new JSONObject();
        final JSONObject titleData = new JSONObject();
        final JSONObject subtitleData = new JSONObject();
        final JSONObject shadowData = new JSONObject();
        final JSONObject borderData = new JSONObject();
        titleData.put((Object)"show", ((Hashtable<K, Object>)this.zohoChartProps).get("ChartTitleShow"));
        titleData.put((Object)"text", ((Hashtable<K, Object>)this.zohoChartProps).get("ChartTitleTxt"));
        subtitleData.put((Object)"show", ((Hashtable<K, Object>)this.zohoChartProps).get("ChartSubTitleShow"));
        shadowData.put((Object)"show", ((Hashtable<K, Object>)this.zohoChartProps).get("ChartShadowShow"));
        borderData.put((Object)"show", ((Hashtable<K, Object>)this.zohoChartProps).get("ChartBorderShow"));
        canvasData.put((Object)"title", (Object)titleData);
        canvasData.put((Object)"subtitle", (Object)subtitleData);
        canvasData.put((Object)"theme", ((Hashtable<K, Object>)this.zohoChartProps).get("ThemeName"));
        canvasData.put((Object)"shadow", (Object)shadowData);
        canvasData.put((Object)"border", (Object)borderData);
        DefaultChartThemeGenerator.logger.log(Level.FINE, "End of DefaultChartThemeGenerator-constructCanvasData", canvasData);
        return canvasData;
    }
    
    public JSONObject constructPlotJSON() {
        DefaultChartThemeGenerator.logger.log(Level.FINE, "In DefaultChartThemeGenerator-constructPlotJSON");
        final JSONObject plotJSON = new JSONObject();
        final JSONObject eventJSON = new JSONObject();
        final JSONObject plotOptions = new JSONObject();
        final JSONObject barOptions = new JSONObject();
        final JSONObject pieOptions = new JSONObject();
        JSONObject dataLabel = new JSONObject();
        dataLabel.put((Object)"show", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotLabelShow"));
        dataLabel.put((Object)"showAs", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotLabelShowAs"));
        dataLabel.put((Object)"labelPos", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotLabelPos"));
        barOptions.put((Object)"datalabels", (Object)dataLabel);
        barOptions.put((Object)"maxBandWidth", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotMaxBandwidth"));
        barOptions.put((Object)"multiColoring", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotMultiColouring"));
        dataLabel.put((Object)"fontColor", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotPieDataLabelColor"));
        pieOptions.put((Object)"datalabels", (Object)dataLabel);
        pieOptions.put((Object)"innerRadius", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotPieInnerRadius"));
        plotOptions.put((Object)"bar", (Object)barOptions);
        plotOptions.put((Object)"pie", (Object)pieOptions);
        plotJSON.put((Object)"plotoptions", (Object)plotOptions);
        eventJSON.put((Object)"click", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotClickFn"));
        eventJSON.put((Object)"doubleclick", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotDblClickFn"));
        plotJSON.put((Object)"events", (Object)eventJSON);
        dataLabel = new JSONObject();
        dataLabel.put((Object)"handleOverlapping", ((Hashtable<K, Object>)this.zohoChartProps).get("PlotHandleOverLap"));
        plotJSON.put((Object)"datalabels", (Object)dataLabel);
        DefaultChartThemeGenerator.logger.log(Level.FINE, "End of DefaultChartThemeGenerator-constructPlotJSON", plotJSON);
        return plotJSON;
    }
    
    public JSONObject constructNotesJSON() {
        DefaultChartThemeGenerator.logger.log(Level.FINE, "In DefaultChartThemeGenerator-constructNotesJSON");
        final JSONObject notesJSON = new JSONObject();
        final JSONArray chartValArr = new JSONArray();
        final JSONObject chartValue = new JSONObject();
        chartValue.put((Object)"x", ((Hashtable<K, Object>)this.zohoChartProps).get("NotesXPosFn"));
        chartValue.put((Object)"y", ((Hashtable<K, Object>)this.zohoChartProps).get("NotesYPosFn"));
        chartValue.put((Object)"type", ((Hashtable<K, Object>)this.zohoChartProps).get("NotesType"));
        chartValue.put((Object)"htmlEl", ((Hashtable<K, Object>)this.zohoChartProps).get("NotesHTMLFn"));
        chartValArr.add((Object)chartValue);
        notesJSON.put((Object)"enabled", ((Hashtable<K, Object>)this.zohoChartProps).get("NotesEnabled"));
        notesJSON.put((Object)"chartValues", (Object)chartValArr);
        DefaultChartThemeGenerator.logger.log(Level.FINE, "End of DefaultChartThemeGenerator-constructNotesJSON", notesJSON);
        return notesJSON;
    }
    
    public JSONObject constructLegendJSON() {
        DefaultChartThemeGenerator.logger.log(Level.FINE, "In DefaultChartThemeGenerator-constructLegendJSON");
        final JSONObject legend = new JSONObject();
        final JSONObject colourPallete = new JSONObject();
        final JSONObject options = new JSONObject();
        final JSONObject eventsObj = new JSONObject();
        options.put((Object)"multicolor", ((Hashtable<K, Object>)this.zohoChartProps).get("flat-ui-colors"));
        colourPallete.put((Object)"type", ((Hashtable<K, Object>)this.zohoChartProps).get("multicolor"));
        colourPallete.put((Object)"options", (Object)options);
        legend.put((Object)"colorPallete", (Object)colourPallete);
        legend.put((Object)"lineHeight", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendLineHeight"));
        legend.put((Object)"fontSize", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendFontSize"));
        legend.put((Object)"marginTop", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendMarginTop"));
        legend.put((Object)"marginRight", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendMarginRight"));
        eventsObj.put((Object)"mouseover", ((Hashtable<K, Object>)this.zohoChartProps).get("LegendMouseOver"));
        legend.put((Object)"events", (Object)eventsObj);
        DefaultChartThemeGenerator.logger.log(Level.FINE, "In DefaultChartThemeGenerator-constructLegendJSON", legend);
        return legend;
    }
    
    static {
        DefaultChartThemeGenerator.logger = Logger.getLogger(DefaultChartThemeGenerator.class.getName());
    }
}
