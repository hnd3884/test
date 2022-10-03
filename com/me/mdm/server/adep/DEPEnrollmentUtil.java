package com.me.mdm.server.adep;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONArray;
import org.json.JSONException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class DEPEnrollmentUtil
{
    public static Logger logger;
    
    public static boolean isADAuthenticationEnabledForTemplateToken(final String templateToken) {
        try {
            if (!CustomerInfoUtil.getInstance().isMSP() || !CustomerInfoUtil.isSAS()) {
                final SelectQuery depQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
                depQuery.setCriteria(new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0));
                depQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_ID"));
                depQuery.addSelectColumn(new Column("DEPEnrollmentTemplate", "TEMPLATE_ID", "tempID"));
                depQuery.addSelectColumn(new Column("EnrollmentTemplate", "CUSTOMER_ID"));
                depQuery.addSelectColumn(new Column("DEPEnrollmentTemplate", "ENABLE_SELF_ENROLL"));
                final DataObject depDo = MDMUtil.getPersistence().get(depQuery);
                if (!depDo.isEmpty() && depDo.containsTable("DEPEnrollmentTemplate")) {
                    final Row depRow = depDo.getFirstRow("DEPEnrollmentTemplate");
                    if (depRow.get("ENABLE_SELF_ENROLL")) {
                        final Row enrollTemplateRow = depDo.getFirstRow("EnrollmentTemplate");
                        final Long customerId = (Long)enrollTemplateRow.get("CUSTOMER_ID");
                        final List domainList = MDMEnrollmentUtil.getInstance().getDomainNames(customerId);
                        if (domainList != null && !domainList.isEmpty()) {
                            return true;
                        }
                    }
                }
                else {
                    DEPEnrollmentUtil.logger.log(Level.INFO, "No row in DEPENROLLMENTTEMPLATE table, probably because of device enrolling through Apple configurator URL");
                }
            }
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        return false;
    }
    
    public static Long getDEPTokenIDForEnrollmentTemplateToken(final String templateToken) {
        try {
            final SelectQuery depQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            depQuery.setCriteria(new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0));
            depQuery.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            final DataObject depDo = MDMUtil.getPersistence().get(depQuery);
            final Row depRow = depDo.getFirstRow("DEPTokenDetails");
            return (Long)depRow.get("DEP_TOKEN_ID");
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(DEPEnrollmentUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
    
    public static String getTemplateTokenForTokenID(final Long tokenID) {
        try {
            final SelectQuery depQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            depQuery.setCriteria(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0));
            depQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
            final DataObject depDo = MDMUtil.getPersistence().get(depQuery);
            final Row depRow = depDo.getFirstRow("EnrollmentTemplate");
            return (String)depRow.get("TEMPLATE_TOKEN");
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
    
    public static Long getTemplateIDTokenForTokenID(final Long tokenID) {
        try {
            final SelectQuery depQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            depQuery.setCriteria(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0));
            depQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
            final DataObject depDo = MDMUtil.getPersistence().get(depQuery);
            if (depDo.containsTable("EnrollmentTemplate")) {
                final Row depRow = depDo.getFirstRow("EnrollmentTemplate");
                return (Long)depRow.get("TEMPLATE_ID");
            }
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        return null;
    }
    
    public static JSONObject getTemplateDetailsForUserAssignment(final String deviceSerialNumber) {
        try {
            final SelectQuery depQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            depQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            depQuery.addJoin(new Join("AppleDEPDeviceForEnrollment", "EnrollmentTemplateToDeviceEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            depQuery.addJoin(new Join("EnrollmentTemplateToDeviceEnrollment", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            depQuery.setCriteria(new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)deviceSerialNumber, 0));
            depQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_ID"));
            depQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"));
            depQuery.addSelectColumn(new Column("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            depQuery.addSelectColumn(new Column("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"));
            DataObject depDo = MDMUtil.getPersistence().get(depQuery);
            if (!depDo.containsTable("EnrollmentTemplate")) {
                final Row appleDEPRow = depDo.getFirstRow("AppleDEPDeviceForEnrollment");
                final Long tokenID = (Long)appleDEPRow.get("DEP_TOKEN_ID");
                try {
                    syncParticularDEPToken(tokenID);
                    depDo = MDMUtil.getPersistence().get(depQuery);
                }
                catch (final Exception e) {
                    DEPEnrollmentUtil.logger.log(Level.SEVERE, "Issue in syncing DEP token", e);
                }
            }
            final JSONObject responseJSON = new JSONObject();
            final Row etRow = depDo.getFirstRow("EnrollmentTemplate");
            final Row adfeRow = depDo.getFirstRow("AppleDEPDeviceForEnrollment");
            responseJSON.put("TEMPLATE_TOKEN", etRow.get("TEMPLATE_TOKEN"));
            responseJSON.put("DEP_TOKEN_ID", adfeRow.get("DEP_TOKEN_ID"));
            return responseJSON;
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Issue in syncing DEP token DataAccessException :", (Throwable)ex);
        }
        catch (final JSONException ex2) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Issue in syncing DEP token JSONException :", (Throwable)ex2);
        }
        return null;
    }
    
    public static String getDEPServerName(final Long depTokenID) {
        try {
            final JSONObject depServerDetails = getDEPServerDetails(depTokenID);
            return (depServerDetails == null) ? "--" : depServerDetails.optString("SERVER_NAME".toLowerCase(), "--");
        }
        catch (final JSONException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in getDEPServerName()..", (Throwable)ex);
            return null;
        }
    }
    
    public static void syncDEPToken(final Criteria cri) throws DataAccessException, JSONException, Exception {
        final JSONArray depSyncAllTokenJSON = new JSONArray();
        final SelectQuery sQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"));
        if (cri != null) {
            sQuery.setCriteria(cri);
        }
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        final Iterator item = DO.getRows("DEPTokenDetails");
        while (item.hasNext()) {
            final JSONObject depSingleTokenSyncJSON = new JSONObject();
            final Row tokenRow = item.next();
            depSingleTokenSyncJSON.put("DEP_TOKEN_ID", (Object)tokenRow.get("DEP_TOKEN_ID"));
            depSingleTokenSyncJSON.put("CUSTOMER_ID", (Object)tokenRow.get("CUSTOMER_ID"));
            depSyncAllTokenJSON.put((Object)depSingleTokenSyncJSON);
        }
        if (depSyncAllTokenJSON.length() > 0) {
            for (int i = 0; i < depSyncAllTokenJSON.length(); ++i) {
                final JSONObject singleTokenDetail = depSyncAllTokenJSON.optJSONObject(i);
                if (singleTokenDetail != null) {
                    try {
                        final Long customerID = singleTokenDetail.getLong("CUSTOMER_ID");
                        final Long tokenID = singleTokenDetail.getLong("DEP_TOKEN_ID");
                        final JSONObject depServerSyncDetails = ADEPServerSyncHandler.getInstance(tokenID, customerID).getDEPServerSyncDetails();
                        final int syncStatus = depServerSyncDetails.getInt("serverStatus");
                        if (syncStatus == 2) {
                            DEPEnrollmentUtil.logger.log(Level.INFO, "Previous sync failed with some error. So retrying DefineProfile, FetchDevices and AssignProfile for ABM server: {0}", new Object[] { tokenID });
                            AppleDEPWebServicetHandler.getInstance(tokenID, customerID).updateCursor(null);
                            final JSONObject profileJSON = AppleDEPProfileHandler.getInstance(tokenID, customerID).getDEPProfileDetails();
                            AppleDEPProfileHandler.getInstance(tokenID, customerID).createProfile(profileJSON);
                        }
                        if (syncStatus != 3 && syncStatus != 2) {
                            AppleDEPAccountDetailsHandler.getInstance().manageAccountDetails(customerID, tokenID);
                            AppleDEPProfileHandler.getInstance(tokenID, customerID).assignDEPProfile();
                        }
                    }
                    catch (final SyMException ex) {
                        DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception while syncing all tokens..", (Throwable)ex);
                    }
                }
            }
        }
    }
    
    public static void syncAllDepToken() throws DataAccessException, JSONException, Exception {
        syncDEPToken(null);
    }
    
    public static void syncDEPTokensForCustomer(final Long customerID) throws Exception {
        try {
            final Criteria customerCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            syncDEPToken(customerCri);
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public static void syncParticularDEPToken(final Long tokenID) throws Exception {
        try {
            final Criteria tokenCri = new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0);
            syncDEPToken(tokenCri);
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final JSONException ex2) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex2);
        }
    }
    
    public static void validateDEPTokenExpiry() {
        try {
            final Long[] customerIdsFromDB;
            final Long[] customerList = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (final Long customerID : customerIdsFromDB) {
                try {
                    DEPEnrollmentUtil.logger.log(Level.INFO, "Validate DEP token expiry for customer: {0}", customerID);
                    MessageProvider.getInstance().hideMessage("DEP_EXPIRED_MSG", customerID);
                    MessageProvider.getInstance().hideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerID);
                    validateDEPTokenExpiryForCustomer(customerID);
                }
                catch (final Exception ex) {
                    DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception inside validateDEPTokenExpiry: ", ex);
                }
            }
        }
        catch (final Exception ex2) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in validateDEPTokenExpiry: ", ex2);
        }
    }
    
    public static void validateDEPTokenExpiryForToken(final Long tokenID) {
        validateDEPTokenExpiry(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0));
    }
    
    public static void validateDEPTokenExpiryForCustomer(final Long customerID) {
        validateDEPTokenExpiry(new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0));
    }
    
    private static void validateDEPTokenExpiry(final Criteria cri) {
        try {
            final SelectQuery depServerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            depServerQuery.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
            depServerQuery.addSelectColumn(new Column("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE"));
            depServerQuery.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
            depServerQuery.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            depServerQuery.addSelectColumn(new Column("DEPAccountDetails", "SERVER_NAME"));
            depServerQuery.addSelectColumn(new Column("DEPAccountDetails", "ORG_NAME"));
            depServerQuery.addSelectColumn(new Column("DEPAccountDetails", "DEP_TOKEN_ID"));
            depServerQuery.addSelectColumn(new Column("DEPAccountDetails", "ADMIN_EMAIL_ID"));
            depServerQuery.setCriteria(cri);
            final DataObject DO = MDMUtil.getPersistence().get(depServerQuery);
            final Map<Long, Boolean> closeMap = new HashMap<Long, Boolean>();
            final JSONArray expiredServerList = new JSONArray();
            final JSONArray aboutToExpireServerList = new JSONArray();
            final Properties prop = new Properties();
            if (!DO.isEmpty()) {
                final Iterator tokenItem = DO.getRows("DEPTokenDetails");
                while (tokenItem.hasNext()) {
                    final Row tokenRow = tokenItem.next();
                    final Long expDate = (Long)tokenRow.get("ACCESS_TOKEN_EXPIRY_DATE");
                    final Long customerid = (Long)tokenRow.get("CUSTOMER_ID");
                    final Long depTokenID = (Long)tokenRow.get("DEP_TOKEN_ID");
                    final Row accountRow = DO.getRow("DEPAccountDetails", new Criteria(new Column("DEPAccountDetails", "DEP_TOKEN_ID"), (Object)depTokenID, 0));
                    final String serverName = (String)accountRow.get("SERVER_NAME");
                    final String tokenOrganisationName = (String)accountRow.get("ORG_NAME");
                    Hashtable ht = new Hashtable();
                    ht = DateTimeUtil.determine_From_To_Times("today");
                    final Long alertDate = expDate - 1296000000L;
                    final Long today = ht.get("date1");
                    ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerid);
                    if (!closeMap.containsKey(customerid)) {
                        closeMap.put(customerid, Boolean.FALSE);
                    }
                    if (today > alertDate) {
                        final Long diff = expDate - today;
                        final Long remaingDay = diff / 86400000L;
                        final JSONObject expiredServerObject = new JSONObject();
                        expiredServerObject.put("DEP_TOKEN_ID", tokenRow.get("DEP_TOKEN_ID"));
                        expiredServerObject.put("SERVER_NAME", (Object)serverName);
                        expiredServerObject.put("ORG_NAME", (Object)tokenOrganisationName);
                        expiredServerObject.put("ACCESS_TOKEN_EXPIRY_DATE", (Object)Utils.getEventTime(expDate));
                        expiredServerObject.put("ADMIN_EMAIL_ID", accountRow.get("ADMIN_EMAIL_ID"));
                        if (remaingDay > 0L) {
                            aboutToExpireServerList.put((Object)expiredServerObject);
                            if (closeMap.get(customerid)) {
                                continue;
                            }
                            MessageProvider.getInstance().unhideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerid);
                            MessageProvider.getInstance().hideMessage("DEP_EXPIRED_MSG", customerid);
                            closeMap.put(customerid, Boolean.TRUE);
                        }
                        else {
                            expiredServerList.put((Object)expiredServerObject);
                            if (closeMap.get(customerid)) {
                                continue;
                            }
                            MessageProvider.getInstance().unhideMessage("DEP_EXPIRED_MSG", customerid);
                            MessageProvider.getInstance().hideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerid);
                            closeMap.put(customerid, Boolean.TRUE);
                        }
                    }
                }
                String tableRows = "";
                int numberOfRows = 0;
                for (int i = 0; i < expiredServerList.length(); ++i) {
                    final JSONObject serverObj = expiredServerList.optJSONObject(i);
                    tableRows = tableRows + "<tr><td style=\"text-align: center;\">" + ++numberOfRows + "</td>" + "<td style=\"text-align: center;\">" + serverObj.optString("SERVER_NAME") + "</td>" + "<td style=\"text-align: center;\">" + serverObj.optString("ORG_NAME") + "</td>" + "<td style=\"text-align: center;\">" + serverObj.optString("ADMIN_EMAIL_ID") + "</td>" + "<td style=\"text-align: center;color: #ff0000;\">" + I18N.getMsg("mdm.common.expired_on", new Object[0]) + " " + serverObj.optString("ACCESS_TOKEN_EXPIRY_DATE") + "</td>" + "</tr>";
                }
                for (int i = 0; i < aboutToExpireServerList.length(); ++i) {
                    final JSONObject serverObj = aboutToExpireServerList.optJSONObject(i);
                    tableRows = tableRows + "<tr><td style=\"text-align: center;\">" + ++numberOfRows + "</td>" + "<td style=\"text-align: center;\">" + serverObj.optString("SERVER_NAME") + "</td>" + "<td style=\"text-align: center;\">" + serverObj.optString("ORG_NAME") + "</td>" + "<td style=\"text-align: center;\">" + serverObj.optString("ADMIN_EMAIL_ID") + "</td>" + "<td style=\"text-align: center;color: #ff9900;\">" + I18N.getMsg("dc.mdm.common.expires_on", new Object[0]) + " " + serverObj.optString("ACCESS_TOKEN_EXPIRY_DATE") + "</td>" + "</tr>";
                }
                if (expiredServerList.length() > 0 || aboutToExpireServerList.length() > 0) {
                    ((Hashtable<String, String>)prop).put("serverRows", tableRows);
                    MDMMailNotificationHandler.getInstance().sendDEPExpiryNotificationMailForCustomer(prop);
                }
            }
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, ex2);
        }
    }
    
    public static void createAndAssignAllDEPProfileAsynchronously() {
        processCreateAndAssignDEPProfileAsynchronously(null);
    }
    
    public static void processCreateAndAssignDEPProfileAsynchronously(final Criteria criteria) {
        try {
            final SelectQuery sQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            if (criteria != null) {
                sQuery.setCriteria(criteria);
            }
            sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
            sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"));
            final DataObject DO = MDMUtil.getReadOnlyPersistence().get(sQuery);
            final Iterator item = DO.getRows("DEPTokenDetails");
            while (item.hasNext()) {
                final Row tokenRow = item.next();
                final Long tokenId = (Long)tokenRow.get("DEP_TOKEN_ID");
                final Long custoemrId = (Long)tokenRow.get("CUSTOMER_ID");
                final JSONObject queueData = new JSONObject();
                queueData.put("DEP_TOKEN_ID", (Object)tokenId);
                final Long userId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
                if (userId != null) {
                    queueData.put("USER_ID", (Object)String.valueOf(userId));
                }
                final CommonQueueData depQueueItem = new CommonQueueData();
                depQueueItem.setClassName("com.me.mdm.server.adep.DEPAssignProfileTask");
                depQueueItem.setTaskName("DEPAssignProfileTask");
                depQueueItem.setCustomerId(custoemrId);
                depQueueItem.setJsonQueueData(queueData);
                CommonQueueUtil.getInstance().addToQueue(depQueueItem, CommonQueues.MDM_ENROLLMENT);
            }
        }
        catch (final Exception ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean isADAuthenticationEnabledForAnyDEPServerForCustomer(final Long customerID) {
        try {
            final SelectQuery depQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
            depQuery.addJoin(new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            final Criteria customerCri = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria isDEPSelfEnrollCriteria = new Criteria(new Column("DEPEnrollmentTemplate", "ENABLE_SELF_ENROLL"), (Object)true, 0);
            depQuery.setCriteria(customerCri.and(isDEPSelfEnrollCriteria));
            depQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
            final DataObject depDo = MDMUtil.getPersistence().get(depQuery);
            return !depDo.isEmpty();
        }
        catch (final Exception ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static Long getDEPTokenPendingProfileCreation(final Long customerID) {
        try {
            final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            deviceQuery.addJoin(new Join("DEPTokenDetails", "DEPTokenToGroup", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 1));
            deviceQuery.addJoin(new Join("DEPTokenDetails", "AppleDEPServerSyncStatus", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 1));
            deviceQuery.addJoin(new Join("DEPTokenToGroup", "EnrollmentTemplateToGroupRel", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
            deviceQuery.addJoin(new Join("EnrollmentTemplateToGroupRel", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            deviceQuery.addJoin(new Join("DEPEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            deviceQuery.addJoin(new Join("EnrollmentTemplate", "EnrollmentTemplateToRequest", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            deviceQuery.addJoin(new Join("EnrollmentTemplate", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            deviceQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
            deviceQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"));
            final Criteria errorCri = new Criteria(new Column("AppleDEPServerSyncStatus", "ERROR_CODE"), (Object)(-1), 0);
            final Criteria customerIDCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria profileCreationCriteria = new Criteria(new Column("DEPEnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 0);
            deviceQuery.setCriteria(customerIDCri.and(profileCreationCriteria).and(errorCri));
            final DataObject DO = MDMUtil.getPersistence().get(deviceQuery);
            if (!DO.isEmpty()) {
                final Row depTokenRow = DO.getFirstRow("DEPTokenDetails");
                return (Long)depTokenRow.get("DEP_TOKEN_ID");
            }
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception while checking whether there's any ABM server pending setup completion", (Throwable)ex);
        }
        return null;
    }
    
    public static List getExpiredServerNames(final Long customerID) {
        final List<String> serverNameList = new ArrayList<String>();
        final JSONArray serverArray = getExpiredServerDetails(customerID);
        for (int i = 0; i < serverArray.length(); ++i) {
            try {
                serverNameList.add(serverArray.getJSONObject(i).optString("SERVER_NAME".toLowerCase()));
            }
            catch (final JSONException ex) {
                DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return serverNameList;
    }
    
    public static List getAboutToExpireServerNames(final Long customerID) {
        final List<String> serverNameList = new ArrayList<String>();
        final JSONArray serverArray = getAboutToExpireServersDetails(customerID);
        for (int i = 0; i < serverArray.length(); ++i) {
            try {
                serverNameList.add(serverArray.getJSONObject(i).optString("SERVER_NAME".toLowerCase()));
            }
            catch (final JSONException ex) {
                DEPEnrollmentUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return serverNameList;
    }
    
    public static JSONArray getExpiredServerDetails(final Long customerID) {
        JSONArray expiredServers = new JSONArray();
        try {
            final Criteria custoemrCriteria = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Long today = MDMUtil.getCurrentTimeInMillis();
            final Criteria aboutToExpireCriteria = new Criteria(new Column("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE"), (Object)today, 6);
            expiredServers = getDEPServerDetails(custoemrCriteria.and(aboutToExpireCriteria));
        }
        catch (final Exception ex) {
            Logger.getLogger(DEPEnrollmentUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return expiredServers;
    }
    
    public static JSONArray getAboutToExpireServersDetails(final Long customerID) {
        JSONArray aboutTOExpireServers = new JSONArray();
        try {
            final Criteria custoemrCriteria = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Long today = MDMUtil.getCurrentTimeInMillis();
            final Criteria aboutToExpireCriteria = new Criteria(new Column("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE"), (Object)(today + 1296000000L), 6);
            aboutTOExpireServers = getDEPServerDetails(custoemrCriteria.and(aboutToExpireCriteria));
        }
        catch (final Exception ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, null, ex);
        }
        return aboutTOExpireServers;
    }
    
    public static JSONObject getDEPServerDetails(final Long depTokenID) {
        try {
            final JSONArray depServerDetails = getDEPServerDetails(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)depTokenID, 0));
            return depServerDetails.optJSONObject(0);
        }
        catch (final JSONException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in getDEPServerDetails()..", (Throwable)ex);
            return null;
        }
    }
    
    public static JSONArray getDEPServerDetails(final Criteria criteria) {
        final JSONArray serverDetailsJSONArray = new JSONArray();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            sQuery.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
            sQuery.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            sQuery.addSelectColumn(new Column("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE"));
            sQuery.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "ADMIN_EMAIL_ID"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "ORG_EMAIL"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "ORG_NAME"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "ORG_TYPE"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "SERVER_NAME"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "SERVER_UDID"));
            sQuery.addSelectColumn(new Column("DEPAccountDetails", "DEP_TOKEN_ID", "depTokenID"));
            sQuery.addSortColumn(new SortColumn(new Column("DEPTokenDetails", "ACCESS_TOKEN_EXPIRY_DATE"), false));
            if (criteria != null) {
                sQuery.setCriteria(criteria);
            }
            final DataObject serverDetailsDO = MDMUtil.getPersistence().get(sQuery);
            if (!serverDetailsDO.isEmpty()) {
                final Iterator tokenDetailRowItr = serverDetailsDO.getRows("DEPTokenDetails");
                while (tokenDetailRowItr.hasNext()) {
                    final Row tokenDetailRow = tokenDetailRowItr.next();
                    final Row accountDetailRow = serverDetailsDO.getRow("DEPAccountDetails", new Criteria(new Column("DEPAccountDetails", "DEP_TOKEN_ID", "depTokenID"), (Object)tokenDetailRow.get("DEP_TOKEN_ID"), 0));
                    final JSONObject tokenTableJSON = tokenDetailRow.getAsJSON();
                    final JSONObject accountTableJSON = accountDetailRow.getAsJSON();
                    accountTableJSON.put("server_name", (Object)StringUtils.abbreviate((String)accountDetailRow.get("SERVER_NAME"), 60));
                    JSONUtil.putAll(tokenTableJSON, accountTableJSON);
                    serverDetailsJSONArray.put((Object)tokenTableJSON);
                }
            }
        }
        catch (final DataAccessException ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in getDEPServerDetails()..", (Throwable)ex);
        }
        catch (final JSONException ex2) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in getDEPServerDetails()..", (Throwable)ex2);
        }
        return serverDetailsJSONArray;
    }
    
    public static Long createNewCustomGroupForDEPToken(final Long tokenID) {
        Long groupID = null;
        try {
            groupID = (Long)DBUtil.getValueFromDB("DEPTokenToGroup", "DEP_TOKEN_ID", (Object)tokenID, "GROUP_RESOURCE_ID");
            if (groupID == null) {
                DEPEnrollmentUtil.logger.log(Level.INFO, "No Group is mapped for DEP tokken {0},So ging to create New..:{0}", tokenID);
                groupID = createHiddenCustomGroup(tokenID);
                final DataObject newRowDO = (DataObject)new WritableDataObject();
                final Row depTokenTOGroupRow = new Row("DEPTokenToGroup");
                depTokenTOGroupRow.set("DEP_TOKEN_ID", (Object)tokenID);
                depTokenTOGroupRow.set("GROUP_RESOURCE_ID", (Object)groupID);
                newRowDO.addRow(depTokenTOGroupRow);
                MDMUtil.getPersistence().add(newRowDO);
                DEPEnrollmentUtil.logger.log(Level.INFO, "New Group Mapped to DEP token");
            }
            else {
                DEPEnrollmentUtil.logger.log(Level.INFO, "A Group is already Mapped to DEP token,So not creating a new CustomGroup");
            }
        }
        catch (final Exception ex) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception occured in Create/Get DEP Group for Token : createNewCustomGroupForDEPToken :", ex);
        }
        return groupID;
    }
    
    private static Long createHiddenCustomGroup(final Long tokenID) {
        final JSONObject depTokenDetails = getDEPServerDetails(tokenID);
        DMSecurityLogger.info(DEPEnrollmentUtil.logger, "DEPEnrollmentUtil", "createHiddenCustomerGroup", "Going to create new DEP Group for Token Details:{0}", (Object)depTokenDetails);
        final MDMCustomGroupDetails cgDetails = new MDMCustomGroupDetails();
        cgDetails.groupType = 8;
        cgDetails.platformType = 1;
        cgDetails.groupCategory = 1;
        cgDetails.customerId = CustomerInfoUtil.getInstance().getCustomerId();
        cgDetails.domainName = "MDM";
        cgDetails.groupPlatformType = 1;
        cgDetails.groupName = ((depTokenDetails == null) ? "--" : depTokenDetails.optString("SERVER_NAME".toLowerCase(), "--")) + tokenID;
        MDMGroupHandler.getInstance().addGroup(cgDetails);
        DEPEnrollmentUtil.logger.log(Level.INFO, "New DEP Group Created,Group detail:{0}", cgDetails);
        return cgDetails.resourceId;
    }
    
    public static int getDeviceFamily(final String depFamilyPropertyString) {
        if (depFamilyPropertyString.equals("iPad")) {
            return DEPConstants.DFEModel.IPAD;
        }
        if (depFamilyPropertyString.equals("iPhone")) {
            return DEPConstants.DFEModel.IPHONE;
        }
        if (depFamilyPropertyString.equals("iPod")) {
            return DEPConstants.DFEModel.IPOD;
        }
        if (depFamilyPropertyString.equals("Mac")) {
            return DEPConstants.DFEModel.MAC;
        }
        if (depFamilyPropertyString.equals("AppleTV")) {
            return DEPConstants.DFEModel.APPLE_TV;
        }
        return 0;
    }
    
    public static int getDeviceDEPProfileStatus(final String depProfileStatusString) {
        if (depProfileStatusString.equals("empty")) {
            return DEPConstants.DFEProfileStatus.EMPTY;
        }
        if (depProfileStatusString.equals("assigned")) {
            return DEPConstants.DFEProfileStatus.ASSIGNED;
        }
        if (depProfileStatusString.equals("pushed")) {
            return DEPConstants.DFEProfileStatus.PUSHED;
        }
        if (depProfileStatusString.equals("removed")) {
            return DEPConstants.DFEProfileStatus.REMOVED;
        }
        return 0;
    }
    
    public static void setDEPEnrollmentStatus(final int status, final Long customerId) {
        try {
            final int tokenCount = DBUtil.getRecordCount("DEPTokenDetails", "DEP_TOKEN_ID", new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            if (tokenCount > 1 && status != 3) {
                return;
            }
            final DataObject DO = MDMUtil.getPersistenceLite().get("DEPEnrollmentStatus", new Criteria(new Column("DEPEnrollmentStatus", "CUSTOMER_ID"), (Object)customerId, 0));
            if (DO.isEmpty()) {
                final Row depRow = new Row("DEPEnrollmentStatus");
                depRow.set("CUSTOMER_ID", (Object)customerId);
                depRow.set("DEP_STAUS", (Object)status);
                DO.addRow(depRow);
                MDMUtil.getPersistenceLite().add(DO);
            }
            else {
                final Row depRow = DO.getFirstRow("DEPEnrollmentStatus");
                depRow.set("CUSTOMER_ID", (Object)customerId);
                depRow.set("DEP_STAUS", (Object)status);
                DO.updateRow(depRow);
                MDMUtil.getPersistenceLite().update(DO);
            }
        }
        catch (final Exception e) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in setDEPEnrollmentStatus ", e);
        }
    }
    
    public static int checkAndResetDEPStatus(final Long customerID) throws Exception {
        final SelectQuery depDelQry = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
        depDelQry.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        depDelQry.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
        depDelQry.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
        depDelQry.setCriteria(new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject delDO = MDMUtil.getPersistence().get(depDelQry);
        int tokenCount = 0;
        if (delDO.containsTable("DEPTokenDetails")) {
            final Iterator itr = delDO.getRows("DEPTokenDetails");
            tokenCount = DBUtil.getIteratorSize(itr);
        }
        if (tokenCount == 0) {
            setDEPEnrollmentStatus(0, customerID);
            final DeleteQuery deleteAllTokens = (DeleteQuery)new DeleteQueryImpl("DEPTokenDetails");
            deleteAllTokens.setCriteria(new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            MDMUtil.getPersistence().delete(deleteAllTokens);
        }
        return tokenCount;
    }
    
    public static ArrayList<Long> getAllDepTokenIds(final Long customerID) throws Exception {
        try {
            final ArrayList<Long> depTokenIds = new ArrayList<Long>();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
            sQuery.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
            final Criteria cCusId = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            sQuery.setCriteria(cCusId);
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator iter = DO.get("DEPTokenDetails", "DEP_TOKEN_ID");
                while (iter.hasNext()) {
                    depTokenIds.add(iter.next());
                }
            }
            return depTokenIds;
        }
        catch (final Exception e) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in getAllDepTokenIds", e);
            throw e;
        }
    }
    
    public static int getDEPEnrollmentStatus(final Long customerId) {
        int depstatus = 0;
        try {
            final Integer status = (Integer)DBUtil.getValueFromDB("DEPEnrollmentStatus", "CUSTOMER_ID", (Object)customerId, "DEP_STAUS");
            if (status != null) {
                depstatus = status;
            }
        }
        catch (final Exception e) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception in getDEPEnrollmentStatus ", e);
        }
        return depstatus;
    }
    
    public static Map<Long, Boolean> getSelfEnrollDetailForABMServers(final Long customerId) throws DataAccessException {
        final Map<Long, Boolean> depTokenToSelfEnrollMap = new HashMap<Long, Boolean>();
        try {
            final SelectQuery selectQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            selectQuery.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            selectQuery.addSelectColumn(new Column("DEPTokenToGroup", "DEP_TOKEN_ID"));
            selectQuery.addSelectColumn(new Column("DEPTokenToGroup", "GROUP_RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("EnrollmentTemplateToGroupRel", "TEMPLATE_ID"));
            selectQuery.addSelectColumn(new Column("EnrollmentTemplateToGroupRel", "GROUP_RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("DEPEnrollmentTemplate", "TEMPLATE_ID"));
            selectQuery.addSelectColumn(new Column("DEPEnrollmentTemplate", "ENABLE_SELF_ENROLL"));
            selectQuery.setCriteria(new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("DEPTokenToGroup");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final long depToken = (long)row.get("DEP_TOKEN_ID");
                    final long depTokenGroup = (long)row.get("GROUP_RESOURCE_ID");
                    final Criteria depTokenGroupCriteria = new Criteria(new Column("EnrollmentTemplateToGroupRel", "GROUP_RESOURCE_ID"), (Object)depTokenGroup, 0);
                    final Join join = new Join("EnrollmentTemplateToGroupRel", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
                    final Row depEnrollmentTemplateRow = dataObject.getRow("DEPEnrollmentTemplate", depTokenGroupCriteria, join);
                    boolean isSelfEnrollEnabled = false;
                    if (depEnrollmentTemplateRow != null) {
                        isSelfEnrollEnabled = (boolean)depEnrollmentTemplateRow.get("ENABLE_SELF_ENROLL");
                    }
                    depTokenToSelfEnrollMap.put(depToken, isSelfEnrollEnabled);
                }
            }
        }
        catch (final Exception e) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception while checking whether self enroll is enabled for ABM Servers");
        }
        return depTokenToSelfEnrollMap;
    }
    
    public static Map<Long, Integer> getTypeForDepTokens(final Long customerId) {
        final Map<Long, Integer> depTokenToTypeMap = new HashMap<Long, Integer>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DEPTokenDetails"));
            selectQuery.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
            selectQuery.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID", "DEPTOKENDETAILS.DEP_TOKEN_ID"));
            selectQuery.addSelectColumn(new Column("DEPAccountDetails", "DEP_TOKEN_ID"));
            selectQuery.addSelectColumn(new Column("DEPAccountDetails", "ORG_TYPE"));
            selectQuery.setCriteria(new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("DEPAccountDetails");
                while (iterator.hasNext()) {
                    final Row depAccountDetailRow = iterator.next();
                    final long depToken = (long)depAccountDetailRow.get("DEP_TOKEN_ID");
                    final int orgType = (int)depAccountDetailRow.get("ORG_TYPE");
                    depTokenToTypeMap.put(depToken, orgType);
                }
            }
        }
        catch (final DataAccessException e) {
            DEPEnrollmentUtil.logger.log(Level.SEVERE, "Exception while getting dep token type", (Throwable)e);
        }
        return depTokenToTypeMap;
    }
    
    static {
        DEPEnrollmentUtil.logger = Logger.getLogger("MDMEnrollment");
    }
}
