package com.me.emsalerts.common.tracking;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class AlertsTrackingParamUtil
{
    static Logger alertsLogger;
    
    private static void addAlertsTrackingParams(final String paramName, final String paramValue, final DataObject trackingParamsDO) {
        try {
            final Row paramRow = new Row("AlertsTrackingParams");
            paramRow.set("PARAM_NAME", (Object)paramName);
            paramRow.set("PARAM_VALUE", (Object)paramValue);
            paramRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            trackingParamsDO.addRow(paramRow);
            SyMUtil.getPersistence().add(trackingParamsDO);
        }
        catch (final Exception e) {
            AlertsTrackingParamUtil.alertsLogger.log(Level.WARNING, "Exception occured while adding row in AlertsTrackingParams ", e);
        }
    }
    
    private static void updateAlertsTrackingParams(final String paramName, final String paramValue, final DataObject trackingParamsDO) {
        try {
            final Row paramRow = trackingParamsDO.getFirstRow("AlertsTrackingParams");
            paramRow.set("PARAM_VALUE", (Object)paramValue);
            paramRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            trackingParamsDO.updateRow(paramRow);
            SyMUtil.getPersistence().update(trackingParamsDO);
        }
        catch (final Exception e) {
            AlertsTrackingParamUtil.alertsLogger.log(Level.WARNING, "Exception occured while updating row in AlertsTrackingParam ", e);
        }
    }
    
    public static void incrementTrackingParam(final String paramName) {
        final int incrementBy = 1;
        try {
            final Criteria paramCriteria = new Criteria(Column.getColumn("AlertsTrackingParams", "PARAM_NAME"), (Object)paramName, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AlertsTrackingParams", paramCriteria);
            if (dataObject.isEmpty()) {
                addAlertsTrackingParams(paramName, String.valueOf(incrementBy), dataObject);
            }
            else {
                String paramValue = String.valueOf(dataObject.getFirstRow("AlertsTrackingParams").get("PARAM_VALUE"));
                try {
                    paramValue = String.valueOf(Integer.parseInt(paramValue) + incrementBy);
                }
                catch (final Exception e) {
                    paramValue = String.valueOf(incrementBy);
                }
                updateAlertsTrackingParams(paramName, paramValue, dataObject);
            }
        }
        catch (final Exception e2) {
            AlertsTrackingParamUtil.alertsLogger.log(Level.WARNING, "Exception occured while incrementing the AlertsTrackingParam Value ", e2);
        }
    }
    
    public static void addOrUpdateTrackingParam(final String paramName, final String paramValue) {
        try {
            final Criteria paramCriteria = new Criteria(Column.getColumn("AlertsTrackingParams", "PARAM_NAME"), (Object)paramName, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AlertsTrackingParams", paramCriteria);
            if (dataObject.isEmpty()) {
                addAlertsTrackingParams(paramName, paramValue, dataObject);
            }
            else {
                updateAlertsTrackingParams(paramName, paramValue, dataObject);
            }
        }
        catch (final Exception e) {
            AlertsTrackingParamUtil.alertsLogger.log(Level.WARNING, "Exception occured in addOrUpdateAlertsTrackingParams ", e);
        }
    }
    
    public static String getAlertsTrackingParam(final String paramName) {
        String paramValue = "";
        try {
            final Criteria paramCriteria = new Criteria(Column.getColumn("AlertsTrackingParams", "PARAM_NAME"), (Object)paramName, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AlertsTrackingParams", paramCriteria);
            if (!dataObject.isEmpty()) {
                final Row alertsTrackingParamsRow = dataObject.getFirstRow("AlertsTrackingParams");
                paramValue = String.valueOf(alertsTrackingParamsRow.get("PARAM_VALUE"));
            }
        }
        catch (final Exception e) {
            AlertsTrackingParamUtil.alertsLogger.log(Level.WARNING, "Exception while retrieving the alertsTrackingParam ", e);
        }
        return paramValue;
    }
    
    static {
        AlertsTrackingParamUtil.alertsLogger = Logger.getLogger("EMSAlertsLogger");
    }
}
