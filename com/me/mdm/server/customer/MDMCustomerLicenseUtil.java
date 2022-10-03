package com.me.mdm.server.customer;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;

public class MDMCustomerLicenseUtil
{
    private static MDMCustomerLicenseUtil util;
    
    public static MDMCustomerLicenseUtil getInstance() {
        if (MDMCustomerLicenseUtil.util == null) {
            MDMCustomerLicenseUtil.util = new MDMCustomerLicenseUtil();
        }
        return MDMCustomerLicenseUtil.util;
    }
    
    public void addorUpdateDeviceAllocation(final Long customerID, final int deviceLimit) {
        try {
            final Criteria criteria = new Criteria(new Column("DeviceLimitToCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject deviceLimitDO = MDMUtil.getPersistence().get("DeviceLimitToCustomerMapping", criteria);
            Row deviceLimitRow = deviceLimitDO.getRow("DeviceLimitToCustomerMapping");
            if (deviceLimitRow == null) {
                deviceLimitRow = new Row("DeviceLimitToCustomerMapping");
                deviceLimitRow.set("CUSTOMER_ID", (Object)customerID);
                deviceLimitRow.set("NO_OF_DEVICES", (Object)deviceLimit);
                deviceLimitDO.addRow(deviceLimitRow);
                MDMUtil.getPersistence().add(deviceLimitDO);
            }
            else {
                deviceLimitRow.set("NO_OF_DEVICES", (Object)deviceLimit);
                deviceLimitDO.updateRow(deviceLimitRow);
                MDMUtil.getPersistence().update(deviceLimitDO);
                MDMMessageHandler.getInstance().messageAction("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", customerID);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.INFO, "Exception in addorUpdateDeviceAllocation ", ex);
        }
    }
    
    public void addOrUpdateCustomerLicenseInfo(final Long customerId, final Properties customerParams) {
        try {
            final Criteria criteria = new Criteria(new Column("MDCustomerLienseInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject deviceLimitDO = MDMUtil.getPersistence().get("MDCustomerLienseInfo", criteria);
            Row deviceLimitRow = deviceLimitDO.getRow("MDCustomerLienseInfo");
            if (deviceLimitRow == null) {
                deviceLimitRow = new Row("MDCustomerLienseInfo");
                deviceLimitRow.set("CUSTOMER_ID", (Object)customerId);
                deviceLimitRow.set("NO_OF_DEVICES", (Object)Integer.parseInt(((Hashtable<K, String>)customerParams).get("NO_OF_DEVICES")));
                deviceLimitRow.set("NO_OF_TECHNICIAN", (Object)Integer.parseInt(((Hashtable<K, String>)customerParams).get("NO_OF_TECHNICIAN")));
                deviceLimitRow.set("EDITION", (Object)((Hashtable<K, String>)customerParams).get("EDITION"));
                deviceLimitDO.addRow(deviceLimitRow);
                MDMUtil.getPersistence().add(deviceLimitDO);
            }
            else {
                deviceLimitRow.set("NO_OF_DEVICES", (Object)Integer.parseInt(((Hashtable<K, String>)customerParams).get("NO_OF_DEVICES")));
                deviceLimitRow.set("NO_OF_TECHNICIAN", (Object)Integer.parseInt(((Hashtable<K, String>)customerParams).get("NO_OF_TECHNICIAN")));
                deviceLimitRow.set("EDITION", (Object)((Hashtable<K, String>)customerParams).get("EDITION"));
                deviceLimitDO.updateRow(deviceLimitRow);
                MDMUtil.getPersistence().update(deviceLimitDO);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.INFO, "Exception in addOrUpdateCustomerLicenseInfo ", e);
        }
    }
    
    public void resetDeviceAllocationForCustomers() {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DeviceLimitToCustomerMapping");
            updateQuery.setUpdateColumn("NO_OF_DEVICES", (Object)0);
            SyMUtil.getPersistence().update(updateQuery);
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIds != null && customerIds.length >= 0) {
                for (final Long customerID : customerIds) {
                    MessageProvider.getInstance().hideMessage("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", customerID);
                }
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in resetDeviceAllocationForCustomers ", ex);
        }
    }
    
    static {
        MDMCustomerLicenseUtil.util = null;
    }
}
