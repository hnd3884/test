package com.adventnet.db.persistence.metadata.util;

import java.util.HashMap;
import java.util.Map;
import com.zoho.conf.AppResources;
import java.io.IOException;
import com.adventnet.persistence.Row;
import java.util.Arrays;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.SchemaBrowserUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.net.URL;
import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import java.util.Enumeration;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Iterator;
import java.util.List;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class OnDemandMetaDataInfo implements MetaDataInfo
{
    private static final Logger LOGGER;
    private static TableListProvider tableListProvider;
    private LocalMetaDataInfo localMeta;
    private IdBasedIndex idBasedIndex;
    private Set<String> danglingPIDXTables;
    private static Depth depth;
    
    public OnDemandMetaDataInfo() {
        this.localMeta = new LocalMetaDataInfo();
        this.idBasedIndex = new IdBasedIndex();
        this.danglingPIDXTables = new HashSet<String>();
    }
    
    @Override
    public Set getAllModuleNames() {
        return this.localMeta.getAllModuleNames();
    }
    
    @Override
    public synchronized void addDataDictionaryConfiguration(final DataDictionary dd) throws MetaDataException {
        this.localMeta.addDataDictionaryConfiguration(dd);
    }
    
    @Override
    public synchronized void addTableDefinition(final String moduleName, final TableDefinition td) throws MetaDataException {
        if (this.localMeta.getTableDefinitionByName(td.getTableName()) != null) {
            OnDemandMetaDataInfo.LOGGER.log(Level.WARNING, "Table [{0}] is already loaded.Hence returning.", td.getTableName());
            return;
        }
        final List<ForeignKeyDefinition> fkList = td.getForeignKeyList();
        for (final ForeignKeyDefinition fkDefn : fkList) {
            this.loadTableIfNeeded(fkDefn.getMasterTableName());
        }
        this.localMeta.addTableDefinition(moduleName, td);
        this.idBasedIndex.addToIndex(td);
        OnDemandMetaDataInfo.LOGGER.log(Level.INFO, "Added table [{0}] to OnDemandMetaDataInfo", td.getTableName());
    }
    
    @Override
    public void validateTableDefinition(final TableDefinition tableDefinition) throws MetaDataException {
        this.localMeta.validateTableDefinition(tableDefinition);
    }
    
    @Override
    public synchronized void alterTableDefinition(final AlterTableQuery atq) throws MetaDataException {
        if (this.localMeta.getTableDefinitionByName(atq.getTableName()) == null) {
            return;
        }
        this.localMeta.alterTableDefinition(atq);
    }
    
    @Override
    public Enumeration getAllTableDefinitions() throws MetaDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List getTableDefinitions() throws MetaDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public DataDictionary getDataDictionary(final String moduleName) throws MetaDataException {
        return this.localMeta.getDataDictionary(moduleName);
    }
    
    private void loadTableIfNeeded(final String tableName) throws MetaDataException {
        if (tableName == null || OnDemandMetaDataInfo.tableListProvider == null || this.danglingPIDXTables.contains(tableName) || this.localMeta.getTableDefinitionByName(tableName) != null) {
            return;
        }
        OnDemandMetaDataInfo.LOGGER.log(Level.INFO, "Delegating to TableListProvider for table [{0}]", tableName);
        final Collection<Long> tableIDs = OnDemandMetaDataInfo.tableListProvider.getTablesToBeLoaded(tableName);
        if (tableIDs == null || tableIDs.isEmpty()) {
            OnDemandMetaDataInfo.LOGGER.log(Level.WARNING, "The received TableID List for table [{0}] is EMPTY", tableName);
            this.nonExistentTableHandling(tableName);
            return;
        }
        this.loadTables(tableIDs);
    }
    
    private void nonExistentTableHandling(final String tableName) throws MetaDataException {
        try {
            final boolean isLoaded = this.loadTableFromDB(tableName);
            if (!isLoaded && tableName.endsWith("_PIDX")) {
                OnDemandMetaDataInfo.LOGGER.log(Level.WARNING, "Adding the _PIDX Table [{0}] to Dangling List", tableName);
                this.danglingPIDXTables.add(tableName);
            }
        }
        catch (final DataAccessException ex) {
            throw new MetaDataException("", ex);
        }
    }
    
    private synchronized void loadTables(final Collection<Long> tableIDs) throws MetaDataException {
        OnDemandMetaDataInfo.LOGGER.log(Level.FINEST, "Tables to be loaded [{0}]", tableIDs);
        this.removeAlreadyLoadedTables(tableIDs);
        if (tableIDs.isEmpty()) {
            OnDemandMetaDataInfo.LOGGER.log(Level.WARNING, "All the specified tables [{0}] are already loaded", tableIDs);
            return;
        }
        try {
            this.loadTablesFromDB((Long[])tableIDs.toArray(new Long[tableIDs.size()]));
        }
        catch (final DataAccessException ex) {
            throw new MetaDataException("", ex);
        }
    }
    
    private void removeAlreadyLoadedTables(final Collection<Long> tableIDs) throws MetaDataException {
        final Iterator<Long> it = tableIDs.iterator();
        while (it.hasNext()) {
            final Long tableId = it.next();
            if (this.getTableDefnFromMemory(tableId) != null) {
                it.remove();
            }
        }
    }
    
    private void _loadTableIfNeeded(final String tableName) {
        try {
            this.loadTableIfNeeded(tableName);
        }
        catch (final MetaDataException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public TableDefinition getTableDefnFromMemory(final Long tableId) {
        return this.idBasedIndex.getTableDefinition(tableId);
    }
    
    @Override
    public TableDefinition getTableDefinitionByName(final String tableName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getTableDefinitionByName(tableName);
    }
    
    @Override
    public ForeignKeyDefinition getForeignKeyDefinitionByName(final String fkName) throws MetaDataException {
        return this.localMeta.getForeignKeyDefinitionByName(fkName);
    }
    
    @Override
    public List getAllRelatedTableDefinitions(final String tableName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getAllRelatedTableDefinitions(tableName);
    }
    
    @Override
    public List getForeignKeys(final String tableName1, final String tableName2) throws MetaDataException {
        this.loadTableIfNeeded(tableName1);
        this.loadTableIfNeeded(tableName2);
        return this.localMeta.getForeignKeys(tableName1, tableName2);
    }
    
    @Override
    public String getDefinedTableName(final String tableName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getDefinedTableName(tableName);
    }
    
    @Override
    public String getDefinedTableNameByDisplayName(final String displayName) throws MetaDataException {
        return this.localMeta.getDefinedTableNameByDisplayName(displayName);
    }
    
    @Override
    public String getDefinedColumnName(final String tableName, final String columnName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getDefinedColumnName(tableName, columnName);
    }
    
    @Override
    public String getModuleNameOfTable(final String tableName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getModuleNameOfTable(tableName);
    }
    
    @Override
    public synchronized void removeDataDictionaryConfiguration(final String moduleName) throws MetaDataException {
        this.localMeta.removeDataDictionaryConfiguration(moduleName);
    }
    
    @Override
    public synchronized void removeTableDefinition(final String tableName) throws MetaDataException {
        this.idBasedIndex.removeFromIndex(tableName);
        OnDemandMetaDataInfo.LOGGER.log(Level.INFO, "Removed table [{0}] from OnDemandMetaDataInfo", tableName);
        this.localMeta.removeTableDefinition(tableName);
    }
    
    @Override
    public List getReferringForeignKeyDefinitions(final String tableName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getReferringForeignKeyDefinitions(tableName);
    }
    
    @Override
    public List getReferringForeignKeyDefinitions(final String tableName, final String columnName) throws MetaDataException {
        this.loadTableIfNeeded(tableName);
        return this.localMeta.getReferringForeignKeyDefinitions(tableName, columnName);
    }
    
    @Override
    public List getReferringTableNames(final String tableName) {
        this._loadTableIfNeeded(tableName);
        return this.localMeta.getReferringTableNames(tableName);
    }
    
    @Override
    public void dump() throws MetaDataException {
        this.localMeta.dump();
    }
    
    @Override
    public DataDictionary loadDataDictionary(final URL url) throws MetaDataException {
        throw new UnsupportedOperationException("Not Supported for OnDemand MetaData loading");
    }
    
    @Override
    public DataDictionary loadDataDictionary(final URL url, final boolean createTable) throws MetaDataException {
        throw new UnsupportedOperationException("Not Supported for OnDemand MetaData loading");
    }
    
    @Override
    public DataDictionary loadDataDictionary(final URL url, final boolean createTable, final String module) throws MetaDataException {
        throw new UnsupportedOperationException("Not Supported for OnDemand MetaData loading");
    }
    
    @Override
    public String[] getAllDataDictionarNames() throws MetaDataException {
        return this.localMeta.getAllDataDictionarNames();
    }
    
    @Override
    public void setLoaded(final boolean isloaded) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private boolean loadTableFromDB(final String tableName) throws DataAccessException, MetaDataException {
        if (tableName == null) {
            throw new IllegalArgumentException("tableName is NULL");
        }
        final DataObject tablesDO = SchemaBrowserUtil.getData(new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0), true);
        if (tablesDO == null || tablesDO.isEmpty()) {
            OnDemandMetaDataInfo.LOGGER.log(Level.WARNING, "DataObject is EMPTY for table [{0}]", tableName);
            return false;
        }
        this.createAndAddTableDefinitions(tablesDO);
        return true;
    }
    
    private void loadTablesFromDB(final Long... tableIDs) throws DataAccessException, MetaDataException {
        if (tableIDs == null || tableIDs.length == 0) {
            throw new IllegalArgumentException("tableIDs array is EMPTY");
        }
        final DataObject tablesDO = SchemaBrowserUtil.getData(createTablesFilter(tableIDs), true);
        if (tablesDO == null || tablesDO.isEmpty()) {
            OnDemandMetaDataInfo.LOGGER.log(Level.SEVERE, "TablesDO is EMPTY for tableIds [{0}]", Arrays.toString(tableIDs));
            return;
        }
        if (tablesDO.size("SB_Applications") != 1) {
            throw new IllegalArgumentException("The specified tables {" + Arrays.toString(tableIDs) + "} doesn't belong to same module");
        }
        this.createAndAddTableDefinitions(tablesDO);
    }
    
    private static Criteria createTablesFilter(final Long... tableIDs) {
        if (tableIDs.length == 1) {
            return new Criteria(new Column("TableDetails", 1), tableIDs[0], 0);
        }
        return new Criteria(new Column("TableDetails", 1), tableIDs, 8);
    }
    
    private void createAndAddTableDefinitions(final DataObject tablesDO) throws DataAccessException, MetaDataException {
        OnDemandMetaDataInfo.LOGGER.log(Level.FINEST, "TablesDO : {0}", tablesDO);
        final String moduleName = (String)tablesDO.getRow("SB_Applications").get(2);
        if (this.getDataDictionary(moduleName) == null) {
            this.addDataDictionaryConfiguration(new DataDictionary(moduleName));
        }
        final Iterator rowsIter = tablesDO.getRows("TableDetails");
        while (rowsIter.hasNext()) {
            final Row tableRow = rowsIter.next();
            OnDemandMetaDataInfo.LOGGER.log(Level.FINEST, "TableRow-[{0}]", tableRow);
            TableDefinition td = this.localMeta.getTableDefinitionByName((String)tableRow.get(3));
            if (td != null) {
                continue;
            }
            try {
                OnDemandMetaDataInfo.depth.incr();
                td = SchemaBrowserUtil.getTableDefinition(tablesDO, tableRow);
            }
            catch (final DataAccessException exp) {
                OnDemandMetaDataInfo.LOGGER.log(Level.SEVERE, "Problem while creating TableDefiniton for table Row [" + tableRow + "]", exp);
            }
            catch (final MetaDataException exp2) {
                OnDemandMetaDataInfo.LOGGER.log(Level.SEVERE, "Problem while creating TableDefiniton for table Row [" + tableRow + "]", exp2);
            }
            finally {
                OnDemandMetaDataInfo.depth.decr();
            }
            this.localMeta.addTableDefinition(moduleName, td);
            OnDemandMetaDataInfo.LOGGER.log(Level.INFO, "Added table [{0}] to OnDemandMetaDataInfo", td.getTableName());
        }
    }
    
    @Override
    public void addTemplateInstance(final String templateTableName, final String instanceId) throws MetaDataException {
        throw new UnsupportedOperationException("addTemplateInstance:" + templateTableName + " " + instanceId);
    }
    
    @Override
    public boolean removeTemplateInstance(final String templateTableName, final String instanceId) throws MetaDataException {
        throw new UnsupportedOperationException("removeTemplateInstance:" + templateTableName + " " + instanceId);
    }
    
    @Override
    public TemplateMetaHandler getTemplateHandler(final String ddName) throws MetaDataException {
        return this.localMeta.getTemplateHandler(ddName);
    }
    
    @Override
    public void validateAlterTableQuery(final AlterTableQuery alterTableQuery) throws MetaDataException {
        this.localMeta.validateAlterTableQuery(alterTableQuery);
    }
    
    @Override
    public List<String> getTableNamesInDefinedOrder() throws MetaDataException {
        return null;
    }
    
    @Override
    public String getAttribute(final String tableName, final String columnName, final String attributeName) throws MetaDataException {
        return this.localMeta.getAttribute(tableName, columnName, attributeName);
    }
    
    @Override
    public String getAttribute(final String tableName, final String attributeName) throws MetaDataException {
        return this.localMeta.getAttribute(tableName, attributeName);
    }
    
    @Override
    public String getAttribute(final String attrName) throws MetaDataException {
        return this.localMeta.getAttribute(attrName);
    }
    
    @Override
    public void loadCustomAttributes(final List<URL> listOfExtendedDDFiles, final boolean validate, final boolean loadDynamicAttributes) throws Exception {
        this.localMeta.loadCustomAttributes(listOfExtendedDDFiles, validate, loadDynamicAttributes);
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String columnName, final String attributeName, final String value) throws MetaDataException, IOException, DataAccessException {
        return this.localMeta.setAttribute(tableName, columnName, attributeName, value);
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String attributeName, final String value) throws MetaDataException, IOException, DataAccessException {
        return this.localMeta.setAttribute(tableName, attributeName, value);
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String columnName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        return this.localMeta.removeAttribute(tableName, columnName, attributeName);
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        return this.localMeta.removeAttribute(tableName, attributeName);
    }
    
    @Override
    public void loadDynamicCustomDDAttributes() throws IOException, DataAccessException, MetaDataException {
        this.localMeta.loadDynamicCustomDDAttributes();
    }
    
    @Override
    public void removeCustomAttributeConfigurations() {
        this.localMeta.removeCustomAttributeConfigurations();
    }
    
    static {
        LOGGER = Logger.getLogger(OnDemandMetaDataInfo.class.getName());
        OnDemandMetaDataInfo.tableListProvider = null;
        final String clazName = AppResources.getString(TableListProvider.class.getName());
        if (clazName != null) {
            try {
                OnDemandMetaDataInfo.tableListProvider = (TableListProvider)Class.forName(clazName).newInstance();
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
            OnDemandMetaDataInfo.LOGGER.log(Level.INFO, "Loaded TableListProvider [{0}]", clazName);
        }
        OnDemandMetaDataInfo.depth = new Depth();
    }
    
    private class IdBasedIndex
    {
        private Map<Long, String> idVsNameMap;
        
        private IdBasedIndex() {
            this.idVsNameMap = new HashMap<Long, String>();
        }
        
        private TableDefinition getTableDefinition(final Long tableId) {
            final String tableName = this.idVsNameMap.get(tableId);
            if (tableName == null) {
                return null;
            }
            TableDefinition td = null;
            try {
                td = OnDemandMetaDataInfo.this.localMeta.getTableDefinitionByName(tableName);
            }
            catch (final MetaDataException exp) {
                throw new RuntimeException(exp);
            }
            if (td == null) {
                OnDemandMetaDataInfo.LOGGER.log(Level.WARNING, "tableIdVsName not in sync with LocalMetaDataInfo.Removing the key [{0}]", tableId);
                this.idVsNameMap.remove(tableId);
            }
            return td;
        }
        
        private void addToIndex(final TableDefinition td) {
            final Long tableId = td.getTableID();
            if (tableId == null) {
                return;
            }
            this.idVsNameMap.put(tableId, td.getTableName());
        }
        
        private void removeFromIndex(final String tableName) {
            TableDefinition td = null;
            try {
                td = OnDemandMetaDataInfo.this.localMeta.getTableDefinitionByName(tableName);
            }
            catch (final MetaDataException exp) {
                throw new RuntimeException(exp);
            }
            if (td != null) {
                final Long tableId = td.getTableID();
                if (tableId != null) {
                    this.idVsNameMap.remove(tableId);
                }
            }
        }
    }
    
    private static class Depth extends InheritableThreadLocal
    {
        int value;
        
        private void incr() throws MetaDataException {
            ++this.value;
            if (this.value > 20) {
                throw new MetaDataException("ERROR: createAndAddTableDefinitions() method is invoked more than 20 times recursively !!");
            }
        }
        
        private void decr() {
            --this.value;
        }
    }
}
