package com.zoho.db.scanner;

import com.zoho.db.model.ForeignKeyBuilder;
import com.zoho.db.model.ForeignKey;
import java.util.Iterator;
import com.zoho.db.model.UniqueKeyBuilder;
import com.zoho.db.model.IndexBuilder;
import com.zoho.db.model.Index;
import java.util.List;
import com.zoho.db.model.PrimaryKeyBuilder;
import java.util.Collections;
import java.util.ArrayList;
import com.zoho.db.model.PrimaryKey;
import java.sql.SQLException;
import java.util.Map;
import com.zoho.db.model.ColumnBuilder;
import java.util.HashMap;
import com.zoho.db.model.Column;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import com.zoho.db.model.TableBuilder;
import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import com.zoho.db.model.Table;
import java.util.logging.Logger;
import com.zoho.db.model.DataBase;
import java.util.concurrent.Callable;

public class ScannerCrew implements Callable<Boolean>
{
    private DataBase dataBase;
    private Logger out;
    private String tableName;
    private Table table;
    private boolean mapFkRelations;
    private static ExecutorService scannerThreadPool;
    private static DataSource dataSource;
    
    public ScannerCrew(final DataBase dataBase, final String tableName, final boolean mapFKRelations) {
        this.mapFkRelations = false;
        this.out = Logger.getLogger(tableName);
        this.dataBase = dataBase;
        this.tableName = tableName;
        this.mapFkRelations = mapFKRelations;
    }
    
    static void setThreadPool(final ExecutorService threadPool) {
        ScannerCrew.scannerThreadPool = threadPool;
    }
    
    public static void setDataSource(final DataSource dataSource) {
        ScannerCrew.dataSource = dataSource;
    }
    
    @Override
    public Boolean call() throws Exception {
        try (final Connection connection = ScannerCrew.dataSource.getConnection()) {
            final String catalog = this.dataBase.getCatalog();
            final String schema = this.dataBase.getSchema();
            final DatabaseMetaData metaData = connection.getMetaData();
            this.out.fine("Processing table " + this.tableName);
            this.table = this.dataBase.getTable(this.tableName);
            Statement stmt = null;
            if (this.table == null) {
                this.table = TableBuilder.table().withTableName(this.tableName).withDbLable(this.dataBase.getDbLable()).build();
                this.dataBase.addTable(this.table);
            }
            if (!this.mapFkRelations) {
                try (final ResultSet rs = metaData.getColumns(catalog, schema, this.tableName, null)) {
                    this.processColumns(rs);
                }
                try (final ResultSet rs = metaData.getPrimaryKeys(catalog, schema, this.tableName)) {
                    this.processPK(rs);
                }
                try (final ResultSet rs = metaData.getIndexInfo(catalog, schema, this.tableName, false, false)) {
                    this.processIndexAndUnique(rs);
                }
                ResultSet rs = null;
                try {
                    stmt = connection.createStatement();
                    this.out.info("Setting cordinality for PK.");
                    final String dbSpecificTableName = this.dataBase.getIdentifierQuoteString() + this.tableName + this.dataBase.getIdentifierQuoteString();
                    rs = stmt.executeQuery("select count(*) from " + dbSpecificTableName);
                    if (rs.next()) {
                        this.table.getPrimaryKey().setCordinality(rs.getString(1));
                    }
                }
                finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
            else {
                try (final ResultSet rs = metaData.getImportedKeys(catalog, schema, this.tableName)) {
                    this.processForiegnKey(rs);
                }
            }
        }
        catch (final Throwable e) {
            this.out.severe("Exception occured while process table :: " + this.tableName);
            e.printStackTrace();
            this.getScannerThreadPool().shutdownNow();
            throw new Exception(e);
        }
        return Boolean.TRUE;
    }
    
    protected void processColumns(final ResultSet rs) throws SQLException {
        boolean is2005SQLServer = false;
        final Column.MetaDataLable[] labels = Column.MetaDataLable.values();
        try (final Connection conn = ScannerCrew.dataSource.getConnection()) {
            final DatabaseMetaData metaData = conn.getMetaData();
            if (metaData.getDriverName().startsWith("Microsoft JDBC Driver") && metaData.getDatabaseMajorVersion() < 10) {
                is2005SQLServer = true;
            }
        }
        Map<Column.MetaDataLable, String> columnProps = null;
        boolean isRecFound = false;
        while (rs.next()) {
            isRecFound = true;
            columnProps = new HashMap<Column.MetaDataLable, String>();
            for (final Column.MetaDataLable metaDataLable : labels) {
                if (!is2005SQLServer || !metaDataLable.toString().equals("IS_AUTOINCREMENT")) {
                    final String value = rs.getString(metaDataLable.toString());
                    if (value != null) {
                        columnProps.put(metaDataLable, value);
                    }
                }
            }
            this.table.addColumn(ColumnBuilder.column().withDbLable(this.dataBase.getDbLable()).withProperties(columnProps).build());
        }
        if (!isRecFound) {
            throw new IllegalArgumentException("No columns found for table " + this.tableName);
        }
    }
    
    protected void processPK(final ResultSet rs) throws SQLException {
        final PrimaryKey.MetaDataLabel[] labels = PrimaryKey.MetaDataLabel.values();
        final Map<PrimaryKey.MetaDataLabel, String> pkProps = new HashMap<PrimaryKey.MetaDataLabel, String>();
        final List<ColumnIndex> columnsInOrder = new ArrayList<ColumnIndex>(10);
        boolean isRecFound = false;
        while (rs.next()) {
            isRecFound = true;
            for (final PrimaryKey.MetaDataLabel metaDataLabel : labels) {
                if (metaDataLabel != PrimaryKey.MetaDataLabel.COLUMN_NAME && metaDataLabel != PrimaryKey.MetaDataLabel.KEY_SEQ && metaDataLabel != PrimaryKey.MetaDataLabel.COLUMNS_IN_ORDER) {
                    final String value = rs.getString(metaDataLabel.toString());
                    if (value != null) {
                        pkProps.put(metaDataLabel, value);
                    }
                }
            }
            columnsInOrder.add(new ColumnIndex(rs.getString(PrimaryKey.MetaDataLabel.COLUMN_NAME.toString()), rs.getInt(PrimaryKey.MetaDataLabel.KEY_SEQ.toString())));
        }
        if (!isRecFound) {
            this.out.info("Table " + this.tableName + " exists without PK.");
        }
        Collections.sort(columnsInOrder);
        pkProps.put(PrimaryKey.MetaDataLabel.COLUMNS_IN_ORDER, columnsInOrder.toString());
        this.table.setPrimaryKey(PrimaryKeyBuilder.primaryKey().withTable(this.table).withDbLable(this.dataBase.getDbLable()).withProperties(pkProps).build());
    }
    
    protected void processIndexAndUnique(final ResultSet rs) throws SQLException {
        final Index.MetaDataLabel[] labels = Index.MetaDataLabel.values();
        final Map<String, List<ColumnIndex>> idxNameVsIdxColumns = new HashMap<String, List<ColumnIndex>>();
        final Map<String, Map<Index.MetaDataLabel, String>> idxNameVsProps = new HashMap<String, Map<Index.MetaDataLabel, String>>();
        boolean isRecFound = false;
        while (rs.next()) {
            isRecFound = true;
            final String indexName = rs.getString(Index.MetaDataLabel.INDEX_NAME.toString());
            if (indexName == null) {
                continue;
            }
            final Map<Index.MetaDataLabel, String> idxProps = idxNameVsProps.containsKey(indexName) ? idxNameVsProps.get(indexName) : new HashMap<Index.MetaDataLabel, String>();
            if (idxProps.isEmpty()) {
                for (final Index.MetaDataLabel metaDataLabel : labels) {
                    final String value = rs.getString(metaDataLabel.toString());
                    if (metaDataLabel == Index.MetaDataLabel.NON_UNIQUE) {
                        idxProps.put(metaDataLabel, Boolean.toString(rs.getBoolean(metaDataLabel.toString())));
                    }
                    else if (metaDataLabel != Index.MetaDataLabel.COLUMN_NAME) {
                        idxProps.put(metaDataLabel, value);
                    }
                }
            }
            final List<ColumnIndex> columnsInOrder = idxNameVsIdxColumns.containsKey(indexName) ? idxNameVsIdxColumns.get(indexName) : new ArrayList<ColumnIndex>(10);
            columnsInOrder.add(new ColumnIndex(rs.getString(Index.MetaDataLabel.COLUMN_NAME.toString()), rs.getInt(Index.MetaDataLabel.ORDINAL_POSITION.toString())));
            idxNameVsIdxColumns.put(indexName, columnsInOrder);
            idxNameVsProps.put(indexName, idxProps);
        }
        if (!isRecFound) {
            this.out.info("No index/unique found for table " + this.tableName);
        }
        for (final String idxName : idxNameVsIdxColumns.keySet()) {
            if (!idxName.equalsIgnoreCase(this.table.getPrimaryKey().getName())) {
                final List<ColumnIndex> list = idxNameVsIdxColumns.get(idxName);
                Collections.sort(list);
                idxNameVsProps.get(idxName).put(Index.MetaDataLabel.COLUMN_NAME, list.toString());
                if (Boolean.valueOf((String)idxNameVsProps.get(idxName).get(Index.MetaDataLabel.NON_UNIQUE))) {
                    this.table.addIndex(IndexBuilder.index().withTable(this.table).withDbLable(this.dataBase.getDbLable()).withProperties(idxNameVsProps.get(idxName)).build());
                }
                else {
                    this.table.addUniqueKey(UniqueKeyBuilder.uniqueKey().withTable(this.table).withDbLable(this.dataBase.getDbLable()).withProperties(idxNameVsProps.get(idxName)).build());
                }
            }
            else {
                this.table.getPrimaryKey().setCordinality((String)idxNameVsProps.get(idxName).get(Index.MetaDataLabel.CARDINALITY));
            }
        }
    }
    
    protected void processForiegnKey(final ResultSet rs) throws SQLException {
        final ForeignKey.MetaDataLabel[] metaDataLabels = ForeignKey.MetaDataLabel.values();
        boolean isRecFound = false;
        while (rs.next()) {
            isRecFound = true;
            final String fkName = rs.getString(ForeignKey.MetaDataLabel.FK_NAME.toString());
            if (!this.table.containsForeignKey(fkName)) {
                final Map<ForeignKey.MetaDataLabel, String> fkProps = new HashMap<ForeignKey.MetaDataLabel, String>();
                for (final ForeignKey.MetaDataLabel metaDataLabel : metaDataLabels) {
                    if (metaDataLabel != ForeignKey.MetaDataLabel.FKCOLUMN_NAME || metaDataLabel != ForeignKey.MetaDataLabel.PK_NAME) {
                        fkProps.put(metaDataLabel, rs.getString(metaDataLabel.toString()));
                    }
                }
                this.table.addForeignKey(ForeignKeyBuilder.foreignKey().withName(fkName).inTable(this.table.getName()).withdbLable(this.dataBase.getDbLable()).withParentTable(rs.getString(ForeignKey.MetaDataLabel.PKTABLE_NAME.toString())).withProperties(fkProps).build());
            }
            this.table.getForeignKey(fkName).addFkColumn(rs.getString(ForeignKey.MetaDataLabel.FKCOLUMN_NAME.toString()), rs.getString(ForeignKey.MetaDataLabel.PKCOLUMN_NAME.toString()), rs.getInt(ForeignKey.MetaDataLabel.KEY_SEQ.toString()));
        }
        if (!isRecFound) {
            this.out.info("No foreignKey found for table " + this.tableName);
        }
    }
    
    public ExecutorService getScannerThreadPool() {
        return ScannerCrew.scannerThreadPool;
    }
    
    public void setScannerThreadPool(final ExecutorService scannerThreadPool) {
        ScannerCrew.scannerThreadPool = scannerThreadPool;
    }
    
    static {
        ScannerCrew.scannerThreadPool = null;
    }
    
    protected class ColumnIndex implements Comparable<ColumnIndex>
    {
        private String columnName;
        private Integer columnIndex;
        
        public ColumnIndex(final String columnName, final int columnIndex) {
            this.columnName = columnName;
            this.columnIndex = columnIndex;
        }
        
        @Override
        public int compareTo(final ColumnIndex o) {
            return this.columnIndex.compareTo(o.getColumnIndex());
        }
        
        public String getColumnName() {
            return this.columnName;
        }
        
        protected int getColumnIndex() {
            return this.columnIndex;
        }
        
        @Override
        public String toString() {
            return this.getColumnName();
        }
    }
}
