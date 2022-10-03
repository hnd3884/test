package com.me.mdm.api.reports.integ;

import com.me.devicemanagement.framework.webclient.reports.ReportBIDataValueTransformer;

public class MDMBIDataValueTransformer implements ReportBIDataValueTransformer
{
    private static final String BATTERY_LEVEL = "Battery Level";
    
    public Object transformValue(final String columnName, Object dataValue) {
        if (columnName.equalsIgnoreCase("Battery Level") && (float)dataValue == -1.0) {
            dataValue = "Error fetching data";
        }
        return dataValue;
    }
}
