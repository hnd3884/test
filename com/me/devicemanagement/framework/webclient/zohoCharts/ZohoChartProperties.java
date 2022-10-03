package com.me.devicemanagement.framework.webclient.zohoCharts;

import java.util.Properties;

public interface ZohoChartProperties
{
    Properties getDefaultChartProperties();
    
    Properties getCustomisedProperties(final Properties p0);
}
