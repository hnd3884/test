package com.me.mdm.mdmmigration.source;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONException;
import com.me.mdm.api.paging.model.PagingResponse;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.server.device.api.model.MetaDataModel;
import java.util.List;
import java.util.Collection;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AFWMigrationDataFetcher
{
    private static Logger logger;
    
    public AFWResponseModel fetchAFWDetailsForMigration(final AFWRequestModel request) throws Exception {
        final AFWResponseModel response = new AFWResponseModel();
        final Long customerId = request.getCustomerId();
        final DataObject dO = this.getAFWConfigDO(customerId);
        this.validiateAFWConfigDataForMigration(dO);
        AFWMigrationDataFetcher.logger.log(Level.INFO, "Fetching AFW data for migration for customer ID {0}", customerId);
        response.setData(this.constructAFWConfigMigrationJSON(dO).toString());
        MDMEventLogHandler.getInstance().MDMEventLogEntry(72517, null, null, "mdm.migration.afwaccount.success", null, customerId);
        return response;
    }
    
    private DataObject getAFWConfigDO(final Long customerId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleESADetails"));
        final Join resJoin = new Join("GoogleESADetails", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join businessStoreJoin = new Join("GoogleESADetails", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2);
        final Criteria customerIdCrietria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        sQuery.addJoin(resJoin);
        sQuery.addJoin(businessStoreJoin);
        sQuery.setCriteria(customerIdCrietria);
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ESA_EMAIL_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "DOMAIN_ADMIN_EMAIL_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "MANAGED_DOMAIN_NAME"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ENTERPRISE_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ENTERPRISE_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ESA_CREDENTIAL_JSON_PATH"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "BUSINESSSTORE_ID", "GoogleESADetails.BUSINESSSTORE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"));
        sQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID", "ManagedBusinessStore.BUSINESSSTORE_ID"));
        sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        return MDMUtil.getPersistence().get(sQuery);
    }
    
    private void validiateAFWConfigDataForMigration(final DataObject dO) throws DataAccessException {
        if (dO.isEmpty()) {
            AFWMigrationDataFetcher.logger.log(Level.WARNING, "AFW not configured");
            throw new APIHTTPException("COM0015", new Object[] { "AFW not configured" });
        }
        final Row row = dO.getRow("GoogleESADetails");
        if (row == null) {
            AFWMigrationDataFetcher.logger.log(Level.WARNING, "AFW not configured");
            throw new APIHTTPException("COM0015", new Object[] { "AFW not configured" });
        }
        if (!((Integer)row.get("ENTERPRISE_TYPE")).equals(GoogleForWorkSettings.ENTERPRISE_TYPE_EMM)) {
            AFWMigrationDataFetcher.logger.log(Level.WARNING, "AFW with GSuite configured. Migration not supported");
            throw new APIHTTPException("COM0015", new Object[] { "AFW with GSuite is not supported for configuration" });
        }
    }
    
    private JSONObject constructAFWConfigMigrationJSON(final DataObject dO) throws Exception {
        final JSONObject response = new JSONObject();
        final Row resourceRow = dO.getRow("Resource");
        final Long resourceId = (Long)resourceRow.get("RESOURCE_ID");
        final JSONObject detailsForAResource = new JSONObject();
        final Row mdBStoreRow = dO.getRow("ManagedBusinessStore");
        detailsForAResource.put("BUSINESSSTORE_IDENTIFICATION", mdBStoreRow.get("BUSINESSSTORE_IDENTIFICATION"));
        final List<String> identifiers = MDBusinessStoreAssetUtil.getStoreAssetIdentifiersForBusinessStoreId((Long)mdBStoreRow.get("BUSINESSSTORE_ID"));
        final Row gesaDetails = dO.getRow("GoogleESADetails");
        detailsForAResource.put("DOMAIN_ADMIN_EMAIL_ID", gesaDetails.get("DOMAIN_ADMIN_EMAIL_ID"));
        detailsForAResource.put("ENTERPRISE_ID", gesaDetails.get("ENTERPRISE_ID"));
        detailsForAResource.put("ESA_EMAIL_ID", gesaDetails.get("ESA_EMAIL_ID"));
        detailsForAResource.put("MANAGED_DOMAIN_NAME", gesaDetails.get("MANAGED_DOMAIN_NAME"));
        final String credentialPath = (String)gesaDetails.get("ESA_CREDENTIAL_JSON_PATH");
        final JSONObject credentialJSON = new JSONObject(new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(credentialPath)));
        detailsForAResource.put("credential.json", (Object)credentialJSON);
        final JSONArray details = new JSONArray();
        details.put((Object)detailsForAResource);
        response.put("topic", (Object)"ManagedGooglePlay");
        response.put("data", (Object)details);
        response.put("bundle_identifier", (Collection)identifiers);
        AFWMigrationDataFetcher.logger.log(Level.INFO, "AFW data fetched for migration RESOURCE_ID {0}", resourceId);
        return response;
    }
    
    public AFWResponseModel fetchAFWUsersAndAccounts(final AFWRequestModel request) throws Exception {
        final AFWResponseModel response = new AFWResponseModel();
        final Long customerId = request.getCustomerId();
        final Long bsID = this.validateAndGetBSIdForUsers(customerId);
        final PagingUtil pagingUtil = request.getPagingUtil();
        final MetaDataModel meta = new MetaDataModel();
        final int count = this.getAFWUsersAccountCount(bsID);
        meta.setTotalCount(count);
        response.setMetadata(meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(request.getRequestUri());
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            response.setDeltaToken(newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
            if (pagingJSON != null) {
                response.setPaging(pagingJSON);
            }
        }
        AFWMigrationDataFetcher.logger.log(Level.INFO, "Going to fetch AFW user details for customerId {0}, bstoreid {1}", new Object[] { customerId, bsID });
        if (count != 0) {
            final Range range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
            final DataObject dO = this.getAFWUsersAccountsDO(bsID, range);
            response.setData(this.constructAFWUsersAndAccountsDO(dO).toString());
        }
        MDMEventLogHandler.getInstance().MDMEventLogEntry(72518, null, null, "mdm.migration.afwuser.success", null, customerId);
        return response;
    }
    
    private Long validateAndGetBSIdForUsers(final Long customerId) throws DataAccessException, JSONException {
        final JSONObject afwSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        if (!afwSettings.optBoolean("isConfigured", false)) {
            AFWMigrationDataFetcher.logger.log(Level.WARNING, "AFW not configured");
            throw new APIHTTPException("COM0015", new Object[] { "AFW not configured" });
        }
        if (afwSettings.optInt("ENTERPRISE_TYPE") != GoogleForWorkSettings.ENTERPRISE_TYPE_EMM) {
            AFWMigrationDataFetcher.logger.log(Level.WARNING, "AFW with GSuite configured. Migration not supported");
            throw new APIHTTPException("COM0015", new Object[] { "AFW with GSuite is not supported for configuration" });
        }
        return afwSettings.getLong("BUSINESSSTORE_ID");
    }
    
    private DataObject getAFWUsersAccountsDO(final Long bsID, final Range range) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreUsers"));
        final Join userDeviceJoin = new Join("BusinessStoreUsers", "BSUsersToManagedDevices", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 2);
        final Join userDeviceAccStateJoin = new Join("BSUsersToManagedDevices", "BSUserToManagedDeviceAccState", new String[] { "BS_USER_TO_DEVICE_ID" }, new String[] { "BS_USER_TO_DEVICE_ID" }, 2);
        final Join accountstatusJoin = new Join("BSUsersToManagedDevices", "AFWAccountStatus", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join managedDeviceJoin = new Join("BSUsersToManagedDevices", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        sQuery.addJoin(userDeviceJoin);
        sQuery.addJoin(userDeviceAccStateJoin);
        sQuery.addJoin(accountstatusJoin);
        sQuery.addJoin(managedDeviceJoin);
        sQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"));
        sQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"));
        sQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_TO_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID", "BusinessStoreUsers.BS_USER_ID"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "RESOURCE_ID", "AFWAccountStatus.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"));
        sQuery.addSelectColumn(Column.getColumn("BSUserToManagedDeviceAccState", "BS_USER_TO_DEVICE_ID", "BSUserToManagedDeviceAccState.BS_USER_TO_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID", "ManagedDevice.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        sQuery.setRange(range);
        sQuery.addSortColumn(new SortColumn("BusinessStoreUsers", "BS_USER_ID", true));
        final Criteria bsIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)bsID, 0);
        sQuery.setCriteria(bsIdCriteria);
        return MDMUtil.getPersistence().get(sQuery);
    }
    
    private int getAFWUsersAccountCount(final Long bsId) throws Exception {
        final SelectQuery countQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreUsers"));
        final Join userDeviceJoin = new Join("BusinessStoreUsers", "BSUsersToManagedDevices", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 2);
        final Join userDeviceAccStateJoin = new Join("BSUsersToManagedDevices", "BSUserToManagedDeviceAccState", new String[] { "BS_USER_TO_DEVICE_ID" }, new String[] { "BS_USER_TO_DEVICE_ID" }, 2);
        final Join accountstatusJoin = new Join("BSUsersToManagedDevices", "AFWAccountStatus", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join managedDeviceJoin = new Join("BSUsersToManagedDevices", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        countQuery.addJoin(userDeviceJoin);
        countQuery.addJoin(userDeviceAccStateJoin);
        countQuery.addJoin(accountstatusJoin);
        countQuery.addJoin(managedDeviceJoin);
        final Criteria bsIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)bsId, 0);
        countQuery.setCriteria(bsIdCriteria);
        countQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID").count());
        final int count = DBUtil.getRecordCount(countQuery);
        return count;
    }
    
    private JSONObject constructAFWUsersAndAccountsDO(final DataObject dO) throws Exception {
        final JSONObject response = new JSONObject();
        final JSONArray detailsForResources = new JSONArray();
        final Iterator mdDeviceItr = dO.getRows("ManagedDevice");
        while (mdDeviceItr.hasNext()) {
            final Row mdDeviceRow = mdDeviceItr.next();
            final Long resourceId = (Long)mdDeviceRow.get("RESOURCE_ID");
            final JSONObject detailsForAResource = new JSONObject();
            detailsForAResource.put("UDID", mdDeviceRow.get("UDID"));
            final Row afwAccRow = dO.getRow("AFWAccountStatus", new Criteria(Column.getColumn("AFWAccountStatus", "RESOURCE_ID"), (Object)resourceId, 0));
            detailsForAResource.put("ACCOUNT_STATUS", afwAccRow.get("ACCOUNT_STATUS"));
            final Row bsUserDeviceRow = dO.getRow("BSUsersToManagedDevices", new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
            final Long bsUserId = (Long)bsUserDeviceRow.get("BS_USER_ID");
            final Long bsUserDeviceRelId = (Long)bsUserDeviceRow.get("BS_USER_TO_DEVICE_ID");
            final Row bsUserRow = dO.getRow("BusinessStoreUsers", new Criteria(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"), (Object)bsUserId, 0));
            detailsForAResource.put("BS_STORE_ID", bsUserRow.get("BS_STORE_ID"));
            detailsForAResource.put("BS_MDM_ID", bsUserRow.get("BS_MDM_ID"));
            detailsForResources.put((Object)detailsForAResource);
        }
        AFWMigrationDataFetcher.logger.log(Level.INFO, "Fetched details for {0} devices to migrate AFW user accounts", detailsForResources.length());
        response.put("topic", (Object)"EMMUsersAndAccounts");
        response.put("data", (Object)detailsForResources);
        return response;
    }
    
    static {
        AFWMigrationDataFetcher.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
