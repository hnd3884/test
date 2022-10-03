package com.zoho.mickey.dt;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.db.adapter.ResultSetAdapter;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.ds.query.Query;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.util.Iterator;
import com.adventnet.ds.query.DataSet;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.AlterTableQuery;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.zoho.mickey.exception.KeyModificationException;
import com.zoho.mickey.tools.crypto.ECTagModifierUtil;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Properties;
import com.adventnet.db.adapter.SQLGenerator;
import com.adventnet.db.adapter.DBAdapter;
import java.util.logging.Logger;

public class DefaultDTKeyModifier implements DTKeyModifier
{
    static Logger out;
    private static DBAdapter dbAdapter;
    private static DBAdapter newDBAdapter;
    private static SQLGenerator sqlGen;
    private static SQLGenerator newSqlGen;
    
    public void initialize(final Properties properties) throws Exception {
        (DefaultDTKeyModifier.newDBAdapter = PersistenceInitializer.createDBAdapter(properties)).initialize(properties);
        (DefaultDTKeyModifier.newSqlGen = DefaultDTKeyModifier.newDBAdapter.getSQLGenerator()).setKey(properties.getProperty("ECTag"));
        DefaultDTKeyModifier.dbAdapter = RelationalAPI.getInstance().getDBAdapter();
        DefaultDTKeyModifier.sqlGen = DefaultDTKeyModifier.dbAdapter.getSQLGenerator();
    }
    
    public boolean changeKey(final String tableName, final String columnName) throws KeyModificationException {
        try {
            ECTagModifierUtil.addStatusRow(tableName, columnName);
            this.addNullableTempColumn(tableName, columnName);
            this.addTempColumnInTableDefinition(tableName, columnName, columnName + "_tempcol");
            ECTagModifierUtil.updateStatus(tableName, columnName, 2);
            this.encryptDataInTempColumn(tableName, columnName);
            ECTagModifierUtil.updateStatus(tableName, columnName, 3);
            this.modifyTempColumn(tableName, columnName);
            ECTagModifierUtil.updateStatus(tableName, columnName, 4);
            renameColumn(tableName, columnName, columnName + "_tempcol1");
            this.addTempColumnInTableDefinition(tableName, columnName, columnName + "_tempcol1");
            ECTagModifierUtil.updateStatus(tableName, columnName, 5);
            renameColumn(tableName, columnName + "_tempcol", columnName);
            ECTagModifierUtil.updateStatus(tableName, columnName, 6);
            return true;
        }
        catch (final Exception e) {
            throw new KeyModificationException("Exception occurred while reEncryptWithNewKey for the column " + columnName + " in the table " + tableName, (Throwable)e);
        }
    }
    
    private void addTempColumnInTableDefinition(final String tableName, final String actualColumnName, final String tempColumnName) throws MetaDataException, CloneNotSupportedException {
        final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(actualColumnName);
        final ColumnDefinition cloned_cd = (ColumnDefinition)colDef.clone();
        cloned_cd.setColumnName(tempColumnName);
        tabDef.addColumnDefinition(cloned_cd);
    }
    
    private void addNullableTempColumn(final String tableName, final String encryptedColName) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            DefaultDTKeyModifier.out.info("going to add temp column for the column " + encryptedColName + " in the table " + tableName);
            final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
            con = RelationalAPI.getInstance().getConnection();
            stmt = con.createStatement();
            final AlterTableQuery atq = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(encryptedColName);
            final ColumnDefinition cloned_cd = (ColumnDefinition)colDef.clone();
            if (!cloned_cd.isNullable()) {
                cloned_cd.setNullable(true);
            }
            cloned_cd.setColumnName(encryptedColName + "_tempcol");
            atq.addColumn(cloned_cd);
            final String sql = DefaultDTKeyModifier.sqlGen.getSQLForAlterTable(atq);
            stmt.execute(sql);
            DefaultDTKeyModifier.out.info(encryptedColName + "_tempcol has been added.");
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new SQLException("Exception occured while adding temp column for the encrypted column " + encryptedColName + " in the table " + tableName, e);
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    private void encryptDataInTempColumn(final String tableName, final String encryptedColName) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            DefaultDTKeyModifier.out.info("going to reencrypt data for the column " + encryptedColName + " in the table " + tableName);
            con = RelationalAPI.getInstance().getConnection();
            stmt = con.createStatement();
            final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
            final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(encryptedColName);
            final Column newCol = new Column(tableName, encryptedColName + "_tempcol");
            newCol.setDefinition(colDef);
            final Column oldCol = new Column(tableName, encryptedColName);
            oldCol.setDefinition(colDef);
            final String sql = this.getSQLForReEncryptingData(tableName, oldCol, newCol);
            DefaultDTKeyModifier.out.fine("sql:: " + sql);
            stmt.executeUpdate(sql);
            DefaultDTKeyModifier.out.info("column re-encrypted with new key: " + encryptedColName + "_tempcol");
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new SQLException("Exception occured while recrypting the data in temp column for the encrypted column " + encryptedColName + " in the table " + tableName, e);
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    private String getSQLForReEncryptingData(final String tableName, final Column oldCol, final Column newCol) throws QueryConstructionException {
        final StringBuilder updateBuffer = new StringBuilder(100);
        updateBuffer.append("UPDATE ");
        updateBuffer.append(DefaultDTKeyModifier.sqlGen.getDBSpecificTableName(tableName));
        updateBuffer.append(" SET ");
        updateBuffer.append(DefaultDTKeyModifier.sqlGen.getDBSpecificColumnName(newCol.getColumnName()) + " = ");
        final String decryptStr = this.getDBSpecificDecryptionString(oldCol, DefaultDTKeyModifier.sqlGen.getDBSpecificColumnName(oldCol.getColumnName()), true);
        updateBuffer.append(this.getDBSpecificEncryptionString(newCol, decryptStr));
        return updateBuffer.toString();
    }
    
    private String getDBSpecificDecryptionString(final Column column, final String value, final boolean useOldKey) {
        String dataType = column.getDataType();
        if (DataTypeUtil.isEDT(dataType)) {
            final DataTypeDefinition dt = DataTypeManager.getDataTypeDefinition(dataType);
            dataType = dt.getBaseType();
        }
        if (useOldKey) {
            return DefaultDTKeyModifier.sqlGen.getDecryptSQL(value, dataType);
        }
        return DefaultDTKeyModifier.newSqlGen.getDecryptSQL(value, dataType);
    }
    
    private String getDBSpecificEncryptionString(final Column column, final String value) {
        return DefaultDTKeyModifier.newSqlGen.getDBSpecificEncryptionString(column, value);
    }
    
    private void modifyTempColumn(final String tableName, final String encryptedColName) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            DefaultDTKeyModifier.out.info("going to alter column " + encryptedColName + "_tempcol");
            con = RelationalAPI.getInstance().getConnection();
            stmt = con.createStatement();
            final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
            final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(encryptedColName);
            final AlterTableQuery atq = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            final ColumnDefinition cloned_cd = (ColumnDefinition)colDef.clone();
            cloned_cd.setColumnName(encryptedColName + "_tempcol");
            atq.modifyColumn(cloned_cd.getColumnName(), cloned_cd);
            final String sql = DefaultDTKeyModifier.sqlGen.getSQLForAlterTable(atq);
            stmt.execute(sql);
            DefaultDTKeyModifier.out.info(encryptedColName + "_tempcol has been altered.");
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new SQLException("Exception occured while modifying temp column for the encrypted column " + encryptedColName + " in the table " + tableName, e);
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    private static void renameColumn(final String tableName, final String oldColumnName, final String newColumnName) throws QueryConstructionException, SQLException, DataAccessException, MetaDataException, CloneNotSupportedException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            stmt = con.createStatement();
            DefaultDTKeyModifier.out.info("going to rename column : " + oldColumnName + " to : " + newColumnName + " in the table " + tableName);
            final AlterTableQuery atq = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            atq.renameColumn(oldColumnName, newColumnName);
            final String sql = DefaultDTKeyModifier.sqlGen.getSQLForAlterTable(atq);
            DefaultDTKeyModifier.out.fine("rename sql:: " + sql);
            stmt.execute(sql);
            DefaultDTKeyModifier.out.info("renamed column : " + oldColumnName + " to : " + newColumnName + " in the table " + tableName);
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    public boolean sanitize(final String tableName, final String encryptedColName, final Map<String, String> diffMap) throws KeyModificationException {
        Connection con = null;
        Statement stmt = null;
        DataSet ds = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            stmt = con.createStatement();
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            DefaultDTKeyModifier.out.info("Going to run sanity for the table:: " + tableName);
            ds = this.getDataForSanityTest(tableName, encryptedColName, con, stmt);
            if (ds.next()) {
                final StringBuilder strBuff = new StringBuilder(50);
                strBuff.append("{");
                for (final String pkcol : td.getPrimaryKey().getColumnList()) {
                    strBuff.append(pkcol).append("=").append(ds.getValue(pkcol)).append(",");
                }
                strBuff.append("diff_column={").append(encryptedColName).append(",").append(encryptedColName + "_tempcol1").append("}");
                strBuff.append("}");
                diffMap.put(tableName, strBuff.toString());
                return false;
            }
            return true;
        }
        catch (final Exception e) {
            throw new KeyModificationException("Exception occured while running sanity for the column " + encryptedColName + " in the table " + tableName, (Throwable)e);
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    protected static boolean isEquals(final Object srcVal, final Object dstVal) throws IOException {
        if (srcVal instanceof InputStream && dstVal instanceof InputStream) {
            return IOUtils.contentEquals((InputStream)srcVal, (InputStream)dstVal);
        }
        return (srcVal == null) ? (dstVal == null) : srcVal.equals(dstVal);
    }
    
    private DataSet getDataForSanityTest(final String tableName, final String encryptedColName, final Connection con, final Statement stmt) throws SQLException {
        ColumnDefinition colDef = null;
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
            colDef = td.getColumnDefinitionByName(encryptedColName);
        }
        catch (final MetaDataException e) {
            e.printStackTrace();
        }
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        final Column newColumn = new Column(tableName, encryptedColName);
        newColumn.setDefinition(colDef);
        final Column oldColumn = new Column(tableName, encryptedColName + "_tempcol1");
        oldColumn.setDefinition(colDef);
        ResultSetAdapter rs = null;
        List<String> pkColNames = new ArrayList<String>();
        DataSet ds = null;
        String sql = null;
        try {
            pkColNames = td.getPrimaryKey().getColumnList();
            sql = this.getSanitySQL(tableName, oldColumn, newColumn, pkColNames);
            final List<Column> colList = new ArrayList<Column>();
            Column col = null;
            for (final String pkcolName : pkColNames) {
                col = new Column(tableName, pkcolName);
                col.setDefinition(td.getColumnDefinitionByName(pkcolName));
                colList.add(col);
                sq.addSelectColumn(col);
            }
            colList.add(oldColumn);
            colList.add(newColumn);
            sq.addSelectColumn(oldColumn);
            sq.addSelectColumn(newColumn);
            rs = DefaultDTKeyModifier.dbAdapter.executeQuery(stmt, sql);
            ds = new DataSet(rs, (Query)sq, (List)colList, stmt);
        }
        catch (final Exception e2) {
            throw new SQLException("Exception while checking the sanity for the table: " + tableName, e2);
        }
        return ds;
    }
    
    private String getSanitySQL(final String tableName, final Column oldColumn, final Column newColumn, final List<String> pkColList) throws QueryConstructionException {
        final StringBuilder selectBuffer = new StringBuilder(150);
        selectBuffer.append("SELECT");
        for (int i = 0; i < pkColList.size(); ++i) {
            if (i == 0) {
                selectBuffer.append(" " + DefaultDTKeyModifier.sqlGen.getDBSpecificColumnName((String)pkColList.get(i)));
            }
            else {
                selectBuffer.append(" , " + DefaultDTKeyModifier.sqlGen.getDBSpecificColumnName((String)pkColList.get(i)));
            }
        }
        selectBuffer.append(" FROM ");
        selectBuffer.append(" " + DefaultDTKeyModifier.sqlGen.getDBSpecificTableName(tableName) + " ");
        selectBuffer.append(" WHERE ");
        selectBuffer.append(this.getDBSpecificDecryptionString(oldColumn, DefaultDTKeyModifier.sqlGen.getDBSpecificColumnName(oldColumn.getColumnName()), true) + " != " + this.getDBSpecificDecryptionString(newColumn, DefaultDTKeyModifier.sqlGen.getDBSpecificColumnName(newColumn.getColumnName()), false));
        DefaultDTKeyModifier.out.fine("sanity sql:: " + selectBuffer.toString());
        return selectBuffer.toString();
    }
    
    private void dropColumn(final String tableName, final String columnName) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            stmt = con.createStatement();
            DefaultDTKeyModifier.out.info("going to remove column: " + columnName + " of the table: " + tableName);
            final AlterTableQuery atq = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            atq.removeColumn(columnName);
            final String sql = DefaultDTKeyModifier.dbAdapter.getSQLGenerator().getSQLForAlterTable(atq);
            DefaultDTKeyModifier.out.fine("drop sql:: " + sql);
            stmt.execute(sql);
            DefaultDTKeyModifier.out.info("removed column: " + columnName + " of the table: " + tableName);
        }
        catch (final Exception e) {
            throw new SQLException("Exception occurred while dropping up the temp column " + columnName + " in the table " + tableName, e);
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    public boolean cleanUp(final String tableName, final String columnName, final boolean isSuccess) throws KeyModificationException {
        try {
            if (isSuccess) {
                this.dropColumn(tableName, columnName + "_tempcol1");
                ECTagModifierUtil.updateStatus(tableName, columnName, 7);
            }
            else {
                Criteria cri = new Criteria(new Column("ECMStatus", "TABLENAME"), (Object)tableName, 0);
                cri = cri.and(new Criteria(new Column("ECMStatus", "COLUMNNAME"), (Object)columnName, 0));
                final DataObject dobj = DataAccess.get("ECMStatus", cri);
                final Row row = dobj.getFirstRow("ECMStatus");
                final int status = (int)row.get("STATUS");
                if (status == 2 || status == 3 || status == 4) {
                    this.dropColumn(tableName, columnName + "_tempcol");
                }
                else if (status == 5) {
                    renameColumn(tableName, columnName + "_tempcol1", columnName);
                    this.dropColumn(tableName, columnName + "_tempcol");
                }
                else if (status == 6) {
                    this.dropColumn(tableName, columnName);
                    renameColumn(tableName, columnName + "_tempcol1", columnName);
                }
            }
            return true;
        }
        catch (final Exception e) {
            throw new KeyModificationException("Exception occurred while cleaning up the temp column " + columnName + " in the table " + tableName, (Throwable)e);
        }
    }
    
    static {
        DefaultDTKeyModifier.out = Logger.getLogger(DefaultDTKeyModifier.class.getName());
        DefaultDTKeyModifier.dbAdapter = null;
        DefaultDTKeyModifier.newDBAdapter = null;
        DefaultDTKeyModifier.sqlGen = null;
        DefaultDTKeyModifier.newSqlGen = null;
    }
}
