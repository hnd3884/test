package com.me.devicemanagement.framework.server.license;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;

public class LicensePercentHandler
{
    private static LicensePercentHandler licensePercentHandler;
    
    public static LicensePercentHandler getInstance() {
        if (LicensePercentHandler.licensePercentHandler == null) {
            LicensePercentHandler.licensePercentHandler = new LicensePercentHandler();
        }
        return LicensePercentHandler.licensePercentHandler;
    }
    
    public void updateDeviceLicPercent(final long customerId, final Long alertType, final int percent, final Long bitWiseValue) {
        try {
            final Criteria criAlertType = new Criteria(Column.getColumn("LicensePercentAlert", "ALERT_TYPE_ID"), (Object)alertType, 0);
            final Criteria criCustomer = new Criteria(Column.getColumn("LicensePercentAlert", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cribitWise = new Criteria(Column.getColumn("LicensePercentAlert", "BITWISE_VALUE"), (Object)bitWiseValue, 0);
            final Criteria cri = cribitWise.and(criAlertType.and(criCustomer));
            final DataObject dObj = SyMUtil.getPersistence().get("LicensePercentAlert", cri);
            if (dObj.isEmpty()) {
                final Row row = new Row("LicensePercentAlert");
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("ALERT_TYPE_ID", (Object)alertType);
                row.set("LIC_PERCENTAGE", (Object)percent);
                row.set("BITWISE_VALUE", (Object)bitWiseValue);
                row.set("LAST_SENT_TIME", (Object)0L);
                dObj.addRow(row);
                SyMUtil.getPersistence().add(dObj);
            }
            else {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("LicensePercentAlert");
                updateQuery.setUpdateColumn("LIC_PERCENTAGE", (Object)percent);
                updateQuery.setUpdateColumn("LAST_SENT_TIME", (Object)0L);
                final Criteria criPercent = new Criteria(Column.getColumn("LicensePercentAlert", "LIC_PERCENTAGE"), (Object)percent, 1);
                updateQuery.setCriteria(cri.and(criPercent));
                SyMUtil.getPersistence().update(updateQuery);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(LicensePercentHandler.class.getName()).log(Level.SEVERE, "Exception occurred at -updateDeviceLicPercent  ", ex);
        }
    }
    
    public int getDeviceLicPercent(final Long customerId, final Long alertType, final Long bitWiseValue) {
        int alertpercent = -1;
        try {
            final Criteria criAlertType = new Criteria(Column.getColumn("LicensePercentAlert", "ALERT_TYPE_ID"), (Object)alertType, 0);
            final Criteria criCustomer = new Criteria(Column.getColumn("LicensePercentAlert", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria criProduct = new Criteria(Column.getColumn("LicensePercentAlert", "BITWISE_VALUE"), (Object)bitWiseValue, 0);
            final Criteria cri = criAlertType.and(criCustomer.and(criProduct));
            final DataObject dObj = SyMUtil.getPersistence().get("LicensePercentAlert", cri);
            if (!dObj.isEmpty()) {
                alertpercent = (int)dObj.getRow("LicensePercentAlert").get("LIC_PERCENTAGE");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(LicensePercentHandler.class.getName()).log(Level.SEVERE, "Exception occurred at -getDeviceLicPercent", ex);
        }
        return alertpercent;
    }
    
    public boolean getDeviceEmailSendStatus(final long customerId, final Long alertType, final Long bitWiseValue) {
        try {
            final Criteria criAlertType = new Criteria(Column.getColumn("LicensePercentAlert", "ALERT_TYPE_ID"), (Object)alertType, 0);
            final Criteria criCustomer = new Criteria(Column.getColumn("LicensePercentAlert", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria criProduct = new Criteria(Column.getColumn("LicensePercentAlert", "BITWISE_VALUE"), (Object)bitWiseValue, 0);
            final Criteria cri = criAlertType.and(criCustomer.and(criProduct));
            final DataObject dObj = SyMUtil.getPersistence().get("LicensePercentAlert", cri);
            if (!dObj.isEmpty()) {
                final long lastemailtime = (long)dObj.getRow("LicensePercentAlert").get("LAST_SENT_TIME");
                final long diffinmillis = System.currentTimeMillis() - lastemailtime;
                final long days = TimeUnit.DAYS.convert(diffinmillis, TimeUnit.MILLISECONDS);
                if (days > 0L) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(LicensePercentHandler.class.getName()).log(Level.SEVERE, "Exception occurred at -getDeviceEmailStatus", ex);
        }
        return false;
    }
    
    public void updateDeviceEmailSentTime(final long customerId, final Long alertType, final Long bitWiseValue) {
        try {
            final Criteria criAlertType = new Criteria(Column.getColumn("LicensePercentAlert", "ALERT_TYPE_ID"), (Object)alertType, 0);
            final Criteria criCustomer = new Criteria(Column.getColumn("LicensePercentAlert", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria criProduct = new Criteria(Column.getColumn("LicensePercentAlert", "BITWISE_VALUE"), (Object)bitWiseValue, 0);
            final Criteria cri = criAlertType.and(criCustomer.and(criProduct));
            final DataObject dObj = SyMUtil.getPersistence().get("LicensePercentAlert", cri);
            if (!dObj.isEmpty()) {
                final Calendar calendar = Calendar.getInstance();
                calendar.clear(11);
                calendar.clear(9);
                calendar.clear(10);
                calendar.clear(12);
                calendar.clear(13);
                calendar.clear(14);
                final long timeinmillis = calendar.getTimeInMillis();
                final Row row = dObj.getRow("LicensePercentAlert");
                row.set("LAST_SENT_TIME", (Object)timeinmillis);
                dObj.updateRow(row);
                SyMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(LicensePercentHandler.class.getName()).log(Level.SEVERE, "Exception occurred at -updateDeviceEmailSentTime", ex);
        }
    }
    
    public void deleteDeviceLicPercent(final Long customerId, final Long alertType) {
        try {
            SyMUtil.getPersistence().delete(new Criteria(Column.getColumn("LicensePercentAlert", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(Column.getColumn("LicensePercentAlert", "ALERT_TYPE_ID"), (Object)alertType, 0)));
        }
        catch (final Exception ex) {
            Logger.getLogger(LicensePercentHandler.class.getName()).log(Level.SEVERE, "Exception occurred at -deleteDeviceLicPercent", ex);
        }
    }
    
    static {
        LicensePercentHandler.licensePercentHandler = null;
    }
}
