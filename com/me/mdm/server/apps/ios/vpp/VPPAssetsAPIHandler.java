package com.me.mdm.server.apps.ios.vpp;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAssetsHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPServiceConfigHandler;
import org.json.JSONObject;
import java.util.Collection;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.ios.ContentMetaDataAppDetails;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.ArrayList;

public class VPPAssetsAPIHandler
{
    Long customerId;
    Long businessStoreID;
    String vppCountrycode;
    Integer appAssignmentType;
    ArrayList<String> existingAdamIds;
    ArrayList<String> existingVppAdamIdsInAllTokens;
    ArrayList<String> existingAdamIdsToMigrateToVPP;
    ArrayList<String> newToPkgAdamIdList;
    ArrayList<String> toBeMigratedAdamIDs;
    ArrayList<String> toBeSyncedAdamIDList;
    int noOfAppsSyncFailed;
    HashMap vppAssetMap;
    final Logger logger;
    
    public VPPAssetsAPIHandler(final Long businessStoreID, final Long customerId) {
        this.existingAdamIds = new ArrayList<String>();
        this.existingVppAdamIdsInAllTokens = new ArrayList<String>();
        this.existingAdamIdsToMigrateToVPP = new ArrayList<String>();
        this.newToPkgAdamIdList = new ArrayList<String>();
        this.toBeMigratedAdamIDs = new ArrayList<String>();
        this.toBeSyncedAdamIDList = new ArrayList<String>();
        this.vppAssetMap = new HashMap();
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
        this.customerId = customerId;
        this.businessStoreID = businessStoreID;
        this.vppCountrycode = VPPTokenDataHandler.getInstance().getVppCountryCode(businessStoreID);
        this.appAssignmentType = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(customerId, businessStoreID);
    }
    
    public HashMap getVppAssetMap() {
        return this.vppAssetMap;
    }
    
    public ArrayList<String> getToBeSyncedAdamIDList() {
        return this.toBeSyncedAdamIDList;
    }
    
    protected void updateTotalCountInDB(final Integer count) throws Exception {
        MDBusinessStoreUtil.updateTotalAppsCount(this.businessStoreID, count);
    }
    
    public static boolean checkIfError(final HashMap appDetailsMap, final Object errorKey) {
        final boolean errorStatus = appDetailsMap.containsKey(errorKey);
        return errorStatus;
    }
    
    public static boolean checkIfError(final ContentMetaDataAppDetails appDetails, final Object errorKey) {
        if (appDetails != null) {
            final boolean errorStatus = appDetails.getIsError();
            return errorStatus;
        }
        return false;
    }
    
    protected void updateSyncProgressTime() throws Exception {
        MDBusinessStoreUtil.updateCurrentSyncLastProgress(this.businessStoreID);
    }
    
    private ArrayList<String> getExistingPackageAdamIds(final ArrayList<String> adamIds) {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final SelectQuery q = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
            q.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            final Criteria c = new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)adamIds.toArray(), 8);
            final Criteria customerCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)this.customerId, 0);
            final Column packageIDColumn = Column.getColumn("MdPackage", "PACKAGE_ID");
            final Column storeIDColumn = Column.getColumn("MdPackageToAppData", "STORE_ID");
            q.addSelectColumn(packageIDColumn);
            q.addSelectColumn(storeIDColumn);
            final List groupByList = new ArrayList();
            groupByList.add(packageIDColumn);
            groupByList.add(storeIDColumn);
            final GroupByClause groupByClause = new GroupByClause(groupByList);
            q.setGroupByClause(groupByClause);
            q.setCriteria(c.and(customerCriteria));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)q);
            while (ds.next()) {
                list.add((String)ds.getValue("STORE_ID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getExistingPackageAdamIds() ", e);
        }
        return list;
    }
    
    private ArrayList<String> getExistingVppAdamIdsInAllTokens(final Object[] existingPkgAdamIds) {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria storeIdCrit = new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)existingPkgAdamIds, 8);
            final Criteria customerCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)this.customerId, 0);
            selectQuery.setCriteria(storeIdCrit.and(customerCriteria));
            final Column packageIDColumn = Column.getColumn("MdPackage", "PACKAGE_ID");
            final Column storeIDColumn = Column.getColumn("MdPackageToAppData", "STORE_ID");
            selectQuery.addSelectColumn(packageIDColumn);
            selectQuery.addSelectColumn(storeIDColumn);
            final List groupByList = new ArrayList();
            groupByList.add(packageIDColumn);
            groupByList.add(storeIDColumn);
            final GroupByClause groupByClause = new GroupByClause(groupByList);
            selectQuery.setGroupByClause(groupByClause);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                list.add((String)ds.getValue("STORE_ID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getExistingVppAdamIdsInAllTokens() ", e);
        }
        return list;
    }
    
    private void populateAppSyncList(final ArrayList<String> adamIdsList, final Boolean isFirstSync) {
        this.existingAdamIds = this.getExistingPackageAdamIds(adamIdsList);
        (this.newToPkgAdamIdList = new ArrayList<String>(adamIdsList)).removeAll(this.existingAdamIds);
        this.existingVppAdamIdsInAllTokens = this.getExistingVppAdamIdsInAllTokens(this.existingAdamIds.toArray());
        (this.existingAdamIdsToMigrateToVPP = new ArrayList<String>(this.existingAdamIds)).removeAll(this.existingVppAdamIdsInAllTokens);
        (this.toBeMigratedAdamIDs = new ArrayList<String>()).addAll(this.existingAdamIdsToMigrateToVPP);
        (this.toBeSyncedAdamIDList = new ArrayList<String>()).addAll(adamIdsList);
    }
    
    public Boolean toBeMigratedListContainsAdamID(final String adamID) {
        if (this.toBeMigratedAdamIDs.contains(adamID)) {
            return true;
        }
        return false;
    }
    
    public ArrayList<String> getToBeMigratedAdamIDs() {
        return this.toBeMigratedAdamIDs;
    }
    
    public JSONObject syncVPPAssets(final Boolean isFirstSync) {
        final JSONObject jsonObject = new JSONObject();
        try {
            VPPServiceConfigHandler.getInstance().checkAndFetchServiceUrl();
            this.vppAssetMap = VPPAssetsHandler.getInstance().getVppAssetDetailsMap(this.businessStoreID, this.customerId);
            MDBusinessStoreUtil.deleteBusinessStoreParam("storeToVppMigrationCount", this.businessStoreID);
            final Boolean isError = checkIfError(this.vppAssetMap, "error");
            if (isError) {
                final Properties prop = this.vppAssetMap.get("error");
                jsonObject.put("error", (Map)prop);
            }
            else {
                final String[] adamIdsArray = this.getAdamIdsArray(this.vppAssetMap.keySet().toArray());
                VPPAssetsHandler.getInstance().addOrUpdateVppAssets(this.vppAssetMap, adamIdsArray, this.businessStoreID, this.appAssignmentType);
                final ArrayList<String> appAdamIDList = this.excludeBooksIDS(adamIdsArray);
                this.updateTotalCountInDB(appAdamIDList.size());
                this.populateAppSyncList(appAdamIDList, isFirstSync);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in syncVPPAssets");
        }
        return jsonObject;
    }
    
    private ArrayList<String> excludeBooksIDS(final String[] adamIDs) {
        final ArrayList<String> appAdamIDsList = new ArrayList<String>();
        for (int i = 0; i < adamIDs.length; ++i) {
            final Properties properties = this.vppAssetMap.get(adamIDs[i]);
            final Integer productType = ((Hashtable<K, Integer>)properties).get("ASSET_TYPE");
            if (productType != 10) {
                appAdamIDsList.add(adamIDs[i]);
            }
        }
        return appAdamIDsList;
    }
    
    private String[] getAdamIdsArray(final Object[] intArray) {
        final String[] array = new String[intArray.length];
        for (int i = 0; i < intArray.length; ++i) {
            array[i] = intArray[i].toString();
        }
        return array;
    }
}
