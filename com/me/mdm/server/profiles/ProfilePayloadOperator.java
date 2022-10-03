package com.me.mdm.server.profiles;

import java.util.Hashtable;
import java.util.Arrays;
import java.util.Collection;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ProfilePayloadOperator
{
    private static Logger logger;
    private List<ProfilePayloadMapping> payloadMapptedTableList;
    private HashMap unConfigureMap;
    private static final HashMap<String, List<Integer>> PAYLOAD_TABLE_MAPPING;
    
    public ProfilePayloadOperator(final List<ProfilePayloadMapping> payloadMapptedTableList, final HashMap unConfigureMap) {
        this.payloadMapptedTableList = payloadMapptedTableList;
        this.unConfigureMap = unConfigureMap;
    }
    
    public void performPayloadOperation(final List idList, final Long customerID, final Long userID, final Long newCert, final Boolean isDelete, final Boolean isRedistribute) throws Exception {
        ProfilePayloadOperator.logger.log(Level.INFO, "going to perform Payload Operation with following params ids : {0}, IsDelete {1}, new Cert Ids {2}, userID {3}, customerID {4} is REdistribute {5}", new Object[] { idList, isDelete, newCert, userID, customerID, isRedistribute });
        if (idList.size() == 0) {
            return;
        }
        final SelectQuery selectQuery = this.getAssociatedProfilesSelectQuery(idList);
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        ProfilePayloadOperator.logger.log(Level.FINE, "The DO that was fetched for getting associated profiles to be modified is {0}", dataObject);
        final Iterator iterator = dataObject.getRows("ProfileToCollection");
        while (iterator.hasNext()) {
            final HashMap removeProfileCollectionMap = new HashMap();
            final HashMap redistribueProfileCollectionMap = new HashMap();
            Boolean publish = Boolean.TRUE;
            final Row profileRow = iterator.next();
            final Long collectionID = (Long)profileRow.get("COLLECTION_ID");
            final Long profileID = (Long)profileRow.get("PROFILE_ID");
            ProfilePayloadOperator.logger.log(Level.INFO, "Going to Modify the profile {0} and collection {1} ", new Object[] { profileID, collectionID });
            final JSONObject profileDetails = ProfileConfigHandler.getProfileDetailsForCloning(collectionID, customerID);
            final Long clonedCollectionID = ProfileHandler.addOrUpdateProfileCollectionDO(profileDetails);
            final DataObject clonedCollnDo = ProfileConfigHandler.cloneConfigurations(collectionID, clonedCollectionID);
            List tables = clonedCollnDo.getTableNames();
            final Iterator payloadTablesIterator = this.payloadMapptedTableList.iterator();
            final Iterator payloadIterator = clonedCollnDo.getRows("ConfigData");
            int payloadCount = 0;
            while (payloadIterator.hasNext()) {
                ++payloadCount;
                payloadIterator.next();
            }
            int payloadDeletedCount = 0;
            ProfilePayloadOperator.logger.log(Level.INFO, "number of payloads configured is {0}", payloadCount);
            while (payloadTablesIterator.hasNext()) {
                final ProfilePayloadMapping payloadMapping = payloadTablesIterator.next();
                final List<String> tableNames = payloadMapping.getTableNames();
                if (payloadMapping.modifyClonedDO) {
                    payloadMapping.modifyClonedDO(clonedCollnDo);
                    tables = clonedCollnDo.getTableNames();
                }
                for (final String tableName : tableNames) {
                    if (tables.contains(tableName)) {
                        final List<Integer> configIds = ProfilePayloadOperator.PAYLOAD_TABLE_MAPPING.get(tableName);
                        final List unConfigureConfigID = this.unConfigureMap.get(tableName);
                        for (final Integer configId : configIds) {
                            final Iterator tableItr = this.getPayloadRowForConfig(configId, clonedCollnDo, tableName, payloadMapping);
                            if (tableItr != null) {
                                int configCount = 0;
                                int deletedCount = 0;
                                while (tableItr.hasNext()) {
                                    final Row row = tableItr.next();
                                    ++configCount;
                                    if (!idList.contains(row.get(payloadMapping.getColumnName(tableName)))) {
                                        continue;
                                    }
                                    if (payloadMapping.isUnConfigurePayload() && isDelete && unConfigureConfigID.contains(configId)) {
                                        ProfilePayloadOperator.logger.log(Level.INFO, "Going to delete the configdataitem");
                                        clonedCollnDo.deleteRow(this.getConfigDataRow(clonedCollnDo, row, payloadMapping));
                                        ++deletedCount;
                                    }
                                    else if (payloadMapping.isDeleteRow()) {
                                        ProfilePayloadOperator.logger.log(Level.INFO, "Going to delete the specific row");
                                        final int count = payloadMapping.customDeleteRow(clonedCollnDo, row, configId);
                                        if (count == 0) {
                                            clonedCollnDo.deleteRow(row);
                                        }
                                        else {
                                            ++deletedCount;
                                        }
                                        redistribueProfileCollectionMap.put(profileID, clonedCollectionID);
                                    }
                                    else {
                                        ProfilePayloadOperator.logger.log(Level.INFO, "Going to unconfigure payload from payload Row {0}", row);
                                        payloadMapping.updateCertId(row, payloadMapping.getNewCertValue(newCert));
                                        clonedCollnDo.updateRow(row);
                                        redistribueProfileCollectionMap.put(profileID, clonedCollectionID);
                                    }
                                }
                                if (!payloadMapping.isUnConfigurePayload() || !isDelete || configCount != deletedCount) {
                                    continue;
                                }
                                ProfilePayloadOperator.logger.log(Level.INFO, "Going to unconfigure payload Row {0}", tableName);
                                clonedCollnDo.deleteRows("ConfigData", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configId, 0));
                                ++payloadDeletedCount;
                            }
                        }
                    }
                }
            }
            if (payloadCount == payloadDeletedCount) {
                removeProfileCollectionMap.put(profileID, collectionID);
                redistribueProfileCollectionMap.remove(profileID);
                publish = Boolean.FALSE;
            }
            else {
                redistribueProfileCollectionMap.put(profileID, clonedCollectionID);
            }
            MDMUtil.getPersistenceLite().update(clonedCollnDo);
            ProfilePayloadOperator.logger.log(Level.INFO, "The cloned DO is Updated in the database for the new collection. The new colleciton id is {0}", clonedCollectionID);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("COLLECTION_ID", (Object)clonedCollectionID);
            jsonObject.put("PROFILE_ID", (Object)profileID);
            jsonObject.put("PLATFORM_TYPE", new ProfileUtil().getPlatformType(profileID));
            jsonObject.put("APP_CONFIG", false);
            jsonObject.put("LAST_MODIFIED_BY", (Object)userID);
            jsonObject.put("PROFILE_TYPE", 1);
            if (publish) {
                ProfileConfigHandler.publishProfile(jsonObject);
                ProfilePayloadOperator.logger.log(Level.INFO, "The new collection is published ");
            }
            if (isRedistribute) {
                ProfilePayloadOperator.logger.log(Level.INFO, "Going to associate to all entities");
                final List oldCollectionIDs = new ArrayList();
                oldCollectionIDs.add(collectionID);
                final Properties associationParams = new Properties();
                ((Hashtable<String, Boolean>)associationParams).put("isAppConfig", false);
                ((Hashtable<String, Long>)associationParams).put("customerId", customerID);
                ((Hashtable<String, HashMap>)associationParams).put("profileCollectionMap", redistribueProfileCollectionMap);
                ((Hashtable<String, Long>)associationParams).put("loggedOnUser", userID);
                ((Hashtable<String, Boolean>)associationParams).put("associateToDevice", true);
                ((Hashtable<String, String>)associationParams).put("commandName", "InstallProfile");
                ((Hashtable<String, List>)associationParams).put("OldCollectionList", oldCollectionIDs);
                ProfileAssociateHandler.getInstance().associateCollectionToAllAssociatedEntities(associationParams);
                ((Hashtable<String, String>)associationParams).put("commandName", "RemoveProfile");
                ((Hashtable<String, HashMap>)associationParams).put("profileCollectionMap", removeProfileCollectionMap);
                ProfileAssociateHandler.getInstance().disassociateCollectionToAllAssociatedEntities(associationParams);
                ProfilePayloadOperator.logger.log(Level.INFO, "Association to all entities is complete");
            }
        }
    }
    
    private Iterator getPayloadRowForConfig(final int configId, final DataObject dataObject, final String tableName, final ProfilePayloadMapping mapping) throws DataAccessException {
        final Row configRow = dataObject.getRow("ConfigData", new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configId, 0));
        if (configRow == null) {
            return null;
        }
        if (mapping.joinList != null) {
            final List tableList = new ArrayList();
            tableList.add("ConfigData");
            tableList.add("ConfigDataItem");
            this.addPayloadMappingTables(tableList, mapping, tableName);
            final DataObject joinDO = dataObject.getDataObject(tableList, configRow);
            return joinDO.getRows(tableName);
        }
        final Iterator configDataItemIterator = dataObject.getRows("ConfigDataItem", configRow);
        final List configDataItemList = new ArrayList();
        while (configDataItemIterator.hasNext()) {
            final Row configDataRow = configDataItemIterator.next();
            configDataItemList.add(configDataRow.get("CONFIG_DATA_ITEM_ID"));
        }
        return dataObject.getRows(tableName, new Criteria(new Column(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemList.toArray(), 8));
    }
    
    private void addPayloadMappingTables(final List tableList, final ProfilePayloadMapping mapping, final String tableName) {
        if (mapping.joinList != null) {
            for (final Join join : mapping.joinList) {
                tableList.add(join.getReferencedTableAlias());
            }
        }
        else {
            if (mapping.cfgDataItemTable != null) {
                tableList.add(mapping.cfgDataItemTable);
            }
            if (!tableList.contains(tableName)) {
                tableList.add(tableName);
            }
        }
    }
    
    private Row getConfigDataRow(final DataObject dataObject, final Row tableRow, final ProfilePayloadMapping mapping) throws DataAccessException {
        final List tableList = new ArrayList();
        tableList.add("ConfigDataItem");
        this.addPayloadMappingTables(tableList, mapping, tableRow.getTableName());
        final DataObject tempDO = dataObject.getDataObject(tableList, tableRow);
        return tempDO.getRow("ConfigDataItem");
    }
    
    public SelectQuery getAssociatedProfilesSelectQuery(final List payloadIDs) {
        final SelectQuery subSelectQuery = this.getCertConfigSelectQuery(payloadIDs);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        final DerivedColumn derivedColumn = new DerivedColumn("CertConfigDataItems", subSelectQuery);
        selectQuery.setCriteria(new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)derivedColumn, 8));
        return selectQuery;
    }
    
    public SelectQuery getCertConfigSelectQuery(final List certificateIDs) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ConfigDataItem"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        Criteria criteria = null;
        for (final ProfilePayloadMapping payloadMapping : this.payloadMapptedTableList) {
            if (!selectQuery.getTableList().contains(Table.getTable(payloadMapping.getTableName()))) {
                payloadMapping.addCfgDataItemJoin(selectQuery, 1);
            }
            if (criteria == null) {
                criteria = payloadMapping.getCriteria(certificateIDs);
            }
            else {
                criteria = criteria.or(payloadMapping.getCriteria(certificateIDs));
            }
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    public void rePublishPayloadProfiles(final List idList, final Long customerID, final Long userID, final Boolean redistributionEnabled, final Boolean cloneCollection) throws Exception {
        ProfilePayloadOperator.logger.log(Level.INFO, "going to redpublish and redistribute  ids : {0}, customerID {1} , userID {2} , redistributionfalge {3}", new Object[] { idList, customerID, userID, redistributionEnabled });
        final List redistributionList = new ArrayList();
        if (redistributionEnabled) {
            redistributionList.addAll(idList);
        }
        this.rePublishPayloadProfiles(idList, customerID, userID, redistributionList, cloneCollection);
    }
    
    public void rePublishPayloadProfiles(final List idList, final Long customerID, final Long userID, final List redistributionList, final Boolean cloneCollection) throws Exception {
        ProfilePayloadOperator.logger.log(Level.INFO, "going to redpublish and redistribute  ids : {0}, customerID {1} , userID {2} , redistributionList {3}", new Object[] { idList, customerID, userID, redistributionList });
        if (idList.size() == 0) {
            return;
        }
        final SelectQuery selectQuery = this.getAssociatedProfilesSelectQuery(idList);
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        ProfilePayloadOperator.logger.log(Level.FINE, "The DO that was fetched for getting associated profiles to be modified is {0}", dataObject);
        final Iterator iterator = dataObject.getRows("ProfileToCollection");
        boolean redistributionEnabled = false;
        final List<Long> redistributeProfileList = new ArrayList<Long>();
        if (redistributionList.containsAll(idList)) {
            redistributionEnabled = true;
        }
        else if (!redistributionList.isEmpty() && !dataObject.isEmpty()) {
            final SelectQuery redistributeQuery = this.getAssociatedProfilesSelectQuery(idList);
            redistributeQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
            redistributeQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
            final DataObject redistributeDO = MDMUtil.getPersistenceLite().get(redistributeQuery);
            if (!redistributeDO.isEmpty()) {
                final Iterator redistributeIterator = redistributeDO.getRows("ProfileToCollection");
                while (redistributeIterator.hasNext()) {
                    final Row profileRow = redistributeIterator.next();
                    redistributeProfileList.add((Long)profileRow.get("PROFILE_ID"));
                }
            }
        }
        while (iterator.hasNext()) {
            final HashMap redistribueProfileCollectionMap = new HashMap();
            final Row profileRow2 = iterator.next();
            Long collectionID = (Long)profileRow2.get("COLLECTION_ID");
            final Long profileID = (Long)profileRow2.get("PROFILE_ID");
            ProfilePayloadOperator.logger.log(Level.INFO, "Going to Modify the profile {0} and collection {1} ", new Object[] { profileID, collectionID });
            final Long oldCollnID = collectionID;
            if (cloneCollection) {
                final JSONObject profileDetails = ProfileConfigHandler.getProfileDetailsForCloning(collectionID, customerID);
                final Long clonedCollectionID = ProfileHandler.addOrUpdateProfileCollectionDO(profileDetails);
                final DataObject clonedCollnDo = ProfileConfigHandler.cloneConfigurations(collectionID, clonedCollectionID);
                collectionID = clonedCollectionID;
            }
            redistribueProfileCollectionMap.put(profileID, collectionID);
            final List oldCollectionIDs = new ArrayList();
            oldCollectionIDs.add(oldCollnID);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("COLLECTION_ID", (Object)collectionID);
            jsonObject.put("PROFILE_ID", (Object)profileID);
            jsonObject.put("PLATFORM_TYPE", new ProfileUtil().getPlatformType(profileID));
            jsonObject.put("APP_CONFIG", false);
            jsonObject.put("LAST_MODIFIED_BY", (Object)userID);
            jsonObject.put("PROFILE_TYPE", 1);
            ProfileConfigHandler.publishProfile(jsonObject);
            ProfilePayloadOperator.logger.log(Level.INFO, "The collection is republished {0} ", collectionID);
            if (redistributionEnabled || redistributeProfileList.contains(profileID)) {
                final Properties associationParams = new Properties();
                ((Hashtable<String, Boolean>)associationParams).put("isAppConfig", false);
                ((Hashtable<String, Long>)associationParams).put("customerId", customerID);
                ((Hashtable<String, HashMap>)associationParams).put("profileCollectionMap", redistribueProfileCollectionMap);
                ((Hashtable<String, Long>)associationParams).put("loggedOnUser", userID);
                ((Hashtable<String, Boolean>)associationParams).put("associateToDevice", true);
                ((Hashtable<String, String>)associationParams).put("commandName", "InstallProfile");
                ((Hashtable<String, List>)associationParams).put("OldCollectionList", oldCollectionIDs);
                ProfilePayloadOperator.logger.log(Level.INFO, "Going to associate to all entities");
                ProfileAssociateHandler.getInstance().associateCollectionToAllAssociatedEntities(associationParams);
                ProfilePayloadOperator.logger.log(Level.INFO, "Association to all entities is complete");
            }
        }
    }
    
    static {
        ProfilePayloadOperator.logger = Logger.getLogger("MDMConfigLogger");
        PAYLOAD_TABLE_MAPPING = new HashMap<String, List<Integer>>() {
            {
                ((HashMap<String, ArrayList<Integer>>)this).put("WebClipToConfigRel", new ArrayList<Integer>(Arrays.asList(182, 560, 557, 183)));
                ((HashMap<String, ArrayList<Integer>>)this).put("ScreenPageLayoutToWebClipRel", new ArrayList<Integer>(Arrays.asList(557, 183)));
                ((HashMap<String, ArrayList<Integer>>)this).put("FolderPageLayoutWebclip", new ArrayList<Integer>(Arrays.asList(557, 183)));
                ((HashMap<String, ArrayList<Integer>>)this).put("WifiEnterprise", new ArrayList<Integer>(Arrays.asList(556, 177, 774, 605, 701)));
                ((HashMap<String, ArrayList<Integer>>)this).put("PayloadWifiEnterprise", new ArrayList<Integer>(Arrays.asList(702)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnL2TP", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 564, 609, 704)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnPPTP", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 564, 609)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnIPSec", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 564, 609)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnIKEv2", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 609)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnCisco", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 564)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnJuniperSSL", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 564)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnF5SSL", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 564, 609, 704)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnPaloAlto", new ArrayList<Integer>(Arrays.asList(564)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnCustomSSL", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756, 609)));
                ((HashMap<String, ArrayList<Integer>>)this).put("OpenVPNPolicy", new ArrayList<Integer>(Arrays.asList(704)));
                ((HashMap<String, ArrayList<Integer>>)this).put("VpnPolicyToCertificate", new ArrayList<Integer>(Arrays.asList(176, 766, 521, 756)));
                ((HashMap<String, ArrayList<Integer>>)this).put("CertificatePolicy", new ArrayList<Integer>(Arrays.asList(515, 772, 607, 555, 703)));
                ((HashMap<String, ArrayList<Integer>>)this).put("ExchangeActiveSyncPolicy", new ArrayList<Integer>(Arrays.asList(175)));
                ((HashMap<String, ArrayList<Integer>>)this).put("AndroidActiveSyncPolicy", new ArrayList<Integer>(Arrays.asList(554)));
                ((HashMap<String, ArrayList<Integer>>)this).put("EMailPolicy", new ArrayList<Integer>(Arrays.asList(174, 553)));
                ((HashMap<String, ArrayList<Integer>>)this).put("SCEPPolicy", new ArrayList<Integer>(Arrays.asList(516, 773, 606, 566)));
                ((HashMap<String, ArrayList<Integer>>)this).put("SSOToCertificateRel", new ArrayList<Integer>(Arrays.asList(520)));
                ((HashMap<String, ArrayList<Integer>>)this).put("CfgDataItemToFontRel", new ArrayList<Integer>(Arrays.asList(526, 763)));
            }
        };
    }
}
