package com.me.mdm.webclient.zohoCharts;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartProperties;

public class ZohoChartPropertiesImpl extends com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartPropertiesImpl implements ZohoChartProperties
{
    public Properties getCustomisedProperties(final Properties chartProperties) {
        ((Hashtable<String, String>)chartProperties).put("PlotMaxBandwidth", "35");
        ((Hashtable<String, String>)chartProperties).put("PlotPieInnerRadius", "55%");
        ((Hashtable<String, String>)chartProperties).put("yAxesGridColor", "#F5F5F5");
        ((Hashtable<String, Integer>)chartProperties).put("yAxesGridStrokeWidth", 1);
        ((Hashtable<String, String>)chartProperties).put("plotPieInnerLableShow", "true");
        ((Hashtable<String, String>)chartProperties).put("plotPieInnerLableShowAs", "y");
        ((Hashtable<String, String>)chartProperties).put("plotPieDataLableType", "doubleside");
        ((Hashtable<String, String>)chartProperties).put("plotPieDataLableShowAs", "percent");
        ((Hashtable<String, String>)chartProperties).put("plotPieMaxRadius", "90");
        return chartProperties;
    }
}
