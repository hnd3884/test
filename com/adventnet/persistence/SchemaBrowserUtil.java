package com.adventnet.persistence;

import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.util.DCManager;
import java.util.Properties;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Locale;
import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeMap;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataInfo;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.logging.Logger;

public class SchemaBrowserUtil
{
    private static final Logger LOGGER;
    private static boolean isReady;
    private static boolean canAppend;
    
    private SchemaBrowserUtil() throws Exception {
    }
    
    public static boolean isReady() {
        return SchemaBrowserUtil.isReady;
    }
    
    public static void addSBApplicationsForDD(final DataDictionary dd) throws DataAccessException {
        final String templateMetaHandler = (dd.getTemplateMetaHandler() != null) ? dd.getTemplateMetaHandler().getClass().getName() : null;
        final DataObject data = DataAccess.get("SB_Applications", new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), dd.getName(), 0));
        Row row = data.getRow("SB_Applications");
        if (row == null) {
            row = new Row("SB_Applications");
            row.set(2, dd.getName());
            row.set(3, dd.getDescription());
            row.set("TEMPLATE_META_HANDLER", templateMetaHandler);
            row.set("DC_TYPE", dd.getDynamicColumnType());
            data.addRow(row);
            DataAccess.add(data);
        }
    }
    
    public static DataObject getSBDO() throws DataAccessException {
        return getData(true);
    }
    
    public static void setReady(final boolean ready) {
        SchemaBrowserUtil.isReady = ready;
    }
    
    public static boolean isDDExists(final String moduleName) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), moduleName, 2);
        final DataObject data = DataAccess.get("SB_Applications", criteria, false);
        return data.getRows("SB_Applications", criteria).hasNext();
    }
    
    private static Object getApplicationID(final String applicationName, final DataObject data) throws DataAccessException {
        final Criteria c = new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), applicationName, 0);
        Row sbRow = data.getRow("SB_Applications", c);
        if (sbRow != null) {
            return sbRow.get("APPL_ID");
        }
        sbRow = DataAccess.get("SB_Applications", c).getRow("SB_Applications");
        return (sbRow == null) ? null : sbRow.get("APPL_ID");
    }
    
    private static Long getTableID(final String tableName) throws DataAccessException {
        final DataObject tableDO = DataAccess.get("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0), false);
        final Row row = tableDO.getRow("TableDetails");
        if (row == null) {
            return null;
        }
        return (Long)row.get(1);
    }
    
    private static Object getTableID(final String tableName, final DataObject data) throws DataAccessException {
        final Criteria c = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0);
        final Row tableRow = data.getRow("TableDetails", c);
        if (tableRow != null) {
            final Object tableID_DO = tableRow.get("TABLE_ID");
            if (tableID_DO != null) {
                return tableID_DO;
            }
        }
        try {
            final Object tableID_MetaData = MetaDataUtil.getTableDefinitionByName(tableName).getTableID();
            if (tableID_MetaData != null) {
                return tableID_MetaData;
            }
        }
        catch (final MetaDataException e) {
            throw new DataAccessException(e);
        }
        return getTableID(tableName);
    }
    
    private static Object getColumnID(final String tableName, final String columnName, final DataObject data) throws DataAccessException {
        final Object columnID_DO = getColumnID1(getTableID(tableName, data), columnName, data);
        if (columnID_DO != null) {
            return columnID_DO;
        }
        try {
            final Object columnID_MetaData = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName).getColumnID();
            if (columnID_MetaData != null) {
                return columnID_MetaData;
            }
        }
        catch (final MetaDataException e) {
            throw new DataAccessException(e);
        }
        return getColumnID(tableName, columnName);
    }
    
    private static Object getColumnID1(final Object tableID, final String columnName, final DataObject data) throws DataAccessException {
        Criteria c = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), columnName, 0);
        c = c.and(new Criteria(Column.getColumn("ColumnDetails", "TABLE_ID"), tableID, 0));
        final Row columnRow = data.getRow("ColumnDetails", c);
        return (columnRow == null) ? null : columnRow.get("COLUMN_ID");
    }
    
    public static void addTableDefinitionInDO(final String ddName, final TableDefinition td, final DataObject data) throws DataAccessException, MetaDataException {
        final Object applID = getApplicationID(ddName, data);
        final String tableName = td.getTableName();
        final Row tableRow = new Row("TableDetails");
        tableRow.set("TABLE_NAME", tableName);
        tableRow.set("TABLE_DESC", td.getDescription());
        tableRow.set("APPL_ID", applID);
        tableRow.set("DISPLAY_NAME", td.getDisplayName());
        tableRow.set("SYSTEM", td.isSystem());
        tableRow.set("CREATETABLE", td.creatable());
        tableRow.set("ISTEMPLATE", td.isTemplate());
        tableRow.set("DC_TYPE", td.getDynamicColumnType());
        tableRow.set("DIRTYWRITECHECKCOLS", (null != td.getDirtyWriteCheckColumnNames()) ? td.getDirtyWriteCheckColumnNames().toString().substring(1, td.getDirtyWriteCheckColumnNames().toString().length() - 1) : null);
        tableRow.set("TIP", (null != td.getTemplateInstancePatternName()) ? td.getTemplateInstancePatternName() : null);
        DataAccess.generateValues(tableRow);
        final Object tableID = tableRow.get(1);
        if (!tableID.equals(td.getTableID())) {
            if (td.getTableID() != null) {
                SchemaBrowserUtil.LOGGER.log(Level.WARNING, "addTableDefinitionsInDO :: TABLE_ID for table [{0}] is being over-written from [{1}] to [{2}]", new Object[] { td.getTableName(), td.getTableID(), tableID });
            }
            td.setTableID((Long)tableRow.get(1));
        }
        data.addRow(tableRow);
        final List<String> colNames = td.getColumnNames();
        for (int i = 0; i < colNames.size(); ++i) {
            final Row colRow = new Row("ColumnDetails");
            final String columnName = colNames.get(i);
            final ColumnDefinition colDef = td.getColumnDefinitionByName(columnName);
            setColumnAttributesInRow(colDef, colRow, tableID);
            data.addRow(colRow);
            DataAccess.generateValues(colRow);
            final Object columnID = colRow.get(1);
            if (!columnID.equals(colDef.getColumnID())) {
                if (colDef.getColumnID() != null) {
                    SchemaBrowserUtil.LOGGER.log(Level.WARNING, "addTableDefinitionsInDO :: COLUMN_ID for column [{0}] in table [{1}] is being over-written from [{2}] to [{3}]", new Object[] { colDef.getColumnName(), colDef.getTableName(), colDef.getColumnID(), columnID });
                }
                colDef.setColumnID((Long)columnID);
            }
            final AllowedValues alv = colDef.getAllowedValues();
            boolean addAllowedValuesInDO = false;
            if (DataTypeUtil.isEDT(colDef.getDataType())) {
                if (alv != null && !alv.equals(DataTypeManager.getDataTypeDefinition(colDef.getDataType()).getAllowedValues())) {
                    addAllowedValuesInDO = true;
                }
            }
            else {
                addAllowedValuesInDO = true;
            }
            if (alv != null && addAllowedValuesInDO) {
                if (alv.getFromVal() != null || alv.getToVal() != null) {
                    final Row dataRow = new Row("RangeValues");
                    dataRow.set(1, columnID);
                    dataRow.set(2, alv.getFromVal().toString());
                    dataRow.set(3, alv.getToVal().toString());
                    data.addRow(dataRow);
                }
                else {
                    final String pattern = alv.getPattern();
                    if (pattern != null) {
                        final Row dataRow2 = new Row("AllowedPattern");
                        dataRow2.set(1, columnID);
                        dataRow2.set(2, pattern);
                        data.addRow(dataRow2);
                    }
                    else {
                        final List valuesList = alv.getValueList();
                        if (valuesList != null) {
                            for (int k = 0; k < valuesList.size(); ++k) {
                                final Row dataRow3 = new Row("AllowedValues");
                                dataRow3.set(1, columnID);
                                dataRow3.set(2, valuesList.get(k).toString());
                                data.addRow(dataRow3);
                            }
                        }
                    }
                }
            }
        }
        final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
        List pkColumnList = null;
        if (pkDef != null) {
            pkColumnList = pkDef.getColumnList();
            final Row constDefRow = new Row("ConstraintDefinition");
            constDefRow.set(2, pkDef.getName());
            constDefRow.set(3, tableID);
            DataAccess.generateValues(constDefRow);
            final Long id = (Long)constDefRow.get(1);
            if (!id.equals(pkDef.getID())) {
                if (pkDef.getID() != null) {
                    SchemaBrowserUtil.LOGGER.log(Level.WARNING, "addTableDefinitionsInDO :: The constraint ID for the PK [{0}] is being changed from [{1}] to [{2}]", new Object[] { pkDef.getName(), pkDef.getID(), id });
                }
                pkDef.setID(id);
            }
            data.addRow(constDefRow);
            for (int j = 0; j < pkColumnList.size(); ++j) {
                final Row pkDefRow = new Row("PKDefinition");
                pkDefRow.set(2, constDefRow.get("CONSTRAINT_ID"));
                pkDefRow.set(1, getColumnID(tableName, pkColumnList.get(j), data));
                pkDefRow.set(3, new Integer(j + 1));
                data.addRow(pkDefRow);
            }
        }
        final List fkDefList = td.getForeignKeyList();
        if (!fkDefList.isEmpty()) {
            for (int l = 0; l < fkDefList.size(); ++l) {
                final ForeignKeyDefinition fkDef = fkDefList.get(l);
                final Row constDefRow2 = new Row("ConstraintDefinition");
                constDefRow2.set(2, fkDef.getName());
                constDefRow2.set(3, tableID);
                DataAccess.generateValues(constDefRow2);
                final Long id2 = (Long)constDefRow2.get(1);
                if (!id2.equals(fkDef.getID())) {
                    if (fkDef.getID() != null) {
                        SchemaBrowserUtil.LOGGER.log(Level.WARNING, "addTableDefinitionsInDO :: The constraint ID for the FK [{0}] is being changed from [{1}] to [{2}]", new Object[] { fkDef.getName(), fkDef.getID(), id2 });
                    }
                    fkDef.setID(id2);
                }
                data.addRow(constDefRow2);
                final Object fkDefConstID = constDefRow2.get(1);
                final Row fkDefRow = new Row("FKDefinition");
                fkDefRow.set(1, fkDefConstID);
                final Boolean isBidirectional = fkDef.isBidirectional();
                fkDefRow.set(4, isBidirectional);
                final String fkDesc = fkDef.getDescription();
                if (fkDesc != null) {
                    fkDefRow.set(5, fkDesc);
                }
                fkDefRow.set(3, fkDef.getConstraintsAsString());
                final String masterTableName = fkDef.getMasterTableName();
                final String slaveTableName = fkDef.getSlaveTableName();
                final Object masterId = getTableID(masterTableName, data);
                final Object slaveId = getTableID(slaveTableName, data);
                fkDefRow.set(2, masterId);
                data.addRow(fkDefRow);
                final List fkColumns = fkDef.getForeignKeyColumns();
                Object localColId = null;
                Object refColId = null;
                for (int m = 0; m < fkColumns.size(); ++m) {
                    final ForeignKeyColumnDefinition fkDefColDef = fkColumns.get(m);
                    ColumnDefinition colDef2 = fkDefColDef.getLocalColumnDefinition();
                    localColId = getColumnID(slaveTableName, colDef2.getColumnName(), data);
                    colDef2 = fkDefColDef.getReferencedColumnDefinition();
                    refColId = getColumnID(masterTableName, colDef2.getColumnName(), data);
                    final Row fkColDefRow = new Row("FKColumnDefinition");
                    fkColDefRow.set(1, fkDefConstID);
                    fkColDefRow.set(2, localColId);
                    fkColDefRow.set(3, refColId);
                    fkColDefRow.set(4, new Integer(m + 1));
                    data.addRow(fkColDefRow);
                }
            }
        }
        final List ukDefList = td.getUniqueKeys();
        if (ukDefList != null) {
            for (int j = 0; j < ukDefList.size(); ++j) {
                final UniqueKeyDefinition ukDef = ukDefList.get(j);
                final Row constDefRow3 = new Row("ConstraintDefinition");
                constDefRow3.set(2, ukDef.getName());
                constDefRow3.set(3, tableID);
                DataAccess.generateValues(constDefRow3);
                final Object consId = constDefRow3.get(1);
                if (!consId.equals(ukDef.getID())) {
                    if (ukDef.getID() != null) {
                        SchemaBrowserUtil.LOGGER.log(Level.WARNING, "addTableDefinitionsInDO :: The constraint ID for the UK [{0}] is being changed from [{1}] to [{2}]", new Object[] { ukDef.getName(), ukDef.getID(), consId });
                    }
                    ukDef.setID((Long)consId);
                }
                data.addRow(constDefRow3);
                final List ukColList = ukDef.getColumns();
                for (int m2 = 0; m2 < ukColList.size(); ++m2) {
                    final Object ukColumnID = getColumnID(tableName, ukColList.get(m2), data);
                    final Row ukDefRow = new Row("UniqueKeyDefinition");
                    ukDefRow.set(1, consId);
                    ukDefRow.set(2, ukColumnID);
                    ukDefRow.set(3, new Integer(m2 + 1));
                    data.addRow(ukDefRow);
                }
            }
        }
        final List indexes = td.getIndexes();
        if (indexes != null) {
            for (int i2 = 0; i2 < indexes.size(); ++i2) {
                final IndexDefinition idxDef = indexes.get(i2);
                final Row constDefRow4 = new Row("ConstraintDefinition");
                constDefRow4.set(2, idxDef.getName());
                constDefRow4.set(3, tableID);
                DataAccess.generateValues(constDefRow4);
                final Object consId2 = constDefRow4.get(1);
                if (!consId2.equals(idxDef.getID())) {
                    if (idxDef.getID() != null) {
                        SchemaBrowserUtil.LOGGER.log(Level.WARNING, "addTableDefinitionsInDO :: The constraint ID for the IDX [{0}] is being changed from [{1}] to [{2}]", new Object[] { idxDef.getName(), idxDef.getID(), consId2 });
                    }
                    idxDef.setID((Long)consId2);
                }
                data.addRow(constDefRow4);
                final List<IndexColumnDefinition> idxColDefList = idxDef.getColumnDefnitions();
                for (int idx = 0; idx < idxColDefList.size(); ++idx) {
                    final IndexColumnDefinition icd = idxColDefList.get(idx);
                    final Row idxDefRow = new Row("IndexDefinition");
                    idxDefRow.set(1, consId2);
                    idxDefRow.set(2, getColumnID(tableName, icd.getColumnName(), data));
                    idxDefRow.set(3, new Integer(idx + 1));
                    idxDefRow.set("SIZE", icd.getSize());
                    idxDefRow.set("ISASCENDING", icd.isAscending());
                    idxDefRow.set("ISNULLSFIRST", icd.isNullsFirst());
                    data.addRow(idxDefRow);
                }
            }
        }
    }
    
    public static DataObject getDOForDataDictionary(final DataDictionary dataDictionary) throws DataAccessException, MetaDataException {
        final String moduleName = dataDictionary.getName();
        DataObject ddDO = null;
        final List<TableDefinition> tabledefns = dataDictionary.getTableDefinitions();
        TableDefinition td = null;
        final int size = tabledefns.size();
        ddDO = DataAccess.constructDataObject();
        final String templateMetaHandler = (dataDictionary.getTemplateMetaHandler() != null) ? dataDictionary.getTemplateMetaHandler().getClass().getName() : null;
        final String dcType = dataDictionary.getDynamicColumnType();
        Row dataRow = null;
        Boolean isNewRow = Boolean.FALSE;
        if (PersistenceInitializer.onSAS()) {
            ddDO = DataAccess.get("SB_Applications", new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), dataDictionary.getName(), 0));
            dataRow = ddDO.getRow("SB_Applications");
        }
        else {
            isNewRow = Boolean.TRUE;
        }
        if (isNewRow || null == dataRow) {
            dataRow = new Row("SB_Applications");
            dataRow.set(2, dataDictionary.getName());
            dataRow.set(3, dataDictionary.getDescription());
            dataRow.set("TEMPLATE_META_HANDLER", templateMetaHandler);
            dataRow.set("DC_TYPE", dcType);
        }
        if (PersistenceInitializer.onSAS()) {
            ddDO.addRow(dataRow);
            DataAccess.add(ddDO);
        }
        else {
            DataAccess.generateValues(dataRow);
            ddDO.addRow(dataRow);
            for (int i = 0; i < size; ++i) {
                td = tabledefns.get(i);
                addTableDefinitionInDO(dataDictionary.getName(), td, ddDO);
            }
        }
        return ddDO;
    }
    
    public static void loadAllDDs(DataObject sbDO) throws Exception {
        if (sbDO == null) {
            sbDO = getSBDO();
        }
        DataDictionary dd = null;
        final SelectQuery sq = new SelectQueryImpl(new Table("SB_Applications"));
        sq.addSelectColumn(Column.getColumn(null, "*"));
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName("Module");
        sq.addJoin(new Join("SB_Applications", "Module", new String[] { "APPL_NAME" }, new String[] { "MODULENAME" }, 2));
        sq.addSortColumn(new SortColumn(Column.getColumn("Module", "MODULEORDER"), true));
        sq.setCriteria(new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), new String[] { "MetaPersistence" }, 9));
        final Map tableIDVsTableName = new HashMap();
        final Map columnIDVsColumnName = new HashMap();
        final DataObject sbApplDO = DataAccess.get(sq);
        final Iterator iterator = sbApplDO.getRows("SB_Applications");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String moduleName = (String)row.get("APPL_NAME");
            final DataDictionary ddFromMetaData = MetaDataUtil.getDataDictionary(moduleName);
            if (ddFromMetaData == null) {
                SchemaBrowserUtil.LOGGER.log(Level.INFO, "Loading module :: [" + moduleName + "]");
                dd = getDataDictionary(sbDO, moduleName, tableIDVsTableName, columnIDVsColumnName);
                SchemaBrowserUtil.LOGGER.log(Level.FINER, "DataDictionary being added to moduleName {0} is {1}", new Object[] { moduleName, dd });
                MetaDataUtil.addDataDictionaryConfiguration(dd);
                SchemaBrowserUtil.LOGGER.log(Level.FINE, "DataDictionary for the module :: [{0}] has been loaded sucessfully.", moduleName);
            }
        }
    }
    
    public static void loadAllDDs() throws Exception {
        loadAllDDs(null);
    }
    
    public static void loadAllModuleDDInMetaDataInfo(final MetaDataInfo metaDataInfo) throws DataAccessException, MetaDataException {
        final Map tableIDVsTableName = new HashMap();
        final Map columnIDVsColumnName = new HashMap();
        final DataObject sbDO = getData(null, true);
        final Iterator iterator = sbDO.getRows("SB_Applications");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String moduleName = (String)row.get("APPL_NAME");
            final DataDictionary dd = getDataDictionary(sbDO, moduleName, tableIDVsTableName, columnIDVsColumnName);
            SchemaBrowserUtil.LOGGER.log(Level.INFO, "DataDictionary being added to moduleName {0} is {1}", new Object[] { moduleName, dd });
            metaDataInfo.addDataDictionaryConfiguration(dd);
        }
    }
    
    public static DataDictionary getDataDictionary(final String moduleName) throws DataAccessException, MetaDataException {
        return getDataDictionary(null, moduleName, new HashMap(), new HashMap());
    }
    
    static DataDictionary getDataDictionary(DataObject data, final String moduleName, final Map tableIDVsTableName, final Map columnIDVsColumnName) throws DataAccessException, MetaDataException {
        DataDictionary dd = null;
        SchemaBrowserUtil.LOGGER.log(Level.FINER, "Fetching dd for module : " + moduleName);
        if (data == null) {
            data = getSBDO();
        }
        final Criteria criteria = new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), moduleName, 2);
        Row applnRow = null;
        Iterator iterator = data.getRows("SB_Applications", criteria);
        if (iterator.hasNext()) {
            applnRow = iterator.next();
            dd = new DataDictionary((String)applnRow.get("APPL_NAME"), false);
            dd.setDescription((String)applnRow.get("APPL_DESC"));
            dd.setTemplateMetaHandler((String)applnRow.get("TEMPLATE_META_HANDLER"));
            dd.setDynamicColumnType((String)applnRow.get("DC_TYPE"));
            iterator = data.getRows("TableDetails", applnRow);
            while (iterator.hasNext()) {
                final Row tRow = iterator.next();
                final String tableName = (String)tRow.get("TABLE_NAME");
                tableIDVsTableName.put(tRow.get("TABLE_ID"), tableName);
                if (dd.getTableDefinitionByName(tableName) == null) {
                    final TableDefinition tabDef = getTableDefinition(tableName, dd, data, tRow, tableIDVsTableName, columnIDVsColumnName);
                    dd.addTableDefinition(tabDef);
                }
            }
            return dd;
        }
        SchemaBrowserUtil.LOGGER.log(Level.FINER, "There is no such module named " + moduleName + " in schema browser");
        return null;
    }
    
    private static Row getTableRow(final String tableName, final DataObject data) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 2);
        final Row row = data.getRow("TableDetails", criteria);
        if (row != null) {
            return row;
        }
        return row;
    }
    
    public static TableDefinition getTableDefinition(final DataObject data, final Row tRow) throws DataAccessException, MetaDataException {
        final TableDefinition td = getTableDefinition(tRow, null);
        final Map columnIDVsColumnName = addColumnDefinitionsInTableDefinition(data, tRow, td);
        addConstraintDefinition(data, tRow, td, null, columnIDVsColumnName, new HashMap());
        return td;
    }
    
    private static TableDefinition getTableDefinition(final Row tRow, String tableName) {
        tableName = (String)((tableName == null) ? tRow.get("TABLE_NAME") : tableName);
        final Boolean isSystem = (Boolean)tRow.get("SYSTEM");
        final Boolean creatable = (Boolean)tRow.get("CREATETABLE");
        final Boolean isTemplate = (Boolean)tRow.get("ISTEMPLATE");
        final TableDefinition tabDef = new TableDefinition(isSystem, creatable, (String)tRow.get("DIRTYWRITECHECKCOLS"));
        tabDef.setTemplate(isTemplate);
        tabDef.setTableName(tableName);
        tabDef.setDisplayName((String)tRow.get("DISPLAY_NAME"));
        tabDef.setDescription((String)tRow.get("TABLE_DESC"));
        tabDef.setTableID((Long)tRow.get("TABLE_ID"));
        tabDef.setDynamicColumnType((String)tRow.get("DC_TYPE"));
        tabDef.setTemplateInstancePatternName((String)tRow.get("TIP"));
        return tabDef;
    }
    
    public static ColumnDefinition getColumnDefinition(final Row cRow, final String tableName) throws DataAccessException, MetaDataException {
        final ColumnDefinition colDef = new ColumnDefinition();
        colDef.setColumnID((Long)cRow.get("COLUMN_ID"));
        final String columnName = (String)cRow.get("COLUMN_NAME");
        colDef.setTableName(tableName);
        colDef.setColumnName(columnName);
        String dataType = (String)cRow.get("DATA_TYPE");
        if (dataType.startsWith("DECIMAL")) {
            if (dataType.indexOf(",") >= 0) {
                final String precision = dataType.substring(dataType.indexOf(",") + 1, dataType.indexOf(")"));
                colDef.setPrecision(new Integer(precision.trim()));
            }
            else {
                colDef.setPrecision(new Integer(2));
            }
            dataType = "DECIMAL";
        }
        if (DataTypeManager.isDataTypeSupported(dataType)) {
            final DataTypeDefinition dt = DataTypeManager.getDataTypeDefinition(dataType);
            colDef.setMaxLength(dt.getMaxLength());
            colDef.setPrecision(dt.getPrecision());
            colDef.setAllowedValues(dt.getAllowedValues());
        }
        final Integer maxSize = (Integer)cRow.get("MAX_SIZE");
        if (maxSize != null) {
            colDef.setMaxLength(maxSize);
        }
        colDef.setDataType(dataType);
        final int sqlType = QueryUtil.getJavaSQLType(dataType);
        colDef.setSQLType(sqlType);
        colDef.setDefaultValue(cRow.get("DEFAULT_VALUE"));
        colDef.setDescription((String)cRow.get("COLUMN_DESC"));
        colDef.setDisplayName((String)cRow.get("DISPLAY_NAME"));
        colDef.setNullable((boolean)cRow.get("ISNULLABLE"));
        String generationName = (String)cRow.get("GENERATOR_NAME");
        String genClass = null;
        if (generationName != null) {
            final UniqueValueGeneration generation = new UniqueValueGeneration();
            if (generationName.indexOf("/") > 0) {
                final String[] genDetails = generationName.split("/");
                generationName = genDetails[0];
                genClass = genDetails[1];
            }
            generation.setGeneratorName(generationName);
            generation.setNameColumn((String)cRow.get("NAME_COLUMN"));
            generation.setInstanceSpecificSequenceGenerator((boolean)cRow.get("IS_INSTANCESPECIFIC_SEQGEN"));
            if (genClass != null) {
                generation.setGeneratorClass(genClass);
            }
            colDef.setUniqueValueGeneration(generation);
        }
        colDef.setDynamic((boolean)cRow.get("IS_DYNAMIC"));
        colDef.setPhysicalColumn((String)cRow.get("PHYSICAL_COLUMN"));
        return colDef;
    }
    
    public static Map addColumnDefinitionsInTableDefinition(final DataObject data, final Row tRow, final TableDefinition tabDef) throws DataAccessException, MetaDataException {
        final Map columnIDVsColumnName = new HashMap();
        final String tableName = tabDef.getTableName();
        final Iterator iterator2 = data.getRows("ColumnDetails", tRow);
        while (iterator2.hasNext()) {
            final Row cRow = iterator2.next();
            final ColumnDefinition colDef = getColumnDefinition(cRow, tableName);
            columnIDVsColumnName.put(cRow.get("COLUMN_ID"), colDef.getColumnName());
            addAllowedValuesInColDefinition(data, cRow, colDef);
            colDef.init();
            tabDef.addColumnDefinition(colDef);
        }
        return columnIDVsColumnName;
    }
    
    public static void addAllowedValuesInColDefinition(final DataObject data, final Row cRow, final ColumnDefinition colDef) throws DataAccessException, MetaDataException {
        String dataType = colDef.getDataType();
        if (DataTypeUtil.isEDT(dataType)) {
            final DataTypeDefinition udt = DataTypeManager.getDataTypeDefinition(dataType);
            dataType = udt.getBaseType();
        }
        AllowedValues allowed = null;
        Iterator rangeIterator = data.getRows("RangeValues", cRow);
        if (rangeIterator.hasNext()) {
            final Row rangeRow = rangeIterator.next();
            allowed = new AllowedValues();
            final String fromVal = (String)rangeRow.get("RANGE_FROM");
            Object setVal = MetaDataUtil.convertToCorrespondingDataType(fromVal, dataType);
            allowed.setFromVal(setVal);
            final String toVal = (String)rangeRow.get("RANGE_TO");
            setVal = MetaDataUtil.convertToCorrespondingDataType(toVal, dataType);
            allowed.setToVal(setVal);
        }
        else {
            rangeIterator = data.getRows("AllowedValues", cRow);
            if (rangeIterator.hasNext()) {
                allowed = new AllowedValues();
                while (rangeIterator.hasNext()) {
                    final Row row = rangeIterator.next();
                    final Object value = row.get("VALUE");
                    final Object setVal = MetaDataUtil.convertToCorrespondingDataType((String)value, dataType);
                    allowed.addValue(setVal);
                }
            }
            else {
                final Iterator patternIterator = data.getRows("AllowedPattern", cRow);
                if (patternIterator.hasNext()) {
                    allowed = new AllowedValues();
                    final Row row2 = patternIterator.next();
                    allowed.setPattern((String)row2.get("PATTERN"));
                }
            }
        }
        if (allowed != null) {
            colDef.setAllowedValues(allowed);
        }
    }
    
    private static void addConstraintDefinition(final DataObject data, final Row tRow, final TableDefinition tabDef, final DataDictionary dd, final Map columnIDVsColumnName, final Map tableIDVsTableName) throws DataAccessException, MetaDataException {
        final Iterator consIterator = data.getRows("ConstraintDefinition", tRow);
        while (consIterator.hasNext()) {
            final Row consRow = consIterator.next();
            final ForeignKeyDefinition fkDef = getForeignKeyDefinition(data, consRow, tabDef, dd, columnIDVsColumnName, tableIDVsTableName);
            if (fkDef != null) {
                tabDef.addForeignKey(fkDef);
            }
            else {
                final UniqueKeyDefinition ukDef = getUniqueKeyDefinition(data, consRow, columnIDVsColumnName);
                if (ukDef != null) {
                    tabDef.addUniqueKey(ukDef);
                }
                else {
                    final PrimaryKeyDefinition pkDef = getPrimaryKeyDefinition(data, consRow, tabDef, columnIDVsColumnName);
                    if (pkDef != null) {
                        tabDef.setPrimaryKey(pkDef);
                    }
                    else {
                        final IndexDefinition iDef = getIndexDefinition(data, consRow, columnIDVsColumnName, tabDef);
                        if (iDef == null) {
                            continue;
                        }
                        tabDef.addIndex(iDef);
                    }
                }
            }
        }
        if (tabDef.getPrimaryKey() == null) {
            throw new MetaDataException("PrimaryKeyDefinition entry for table \"" + tabDef.getTableName() + "\" is missing in meta-data tables.");
        }
    }
    
    private static PrimaryKeyDefinition getPrimaryKeyDefinition(final DataObject data, final Row consRow, final TableDefinition tabDef, final Map columnIDVsColumnName) throws DataAccessException, MetaDataException {
        final String tableName = tabDef.getTableName();
        final String keyName = (String)consRow.get("CONSTRAINT_NAME");
        final Iterator pkIterator = data.getRows("PKDefinition", consRow);
        PrimaryKeyDefinition pkDef = null;
        if (pkIterator.hasNext()) {
            final TreeMap posMap = new TreeMap();
            pkDef = new PrimaryKeyDefinition();
            pkDef.setID((Long)consRow.get("CONSTRAINT_ID"));
            pkDef.setTableName(tableName);
            pkDef.setName(keyName);
            while (pkIterator.hasNext()) {
                final Row pkRow = pkIterator.next();
                final String columnName = columnIDVsColumnName.get(pkRow.get("PK_COLUMN_ID"));
                pkRow.get("PK_ID");
                posMap.put(pkRow.get("POSITION"), columnName);
            }
            for (final String colname : posMap.values()) {
                pkDef.addColumnName(colname);
            }
        }
        return pkDef;
    }
    
    private static ForeignKeyDefinition getForeignKeyDefinition(final DataObject data, final Row consRow, final TableDefinition tabDef, final DataDictionary dd, final Map columnIDVsColumnName, final Map tableIDVsTableName) throws DataAccessException, MetaDataException {
        final String tableName = tabDef.getTableName();
        final String keyName = (String)consRow.get("CONSTRAINT_NAME");
        final Row fetchRow = new Row("FKDefinition");
        fetchRow.set(1, consRow.get(1));
        final Row fkRow = data.findRow(fetchRow);
        ForeignKeyDefinition fkDef = null;
        if (fkRow != null) {
            fkDef = new ForeignKeyDefinition();
            fkDef.setName(keyName);
            fkDef.setSlaveTableName(tableName);
            fkDef.setID((Long)consRow.get("CONSTRAINT_ID"));
            fkDef.setDescription((String)fkRow.get("FK_DESC"));
            fkDef.setBidirectional((boolean)fkRow.get("IS_BIDIRECTIONAL"));
            final String constraintStr = (String)fkRow.get("FK_CONSTRAINT");
            final int constraints = getIntVal(constraintStr);
            fkDef.setConstraints(constraints);
            fkDef.setDescription((String)fkRow.get("FK_DESC"));
            Row parentRow = null;
            String masterTableName = getTableName(fkRow.get("FK_REF_TABLE_ID"));
            if (masterTableName == null) {
                masterTableName = tableIDVsTableName.get(fkRow.get("FK_REF_TABLE_ID"));
                if (masterTableName == null) {
                    parentRow = data.getRow("TableDetails", fkRow);
                    if (parentRow == null) {
                        throw new DataAccessException("Cannot find suitable fk reference table for the table [" + tableName + "] refTableID is [" + fkRow.get("FK_REF_TABLE_ID") + "]");
                    }
                    masterTableName = (String)parentRow.get("TABLE_NAME");
                }
            }
            fkDef.setMasterTableName(masterTableName);
            TableDefinition refTabDef = null;
            if (masterTableName.equals(tableName)) {
                refTabDef = tabDef;
            }
            else {
                refTabDef = getTableDefinitionByName(masterTableName, dd);
            }
            if (refTabDef == null) {
                if (parentRow == null) {
                    parentRow = data.getRow("TableDetails", fkRow);
                }
                refTabDef = getTableDefinition(masterTableName, dd, data, parentRow, tableIDVsTableName, columnIDVsColumnName);
                if (dd != null) {
                    dd.addTableDefinition(refTabDef);
                }
            }
            final Iterator fkColIterator = data.getRows("FKColumnDefinition", fkRow);
            final TreeMap posMap = new TreeMap();
            while (fkColIterator.hasNext()) {
                final Row fkColRow = fkColIterator.next();
                final String locColName = columnIDVsColumnName.get(fkColRow.get("FK_LOCAL_COL_ID"));
                String refColName = getColumnName(fkColRow.get("FK_REF_COL_ID"));
                if (refColName == null) {
                    refColName = columnIDVsColumnName.get(fkColRow.get("FK_REF_COL_ID"));
                }
                final ColumnDefinition locColDef = tabDef.getColumnDefinitionByName(locColName);
                final ColumnDefinition refColDef = refTabDef.getColumnDefinitionByName(refColName);
                final ForeignKeyColumnDefinition fkColDef = new ForeignKeyColumnDefinition();
                fkColDef.setLocalColumnDefinition(locColDef);
                fkColDef.setReferencedColumnDefinition(refColDef);
                posMap.put(fkColRow.get("POSITION"), fkColDef);
            }
            final Iterator tempIterator = posMap.values().iterator();
            while (tempIterator.hasNext()) {
                fkDef.addForeignKeyColumns(tempIterator.next());
            }
        }
        return fkDef;
    }
    
    private static UniqueKeyDefinition getUniqueKeyDefinition(final DataObject data, final Row consRow, final Map columnIDVsColumnName) throws DataAccessException, MetaDataException {
        final String keyName = (String)consRow.get("CONSTRAINT_NAME");
        final Iterator uniIterator = data.getRows("UniqueKeyDefinition", consRow);
        UniqueKeyDefinition ukDef = null;
        if (uniIterator.hasNext()) {
            final TreeMap posMap = new TreeMap();
            ukDef = new UniqueKeyDefinition();
            ukDef.setName(keyName);
            ukDef.setID((Long)consRow.get("CONSTRAINT_ID"));
            while (uniIterator.hasNext()) {
                final Row uRow = uniIterator.next();
                final String columnName = columnIDVsColumnName.get(uRow.get("COLUMN_ID"));
                posMap.put(uRow.get("POSITION"), columnName);
            }
            for (final String colname : posMap.values()) {
                ukDef.addColumn(colname);
            }
        }
        return ukDef;
    }
    
    private static IndexDefinition getIndexDefinition(final DataObject data, final Row consRow, final Map columnIDVsColumnName, final TableDefinition td) throws DataAccessException, MetaDataException {
        final String keyName = (String)consRow.get("CONSTRAINT_NAME");
        final Iterator indexIterator = data.getRows("IndexDefinition", consRow);
        IndexDefinition iDef = null;
        if (indexIterator.hasNext()) {
            final TreeMap posMap = new TreeMap();
            final HashMap<String, Integer> sizeMap = new HashMap<String, Integer>();
            final HashMap<String, Boolean> isAscMap = new HashMap<String, Boolean>();
            final HashMap<String, Boolean> isNullsFirstMap = new HashMap<String, Boolean>();
            iDef = new IndexDefinition();
            iDef.setID((Long)consRow.get("CONSTRAINT_ID"));
            iDef.setName(keyName);
            while (indexIterator.hasNext()) {
                final Row idxRow = indexIterator.next();
                final String columnName = columnIDVsColumnName.get(idxRow.get("COLUMN_ID"));
                posMap.put(idxRow.get("POSITION"), columnName);
                sizeMap.put(columnName, (Integer)idxRow.get("SIZE"));
                isAscMap.put(columnName, (Boolean)idxRow.get("ISASCENDING"));
                isNullsFirstMap.put(columnName, (Boolean)idxRow.get("ISNULLSFIRST"));
            }
            for (final String colName : posMap.values()) {
                iDef.addIndexColumnDefinition(new IndexColumnDefinition(td.getColumnDefinitionByName(colName), sizeMap.get(colName), isAscMap.get(colName), isNullsFirstMap.get(colName)));
            }
        }
        return iDef;
    }
    
    public static void clearCache() {
    }
    
    public static TableDefinition getTableDefinition(final String tableName) throws DataAccessException, MetaDataException {
        TableDefinition tabDef = null;
        tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        if (tabDef != null) {
            return tabDef;
        }
        final DataObject tableData = getTableData(tableName);
        return getTableDefinition(tableName, null, tableData, tableData.getRow("TableDetails"), new HashMap(), new HashMap());
    }
    
    private static TableDefinition getTableDefinition(final String tableName, final DataDictionary dd, final DataObject data, final Row tRow, final Map tableIDVsTableName, final Map columnIDVsColumnName) throws DataAccessException, MetaDataException {
        if (dd != null && dd.getTableDefinitionByName(tableName) != null) {
            return dd.getTableDefinitionByName(tableName);
        }
        TableDefinition tabDef = null;
        tabDef = getTableDefinition(tRow, tableName);
        tabDef.setModuleName(dd.getName());
        if (tabDef.getDynamicColumnType() == null) {
            tabDef.setDynamicColumnType(dd.getDynamicColumnType());
        }
        columnIDVsColumnName.putAll(addColumnDefinitionsInTableDefinition(data, tRow, tabDef));
        addConstraintDefinition(data, tRow, tabDef, dd, columnIDVsColumnName, tableIDVsTableName);
        return tabDef;
    }
    
    private static DataObject getTableData(final String tableName) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0, false);
        final DataObject tableDO = getData(criteria, false);
        return tableDO;
    }
    
    static DataObject getData(final boolean includeSB) throws DataAccessException {
        final List<String> tableNames = new ArrayList<String>(10);
        if (includeSB) {
            tableNames.add("SB_Applications");
        }
        tableNames.add("TableDetails");
        tableNames.add("ColumnDetails");
        tableNames.add("RangeValues");
        tableNames.add("AllowedValues");
        tableNames.add("ConstraintDefinition");
        tableNames.add("UniqueKeyDefinition");
        tableNames.add("PKDefinition");
        tableNames.add("FKDefinition");
        tableNames.add("FKColumnDefinition");
        tableNames.add("IndexDefinition");
        tableNames.add("AllowedPattern");
        final DataObject tableDO = new WritableDataObject();
        int count = 0;
        for (final String tableName : tableNames) {
            final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
            sq.addSelectColumn(Column.getColumn(null, "*"));
            if (tableName.equals("TableDetails")) {
                sq.addSortColumn(new SortColumn(Column.getColumn("TableDetails", "TABLE_ORDER"), true));
            }
            else if (tableName.equals("ColumnDetails")) {
                sq.addSortColumn(new SortColumn(Column.getColumn("ColumnDetails", "COLUMN_ID"), true));
            }
            else if (tableName.equals("ConstraintDefinition")) {
                sq.addSortColumn(new SortColumn(Column.getColumn("ConstraintDefinition", "CONSTRAINT_ID"), true));
            }
            else if (tableName.equals("UniqueKeyDefinition")) {
                sq.addSortColumn(new SortColumn(Column.getColumn("UniqueKeyDefinition", "POSITION"), true));
            }
            else if (tableName.equals("IndexDefinition")) {
                sq.addSortColumn(new SortColumn(Column.getColumn("IndexDefinition", "POSITION"), true));
            }
            final DataObject dataObject = DataAccess.get(sq, false);
            count += dataObject.size(tableName);
            tableDO.append(dataObject);
        }
        return tableDO;
    }
    
    public static DataObject getData(final Criteria criteria, final boolean includeSB) throws DataAccessException {
        final List tableNames = new ArrayList(10);
        if (includeSB) {
            tableNames.add("SB_Applications");
        }
        tableNames.add("TableDetails");
        tableNames.add("ColumnDetails");
        tableNames.add("RangeValues");
        tableNames.add("AllowedValues");
        tableNames.add("ConstraintDefinition");
        tableNames.add("UniqueKeyDefinition");
        tableNames.add("PKDefinition");
        tableNames.add("FKDefinition");
        tableNames.add("FKColumnDefinition");
        tableNames.add("IndexDefinition");
        tableNames.add("AllowedPattern");
        final List optionalTableNames = new ArrayList(5);
        optionalTableNames.add("RangeValues");
        optionalTableNames.add("ConstraintDefinition");
        optionalTableNames.add("AllowedValues");
        optionalTableNames.add("UniqueKeyDefinition");
        optionalTableNames.add("PKDefinition");
        optionalTableNames.add("FKDefinition");
        optionalTableNames.add("FKColumnDefinition");
        optionalTableNames.add("IndexDefinition");
        optionalTableNames.add("AllowedPattern");
        final SelectQuery query = QueryConstructor.get(tableNames, optionalTableNames, criteria);
        query.addSortColumn(new SortColumn(Column.getColumn("TableDetails", "TABLE_ORDER"), true));
        query.addSortColumn(new SortColumn(Column.getColumn("ColumnDetails", "COLUMN_ID"), true));
        query.addSortColumn(new SortColumn(Column.getColumn("ConstraintDefinition", "CONSTRAINT_ID"), true));
        query.addSortColumn(new SortColumn(Column.getColumn("UniqueKeyDefinition", "POSITION"), true));
        query.addSortColumn(new SortColumn(Column.getColumn("IndexDefinition", "POSITION"), true));
        try {
            if (getTableDefinition("ColumnDetails").hasColumn("IS_DYNAMIC")) {
                query.addSortColumn(new SortColumn(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), true));
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException(mde.getMessage(), mde);
        }
        final DataObject tableDO = DataAccess.get(query, false);
        return tableDO;
    }
    
    private static void addAllowedValuesInDO(final AllowedValues allowValues, final Row columnRow, final DataObject tableData) throws DataAccessException {
        Iterator allIterator = null;
        if (allowValues != null) {
            final List allowedValuesList = allowValues.getValueList();
            if (allowedValuesList != null && allowedValuesList.size() > 0) {
                allIterator = allowedValuesList.iterator();
                while (allIterator.hasNext()) {
                    final Row allowedRow = new Row("AllowedValues");
                    allowedRow.set(1, columnRow.get(1));
                    allowedRow.set(2, allIterator.next().toString());
                    tableData.addRow(allowedRow);
                }
            }
            final String pattern = allowValues.getPattern();
            if (pattern != null) {
                final Row allowedRow2 = new Row("AllowedPattern");
                allowedRow2.set(1, columnRow.get(1));
                allowedRow2.set(2, pattern);
                tableData.addRow(allowedRow2);
            }
            final Object from = allowValues.getFromVal();
            final Object to = allowValues.getToVal();
            if (from != null || to != null) {
                final Row allowedRow3 = new Row("RangeValues");
                allowedRow3.set(1, columnRow.get(1));
                allowedRow3.set(2, from.toString());
                allowedRow3.set(3, to.toString());
                tableData.addRow(allowedRow3);
            }
        }
    }
    
    private static void setColumnAttributesInRow(final ColumnDefinition cd, final Row dataRow, final Object tableId) {
        dataRow.set(3, cd.getColumnName());
        dataRow.set(2, tableId);
        dataRow.set(4, cd.getDataType());
        dataRow.set(10, new Integer(cd.getMaxLength()));
        final int precision = cd.getPrecision();
        if (cd.getDataType().equals("DECIMAL")) {
            dataRow.set(4, cd.getDataType() + "(" + dataRow.get("MAX_SIZE") + "," + precision + ")");
        }
        dataRow.set(11, cd.getDisplayName());
        dataRow.set(8, cd.getDescription());
        final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
        if (uvg != null) {
            String generatorName = uvg.getGeneratorName();
            final String genClass = uvg.getGeneratorClass();
            if (genClass != null) {
                generatorName = generatorName + "/" + genClass;
            }
            dataRow.set(6, generatorName);
            dataRow.set(7, uvg.getNameColumn());
            dataRow.set("IS_INSTANCESPECIFIC_SEQGEN", uvg.isInstanceSpecificSequenceGeneratorEnabled());
        }
        else {
            dataRow.set(6, null);
            dataRow.set(7, null);
            dataRow.set("IS_INSTANCESPECIFIC_SEQGEN", false);
        }
        if (cd.getDefaultValue() != null) {
            dataRow.set(9, cd.getDefaultValue().toString());
        }
        else {
            dataRow.set(9, null);
        }
        dataRow.set(5, cd.isNullable());
        dataRow.set(14, cd.isDynamic());
        dataRow.set(15, cd.getPhysicalColumn());
    }
    
    private static void updateAllowedValuesInDO(final AllowedValues allowValues, final Row columnRow, final DataObject tableData, final String dataType) throws DataAccessException, MetaDataException {
        final Iterator allValRows = tableData.getRows("AllowedValues", columnRow);
        List allowedValuesList = null;
        if (allowValues.getValueList() == null || allowValues.getValueList().size() <= 0) {
            tableData.deleteRows("AllowedValues", columnRow);
        }
        else if (allValRows != null) {
            allowedValuesList = new ArrayList(allowValues.getValueList());
            final List deleteList = new ArrayList();
            while (allValRows.hasNext()) {
                final Row allValRow = allValRows.next();
                Object value = MetaDataUtil.convert((String)allValRow.get("VALUE"), dataType);
                if (dataType.equals("CHAR") || dataType.equals("NCHAR")) {
                    value = ((String)value).toLowerCase(Locale.ENGLISH);
                }
                if (allowedValuesList.contains(value)) {
                    allowedValuesList.remove(value);
                }
                else {
                    deleteList.add(allValRow);
                }
            }
            if (deleteList.size() > 0) {
                for (final Row row : deleteList) {
                    tableData.deleteRow(row);
                }
            }
            if (allowedValuesList.size() > 0) {
                final Iterator allIterator = allowedValuesList.iterator();
                while (allIterator.hasNext()) {
                    final Row allowedRow = new Row("AllowedValues");
                    allowedRow.set(1, columnRow.get(1));
                    allowedRow.set(2, allIterator.next().toString());
                    tableData.addRow(allowedRow);
                }
            }
        }
        else if (allowedValuesList != null && allowedValuesList.size() > 0) {
            final Iterator allIterator2 = allowedValuesList.iterator();
            while (allIterator2.hasNext()) {
                final Row allowedRow2 = new Row("AllowedValues");
                allowedRow2.set(1, columnRow.get(1));
                allowedRow2.set(2, allIterator2.next().toString());
                tableData.addRow(allowedRow2);
            }
        }
        final Row allPattRow = tableData.getRow("AllowedPattern", columnRow);
        final String pattern = allowValues.getPattern();
        if (allPattRow != null) {
            if (pattern != null) {
                final Row allowedRow = new Row("AllowedPattern");
                allowedRow.set(1, columnRow.get(1));
                allowedRow.set(2, pattern);
                tableData.updateRow(allowedRow);
            }
            else {
                tableData.deleteRows("AllowedPattern", columnRow);
            }
        }
        else if (pattern != null) {
            final Row allowedRow = new Row("AllowedPattern");
            allowedRow.set(1, columnRow.get(1));
            allowedRow.set(2, pattern);
            tableData.addRow(allowedRow);
        }
        final Row ranValRow = tableData.getRow("RangeValues", columnRow);
        final Object from = allowValues.getFromVal();
        final Object to = allowValues.getToVal();
        if (ranValRow != null) {
            if (from != null || to != null) {
                final Row allowedRow3 = new Row("RangeValues");
                allowedRow3.set(1, columnRow.get(1));
                allowedRow3.set(2, from.toString());
                allowedRow3.set(3, to.toString());
                tableData.updateRow(allowedRow3);
            }
            else {
                tableData.deleteRows("RangeValues", columnRow);
            }
        }
        else if (from != null || to != null) {
            final Row allowedRow3 = new Row("RangeValues");
            allowedRow3.set(1, columnRow.get(1));
            allowedRow3.set(2, from.toString());
            allowedRow3.set(3, to.toString());
            tableData.addRow(allowedRow3);
        }
    }
    
    public static DataObject alterTableDefinition(final AlterTableQuery atq) throws MetaDataException, DataAccessException {
        final String tableName = atq.getTableName();
        final DataObject tableData = getTableData(tableName);
        if (tableData.isEmpty()) {
            return tableData;
        }
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
        AlterOperation ao = null;
        final Object tableId = tableData.getFirstValue("TableDetails", 1);
        for (int index = 0; index < atq.getAlterOperations().size(); ++index) {
            ao = atq.getAlterOperations().get(index);
            final int operation = ao.getOperationType();
            Row dataRow = null;
            switch (operation) {
                case 13: {
                    final String newTableName = (String)ao.getAlterObject();
                    final Row tableRow = tableData.getRow("TableDetails");
                    tableRow.set(3, newTableName);
                    tableData.updateRow(tableRow);
                    break;
                }
                case 1: {
                    final ColumnDefinition addColDef = (ColumnDefinition)ao.getAlterObject();
                    dataRow = new Row("ColumnDetails");
                    setColumnAttributesInRow(addColDef, dataRow, tableId);
                    DataAccess.generateValues(dataRow);
                    tableData.addRow(dataRow);
                    addColDef.setColumnID((Long)dataRow.get(1));
                    final AllowedValues allowValues = addColDef.getAllowedValues();
                    if (DataTypeManager.getDataTypeDefinition(addColDef.getDataType()) == null && allowValues != null) {
                        addAllowedValuesInDO(allowValues, dataRow, tableData);
                        break;
                    }
                    if (DataTypeUtil.isEDT(addColDef.getDataType()) && allowValues != null && !allowValues.equals(DataTypeManager.getDataTypeDefinition(addColDef.getDataType()).getAllowedValues())) {
                        addAllowedValuesInDO(allowValues, dataRow, tableData);
                        break;
                    }
                    break;
                }
                case 2:
                case 21: {
                    final ColumnDefinition modColDef = (ColumnDefinition)ao.getAlterObject();
                    final String modColName = modColDef.getColumnName();
                    final Iterator iterator = tableData.getRows("ColumnDetails", new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), modColName, 2));
                    if (!iterator.hasNext()) {
                        throw new MetaDataException("column to be modified is not in tha table's existing columns " + tableName + "." + modColName);
                    }
                    dataRow = iterator.next();
                    setColumnAttributesInRow(modColDef, dataRow, tableId);
                    tableData.updateRow(dataRow);
                    final AllowedValues allowValues = modColDef.getAllowedValues();
                    if (allowValues == null) {
                        tableData.deleteRows("AllowedValues", dataRow);
                        tableData.deleteRows("AllowedPattern", dataRow);
                        tableData.deleteRows("RangeValues", dataRow);
                        break;
                    }
                    if (!DataTypeUtil.isEDT(modColDef.getDataType())) {
                        updateAllowedValuesInDO(allowValues, dataRow, tableData, modColDef.getDataType());
                        break;
                    }
                    if (!allowValues.equals(DataTypeManager.getDataTypeDefinition(modColDef.getDataType()).getAllowedValues())) {
                        updateAllowedValuesInDO(allowValues, dataRow, tableData, modColDef.getDataType());
                        break;
                    }
                    break;
                }
                case 3:
                case 20: {
                    tableData.deleteRows("ColumnDetails", new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), ao.getAlterObject(), 2));
                    break;
                }
                case 12:
                case 22: {
                    final String[] names = (String[])ao.getAlterObject();
                    final Iterator iterator = tableData.getRows("ColumnDetails", new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), names[0], 2));
                    if (iterator.hasNext()) {
                        dataRow = iterator.next();
                        dataRow.set(3, names[1]);
                        tableData.updateRow(dataRow);
                        break;
                    }
                    throw new MetaDataException("Column to be renamed is not exist in the table " + tableName + "." + names[0]);
                }
                case 6: {
                    final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)ao.getAlterObject();
                    final String masterTableName = fkDef.getMasterTableName();
                    final DataObject masterTableDO = getTableData(masterTableName);
                    final Row constdef = new Row("ConstraintDefinition");
                    constdef.set(2, fkDef.getName());
                    constdef.set(3, tableId);
                    DataAccess.generateValues(constdef);
                    tableData.addRow(constdef);
                    final Long fkid = (Long)constdef.get("CONSTRAINT_ID");
                    fkDef.setID(fkid);
                    dataRow = new Row("FKDefinition");
                    dataRow.set(1, fkid);
                    final Object masterId = masterTableDO.getFirstValue("TableDetails", "TABLE_ID");
                    dataRow.set(2, masterId);
                    dataRow.set(4, fkDef.isBidirectional());
                    dataRow.set(5, fkDef.getDescription());
                    dataRow.set(3, fkDef.getConstraintsAsString());
                    tableData.addRow(dataRow);
                    final List<String> lc = fkDef.getFkColumns();
                    final List<String> rc = fkDef.getFkRefColumns();
                    Object localColId = null;
                    Object refColId = null;
                    for (int m = 0; m < lc.size(); ++m) {
                        localColId = getColumnID(tableName, lc.get(m), tableData);
                        refColId = getColumnID(fkDef.getMasterTableName(), rc.get(m), getTableData(fkDef.getMasterTableName()));
                        dataRow = new Row("FKColumnDefinition");
                        dataRow.set(1, fkid);
                        dataRow.set(2, localColId);
                        dataRow.set(3, refColId);
                        dataRow.set(4, new Integer(m + 1));
                        tableData.addRow(dataRow);
                    }
                    break;
                }
                case 14: {
                    final ForeignKeyDefinition fk = (ForeignKeyDefinition)ao.getAlterObject();
                    final DataObject referenceTableDO = getTableData(fk.getMasterTableName());
                    Criteria cr = new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), fk.getName(), 0);
                    final Row ro = tableData.getRow("ConstraintDefinition", cr);
                    final Long fkID = (Long)ro.get("CONSTRAINT_ID");
                    cr = new Criteria(Column.getColumn("FKDefinition", "FK_CONSTRAINT_ID"), fkID, 0);
                    final Row r = tableData.getRow("FKDefinition", cr);
                    r.set("FK_REF_TABLE_ID", referenceTableDO.getRow("TableDetails").get("TABLE_ID"));
                    r.set("IS_BIDIRECTIONAL", fk.isBidirectional());
                    r.set("FK_DESC", fk.getDescription());
                    r.set("FK_CONSTRAINT", fk.getConstraintsAsString());
                    tableData.updateRow(r);
                    cr = new Criteria(Column.getColumn("FKColumnDefinition", "FK_CONSTRAINT_ID"), fkID, 0);
                    tableData.deleteRows("FKColumnDefinition", cr);
                    final List<String> refCols = fk.parentColumnNames();
                    final List<String> locCols = fk.childColumnNames();
                    for (int i = 0; i < refCols.size(); ++i) {
                        final Long locColID = getColumnID(tableName, locCols.get(i));
                        final Long refColID = getColumnID(fk.getMasterTableName(), refCols.get(i));
                        final Row rw = new Row("FKColumnDefinition");
                        rw.set("FK_CONSTRAINT_ID", fkID);
                        rw.set("FK_LOCAL_COL_ID", locColID);
                        rw.set("FK_REF_COL_ID", refColID);
                        rw.set("POSITION", i + 1);
                        tableData.addRow(rw);
                    }
                    break;
                }
                case 7:
                case 8:
                case 11: {
                    tableData.deleteRows("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), ao.getAlterObject(), 2));
                    break;
                }
                case 5: {
                    final String delUkName = (String)ao.getAlterObject();
                    tableData.deleteRows("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), delUkName, 2));
                    break;
                }
                case 15: {
                    final UniqueKeyDefinition uk = (UniqueKeyDefinition)ao.getAlterObject();
                    final Row constarintRow = tableData.getRow("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), uk.getName(), 2));
                    final Long ukId = (Long)constarintRow.get("CONSTRAINT_ID");
                    tableData.deleteRows("UniqueKeyDefinition", new Criteria(Column.getColumn("UniqueKeyDefinition", "UNIQUE_CONS_ID"), ukId, 0));
                    final List<String> ukCols = uk.getColumns();
                    for (int j = 0; j < ukCols.size(); ++j) {
                        final Row ukDefRow = new Row("UniqueKeyDefinition");
                        ukDefRow.set("UNIQUE_CONS_ID", ukId);
                        ukDefRow.set("COLUMN_ID", getColumnID(tableName, ukCols.get(j), tableData));
                        ukDefRow.set(3, j + 1);
                        tableData.addRow(ukDefRow);
                    }
                    break;
                }
                case 4: {
                    final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)ao.getAlterObject();
                    final List columns = new ArrayList(2);
                    final List uniqCols = ukDef.getColumns();
                    final Iterator ucIterator = uniqCols.iterator();
                    while (ucIterator.hasNext()) {
                        columns.add(getColumnID(tableName, ucIterator.next(), tableData));
                    }
                    addUniqueKey(tableData, columns, ukDef, tableId);
                    break;
                }
                case 9: {
                    if (atq.isExecutable()) {
                        final PrimaryKeyDefinition pkDef = (PrimaryKeyDefinition)ao.getAlterObject();
                        pkDef.setID(td.getPrimaryKey().getID());
                        final Object pk_id = pkDef.getID();
                        final Row pkConstraintRow = new Row("ConstraintDefinition");
                        pkConstraintRow.set(2, pkDef.getName());
                        pkConstraintRow.set(3, tableId);
                        tableData.addRow(pkConstraintRow);
                        final Iterator iterator = pkDef.getColumnList().iterator();
                        int k = 1;
                        while (iterator.hasNext()) {
                            final String colName = iterator.next();
                            final Row colRow = tableData.getRow("ColumnDetails", new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), colName, 0));
                            final Long colID = (Long)colRow.get(1);
                            final Row newPkRow = new Row("PKDefinition");
                            newPkRow.set(2, pkConstraintRow.get(1));
                            newPkRow.set(1, colID);
                            newPkRow.set(3, k);
                            tableData.addRow(newPkRow);
                            ++k;
                        }
                        break;
                    }
                    break;
                }
                case 17: {
                    final PrimaryKeyDefinition pk = (PrimaryKeyDefinition)((Object[])ao.getAlterObject())[1];
                    final String oldPKName = ((Object[])ao.getAlterObject())[0].toString();
                    final Row constarintRow = tableData.getRow("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), oldPKName, 2));
                    final Long pkId = (Long)constarintRow.get("CONSTRAINT_ID");
                    constarintRow.set("CONSTRAINT_NAME", pk.getName());
                    tableData.updateRow(constarintRow);
                    tableData.deleteRows("PKDefinition", new Criteria(Column.getColumn("PKDefinition", "PK_ID"), pkId, 0));
                    final List<String> pkCols = pk.getColumnList();
                    for (int l = 0; l < pkCols.size(); ++l) {
                        final Row pkRow = new Row("PKDefinition");
                        pkRow.set("PK_ID", pkId);
                        pkRow.set("PK_COLUMN_ID", getColumnID(tableName, pkCols.get(l), tableData));
                        pkRow.set("POSITION", l + 1);
                        tableData.addRow(pkRow);
                    }
                    break;
                }
                case 16: {
                    final IndexDefinition indexDef = (IndexDefinition)ao.getAlterObject();
                    final Row constarintRow = tableData.getRow("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), indexDef.getName(), 2));
                    final Long idxId = (Long)constarintRow.get("CONSTRAINT_ID");
                    tableData.deleteRows("IndexDefinition", new Criteria(Column.getColumn("IndexDefinition", "INDEX_CONS_ID"), idxId, 0));
                    final List<IndexColumnDefinition> idxColDefList = indexDef.getColumnDefnitions();
                    for (int idx = 0; idx < idxColDefList.size(); ++idx) {
                        final IndexColumnDefinition icd = idxColDefList.get(idx);
                        final Object idxColId = null;
                        final Row idxDefRow = new Row("IndexDefinition");
                        idxDefRow.set(1, idxId);
                        idxDefRow.set(2, getColumnID(tableName, icd.getColumnName(), tableData));
                        idxDefRow.set(3, new Integer(idx + 1));
                        idxDefRow.set("SIZE", icd.getSize());
                        idxDefRow.set("ISASCENDING", icd.isAscending());
                        idxDefRow.set("ISNULLSFIRST", icd.isNullsFirst());
                        tableData.addRow(idxDefRow);
                    }
                    break;
                }
                case 10: {
                    final IndexDefinition idxDef = (IndexDefinition)ao.getAlterObject();
                    final List idxColumns = new ArrayList(2);
                    final List idxCols = idxDef.getColumns();
                    final Iterator idxIterator = idxCols.iterator();
                    while (idxIterator.hasNext()) {
                        idxColumns.add(getColumnID(tableName, idxIterator.next(), tableData));
                    }
                    addIndexKey(tableData, idxColumns, idxDef, tableId);
                    break;
                }
                case 18: {
                    final Properties tableProp = (Properties)ao.getAlterObject();
                    final Row tableRow = tableData.getRow("TableDetails");
                    for (final String key : ((Hashtable<Object, V>)tableProp).keySet()) {
                        if (key.equals("description")) {
                            final String newDesc = tableProp.getProperty(key);
                            tableRow.set("TABLE_DESC", newDesc.isEmpty() ? null : newDesc);
                        }
                        else if (key.equals("display-name")) {
                            final String newDisplayName = tableProp.getProperty(key);
                            tableRow.set("DISPLAY_NAME", newDisplayName);
                        }
                        else if (key.equals("createtable")) {
                            final boolean createTable = Boolean.valueOf(tableProp.getProperty(key));
                            tableRow.set("CREATETABLE", createTable);
                        }
                        else if (key.equals("modulename")) {
                            final String newModuleName = tableProp.getProperty(key);
                            final Object appl_id = getApplicationID(newModuleName, tableData);
                            if (appl_id == null) {
                                throw new MetaDataException("Entry for module \"" + newModuleName + "\" not exists in " + "SB_Applications" + " table.");
                            }
                            tableRow.set("APPL_ID", appl_id);
                        }
                        else {
                            if (!key.equals("dc-type")) {
                                continue;
                            }
                            final String oldDCType = td.getDynamicColumnType();
                            String newDCType = tableProp.getProperty(key);
                            if (newDCType.isEmpty()) {
                                newDCType = null;
                            }
                            if (oldDCType != null && !oldDCType.equals("nodc") && (newDCType == null || newDCType.equals("nodc"))) {
                                tableData.deleteRows("ColumnDetails", new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), true, 0));
                            }
                            tableRow.set("DC_TYPE", newDCType);
                        }
                    }
                    tableData.updateRow(tableRow);
                    break;
                }
                case 19: {
                    final ColumnDefinition addDyColDef = (ColumnDefinition)ao.getAlterObject();
                    dataRow = new Row("ColumnDetails");
                    setColumnAttributesInRow(addDyColDef, dataRow, tableId);
                    DataAccess.generateValues(dataRow);
                    tableData.addRow(dataRow);
                    addDyColDef.setColumnID((Long)dataRow.get(1));
                    final AllowedValues dyAllowValues = addDyColDef.getAllowedValues();
                    if (DataTypeManager.getDataTypeDefinition(addDyColDef.getDataType()) == null && dyAllowValues != null) {
                        addAllowedValuesInDO(dyAllowValues, dataRow, tableData);
                        break;
                    }
                    if (DataTypeUtil.isEDT(addDyColDef.getDataType()) && dyAllowValues != null && !dyAllowValues.equals(DataTypeManager.getDataTypeDefinition(addDyColDef.getDataType()).getAllowedValues())) {
                        addAllowedValuesInDO(dyAllowValues, dataRow, tableData);
                        break;
                    }
                    break;
                }
            }
        }
        return tableData;
    }
    
    private static void addIndexKey(final DataObject data, final List columns, final IndexDefinition idxDef, final Object tableId) throws DataAccessException {
        final Row constdef = new Row("ConstraintDefinition");
        constdef.set(2, idxDef.getName());
        constdef.set(3, tableId);
        DataAccess.generateValues(constdef);
        final Long consId = (Long)constdef.get(1);
        idxDef.setID(consId);
        data.addRow(constdef);
        final Row dataRow = null;
        final List<IndexColumnDefinition> idxColDefList = idxDef.getColumnDefnitions();
        for (int idx = 0; idx < idxColDefList.size(); ++idx) {
            final IndexColumnDefinition icd = idxColDefList.get(idx);
            final Row idxDefRow = new Row("IndexDefinition");
            idxDefRow.set(1, consId);
            idxDefRow.set(2, columns.get(idx));
            idxDefRow.set(3, new Integer(idx + 1));
            idxDefRow.set("SIZE", icd.getSize());
            idxDefRow.set("ISASCENDING", icd.isAscending());
            idxDefRow.set("ISNULLSFIRST", icd.isNullsFirst());
            data.addRow(idxDefRow);
        }
    }
    
    private static void addUniqueKey(final DataObject data, final List columns, final UniqueKeyDefinition ukDef, final Object tableId) throws DataAccessException {
        final String constraintName = ukDef.getName();
        final Row constdef = new Row("ConstraintDefinition");
        DataAccess.generateValues(constdef);
        final Long consId = (Long)constdef.get("CONSTRAINT_ID");
        ukDef.setID(consId);
        constdef.set(2, constraintName);
        constdef.set(3, tableId);
        data.addRow(constdef);
        Row dataRow = null;
        for (int m = 0; m < columns.size(); ++m) {
            dataRow = new Row("UniqueKeyDefinition");
            dataRow.set(1, consId);
            dataRow.set(2, columns.get(m));
            dataRow.set(3, m + 1);
            data.addRow(dataRow);
        }
    }
    
    private static TableDefinition getTableDefinitionByName(final String tableName, final DataDictionary dd) throws MetaDataException {
        TableDefinition td = null;
        td = ((dd != null) ? dd.getTableDefinitionByName(tableName) : null);
        if (td == null) {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        return td;
    }
    
    public static int getIntVal(final String constraintStr) throws MetaDataException {
        if (constraintStr.equalsIgnoreCase("ON-DELETE-CASCADE")) {
            return 1;
        }
        if (constraintStr.equalsIgnoreCase("ON-DELETE-SET-DEFAULT")) {
            return 3;
        }
        if (constraintStr.equalsIgnoreCase("ON-DELETE-SET-NULL")) {
            return 2;
        }
        if (constraintStr.equalsIgnoreCase("ON-DELETE-RESTRICT")) {
            return 0;
        }
        throw new MetaDataException("Unknown fk-constraint specified \"" + constraintStr + "\"");
    }
    
    public static void fillTableOrderInDO(DataObject dataObject) throws DataAccessException {
        dataObject = DataAccess.fillGeneratedValues(dataObject);
        final Iterator iterator = dataObject.getRows("TableDetails");
        while (iterator.hasNext()) {
            final Row tableRow = iterator.next();
            final Long tableId = (Long)tableRow.get(1);
            final long tableOrder = tableId * 10L;
            tableRow.set("TABLE_ORDER", new Long(tableOrder));
        }
    }
    
    static List updateSBWithPCData(final DataObject pcDO) throws DataAccessException, MetaDataException {
        throw new MetaDataException("Don't use SBCache use MetaDataUtil instead.");
    }
    
    static void dumpData() throws DataAccessException, MetaDataException {
        throw new MetaDataException("Don't use SBCache use MetaDataUtil instead.");
    }
    
    public static void init() throws DataAccessException {
        SchemaBrowserUtil.LOGGER.log(Level.WARNING, "Don't use this method, use getSBDO instead.");
    }
    
    private static String getTableName(final Object tableID) throws DataAccessException {
        final DataObject tableDO = DataAccess.get("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_ID"), tableID, 0), false);
        final Row row = tableDO.getRow("TableDetails");
        if (row == null) {
            return null;
        }
        return (String)row.get(3);
    }
    
    private static String getColumnName(final Object columnID) throws DataAccessException {
        final Criteria c = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_ID"), columnID, 0);
        final DataObject tableDO = DataAccess.get("ColumnDetails", c, false);
        final Row row = tableDO.getRow("ColumnDetails");
        if (row == null) {
            return null;
        }
        return (String)row.get(3);
    }
    
    private static Long getColumnID(final String tableName, final String columnName) throws DataAccessException {
        Criteria c = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0);
        c = c.and(Column.getColumn("ColumnDetails", "COLUMN_NAME"), columnName, 0);
        final DataObject tableDO = DataAccess.get("ColumnDetails", c, false);
        final Row row = tableDO.getRow("ColumnDetails");
        if (row == null) {
            return null;
        }
        return (Long)row.get(1);
    }
    
    public static String getOriginalContraintName(final String constraintName) {
        try {
            final DataObject tableDO = DataAccess.get("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_NAME"), constraintName, 0), false);
            final Row row = tableDO.getRow("ConstraintDefinition");
            if (row == null) {
                return null;
            }
            return (String)row.get(4);
        }
        catch (final DataAccessException ex) {
            return null;
        }
    }
    
    public static Map<String, Map<String, List<ColumnDefinition>>> getAllDynamicColumnDetails() throws DataAccessException, MetaDataException {
        final List<String> tableNames = new ArrayList<String>(2);
        tableNames.add("TableDetails");
        tableNames.add("ColumnDetails");
        final Criteria criteria = new Criteria(new Column("ColumnDetails", "IS_DYNAMIC"), true, 0);
        final DataObject tableDO = DataAccess.get(tableNames, criteria);
        final Map<String, Map<String, List<ColumnDefinition>>> dyCols = new HashMap<String, Map<String, List<ColumnDefinition>>>();
        final List<String> dcTypes = DCManager.getDCTypes();
        for (final String dcType : dcTypes) {
            dyCols.put(dcType, new HashMap<String, List<ColumnDefinition>>());
        }
        final Iterator tableRows = tableDO.getRows("TableDetails");
        while (tableRows.hasNext()) {
            final Row tableRow = tableRows.next();
            final String tableName = (String)tableRow.get("TABLE_NAME");
            final String dcType2 = MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnType();
            dyCols.get(dcType2).put(tableName, MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnList());
        }
        return dyCols;
    }
    
    public static HashMap getPatternValues(final String urlLocation) throws Exception {
        final SelectQuery query = new SelectQueryImpl(Table.getTable("ConfFile"));
        HashMap patternVsValue = null;
        try {
            query.addSelectColumn(Column.getColumn("UVHValues", "*"));
            final Join confToUVHValues = new Join("ConfFile", "UVHValues", new String[] { "FILEID" }, new String[] { "FILEID" }, 2);
            query.addJoin(confToUVHValues);
            final Column urlCol = Column.getColumn("ConfFile", "URL");
            final Criteria urlCriteria = new Criteria(urlCol, "*" + urlLocation, 2);
            query.setCriteria(urlCriteria);
            SchemaBrowserUtil.LOGGER.log(Level.INFO, " Query  to fetch uvh pattern  :" + RelationalAPI.getInstance().getSelectSQL(query));
            final DataObject sqlDO = DataAccess.get(query);
            if (sqlDO.size("UVHValues") > 0) {
                patternVsValue = new HashMap();
                final Iterator keyiter = sqlDO.get("UVHValues", "PATTERN");
                final Iterator valueiter = sqlDO.get("UVHValues", "GENVALUES");
                while (keyiter.hasNext()) {
                    patternVsValue.put(keyiter.next(), valueiter.next());
                }
            }
        }
        catch (final Exception x) {
            x.printStackTrace();
            throw x;
        }
        return patternVsValue;
    }
    
    public static DataObject getData(final Long... tableIDs) throws DataAccessException {
        final Criteria tableCR = new Criteria(Column.getColumn("TableDetails", "TABLE_ID"), tableIDs, 8);
        final Criteria columnCR = new Criteria(Column.getColumn("ColumnDetails", "TABLE_ID"), tableIDs, 8);
        final Criteria constraintCR = new Criteria(Column.getColumn("ConstraintDefinition", "TABLE_ID"), tableIDs, 8);
        final Table sbAppTable = Table.getTable("SB_Applications");
        final Table tbDetailTable = Table.getTable("TableDetails");
        final Table colDetailTable = Table.getTable("ColumnDetails");
        final Table rangeValTable = Table.getTable("RangeValues");
        final Table allowedValTable = Table.getTable("AllowedValues");
        final Table allowedPatTable = Table.getTable("AllowedPattern");
        final Table constDefTable = Table.getTable("ConstraintDefinition");
        final Table uniqueDefTable = Table.getTable("UniqueKeyDefinition");
        final Table pkDefTable = Table.getTable("PKDefinition");
        final Table indexDefTable = Table.getTable("IndexDefinition");
        final Table fkDefTable = Table.getTable("FKDefinition");
        final Table fkColumnDefTable = Table.getTable("FKColumnDefinition");
        final Join sbAppToTableJoin = new Join(sbAppTable, tbDetailTable, new String[] { "APPL_ID" }, new String[] { "APPL_ID" }, 1);
        final Join tableToColJoin = new Join(tbDetailTable, colDetailTable, new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 1);
        final Join tableToConsDefJoin = new Join(tbDetailTable, constDefTable, new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 1);
        final Join consDefToUniqueDefJoin = new Join(constDefTable, uniqueDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "UNIQUE_CONS_ID" }, 1);
        final Join consDefToPkDefJoin = new Join(constDefTable, pkDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "PK_ID" }, 1);
        final Join consDefToFkDefJoin = new Join(constDefTable, fkDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "FK_CONSTRAINT_ID" }, 1);
        final Join fkDefTofkColDefJoin = new Join(fkDefTable, fkColumnDefTable, new String[] { "FK_CONSTRAINT_ID" }, new String[] { "FK_CONSTRAINT_ID" }, 2);
        final Join colToRangeJoin = new Join(colDetailTable, rangeValTable, new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 1);
        final Join colToAllValJoin = new Join(colDetailTable, allowedValTable, new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 1);
        final Join colToAllPatJoin = new Join(colDetailTable, allowedPatTable, new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 1);
        SelectQuery query = new SelectQueryImpl(tbDetailTable);
        query.addSelectColumn(new Column("TableDetails", "*"));
        query.addSortColumn(new SortColumn(Column.getColumn("TableDetails", "TABLE_ORDER"), true));
        query.setCriteria(tableCR);
        final DataObject tableInfoDO = DataAccess.get(query, false);
        final Set<Long> appIDs = new HashSet<Long>();
        final Iterator<Row> iter = tableInfoDO.getRows("TableDetails");
        while (iter.hasNext()) {
            appIDs.add((Long)iter.next().get("APPL_ID"));
        }
        query = new SelectQueryImpl(sbAppTable);
        query.addSelectColumn(new Column("SB_Applications", "*"));
        query.setCriteria(new Criteria(Column.getColumn("SB_Applications", "APPL_ID"), appIDs.toArray(), 8));
        final DataObject sbAppInfoDO = DataAccess.get(query, false);
        query = new SelectQueryImpl(colDetailTable);
        query.addSelectColumn(new Column(null, "*"));
        query.addSortColumn(new SortColumn(Column.getColumn("ColumnDetails", "COLUMN_ID"), true));
        query.setCriteria(columnCR);
        query.addJoin(colToRangeJoin);
        query.addJoin(colToAllValJoin);
        query.addJoin(colToAllPatJoin);
        final DataObject columnDetailDO = DataAccess.get(query, false);
        query = new SelectQueryImpl(constDefTable);
        final Join CriteriaUniqueJoin = new Join(constDefTable, uniqueDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "UNIQUE_CONS_ID" }, 1);
        final Join CriteriaPkJoin = new Join(constDefTable, pkDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "PK_ID" }, 1);
        final Join CriteriaIndexJoin = new Join(constDefTable, indexDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "INDEX_CONS_ID" }, 1);
        final Join CriteriaFKJoin = new Join(constDefTable, fkDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "FK_CONSTRAINT_ID" }, 1);
        final Join CriteriafkColDefJoin = new Join(constDefTable, fkColumnDefTable, new String[] { "CONSTRAINT_ID" }, new String[] { "FK_CONSTRAINT_ID" }, 1);
        query.addSelectColumn(new Column(null, "*"));
        query.addJoin(CriteriaUniqueJoin);
        query.addJoin(CriteriaPkJoin);
        query.addJoin(CriteriaIndexJoin);
        query.addJoin(CriteriaFKJoin);
        query.addJoin(CriteriafkColDefJoin);
        query.setCriteria(new Criteria(Column.getColumn("ConstraintDefinition", "TABLE_ID"), tableIDs, 8));
        query.addSortColumn(new SortColumn(Column.getColumn("ConstraintDefinition", "CONSTRAINT_ID"), true));
        query.addSortColumn(new SortColumn(Column.getColumn("PKDefinition", "POSITION"), true));
        final DataObject combinedDefDO = DataAccess.get(query);
        final WritableDataObject returnDO = new WritableDataObject();
        returnDO.append(tableInfoDO);
        returnDO.append(sbAppInfoDO);
        returnDO.append(columnDetailDO);
        returnDO.append(combinedDefDO);
        returnDO.clearOperations();
        returnDO.addJoin(sbAppToTableJoin);
        returnDO.addJoin(tableToColJoin);
        returnDO.addJoin(tableToConsDefJoin);
        returnDO.addJoin(consDefToUniqueDefJoin);
        returnDO.addJoin(consDefToPkDefJoin);
        returnDO.addJoin(consDefToFkDefJoin);
        returnDO.addJoin(fkDefTofkColDefJoin);
        returnDO.addJoin(colToRangeJoin);
        returnDO.addJoin(colToAllValJoin);
        returnDO.addJoin(colToAllPatJoin);
        return returnDO;
    }
    
    static {
        LOGGER = Logger.getLogger(SchemaBrowserUtil.class.getName());
        SchemaBrowserUtil.isReady = false;
        SchemaBrowserUtil.canAppend = false;
    }
}
