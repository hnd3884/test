package com.me.mdm.server.easmanagement;

import java.util.Collection;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.List;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.GroupByClause;
import com.me.mdm.api.APIUtil;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.customview.ViewData;
import com.adventnet.i18n.I18N;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import com.adventnet.model.table.CVTableModel;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import org.json.JSONArray;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class CEAApiHandler
{
    public JSONObject getServerDetails(final Long customerId, final Long serverId) throws Exception {
        final org.json.simple.JSONObject requestJSON = new org.json.simple.JSONObject();
        if (serverId != null && serverId != -1L) {
            requestJSON.put((Object)"EAS_SERVER_ID", (Object)serverId);
            requestJSON.put((Object)"EASSelectedMailbox", (Object)"true");
            requestJSON.put((Object)"CEAAudit", (Object)"true");
        }
        requestJSON.put((Object)"EASServerDetails", (Object)"true");
        requestJSON.put((Object)"CUSTOMER_ID", (Object)customerId);
        final org.json.simple.JSONObject rawSimpleResponseJSON = EASMgmt.getInstance().getCEAdetails(requestJSON);
        final JSONObject rawResponseJSON = JSONUtil.getInstance().convertSimpleJSONtoJSON(rawSimpleResponseJSON);
        return this.formatGetServerDetailsResponse(rawResponseJSON);
    }
    
    private JSONObject formatGetServerDetailsResponse(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        if (requestJSON.has("EAS_SERVER_ID")) {
            responseJSON.put("server_id", (Object)JSONUtil.optLongForUVH(requestJSON, "EAS_SERVER_ID", Long.valueOf(-1L)));
            responseJSON.put("exchange_version", (Object)requestJSON.optString("exchange_version"));
            responseJSON.put("access_level_msg", (Object)requestJSON.optString("access_level"));
            responseJSON.put("total_mailboxes", requestJSON.optInt("total_mailboxes"));
            responseJSON.put("last_successful_sync", (Object)JSONUtil.optLong(requestJSON, "LAST_SUCCESSFUL_SYNC_TASK", -1L));
            responseJSON.put("unmanaged_device_count", requestJSON.optInt("unManagedDeviceCount"));
            responseJSON.put("eas_admin_email", (Object)requestJSON.optString("EAS_ADMIN_EMAIL"));
            responseJSON.put("sync_user_count", requestJSON.optInt("syncUserCount"));
            responseJSON.put("remarks", (Object)requestJSON.optString("REMARKS"));
            responseJSON.put("ps_version", requestJSON.optInt("PS_VERSION"));
            responseJSON.put("error_code", requestJSON.optInt("ERROR_CODE", -1));
            responseJSON.put("sync_status", requestJSON.optInt("SYNC_STATUS", -1));
            responseJSON.put("grace_device_count", requestJSON.optInt("graceDeviceCount"));
            responseJSON.put("last_attempted_sync_time", (Object)JSONUtil.optLong(requestJSON, "LAST_ATTEMPTED_SYNC_TASK", -1L));
            responseJSON.put("allowed_device_count", requestJSON.optInt("allowedDeviceCount"));
            responseJSON.put("server_name", (Object)requestJSON.optString("CONNECTION_URI"));
            responseJSON.put("customer_id", (Object)JSONUtil.optLongForUVH(requestJSON, "CUSTOMER_ID", Long.valueOf(-1L)));
            responseJSON.put("block_device_count", requestJSON.optInt("blockDeviceCount"));
            responseJSON.put("auth_mode", (Object)String.valueOf(requestJSON.get("AUTH_MODE")));
            responseJSON.put("selected_mailbox_count", (Object)requestJSON.optString("selectedMailboxCount"));
            responseJSON.put("sync_device_count", requestJSON.optInt("syncDeviceCount"));
            responseJSON.put("exchange_server_version", (Object)requestJSON.optString("EXCHANGE_SERVER_VERSION", "--"));
            responseJSON.put("eas_sync_status_id", (Object)JSONUtil.optLongForUVH(responseJSON, "EAS_Sync_Status_ID", Long.valueOf(-1L)));
            responseJSON.put("managed_device_count", requestJSON.optInt("managedDeviceCount"));
            responseJSON.put("default_access_level", requestJSON.optInt("DEFAULT_ACCESS_LEVEL", -1));
            responseJSON.put("remarks", (Object)requestJSON.optString("REMARKS"));
            responseJSON.put("description", (Object)requestJSON.optString("DETAILED_DESC", "--"));
            responseJSON.put("kb_url", (Object)requestJSON.optString("KB_URL", "--"));
            responseJSON.put("session_status", requestJSON.optInt("SESSION_STATUS", -1));
            responseJSON.put("rollback_date", requestJSON.optInt("CEAAudit", -1));
            responseJSON.put("policy_state", requestJSON.optInt("POLICY_STATUS", -1));
        }
        return responseJSON;
    }
    
    public JSONObject addOrUpdateCEAServer(final JSONObject requestJSON) throws Exception {
        final String task = String.valueOf(requestJSON.get("task"));
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        JSONObject responseJSON = new JSONObject();
        final org.json.simple.JSONObject serverJSON = new org.json.simple.JSONObject();
        serverJSON.put((Object)"TASK_TYPE", (Object)task);
        serverJSON.put((Object)"CUSTOMER_ID", (Object)customerId);
        serverJSON.put((Object)"ROLLBACK_BLOCKED_DEVICES", (Object)requestJSON.optString("rollback_blocked_devices"));
        if (task.equalsIgnoreCase("add") || task.equalsIgnoreCase("update")) {
            final String email = String.valueOf(requestJSON.get("eas_admin_email"));
            final String password = String.valueOf(requestJSON.get("eas_admin_password"));
            serverJSON.put((Object)"EAS_ADMIN_EMAIL", (Object)email);
            serverJSON.put((Object)"EAS_ADMIN_PASSWORD", (Object)password);
            if (task.equalsIgnoreCase("update")) {
                final Long serverId = JSONUtil.optLongForUVH(requestJSON, "server_id", Long.valueOf(-1L));
                serverJSON.put((Object)"EAS_SERVER_ID", (Object)serverId);
            }
            if (task.equalsIgnoreCase("add")) {
                final String serverName = String.valueOf(requestJSON.get("connection_uri"));
                serverJSON.put((Object)"CONNECTION_URI", (Object)serverName);
            }
            final Long newEXServerID = EASMgmtDataHandler.getInstance().addOrUpdateEASServerDetails(serverJSON);
            final org.json.simple.JSONObject syncRequestDetails = new org.json.simple.JSONObject();
            syncRequestDetails.put((Object)"install_exo_v2", (Object)requestJSON.optBoolean("install_exo_v2", false));
            syncRequestDetails.put((Object)"SEND_GRACE_MAILS", (Object)Boolean.FALSE);
            syncRequestDetails.put((Object)"EAS_SERVER_ID", (Object)String.valueOf(newEXServerID));
            EASMgmt.getInstance().handleSyncRequest(syncRequestDetails);
            responseJSON = this.getServerDetails(newEXServerID, customerId);
        }
        else if (task.equalsIgnoreCase("delete")) {
            serverJSON.put((Object)"EAS_ADMIN_PASSWORD", (Object)"");
            serverJSON.put((Object)"CONNECTION_URI", (Object)"");
            final Long serverId2 = JSONUtil.optLongForUVH(requestJSON, "server_id", Long.valueOf(-1L));
            serverJSON.put((Object)"EAS_SERVER_ID", (Object)String.valueOf(serverId2));
            EASMgmt.getInstance().removeCEA(serverJSON);
        }
        return responseJSON;
    }
    
    public void validateCEAServer(final Long serverId, final Long customerId) throws DataAccessException {
        final Criteria customerCriteria = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
        final DataObject dataObject = MDMUtil.getPersistence().get("EASServerDetails", customerCriteria.and(serverCriteria));
        if (dataObject == null || dataObject.isEmpty()) {
            throw new APIHTTPException("CEA0001", new Object[] { serverId });
        }
    }
    
    public JSONObject addOrUpdateCEAPolicy(final JSONObject requestJSON) throws JSONException, DataAccessException, SyMException {
        final String task = String.valueOf(requestJSON.get("task"));
        final Long serverId = JSONUtil.optLongForUVH(requestJSON, "server_id", Long.valueOf(-1L));
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final org.json.simple.JSONObject serverJSON = new org.json.simple.JSONObject();
        serverJSON.put((Object)"EAS_SERVER_ID", (Object)String.valueOf(serverId));
        serverJSON.put((Object)"CUSTOMER_ID", (Object)customerId);
        serverJSON.put((Object)"TASK_TYPE", (Object)task);
        serverJSON.put((Object)"ROLLBACK_BLOCKED_DEVICES", (Object)requestJSON.optString("rollback_blocked_devices"));
        JSONObject responseJSON = new JSONObject();
        if (task.equalsIgnoreCase("add") || task.equalsIgnoreCase("update")) {
            serverJSON.put((Object)"APPLIED_FOR", (Object)requestJSON.getInt("applied_for"));
            if (!requestJSON.has("SEND_NOTIF_MAIL".toLowerCase())) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            final int sendNotifMail = requestJSON.getInt("SEND_NOTIF_MAIL".toLowerCase());
            if (sendNotifMail != 0 && !EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerId)) {
                throw new APIHTTPException("CEA0008", new Object[0]);
            }
            serverJSON.put((Object)"SEND_NOTIF_MAIL", (Object)requestJSON.getInt("SEND_NOTIF_MAIL".toLowerCase()));
            serverJSON.put((Object)"GRACE_DAYS", (Object)requestJSON.getInt("grace_days"));
            serverJSON.put((Object)"ROLLBACK_BLOCKED_DEVICES", (Object)String.valueOf(requestJSON.get("rollback_blocked_devices")));
            final JSONArray userList = requestJSON.optJSONArray("user_list");
            serverJSON.put((Object)"EASSelectedMailbox", (Object)JSONUtil.getInstance().convertStringJSONArrayTOList(userList));
            serverJSON.put((Object)"UPDATE_POLICY_SELECTION", (Object)String.valueOf(Boolean.TRUE));
        }
        EASMgmt.getInstance().configCEApolicy(serverJSON);
        if (task.equalsIgnoreCase("add")) {
            responseJSON = this.getCEAPolicyDetails(requestJSON);
        }
        return responseJSON;
    }
    
    public JSONObject getCEAPolicyDetails(final JSONObject requestJSON) throws DataAccessException, SyMException, JSONException {
        final Long serverId = JSONUtil.optLongForUVH(requestJSON, "server_id", Long.valueOf(-1L));
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final SelectQuery selectQuery = getCEAPolicyQuery();
        final Criteria customerCriteria = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
        selectQuery.setCriteria(customerCriteria.and(serverCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject responseJSON = new JSONObject();
        if (dataObject == null || dataObject.isEmpty()) {
            Logger.getLogger("EASMgmtLogger").log(Level.INFO, "No policy configured for server id: {0}", serverId);
        }
        else {
            final Row easPolicyRow = dataObject.getFirstRow("EASPolicy");
            final Long policyId = (Long)easPolicyRow.get("EAS_POLICY_ID");
            final boolean enforceCEA = (boolean)easPolicyRow.get("ENFORCE_CONDITIONAL_ACCESS");
            final int appliedFor = (int)easPolicyRow.get("APPLIED_FOR");
            final Integer sendNotifMail = (Integer)easPolicyRow.get("SEND_NOTIF_MAIL");
            final int graceDays = (int)easPolicyRow.get("GRACE_DAYS");
            final Long updatedTime = (Long)easPolicyRow.get("UPDATED_TIME");
            final Long updatedBy = (Long)easPolicyRow.get("UPDATED_BY");
            final String updatedByName = DMUserHandler.getUserNameFromUserID(updatedBy);
            final int policyStatus = (int)easPolicyRow.get("POLICY_STATUS");
            final Iterator selectedMailBoxIteror = dataObject.getRows("EASSelectedMailbox");
            final JSONArray userListJSONArray = new JSONArray();
            while (selectedMailBoxIteror.hasNext()) {
                final Row selectedMailBoxRow = selectedMailBoxIteror.next();
                final Long mailBoxId = (Long)selectedMailBoxRow.get("EAS_MAILBOX_ID");
                final Row mailBoxDetailsRow = dataObject.getRow("EASMailboxDetails", new Criteria(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"), (Object)mailBoxId, 0));
                final String displayName = (String)mailBoxDetailsRow.get("DISPLAY_NAME");
                final String emailAddress = (String)mailBoxDetailsRow.get("EMAIL_ADDRESS");
                final JSONObject userJSON = new JSONObject();
                userJSON.put("eas_mailbox_id", (Object)mailBoxId);
                userJSON.put("user_name", (Object)displayName);
                userJSON.put("email", (Object)emailAddress);
                userListJSONArray.put((Object)userJSON);
            }
            responseJSON.put("policy_id", (Object)policyId);
            responseJSON.put("server_id", (Object)serverId);
            responseJSON.put("rollback_blocked_devices", enforceCEA);
            responseJSON.put("applied_for", appliedFor);
            responseJSON.put("SEND_NOTIF_MAIL".toLowerCase(), (Object)sendNotifMail);
            responseJSON.put("grace_days", graceDays);
            responseJSON.put("updated_time", (Object)updatedTime);
            responseJSON.put("updated_by", (Object)updatedBy);
            responseJSON.put("updated_by_name", (Object)updatedByName);
            responseJSON.put("policy_state", policyStatus);
            responseJSON.put("user_list", (Object)userListJSONArray);
        }
        return responseJSON;
    }
    
    public static SelectQuery getCEAPolicyQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EASServerDetails"));
        final Join easPolicyJoin = new Join("EASServerDetails", "EASPolicy", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2);
        final Join easMailBoxDetailsJoin = new Join("EASServerDetails", "EASMailboxDetails", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2);
        final Join easSelectedMailBox = new Join("EASMailboxDetails", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 1);
        selectQuery.addJoin(easPolicyJoin);
        selectQuery.addJoin(easMailBoxDetailsJoin);
        selectQuery.addJoin(easSelectedMailBox);
        selectQuery.addSelectColumn(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASServerDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "EAS_POLICY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "EAS_SERVER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "ENFORCE_CONDITIONAL_ACCESS"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "APPLIED_FOR"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "GRACE_DAYS"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "UPDATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "UPDATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "POLICY_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("EASPolicy", "SEND_NOTIF_MAIL"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"));
        return selectQuery;
    }
    
    public JSONObject getCEADeviceDetails(final JSONObject requestJSON) throws Exception {
        final SelectQuery selectQuery = this.getCEADeviceQuery();
        selectQuery.setRange((Range)null);
        final Long serverId = JSONUtil.optLongForUVH(requestJSON, "server_id", Long.valueOf(-1L));
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
        selectQuery.setCriteria(serverCriteria);
        return this.getJSONFromDeviceDetailsQuery(selectQuery);
    }
    
    private JSONObject getJSONFromDeviceDetailsQuery(final SelectQuery selectQuery) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray devicesJSONArray = new JSONArray();
        final CustomViewRequest customViewRequest = new CustomViewRequest(selectQuery);
        final CustomViewManager customViewManager = (CustomViewManager)BeanUtil.lookup("TableViewManager");
        final ViewData viewData = customViewManager.getData(customViewRequest);
        final CVTableModel model = (CVTableModel)viewData.getModel();
        final int rowCount = model.getRowCount();
        final int columnCount = model.getColumnCount();
        final Column[] modelColumns = model.getColumns();
        final ArrayList requiredColumnList = new ArrayList();
        requiredColumnList.add(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"));
        requiredColumnList.add(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
        requiredColumnList.add(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"));
        requiredColumnList.add(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"));
        requiredColumnList.add(Column.getColumn("EASDeviceDetails", "EAS_DEVICE_ID"));
        requiredColumnList.add(Column.getColumn("EASDeviceDetails", "EAS_DEVICE_IDENTIFIER"));
        requiredColumnList.add(Column.getColumn("EASDeviceDetails", "DEVICE_NAME"));
        requiredColumnList.add(Column.getColumn("EASDeviceDetails", "DEVICE_MODEL"));
        requiredColumnList.add(Column.getColumn("EASDeviceDetails", "DEVICE_OS"));
        requiredColumnList.add(Column.getColumn("EASDeviceDetails", "LAST_UPDATED_TIME"));
        requiredColumnList.add(Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_DEVICE_ID"));
        requiredColumnList.add(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"));
        requiredColumnList.add(Column.getColumn("EASMailboxDeviceInfo", "LAST_SUCCESSFUL_SYNC"));
        requiredColumnList.add(Column.getColumn("EASManagedDeviceRel", "MANAGED_DEVICE_ID"));
        requiredColumnList.add(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        requiredColumnList.add(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
        requiredColumnList.add(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        requiredColumnList.add(Column.getColumn("EASMailboxGracePeriod", "GRACE_PERIOD_START"));
        requiredColumnList.add(Column.getColumn("EASPolicy", "GRACE_DAYS"));
        for (int i = 0; i < rowCount; ++i) {
            final JSONObject deviceJSON = new JSONObject();
            for (int j = 0; j < columnCount; ++j) {
                if (requiredColumnList.contains(modelColumns[j])) {
                    deviceJSON.put(modelColumns[j].getColumnName().toLowerCase(), model.getValueAt(i, j));
                }
            }
            JSONUtil.changeKey(deviceJSON, "device_id", "EAS_DEVICE_ID".toLowerCase());
            JSONUtil.changeKey(deviceJSON, "server_id", "EAS_SERVER_ID".toLowerCase());
            JSONUtil.changeKey(deviceJSON, "user_name", "DISPLAY_NAME".toLowerCase());
            if (deviceJSON.has("MANAGED_STATUS".toLowerCase())) {
                final int managedStatus = deviceJSON.getInt("MANAGED_STATUS".toLowerCase());
                final Long gracePeriodStart = JSONUtil.optLongForUVH(deviceJSON, "GRACE_PERIOD_START".toLowerCase(), Long.valueOf(-1L));
                final int graceDays = deviceJSON.optInt("GRACE_DAYS".toLowerCase(), 0);
                final int accessState = deviceJSON.optInt("DEVICE_ACCESS_STATE".toLowerCase(), 0);
                Long gracePeriodExpiresInMilli = null;
                Long gracePeriodExpiresIn = null;
                String managedStatusInfo = null;
                final String managedUserEmailId = String.valueOf(deviceJSON.get("EMAIL_ADDRESS".toLowerCase()));
                final String mailBoxEmailId = String.valueOf(deviceJSON.get("EMAIL_ADDRESS".toLowerCase()));
                if (gracePeriodStart != -1L && graceDays != 0) {
                    final Long graceDaysinMillis = TimeUnit.DAYS.toMillis(graceDays);
                    gracePeriodExpiresInMilli = graceDaysinMillis - (System.currentTimeMillis() - gracePeriodStart);
                    gracePeriodExpiresIn = TimeUnit.MILLISECONDS.toDays(gracePeriodExpiresInMilli);
                    if (gracePeriodExpiresInMilli > 0L && gracePeriodExpiresIn == 0L) {
                        gracePeriodExpiresIn = 1L;
                    }
                }
                if (managedStatus == 2) {
                    if (managedUserEmailId.equalsIgnoreCase(mailBoxEmailId)) {
                        managedStatusInfo = I18N.getMsg("dc.mdm.db.agent.enroll.agent_enroll_finished", new Object[0]);
                    }
                    else {
                        managedStatusInfo = I18N.getMsg("mdm.cea.graceperiod.emailmistch.info", new Object[] { gracePeriodExpiresIn, mailBoxEmailId, managedUserEmailId });
                    }
                }
                else if (accessState == 0 && gracePeriodExpiresIn != null && gracePeriodExpiresIn > 0L) {
                    managedStatusInfo = I18N.getMsg("mdm.cea.graceperiod.notenrolled.info", new Object[] { gracePeriodExpiresIn });
                }
                deviceJSON.put("status_msg", (Object)managedStatusInfo);
            }
            devicesJSONArray.put((Object)deviceJSON);
        }
        responseJSON.put("devices", (Object)devicesJSONArray);
        return responseJSON;
    }
    
    private SelectQuery getCEADeviceQuery() throws DataAccessException {
        final Long queryId = (Long)MDMUtil.getPersistence().get("SelectTable", new Criteria(Column.getColumn("SelectTable", "TABLEALIAS"), (Object)"EASMailboxDetails", 0, false)).getFirstRow("SelectTable").get("QUERYID");
        return QueryUtil.getSelectQuery((long)queryId);
    }
    
    public void removeCEADevices(final JSONObject requestJSON) throws JSONException {
        final Long serverId = JSONUtil.optLongForUVH(requestJSON, "server_id", Long.valueOf(-1L));
        final JSONArray devicesJSONArray = requestJSON.getJSONArray("devices");
        for (int i = 0; i < devicesJSONArray.length(); ++i) {
            final org.json.simple.JSONObject removeDeviceJSON = new org.json.simple.JSONObject();
            final Long deviceId = JSONUtil.optLongForUVH(devicesJSONArray, i, -1L);
            removeDeviceJSON.put((Object)"EAS_SERVER_ID", (Object)String.valueOf(serverId));
            removeDeviceJSON.put((Object)"EAS_MAILBOX_DEVICE_ID", (Object)String.valueOf(deviceId));
            EASMgmt.getInstance().removeDeviceFromEAShost(removeDeviceJSON);
        }
    }
    
    public JSONObject getMailBoxPickList(final JSONObject requestJSON) throws DataAccessException, JSONException {
        final JSONObject responseJSON = new JSONObject();
        final APIUtil apiUtil = new APIUtil();
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
        final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final SelectQuery selectQuery = this.getEASMailBoxQuery();
        selectQuery.addSelectColumn(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "ADDED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "LAST_UPDATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
        final SelectQuery countQuery = this.getEASMailBoxQuery();
        final Column countColumn = new Column("EASMailboxDetails", "EAS_MAILBOX_ID").distinct().count();
        countColumn.setColumnAlias("total_mailboxes");
        final Column groupByColumn = new Column("EASMailboxDetails", "EAS_SERVER_ID");
        final List groupByList = new ArrayList();
        groupByList.add(groupByColumn);
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        countQuery.addSelectColumn(countColumn);
        countQuery.setGroupByClause(groupByClause);
        final Criteria customerCriteria = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
        selectQuery.setCriteria(customerCriteria.and(serverCriteria));
        countQuery.setCriteria(customerCriteria.and(serverCriteria));
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"), (Object)search, 12, false).or(new Criteria(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), (Object)search, 12, false));
            Criteria criteria = selectQuery.getCriteria().and(searchCriteria);
            selectQuery.setCriteria(criteria);
            criteria = countQuery.getCriteria().and(searchCriteria);
            countQuery.setCriteria(criteria);
        }
        final JSONArray resultJSONArray = JSONUtil.getInstance().convertSimpleJSONarToJSONar(MDMUtil.executeSelectQuery(countQuery));
        int count = 0;
        if (resultJSONArray.length() > 0) {
            count = resultJSONArray.getJSONObject(0).optInt("total_mailboxes", 0);
        }
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
            if (!selectAll) {
                selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("display_name")) {
                        selectQuery.addSortColumn(new SortColumn("EASMailboxDetails", "DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("email")) {
                        selectQuery.addSortColumn(new SortColumn("EASMailboxDetails", "EMAIL_ADDRESS", (boolean)isSortOrderASC));
                    }
                }
                else {
                    selectQuery.addSortColumn(new SortColumn("EASMailboxDetails", "EAS_MAILBOX_ID", true));
                }
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final JSONObject tempJSON = this.getMailBoxJSONFromDO(dataObject);
                responseJSON.put("mail_boxes", (Object)tempJSON.getJSONArray("mail_boxes"));
            }
            else {
                responseJSON.put("mail_boxes", (Object)new JSONArray());
            }
        }
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(requestJSON.getJSONObject("msg_header").get("request_url")));
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            responseJSON.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
        }
        return responseJSON;
    }
    
    private JSONObject getMailBoxJSONFromDO(final DataObject dataObject) throws DataAccessException, JSONException {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray mailBoxesJSONArray = new JSONArray();
        JSONObject mailBoxJSON = null;
        final Iterator iterator = dataObject.getRows("EASMailboxDetails");
        while (iterator.hasNext()) {
            final Row easMailBoxRow = iterator.next();
            mailBoxJSON = new JSONObject();
            final Long easMailBoxId = (Long)easMailBoxRow.get("EAS_MAILBOX_ID");
            final String emailAddress = (String)easMailBoxRow.get("EMAIL_ADDRESS");
            final String displayName = (String)easMailBoxRow.get("DISPLAY_NAME");
            final Long addedTime = (Long)easMailBoxRow.get("ADDED_TIME");
            final Long updatedTime = (Long)easMailBoxRow.get("LAST_UPDATED_TIME");
            mailBoxJSON.put("eas_mailbox_id", (Object)easMailBoxId);
            mailBoxJSON.put("email", (Object)emailAddress);
            mailBoxJSON.put("user_name", (Object)displayName);
            mailBoxJSON.put("added_time", (Object)addedTime);
            mailBoxJSON.put("updated_time", (Object)updatedTime);
            mailBoxesJSONArray.put((Object)mailBoxJSON);
        }
        responseJSON.put("mail_boxes", (Object)mailBoxesJSONArray);
        return responseJSON;
    }
    
    private SelectQuery getEASMailBoxQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EASMailboxDetails"));
        final Join easServerDetailsJoin = new Join("EASMailboxDetails", "EASServerDetails", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2);
        selectQuery.addJoin(easServerDetailsJoin);
        return selectQuery;
    }
    
    public void validateMailBoxDetails(final JSONArray usersJSONArray, final Long serverId) throws DataAccessException {
        final ArrayList userList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(usersJSONArray);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EASMailboxDetails"));
        final Criteria mailBoxCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"), (Object)userList.toArray(), 8);
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
        selectQuery.setCriteria(mailBoxCriteria.and(serverCriteria));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("EASMailboxDetails");
        final ArrayList existingUserList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "EAS_MAILBOX_ID");
        userList.removeAll(existingUserList);
        if (userList.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Object userId : userList) {
                stringBuilder.append(userId);
                stringBuilder.append(", ");
            }
            throw new APIHTTPException("CEA0006", new Object[] { stringBuilder });
        }
    }
    
    public void validateCEADevices(final JSONArray devicesJSONArray, final Long serverId) throws DataAccessException {
        final ArrayList deviceList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJSONArray);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EASMailboxDetails"));
        final Join easMailBoxDeviceRel = new Join("EASMailboxDetails", "EASMailboxDeviceRel", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2);
        selectQuery.addJoin(easMailBoxDeviceRel);
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
        selectQuery.setCriteria(serverCriteria);
        selectQuery.addSelectColumn(Column.getColumn("EASMailboxDeviceRel", "EAS_MAILBOX_DEVICE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("EASMailboxDeviceRel");
        final ArrayList existingDeviceList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "EAS_MAILBOX_DEVICE_ID");
        deviceList.removeAll(existingDeviceList);
        if (deviceList.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Object deviceId : deviceList) {
                stringBuilder.append(deviceId);
                stringBuilder.append(", ");
            }
            throw new APIHTTPException("CEA0007", new Object[] { stringBuilder });
        }
    }
    
    public void validateCEAPolicy(final Long serverId, final Long policyId) throws DataAccessException {
        final Criteria serverCriteria = new Criteria(Column.getColumn("EASPolicy", "EAS_SERVER_ID"), (Object)serverId, 0);
        final Criteria policyCriteria = new Criteria(Column.getColumn("EASPolicy", "EAS_POLICY_ID"), (Object)policyId, 0);
        final DataObject dataObject = MDMUtil.getPersistence().get("EASPolicy", serverCriteria.and(policyCriteria));
        if (dataObject == null || dataObject.isEmpty()) {
            throw new APIHTTPException("CEA0002", new Object[] { policyId });
        }
    }
    
    public void syncCEAServer(final Long serverId, final Long customerId) {
        final org.json.simple.JSONObject CEASyncRequest = new org.json.simple.JSONObject();
        CEASyncRequest.put((Object)"SEND_GRACE_MAILS", (Object)Boolean.FALSE);
        EASMgmt.getInstance().handleSyncRequest(CEASyncRequest);
    }
}
