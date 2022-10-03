package com.me.devicemanagement.framework.webclient.alert;

public interface AlertsAPI
{
    boolean isAlertTechnicianSegmented(final Long p0);
    
    boolean specialHandlingRequired(final Long p0);
    
    boolean validateDynamicVariablesInAlerts(final Long p0, final String p1, final String p2) throws Exception;
}
