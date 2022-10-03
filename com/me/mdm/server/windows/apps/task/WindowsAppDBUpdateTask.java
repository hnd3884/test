package com.me.mdm.server.windows.apps.task;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.Properties;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.apps.windows.BusinessStoreAPIAccess;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class WindowsAppDBUpdateTask implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    public static Logger logger;
    
    @Override
    public void processData(final CommonQueueData data) {
        try {
            final JSONObject jsonQueueData = data.getJsonQueueData();
            final JSONArray appGroupIDArr = new JSONArray((String)jsonQueueData.get("appJSON"));
            JSONObject appData = null;
            if (jsonQueueData.get("allowedApps") instanceof String) {
                appData = new JSONObject((String)jsonQueueData.get("allowedApps"));
            }
            else if (jsonQueueData.get("allowedApps") instanceof JSONObject) {
                appData = (JSONObject)jsonQueueData.get("allowedApps");
            }
            final List appGrpList = new ArrayList();
            for (int i = 0; i < appGroupIDArr.length(); ++i) {
                final Long appGroupID = Long.valueOf(appGroupIDArr.optString(i, "-1"));
                appGrpList.add(appGroupID);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGrpList.toArray(), 8));
            DataObject dataObject = null;
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            for (int j = 0; j < appGroupIDArr.length(); ++j) {
                final Long appGroupID2 = Long.valueOf(appGroupIDArr.optString(j, "-1"));
                final Iterator iterator = dataObject.getRows("MdAppToGroupRel", new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupID2, 0));
                final List appIDList = new ArrayList();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    appIDList.add(row.get("APP_ID"));
                }
                for (int k = 0; k < appIDList.size(); ++k) {
                    Row row2 = dataObject.getRow("WindowsAppDetails", new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), appIDList.get(k), 0));
                    final String AUMID = (String)appData.getJSONObject(appGroupIDArr.optString(j, "-1")).get("AUMID");
                    String productID = (String)appData.getJSONObject(appGroupIDArr.optString(j, "-1")).opt("productID");
                    final String storeID = (String)appData.getJSONObject(appGroupIDArr.optString(j, "-1")).opt("storeID");
                    if (row2 == null) {
                        row2 = new Row("WindowsAppDetails");
                        row2.set("APP_ID", appIDList.get(k));
                        row2.set("AUMID", (Object)AUMID);
                        if (storeID != null && !storeID.equals("")) {
                            row2.set("PRODUCT_ID", (Object)storeID);
                            if (productID == null) {
                                final JSONObject params = new JSONObject();
                                params.put("Type", (Object)"PackageIDQuery");
                                params.put("StoreID", (Object)storeID);
                                final JSONObject jsonObject = new BusinessStoreAPIAccess().getDataFromBusinessStore(params);
                                productID = jsonObject.optString("windowsPhoneLegacyId", "");
                            }
                        }
                        if (productID != null && !productID.equals("")) {
                            row2.set("PHONE_PRODUCT_ID", (Object)productID);
                        }
                        dataObject.addRow(row2);
                    }
                    else {
                        row2.set("AUMID", (Object)AUMID);
                        if (storeID != null && !storeID.equals("")) {
                            row2.set("PRODUCT_ID", (Object)storeID);
                            if (productID == null) {
                                final JSONObject params = new JSONObject();
                                params.put("Type", (Object)"PackageIDQuery");
                                params.put("StoreID", (Object)storeID);
                                final JSONObject jsonObject = new BusinessStoreAPIAccess().getDataFromBusinessStore(params);
                                productID = jsonObject.optString("windowsPhoneLegacyId", "");
                            }
                        }
                        if (productID != null && !productID.equals("")) {
                            row2.set("PHONE_PRODUCT_ID", (Object)productID);
                        }
                        dataObject.updateRow(row2);
                    }
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            WindowsAppDBUpdateTask.logger.log(Level.WARNING, "Error in updating WindowsApp Details", e);
        }
    }
    
    public void executeTask(final Properties props) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerId"));
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setTaskName(((Hashtable<K, String>)props).get("taskName"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            WindowsAppDBUpdateTask.logger.log(Level.SEVERE, "Cannot form JSON from the props file ", (Throwable)exp);
        }
    }
    
    static {
        WindowsAppDBUpdateTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
