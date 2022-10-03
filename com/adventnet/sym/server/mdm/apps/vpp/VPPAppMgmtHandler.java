package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import java.util.Map;
import java.util.Collection;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.Properties;
import org.json.JSONArray;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.DerivedColumn;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Logger;

public class VPPAppMgmtHandler
{
    private static VPPAppMgmtHandler vppAppmgmtHandler;
    private Logger logger;
    
    public VPPAppMgmtHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPAppMgmtHandler getInstance() {
        if (VPPAppMgmtHandler.vppAppmgmtHandler == null) {
            VPPAppMgmtHandler.vppAppmgmtHandler = new VPPAppMgmtHandler();
        }
        return VPPAppMgmtHandler.vppAppmgmtHandler;
    }
    
    public Integer getVppAppAssignmentType(final Long appGroupId) {
        Integer typeOfAssignment = 0;
        try {
            final Object appAssignableObject = DBUtil.getValueFromDB("MDAppAssignableDetails", "APP_GROUP_ID", (Object)appGroupId, "APP_ASSIGNABLE_TYPE");
            if (appAssignableObject != null) {
                typeOfAssignment = (Integer)appAssignableObject;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVppAppAssignmentType : {0}", ex);
        }
        return typeOfAssignment;
    }
    
    public Integer getVppAppAssignmentType(final String appStoreID, final Long businessStoreID) {
        Integer typeOfAssignment = 0;
        try {
            typeOfAssignment = new VPPAssetsHandler().getAssetAssignmentType(businessStoreID, appStoreID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVppAppAssignmentType : {0}", ex);
        }
        return typeOfAssignment;
    }
    
    public Integer getVppGlobalAssignmentType(final Long customerID) {
        Integer typeOfAssignment = 0;
        final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
        try {
            final Criteria cCustomerId = new Criteria(new Column("MdVPPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject dataObject = cachedPersistence.get("MdVPPTokenDetails", cCustomerId);
            if (!dataObject.isEmpty()) {
                final Row settingsRow = dataObject.getFirstRow("MdVPPTokenDetails");
                typeOfAssignment = (Integer)settingsRow.get("LICENSE_ASSIGN_TYPE");
                return typeOfAssignment;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while getting License type", e);
        }
        return typeOfAssignment;
    }
    
    public Integer getVppGlobalAssignmentType(final Long customerID, final Long businessStoreID) {
        Integer typeOfAssignment = 0;
        final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPTokenDetails"));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria businessCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(customerCriteria.and(businessCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"));
            final DataObject dataObject = cachedPersistence.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row settingsRow = dataObject.getFirstRow("MdVPPTokenDetails");
                typeOfAssignment = (Integer)settingsRow.get("LICENSE_ASSIGN_TYPE");
                return typeOfAssignment;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while getting License type", e);
        }
        return typeOfAssignment;
    }
    
    public int getVPPAccountAppsInTrash(final int platform, final Long customerID, final long businessStoreID) {
        int count = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
        selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria accountCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        selectQuery.setCriteria(customerCriteria.and(accountCriteria).and(platformCriteria).and(trashCriteria));
        final Column countcol = new Column("Profile", "PROFILE_ID", "trashCount").distinct().count();
        countcol.setColumnAlias("trashCount");
        selectQuery.addSelectColumn(countcol);
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                count = (int)ds.getValue("trashCount");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getingtrashed count", e);
        }
        return count;
    }
    
    public String getVppMigrationFailureDetailsFromFile(final Long customerId, final String migrationFailureDetailsFileName) {
        String fileContent = "";
        try {
            final String fileName = VPPAppConstants.VPP_APP_MIGRATION_FAILURE_DETAILS_FILE_PATH + customerId + File.separator + migrationFailureDetailsFileName;
            final byte[] byteArray = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(fileName);
            if (byteArray != null) {
                fileContent = new String(byteArray);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVppMigrationFailureDetailsFromFile :{0}", ex);
        }
        return fileContent;
    }
    
    public boolean isVPPAppHasError(final Long customerId, final String adamId) {
        boolean isError = false;
        try {
            final SelectQuery selectQuery = VPPAssetsHandler.getInstance().getVppAssetsQuery();
            selectQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetErrorDetails", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Criteria adamIDCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamId, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(adamIDCriteria.and(customerCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdStoreAssetErrorDetails", "*"));
            final DataObject assetErrorDo = MDMUtil.getPersistence().get(selectQuery);
            if (!assetErrorDo.isEmpty()) {
                isError = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isVPPAppHasError", ex);
        }
        return isError;
    }
    
    public void updateIsPurchasedFromPortalForNonVppApps(final Long customerID) {
        final List appGroupIDs = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria isPurchasedFromPortalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
            final Criteria nonLicensedCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)null, 0);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.setCriteria(customerCriteria.and(nonLicensedCriteria).and(isPurchasedFromPortalCriteria));
            final DataObject assetDO = MDMUtil.getPersistence().get(selectQuery);
            if (!assetDO.isEmpty()) {
                final Iterator iterator = assetDO.getRows("MdAppGroupDetails");
                while (iterator.hasNext()) {
                    final Row assetToAppGrpRow = iterator.next();
                    final Long appGroupID = (Long)assetToAppGrpRow.get("APP_GROUP_ID");
                    if (!appGroupIDs.contains(appGroupID)) {
                        appGroupIDs.add(appGroupID);
                    }
                }
            }
            if (!appGroupIDs.isEmpty()) {
                this.logger.log(Level.INFO, "These apps with appGroupIDs: {0} doesn't have licenses but have purchased from portal as true. Hence changing it", appGroupIDs);
                this.updateIsPurchasedFromPortal(appGroupIDs, false);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "updateIsPurchasedFromPortalForNonVppApps");
        }
    }
    
    public JSONObject removeVPPTokenDetails(final Long businessStoreID, final Long customerId) throws Exception {
        this.logger.log(Level.INFO, "Going to remove VPP token from MDM for customer ID {0}", customerId);
        final JSONObject response = new JSONObject();
        this.hideAllVppMessageBoxes(customerId);
        final List vppAppGroupList = this.getVPPTokenAppsNotPresentInOtherVPPTokens(businessStoreID, customerId);
        vppAppGroupList.remove(MDMAppMgmtHandler.getInstance().getIOSNativeAgentAppGroupId(customerId));
        new AppTrashModeHandler().moveAppsToTrash(vppAppGroupList, customerId);
        this.updateIsPurchasedFromPortal(vppAppGroupList, false);
        final SelectQuery selectQuery = VPPTokenDataHandler.getInstance().getVppTokenDetailsWithDEPJoin();
        selectQuery.addSelectColumn(Column.getColumn("DEPAccountDetails", "DEP_TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DEPAccountDetails", "ORG_TYPE"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
        selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "ORGANISATION_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LOCATION_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "EMAIL"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdVPPTokenDetails");
            final Row resRow = dataObject.getFirstRow("Resource");
            response.put("ORGANISATION_NAME", row.get("ORGANISATION_NAME"));
            response.put("LOCATION_NAME", row.get("LOCATION_NAME"));
            response.put("EMAIL", row.get("EMAIL"));
            int orgType = 0;
            if (dataObject.containsTable("DEPAccountDetails")) {
                final Row depRow = dataObject.getFirstRow("DEPAccountDetails");
                if (depRow != null) {
                    orgType = (int)depRow.get("ORG_TYPE");
                }
            }
            response.put("ORG_TYPE", orgType);
            dataObject.deleteRow(row);
            dataObject.deleteRow(resRow);
            MDMUtil.getPersistence().update(dataObject);
        }
        this.logger.log(Level.INFO, "Successfully removed VPP token from MDM for customer ID {0}", customerId);
        return response;
    }
    
    private List getVPPTokenAppsNotPresentInOtherVPPTokens(final Long businessStoreID, final Long customerID) {
        final List vppAppGroupList = new ArrayList();
        try {
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            final Join assetToAppGroupRelJoin = new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2);
            final Join assetToBusinessStoreRelJoin = new Join("MdVppAsset", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2);
            final Join businessStoreJoin = new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2);
            final Column businessStoreColumn = new Column("ManagedBusinessStore", "BUSINESSSTORE_ID");
            subQuery.addJoin(assetToAppGroupRelJoin);
            subQuery.addJoin(assetToBusinessStoreRelJoin);
            subQuery.addJoin(businessStoreJoin);
            subQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            subQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            final Criteria vppStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)BusinessStoreSyncConstants.BS_SERVICE_VPP, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria otherBusinessStoreCritria = new Criteria(businessStoreColumn, (Object)businessStoreID, 1);
            subQuery.setCriteria(vppStoreCriteria.and(customerCriteria).and(otherBusinessStoreCritria));
            final DerivedColumn derivedColumn = new DerivedColumn("APP_GROUP_ID", subQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            selectQuery.addJoin(assetToAppGroupRelJoin);
            selectQuery.addJoin(assetToBusinessStoreRelJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "*"));
            final Criteria businessCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria appNotInCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)derivedColumn, 9);
            selectQuery.setCriteria(businessCriteria.and(appNotInCriteria));
            final DataObject vppAppsDO = MDMUtil.getPersistence().get(selectQuery);
            if (!vppAppsDO.isEmpty()) {
                final Iterator iter = vppAppsDO.getRows("MdStoreAssetToAppGroupRel");
                while (iter.hasNext()) {
                    final Row assetToAppGrpRow = iter.next();
                    final Long appGroupID = (Long)assetToAppGrpRow.get("APP_GROUP_ID");
                    vppAppGroupList.add(appGroupID);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVPPTokenAppsNotPresentInOtherVPPTokens");
        }
        return vppAppGroupList;
    }
    
    private void regenerateInstallAppCommand(final List appGroupIds) throws DataAccessException {
        this.logger.log(Level.INFO, "Going to regenerateInstallAppCommand from MDM for appGroupIds {0}", appGroupIds);
        this.deleteVPPLicensesForTokenRemoval(appGroupIds);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        final Join mdAppGroupToCollectionJoin = new Join("AppGroupToCollection", "CollectionMetaData", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Criteria appGroupCri = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        sq.addSelectColumn(Column.getColumn("CollectionMetaData", "*"));
        sq.setCriteria(appGroupCri);
        sq.addJoin(mdAppGroupToCollectionJoin);
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        final Iterator<Row> iter = dataObject.getRows("CollectionMetaData");
        while (iter.hasNext()) {
            final Row r = iter.next();
            final Long collectionId = (Long)r.get("COLLECTION_ID");
            final String collectionFilePath = (String)r.get("COLLECTION_FILE_PATH");
            final String profileFileName = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + collectionFilePath;
            final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
            payloadHdlr.generateInstallAppProfile(collectionId, profileFileName);
        }
    }
    
    private void deleteVPPLicensesForTokenRemoval(final List appGroupIds) throws DataAccessException {
        this.logger.log(Level.INFO, "Going to deleteVPPLicensesForTokenRemoval from MDM for appGroupIds {0}", appGroupIds);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdLicense");
        final Join licenseDetails = new Join("MdLicense", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        final Criteria licenseToAppGroupRel = new Criteria(new Column("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        deleteQuery.addJoin(licenseDetails);
        deleteQuery.setCriteria(licenseToAppGroupRel);
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    public int getVppAppsCount(final Long businessStoreID) throws Exception {
        int totalVppAppInRepo = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAsset"));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(businessStoreCriteria);
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "*"));
            totalVppAppInRepo = DBUtil.getRecordCount(selectQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVPPAppsCount", ex);
        }
        return totalVppAppInRepo;
    }
    
    private void updateIsPurchasedFromPortal(final List appGroupIds, final boolean isPurchasedFromPortal) throws DataAccessException {
        this.logger.log(Level.INFO, "Going to updateIsPurchasedFromPortal from MDM for appGroupIds {0}", appGroupIds);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdPackageToAppGroup");
        final Criteria appGroupCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        updateQuery.setCriteria(appGroupCri);
        updateQuery.setUpdateColumn("IS_PURCHASED_FROM_PORTAL", (Object)isPurchasedFromPortal);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    private void deleteCustomerParamsDataForVPP(final Long customerID) throws DataAccessException {
        this.logger.log(Level.INFO, "Going to delete CustomerParamsDataForVPP from MDM for customerID {0}", customerID);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("CustomerParams");
        final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)new String[] { "VppSync_TotalCount", "Download", "VppSync_Remarks", "VppSync_FailedCount", "VppSync_CompletedCount", "IsVppAppMigrationFailed", "VPPOtherMDMClientContext" }, 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
        deleteQuery.setCriteria(criteria.and(customerCriteria));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    private void hideAllVppMessageBoxes(final Long customerId) {
        MessageProvider.getInstance().hideMessage("VPP_ABOUT_TO_EXPIRE", customerId);
        MessageProvider.getInstance().hideMessage("VPP_EXPIRED", customerId);
        MessageProvider.getInstance().hideMessage("VPP_REVOKED", customerId);
        MessageProvider.getInstance().hideMessage("VPP_USED_IN_OTHER_MDM", customerId);
    }
    
    public List getAllVppAppGroupIds(final Long customerId) throws Exception {
        final ArrayList<Long> list = new ArrayList<Long>();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdLicense"));
            final Join vppAppLicenseJoin = new Join("MdLicense", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
            final Join appGroupToCollectionJoin = new Join("MdLicenseToAppGroupRel", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join profileToCollectionJoin = new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Criteria appAssignableTypeCri = new Criteria(new Column("MdLicense", "LICENSED_TYPE"), (Object)2, 0);
            final Criteria customerIdCri = new Criteria(new Column("MdLicense", "CUSTOMER_ID"), (Object)customerId, 0);
            sq.addSelectColumn(Column.getColumn("MdLicenseToAppGroupRel", "*"));
            sq.setCriteria(appAssignableTypeCri.and(customerIdCri));
            sq.addJoin(vppAppLicenseJoin);
            sq.addJoin(appGroupToCollectionJoin);
            sq.addJoin(profileToCollectionJoin);
            sq.addJoin(profileJoin);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> licensesToAppGroup = dataObject.getRows("MdLicenseToAppGroupRel");
            while (licensesToAppGroup.hasNext()) {
                list.add((Long)licensesToAppGroup.next().get("APP_GROUP_ID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAllVppAppGroupIds ", e);
            throw e;
        }
        return list;
    }
    
    public int getVPPAppsCountNotPurchasedFromPortal(final Long customerId) {
        int nonBusinessStoreAppCount = 0;
        try {
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            subQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            subQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            subQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            subQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppAsset", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            subQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            subQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            final Criteria resCustomerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            subQuery.setCriteria(resCustomerCri);
            final DerivedColumn derivedColumn = new DerivedColumn("APP_GROUP_ID", subQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            final Criteria isNonEnterpriseApps = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
            final Criteria iOSApp = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria customerIdCri = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria isPurchasedFromPortalCri = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)derivedColumn, 9);
            selectQuery.setCriteria(isNonEnterpriseApps.and(customerIdCri).and(isPurchasedFromPortalCri).and(iOSApp).and(trashCriteria));
            final Column appGroupColumn = Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID");
            final Column countColumn = appGroupColumn.count();
            countColumn.setColumnAlias("APP_GROUP_COUNT");
            selectQuery.addSelectColumn(countColumn);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next() && ds.getValue("APP_GROUP_COUNT") != null) {
                nonBusinessStoreAppCount = (int)ds.getValue("APP_GROUP_COUNT");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVPPAppsNotPurchasedFromPortal :{0}", ex);
        }
        return nonBusinessStoreAppCount;
    }
    
    public ArrayList<String> getVPPAppsNotPurchasedFromPortal(final Long customerId) {
        final ArrayList<String> storeIDs = new ArrayList<String>();
        try {
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            subQuery.addJoin(new Join("Resource", "ManagedBusinessStore", new String[] { "RESOURCE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            subQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            subQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            subQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppAsset", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            subQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            subQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            final Criteria resCustomerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            subQuery.setCriteria(resCustomerCri);
            final DerivedColumn derivedColumn = new DerivedColumn("APP_GROUP_ID", subQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppData"));
            selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria isNonEnterpriseApps = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
            final Criteria customerIdCri = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria isPurchasedFromPortalCri = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)derivedColumn, 9);
            selectQuery.setCriteria(isNonEnterpriseApps.and(customerIdCri).and(isPurchasedFromPortalCri));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "STORE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iter = dataObject.getRows("MdPackageToAppData");
            while (iter.hasNext()) {
                final Row appRow = iter.next();
                final String storeID = (String)appRow.get("STORE_ID");
                if (!storeIDs.contains(storeID)) {
                    storeIDs.add(storeID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVPPAppsNotPurchasedFromPortal :{0}", ex);
        }
        return storeIDs;
    }
    
    public int getIOSNativeAgentLicenseType(final Long businessStoreID) {
        int appLicenseType = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LICENSE_TYPE"));
            final Criteria bundleIdCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
            final Criteria businessCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(bundleIdCriteria.and(businessCriteria));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            if (DO != null && !DO.isEmpty()) {
                final Row assetRow = DO.getFirstRow("MdVppAsset");
                appLicenseType = (int)assetRow.get("LICENSE_TYPE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppLicenseType {0}", ex);
        }
        return appLicenseType;
    }
    
    public void removeImproperLicenseForApps(final JSONObject jsonObject, final Long customerID) {
        final String appStoreId = jsonObject.getString("ADAM_ID");
        final Long assetID = jsonObject.getLong("VPP_ASSET_ID");
        final Long businessStoreID = jsonObject.getLong("BUSINESSSTORE_ID");
        final JSONArray serialNumbers = jsonObject.optJSONArray("failedSerialNumbers");
        final JSONArray userIdStrs = jsonObject.optJSONArray("failedUserIdStrs");
        List failedSerialNumbers = null;
        List failedUserIdStrs = null;
        if (serialNumbers != null) {
            failedSerialNumbers = serialNumbers.toList();
        }
        if (userIdStrs != null) {
            failedUserIdStrs = userIdStrs.toList();
        }
        try {
            if (failedSerialNumbers != null && !failedSerialNumbers.isEmpty()) {
                this.logger.log(Level.INFO, "Serial number for which the licenses are going to free up {0}", failedSerialNumbers.toString());
                final JSONObject disassociationJson = new JSONObject();
                final Object serialNumberListObj = failedSerialNumbers;
                disassociationJson.put("entityList", serialNumberListObj);
                disassociationJson.put("CUSTOMER_ID", (Object)customerID);
                disassociationJson.put("appStoreId", (Object)appStoreId);
                disassociationJson.put("notifyDisassociation", false);
                disassociationJson.put("typeOfAssignment", 2);
                final Properties associatedProp = VPPAppDisassociationHandler.getInstance().disassociateLicenseInBulk(disassociationJson, businessStoreID);
                final List successList = ((Hashtable<K, List>)associatedProp).get("Success");
                new VPPAssetsHandler().changeLicenseAvailableCountForAssets(assetID, successList.size(), "INCREMENT");
            }
            if (failedUserIdStrs != null && !failedUserIdStrs.isEmpty()) {
                this.logger.log(Level.INFO, "Vpp user ids for which the licenses are going to free up {0}", failedUserIdStrs.toString());
                final JSONObject disassociationJson = new JSONObject();
                final Object vppClientUserIdListObj = failedUserIdStrs;
                disassociationJson.put("entityList", vppClientUserIdListObj);
                disassociationJson.put("CUSTOMER_ID", (Object)customerID);
                disassociationJson.put("appStoreId", (Object)appStoreId);
                disassociationJson.put("notifyDisassociation", false);
                disassociationJson.put("typeOfAssignment", 1);
                final Properties associatedProp = VPPAppDisassociationHandler.getInstance().disassociateLicenseInBulk(disassociationJson, businessStoreID);
                final List successList = ((Hashtable<K, List>)associatedProp).get("Success");
                new VPPAssetsHandler().changeLicenseAvailableCountForAssets(assetID, successList.size(), "INCREMENT");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception in freeUpUsedLicensesForSerialNumber {0}", ex);
        }
        finally {
            VPPAssetsHandler.getInstance().updateVPPAssetSyncStatus(assetID, 0, MDMUtil.getCurrentTimeInMillis());
        }
    }
    
    public void assignLicensesForFailedDevices(final JSONObject jsonObject, final Long customerID) {
        try {
            final Long appGroupId = jsonObject.getLong("APP_GROUP_ID");
            final Long businessStoreID = jsonObject.getLong("BUSINESSSTORE_ID");
            final JSONArray tempResArray = jsonObject.optJSONArray("resListToReAssociateApps");
            final List resListToReAssociateApps = tempResArray.toList();
            final Long userID = jsonObject.getLong("userID");
            final Long collectionID = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appGroupId);
            final ArrayList collectionList = new ArrayList();
            collectionList.add(collectionID);
            final Map profileInfoFromCollectionID = new ProfileHandler().getProfileInfoFromCollectionID(collectionID);
            final Long profileID = profileInfoFromCollectionID.get("PROFILE_ID");
            final Properties profileToBusinessStore = new Properties();
            ((Hashtable<Long, Long>)profileToBusinessStore).put(profileID, businessStoreID);
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", userID);
            ((Hashtable<String, Properties>)properties).put("profileToBusinessStore", profileToBusinessStore);
            ((Hashtable<String, Boolean>)properties).put("sendEnrollmentRequest", Boolean.FALSE);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", Boolean.FALSE);
            List toSilentIntsallResourceList = new ArrayList();
            if (profileID != null) {
                toSilentIntsallResourceList = new AppDeploymentPolicyImpl().getSilentInstallDeployedResources(resListToReAssociateApps, profileID, businessStoreID);
            }
            if (!toSilentIntsallResourceList.isEmpty() && !toSilentIntsallResourceList.isEmpty()) {
                this.logger.log(Level.INFO, "In reclaimRevokedLicensesForDevice redistribute app with silent install for failure colln status {0}", toSilentIntsallResourceList.toString());
                ((Hashtable<String, List>)properties).put("resourceList", toSilentIntsallResourceList);
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", Boolean.TRUE);
                new AppFacade().updateDepPolicyAndAssociateAppToDevices(properties);
            }
            resListToReAssociateApps.removeAll(toSilentIntsallResourceList);
            if (!resListToReAssociateApps.isEmpty() && !resListToReAssociateApps.isEmpty()) {
                this.logger.log(Level.INFO, "In associateLicenseForFailedAppsToDevices redistribute app without silent install for devices without licenses {0}", resListToReAssociateApps.toString());
                ((Hashtable<String, List>)properties).put("resourceList", resListToReAssociateApps);
                new AppFacade().updateDepPolicyAndAssociateAppToDevices(properties);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in assignLicenseForDevicesWithVppApps", e);
        }
    }
    
    static {
        VPPAppMgmtHandler.vppAppmgmtHandler = null;
    }
}
