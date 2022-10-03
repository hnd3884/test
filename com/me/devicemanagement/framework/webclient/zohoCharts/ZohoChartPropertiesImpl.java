package com.me.devicemanagement.framework.webclient.zohoCharts;

import java.util.Hashtable;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.Properties;
import java.util.logging.Logger;

public class ZohoChartPropertiesImpl implements ZohoChartProperties
{
    private static Logger logger;
    
    @Override
    public Properties getDefaultChartProperties() {
        Properties chartProperties = new Properties();
        ((Hashtable<String, String>)chartProperties).put("ThemeName", "dmDefaulttheme");
        ((Hashtable<String, Integer>)chartProperties).put("ChartMarginTop", 30);
        ((Hashtable<String, String>)chartProperties).put("ChartTitleTxt", "");
        ((Hashtable<String, Boolean>)chartProperties).put("ChartTitleShow", Boolean.FALSE);
        ((Hashtable<String, Boolean>)chartProperties).put("ChartShadowShow", Boolean.FALSE);
        ((Hashtable<String, Boolean>)chartProperties).put("ChartBorderShow", Boolean.FALSE);
        ((Hashtable<String, Boolean>)chartProperties).put("ChartSubTitleShow", Boolean.FALSE);
        ((Hashtable<String, Integer>)chartProperties).put("LegendPadding", 5);
        ((Hashtable<String, Integer>)chartProperties).put("LegendFontSize", 12);
        ((Hashtable<String, Integer>)chartProperties).put("LegendXPos", 470);
        ((Hashtable<String, Integer>)chartProperties).put("LegendYPos", 50);
        ((Hashtable<String, Integer>)chartProperties).put("LegendMarginTop", 50);
        ((Hashtable<String, Integer>)chartProperties).put("LegendMarginRight", 20);
        ((Hashtable<String, Integer>)chartProperties).put("LegendLineHeight", 17);
        ((Hashtable<String, String>)chartProperties).put("LegendMouseOver", "legendMouseOver");
        ((Hashtable<String, String>)chartProperties).put("multicolor", "multicolor");
        ((Hashtable<String, String>)chartProperties).put("flat-ui-colors", "flat-ui-colors");
        ((Hashtable<String, String>)chartProperties).put("yGridColor", "#F5F5F5");
        ((Hashtable<String, String>)chartProperties).put("xAxisDataType", "ordinal");
        ((Hashtable<String, String>)chartProperties).put("yAxisDataType", "numeric");
        ((Hashtable<String, String>)chartProperties).put("subfunction", "integer");
        ((Hashtable<String, String>)chartProperties).put("xAxisLabelClick", "callTickLabelOnClick");
        ((Hashtable<String, String>)chartProperties).put("xAxisRotation", "auto");
        ((Hashtable<String, Integer>)chartProperties).put("xAxisLabelFontSize", 12);
        ((Hashtable<String, Integer>)chartProperties).put("xAxisLabelMarginTop", 10);
        ((Hashtable<String, Integer>)chartProperties).put("yAxisLabelFontSize", 12);
        ((Hashtable<String, Integer>)chartProperties).put("yAxisLabelMarginRight", 10);
        ((Hashtable<String, String>)chartProperties).put("yAxisLabelCursorType", "default");
        ((Hashtable<String, Integer>)chartProperties).put("TickLabelFontSize", 11);
        ((Hashtable<String, String>)chartProperties).put("yDataTypeThousandSeparator", ",");
        ((Hashtable<String, Integer>)chartProperties).put("yDataTypeDecimal", 0);
        ((Hashtable<String, Boolean>)chartProperties).put("yDataTypeSignEnabled", Boolean.FALSE);
        ((Hashtable<String, String>)chartProperties).put("yDataTypePrefix", "");
        ((Hashtable<String, String>)chartProperties).put("ToolTipBackground", "WHITE");
        ((Hashtable<String, String>)chartProperties).put("ToolTipBorder", "#CCCCCC");
        ((Hashtable<String, String>)chartProperties).put("ToolTipShadow", "0px 0px 4px rgba(0,0,0,0.4)");
        ((Hashtable<String, String>)chartProperties).put("ToolTipFontColour", "BLACK");
        ((Hashtable<String, Integer>)chartProperties).put("ToolTipFontSize", 12);
        ((Hashtable<String, String>)chartProperties).put("ToolTipLineHeight", "20");
        ((Hashtable<String, Double>)chartProperties).put("ToolTipOpacity", 0.9);
        ((Hashtable<String, String>)chartProperties).put("NotesType", "customNote");
        ((Hashtable<String, Boolean>)chartProperties).put("NotesEnabled", Boolean.TRUE);
        ((Hashtable<String, String>)chartProperties).put("NotesXPosFn", "pieTotalValXPos");
        ((Hashtable<String, String>)chartProperties).put("NotesYPosFn", "pieTotalValYPos");
        ((Hashtable<String, String>)chartProperties).put("NotesHTMLFn", "pieTotalValHTML");
        ((Hashtable<String, Boolean>)chartProperties).put("PlotLabelShow", Boolean.TRUE);
        ((Hashtable<String, String>)chartProperties).put("PlotLabelShowAs", "y");
        ((Hashtable<String, String>)chartProperties).put("PlotLabelPos", "top");
        ((Hashtable<String, String>)chartProperties).put("PlotMaxBandwidth", "40");
        ((Hashtable<String, Boolean>)chartProperties).put("PlotMultiColouring", Boolean.TRUE);
        ((Hashtable<String, Boolean>)chartProperties).put("PlotHandleOverLap", Boolean.TRUE);
        ((Hashtable<String, String>)chartProperties).put("PlotPieDataLabelColor", "#000000");
        ((Hashtable<String, String>)chartProperties).put("PlotPieInnerRadius", "50%");
        ((Hashtable<String, String>)chartProperties).put("PlotClickFn", "callChartOnclickEvent");
        ((Hashtable<String, String>)chartProperties).put("PlotDblClickFn", "callChartOnclickEvent");
        String noDataAvailable = "No Data Available";
        try {
            noDataAvailable = I18N.getMsg("dc.common.NO_DATA_AVAILABLE", new Object[0]);
        }
        catch (final Exception e) {
            ZohoChartPropertiesImpl.logger.log(Level.SEVERE, "Exeception while fetching No data available I18N value", e);
        }
        ((Hashtable<String, String>)chartProperties).put("nodataHandlerHTMLEL", "<div><img src='/images/chartNodataImg.png' style='width:500px;height:240px;'><span class='bodytextgreynew nodataText' >" + noDataAvailable + "</span></div>");
        ((Hashtable<String, Integer>)chartProperties).put("nodataHandlerHTMLEL_X", 70);
        ((Hashtable<String, Integer>)chartProperties).put("nodataHandlerHTMLEL_Y", 0);
        chartProperties = this.getCustomisedProperties(chartProperties);
        return chartProperties;
    }
    
    @Override
    public Properties getCustomisedProperties(final Properties chartProperties) {
        return chartProperties;
    }
    
    static {
        ZohoChartPropertiesImpl.logger = Logger.getLogger(ZohoChartPropertiesImpl.class.getName());
    }
}
