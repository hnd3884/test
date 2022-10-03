package com.me.mdm.server.apps.businessstore;

import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.apps.businessstore.model.android.AndroidStoreAppSyncDetailsModel;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class MDBusinessStoreAssetUtil
{
    public static Logger logger;
    
    public static Long addMdBusinessStoreToAssetRel(final Long businessStoreID, final String assetIdentifier) {
        Long storeAssetId = -1L;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreToAssetRel"));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria identifierCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "ASSET_IDENTIFIER"), (Object)assetIdentifier, 0, false);
            sQuery.setCriteria(businessStoreCriteria.and(identifierCriteria));
            sQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "*"));
            DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (dataObject.isEmpty()) {
                final Row storeAssetRow = new Row("MdBusinessStoreToAssetRel");
                storeAssetRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
                storeAssetRow.set("ASSET_IDENTIFIER", (Object)assetIdentifier);
                dataObject.addRow(storeAssetRow);
                dataObject = MDMUtil.getPersistence().add(dataObject);
            }
            storeAssetId = (Long)dataObject.getFirstValue("MdBusinessStoreToAssetRel", "STORE_ASSET_ID");
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in addMdBusinessStoreToAssetRel() ", ex);
        }
        return storeAssetId;
    }
    
    public static void deleteStoreAssetIds(final Long businessStoreId, final List identifiers) throws DataAccessException {
        final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreId, 0);
        final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "ASSET_IDENTIFIER"), (Object)identifiers.toArray(), 8);
        final Criteria criteria = businessStoreCriteria.and(appGroupIdCriteria);
        DataAccess.delete("MdBusinessStoreToAssetRel", criteria);
    }
    
    public static List getStoreAssetIdsForBusinessStoreId(final Long businessStoreId) {
        List storeAssetIds = new ArrayList();
        try {
            final SelectQuery storeAssetToBusinessQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreToAssetRel"));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreId, 0);
            storeAssetToBusinessQuery.setCriteria(businessStoreCriteria);
            storeAssetToBusinessQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "STORE_ASSET_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(storeAssetToBusinessQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> storeAssets = dataObject.getRows("MdBusinessStoreToAssetRel");
                storeAssetIds = DBUtil.getColumnValuesAsList((Iterator)storeAssets, "STORE_ASSET_ID");
            }
            else {
                MDBusinessStoreAssetUtil.logger.log(Level.INFO, "No apps synced for businessStoreId : {0}", businessStoreId);
            }
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in addMDStoreAssetToAppGroupRel() ", ex);
        }
        return storeAssetIds;
    }
    
    public static List getStoreAssetIdentifiersForBusinessStoreId(final Long businessStoreId) {
        List storeAssetIdentifiers = new ArrayList();
        try {
            final SelectQuery storeAssetToBusinessQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreToAssetRel"));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreId, 0);
            storeAssetToBusinessQuery.setCriteria(businessStoreCriteria);
            storeAssetToBusinessQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "STORE_ASSET_ID"));
            storeAssetToBusinessQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "ASSET_IDENTIFIER"));
            final DataObject dataObject = MDMUtil.getPersistence().get(storeAssetToBusinessQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> storeAssets = dataObject.getRows("MdBusinessStoreToAssetRel");
                storeAssetIdentifiers = DBUtil.getColumnValuesAsList((Iterator)storeAssets, "ASSET_IDENTIFIER");
            }
            else {
                MDBusinessStoreAssetUtil.logger.log(Level.INFO, "No apps synced for businessStoreId : {0}", businessStoreId);
            }
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in addMDStoreAssetToAppGroupRel() ", ex);
        }
        return storeAssetIdentifiers;
    }
    
    public static void removeStoreAssetsFromMdBusinessStoreToAssetRel(final Long businessStoreID, final List appGroupIds) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdBusinessStoreToAssetRel");
            deleteQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "MdStoreAssetToAppGroupRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
            deleteQuery.setCriteria(businessStoreCriteria.and(appGroupIdCriteria));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in removeStoreAssetsFromMdBusinessStoreToAssetRel() ", ex);
        }
    }
    
    public static void addOrUpdateMdStoreAssetErrorDetails(final Long storeAssetId, final Integer errorCode, final String remarks, final String remarksParams) {
        try {
            final SelectQuery assetErrorQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetErrorDetails"));
            final Criteria storeAssetIdCriteria = new Criteria(Column.getColumn("MdStoreAssetErrorDetails", "STORE_ASSET_ID"), (Object)storeAssetId, 0);
            assetErrorQuery.setCriteria(storeAssetIdCriteria);
            assetErrorQuery.addSelectColumn(Column.getColumn("MdStoreAssetErrorDetails", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(assetErrorQuery);
            Row errorRow = null;
            if (dataObject.isEmpty()) {
                errorRow = new Row("MdStoreAssetErrorDetails");
                errorRow.set("STORE_ASSET_ID", (Object)storeAssetId);
            }
            else {
                errorRow = dataObject.getFirstRow("MdStoreAssetErrorDetails");
            }
            errorRow.set("ERROR_CODE", (Object)errorCode);
            errorRow.set("REMARKS", (Object)remarks);
            errorRow.set("REMARKS_PARAMS", (Object)remarksParams);
            errorRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            if (dataObject.isEmpty()) {
                dataObject.addRow(errorRow);
                MDBusinessStoreAssetUtil.logger.log(Level.INFO, "Row added: ", new Object[] { errorRow });
            }
            else {
                dataObject.updateRow(errorRow);
                MDBusinessStoreAssetUtil.logger.log(Level.INFO, "Row updated: ", new Object[] { errorRow });
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateMdStoreAssetErrorDetails() ", ex);
        }
    }
    
    public static void resetMdStoreAssetErrorDetails(final Long businessStoreID) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdStoreAssetErrorDetails");
            deleteQuery.addJoin(new Join("MdStoreAssetErrorDetails", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            deleteQuery.setCriteria(businessStoreCriteria);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in resetMdStoreAssetErrorDetails() ", ex);
        }
    }
    
    public static void addMDStoreAssetToAppGroupRel(final Long storeAssetId, final Long appGroupId) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            final Criteria storeAssetIdCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "STORE_ASSET_ID"), (Object)storeAssetId, 0);
            final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            sQuery.setCriteria(storeAssetIdCriteria.and(appGroupIdCriteria));
            sQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (dataObject.isEmpty()) {
                final Row row = new Row("MdStoreAssetToAppGroupRel");
                row.set("STORE_ASSET_ID", (Object)storeAssetId);
                row.set("APP_GROUP_ID", (Object)appGroupId);
                dataObject.addRow(row);
                MDMUtil.getPersistence().add(dataObject);
            }
        }
        catch (final Exception ex) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in addMDStoreAssetToAppGroupRel() ", ex);
        }
    }
    
    public static List getBusinessStoresWithApp(final Long appGroupID) {
        final List businessStoreList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "*"));
            final DataObject assetBSRelDo = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = assetBSRelDo.getRows("MdBusinessStoreToAssetRel");
            while (iterator.hasNext()) {
                final Row assetBSRelRow = iterator.next();
                final Long businessStoreID = (Long)assetBSRelRow.get("BUSINESSSTORE_ID");
                if (!businessStoreList.contains(businessStoreID)) {
                    businessStoreList.add(businessStoreID);
                }
            }
        }
        catch (final Exception e) {
            MDBusinessStoreAssetUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoresWithApp", e);
        }
        return businessStoreList;
    }
    
    public static String getSyncAppFailureRemarks(final int errorCode, final String appName, final Long userId, final AndroidStoreAppSyncDetailsModel androidStoreAppSyncDetailsModel) throws Exception {
        final String supportMsg = I18N.getMsg("mdm.appmgmt.afw.app_sync_failed_contact_support", new Object[] { appName });
        String remarks;
        if (errorCode == 2000 && androidStoreAppSyncDetailsModel.getPackageId() != null && androidStoreAppSyncDetailsModel.getReleaseLabelId() != null) {
            final String errorMsg = "mdm.appmgmt.afw.nonprod_releaselabel_present";
            final String packageId = String.valueOf(androidStoreAppSyncDetailsModel.getPackageId());
            final String releaseLabelId = String.valueOf(androidStoreAppSyncDetailsModel.getReleaseLabelId());
            final String link = "/webclient#/uems/mdm/manage/appRepo/apps/versionList/" + packageId + "?labelId=" + releaseLabelId + "&trash=false";
            remarks = errorMsg + "@@@<l>" + link;
        }
        else if (errorCode == 2002) {
            remarks = "mdm.appmgmt.afw.insufficient_app_details@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(supportMsg);
        }
        else {
            remarks = "mdm.android.appmgmt.unknown_error@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(supportMsg);
        }
        remarks = MDMI18N.getMsg(remarks, false, false);
        return remarks;
    }
    
    static {
        MDBusinessStoreAssetUtil.logger = Logger.getLogger("MDMBStoreLogger");
    }
}
