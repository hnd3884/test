package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import org.json.JSONObject;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import com.adventnet.i18n.I18N;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class BlacklistMailUtils
{
    private Logger logger;
    
    public BlacklistMailUtils() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public void sendBlackListMail(final Properties params) throws Exception {
        final List resourceList = ((Hashtable<K, List>)params).get("resourceList");
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)params).get("profileCollectionMap");
        final JSONArray resourceArray = new BlacklistAppHandler().getResourceDetails(resourceList);
        final List appNames = new BlacklistAppHandler().getAppNames(new ArrayList(profileCollectionMap.values()));
        final Iterator iterator = appNames.iterator();
        final StringBuilder apps = new StringBuilder();
        apps.append("<ol>");
        while (iterator.hasNext()) {
            final String curApp = iterator.next();
            apps.append("<li>").append(curApp).append("</li>");
        }
        apps.append("</ol>");
        for (int i = 0; i < resourceArray.length(); ++i) {
            final JSONObject jsonObject = resourceArray.getJSONObject(i);
            final String deviceName = (String)jsonObject.get("NAME");
            final String emailAdrs = (String)jsonObject.get("EMAIL_ADDRESS");
            final String subject = I18N.getMsg("mdm.blacklist.dailymail.subject", new Object[] { deviceName });
            String content = I18N.getMsg("mdm.blacklist.dailymail.body", new Object[] { deviceName });
            content += apps.toString();
            MDMMailNotificationHandler.getInstance().sendMail(emailAdrs, content, subject);
        }
    }
    
    public void initiateBlacklistingMailOnAction(final Long customerID) {
        try {
            final CommonQueueData commonQueueData = new CommonQueueData();
            commonQueueData.setEmptyJsonQueueData();
            commonQueueData.setClassName("com.me.mdm.server.apps.blacklist.task.BlacklistMailTask");
            commonQueueData.setTaskName("BlacklistMailTask");
            commonQueueData.setCustomerId(customerID);
            CommonQueueUtil.getInstance().addToQueue(commonQueueData, CommonQueues.MDM_MAILTASK);
        }
        catch (final Exception exp) {
            Logger.getLogger(BlacklistMailUtils.class.getName()).log(Level.SEVERE, "Cannot add black list mails tas  to queue", exp);
        }
    }
    
    public void sendDailyBlacklistMailToResources(final Long customerID) {
        try {
            this.logger.log(Level.INFO, "sending out daily mail for blacklist for customerID : {0}", customerID);
            final HashMap violoationList = this.getResourcesViolatingBlacklistPolicy(customerID);
            final Iterator iterator = violoationList.keySet().iterator();
            final JSONObject notifySettings = new BlacklistAppHandler().getBlackListAppSettings(customerID);
            final int notifyDays = notifySettings.getInt("NOTIFY_DAYS");
            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
            while (iterator.hasNext()) {
                final List notifiedAppList = new ArrayList();
                final Long resID = iterator.next();
                final HashMap resMap = violoationList.get(resID);
                final JSONArray appNames = resMap.get("AppList");
                final StringBuilder apps = new StringBuilder();
                Boolean shouldNotify = false;
                apps.append("<ol>");
                for (int i = 0; i < appNames.length(); ++i) {
                    final JSONObject appsObject = appNames.getJSONObject(i);
                    final int notifiedCount = appsObject.getInt("NOTIFIED_COUNT");
                    final Long appGrpId = (Long)appsObject.get("APP_GROUP_ID");
                    if (notifyDays != 0 && notifyDays > notifiedCount) {
                        apps.append("<li>").append(appsObject.get("GROUP_DISPLAY_NAME")).append("</li>");
                        shouldNotify = true;
                        notifiedAppList.add(appGrpId);
                    }
                }
                apps.append("</ol>");
                final String deviceName = resMap.get("NAME");
                final String emailAdrs = resMap.get("EMAIL_ADDRESS");
                final String platform = resMap.get("PLATFORM_TYPE");
                final Properties prop = new Properties();
                ((Hashtable<String, String>)prop).put("$device_name$", deviceName);
                ((Hashtable<String, StringBuilder>)prop).put("$app_list$", apps);
                ((Hashtable<String, String>)prop).put("$user_emailid$", emailAdrs);
                ((Hashtable<String, String>)prop).put("$platform_type$", platform);
                if (shouldNotify) {
                    mailGenerator.sendMail(MDMAlertConstants.USER_BLACK_LIST, "MdM-BlackListApp", customerID, prop);
                    this.updateNotifiedCount(resID, notifiedAppList);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "error while sending out blacklist mail for cusotmer : " + n);
        }
    }
    
    public HashMap getResourcesViolatingBlacklistPolicy(final Long customerID) throws Exception {
        final HashMap violationList = new HashMap();
        final SelectQuery violationQuery = BlacklistQueryUtils.getInstance().getViolationQuery(customerID);
        DMDataSetWrapper dataSet = null;
        try {
            dataSet = DMDataSetWrapper.executeQuery((Object)violationQuery);
            while (dataSet.next()) {
                final String appname = (String)dataSet.getValue("GROUP_DISPLAY_NAME");
                final String email = (String)dataSet.getValue("DISPLAY_NAME");
                final String username = (String)dataSet.getValue("EMAIL_ADDRESS");
                final String resourceName = (String)dataSet.getValue("NAME");
                final Long resID = (Long)dataSet.getValue("RESOURCE_ID");
                final Long appGrpID = (Long)dataSet.getValue("APP_GROUP_ID");
                final Integer platformType = (Integer)dataSet.getValue("PLATFORM_TYPE");
                final Integer notifyCount = (Integer)dataSet.getValue("NOTIFIED_COUNT");
                HashMap resMap = violationList.get(resID);
                if (resMap == null) {
                    resMap = new HashMap();
                    resMap.put("DISPLAY_NAME", email);
                    resMap.put("EMAIL_ADDRESS", username);
                    resMap.put("NAME", resourceName);
                    resMap.put("PLATFORM_TYPE", (platformType == 1) ? "iOS" : ((platformType == 2) ? "android" : ((platformType == 3) ? "windows" : "ChromeOS")));
                }
                JSONArray jsonArray = resMap.get("AppList");
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                }
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("GROUP_DISPLAY_NAME", (Object)appname);
                jsonObject.put("APP_GROUP_ID", (Object)appGrpID);
                jsonObject.put("NOTIFIED_COUNT", (Object)notifyCount);
                jsonArray.put((Object)jsonObject);
                resMap.put("AppList", jsonArray);
                violationList.put(resID, resMap);
            }
        }
        catch (final SQLException e) {
            this.logger.log(Level.SEVERE, e, () -> "error while sending out blacklist mail for cusotmer : " + n);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.SEVERE, (Throwable)e2, () -> "error while sending out blacklist mail for cusotmer : " + n2);
        }
        return violationList;
    }
    
    private void updateNotifiedCount(final Long resID, final List appGroupIds) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria resCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resID, 0);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        selectQuery.setCriteria(resCriteria.and(appGroupCriteria));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "NOTIFIED_COUNT"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "LAST_NOTIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("BlacklistAppCollectionStatus");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            if (row != null) {
                Integer notfCnt = (Integer)row.get("NOTIFIED_COUNT");
                if (notfCnt == null) {
                    notfCnt = 0;
                }
                row.set("NOTIFIED_COUNT", (Object)(notfCnt + 1));
                row.set("LAST_NOTIFIED_TIME", (Object)System.currentTimeMillis());
                dataObject.updateRow(row);
            }
        }
        MDMUtil.getPersistenceLite().update(dataObject);
    }
}
