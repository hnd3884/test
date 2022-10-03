package com.me.mdm.server.msp.sync;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Collection;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SyncConfigurationListeners
{
    private static Logger logger;
    
    private static JSONObject getNecessaryDetailsToCloneProfile(final Long profileId) throws DataAccessException {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Row profileToCustomerRow = dataObject.getFirstRow("ProfileToCustomerRel");
            jsonObject.put("PROFILE_PAYLOAD_IDENTIFIER", profileRow.get("PROFILE_PAYLOAD_IDENTIFIER"));
            jsonObject.put("PROFILE_TYPE", profileRow.get("PROFILE_TYPE"));
            jsonObject.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
            jsonObject.put("CUSTOMER_ID", profileToCustomerRow.get("CUSTOMER_ID"));
            jsonObject.put("LAST_MODIFIED_BY", profileRow.get("LAST_MODIFIED_BY"));
        }
        return jsonObject;
    }
    
    private static JSONObject getNecessaryDetailsToCloneApp(final JSONObject jsonObject) {
        final JSONObject queueJson = new JSONObject();
        queueJson.put("msg_body", (Object)jsonObject);
        queueJson.put("CUSTOMER_ID", jsonObject.getLong("customerID"));
        queueJson.put("LAST_MODIFIED_BY", jsonObject.getLong("userID"));
        queueJson.put("PROFILE_TYPE", 2);
        queueJson.put("PLATFORM_TYPE", jsonObject.getInt("platform_type"));
        queueJson.put("LOGIN_ID", jsonObject.getLong("LOGIN_ID"));
        if (jsonObject.has("childCustomerId")) {
            queueJson.put("childCustomerId", jsonObject.getLong("childCustomerId"));
        }
        return queueJson;
    }
    
    private static List getGlobalAppIdentifiersFromPackageIds(final Long[] appIds) throws DataAccessException {
        final List appIdentifiers = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appIds, 8);
        final Criteria globalAppCriteria = new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        selectQuery.setCriteria(appCriteria.and(globalAppCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Iterator<Row> iterator = dataObject.getRows("MdAppGroupDetails");
        while (iterator.hasNext()) {
            final Row appGroupRow = iterator.next();
            final String identifier = (String)appGroupRow.get("IDENTIFIER");
            if (!appIdentifiers.contains(identifier)) {
                appIdentifiers.add(identifier);
            }
        }
        return appIdentifiers;
    }
    
    private static String getQueueFileName(final int profileType, final Long customerId, final int actionType) {
        String fileName = customerId + "_" + System.currentTimeMillis();
        if (profileType == 1) {
            if (actionType == 100) {
                fileName += "_publish";
            }
            else if (actionType == 101) {
                fileName += "_trash";
            }
            else if (actionType == 102) {
                fileName += "_delete";
            }
            fileName += "_profile_sync_qdata";
        }
        else if (profileType == 2) {
            if (actionType == 201) {
                fileName += "_new_app_add";
            }
            else if (actionType == 202) {
                fileName += "_new_app_version";
            }
            else if (actionType == 203) {
                fileName += "_app_version_update";
            }
            else if (actionType == 204) {
                fileName += "_app_version_approval";
            }
            else if (actionType == 205) {
                fileName += "_app_move_to_all";
            }
            fileName += "_app_sync_qdata";
        }
        return fileName;
    }
    
    public static void invokeListenersOnCustomerCreation(final Long customerId) {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
            SyncConfigurationListeners.logger.log(Level.INFO, "Going to add SyncConfigurationCustomerListener asynchronously");
            try {
                final HashMap taskInfoMap = new HashMap();
                final Properties properties = new Properties();
                ((Hashtable<String, Long>)properties).put("customerId", customerId);
                taskInfoMap.put("taskName", "SyncConfigurationCustomerListenerTask");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                taskInfoMap.put("poolName", "mdmPool");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.msp.sync.SyncConfigurationCustomerListener", taskInfoMap, properties);
            }
            catch (final Exception e) {
                SyncConfigurationListeners.logger.log(Level.SEVERE, "Exception in executing SyncConfigurationCustomerListener asynchronously", e);
            }
        }
    }
    
    public static void invokeListeners(final JSONObject jsonObject, final int actionType) {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
            try {
                switch (actionType) {
                    case 100: {
                        final Long profileId = (Long)jsonObject.get("PROFILE_ID");
                        final Long aaaLogin = (Long)jsonObject.get("LOGIN_ID");
                        final int profileType = jsonObject.getInt("PROFILE_TYPE");
                        final Long customerId = jsonObject.getLong("CUSTOMER_ID");
                        final Boolean isForAllCustomers = SyncConfigurationsUtil.checkIfProfileIsForAllCustomers(profileId);
                        if (isForAllCustomers) {
                            final JSONObject necessaryDetailsToClone = getNecessaryDetailsToCloneProfile(profileId);
                            necessaryDetailsToClone.put("LOGIN_ID", (Object)aaaLogin);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Listener invoked for profile publish with props {0}, {1}", new Object[] { jsonObject, necessaryDetailsToClone });
                            final DCQueueData dcQueueData = new DCQueueData();
                            dcQueueData.fileName = getQueueFileName(profileType, customerId, actionType);
                            dcQueueData.queueData = necessaryDetailsToClone.toString();
                            dcQueueData.postTime = System.currentTimeMillis();
                            dcQueueData.queueDataType = 100;
                            final DCQueue dcQueue = DCQueueHandler.getQueue("sync-configurations-processor");
                            dcQueue.addToQueue(dcQueueData);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Data added to queue for profile publish with props {0} ,{1}", new Object[] { jsonObject, dcQueueData });
                            break;
                        }
                        break;
                    }
                    case 101:
                    case 102:
                    case 103: {
                        final Object[] profileIds = (Object[])jsonObject.get("profilesIds");
                        for (int i = 0; i < profileIds.length; ++i) {
                            final Long profileId2 = (Long)profileIds[i];
                            final int profileType2 = jsonObject.getInt("PROFILE_TYPE");
                            final Long customerId2 = jsonObject.getLong("CUSTOMER_ID");
                            final Boolean isForAllCustomers2 = SyncConfigurationsUtil.checkIfProfileIsForAllCustomers(profileId2);
                            if (isForAllCustomers2) {
                                final JSONObject necessaryDetailsToClone2 = getNecessaryDetailsToCloneProfile(profileId2);
                                necessaryDetailsToClone2.put("LOGIN_ID", jsonObject.optLong("LOGIN_ID"));
                                SyncConfigurationListeners.logger.log(Level.INFO, "Listener invoked for profile trash/delete/restore props {0}, {1}", new Object[] { jsonObject, necessaryDetailsToClone2 });
                                final DCQueueData dcQueueData2 = new DCQueueData();
                                dcQueueData2.fileName = getQueueFileName(profileType2, customerId2, actionType);
                                dcQueueData2.queueData = necessaryDetailsToClone2.toString();
                                dcQueueData2.postTime = System.currentTimeMillis();
                                dcQueueData2.queueDataType = actionType;
                                final DCQueue dcQueue2 = DCQueueHandler.getQueue("sync-configurations-processor");
                                dcQueue2.addToQueue(dcQueueData2);
                                SyncConfigurationListeners.logger.log(Level.INFO, "Data added to queue for profile trash/delete with props {0} ,{1}", new Object[] { jsonObject, dcQueueData2 });
                            }
                        }
                        break;
                    }
                    case 201:
                    case 202:
                    case 203:
                    case 204:
                    case 206:
                    case 208:
                    case 212: {
                        final Long appId = jsonObject.getLong("app_id");
                        final Boolean isForAllCustomers3 = SyncConfigurationsUtil.checkIfAppIsForAllCustomers(appId);
                        if (isForAllCustomers3) {
                            final JSONObject queueJson = getNecessaryDetailsToCloneApp(jsonObject);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Listeners invoked for app props {0},{1}, {2}", new Object[] { jsonObject, queueJson, actionType });
                            final DCQueueData dcQueueData3 = new DCQueueData();
                            dcQueueData3.fileName = getQueueFileName(2, queueJson.getLong("CUSTOMER_ID"), actionType);
                            dcQueueData3.queueData = queueJson.toString();
                            dcQueueData3.postTime = System.currentTimeMillis();
                            dcQueueData3.queueDataType = actionType;
                            final DCQueue dcQueue3 = DCQueueHandler.getQueue("sync-configurations-processor");
                            dcQueue3.addToQueue(dcQueueData3);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Data added to app actions with props {0} ,{1} , {2}", new Object[] { jsonObject, dcQueueData3, actionType });
                            break;
                        }
                        break;
                    }
                    case 209:
                    case 210:
                    case 211: {
                        final Long[] appIds = (Long[])jsonObject.get("app_ids");
                        final List globalAppIdentifiers = getGlobalAppIdentifiersFromPackageIds(appIds);
                        if (!globalAppIdentifiers.isEmpty()) {
                            jsonObject.put("appIds", (Collection)globalAppIdentifiers);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Listeners invoked for app props {0}, {2}", new Object[] { jsonObject, actionType });
                            final DCQueueData dcQueueData4 = new DCQueueData();
                            dcQueueData4.fileName = getQueueFileName(2, jsonObject.getLong("CUSTOMER_ID"), actionType);
                            dcQueueData4.queueData = jsonObject.toString();
                            dcQueueData4.postTime = System.currentTimeMillis();
                            dcQueueData4.queueDataType = actionType;
                            final DCQueue dcQueue4 = DCQueueHandler.getQueue("sync-configurations-processor");
                            dcQueue4.addToQueue(dcQueueData4);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Data added to app delete actions with props {0} ,{1} , {2}", new Object[] { jsonObject, dcQueueData4, actionType });
                            break;
                        }
                        break;
                    }
                    case 205: {
                        final Long appId = jsonObject.getLong("app_id");
                        final Boolean isForAllCustomers3 = SyncConfigurationsUtil.checkIfAppIsForAllCustomers(appId);
                        if (isForAllCustomers3) {
                            SyncConfigurationListeners.logger.log(Level.INFO, "Listeners invoked for app move to all customers with props {0}", new Object[] { jsonObject });
                            final DCQueueData dcQueueData4 = new DCQueueData();
                            dcQueueData4.fileName = getQueueFileName(2, jsonObject.getLong("CUSTOMER_ID"), actionType);
                            dcQueueData4.queueData = jsonObject.toString();
                            dcQueueData4.postTime = System.currentTimeMillis();
                            dcQueueData4.queueDataType = actionType;
                            final DCQueue dcQueue4 = DCQueueHandler.getQueue("sync-configurations-processor");
                            dcQueue4.addToQueue(dcQueueData4);
                            SyncConfigurationListeners.logger.log(Level.INFO, "Data added to queue for app move to all customers", new Object[] { jsonObject, dcQueueData4 });
                            break;
                        }
                        break;
                    }
                }
            }
            catch (final Exception ex) {
                SyncConfigurationListeners.logger.log(Level.SEVERE, "Exception in invokeListener for action {0} props {1}", new Object[] { actionType, jsonObject });
            }
        }
    }
    
    static {
        SyncConfigurationListeners.logger = Logger.getLogger("MDMConfigLogger");
    }
}
