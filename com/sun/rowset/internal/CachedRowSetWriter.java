package com.sun.rowset.internal;

import java.io.ObjectInputStream;
import java.sql.Savepoint;
import java.sql.DatabaseMetaData;
import java.util.Map;
import java.sql.ResultSet;
import javax.sql.rowset.serial.SerialArray;
import java.sql.Array;
import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialStruct;
import java.sql.SQLInput;
import javax.sql.rowset.serial.SQLInputImpl;
import sun.reflect.misc.ReflectUtil;
import java.sql.SQLData;
import java.sql.Struct;
import java.util.Vector;
import java.sql.PreparedStatement;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import javax.sql.RowSetInternal;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.util.ArrayList;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.io.Serializable;
import javax.sql.rowset.spi.TransactionalWriter;

public class CachedRowSetWriter implements TransactionalWriter, Serializable
{
    private transient Connection con;
    private String selectCmd;
    private String updateCmd;
    private String updateWhere;
    private String deleteCmd;
    private String deleteWhere;
    private String insertCmd;
    private int[] keyCols;
    private Object[] params;
    private CachedRowSetReader reader;
    private ResultSetMetaData callerMd;
    private int callerColumnCount;
    private CachedRowSetImpl crsResolve;
    private ArrayList<Integer> status;
    private int iChangedValsInDbAndCRS;
    private int iChangedValsinDbOnly;
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = -8506030970299413976L;
    
    public CachedRowSetWriter() {
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public boolean writeData(final RowSetInternal rowSetInternal) throws SQLException {
        long n = 0L;
        PreparedStatement prepareStatement = null;
        this.iChangedValsInDbAndCRS = 0;
        this.iChangedValsinDbOnly = 0;
        final CachedRowSetImpl cachedRowSet = (CachedRowSetImpl)rowSetInternal;
        this.crsResolve = new CachedRowSetImpl();
        this.con = this.reader.connect(rowSetInternal);
        if (this.con == null) {
            throw new SQLException(this.resBundle.handleGetObject("crswriter.connect").toString());
        }
        this.initSQLStatements(cachedRowSet);
        final RowSetMetaDataImpl rowSetMetaDataImpl = (RowSetMetaDataImpl)cachedRowSet.getMetaData();
        final RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
        final int columnCount = rowSetMetaDataImpl.getColumnCount();
        (this.status = new ArrayList<Integer>(cachedRowSet.size() + 1)).add(0, null);
        metaData.setColumnCount(columnCount);
        for (int i = 1; i <= columnCount; ++i) {
            metaData.setColumnType(i, rowSetMetaDataImpl.getColumnType(i));
            metaData.setColumnName(i, rowSetMetaDataImpl.getColumnName(i));
            metaData.setNullable(i, 2);
        }
        this.crsResolve.setMetaData(metaData);
        if (this.callerColumnCount < 1) {
            if (this.reader.getCloseConnection()) {
                this.con.close();
            }
            return true;
        }
        final boolean showDeleted = cachedRowSet.getShowDeleted();
        cachedRowSet.setShowDeleted(true);
        cachedRowSet.beforeFirst();
        int n2 = 1;
        while (cachedRowSet.next()) {
            if (cachedRowSet.rowDeleted()) {
                if (this.deleteOriginalRow(cachedRowSet, this.crsResolve)) {
                    this.status.add(n2, 1);
                    ++n;
                }
                else {
                    this.status.add(n2, 3);
                }
            }
            else if (cachedRowSet.rowInserted()) {
                prepareStatement = this.con.prepareStatement(this.insertCmd);
                if (this.insertNewRow(cachedRowSet, prepareStatement, this.crsResolve)) {
                    this.status.add(n2, 2);
                    ++n;
                }
                else {
                    this.status.add(n2, 3);
                }
            }
            else if (cachedRowSet.rowUpdated()) {
                if (this.updateOriginalRow(cachedRowSet)) {
                    this.status.add(n2, 0);
                    ++n;
                }
                else {
                    this.status.add(n2, 3);
                }
            }
            else {
                cachedRowSet.getMetaData().getColumnCount();
                this.status.add(n2, 3);
                this.crsResolve.moveToInsertRow();
                for (int j = 0; j < columnCount; ++j) {
                    this.crsResolve.updateNull(j + 1);
                }
                this.crsResolve.insertRow();
                this.crsResolve.moveToCurrentRow();
            }
            ++n2;
        }
        if (prepareStatement != null) {
            prepareStatement.close();
        }
        cachedRowSet.setShowDeleted(showDeleted);
        cachedRowSet.beforeFirst();
        this.crsResolve.beforeFirst();
        if (n != 0L) {
            final SyncProviderException ex = new SyncProviderException(n + " " + this.resBundle.handleGetObject("crswriter.conflictsno").toString());
            final SyncResolverImpl syncResolverImpl = (SyncResolverImpl)ex.getSyncResolver();
            syncResolverImpl.setCachedRowSet(cachedRowSet);
            syncResolverImpl.setCachedRowSetResolver(this.crsResolve);
            syncResolverImpl.setStatus(this.status);
            syncResolverImpl.setCachedRowSetWriter(this);
            throw ex;
        }
        return true;
    }
    
    private boolean updateOriginalRow(final CachedRowSet set) throws SQLException {
        int n = 0;
        final ResultSet originalRow = set.getOriginalRow();
        originalRow.next();
        try {
            this.updateWhere = this.buildWhereClause(this.updateWhere, originalRow);
            final int index = this.selectCmd.toLowerCase().indexOf("where");
            if (index != -1) {
                this.selectCmd = this.selectCmd.substring(0, index);
            }
            final PreparedStatement prepareStatement = this.con.prepareStatement(this.selectCmd + this.updateWhere, 1005, 1007);
            for (int i = 0; i < this.keyCols.length; ++i) {
                if (this.params[i] != null) {
                    prepareStatement.setObject(++n, this.params[i]);
                }
            }
            try {
                prepareStatement.setMaxRows(set.getMaxRows());
                prepareStatement.setMaxFieldSize(set.getMaxFieldSize());
                prepareStatement.setEscapeProcessing(set.getEscapeProcessing());
                prepareStatement.setQueryTimeout(set.getQueryTimeout());
            }
            catch (final Exception ex) {}
            final ResultSet executeQuery = prepareStatement.executeQuery();
            executeQuery.getMetaData();
            if (!executeQuery.next()) {
                return true;
            }
            if (executeQuery.next()) {
                return true;
            }
            executeQuery.first();
            int n2 = 0;
            final Vector vector = new Vector();
            String s = this.updateCmd;
            Object o = null;
            int n3 = 1;
            int n4 = 1;
            this.crsResolve.moveToInsertRow();
            for (int j = 1; j <= this.callerColumnCount; ++j) {
                final Object object = originalRow.getObject(j);
                final Object object2 = set.getObject(j);
                Object object3 = executeQuery.getObject(j);
                final Map<String, Class<?>> map = (set.getTypeMap() == null) ? this.con.getTypeMap() : set.getTypeMap();
                if (object3 instanceof Struct) {
                    final Struct struct = (Struct)object3;
                    final Class clazz = map.get(struct.getSQLTypeName());
                    if (clazz != null) {
                        SQLData sqlData;
                        try {
                            sqlData = (SQLData)ReflectUtil.newInstance(clazz);
                        }
                        catch (final Exception ex2) {
                            throw new SQLException("Unable to Instantiate: ", ex2);
                        }
                        sqlData.readSQL(new SQLInputImpl(struct.getAttributes(map), map), struct.getSQLTypeName());
                        object3 = sqlData;
                    }
                }
                else if (object3 instanceof SQLData) {
                    object3 = new SerialStruct((SQLData)object3, map);
                }
                else if (object3 instanceof Blob) {
                    object3 = new SerialBlob((Blob)object3);
                }
                else if (object3 instanceof Clob) {
                    object3 = new SerialClob((Clob)object3);
                }
                else if (object3 instanceof Array) {
                    object3 = new SerialArray((Array)object3, map);
                }
                boolean b = true;
                if (object3 == null && object != null) {
                    ++this.iChangedValsinDbOnly;
                    b = false;
                    o = object3;
                }
                else if (object3 != null && !object3.equals(object)) {
                    ++this.iChangedValsinDbOnly;
                    b = false;
                    o = object3;
                }
                else if (object == null || object2 == null) {
                    if (n3 == 0 || n4 == 0) {
                        s += ", ";
                    }
                    final String string = s + set.getMetaData().getColumnName(j);
                    vector.add(j);
                    s = string + " = ? ";
                    n3 = 0;
                }
                else if (object.equals(object2)) {
                    ++n2;
                }
                else if (!object.equals(object2) && set.columnUpdated(j)) {
                    if (object3.equals(object)) {
                        if (n4 == 0 || n3 == 0) {
                            s += ", ";
                        }
                        final String string2 = s + set.getMetaData().getColumnName(j);
                        vector.add(j);
                        s = string2 + " = ? ";
                        n4 = 0;
                    }
                    else {
                        b = false;
                        o = object3;
                        ++this.iChangedValsInDbAndCRS;
                    }
                }
                if (!b) {
                    this.crsResolve.updateObject(j, o);
                }
                else {
                    this.crsResolve.updateNull(j);
                }
            }
            executeQuery.close();
            prepareStatement.close();
            this.crsResolve.insertRow();
            this.crsResolve.moveToCurrentRow();
            if ((n3 == 0 && vector.size() == 0) || n2 == this.callerColumnCount) {
                return false;
            }
            if (this.iChangedValsInDbAndCRS != 0 || this.iChangedValsinDbOnly != 0) {
                return true;
            }
            final PreparedStatement prepareStatement2 = this.con.prepareStatement(s + this.updateWhere);
            int k;
            for (k = 0; k < vector.size(); ++k) {
                final Object object4 = set.getObject(vector.get(k));
                if (object4 != null) {
                    prepareStatement2.setObject(k + 1, object4);
                }
                else {
                    prepareStatement2.setNull(k + 1, set.getMetaData().getColumnType(k + 1));
                }
            }
            int n5 = k;
            for (int l = 0; l < this.keyCols.length; ++l) {
                if (this.params[l] != null) {
                    prepareStatement2.setObject(++n5, this.params[l]);
                }
            }
            prepareStatement2.executeUpdate();
            return false;
        }
        catch (final SQLException ex3) {
            ex3.printStackTrace();
            this.crsResolve.moveToInsertRow();
            for (int n6 = 1; n6 <= this.callerColumnCount; ++n6) {
                this.crsResolve.updateNull(n6);
            }
            this.crsResolve.insertRow();
            this.crsResolve.moveToCurrentRow();
            return true;
        }
    }
    
    private boolean insertNewRow(final CachedRowSet set, final PreparedStatement preparedStatement, final CachedRowSetImpl cachedRowSetImpl) throws SQLException {
        boolean b = false;
        try (final PreparedStatement prepareStatement = this.con.prepareStatement(this.selectCmd, 1005, 1007);
             final ResultSet executeQuery = prepareStatement.executeQuery();
             final ResultSet primaryKeys = this.con.getMetaData().getPrimaryKeys(null, null, set.getTableName())) {
            final ResultSetMetaData metaData = set.getMetaData();
            final int columnCount = metaData.getColumnCount();
            final String[] array = new String[columnCount];
            int n = 0;
            while (primaryKeys.next()) {
                array[n] = primaryKeys.getString("COLUMN_NAME");
                ++n;
            }
            if (executeQuery.next()) {
                for (final String s : array) {
                    if (this.isPKNameValid(s, metaData)) {
                        final Object object = set.getObject(s);
                        if (object == null) {
                            break;
                        }
                        final String string = executeQuery.getObject(s).toString();
                        if (object.toString().equals(string)) {
                            b = true;
                            this.crsResolve.moveToInsertRow();
                            for (int k = 1; k <= columnCount; ++k) {
                                if (executeQuery.getMetaData().getColumnName(k).equals(s)) {
                                    this.crsResolve.updateObject(k, string);
                                }
                                else {
                                    this.crsResolve.updateNull(k);
                                }
                            }
                            this.crsResolve.insertRow();
                            this.crsResolve.moveToCurrentRow();
                        }
                    }
                }
            }
            if (b) {
                return b;
            }
            try {
                for (int l = 1; l <= columnCount; ++l) {
                    final Object object2 = set.getObject(l);
                    if (object2 != null) {
                        preparedStatement.setObject(l, object2);
                    }
                    else {
                        preparedStatement.setNull(l, set.getMetaData().getColumnType(l));
                    }
                }
                preparedStatement.executeUpdate();
                return false;
            }
            catch (final SQLException ex) {
                this.crsResolve.moveToInsertRow();
                for (int i = 1; i <= columnCount; ++i) {
                    this.crsResolve.updateNull(i);
                }
                this.crsResolve.insertRow();
                this.crsResolve.moveToCurrentRow();
                int i = 1;
                return i != 0;
            }
        }
    }
    
    private boolean deleteOriginalRow(final CachedRowSet set, final CachedRowSetImpl cachedRowSetImpl) throws SQLException {
        int n = 0;
        final ResultSet originalRow = set.getOriginalRow();
        originalRow.next();
        this.deleteWhere = this.buildWhereClause(this.deleteWhere, originalRow);
        final PreparedStatement prepareStatement = this.con.prepareStatement(this.selectCmd + this.deleteWhere, 1005, 1007);
        for (int i = 0; i < this.keyCols.length; ++i) {
            if (this.params[i] != null) {
                prepareStatement.setObject(++n, this.params[i]);
            }
        }
        try {
            prepareStatement.setMaxRows(set.getMaxRows());
            prepareStatement.setMaxFieldSize(set.getMaxFieldSize());
            prepareStatement.setEscapeProcessing(set.getEscapeProcessing());
            prepareStatement.setQueryTimeout(set.getQueryTimeout());
        }
        catch (final Exception ex) {}
        final ResultSet executeQuery = prepareStatement.executeQuery();
        if (!executeQuery.next()) {
            return true;
        }
        if (executeQuery.next()) {
            return true;
        }
        executeQuery.first();
        boolean b = false;
        cachedRowSetImpl.moveToInsertRow();
        for (int j = 1; j <= set.getMetaData().getColumnCount(); ++j) {
            final Object object = originalRow.getObject(j);
            final Object object2 = executeQuery.getObject(j);
            if (object != null && object2 != null) {
                if (!object.toString().equals(object2.toString())) {
                    b = true;
                    cachedRowSetImpl.updateObject(j, originalRow.getObject(j));
                }
            }
            else {
                cachedRowSetImpl.updateNull(j);
            }
        }
        cachedRowSetImpl.insertRow();
        cachedRowSetImpl.moveToCurrentRow();
        if (b) {
            return true;
        }
        final PreparedStatement prepareStatement2 = this.con.prepareStatement(this.deleteCmd + this.deleteWhere);
        int n2 = 0;
        for (int k = 0; k < this.keyCols.length; ++k) {
            if (this.params[k] != null) {
                prepareStatement2.setObject(++n2, this.params[k]);
            }
        }
        if (prepareStatement2.executeUpdate() != 1) {
            return true;
        }
        prepareStatement2.close();
        return false;
    }
    
    public void setReader(final CachedRowSetReader reader) throws SQLException {
        this.reader = reader;
    }
    
    public CachedRowSetReader getReader() throws SQLException {
        return this.reader;
    }
    
    private void initSQLStatements(final CachedRowSet set) throws SQLException {
        this.callerMd = set.getMetaData();
        this.callerColumnCount = this.callerMd.getColumnCount();
        if (this.callerColumnCount < 1) {
            return;
        }
        String s = set.getTableName();
        if (s == null) {
            s = this.callerMd.getTableName(1);
            if (s == null || s.length() == 0) {
                throw new SQLException(this.resBundle.handleGetObject("crswriter.tname").toString());
            }
        }
        final String catalogName = this.callerMd.getCatalogName(1);
        final String schemaName = this.callerMd.getSchemaName(1);
        final DatabaseMetaData metaData = this.con.getMetaData();
        this.selectCmd = "SELECT ";
        for (int i = 1; i <= this.callerColumnCount; ++i) {
            this.selectCmd += this.callerMd.getColumnName(i);
            if (i < this.callerMd.getColumnCount()) {
                this.selectCmd += ", ";
            }
            else {
                this.selectCmd += " ";
            }
        }
        this.selectCmd = this.selectCmd + "FROM " + this.buildTableName(metaData, catalogName, schemaName, s);
        this.updateCmd = "UPDATE " + this.buildTableName(metaData, catalogName, schemaName, s);
        final int index = this.updateCmd.toLowerCase().indexOf("where");
        if (index != -1) {
            this.updateCmd = this.updateCmd.substring(0, index);
        }
        this.updateCmd += "SET ";
        this.insertCmd = "INSERT INTO " + this.buildTableName(metaData, catalogName, schemaName, s);
        this.insertCmd += "(";
        for (int j = 1; j <= this.callerColumnCount; ++j) {
            this.insertCmd += this.callerMd.getColumnName(j);
            if (j < this.callerMd.getColumnCount()) {
                this.insertCmd += ", ";
            }
            else {
                this.insertCmd += ") VALUES (";
            }
        }
        for (int k = 1; k <= this.callerColumnCount; ++k) {
            this.insertCmd += "?";
            if (k < this.callerColumnCount) {
                this.insertCmd += ", ";
            }
            else {
                this.insertCmd += ")";
            }
        }
        this.deleteCmd = "DELETE FROM " + this.buildTableName(metaData, catalogName, schemaName, s);
        this.buildKeyDesc(set);
    }
    
    private String buildTableName(final DatabaseMetaData databaseMetaData, String trim, String trim2, String trim3) throws SQLException {
        String s = "";
        trim = trim.trim();
        trim2 = trim2.trim();
        trim3 = trim3.trim();
        String s2;
        if (databaseMetaData.isCatalogAtStart()) {
            if (trim != null && trim.length() > 0) {
                s = s + trim + databaseMetaData.getCatalogSeparator();
            }
            if (trim2 != null && trim2.length() > 0) {
                s = s + trim2 + ".";
            }
            s2 = s + trim3;
        }
        else {
            if (trim2 != null && trim2.length() > 0) {
                s = s + trim2 + ".";
            }
            s2 = s + trim3;
            if (trim != null && trim.length() > 0) {
                s2 = s2 + databaseMetaData.getCatalogSeparator() + trim;
            }
        }
        return s2 + " ";
    }
    
    private void buildKeyDesc(final CachedRowSet set) throws SQLException {
        this.keyCols = set.getKeyColumns();
        final ResultSetMetaData metaData = set.getMetaData();
        if (this.keyCols == null || this.keyCols.length == 0) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < this.callerColumnCount; ++i) {
                if (metaData.getColumnType(i + 1) != 2005 && metaData.getColumnType(i + 1) != 2002 && metaData.getColumnType(i + 1) != 2009 && metaData.getColumnType(i + 1) != 2004 && metaData.getColumnType(i + 1) != 2003 && metaData.getColumnType(i + 1) != 1111) {
                    list.add(i + 1);
                }
            }
            this.keyCols = new int[list.size()];
            for (int j = 0; j < list.size(); ++j) {
                this.keyCols[j] = (int)list.get(j);
            }
        }
        this.params = new Object[this.keyCols.length];
    }
    
    private String buildWhereClause(String s, final ResultSet set) throws SQLException {
        s = "WHERE ";
        for (int i = 0; i < this.keyCols.length; ++i) {
            if (i > 0) {
                s += "AND ";
            }
            s += this.callerMd.getColumnName(this.keyCols[i]);
            this.params[i] = set.getObject(this.keyCols[i]);
            if (set.wasNull()) {
                s += " IS NULL ";
            }
            else {
                s += " = ? ";
            }
        }
        return s;
    }
    
    void updateResolvedConflictToDB(final CachedRowSet set, final Connection connection) throws SQLException {
        final String s = "WHERE ";
        final int columnCount = set.getMetaData().getColumnCount();
        int[] keyColumns = set.getKeyColumns();
        String s2 = "";
        this.buildWhereClause(s, set);
        if (keyColumns == null || keyColumns.length == 0) {
            keyColumns = new int[columnCount];
            for (int i = 0; i < keyColumns.length; keyColumns[i] = ++i) {}
        }
        final Object[] array = new Object[keyColumns.length];
        final String string = "UPDATE " + this.buildTableName(connection.getMetaData(), set.getMetaData().getCatalogName(1), set.getMetaData().getSchemaName(1), set.getTableName()) + "SET ";
        int n = 1;
        for (int j = 1; j <= columnCount; ++j) {
            if (set.columnUpdated(j)) {
                if (n == 0) {
                    s2 += ", ";
                }
                s2 = s2 + set.getMetaData().getColumnName(j) + " = ? ";
                n = 0;
            }
        }
        final String string2 = string + s2;
        String s3 = "WHERE ";
        for (int k = 0; k < keyColumns.length; ++k) {
            if (k > 0) {
                s3 += "AND ";
            }
            final String string3 = s3 + set.getMetaData().getColumnName(keyColumns[k]);
            array[k] = set.getObject(keyColumns[k]);
            if (set.wasNull()) {
                s3 = string3 + " IS NULL ";
            }
            else {
                s3 = string3 + " = ? ";
            }
        }
        final PreparedStatement prepareStatement = connection.prepareStatement(string2 + s3);
        int n2 = 0;
        for (int l = 0; l < columnCount; ++l) {
            if (set.columnUpdated(l + 1)) {
                final Object object = set.getObject(l + 1);
                if (object != null) {
                    prepareStatement.setObject(++n2, object);
                }
                else {
                    prepareStatement.setNull(l + 1, set.getMetaData().getColumnType(l + 1));
                }
            }
        }
        for (int n3 = 0; n3 < keyColumns.length; ++n3) {
            if (array[n3] != null) {
                prepareStatement.setObject(++n2, array[n3]);
            }
        }
        prepareStatement.executeUpdate();
    }
    
    @Override
    public void commit() throws SQLException {
        this.con.commit();
        if (this.reader.getCloseConnection()) {
            this.con.close();
        }
    }
    
    public void commit(final CachedRowSetImpl cachedRowSetImpl, final boolean b) throws SQLException {
        this.con.commit();
        if (b && cachedRowSetImpl.getCommand() != null) {
            cachedRowSetImpl.execute(this.con);
        }
        if (this.reader.getCloseConnection()) {
            this.con.close();
        }
    }
    
    @Override
    public void rollback() throws SQLException {
        this.con.rollback();
        if (this.reader.getCloseConnection()) {
            this.con.close();
        }
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.con.rollback(savepoint);
        if (this.reader.getCloseConnection()) {
            this.con.close();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private boolean isPKNameValid(final String s, final ResultSetMetaData resultSetMetaData) throws SQLException {
        boolean b = false;
        for (int columnCount = resultSetMetaData.getColumnCount(), i = 1; i <= columnCount; ++i) {
            if (resultSetMetaData.getColumnClassName(i).equalsIgnoreCase(s)) {
                b = true;
                break;
            }
        }
        return b;
    }
}
