package com.adventnet.sym.server.mdm.inv;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.adventnet.iam.xss.IAMEncoder;
import java.text.DateFormat;
import org.json.JSONArray;
import java.util.Arrays;
import com.me.mdm.server.util.CalendarUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.apps.blacklist.BlacklistMailUtils;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.factory.SDPIntegrationAPI;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMMailNotificationHandler
{
    private static MDMMailNotificationHandler mailHandler;
    private Logger logger;
    private Logger mdmAppLogger;
    private int maxRange;
    
    public MDMMailNotificationHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.mdmAppLogger = Logger.getLogger("MDMAppMgmtLogger");
        this.maxRange = 100;
    }
    
    public static synchronized MDMMailNotificationHandler getInstance() {
        if (MDMMailNotificationHandler.mailHandler == null) {
            MDMMailNotificationHandler.mailHandler = new MDMMailNotificationHandler();
        }
        return MDMMailNotificationHandler.mailHandler;
    }
    
    public void sendBlackListAppDetectMailNotification(final Long customerID) throws Exception {
        this.deleteBlackListNotify();
        this.logger.log(Level.INFO, "admin notification of blacklisted app starts for customer {0}", customerID);
        final Criteria cNotify = new Criteria(new Column("BlacklistAppCollectionStatus", "ADMIN_NOTIFIED_COUNT"), (Object)1, 7);
        final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria statusCriteria = new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 3 }, 8);
        final Criteria criteria = cNotify.and(cManagedStatus).and(customerCriteria).and(statusCriteria);
        final List appDetailsList = this.getAppBlackList(criteria);
        final List<Long> appId = new ArrayList<Long>();
        final List<Long> resId = new ArrayList<Long>();
        final Iterator item = appDetailsList.iterator();
        final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.mdmAppLogger);
        final Boolean sendMail = this.enableBlackListNotify(customerID);
        while (item.hasNext()) {
            final Properties itemProp = item.next();
            final Properties prop = new Properties();
            final String appName = ((Hashtable<K, String>)itemProp).get("APP_NAME");
            ((Hashtable<String, String>)prop).put("$app_name$", appName);
            ((Hashtable<String, String>)prop).put("$bundle_identifier$", ((Hashtable<K, String>)itemProp).get("IDENTIFIER"));
            ((Hashtable<String, String>)prop).put("$app_version$", ((Hashtable<K, String>)itemProp).get("APP_VERSION"));
            ((Hashtable<String, String>)prop).put("$device_name$", ((Hashtable<K, String>)itemProp).get("MANAGEDDEVICEEXTN.NAME"));
            ((Hashtable<String, String>)prop).put("$user_name$", ((Hashtable<K, String>)itemProp).get("UserName"));
            ((Hashtable<String, String>)prop).put("$detection_time$", MDMUtil.getDate((long)((Hashtable<K, Long>)itemProp).get("DETECTED_AT")));
            final int platform = ((Hashtable<K, Integer>)itemProp).get("PLATFORM_TYPE");
            ((Hashtable<String, String>)prop).put("$platform_type$", MDMUtil.getInstance().getPlatformName(platform));
            final Long customerId = ((Hashtable<K, Long>)itemProp).get("CUSTOMER_ID");
            appId.add(((Hashtable<K, Long>)itemProp).get("APP_GROUP_ID"));
            resId.add(((Hashtable<K, Long>)itemProp).get("RESOURCE_ID"));
            if (sendMail) {
                mailGenerator.sendMail(MDMAlertConstants.ADMIN_BLACK_LIST, "MdM-BlackListApp", customerId, prop);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2045, ((Hashtable<K, Long>)itemProp).get("RESOURCE_ID"), MDMEventConstant.DC_SYSTEM_USER, "dc.mdm.actionlog.inv.blacklisted_app_discovered", appName, customerId);
            }
            ((Hashtable<String, String>)prop).put("$device_name$", ((Hashtable<K, String>)itemProp).get("DeviceName"));
            ((Hashtable<String, String>)prop).put("$user_emailid$", ((Hashtable<K, String>)itemProp).get("EMAIL_ADDRESS"));
            ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerId);
            final SDPIntegrationAPI sdpAPI = MDMApiFactoryProvider.getSDPIntegrationAPI();
            if (sdpAPI != null) {
                sdpAPI.handleSDPAlerts(prop, "SDP_MDM_HELPDESK_BLACKLIST_APP_ALERT");
            }
        }
        this.updateBlackListStatus(appId, resId);
    }
    
    private void updateBlackListStatus(final List appIdList, final List resourceIdList) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppCollectionStatus"));
        final Join notifyJoin = new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        sQuery.addJoin(notifyJoin);
        final Criteria cAppGroup = new Criteria(new Column("BlacklistAppToCollection", "APP_GROUP_ID"), (Object)appIdList.toArray(), 8);
        final Criteria cRes = new Criteria(new Column("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
        sQuery.setCriteria(cAppGroup.and(cRes));
        sQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "*"));
        sQuery.addSelectColumn(Column.getColumn("BlacklistAppToCollection", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        final Iterator item = DO.getRows("BlacklistAppCollectionStatus");
        while (item.hasNext()) {
            final Row blackListApp = item.next();
            blackListApp.set("ADMIN_NOTIFIED_COUNT", (Object)1);
            DO.updateRow(blackListApp);
        }
        MDMUtil.getPersistence().update(DO);
    }
    
    public void sendBlacklistMails(final Long customerID) {
        new BlacklistMailUtils().sendDailyBlacklistMailToResources(customerID);
    }
    
    public void sendNewAppDetectMailNotification(final Long customerID) throws DataAccessException {
        final Criteria criteria = new Criteria(new Column("MdAppGroupDetails", "NOTIFY_ADMIN"), (Object)Boolean.TRUE, 0);
        final Criteria custcriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        final List newAppList = this.getAppGroupDO(criteria.and(custcriteria));
        final List appGroupId = new ArrayList();
        final Iterator item = newAppList.iterator();
        final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.mdmAppLogger);
        final Boolean sendMail = this.enableNewAppListNotify(customerID);
        while (item.hasNext()) {
            final Properties itemProp = item.next();
            appGroupId.add(((Hashtable<K, Object>)itemProp).get("APP_GROUP_ID"));
            final Properties prop = new Properties();
            final String appName = ((Hashtable<K, String>)itemProp).get("GROUP_DISPLAY_NAME");
            ((Hashtable<String, String>)prop).put("$app_name$", appName);
            ((Hashtable<String, Object>)prop).put("$bundle_identifier$", ((Hashtable<K, Object>)itemProp).get("IDENTIFIER"));
            ((Hashtable<String, Object>)prop).put("$app_version$", ((Hashtable<K, Object>)itemProp).get("APP_VERSION"));
            ((Hashtable<String, String>)prop).put("$detection_time$", MDMUtil.getDate((long)((Hashtable<K, Long>)itemProp).get("ADDED_TIME")));
            final int platform = ((Hashtable<K, Integer>)itemProp).get("PLATFORM_TYPE");
            ((Hashtable<String, String>)prop).put("$platform_type$", MDMUtil.getInstance().getPlatformName(platform));
            ((Hashtable<String, String>)prop).put("$device_name$", ((Hashtable<K, String>)itemProp).get("MANAGEDDEVICEEXTN.NAME"));
            ((Hashtable<String, String>)prop).put("$user_name$", ((Hashtable<K, String>)itemProp).get("UserName"));
            final Long customerId = ((Hashtable<K, Long>)itemProp).get("CUSTOMER_ID");
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2046, null, MDMEventConstant.DC_SYSTEM_USER, "dc.mdm.actionlog.inv.new_app_discovered", appName, customerId);
            if (sendMail) {
                mailGenerator.sendMail(MDMAlertConstants.NEW_APP_DISCOVERED, "MdM-BlackListApp", customerId, prop);
            }
            ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerId);
            final SDPIntegrationAPI sdpAPI = MDMApiFactoryProvider.getSDPIntegrationAPI();
            if (sdpAPI != null) {
                sdpAPI.handleSDPAlerts(prop, "SDP_MDM_HELPDESK_NEW_APP_ALERT");
            }
        }
        if (!appGroupId.isEmpty()) {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdAppGroupDetails");
            final Criteria cGroupId = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId.toArray(), 8);
            uQuery.setCriteria(cGroupId);
            uQuery.setUpdateColumn("NOTIFY_ADMIN", (Object)Boolean.FALSE);
            MDMUtil.getPersistence().update(uQuery);
        }
    }
    
    private void deleteBlackListNotify() {
        try {
            final Criteria cDeleted = new Criteria(new Column("MdBlackListAppInResource", "MARKED_FOR_DELETE"), (Object)Boolean.TRUE, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppBlackListNotify"));
            final Join notifyJoin = new Join("MdAppBlackListNotify", "MdBlackListAppInResource", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
            query.addJoin(notifyJoin);
            query.setCriteria(cDeleted);
            query.addSelectColumn(Column.getColumn("MdAppBlackListNotify", "*"));
            query.addSelectColumn(Column.getColumn("MdBlackListAppInResource", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(query);
            DO.deleteRows("MdAppBlackListNotify", (Criteria)null);
            DO.deleteRows("MdBlackListAppInResource", (Criteria)null);
            MDMUtil.getPersistence().update(DO);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in deleteBlackListNotify ", (Throwable)ex);
        }
    }
    
    private List getAppBlackList(final Criteria criteria) {
        final List appDetails = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppCollectionStatus"));
        final Join colectionJoin = new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join appIDJoin = new Join("BlacklistAppToCollection", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join groupJoin = new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria resCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), 0);
        final Criteria appCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_ID"), (Object)Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), 0);
        final Join installedAppResRel = new Join("BlacklistAppCollectionStatus", "MdInstalledAppResourceRel", appCriteria.and(resCriteria), 2);
        final Join mandevJoin = new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join devResJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, "ManagedDevice", "ManagedDeviceResource", 2);
        final Join userRelJoin = new Join(new Table("Resource", "ManagedDeviceResource"), new Table("ManagedUserToDevice"), new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join userJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final Join userResJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "ManagedUserResource", 2);
        final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        query.addJoin(colectionJoin);
        query.addJoin(appIDJoin);
        query.addJoin(groupJoin);
        query.addJoin(installedAppResRel);
        query.addJoin(mandevJoin);
        query.addJoin(devResJoin);
        query.addJoin(userRelJoin);
        query.addJoin(userJoin);
        query.addJoin(userResJoin);
        query.addJoin(extnJoin);
        final Join appJoin = new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        query.addJoin(appJoin);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        if (criteria != null) {
            query.setCriteria(criteria.and(userNotInTrashCriteria));
        }
        else {
            query.setCriteria(userNotInTrashCriteria);
        }
        query.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "DeviceName"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceResource", "CUSTOMER_ID"));
        query.addSelectColumn(Column.getColumn("ManagedUserResource", "NAME", "UserName"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "UPDATED_AT"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
        query.addSortColumn(new SortColumn("MdAppToGroupRel", "APP_GROUP_ID", true));
        query.setRange(new Range(0, this.maxRange));
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Properties prop = new Properties();
                ((Hashtable<String, Object>)prop).put("DeviceName", ds.getValue("DeviceName"));
                ((Hashtable<String, Object>)prop).put("UserName", ds.getValue("UserName"));
                if (ds.getValue("EMAIL_ADDRESS") != null) {
                    ((Hashtable<String, Object>)prop).put("EMAIL_ADDRESS", ds.getValue("EMAIL_ADDRESS"));
                }
                else {
                    ((Hashtable<String, String>)prop).put("EMAIL_ADDRESS", "--");
                }
                ((Hashtable<String, Object>)prop).put("APP_NAME", ds.getValue("APP_NAME"));
                ((Hashtable<String, Object>)prop).put("IDENTIFIER", ds.getValue("IDENTIFIER"));
                ((Hashtable<String, Object>)prop).put("APP_VERSION", ds.getValue("APP_VERSION"));
                ((Hashtable<String, Object>)prop).put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                ((Hashtable<String, Object>)prop).put("DETECTED_AT", ds.getValue("UPDATED_AT"));
                ((Hashtable<String, Object>)prop).put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
                ((Hashtable<String, Object>)prop).put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                ((Hashtable<String, Object>)prop).put("APP_ID", ds.getValue("APP_ID"));
                ((Hashtable<String, Object>)prop).put("MANAGEDDEVICEEXTN.NAME", ds.getValue("MANAGEDDEVICEEXTN.NAME"));
                appDetails.add(prop);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppBlackList ", ex);
        }
        return appDetails;
    }
    
    private List getAppGroupDO(final Criteria criteria) {
        final List appDetails = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        final Join relJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join appResRelJoin = new Join("MdAppDetails", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join mandevJoin = new Join("MdInstalledAppResourceRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join devResJoin = new Join("MdInstalledAppResourceRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, "MdInstalledAppResourceRel", "ManagedDeviceResource", 2);
        final Join userRelJoin = new Join(new Table("Resource", "ManagedDeviceResource"), new Table("ManagedUserToDevice"), new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join userJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final Join userResJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "ManagedUserResource", 2);
        final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        query.addJoin(relJoin);
        query.addJoin(appJoin);
        query.addJoin(appResRelJoin);
        query.addJoin(mandevJoin);
        query.addJoin(devResJoin);
        query.addJoin(userRelJoin);
        query.addJoin(userJoin);
        query.addJoin(userResJoin);
        query.addJoin(extnJoin);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        if (criteria != null) {
            query.setCriteria(criteria.and(userNotInTrashCriteria));
        }
        else {
            query.setCriteria(userNotInTrashCriteria);
        }
        final Column appGroupIDCol = Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID");
        final Column appGroupDisplayNameCol = Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME").maximum();
        final Column appGroupIdentifierCol = Column.getColumn("MdAppGroupDetails", "IDENTIFIER").maximum();
        final Column appGroupPlatformTypeCol = Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE").maximum();
        final Column appVersionCol = Column.getColumn("MdAppDetails", "APP_VERSION").maximum();
        final Column deviceNameCol = Column.getColumn("ManagedDeviceResource", "NAME").maximum();
        deviceNameCol.setColumnAlias("DeviceName");
        final Column customerIDCol = Column.getColumn("ManagedDeviceResource", "CUSTOMER_ID").maximum();
        final Column userNameCol = Column.getColumn("ManagedUserResource", "NAME").maximum();
        userNameCol.setColumnAlias("UserName");
        final Column appGroupAddedTimeCol = Column.getColumn("MdAppGroupDetails", "ADDED_TIME").minimum();
        appGroupDisplayNameCol.setColumnAlias("GROUP_DISPLAY_NAME");
        appGroupIdentifierCol.setColumnAlias("IDENTIFIER");
        appGroupPlatformTypeCol.setColumnAlias("PLATFORM_TYPE");
        appVersionCol.setColumnAlias("APP_VERSION");
        deviceNameCol.setColumnAlias("DeviceName");
        customerIDCol.setColumnAlias("CUSTOMER_ID");
        userNameCol.setColumnAlias("UserName");
        appGroupAddedTimeCol.setColumnAlias("ADDED_TIME");
        final Column customId = Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID");
        final Column customName = Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME");
        query.addSelectColumn(appGroupIDCol);
        query.addSelectColumn(appGroupDisplayNameCol);
        query.addSelectColumn(appGroupIdentifierCol);
        query.addSelectColumn(appGroupPlatformTypeCol);
        query.addSelectColumn(appVersionCol);
        query.addSelectColumn(deviceNameCol);
        query.addSelectColumn(customerIDCol);
        query.addSelectColumn(userNameCol);
        query.addSelectColumn(appGroupAddedTimeCol);
        query.addSelectColumn(customId);
        query.addSelectColumn(customName);
        final List list = new ArrayList();
        list.add(appGroupIDCol);
        list.add(customId);
        list.add(customName);
        final GroupByClause groupBy = new GroupByClause(list);
        query.setGroupByClause(groupBy);
        query.setRange(new Range(0, this.maxRange));
        final SortColumn sortCol = new SortColumn(appGroupIDCol, true);
        query.addSortColumn(sortCol);
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Properties prop = new Properties();
                ((Hashtable<String, Object>)prop).put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
                ((Hashtable<String, Object>)prop).put("GROUP_DISPLAY_NAME", ds.getValue("GROUP_DISPLAY_NAME"));
                ((Hashtable<String, Object>)prop).put("IDENTIFIER", ds.getValue("IDENTIFIER"));
                ((Hashtable<String, Object>)prop).put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                ((Hashtable<String, Object>)prop).put("APP_VERSION", ds.getValue("APP_VERSION"));
                ((Hashtable<String, Object>)prop).put("DeviceName", ds.getValue("DeviceName"));
                ((Hashtable<String, Object>)prop).put("MANAGEDDEVICEEXTN.NAME", ds.getValue("MANAGEDDEVICEEXTN.NAME"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                ((Hashtable<String, Object>)prop).put("UserName", ds.getValue("UserName"));
                ((Hashtable<String, Object>)prop).put("ADDED_TIME", ds.getValue("ADDED_TIME"));
                appDetails.add(prop);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppBlackList {0}", ex);
        }
        return appDetails;
    }
    
    private void addOrUpdateBlackListNotifiy(final DataObject doNotify, final Long resId, final Long appGroupId) throws Exception {
        final Long currentTime = System.currentTimeMillis();
        Row notifyRow = new Row("MdAppBlackListNotify");
        notifyRow.set("RESOURCE_ID", (Object)resId);
        notifyRow.set("APP_GROUP_ID", (Object)appGroupId);
        notifyRow = doNotify.getRow("MdAppBlackListNotify", notifyRow);
        if (notifyRow == null) {
            notifyRow = new Row("MdAppBlackListNotify");
            notifyRow.set("LAST_NOTIFIED_TIME", (Object)currentTime);
            notifyRow.set("NEXT_NOTIFICATION_TIME", (Object)(currentTime + 86400000L));
            notifyRow.set("NO_OF_NOTIFIED_COUNT", (Object)0);
            notifyRow.set("RESOURCE_ID", (Object)resId);
            notifyRow.set("APP_GROUP_ID", (Object)appGroupId);
            notifyRow.set("IS_EXPIRED", (Object)Boolean.FALSE);
            notifyRow.set("IS_EXPIRED_TIME", (Object)0L);
            doNotify.addRow(notifyRow);
        }
        else {
            final Integer notificationCount = (int)notifyRow.get("NO_OF_NOTIFIED_COUNT") + 1;
            notifyRow.set("LAST_NOTIFIED_TIME", (Object)currentTime);
            notifyRow.set("NEXT_NOTIFICATION_TIME", (Object)(currentTime + 86400000L));
            notifyRow.set("NO_OF_NOTIFIED_COUNT", (Object)notificationCount);
            if (notificationCount >= this.getNotificationCount(resId)) {
                notifyRow.set("IS_EXPIRED", (Object)Boolean.TRUE);
                notifyRow.set("IS_EXPIRED_TIME", (Object)currentTime);
            }
            doNotify.updateRow(notifyRow);
        }
    }
    
    private boolean enableBlackListNotify(final Long customerId) {
        Boolean notify = Boolean.FALSE;
        try {
            final Boolean black = (Boolean)DBUtil.getValueFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerId, "ENABLE_BLACKLIST_ALERT");
            if (black != null) {
                notify = black;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return notify;
    }
    
    private boolean enableNewAppListNotify(final Long customerId) {
        Boolean notify = Boolean.FALSE;
        try {
            final Boolean black = (Boolean)DBUtil.getValueFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerId, "ENABLE_APP_DISCOVERY_ALERT");
            if (black != null) {
                notify = black;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return notify;
    }
    
    public int getNotificationCount(final Long resId) {
        int notify = 5;
        Integer notification = null;
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resId);
            final Row blacklistSettings = DBUtil.getRowFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerId);
            final int agentType = ManagedDeviceHandler.getInstance().getAgentType(resId);
            switch (agentType) {
                case 3: {
                    final int ownerShip = ManagedDeviceHandler.getInstance().getDeviceOwnership(resId);
                    if (ownerShip == 1) {
                        notification = (Integer)blacklistSettings.get("CORP_EMAIL_NOTIFICATION_COUNT");
                        break;
                    }
                    if (ownerShip == 2) {
                        notification = (Integer)blacklistSettings.get("BYOD_EMAIL_NOTIFICATION_COUNT");
                        break;
                    }
                    break;
                }
                default: {
                    notification = (Integer)blacklistSettings.get("NOTIFICATION_COUNT");
                    break;
                }
            }
            if (notification != null) {
                notify = notification;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return notify;
    }
    
    public void sendAPNSExpiredMail(final Properties prop) throws Exception {
        final String emailAdrs = ((Hashtable<K, String>)prop).get("$user_emailid$");
        final String appleId = ((Hashtable<K, String>)prop).get("$apple_id$");
        final String subject = I18N.getMsg("dc.mdm.msg.apns_expired.content1", new Object[0]);
        final String content = I18N.getMsg("dc.mdm.msg.apns_expired.mailSubject", new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/enrollment/mdm_renew_apns_certificate.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue(), appleId });
        this.sendMail(emailAdrs, content, subject);
    }
    
    public void sendAPNSAboutToExpireMail(final Properties prop) throws Exception {
        final String emailAdrs = ((Hashtable<K, String>)prop).get("$user_emailid$");
        final String appleId = ((Hashtable<K, String>)prop).get("$apple_id$");
        final String subject = I18N.getMsg("dc.pm.msg.apns_expired.title", new Object[0]);
        final String content = I18N.getMsg("dc.mdm.msg.apns_about_to_expire.mailSubject", new Object[] { ((Hashtable<K, Object>)prop).get("$remaingDay$"), ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/enrollment/mdm_renew_apns_certificate.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue(), appleId });
        this.sendMail(emailAdrs, content, subject);
    }
    
    public void sendAETExpiredMail(final Properties prop) throws Exception {
        final String emailAdrs = ((Hashtable<K, String>)prop).get("$aet_user_emailid$");
        final String subject = I18N.getMsg("dc.mdm.msg.aet_expired.title", new Object[0]);
        final String content = I18N.getMsg("dc.mdm.msg.aetabouttoexpire.mailSubject", new Object[] { "<a href=\"" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/app_management/windows_app_management.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue() + "#Renewing_AET" + "\">" + I18N.getMsg("mdm.common.HERE", new Object[0]) + "</a>" });
        this.sendMail(emailAdrs, content, subject);
    }
    
    public void sendAETAboutToExpireMail(final Properties prop) throws Exception {
        final String emailAdrs = ((Hashtable<K, String>)prop).get("$aet_user_emailid$");
        final String subject = I18N.getMsg("dc.pm.msg.aetexpired.title", new Object[] { ((Hashtable<K, Object>)prop).get("$remaingDay$") });
        final String content = I18N.getMsg("dc.mdm.msg.aetabouttoexpire.mailSubject", new Object[] { "<a href=\"" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/app_management/windows_app_management.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue() + "#Renewing_AET" + "\">" + I18N.getMsg("mdm.common.HERE", new Object[0]) + "</a>" });
        this.sendMail(emailAdrs, content, subject);
    }
    
    public void sendCertAboutToExpireMail(final Properties prop) throws Exception {
        final String emailAdrs = ((Hashtable<K, String>)prop).get("$cert_user_emailid$");
        final String subject = I18N.getMsg("mdm.pm.msg.certaboutexpired.title", new Object[] { ((Hashtable<K, Object>)prop).get("$remaingDay$") });
        final String content = I18N.getMsg("dc.mdm.msg.certabouttoexpire.mailSubject", new Object[] { "<a href=\"" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/app_management/windows_app_management.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue() + "#Renewing_AET" + "\">" + I18N.getMsg("mdm.common.HERE", new Object[0]) + "</a>" });
        this.sendMail(emailAdrs, content, subject);
    }
    
    public void sendCertExpiredMail(final Properties prop) throws Exception {
        final String emailAdrs = ((Hashtable<K, String>)prop).get("$cert_user_emailid$");
        final String subject = I18N.getMsg("mdm.pm.msg.certexpired.title", new Object[0]);
        final String content = I18N.getMsg("dc.mdm.msg.certabouttoexpire.mailSubject", new Object[] { "<a href=\"" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/app_management/windows_app_management.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue() + "#Renewing_AET" + "\">" + I18N.getMsg("mdm.common.HERE", new Object[0]) + "</a>" });
        this.sendMail(emailAdrs, content, subject);
    }
    
    public void sendMail(final String emailId, final String content, final String subject) throws Exception {
        this.sendMail(emailId, content, subject, null, null);
    }
    
    public void sendMail(String emailId, final String content, final String subject, final List<String> attachmentFilePaths, final JSONObject additionalParams) throws Exception {
        if (emailId != null && emailId != "") {
            String serverURL = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
            if (!serverURL.startsWith("http")) {
                serverURL = "http://" + serverURL + "/";
            }
            final String footer = I18N.getMsg("dc.admin.backup_failed.mail.footer", new Object[] { serverURL, ProductUrlLoader.getInstance().getValue("displayname") });
            final Properties smtpProps = ApiFactoryProvider.getMailSettingAPI().getMailServerDetailsProps();
            final String fromName = smtpProps.getProperty("mail.fromName");
            final String fromAddress = smtpProps.getProperty("mail.fromAddress");
            emailId = emailId.replace(" ", "");
            final MailDetails mailDetails = new MailDetails(fromAddress, emailId);
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("$baseUrl$", serverURL);
            ((Hashtable<String, String>)prop).put("$productName$", ProductUrlLoader.getInstance().getValue("displayname"));
            final String description = ApiFactoryProvider.getMailSettingAPI().appendFooterNote(content, prop);
            mailDetails.bodyContent = description;
            mailDetails.senderDisplayName = fromName;
            mailDetails.subject = subject;
            if (attachmentFilePaths != null && !attachmentFilePaths.isEmpty()) {
                mailDetails.attachment = attachmentFilePaths.toArray(new String[1]);
            }
            if (additionalParams != null && additionalParams.length() > 0) {
                if (additionalParams.has("callbackHandler")) {
                    mailDetails.callBackHandler = additionalParams.getString("callbackHandler");
                    additionalParams.remove("callbackHandler");
                }
                mailDetails.additionalParams = additionalParams;
            }
            if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mailDetails, 0);
            }
            else {
                Logger.getLogger("MailQueueLog").log(Level.INFO, "Mail Server not configured:{0}", mailDetails);
            }
        }
    }
    
    public void processEndUserNextWarningNotification() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBlackListAppInResource"));
        final Join blAppNotifyJoin = new Join("MdBlackListAppInResource", "MdAppBlackListNotify", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
        final Criteria notifyExpired = new Criteria(Column.getColumn("MdAppBlackListNotify", "IS_EXPIRED"), (Object)Boolean.FALSE, 0);
        final Criteria timeElapsed = new Criteria(Column.getColumn("MdAppBlackListNotify", "NEXT_NOTIFICATION_TIME"), (Object)System.currentTimeMillis(), 7);
        sQuery.addJoin(blAppNotifyJoin);
        sQuery.setCriteria(notifyExpired.and(timeElapsed));
        sQuery.addSelectColumn(Column.getColumn("MdBlackListAppInResource", "*"));
        try {
            final DataObject notifyAppsDO = MDMUtil.getPersistence().get(sQuery);
            if (!notifyAppsDO.isEmpty()) {
                final Iterator appIterator = notifyAppsDO.getRows("MdBlackListAppInResource");
                while (appIterator.hasNext()) {
                    final Row appRow = appIterator.next();
                    appRow.set("IS_TAKEN_FOR_NOTIFY", (Object)Boolean.TRUE);
                    notifyAppsDO.updateRow(appRow);
                }
                MDMUtil.getPersistence().update(notifyAppsDO);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendVPPAboutToExpireMail(final Properties prop) throws Exception {
        final Long businessStoreID = ((Hashtable<K, Long>)prop).get("BUSINESSSTORE_ID");
        final String email = MDBusinessStoreUtil.getBusinessStoreAlertMailAddress(businessStoreID);
        final String locationName = ((Hashtable<K, String>)prop).get("LOCATION_NAME");
        final int remainingDays = ((Hashtable<K, Integer>)prop).get("remainingDays");
        final String org = ((Hashtable<K, String>)prop).get("org");
        final String portalUrl = ((Hashtable<K, String>)prop).get("portalUrl");
        final String productName = ProductUrlLoader.getInstance().getValue("productname");
        final String subject = I18N.getMsg("dc.mdm.msg.vpp_about_to_expire_mail_title", new Object[] { org, locationName }) + " | " + productName;
        String content = I18N.getMsg("dc.mdm.msg.vpp_about_to_expire_mail_content", new Object[] { org, locationName, remainingDays, portalUrl });
        content = MDMUtil.replaceProductUrlLoaderValuesinText(content, "aboutToExpireMail");
        if (email != null && !email.equals("")) {
            this.sendMail(email, content, subject);
        }
    }
    
    public void sendVPPExpireMail(final Properties prop) throws Exception {
        final Long businessStoreID = ((Hashtable<K, Long>)prop).get("BUSINESSSTORE_ID");
        final String locationName = ((Hashtable<K, String>)prop).get("LOCATION_NAME");
        final String org = ((Hashtable<K, String>)prop).get("org");
        final String portalUrl = ((Hashtable<K, String>)prop).get("portalUrl");
        final String email = MDBusinessStoreUtil.getBusinessStoreAlertMailAddress(businessStoreID);
        final String productName = ProductUrlLoader.getInstance().getValue("productname");
        final String subject = I18N.getMsg("dc.mdm.msg.vpp_expired_mail_title", new Object[] { org, locationName }) + " | " + productName;
        String content = I18N.getMsg("dc.mdm.msg.vpp_expired_mail_content", new Object[] { org, locationName, portalUrl });
        content = MDMUtil.replaceProductUrlLoaderValuesinText(content, "expireMail");
        if (email != null && !email.equals("")) {
            this.sendMail(email, content, subject);
        }
    }
    
    public void sendVPPRevokedMail(final Properties prop) throws Exception {
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final AlertMailGeneratorUtil mail = new AlertMailGeneratorUtil();
        final String email = mail.getCustomerEMailAddress(customerId, "MdM-VppAppAssgn");
        final String productName = ProductUrlLoader.getInstance().getValue("productname");
        final String subject = I18N.getMsg("dc.mdm.msg.vpp_revoke.title", new Object[0]) + " | " + productName;
        String content = I18N.getMsg("dc.mdm.msg.vpp_revoke.mail_content", new Object[0]);
        content = MDMUtil.replaceProductUrlLoaderValuesinText(content, "revokeMail");
        this.sendMail(email, content, subject);
    }
    
    public void sendDEPExpiryNotificationMailForCustomer(final Properties prop) throws Exception {
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final AlertMailGeneratorUtil mail = new AlertMailGeneratorUtil();
        final String email = mail.getCustomerEMailAddress(customerId, "MdM-DEP");
        final String subject = I18N.getMsg("dc.mdm.msg.dep_about_to_expire_mail_subject", new Object[0]);
        String content = I18N.getMsg("mdm.msg.dep_about_to_expire_mail_content", new Object[] { ((Hashtable<K, Object>)prop).get("serverRows") });
        content = MDMUtil.replaceProductUrlLoaderValuesinText(content, "expireMail");
        this.sendMail(email, content, subject);
    }
    
    public void sendLocationExportMail(final JSONObject requestJson) throws Exception {
        final JSONArray emailAddresses = requestJson.getJSONArray("EMAIL_ADDRESS_LIST");
        final Long userId = requestJson.getLong("USER_ID");
        final Long fromTime = Long.valueOf(String.valueOf(requestJson.get("SELECTED_FROM")));
        final DateFormat dateFormat = new SimpleDateFormat(DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", userId));
        dateFormat.setTimeZone(ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone());
        final String sFromTime = dateFormat.format(new Date(fromTime));
        String sEndTime = null;
        Long endTime = null;
        if (requestJson.has("SELECTED_TO") && !MDMStringUtils.isEmpty(String.valueOf(requestJson.get("SELECTED_TO"))) && !String.valueOf(requestJson.get("SELECTED_TO")).equalsIgnoreCase("-1")) {
            endTime = Long.valueOf(String.valueOf(requestJson.get("SELECTED_TO")));
        }
        else if (requestJson.has("EXPORT_HISTORY_TYPE")) {
            final Integer exportHistoryType = Integer.valueOf(String.valueOf(requestJson.get("EXPORT_HISTORY_TYPE")));
            if (exportHistoryType.equals(2)) {
                endTime = fromTime;
            }
        }
        Date endDate = new Date(endTime);
        endTime = CalendarUtil.getInstance().getStartTimeOfTheDay(CalendarUtil.getInstance().addDays(endDate, 1).getTime()).getTime();
        --endTime;
        endDate = new Date(endTime);
        sEndTime = dateFormat.format(endDate);
        final String[] i18nParams = { String.valueOf(requestJson.get("NAME")), sFromTime, sEndTime };
        final String subject = I18N.getMsg("mdm.mail.loc_export_subject", (Object[])i18nParams);
        final String content = I18N.getMsg("mdm.mail.loc_export_content", (Object[])i18nParams);
        final String filePath = String.valueOf(requestJson.get("EXPORT_FILE_LOC"));
        final JSONObject additionalParams = new JSONObject().put("readAttachmentsFromDFS", (Object)Boolean.TRUE);
        for (int index = 0; index < emailAddresses.length(); ++index) {
            this.sendMail(String.valueOf(emailAddresses.get(index)), content, subject, Arrays.asList(filePath), additionalParams);
        }
    }
    
    public void sendSupportLogMail(final String emailID, final ArrayList successDeviceList, final ArrayList attachments, final boolean readFromDFS) throws Exception {
        if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
            throw new Exception("Mail server not configured");
        }
        String deviceList = "<ul>";
        for (int i = 0; i < successDeviceList.size(); ++i) {
            final String deviceName = successDeviceList.get(i).toString();
            deviceList = deviceList + "<li>" + IAMEncoder.encodeHTML(deviceName) + "</li>";
        }
        final String senderName = ApiFactoryProvider.getMailSettingAPI().getMailServerDetailsProps().getProperty("mail.fromName");
        deviceList += "</ul>";
        final JSONObject additionalParams = new JSONObject().put("readAttachmentsFromDFS", readFromDFS);
        if (!readFromDFS && ApiFactoryProvider.getImplClassInstance("SUPPORT_LOG_MAIL_CALLBACK_HANDLER") != null) {
            additionalParams.put("callbackHandler", (Object)ProductClassLoader.getSingleImplProductClass("SUPPORT_LOG_MAIL_CALLBACK_HANDLER"));
            additionalParams.put("logFileToDelete", attachments.get(0));
        }
        final String subject = I18N.getMsg("mdm.support.mailsubject", new Object[0]);
        final String content = I18N.getMsg("mdm.support.mailcontent", new Object[] { deviceList, senderName });
        this.sendMail(emailID, content, subject, attachments, additionalParams);
    }
    
    static {
        MDMMailNotificationHandler.mailHandler = null;
    }
}
