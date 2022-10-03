package com.me.mdm.server.apps.businessstore;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import org.json.JSONException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import org.json.JSONArray;
import java.util.Properties;
import java.util.List;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import java.util.logging.Logger;

public class BaseStoreHandler implements StoreInterface
{
    protected int platformType;
    protected int serviceType;
    protected Long businessStoreID;
    protected Long customerID;
    protected String storeSyncKey;
    private static Logger logger;
    
    public BaseStoreHandler(final Long businessStoreID, final Long customerID) {
        this.businessStoreID = businessStoreID;
        this.customerID = customerID;
    }
    
    public BaseStoreHandler() {
    }
    
    @Override
    public void modifyStoreDetails(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONObject getStorePromoStatus(final JSONObject jsonObject) throws Exception {
        return new JSONObject().put("promo", false);
    }
    
    @Override
    public JSONObject syncStore(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public Object getSyncStoreStatus(final JSONObject jsonObject) throws Exception {
        jsonObject.put("trashCount", new AppTrashModeHandler().getAccountAppsInTrash(this.platformType, this.customerID));
        return jsonObject;
    }
    
    @Override
    public Object getAppsFailureDetails(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public void addLicenseRemovalTaskToQueue(final JSONObject appToDeviceLicenseDetails, final Long customerID, final List configSourceList, final boolean isAllSourceDisassociation) throws Exception {
    }
    
    @Override
    public String getBusinessStoreName(final Long businessStoreID) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getLicenseSyncStatus(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public void validateAppToBusinessStoreProps(final List profileList, final Properties pkgToBusinessStoreProps, final Properties appToBusinessProps) throws Exception {
    }
    
    @Override
    public JSONObject getAllStoreSyncStatus(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONArray getAllStoreSyncStatus() throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONObject verifyAccountRemoval() throws Exception {
        final List<Long> accountApps = MDMAppMgmtHandler.getInstance().getAccountApps(this.platformType, this.customerID, Boolean.FALSE);
        final int count = accountApps.size();
        JSONObject responseJSON = new JSONObject();
        try {
            if (count > 0) {
                final Long[] accountAppsArray = accountApps.toArray(new Long[0]);
                final Long[] profileIds = AppsUtil.getInstance().getProfileIDSFromAppGroup(accountAppsArray);
                final HashMap params = new HashMap();
                params.put("appGroupIds", accountApps);
                params.put("CustomerID", this.customerID);
                params.put("profileIds", Arrays.asList(profileIds));
                params.put("packageIds", new ArrayList());
                final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
                final JSONObject errorMessages = appTrashModeHandler.checkMoveAppsToTrashFesability(params);
                responseJSON = MDMAppMgmtHandler.getInstance().getMaxDistributedCountForAccountApps(accountAppsArray);
                if (errorMessages != null && errorMessages.has("ErrorMessage")) {
                    responseJSON.put("error_message", errorMessages.get("ErrorMessage"));
                }
            }
        }
        catch (final Exception e) {
            BaseStoreHandler.logger.log(Level.SEVERE, "Exception while fetching verification msg for account removal", e);
        }
        return responseJSON;
    }
    
    @Override
    public JSONObject syncLicenseStatus(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject addStoreDetails(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject removeStoreDetails(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public Object getStoreDetails(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getAllStoreDetails() throws Exception {
        return null;
    }
    
    @Override
    public void syncLicense(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    public void validateIfStoreFound() throws APIHTTPException {
        try {
            final SelectQuery businessQuery = MDBusinessStoreUtil.getBusinessStoreQuery();
            businessQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0);
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0);
            businessQuery.setCriteria(businessStoreCriteria.and(customerCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(businessQuery);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { this.businessStoreID });
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AppsUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getBusinessStoreDetails() throws DataAccessException, JSONException {
        final Table table = new Table("ManagedBusinessStore");
        final Join resJoin = new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        selectQuery.addJoin(resJoin);
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        final Criteria serviceCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)this.serviceType, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0);
        selectQuery.setCriteria(serviceCriteria.and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject response = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("ManagedBusinessStore");
            response.put("BUSINESSSTORE_ID", row.get("BUSINESSSTORE_ID"));
            response.put("BUSINESSSTORE_IDENTIFICATION", row.get("BUSINESSSTORE_IDENTIFICATION"));
        }
        else {
            response.put("Error", (Object)"No rows retrived");
        }
        return response;
    }
    
    @Override
    public void clearSyncStoreStatus() throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    public JSONObject getBusinessStoreDetails(final String uID, final boolean ifCustomerEqual) throws DataAccessException, JSONException {
        final JSONObject response = new JSONObject();
        try {
            final SelectQuery selectQuery = MDBusinessStoreUtil.getBusinessStoreQuery();
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"));
            final Criteria serviceCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)this.serviceType, 0);
            final Criteria identifierCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"), (Object)uID, 0);
            Criteria customerCriteria = null;
            if (ifCustomerEqual) {
                customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0);
            }
            else {
                customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 1);
            }
            selectQuery.setCriteria(serviceCriteria.and(customerCriteria).and(identifierCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ManagedBusinessStore");
                response.put("BUSINESSSTORE_ID", row.get("BUSINESSSTORE_ID"));
                response.put("BUSINESSSTORE_IDENTIFICATION", row.get("BUSINESSSTORE_IDENTIFICATION"));
            }
            else {
                response.put("Error", (Object)"No rows retrived");
            }
        }
        catch (final Exception e) {
            BaseStoreHandler.logger.log(Level.SEVERE, "Exception in getBusinessStoreDetails", e);
        }
        return response;
    }
    
    public JSONObject getBusinessStoreDetails(final String uID) throws DataAccessException {
        return this.getBusinessStoreDetails(uID, Boolean.TRUE);
    }
    
    protected Properties createResourceProps(final String bsIdentifier, final String bsIdentifierForResource) throws DataAccessException {
        final Properties resourceProp = new Properties();
        try {
            ((Hashtable<String, Long>)resourceProp).put("CUSTOMER_ID", this.customerID);
            ((Hashtable<String, String>)resourceProp).put("NAME", bsIdentifier);
            ((Hashtable<String, String>)resourceProp).put("DOMAIN_NETBIOS_NAME", bsIdentifierForResource + getServiceTypeSuffix(this.serviceType));
            ((Hashtable<String, String>)resourceProp).put("RESOURCE_TYPE", String.valueOf(1201));
            final SelectQuery resQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Criteria nameCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)bsIdentifier, 0);
            final Criteria resTypeCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)String.valueOf(1201), 0);
            final Criteria domNetCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)(bsIdentifierForResource + getServiceTypeSuffix(this.serviceType)), 0);
            resQuery.setCriteria(nameCriteria.and(resTypeCriteria).and(domNetCri));
            resQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            final DataObject resDO = MDMUtil.getPersistence().get(resQuery);
            if (resDO != null && !resDO.isEmpty()) {
                final Row resRow = resDO.getFirstRow("Resource");
                final Long addedTime = (Long)resRow.get("DB_ADDED_TIME");
                if (addedTime.equals(0L)) {
                    ((Hashtable<String, Long>)resourceProp).put("DB_ADDED_TIME", MDMUtil.getCurrentTimeInMillis());
                }
                else {
                    ((Hashtable<String, Long>)resourceProp).put("DB_ADDED_TIME", addedTime);
                }
            }
        }
        catch (final Exception e) {
            BaseStoreHandler.logger.log(Level.SEVERE, "Exception in setResourceProps", e);
        }
        return resourceProp;
    }
    
    public Long addOrUpdateManagedStore(final String bsIdentifier, final Long userID) throws DataAccessException, JSONException, SyMException {
        try {
            String bsIdentifierForResource = null;
            if (bsIdentifier.trim().length() > 47) {
                bsIdentifierForResource = bsIdentifier.substring(0, 45).trim();
            }
            else {
                bsIdentifierForResource = bsIdentifier;
            }
            final Properties resourceProp = this.createResourceProps(bsIdentifier, bsIdentifierForResource);
            final DataObject resourceDO = MDMResourceDataPopulator.addOrUpdateMDMResource(resourceProp);
            final Long resourceId = (Long)resourceDO.getFirstValue("Resource", "RESOURCE_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedBusinessStore"));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria resourceCriteria = new Criteria(new Column("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)resourceId, 0);
            selectQuery.addSelectColumn(new Column("ManagedBusinessStore", "*"));
            selectQuery.setCriteria(resourceCriteria);
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            Boolean isAdded = true;
            Row row;
            if (dO.isEmpty()) {
                row = new Row("ManagedBusinessStore");
                row.set("BUSINESSSTORE_ID", (Object)resourceId);
                row.set("BS_SERVICE_TYPE", (Object)this.serviceType);
                if (userID != null) {
                    row.set("BUSINESSSTORE_ADDED_BY", (Object)userID);
                }
            }
            else {
                row = dO.getFirstRow("ManagedBusinessStore");
                isAdded = false;
            }
            row.set("BUSINESSSTORE_IDENTIFICATION", (Object)bsIdentifier);
            if (userID != null) {
                row.set("LAST_MODIFIED_BY", (Object)userID);
            }
            if (isAdded) {
                dO.addRow(row);
                MDMUtil.getPersistence().add(dO);
            }
            else {
                dO.updateRow(row);
                MDMUtil.getPersistence().update(dO);
            }
            BaseStoreHandler.logger.log(Level.INFO, "Managed Business Store {0} of type {1} for customer Id {2} Added Successfully :: BUSINESSSTORE_ID = {3}", new Object[] { bsIdentifier, this.serviceType, this.customerID, resourceId });
            return resourceId;
        }
        catch (final DataAccessException e) {
            BaseStoreHandler.logger.log(Level.WARNING, "Unable to add Business Store Users", (Throwable)e);
            throw e;
        }
    }
    
    protected static String getServiceTypeSuffix(final Integer serviceType) {
        String suffix = "";
        if (serviceType.equals(BusinessStoreSyncConstants.BS_SERVICE_AFW)) {
            suffix = "_pfw";
        }
        else if (serviceType.equals(BusinessStoreSyncConstants.BS_SERVICE_WBS)) {
            suffix = "_wbs";
        }
        else if (serviceType.equals(BusinessStoreSyncConstants.BS_SERVICE_VPP)) {
            suffix = "_vpp";
        }
        return suffix;
    }
    
    @Override
    public void updateStoreSyncKey() throws Exception {
        CustomerParamsHandler.getInstance().addOrUpdateParameter(this.storeSyncKey, "true", (long)this.customerID);
    }
    
    static {
        BaseStoreHandler.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
