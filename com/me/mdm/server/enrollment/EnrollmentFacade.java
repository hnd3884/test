package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.adventnet.sym.server.mdm.message.UEMCentralLicenseMessageHandler;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import javax.transaction.SystemException;
import com.adventnet.sym.server.mdm.util.InactiveDevicePolicyConstants;
import com.me.mdm.server.util.MDMTransactionManager;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.idps.core.util.ADSyncDataHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Set;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;
import com.me.mdm.server.android.knox.enroll.KnoxLicenseHandler;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.notification.WakeUpProcessor;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.TreeSet;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.api.user.UserFacade;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.adventnet.sym.webclient.mdm.MDMEnrollAction;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.net.URLDecoder;
import java.util.Map;
import com.me.devicemanagement.framework.server.tree.datahandler.TreeNodeDataHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.i18n.I18N;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMAgentBuildVersionsUtil;
import com.me.mdm.server.util.MDMCheckSumProvider;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.enrollment.deprovision.DeprovisionRequest;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.device.DeviceFacade;
import java.util.Arrays;
import java.util.Collection;
import com.me.mdm.server.user.ManagedUserFacade;
import java.util.HashMap;
import com.me.mdm.api.APIActionsHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.EREvent;
import java.util.Properties;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import com.me.mdm.api.paging.PagingUtil;
import java.sql.Connection;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class EnrollmentFacade
{
    Logger logger;
    
    public EnrollmentFacade() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public Object getEnrollmentRequests(final JSONObject message) throws APIHTTPException {
        Connection conn = null;
        final Long customerId = APIUtil.getCustomerID(message);
        final JSONObject response = new JSONObject();
        final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
        DataSet ds = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), true));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
        final SelectQuery cQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        cQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        cQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        cQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        cQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        cQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
        Column selCol = new Column("DeviceEnrollmentRequest", "MANAGED_USER_ID");
        selCol = selCol.distinct();
        selCol = selCol.count();
        cQuery.addSelectColumn(selCol);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        selectQuery.setCriteria(userNotInTrashCriteria);
        cQuery.setCriteria(userNotInTrashCriteria);
        try {
            final int count = DBUtil.getRecordCount(cQuery);
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            response.put("metadata", (Object)meta);
            if (pagingJSON != null) {
                response.put("paging", (Object)pagingJSON);
            }
            final JSONArray result = new JSONArray();
            JSONObject eReq = null;
            JSONArray requests = null;
            JSONObject req = null;
            final int start = pagingUtil.getStartIndex();
            final int end = start + pagingUtil.getLimit();
            int i = 0;
            final DataObject managedUserEnrollDO = MDMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> rowIterator = managedUserEnrollDO.getRows("ManagedUser");
            while (rowIterator.hasNext()) {
                if (++i < start) {
                    rowIterator.next();
                }
                else {
                    if (i >= end) {
                        break;
                    }
                    final Row row = rowIterator.next();
                    eReq = new JSONObject();
                    eReq.put("MANAGED_USER_ID", row.get("MANAGED_USER_ID"));
                    final Iterator<Row> erIterator = managedUserEnrollDO.getRows("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), row.get("MANAGED_USER_ID"), 0));
                    requests = new JSONArray();
                    while (erIterator.hasNext()) {
                        final Row erRow = erIterator.next();
                        req = new JSONObject();
                        req.put("ENROLLMENT_REQUEST_ID", (Object)erRow.get("ENROLLMENT_REQUEST_ID").toString());
                        final int platformType = (int)erRow.get("PLATFORM_TYPE");
                        req.put("PLATFORM_TYPE", platformType);
                        req.put("platform", (Object)MDMEnrollmentUtil.getPlatformString(platformType));
                        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
                        query.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                        query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
                        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                        query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                        query.addSelectColumn(Column.getColumn((String)null, "*"));
                        query.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), erRow.get("ENROLLMENT_REQUEST_ID"), 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0)));
                        try {
                            final RelationalAPI relapi = RelationalAPI.getInstance();
                            conn = relapi.getConnection();
                            ds = relapi.executeQuery((Query)query, conn);
                            if (ds.next()) {
                                String managedStatus = null;
                                final Integer mStatus = (Integer)ds.getValue("MANAGED_STATUS");
                                if (mStatus != null) {
                                    switch (mStatus) {
                                        case 1: {
                                            managedStatus = "Yet To Enroll";
                                            break;
                                        }
                                        case 11: {
                                            managedStatus = "Retired";
                                            break;
                                        }
                                        case 4: {
                                            managedStatus = "Device management revoked by user";
                                            break;
                                        }
                                        case 2: {
                                            req.put("device_id", (Object)String.valueOf(ds.getValue("RESOURCE_ID")));
                                            managedStatus = "Enrollment Success";
                                            break;
                                        }
                                    }
                                    req.put("managed_status_id", (Object)mStatus);
                                    req.put("managed_status", (Object)managedStatus);
                                }
                                else if (ds.getValue("RESOURCE_ID") == null) {
                                    req.put("managed_status_id", 1);
                                    req.put("managed_status", (Object)"Yet To Enroll");
                                }
                            }
                        }
                        finally {
                            this.closeConnection(conn, ds);
                        }
                        requests.put((Object)req);
                    }
                    eReq.put("requests", (Object)requests);
                    result.put((Object)eReq);
                }
            }
            response.put("device_enrollment_requests", (Object)result);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception in getEnrollmentRequests", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in closeConnection....", ex);
        }
    }
    
    private JSONObject addEnrollmentRequest(Properties properties) {
        String enrollStatus = "2";
        final JSONObject json = new JSONObject();
        try {
            json.put("ENROLL_STATUS", (Object)enrollStatus);
            String sUserName = ((Hashtable<K, String>)properties).get("NAME");
            final String sEmailID = ((Hashtable<K, String>)properties).get("EMAIL_ADDRESS");
            final Long customerID = ((Hashtable<K, Long>)properties).get("CUSTOMER_ID");
            if (sUserName != null && !sUserName.equals("")) {
                ((Hashtable<String, Long>)properties).put("USER_ID", MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
                final EREvent erEvent = new EREvent(customerID, sUserName, sEmailID, properties.getProperty("ENROLLMENT_REQUEST_ID", null));
                final String status = EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 2);
                if (status != null && status.contains("failure")) {
                    enrollStatus = "9" + status;
                    json.put("ENROLL_STATUS", (Object)enrollStatus);
                    return json;
                }
                ((Hashtable<String, Integer>)properties).put("ENROLLMENT_TYPE", 1);
                ((Hashtable<String, Boolean>)properties).put("IS_SELF_ENROLLMENT", Boolean.FALSE);
                properties = MDMEnrollmentRequestHandler.getInstance().sendEnrollmentRequest(properties);
                enrollStatus = ((Hashtable<K, Object>)properties).get("ENROLL_STATUS").toString();
                json.put("ENROLL_STATUS", (Object)enrollStatus);
                json.put("erid", (Object)((Hashtable<K, Long>)properties).get("ENROLLMENT_REQUEST_ID"));
                final String sEventLogRemarks = "dc.mdm.actionlog.enrollment.request_created";
                sUserName = MDMUtil.getInstance().decodeURIComponentEquivalent(sUserName);
                final Object remarksArgs = sEmailID + "@@@" + sUserName;
                final String sLoggedOnUserName = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sLoggedOnUserName, sEventLogRemarks, remarksArgs, customerID);
                MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception in addEnrollmentRequest", e);
        }
        return json;
    }
    
    public void createEnrollmentRequests(final JSONObject message) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Entering createEnrollmentRequests in EnrollmentFacade", message.toString());
            final List<Long> userSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            final int platform = message.getJSONObject("msg_body").getInt("platform");
            this.createEnrollmentRequests(userSet, platform, APIUtil.getCustomerID(message));
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occured in creating Enrollment request", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void createEnrollmentRequests(final List<Long> userIds, final int platformType, final Long customerID) throws APIHTTPException {
        this.logger.log(Level.INFO, "Entering createEnrollmentRequests in EnrollmentFacade with userIds:{0},platformType:{1},customerId:{2}", new Object[] { userIds, platformType, customerID });
        if (MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerID)) {
            throw new APIHTTPException("COM00020", new Object[0]);
        }
        final List erids = new ArrayList();
        for (final Long userId : userIds) {
            this.logger.log(Level.INFO, "Enrollment request to be added for userId:{0}", new Object[] { userId });
            final HashMap userMap = ManagedUserHandler.getInstance().getManagedUserDetails(userId);
            final String email = userMap.get("EMAIL_ADDRESS");
            boolean addToExistingUser = true;
            if (userMap == null || userMap.size() == 0) {
                addToExistingUser = false;
            }
            final String sUserName = userMap.get("NAME");
            final String sDomainName = userMap.get("DOMAIN_NETBIOS_NAME");
            final String sOwnedBy = String.valueOf(2);
            final String splatform = String.valueOf(platformType);
            final Boolean byAdmin = false;
            final Boolean bKnoxLicenseDS = false;
            final Long groupID = null;
            final Properties properties = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(sDomainName, sUserName, groupID, email, sOwnedBy, customerID, addToExistingUser, splatform, false);
            ((Hashtable<String, Boolean>)properties).put("KNOX_LIC_DS", bKnoxLicenseDS);
            ((Hashtable<String, Boolean>)properties).put("byAdmin", byAdmin);
            final JSONObject resultJSON = this.addEnrollmentRequest(properties);
            if (resultJSON != null && resultJSON.has("erid")) {
                try {
                    erids.add(resultJSON.getLong("erid"));
                }
                catch (final JSONException e) {
                    this.logger.log(Level.SEVERE, "exception in createEnrollmentRequests", (Throwable)e);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
        }
        this.logger.log(Level.INFO, "APIActionERIDListener invoke with erids", erids.toString());
        APIActionsHandler.getInstance().invokeAPIActionERIDListener(erids, null, 1);
    }
    
    public void resendEnrollmentRequests(final JSONObject message) throws APIHTTPException {
        this.logger.log(Level.INFO, "Entering resendEnrollmentRequests in EnrollmentFacade", message.toString());
        try {
            List<Long> userSet = new ArrayList<Long>();
            final Long customerId = APIUtil.getCustomerID(message);
            userSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            ManagedUserFacade.getInstance().validateIfUserExists(userSet, customerId);
            final Long customerID = APIUtil.getCustomerID(message);
            this.resendEnrollmentRequests(userSet, customerID);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in resendEnrollmentrequests ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void resendEnrollmentRequests(final List userIds, final Long customerID) throws APIHTTPException {
        this.logger.log(Level.INFO, "Entering resendEnrollmentRequests in EnrollmentFacade with userIds:{0}customerId:{1}", new Object[] { userIds, customerID });
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)userIds.toArray(), 8).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)));
            final List erids = DBUtil.getColumnValuesAsList(MDMUtil.getPersistence().get(selectQuery).getRows("DeviceEnrollmentRequest"), "ENROLLMENT_REQUEST_ID");
            for (final Object reqId : erids) {
                final Properties properties = new Properties();
                ((Hashtable<String, Object>)properties).put("ENROLLMENT_REQUEST_ID", reqId);
                ((Hashtable<String, Long>)properties).put("USER_ID", MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
                ((Hashtable<String, Integer>)properties).put("AUTH_MODE", EnrollmentSettingsHandler.getInstance().getAuthMode((Long)reqId));
                ((Hashtable<String, Boolean>)properties).put("regenerateDeviceToken", true);
                MDMEnrollmentRequestHandler.getInstance().resendEnrollmentRequest(properties);
            }
            for (final Object user : userIds) {
                Integer profilePlatform = 2;
                if (DBUtil.getColumnValuesAsList(MDMUtil.getPersistence().get("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), user, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)profilePlatform, 0))).getRows("DeviceEnrollmentRequest"), "MANAGED_USER_ID").size() == 0) {
                    new EnrollmentFacade().createEnrollmentRequests(Arrays.asList((Long)user), profilePlatform, customerID);
                }
                profilePlatform = 1;
                if (DBUtil.getColumnValuesAsList(MDMUtil.getPersistence().get("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), user, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)profilePlatform, 0))).getRows("DeviceEnrollmentRequest"), "MANAGED_USER_ID").size() == 0) {
                    new EnrollmentFacade().createEnrollmentRequests(Arrays.asList((Long)user), profilePlatform, customerID);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in resendEnrollmentRequests", e);
        }
    }
    
    public JSONObject deprovisionDevice(final JSONObject requestJSON) throws Exception {
        JSONObject responseJson = new JSONObject();
        try {
            final Long managedDeviceId = APIUtil.getResourceID(requestJSON, "device_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            new DeviceFacade().validateIfDeviceExists(managedDeviceId, customerId);
            final JSONArray resourceIdArray = new JSONArray();
            resourceIdArray.put((Object)managedDeviceId);
            responseJson = this.deprovisionDevicesUtil(requestJSON, resourceIdArray);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            if (e instanceof SyMException) {
                this.logger.log(Level.SEVERE, "error in deprovision device", e);
                throw e;
            }
            this.logger.log(Level.SEVERE, "error in deprovision device", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJson;
    }
    
    public JSONObject deprovisionDevicesUtil(final JSONObject requestJSON, final JSONArray resourceIdArray) throws Exception {
        return this.deprovisionDevicesUtil(requestJSON, resourceIdArray, APIUtil.getCustomerID(requestJSON));
    }
    
    public JSONObject deprovisionDevicesUtil(final JSONObject requestJSON, final JSONArray resourceIdArray, final Long customerID) throws Exception {
        if (resourceIdArray.length() == 0) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        final int wipeReason = body.getInt("wipe_reason");
        if (wipeReason != 1 && wipeReason != 4 && wipeReason != 2 && wipeReason != 3) {
            throw new SyMException(13003, (Throwable)null);
        }
        final int wipeType = body.optInt("wipe_type");
        if ((wipeReason == 4 && wipeType == 0) || (wipeType != 0 && wipeType != 2 && wipeType != 1)) {
            throw new SyMException(13003, (Throwable)null);
        }
        final Long[] residarr = new Long[resourceIdArray.length()];
        for (int i = 0; i < resourceIdArray.length(); ++i) {
            residarr[i] = resourceIdArray.getLong(i);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)residarr, 8));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new SyMException(13001, (Throwable)null);
        }
        final DeprovisionRequest deprovisionRequest = new DeprovisionRequest(customerID, APIUtil.getUserID(requestJSON), wipeType, wipeReason, body.optString("other_reason", "--"), Arrays.asList(residarr));
        final JSONObject responseJSON = ManagedDeviceHandler.getInstance().deprovisionDevice(deprovisionRequest);
        responseJSON.remove("success");
        return responseJSON;
    }
    
    public JSONObject getWipeStatus(final JSONObject requestJSON) throws Exception {
        try {
            final Long managedDeviceId = APIUtil.getResourceID(requestJSON, "device_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            new DeviceFacade().validateIfDeviceExists(managedDeviceId, customerId);
            return ManagedDeviceHandler.getInstance().getDeprovisionWipeStatus(managedDeviceId);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in getting deprovision status", e);
            throw new APIHTTPException("WIP0002", new Object[0]);
        }
    }
    
    public JSONObject getDeviceIdsForUser(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONObject inputJSON = new JSONObject();
        final String email = APIUtil.getStringFilter(requestJSON, "email_id");
        String domain = APIUtil.getStringFilter(requestJSON, "domain_name");
        if (email == null) {
            throw new APIHTTPException("COM0005", new Object[] { "email_id" });
        }
        if (domain == null) {
            domain = "MDM";
        }
        inputJSON.put("EMAIL_ADDRESS", (Object)email);
        inputJSON.put("DOMAIN_NETBIOS_NAME", (Object)domain);
        inputJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(requestJSON));
        responseJSON.put("device_ids", (Object)MDMEnrollmentUtil.getInstance().getDeviceIDForUserId(inputJSON));
        return responseJSON;
    }
    
    public JSONObject getAgentSettings(final JSONObject jsonObject) throws Exception {
        final Long customerId = APIUtil.getCustomerID(jsonObject);
        jsonObject.put("CUSTOMER_ID", (Object)customerId);
        final Long platform = APIUtil.getResourceID(jsonObject, "memdm_app_setting_id");
        switch (platform.intValue()) {
            case 1: {
                return this.getIosAgentSettings(jsonObject);
            }
            case 2: {
                return this.getAndroidAgentSettings(jsonObject);
            }
            case 3: {
                return this.getWindowsAgentSettings(jsonObject);
            }
            case -1: {
                return this.getAllPlatformAgentSetting(jsonObject);
            }
            default: {
                throw new APIHTTPException("ENR001", new Object[0]);
            }
        }
    }
    
    public JSONObject getAllPlatformAgentSetting(final JSONObject jsonObject) throws Exception {
        final JSONObject iosAgentSettings = this.getIosAgentSettings(jsonObject);
        final JSONObject anroidAgentSettings = this.getAndroidAgentSettings(jsonObject);
        final JSONObject windowsAgentSettings = this.getWindowsAgentSettings(jsonObject);
        final JSONObject agentSettings = new JSONObject();
        agentSettings.put(String.valueOf("android"), (Object)anroidAgentSettings);
        agentSettings.put(String.valueOf("iOS"), (Object)iosAgentSettings);
        agentSettings.put(String.valueOf("windows"), (Object)windowsAgentSettings);
        return agentSettings;
    }
    
    public JSONObject getIosAgentSettings(final JSONObject jsonObject) throws Exception {
        final JSONObject iosAgentSetting = new JSONObject();
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        boolean isNativeAgentEnable = false;
        boolean validateCheckSum = false;
        final Row row = DBUtil.getRowFromDB("IOSAgentSettings", "CUSTOMER_ID", (Object)customerID);
        if (row != null) {
            isNativeAgentEnable = (boolean)row.get("IS_NATIVE_APP_ENABLE");
            validateCheckSum = (boolean)row.get("VALIDATE_CHECKSUM");
        }
        final JSONObject complianceRules = MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(customerID);
        if (complianceRules.length() != 0) {
            iosAgentSetting.put("CORPORATE_WIPE_ROOTED_DEVICES", complianceRules.getBoolean("CORPORATE_WIPE_ROOTED_DEVICES"));
        }
        else {
            iosAgentSetting.put("CORPORATE_WIPE_ROOTED_DEVICES", false);
        }
        iosAgentSetting.put("IS_NATIVE_APP_ENABLE", isNativeAgentEnable);
        iosAgentSetting.put("VALIDATE_CHECKSUM", validateCheckSum);
        return iosAgentSetting;
    }
    
    public JSONObject getAndroidAgentSettings(final JSONObject jsonObject) throws Exception {
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        final JSONObject androidAgentSetting = new JSONObject();
        final JSONObject manageMdmettingsJson = new MDMAgentSettingsHandler().getAndroidAgentSetting(customerID);
        final boolean allowadmindisbale = manageMdmettingsJson.getBoolean("ALLOW_ADMIN_DISABLE");
        manageMdmettingsJson.put("ALLOW_ADMIN_DISABLE", !allowadmindisbale);
        final boolean hideserverdetails = manageMdmettingsJson.getBoolean("HIDE_SERVER_INFO");
        manageMdmettingsJson.put("HIDE_SERVER_DETAILS", hideserverdetails);
        manageMdmettingsJson.remove("HIDE_SERVER_INFO");
        final String[] removeList = MDMCommonConstants.ME_MDM_APP_SETTINGS_REMOVE_LIST;
        for (int i = 0; i < removeList.length; ++i) {
            manageMdmettingsJson.remove(removeList[i]);
        }
        androidAgentSetting.put("MANAGE_APP_SETTINGS", (Object)manageMdmettingsJson);
        final MDMAgentSettingsHandler settingsHandler = new MDMAgentSettingsHandler();
        final JSONObject androidNotificationData = settingsHandler.getAndroidNotificationSetting(customerID);
        final JSONObject rebrandingSetting = settingsHandler.getAgentRebrandingSetting(customerID);
        final JSONObject complianceRules = MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(customerID);
        if (complianceRules.length() != 0) {
            androidAgentSetting.put("CORPORATE_WIPE_ROOTED_DEVICES", complianceRules.getBoolean("CORPORATE_WIPE_ROOTED_DEVICES"));
            androidAgentSetting.put("WIPE_INTEGRITY_FAILED_DEVICES", complianceRules.getBoolean("WIPE_INTEGRITY_FAILED_DEVICES"));
            androidAgentSetting.put("WIPE_CTS_FAILED_DEVICES", complianceRules.getBoolean("WIPE_CTS_FAILED_DEVICES"));
        }
        else {
            androidAgentSetting.put("CORPORATE_WIPE_ROOTED_DEVICES", false);
            androidAgentSetting.put("WIPE_INTEGRITY_FAILED_DEVICES", false);
            androidAgentSetting.put("WIPE_CTS_FAILED_DEVICES", false);
        }
        if (androidNotificationData.opt("ANDROID_NOTIFICATION_SERVICE") == null) {
            androidAgentSetting.put("ANDROID_NOTIFICATION_SERVICE", 1);
        }
        else {
            androidAgentSetting.put("ANDROID_NOTIFICATION_SERVICE", androidNotificationData.get("ANDROID_NOTIFICATION_SERVICE"));
        }
        if (androidNotificationData.opt("ANDROID_AGENT_DOWNLOAD_MODE") == null) {
            androidAgentSetting.put("ANDROID_AGENT_DOWNLOAD_MODE", 3);
        }
        else {
            androidAgentSetting.put("ANDROID_AGENT_DOWNLOAD_MODE", androidNotificationData.get("ANDROID_AGENT_DOWNLOAD_MODE"));
        }
        final String folderLocation = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "agent";
        final String fileLocation = folderLocation + File.separator + "MDMAndroidAgent.apk";
        final File file = new File(fileLocation);
        if (file.exists()) {
            final boolean checkSumSuccess = MDMCheckSumProvider.getInstance().validateFileCheckSum(fileLocation, MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagentchecksum"), Boolean.TRUE);
            if (checkSumSuccess) {
                androidAgentSetting.put("IS_ANDROID_AGENT_PRESENT", true);
            }
            else {
                Logger.getLogger(EnrollmentFacade.class.getName()).log(Level.INFO, "Looks like the android agent present is corrupted hence deleting it...");
                file.delete();
                androidAgentSetting.put("IS_ANDROID_AGENT_PRESENT", false);
            }
        }
        else {
            androidAgentSetting.put("IS_ANDROID_AGENT_PRESENT", false);
        }
        final JSONObject rebrandingJson = new JSONObject();
        final String icon = rebrandingSetting.optString("MDM_APP_ICON_FILE_NAME", (String)null);
        final String splashImg = rebrandingSetting.optString("MDM_APP_SPLASH_IMAGE_FILE_NAME", (String)null);
        final String path = rebrandingSetting.optString("REBRANDING_PATH", (String)null);
        if (icon != null && !icon.equals("")) {
            rebrandingJson.put("IS_ICON_CONFIGURED", true);
            rebrandingJson.put("ICON_PATH", (Object)settingsHandler.getAndroidIconPath(path, icon));
        }
        else {
            rebrandingJson.put("IS_ICON_CONFIGURED", false);
        }
        if (splashImg != null && !splashImg.equals("")) {
            rebrandingJson.put("IS_SPLASH_IMAGE_CONFIURED", true);
            rebrandingJson.put("SPLASH_IMAGE_PATH", (Object)settingsHandler.getAndroidSplashScreenurl(path, splashImg));
        }
        else {
            rebrandingJson.put("IS_SPLASH_IMAGE_CONFIURED", false);
        }
        rebrandingJson.put("MDM_APP_NAME", (Object)rebrandingSetting.optString("MDM_APP_NAME", "ME MDM App"));
        androidAgentSetting.put("REBRANDING_SETTINGS", (Object)rebrandingJson);
        return androidAgentSetting;
    }
    
    public JSONObject getWindowsAgentSettings(final JSONObject jsonObject) throws Exception {
        final JSONObject windowsAgentSetting = new MDMAgentSettingsHandler().getWindowsSettings(jsonObject.optLong("CUSTOMER_ID"));
        if (windowsAgentSetting.opt("USER_UNENROLL") == null) {
            windowsAgentSetting.put("USER_UNENROLL", true);
        }
        if (windowsAgentSetting.opt("WINDOWS_PHONE_NOTIFICATION_SERVICE") == null) {
            windowsAgentSetting.put("WINDOWS_PHONE_NOTIFICATION_SERVICE", 1);
        }
        return windowsAgentSetting;
    }
    
    public JSONObject saveAgentSettings(final JSONObject jsonObject) throws Exception {
        final JSONObject requestJSON = jsonObject.getJSONObject("msg_body");
        List statusInfo = new ArrayList();
        final Long platform = APIUtil.getResourceID(jsonObject, "memdm_app_setting_id");
        Long customerId = APIUtil.getCustomerID(jsonObject);
        if (customerId == null || customerId == -1L) {
            customerId = CustomerInfoUtil.getInstance().getCustomerId();
        }
        requestJSON.put("CUSTOMER_ID", (Object)customerId);
        requestJSON.put("UPDATED_BY", (Object)APIUtil.getUserID(jsonObject));
        final MDMAgentSettingsHandler settingHandler = new MDMAgentSettingsHandler();
        final Iterator iter = requestJSON.keys();
        final JSONObject inputJSON = new JSONObject();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (key.equals("recovery_password_encrypted")) {
                inputJSON.put(key.toUpperCase(), requestJSON.get("recovery_password"));
            }
            else {
                inputJSON.put(key.toUpperCase(), requestJSON.get(key));
            }
        }
        switch (platform.intValue()) {
            case 1: {
                settingHandler.processiOSSettings(inputJSON);
                settingHandler.toggleMDMMacAgentAutoDistributionStatus(customerId, inputJSON.optBoolean("IS_NATIVE_APP_ENABLE", false));
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2062, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), "mdm.actionlog.ios_agent_settings", "", customerId);
                return this.getIosAgentSettings(inputJSON);
            }
            case 2: {
                final String[] unSupportedList = MDMCommonConstants.ME_MDM_APP_SETTINGS_REMOVE_LIST;
                for (int i = 0; i < unSupportedList.length; ++i) {
                    inputJSON.put(unSupportedList[i], this.getDefaultValueForDb(unSupportedList[i]));
                }
                if (inputJSON.getInt("ANDROID_AGENT_DOWNLOAD_MODE") == 2 || inputJSON.getInt("ANDROID_AGENT_DOWNLOAD_MODE") == 4) {
                    this.downloadAndroidAgent();
                }
                if (inputJSON.has("IS_ICON_CHANGE") && inputJSON.getInt("IS_ICON_CHANGE") == 1) {
                    if (inputJSON.has("ICON_FILE_ID")) {
                        String tempIconFilePath = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", Long.valueOf(inputJSON.get("ICON_FILE_ID").toString()))).get("file_path"));
                        final int lastIndex = tempIconFilePath.lastIndexOf(File.separator);
                        tempIconFilePath = tempIconFilePath.substring(0, lastIndex) + tempIconFilePath.substring(lastIndex);
                        inputJSON.put("MDM_APP_ICON", (Object)tempIconFilePath);
                    }
                    inputJSON.put("IS_ICON_CHANGE", 1);
                }
                else {
                    inputJSON.put("IS_ICON_CHANGE", 0);
                }
                if (inputJSON.has("IS_SPLASH_IMAGE_CHANGE") && inputJSON.getInt("IS_SPLASH_IMAGE_CHANGE") == 1) {
                    if (inputJSON.has("SPLASH_ICON_FILE_ID")) {
                        String tempSplashIconFilePath = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", Long.valueOf(inputJSON.get("SPLASH_ICON_FILE_ID").toString()))).get("file_path"));
                        final int lastIndex = tempSplashIconFilePath.lastIndexOf(File.separator);
                        tempSplashIconFilePath = tempSplashIconFilePath.substring(0, lastIndex) + tempSplashIconFilePath.substring(lastIndex);
                        inputJSON.put("MDM_APP_SPLASH_IMAGE", (Object)tempSplashIconFilePath);
                    }
                    inputJSON.put("IS_SPLASH_IMAGE_CHANGE", 1);
                }
                else {
                    inputJSON.put("IS_SPLASH_IMAGE_CHANGE", 0);
                }
                statusInfo = settingHandler.processAndroidSettings(inputJSON);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2062, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), "mdm.actionlog.android_agent_settings", "", customerId);
                return this.getAndroidAgentSettings(inputJSON);
            }
            case 3: {
                settingHandler.processWindowsSettings(inputJSON);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2062, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), "mdm.actionlog.windows_memdm_settings", "", customerId);
                return this.getWindowsAgentSettings(inputJSON);
            }
            default: {
                throw new APIHTTPException("ENR001", new Object[0]);
            }
        }
    }
    
    public int getDefaultValueForDb(final String unSupportedKey) {
        switch (unSupportedKey) {
            case "GRACE_TIME": {
                return 5;
            }
            case "USER_REM_TIME": {
                return 30;
            }
            case "USER_REM_COUNT": {
                return 5;
            }
            default: {
                return -1;
            }
        }
    }
    
    public JSONObject getEnrollmentSettings(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject inputJson = new JSONObject();
            inputJson.put("MsgRequestType", (Object)"getEnrollSettings");
            inputJson.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(requestJSON));
            inputJson.put("isApi", true);
            final JSONObject responseJson = EnrollmentSettingsHandler.getInstance().processMessage(inputJson);
            if (responseJson.optString("Status").equalsIgnoreCase("Error")) {
                final String errorMsg = I18N.getMsg(String.valueOf(responseJson.get("ErrorKey")), new Object[0]);
                throw new APIHTTPException("ENR0101", new Object[] { errorMsg });
            }
            final JSONObject authHandlingJson = responseJson.getJSONObject("IS_AUTHENTICATION_HANDLING_AVAILABLE");
            final Boolean status = authHandlingJson.getBoolean("status");
            responseJson.put("IS_AUTHENTICATION_HANDLING_AVAILABLE", (Object)status);
            if (authHandlingJson.opt("isADIntegrated") != null) {
                responseJson.put("is_ad_integrated", authHandlingJson.get("isADIntegrated"));
            }
            if (authHandlingJson.opt("directoryAuthentication") != null) {
                responseJson.put("directory_authentication", authHandlingJson.get("directoryAuthentication"));
            }
            if (authHandlingJson.opt("selectedAD") != null) {
                responseJson.put("selected_ad", authHandlingJson.get("selectedAD"));
            }
            if (authHandlingJson.opt("availableAD") != null) {
                responseJson.put("available_ad", authHandlingJson.get("availableAD"));
            }
            return responseJson;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getEnrollmentSettings ", exp);
            if (exp instanceof APIHTTPException) {
                throw (APIHTTPException)exp;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject saveEnrollmentSettings(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject inputjson = new JSONObject();
            inputjson.put("MsgRequestType", (Object)"SaveEnrollSettings");
            final Long customerID = APIUtil.getCustomerID(requestJSON);
            inputjson.put("CUSTOMER_ID", (Object)customerID);
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            final JSONObject requestJson = new JSONObject();
            final Iterator iter = body.keys();
            while (iter.hasNext()) {
                final String key = iter.next();
                requestJson.put(key.toUpperCase(), body.get(key));
            }
            if (requestJson.opt("AUTH_MODE") == null || (requestJson.opt("EMAIL_UNMANAGED_ALERTS") == null && requestJson.optBoolean("NOTIFY_DEVICE_UNMANAGED", true)) || requestJson.opt("NOTIFY_DEVICE_UNMANAGED") == null) {
                throw new APIHTTPException("ENR0102", new Object[0]);
            }
            if (requestJson.getBoolean("NOTIFY_DEVICE_UNMANAGED") && !MDMEnrollmentUtil.getInstance().isMailServerConfigured()) {
                throw new APIHTTPException("ENR0106", new Object[0]);
            }
            final JSONObject isAuthenticationHandlingAvailable = EnrollmentSettingsHandler.getInstance().isAuthenticationHandlingAvailable(customerID);
            final Boolean isADAuthenticationApplicable = EnrollmentSettingsHandler.getInstance().isADAuthenticationApplicable();
            Boolean isADConfigured = false;
            final List domainList = MDMEnrollmentUtil.getInstance().getDomainNamesWithoutGSuite(customerID);
            if (!domainList.isEmpty()) {
                isADConfigured = true;
            }
            if ((requestJson.getInt("AUTH_MODE") == 2 || requestJson.getInt("AUTH_MODE") == 3) && (!isADAuthenticationApplicable || !isADConfigured) && (isAuthenticationHandlingAvailable == null || !isAuthenticationHandlingAvailable.getBoolean("status"))) {
                throw new APIHTTPException("ENR0105", new Object[0]);
            }
            inputjson.put("MsgRequest", (Object)requestJson);
            final JSONObject responseJson = EnrollmentSettingsHandler.getInstance().processMessage(inputjson);
            if (String.valueOf(responseJson.get("Status")).equalsIgnoreCase("Error")) {
                final String errorMsg = I18N.getMsg(String.valueOf(responseJson.get("ErrorKey")), new Object[0]);
                throw new APIHTTPException("COM0004", new Object[] { errorMsg });
            }
            return responseJson;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in saveEnrollmentSettings ", exp);
            if (exp instanceof APIHTTPException) {
                throw (APIHTTPException)exp;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getSelfEnrollmentSettings(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject inputjson = new JSONObject();
            inputjson.put("MsgRequestType", (Object)"GetSelfEnrollSettings");
            inputjson.put("isApi", true);
            final Long customerID = APIUtil.getCustomerID(requestJSON);
            inputjson.put("CUSTOMER_ID", (Object)customerID);
            final JSONObject responseJson = EnrollmentSettingsHandler.getInstance().processMessage(inputjson);
            if (responseJson.optString("Status").equalsIgnoreCase("Error")) {
                final String errorMsg = I18N.getMsg(String.valueOf(responseJson.get("ErrorKey")), new Object[0]);
                throw new APIHTTPException("ENR0103", new Object[] { errorMsg });
            }
            final String serverUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL();
            responseJson.put("self_enrollment_url", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getSelfEnrollURL(serverUrl));
            if (responseJson.has("OWNED_BY_OPTION")) {
                responseJson.remove("OWNED_BY_OPTION");
            }
            final JSONObject selfEnrollGroups = responseJson.getJSONObject("SELF_ENROLLMENT_GROUPS");
            final Iterator iterator = selfEnrollGroups.keys();
            final JSONObject updatedSelfEnrollGroups = new JSONObject();
            while (iterator.hasNext()) {
                final JSONObject updatedGroupsJson = new JSONObject();
                final String key = String.valueOf(iterator.next());
                final String groupId = String.valueOf(selfEnrollGroups.get(key));
                final String name = MDMGroupHandler.getInstance().getGroupName(Long.parseLong(groupId));
                updatedGroupsJson.put("group_id", (Object)groupId);
                updatedGroupsJson.put("group_name", (Object)name);
                updatedSelfEnrollGroups.put(this.getAutoAssignKey(key), (Object)updatedGroupsJson);
            }
            responseJson.remove("SELF_ENROLLMENT_GROUPS");
            responseJson.put("AUTO_ASSIGN_GROUPS", (Object)updatedSelfEnrollGroups);
            final JSONObject authHandlingJson = responseJson.getJSONObject("IS_AUTHENTICATION_HANDLING_AVAILABLE");
            final Boolean status = authHandlingJson.getBoolean("status");
            responseJson.put("IS_AUTHENTICATION_HANDLING_AVAILABLE", (Object)status);
            if (authHandlingJson.opt("is_ad_integrated") != null) {
                responseJson.put("is_ad_integrated", authHandlingJson.get("is_ad_integrated"));
            }
            if (authHandlingJson.opt("is_directory_authentication") != null) {
                responseJson.put("is_directory_authentication", authHandlingJson.get("is_directory_authentication"));
            }
            if (authHandlingJson.opt("available_ad") != null) {
                responseJson.put("available_ad", authHandlingJson.get("available_ad"));
            }
            if (authHandlingJson.opt("selected_ad") != null) {
                responseJson.put("selected_ad", authHandlingJson.get("selected_ad"));
            }
            if (responseJson.has("IS_APPROVER_HANDLING_AVAILABLE")) {
                responseJson.remove("IS_APPROVER_HANDLING_AVAILABLE");
            }
            final Iterator<String> authHandlingJsonKeys = authHandlingJson.keys();
            while (authHandlingJsonKeys.hasNext()) {
                final String key2 = authHandlingJsonKeys.next();
                if (responseJson.opt(key2) == null) {
                    responseJson.put(key2, authHandlingJson.get(key2));
                }
            }
            int sendNotifMail = 0;
            final org.json.simple.JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
            if (exchangeServerDetails != null && exchangeServerDetails.containsKey((Object)"SEND_NOTIF_MAIL") && exchangeServerDetails.get((Object)"SEND_NOTIF_MAIL") != null) {
                sendNotifMail = Integer.valueOf(String.valueOf(exchangeServerDetails.get((Object)"SEND_NOTIF_MAIL")));
            }
            if (sendNotifMail != 0) {
                responseJson.put("is_eas_policy_configured", true);
            }
            else {
                responseJson.put("is_eas_policy_configured", false);
            }
            final String deviceLimitPerUser = CustomerParamsHandler.getInstance().getParameterValue("selfEnrollDeviceLimit", (long)customerID);
            if (deviceLimitPerUser != null) {
                responseJson.put("device_limit_per_user", Integer.parseInt(deviceLimitPerUser));
            }
            else {
                responseJson.put("device_limit_per_user", -1);
            }
            return responseJson;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getSelfEnrollmentSettings ", exp);
            if (exp instanceof APIHTTPException) {
                throw (APIHTTPException)exp;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject saveSelfEnrollmentSettings(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject inputjson = new JSONObject();
            inputjson.put("MsgRequestType", (Object)"SaveSelfEnrollSettings");
            inputjson.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(requestJSON));
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            final Iterator iter = body.keys();
            final JSONObject requestJson = new JSONObject();
            while (iter.hasNext()) {
                final String key = iter.next();
                if (key.equalsIgnoreCase("AUTO_ASSIGN_GROUPS")) {
                    final JSONObject jsonObject = body.getJSONObject(key);
                    requestJson.put("SELF_ENROLLMENT_GROUPS", (Object)this.formatSelfEnrollmentGroupsJson(jsonObject));
                }
                else if (!key.equalsIgnoreCase("APPROVAL_CRITERIA")) {
                    requestJson.put(key.toUpperCase(), body.get(key));
                }
                else {
                    requestJson.put("ApprovalCriteria", body.get(key));
                }
            }
            if (requestJson.has("ApprovalCriteria")) {
                final JSONObject approvalcri = requestJson.getJSONObject("ApprovalCriteria");
                final JSONObject approvalCriteria = new JSONObject();
                final Iterator approvalcriiter = approvalcri.keys();
                while (approvalcriiter.hasNext()) {
                    final String approvalcrikey = approvalcriiter.next();
                    if (!approvalcrikey.equalsIgnoreCase("resource_list")) {
                        approvalCriteria.put(approvalcrikey.toUpperCase(), approvalcri.get(approvalcrikey));
                    }
                    else {
                        final JSONArray resourceList = approvalcri.getJSONArray("resource_list");
                        final JSONArray resourcelistUpdated = new JSONArray();
                        for (int i = 0; i < resourceList.length(); ++i) {
                            final JSONObject resourceJSON = resourceList.getJSONObject(i);
                            final JSONObject resourceJSONUpdated = new JSONObject();
                            final Iterator resourceiter = resourceJSON.keys();
                            while (resourceiter.hasNext()) {
                                final String resourceKey = resourceiter.next();
                                resourceJSONUpdated.put(resourceKey.toUpperCase(), resourceJSON.get(resourceKey));
                            }
                            resourcelistUpdated.put((Object)resourceJSONUpdated);
                        }
                        approvalCriteria.put(approvalcrikey.toUpperCase(), (Object)resourcelistUpdated);
                    }
                }
                requestJson.put("ApprovalCriteria", (Object)approvalCriteria);
            }
            if ((requestJson.opt("EMAIL_SELF_ENROLLMENT_ALERTS") == null && requestJson.optBoolean("NOTIFY_SELF_ENROLLMENT")) || requestJson.opt("NOTIFY_SELF_ENROLLMENT") == null || requestJson.opt("ENABLE_SELF_ENROLLMENT") == null || (requestJson.optInt("APPROVAL_MODE") == 2 && requestJson.optJSONObject("ApprovalCriteria") == null)) {
                throw new APIHTTPException("ENR0104", new Object[0]);
            }
            requestJson.put("OWNED_BY_OPTION", 0);
            requestJson.put("UPDATED_BY", (Object)APIUtil.getUserID(requestJSON));
            inputjson.put("MsgRequest", (Object)requestJson);
            final JSONObject responseJson = EnrollmentSettingsHandler.getInstance().processMessage(inputjson);
            if (requestJson.opt("DEVICE_LIMIT_PER_USER") != null) {
                final int deviceLimitPerUser = requestJson.getInt("DEVICE_LIMIT_PER_USER");
                if (deviceLimitPerUser == -1) {
                    this.removeSelfEnrollLimit(APIUtil.getCustomerID(requestJSON));
                }
                else {
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("selfEnrollDeviceLimit", String.valueOf(deviceLimitPerUser), (long)APIUtil.getCustomerID(requestJSON));
                    this.enableApprovalMode();
                }
            }
            if (!String.valueOf(responseJson.get("Status")).equalsIgnoreCase("Error")) {
                return responseJson;
            }
            if (responseJson.opt("ErrorKey") != null) {
                final String errorMsg = I18N.getMsg(String.valueOf(responseJson.get("ErrorKey")), new Object[0]);
                throw new APIHTTPException("ENR0104", new Object[] { errorMsg });
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final APIHTTPException exp) {
            this.logger.log(Level.SEVERE, "Exception in saveSelfEnrollmentSettings ", exp);
            throw exp;
        }
        catch (final Exception exp2) {
            this.logger.log(Level.SEVERE, "Exception in saveSelfEnrollmentSettings ", exp2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void enableApprovalMode() throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("SelfEnrollmentSettings");
        updateQuery.setUpdateColumn("APPROVAL_MODE", (Object)2);
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    public JSONObject getSelectedADGroupsListForSelfEnrollment(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Map requestMap = new HashMap();
            requestMap.put("treeId", String.valueOf(2002));
            requestMap.put("actionToCall", "getChildTreeNodes");
            requestMap.put("resourceJSONArr", "[]");
            requestMap.put("treeViewID", 1);
            requestMap.put("cid", APIUtil.getCustomerID(requestJSON));
            final TreeNodeDataHandler dataHandler = TreeNodeDataHandler.getInstance(2002);
            final JSONArray returnArray = this.formatADGroupsList(dataHandler.getTreeJSONObject(requestMap));
            final JSONObject returnJSON = new JSONObject();
            returnJSON.put("groups_list", (Object)returnArray);
            return returnJSON;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception while getting selected ad groups list!! ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getADGroupsListForSelfEnrollment(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Map requestMap = new HashMap();
            requestMap.put("treeId", String.valueOf(2001));
            requestMap.put("actionToCall", "getChildTreeNodes");
            requestMap.put("resourceJSONArr", "[]");
            requestMap.put("treeViewID", 1);
            requestMap.put("cid", APIUtil.getCustomerID(requestJSON));
            final TreeNodeDataHandler dataHandler = TreeNodeDataHandler.getInstance(2001);
            final JSONArray returnArray = this.formatADGroupsList(dataHandler.getTreeJSONObject(requestMap));
            final JSONObject returnJSON = new JSONObject();
            returnJSON.put("groups_list", (Object)returnArray);
            return returnJSON;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception while getting ad groups list!! ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getEnrollmentSteps(final JSONObject requestJSON) throws Exception {
        final Long erid = APIUtil.getResourceID(requestJSON, "enrollment_request_id");
        this.validateEridInput(erid, APIUtil.getCustomerID(requestJSON));
        final JSONObject jsonObject = MDMEnrollmentRequestHandler.getInstance().getEnrollmentDetails(erid);
        final int platformType = jsonObject.getInt("platform_type");
        if (platformType == 2) {
            jsonObject.remove("play_store_url");
        }
        final String email = String.valueOf(jsonObject.get("email_address"));
        jsonObject.put("email_address", (Object)URLDecoder.decode(email, "UTF-8"));
        return jsonObject;
    }
    
    public void resendEnrollmentRequest(final JSONObject requestJSON) throws Exception {
        final Long erid = APIUtil.getResourceID(requestJSON, "enrollment_request_id");
        this.validateInviteEridInput(erid, APIUtil.getCustomerID(requestJSON));
        final JSONObject body = requestJSON.optJSONObject("msg_body");
        boolean sendSms = false;
        boolean sendEmail = false;
        if (body != null) {
            sendSms = body.optBoolean("send_sms", false);
            sendEmail = body.optBoolean("send_email", false);
        }
        this.resendEnrollmentRequestUtil(new Long[] { erid }, sendSms, sendEmail, APIUtil.getCustomerID(requestJSON));
    }
    
    public void resendMultipleEnrollmentRequest(final JSONObject requestJSON) throws Exception {
        final JSONObject body = requestJSON.optJSONObject("msg_body");
        final JSONArray inputArray = body.getJSONArray("enrollment_request_ids");
        final Long[] reqId = new Long[inputArray.length()];
        for (int i = 0; i < inputArray.length(); ++i) {
            this.validateInviteEridInput(Long.parseLong(String.valueOf(inputArray.get(i))), APIUtil.getCustomerID(requestJSON));
            reqId[i] = Long.parseLong(String.valueOf(inputArray.get(i)));
        }
        boolean sendSms = false;
        boolean sendEmail = false;
        if (body != null) {
            sendSms = body.optBoolean("send_sms", false);
            sendEmail = body.optBoolean("send_email", false);
        }
        this.resendEnrollmentRequestUtil(reqId, sendSms, sendEmail, APIUtil.getCustomerID(requestJSON));
    }
    
    public void resendEnrollmentRequestUtil(final Long[] reqIdArray, final boolean sendSMS, boolean sendEmail, final Long customerID) throws Exception {
        for (int i = 0; i < reqIdArray.length; ++i) {
            final Long reqId = reqIdArray[i];
            final Boolean byAdmin = (Boolean)DBUtil.getValueFromDB("InvitationEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)reqId, "IS_INVITED_BY_ADMIN");
            final HashMap map = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(reqId);
            final Long UserId = map.get("MANAGED_USER_ID");
            final String sEmailID = map.get("EMAIL_ADDRESS");
            final Row userRow = DBUtil.getRowFromDB("Resource", "RESOURCE_ID", (Object)UserId);
            final String userName = (String)userRow.get("NAME");
            final String domain = (String)userRow.get("DOMAIN_NETBIOS_NAME");
            if (sEmailID != null && !sEmailID.equals("")) {
                final Properties properties = new Properties();
                ((Hashtable<String, Long>)properties).put("ENROLLMENT_REQUEST_ID", reqId);
                ((Hashtable<String, Long>)properties).put("USER_ID", MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
                ((Hashtable<String, Integer>)properties).put("AUTH_MODE", EnrollmentSettingsHandler.getInstance().getAuthMode(reqId));
                ((Hashtable<String, Boolean>)properties).put("regenerateDeviceToken", true);
                if (!sendEmail && !sendSMS) {
                    sendEmail = true;
                }
                ((Hashtable<String, Boolean>)properties).put("sendEmail", sendEmail);
                ((Hashtable<String, Boolean>)properties).put("sendSMS", sendSMS);
                MDMEnrollmentRequestHandler.getInstance().resendEnrollmentRequest(properties);
                final EREvent erEvent = new EREvent(customerID, null, sEmailID, reqId.toString());
                EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 3);
                String sEventLogRemarks = "dc.mdm.actionlog.enrollment.mail_resent";
                if (byAdmin) {
                    sEventLogRemarks = "dc.mdm.actionlog.enrollment.otp_regenerated";
                }
                final String currentlyLoggedOnUserName = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
                final Object remarksArgs = sEmailID + "@@@" + userName;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, currentlyLoggedOnUserName, sEventLogRemarks, remarksArgs, customerID);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"mail-delivery-success");
                logJSON.put((Object)"ENROLLMENT_REQUEST_ID", (Object)erEvent.enrollmentRequestId);
                MDMOneLineLogger.log(Level.INFO, "SENT_ENROLLMENT_REQUEST", logJSON);
                EnrollmentNotificationHandler.getInstance().removeNotification(reqId);
            }
        }
    }
    
    public void validateEridsInput(final ArrayList<Long> eridList) throws Exception {
        if (eridList.contains(-1L)) {
            throw new APIHTTPException("ENR00103", new Object[0]);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "AUTH_MODE"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "IS_SELF_ENROLLMENT"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)eridList.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("ENR00103", new Object[0]);
        }
    }
    
    public void validateInviteEridInput(final Long erid, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "AUTH_MODE"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "IS_SELF_ENROLLMENT"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        final Criteria processedStatusCri = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0);
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0).and(new Criteria(Column.getColumn("InvitationEnrollmentRequest", "IS_INVITED_BY_ADMIN"), (Object)true, 0).or(processedStatusCri)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            return;
        }
        if ((int)dataObject.getFirstRow("DeviceEnrollmentRequest").get("REQUEST_STATUS") == 3) {
            final String name = dataObject.getFirstRow("Resource").get("NAME").toString();
            throw new APIHTTPException("ENR00115", new Object[] { name });
        }
        throw new APIHTTPException("ENR00102", new Object[0]);
    }
    
    public void validateEridInput(final Long erid, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "AUTH_MODE"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "IS_SELF_ENROLLMENT"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("ENR00101", new Object[0]);
        }
        final Row row = dataObject.getFirstRow("DeviceEnrollmentRequest");
        final int authmode = (int)row.get("AUTH_MODE");
        if (authmode == 4) {
            throw new APIHTTPException("ENR00102", new Object[0]);
        }
    }
    
    public void removeDevice(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long managedDeviceId = APIUtil.getResourceID(requestJSON, "device_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            new DeviceFacade().validateIfDeviceExists(managedDeviceId, customerId);
            final Long userID = APIUtil.getUserID(requestJSON);
            final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromManagedDeviceID(managedDeviceId);
            final boolean override = APIUtil.getBooleanFilter(requestJSON, "force_override", false);
            if (override && erid == null && managedDeviceId > 0L) {
                MDMUtil.getPersistenceLite().delete(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                MDMUtil.getPersistenceLite().delete(new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                MDMUtil.getPersistenceLite().delete(new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                MDMUtil.getPersistenceLite().delete(new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                MDMUtil.getPersistenceLite().delete(new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                MDMUtil.getPersistenceLite().delete(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                return;
            }
            this.removeDevicesUtil(new Long[] { erid }, APIUtil.getCustomerID(requestJSON), DMUserHandler.getUserNameFromUserID(userID), override);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removing devices  ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void removeDevicesUtil(final Long[] erIDs, final Long customerId, final String userName, final boolean override) throws Exception {
        try {
            if (erIDs.length == 0) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            final ArrayList eridList = new ArrayList((Collection<? extends E>)Arrays.asList(erIDs));
            final ArrayList<Long> resourceIDs = (ArrayList<Long>)ManagedDeviceHandler.getInstance().getManagedDeviceIdFromErids(eridList, "RESOURCE_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8));
            final List deviceIdList = new ArrayList();
            deviceIdList.addAll(resourceIDs);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final List notDeprovisionedDevices = new ArrayList();
            if (!dataObject.isEmpty()) {
                final Iterator itr = dataObject.getRows("ManagedDevice");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final Long devId = (Long)row.get("RESOURCE_ID");
                    final int managedStatus = (int)row.get("MANAGED_STATUS");
                    if (managedStatus == 2) {
                        notDeprovisionedDevices.add(devId);
                    }
                    deviceIdList.remove(row.get("RESOURCE_ID"));
                }
            }
            if (!notDeprovisionedDevices.isEmpty() && !override) {
                throw new APIHTTPException("WIP0004", new Object[] { notDeprovisionedDevices.toString() });
            }
            if (!deviceIdList.isEmpty() && !override) {
                throw new APIHTTPException("WIP0002", new Object[] { deviceIdList.toString() });
            }
            String sDeviceIDs = String.valueOf(erIDs[0]);
            for (int i = 1; i < erIDs.length; ++i) {
                sDeviceIDs = sDeviceIDs + "," + String.valueOf(erIDs[i]);
            }
            final Boolean successfullyRemoved = MDMEnrollmentUtil.getInstance().removeDevice(sDeviceIDs, userName, customerId);
            if (!successfullyRemoved) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in removeDeviceUtil ", e);
            throw e;
        }
    }
    
    public JSONObject createEnrollmentRequest(final JSONObject requestJSON) throws Exception {
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        final String sUserName = String.valueOf(body.get("user_name"));
        String sDomainName = body.optString("domain_name", "MDM");
        final String sEmailID = String.valueOf(body.get("email_id"));
        final String sOwnedBy = String.valueOf(body.getInt("owned_by"));
        final String splatform = String.valueOf(body.getInt("platform_type"));
        final String sGroupID = body.optString("group_id");
        final Boolean byAdmin = body.optBoolean("by_admin", false);
        final Boolean isAzure = body.optBoolean("is_azure", false);
        final boolean sendSMS = body.optBoolean("send_sms", false);
        final boolean sendEmail = body.optBoolean("send_email", false);
        final String phoneNumber = body.optString("phone_number");
        final boolean knocLicenseDs = body.optBoolean("knox_container", false);
        Long groupID = null;
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        MICSFeatureTrackerUtil.inviteEnrollmentStart(splatform);
        if (!MDMUtil.getInstance().isValidEmail(sEmailID)) {
            throw new APIHTTPException("COM0005", new Object[] { I18N.getMsg("dc.mdm.device_mgmt.email", new Object[0]) });
        }
        if (MDMCommonConstants.APPLE_PLATFORM.contains(Integer.parseInt(splatform))) {
            this.checkForValidApns();
        }
        if (sendEmail && !ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
            throw new APIHTTPException("ENR0106", new Object[0]);
        }
        if (sendSMS) {
            if (!MDMApiFactoryProvider.getSMSAPI().isSMSSettingsConfigured()) {
                throw new APIHTTPException("ENR0107", new Object[0]);
            }
            if (MDMApiFactoryProvider.getSMSAPI().getRemainingCredits() <= 0) {
                throw new APIHTTPException("ENR0108", new Object[0]);
            }
        }
        if (sDomainName == null) {
            sDomainName = "MDM";
        }
        if (sGroupID != null && !sGroupID.trim().equalsIgnoreCase("")) {
            groupID = Long.valueOf(sGroupID);
        }
        final Properties properties = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(sDomainName, sUserName, groupID, sEmailID, sOwnedBy, customerID, true, splatform, false);
        ((Hashtable<String, Boolean>)properties).put("KNOX_LIC_DS", knocLicenseDs);
        ((Hashtable<String, Boolean>)properties).put("isAzure", isAzure);
        ((Hashtable<String, Boolean>)properties).put("byAdmin", byAdmin);
        MDMEnrollmentUtil.getInstance().setEnrollmentInvitationProperties(properties, phoneNumber, String.valueOf(sendEmail), String.valueOf(sendSMS));
        JSONObject json = new MDMEnrollAction().addEnrollmentRequest(properties);
        json = JSONUtil.getInstance().convertLongToString(json);
        if (json.has("ENROLL_STATUS")) {
            json.remove("ENROLL_STATUS");
        }
        return json;
    }
    
    private void checkForValidApns() {
        if (!MDMEnrollmentUtil.getInstance().isAPNsConfigured()) {
            throw new APIHTTPException("APNS101", new Object[0]);
        }
        final Integer errorCode = APNsCertificateHandler.getInstance().getApnsErrorCode();
        if (errorCode == null) {
            return;
        }
        if (errorCode == 1001) {
            throw new APIHTTPException("APNS102", new Object[0]);
        }
        if (errorCode == 1002) {
            throw new APIHTTPException("APNS103", new Object[0]);
        }
        throw new APIHTTPException("COM0004", new Object[0]);
    }
    
    public void removeRequest(final JSONObject requestJSON) throws Exception {
        final Long erid = APIUtil.getResourceID(requestJSON, "enrollment_request_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        this.validateEridInput(erid, customerId);
        this.removeRequestUtil(new Long[] { erid }, APIUtil.getCustomerID(requestJSON));
    }
    
    public void removeMultipleRequest(final JSONObject requestJSON) throws Exception {
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        final JSONArray eridJSONArray = body.getJSONArray("enrollment_request_ids");
        final Long[] erIdArray = new Long[eridJSONArray.length()];
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        for (int i = 0; i < eridJSONArray.length(); ++i) {
            this.validateEridInput(JSONUtil.optLongForUVH(eridJSONArray, i, -1L), customerId);
            erIdArray[i] = JSONUtil.optLongForUVH(eridJSONArray, i, -1L);
        }
        this.removeRequestUtil(erIdArray, APIUtil.getCustomerID(requestJSON));
    }
    
    public void removeRequestUtil(final Long[] erid, final Long customerId) throws Exception {
        if (erid.length == 0) {
            throw new APIHTTPException("COM0005", new Object[] { "enrollment_request_id" });
        }
        final SelectQuery managedDeviceCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentRequestToDevice"));
        managedDeviceCountQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        managedDeviceCountQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        managedDeviceCountQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)erid, 8));
        managedDeviceCountQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        managedDeviceCountQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        final Criteria yetToEnrollCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)1, 1);
        managedDeviceCountQuery.setCriteria(managedDeviceCountQuery.getCriteria().and(yetToEnrollCri));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(managedDeviceCountQuery);
        final List requestWithDeviceMappingId = new ArrayList();
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("EnrollmentRequestToDevice");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                requestWithDeviceMappingId.add(row.get("ENROLLMENT_REQUEST_ID"));
            }
            if (requestWithDeviceMappingId.size() != 0) {
                throw new APIHTTPException("ENR00104", new Object[] { requestWithDeviceMappingId.toString() });
            }
        }
        String sDeviceIDs = String.valueOf(erid[0]);
        for (int i = 1; i < erid.length; ++i) {
            sDeviceIDs = sDeviceIDs + "," + String.valueOf(erid[i]);
        }
        final Boolean successfullyRemoved = MDMEnrollmentUtil.getInstance().removeDevice(sDeviceIDs, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), customerId);
        if (!successfullyRemoved) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void assignDepProfile(final JSONObject requestJSON) throws Exception {
        final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        new DeviceFacade().validateIfDeviceExists(resourceID, customerId);
        final Integer managed_status = (Integer)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "MANAGED_STATUS");
        if (managed_status == null) {
            throw new APIHTTPException("ENR00106", new Object[0]);
        }
        ManagedDeviceHandler.getInstance().reAssignDepProfile(resourceID, APIUtil.getCustomerID(requestJSON));
    }
    
    public JSONObject getReAssignUserDetails(final JSONObject requestJSON) throws Exception {
        final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        new DeviceFacade().validateIfDeviceExists(resourceID, customerId);
        final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromManagedDeviceID(resourceID);
        final Integer managed_status = (Integer)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "MANAGED_STATUS");
        if (erid == null || managed_status == null || (managed_status != 2 && managed_status != 10)) {
            throw new APIHTTPException("WIP0002", new Object[0]);
        }
        final HashMap userMap = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(erid);
        final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("domain_netbios_name", (Object)userMap.get("DOMAIN_NETBIOS_NAME"));
        responseJSON.put("device_name", (Object)deviceName);
        responseJSON.put("user_name", (Object)userMap.get("NAME"));
        responseJSON.put("email_address", (Object)userMap.get("EMAIL_ADDRESS"));
        return responseJSON;
    }
    
    public void reAssignUser(final JSONObject requestJSON) throws Exception {
        final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        new DeviceFacade().validateIfDeviceExists(resourceID, customerId);
        final JSONObject inputJSON = new JSONObject();
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        final Long assignUserId = JSONUtil.optLongForUVH(body, "user_id", Long.valueOf(-1L));
        if (assignUserId != -1L) {
            new UserFacade().validateIfUsersExists(Arrays.asList(assignUserId), customerId);
            inputJSON.put("MANAGED_USER_ID", (Object)assignUserId);
        }
        else {
            inputJSON.put("DOMAIN_NETBIOS_NAME", body.opt("domain"));
            inputJSON.put("NAME", body.opt("username"));
            inputJSON.put("EMAIL_ADDRESS", body.opt("email_id"));
            if (inputJSON.opt("DOMAIN_NETBIOS_NAME") == null) {
                inputJSON.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            }
            if (!MDMUtil.getInstance().isValidEmail(inputJSON.optString("EMAIL_ADDRESS", ""))) {
                throw new APIHTTPException("ENR00105", new Object[0]);
            }
            if (inputJSON.opt("PHONE_NUMBER") != null) {
                MDMEnrollmentUtil.getInstance();
                if (!MDMEnrollmentUtil.isValidPhone(String.valueOf(inputJSON.get("PHONE_NUMBER")))) {
                    throw new APIHTTPException("ENR00105", new Object[0]);
                }
            }
        }
        inputJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(requestJSON));
        inputJSON.put("DEVICE_NAME", body.opt("device_name"));
        final Integer managed_status = (Integer)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "MANAGED_STATUS");
        final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromManagedDeviceID(resourceID);
        final Boolean isAwaitingInManagedTab = managed_status == 5 && resourceID != null && customerId != null && MDMEnrollmentUtil.getInstance().isDeviceInManagedTab(resourceID, customerId);
        inputJSON.put("isDeviceAwaitingUserInManaged", (Object)isAwaitingInManagedTab);
        if (erid == null || managed_status == null || (managed_status != 2 && !isAwaitingInManagedTab && managed_status != 10)) {
            throw new APIHTTPException("WIP0002", new Object[0]);
        }
        inputJSON.put("ENROLLMENT_REQUEST_ID", (Object)erid);
        inputJSON.put("MANAGED_DEVICE_ID", (Object)resourceID);
        MDMEnrollmentRequestHandler.getInstance().modifyEnrollUserDetails(inputJSON);
    }
    
    private List<Long> getActiveDEPDevices(final List<Long> edidList) throws Exception {
        final ArrayList<Long> activeDEPDevicesList = new ArrayList<Long>();
        final ArrayList<String> activeSerialNumberDEPDevicesList = new ArrayList<String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("AppleDEPDeviceForEnrollment", "DEPDevicesSyncData", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        final Criteria edidCriteria1 = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)edidList.toArray(), 8);
        final Criteria serialNumberCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)Column.getColumn("DEPDevicesSyncData", "SERIAL_NUMBER"), 0);
        final Criteria activeDeviceCriteria = new Criteria(Column.getColumn("DEPDevicesSyncData", "DEVICE_STATUS"), (Object)1, 0);
        final Criteria allCriteria = edidCriteria1.and(activeDeviceCriteria).and(serialNumberCriteria);
        selectQuery.setCriteria(allCriteria);
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator it = dataObject.getRows("DeviceForEnrollment");
        while (it.hasNext()) {
            final Row dfeRow = it.next();
            final Long edid = (Long)dfeRow.get("ENROLLMENT_DEVICE_ID");
            final String serialNumber = (String)dfeRow.get("SERIAL_NUMBER");
            activeDEPDevicesList.add(edid);
            activeSerialNumberDEPDevicesList.add(serialNumber);
        }
        if (!activeSerialNumberDEPDevicesList.isEmpty()) {
            this.logger.log(Level.INFO, "Not deleting devices with serial numbers: {0} and Enrollment Device IDs: {1}, since they are active DEP devices", new Object[] { activeSerialNumberDEPDevicesList, activeDEPDevicesList });
        }
        return activeDEPDevicesList;
    }
    
    public void removeMultipleDevice(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            JSONArray eridJSONArray = new JSONArray();
            if (body.has("erids")) {
                eridJSONArray = body.getJSONArray("erids");
            }
            if (body.has("device_ids")) {
                final JSONArray deviceIDJSONArray = body.getJSONArray("device_ids");
                final Long[] erids = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdsFromManagedDeviceIDs(JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIDJSONArray).toArray(new Long[0]));
                for (int i = 0; i < erids.length; ++i) {
                    final JSONArray tempJSONArray = new JSONArray();
                    tempJSONArray.put((Object)erids[i]);
                    tempJSONArray.put(-1L);
                    eridJSONArray.put((Object)tempJSONArray);
                }
            }
            if (eridJSONArray.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long[] eridArray = new Long[eridJSONArray.length()];
            final ArrayList<Long> toRemoveEridArrayList = new ArrayList<Long>();
            final ArrayList<Long> dfeWithoutEridArrayList = new ArrayList<Long>();
            for (int j = 0; j < eridJSONArray.length(); ++j) {
                final Long erid = JSONUtil.optLongForUVH(eridJSONArray.getJSONArray(j), 0, -1L);
                if (erid != -1L) {
                    toRemoveEridArrayList.add(eridArray[j] = erid);
                }
                else {
                    dfeWithoutEridArrayList.add(JSONUtil.optLongForUVH(eridJSONArray.getJSONArray(j), 1, -1L));
                }
            }
            final Long userID = APIUtil.getUserID(requestJSON);
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final String userName = DMUserHandler.getUserNameFromUserID(userID);
            this.validateDeviceForEnrollmentIdForRemoval(dfeWithoutEridArrayList);
            if (!dfeWithoutEridArrayList.isEmpty()) {
                final List<Long> activeDEPDevices = this.getActiveDEPDevices(dfeWithoutEridArrayList);
                dfeWithoutEridArrayList.removeAll(activeDEPDevices);
                AdminEnrollmentHandler.removeDevice(dfeWithoutEridArrayList, userName, customerId);
            }
            if (!toRemoveEridArrayList.isEmpty()) {
                this.validateEridsInput(toRemoveEridArrayList);
                final boolean override = APIUtil.getBooleanFilter(requestJSON, "force_override", false);
                this.removeDevicesUtil(eridArray, customerId, userName, override);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removing devices  ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void validateDeviceForEnrollmentIdForRemoval(final ArrayList<Long> dfeWithoutEridArrayList) throws Exception {
        try {
            if (dfeWithoutEridArrayList != null && !dfeWithoutEridArrayList.isEmpty()) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
                selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "DEP_TOKEN_ID"));
                final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)dfeWithoutEridArrayList.toArray(), 8);
                selectQuery.setCriteria(eridCriteria);
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                final Iterator iterator = dataObject.getRows("AppleDEPDeviceForEnrollment", new Criteria(Column.getColumn("DEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                if (iterator.hasNext()) {
                    throw new APIHTTPException("ENR00103", new Object[0]);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in validateDeviceForEnrollmentIdForRemoval");
            throw e;
        }
    }
    
    private ArrayList getCustomerIdsForUser(final Long userId) {
        final ArrayList<Long> customerIDs = new ArrayList<Long>();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addJoin(new Join("CustomerInfo", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            query.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
            query.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"));
            query.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"));
            final Criteria cri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0);
            query.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), true);
            query.addSortColumn(sortColumn);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                final Iterator customerRows = resultDO.getRows("CustomerInfo");
                while (customerRows.hasNext()) {
                    final Row custRow = customerRows.next();
                    customerIDs.add((Long)custRow.get("CUSTOMER_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getCustomerIdsForUser", ex);
        }
        return customerIDs;
    }
    
    public JSONObject deprovisionMultipleDevice(final JSONObject requestJSON) throws Exception {
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        final JSONArray resourceIdJSONArray = body.getJSONArray("device_ids");
        final int allowedCount = ManagedDeviceHandler.getInstance().getDeviceCountAllowedToDeprovision();
        if (resourceIdJSONArray.length() > allowedCount) {
            throw new APIHTTPException("WIP0005", new Object[] { allowedCount + "@@@" + allowedCount + "@@@" + allowedCount });
        }
        Long customerId = null;
        Boolean forAllCustomers = false;
        final Boolean isAdminUser = APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "MDM_Settings_Admin", "ModernMgmt_Settings_Admin" });
        try {
            customerId = APIUtil.getCustomerID(requestJSON);
        }
        catch (final APIHTTPException e) {
            if (!ApiFactoryProvider.getUtilAccessAPI().isMSP() || e.toJSONObject().getString("error_code") != "COM0022" || !body.optBoolean("all_customers", false)) {
                throw e;
            }
            if (!isAdminUser) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            forAllCustomers = true;
        }
        final List<Long> deviceList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIdJSONArray);
        List<Long> managedDevicesList = new ArrayList<Long>(new TreeSet<Long>(deviceList));
        final DeviceFacade deviceFacade = new DeviceFacade();
        JSONObject deprovisionReturnJson = new JSONObject();
        JSONArray successList = new JSONArray();
        JSONArray failureList = new JSONArray();
        if (forAllCustomers) {
            final ArrayList customerArrayList = this.getCustomerIdsForUser(APIUtil.getUserID(requestJSON));
            if (body.optBoolean("inverse", false)) {
                managedDevicesList = deviceFacade.getAllManagedDevicesExceptDeviceList(managedDevicesList, customerArrayList);
            }
            final Map<Long, ArrayList> customerToDevicesMap = deviceFacade.validateAndGetDevicesForAllCustomerIds(managedDevicesList, customerArrayList);
            for (int i = 0; i < customerArrayList.size(); ++i) {
                customerId = customerArrayList.get(i);
                if (customerToDevicesMap.get(customerArrayList.get(i)) != null) {
                    deprovisionReturnJson = this.deprovisionDevicesUtil(requestJSON, JSONUtil.getInstance().convertListToStringJSONArray(customerToDevicesMap.get(customerId)), customerId);
                    ManagedDeviceHandler.getInstance().addMDMEventLogForBulkDeprovision(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), deprovisionReturnJson.getJSONArray("SuccessList").length());
                    successList = JSONUtil.mergeJSONArray(successList, (JSONArray)deprovisionReturnJson.get("SuccessList"));
                    failureList = JSONUtil.mergeJSONArray(failureList, (JSONArray)deprovisionReturnJson.get("FailureList"));
                }
            }
            deprovisionReturnJson.put("SuccessList", (Object)successList);
            deprovisionReturnJson.put("FailureList", (Object)failureList);
            return deprovisionReturnJson;
        }
        if (body.optBoolean("inverse", false)) {
            if (!isAdminUser) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            final ArrayList<Long> customerArrayList2 = new ArrayList<Long>();
            customerArrayList2.add(customerId);
            managedDevicesList = deviceFacade.getAllManagedDevicesExceptDeviceList(managedDevicesList, customerArrayList2);
            if (managedDevicesList.size() < 1) {
                deprovisionReturnJson.put("SuccessList", (Object)successList);
                deprovisionReturnJson.put("FailureList", (Object)failureList);
                return deprovisionReturnJson;
            }
        }
        new DeviceFacade().validateIfDevicesExists(managedDevicesList, customerId);
        deprovisionReturnJson = this.deprovisionDevicesUtil(requestJSON, JSONUtil.getInstance().convertListToStringJSONArray(managedDevicesList));
        ManagedDeviceHandler.getInstance().addMDMEventLogForBulkDeprovision(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), deprovisionReturnJson.getJSONArray("SuccessList").length());
        return deprovisionReturnJson;
    }
    
    public String getRequestIdStr(final JSONArray resourceIdArray) throws Exception {
        String reqId = String.valueOf(resourceIdArray.get(0));
        for (int i = 1; i < resourceIdArray.length(); ++i) {
            reqId = reqId + "," + resourceIdArray.get(i);
        }
        return reqId;
    }
    
    public JSONObject getEnrollTabCount(final JSONObject requestJSON) throws Exception {
        return ManagedDeviceHandler.getInstance().getDeviceCountForEnrollmentTabs(APIUtil.getCustomerID(requestJSON));
    }
    
    public JSONObject getstagedviewcount(final JSONObject requestJSON) throws Exception {
        final JSONObject body = requestJSON.getJSONObject("msg_header").getJSONObject("filters");
        final int selectedStatus = body.optInt("device_status", -1);
        final int templateType = body.optInt("template_type", -1);
        final int platformType = body.optInt("platform_type", -1);
        final JSONObject inputjson = new JSONObject();
        inputjson.put("customerid", (Object)APIUtil.getCustomerID(requestJSON));
        inputjson.put("selectedStatus", selectedStatus);
        inputjson.put("templateType", templateType);
        inputjson.put("platformType", platformType);
        final int count = ManagedDeviceHandler.getInstance().getDeviceCountForStagedview(inputjson);
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("count", count);
        return responseJSON;
    }
    
    public JSONObject getDomainList(final JSONObject requestJSON) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final List domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps(APIUtil.getCustomerID(requestJSON));
        jsonObject.put("domain_list", (Collection)domainList);
        return jsonObject;
    }
    
    public JSONObject getEnrollmentStatusForInvite(final JSONObject requestJSON) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final Long erid = APIUtil.getResourceID(requestJSON, "enrollment_request_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        this.validateEridInput(erid, customerId);
        int status = -1;
        final DataObject dataObject = MDMUtil.getPersistence().get("InvitationEnrollmentRequest", new Criteria(Column.getColumn("InvitationEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getRow("InvitationEnrollmentRequest");
            status = (int)row.get("REGISTRATION_STATUS");
        }
        if (status == -1) {
            throw new APIHTTPException("ENR00102", new Object[0]);
        }
        if (status == 3) {
            jsonObject.put("is_enrollment_success", true);
        }
        else {
            jsonObject.put("is_enrollment_success", false);
        }
        return jsonObject;
    }
    
    public JSONObject isMailInQueue() throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_mail_in_queue", MDMEnrollmentRequestHandler.getInstance().getMailSentStatus());
        return jsonObject;
    }
    
    public JSONObject getWakeUpRetryCount(final JSONObject requestJSON) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", MDMEnrollmentRequestHandler.getInstance().getWakeUpRetryCount(APIUtil.getCustomerID(requestJSON)));
        return jsonObject;
    }
    
    public JSONObject retryWakeup(final JSONObject requestJSON) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject requestDetails = requestJSON.getJSONObject("msg_body");
        MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(Long.parseLong(String.valueOf(requestDetails.get("erid"))), 1, "dc.mdm.enroll.wake_up.retry_remarks", -1);
        WakeUpProcessor.wakeUpAsynchronously(requestDetails.getLong("erid"), null);
        return jsonObject;
    }
    
    public JSONObject getWaitingForLicenseDeviceCount(final JSONObject requestJSON) throws Exception {
        final JSONObject deviceCountJson = new JSONObject();
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final int deviceAwaitingLicenseCount = EnrollmentLicenseHandler.getInstance().getAwaitingLicenseDeviceCount(customerId, EnrollmentAPIConstants.AwaitingLicenseType.NON_UEM);
            if (deviceAwaitingLicenseCount != 0) {
                deviceCountJson.put("waiting_for_license_count", deviceAwaitingLicenseCount);
            }
            else {
                deviceCountJson.put("waiting_for_license_count", 0);
            }
            deviceCountJson.put("license_limit", (Object)LicenseProvider.getInstance().getNoOfMobileDevicesManaged());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occured in showLicenseMessage ", e);
        }
        return deviceCountJson;
    }
    
    public JSONObject getLoadEnrollmentProperties(final JSONObject requestJSON) throws Exception {
        final JSONObject props = new JSONObject();
        final Long platform = APIUtil.getResourceID(requestJSON, "load_enrollment_reques_id");
        if (platform != 1L && platform != 2L && platform != 3L) {
            throw new APIHTTPException("ENR001", new Object[0]);
        }
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final boolean isSuperAdminVerified = ApiFactoryProvider.getAuthUtilAccessAPI().isSuperAdminVerified();
        final SMSAPI smsAPI = MDMApiFactoryProvider.getSMSAPI();
        props.put("is_sms_settings_configured", smsAPI.isSMSSettingsConfigured());
        props.put("sms_credits_remaining", smsAPI.getRemainingCredits());
        props.put("is_super_admin_verified", isSuperAdminVerified);
        switch (platform.intValue()) {
            case 1: {
                MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Enrollment_Module", "enrollment_from_ios_enroll");
                final DEPAdminEnrollmentHandler handler = new DEPAdminEnrollmentHandler();
                int enrolledadminDeviceCount = handler.getAdminEnrolledDeviceCount(customerId);
                final AppleConfiguratorEnrollmentHandler achandler = new AppleConfiguratorEnrollmentHandler();
                enrolledadminDeviceCount += achandler.getAdminEnrolledDeviceCount(customerId);
                final String skipLoadiOSPage = MDMUtil.getUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "skipLoadiOSPage");
                final String checkFirstChoice = MDMUtil.getUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "checkFirstChoice");
                if (enrolledadminDeviceCount > 0) {
                    props.put("skip_device_supervision_info", true);
                    break;
                }
                if (checkFirstChoice == null || !checkFirstChoice.equals("true")) {
                    final String checkiosEnroll = MDMUtil.getUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "checkiosEnroll");
                    if (checkiosEnroll == null) {
                        MDMUtil.updateUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "checkiosEnroll", "false");
                    }
                    break;
                }
                if (skipLoadiOSPage == null || !skipLoadiOSPage.equals("true")) {
                    props.put("skip_device_supervision_info", true);
                    break;
                }
                props.put("skip_device_supervision_info", false);
                MDMUtil.updateUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "skipLoadiOSPage", "true");
                break;
            }
            case 2: {
                final Boolean is_samsung = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optBoolean("is_samsung", false);
                if (!is_samsung) {
                    final String skipLoadNonSamsungPage = MDMUtil.getUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "inviteFromNonSamsungPage");
                    if (skipLoadNonSamsungPage == null || !skipLoadNonSamsungPage.equals("true")) {
                        props.put("skip_device_supervision_info", false);
                    }
                    else {
                        props.put("skip_device_supervision_info", true);
                    }
                    if (skipLoadNonSamsungPage != null && !skipLoadNonSamsungPage.isEmpty()) {
                        MDMUtil.updateUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "skipLoadNonSamsungPage", "true");
                    }
                }
                else {
                    props.put("samsung_work_profile_enabled", (Object)MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceSamsungWorkProfileInPersonalOwned"));
                    props.put("skip_device_supervision_info", true);
                }
                final Boolean bLicense = KnoxUtil.getInstance().isKnoxLicenseAvailable(customerId);
                final HashMap dsSetting = KnoxLicenseHandler.getInstance().getKnoxCustomerDS(customerId);
                props.put("knox_container_settings", (Object)this.getKnoxDsJSON(dsSetting));
                props.put("knox_container_license", (Object)bLicense);
                break;
            }
        }
        return props;
    }
    
    private JSONObject getKnoxDsJSON(final HashMap dsSetting) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final Set keySet = dsSetting.keySet();
        for (final String key : keySet) {
            final String jsonKey = this.getKnoxDsKey(key);
            if (!jsonKey.equalsIgnoreCase("")) {
                if (!jsonKey.equals("knox_group_id")) {
                    jsonObject.put(jsonKey, dsSetting.get(key));
                }
                else {
                    final String groupIdStr = dsSetting.get(key);
                    if (groupIdStr == null || groupIdStr.equalsIgnoreCase("")) {
                        continue;
                    }
                    final String[] groupId = groupIdStr.split(",");
                    final JSONArray jsonArray = new JSONArray((Collection)Arrays.asList(groupId));
                    jsonObject.put(jsonKey, (Object)jsonArray);
                }
            }
        }
        return jsonObject;
    }
    
    private String getKnoxDsKey(final String key) {
        if (key.equals("dsGroupID")) {
            return "knox_group_id";
        }
        if (key.equals("dsOption")) {
            return "knox_distibution_option";
        }
        if (key.equals("dsToGroup")) {
            return "knox_distibution_to_selected_group";
        }
        return "";
    }
    
    public void moveToManagementSingleDevice(final JSONObject requestJSON) throws Exception {
        final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        new DeviceFacade().validateIfDeviceExists(resourceID, customerId);
        this.moveToManagementUtil(new Long[] { resourceID });
    }
    
    public void moveToManagementMultipleDevices(final JSONObject requestJSON) throws Exception {
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        final JSONArray resourceIdJSONArray = body.getJSONArray("device_ids");
        final Long[] resourceID = new Long[resourceIdJSONArray.length()];
        if (resourceIdJSONArray.length() != 0) {
            for (int i = 0; i < resourceIdJSONArray.length(); ++i) {
                resourceID[i] = JSONUtil.optLongForUVH(resourceIdJSONArray, i, -1L);
            }
        }
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        new DeviceFacade().validateIfDevicesExists(JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIdJSONArray), customerId);
        this.moveToManagementUtil(resourceID);
    }
    
    public void moveToManagementUtil(final Long[] resourceID) throws Exception {
        if (resourceID == null || resourceID.length == 0) {
            throw new APIHTTPException("COM0005", new Object[] { "device_ids" });
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 8));
        DMDataSetWrapper ds = null;
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final List<Long> deviceIDList = new ArrayList<Long>();
        deviceIDList.addAll(Arrays.asList(resourceID));
        while (ds.next()) {
            final Long managedDeviceId = (Long)ds.getValue("RESOURCE_ID");
            final int devPlatformType = (int)ds.getValue("PLATFORM_TYPE");
            final int modelType = (int)ds.getValue("MODEL_TYPE");
            final boolean isDCEE = ProductUrlLoader.getInstance().getValue("productcode").equals("DCEE");
            final boolean isLaptop = (devPlatformType == 3 || devPlatformType == 1) && modelType != 1;
            if (isLaptop && isDCEE) {
                final boolean isEndpointServiceEnabled = LicenseProvider.getInstance().isEndpointServiceEnabled();
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                final boolean isTrialCustomer = licenseType.equalsIgnoreCase("T");
                final boolean isFreeCustomer = licenseType.equalsIgnoreCase("F");
                final boolean isUemLicenseSatisfied = isEndpointServiceEnabled || isTrialCustomer || isFreeCustomer;
                if (!isUemLicenseSatisfied) {
                    throw new APIHTTPException("ENR00107", new Object[0]);
                }
            }
            deviceIDList.remove(managedDeviceId);
        }
        if (!deviceIDList.isEmpty()) {
            throw new APIHTTPException("COM0005", new Object[] { "device_ids" });
        }
        final JSONArray resourceIDJsonArray = new JSONArray((Collection)Arrays.asList(resourceID));
        final List requestIds = new ArrayList();
        requestIds.addAll(Arrays.asList(MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdsFromManagedDeviceIDs(resourceID)));
        final String result = EnrollmentLicenseHandler.getInstance().manageDevice(resourceIDJsonArray, requestIds);
        if (result.equalsIgnoreCase("license_limit_reached") || result.equalsIgnoreCase("additional_license_needed")) {
            throw new APIHTTPException("COM00020", new Object[0]);
        }
        if (result.equalsIgnoreCase("failure")) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getEnrollmentRequestList(final JSONObject jsonObject) throws Exception {
        final JSONArray erJSON = new JSONArray();
        final Long customerID = APIUtil.getCustomerID(jsonObject);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0).and(userNotInTrashCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "PHONE_NUMBER"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUESTED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final JSONObject requestDetails = new JSONObject();
            requestDetails.put("ENROLLMENT_REQUEST_ID", (Object)ds.getValue("ENROLLMENT_REQUEST_ID"));
            requestDetails.put("EMAIL_ADDRESS", (Object)ds.getValue("EMAIL_ADDRESS"));
            requestDetails.put("REQUESTED_TIME", ds.getValue("REQUESTED_TIME"));
            String ph = (String)ds.getValue("PHONE_NUMBER");
            if (ph == null || ph.equalsIgnoreCase("")) {
                ph = "--";
            }
            requestDetails.put("PHONE_NUMBER", (Object)ph);
            requestDetails.put("NAME", (Object)ds.getValue("NAME"));
            requestDetails.put("DOMAIN_NETBIOS_NAME", (Object)ds.getValue("DOMAIN_NETBIOS_NAME"));
            final int erType = (int)ds.getValue("ENROLLMENT_TYPE");
            requestDetails.put("enrollment_type_constant", erType);
            switch (erType) {
                case 1: {
                    requestDetails.put("ENROLLMENT_TYPE", (Object)I18N.getMsg("dc.common.enrollment.invitation", new Object[0]));
                    break;
                }
                case 2: {
                    requestDetails.put("ENROLLMENT_TYPE", (Object)I18N.getMsg("dc.mdm.enroll.self_enrollment", new Object[0]));
                    break;
                }
                case 3: {
                    requestDetails.put("ENROLLMENT_TYPE", (Object)I18N.getMsg("mdm.enroll.admin_enroll", new Object[0]));
                    break;
                }
            }
            final Integer managed_status = (Integer)ds.getValue("MANAGED_STATUS");
            if (managed_status != null) {
                switch (managed_status) {
                    case 1: {
                        requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("dc.mdm.enrollment_failed", new Object[0]));
                        break;
                    }
                    case 2: {
                        requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("dc.mdm.enrolled", new Object[0]));
                        break;
                    }
                    case 10: {
                        requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("dc.mdm.in_stock", new Object[0]));
                        break;
                    }
                    case 9: {
                        requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("mdm.deprovision.in_repair", new Object[0]));
                        break;
                    }
                    case 11: {
                        requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("dc.mdm.retired", new Object[0]));
                        break;
                    }
                }
            }
            else {
                final Integer request_status = (Integer)ds.getValue("REQUEST_STATUS");
                if (request_status == 1) {
                    requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("dc.mdm.yet_to_enroll", new Object[0]));
                }
                else if (request_status == 0) {
                    requestDetails.put("REQUEST_STATUS", (Object)I18N.getMsg("dc.mdm.enrollment_failed", new Object[0]));
                }
            }
            erJSON.put((Object)requestDetails);
        }
        return erJSON;
    }
    
    public JSONObject wakeUpUsingResourceId(final JSONObject requestJSON) throws Exception {
        final Long managedDeviceId = APIUtil.getResourceID(requestJSON, "device_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        new DeviceFacade().validateIfDeviceExists(managedDeviceId, customerId);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceId, 0).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new SyMException(13001, (Throwable)null);
        }
        final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromManagedDeviceID(managedDeviceId);
        MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(erid, 1, "dc.mdm.enroll.wake_up.retry_remarks", -1);
        WakeUpProcessor.wakeUpAsynchronously(erid, null);
        return new JSONObject();
    }
    
    public JSONArray formatADGroupsList(final JSONObject jsonObject) throws Exception {
        final JSONArray jsonArray = jsonObject.getJSONArray("item");
        final JSONArray returnArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONArray json = jsonArray.getJSONObject(i).getJSONArray("userdata");
            final JSONObject adDetails = new JSONObject();
            for (int j = 0; j < json.length(); ++j) {
                final JSONObject object = json.getJSONObject(j);
                final String key = String.valueOf(object.get("name"));
                if (!key.equalsIgnoreCase("checked")) {
                    adDetails.put(key, (Object)String.valueOf(object.get("content")));
                }
            }
            returnArray.put((Object)adDetails);
        }
        return returnArray;
    }
    
    public JSONObject getAuthorisedDetailsForAD(final JSONObject jsonObject) throws APIHTTPException {
        throw new APIHTTPException("AD1102", new Object[0]);
    }
    
    public String getAutoAssignKey(final String key) throws Exception {
        switch (key) {
            case "1": {
                return "apple_corporate";
            }
            case "2": {
                return "apple_personal";
            }
            case "3": {
                return "android_corporate";
            }
            case "4": {
                return "android_personal";
            }
            case "5": {
                return "windows_corporate";
            }
            case "6": {
                return "windows_personal";
            }
            case "7": {
                return "neutral_corporate";
            }
            case "8": {
                return "neutral_personal";
            }
            case "apple_corporate": {
                return "1";
            }
            case "apple_personal": {
                return "2";
            }
            case "android_corporate": {
                return "3";
            }
            case "android_personal": {
                return "4";
            }
            case "windows_corporate": {
                return "5";
            }
            case "windows_personal": {
                return "6";
            }
            case "neutral_corporate": {
                return "7";
            }
            case "neutral_personal": {
                return "8";
            }
            default: {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
        }
    }
    
    public JSONObject formatSelfEnrollmentGroupsJson(final JSONObject jsonObject) throws Exception {
        final JSONObject selfEnrollmentGroups = new JSONObject();
        final Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            selfEnrollmentGroups.put(this.getAutoAssignKey(key), jsonObject.get(key));
        }
        return selfEnrollmentGroups;
    }
    
    public List getUserList(final Long customerID, final String searchString) {
        final int RANGE = 7;
        final List usersList = ADSyncDataHandler.getInstance().getDirUserListForSuggest(customerID, searchString, RANGE);
        usersList.addAll(ManagedUserHandler.getInstance().getUserListForSearch(customerID, searchString, RANGE - usersList.size()));
        final List mdmProcessedList = new ArrayList();
        for (int i = 0; i < usersList.size(); ++i) {
            Properties userDataProps = usersList.get(i);
            try {
                userDataProps = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().appendManagedUserProperties(userDataProps);
                mdmProcessedList.add(userDataProps);
            }
            catch (final Exception ex) {
                SyMLogger.log("MDMEnrollment", Level.INFO, (String)null, (Throwable)ex);
            }
        }
        return mdmProcessedList;
    }
    
    public List getDomainUserList(final Long customerID, final Long domainId, final String searchString) {
        final int RANGE = 5;
        final List usersList = ADSyncDataHandler.getInstance().getDirObjListForSuggest(customerID, 2, searchString, domainId, new Range(0, RANGE));
        return usersList;
    }
    
    public JSONObject getUsers(final JSONObject requestJSON) throws Exception {
        String search = APIUtil.getStringFilter(requestJSON, "user");
        final Long domainId = APIUtil.getLongFilter(requestJSON, "domain_id");
        final Boolean showOrgUsers = APIUtil.getBooleanFilter(requestJSON, "showorgusers");
        search = (SyMUtil.isStringEmpty(search) ? "" : search);
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        List usersList;
        if (showOrgUsers) {
            final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps("Zoho Directory", customerID);
            final Long domainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            usersList = ADSyncDataHandler.getInstance().getDirObjListForSuggest(customerID, 2, search, domainID, new Range(0, 5));
        }
        else if (domainId != -1L) {
            usersList = this.getDomainUserList(customerID, domainId, search);
        }
        else {
            usersList = this.getUserList(customerID, search);
        }
        final JSONObject jsonObject = new JSONObject();
        final Iterator iterator = usersList.iterator();
        final JSONArray jsonArray = new JSONArray();
        while (iterator.hasNext()) {
            final Properties user = iterator.next();
            final JSONObject newUser = new JSONObject();
            newUser.put("user_id", ((Hashtable<K, Object>)user).get("dataId"));
            final JSONObject details = new JSONObject(user.getProperty("dataValue"));
            newUser.put("email", details.get("EMAIL_ADDRESS"));
            newUser.put("domain_name", details.get("DOMAIN_NETBIOS_NAME"));
            newUser.put("name", details.get("NAME"));
            newUser.put("phone_number", details.opt("PHONE_NUMBER"));
            jsonArray.put((Object)newUser);
        }
        jsonObject.put("user", (Object)jsonArray);
        return jsonObject;
    }
    
    public Long getUserIdForEnrollmentRequestToDevice(final Long resourceId) throws DataAccessException {
        final SelectQuery selectQuery = this.getUserForDeviceEnrollmentRequestQuery();
        selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
        Long userId = null;
        try {
            userId = (Long)MDMUtil.getPersistence().get(selectQuery).getFirstRow("DeviceEnrollmentRequest").get("USER_ID");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.FINE, "no userid found for deviceenrollmentrequest ", (Throwable)e);
        }
        return userId;
    }
    
    private SelectQuery getUserForDeviceEnrollmentRequestQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        final Join enrollmentRequestToDeviceJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        final Join aaaUserJoin = new Join("DeviceEnrollmentRequest", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        selectQuery.addJoin(enrollmentRequestToDeviceJoin);
        selectQuery.addJoin(aaaUserJoin);
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        return selectQuery;
    }
    
    public JSONObject getDeviceProvisioningUserStatus() throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", (Object)ManagedDeviceHandler.getInstance().isDeviceProvisioningUser());
        return jsonObject;
    }
    
    private void removeSelfEnrollLimit(final Long customerID) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("CustomerParams");
        deleteQuery.setCriteria(new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)"selfEnrollDeviceLimit", 0, false).and(new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0)));
        MDMUtil.getPersistenceLite().delete(deleteQuery);
    }
    
    private void downloadAndroidAgent() throws Exception {
        try {
            final String folderLocation = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "agent";
            final String fileLocation = folderLocation + File.separator + "MDMAndroidAgent.apk";
            final File file = new File(fileLocation);
            if (!file.exists()) {
                final String agentDownloadURL = MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagenturl") + MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagentversioncode") + "/" + "MDMAndroidAgent.apk";
                final DownloadStatus downloadstatus = DownloadManager.getInstance().downloadBinaryFile(agentDownloadURL, fileLocation, MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagentchecksum"), new SSLValidationType[0]);
                if (downloadstatus.getStatus() != 0) {
                    Logger.getLogger("DownloadManager").log(Level.INFO, "Android agent download failed {0}", downloadstatus.getErrorMessage());
                    throw new APIHTTPException("CTS001", new Object[0]);
                }
                Logger.getLogger("DownloadManager").log(Level.INFO, "Android agent downloaded successfully");
            }
            else {
                Logger.getLogger("DownloadManager").log(Level.INFO, "Skipping android agent download as it is already present");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("DownloadManager").log(Level.SEVERE, "Exception while downloading android agent ", ex);
            throw ex;
        }
    }
    
    public JSONObject getLastUsedOwnedByValue(final JSONObject requestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final JSONObject jsonObject = new JSONObject();
            final HashMap enrollParamMap = MDMEnrollmentUtil.getInstance().getLastEnrollParamMap();
            final int lastOwnedBy = enrollParamMap.get("OWNED_BY");
            jsonObject.put("owned_by", lastOwnedBy);
            final int lastPlatformType = enrollParamMap.get("PLATFORM_TYPE");
            jsonObject.put("platform_type", lastPlatformType);
            final JSONObject technicianDetails = MDMEnrollmentRequestHandler.getInstance().getCurrentTechnicianDetails(customerId);
            jsonObject.put("email_id", (Object)technicianDetails.optString("EmailAddress", (String)null));
            jsonObject.put("is_editable", technicianDetails.optBoolean("isEditable", true));
            jsonObject.put("user_name", (Object)technicianDetails.optString("UserName", (String)null));
            jsonObject.put("domain_name", (Object)technicianDetails.optString("DomainName", (String)null));
            jsonObject.put("phone_number", (Object)technicianDetails.optString("PHONE_NUMBER", (String)null));
            return jsonObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getLastUsedOwnedByValue", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateOwnedByForDevice(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long enrollmentRequestId = APIUtil.getResourceID(requestJSON, "enrol_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final ArrayList enrolmentRequestList = new ArrayList();
            enrolmentRequestList.add(enrollmentRequestId);
            if (!new MDMEnrollAction().validateEnrollmentRequest(enrolmentRequestList, customerId)) {
                throw new APIHTTPException("ENR00101", new Object[0]);
            }
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            final int ownedBy = bodyJSON.getInt("owned_by");
            MDMEnrollmentUtil.getInstance().updateDeviceOwnedBy(enrollmentRequestId, ownedBy);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateOwnedByForDevice   ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void saveInactiveDevicePolicySettings(final JSONObject requestJSON) throws APIHTTPException, SystemException {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            final Long inactiveThreshold = body.getLong("inactive_threshold");
            if (inactiveThreshold == null || inactiveThreshold < InactiveDevicePolicyConstants.INACTIVE_THRESHOLD_DEFAULT) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.api.error.invalid_inactive_threshold", new Object[0]) });
            }
            Long actionThreshold = body.getLong("action_threshold");
            if (actionThreshold == -1L) {
                actionThreshold = InactiveDevicePolicyConstants.IDP_ACTION_THRESHOLD_DEFAULT;
            }
            if (actionThreshold == null || actionThreshold < InactiveDevicePolicyConstants.IDP_ACTION_THRESHOLD_DEFAULT) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.api.error.invalid_action_threshold", new Object[0]) });
            }
            int actionType = body.getInt("action_type");
            if (actionType == -1) {
                actionType = 0;
            }
            if (actionType < 0 || actionType > 3) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.api.error.invalid_action_type", new Object[0]) });
            }
            final Long currentTime = MDMUtil.getCurrentTimeInMillis();
            final JSONObject settingsParam = new JSONObject();
            settingsParam.put("customerId", (Object)customerId);
            settingsParam.put("userId", (Object)userId);
            settingsParam.put("inactiveThreshold", (Object)inactiveThreshold);
            settingsParam.put("actionThreshold", (Object)actionThreshold);
            settingsParam.put("actionType", actionType);
            settingsParam.put("currentTime", (Object)currentTime);
            MDMEnrollmentUtil.getInstance().addOrUpdateInactiveDevicePolicySettings(settingsParam);
            mdmTransactionManager.commit();
        }
        catch (final Exception e) {
            mdmTransactionManager.rollBack();
            this.logger.log(Level.SEVERE, "Exception in saveInactiveDevicePolicySettings   ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject sendAppEnrollmentMail(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long enrollmentRequestId = APIUtil.getResourceID(requestJSON, "enrol_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final ArrayList enrolmentRequestList = new ArrayList();
            enrolmentRequestList.add(enrollmentRequestId);
            if (!new MDMEnrollAction().validateEnrollmentRequest(enrolmentRequestList, customerId)) {
                throw new APIHTTPException("ENR00101", new Object[0]);
            }
            final int platform = ManagedDeviceHandler.getInstance().getPlatformForErid(enrollmentRequestId);
            final JSONObject responseJSON = new JSONObject();
            Label_0238: {
                switch (platform) {
                    case 3: {
                        final Long resourceId = ManagedDeviceHandler.getInstance().getManagedDeviceIDFromEnrollRequestID(enrollmentRequestId);
                        final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
                        final JSONObject jsonObject = WpCompanyHubAppHandler.getInstance().sendWPCompanyHubAppMail(Arrays.asList(resourceId), new Long(1L), deviceDetails.customerId, 0);
                        final int status = jsonObject.getInt("code");
                        switch (status) {
                            case 0: {
                                responseJSON.put("mail_sent", true);
                                break Label_0238;
                            }
                            case 1: {
                                throw new APIHTTPException("MAS001", new Object[0]);
                            }
                            case 2: {
                                responseJSON.put("mail_sent", false);
                                break Label_0238;
                            }
                        }
                        break;
                    }
                }
            }
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in sendAppEnrollmentMail   ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getInactiveDevicePolicyDetails(final JSONObject requestJSON) throws APIHTTPException {
        JSONObject idpParams = new JSONObject();
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            idpParams = MDMEnrollmentUtil.getInstance().getInactiveDevicePolicyDetailsParam(customerId);
            if (idpParams.isNull("policy_id")) {
                throw new APIHTTPException("ENR00114", new Object[0]);
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in getInactiveDevicePolicyDetails ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return idpParams;
    }
    
    public void deleteInactiveDevicePolicy(final JSONObject requestJSON) throws Exception {
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            final JSONObject idpParams = MDMEnrollmentUtil.getInstance().getInactiveDevicePolicyDetailsParam(customerID);
            if (idpParams.isNull("policy_id")) {
                throw new APIHTTPException("ENR00114", new Object[0]);
            }
            MDMEnrollmentUtil.getInstance().removeInactiveDevicePolicy(customerID);
            mdmTransactionManager.commit();
        }
        catch (final Exception e) {
            mdmTransactionManager.rollBack();
            this.logger.log(Level.SEVERE, "Exception in removeInactiveDevicePolicy   ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getEnrollmentProperties(final JSONObject requestJSON) {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final SMSAPI smsAPI = MDMApiFactoryProvider.getSMSAPI();
            responseJSON.put("is_sms_settings_configured", smsAPI.isSMSSettingsConfigured());
            responseJSON.put("sms_credits_remaining", smsAPI.getRemainingCredits());
            responseJSON.put("is_super_admin_verified", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().isSuperAdminVerified());
            final DEPAdminEnrollmentHandler handler = new DEPAdminEnrollmentHandler();
            int enrolledadminDeviceCount = handler.getAdminEnrolledDeviceCount(customerId);
            final AppleConfiguratorEnrollmentHandler achandler = new AppleConfiguratorEnrollmentHandler();
            enrolledadminDeviceCount += achandler.getAdminEnrolledDeviceCount(customerId);
            responseJSON.put("apple_admin_enrollment_count", enrolledadminDeviceCount);
            responseJSON.put("is_license_limit_reached", MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId));
            responseJSON.put("waiting_for_license_count", EnrollmentLicenseHandler.getInstance().getAwaitingLicenseDeviceCount(customerId, EnrollmentAPIConstants.AwaitingLicenseType.NON_UEM));
            responseJSON.put("enrolled_count", ManagedDeviceHandler.getInstance().getManagedDeviceCount(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0))));
            responseJSON.put("license_type", (Object)LicenseProvider.getInstance().getLicenseType());
            final UEMCentralLicenseMessageHandler uemMessageHandler = new UEMCentralLicenseMessageHandler();
            responseJSON.put("is_uem_limit_exceed", !uemMessageHandler.getMessageStatus(null));
            responseJSON.put("license_count", (Object)LicenseProvider.getInstance().getNoOfMobileDevicesManaged());
            if (CustomerInfoUtil.getInstance().isMSP()) {
                responseJSON.put("customer_license_count", new MDMLicenseImplMSP().getTotalDeviceAllocated());
            }
            responseJSON.put("uem_waiting_for_license_count", EnrollmentLicenseHandler.getInstance().getAwaitingLicenseDeviceCount(customerId, EnrollmentAPIConstants.AwaitingLicenseType.UEM));
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getEnrollmentProperties   ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject checkAPNSConnection() throws APIHTTPException {
        try {
            final boolean isOpened = APNSImpl.getInstance().IsAPNsReachacble();
            if (isOpened) {
                MessageProvider.getInstance().hideMessage("APNS_PORT_BLOCKED");
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("apns_reachable", isOpened);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkAPNSConnection : ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public boolean isAssignUserEnabled(final Long customerId) {
        boolean isAssignUserEnabled = true;
        if (MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId)) {
            isAssignUserEnabled = false;
        }
        return isAssignUserEnabled;
    }
    
    public boolean isAssignUserForLaptopEnabled(final Long customerId) {
        return this.isAssignUserEnabled(customerId);
    }
    
    public boolean validateEnrollmentRequest(final List reqID, final Long customerId) throws DataAccessException {
        boolean isValid = true;
        boolean duplicateDevicesPresent = false;
        Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)reqID.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        criteria = criteria.and(customerCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            isValid = false;
        }
        else {
            final Iterator<Row> enrollmentRequestIds = dataObject.getRows("DeviceEnrollmentRequest");
            final List enrollementRequestList = DBUtil.getColumnValuesAsList((Iterator)enrollmentRequestIds, "ENROLLMENT_REQUEST_ID");
            if (enrollementRequestList.size() != reqID.size()) {
                for (int i = 0; i < enrollementRequestList.size(); ++i) {
                    try {
                        final JSONObject resourceIDjson = ManagedDeviceHandler.getInstance().getResourceIDFromErid(Arrays.asList(enrollementRequestList.get(i)));
                        final JSONArray resID = (JSONArray)resourceIDjson.get("ResourceID");
                        if (resID.length() > 1) {
                            duplicateDevicesPresent = true;
                        }
                    }
                    catch (final Exception ex) {
                        this.logger.log(Level.WARNING, "Exception while checking for duplicate device entry in validateEnrollmentRequest", ex);
                    }
                }
                if (!duplicateDevicesPresent) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }
    
    public boolean validateEnrollmentDeviceId(final List enrollmentDeviceIdList, final Long customerId) throws DataAccessException {
        boolean isValid = true;
        Criteria criteria = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)enrollmentDeviceIdList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0);
        criteria = criteria.and(customerCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            isValid = false;
        }
        else {
            final Iterator<Row> enrollmentRequestIds = dataObject.getRows("DeviceForEnrollment");
            final List enrollementRequestList = DBUtil.getColumnValuesAsList((Iterator)enrollmentRequestIds, "ENROLLMENT_DEVICE_ID");
            if (enrollementRequestList.size() != enrollmentDeviceIdList.size()) {
                isValid = false;
            }
        }
        return isValid;
    }
}
