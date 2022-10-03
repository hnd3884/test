package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AddBulkAFWCommandTask implements SchedulerExecutionInterface, CommonQueueProcessorInterface
{
    public static Logger logger;
    private static final String SAMSUNG_ON_UPGRADE = "SamsungOnUpgrade";
    private static final String LEGACY_ON_UPGRADE = "LegacyOnUpgrade";
    
    public void executeTask(final Properties taskProps) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)taskProps).get("jsonParams")));
            tempData.setTaskName(((Hashtable<K, String>)taskProps).get("taskName"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            AddBulkAFWCommandTask.logger.log(Level.SEVERE, "Cannot form JSON from the props file ", (Throwable)exp);
        }
    }
    
    public void processData(final CommonQueueData data) {
        try {
            final JSONObject jsonData = data.getJsonQueueData();
            final String deviceType = (String)jsonData.get("DeviceType");
            if ("SamsungOnUpgrade".equalsIgnoreCase(deviceType)) {
                this.sendCommandToSamsungDevices();
            }
            else if ("allDevicesOnConfigure".equalsIgnoreCase(deviceType)) {
                this.sendCommandToAllDevices(data.getCustomerId());
            }
            else if ("LegacyOnUpgrade".equalsIgnoreCase(deviceType)) {
                this.sendCommandToLegacyDevices();
            }
        }
        catch (final Exception exp) {
            AddBulkAFWCommandTask.logger.log(Level.SEVERE, "Exception while processing data from common queue ", exp);
        }
    }
    
    private void sendCommandToSamsungDevices() throws Exception {
        if ("true".equals(SyMUtil.getSyMParameter("isPFWSamsungPending"))) {
            this.sendCommandToNewlyEligibleDevices("SamsungOnUpgrade");
        }
    }
    
    private void sendCommandToLegacyDevices() throws Exception {
        if ("true".equals(SyMUtil.getSyMParameter("isPFWAllPending"))) {
            this.sendCommandToNewlyEligibleDevices("LegacyOnUpgrade");
        }
    }
    
    private void sendCommandToNewlyEligibleDevices(final String deviceType) throws Exception {
        final Long[] customerIdsFromDB;
        final Long[] customerIdList = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        for (final Long customerId : customerIdsFromDB) {
            if (GoogleForWorkSettings.isEMMTypeAFWConfigured(customerId)) {
                final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
                final Criteria samsungCriteria = new Criteria(Column.getColumn("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
                final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria androidCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
                Criteria criteria = customerCriteria.and(androidCri);
                if (deviceType.equals("SamsungOnUpgrade")) {
                    criteria = criteria.and(samsungCriteria);
                }
                sQuery.setCriteria(criteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator mDeviceiterator = dataObject.getRows("ManagedDevice");
                    final Iterator deviceInfoIterator = dataObject.getRows("MdDeviceInfo");
                    while (mDeviceiterator.hasNext() && deviceInfoIterator.hasNext()) {
                        final Row mDeviceRow = mDeviceiterator.next();
                        final Row deviceInfoRow = deviceInfoIterator.next();
                        if (ManagedDeviceHandler.getInstance().isDeviceAFWCompatible(mDeviceRow, deviceInfoRow)) {
                            final Long resourceID = (Long)mDeviceRow.get("RESOURCE_ID");
                            final Long bsId = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW).getLong("BUSINESSSTORE_ID");
                            if (new StoreAccountManagementHandler().isStoreAccountAddedForDevice(resourceID, bsId)) {
                                continue;
                            }
                            final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "UDID");
                            new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(resourceID, udid, customerId);
                        }
                    }
                }
            }
        }
        SyMUtil.updateSyMParameter(deviceType.equals("SamsungOnUpgrade") ? "isPFWSamsungPending" : "isPFWAllPending", "false");
    }
    
    private void sendCommandToAllDevices(final Long customerId) throws Exception {
        final List afwDevicesList = ManagedDeviceHandler.getInstance().getAFWEligibleDevices(customerId);
        AddBulkAFWCommandTask.logger.log(Level.INFO, "Devices eligible for AFW command {0}", afwDevicesList);
        if (afwDevicesList != null) {
            final List resDetailsList = new ArrayList();
            final List resList = new ArrayList();
            for (final Object afwDevice : afwDevicesList) {
                final Properties deviceProp = (Properties)afwDevice;
                final JSONObject resJSON = new JSONObject();
                final Long resourceId = ((Hashtable<K, Long>)deviceProp).get("RESOURCE_ID");
                resJSON.put("resID", (Object)resourceId);
                resJSON.put("udid", (Object)("udid#" + ((Hashtable<K, String>)deviceProp).get("UDID")));
                resDetailsList.add(resJSON);
                resList.add(resourceId);
            }
            new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(customerId, resDetailsList, resList);
        }
    }
    
    static {
        AddBulkAFWCommandTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
