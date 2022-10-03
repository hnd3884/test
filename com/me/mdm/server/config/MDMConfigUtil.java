package com.me.mdm.server.config;

import org.json.simple.JSONArray;
import java.util.Arrays;
import java.util.HashMap;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.config.MDMConfigQueryUtil;
import com.adventnet.sym.server.mdm.config.MDMConfigQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.List;
import com.me.devicemanagement.framework.server.config.ConfigUtil;

public class MDMConfigUtil extends ConfigUtil
{
    public static List getConfigurations(final Long collectionId) throws SyMException {
        MDMConfigUtil.logger.log(Level.INFO, "getConfigurations() invoked with collection id: {0}", collectionId);
        final List configList = new ArrayList();
        try {
            final String relTblName = "CfgDataToCollection";
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
            final Column col = Column.getColumn(relTblName, "COLLECTION_ID");
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            selectQuery.setCriteria(criteria);
            final SortColumn sortColumn = new SortColumn("CfgDataToCollection", "ORDER_OF_EXECUTION", true);
            selectQuery.addSortColumn(sortColumn);
            final DataObject collRelDO = MDMUtil.getPersistence().get(selectQuery);
            if (!collRelDO.isEmpty()) {
                final Iterator rows = collRelDO.getRows(relTblName);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Long cfgDataId = (Long)row.get("CONFIG_DATA_ID");
                    configList.add(getConfigurationDO(cfgDataId));
                }
            }
        }
        catch (final DataAccessException ex) {
            MDMConfigUtil.logger.log(Level.SEVERE, (Throwable)ex, () -> "Error while retrieving configurations for collection id: " + n);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            MDMConfigUtil.logger.log(Level.SEVERE, ex2, () -> "Error while retrieving configurations for collection id: " + n2);
            throw new SyMException(1001, (Throwable)ex2);
        }
        MDMConfigUtil.logger.log(Level.FINEST, "Returning ConfigData list for given collectionId: {0} is: {1}", new Object[] { collectionId, configList });
        return configList;
    }
    
    public static DataObject getConfigurationDO(final Long configDataId) throws SyMException {
        final SelectQuery query = null;
        try {
            final Integer configType = (Integer)DBUtil.getValueFromDB("ConfigData", "CONFIG_DATA_ID", (Object)configDataId, "CONFIG_ID");
            final Column col = Column.getColumn("ConfigData", "CONFIG_DATA_ID");
            final Criteria criteria = new Criteria(col, (Object)configDataId, 0);
            final List<Integer> configList = new ArrayList<Integer>();
            configList.add(configType);
            final MDMConfigQuery configQueryObject = new MDMConfigQuery(configList, criteria);
            MDMConfigUtil.logger.log(Level.FINEST, "fetching configuration with config data id: {0}", new Object[] { configDataId });
            final DataObject resultDO = MDMConfigQueryUtil.getConfigDataObject(configQueryObject);
            return resultDO;
        }
        catch (final Exception ex) {
            MDMConfigUtil.logger.log(Level.WARNING, ex, () -> "Caught Exception while retrieving ConfigData for given configDataId: " + n + " query used: " + selectQuery);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public static DataObject getConfigDataItemsDO(final int configID, final Long configDataItemID) throws SyMException {
        try {
            final List<Integer> configList = new ArrayList<Integer>();
            configList.add(configID);
            final List<Join> joinList = new ArrayList<Join>();
            joinList.add(new Join("ConfigDataItem", "MdConfigDataItemExtn", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            final Column col = Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID");
            final Criteria criteria = new Criteria(col, (Object)configDataItemID, 0);
            final MDMConfigQuery configQueryObject = new MDMConfigQuery(configList, criteria);
            configQueryObject.setConfigJoins(joinList);
            MDMConfigUtil.logger.log(Level.FINEST, "fetching configuration with config data item id: {0}", new Object[] { configDataItemID });
            final DataObject resultDO = MDMConfigQueryUtil.getConfigDataObject(configQueryObject);
            return resultDO;
        }
        catch (final Exception ex) {
            MDMConfigUtil.logger.log(Level.WARNING, ex, () -> "Caught Exception while retrieving ConfigData for given configDataId: " + n);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public static JSONObject getConfiuguredPolicyInfo(final Long collectionID) {
        final JSONObject configurations = new JSONObject();
        try {
            final Criteria collectionIDCri = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConfigData"));
            final Join configDataJoin = new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
            sQuery.addSelectColumn(Column.getColumn("ConfigData", "*"));
            sQuery.addJoin(configDataJoin);
            sQuery.setCriteria(collectionIDCri);
            final DataObject configDO = MDMUtil.getPersistence().get(sQuery);
            if (configDO != null && !configDO.isEmpty()) {
                final Iterator configRows = configDO.getRows("ConfigData");
                while (configRows.hasNext()) {
                    final Row row = configRows.next();
                    configurations.put(row.get("CONFIG_ID") + "", (Object)row.get("LABEL"));
                }
            }
        }
        catch (final Exception ex) {
            MDMConfigUtil.logger.log(Level.WARNING, "Exception occurred whiled getConfiuguredPolicyInfo ConfigurationID : {0}.", collectionID);
            MDMConfigUtil.logger.log(Level.WARNING, "Error configuration policy data : {0}.", configurations);
        }
        return configurations;
    }
    
    public Long getCollectionIdForItemId(final Long configDataItemId) {
        long collectionId = -1L;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
            sQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            final Row collectionRow = DO.getFirstRow("CfgDataToCollection");
            collectionId = (long)collectionRow.get("COLLECTION_ID");
        }
        catch (final Exception e) {
            MDMConfigUtil.logger.log(Level.SEVERE, "Error getCollectionIdForItemId", e);
        }
        return collectionId;
    }
    
    public static String getConfigLabel(final int configID) throws SyMException {
        String configLabel = null;
        try {
            final String baseTableName = "ConfigurationParams";
            final String configurationTable = "Configuration";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, configurationTable, new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 2));
            query.addSelectColumn(new Column((String)null, "*"));
            final Column col = Column.getColumn(configurationTable, "CONFIG_ID");
            final Criteria criteria = new Criteria(col, (Object)configID, 0);
            query.setCriteria(criteria);
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty() && resultDO.containsTable(configurationTable)) {
                configLabel = (String)resultDO.getFirstValue(configurationTable, "CONFIG_NAME");
            }
        }
        catch (final DataAccessException ex) {
            MDMConfigUtil.logger.log(Level.WARNING, (Throwable)ex, () -> "Exception while getting config Label for the given config id :" + n);
            throw new SyMException(1001, "Exception while getting config Label for the given config id", (Throwable)ex);
        }
        return configLabel;
    }
    
    public static Set<Integer> getConfigIdsForCollection(final Long collectionID) {
        final Set<Integer> configIDList = new HashSet<Integer>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Criteria criteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(new Column("ConfigData", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (dataObject.isEmpty()) {
                return configIDList;
            }
            final Iterator<Row> iterator = dataObject.getRows("ConfigData");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                configIDList.add((Integer)row.get("CONFIG_ID"));
            }
        }
        catch (final Exception e) {
            MDMConfigUtil.logger.log(Level.SEVERE, "Error while getting ConfigIdList From Collection", e);
        }
        return configIDList;
    }
    
    public static Long getConfigDataItemIDForCollection(final int configID, final Long collectionID) {
        long configDataItemID = -1L;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Criteria configIDCri = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configID, 0);
            final Criteria colledtionIDCri = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            sQuery.setCriteria(configIDCri.and(colledtionIDCri));
            sQuery.addSelectColumn(new Column("ConfigDataItem", "*"));
            final String abc = RelationalAPI.getInstance().getSelectSQL((Query)sQuery);
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            final Row cfgDataItemRow = DO.getFirstRow("ConfigDataItem");
            configDataItemID = (long)cfgDataItemRow.get("CONFIG_DATA_ITEM_ID");
        }
        catch (final Exception e) {
            MDMConfigUtil.logger.log(Level.SEVERE, "Error getConfigDataItemIDForCollection", e);
        }
        return configDataItemID;
    }
    
    public DataObject getPolicyConfiguredOnPreviousVersionOfCollection(final Long collectionId) {
        DataObject dataObject = null;
        final HashMap profileDetails = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionId);
        final Long profileId = profileDetails.get("PROFILE_ID");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            query.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            query.addJoin(new Join("ProfileToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Criteria notCriteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 1);
            final Criteria profileCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria finalCriteria = notCriteria.and(profileCriteria);
            query.setCriteria(finalCriteria);
            final SortColumn sortColmn = new SortColumn(new Column("Collection", "COLLECTION_ID"), false);
            query.addSortColumn(sortColmn);
            query.addSelectColumn(new Column((String)null, "*"));
            dataObject = MDMUtil.getPersistence().get(query);
        }
        catch (final Exception ex) {
            MDMConfigUtil.logger.log(Level.SEVERE, "Exception while getting the policy configured for collection", ex);
        }
        return dataObject;
    }
    
    public static DataObject getConfigDataItemDOByCollectionId(final int configID, final Long collectionId) throws SyMException {
        try {
            final List<Integer> configList = new ArrayList<Integer>();
            configList.add(configID);
            final Column collectionIdColumn = Column.getColumn("CfgDataToCollection", "COLLECTION_ID");
            final Column configIdColumn = Column.getColumn("ConfigData", "CONFIG_ID");
            Criteria criteria = new Criteria(configIdColumn, (Object)configID, 0);
            criteria = criteria.and(new Criteria(collectionIdColumn, (Object)collectionId, 0));
            final SortColumn sortColumn2 = new SortColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID", true);
            final MDMConfigQuery configQuery = new MDMConfigQuery(configList, criteria);
            configQuery.setConfigSortColumn(sortColumn2);
            MDMConfigUtil.logger.log(Level.FINEST, "for fetching configuration with collection id: {0} and config id: {1}", new Object[] { collectionId, configID });
            final DataObject resultDO = MDMConfigQueryUtil.getConfigDataObject(configQuery);
            return resultDO;
        }
        catch (final Exception ex) {
            MDMConfigUtil.logger.log(Level.WARNING, ex, () -> "Caught Exception while retrieving ConfigData for given collectionId: " + n);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public static DataObject getConfigDataDOByCollectionId(final int configID, final Long collectionId) throws SyMException {
        SelectQuery query = null;
        try {
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("ConfigData"));
            query.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Column collectionIdColumn = Column.getColumn("CfgDataToCollection", "COLLECTION_ID");
            final Column configIdColumn = Column.getColumn("ConfigData", "CONFIG_ID");
            Criteria criteria = new Criteria(configIdColumn, (Object)configID, 0);
            criteria = criteria.and(new Criteria(collectionIdColumn, (Object)collectionId, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(new Column("ConfigData", "*"));
            query.addSelectColumn(new Column("CfgDataToCollection", "*"));
            MDMConfigUtil.logger.log(Level.FINEST, "Config Select Query: {0} for fetching configuration with collection id: {1} and config id: {2}", new Object[] { query, collectionId, configID });
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            return resultDO;
        }
        catch (final Exception ex) {
            MDMConfigUtil.logger.log(Level.WARNING, "Caught Exception while retrieving ConfigData for given collectionId: " + collectionId + " query used: " + query, ex);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public static List getConfigurationDataItems(final Long collectionId) throws SyMException {
        MDMConfigUtil.logger.log(Level.INFO, "getConfigurationDataItems() invoked with collection id: {0}", collectionId);
        final List configList = new ArrayList();
        try {
            final String relTblName = "CfgDataToCollection";
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
            final Join configDataJoin = new Join(relTblName, "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
            final Join configDataItemJoin = new Join(relTblName, "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
            selectQuery.addJoin(configDataJoin);
            selectQuery.addJoin(configDataItemJoin);
            final Column col = Column.getColumn(relTblName, "COLLECTION_ID");
            selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            selectQuery.setCriteria(criteria);
            final SortColumn sortColumn = new SortColumn("CfgDataToCollection", "ORDER_OF_EXECUTION", true);
            final SortColumn sortColumn2 = new SortColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID", true);
            final List sortColumnList = Arrays.asList(sortColumn, sortColumn2);
            selectQuery.addSortColumns(sortColumnList);
            final JSONArray valueArray = MDMUtil.executeSelectQuery(selectQuery);
            for (int initial = 0; initial < valueArray.size(); ++initial) {
                final org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject)valueArray.get(initial);
                final Long configDataItemId = (Long)jsonObject.get((Object)"CONFIG_DATA_ITEM_ID");
                final int configId = MDMUtil.getInstance().getIntVal(jsonObject.get((Object)"CONFIG_ID"));
                configList.add(getConfigDataItemsDO(configId, configDataItemId));
            }
        }
        catch (final Exception ex2) {
            MDMConfigUtil.logger.log(Level.SEVERE, ex2, () -> "Error while retrieving configurations data item for collection id: " + n);
            throw new SyMException(1001, (Throwable)ex2);
        }
        MDMConfigUtil.logger.log(Level.FINEST, "Returning ConfigDataItem list for given collectionId: {0} is: {1}", new Object[] { collectionId, configList });
        return configList;
    }
    
    public static String getConfigPayloadIdentifier(final Object configDataItemID) throws SyMException {
        String configPayloadIdentifier = null;
        try {
            final String baseTableName = "MdConfigDataItemExtn";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addSelectColumn(new Column((String)null, "*"));
            final Column col = Column.getColumn("MdConfigDataItemExtn", "CONFIG_DATA_ITEM_ID");
            final Criteria criteria = new Criteria(col, configDataItemID, 0);
            query.setCriteria(criteria);
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty() && resultDO.containsTable(baseTableName)) {
                configPayloadIdentifier = String.valueOf(resultDO.getFirstValue(baseTableName, "CONFIG_PAYLOAD_IDENTIFIER"));
            }
        }
        catch (final DataAccessException ex) {
            MDMConfigUtil.logger.log(Level.WARNING, (Throwable)ex, () -> "Exception while getting config Payload Identifier for the given config data item id :" + o);
            throw new SyMException(1001, "Exception while getting config Payload Identifier for the given config data item id", (Throwable)ex);
        }
        return configPayloadIdentifier;
    }
}
