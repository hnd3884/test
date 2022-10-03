package com.adventnet.db.persistence.metadata.util;

import java.util.Hashtable;
import com.adventnet.db.persistence.metadata.extended.CustomAttributeValidator;
import java.io.IOException;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.persistence.migration.MigrationUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.PersistenceUtil;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Properties;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Vector;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.parser.ParserUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.persistence.util.DCManager;
import java.util.Set;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.MetaDataAccess;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.net.URL;
import java.util.logging.Level;
import com.zoho.conf.AppResources;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.HashSet;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.List;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Map;
import com.adventnet.db.persistence.metadata.extended.CustomAttributeHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class LocalMetaDataInfo implements MetaDataInfo
{
    private static final Logger OUT;
    private static final String DEFAULT_TEMPLATE_HANDLER = "DEFAULT_TEMPLATE_HANDLER";
    private static boolean getTdForAnyTableNameCase;
    private ConcurrentHashMap<String, String> customAttributes;
    private static CustomAttributeHandler customAttributeHandler;
    private boolean areDynamicCustomAttrLoaded;
    private TemplateMetaHandler defaultTemplateHandler;
    private Map<String, TableDefinition> definitionsMap;
    private Map<String, String> tableNameVsDefinedTableName;
    private Map<String, DataDictionary> moduleToEntitiesMap;
    private Map<String, List<TableDefinition>> referringTablesHash;
    private Map<String, List<ForeignKeyDefinition>> referringFKsHash;
    private Map<String, ForeignKeyDefinition> fkMap;
    private boolean isLoaded;
    private Map<String, String> displayNameVsDefinedTableName;
    private HashSet<String> constraintNames;
    private static final int THROW_EXCEPTION_IF_EXISTS = 1001;
    private static final int THROW_EXCEPTION_IF_NOT_EXISTS = 1002;
    
    public LocalMetaDataInfo() {
        this.customAttributes = new ConcurrentHashMap<String, String>(16, 0.75f, 1);
        this.areDynamicCustomAttrLoaded = false;
        this.defaultTemplateHandler = null;
        this.definitionsMap = new TreeMap<String, TableDefinition>(String.CASE_INSENSITIVE_ORDER);
        this.tableNameVsDefinedTableName = new HashMap<String, String>();
        this.moduleToEntitiesMap = new LinkedHashMap<String, DataDictionary>();
        this.referringTablesHash = new HashMap<String, List<TableDefinition>>();
        this.referringFKsHash = new HashMap<String, List<ForeignKeyDefinition>>();
        this.fkMap = new HashMap<String, ForeignKeyDefinition>();
        this.isLoaded = false;
        this.displayNameVsDefinedTableName = new HashMap<String, String>();
        this.constraintNames = new HashSet<String>();
        final boolean throughSqlCreation = Boolean.getBoolean("generate.datadic.diffs");
        if (!throughSqlCreation) {
            this.initDefaultTemplateMetaHandler("DEFAULT_TEMPLATE_HANDLER", PersistenceInitializer.getConfigurationValue("TemplateMetaHandler"));
        }
        else {
            this.initDefaultTemplateMetaHandler("DEFAULT_TEMPLATE_HANDLER", "com.adventnet.sas.metadata.DummyTemplateHandler");
        }
        LocalMetaDataInfo.getTdForAnyTableNameCase = AppResources.getBoolean("get.tabledefinition.for.any.tablenamecase", Boolean.valueOf(false));
        if (LocalMetaDataInfo.customAttributeHandler == null) {
            String handler = PersistenceInitializer.getConfigurationValue("CustomAttributeHandler");
            if (handler == null) {
                handler = "com.adventnet.db.persistence.metadata.extended.MECustomAttributeHandler";
            }
            try {
                LocalMetaDataInfo.customAttributeHandler = (CustomAttributeHandler)Class.forName(handler).newInstance();
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    
    private void initDefaultTemplateMetaHandler(final String ddName, final String clazzName) {
        if (clazzName != null) {
            if (this.defaultTemplateHandler != null) {
                LocalMetaDataInfo.OUT.warning("Old Default_Template_Meta_Handler instance is deleted and new instance is being created");
            }
            this.defaultTemplateHandler = DataDictionary.initTemplateMetaHandler(clazzName);
            LocalMetaDataInfo.OUT.log(Level.INFO, "Loaded TemplateMetaHandler [{0}]", clazzName);
        }
    }
    
    @Override
    public TemplateMetaHandler getTemplateHandler(final String ddName) {
        TemplateMetaHandler tmh = null;
        final DataDictionary dd = this.moduleToEntitiesMap.get(ddName);
        if (dd != null) {
            tmh = dd.getTemplateMetaHandler();
        }
        if (tmh == null && this.defaultTemplateHandler != null) {
            tmh = this.defaultTemplateHandler;
        }
        return tmh;
    }
    
    @Override
    public DataDictionary loadDataDictionary(final URL url) throws MetaDataException {
        return this.loadDataDictionary(url, true);
    }
    
    @Override
    public DataDictionary loadDataDictionary(final URL url, final boolean createTables) throws MetaDataException {
        return MetaDataAccess.loadDataDictionary(url, createTables);
    }
    
    @Override
    public DataDictionary loadDataDictionary(final URL url, final boolean createTables, final String module) throws MetaDataException {
        return MetaDataAccess.loadDataDictionary(url, createTables, module);
    }
    
    @Override
    public List getReferringForeignKeyDefinitions(final String tableName, final String columnName) throws MetaDataException {
        final List retFKs = new ArrayList();
        final List fkDefs = this.getReferringForeignKeyDefinitions(tableName);
        if (fkDefs != null) {
            for (final ForeignKeyDefinition fkd : fkDefs) {
                for (final ForeignKeyColumnDefinition fkcd : fkd.getForeignKeyColumns()) {
                    if (fkcd.getReferencedColumnDefinition().getColumnName().equalsIgnoreCase(columnName)) {
                        retFKs.add(fkd);
                        break;
                    }
                }
            }
        }
        return retFKs;
    }
    
    public MetaDataInfo init() {
        return this;
    }
    
    @Override
    public void addDataDictionaryConfiguration(final DataDictionary dd) throws MetaDataException {
        final String moduleName = dd.getName();
        if (moduleName == null) {
            throw new MetaDataException("DataDictionary doesn't have a moduleName");
        }
        if (this.moduleToEntitiesMap.containsKey(moduleName)) {
            throw new MetaDataException("Data Dictionary with name " + moduleName + " already defined. Please use a different name");
        }
        this.moduleToEntitiesMap.put(moduleName, dd);
        final List entityDefinitionList = dd.getTableDefinitions();
        if (entityDefinitionList != null && !entityDefinitionList.isEmpty()) {
            for (int size = entityDefinitionList.size(), i = 0; i < size; ++i) {
                final TableDefinition td = entityDefinitionList.get(i);
                if (!dd.isValidated()) {
                    try {
                        this.validateTableDefinition(td);
                    }
                    catch (final MetaDataException mde) {
                        this.removeDataDictionaryConfiguration(moduleName);
                        throw mde;
                    }
                }
                td.setModuleName(moduleName);
                this.mapTableDefinition(moduleName, td);
                if (td.hasBDFK()) {
                    final List list = new ArrayList();
                    list.add(td.getTableName());
                    this.markAsBDFK(td, list);
                }
                if (td.getDynamicColumnType() == null && !td.isTemplate()) {
                    td.setDynamicColumnType(dd.getDynamicColumnType());
                }
            }
        }
    }
    
    @Override
    public String[] getAllDataDictionarNames() throws MetaDataException {
        final Set keys = this.moduleToEntitiesMap.keySet();
        final String[] names = new String[keys.size()];
        final Iterator iterator = keys.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            names[counter++] = iterator.next();
        }
        return names;
    }
    
    @Override
    public Set getAllModuleNames() {
        return this.moduleToEntitiesMap.keySet();
    }
    
    @Override
    public void addTemplateInstance(final String templateTableName, final String instanceId) throws MetaDataException {
        final TableDefinition td = this.getTableDefinitionByName(templateTableName);
        if (td == null || !td.isTemplate()) {
            throw new IllegalArgumentException("No such template-table [" + templateTableName + "]");
        }
        final TemplateMetaHandler templateMeta = this.getTemplateHandler(td.getModuleName());
        templateMeta.addTemplateInstance(templateTableName, instanceId);
    }
    
    @Override
    public void addTableDefinition(final String moduleName, final TableDefinition td) throws MetaDataException {
        if (!this.moduleToEntitiesMap.containsKey(moduleName)) {
            throw new MetaDataException("The specified moduleName " + moduleName + " for table " + td.getTableName() + " is incorrect");
        }
        final boolean validate = td.isValidated();
        td.setModuleName(moduleName);
        if (!validate) {
            this.validateTableDefinition(td);
        }
        final DataDictionary dd = this.moduleToEntitiesMap.get(moduleName);
        dd.addTableDefinition(td);
        this.mapTableDefinition(moduleName, td);
        if (td.hasBDFK()) {
            final List list = new ArrayList();
            list.add(td.getTableName());
            this.markAsBDFK(td, list);
        }
    }
    
    @Override
    public void validateTableDefinition(final TableDefinition tableDefinition) throws MetaDataException {
        final String tableName = tableDefinition.getTableName();
        if (tableName == null || tableName.trim().equals("")) {
            throw new MetaDataException("TableName cannot be null/empty");
        }
        if (tableName.length() > MetaDataUtil.DBOBJECT_NAMELENGTH) {
            throw new MetaDataException("The tableName \"" + tableName + "\" has " + tableName.length() + " characters. But it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".");
        }
        if (this.getTableDefinitionByName(tableName) != null) {
            throw new MetaDataException("Table with name " + tableName + " is already defined. Please use a different name");
        }
        if (tableDefinition.isTemplate() && tableDefinition.getTemplateInstancePatternName() != null) {
            final String patternName = tableDefinition.getTemplateInstancePatternName();
            if (!patternName.contains("${instancename}")) {
                throw new MetaDataException(" The template table [" + tableName + "] should define pattern with ${instancename} for proper replacing of this holder with instance-name.");
            }
        }
        if (tableDefinition.isTemplate() && tableDefinition.getDynamicColumnType() != null) {
            throw new MetaDataException("Template table cannot have dynamic column handler");
        }
        final String dcType = tableDefinition.getDynamicColumnType();
        if (dcType != null && !dcType.equalsIgnoreCase("nodc") && !DCManager.getDCTypes().contains(tableDefinition.getDynamicColumnType())) {
            throw new MetaDataException("dc-type :: " + tableDefinition.getDynamicColumnType() + " not defined in dynamic-column-types.props");
        }
        for (final Object column : tableDefinition.getColumnList()) {
            final ColumnDefinition cd = (ColumnDefinition)column;
            cd.validate();
            final String columnName = cd.getColumnName();
            if (columnName.length() > MetaDataUtil.DBOBJECT_NAMELENGTH) {
                throw new IllegalArgumentException("The ColumnName \"" + columnName + "\" has " + columnName.length() + " characters. But it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".");
            }
        }
        final PrimaryKeyDefinition pk = tableDefinition.getPrimaryKey();
        if (pk == null) {
            throw new MetaDataException("Table " + tableName + " cannot be created without Primary key.");
        }
        ParserUtil.validatePrimaryKey(tableDefinition, pk, null);
        final List<UniqueKeyDefinition> ukdefs = tableDefinition.getUniqueKeys();
        if (ukdefs != null) {
            for (final UniqueKeyDefinition ukdef : ukdefs) {
                ParserUtil.validateUniqueKey(tableDefinition, ukdef, null);
            }
        }
        final List<IndexDefinition> indexes = tableDefinition.getIndexes();
        if (indexes != null) {
            for (final IndexDefinition idx : indexes) {
                ParserUtil.validateIndexDefinition(tableDefinition, idx, null);
            }
        }
        final List fkList = tableDefinition.getForeignKeyList();
        if (fkList != null) {
            for (int size = fkList.size(), i = 0; i < size; ++i) {
                final ForeignKeyDefinition fkDef = fkList.get(i);
                final String fkName = fkDef.getName();
                final ForeignKeyDefinition oldFK = this.getForeignKeyDefinitionByName(fkName);
                if (oldFK != null) {
                    throw new MetaDataException("Foreign Key with name [" + fkName + "] is already defined in table [" + oldFK.getSlaveTableName() + "] Please use a different foreign key name in the definition of [" + tableName + "]");
                }
                final String masterTableName = fkDef.getMasterTableName();
                if (masterTableName == null) {
                    throw new MetaDataException("The Reference Table name in the Foreign Key " + fkDef + " of table " + tableName + " cannot be null.");
                }
                TableDefinition masterTabDef = this.getTableDefinitionByName(masterTableName);
                if (masterTabDef == null) {
                    if (!masterTableName.equals(tableName)) {
                        throw new MetaDataException("Unknown table referenced as Foreign Key " + masterTableName + " in the definition " + tableName);
                    }
                    masterTabDef = tableDefinition;
                }
                this.validateFK(masterTabDef, tableDefinition, fkDef);
                final List fkCols = fkDef.getForeignKeyColumns();
                for (final ForeignKeyColumnDefinition fkColDef : fkCols) {
                    final ColumnDefinition locColDefn = fkColDef.getLocalColumnDefinition();
                    final ColumnDefinition refColDefn = fkColDef.getReferencedColumnDefinition();
                    final ColumnDefinition actRefColDefn = masterTabDef.getColumnDefinitionByName(refColDefn.getColumnName());
                    if (actRefColDefn == null) {
                        throw new MetaDataException("Unknown Column Name specified " + refColDefn.getColumnName() + " as reference column in the Foreign Key Definition " + fkDef.getName() + " for Table Definition " + tableName);
                    }
                    if (!locColDefn.matches(actRefColDefn)) {
                        throw new MetaDataException("Mismatch in the Foreign Key definition: The datatype and/or size of columns " + locColDefn.getColumnName() + ", " + refColDefn.getColumnName() + " defined for Foreign Key " + fkDef.getName() + " in table " + tableName + " does not match");
                    }
                    fkColDef.setReferencedColumnDefinition(actRefColDefn);
                }
                if (fkDef.isBidirectional() && masterTabDef.isSystem() && !tableDefinition.isSystem()) {
                    throw new MetaDataException("Non system table [" + tableName + "] cannot have  Bidirectional Foreign Key with System table [" + fkDef.getMasterTableName() + "]");
                }
            }
        }
        if (tableDefinition.isTemplate() && this.getTemplateHandler(tableDefinition.getModuleName()) == null) {
            throw new MetaDataException("Template Table " + tableDefinition.getTableName() + " cannot be defined without a TemplateMetaHandler. Implement TemplateMetaHandler interface to make use of Template Table feature.");
        }
        final List<IndexDefinition> idxList = tableDefinition.getIndexes();
        if (idxList != null) {
            for (final IndexDefinition idx2 : idxList) {
                this.validateIndexDefinition(tableDefinition, idx2);
            }
        }
    }
    
    protected void validateFK(final TableDefinition parent, final TableDefinition child, final ForeignKeyDefinition fkDef) {
        ParserUtil.validateFK(parent, child, fkDef, null);
    }
    
    private void markAsBDFK(final TableDefinition td, final List tableNames) throws MetaDataException {
        final List fkList = td.getForeignKeyList();
        boolean setBDFKImpactforAllFK = false;
        final boolean isInitialTable = tableNames.indexOf(td.getTableName()) == 0;
        if (isInitialTable) {
            final List<String> refTableNames = new ArrayList<String>(fkList.size());
            for (int i = 0; i < fkList.size(); ++i) {
                final ForeignKeyDefinition fk = fkList.get(i);
                if (fk.isBidirectional() && !refTableNames.contains(fk.getMasterTableName())) {
                    refTableNames.add(fk.getMasterTableName());
                }
            }
            if (refTableNames.size() > 1) {
                setBDFKImpactforAllFK = true;
            }
        }
        if (fkList != null) {
            for (int j = 0; j < fkList.size(); ++j) {
                final ForeignKeyDefinition fkDef = fkList.get(j);
                if (setBDFKImpactforAllFK || !isInitialTable || !fkDef.isBidirectional()) {
                    final TableDefinition masterDef = this.getTableDefinitionByName(fkList.get(j).getMasterTableName());
                    if (!tableNames.contains(masterDef.getTableName())) {
                        masterDef.setBDFKImpact(true);
                        tableNames.add(masterDef.getTableName());
                        this.markAsBDFK(masterDef, tableNames);
                    }
                }
            }
        }
    }
    
    private void mapTableDefinition(final String moduleName, final TableDefinition td) throws MetaDataException {
        this.addConstraintName(td.getPrimaryKey().getName());
        if (td.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition ukd : td.getUniqueKeys()) {
                this.addConstraintName(ukd.getName());
            }
        }
        if (td.getIndexes() != null) {
            for (final IndexDefinition idx : td.getIndexes()) {
                this.addConstraintName(idx.getName());
            }
        }
        this.markIsUnique(td);
        td.setModuleName(moduleName);
        final String tableName = td.getTableName();
        this.addTableDefinitionToDefinitionMap(tableName, td);
        this.tableNameVsDefinedTableName.put(tableName.toLowerCase(Locale.ENGLISH), tableName);
        final String displayName = td.getDisplayName().toLowerCase(Locale.ENGLISH);
        this.displayNameVsDefinedTableName.put(displayName, tableName);
        final List fkList = td.getForeignKeyList();
        if (fkList != null) {
            for (int size = fkList.size(), i = 0; i < size; ++i) {
                this.findAndAddToRelatedTables(fkList.get(i));
            }
        }
        if (td.hasBDFK()) {
            final List list = new ArrayList();
            list.add(td.getTableName());
            this.markAsBDFK(td, list);
        }
        if (td.isTemplate()) {
            final TemplateMetaHandler templateMeta = this.getTemplateHandler(moduleName);
            if (templateMeta == null) {
                throw new MetaDataException("Template Table " + td.getTableName() + " cannot be defined without a TemplateMetaHandler. Implement TemplateMetaHandler interface to make use of Template Table feature.");
            }
            templateMeta.addTemplate(moduleName, td);
        }
    }
    
    private void validateIndexDefinition(final TableDefinition tableDefinition, final IndexDefinition idx) throws MetaDataException {
        if (idx != null && idx.isPartialIndex()) {
            for (final String col : idx.getColumns()) {
                final String dataType = tableDefinition.getColumnDefinitionByName(col).getDataType();
                final DataTypeDefinition udt = DataTypeManager.getDataTypeDefinition(dataType);
                final boolean isUDT = udt != null && udt.getMeta() != null;
                if (idx.getSize(col) != -1 && ((isUDT && !udt.getMeta().isPartialIndexSupported()) || (!isUDT && !dataType.equals("CHAR")))) {
                    throw new MetaDataException("Partial Indexing is not supported for this column " + tableDefinition.getTableName() + "." + col + " type ");
                }
            }
        }
    }
    
    @Override
    public List getReferringTableNames(final String tableName) {
        final List list = this.referringTablesHash.get(tableName);
        final List retList = new ArrayList();
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                retList.add(list.get(i).getTableName());
            }
        }
        return retList;
    }
    
    private void findAndAddToRelatedTables(final ForeignKeyDefinition fkDef) throws MetaDataException {
        this.fkMap.put(fkDef.getName(), fkDef);
        final String masterTableName = fkDef.getMasterTableName();
        final String slaveTableName = fkDef.getSlaveTableName();
        final TableDefinition masterTable = this.getTableDefinitionByName(masterTableName);
        final TableDefinition slaveTable = this.getTableDefinitionByName(slaveTableName);
        if (masterTable != null && slaveTable != null) {
            List referringTableList = this.referringTablesHash.get(masterTableName);
            if (referringTableList == null) {
                referringTableList = new Vector();
                this.referringTablesHash.put(masterTableName, referringTableList);
            }
            referringTableList.add(slaveTable);
            List referringFKList = this.referringFKsHash.get(masterTableName);
            if (referringFKList == null) {
                referringFKList = new Vector();
                this.referringFKsHash.put(masterTableName, referringFKList);
            }
            referringFKList.add(fkDef);
            this.addConstraintName(fkDef.getName());
            final List<ForeignKeyColumnDefinition> fkCols = fkDef.getForeignKeyColumns();
            for (final ForeignKeyColumnDefinition fkColDef : fkCols) {
                fkColDef.setLocalColumnDefinition(slaveTable.getColumnDefinitionByName(fkColDef.getLocalColumnDefinition().getColumnName()));
                fkColDef.setReferencedColumnDefinition(masterTable.getColumnDefinitionByName(fkColDef.getReferencedColumnDefinition().getColumnName()));
            }
        }
    }
    
    private void markIsUnique(final TableDefinition td) {
        for (final ColumnDefinition cd : td.getColumnList()) {
            cd.setUnique(false);
        }
        final PrimaryKeyDefinition pk = td.getPrimaryKey();
        final List<String> pkColumns = pk.getColumnList();
        if (pkColumns.size() == 1) {
            final ColumnDefinition pkCol = td.getColumnDefinitionByName(pkColumns.get(0));
            pkCol.setUnique(true);
        }
        final List<UniqueKeyDefinition> uniqueKeys = td.getUniqueKeys();
        if (uniqueKeys != null) {
            for (final UniqueKeyDefinition uk : uniqueKeys) {
                final List<String> ukColumns = uk.getColumns();
                if (ukColumns.size() == 1) {
                    final ColumnDefinition columnDefinition = td.getColumnDefinitionByName(ukColumns.get(0));
                    columnDefinition.setUnique(true);
                }
            }
        }
    }
    
    @Override
    public void alterTableDefinition(final AlterTableQuery atq) throws MetaDataException {
        final String tableName = atq.getTableName();
        final TableDefinition tableDefinition = this.getTableDefinitionByName(tableName);
        if (null != atq) {
            tableDefinition.resetMetaDigest();
            LocalMetaDataInfo.OUT.fine("MetaDigest reset!!!");
        }
        final boolean template = tableDefinition.isTemplate();
        for (final AlterOperation ao : atq.getAlterOperations()) {
            final int operation = ao.getOperationType();
            ColumnDefinition columnDefinition = null;
            switch (operation) {
                case 1: {
                    final ColumnDefinition addCD = (ColumnDefinition)ao.getAlterObject();
                    columnDefinition = new ColumnDefinition();
                    columnDefinition.setTableName(tableName);
                    columnDefinition.setColumnID(addCD.getColumnID());
                    columnDefinition.setColumnName(addCD.getColumnName());
                    columnDefinition.setDataType(addCD.getDataType());
                    columnDefinition.setDefaultValue(addCD.getDefaultValue());
                    columnDefinition.setMaxLength(addCD.getMaxLength());
                    columnDefinition.setNullable(addCD.isNullable());
                    columnDefinition.setAllowedValues(addCD.getAllowedValues());
                    columnDefinition.setUniqueValueGeneration(addCD.getUniqueValueGeneration());
                    columnDefinition.setDescription(addCD.getDescription());
                    columnDefinition.setDisplayName(addCD.getDisplayName());
                    columnDefinition.setPrecision(addCD.getPrecision());
                    columnDefinition.setUnique(addCD.isUnique());
                    columnDefinition.setSQLType(QueryUtil.getJavaSQLType(addCD.getDataType()));
                    tableDefinition.addColumnDefinition(columnDefinition);
                    continue;
                }
                case 2: {
                    final ColumnDefinition modCD = (ColumnDefinition)ao.getAlterObject();
                    tableDefinition.modifyColumnDefinition(modCD);
                    continue;
                }
                case 3: {
                    final ColumnDefinition cd = tableDefinition.removeColumnDefinition((String)ao.getAlterObject());
                    continue;
                }
                case 12:
                case 22: {
                    final String[] names = (String[])ao.getAlterObject();
                    tableDefinition.renameColumn(names[0], names[1]);
                    final List<ForeignKeyDefinition> referredFKs = this.getReferringForeignKeyDefinitions(tableName, names[0]);
                    if (referredFKs != null) {
                        for (final ForeignKeyDefinition fk : referredFKs) {
                            fk.renameColumn(names[0], names[1], false);
                        }
                        continue;
                    }
                    continue;
                }
                case 13: {
                    final String newTableName = (String)ao.getAlterObject();
                    this.definitionsMap.remove(tableName);
                    this.tableNameVsDefinedTableName.remove(tableName.toLowerCase(Locale.ENGLISH));
                    this.addTableDefinitionToDefinitionMap(newTableName, tableDefinition);
                    tableDefinition.renameTableName(newTableName);
                    this.tableNameVsDefinedTableName.put(newTableName.toLowerCase(Locale.ENGLISH), tableDefinition.getTableName());
                    final List fks = this.getReferringForeignKeyDefinitions(tableName);
                    if (null != fks) {
                        for (final Object o : fks) {
                            final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)o;
                            fkDef.setMasterTableName(newTableName);
                        }
                    }
                    final List<ForeignKeyDefinition> refFKs = this.referringFKsHash.get(tableName);
                    if (refFKs != null) {
                        this.referringFKsHash.put(newTableName, refFKs);
                        this.referringFKsHash.remove(tableName);
                    }
                    final List<TableDefinition> refferingTables = this.referringTablesHash.get(tableName);
                    if (refferingTables != null) {
                        this.referringTablesHash.put(newTableName, refferingTables);
                        this.referringTablesHash.remove(tableName);
                    }
                    final String moduleName = this.getModuleNameOfTable(tableName);
                    final DataDictionary dd = this.moduleToEntitiesMap.get(moduleName);
                    dd.renameTable(tableName, newTableName);
                    continue;
                }
                case 6: {
                    final ForeignKeyDefinition fkDef2 = (ForeignKeyDefinition)ao.getAlterObject();
                    final TableDefinition masterTableDefinition = this.getTableDefinitionByName(fkDef2.getMasterTableName());
                    final ForeignKeyDefinition fkd = new ForeignKeyDefinition();
                    fkd.setID(fkDef2.getID());
                    fkd.setName(fkDef2.getName());
                    fkd.setMasterTableName(fkDef2.getMasterTableName());
                    fkd.setSlaveTableName(tableName);
                    fkd.setConstraints(fkDef2.getConstraints());
                    fkd.setBidirectional(fkDef2.isBidirectional());
                    fkd.setDescription(fkDef2.getDescription());
                    final List<String> lc = fkDef2.getFkColumns();
                    final List<String> rc = fkDef2.getFkRefColumns();
                    for (int i = 0; i < lc.size(); ++i) {
                        final ForeignKeyColumnDefinition fkcd = new ForeignKeyColumnDefinition();
                        fkcd.setLocalColumnDefinition(tableDefinition.getColumnDefinitionByName(lc.get(i)));
                        fkcd.setReferencedColumnDefinition(masterTableDefinition.getColumnDefinitionByName(rc.get(i)));
                        fkd.addForeignKeyColumns(fkcd);
                    }
                    tableDefinition.addForeignKey(fkd);
                    this.findAndAddToRelatedTables(fkd);
                    continue;
                }
                case 14: {
                    final ForeignKeyDefinition modFK = (ForeignKeyDefinition)ao.getAlterObject();
                    this.constraintNames.remove(modFK.getName());
                    final ForeignKeyDefinition oldFK = this.fkMap.remove(modFK.getName());
                    final String oldMasterTableName = oldFK.getMasterTableName();
                    tableDefinition.removeForeignKey(oldFK.getName());
                    List fkList = this.referringFKsHash.get(oldMasterTableName);
                    fkList.remove(oldFK);
                    final List referenceList = this.referringTablesHash.get(oldMasterTableName);
                    if (referenceList != null) {
                        referenceList.remove(this.getTableDefinitionByName(tableName));
                    }
                    if (fkList.isEmpty()) {
                        this.referringFKsHash.remove(oldMasterTableName);
                    }
                    fkList = this.getForeignKeys(oldMasterTableName, tableName);
                    if (fkList != null && fkList.isEmpty()) {
                        final List referringTables = this.referringTablesHash.get(oldMasterTableName);
                        referringTables.remove(tableName);
                    }
                    this.fkMap.remove(oldFK.getName());
                    final String newMasterTableName = modFK.getMasterTableName();
                    final TableDefinition referenceTableDefinition = this.getTableDefinitionByName(newMasterTableName);
                    final ForeignKeyDefinition newFK = new ForeignKeyDefinition();
                    newFK.setName(modFK.getName());
                    newFK.setID(modFK.getID());
                    newFK.setSlaveTableName(tableName);
                    newFK.setMasterTableName(newMasterTableName);
                    newFK.setBidirectional(modFK.isBidirectional());
                    newFK.setConstraints(modFK.getConstraints());
                    newFK.setDescription(modFK.getDescription());
                    final List<String> locCols = modFK.childColumnNames();
                    final List<String> refCols = modFK.parentColumnNames();
                    for (int j = 0; j < locCols.size(); ++j) {
                        final ForeignKeyColumnDefinition fkCol = new ForeignKeyColumnDefinition();
                        fkCol.setLocalColumnDefinition(tableDefinition.getColumnDefinitionByName(locCols.get(j)));
                        fkCol.setReferencedColumnDefinition(referenceTableDefinition.getColumnDefinitionByName(refCols.get(j)));
                        newFK.addForeignKeyColumns(fkCol);
                    }
                    tableDefinition.addForeignKey(newFK);
                    this.findAndAddToRelatedTables(newFK);
                    continue;
                }
                case 7: {
                    final String delFKName = (String)ao.getAlterObject();
                    this.constraintNames.remove(delFKName);
                    final ForeignKeyDefinition fkToBeRemoved = this.fkMap.remove(delFKName);
                    final String masterTableName = fkToBeRemoved.getMasterTableName();
                    final String slaveTableName = fkToBeRemoved.getSlaveTableName();
                    tableDefinition.removeForeignKey(fkToBeRemoved.getName());
                    List foreignKeyList = this.referringFKsHash.get(masterTableName);
                    foreignKeyList.remove(fkToBeRemoved);
                    final List referringTableList = this.referringTablesHash.get(masterTableName);
                    if (referringTableList != null) {
                        referringTableList.remove(this.getTableDefinitionByName(slaveTableName));
                    }
                    if (foreignKeyList.isEmpty()) {
                        this.referringFKsHash.remove(masterTableName);
                    }
                    foreignKeyList = this.getForeignKeys(masterTableName, slaveTableName);
                    if (foreignKeyList != null && foreignKeyList.isEmpty()) {
                        final List refTables = this.referringTablesHash.get(masterTableName);
                        refTables.remove(slaveTableName);
                    }
                    this.fkMap.remove(fkToBeRemoved.getName());
                    continue;
                }
                case 4: {
                    final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)ao.getAlterObject();
                    final UniqueKeyDefinition ukd = new UniqueKeyDefinition();
                    ukd.setID(ukDef.getID());
                    ukd.setName(ukDef.getName());
                    final List<String> uniqCols = ukDef.getColumns();
                    for (final String ukColName : uniqCols) {
                        ukd.addColumn(ukColName);
                    }
                    tableDefinition.addUniqueKey(ukd);
                    this.addConstraintName(ukDef.getName());
                    continue;
                }
                case 5: {
                    final UniqueKeyDefinition uk = tableDefinition.removeUniqueKey((String)ao.getAlterObject());
                    this.constraintNames.remove(uk.getName());
                    continue;
                }
                case 15: {
                    final UniqueKeyDefinition uniqueKey = (UniqueKeyDefinition)ao.getAlterObject();
                    final UniqueKeyDefinition oldUK = tableDefinition.removeUniqueKey(uniqueKey.getName());
                    this.constraintNames.remove(oldUK.getName());
                    final UniqueKeyDefinition newUK = new UniqueKeyDefinition();
                    newUK.setID(uniqueKey.getID());
                    newUK.setName(uniqueKey.getName());
                    for (final String ukColName2 : uniqueKey.getColumns()) {
                        newUK.addColumn(ukColName2);
                    }
                    tableDefinition.addUniqueKey(newUK);
                    this.addConstraintName(newUK.getName());
                    continue;
                }
                case 8: {
                    final PrimaryKeyDefinition pk = new PrimaryKeyDefinition();
                    pk.setName((String)ao.getAlterObject());
                    tableDefinition.setPrimaryKey(pk);
                    this.constraintNames.remove(ao.getAlterObject());
                    continue;
                }
                case 9:
                case 17: {
                    PrimaryKeyDefinition pkDef;
                    if (operation == 17) {
                        pkDef = (PrimaryKeyDefinition)((Object[])ao.getAlterObject())[1];
                        this.constraintNames.remove(((Object[])ao.getAlterObject())[0].toString());
                    }
                    else {
                        pkDef = (PrimaryKeyDefinition)ao.getAlterObject();
                    }
                    final PrimaryKeyDefinition newPKDef = new PrimaryKeyDefinition();
                    newPKDef.setID(pkDef.getID());
                    newPKDef.setName(pkDef.getName());
                    newPKDef.setTableName(tableName);
                    for (final String colName : pkDef.getColumnList()) {
                        newPKDef.addColumnName(colName);
                    }
                    tableDefinition.setPrimaryKey(newPKDef);
                    this.addConstraintName(newPKDef.getName());
                    continue;
                }
                case 10: {
                    final IndexDefinition iDef = (IndexDefinition)ao.getAlterObject();
                    final IndexDefinition idxDef = new IndexDefinition();
                    idxDef.setID(iDef.getID());
                    idxDef.setName(iDef.getName());
                    for (final IndexColumnDefinition idxColDef : iDef.getColumnDefnitions()) {
                        final IndexColumnDefinition icd = new IndexColumnDefinition(idxColDef.getColumnDefinition(), idxColDef.getSize(), idxColDef.isAscending(), idxColDef.isNullsFirst());
                        idxDef.addIndexColumnDefinition(icd);
                    }
                    tableDefinition.addIndex(idxDef);
                    this.addConstraintName(iDef.getName());
                    continue;
                }
                case 11: {
                    final IndexDefinition idxDef = tableDefinition.removeIndex((String)ao.getAlterObject());
                    this.constraintNames.remove(idxDef.getName());
                    continue;
                }
                case 16: {
                    final IndexDefinition indexDef = (IndexDefinition)ao.getAlterObject();
                    final IndexDefinition oldIdx = tableDefinition.removeIndex(indexDef.getName());
                    this.constraintNames.remove(oldIdx.getName());
                    final IndexDefinition newIdx = new IndexDefinition();
                    newIdx.setID(indexDef.getID());
                    newIdx.setName(indexDef.getName());
                    for (final IndexColumnDefinition idxColDef2 : indexDef.getColumnDefnitions()) {
                        final IndexColumnDefinition icd2 = new IndexColumnDefinition(idxColDef2.getColumnDefinition(), idxColDef2.getSize(), idxColDef2.isAscending(), idxColDef2.isNullsFirst());
                        newIdx.addIndexColumnDefinition(icd2);
                    }
                    tableDefinition.addIndex(newIdx);
                    this.addConstraintName(newIdx.getName());
                    continue;
                }
                case 18: {
                    final Properties tableProp = (Properties)ao.getAlterObject();
                    for (final String key : ((Hashtable<Object, V>)tableProp).keySet()) {
                        if (key.equals("description")) {
                            final String newDesc = tableProp.getProperty(key);
                            tableDefinition.setDescription(newDesc);
                        }
                        else if (key.equals("display-name")) {
                            final String newDisplayName = tableProp.getProperty(key);
                            tableDefinition.setDisplayName(newDisplayName);
                        }
                        else if (key.equals("createtable")) {
                            final boolean createTable = Boolean.valueOf(tableProp.getProperty(key));
                            tableDefinition.setCreateTable(createTable);
                        }
                        else if (key.equals("modulename")) {
                            final String newModuleName = tableProp.getProperty(key);
                            final DataDictionary dD = this.moduleToEntitiesMap.get(tableDefinition.getModuleName());
                            final TableDefinition td = dD.getTableDefinitionByName(tableName);
                            dD.removeTableDefinition(tableName);
                            tableDefinition.setModuleName(newModuleName);
                            td.setModuleName(newModuleName);
                            this.moduleToEntitiesMap.get(newModuleName).addTableDefinition(td);
                        }
                        else {
                            if (!key.equals("dc-type")) {
                                continue;
                            }
                            final String oldDCType = tableDefinition.getDynamicColumnType();
                            String newDCType = tableProp.getProperty(key);
                            if (newDCType.isEmpty()) {
                                newDCType = null;
                            }
                            if (oldDCType != null && !oldDCType.equals("nodc") && (newDCType == null || newDCType.equals("nodc")) && tableDefinition.getDynamicColumnNames() != null) {
                                final List<String> dynamicColumnNames = new ArrayList<String>(tableDefinition.getDynamicColumnNames());
                                for (final String dynamicColumnName : dynamicColumnNames) {
                                    tableDefinition.removeColumnDefinition(dynamicColumnName);
                                }
                            }
                            tableDefinition.setDynamicColumnType(newDCType);
                        }
                    }
                    continue;
                }
                case 19: {
                    final ColumnDefinition addDynamicCD = (ColumnDefinition)ao.getAlterObject();
                    columnDefinition = new ColumnDefinition();
                    columnDefinition.setTableName(tableName);
                    columnDefinition.setColumnID(addDynamicCD.getColumnID());
                    columnDefinition.setColumnName(addDynamicCD.getColumnName());
                    columnDefinition.setDataType(addDynamicCD.getDataType());
                    columnDefinition.setDefaultValue(addDynamicCD.getDefaultValue());
                    columnDefinition.setMaxLength(addDynamicCD.getMaxLength());
                    columnDefinition.setNullable(addDynamicCD.isNullable());
                    columnDefinition.setAllowedValues(addDynamicCD.getAllowedValues());
                    columnDefinition.setUniqueValueGeneration(addDynamicCD.getUniqueValueGeneration());
                    columnDefinition.setDescription(addDynamicCD.getDescription());
                    columnDefinition.setDisplayName(addDynamicCD.getDisplayName());
                    columnDefinition.setPrecision(addDynamicCD.getPrecision());
                    columnDefinition.setSQLType(QueryUtil.getJavaSQLType(addDynamicCD.getDataType()));
                    columnDefinition.setDynamic(addDynamicCD.isDynamic());
                    columnDefinition.setUnique(addDynamicCD.isUnique());
                    columnDefinition.setPhysicalColumn(addDynamicCD.getPhysicalColumn());
                    tableDefinition.addColumnDefinition(columnDefinition);
                    continue;
                }
                case 20: {
                    final ColumnDefinition deleteDynamicCD = tableDefinition.removeColumnDefinition((String)ao.getAlterObject());
                    continue;
                }
                case 21: {
                    final ColumnDefinition modDynamicCD = (ColumnDefinition)ao.getAlterObject();
                    columnDefinition = tableDefinition.getColumnDefinitionByName(modDynamicCD.getColumnName());
                    columnDefinition.setDataType(modDynamicCD.getDataType());
                    columnDefinition.setDefaultValue(modDynamicCD.getDefaultValue());
                    columnDefinition.setMaxLength(modDynamicCD.getMaxLength());
                    columnDefinition.setNullable(modDynamicCD.isNullable());
                    columnDefinition.setAllowedValues(modDynamicCD.getAllowedValues());
                    columnDefinition.setUniqueValueGeneration(modDynamicCD.getUniqueValueGeneration());
                    columnDefinition.setDescription(modDynamicCD.getDescription());
                    columnDefinition.setDisplayName(modDynamicCD.getDisplayName());
                    columnDefinition.setSQLType(QueryUtil.getJavaSQLType(modDynamicCD.getDataType()));
                    columnDefinition.setDynamic(modDynamicCD.isDynamic());
                    columnDefinition.setPhysicalColumn(modDynamicCD.getPhysicalColumn());
                    columnDefinition.setPrecision(modDynamicCD.getPrecision());
                    columnDefinition.setUnique(modDynamicCD.isUnique());
                    continue;
                }
            }
        }
        if (template) {
            final TemplateMetaHandler templateMeta = this.getTemplateHandler(tableDefinition.getModuleName());
            templateMeta.alterTemplate(atq);
        }
        this.markIsUnique(tableDefinition);
    }
    
    private void addTableDefinitionToDefinitionMap(final String newTableName, final TableDefinition tableDefinition) {
        this.definitionsMap.put(newTableName, tableDefinition);
    }
    
    @Override
    public String getDefinedTableName(final String tableName) throws MetaDataException {
        if (tableName != null) {
            final String definedTableName = this.tableNameVsDefinedTableName.get(tableName.toLowerCase(Locale.ENGLISH));
            if (definedTableName != null) {
                return definedTableName;
            }
        }
        throw new MetaDataException("No such tableName exists :: [" + tableName + "]");
    }
    
    @Override
    public String getDefinedTableNameByDisplayName(final String displayName) throws MetaDataException {
        if (displayName != null) {
            final String displayNameinLC = displayName.toLowerCase(Locale.ENGLISH);
            if (this.displayNameVsDefinedTableName.containsKey(displayNameinLC)) {
                final String definedName = this.displayNameVsDefinedTableName.get(displayNameinLC);
                return definedName;
            }
        }
        return null;
    }
    
    @Override
    public String getDefinedColumnName(final String tableName, final String columnName) throws MetaDataException {
        final TableDefinition td = this.getTableDefinitionByName(this.getDefinedTableName(tableName));
        final String actualColName = td.getDefinedColumnName(columnName);
        if (actualColName == null) {
            throw new MetaDataException("No such column :: [" + columnName + "] exists in the tableName :: [" + tableName + "]");
        }
        return actualColName;
    }
    
    @Override
    public String getModuleNameOfTable(final String tableName) {
        if (tableName == null) {
            return null;
        }
        final Set entrySet = this.moduleToEntitiesMap.entrySet();
        for (final Map.Entry entry : entrySet) {
            final DataDictionary dd = entry.getValue();
            if (dd.getTableDefinitionByName(tableName) != null) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    @Override
    public Enumeration getAllTableDefinitions() throws MetaDataException {
        return Collections.enumeration(this.definitionsMap.values());
    }
    
    @Override
    public List getTableDefinitions() throws MetaDataException {
        final List list = new ArrayList(this.definitionsMap.values());
        return Collections.unmodifiableList((List<?>)list);
    }
    
    @Override
    public List<String> getTableNamesInDefinedOrder() throws MetaDataException {
        final List list = new ArrayList(this.definitionsMap.keySet());
        List sortedList = null;
        try {
            sortedList = PersistenceUtil.sortTables(list);
        }
        catch (final DataAccessException e) {
            throw new MetaDataException("Exception occured while sorting tables: " + e.getMessage());
        }
        return Collections.unmodifiableList((List<? extends String>)sortedList);
    }
    
    @Override
    public TableDefinition getTableDefinitionByName(final String tableName) throws MetaDataException {
        TableDefinition toRet = this.definitionsMap.get(tableName);
        if (toRet != null) {
            return toRet;
        }
        String templateTableName = null;
        for (final String moduleName : this.moduleToEntitiesMap.keySet()) {
            final TemplateMetaHandler tmh = this.moduleToEntitiesMap.get(moduleName).getTemplateMetaHandler();
            if (tmh != null) {
                templateTableName = tmh.getTemplateName(tableName);
                if (templateTableName == null) {
                    continue;
                }
                toRet = this.definitionsMap.get(templateTableName);
                if (toRet != null) {
                    return toRet;
                }
                continue;
            }
        }
        if (this.defaultTemplateHandler != null) {
            templateTableName = this.defaultTemplateHandler.getTemplateName(tableName);
            if (templateTableName != null) {
                return this.definitionsMap.get(templateTableName);
            }
        }
        return null;
    }
    
    @Override
    public ForeignKeyDefinition getForeignKeyDefinitionByName(final String fkName) throws MetaDataException {
        return this.fkMap.get(fkName);
    }
    
    @Override
    public DataDictionary getDataDictionary(final String moduleName) throws MetaDataException {
        return this.moduleToEntitiesMap.get(moduleName);
    }
    
    @Override
    public List getAllRelatedTableDefinitions(final String tableName) throws MetaDataException {
        return this.referringTablesHash.get(tableName);
    }
    
    @Override
    public List getForeignKeys(final String tableName1, final String tableName2) throws MetaDataException {
        final TableDefinition t1 = this.getTableDefinitionByName(tableName1);
        final TableDefinition t2 = this.getTableDefinitionByName(tableName2);
        if (t1 == null || t2 == null) {
            return null;
        }
        List fkDefs = this.findFKs(t1, t2.getTableName());
        if (fkDefs == null) {
            fkDefs = this.findFKs(t2, t1.getTableName());
        }
        return fkDefs;
    }
    
    private List findFKs(final TableDefinition slaveTable, final String masterTableName) {
        final List foreignKeys = slaveTable.getForeignKeyList();
        if (foreignKeys == null) {
            return null;
        }
        final List retList = new ArrayList();
        for (int size = foreignKeys.size(), i = 0; i < size; ++i) {
            final ForeignKeyDefinition fd = foreignKeys.get(i);
            if (fd.getMasterTableName().equals(masterTableName)) {
                retList.add(fd);
            }
        }
        return retList.isEmpty() ? null : retList;
    }
    
    @Override
    public void removeDataDictionaryConfiguration(final String moduleName) throws MetaDataException {
        if (moduleName == null || this.moduleToEntitiesMap.get(moduleName) == null) {
            throw new MetaDataException("Invalid module name " + moduleName);
        }
        final DataDictionary dd = this.moduleToEntitiesMap.get(moduleName);
        if (dd != null) {
            final List definitionList = dd.getTableDefinitions();
            for (int size = definitionList.size(), i = 0; i < size; ++i) {
                final TableDefinition tableDefinition = definitionList.get(i);
                this.removeReferencesOf(tableDefinition);
            }
        }
        this.moduleToEntitiesMap.remove(moduleName);
    }
    
    @Override
    public boolean removeTemplateInstance(final String templateTableName, final String instanceId) throws MetaDataException {
        final TableDefinition td = this.getTableDefinitionByName(templateTableName);
        if (td == null || !td.isTemplate()) {
            throw new IllegalArgumentException("No such template-table [" + templateTableName + "]");
        }
        final TemplateMetaHandler templateMeta = this.getTemplateHandler(td.getModuleName());
        return templateMeta.removeTemplateInstance(templateTableName, instanceId);
    }
    
    @Override
    public void removeTableDefinition(final String tableName) throws MetaDataException {
        String moduleName = null;
        if (tableName == null || (moduleName = this.getModuleNameOfTable(tableName)) == null) {
            throw new MetaDataException("Invalid table name " + tableName);
        }
        final DataDictionary dd = this.moduleToEntitiesMap.get(moduleName);
        if (dd == null) {
            LocalMetaDataInfo.OUT.log(Level.WARNING, "Problem in removeTableDefinition of [{0}]. No such module [{1}]", new Object[] { tableName, moduleName });
        }
        final TableDefinition tableDefinition = this.getTableDefinitionByName(tableName);
        this.removeReferencesOf(tableDefinition);
        LocalMetaDataInfo.OUT.log(Level.FINEST, "Removing the table :: [{0}] from the Cache", tableName);
        if (dd != null) {
            dd.removeTableDefinition(tableName);
        }
    }
    
    private void removeReferencesOf(final TableDefinition tableDefinition) {
        final String tableDefinitionName = tableDefinition.getTableName();
        LocalMetaDataInfo.OUT.log(Level.FINE, " Removing table definition {0}", tableDefinitionName);
        this.definitionsMap.remove(tableDefinitionName);
        this.tableNameVsDefinedTableName.remove(tableDefinition.getTableName().toLowerCase(Locale.ENGLISH));
        final String displayName = tableDefinition.getDisplayName().toLowerCase(Locale.ENGLISH);
        this.displayNameVsDefinedTableName.remove(displayName);
        this.constraintNames.remove(tableDefinition.getPrimaryKey().getName());
        if (tableDefinition.getIndexes() != null) {
            for (final IndexDefinition idx : tableDefinition.getIndexes()) {
                this.constraintNames.remove(idx.getName());
            }
        }
        if (tableDefinition.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition uk : tableDefinition.getUniqueKeys()) {
                this.constraintNames.remove(uk.getName());
            }
        }
        final List fkList = tableDefinition.getForeignKeyList();
        this.referringFKsHash.remove(tableDefinitionName);
        if (fkList != null) {
            for (int fkSize = fkList.size(), j = 0; j < fkSize; ++j) {
                final ForeignKeyDefinition fkDef = fkList.get(j);
                final String fkName = fkDef.getName();
                final ForeignKeyDefinition conDef = this.fkMap.remove(fkName);
                this.constraintNames.remove(fkName);
                final String masterTableName = fkDef.getMasterTableName();
                final List referringFKList = this.referringFKsHash.get(masterTableName);
                if (referringFKList != null) {
                    final Iterator itr = referringFKList.iterator();
                    while (itr.hasNext()) {
                        final ForeignKeyDefinition fk = itr.next();
                        if (fk.getName().equals(fkName)) {
                            itr.remove();
                            break;
                        }
                    }
                }
            }
        }
        this.referringTablesHash.remove(tableDefinitionName);
        for (final String tableName : this.referringTablesHash.keySet()) {
            final List referringTablesList = this.referringTablesHash.get(tableName);
            boolean loop = true;
            do {
                loop = referringTablesList.remove(tableDefinition);
            } while (loop);
        }
    }
    
    @Override
    public List getReferringForeignKeyDefinitions(final String tableName) {
        return this.referringFKsHash.get(tableName);
    }
    
    @Override
    public void dump() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<Information_in_MetaDataUtil>");
        buff.append("<definitionsMap>");
        for (final String defnName : this.definitionsMap.keySet()) {
            buff.append("<definition name=" + defnName + "/>");
        }
        buff.append("</definitionsMap>");
        buff.append("<moduleToEntitiesMap>");
        for (final String defnName : this.moduleToEntitiesMap.keySet()) {
            buff.append("<module name=" + defnName + " ddName=" + this.moduleToEntitiesMap.get(defnName).getName() + "/>");
        }
        buff.append("</moduleToEntitiesMap>");
        buff.append("<referringTablesHash>");
        for (final String defnName : this.referringTablesHash.keySet()) {
            buff.append("<table name=" + defnName + ">");
            final List referringTables = this.referringTablesHash.get(defnName);
            for (int i = 0; i < referringTables.size(); ++i) {
                buff.append("<referredTable name=" + referringTables.get(i).getTableName() + "/>");
            }
            buff.append("</table>");
        }
        buff.append("</referringTablesHash>");
        buff.append("<referringFKsHash>");
        for (final String defnName : this.referringFKsHash.keySet()) {
            buff.append("<table name=" + defnName + ">");
            final List referringFKs = this.referringFKsHash.get(defnName);
            for (int i = 0; i < referringFKs.size(); ++i) {
                buff.append("<referringFK name=" + referringFKs.get(i).getName() + "/>");
            }
            buff.append("</table>");
        }
        buff.append("</referringFKsHash>");
        buff.append("<fkMap>");
        for (final String defnName : this.fkMap.keySet()) {
            buff.append("<foreignKey name=" + defnName + " fkName=" + this.fkMap.get(defnName).getName() + "/>");
        }
        buff.append("</fkMap>");
        buff.append("</Information_in_MetaDataUtil>");
        LocalMetaDataInfo.OUT.log(Level.INFO, buff.toString());
    }
    
    @Override
    public void setLoaded(final boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
    
    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }
    
    private void addConstraintName(final String name) throws MetaDataException {
        if (!this.constraintNames.contains(name)) {
            this.constraintNames.add(name);
            return;
        }
        throw new MetaDataException("Already a tableName / constraintName has been defined with this name :: [" + name + "], hence cannot be reused.");
    }
    
    private void validateColumn(final TableDefinition td, final String columnName, final int throwException) throws MetaDataException {
        if (throwException == 1002 && td.getColumnDefinitionByName(columnName) == null) {
            throw new MetaDataException("No column with this name [" + columnName + "] found in this table [" + td.getTableName() + "]");
        }
        if (throwException == 1001 && td.getColumnDefinitionByName(columnName) != null) {
            throw new MetaDataException("Already a column with this name [" + columnName + "] found in this table [" + td.getTableName() + "]");
        }
    }
    
    private void validate_Table_Constraint_Name(final String name, final int throwException) throws MetaDataException {
        if (throwException == 1001) {
            if (this.definitionsMap.containsKey(name)) {
                throw new MetaDataException("Already a tableName with this name :: [" + name + "] found");
            }
            if (this.constraintNames.contains(name)) {
                throw new MetaDataException("Already a constraintName with this name :: [" + name + "] found");
            }
        }
        else if (throwException == 1002 && !this.constraintNames.contains(name)) {
            throw new MetaDataException("No constraint with this name [" + name + "] is defined");
        }
    }
    
    @Override
    public void validateAlterTableQuery(final AlterTableQuery alterTableQuery) throws MetaDataException {
        final String tableName = alterTableQuery.getTableName();
        final TableDefinition td = this.getTableDefinitionByName(tableName);
        final List<AlterOperation> operations = alterTableQuery.getAlterOperations();
        final List<ColumnDefinition> newColumns = new ArrayList<ColumnDefinition>();
        for (int i = 0; i < operations.size(); ++i) {
            final AlterOperation alterOperation = operations.get(i);
            if (alterOperation.getOperationType() == 1) {
                newColumns.add((ColumnDefinition)alterOperation.getAlterObject());
            }
        }
        for (final AlterOperation ao : operations) {
            switch (ao.getOperationType()) {
                case 1: {
                    final ColumnDefinition addColDef = (ColumnDefinition)ao.getAlterObject();
                    addColDef.validate();
                    if (addColDef.getColumnName().length() > MetaDataUtil.DBOBJECT_NAMELENGTH) {
                        throw new MetaDataException("The ColumnName \"" + addColDef.getColumnName() + "\" has " + addColDef.getColumnName().length() + " characters. But it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".");
                    }
                    this.validateColumn(td, addColDef.getColumnName(), 1001);
                    continue;
                }
                case 2: {
                    final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
                    colDef.validate();
                    this.validateColumn(td, colDef.getColumnName(), 1002);
                    if (td.getColumnDefinitionByName(colDef.getColumnName()).isDynamic()) {
                        throw new MetaDataException("The table is of dynamic type. Kindly use modifyDynamicColumn API instead of modifyColumn.");
                    }
                    final boolean isPkColumn = td.getPrimaryKey().getColumnList().contains(colDef.getColumnName());
                    final String oldDataType = td.getColumnDefinitionByName(colDef.getColumnName()).getDataType();
                    if (isPkColumn && (colDef.getDataType().equals("SCHAR") || colDef.getDataType().equals("BLOB") || colDef.getDataType().equals("SBLOB"))) {
                        throw new MetaDataException("The data-type of Primary-key column \"" + tableName + "." + colDef.getColumnName() + "\" cannot be changed from \"" + oldDataType + "\" to \"" + colDef.getDataType() + "\"");
                    }
                    continue;
                }
                case 3:
                case 20: {
                    final String delColName = (String)ao.getAlterObject();
                    this.validateColumn(td, delColName, 1002);
                    continue;
                }
                case 12:
                case 22: {
                    final String[] names = (String[])ao.getAlterObject();
                    this.validateColumn(td, names[0], 1002);
                    this.validateColumn(td, names[1], 1001);
                    final String newColumnName = names[1];
                    if (newColumnName.length() > MetaDataUtil.DBOBJECT_NAMELENGTH) {
                        throw new MetaDataException("The new ColumnName \"" + newColumnName + "\" has " + newColumnName.length() + " characters. But it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".");
                    }
                    continue;
                }
                case 7: {
                    final String delFKName = (String)ao.getAlterObject();
                    this.validate_Table_Constraint_Name(delFKName, 1002);
                    continue;
                }
                case 11: {
                    final String delIdxName = (String)ao.getAlterObject();
                    this.validate_Table_Constraint_Name(delIdxName, 1002);
                    continue;
                }
                case 5: {
                    final String delUKName = (String)ao.getAlterObject();
                    this.validate_Table_Constraint_Name(delUKName, 1002);
                    continue;
                }
                case 8: {
                    final String delPKName = (String)ao.getAlterObject();
                    this.validate_Table_Constraint_Name(delPKName, 1002);
                    final PrimaryKeyDefinition existingPK = td.getPrimaryKey();
                    if (existingPK.getColumnList().isEmpty()) {
                        throw new MetaDataException("Primary-key not exists for table \"" + tableName + "\".");
                    }
                    continue;
                }
                case 6:
                case 14: {
                    final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)ao.getAlterObject();
                    if (ao.getOperationType() == 6) {
                        this.validate_Table_Constraint_Name(fkDef.getName(), 1001);
                    }
                    else {
                        this.validate_Table_Constraint_Name(fkDef.getName(), 1002);
                    }
                    final TableDefinition masterTable = this.getTableDefinitionByName(fkDef.getMasterTableName());
                    if (masterTable == null) {
                        throw new MetaDataException("The master table \"" + fkDef.getMasterTableName() + "\" in foreign-key \"" + tableName + "." + fkDef.getName() + "\" doesn't exists.");
                    }
                    if (fkDef.getSlaveTableName() != null && !fkDef.getSlaveTableName().equals(tableName)) {
                        throw new MetaDataException("The table Name \"" + tableName + "\" and slave table Name \"" + fkDef.getSlaveTableName() + "\" were not same in the foreign-key\"" + fkDef.getName() + "\".");
                    }
                    ParserUtil.validateFK(masterTable, td, fkDef, newColumns);
                    continue;
                }
                case 10:
                case 16: {
                    final IndexDefinition idxDef = (IndexDefinition)ao.getAlterObject();
                    if (ao.getOperationType() == 10) {
                        this.validate_Table_Constraint_Name(idxDef.getName(), 1001);
                        this.validateIndexDefinition(td, idxDef);
                    }
                    else {
                        this.validate_Table_Constraint_Name(idxDef.getName(), 1002);
                    }
                    ParserUtil.validateIndexDefinition(td, idxDef, newColumns);
                    continue;
                }
                case 4:
                case 15: {
                    final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)ao.getAlterObject();
                    if (ao.getOperationType() == 4) {
                        this.validate_Table_Constraint_Name(ukDef.getName(), 1001);
                    }
                    else {
                        this.validate_Table_Constraint_Name(ukDef.getName(), 1002);
                    }
                    ParserUtil.validateUniqueKey(td, ukDef, newColumns);
                    continue;
                }
                case 13: {
                    final String newTableName = (String)ao.getAlterObject();
                    this.validate_Table_Constraint_Name(newTableName, 1001);
                    if (newTableName.length() > MetaDataUtil.DBOBJECT_NAMELENGTH) {
                        throw new MetaDataException("The new tableName \"" + newTableName + "\" has " + newTableName.length() + " characters. But it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".");
                    }
                    continue;
                }
                case 9: {
                    final PrimaryKeyDefinition existingPK = td.getPrimaryKey();
                    if (!existingPK.getColumnList().isEmpty()) {
                        throw new MetaDataException("Already Primary-key exists for table \"" + tableName + "\".");
                    }
                    ParserUtil.validatePrimaryKey(td, (PrimaryKeyDefinition)ao.getAlterObject(), newColumns);
                    continue;
                }
                case 17: {
                    final PrimaryKeyDefinition existingPK = td.getPrimaryKey();
                    if (existingPK.getColumnList().isEmpty()) {
                        throw new MetaDataException("Primary-key not exists for table \"" + tableName + "\".");
                    }
                    final String oldName = ((Object[])ao.getAlterObject())[0].toString();
                    this.validate_Table_Constraint_Name(oldName, 1002);
                    final PrimaryKeyDefinition pk = (PrimaryKeyDefinition)((Object[])ao.getAlterObject())[1];
                    ParserUtil.validatePrimaryKey(td, pk, newColumns);
                    continue;
                }
                case 18: {
                    final Properties tableProp = (Properties)ao.getAlterObject();
                    for (final String key : ((Hashtable<Object, V>)tableProp).keySet()) {
                        if (key.equals("dc-type") && !MigrationUtil.isMigrationRunning() && !DBMigrationUtil.isDBMigrationRunning()) {
                            throw new IllegalArgumentException("Cannot modify dc-type for the given table :: " + tableName);
                        }
                    }
                    if (!tableProp.containsKey("dc-type")) {
                        continue;
                    }
                    final String oldDcType = td.getDynamicColumnType();
                    final String newDcType = tableProp.getProperty("dc-type");
                    if (oldDcType != null && !newDcType.isEmpty() && !oldDcType.equals(newDcType)) {
                        throw new MetaDataException("The dc-type of table \"" + tableName + "\" cannot be changed from \"" + oldDcType + "\" to \"" + newDcType + "\".");
                    }
                    continue;
                }
                case 19: {
                    final ColumnDefinition addDyColDef = (ColumnDefinition)ao.getAlterObject();
                    addDyColDef.setDynamic(true);
                    addDyColDef.validate();
                    this.validateColumn(td, addDyColDef.getColumnName(), 1001);
                    continue;
                }
                case 21: {
                    final ColumnDefinition colDyDef = (ColumnDefinition)ao.getAlterObject();
                    colDyDef.setDynamic(true);
                    colDyDef.validate();
                    this.validateColumn(td, colDyDef.getColumnName(), 1002);
                    if (!td.getColumnDefinitionByName(colDyDef.getColumnName()).isDynamic()) {
                        throw new MetaDataException("The table is not of dynamic type. Kindly use modifyColumn API instead of modifyDynamicColumn.");
                    }
                    final boolean isPkCol = td.getPrimaryKey().getColumnList().contains(colDyDef.getColumnName());
                    final String oldDyDataType = td.getColumnDefinitionByName(colDyDef.getColumnName()).getDataType();
                    if (isPkCol && (colDyDef.getDataType().equals("SCHAR") || colDyDef.getDataType().equals("BLOB") || colDyDef.getDataType().equals("SBLOB"))) {
                        throw new MetaDataException("The data-type of Primary-key column \"" + tableName + "." + colDyDef.getColumnName() + "\" cannot be changed from \"" + oldDyDataType + "\" to \"" + colDyDef.getDataType() + "\"");
                    }
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Unknown operationType :: [" + ao.getOperationType() + "] found in this alterTableQuery :: " + alterTableQuery);
                }
            }
        }
    }
    
    @Override
    public void loadDynamicCustomDDAttributes() throws IOException, DataAccessException, MetaDataException {
        if (!this.areDynamicCustomAttrLoaded) {
            this.customAttributes.putAll(LocalMetaDataInfo.customAttributeHandler.loadDynamicCustomDDAttributes());
            this.areDynamicCustomAttrLoaded = true;
        }
    }
    
    @Override
    public String getAttribute(final String tableName, final String columnName, final String attributeName) throws MetaDataException {
        return this.getAttribute(this.getKey(tableName, columnName, attributeName));
    }
    
    @Override
    public String getAttribute(final String tableName, final String attributeName) throws MetaDataException {
        return this.getAttribute(this.getKey(tableName, null, attributeName));
    }
    
    @Override
    public String getAttribute(final String key) throws MetaDataException {
        try {
            this.loadDynamicCustomDDAttributes();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new MetaDataException("Exception occurred while loading dynamic custom Attributes", e);
        }
        return this.customAttributes.get(key);
    }
    
    private String getKey(final String tableName, final String columnName, final String attributeName) {
        String key = null;
        if (columnName != null) {
            key = tableName + "." + columnName + "." + attributeName;
        }
        else {
            key = tableName + "." + attributeName;
        }
        return key;
    }
    
    @Override
    public void loadCustomAttributes(final List<URL> listOfExtendedDDFiles, final boolean validate, final boolean loadDynamicAttributes) throws Exception {
        this.customAttributes.putAll(MetaDataUtil.getCustomAttributes(listOfExtendedDDFiles, validate));
        if (loadDynamicAttributes) {
            this.loadDynamicCustomDDAttributes();
        }
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String columnName, final String attributeName, final String value) throws MetaDataException, IOException, DataAccessException {
        final CustomAttributeValidator instance = MetaDataUtil.getValidator(attributeName);
        final String key = this.getKey(tableName, columnName, attributeName);
        if (instance.validateDynamicAttribute(key, this.getAttribute(key), value) && LocalMetaDataInfo.customAttributeHandler.setAttribute(tableName, columnName, attributeName, value)) {
            this.customAttributes.put(key, value);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String attributeName, final String value) throws MetaDataException, IOException, DataAccessException {
        return this.setAttribute(tableName, null, attributeName, value);
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String columnName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        if (LocalMetaDataInfo.customAttributeHandler.removeAttribute(tableName, columnName, attributeName)) {
            this.customAttributes.remove(this.getKey(tableName, columnName, attributeName));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        return this.removeAttribute(tableName, null, attributeName);
    }
    
    @Override
    public void removeCustomAttributeConfigurations() {
        this.customAttributes.clear();
    }
    
    static {
        OUT = Logger.getLogger(LocalMetaDataInfo.class.getName());
        LocalMetaDataInfo.getTdForAnyTableNameCase = false;
        LocalMetaDataInfo.customAttributeHandler = null;
    }
}
