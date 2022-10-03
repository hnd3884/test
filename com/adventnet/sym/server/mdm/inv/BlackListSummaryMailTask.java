package com.adventnet.sym.server.mdm.inv;

import java.util.Hashtable;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class BlackListSummaryMailTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public BlackListSummaryMailTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            final Criteria summaryEnabled = new Criteria(new Column("MdAppBlackListSetting", "ENABLE_SUMMARY_ALERT"), (Object)Boolean.TRUE, 0);
            final List customerList = DBUtil.getDistinctColumnValue("MdAppBlackListSetting", "CUSTOMER_ID", summaryEnabled);
            final Iterator item = customerList.iterator();
            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
            while (item.hasNext()) {
                final Long customerId = Long.parseLong(item.next());
                final Properties prop = new Properties();
                final String summaryDetails = this.getSummaryDetails(customerId);
                if (!summaryDetails.equals("")) {
                    ((Hashtable<String, String>)prop).put("$Expired_blacklist_app_summary$", summaryDetails);
                    mailGenerator.sendMail(MDMAlertConstants.BLACKLIST_SUMMARY_MAIL, "MdM-BlackListApp", customerId, prop);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getSummaryDetails(final Long customerId) {
        String sAppNames = "";
        try {
            final DataObject appdetails = this.getExpiredAppList(customerId);
            if (appdetails != null && !appdetails.isEmpty()) {
                final String xslFile = MDMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "MDM" + File.separator + "xsl" + File.separator + "ExpiredBlacklistAppSumry.xsl";
                final String xslFile2 = MDMUtil.createI18NxslFile(xslFile, "ExpiredBlacklistAppSumry-temp.xsl");
                final MailContentGeneratorUtil mailGenerator = new MailContentGeneratorUtil(this.logger);
                sAppNames = mailGenerator.getHTMLContent(xslFile2, appdetails, "ExpiredMail");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception while getting App Names", ex);
        }
        return sAppNames;
    }
    
    private DataObject getExpiredAppList(final Long customerId) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppCollectionStatus"));
        DataObject DO = null;
        final Join devResJoin = new Join("BlacklistAppCollectionStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, "BlacklistAppCollectionStatus", "ManagedDeviceResource", 2);
        final Join userRelJoin = new Join(new Table("Resource", "ManagedDeviceResource"), new Table("ManagedUserToDevice"), new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join userJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final Join userResJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "ManagedUserResource", 2);
        final Join extnJoin = new Join(new Table("Resource", "ManagedDeviceResource"), new Table("ManagedDeviceExtn"), new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        query.addJoin(devResJoin);
        query.addJoin(userRelJoin);
        query.addJoin(userJoin);
        query.addJoin(userResJoin);
        query.addJoin(extnJoin);
        final Join notifyJoin = new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join mdtogrorelJoin = new Join("BlacklistAppToCollection", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join installedAppJoin = new Join("MdAppToGroupRel", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        query.addJoin(notifyJoin);
        query.addJoin(mdtogrorelJoin);
        query.addJoin(installedAppJoin);
        final Join appJoin = new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        query.addJoin(appJoin);
        final JSONObject jsonObject = new BlacklistAppHandler().getBlackListAppSettings(customerId);
        final Integer numDays = jsonObject.getInt("NOTIFY_DAYS");
        final Criteria cCust = new Criteria(new Column("ManagedDeviceResource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria cExpired = new Criteria(new Column("BlacklistAppCollectionStatus", "NOTIFIED_COUNT"), (Object)numDays, 4);
        final Criteria cNotRemoved = new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 3 }, 4);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        query.setCriteria(cCust.and(cExpired).and(cNotRemoved).and(userNotInTrashCriteria));
        query.addSelectColumn(Column.getColumn("ManagedDeviceResource", "NAME", "DeviceName"));
        query.addSelectColumn(Column.getColumn("ManagedUserResource", "NAME", "UserName"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        query.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "LAST_NOTIFIED_TIME"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
        try {
            DO = MDMUtil.getPersistence().constructDataObject();
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Row mailRow = new Row("MDMExpiredAppSummaryMail");
                mailRow.set("DEVICE_NAME", (Object)ds.getValue("MANAGEDDEVICEEXTN.NAME"));
                mailRow.set("USER_NAME", (Object)ds.getValue("UserName"));
                mailRow.set("EMAIL_ADDRESS", (Object)ds.getValue("EMAIL_ADDRESS"));
                mailRow.set("APP_NAME", (Object)ds.getValue("APP_NAME"));
                mailRow.set("IDENTIFIER", (Object)ds.getValue("IDENTIFIER"));
                mailRow.set("APP_VERSION", (Object)ds.getValue("APP_VERSION"));
                mailRow.set("EXPIRED_TIME", (Object)MDMUtil.getDate((long)ds.getValue("LAST_NOTIFIED_TIME")));
                DO.addRow(mailRow);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppBlackList ", ex);
        }
        return DO;
    }
}
