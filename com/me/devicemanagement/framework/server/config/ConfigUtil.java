package com.me.devicemanagement.framework.server.config;

import com.me.devicemanagement.framework.server.factory.DBHandlerFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.List;
import java.util.logging.Logger;

public class ConfigUtil
{
    protected static Logger logger;
    
    public static List getConfigurationIds(final Long collectionId) throws SyMException {
        return getConfigurationIds(collectionId, null);
    }
    
    public static List getConfigurationIds(final Long collectionId, final Integer configType) throws SyMException {
        final List configIdList = new ArrayList();
        try {
            final String baseTableName = "CfgDataToCollection";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addSelectColumn(new Column(baseTableName, "*"));
            final SortColumn sortColumn = new SortColumn("CfgDataToCollection", "ORDER_OF_EXECUTION", true);
            query.addSortColumn(sortColumn);
            final Column col = Column.getColumn(baseTableName, "COLLECTION_ID");
            Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            if (configType != null) {
                final Column typeCol = Column.getColumn("ConfigData", "CONFIG_TYPE");
                final Criteria typeCri = new Criteria(typeCol, (Object)configType, 0);
                criteria = criteria.and(typeCri);
            }
            query.setCriteria(criteria);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows(baseTableName);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    configIdList.add(row.get("CONFIG_DATA_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            ConfigUtil.logger.log(Level.SEVERE, "Error while retrieving configuration ids for collection id: " + collectionId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ConfigUtil.logger.log(Level.SEVERE, "Error while retrieving configuration ids for collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
        return configIdList;
    }
    
    public static List getConfigurationIds(final Integer configId) throws SyMException {
        final List configIdList = new ArrayList();
        try {
            final String baseTableName = "CfgDataToCollection";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addSelectColumn(new Column(baseTableName, "*"));
            final Column typeCol = Column.getColumn("ConfigData", "CONFIG_ID");
            final Criteria typeCri = new Criteria(typeCol, (Object)configId, 0);
            query.setCriteria(typeCri);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows(baseTableName);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    configIdList.add(row.get("CONFIG_DATA_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            ConfigUtil.logger.log(Level.SEVERE, "Error while retrieving configuration ids for config id: " + configId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ConfigUtil.logger.log(Level.SEVERE, "Error while retrieving configuration ids for config id: " + configId, ex2);
            throw new SyMException(1001, ex2);
        }
        return configIdList;
    }
    
    public static int getConfigID(final String configLabel) throws SyMException {
        int configID = -1;
        try {
            final String baseTableName = "ConfigurationParams";
            final String configurationTable = "Configuration";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, configurationTable, new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 2));
            query.addSelectColumn(new Column((String)null, "*"));
            final Column col = Column.getColumn(configurationTable, "CONFIG_NAME");
            final Criteria criteria = new Criteria(col, (Object)configLabel, 0);
            query.setCriteria(criteria);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty() && resultDO.containsTable(configurationTable)) {
                configID = (int)resultDO.getFirstValue(configurationTable, "CONFIG_ID");
            }
        }
        catch (final DataAccessException ex) {
            ConfigUtil.logger.log(Level.WARNING, "Exception while getting config ID for the given config label :" + configLabel, (Throwable)ex);
            throw new SyMException(1001, "Exception while getting config ID for the given config label", (Throwable)ex);
        }
        return configID;
    }
    
    public static List getConfigIds(final Long collectionId) throws SyMException {
        final List configIdList = new ArrayList();
        try {
            final String baseTableName = "CfgDataToCollection";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addSelectColumn(new Column("ConfigData", "*"));
            final SortColumn sortColumn = new SortColumn("CfgDataToCollection", "ORDER_OF_EXECUTION", true);
            query.addSortColumn(sortColumn);
            final Column col = Column.getColumn("CfgDataToCollection", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            query.setCriteria(criteria);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows("ConfigData");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    configIdList.add(row.get("CONFIG_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            ConfigUtil.logger.log(Level.SEVERE, "Error while retrieving configuration ids for collection id: " + collectionId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ConfigUtil.logger.log(Level.SEVERE, "Error while retrieving configuration ids for collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
        return configIdList;
    }
    
    public static void deleteConfiguration(final Long configDataId) throws SyMException {
        try {
            if (configDataId != null) {
                final Row row = new Row("ConfigData");
                row.set("CONFIG_DATA_ID", (Object)configDataId);
                DBHandlerFactoryProvider.getDBHandler().delete(row);
            }
            else {
                ConfigUtil.logger.log(Level.WARNING, "###########################################################");
                ConfigUtil.logger.log(Level.WARNING, "ConfigUtil -> deleteConfiguration -> configDataId  is NULL ");
                ConfigUtil.logger.log(Level.WARNING, "###########################################################");
            }
        }
        catch (final DataAccessException ex) {
            ConfigUtil.logger.log(Level.WARNING, "Caught exception while deleting config data: " + configDataId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ConfigUtil.logger.log(Level.WARNING, "Caught exception while deleting config data: " + configDataId, ex2);
            throw new SyMException(1001, ex2);
        }
    }
    
    static {
        ConfigUtil.logger = Logger.getLogger(ConfigUtil.class.getName());
    }
}
