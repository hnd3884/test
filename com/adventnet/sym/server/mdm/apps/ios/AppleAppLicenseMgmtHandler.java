package com.adventnet.sym.server.mdm.apps.ios;

import java.util.Hashtable;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccess;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Collection;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppAssociationHandler;
import java.util.Properties;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;

public class AppleAppLicenseMgmtHandler extends AppLicenseMgmtHandler
{
    @Override
    public Properties assignLicenseForDevices(final List<Long> resourceList, final Long appGroupId, final String storeId, final Long collectionId, final Long customerId, final Long businessStoreID) {
        Properties resultProp = new Properties();
        try {
            final int appStoreID = Integer.parseInt(storeId);
            resultProp = VPPAppAssociationHandler.getInstance().associateVppApps(resourceList, appGroupId, appStoreID, collectionId, customerId, businessStoreID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while assign license for devices", e);
        }
        return resultProp;
    }
    
    @Override
    public Properties assignLicenseForDevices(final List<Long> resourceList, final Long appGroupId, final Integer storeId, final Long collectionId, final Long customerId) {
        Properties resultProp = new Properties();
        try {
            final int licensetype = this.getLicenseTypeWithMigration(appGroupId);
            if (licensetype == 1) {
                resultProp = this.assignVppLicense(resourceList, appGroupId, collectionId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while assign license for devices", e);
        }
        return resultProp;
    }
    
    private Properties assignVppLicense(final List<Long> resourceList, final Long appGroupId, final Long collectionId) throws Exception {
        final Properties resultProp = new Properties();
        this.logger.log(Level.INFO, "Inside assignVppLicense()");
        final Criteria licenseAppCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria isUsedByDCCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "IS_CODE_ASSIGNED_BY_DC"), (Object)Boolean.FALSE, 0);
        final Criteria isUsedByAppStoreCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "IS_REEDEEMED_APPSTORE_CODE"), (Object)Boolean.FALSE, 0);
        Criteria criteria = licenseAppCriteria.and(isUsedByDCCriteria);
        criteria = criteria.and(isUsedByAppStoreCriteria);
        final int availableLicenseCount = DBUtil.getRecordCount("MdLicenseCodes", "APP_LICENSE_CODE", criteria);
        final DataObject availableCodeDO = MDMUtil.getPersistence().get("MdLicenseCodes", criteria);
        final Iterator appLicenseCodeRows = availableCodeDO.getRows("MdLicenseCodes");
        final List availableCodesList = new ArrayList();
        while (appLicenseCodeRows.hasNext()) {
            final Row appLicenseRow = appLicenseCodeRows.next();
            availableCodesList.add(appLicenseRow);
        }
        final List alreadyAssignedList = this.getAlreadyLicenseAssignedRes(appGroupId, resourceList);
        final int neededLicenseCount = resourceList.size() - alreadyAssignedList.size();
        final Properties failedProp = new Properties();
        if (availableLicenseCount >= neededLicenseCount) {
            final DataObject appAssignDO = MDMUtil.getPersistence().constructDataObject();
            for (int i = 0; i < resourceList.size(); ++i) {
                final Long resourceID = resourceList.get(i);
                if (!alreadyAssignedList.contains(resourceID)) {
                    final Row assignRow = new Row("MdAppLicenseToResources");
                    assignRow.set("RESOURCE_ID", (Object)resourceID);
                    assignRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                    assignRow.set("APP_GROUP_ID", (Object)appGroupId);
                    final Row availableCodeRow = availableCodesList.get(i);
                    final Long appLicenseCode = (Long)availableCodeRow.get("LICENSE_CODE_ID");
                    assignRow.set("LICENSE_CODE_ID", (Object)appLicenseCode);
                    assignRow.set("LICENSE_CODE_STATUS", (Object)0);
                    appAssignDO.addRow(assignRow);
                    availableCodeRow.set("IS_CODE_ASSIGNED_BY_DC", (Object)true);
                    availableCodeDO.updateRow(availableCodeRow);
                }
            }
            MDMUtil.getPersistence().add(appAssignDO);
            MDMUtil.getPersistence().update(availableCodeDO);
        }
        else {
            resourceList.removeAll(alreadyAssignedList);
            for (int j = 0; j < resourceList.size(); ++j) {
                final Long resourceID2 = resourceList.get(j);
                final Properties prop = new Properties();
                ((Hashtable<String, String>)prop).put("REMARKS", "dc.db.mdm.licenseStatus.UnderLicense");
                ((Hashtable<Long, Properties>)failedProp).put(resourceID2, prop);
            }
            ((Hashtable<String, Properties>)resultProp).put("FailedProp", failedProp);
        }
        ((Hashtable<String, Properties>)resultProp).put("FailedProp", failedProp);
        return resultProp;
    }
    
    private List getAlreadyLicenseAssignedRes(final Long appGroupId, final List resourceList) throws DataAccessException {
        final List alreadyAssignedList = new ArrayList();
        final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria resourceListCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria licenseResourcecriteria = resourceListCriteria.and(appCriteria);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicenseCodes"));
        sQuery.addJoin(new Join("MdLicenseCodes", "MdAppLicenseToResources", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 2));
        sQuery.setCriteria(licenseResourcecriteria);
        sQuery.addSelectColumn(new Column("MdAppLicenseToResources", "*"));
        final DataObject alreadyAssignedDO = MDMUtil.getPersistence().get(sQuery);
        final Iterator alreadyAssignedRows = alreadyAssignedDO.getRows("MdAppLicenseToResources");
        while (alreadyAssignedRows.hasNext()) {
            final Row alreadyAssignedRow = alreadyAssignedRows.next();
            alreadyAssignedList.add(alreadyAssignedRow.get("RESOURCE_ID"));
        }
        return alreadyAssignedList;
    }
    
    public String getRedemptionCodeForResource(final Long resourceId, final Long appGroupID) {
        String redemptionCode = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicenseCodes"));
            sQuery.addJoin(new Join("MdLicenseCodes", "MdAppLicenseToResources", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 2));
            final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cri = resCriteria.and(appCriteria);
            sQuery.setCriteria(cri);
            sQuery.addSelectColumn(Column.getColumn("MdLicenseCodes", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                redemptionCode = (String)dataObject.getFirstValue("MdLicenseCodes", "APP_LICENSE_CODE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getRedemptionCodeForResource..", ex);
        }
        return redemptionCode;
    }
    
    private List getRedeemedLicenseRes(final Long appGroupId, final List resourceList) throws DataAccessException {
        final List alreadyAssignedList = new ArrayList();
        final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria resourceListCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria reedemedStatusCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "LICENSE_CODE_STATUS"), (Object)1, 0);
        final Criteria licenseResourcecriteria = resourceListCriteria.and(appCriteria).and(reedemedStatusCriteria);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicenseCodes"));
        sQuery.addJoin(new Join("MdLicenseCodes", "MdAppLicenseToResources", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 2));
        sQuery.setCriteria(licenseResourcecriteria);
        sQuery.addSelectColumn(new Column("MdAppLicenseToResources", "*"));
        final DataObject alreadyAssignedDO = MDMUtil.getPersistence().get(sQuery);
        final Iterator alreadyAssignedRows = alreadyAssignedDO.getRows("MdAppLicenseToResources");
        while (alreadyAssignedRows.hasNext()) {
            final Row alreadyAssignedRow = alreadyAssignedRows.next();
            alreadyAssignedList.add(alreadyAssignedRow.get("RESOURCE_ID"));
        }
        return alreadyAssignedList;
    }
    
    @Override
    public HashMap getAppLicenseDetails(final Long appGroupId, final Long businessStoreID) {
        HashMap appLicenseDetails = new HashMap();
        int licenseMappedCount = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdLicense"));
            final Join licenseRel = new Join("MdLicense", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
            final Join appDetailsJoin = new Join("MdLicense", "MdLicenseDetails", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
            final Join licenseCodeJoin = new Join("MdLicenseDetails", "MdLicenseCodes", new String[] { "LICENSE_DETAILS_ID" }, new String[] { "LICENSE_DETAILS_ID" }, 1);
            final Join licenseInfoCodeJoin = new Join("MdLicenseDetails", "MdAppLicenseInfo", new String[] { "LICENSE_DETAILS_ID" }, new String[] { "LICENSE_DETAILS_ID" }, 1);
            selectQuery.addJoin(licenseRel);
            selectQuery.addJoin(appDetailsJoin);
            selectQuery.addJoin(licenseCodeJoin);
            selectQuery.addJoin(licenseInfoCodeJoin);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria licDetailCri = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria licenseTypeCriteria = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)1, 0);
            selectQuery.setCriteria(licDetailCri.and(licenseTypeCriteria));
            final DataObject dObj = MDMUtil.getPersistence().get(selectQuery);
            String productName = "";
            int totalCount = 0;
            int yettoAssignedCount = 0;
            int yettoRedeemedCount = 0;
            final int refundedLicenseCount = 0;
            final int otherLicensesCount = 0;
            Boolean isDeviceAssignable = true;
            int appAssignmentType = 0;
            if (!dObj.isEmpty()) {
                final Row licecseRow = dObj.getFirstRow("MdLicense");
                final Integer licenseType = (Integer)licecseRow.get("LICENSED_TYPE");
                appLicenseDetails = new HashMap();
                appLicenseDetails.put("appLicenseType", licenseType);
                if (licenseType == 1) {
                    final Row licenseInfosRow = dObj.getFirstRow("MdAppLicenseInfo");
                    if (licenseInfosRow != null) {
                        productName = (String)licenseInfosRow.get("PRODUCT_NAME");
                    }
                    appLicenseDetails.put("productName", productName);
                    totalCount = this.getTotalLicenseCodeCount(appGroupId);
                    yettoAssignedCount = this.getLicenseCodeRemainingCount(appGroupId);
                    yettoRedeemedCount = this.getLicenseCodeYetToRedeemedCount(appGroupId);
                }
            }
            final SelectQuery vppQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            vppQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            vppQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            vppQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            vppQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            vppQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreSyncStatus", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 1));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOTAL_LICENSE"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "ASSIGNED_LICENSE_COUNT"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LICENSE_TYPE"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "*"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "*"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "REMARKS"));
            Criteria defaultCri = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            if (businessStoreID != null && businessStoreID != -1L && businessStoreID != 0L) {
                final Criteria bsCri = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                defaultCri = defaultCri.and(bsCri);
            }
            vppQuery.setCriteria(defaultCri);
            final DataObject dataObject = MDMUtil.getPersistence().get(vppQuery);
            Boolean syncFailure = false;
            appLicenseDetails = new HashMap();
            if (!dataObject.isEmpty()) {
                final Iterator rows = dataObject.getRows("MdStoreAssetToAppGroupRel");
                while (rows.hasNext()) {
                    appLicenseDetails.put("appLicenseType", 2);
                    final Row assetToAppGroupRow = rows.next();
                    final Long vppAssetID = (Long)assetToAppGroupRow.get("STORE_ASSET_ID");
                    final Row vppAssetRow = dataObject.getRow("MdVppAsset", new Criteria(new Column("MdVppAsset", "VPP_ASSET_ID"), (Object)vppAssetID, 0));
                    final Long vppTokenID = (Long)vppAssetRow.get("TOKEN_ID");
                    final Row bsStoreRow = dataObject.getRow("MdBusinessStoreToVppRel", new Criteria(new Column("MdBusinessStoreToVppRel", "TOKEN_ID"), (Object)vppTokenID, 0));
                    final Long bsID = (Long)bsStoreRow.get("BUSINESSSTORE_ID");
                    if (dataObject.size("MdBusinessStoreSyncStatus") > 0) {
                        final Row row = dataObject.getRow("MdBusinessStoreSyncStatus", new Criteria(new Column("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"), (Object)bsID, 0));
                        final String remarks = (String)row.get("REMARKS");
                        if (remarks != null && !remarks.equalsIgnoreCase("") && !remarks.equalsIgnoreCase("settingClientContext")) {
                            syncFailure = true;
                        }
                    }
                    if (assetToAppGroupRow != null) {
                        if (businessStoreID != null) {
                            appAssignmentType = (int)vppAssetRow.get("LICENSE_TYPE");
                        }
                        totalCount += (int)vppAssetRow.get("TOTAL_LICENSE");
                        yettoAssignedCount += (int)vppAssetRow.get("AVAILABLE_LICENSE_COUNT");
                        licenseMappedCount += (int)vppAssetRow.get("ASSIGNED_LICENSE_COUNT");
                        isDeviceAssignable = (Boolean)vppAssetRow.get("IS_DEVICE_ASSIGNABLE");
                    }
                }
            }
            appLicenseDetails.put("sync_failure", syncFailure);
            appLicenseDetails.put("totalCount", totalCount);
            appLicenseDetails.put("yettoAssignedCount", yettoAssignedCount);
            appLicenseDetails.put("refundedLicenseCount", refundedLicenseCount);
            appLicenseDetails.put("otherLicensesCount", otherLicensesCount);
            appLicenseDetails.put("yettoRedeemedCount", yettoRedeemedCount);
            if (appAssignmentType != 0) {
                appLicenseDetails.put("appAssignmentType", appAssignmentType);
            }
            appLicenseDetails.put("isDeviceAssignable", isDeviceAssignable);
            appLicenseDetails.put("licenseMappedCount", licenseMappedCount);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAppLicenseDetails...", ex);
        }
        this.logger.log(Level.INFO, "License details for App GroupID:{0} are: {1}", new Object[] { appGroupId, appLicenseDetails });
        return appLicenseDetails;
    }
    
    public void revokeLicenseForDevices(final String licenseCode) {
        try {
            this.logger.log(Level.INFO, "Inside revokeLicenseForDevices()");
            final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_LICENSE_CODE"), (Object)licenseCode, 0, false);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppLicenseToResources"));
            final Join appCodeJoin = new Join("MdAppLicenseToResources", "MdLicenseCodes", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 2);
            selectQuery.addJoin(appCodeJoin);
            selectQuery.setCriteria(appCriteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject alreadyAssignedDO = MDMUtil.getPersistence().get(selectQuery);
            alreadyAssignedDO.deleteRows("MdAppLicenseToResources", (Criteria)null);
            final Iterator alreadyAssignedCodeRows = alreadyAssignedDO.getRows("MdLicenseCodes");
            while (alreadyAssignedCodeRows.hasNext()) {
                final Row alreadyAssignedCodeRow = alreadyAssignedCodeRows.next();
                alreadyAssignedCodeRow.set("IS_CODE_ASSIGNED_BY_DC", (Object)false);
                alreadyAssignedDO.updateRow(alreadyAssignedCodeRow);
            }
            MDMUtil.getUserTransaction().begin();
            MDMUtil.getPersistence().update(alreadyAssignedDO);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred revokeLicenseForDevices()", exp);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex1) {
                this.logger.log(Level.SEVERE, "Exception in revokeLicenseForDevices..", ex1);
            }
        }
    }
    
    public void updateLicenseStatus(final Long resourceId, final Long appGroupId, final int licenseStatus) {
        try {
            final Criteria resCri = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appCri = new Criteria(Column.getColumn("MdAppLicenseToResources", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria criteria = resCri.and(appCri);
            final DataObject dObj = DataAccess.get("MdAppLicenseToResources", criteria);
            if (!dObj.isEmpty()) {
                final Row licRow = dObj.getFirstRow("MdAppLicenseToResources");
                licRow.set("LICENSE_CODE_STATUS", (Object)licenseStatus);
                licRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                dObj.updateRow(licRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in updateLicenseStatus...", ex);
        }
    }
    
    public JSONObject getAppLicenseResourcesForAppFromAllStores(final JSONObject bsToDeviceList, final Long profileID, final List resourceList) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileID, 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            selectQuery.setCriteria(profileCriteria.and(resourceCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MDMResourceToDeploymentConfigs");
                while (iterator.hasNext()) {
                    final Row depConfRow = iterator.next();
                    final Long deviceID = (Long)depConfRow.get("RESOURCE_ID");
                    final Long businessStoreID = (Long)depConfRow.get("BUSINESSSTORE_ID");
                    JSONArray deviceArray = bsToDeviceList.optJSONArray(String.valueOf(businessStoreID));
                    if (deviceArray == null) {
                        deviceArray = new JSONArray();
                    }
                    if (!deviceArray.toList().contains(deviceID)) {
                        deviceArray.put((Object)deviceID);
                    }
                    bsToDeviceList.put(String.valueOf(businessStoreID), (Object)deviceArray);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAppLicenseResourcesForAppFromAllStores", e);
        }
        return bsToDeviceList;
    }
    
    public int getLicenseTypeWithMigration(final Long appGroupId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdLicense"));
        final Join licenseRel = new Join("MdLicense", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        selectQuery.addJoin(licenseRel);
        final Criteria licDetailCri = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        selectQuery.setCriteria(licDetailCri);
        selectQuery.addSelectColumn(Column.getColumn("MdLicense", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
        if (!DO.isEmpty()) {
            final Row licenseRow = DO.getFirstRow("MdLicense");
            if (licenseRow != null) {
                final Boolean isMigrated = (Boolean)licenseRow.get("IS_MIGRATED");
                if (isMigrated) {
                    return 0;
                }
                return (int)licenseRow.get("LICENSED_TYPE");
            }
        }
        return 0;
    }
    
    public int getLicenseCodeRemainingCount(final Long appGroupID) {
        int remainingCount = 0;
        try {
            this.logger.log(Level.INFO, "Inside getLicenseCodeRemainingCount()");
            final Criteria licenseAppCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final Criteria isUsedByDCCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "IS_CODE_ASSIGNED_BY_DC"), (Object)Boolean.FALSE, 0);
            final Criteria isUsedByAppStoreCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "IS_REEDEEMED_APPSTORE_CODE"), (Object)Boolean.FALSE, 0);
            Criteria criteria = licenseAppCriteria.and(isUsedByDCCriteria);
            criteria = criteria.and(isUsedByAppStoreCriteria);
            remainingCount = DBUtil.getRecordCount("MdLicenseCodes", "APP_LICENSE_CODE", criteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred getLicenseCodeRemainingCount()", exp);
        }
        return remainingCount;
    }
    
    public int getLicenseCodeYetToRedeemedCount(final Long appGroupID) {
        int remainingCount = 0;
        try {
            this.logger.log(Level.INFO, "Inside getLicenseCodeYetToRedeemedCount()");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicenseCodes"));
            sQuery.addJoin(new Join("MdLicenseCodes", "MdAppLicenseToResources", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 2));
            final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final Criteria statusCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "LICENSE_CODE_STATUS"), (Object)new Integer(0), 0);
            final Criteria criteria = appCriteria.and(statusCriteria);
            sQuery.setCriteria(criteria);
            remainingCount = DBUtil.getRecordCount(sQuery, "MdAppLicenseToResources", "RESOURCE_ID");
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred getLicenseCodeYetToRedeemedCount()", exp);
        }
        return remainingCount;
    }
    
    public int getTotalLicenseCodeCount(final Long appGroupID) {
        int remainingCount = 0;
        try {
            this.logger.log(Level.INFO, "Inside getTotalLicenseCodeCount()");
            final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupID, 0);
            remainingCount = DBUtil.getRecordCount("MdLicenseCodes", "APP_LICENSE_CODE", appCriteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred getTotalLicenseCodeCount()", exp);
        }
        return remainingCount;
    }
    
    public void deleteMultipleAppLicenses(final Long[] licenseIds) {
        this.logger.log(Level.INFO, "Dummy method called...");
    }
    
    private void revokeVPPLicense(final List resourceList, final Long appGroupId) {
        try {
            this.logger.log(Level.INFO, "Inside revokeLicenseForDevices()");
            Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria statusCriteria = new Criteria(Column.getColumn("MdAppLicenseToResources", "LICENSE_CODE_STATUS"), (Object)0, 0);
            appCriteria = appCriteria.and(resourceCriteria);
            appCriteria = appCriteria.and(statusCriteria);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppLicenseToResources"));
            final Join appCodeJoin = new Join("MdAppLicenseToResources", "MdLicenseCodes", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 2);
            selectQuery.addJoin(appCodeJoin);
            selectQuery.setCriteria(appCriteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject alreadyAssignedDO = MDMUtil.getPersistence().get(selectQuery);
            alreadyAssignedDO.deleteRows("MdAppLicenseToResources", (Criteria)null);
            final Iterator alreadyAssignedCodeRows = alreadyAssignedDO.getRows("MdLicenseCodes");
            while (alreadyAssignedCodeRows.hasNext()) {
                final Row alreadyAssignedCodeRow = alreadyAssignedCodeRows.next();
                alreadyAssignedCodeRow.set("IS_CODE_ASSIGNED_BY_DC", (Object)false);
                alreadyAssignedDO.updateRow(alreadyAssignedCodeRow);
            }
            MDMUtil.getUserTransaction().begin();
            MDMUtil.getPersistence().update(alreadyAssignedDO);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred revokeLicenseForDevices()", exp);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex1) {
                this.logger.log(Level.SEVERE, "Exception in revokeVPPLicense..", ex1);
            }
        }
    }
    
    public void revokeAllAppLicensesForResource(final Long resourceID, final Long customerID) {
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        this.revokeAllAppLicenseForResources(resourceList, customerID);
    }
    
    public void revokeAllAppLicenseForResources(final List resourceList, final Long customerId) {
        this.logger.log(Level.INFO, "revokeAllAppLicenseForResources() begins...{0}", resourceList);
        try {
            this.revokeAllVppRedemptionLicenses(resourceList, customerId);
            final JSONObject platformToProfileMap = new MDMResourceToProfileDeploymentConfigHandler().getProfileDirectlyDistributedForResources(resourceList);
            final List profileList = platformToProfileMap.getJSONArray(String.valueOf(1)).toList();
            final JSONObject appToDeviceLicenseDetails = new MDMResourceToProfileDeploymentConfigHandler().getAppLicenseDetailsForResources(null, resourceList, profileList, 1);
            new IOSStoreHandler(null, customerId).addLicenseRemovalTaskToQueue(appToDeviceLicenseDetails, customerId, resourceList, Boolean.TRUE);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in revokeAllAppLicenseForResources {0}", e);
        }
    }
    
    public void revokeAllVppRedemptionLicenses(final List resourceList, final Long customerId) {
        this.logger.log(Level.INFO, "VPP: revokeAllVppRedemptionLicenses() begins...{0}", resourceList);
        try {
            final HashMap<Long, Properties> appGrpToResCollnMap = this.getAppGroupAndCollnDetailsForResList(resourceList);
            if (!appGrpToResCollnMap.isEmpty()) {
                for (final Long appGroupId : appGrpToResCollnMap.keySet()) {
                    final Properties p = appGrpToResCollnMap.get(appGroupId);
                    final Long collnId = ((Hashtable<K, Long>)p).get("COLLECTION_ID");
                    final ArrayList appSpecificResourceList = ((Hashtable<K, ArrayList>)p).get("RESOURCE_LIST");
                    this.logger.log(Level.INFO, "Revoking licenses for appGroup = {0}", new Long[] { appGroupId });
                    this.logger.log(Level.INFO, "Collection = {0}", new Long[] { collnId });
                    this.logger.log(Level.INFO, "Resources list = {0}", new String[] { appSpecificResourceList.toString() });
                    final int licenseType = this.getLicenseType(appGroupId);
                    if (licenseType == 1) {
                        this.revokeVPPLicense(resourceList, appGroupId);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in revokeAllVppRedemptionLicenses {0}", e);
        }
    }
}
