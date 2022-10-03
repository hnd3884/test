package com.me.mdm.server.apps.android.afw;

import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONArray;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONException;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AFWAccountStatusHandler
{
    private Logger logger;
    public static final int ACCOUNT_INPROGRESS = 1;
    public static final int ACCOUNT_READY = 2;
    public static final int ACCOUNT_FAILED = 3;
    public static final int ACCOUNT_INITIATED = 4;
    public static final int ACCOUNT_NO_ACTION = 5;
    public static final String ACCOUNT_SUMMARY = "managed_account_summary";
    public static final String TOTAL_COUNT = "total_managed_account_count";
    public static final String SUCCESS_COUNT = "success_managed_account_count";
    
    public AFWAccountStatusHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void addOrUpdateAFWAccountStatus(final JSONObject dataJSON) throws JSONException {
        try {
            final Long resourceID = dataJSON.getLong("RESOURCE_ID");
            final Integer status = dataJSON.getInt("ACCOUNT_STATUS");
            final Integer errorCode = dataJSON.optInt("ERROR_CODE", -1);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.addOrUpdateAFWAccountStatus(resourceList, status, errorCode);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Unable to Change AFW Account status ", (Throwable)e);
        }
    }
    
    public void addOrUpdateAFWAccountStatus(final List resourceList, final int status, final int errorCode) throws DataAccessException {
        if (!resourceList.isEmpty()) {
            final Criteria resourceListCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject dO = MDMUtil.getPersistence().get("AFWAccountStatus", resourceListCriteria);
            for (final Object resourceObj : resourceList) {
                final Long resourceID = (Long)resourceObj;
                final Criteria resourceCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "RESOURCE_ID"), (Object)resourceID, 0);
                Row row = dO.getRow("AFWAccountStatus", resourceCriteria);
                final String remarks = new AFWAccountErrorHandler().getRemarksForErrorCode(status, errorCode, resourceID);
                if (row == null) {
                    row = new Row("AFWAccountStatus");
                    row.set("RESOURCE_ID", (Object)resourceID);
                    row.set("ACCOUNT_STATUS", (Object)status);
                    row.set("ERROR_CODE", (Object)errorCode);
                    row.set("REMARKS", (Object)remarks);
                    row.set("ACCOUNT_INITIATION_TIME", (Object)System.currentTimeMillis());
                    row.set("ATTEMPT_COUNT", (Object)0);
                    dO.addRow(row);
                }
                else {
                    row.set("ACCOUNT_STATUS", (Object)status);
                    row.set("ERROR_CODE", (Object)errorCode);
                    row.set("REMARKS", (Object)remarks);
                    if (status == 4) {
                        row.set("ATTEMPT_COUNT", (Object)((int)row.get("ATTEMPT_COUNT") + 1));
                        row.set("ACCOUNT_INITIATION_TIME", (Object)System.currentTimeMillis());
                    }
                    dO.updateRow(row);
                }
            }
            MDMUtil.getPersistence().update(dO);
            this.logger.log(Level.INFO, "Changed AFW Account status :: resource:{0} status: {1} errorcode: {2}", new Object[] { resourceList, status, errorCode });
        }
    }
    
    public JSONObject getAccountStatusDetails(final JSONArray resList) throws Exception {
        final JSONObject accountStatusDetails = new JSONObject();
        final JSONArray accountFailureList = new JSONArray();
        final JSONArray accountSuccessList = new JSONArray();
        final JSONArray accountFailureResourceList = new JSONArray();
        final JSONArray accountSuccessResourceList = new JSONArray();
        final int totalRes = resList.length();
        if (totalRes > 0) {
            final DataObject dO = MDMUtil.getPersistence().get("AFWAccountStatus", (Criteria)null);
            for (int i = 0; i < totalRes; ++i) {
                final JSONObject accFailureDetails = new JSONObject();
                final JSONObject resJSON = resList.getJSONObject(i);
                final Long resID = resJSON.getLong("MANAGED_DEVICE_ID");
                Boolean isFailure = true;
                final Criteria resCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "RESOURCE_ID"), (Object)resID, 0);
                final Row row = dO.getRow("AFWAccountStatus", resCriteria);
                int status;
                int errorCode;
                if (row == null) {
                    status = 4;
                    errorCode = -1;
                }
                else {
                    status = (int)row.get("ACCOUNT_STATUS");
                    errorCode = (int)row.get("ERROR_CODE");
                    if (status == 2) {
                        accountSuccessList.put((Object)resJSON);
                        accountSuccessResourceList.put((Object)resID);
                        isFailure = false;
                    }
                }
                if (isFailure) {
                    accFailureDetails.put("resourceID", (Object)resID);
                    accFailureDetails.put("status", status);
                    accFailureDetails.put("errorCode", errorCode);
                    accountFailureList.put((Object)accFailureDetails);
                    accountFailureResourceList.put((Object)resID);
                }
            }
        }
        accountStatusDetails.put("AccountAddedList", (Object)accountSuccessList);
        accountStatusDetails.put("AccountNotAddedList", (Object)accountFailureList);
        accountStatusDetails.put("AccountAddedResourceList", (Object)accountSuccessResourceList);
        accountStatusDetails.put("AccountNotAddedResourceList", (Object)accountFailureResourceList);
        return accountStatusDetails;
    }
    
    public void updateAFWAccountStatusForAddition(final List resourceList) throws Exception {
        new AFWAccountStatusHandler().addOrUpdateAFWAccountStatus(resourceList, 4, -1);
    }
    
    public void resetAllAfWAccountStatus(final Long customerId) throws DataAccessException {
        final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("AFWAccountStatus");
        final Join customerJoin = new Join("AFWAccountStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Criteria customerCrtieria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        dQuery.addJoin(customerJoin);
        dQuery.setCriteria(customerCrtieria);
        DataAccess.delete(dQuery);
    }
    
    public void updateUserInsertionFailure(final List resourceList) {
        try {
            this.addOrUpdateAFWAccountStatus(resourceList, 3, 74000);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in updateUserInsertionFailure", (Throwable)ex);
        }
    }
    
    public void handleAfWAccountStatusFromDevice(final JSONObject statusMsg, final Long resourceID) throws Exception {
        final JSONObject googleSettings = GoogleForWorkSettings.getAfWSettingsForResource(resourceID);
        if (googleSettings.optInt("ENTERPRISE_TYPE", 0) == GoogleForWorkSettings.ENTERPRISE_TYPE_EMM) {
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            final String strStatus = String.valueOf(statusMsg.get("Status")).toLowerCase();
            if (strStatus.contains("error") || strStatus.contains("acknowledged")) {
                final int status = strStatus.contains("error") ? 3 : 2;
                final int errorCode = statusMsg.getInt("ErrorCode");
                this.addOrUpdateAFWAccountStatus(resourceList, status, errorCode);
                if (status == 2) {
                    DeviceCommandRepository.getInstance().addDeviceScanCommand(new DeviceDetails(resourceID), null);
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
                    int appRedistributionDelay = 0;
                    final String appRedistributionDelayStr = CustomerParamsHandler.getInstance().getParameterValue("AFWAccAppRedistributionDelayTime", (long)customerId);
                    if (new GoogleApiRetryHandler().validateIfDelayRedistributionNeeded(true, customerId)) {
                        if (appRedistributionDelayStr == null) {
                            CustomerParamsHandler.getInstance().addOrUpdateParameter("AFWAccAppRedistributionDelayTime", String.valueOf(0), (long)customerId);
                        }
                        else {
                            appRedistributionDelay = Integer.parseInt(appRedistributionDelayStr);
                        }
                    }
                    new GoogleAccountChangeHandler().redistributeAppAssociation(resourceID, googleSettings.getLong("CUSTOMER_ID"), appRedistributionDelay, true);
                }
                else if (status == 3 && errorCode == 84010) {
                    this.logger.log(Level.WARNING, "AFW account expired");
                }
            }
            else {
                this.logger.log(Level.WARNING, "AFWAccountStatusUpdate message with unknown status : {0}", strStatus);
            }
        }
        else {
            this.logger.log(Level.WARNING, "AFWAccountStatusUpdate ignored since AfW settings not compatible {0}", statusMsg);
        }
    }
    
    public JSONObject getManagedAccountSummary(final Long customerId) throws DataAccessException, JSONException {
        final JSONObject accountSummary = new JSONObject();
        accountSummary.put("total_managed_account_count", this.getManagedAccountCount(customerId, null));
        accountSummary.put("success_managed_account_count", this.getManagedAccountCount(customerId, new Integer[] { 2 }));
        return accountSummary;
    }
    
    public SelectQuery getManagedAccountQuery(final Long customerId) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join mdDeviceJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join mdDeviceExtnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join afwAccountJoin = new Join("ManagedDevice", "AFWAccountStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        sQuery.addJoin(resourceJoin);
        sQuery.addJoin(mdDeviceJoin);
        sQuery.addJoin(mdDeviceExtnJoin);
        sQuery.addJoin(afwAccountJoin);
        final Criteria androidCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria managedCritiera = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria criteria = androidCriteria.and(managedCritiera).and(customerCriteria);
        sQuery.setCriteria(criteria);
        return sQuery;
    }
    
    public int getManagedAccountCount(final Long customerId, final Integer[] filterStatus) throws DataAccessException {
        try {
            SelectQuery sQuery = this.getManagedAccountQuery(customerId);
            Criteria criteria = sQuery.getCriteria();
            if (filterStatus != null) {
                final Criteria statusCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"), (Object)filterStatus, 8);
                criteria = criteria.and(statusCriteria);
            }
            sQuery.setCriteria(criteria);
            final Column countColumn = Column.getColumn("ManagedDevice", "RESOURCE_ID").count();
            sQuery.addSelectColumn(countColumn);
            sQuery = RBDAUtil.getInstance().getRBDAQuery(sQuery);
            return DBUtil.getRecordCount(sQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception when getting account summary", e);
            return -1;
        }
    }
    
    public JSONObject validateAndGetFilters(final JSONObject requestJSON) throws JSONException {
        final JSONObject filterParams = new JSONObject();
        if (requestJSON.has("msg_header") && requestJSON.getJSONObject("msg_header").has("filters")) {
            final JSONObject filters = requestJSON.getJSONObject("msg_header").getJSONObject("filters");
            if (filters.has("os_version")) {
                if (MDMStringUtils.isEmpty("os_version")) {
                    throw new APIHTTPException("COM0005", new Object[] { "Empty search value posted" });
                }
                filterParams.put("os_version", filters.get("os_version"));
            }
            if (filters.has("udid")) {
                if (MDMStringUtils.isEmpty("udid")) {
                    throw new APIHTTPException("COM0005", new Object[] { "Empty search value posted" });
                }
                filterParams.put("udid", filters.get("udid"));
            }
            if (filters.has("status")) {
                if (MDMStringUtils.isEmpty("status")) {
                    throw new APIHTTPException("COM0005", new Object[] { "Empty search value posted" });
                }
                final int status = Integer.parseInt(filters.getString("status"));
                if (status != 2 && status != 3 && status != 4 && status != 1 && status != 5) {
                    this.logger.log(Level.WARNING, "Invalid status requested");
                    throw new APIHTTPException("COM0024", new Object[] { "Invalid value for status" });
                }
                filterParams.put("status", status);
            }
            if (filters.has("resource_id")) {
                if (MDMStringUtils.isEmpty("resource_id")) {
                    throw new APIHTTPException("COM0005", new Object[] { "Empty search value posted" });
                }
                filterParams.put("resource_id", filters.get("resource_id"));
            }
            if (filters.has("device_name")) {
                if (MDMStringUtils.isEmpty("device_name")) {
                    throw new APIHTTPException("COM0005", new Object[] { "Empty search value posted" });
                }
                filterParams.put("device_name", filters.get("device_name"));
            }
        }
        return filterParams;
    }
    
    public JSONObject getAFWAccountDetails(final JSONObject requestJSON, final Long customerId) throws Exception {
        new GoogleManagedAccountHandler().validateManagedAccountActions(customerId);
        final JSONObject response = new JSONObject();
        try {
            final JSONObject filterParams = this.validateAndGetFilters(requestJSON);
            SelectQuery dataQuery = this.getAccountDetailsQuery(filterParams, customerId);
            SelectQuery countQuery = this.getAccountDetailsQuery(filterParams, customerId);
            dataQuery = this.addSelectColumnsForData(dataQuery);
            countQuery = this.addSelectColumnsForCount(countQuery);
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject metaData = new JSONObject();
            metaData.put("count", count);
            response.put("metadata", (Object)metaData);
            if (count != 0) {
                final APIUtil apiUtil = APIUtil.getNewInstance();
                final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                dataQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final DataObject dO = MDMUtil.getPersistence().get(dataQuery);
                response.put("devices", (Object)this.getAFWAccountDetails(dO));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception when getting AFW account status", e);
            throw e;
        }
        return response;
    }
    
    public JSONArray getAFWAccountDetails(final DataObject dO) throws Exception {
        final JSONArray devices = new JSONArray();
        if (!dO.isEmpty()) {
            final Iterator devicesItr = dO.getRows("MdDeviceInfo");
            while (devicesItr.hasNext()) {
                final JSONObject device = new JSONObject();
                final Row mdDeviceInfoRow = devicesItr.next();
                final Long resourceId = (Long)mdDeviceInfoRow.get("RESOURCE_ID");
                device.put("resource_id", (Object)resourceId);
                device.put("os_version", mdDeviceInfoRow.get("OS_VERSION"));
                device.put("is_device_owner", mdDeviceInfoRow.get("IS_SUPERVISED"));
                device.put("is_profile_owner", mdDeviceInfoRow.get("IS_PROFILEOWNER"));
                final Row managedDeviceInfoRow = dO.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID", "ManagedDevice.RESOURCE_ID"), (Object)resourceId, 0));
                device.put("udid", managedDeviceInfoRow.get("UDID"));
                final Row afwAccountRow = dO.getRow("AFWAccountStatus", new Criteria(Column.getColumn("AFWAccountStatus", "RESOURCE_ID", "AFWAccountStatus.RESOURCE_ID"), (Object)resourceId, 0));
                if (afwAccountRow != null) {
                    final String remarks = (String)afwAccountRow.get("REMARKS");
                    final int status = (int)afwAccountRow.get("ACCOUNT_STATUS");
                    final int errorCode = (int)afwAccountRow.get("ERROR_CODE");
                    device.put("account_status", status);
                    device.put("retry_allowed", new AFWAccountErrorHandler().getIfRetryAllowed(errorCode, status));
                    device.put("account_initiation_time", afwAccountRow.get("ACCOUNT_INITIATION_TIME"));
                    device.put("attempt_count", afwAccountRow.get("ATTEMPT_COUNT"));
                    device.put("remarks", (Object)remarks);
                    device.put("error_code", errorCode);
                    if (remarks == null || MDMStringUtils.isEmpty(remarks)) {
                        device.put("remarks", (Object)new AFWAccountErrorHandler().getRemarksForErrorCode(status, errorCode, resourceId));
                    }
                }
                else {
                    device.put("account_status", 5);
                    device.put("retry_allowed", true);
                    device.put("account_initiation_time", -1);
                    device.put("attempt_count", 0);
                    device.put("remarks", (Object)"mdm.afw.account.not_initiated");
                    device.put("error_code", -1);
                }
                final Row managedDeviceExtnRow = dO.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
                device.put("device_name", managedDeviceExtnRow.get("NAME"));
                devices.put((Object)device);
            }
        }
        return devices;
    }
    
    public SelectQuery getAccountDetailsQuery(final JSONObject filterParams, final Long customerId) throws JSONException {
        final SelectQuery sQuery = this.getManagedAccountQuery(customerId);
        Criteria criteria = sQuery.getCriteria();
        if (filterParams.has("os_version")) {
            final Integer filterOS = filterParams.getInt("os_version");
            final Criteria osCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)filterOS.toString(), 10);
            criteria = criteria.and(osCriteria);
        }
        if (filterParams.has("resource_id")) {
            final Long resourceId = filterParams.getLong("resource_id");
            final Criteria resourceC = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MDDeviceInfo.RESOURCE_ID"), (Object)resourceId, 0);
            criteria = criteria.and(resourceC);
        }
        if (filterParams.has("udid")) {
            final String udid = filterParams.getString("udid");
            final Criteria udidCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 12, false);
            criteria = criteria.and(udidCriteria);
        }
        if (filterParams.has("device_name")) {
            final String deviceName = filterParams.getString("device_name");
            final Criteria deviceNameC = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)deviceName, 12, false);
            criteria = criteria.and(deviceNameC);
        }
        if (filterParams.has("status")) {
            final Integer status = filterParams.getInt("status");
            final Criteria statusCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"), (Object)status, 0);
            criteria = criteria.and(statusCriteria);
        }
        sQuery.setCriteria(criteria);
        return sQuery;
    }
    
    public SelectQuery addSelectColumnsForData(final SelectQuery sQuery) {
        sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "Resource.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "RESOURCE_ID", "AFWAccountStatus.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ERROR_CODE"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "REMARKS"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ATTEMPT_COUNT"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ACCOUNT_INITIATION_TIME"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MDDeviceInfo.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_PROFILEOWNER"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID", "ManagedDevice.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        return sQuery;
    }
    
    public SelectQuery addSelectColumnsForCount(final SelectQuery sQuery) {
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID").count());
        return sQuery;
    }
    
    public JSONObject getAccountStatusDetailsForResource(final Long resourceId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AFWAccountStatus"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ERROR_CODE"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "RESOURCE_ID"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.setCriteria(resourceCriteria);
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            return null;
        }
        final Row row = dO.getRow("AFWAccountStatus");
        final JSONObject accountStatus = new JSONObject();
        try {
            accountStatus.put("ACCOUNT_STATUS", row.get("ACCOUNT_STATUS"));
            accountStatus.put("ERROR_CODE", row.get("ERROR_CODE"));
        }
        catch (final JSONException ex) {}
        return accountStatus;
    }
}
