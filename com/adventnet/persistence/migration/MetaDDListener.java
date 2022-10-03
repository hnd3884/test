package com.adventnet.persistence.migration;

import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.ResultSet;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.sql.DatabaseMetaData;
import com.adventnet.persistence.DataObject;
import java.util.Locale;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import com.adventnet.db.adapter.mssql.MssqlDBAdapter;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.logging.Logger;

public class MetaDDListener extends DefaultDDChangeListener
{
    private static final Logger LOGGER;
    
    @Override
    public boolean preInvokeForAlterTable(final AlterTableQuery atq) throws Exception {
        final AlterOperation ao = atq.getAlterOperations().get(0);
        if (ao.getOperationType() == 2) {
            final ColumnDefinition col = (ColumnDefinition)ao.getAlterObject();
            final String tableName = atq.getTableName();
            final String columnName = col.getColumnName();
            if (this.getMigrationType() == DDChangeListener.MigrationType.INSTALL || this.getMigrationType() == DDChangeListener.MigrationType.UNINSTALL_FAILURE) {
                if ((tableName.equals("TableDetails") && columnName.equals("TABLE_NAME")) || (tableName.equals("ColumnDetails") && columnName.equals("COLUMN_NAME"))) {
                    final int existingColumnSize = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName).getMaxLength();
                    final int newColumnSize = col.getMaxLength();
                    if (existingColumnSize > newColumnSize) {
                        MetaDDListener.LOGGER.severe("Changing max-size of the column in cache, to avoid max-size reduction validation.");
                        MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName).setMaxLength(newColumnSize - 1);
                        if (tableName.equals("TableDetails") && columnName.equals("TABLE_NAME")) {
                            final DBAdapter dbAdapter = RelationalAPI.getInstance().getDBAdapter();
                            if (dbAdapter.getDBType().equals("mssql") && ((MssqlDBAdapter)dbAdapter).isFilteredUniqueIndexSupported()) {
                                try (final Connection conn = RelationalAPI.getInstance().getConnection();
                                     final Statement stmt = conn.createStatement()) {
                                    if (!((MssqlDBAdapter)dbAdapter).isUniqueIndexCreated(stmt, "TableDetails", "TableDetails_UK0")) {
                                        final StringBuilder query = new StringBuilder();
                                        query.append("ALTER TABLE \"TableDetails\" DROP  CONSTRAINT \"TableDetails_UK0\"");
                                        query.append(" ; ");
                                        query.append("ALTER TABLE \"TableDetails\" ALTER COLUMN \"TABLE_NAME\" VARCHAR(" + newColumnSize + ") NOT NULL");
                                        query.append(" ; ");
                                        query.append("ALTER TABLE \"TableDetails\" ADD CONSTRAINT \"TableDetails_UK0\" UNIQUE (\"TABLE_NAME\")");
                                        query.append(" ; ");
                                        final String sql = query.toString();
                                        MetaDDListener.LOGGER.log(Level.INFO, "Executing query :: {0}", sql);
                                        stmt.execute(sql);
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
                else if (tableName.equals("IndexDefinition") && columnName.equals("SIZE")) {
                    final ColumnDefinition existingColDef = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName);
                    if (existingColDef.isNullable() && existingColDef.getDefaultValue() == null && !col.isNullable() && col.getDefaultValue() != null) {
                        final UpdateQuery uq = new UpdateQueryImpl(tableName);
                        uq.setUpdateColumn(columnName, col.getDefaultValue());
                        uq.setCriteria(new Criteria(Column.getColumn(tableName, columnName), null, 0));
                        MetaDDListener.LOGGER.info("Updating null values with default-value.");
                        MetaDDListener.LOGGER.info(RelationalAPI.getInstance().getUpdateSQL(uq));
                        DataAccess.update(uq);
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public void postInvokeForAlterTable(final AlterTableQuery atq) throws DataAccessException, SQLException {
        final AlterOperation ao = atq.getAlterOperations().get(0);
        if (ao.getOperationType() == 1) {
            final ColumnDefinition cd = (ColumnDefinition)ao.getAlterObject();
            if ((atq.getTableName().equals("UniqueKeyDefinition") && cd.getColumnName().equals("POSITION")) || (atq.getTableName().equals("IndexDefinition") && cd.getColumnName().equals("POSITION"))) {
                this.populatePositionColumn(atq.getTableName());
            }
        }
    }
    
    private void populatePositionColumn(final String tableName) throws DataAccessException, SQLException {
        final DataObject tableDO = DataAccess.get("TableDetails", (Criteria)null);
        final DataObject columnDO = DataAccess.get("ColumnDetails", (Criteria)null);
        final DataObject constraintSpecificDO = DataAccess.get(tableName, (Criteria)null);
        ((WritableDataObject)constraintSpecificDO).clearOperations();
        constraintSpecificDO.set(tableName, tableName.equals("UniqueKeyDefinition") ? "POSITION" : "POSITION", 1);
        Connection con = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            final DatabaseMetaData dbmd = con.getMetaData();
            final SelectQuery sq = new SelectQueryImpl(Table.getTable("ConstraintDefinition"));
            sq.addSelectColumn(Column.getColumn(null, "*"));
            SelectQuery innerSq;
            if (tableName.equals("UniqueKeyDefinition")) {
                innerSq = new SelectQueryImpl(Table.getTable("UniqueKeyDefinition"));
                innerSq.addSelectColumn(Column.getColumn("UniqueKeyDefinition", "UNIQUE_CONS_ID"));
            }
            else {
                innerSq = new SelectQueryImpl(Table.getTable("IndexDefinition"));
                innerSq.addSelectColumn(Column.getColumn("IndexDefinition", "INDEX_CONS_ID"));
            }
            final Column criCol = new DerivedColumn("subQuery", innerSq);
            sq.setCriteria(new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_ID"), criCol, 8));
            final DataObject constraintsDO = DataAccess.get(sq);
            final Iterator rows = constraintsDO.getRows("ConstraintDefinition");
            while (rows.hasNext()) {
                final Row constraintRow = rows.next();
                final String constraintName = (String)constraintRow.get("CONSTRAINT_NAME");
                Criteria subDoCri;
                if (tableName.equals("IndexDefinition")) {
                    subDoCri = new Criteria(Column.getColumn("IndexDefinition", "INDEX_CONS_ID"), constraintRow.get("CONSTRAINT_ID"), 0);
                }
                else {
                    subDoCri = new Criteria(Column.getColumn("UniqueKeyDefinition", "UNIQUE_CONS_ID"), constraintRow.get("CONSTRAINT_ID"), 0);
                }
                final DataObject subDO = constraintSpecificDO.getDataObject(tableName, subDoCri);
                if (subDO.size(tableName) == 1) {
                    continue;
                }
                final Row tableRow = tableDO.getRow("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_ID"), constraintRow.get("TABLE_ID"), 0));
                final String constraintsTableName = (String)tableRow.get("TABLE_NAME");
                String dbSpecificTableName = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getDBSpecificTableName(constraintsTableName);
                if (dbSpecificTableName.equals(constraintsTableName)) {
                    dbSpecificTableName = dbSpecificTableName.toLowerCase(Locale.ENGLISH);
                }
                else {
                    dbSpecificTableName = constraintsTableName;
                }
                ResultSet rs = null;
                try {
                    if (tableName.equals("UniqueKeyDefinition")) {
                        rs = dbmd.getIndexInfo(null, null, dbSpecificTableName, true, true);
                    }
                    else {
                        rs = dbmd.getIndexInfo(null, null, dbSpecificTableName, false, true);
                    }
                    while (rs.next()) {
                        final String dbConstraintName = rs.getString("INDEX_NAME");
                        if (dbConstraintName != null && dbConstraintName.equalsIgnoreCase(constraintName)) {
                            final String columnName = rs.getString("COLUMN_NAME");
                            final int columnPosition = rs.getInt("ORDINAL_POSITION");
                            final Criteria colCri1 = new Criteria(Column.getColumn("ColumnDetails", "TABLE_ID"), constraintRow.get("TABLE_ID"), 0);
                            final Criteria colCri2 = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), columnName, 0, false);
                            final Row columnRow = columnDO.getRow("ColumnDetails", colCri1.and(colCri2));
                            final long columnId = (long)columnRow.get("COLUMN_ID");
                            if (tableName.equals("UniqueKeyDefinition")) {
                                final Criteria ukCri1 = new Criteria(Column.getColumn("UniqueKeyDefinition", "UNIQUE_CONS_ID"), constraintRow.get("CONSTRAINT_ID"), 0);
                                final Criteria ukCri2 = new Criteria(Column.getColumn("UniqueKeyDefinition", "COLUMN_ID"), columnId, 0);
                                final Row ukRow = constraintSpecificDO.getRow("UniqueKeyDefinition", ukCri1.and(ukCri2));
                                ukRow.set("POSITION", columnPosition);
                                constraintSpecificDO.updateRow(ukRow);
                            }
                            else {
                                final Criteria idxCri1 = new Criteria(Column.getColumn("IndexDefinition", "INDEX_CONS_ID"), constraintRow.get("CONSTRAINT_ID"), 0);
                                final Criteria idxCri2 = new Criteria(Column.getColumn("IndexDefinition", "COLUMN_ID"), columnId, 0);
                                final Row idxRow = constraintSpecificDO.getRow("IndexDefinition", idxCri1.and(idxCri2));
                                idxRow.set("POSITION", columnPosition);
                                constraintSpecificDO.updateRow(idxRow);
                            }
                        }
                    }
                }
                finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            }
        }
        finally {
            if (con != null) {
                con.close();
            }
        }
        if (!constraintSpecificDO.getOperations().isEmpty()) {
            MetaDDListener.LOGGER.log(Level.INFO, "Going to update changes :: {0}", constraintSpecificDO);
            DataAccess.update(constraintSpecificDO);
        }
    }
    
    @Override
    public boolean preInvokeForCreateTable(final TableDefinition tabDef) throws Exception {
        if (tabDef.getTableName().equals("DBCredentialsAudit")) {
            Connection conn = null;
            try {
                conn = RelationalAPI.getInstance().getConnection();
                if (RelationalAPI.getInstance().getDBAdapter().isTablePresentInDB(conn, null, "DBCredentialsAudit")) {
                    final String moduleName = tabDef.getModuleName();
                    MetaDataUtil.addTableDefinition(moduleName, tabDef);
                    return false;
                }
            }
            finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
        return super.preInvokeForCreateTable(tabDef);
    }
    
    static {
        LOGGER = Logger.getLogger(MetaDDListener.class.getName());
    }
}
