package com.me.devicemanagement.framework.webclient.zohoCharts;

import org.json.simple.JSONObject;
import java.util.Properties;

public interface ZohoChartJSONGenerator
{
    void initialiseZohoChartJSONGenerator(final Properties p0);
    
    JSONObject constructChartData();
    
    String getChartTitle();
}
