package com.me.mdm.server.easmanagement;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.ActionInfo;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.persistence.WritableDataObject;
import javax.transaction.TransactionManager;
import com.adventnet.ds.query.Range;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.scheduler.SchedulerConstants;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.Utils;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.SortColumn;
import java.util.Properties;
import com.adventnet.persistence.Row;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;

public class EASMgmtDataHandler
{
    private static EASMgmtDataHandler easMgmtDataHandler;
    
    public static EASMgmtDataHandler getInstance() {
        if (EASMgmtDataHandler.easMgmtDataHandler == null) {
            EASMgmtDataHandler.easMgmtDataHandler = new EASMgmtDataHandler();
        }
        return EASMgmtDataHandler.easMgmtDataHandler;
    }
    
    private boolean isEASconfigured(final Long customerID) throws DataAccessException {
        boolean isExchangeProfileConfig = false;
        final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConfigData"));
        final Join cfgDataCollnJoin = new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
        final Join collnCusJoin = new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        profileQuery.addJoin(cfgDataCollnJoin);
        profileQuery.addJoin(collnCusJoin);
        profileQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_DATA_ID"));
        profileQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_ID"));
        profileQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"));
        profileQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToCustomerRel", "COLLECTION_ID"));
        profileQuery.addSelectColumn(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"));
        final Criteria cusCri = new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria exchangeProfileIOSCri = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)175, 0);
        final Criteria exchangeProfileAndroidCri = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)554, 0);
        final Criteria exchangeProfileWindowsCri = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)603, 0);
        final Criteria cri = cusCri.and(exchangeProfileIOSCri.or(exchangeProfileAndroidCri).or(exchangeProfileWindowsCri));
        profileQuery.setCriteria(cri);
        final DataObject dObj = MDMUtil.getPersistence().get(profileQuery);
        if (dObj != null && !dObj.isEmpty()) {
            isExchangeProfileConfig = true;
        }
        return isExchangeProfileConfig;
    }
    
    public boolean hideOrShowEASNotConfiguredNotification(final Long customerID) {
        boolean isShowEASNotification = false;
        try {
            final Integer defaultAccessLevel = (Integer)this.getExchangeServerDetails(false).get((Object)"DEFAULT_ACCESS_LEVEL");
            isShowEASNotification = (this.isEASconfigured(customerID) && defaultAccessLevel == null);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return isShowEASNotification;
    }
    
    public boolean showEASandroidMsg(final Long customerID) {
        return false;
    }
    
    public boolean showCEAerrorMsg(final Long customerID) {
        return false;
    }
    
    public boolean showCEAnotFullyAppliedMsg(final Long customerID) {
        return false;
    }
    
    public boolean showCEApsUpgradeMsg(final Long customerID) {
        return false;
    }
    
    public boolean showCEADeviceDeleteLimitReachedMsg(final Long customerID) {
        return false;
    }
    
    public void mapDomainNameToCEA(Long esServerID) {
        try {
            final JSONObject exchangeServerDetails = this.getExchangeServerDetails(false);
            final Long cusomterID = (Long)exchangeServerDetails.get((Object)"CUSTOMER_ID");
            if (esServerID == null) {
                esServerID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
            }
            String domainName = null;
            final String userName = (String)exchangeServerDetails.get((Object)"EAS_ADMIN_EMAIL");
            if (userName.contains("\\")) {
                domainName = userName.substring(0, userName.indexOf("\\"));
            }
            if (MDMStringUtils.isEmpty(domainName)) {
                final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)cusomterID, 0);
                final Criteria upnCri = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)112L, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)userName, 0, false));
                final Criteria emailCri = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)106L, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)userName, 0, false));
                final Criteria samCri = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)107L, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)userName, 0, false));
                final Criteria resNameCri = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)2L, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)userName, 0, false));
                final SelectQuery query = DirectoryQueryutil.getInstance().getDirObjAttrQuery(custCri.and(upnCri.or(emailCri).or(samCri).or(resNameCri)));
                query.addJoin(new Join("DirObjRegStrVal", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                query.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
                final DataObject dObj = SyMUtil.getPersistence().get(query);
                if (dObj != null && !dObj.isEmpty() && dObj.containsTable("Resource")) {
                    final Row row = dObj.getFirstRow("Resource");
                    domainName = (String)row.get("DOMAIN_NETBIOS_NAME");
                }
            }
            else {
                final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProp(domainName, cusomterID, (List)new ArrayList(Arrays.asList(3, 2)));
                if (dmDomainProps != null && dmDomainProps.containsKey("NAME")) {
                    domainName = dmDomainProps.getProperty("NAME");
                }
            }
            if (!MDMStringUtils.isEmpty(domainName)) {
                final JSONObject exchangeServerDomainDetails = new JSONObject();
                exchangeServerDomainDetails.put((Object)"DOMAIN", (Object)domainName);
                exchangeServerDomainDetails.put((Object)"EAS_SERVER_ID", (Object)esServerID);
                this.addOrUpdateEASServerDetails(exchangeServerDomainDetails);
            }
        }
        catch (final Exception e) {
            EASMgmt.logger.log(Level.SEVERE, null, e);
        }
    }
    
    public Integer getIntegerDefaultAccessLevel(final String defaultAccessLevelValue) {
        switch (defaultAccessLevelValue) {
            case "Block": {
                return 1;
            }
            case "Allow": {
                return 0;
            }
            case "Quarantine": {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public String getStringDefaultAccessLevel(final Integer defaultAccessLevelValue) {
        if (defaultAccessLevelValue == null || defaultAccessLevelValue == -1) {
            return "NULL";
        }
        switch (defaultAccessLevelValue) {
            case 1: {
                return "Block";
            }
            case 0: {
                return "Allow";
            }
            case 2: {
                return "Quarantine";
            }
            default: {
                return "Allow";
            }
        }
    }
    
    public SelectQuery getSelectedMailboxQuery(final JSONObject exsServerDetailsJSON) {
        final Integer appliedFor = (Integer)exsServerDetailsJSON.get((Object)"APPLIED_FOR");
        final Long esServerID = (Long)exsServerDetailsJSON.get((Object)"EAS_SERVER_ID");
        final Long lastSuccessfulSyncTask = (Long)exsServerDetailsJSON.get((Object)"LAST_SUCCESSFUL_SYNC_TASK");
        final Column easMailbox = new Column("EASMailboxDetails", "EAS_MAILBOX_ID");
        easMailbox.setColumnAlias("easMailbx");
        final Column easSelectedMailbox = new Column("EASSelectedMailbox", "EAS_MAILBOX_ID");
        easSelectedMailbox.setColumnAlias("easSelectedMailbox");
        final SelectQuery selectedMailboxQuery = SyMUtil.formSelectQuery("EASMailboxDetails", new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)esServerID, 0).and(new Criteria(easSelectedMailbox, (Object)null, (int)((appliedFor == null || !appliedFor.equals(1)) ? 1 : 0))), new ArrayList((Collection<? extends E>)Arrays.asList(easMailbox, easSelectedMailbox, Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"), Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"))), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new SortColumn(easMailbox, true))), new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASMailboxDetails", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, (appliedFor == null || !appliedFor.equals(1)) ? 2 : 1))), (Criteria)null);
        if (lastSuccessfulSyncTask != null) {
            Criteria queryCri = selectedMailboxQuery.getCriteria();
            queryCri = queryCri.and(new Criteria(Column.getColumn("EASMailboxDetails", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTask, 4));
            selectedMailboxQuery.setCriteria(queryCri);
        }
        return selectedMailboxQuery;
    }
    
    public SelectQuery getSelectedMailboxQuery() {
        final JSONObject exServerDetailsJSON = getInstance().getExchangeServerDetails(false);
        return this.getSelectedMailboxQuery(exServerDetailsJSON);
    }
    
    public JSONObject getCEASummary(final JSONObject esServerDetails) {
        JSONObject summaryResult = new JSONObject();
        try {
            final Integer appliedFor = (Integer)esServerDetails.get((Object)"APPLIED_FOR");
            final Long esServerID = (Long)esServerDetails.get((Object)"EAS_SERVER_ID");
            final Long lastSuccessfulSyncTime = (Long)esServerDetails.get((Object)"LAST_SUCCESSFUL_SYNC_TASK");
            final SelectQuery ceaSummaryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EASServerDetails"));
            ceaSummaryQuery.addJoin(new Join("EASServerDetails", "EASMailboxDetails", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2));
            ceaSummaryQuery.addJoin(new Join("EASMailboxDetails", "EASMailboxDeviceRel", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("EASMailboxDeviceRel", "EASDeviceDetails", new String[] { "EAS_DEVICE_ID" }, new String[] { "EAS_DEVICE_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("EASMailboxDeviceRel", "EASMailboxDeviceInfo", new String[] { "EAS_MAILBOX_DEVICE_ID" }, new String[] { "EAS_MAILBOX_DEVICE_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("EASDeviceDetails", "EASManagedDeviceRel", new String[] { "EAS_DEVICE_ID" }, new String[] { "EAS_DEVICE_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("EASManagedDeviceRel", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            ceaSummaryQuery.addJoin(new Join("EASMailboxDetails", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 1));
            final Criteria mailboxCri = new Criteria(Column.getColumn("EASMailboxDetails", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTime, 4);
            final Criteria baseCri = new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)esServerID, 0).and(mailboxCri).and(new Criteria(Column.getColumn("EASMailboxDeviceRel", "ACTION_STATUS"), (Object)1, 1).or(new Criteria(Column.getColumn("EASMailboxDeviceRel", "ACTION_STATUS"), (Object)null, 0)));
            final Criteria syncedMailboxDeviceCri = new Criteria(Column.getColumn("EASMailboxDeviceRel", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTime, 4);
            final Criteria selectedMailboxCri = new Criteria(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"), (Object)null, (int)((appliedFor == null || !appliedFor.equals(1)) ? 1 : 0));
            final Criteria managedDeviceCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0).and(new Criteria(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), (Object)Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), 0, false));
            ceaSummaryQuery.setCriteria(baseCri);
            final CaseExpression syncedMailboxExp = new CaseExpression("syncUserCount");
            syncedMailboxExp.addWhen(mailboxCri, (Object)Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
            final Column mailboxCountColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(syncedMailboxExp, 4, "syncUserCount");
            final CaseExpression selectedMailboxExp = new CaseExpression("selectedMailboxCount");
            selectedMailboxExp.addWhen(selectedMailboxCri, (Object)Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
            final Column selectedMailboxCountColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(selectedMailboxExp, 4, "selectedMailboxCount");
            final Criteria blockDeviceCri = selectedMailboxCri.and(syncedMailboxDeviceCri).and(new Criteria(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), (Object)0, 1));
            final CaseExpression blockDeviceExp = new CaseExpression("blockDeviceCount");
            blockDeviceExp.addWhen(blockDeviceCri, (Object)Column.getColumn("EASMailboxDeviceInfo", "EAS_MAILBOX_DEVICE_ID"));
            final Column blockDeviceCountColumn = MDMUtil.getInstance().getCountCaseExpressionColumn(blockDeviceExp, 4, "blockDeviceCount");
            final Criteria allowDeviceCri = selectedMailboxCri.and(managedDeviceCri).and(syncedMailboxDeviceCri).and(new Criteria(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), (Object)0, 0));
            final CaseExpression allowDeviceExp = new CaseExpression("allowedDeviceCount");
            allowDeviceExp.addWhen(allowDeviceCri, (Object)Column.getColumn("EASMailboxDeviceInfo", "EAS_MAILBOX_DEVICE_ID"));
            final Column allowDeviceCountColumn = MDMUtil.getInstance().getCountCaseExpressionColumn(allowDeviceExp, 4, "allowedDeviceCount");
            final Criteria notManagedCri = managedDeviceCri.negate().or(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0));
            final Criteria graceDeviceCri = selectedMailboxCri.and(notManagedCri).and(syncedMailboxDeviceCri).and(new Criteria(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), (Object)0, 0));
            final CaseExpression graceDeviceExp = new CaseExpression("graceDeviceCount");
            graceDeviceExp.addWhen(graceDeviceCri, (Object)Column.getColumn("EASMailboxDeviceInfo", "EAS_MAILBOX_DEVICE_ID"));
            final Column graceDeviceCountColumn = MDMUtil.getInstance().getCountCaseExpressionColumn(graceDeviceExp, 4, "graceDeviceCount");
            final CaseExpression managedDeviceExp = new CaseExpression("managedDeviceCount");
            managedDeviceExp.addWhen(managedDeviceCri, (Object)Column.getColumn("EASManagedDeviceRel", "MANAGED_DEVICE_ID"));
            final Column managedDeviceCountColumn = MDMUtil.getInstance().getCountCaseExpressionColumn(managedDeviceExp, 4, "managedDeviceCount");
            final CaseExpression syncedDeviceExp = new CaseExpression("syncDeviceCount");
            syncedDeviceExp.addWhen(syncedMailboxDeviceCri, (Object)Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_DEVICE_ID"));
            final Column easMailboxDeviceCountColumn = MDMUtil.getInstance().getCountCaseExpressionColumn(syncedDeviceExp, 4, "syncDeviceCount");
            ceaSummaryQuery.addSelectColumn(mailboxCountColumn);
            ceaSummaryQuery.addSelectColumn(blockDeviceCountColumn);
            ceaSummaryQuery.addSelectColumn(allowDeviceCountColumn);
            ceaSummaryQuery.addSelectColumn(graceDeviceCountColumn);
            ceaSummaryQuery.addSelectColumn(managedDeviceCountColumn);
            ceaSummaryQuery.addSelectColumn(selectedMailboxCountColumn);
            ceaSummaryQuery.addSelectColumn(easMailboxDeviceCountColumn);
            final JSONArray res = MDMUtil.executeSelectQuery(ceaSummaryQuery);
            if (res.size() > 0) {
                summaryResult = (JSONObject)res.get(0);
                if (summaryResult != null && !summaryResult.isEmpty()) {
                    final int syncDeviceCount = MDMUtil.getInstance().getIntVal(summaryResult.get((Object)"syncDeviceCount"));
                    final int managedDeviceCount = MDMUtil.getInstance().getIntVal(summaryResult.get((Object)"managedDeviceCount"));
                    summaryResult.put((Object)"unManagedDeviceCount", (Object)(syncDeviceCount - managedDeviceCount));
                }
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return summaryResult;
    }
    
    public SelectQuery getExchangeServerDetailsQuery() {
        return SyMUtil.formSelectQuery("EASServerDetails", (Criteria)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Column((String)null, "*"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASServerDetails", "EASPolicy", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 1), new Join("EASServerDetails", "EASSyncStatus", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_Sync_Status_ID" }, 1), new Join("EASSyncStatus", "ErrorCode", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 1))), (Criteria)null);
    }
    
    public JSONObject getExchangeServerDetails(final boolean getPassword) {
        final JSONObject exchangeServerDetails = new JSONObject();
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(this.getExchangeServerDetailsQuery());
            if (dataObject != null && !dataObject.isEmpty()) {
                final List<String> tableNames = dataObject.getTableNames();
                for (final String tableName : tableNames) {
                    final Row row = dataObject.getRow(tableName);
                    final List<String> tableColumns = row.getColumns();
                    for (final String columnName : tableColumns) {
                        exchangeServerDetails.put((Object)columnName, row.get(columnName));
                    }
                }
                final String defaultAccessLevel = String.valueOf(exchangeServerDetails.getOrDefault((Object)"DEFAULT_ACCESS_LEVEL", (Object)null));
                if (SyMUtil.isStringEmpty(defaultAccessLevel)) {
                    exchangeServerDetails.put((Object)"EXCHANGE_SERVER_VERSION", (Object)null);
                }
                Long lastSuccessfulSync = null;
                if (exchangeServerDetails.containsKey((Object)"LAST_SUCCESSFUL_SYNC_TASK")) {
                    lastSuccessfulSync = (Long)exchangeServerDetails.get((Object)"LAST_SUCCESSFUL_SYNC_TASK");
                    if (lastSuccessfulSync != null) {
                        exchangeServerDetails.put((Object)"LAST_SUCCESSFUL_SYNC_DATE_TIME", (Object)Utils.getEventTime(lastSuccessfulSync));
                    }
                }
                if (!getPassword) {
                    exchangeServerDetails.remove((Object)"EAS_ADMIN_PASSWORD");
                }
                exchangeServerDetails.remove((Object)"ENFORCE_CONDITIONAL_ACCESS");
                exchangeServerDetails.remove((Object)"N_EAS_ADMIN_PASSWORD");
                exchangeServerDetails.putAll((Map)this.getCEASummary(exchangeServerDetails));
                exchangeServerDetails.put((Object)"isDemoMode", (Object)String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode()));
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return exchangeServerDetails;
    }
    
    public JSONObject getPolicySelectedOrExcludedMailboxes(final JSONObject exServerDetailsJSON) {
        final JSONObject policyMailboxes = new JSONObject();
        final JSONObject ceaSelectedMailboxJSON = new JSONObject();
        try {
            if (exServerDetailsJSON.containsKey((Object)"EAS_POLICY_ID") && exServerDetailsJSON.get((Object)"EAS_POLICY_ID") != null) {
                final Long esServerID = (Long)exServerDetailsJSON.get((Object)"EAS_SERVER_ID");
                final DataObject easMailDO = MDMUtil.getPersistence().get(MDMUtil.formSelectQuery("EASMailboxDetails", new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)esServerID, 0), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"), Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASMailboxDetails", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2))), (Criteria)null));
                final Iterator mailPolicyItr = easMailDO.getRows("EASMailboxDetails");
                while (mailPolicyItr.hasNext()) {
                    final Row easMailRow = mailPolicyItr.next();
                    final JSONObject easMailPropJSON = new JSONObject();
                    easMailPropJSON.put((Object)"NODE_ID", (Object)String.valueOf(easMailRow.get("EAS_MAILBOX_ID")));
                    easMailPropJSON.put((Object)"NODE_NAME", (Object)String.valueOf(easMailRow.get("DISPLAY_NAME")));
                    easMailPropJSON.put((Object)"NODE_STYLE", (Object)"background-color: #eee;color: #000; font:12px 'Lato', 'Roboto', sans-serif; text-decoration:none;");
                    ceaSelectedMailboxJSON.put((Object)String.valueOf(easMailRow.get("EAS_MAILBOX_ID")), (Object)easMailPropJSON);
                }
            }
        }
        catch (final DataAccessException ex) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        policyMailboxes.put((Object)"EASSelectedMailbox", (Object)ceaSelectedMailboxJSON);
        return policyMailboxes;
    }
    
    public String getCEArollbackInstalledDate() {
        try {
            final String rollbackInitDate = MDMUtil.getSyMParameter("ROLLBACK_BLOCKED_DEVICES");
            if (SyMUtil.isStringValid(rollbackInitDate)) {
                return Utils.getEventDate(Long.valueOf(rollbackInitDate));
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public JSONObject getEnrollmentMailboxDetails(final String mailboxEmailAddress, final JSONObject exServerDetails) throws DataAccessException {
        final JSONObject mailboxDetails = new JSONObject();
        final Integer graceDays = (Integer)exServerDetails.get((Object)"GRACE_DAYS");
        final Integer policyApplicableFor = (Integer)exServerDetails.get((Object)"APPLIED_FOR");
        Long gracePeriodStart = null;
        Long easSelectedMailboxId = null;
        final SelectQuery selectQuery = this.getSelectedMailboxQuery(exServerDetails);
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), (Object)mailboxEmailAddress, 0, false)));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxGracePeriod", "GRACE_PERIOD_START"));
        selectQuery.addJoin(new Join("EASMailboxDetails", "EASMailboxGracePeriod", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 1));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty()) {
            final Row easMailboxRow = dataObject.getRow("EASMailboxDetails");
            Long easMailboxId = null;
            if (easMailboxRow != null) {
                easMailboxId = (Long)easMailboxRow.get("EAS_MAILBOX_ID");
            }
            if (easMailboxId != null) {
                final Row easGracePeriodMailRow = dataObject.getRow("EASMailboxGracePeriod", new Criteria(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"), (Object)easMailboxId, 0));
                if (easGracePeriodMailRow != null) {
                    gracePeriodStart = (Long)easGracePeriodMailRow.get("GRACE_PERIOD_START");
                }
                final Row easSelectedMailboxRow = dataObject.getRow("EASSelectedMailbox", new Criteria(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"), (Object)easMailboxId, 0));
                if (easSelectedMailboxRow != null) {
                    easSelectedMailboxId = (Long)easSelectedMailboxRow.get("EAS_MAILBOX_ID");
                }
            }
        }
        if (gracePeriodStart != null) {
            mailboxDetails.put((Object)"IS_MANAGED_USER_MAILBOX_EAS_GRACED", (Object)(gracePeriodStart + TimeUnit.DAYS.toMillis(graceDays) > System.currentTimeMillis()));
        }
        else if (policyApplicableFor == 1) {
            mailboxDetails.put((Object)"IS_MANAGED_USER_MAILBOX_EAS_GRACED", (Object)true);
        }
        else {
            mailboxDetails.put((Object)"IS_MANAGED_USER_MAILBOX_EAS_GRACED", (Object)false);
        }
        mailboxDetails.put((Object)"IS_MANAGED_USER_MAILBOX_EAS_SELECTED", (Object)(easSelectedMailboxId == null == (policyApplicableFor == 1)));
        return mailboxDetails;
    }
    
    public Integer getErrorCode(final String exceptionType) {
        EASMgmt.logger.log(Level.INFO, "have to get error code for {0}", exceptionType);
        Integer errorId = 51209;
        Integer tempErrorID = null;
        try {
            tempErrorID = (Integer)DBUtil.getValueFromDB("ErrorCode", "SHORT_DESC", (Object)exceptionType, "ERROR_CODE");
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        if (tempErrorID != null) {
            errorId = tempErrorID;
        }
        return errorId;
    }
    
    private DataObject waitUntilMDDeviceInfoIsUpdated(final Long resourceID, final int waitseconds) {
        DataObject mdeviceInfoDO = null;
        int sleepCount = 0;
        while ((mdeviceInfoDO == null || mdeviceInfoDO.isEmpty()) && !resourceID.equals(0L) && sleepCount < waitseconds) {
            EASMgmt.logger.log(Level.INFO, "waiting for {0} to be updated", resourceID);
            try {
                mdeviceInfoDO = SyMUtil.getPersistence().get("MdDeviceInfo", new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0));
                if (!mdeviceInfoDO.isEmpty()) {
                    continue;
                }
                EASMgmt.logger.log(Level.WARNING, "sleeping beauty sleeping for one second until prince charming comes and updates MDDeviceINFO table.. lets hope it is updated by next loop");
                Thread.sleep(1000L);
                ++sleepCount;
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, "MDDeviceInfo still not updated", ex);
            }
        }
        return mdeviceInfoDO;
    }
    
    public String getMDDeviceInfoEasDeviceIdentifer(final Long resourceID, final int waitSeconds) {
        try {
            final DataObject dataObject = this.waitUntilMDDeviceInfoIsUpdated(resourceID, waitSeconds);
            final Row mdDeviceInfoRow = dataObject.getRow("MdDeviceInfo");
            final String mdDeviceInfoEASDeviceIdentifier = (String)mdDeviceInfoRow.get("EAS_DEVICE_IDENTIFIER");
            if (SyMUtil.isStringValid(mdDeviceInfoEASDeviceIdentifier) && mdDeviceInfoEASDeviceIdentifier.length() > 3) {
                return mdDeviceInfoEASDeviceIdentifier;
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public JSONObject getPolicyDeviatingCombination(final Long esServerID) {
        Boolean needToPerformConditionalAccess = false;
        final JSONObject retJsObject = new JSONObject();
        retJsObject.put((Object)"NEED_TO_PERFORM_CONDITIONAL_ACCESS", (Object)needToPerformConditionalAccess);
        try {
            final JSONObject exsServerDetailsJSON = getInstance().getExchangeServerDetails(false);
            if (exsServerDetailsJSON.get((Object)"EAS_POLICY_ID") == null) {
                return retJsObject;
            }
            final Integer graceDays = (Integer)exsServerDetailsJSON.get((Object)"GRACE_DAYS");
            final Integer appliedFor = (Integer)exsServerDetailsJSON.get((Object)"APPLIED_FOR");
            final Integer defaultAccessLevel = (Integer)exsServerDetailsJSON.get((Object)"DEFAULT_ACCESS_LEVEL");
            final Long lastSuccessfulSyncTask = (Long)exsServerDetailsJSON.get((Object)"LAST_SUCCESSFUL_SYNC_TASK");
            final Long graceDaysinMillis = TimeUnit.DAYS.toMillis(graceDays);
            retJsObject.put((Object)"CUSTOMER_ID", exsServerDetailsJSON.get((Object)"CUSTOMER_ID"));
            Criteria criteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)esServerID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"), (Object)null, (int)((appliedFor != 1) ? 1 : 0)));
            if (lastSuccessfulSyncTask != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("EASDeviceDetails", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTask, 4));
                criteria = criteria.and(new Criteria(Column.getColumn("EASMailboxDetails", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTask, 4));
                criteria = criteria.and(new Criteria(Column.getColumn("EASMailboxDeviceRel", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTask, 4));
            }
            final DataObject dObj = SyMUtil.getPersistence().get(SyMUtil.formSelectQuery("EASMailboxDetails", criteria, new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"), Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"), Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"), Column.getColumn("EASMailboxGracePeriod", "GRACE_PERIOD_START"), Column.getColumn("EASMailboxDeviceRel", "EAS_DEVICE_ID"), Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_ID"), Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_DEVICE_ID"), Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), Column.getColumn("EASMailboxDeviceInfo", "EAS_MAILBOX_DEVICE_ID"), Column.getColumn("EASDeviceDetails", "DEVICE_OS"), Column.getColumn("EASDeviceDetails", "DEVICE_IMEI"), Column.getColumn("EASDeviceDetails", "DEVICE_NAME"), Column.getColumn("EASDeviceDetails", "DEVICE_MODEL"), Column.getColumn("EASDeviceDetails", "EAS_DEVICE_ID"), Column.getColumn("EASDeviceDetails", "EAS_DEVICE_IDENTIFIER"), Column.getColumn("EASManagedDeviceRel", "EAS_DEVICE_ID"), Column.getColumn("EASManagedDeviceRel", "MANAGED_DEVICE_ID"), Column.getColumn("ManagedDevice", "RESOURCE_ID"), Column.getColumn("ManagedDevice", "MANAGED_STATUS"), Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), Column.getColumn("ManagedUser", "MANAGED_USER_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASMailboxDetails", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 1), new Join("EASMailboxDetails", "EASMailboxDeviceRel", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2), new Join("EASMailboxDetails", "EASMailboxGracePeriod", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2), new Join("EASMailboxDeviceRel", "EASDeviceDetails", new String[] { "EAS_DEVICE_ID" }, new String[] { "EAS_DEVICE_ID" }, 2), new Join("EASMailboxDeviceRel", "EASMailboxDeviceInfo", new String[] { "EAS_MAILBOX_DEVICE_ID" }, new String[] { "EAS_MAILBOX_DEVICE_ID" }, 2), new Join("EASDeviceDetails", "EASManagedDeviceRel", new String[] { "EAS_DEVICE_ID" }, new String[] { "EAS_DEVICE_ID" }, 1), new Join("EASManagedDeviceRel", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1), new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1), new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1))), (Criteria)null));
            final JSONArray jsArray = new JSONArray();
            final Iterator iterator = dObj.getRows("EASMailboxDetails");
            while (iterator != null && iterator.hasNext()) {
                final Row easMailboxRow = iterator.next();
                final Long easMailboxID = (Long)easMailboxRow.get("EAS_MAILBOX_ID");
                final String userDisplayName = (String)easMailboxRow.get("DISPLAY_NAME");
                final String emailAddress = (String)easMailboxRow.get("EMAIL_ADDRESS");
                final Row easMailboxGracePeriodRow = dObj.getRow("EASMailboxGracePeriod", new Criteria(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"), (Object)easMailboxID, 0));
                final Long gracePeriodStart = (Long)easMailboxGracePeriodRow.get("GRACE_PERIOD_START");
                final Long gracePeriodExpiresIn = graceDaysinMillis - (System.currentTimeMillis() - gracePeriodStart);
                final Boolean underGracePeriod = gracePeriodExpiresIn > 0L;
                final JSONArray mailboxAllowedList = new JSONArray();
                final JSONArray mailboxNonAllowedList = new JSONArray();
                final JSONArray mailboxBlockedList = new JSONArray();
                final Iterator deviceIterator = dObj.getRows("EASMailboxDeviceRel", new Criteria(Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_ID"), (Object)easMailboxID, 0));
                while (deviceIterator != null && deviceIterator.hasNext()) {
                    final Row easMailboxDeviceRelRow = deviceIterator.next();
                    final Long easMailboxDeviceID = (Long)easMailboxDeviceRelRow.get("EAS_MAILBOX_DEVICE_ID");
                    final Long easDeviceID = (Long)easMailboxDeviceRelRow.get("EAS_DEVICE_ID");
                    final Row easMailboxDeviceInfoRow = dObj.getRow("EASMailboxDeviceInfo", new Criteria(Column.getColumn("EASMailboxDeviceInfo", "EAS_MAILBOX_DEVICE_ID"), (Object)easMailboxDeviceID, 0));
                    final Integer deviceAccessState = (Integer)easMailboxDeviceInfoRow.get("DEVICE_ACCESS_STATE");
                    final Row easDeviceRow = dObj.getRow("EASDeviceDetails", new Criteria(Column.getColumn("EASDeviceDetails", "EAS_DEVICE_ID"), (Object)easDeviceID, 0));
                    final JSONObject deviceJSON = new JSONObject();
                    deviceJSON.put((Object)"DEVICE_OS", easDeviceRow.get("DEVICE_OS"));
                    deviceJSON.put((Object)"DEVICE_NAME", easDeviceRow.get("DEVICE_NAME"));
                    deviceJSON.put((Object)"DEVICE_IMEI", easDeviceRow.get("DEVICE_IMEI"));
                    deviceJSON.put((Object)"DEVICE_MODEL", easDeviceRow.get("DEVICE_MODEL"));
                    deviceJSON.put((Object)"EAS_DEVICE_IDENTIFIER", easDeviceRow.get("EAS_DEVICE_IDENTIFIER"));
                    Long managedDeviceID = null;
                    Integer managedDeviceStatus = null;
                    String managedUserEmailAddress = null;
                    final Row easManagedDeviceRelRow = dObj.getRow("EASManagedDeviceRel", new Criteria(Column.getColumn("EASManagedDeviceRel", "EAS_DEVICE_ID"), (Object)easDeviceID, 0));
                    if (easManagedDeviceRelRow != null) {
                        managedDeviceID = (Long)easManagedDeviceRelRow.get("MANAGED_DEVICE_ID");
                        if (managedDeviceID != null) {
                            final Row managedDeviceRow = dObj.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
                            managedDeviceStatus = (Integer)managedDeviceRow.get("MANAGED_STATUS");
                            final Row manaagedUserToDeviceRow = dObj.getRow("ManagedUserToDevice", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)managedDeviceID, 0));
                            final Long managedUserID = (Long)manaagedUserToDeviceRow.get("MANAGED_USER_ID");
                            final Row managedUserRow = dObj.getRow("ManagedUser", new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserID, 0));
                            managedUserEmailAddress = (String)managedUserRow.get("EMAIL_ADDRESS");
                        }
                    }
                    if (managedDeviceID != null && managedDeviceStatus != null && managedDeviceStatus == 2 && managedUserEmailAddress != null && emailAddress.equalsIgnoreCase(managedUserEmailAddress)) {
                        if (deviceAccessState == 0) {
                            continue;
                        }
                        mailboxAllowedList.add((Object)deviceJSON);
                        needToPerformConditionalAccess = true;
                    }
                    else if (defaultAccessLevel == 0) {
                        if (deviceAccessState == 1) {
                            continue;
                        }
                        mailboxBlockedList.add((Object)deviceJSON);
                        if (underGracePeriod) {
                            continue;
                        }
                        needToPerformConditionalAccess = true;
                    }
                    else {
                        if (deviceAccessState != 0) {
                            continue;
                        }
                        mailboxNonAllowedList.add((Object)deviceJSON);
                        if (underGracePeriod) {
                            continue;
                        }
                        needToPerformConditionalAccess = true;
                    }
                }
                final JSONObject mailboxEntry = new JSONObject();
                if (mailboxAllowedList.size() > 0 || mailboxBlockedList.size() > 0 || mailboxNonAllowedList.size() > 0) {
                    mailboxEntry.put((Object)"EMAIL_ADDRESS", (Object)emailAddress);
                    mailboxEntry.put((Object)"DISPLAY_NAME", (Object)userDisplayName);
                    mailboxEntry.put((Object)"TO_BE_ALLOWED", (Object)mailboxAllowedList);
                    mailboxEntry.put((Object)"TO_BE_BLOCKED", (Object)mailboxBlockedList);
                    mailboxEntry.put((Object)"TO_BE_NOT_ALOLWED", (Object)mailboxNonAllowedList);
                    mailboxEntry.put((Object)"$days_to_expire$", (Object)gracePeriodExpiresIn);
                    mailboxEntry.put((Object)"IS_MANAGED_USER_MAILBOX_EAS_GRACED", (Object)underGracePeriod);
                    jsArray.add((Object)mailboxEntry);
                }
            }
            final JSONObject json = getUnselectedMailBoxesFromDB();
            final Long[] unselectedMailboxIDs = (Long[])json.getOrDefault((Object)"INAPPROPRIATE_DEVICE_ACCESS_STATES", (Object)null);
            final Long dispatchedTime = (Long)json.getOrDefault((Object)"DISPATCHED_TIME", (Object)(-1L));
            if (unselectedMailboxIDs != null && unselectedMailboxIDs.length > 0) {
                final SelectQuery ceaAuditQuery = SyMUtil.formSelectQuery("EASMailboxDetails", new Criteria(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"), (Object)unselectedMailboxIDs, 8).and(new Criteria(Column.getColumn("CEAAudit", "ACTION_STATUS"), (Object)2, 0)), new ArrayList((Collection<? extends E>)Arrays.asList(new Column((String)null, "*"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASMailboxDetails", "CEAAudit", new String[] { "EMAIL_ADDRESS" }, new String[] { "EMAIL_ADDRESS" }, 2))), (Criteria)null);
                final DataObject unselectedMailboxActionObj = SyMUtil.getPersistence().get(ceaAuditQuery);
                final Iterator unselectedMailboxIterator = unselectedMailboxActionObj.getRows("EASMailboxDetails", (Criteria)null);
                while (unselectedMailboxIterator != null && unselectedMailboxIterator.hasNext()) {
                    final Row easMailboxDetailsRow = unselectedMailboxIterator.next();
                    final String emailAddress2 = (String)easMailboxDetailsRow.get("EMAIL_ADDRESS");
                    final Iterator ceaIt = unselectedMailboxActionObj.getRows("CEAAudit", new Criteria(Column.getColumn("CEAAudit", "EMAIL_ADDRESS"), (Object)emailAddress2, 0, false));
                    final JSONArray mailboxAllowedList2 = new JSONArray();
                    final JSONArray mailboxNonAllowedList2 = new JSONArray();
                    final JSONArray mailboxBlockedList2 = new JSONArray();
                    while (ceaIt != null && ceaIt.hasNext()) {
                        final Row ceaAuditRow = ceaIt.next();
                        needToPerformConditionalAccess = true;
                        final Integer actionPerformed = (Integer)ceaAuditRow.get("ACTION_STATUS");
                        final String easDeviceIdentifer = (String)ceaAuditRow.get("EAS_DEVICE_IDENTIFIER");
                        final JSONObject deviceJSON2 = new JSONObject();
                        deviceJSON2.put((Object)"EAS_DEVICE_IDENTIFIER", (Object)easDeviceIdentifer);
                        if (actionPerformed == 2) {
                            mailboxAllowedList2.add((Object)deviceJSON2);
                            needToPerformConditionalAccess = true;
                        }
                    }
                    final JSONObject mailboxEntry = new JSONObject();
                    if (mailboxAllowedList2.size() > 0 || mailboxBlockedList2.size() > 0 || mailboxNonAllowedList2.size() > 0) {
                        mailboxEntry.put((Object)"EMAIL_ADDRESS", (Object)emailAddress2);
                        mailboxEntry.put((Object)"TO_BE_ALLOWED", (Object)mailboxAllowedList2);
                        jsArray.add((Object)mailboxEntry);
                    }
                }
                EASMgmt.logger.log(Level.INFO, unselectedMailboxActionObj.toString());
            }
            retJsObject.put((Object)"NEED_TO_PERFORM_CONDITIONAL_ACCESS", (Object)needToPerformConditionalAccess);
            retJsObject.put((Object)"INAPPROPRIATE_DEVICE_ACCESS_STATES", (Object)jsArray);
            retJsObject.put((Object)"DISPATCHED_TIME", (Object)dispatchedTime);
            return retJsObject;
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void addOrUpdateCEAaudit(final Long esServerID, final String emailAddress, final String easDeviceIdentifier, final Integer actionPerformed) {
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get("CEAAudit", new Criteria(Column.getColumn("CEAAudit", "EAS_SERVER_ID"), (Object)esServerID, 0).and(new Criteria(Column.getColumn("CEAAudit", "EMAIL_ADDRESS"), (Object)emailAddress, 0, false)).and(new Criteria(Column.getColumn("CEAAudit", "EAS_DEVICE_IDENTIFIER"), (Object)easDeviceIdentifier, 0, false)));
            Row ceaAuditRow = null;
            if (!dataObject.isEmpty() && dataObject.containsTable("CEAAudit")) {
                ceaAuditRow = dataObject.getRow("CEAAudit");
            }
            if (ceaAuditRow == null) {
                ceaAuditRow = new Row("CEAAudit");
                ceaAuditRow.set("EAS_SERVER_ID", (Object)esServerID);
                ceaAuditRow.set("EMAIL_ADDRESS", (Object)emailAddress);
                ceaAuditRow.set("EAS_DEVICE_IDENTIFIER", (Object)easDeviceIdentifier);
                ceaAuditRow.set("ACTION_STATUS", (Object)actionPerformed);
                dataObject.addRow(ceaAuditRow);
            }
            else {
                ceaAuditRow.set("ACTION_STATUS", (Object)actionPerformed);
                dataObject.updateRow(ceaAuditRow);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            EASMgmt.logger.log(Level.SEVERE, null, e);
        }
    }
    
    private void addOrUpdateCEAaudit(final Long esServerID, final String emailAddress, final JSONArray mailboxMobileActionList, final Integer actionPerformed) {
        if (mailboxMobileActionList != null && mailboxMobileActionList.size() > 0) {
            for (final Object deviceObj : mailboxMobileActionList) {
                final JSONObject deviceJSON = (JSONObject)deviceObj;
                if (deviceJSON != null && deviceJSON.containsKey((Object)"EAS_DEVICE_IDENTIFIER")) {
                    final String easDeviceIdentifier = (String)deviceJSON.get((Object)"EAS_DEVICE_IDENTIFIER");
                    this.addOrUpdateCEAaudit(esServerID, emailAddress, easDeviceIdentifier, actionPerformed);
                }
            }
        }
    }
    
    public void addOrUpdateCEAaudit(final Long esServerID, final JSONArray userConditionalAccessJSArray) {
        for (int i = 0; i < userConditionalAccessJSArray.size(); ++i) {
            final JSONObject mailboxMobileDetails = (JSONObject)userConditionalAccessJSArray.get(i);
            final String emailAddress = (String)mailboxMobileDetails.get((Object)"EMAIL_ADDRESS");
            final JSONArray mailboxAllowedList = (JSONArray)mailboxMobileDetails.get((Object)"TO_BE_ALLOWED");
            final JSONArray mailboxBlockedList = (JSONArray)mailboxMobileDetails.get((Object)"TO_BE_BLOCKED");
            final JSONArray mailboxNonAllowedList = (JSONArray)mailboxMobileDetails.get((Object)"TO_BE_NOT_ALOLWED");
            if (SyMUtil.isStringValid(emailAddress)) {
                this.addOrUpdateCEAaudit(esServerID, emailAddress, mailboxAllowedList, 1);
                this.addOrUpdateCEAaudit(esServerID, emailAddress, mailboxBlockedList, 2);
                this.addOrUpdateCEAaudit(esServerID, emailAddress, mailboxNonAllowedList, 2);
            }
        }
    }
    
    public void updateEASScheduler(final boolean enableScheduler) {
        if (enableScheduler) {
            ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.ENABLE, "EASSchedule");
        }
        else {
            ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.DISABLE, "EASSchedule");
        }
    }
    
    private String normalizeFQDN(String connectionURI) {
        final String http = "http://";
        final String https = "https://";
        if (connectionURI.startsWith(http)) {
            connectionURI = connectionURI.substring(http.length());
        }
        if (connectionURI.startsWith(https)) {
            connectionURI = connectionURI.substring(https.length());
        }
        return connectionURI;
    }
    
    public int getEventIDforTaskType(final String taskType) {
        if (taskType.equals("EXCHANGE_SERVER_DETAILS_REQUEST")) {
            return 2076;
        }
        if (taskType.equals("SYNC_REQUEST")) {
            return 2077;
        }
        if (taskType.equals("FULL_CONDITIONAL_ACCESS_REQUEST")) {
            return 2078;
        }
        if (taskType.equals("ENROLLMENT_TASK")) {
            return 2079;
        }
        return 0;
    }
    
    public String getTaskRemark(final int taskEventID, final Boolean response) {
        String taskRemark = null;
        if (taskEventID == 2076) {
            if (response) {
                taskRemark = "mdm.cea.server.details.success";
            }
            else {
                taskRemark = "mdm.cea.server.details.fail";
            }
        }
        else if (taskEventID == 2077) {
            if (response) {
                taskRemark = "mdm.cea.sync.success";
            }
            else {
                taskRemark = "mdm.cea.sync.fail";
            }
        }
        else if (taskEventID == 2078) {
            if (response) {
                taskRemark = "mdm.cea.conditional.access.success";
            }
            else {
                taskRemark = "mdm.cea.conditional.access.fail";
            }
        }
        else if (taskEventID == 2079) {
            if (response) {
                taskRemark = "dc.mdm.eas.eas_enrollment_success";
            }
            else {
                taskRemark = "mdm.cea.enroll.fail";
            }
        }
        return taskRemark;
    }
    
    private Calendar getCEAschedulerRunTimeForToday() {
        final HashMap easSchedulerValues = ApiFactoryProvider.getSchedulerAPI().getScheduledValues("EASSchedule");
        final Integer exeHours = easSchedulerValues.get("exeHours");
        final Integer exeMinutes = easSchedulerValues.get("exeMinutes");
        final Integer exeSeconds = easSchedulerValues.get("exeSeconds");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(11, exeHours);
        calendar.set(12, exeMinutes);
        calendar.set(13, exeSeconds);
        calendar.set(14, 0);
        return calendar;
    }
    
    private Row setValueIntoRow(final Row row, final JSONObject jsonObject, final String rowKey, final String jsonKey) {
        if (jsonObject.containsKey((Object)jsonKey)) {
            row.set(rowKey, jsonObject.get((Object)jsonKey));
        }
        return row;
    }
    
    private Row setValueIntoRow(final Row row, final JSONObject jsonObject, final String key) {
        return this.setValueIntoRow(row, jsonObject, key, key);
    }
    
    private Row getEASServerDetailsRow(Row easServerRow, final JSONObject easSummaryJSON) {
        if (easSummaryJSON.containsKey((Object)"DefaultAccessLevel")) {
            final String defaultAccessLevelValue = (String)easSummaryJSON.get((Object)"DefaultAccessLevel");
            final int defaultAccessLevel = this.getIntegerDefaultAccessLevel(defaultAccessLevelValue);
            easServerRow.set("DEFAULT_ACCESS_LEVEL", (Object)defaultAccessLevel);
        }
        if (easSummaryJSON.containsKey((Object)"AdminDisplayVersion")) {
            int exchangeServerVersion = 0;
            try {
                StringTokenizer stringTokenizer = new StringTokenizer((String)easSummaryJSON.get((Object)"AdminDisplayVersion"), " ");
                stringTokenizer.nextElement();
                final String version = (String)stringTokenizer.nextElement();
                stringTokenizer = new StringTokenizer(version, ".");
                String major = (String)stringTokenizer.nextElement();
                if (stringTokenizer.hasMoreElements()) {
                    final String minor = (String)stringTokenizer.nextElement();
                    if (Integer.valueOf(minor) == 1 && Integer.valueOf(major) == 15) {
                        major = String.valueOf(16);
                    }
                    if (Integer.valueOf(minor) >= 2 && Integer.valueOf(major) == 15) {
                        major = String.valueOf(19);
                    }
                }
                exchangeServerVersion = Integer.valueOf(major);
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            easServerRow.set("EXCHANGE_SERVER_VERSION", (Object)exchangeServerVersion);
        }
        if (easSummaryJSON.containsKey((Object)"Domain")) {
            String domain = "---";
            try {
                domain = (String)easSummaryJSON.get((Object)"Domain");
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            easServerRow.set("DOMAIN", (Object)domain);
        }
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "DOMAIN");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "AUTH_MODE");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "PS_VERSION");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "CUSTOMER_ID");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "CONNECTION_URI");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "EAS_ADMIN_EMAIL");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "EAS_ADMIN_PASSWORD");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "DEFAULT_ACCESS_LEVEL");
        easServerRow = this.setValueIntoRow(easServerRow, easSummaryJSON, "EXCHANGE_SERVER_VERSION");
        return easServerRow;
    }
    
    public Long addOrUpdateEASServerDetails(final JSONObject easServerDetailsJSON) {
        Long esServerID = null;
        try {
            final Long customerID = (Long)easServerDetailsJSON.get((Object)"CUSTOMER_ID");
            Criteria criteria = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            if (customerID == null && easServerDetailsJSON.containsKey((Object)"EAS_SERVER_ID")) {
                esServerID = (Long)easServerDetailsJSON.get((Object)"EAS_SERVER_ID");
                if (esServerID != null) {
                    criteria = new Criteria(new Column("EASServerDetails", "EAS_SERVER_ID"), (Object)esServerID, 0);
                }
            }
            if (easServerDetailsJSON.containsKey((Object)"CONNECTION_URI")) {
                easServerDetailsJSON.put((Object)"CONNECTION_URI", (Object)this.normalizeFQDN((String)easServerDetailsJSON.get((Object)"CONNECTION_URI")));
            }
            final DataObject dObj = SyMUtil.getPersistence().get("EASServerDetails", criteria);
            Row easServerDetailsRow;
            if (dObj.isEmpty()) {
                final DataObject dObj2 = MDMUtil.getPersistence().get("EASServerDetails", (Criteria)null);
                if (dObj2 != null && !dObj2.isEmpty() && dObj2.containsTable("EASServerDetails")) {
                    final Long existingEsServerID = (Long)dObj2.getRow("EASServerDetails").get("EAS_SERVER_ID");
                    if (existingEsServerID != null) {
                        throw new Exception("Exchange Server config for CEA already exists");
                    }
                }
                easServerDetailsRow = this.getEASServerDetailsRow(new Row("EASServerDetails"), easServerDetailsJSON);
                dObj.addRow(easServerDetailsRow);
                SyMUtil.getPersistence().add(dObj);
            }
            else {
                easServerDetailsRow = this.getEASServerDetailsRow(dObj.getFirstRow("EASServerDetails"), easServerDetailsJSON);
                dObj.updateRow(easServerDetailsRow);
                SyMUtil.getPersistence().update(dObj);
            }
            esServerID = (Long)easServerDetailsRow.get("EAS_SERVER_ID");
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return esServerID;
    }
    
    public void addOrUpdateCEAmailboxGracePeriod(final Long esServerID) {
        try {
            final JSONObject exchangeServerDetails = this.getExchangeServerDetails(false);
            if (exchangeServerDetails.get((Object)"EAS_POLICY_ID") == null) {
                return;
            }
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(11, 4);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            final SelectQuery selectedMailboxQuery = this.getSelectedMailboxQuery();
            selectedMailboxQuery.addJoin(new Join("EASMailboxDetails", "EASMailboxGracePeriod", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 1));
            selectedMailboxQuery.addSelectColumn(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"));
            selectedMailboxQuery.addSelectColumn(Column.getColumn("EASMailboxGracePeriod", "GRACE_PERIOD_START"));
            final int batchSize = 100;
            int rangeIterator = 0;
            Range range = new Range(0, batchSize);
            selectedMailboxQuery.setRange(range);
            for (DataObject dObj = SyMUtil.getPersistence().get(selectedMailboxQuery); !dObj.isEmpty(); dObj = SyMUtil.getPersistence().get(selectedMailboxQuery)) {
                final Iterator dObjIterator = dObj.getRows("EASMailboxDetails");
                while (dObjIterator.hasNext()) {
                    final Row easMailboxRow = dObjIterator.next();
                    final Long easMailboxID = (Long)easMailboxRow.get("EAS_MAILBOX_ID");
                    Row easMailboxGracePeriodRow = dObj.getRow("EASMailboxGracePeriod", new Criteria(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"), (Object)easMailboxID, 0));
                    if (easMailboxGracePeriodRow == null) {
                        easMailboxGracePeriodRow = new Row("EASMailboxGracePeriod");
                        easMailboxGracePeriodRow.set("EAS_MAILBOX_ID", (Object)easMailboxID);
                        easMailboxGracePeriodRow.set("GRACE_PERIOD_START", (Object)calendar.getTimeInMillis());
                        dObj.addRow(easMailboxGracePeriodRow);
                    }
                    else {
                        final Long easMailboxGracePeriodStart = (Long)easMailboxGracePeriodRow.get("GRACE_PERIOD_START");
                        if (easMailboxGracePeriodStart != -1L && easMailboxGracePeriodStart != null) {
                            continue;
                        }
                        easMailboxGracePeriodRow.set("GRACE_PERIOD_START", (Object)calendar.getTimeInMillis());
                        dObj.updateRow(easMailboxGracePeriodRow);
                    }
                }
                SyMUtil.getPersistence().update(dObj);
                rangeIterator += batchSize;
                range = new Range(rangeIterator, batchSize);
                selectedMailboxQuery.setRange(range);
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, "error.. rolling back", ex);
        }
    }
    
    private Row getEASMailboxDetailsRow(Row easMailboxDetailsRow, final JSONObject jsonObject, final Long esServerID) {
        easMailboxDetailsRow.set("EAS_SERVER_ID", (Object)esServerID);
        easMailboxDetailsRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
        easMailboxDetailsRow = this.setValueIntoRow(easMailboxDetailsRow, jsonObject, "DISPLAY_NAME", "DisplayName");
        easMailboxDetailsRow = this.setValueIntoRow(easMailboxDetailsRow, jsonObject, "EMAIL_ADDRESS", "PrimarySmtpAddress");
        return easMailboxDetailsRow;
    }
    
    private Long addOrUpdateEASMailboxDetails(final Long esServerID, final JSONObject jsonObject) {
        if (!jsonObject.containsKey((Object)"PrimarySmtpAddress")) {
            return null;
        }
        try {
            final String easEmailAddress = (String)jsonObject.get((Object)"PrimarySmtpAddress");
            final DataObject dObj = SyMUtil.getPersistence().get("EASMailboxDetails", new Criteria(new Column("EASMailboxDetails", "EMAIL_ADDRESS"), (Object)easEmailAddress, 0, false));
            Row easMailboxDetailsRow = null;
            if (dObj.isEmpty()) {
                easMailboxDetailsRow = new Row("EASMailboxDetails");
                easMailboxDetailsRow = this.getEASMailboxDetailsRow(easMailboxDetailsRow, jsonObject, esServerID);
                if (easMailboxDetailsRow == null) {
                    return null;
                }
                easMailboxDetailsRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                dObj.addRow(easMailboxDetailsRow);
                SyMUtil.getPersistence().add(dObj);
            }
            else {
                easMailboxDetailsRow = dObj.getFirstRow("EASMailboxDetails");
                easMailboxDetailsRow = this.getEASMailboxDetailsRow(easMailboxDetailsRow, jsonObject, esServerID);
                dObj.updateRow(easMailboxDetailsRow);
                SyMUtil.getPersistence().update(dObj);
            }
            return (Long)easMailboxDetailsRow.get("EAS_MAILBOX_ID");
        }
        catch (final DataAccessException ex) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            EASMgmt.logger.log(Level.SEVERE, null, ex2);
        }
        return null;
    }
    
    private Row getEASDeviceDetailsRow(Row easDeviceDetailsRow, final JSONObject jsonObject) {
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "DEVICE_OS", "DeviceOS");
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "DEVICE_IMEI", "DeviceImei");
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "DEVICE_MODEL", "DeviceModel");
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "DEVICE_NAME", "FriendlyName");
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "DEVICE_NAME", "DeviceFriendlyName");
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "EAS_DEVICE_IDENTIFIER", "DeviceId");
        easDeviceDetailsRow = this.setValueIntoRow(easDeviceDetailsRow, jsonObject, "EAS_DEVICE_IDENTIFIER", "DeviceID");
        return easDeviceDetailsRow;
    }
    
    private Long addOrUpdateEASDeviceDetails(final JSONObject jsonObject) {
        if (!jsonObject.containsKey((Object)"DeviceID") && !jsonObject.containsKey((Object)"DeviceId")) {
            return null;
        }
        String easDeviceIdentifier = null;
        if (jsonObject.containsKey((Object)"DeviceID")) {
            easDeviceIdentifier = (String)jsonObject.get((Object)"DeviceID");
        }
        if (!SyMUtil.isStringValid(easDeviceIdentifier) && jsonObject.containsKey((Object)"DeviceId")) {
            easDeviceIdentifier = (String)jsonObject.get((Object)"DeviceId");
        }
        try {
            final DataObject dObj = SyMUtil.getPersistence().get("EASDeviceDetails", new Criteria(new Column("EASDeviceDetails", "EAS_DEVICE_IDENTIFIER"), (Object)easDeviceIdentifier, 0));
            Row easDeviceDetailsRow = null;
            if (dObj.isEmpty()) {
                easDeviceDetailsRow = new Row("EASDeviceDetails");
                easDeviceDetailsRow = this.getEASDeviceDetailsRow(easDeviceDetailsRow, jsonObject);
                easDeviceDetailsRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                easDeviceDetailsRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
                dObj.addRow(easDeviceDetailsRow);
                SyMUtil.getPersistence().add(dObj);
            }
            else {
                easDeviceDetailsRow = dObj.getFirstRow("EASDeviceDetails");
                easDeviceDetailsRow = this.getEASDeviceDetailsRow(easDeviceDetailsRow, jsonObject);
                easDeviceDetailsRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
                dObj.updateRow(easDeviceDetailsRow);
                SyMUtil.getPersistence().update(dObj);
            }
            final Long easDeviceID = (Long)easDeviceDetailsRow.get("EAS_DEVICE_ID");
            return easDeviceID;
        }
        catch (final DataAccessException ex) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
    
    private Row getEASMailboxDevieRelRow(Row easMailboxDeviceRelRow, final Long easMailboxID, final Long easDeviceID, final JSONObject jsonObject) {
        easMailboxDeviceRelRow.set("EAS_DEVICE_ID", (Object)easDeviceID);
        easMailboxDeviceRelRow.set("EAS_MAILBOX_ID", (Object)easMailboxID);
        easMailboxDeviceRelRow.set("ACTION_STATUS", (Object)(-1));
        easMailboxDeviceRelRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
        easMailboxDeviceRelRow = this.setValueIntoRow(easMailboxDeviceRelRow, jsonObject, "GUID", "Guid");
        return easMailboxDeviceRelRow;
    }
    
    private Long addOrUpdateEASMailboxDeviceRel(final Long esServerID, final JSONObject jsonObject) {
        final Long easMailboxID = this.addOrUpdateEASMailboxDetails(esServerID, jsonObject);
        final Long easDeviceID = this.addOrUpdateEASDeviceDetails(jsonObject);
        if (easMailboxID != null && easDeviceID != null) {
            Row easMailboxDeviceRelRow = null;
            try {
                final DataObject dObj = SyMUtil.getPersistence().get("EASMailboxDeviceRel", new Criteria(new Column("EASMailboxDeviceRel", "EAS_MAILBOX_ID"), (Object)easMailboxID, 0).and(new Criteria(new Column("EASMailboxDeviceRel", "EAS_DEVICE_ID"), (Object)easDeviceID, 0)));
                if (dObj.isEmpty()) {
                    easMailboxDeviceRelRow = new Row("EASMailboxDeviceRel");
                    easMailboxDeviceRelRow = this.getEASMailboxDevieRelRow(easMailboxDeviceRelRow, easMailboxID, easDeviceID, jsonObject);
                    easMailboxDeviceRelRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                    dObj.addRow(easMailboxDeviceRelRow);
                    SyMUtil.getPersistence().add(dObj);
                }
                else {
                    easMailboxDeviceRelRow = dObj.getFirstRow("EASMailboxDeviceRel");
                    easMailboxDeviceRelRow = this.getEASMailboxDevieRelRow(easMailboxDeviceRelRow, easMailboxID, easDeviceID, jsonObject);
                    dObj.updateRow(easMailboxDeviceRelRow);
                    SyMUtil.getPersistence().update(dObj);
                }
            }
            catch (final DataAccessException ex) {
                EASMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
            return (Long)easMailboxDeviceRelRow.get("EAS_MAILBOX_DEVICE_ID");
        }
        return null;
    }
    
    private Row getEASMailboxDeviceInfoRow(final Row easMailboxDeviceInfoRow, final JSONObject jsonObject) {
        if (jsonObject.containsKey((Object)"FirstSyncTime")) {
            Long firstSyncTime = null;
            final String firstSyncTimeString = (String)jsonObject.get((Object)"FirstSyncTime");
            if (firstSyncTimeString != null) {
                firstSyncTime = Long.parseLong(firstSyncTimeString.substring(6, firstSyncTimeString.length() - 2));
            }
            easMailboxDeviceInfoRow.set("FIRST_SYNC_TIME", (Object)firstSyncTime);
        }
        if (jsonObject.containsKey((Object)"LastSuccessSync")) {
            Long lastSuccessSync = null;
            final String lastSuccessSyncString = (String)jsonObject.get((Object)"LastSuccessSync");
            if (lastSuccessSyncString != null) {
                lastSuccessSync = Long.parseLong(lastSuccessSyncString.substring(6, lastSuccessSyncString.length() - 2));
            }
            easMailboxDeviceInfoRow.set("LAST_SUCCESSFUL_SYNC", (Object)lastSuccessSync);
        }
        if (jsonObject.containsKey((Object)"LastSyncAttemptTime")) {
            Long lastAttemptedSync = null;
            final String lastAttemptedSyncString = (String)jsonObject.get((Object)"LastSyncAttemptTime");
            if (lastAttemptedSyncString != null) {
                lastAttemptedSync = Long.parseLong(lastAttemptedSyncString.substring(6, lastAttemptedSyncString.length() - 2));
            }
            easMailboxDeviceInfoRow.set("LAST_ATTEMPTED_SYNC", (Object)lastAttemptedSync);
        }
        if (jsonObject.containsKey((Object)"DeviceAccessState")) {
            Integer deviceAccessState = 0;
            final Object deviceAccessStateObj = jsonObject.get((Object)"DeviceAccessState");
            if (deviceAccessStateObj != null) {
                if (deviceAccessStateObj instanceof String) {
                    final String deviceAccessStateStr = (String)deviceAccessStateObj;
                    if (deviceAccessStateStr.equalsIgnoreCase("Allowed")) {
                        deviceAccessState = 0;
                    }
                    else if (deviceAccessStateStr.equalsIgnoreCase("Blocked")) {
                        deviceAccessState = 1;
                    }
                    else if (deviceAccessStateStr.equalsIgnoreCase("Quarantined")) {
                        deviceAccessState = 2;
                    }
                    else {
                        deviceAccessState = 0;
                    }
                }
                else if (deviceAccessStateObj instanceof Long) {
                    final Long MS_EAS_ACCESS_STATE_ALLOWED = 1L;
                    final Long MS_EAS_ACCESS_STATE_BLOCKED = 2L;
                    final Long MS_EAS_ACCESS_STATE_QUARANTINED = 3L;
                    final Long deviceAccessStateL = (Long)deviceAccessStateObj;
                    if (deviceAccessStateL.equals(MS_EAS_ACCESS_STATE_ALLOWED)) {
                        deviceAccessState = 0;
                    }
                    else if (deviceAccessStateL.equals(MS_EAS_ACCESS_STATE_BLOCKED)) {
                        deviceAccessState = 1;
                    }
                    else if (deviceAccessStateL.equals(MS_EAS_ACCESS_STATE_QUARANTINED)) {
                        deviceAccessState = 2;
                    }
                    else {
                        deviceAccessState = 0;
                    }
                }
            }
            easMailboxDeviceInfoRow.set("DEVICE_ACCESS_STATE", (Object)deviceAccessState);
        }
        return easMailboxDeviceInfoRow;
    }
    
    public void addOrUpdateEASMailboxDeviceInfo(final Long esServerID, final JSONObject jsonObject) {
        final Long easMailboxDeviceID = this.addOrUpdateEASMailboxDeviceRel(esServerID, jsonObject);
        if (easMailboxDeviceID != null) {
            try {
                final DataObject dObj = SyMUtil.getPersistence().get("EASMailboxDeviceInfo", new Criteria(new Column("EASMailboxDeviceInfo", "EAS_MAILBOX_DEVICE_ID"), (Object)easMailboxDeviceID, 0));
                if (dObj.isEmpty()) {
                    Row easMailboxDeviceInfoRow = new Row("EASMailboxDeviceInfo");
                    easMailboxDeviceInfoRow = this.getEASMailboxDeviceInfoRow(easMailboxDeviceInfoRow, jsonObject);
                    easMailboxDeviceInfoRow.set("EAS_MAILBOX_DEVICE_ID", (Object)easMailboxDeviceID);
                    dObj.addRow(easMailboxDeviceInfoRow);
                    SyMUtil.getPersistence().add(dObj);
                }
                else {
                    Row easMailboxDeviceInfoRow = dObj.getFirstRow("EASMailboxDeviceInfo");
                    easMailboxDeviceInfoRow = this.getEASMailboxDeviceInfoRow(easMailboxDeviceInfoRow, jsonObject);
                    dObj.updateRow(easMailboxDeviceInfoRow);
                    SyMUtil.getPersistence().update(dObj);
                }
            }
            catch (final DataAccessException ex) {
                EASMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
    }
    
    public void addOrUpdateEASManagedDeviceRel(final Long updatedTime) {
        try {
            EASMgmt.logger.log(Level.INFO, "bridging with easid");
            final JSONArray dsJSArray = MDMUtil.executeSelectQuery(SyMUtil.formSelectQuery("MdDeviceInfo", new Criteria(Column.getColumn("EASDeviceDetails", "LAST_UPDATED_TIME"), (Object)updatedTime, 4).and(new Criteria(Column.getColumn("MdDeviceInfo", "EAS_DEVICE_IDENTIFIER"), (Object)null, 1)), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), Column.getColumn("EASDeviceDetails", "EAS_DEVICE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("MdDeviceInfo", "EASDeviceDetails", new String[] { "EAS_DEVICE_IDENTIFIER" }, new String[] { "EAS_DEVICE_IDENTIFIER" }, 2))), (Criteria)null));
            for (int i = 0; i < dsJSArray.size(); ++i) {
                final JSONObject jsObject = (JSONObject)dsJSArray.get(i);
                final Long resourceID = (Long)jsObject.get((Object)"RESOURCE_ID");
                final Long easDeviceID = (Long)jsObject.get((Object)"EAS_DEVICE_ID");
                try {
                    EASMgmt.logger.log(Level.INFO, "bridging {0} to {1}", new Object[] { easDeviceID, resourceID });
                    final DataObject dObj = SyMUtil.getPersistence().get("EASManagedDeviceRel", new Criteria(Column.getColumn("EASManagedDeviceRel", "EAS_DEVICE_ID"), (Object)easDeviceID, 0));
                    if (dObj != null && !dObj.isEmpty()) {
                        final Row managedDeviceRelRow = dObj.getFirstRow("EASManagedDeviceRel");
                        managedDeviceRelRow.set("MANAGED_DEVICE_ID", (Object)resourceID);
                        dObj.updateRow(managedDeviceRelRow);
                        MDMUtil.getPersistence().update(dObj);
                    }
                    else {
                        MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)resourceID, 0));
                        final Row managedDeviceRelRow = new Row("EASManagedDeviceRel");
                        managedDeviceRelRow.set("EAS_DEVICE_ID", (Object)easDeviceID);
                        managedDeviceRelRow.set("MANAGED_DEVICE_ID", (Object)resourceID);
                        dObj.addRow(managedDeviceRelRow);
                        MDMUtil.getPersistence().add(dObj);
                    }
                }
                catch (final Exception ex) {
                    EASMgmt.logger.log(Level.SEVERE, ex, () -> "bridging " + n + " to " + n2 + " failed");
                }
            }
            EASMgmt.logger.log(Level.INFO, "bridging with easid done");
        }
        catch (final Exception ex2) {
            EASMgmt.logger.log(Level.SEVERE, "exception occured", ex2);
        }
    }
    
    private Row getEASSyncStatusRow(Row easSyncStatusRow, final JSONObject easTaskProps) {
        easSyncStatusRow.set("EAS_Sync_Status_ID", easTaskProps.get((Object)"EAS_Sync_Status_ID"));
        easSyncStatusRow = this.setValueIntoRow(easSyncStatusRow, easTaskProps, "REMARKS");
        easSyncStatusRow = this.setValueIntoRow(easSyncStatusRow, easTaskProps, "SYNC_STATUS");
        easSyncStatusRow = this.setValueIntoRow(easSyncStatusRow, easTaskProps, "LAST_ATTEMPTED_SYNC_TASK");
        easSyncStatusRow = this.setValueIntoRow(easSyncStatusRow, easTaskProps, "LAST_SUCCESSFUL_SYNC_TASK");
        if (easTaskProps.containsKey((Object)"ERROR_CODE") && easTaskProps.get((Object)"ERROR_CODE") != null) {
            easSyncStatusRow.set("ERROR_CODE", easTaskProps.get((Object)"ERROR_CODE"));
        }
        else {
            easSyncStatusRow.set("ERROR_CODE", (Object)(-1));
        }
        return easSyncStatusRow;
    }
    
    public void addOrUpdateEASSyncStatus(final JSONObject easTaskProps) {
        try {
            Row easSyncStatusRow = null;
            final Long easSyncStatusID = (Long)easTaskProps.get((Object)"EAS_Sync_Status_ID");
            final DataObject dObj = SyMUtil.getPersistence().get("EASSyncStatus", new Criteria(new Column("EASSyncStatus", "EAS_Sync_Status_ID"), (Object)easSyncStatusID, 0));
            if (dObj.isEmpty()) {
                easSyncStatusRow = new Row("EASSyncStatus");
                easSyncStatusRow = this.getEASSyncStatusRow(easSyncStatusRow, easTaskProps);
                dObj.addRow(easSyncStatusRow);
                SyMUtil.getPersistence().add(dObj);
            }
            else {
                easSyncStatusRow = dObj.getFirstRow("EASSyncStatus");
                if (!easTaskProps.containsKey((Object)"ERROR_CODE") || easTaskProps.get((Object)"ERROR_CODE") == null) {
                    easTaskProps.put((Object)"ERROR_CODE", (Object)easSyncStatusRow.get("ERROR_CODE"));
                }
                easSyncStatusRow = this.getEASSyncStatusRow(easSyncStatusRow, easTaskProps);
                dObj.updateRow(easSyncStatusRow);
                SyMUtil.getPersistence().update(dObj);
            }
        }
        catch (final DataAccessException ex) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    private void addOrUpdateaCEApolicyMailboxes(final JSONObject easPolicyJSON) {
        try {
            final Long serverID = (Long)easPolicyJSON.get((Object)"EAS_SERVER_ID");
            final JSONArray easMailJSON = (JSONArray)easPolicyJSON.get((Object)"EASSelectedMailbox");
            if (easMailJSON != null) {
                final DataObject selectedMailboxesDobj = MDMUtil.getPersistence().get(this.getSelectedMailboxQuery());
                for (int i = 0; i < easMailJSON.size(); ++i) {
                    final Long easMailID = Long.valueOf((String)easMailJSON.get(i));
                    final Criteria easMailIDCri = new Criteria(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"), (Object)easMailID, 0);
                    Row easMailCriRow = selectedMailboxesDobj.getRow("EASSelectedMailbox", easMailIDCri);
                    if (easMailCriRow == null) {
                        easMailCriRow = new Row("EASSelectedMailbox");
                        easMailCriRow.set("EAS_MAILBOX_ID", (Object)easMailID);
                        easMailCriRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        selectedMailboxesDobj.addRow(easMailCriRow);
                    }
                    else {
                        easMailCriRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        selectedMailboxesDobj.updateRow(easMailCriRow);
                    }
                    EASMgmt.logger.log(Level.INFO, "mailbox {0}selected to be managed from {1}", new Object[] { easMailID, easMailCriRow.get("UPDATED_TIME") });
                }
                MDMUtil.getPersistence().update(selectedMailboxesDobj);
            }
        }
        catch (final Exception e) {
            EASMgmt.logger.log(Level.WARNING, "Exception occurred in addOrUpdateEASPolicyCriteria() method : {0}", e);
        }
    }
    
    private Row getEASPolicyRow(final Row easPolicyRow, final JSONObject easPolicyJSON) {
        try {
            easPolicyRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
            easPolicyRow.set("EAS_SERVER_ID", easPolicyJSON.get((Object)"EAS_SERVER_ID"));
            final Long updatedBy = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            if (updatedBy != null) {
                easPolicyRow.set("UPDATED_BY", (Object)updatedBy);
            }
            else if (easPolicyJSON.containsKey((Object)"UPDATED_BY")) {
                easPolicyRow.set("UPDATED_BY", easPolicyJSON.get((Object)"UPDATED_BY"));
            }
            if (easPolicyJSON.containsKey((Object)"GRACE_DAYS")) {
                easPolicyRow.set("GRACE_DAYS", (Object)Integer.valueOf(String.valueOf(easPolicyJSON.get((Object)"GRACE_DAYS"))));
            }
            if (easPolicyJSON.containsKey((Object)"APPLIED_FOR")) {
                easPolicyRow.set("APPLIED_FOR", (Object)Integer.valueOf(String.valueOf(easPolicyJSON.get((Object)"APPLIED_FOR"))));
            }
            if (easPolicyJSON.containsKey((Object)"POLICY_STATUS")) {
                easPolicyRow.set("POLICY_STATUS", easPolicyJSON.get((Object)"POLICY_STATUS"));
            }
            if (easPolicyJSON.containsKey((Object)"SEND_NOTIF_MAIL")) {
                easPolicyRow.set("SEND_NOTIF_MAIL", easPolicyJSON.get((Object)"SEND_NOTIF_MAIL"));
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return easPolicyRow;
    }
    
    public void addorUpdateEASPolicy(final JSONObject easPolicyJSON, final boolean addPolicy) {
        final TransactionManager tm = SyMUtil.getUserTransaction();
        try {
            tm.begin();
            final Long serverID = (Long)easPolicyJSON.get((Object)"EAS_SERVER_ID");
            boolean updatePolicyMailboxSelection = false;
            if (easPolicyJSON.containsKey((Object)"UPDATE_POLICY_SELECTION")) {
                updatePolicyMailboxSelection = Boolean.valueOf((String)easPolicyJSON.get((Object)"UPDATE_POLICY_SELECTION"));
            }
            DataObject selectedMailboxBeforePolicyChange = null;
            DataObject selectedMailboxAfterPolicyChange = null;
            if (!updatePolicyMailboxSelection) {
                easPolicyJSON.remove((Object)"APPLIED_FOR");
                easPolicyJSON.remove((Object)"EASSelectedMailbox");
            }
            else {
                selectedMailboxBeforePolicyChange = MDMUtil.getPersistence().get(this.getSelectedMailboxQuery());
                MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"), (Object)null, 1));
            }
            final DataObject easPolicyDObj = MDMUtil.getPersistence().get("EASPolicy", new Criteria(Column.getColumn("EASPolicy", "EAS_SERVER_ID"), (Object)serverID, 0));
            if (easPolicyDObj.isEmpty()) {
                if (addPolicy) {
                    Row easPolicyRow = new Row("EASPolicy");
                    easPolicyRow = this.getEASPolicyRow(easPolicyRow, easPolicyJSON);
                    easPolicyDObj.addRow(easPolicyRow);
                    MDMUtil.getPersistence().add(easPolicyDObj);
                }
            }
            else {
                Row easPolicyRow = easPolicyDObj.getRow("EASPolicy");
                easPolicyRow = this.getEASPolicyRow(easPolicyRow, easPolicyJSON);
                easPolicyDObj.updateRow(easPolicyRow);
                MDMUtil.getPersistence().update(easPolicyDObj);
            }
            if (updatePolicyMailboxSelection) {
                if (easPolicyJSON.containsKey((Object)"EASSelectedMailbox")) {
                    this.addOrUpdateaCEApolicyMailboxes(easPolicyJSON);
                }
                selectedMailboxAfterPolicyChange = MDMUtil.getPersistence().get(this.getSelectedMailboxQuery());
                boolean policyAppliedOnAll = false;
                if (easPolicyJSON.containsKey((Object)"APPLIED_FOR")) {
                    final Integer appliedFor = Integer.valueOf(String.valueOf(easPolicyJSON.get((Object)"APPLIED_FOR")));
                    if (appliedFor == 1) {
                        final JSONArray easMailJSON = (JSONArray)easPolicyJSON.get((Object)"EASSelectedMailbox");
                        if (easMailJSON == null || easMailJSON.size() == 0) {
                            policyAppliedOnAll = true;
                        }
                    }
                }
                if (!policyAppliedOnAll) {
                    boolean isRevert = true;
                    if (easPolicyJSON.containsKey((Object)"ROLLBACK_BLOCKED_DEVICES") && Boolean.valueOf((String)easPolicyJSON.get((Object)"ROLLBACK_BLOCKED_DEVICES")).equals(Boolean.FALSE)) {
                        isRevert = false;
                    }
                    this.getUnselectedMailboxes(serverID, selectedMailboxBeforePolicyChange.diff(selectedMailboxAfterPolicyChange), isRevert);
                }
            }
            this.addOrUpdateCEAmailboxGracePeriod(serverID);
            this.mapDomainNameToCEA(serverID);
            tm.commit();
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
            try {
                tm.setRollbackOnly();
                if (tm.getStatus() == 1) {
                    tm.rollback();
                }
            }
            catch (final Exception ex2) {
                EASMgmt.logger.log(Level.SEVERE, "failed to roll back", ex2);
            }
        }
    }
    
    public void getUnselectedMailboxesOnCEApolicyRemoval(final Long esServerID, final boolean isRevert) {
        try {
            final DataObject unselectedMailboxes = MDMUtil.getPersistence().get(this.getSelectedMailboxQuery());
            this.getUnselectedMailboxes(esServerID, unselectedMailboxes.diff((DataObject)new WritableDataObject()), isRevert);
        }
        catch (final DataAccessException ex) {
            EASMgmt.logger.log(Level.WARNING, "EASMgmtDataHandler : Exception while deleting EASPOLICY", (Throwable)ex);
        }
    }
    
    private void removeCorrespondingEASDevices(final Long esServerID) {
        final SelectQuery removeDevicesQuery = SyMUtil.formSelectQuery("EASServerDetails", new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)esServerID, 0), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("EASDeviceDetails", "EAS_DEVICE_ID"))), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new SortColumn(Column.getColumn("EASDeviceDetails", "EAS_DEVICE_ID"), true))), new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASServerDetails", "EASMailboxDetails", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2), new Join("EASMailboxDetails", "EASMailboxDeviceRel", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2), new Join("EASMailboxDeviceRel", "EASDeviceDetails", new String[] { "EAS_DEVICE_ID" }, new String[] { "EAS_DEVICE_ID" }, 2))), (Criteria)null);
        final Range range = new Range(0, 100);
        removeDevicesQuery.setRange(range);
        final TransactionManager tm = SyMUtil.getUserTransaction();
        try {
            for (DataObject dObj = SyMUtil.getPersistence().get(removeDevicesQuery); !dObj.isEmpty(); dObj = SyMUtil.getPersistence().get(removeDevicesQuery)) {
                dObj.deleteRows("EASDeviceDetails", (Criteria)null);
                tm.begin();
                SyMUtil.getPersistence().update(dObj);
                tm.commit();
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, "error.. rolling back", ex);
            try {
                tm.rollback();
            }
            catch (final Exception ex2) {
                EASMgmt.logger.log(Level.SEVERE, "failed to roll back", ex2);
            }
        }
    }
    
    public void deleteCEApolicy(final Long esServerID) throws DataAccessException {
        MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASPolicy", "EAS_SERVER_ID"), (Object)esServerID, 0));
        MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"), (Object)null, 1));
        MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"), (Object)null, 1));
    }
    
    public void deleteCEA(final Long esServerID) throws DataAccessException {
        MDMApiFactoryProvider.getConditionalExchangeAccessApi().closeSession(esServerID);
        this.removeCorrespondingEASDevices(esServerID);
        MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)esServerID, 0));
        this.updateEASScheduler(false);
    }
    
    private void getUnselectedMailboxes(final Long esServerID, final DataObject dObj, final boolean isRevert) {
        final DataObject unSelectedMailBoxes = (DataObject)new WritableDataObject();
        try {
            final List<Long> unselectedMailboxIDs = new ArrayList<Long>();
            final List operationsList = dObj.getOperations();
            for (int i = 0; i < operationsList.size(); ++i) {
                final ActionInfo actionInfo = operationsList.get(i);
                final int operation = actionInfo.getOperation();
                if (operation == 3) {
                    final Row row = actionInfo.getValue();
                    final String tableName = row.getTableName();
                    if (tableName.equalsIgnoreCase("EASMailboxDetails")) {
                        final Row unselectedRow = new Row("EASUnSelectedMailBoxes");
                        final Long easMailboxID = (Long)row.get("EAS_MAILBOX_ID");
                        unselectedRow.set("EAS_MAILBOX_ID", (Object)easMailboxID);
                        unselectedRow.set("STATUS", (Object)1);
                        if (isRevert) {
                            unSelectedMailBoxes.addRow(unselectedRow);
                        }
                        unselectedMailboxIDs.add(easMailboxID);
                    }
                }
            }
            if (!unselectedMailboxIDs.isEmpty()) {
                if (isRevert && !unSelectedMailBoxes.isEmpty()) {
                    EASMgmt.logger.log(Level.INFO, "unselection handling being done for : {0}, ids length : {1}", new Object[] { unSelectedMailBoxes.toString(), unselectedMailboxIDs.size() });
                    SyMUtil.getPersistenceLite().update(unSelectedMailBoxes);
                }
                EASMgmt.logger.log(Level.INFO, "unselection ids length : {0}", new Object[] { unselectedMailboxIDs.size() });
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("EASMailboxGracePeriod");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("EASMailboxGracePeriod", "EAS_MAILBOX_ID"), (Object)unselectedMailboxIDs.toArray(new Long[unselectedMailboxIDs.size()]), 8));
                final int deletedRows = DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
                EASMgmt.logger.log(Level.INFO, "number of rows deleted from grace period : {0}", new Object[] { deletedRows });
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public org.json.JSONObject getMeTrackingData() {
        final org.json.JSONObject exchangeServerDetails = new org.json.JSONObject();
        String mailboxCount = "NULL";
        String selectedMailboxCount = "NULL";
        String defaultAccessLevel = "NULL";
        String exchangeServerVersion = "NULL";
        String exchangeServerPolicyGraceDays = "-1";
        String errorCode = "-1";
        String mailboxDevicePartnershipCount = "NULL";
        String applliedFor = "-1";
        try {
            final DataObject dObj = MDMUtil.getPersistence().get("EASServerDetails", (Criteria)null);
            final Iterator iterator = dObj.getRows("EASServerDetails");
            while (iterator != null && iterator.hasNext()) {
                final Row row = iterator.next();
                final Long easServerID = (Long)row.get("EAS_SERVER_ID");
                final JSONObject ceaDetailsRequest = new JSONObject();
                ceaDetailsRequest.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                ceaDetailsRequest.put((Object)"EASServerDetails", (Object)String.valueOf(Boolean.TRUE));
                final JSONObject serverDetails = EASMgmt.getInstance().getCEAdetails(ceaDetailsRequest);
                final org.json.JSONObject serverDetialsJSON = JSONUtil.getInstance().convertSimpleJSONtoJSON(serverDetails);
                mailboxCount = String.valueOf(serverDetialsJSON.optString("syncUserCount", mailboxCount));
                errorCode = String.valueOf(serverDetialsJSON.optString("ERROR_CODE", errorCode));
                applliedFor = String.valueOf(serverDetialsJSON.optString("APPLIED_FOR", applliedFor));
                selectedMailboxCount = String.valueOf(serverDetialsJSON.optString("selectedMailboxCount", selectedMailboxCount));
                defaultAccessLevel = this.getStringDefaultAccessLevel(serverDetialsJSON.optInt("DEFAULT_ACCESS_LEVEL", -1));
                mailboxDevicePartnershipCount = String.valueOf(serverDetialsJSON.optString("syncDeviceCount", mailboxDevicePartnershipCount));
                exchangeServerPolicyGraceDays = String.valueOf(serverDetialsJSON.optString("GRACE_DAYS", exchangeServerPolicyGraceDays));
                exchangeServerVersion = String.valueOf(serverDetialsJSON.optString("EXCHANGE_SERVER_VERSION", exchangeServerVersion));
            }
            exchangeServerDetails.put("APPLIED_FOR", (Object)applliedFor);
            exchangeServerDetails.put("ERROR_CODE", (Object)errorCode);
            exchangeServerDetails.put("MAILBOX_COUNT", (Object)mailboxCount);
            exchangeServerDetails.put("GRACE_DAYS", (Object)exchangeServerPolicyGraceDays);
            exchangeServerDetails.put("DEFAULT_ACCESS_LEVEL", (Object)defaultAccessLevel);
            exchangeServerDetails.put("SELECTED_MAILBOX_COUNT", (Object)selectedMailboxCount);
            exchangeServerDetails.put("EXCHANGE_SERVER_VERSION", (Object)exchangeServerVersion);
            exchangeServerDetails.put("MAILBOX_DEVICE_PARTNERSHIP_COUNT", (Object)mailboxDevicePartnershipCount);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return exchangeServerDetails;
    }
    
    private DataObject getEASdeviceForExchangeServer(final Object mailboxDeviceIdentifier) throws Exception {
        Criteria queryCri = null;
        if (mailboxDeviceIdentifier instanceof Long) {
            queryCri = new Criteria(Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_DEVICE_ID"), (Object)mailboxDeviceIdentifier, 0);
        }
        else if (mailboxDeviceIdentifier instanceof String) {
            queryCri = new Criteria(Column.getColumn("EASMailboxDeviceRel", "GUID"), (Object)mailboxDeviceIdentifier, 0, false);
        }
        return MDMUtil.getPersistence().get(MDMUtil.formSelectQuery("EASMailboxDeviceRel", queryCri, new ArrayList((Collection<? extends E>)Arrays.asList(new Column((String)null, "*"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("EASMailboxDeviceRel", "EASMailboxDeviceInfo", new String[] { "EAS_MAILBOX_DEVICE_ID" }, new String[] { "EAS_MAILBOX_DEVICE_ID" }, 1))), (Criteria)null));
    }
    
    public JSONObject getMailboxDeviceDetailsAndMarkDevice(final Object deviceIdentifier, final Integer actionStatus) throws Exception {
        final JSONObject easMailboxDeviceDetails = new JSONObject();
        final DataObject dataObject = this.getEASdeviceForExchangeServer(deviceIdentifier);
        if (dataObject != null && !dataObject.isEmpty()) {
            final Row easMailboxDeviceRow = dataObject.getRow("EASMailboxDeviceRel");
            final List<String> tableColumns = easMailboxDeviceRow.getColumns();
            for (final String columnName : tableColumns) {
                easMailboxDeviceDetails.put((Object)columnName, easMailboxDeviceRow.get(columnName));
            }
            easMailboxDeviceRow.set("ACTION_STATUS", (Object)actionStatus);
            dataObject.updateRow(easMailboxDeviceRow);
            if (actionStatus.equals(1) && dataObject.containsTable("EASMailboxDeviceInfo")) {
                dataObject.deleteRows("EASMailboxDeviceInfo", (Criteria)null);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        return easMailboxDeviceDetails;
    }
    
    public void deleteEASdeviceForEAShost(final Long esMailboxDeviceID) {
        try {
            final DataObject dObj = this.getEASdeviceForExchangeServer(esMailboxDeviceID);
            dObj.deleteRows("EASMailboxDeviceRel", new Criteria(Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_DEVICE_ID"), (Object)esMailboxDeviceID, 0));
            MDMUtil.getPersistence().update(dObj);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public int getSendNotifMailSetting(final Long easServerID) {
        try {
            final Integer sendNotifMail = (Integer)DBUtil.getValueFromDB("EASPolicy", "EAS_SERVER_ID", (Object)easServerID, "SEND_NOTIF_MAIL");
            return sendNotifMail;
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
            return 3;
        }
    }
    
    public static JSONObject getUnselectedMailBoxesFromDB() throws DataAccessException {
        final JSONObject returnJSON = new JSONObject();
        final List<Long> unselectedMailboxesID = new ArrayList<Long>();
        final DataObject dObj = SyMUtil.getPersistence().get("EASUnSelectedMailBoxes", new Criteria(Column.getColumn("EASUnSelectedMailBoxes", "STATUS"), (Object)1, 0));
        if (dObj != null && !dObj.isEmpty()) {
            final long dispatchedTime = System.currentTimeMillis();
            final Iterator rowItr = dObj.getRows("EASUnSelectedMailBoxes");
            while (rowItr.hasNext()) {
                final Row row = rowItr.next();
                row.set("DISPATCHED_TIME", (Object)dispatchedTime);
                row.set("STATUS", (Object)3);
                dObj.updateRow(row);
                unselectedMailboxesID.add((Long)row.get("EAS_MAILBOX_ID"));
            }
            SyMUtil.getPersistence().update(dObj);
            returnJSON.put((Object)"DISPATCHED_TIME", (Object)dispatchedTime);
            returnJSON.put((Object)"INAPPROPRIATE_DEVICE_ACCESS_STATES", (Object)unselectedMailboxesID.toArray(new Long[unselectedMailboxesID.size()]));
        }
        return returnJSON;
    }
    
    public static boolean isExchangeOnlineURI(final String serverFQDN) {
        final String EO_CHINA = "partner.outlook.cn";
        final String EO_GERMANY = "outlook.office.de";
        final String EO_DEFAULT = "outlook.office365.com";
        final String EO_US_GOV_DOD_1 = "webmail.apps.mil";
        final String EO_US_GOV_DOD_2 = "outlook-dod.office365.us";
        final String EO_US_GOVT_GCC_HIGH = "outlook.office365.us";
        return !SyMUtil.isStringEmpty(serverFQDN) && (serverFQDN.contains(EO_CHINA) || serverFQDN.contains(EO_GERMANY) || serverFQDN.contains(EO_DEFAULT) || serverFQDN.contains(EO_US_GOV_DOD_1) || serverFQDN.contains(EO_US_GOV_DOD_2) || serverFQDN.contains(EO_US_GOVT_GCC_HIGH));
    }
    
    static {
        EASMgmtDataHandler.easMgmtDataHandler = null;
    }
}
