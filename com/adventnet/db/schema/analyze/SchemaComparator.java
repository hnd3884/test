package com.adventnet.db.schema.analyze;

import java.util.Collections;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.zoho.db.model.ForeignKey;
import java.util.HashSet;
import com.zoho.db.model.UniqueKey;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.zoho.db.model.Index;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.adapter.postgres.PostgresDBAdapter;
import java.math.BigDecimal;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.HashMap;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.TreeMap;
import com.zoho.db.model.Table;
import java.util.Locale;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.persistence.template.TemplateUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Level;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.db.schema.analyze.notifier.SchemaAnalyzerProgressNotifier;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.List;
import javax.sql.DataSource;
import com.adventnet.db.adapter.DBAdapter;
import org.json.JSONArray;
import java.util.Map;
import java.util.logging.Logger;
import com.zoho.db.model.DataBase;

public class SchemaComparator
{
    private DataBase srcDatabase;
    private DataBase destDatabase;
    private static final Logger LOGGER;
    private Map<String, JSONArray> diffStrings;
    private DBAdapter srcDBAdapter;
    private DBAdapter dstDBAdapter;
    private DataSource srcDS;
    private DataSource dstDS;
    private SchemaComparatorHandler handler;
    private ComparatorType type;
    private static final int TINYINT_MAXVALUE = 120;
    private List<TableDefinition> tableDefinitionList;
    private SchemaAnalyzerProgressNotifier notifier;
    private SchemaAnalyzerUtil.DBType sourceType;
    private SchemaAnalyzerUtil.DBType dstType;
    private boolean exitOnFirstDiff;
    private boolean isModified;
    
    public SchemaComparator(final SchemaComparatorObject compObj, final DataBase srcDB, final DataBase dstDB, final int comparatorPoolSize, final Map<String, JSONArray> diffStrings, final SchemaAnalyzerProgressNotifier notifier) {
        this.diffStrings = null;
        this.handler = null;
        this.type = ComparatorType.METADATA_VS_DATABASE;
        this.tableDefinitionList = new ArrayList<TableDefinition>();
        this.notifier = null;
        this.exitOnFirstDiff = false;
        this.isModified = false;
        this.srcDBAdapter = compObj.getSrcDBAdapter();
        this.srcDS = compObj.getSrcDataSource();
        this.srcDatabase = srcDB;
        this.dstDBAdapter = compObj.getDestDBAdapter();
        this.dstDS = compObj.getDestDataSource();
        this.destDatabase = dstDB;
        this.handler = compObj.getComparatorHandler();
        if (this.handler != null) {
            this.handler.setComparatorType(ComparatorType.DATABASE_VS_DATABASE);
        }
        this.diffStrings = diffStrings;
        this.type = compObj.getComparatorType();
        this.notifier = notifier;
        this.sourceType = compObj.getSrcDBType();
        this.dstType = compObj.getDestDBType();
        this.exitOnFirstDiff = compObj.whetherToexitOnFirstDiff();
    }
    
    public SchemaComparator(final SchemaComparatorObject compObj, final List<TableDefinition> tds, final DataBase srcDB, final Map<String, JSONArray> diffStrings, final SchemaAnalyzerProgressNotifier notifier) {
        this.diffStrings = null;
        this.handler = null;
        this.type = ComparatorType.METADATA_VS_DATABASE;
        this.tableDefinitionList = new ArrayList<TableDefinition>();
        this.notifier = null;
        this.exitOnFirstDiff = false;
        this.isModified = false;
        this.srcDBAdapter = compObj.getSrcDBAdapter();
        this.srcDS = compObj.getSrcDataSource();
        this.srcDatabase = srcDB;
        this.dstDBAdapter = compObj.getSrcDBAdapter();
        this.dstDS = compObj.getSrcDataSource();
        this.destDatabase = srcDB;
        this.handler = compObj.getComparatorHandler();
        if (this.handler != null) {
            this.handler.setComparatorType(ComparatorType.METADATA_VS_DATABASE);
        }
        this.diffStrings = diffStrings;
        this.type = compObj.getComparatorType();
        this.notifier = notifier;
        this.sourceType = compObj.getSrcDBType();
        this.dstType = compObj.getSrcDBType();
        this.tableDefinitionList = tds;
        this.exitOnFirstDiff = compObj.whetherToexitOnFirstDiff();
    }
    
    public boolean isHandlerExists() {
        return this.handler != null;
    }
    
    private boolean isNotifierExists() {
        return this.notifier != null;
    }
    
    public boolean validateTableDefinitions() throws Exception {
        final List<String> tableNames = new ArrayList<String>(this.getAllTableNames());
        if (this.isNotifierExists()) {
            this.notifier.initialize(tableNames.size(), tableNames);
        }
        final Set<String> comparedTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        boolean isDiffExists = false;
        for (final TableDefinition sourceTd : this.tableDefinitionList) {
            if (!sourceTd.isTemplate() && sourceTd.creatable() && (!this.isHandlerExists() || this.handler.compareTableSchema(sourceTd.getTableName()))) {
                comparedTables.add(sourceTd.getTableName());
                isDiffExists = this.compareTableVsTableDefinition(sourceTd, this.sourceType, sourceTd.getTableName(), this.dstType);
                if (isDiffExists && this.exitOnFirstDiff) {
                    return isDiffExists;
                }
            }
            else {
                SchemaComparator.LOGGER.log(Level.INFO, "Skipped tableVsTableDefinition for the table [" + sourceTd.getTableName() + " since it is " + (sourceTd.isTemplate() ? "a template-table" : "non-creatable"));
            }
            if (this.isNotifierExists()) {
                this.notifier.updateProgress(sourceTd.getTableName());
            }
        }
        for (final String tableName : this.destDatabase.getAllTableNames()) {
            if (!comparedTables.contains(tableName)) {
                TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                if (tabDef != null && tabDef.isTemplate() && !tabDef.getTableName().equalsIgnoreCase(tableName)) {
                    tabDef = TemplateUtil.createTableDefnForTemplateInstance(tabDef, tableName.substring(tabDef.getTableName().length() + 1));
                }
                isDiffExists = this.compareTableVsTableDefinition(tabDef, this.sourceType, tableName, this.dstType);
                if (isDiffExists && this.exitOnFirstDiff) {
                    return isDiffExists;
                }
                if (!this.isNotifierExists()) {
                    continue;
                }
                this.notifier.updateProgress(tableName);
            }
        }
        SchemaComparator.LOGGER.log(Level.INFO, "FINAL DIFF strings :: " + this.diffStrings);
        if (this.isNotifierExists()) {
            this.notifier.printMessage("Schema Analysis completed successfully!!");
        }
        return this.isModified;
    }
    
    protected void addToTableVsDiffMap(final String tabName, final JSONObject obj) {
        if (!this.isHandlerExists() || !this.handler.isDiffIgnorable(tabName, obj)) {
            if (this.diffStrings.get(tabName) == null) {
                this.diffStrings.put(tabName, new JSONArray());
            }
            final JSONArray arr = this.diffStrings.get(tabName);
            arr.put((Object)obj);
            this.diffStrings.put(tabName, arr);
        }
    }
    
    private Set<String> getAllTableNames() {
        final Set<String> tableNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        if (this.type == ComparatorType.DATABASE_VS_DATABASE) {
            tableNames.addAll(this.srcDatabase.getAllTableNames());
        }
        else {
            for (final TableDefinition td : this.tableDefinitionList) {
                tableNames.add(td.getTableName());
            }
        }
        for (final String tableName : this.destDatabase.getAllTableNames()) {
            if (!tableNames.contains(tableName)) {
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }
    
    public boolean validateSchemas() throws Exception {
        final Set<String> comparedTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        final List<String> tableNames = new ArrayList<String>(this.getAllTableNames());
        if (this.isNotifierExists()) {
            this.notifier.initialize(tableNames.size(), tableNames);
        }
        boolean isDiffExists = false;
        for (final String tableName : this.srcDatabase.getAllTableNames()) {
            if (!this.isHandlerExists() || this.handler.compareTableSchema(tableName)) {
                comparedTables.add(tableName);
                SchemaComparator.LOGGER.info("Submitting table name [" + tableName + "] for validating.");
                final DiffGenerator diffGenerator = new DiffGenerator(this.srcDatabase.getTable(tableName), this.sourceType, this.destDatabase.getTable(tableName), this.dstType);
                isDiffExists = diffGenerator.compare();
                if (isDiffExists && this.exitOnFirstDiff) {
                    return isDiffExists;
                }
            }
            else {
                SchemaComparator.LOGGER.info("Skipping table name [" + tableName + "] for validating.");
            }
            if (this.isNotifierExists()) {
                this.notifier.updateProgress(tableName);
            }
        }
        for (final String tableName : this.destDatabase.getAllTableNames()) {
            if (!comparedTables.contains(tableName) && !tableName.equalsIgnoreCase("DBMProcessStats") && (!this.isHandlerExists() || this.handler.compareTableSchema(tableName))) {
                SchemaComparator.LOGGER.info("Submitting table name [" + tableName + "] for validating..");
                final DiffGenerator diffGenerator = new DiffGenerator(this.srcDatabase.getTable(tableName), this.sourceType, this.destDatabase.getTable(tableName), this.dstType);
                isDiffExists = diffGenerator.compare();
                if (isDiffExists && this.exitOnFirstDiff) {
                    return isDiffExists;
                }
                if (!this.isNotifierExists()) {
                    continue;
                }
                this.notifier.updateProgress(tableName);
            }
        }
        if (this.isNotifierExists()) {
            this.notifier.printMessage("Schema Comparison completed successfully!!");
        }
        return this.isModified;
    }
    
    protected boolean compareTableVsTableDefinition(final TableDefinition sourceTd, final SchemaAnalyzerUtil.DBType sourceType, final String dstTableName, final SchemaAnalyzerUtil.DBType dstType) throws Exception {
        final Table dstTable = this.destDatabase.getTable(dstTableName.toLowerCase(Locale.ENGLISH));
        if (dstTable == null && sourceTd == null) {
            SchemaComparator.LOGGER.severe("Table & Table Definition provided for schema comparator is null.");
            throw new IllegalArgumentException("Table & Table Definition provided for schema comparator is null.");
        }
        final DiffGenerator diffGenerator = new DiffGenerator(sourceTd, sourceType, dstTable, dstType);
        return diffGenerator.compare();
    }
    
    static {
        LOGGER = Logger.getLogger(SchemaComparator.class.getName());
    }
    
    public enum ComparatorType
    {
        DATABASE_VS_DATABASE, 
        METADATA_VS_DATABASE;
    }
    
    protected class DiffGenerator
    {
        private final Table srcTable;
        private final Table destTable;
        private TableDefinition sourceTd;
        private String tableName;
        private final SchemaAnalyzerUtil.DBType srcDBType;
        private final SchemaAnalyzerUtil.DBType dstDBType;
        private Map<String, String> fkIdxVsMap;
        private List<String> srcPKColumns;
        private List<String> destPKColumns;
        
        public DiffGenerator(final Table srcTable, final SchemaAnalyzerUtil.DBType srcType, final Table destTable, final SchemaAnalyzerUtil.DBType dstType) {
            this.fkIdxVsMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            this.srcPKColumns = new ArrayList<String>();
            this.destPKColumns = new ArrayList<String>();
            this.tableName = ((srcTable != null) ? srcTable.getName() : destTable.getName());
            this.srcTable = srcTable;
            this.destTable = destTable;
            this.srcDBType = srcType;
            this.dstDBType = dstType;
        }
        
        public DiffGenerator(final TableDefinition td, final SchemaAnalyzerUtil.DBType srcType, final Table dstTable, final SchemaAnalyzerUtil.DBType dstType) {
            this.fkIdxVsMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            this.srcPKColumns = new ArrayList<String>();
            this.destPKColumns = new ArrayList<String>();
            this.tableName = ((td != null) ? td.getTableName() : dstTable.getName());
            this.srcTable = null;
            this.sourceTd = td;
            this.destTable = dstTable;
            this.srcDBType = srcType;
            this.dstDBType = dstType;
        }
        
        public boolean compare() throws Exception {
            try {
                if (this.sourceTd != null) {
                    this.compareTableVsTableDefinition();
                }
                else {
                    this.compareTables();
                }
                return this.isModified();
            }
            catch (final Throwable e) {
                e.printStackTrace();
                throw new Exception(e);
            }
        }
        
        private void checkForAbandonedChild() throws Exception {
            if (!SchemaAnalyzerUtil.getConfiguration().isAbandonedRowsCheckEnabled()) {
                SchemaComparator.LOGGER.fine("Abandoned row checking is skipped due to configuration given in schema_analyzer.conf");
                return;
            }
            final List<ForeignKeyDefinition> fkDefs = MetaDataUtil.getTableDefinitionByName(this.tableName).getForeignKeyList();
            for (int i = 0; i < fkDefs.size(); ++i) {
                boolean isParentExists = true;
                final Set<String> colSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                colSet.addAll(fkDefs.get(i).getFkColumns());
                try (final Connection conn = SchemaComparator.this.dstDS.getConnection()) {
                    isParentExists = SchemaComparator.this.dstDBAdapter.isTablePresentInDB(conn, SchemaAnalyzerUtil.getSchema(SchemaComparator.this.dstDBAdapter.getSQLGenerator().getSchemaQuery(), conn), fkDefs.get(i).getMasterTableName());
                    final List<String> colListFromDB = SchemaComparator.this.dstDBAdapter.getColumnNamesFromDB(this.tableName, null, conn.getMetaData());
                    final Set<String> colSetFromDB = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                    colSetFromDB.addAll(colListFromDB);
                    if (!isParentExists) {
                        SchemaComparator.LOGGER.warning("Skipping checkForAbandonedChild for the Foreignkey definition because parent table is not present:: " + fkDefs.get(i));
                    }
                    else if (!colSetFromDB.containsAll(colSet)) {
                        SchemaComparator.LOGGER.warning("Skipping checkForAbandonedChild for the Foreignkey definition because the following columns does not exists:: " + fkDefs.get(i) + " The columns are:: " + colSet);
                    }
                    else if (fkDefs.get(i).getMasterTableName().equalsIgnoreCase(fkDefs.get(i).getSlaveTableName())) {
                        SchemaComparator.LOGGER.warning("Skipping checkForAbandonedChild for the Foreignkey definition because both parent and child tables are same:: " + fkDefs.get(i));
                    }
                    else {
                        final List<String> tableNames = new ArrayList<String>();
                        tableNames.add(fkDefs.get(i).getSlaveTableName());
                        tableNames.add(fkDefs.get(i).getMasterTableName());
                        SelectQuery sq = new SelectQueryImpl(com.adventnet.ds.query.Table.getTable(fkDefs.get(i).getSlaveTableName(), fkDefs.get(i).getSlaveTableName()));
                        final List<String> pkColumns = MetaDataUtil.getTableDefinitionByName(fkDefs.get(i).getSlaveTableName()).getPrimaryKey().getColumnList();
                        for (final String columnName : pkColumns) {
                            final Column column = Column.getColumn(fkDefs.get(i).getSlaveTableName(), columnName);
                            sq.addSelectColumn(column);
                        }
                        final Join join = new Join(fkDefs.get(i).getSlaveTableName(), fkDefs.get(i).getMasterTableName(), fkDefs.get(i).getFkColumns().toArray(new String[fkDefs.get(i).getFkColumns().size()]), fkDefs.get(i).getFkRefColumns().toArray(new String[fkDefs.get(i).getFkRefColumns().size()]), fkDefs.get(i).getSlaveTableName(), fkDefs.get(i).getMasterTableName(), 1);
                        sq.addJoin(join);
                        Criteria criteria = null;
                        for (final String columnName2 : fkDefs.get(i).getFkRefColumns()) {
                            final Column column2 = Column.getColumn(fkDefs.get(i).getMasterTableName(), columnName2);
                            criteria = ((criteria == null) ? new Criteria(column2, null, 0) : criteria.and(column2, null, 0));
                        }
                        if (criteria != null) {
                            for (final String columnName2 : fkDefs.get(i).getFkColumns()) {
                                final Column column2 = Column.getColumn(fkDefs.get(i).getSlaveTableName(), columnName2);
                                criteria = criteria.and(column2, null, 1);
                            }
                        }
                        sq.setCriteria(criteria);
                        sq = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery((Query)sq.clone());
                        RelationalAPI.getInstance().setDataType(sq);
                        DataSet ds = null;
                        try {
                            ds = RelationalAPI.getInstance().executeQuery(sq, conn);
                            final DataObject dobj = new WritableDataObject(ds);
                            if (!dobj.isEmpty()) {
                                final Iterator itr = dobj.getRows(fkDefs.get(i).getSlaveTableName());
                                final JSONArray jsonArr = new JSONArray();
                                while (itr.hasNext()) {
                                    final Row row = itr.next();
                                    final JSONObject pkJsonObj = new JSONObject();
                                    for (final String pkCol : pkColumns) {
                                        pkJsonObj.put(pkCol, row.get(pkCol));
                                    }
                                    jsonArr.put((Object)pkJsonObj);
                                }
                                this.addDiff(this.sourceTd.getModuleName(), this.sourceTd.getTableName(), "abandoned_childrows", fkDefs.get(i).getName(), "", jsonArr);
                            }
                        }
                        finally {
                            if (ds != null) {
                                ds.close();
                            }
                        }
                    }
                }
            }
        }
        
        private JSONObject getDiffObject(final String modName, final String tabName, final String diffType, final String name, final Object source, final Object dest) throws JSONException {
            final JSONObject obj = new JSONObject();
            obj.put("modulename", (Object)modName);
            obj.put("tablename", (Object)tabName);
            obj.put("difftype", (Object)diffType);
            obj.put("name", (Object)name);
            this.appendDiff(obj, source, dest);
            return obj;
        }
        
        private void addDiff(final String modName, final String tabName, final String diffType, final String name, final Object source, final Object dest) throws JSONException {
            final JSONObject obj = this.getDiffObject(modName, tabName, diffType, name, source, dest);
            SchemaComparator.this.addToTableVsDiffMap(tabName, obj);
            this.setModified(true);
        }
        
        private void compareTableVsTableDefinition() throws Exception {
            SchemaComparator.LOGGER.info("Started validating table definition " + this.tableName);
            if (SchemaComparator.this.isHandlerExists()) {
                SchemaComparator.this.handler.preInvoke(this.tableName);
            }
            if (this.compareTableVsTableDefintionAttributes()) {
                SchemaComparator.LOGGER.fine("Started validating PK columns of the table " + this.tableName);
                if ((!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.comparePKColumns(this.tableName)) && !this.comparePKColumns(this.destTable.getPrimaryKey().getPkColumnNames(), this.sourceTd.getPrimaryKey().getColumnList())) {
                    this.addDiff(this.sourceTd.getModuleName(), this.sourceTd.getTableName(), "primary_key_columns", this.sourceTd.getPrimaryKey().getName(), this.sourceTd.getPrimaryKey().getColumnList(), this.destTable.getPrimaryKey().getPkColumnNames());
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareColumns(this.tableName)) {
                    this.compareDbVsMetadata_Columns();
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareFKConstrains(this.tableName)) {
                    this.compareTableVsTableDefinition_ForeignKeys(this.destTable, this.sourceTd);
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareIndexes(this.tableName)) {
                    this.compareTableVsTableDefinition_Index();
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareUniqueConstraints(this.tableName)) {
                    this.compareTableVsTableDefinition_Unique();
                }
                this.checkForAbandonedChild();
            }
            if (SchemaComparator.this.isHandlerExists()) {
                SchemaComparator.this.handler.postInvoke(this.tableName, SchemaComparator.this.diffStrings.get(this.tableName));
            }
        }
        
        private void compareTables() throws Exception {
            SchemaComparator.LOGGER.info("Started validating table " + this.tableName);
            if (SchemaComparator.this.isHandlerExists()) {
                SchemaComparator.this.handler.preInvoke(this.tableName);
            }
            if (this.compareTableAttributes()) {
                SchemaComparator.LOGGER.fine("Started validating PK columns of the table " + this.tableName);
                if ((!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.comparePKColumns(this.tableName)) && !this.comparePKColumns(this.srcTable.getPrimaryKey().getPkColumnNames(), this.destTable.getPrimaryKey().getPkColumnNames())) {
                    this.addDiff("", this.tableName, "primary_key_columns", this.tableName, this.srcTable.getPrimaryKey().getPkColumnNames(), this.destTable.getPrimaryKey().getPkColumnNames());
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareColumns(this.tableName)) {
                    this.compareColumns();
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareFKConstrains(this.tableName)) {
                    this.compareForeignKeys(this.srcTable, this.destTable);
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareIndexes(this.tableName)) {
                    this.compareIndex();
                }
                if (!SchemaComparator.this.isHandlerExists() || SchemaComparator.this.handler.compareUniqueConstraints(this.tableName)) {
                    this.compareUnique();
                }
            }
            if (SchemaComparator.this.isHandlerExists()) {
                SchemaComparator.this.handler.postInvoke(this.tableName, SchemaComparator.this.diffStrings.get(this.tableName));
            }
        }
        
        private void compareColumns() throws Exception {
            SchemaComparator.LOGGER.fine("Started validating table columns " + this.tableName);
            final List<String> comparedColumnNames = new ArrayList<String>();
            for (final com.zoho.db.model.Column srcColumn : this.srcTable.getColumns()) {
                com.zoho.db.model.Column destCol = null;
                comparedColumnNames.add(srcColumn.getName());
                try {
                    destCol = this.destTable.getColumn(srcColumn.getName());
                }
                catch (final IllegalArgumentException ex) {}
                this.compareColumns(srcColumn, destCol);
            }
            for (final com.zoho.db.model.Column destColumn : this.destTable.getColumns()) {
                if (!comparedColumnNames.contains(destColumn.getName())) {
                    com.zoho.db.model.Column srcCol = null;
                    try {
                        srcCol = this.srcTable.getColumn(destColumn.getName());
                    }
                    catch (final IllegalArgumentException ex2) {}
                    this.compareColumns(srcCol, destColumn);
                }
            }
        }
        
        private void compareDbVsMetadata_Columns() throws Exception {
            SchemaComparator.LOGGER.fine("Started validating table columns " + this.tableName);
            final Set<String> comparedColumnNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            String sourceColName = null;
            final List<ColumnDefinition> colDefList = this.sourceTd.getColumnList();
            Map<String, String> options = null;
            Connection conn = null;
            try {
                if (this.srcDBType == SchemaAnalyzerUtil.DBType.POSTGRES) {
                    conn = SchemaComparator.this.srcDS.getConnection();
                    for (final ColumnDefinition sourceColumn : colDefList) {
                        if (sourceColumn.getDataType().equalsIgnoreCase("CHAR") || sourceColumn.getDataType().equalsIgnoreCase("NCHAR")) {
                            options = new HashMap<String, String>(this.getMaxSizeForCiText(this.sourceTd.getTableName(), SchemaComparator.this.srcDBAdapter, conn));
                        }
                    }
                }
            }
            finally {
                if (conn != null) {
                    conn.close();
                }
            }
            for (final ColumnDefinition sourceColumn : colDefList) {
                com.zoho.db.model.Column dstCol = null;
                try {
                    sourceColName = ((this.srcDBType == SchemaAnalyzerUtil.DBType.POSTGRES) ? sourceColumn.getColumnName().toLowerCase(Locale.ENGLISH) : sourceColumn.getColumnName());
                    comparedColumnNames.add(sourceColName);
                    dstCol = this.destTable.getColumn(sourceColName);
                }
                catch (final IllegalArgumentException ex) {}
                this.compareColumns(dstCol, sourceColumn, options);
            }
            for (final com.zoho.db.model.Column destColumn : this.destTable.getColumns()) {
                if (!comparedColumnNames.contains(destColumn.getName())) {
                    final ColumnDefinition destCol = null;
                    comparedColumnNames.add(destColumn.getName());
                    this.compareColumns(destColumn, destCol, options);
                }
            }
        }
        
        private boolean isMaxLengthComparable(final String dataType, final ColumnDefinition colDef, final SchemaAnalyzerUtil.DBType dbType) {
            final int maxLength = colDef.getMaxLength();
            final String upperCase = dataType.toUpperCase(Locale.ENGLISH);
            switch (upperCase) {
                case "CHAR":
                case "NCHAR":
                case "VARCHAR":
                case "NVARCHAR":
                case "CITEXT": {
                    return ((dbType == SchemaAnalyzerUtil.DBType.POSTGRES || dbType == SchemaAnalyzerUtil.DBType.MYSQL) && maxLength < 256) || (dbType == SchemaAnalyzerUtil.DBType.MSSQL && maxLength <= 4000 && maxLength != -1);
                }
                case "DECIMAL":
                case "NUMERIC": {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        private Object getMaxValue(final String tabName, final String colName) throws SQLException, QueryConstructionException {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            final com.adventnet.ds.query.Table table = com.adventnet.ds.query.Table.getTable(tabName);
            final SelectQuery sq = new SelectQueryImpl(table);
            sq.addSelectColumn(Column.getColumn(table.getTableName(), colName).maximum());
            Connection c = null;
            DataSet ds = null;
            Object maxVal = 0;
            try {
                c = SchemaComparator.this.dstDS.getConnection();
                ds = relapi.executeQuery(sq, c);
                while (ds.next()) {
                    maxVal = ds.getValue(1);
                }
            }
            finally {
                if (ds != null) {
                    ds.close();
                }
                if (c != null) {
                    c.close();
                }
            }
            return maxVal;
        }
        
        private String getColumnInformationAsString(final ColumnDefinition col) {
            if (col == null) {
                return null;
            }
            final StringBuffer sb = new StringBuffer("\ncolumn name :: " + col.getColumnName());
            sb.append("\n data type :: " + col.getDataType());
            sb.append("\n max size :: " + col.getMaxLength());
            sb.append("\n nullable :: " + col.isNullable());
            sb.append("\n default value :: " + ((col.getDefaultValue() == null) ? "" : col.getDefaultValue()));
            sb.append("\n unique :: " + col.isUnique());
            return sb.toString();
        }
        
        private String getColumnInformationAsString(final com.zoho.db.model.Column col) {
            if (col == null) {
                return null;
            }
            final StringBuffer sb = new StringBuffer("\ncolumn name :: " + col.getName());
            sb.append("\n data type :: " + col.getType());
            sb.append("\n max size :: " + col.getMaxSize());
            sb.append("\n nullable :: " + col.isNullable());
            sb.append("\n default value :: " + ((col.getDefaultValue() == null) ? "" : col.getDefaultValue()));
            sb.append("\n unique :: " + col.isUnique());
            return sb.toString();
        }
        
        private void compareColumns(final com.zoho.db.model.Column destColumn, final ColumnDefinition sourceCol, final Map<String, String> options) throws Exception {
            boolean proceed = true;
            final String columnName = (destColumn != null) ? destColumn.getName() : ((this.srcDBType == SchemaAnalyzerUtil.DBType.POSTGRES) ? sourceCol.getColumnName().toLowerCase(Locale.ENGLISH) : sourceCol.getColumnName());
            if (destColumn == null || sourceCol == null) {
                proceed = false;
                if (SchemaAnalyzerUtil.getConfiguration().getConfigurationValueAsBoolean("include.column.information")) {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "is_column_exists", columnName, this.getColumnInformationAsString(sourceCol), this.getColumnInformationAsString(destColumn));
                }
                else {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "is_column_exists", columnName, sourceCol != null, destColumn != null);
                }
            }
            if (proceed) {
                final String sourceDataType = SchemaComparator.this.srcDBAdapter.getSQLGenerator().getDBDataType(sourceCol).split("\\(")[0];
                final DataTypeAliases.AliasesType dstDataTypeAlias = DataTypeAliases.getDataTypeAliases(this.dstDBType, sourceDataType.replace(" ", ""));
                final String dstDataType = (dstDataTypeAlias != null && dstDataTypeAlias.contains(destColumn.getType())) ? dstDataTypeAlias.getName() : destColumn.getType();
                if (!this.isEquals(dstDataType, sourceDataType.replace(" ", ""))) {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "column_data_type", columnName, sourceDataType, dstDataType);
                }
                final int sourceMaxSize = sourceCol.getMaxLength();
                if (this.isMaxLengthComparable(sourceDataType, sourceCol, this.srcDBType) && SchemaComparator.this.dstDBAdapter.isMaxLengthComparable(dstDataType)) {
                    final int dstMaxSize = (this.dstDBType == SchemaAnalyzerUtil.DBType.POSTGRES && dstDataType.equalsIgnoreCase("citext")) ? Integer.valueOf((options.get(destColumn.getName()) != null) ? options.get(destColumn.getName()) : "-1") : destColumn.getMaxSize();
                    if (!this.isEquals(dstMaxSize, sourceMaxSize)) {
                        this.addDiff(this.sourceTd.getModuleName(), this.tableName, "column_max_size", columnName, sourceMaxSize, dstMaxSize);
                    }
                    if (!this.isEquals(destColumn.getPrecision(), sourceCol.getPrecision())) {
                        this.addDiff(this.sourceTd.getModuleName(), this.tableName, "column_precision", columnName, sourceCol.getPrecision(), destColumn.getPrecision());
                    }
                }
                if (sourceCol.getDataType().equalsIgnoreCase("TINYINT") && this.dstDBType == SchemaAnalyzerUtil.DBType.MYSQL) {
                    if (!SchemaAnalyzerUtil.getConfiguration().getConfigurationValueAsBoolean("check.tinyint.column.maxvalue")) {
                        SchemaComparator.LOGGER.info("Skipping maxvalue validation for tinyint column " + columnName + " for the table " + this.tableName);
                    }
                    else {
                        final Integer maxVal = (Integer)this.getMaxValue(this.tableName, columnName);
                        SchemaComparator.LOGGER.info("max value returned for the column " + columnName + "in the table " + this.tableName + " is " + maxVal);
                        if (maxVal != null && maxVal > 120) {
                            this.addDiff(this.sourceTd.getModuleName(), this.tableName, "tinyintcolumn_maxvalue", columnName, "", maxVal);
                        }
                    }
                }
                if (!this.srcPKColumns.contains(columnName.toUpperCase(Locale.ENGLISH)) && !this.destPKColumns.contains(columnName.toUpperCase(Locale.ENGLISH)) && ((destColumn.getDefaultValue() != null && !destColumn.getDefaultValue().isEmpty()) || sourceCol.getDefaultValue() != null)) {
                    final DataType.Type dstDataTypeIns = DataType.getDBSpecificDataType(this.dstDBType, destColumn.getType());
                    final String dstDefVal = dstDataTypeIns.getDefaultValueFromMetaDataString(destColumn.getDefaultValue(), dstDataTypeIns.toString());
                    Object convertedDstVal = dstDataTypeIns.getDefaultValue(dstDefVal);
                    final Object convertedSourceVal = sourceCol.getDefaultValue();
                    if (convertedSourceVal instanceof Boolean && convertedDstVal instanceof String) {
                        convertedDstVal = (((String)convertedDstVal).equalsIgnoreCase("1") || (!((String)convertedDstVal).equalsIgnoreCase("0") && !((String)convertedDstVal).equalsIgnoreCase("false")));
                    }
                    if (convertedSourceVal instanceof Long && !(convertedDstVal instanceof Long)) {
                        if (convertedDstVal instanceof String) {
                            convertedDstVal = Long.valueOf((String)convertedDstVal);
                        }
                        if (convertedDstVal instanceof Integer) {
                            convertedDstVal = convertedDstVal;
                        }
                    }
                    if (convertedSourceVal instanceof Integer && !(convertedDstVal instanceof Integer) && convertedDstVal instanceof String) {
                        convertedDstVal = Integer.valueOf((String)convertedDstVal);
                    }
                    if (convertedSourceVal instanceof BigDecimal && !(convertedDstVal instanceof BigDecimal)) {
                        if (convertedDstVal instanceof String) {
                            convertedDstVal = Long.valueOf((String)convertedDstVal);
                            convertedDstVal = BigDecimal.valueOf((long)convertedDstVal);
                        }
                        if (convertedDstVal instanceof Integer) {
                            convertedDstVal = BigDecimal.valueOf((int)convertedDstVal);
                        }
                        if (convertedDstVal instanceof Long) {
                            convertedDstVal = BigDecimal.valueOf((long)convertedDstVal);
                        }
                        if (convertedDstVal instanceof Double) {
                            convertedDstVal = BigDecimal.valueOf((double)convertedDstVal);
                        }
                    }
                    if (!this.isEquals(convertedDstVal, convertedSourceVal)) {
                        this.addDiff(this.sourceTd.getModuleName(), this.tableName, "column_default_value", columnName, convertedSourceVal, convertedDstVal);
                    }
                }
                if (!this.srcPKColumns.contains(columnName.toUpperCase(Locale.ENGLISH)) && !this.destPKColumns.contains(columnName.toUpperCase(Locale.ENGLISH)) && !this.isEquals(destColumn.isNullable(), sourceCol.isNullable())) {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "column_nullable", columnName, sourceCol.isNullable(), destColumn.isNullable());
                }
            }
        }
        
        private Map<String, String> getMaxSizeForCiText(final String tableName, final DBAdapter dbAdapter, final Connection conn) throws SQLException {
            if (this.dstDBType == SchemaAnalyzerUtil.DBType.POSTGRES) {
                final PostgresDBAdapter pgDbAdapter = (PostgresDBAdapter)dbAdapter;
                return pgDbAdapter.getCitextColumnVsMaxSize(tableName.toLowerCase(Locale.ENGLISH), conn);
            }
            return null;
        }
        
        private void compareTableVsTableDefinition_Index() throws JSONException, SQLException {
            SchemaComparator.LOGGER.fine("Started validating Indexes table " + this.tableName);
            final List<String> comparedIndexNames = new ArrayList<String>();
            if (this.sourceTd.getIndexes() != null) {
                for (final IndexDefinition sourceIndex : this.sourceTd.getIndexes()) {
                    final String destIndexName = (this.srcDBType == SchemaAnalyzerUtil.DBType.POSTGRES) ? sourceIndex.getName().toLowerCase(Locale.ENGLISH) : sourceIndex.getName();
                    comparedIndexNames.add(destIndexName);
                    final Index dstIndex = this.destTable.getIndex(destIndexName);
                    if (this.srcDBType == SchemaAnalyzerUtil.DBType.MSSQL && dstIndex == null && this.destTable.getUniqueKey(destIndexName) != null) {
                        continue;
                    }
                    this.compareIndex(dstIndex, sourceIndex);
                }
            }
            for (final Index dstIndex2 : this.destTable.getIndexes()) {
                if (!comparedIndexNames.contains(dstIndex2.getName())) {
                    comparedIndexNames.add(dstIndex2.getName());
                    if (this.fkIdxVsMap.containsKey(dstIndex2.getName())) {
                        continue;
                    }
                    if (this.srcDBType == SchemaAnalyzerUtil.DBType.MSSQL && this.sourceTd.getUniqueKeyDefinitionByName(dstIndex2.getName()) != null) {
                        continue;
                    }
                    this.compareIndex(null, dstIndex2);
                }
            }
        }
        
        private void compareIndex() throws JSONException, SQLException {
            SchemaComparator.LOGGER.fine("Started validating Indexes table " + this.tableName);
            final List<String> comparedIndexNames = new ArrayList<String>();
            for (final Index srcIndex : this.srcTable.getIndexes()) {
                comparedIndexNames.add(srcIndex.getName());
                Index destIndex = this.destTable.getIndex(srcIndex.getName());
                if (this.srcDBType == SchemaAnalyzerUtil.DBType.MSSQL && destIndex == null && this.destTable.getUniqueKey(srcIndex.getName()) != null) {
                    continue;
                }
                if (destIndex == null) {
                    final String idxName = this.fkIdxVsMap.get(srcIndex.getName().toUpperCase(Locale.ENGLISH));
                    if (idxName != null) {
                        destIndex = this.destTable.getIndex(idxName);
                    }
                }
                this.compareIndex(srcIndex, destIndex);
            }
            for (final Index destIndex2 : this.destTable.getIndexes()) {
                if (!comparedIndexNames.contains(destIndex2.getName())) {
                    Index srcIndex2 = this.srcTable.getIndex(destIndex2.getName());
                    if (this.dstDBType == SchemaAnalyzerUtil.DBType.MSSQL && srcIndex2 == null && this.srcTable.getUniqueKey(destIndex2.getName()) != null) {
                        continue;
                    }
                    if (srcIndex2 == null) {
                        final String idxName = this.fkIdxVsMap.get(destIndex2.getName().toUpperCase(Locale.ENGLISH));
                        if (idxName != null) {
                            srcIndex2 = this.srcTable.getIndex(idxName);
                        }
                    }
                    this.compareIndex(srcIndex2, destIndex2);
                }
            }
        }
        
        private void compareTableVsTableDefinition_Unique() throws JSONException, SQLException {
            SchemaComparator.LOGGER.fine("Started validating Indexes table " + this.tableName);
            final List<String> comparedUKNames = new ArrayList<String>();
            if (this.sourceTd.getUniqueKeys() != null) {
                for (final UniqueKeyDefinition sourceUK : this.sourceTd.getUniqueKeys()) {
                    final String sourceUKName = (this.srcDBType == SchemaAnalyzerUtil.DBType.POSTGRES) ? sourceUK.getName().toLowerCase(Locale.ENGLISH) : sourceUK.getName();
                    comparedUKNames.add(sourceUKName);
                    final UniqueKey dstUk = this.destTable.getUniqueKey(sourceUKName);
                    if (this.srcDBType == SchemaAnalyzerUtil.DBType.MSSQL && dstUk == null && this.destTable.getIndex(sourceUKName) != null) {
                        final Index dstIdx = this.destTable.getIndex(sourceUKName);
                        this.compareUK(sourceUKName, dstIdx.getName(), sourceUK.getColumns(), dstIdx.getIndexColumnNames());
                    }
                    else {
                        this.compareUK(sourceUK, dstUk);
                    }
                }
            }
            for (final UniqueKey destUk : this.destTable.getUniqueKeys()) {
                if (!comparedUKNames.contains(destUk.getName())) {
                    this.compareUK(destUk.getName(), null, destUk.getUkColumns(), null);
                }
            }
        }
        
        private void compareUnique() throws JSONException, SQLException {
            SchemaComparator.LOGGER.fine("Started validating Indexes table " + this.tableName);
            final List<String> comparedUKNames = new ArrayList<String>();
            for (final UniqueKey srcUk : this.srcTable.getUniqueKeys()) {
                comparedUKNames.add(srcUk.getName());
                final UniqueKey destUk = this.destTable.getUniqueKey(srcUk.getName());
                if (this.dstDBType == SchemaAnalyzerUtil.DBType.MSSQL && destUk == null && this.destTable.getIndex(srcUk.getName()) != null) {
                    final Index index = this.destTable.getIndex(srcUk.getName());
                    this.compareUK(srcUk.getName(), index.getName(), srcUk.getUkColumns(), index.getIndexColumnNames());
                }
                else {
                    this.compareUK(srcUk, destUk);
                }
            }
            for (final UniqueKey destUK : this.destTable.getUniqueKeys()) {
                if (!comparedUKNames.contains(destUK.getName())) {
                    final UniqueKey srcUk2 = this.srcTable.getUniqueKey(destUK.getName());
                    if (this.srcDBType == SchemaAnalyzerUtil.DBType.MSSQL && srcUk2 == null && this.srcTable.getIndex(destUK.getName()) != null) {
                        final Index srcIdx = this.srcTable.getIndex(destUK.getName());
                        this.compareUK(srcIdx.getName(), destUK.getName(), srcIdx.getIndexColumnNames(), destUK.getUkColumns());
                    }
                    else {
                        this.compareUK(srcUk2, destUK);
                    }
                }
            }
        }
        
        private void compareUK(final UniqueKey srcUk, final UniqueKey destUk) throws JSONException {
            this.compareUK((srcUk != null) ? srcUk.getName() : null, (destUk != null) ? destUk.getName() : null, (srcUk != null) ? srcUk.getUkColumns() : null, (destUk != null) ? destUk.getUkColumns() : null);
        }
        
        private void compareUK(final UniqueKeyDefinition sourceUk, final UniqueKey dstUk) throws JSONException {
            this.compareUK((sourceUk != null) ? sourceUk.getName() : null, (dstUk != null) ? dstUk.getName() : null, (sourceUk != null) ? sourceUk.getColumns() : null, (dstUk != null) ? dstUk.getUkColumns() : null);
        }
        
        private void compareUK(final String srcUKName, final String destUKName, final List<String> srcUKColumns, final List<String> destUKColumns) throws JSONException {
            final String ukName = (srcUKColumns != null) ? srcUKName : destUKName;
            boolean proceed = true;
            if (srcUKColumns == null || destUKColumns == null) {
                proceed = false;
                this.addDiff((this.sourceTd != null) ? this.sourceTd.getModuleName() : "", this.tableName, "is_unique_key_exists", ukName, srcUKColumns != null, destUKColumns != null);
            }
            if (proceed && !this.isEquals(srcUKColumns, destUKColumns)) {
                this.addDiff(this.sourceTd.getModuleName(), this.tableName, "uk_columns", ukName, srcUKColumns, destUKColumns);
            }
        }
        
        private String getType(final JSONObject obj) throws JSONException {
            final String type = (String)obj.get("difftype");
            if (type.contains("column")) {
                return "column";
            }
            if (type.contains("table")) {
                return "table";
            }
            if (type.contains("index")) {
                return "indexkey";
            }
            if (type.contains("unique")) {
                return "uniquekey";
            }
            if (type.contains("fk")) {
                return "foreignkey";
            }
            return type;
        }
        
        private void changeDiffTypeForIsExists(final JSONObject obj, final Object srcValue, final Object destValue) throws JSONException {
            if (SchemaComparator.this.type.equals(ComparatorType.METADATA_VS_DATABASE)) {
                final String type = this.getType(obj);
                if (srcValue != null && destValue != null) {
                    if (srcValue.toString().equals("false") && destValue.toString().equals("true")) {
                        obj.put("difftype", (Object)(obj.get("difftype") + "(extra_" + type + ")"));
                    }
                    else if (destValue.toString().equals("false") && srcValue.toString().equals("true")) {
                        obj.put("difftype", (Object)(obj.get("difftype") + "(missing_" + type + ")"));
                    }
                }
                else if (srcValue == null && destValue != null) {
                    obj.put("difftype", (Object)(obj.get("difftype") + "(extra_" + type + ")"));
                }
                else if (destValue == null && srcValue != null) {
                    obj.put("difftype", (Object)(obj.get("difftype") + "(missing_" + type + ")"));
                }
            }
        }
        
        private void appendDiff(final JSONObject obj, final Object srcValue, final Object destValue) throws JSONException {
            if (((String)obj.get("difftype")).contains("exists")) {
                this.changeDiffTypeForIsExists(obj, srcValue, destValue);
            }
            obj.put("source", (srcValue != null) ? srcValue.toString() : JSONObject.NULL);
            obj.put("destination", (destValue != null) ? destValue.toString() : JSONObject.NULL);
        }
        
        private boolean compareTableVsTableDefintionAttributes() throws Exception {
            SchemaComparator.LOGGER.fine("Started validating table attributes " + this.tableName);
            if (this.destTable == null || this.sourceTd == null) {
                this.addDiff(this.sourceTd.getModuleName(), this.tableName, "is_table_exists", this.tableName, this.sourceTd != null, this.destTable != null);
                return false;
            }
            if (!this.isEquals(this.destTable.getName(), this.sourceTd.getTableName())) {
                this.addDiff(this.sourceTd.getModuleName(), this.tableName, "table_name", this.tableName, this.sourceTd.getTableName(), this.destTable.getName());
            }
            return true;
        }
        
        private Set<String> getParentTableNames(final TableDefinition td) {
            final List<ForeignKeyDefinition> cdList = td.getForeignKeyList();
            final Set<String> parentTableNames = new HashSet<String>();
            for (final ForeignKeyDefinition cd : cdList) {
                parentTableNames.add(cd.getMasterTableName());
            }
            return parentTableNames;
        }
        
        private Set<String> getChildTableNames(final TableDefinition td) throws Exception {
            final Set<String> childTableNames = new HashSet<String>();
            final List<TableDefinition> tdList = MetaDataUtil.getAllRelatedTableDefinitions(td.getTableName());
            if (tdList != null) {
                for (final TableDefinition td2 : tdList) {
                    childTableNames.add(td2.getTableName());
                }
            }
            return childTableNames;
        }
        
        private boolean compareTableAttributes() throws JSONException {
            SchemaComparator.LOGGER.fine("Started validating table attributes" + this.tableName);
            if (this.srcTable == null || this.destTable == null) {
                this.addDiff("", this.tableName, "is_table_exists", this.tableName, this.srcTable != null, this.destTable != null);
                return false;
            }
            if (!this.isEquals(this.srcTable.getName(), this.destTable.getName())) {
                this.addDiff("", this.tableName, "tableName", this.tableName, this.srcTable.getName(), this.destTable.getName());
            }
            try {
                if (!SchemaComparator.this.isHandlerExists() || !SchemaComparator.this.handler.ignoreTableRowCount(this.tableName)) {
                    if (!this.isEquals(this.srcTable.getPrimaryKey().getCordinality(), this.destTable.getPrimaryKey().getCordinality())) {
                        this.addDiff("", this.tableName, "no_of_rows", this.tableName, this.srcTable.getPrimaryKey().getCordinality(), this.destTable.getPrimaryKey().getCordinality());
                    }
                }
            }
            catch (final Exception e) {
                this.addDiff("", this.tableName, "no_of_rows", this.tableName, this.srcTable.getPrimaryKey().getCordinality(), this.destTable.getPrimaryKey().getCordinality());
                e.printStackTrace();
            }
            return true;
        }
        
        private void compareColumns(final com.zoho.db.model.Column srcColumn, final com.zoho.db.model.Column destColumn) throws Exception {
            boolean proceed = true;
            final String columnName = (srcColumn != null) ? srcColumn.getName() : destColumn.getName();
            if (srcColumn == null || destColumn == null) {
                proceed = false;
                if (SchemaAnalyzerUtil.getConfiguration().getConfigurationValueAsBoolean("include.column.information")) {
                    this.addDiff("", this.tableName, "is_column_exists", columnName, this.getColumnInformationAsString(srcColumn), this.getColumnInformationAsString(destColumn));
                }
                else {
                    this.addDiff("", this.tableName, "is_column_exists", columnName, srcColumn != null, destColumn != null);
                }
            }
            if (proceed) {
                final DataType.Type srcDataType = DataType.getDBSpecificDataType(this.srcDBType, srcColumn.getType());
                final DataType.Type dstDataType = DataType.getDBSpecificDataType(this.dstDBType, destColumn.getType());
                if (!srcDataType.contains(this.dstDBType, dstDataType) && !srcDataType.toString().equalsIgnoreCase(dstDataType.toString())) {
                    this.addDiff("", this.tableName, "column_data_type", columnName, srcColumn.getType(), destColumn.getType());
                }
                if (SchemaComparator.this.srcDBAdapter.isMaxLengthComparable(srcColumn.getType()) && SchemaComparator.this.dstDBAdapter.isMaxLengthComparable(destColumn.getType())) {
                    if (!this.isEquals(srcColumn.getMaxSize(), destColumn.getMaxSize())) {
                        this.addDiff("", this.tableName, "column_max_size", columnName, srcColumn.getMaxSize(), destColumn.getMaxSize());
                    }
                    if (!this.isEquals(srcColumn.getPrecision(), destColumn.getPrecision())) {
                        this.addDiff("", this.tableName, "column_precision", columnName, srcColumn.getPrecision(), destColumn.getPrecision());
                    }
                }
                final String srcDefVal = srcDataType.getDefaultValueFromMetaDataString(srcColumn.getDefaultValue(), srcDataType.toString());
                final String destDefVal = dstDataType.getDefaultValueFromMetaDataString(destColumn.getDefaultValue(), dstDataType.toString());
                if (!this.srcPKColumns.contains(columnName.toUpperCase(Locale.ENGLISH)) && !this.destPKColumns.contains(columnName.toUpperCase(Locale.ENGLISH))) {
                    final Object convertedSrcVal = srcDataType.getDefaultValue(srcDefVal);
                    final Object convertedDestVal = dstDataType.getDefaultValue(destDefVal);
                    if (!this.isEquals(convertedDestVal, convertedSrcVal)) {
                        this.addDiff("", this.tableName, "column_default_value", columnName, convertedSrcVal, convertedDestVal);
                    }
                }
                if (!this.isEquals(srcColumn.getOrdinalPosition(), destColumn.getOrdinalPosition())) {
                    this.addDiff("", this.tableName, "column_position", columnName, srcColumn.getOrdinalPosition(), destColumn.getOrdinalPosition());
                }
            }
        }
        
        private void compareIndex(final Index srcIndex, final Index destIndex) throws JSONException {
            final String indexName = (srcIndex != null) ? srcIndex.getName() : destIndex.getName();
            boolean proceed = true;
            if (srcIndex == null || destIndex == null) {
                proceed = false;
                this.addDiff((this.sourceTd != null) ? this.sourceTd.getModuleName() : "", this.tableName, "is_index_exists", indexName, srcIndex != null, destIndex != null);
            }
            if (proceed && !this.isEquals(srcIndex.getIndexColumnNames(), destIndex.getIndexColumnNames())) {
                this.addDiff((this.sourceTd != null) ? this.sourceTd.getModuleName() : "", this.tableName, "index_columns", indexName, srcIndex.getIndexColumnNames(), destIndex.getIndexColumnNames());
            }
        }
        
        private void compareIndex(final Index dstIndex, final IndexDefinition sourceIndex) throws JSONException {
            final String indexName = (dstIndex != null) ? dstIndex.getName() : sourceIndex.getName();
            boolean proceed = true;
            if (dstIndex == null || sourceIndex == null) {
                proceed = false;
                this.addDiff((this.sourceTd != null) ? this.sourceTd.getModuleName() : "", this.tableName, "is_index_exists", indexName, sourceIndex != null, dstIndex != null);
            }
            if (proceed && !this.isEqualsNotInOrder(dstIndex.getIndexColumnNames(), new ArrayList<String>(sourceIndex.getColumns()))) {
                this.addDiff(this.sourceTd.getModuleName(), this.tableName, "index_columns", indexName, sourceIndex.getColumns(), dstIndex.getIndexColumnNames());
            }
        }
        
        private void compareTableVsTableDefinition_ForeignKeys(final Table dstTable, final TableDefinition sourceTd) throws JSONException, SQLException {
            SchemaComparator.LOGGER.fine("Started validating FKeys of the table " + this.tableName);
            final List<String> comparedFKs = new ArrayList<String>();
            final List<ForeignKeyDefinition> FKList = sourceTd.getForeignKeyList();
            for (final ForeignKeyDefinition srcFK : FKList) {
                final String sourceFKName = srcFK.getName().toLowerCase(Locale.ENGLISH);
                comparedFKs.add(sourceFKName);
                this.compareTableVsTableDefinition_ForeignKey(dstTable.getForeignKey(sourceFKName), srcFK);
            }
            for (final ForeignKey dstFK : dstTable.getForeignKeys()) {
                final String dstFKName = dstFK.getName().toLowerCase(Locale.ENGLISH);
                if (!comparedFKs.contains(dstFKName)) {
                    this.compareTableVsTableDefinition_ForeignKey(dstFK, sourceTd.getForeignKeyDefinitionByName(dstFK.getName()));
                }
            }
        }
        
        private void compareForeignKeys(final Table srcTable, final Table destTable) throws JSONException {
            SchemaComparator.LOGGER.fine("Started validating FKeys of the table " + this.tableName);
            final List<String> comparedFKs = new ArrayList<String>();
            for (final ForeignKey srcFK : srcTable.getForeignKeys()) {
                final String srcFKName = srcFK.getName().toLowerCase(Locale.ENGLISH);
                comparedFKs.add(srcFKName);
                this.compareForeignKey(srcFK, destTable.getForeignKey(srcFK.getName()));
            }
            for (final ForeignKey dstFK : destTable.getForeignKeys()) {
                final String destFKName = dstFK.getName().toLowerCase(Locale.ENGLISH);
                if (!comparedFKs.contains(destFKName)) {
                    this.compareForeignKey(srcTable.getForeignKey(destFKName), dstFK);
                }
            }
        }
        
        private void compareForeignKey(final ForeignKey srcFK, final ForeignKey destFK) throws JSONException {
            final String fkName = (srcFK != null) ? srcFK.getName() : destFK.getName();
            boolean proceed = true;
            if (srcFK == null || destFK == null) {
                proceed = false;
                this.addDiff("", this.tableName, "is_fk_exists", fkName, srcFK != null, destFK != null);
            }
            if (proceed) {
                final String srcFKIDX = SchemaComparator.this.srcDBAdapter.getSQLGenerator().getIndexName(srcFK.getName()).toUpperCase(Locale.ENGLISH);
                final String destFKIDX = SchemaComparator.this.dstDBAdapter.getSQLGenerator().getIndexName(destFK.getName()).toUpperCase(Locale.ENGLISH);
                this.fkIdxVsMap.put(srcFKIDX, destFKIDX);
                if (!this.fkIdxVsMap.containsKey(destFKIDX)) {
                    this.fkIdxVsMap.put(destFKIDX, srcFKIDX);
                }
                if (!this.isEquals(srcFK.getConstraint(), destFK.getConstraint()) && !this.isEquals(DataType.getFKConstraintString(srcFK.getConstraint(), this.srcDBType), DataType.getFKConstraintString(destFK.getConstraint(), this.dstDBType)) && (this.srcDBType != SchemaAnalyzerUtil.DBType.MSSQL || !"".equals(srcFK.getConstraint()) || !"ON DELETE SET NULL".equalsIgnoreCase(destFK.getConstraint())) && (this.dstDBType != SchemaAnalyzerUtil.DBType.MSSQL || !"ON DELETE SET NULL".equalsIgnoreCase(srcFK.getConstraint()) || !"".equals(destFK.getConstraint()))) {
                    this.addDiff("", this.tableName, "fk_delete_rule_name", fkName, srcFK.getConstraint(), destFK.getConstraint());
                }
                this.compareFKColumns(fkName, srcFK.getFkColumns(), destFK.getFkColumns());
                if (!this.isFKTablesEquals(srcFK.getParentTableName(), destFK.getParentTableName())) {
                    this.addDiff("", this.tableName, "fk_parent_tablename", fkName, srcFK.getParentTableName(), destFK.getParentTableName());
                }
                if (!this.isFKTablesEquals(srcFK.getTableName(), destFK.getTableName())) {
                    this.addDiff("", this.tableName, "fk_child_tablename", fkName, srcFK.getTableName(), destFK.getTableName());
                }
            }
        }
        
        private void compareTableVsTableDefinition_ForeignKey(final ForeignKey dstFK, final ForeignKeyDefinition sourceFK) throws JSONException, SQLException {
            final String fkName = (dstFK != null) ? dstFK.getName() : sourceFK.getName();
            boolean proceed = true;
            if (dstFK == null || sourceFK == null) {
                proceed = false;
                this.addDiff(this.sourceTd.getModuleName(), this.tableName, "is_fk_exists", fkName, sourceFK != null, dstFK != null);
            }
            final String sourceFKIDX = SchemaComparator.this.srcDBAdapter.getSQLGenerator().getIndexName(fkName);
            if (!this.fkIdxVsMap.containsKey(sourceFKIDX)) {
                this.fkIdxVsMap.put((this.srcDBType == SchemaAnalyzerUtil.DBType.POSTGRES) ? sourceFKIDX.toLowerCase(Locale.ENGLISH) : sourceFKIDX, fkName);
            }
            if (proceed) {
                if ((dstFK.getConstraint() != "" || !sourceFK.getConstraintsAsString().equalsIgnoreCase("ON-DELETE-RESTRICT")) && (this.srcDBType != SchemaAnalyzerUtil.DBType.MSSQL || (!sourceFK.getConstraintsAsString().equalsIgnoreCase("ON-DELETE-CASCADE") && !sourceFK.getConstraintsAsString().equalsIgnoreCase("ON-DELETE-SET-NULL"))) && !this.isEquals(dstFK.getConstraint(), sourceFK.getConstraintsAsString().replace("-", " ")) && !this.isEquals(DataType.getFKConstraintString(dstFK.getConstraint(), this.dstDBType), DataType.getFKConstraintString(sourceFK.getConstraintsAsString(), this.srcDBType)) && (this.dstDBType != SchemaAnalyzerUtil.DBType.MSSQL || !"".equals(dstFK.getConstraint()) || !"ON DELETE SET NULL".equalsIgnoreCase(sourceFK.getConstraintsAsString())) && (this.dstDBType != SchemaAnalyzerUtil.DBType.MSSQL || !"ON DELETE SET NULL".equalsIgnoreCase(dstFK.getConstraint()) || !"".equals(sourceFK.getConstraintsAsString()))) {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "fk_delete_rule_name", fkName, sourceFK.getConstraintsAsString(), dstFK.getConstraint());
                }
                this.compareTableVsTableDefinition_FKColumns(fkName, dstFK.getFkColumns(), sourceFK.getForeignKeyColumns());
                if (!this.isFKTablesEquals(sourceFK.getMasterTableName(), dstFK.getParentTableName())) {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "fk_parent_tablename", fkName, sourceFK.getMasterTableName(), dstFK.getParentTableName());
                }
                if (!this.isFKTablesEquals(sourceFK.getSlaveTableName(), dstFK.getTableName())) {
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "fk_child_tablename", fkName, sourceFK.getSlaveTableName(), dstFK.getTableName());
                }
            }
        }
        
        private JSONObject appendElement(final String key, final Object srcValue, final Object destValue) throws JSONException {
            this.setModified(true);
            final JSONObject newElement = new JSONObject();
            newElement.put("src_" + key, (srcValue != null) ? srcValue.toString() : JSONObject.NULL);
            newElement.put("dst_" + key, (destValue != null) ? destValue.toString() : JSONObject.NULL);
            return newElement;
        }
        
        private boolean isFKTablesEquals(final String lhsTable, final String rhsTable) throws JSONException {
            return this.isEquals(lhsTable, rhsTable);
        }
        
        private void compareFKColumns(final String fkName, final List<ForeignKey.ForeignKeyColumn> lhsColumns, final List<ForeignKey.ForeignKeyColumn> rhsColumns) throws JSONException {
            if (lhsColumns == null || rhsColumns == null) {
                this.addDiff("", this.tableName, "fk_column_isnull", fkName, lhsColumns == null, rhsColumns == null);
            }
            if (lhsColumns.size() != rhsColumns.size()) {
                this.addDiff("", this.tableName, "fk_column_size", fkName, lhsColumns.size(), rhsColumns.size());
            }
            for (int i = 0; i < lhsColumns.size(); ++i) {
                final ForeignKey.ForeignKeyColumn lhsCol = lhsColumns.get(i);
                final ForeignKey.ForeignKeyColumn rhsCol = rhsColumns.get(i);
                if (!this.isEquals(lhsCol.getColumnName(), rhsCol.getColumnName()) || !this.isEquals(lhsCol.getParentColumnName(), rhsCol.getParentColumnName())) {
                    this.setModified(true);
                    this.addDiff("", this.tableName, "fk_column_index" + i, fkName, lhsCol.getColumnName(), rhsCol.getColumnName());
                }
            }
        }
        
        private void compareTableVsTableDefinition_FKColumns(final String fkName, final List<ForeignKey.ForeignKeyColumn> lhsColumns, final List<ForeignKeyColumnDefinition> rhsColumns) throws JSONException {
            if (lhsColumns == null || rhsColumns == null) {
                this.addDiff(this.sourceTd.getModuleName(), this.tableName, "fk_column_isnull", fkName, rhsColumns == null, lhsColumns == null);
            }
            for (int i = 0; i < lhsColumns.size(); ++i) {
                final ForeignKey.ForeignKeyColumn lhsCol = lhsColumns.get(i);
                final ForeignKeyColumnDefinition rhsCol = rhsColumns.get(i);
                if (!this.isEquals(lhsCol.getColumnName(), rhsCol.getLocalColumnDefinition().getColumnName()) || !this.isEquals(lhsCol.getParentColumnName(), rhsCol.getReferencedColumnDefinition().getColumnName())) {
                    this.setModified(true);
                    this.addDiff(this.sourceTd.getModuleName(), this.tableName, "fk_column_index" + i, fkName, rhsCol.getReferencedColumnDefinition().getColumnName(), lhsCol.getColumnName());
                }
            }
        }
        
        private boolean isModified() {
            return SchemaComparator.this.isModified;
        }
        
        private void setModified(final boolean ismodified) {
            if (!SchemaComparator.this.isModified && ismodified) {
                SchemaComparator.this.isModified = ismodified;
            }
        }
        
        private boolean isEquals(final String string1, final String string2) {
            return (string1 == null) ? (string2 == null) : ((string2 == null) ? (string1 == null) : string1.toUpperCase(Locale.ENGLISH).equals(string2.toUpperCase(Locale.ENGLISH)));
        }
        
        private boolean isEquals(final int num1, final int num2) {
            return num1 == num2;
        }
        
        private boolean isEquals(Object obj1, final Object obj2) {
            if (this.dstDBType == SchemaAnalyzerUtil.DBType.MSSQL && obj2 instanceof Float && obj1 instanceof Double) {
                obj1 = ((Double)obj1).floatValue();
            }
            return (obj1 == null) ? (obj2 == null) : ((obj1 instanceof String && obj2 instanceof String) ? ((String)obj1).toUpperCase(Locale.ENGLISH).equals(((String)obj2).toUpperCase(Locale.ENGLISH)) : obj1.equals(obj2));
        }
        
        private boolean isEquals(final List<String> obj1, final List<String> obj2) {
            return (obj1 == null && obj2 == null) || obj1.toString().toUpperCase(Locale.ENGLISH).equals(obj2.toString().toUpperCase(Locale.ENGLISH));
        }
        
        private boolean isEqualsNotInOrder(final List<String> obj1, final List<String> obj2) {
            if (obj1 != null && obj2 != null) {
                Collections.sort(obj1);
                Collections.sort(obj2);
            }
            return (obj1 == null && obj2 == null) || obj1.toString().toUpperCase(Locale.ENGLISH).equals(obj2.toString().toUpperCase(Locale.ENGLISH));
        }
        
        private boolean comparePKColumns(final List<String> srcColumns, final List<String> dstColumns) {
            if ((srcColumns == null && dstColumns == null) || (srcColumns != null && dstColumns == null) || (srcColumns == null && dstColumns != null) || srcColumns.size() != dstColumns.size()) {
                return false;
            }
            this.srcPKColumns.addAll(srcColumns);
            this.destPKColumns.addAll(dstColumns);
            return this.isEquals(this.srcPKColumns.toString(), this.destPKColumns.toString());
        }
    }
}
