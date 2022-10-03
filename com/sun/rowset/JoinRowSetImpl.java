package com.sun.rowset;

import java.io.ObjectInputStream;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.RowSetListener;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.util.Calendar;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.sql.Statement;
import java.io.Reader;
import java.util.Map;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import java.util.Collection;
import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.sql.ResultSet;
import javax.sql.RowSet;
import javax.sql.rowset.Joinable;
import java.sql.SQLException;
import java.io.IOException;
import javax.sql.rowset.WebRowSet;
import java.util.Vector;
import javax.sql.rowset.JoinRowSet;

public class JoinRowSetImpl extends WebRowSetImpl implements JoinRowSet
{
    private Vector<CachedRowSetImpl> vecRowSetsInJOIN;
    private CachedRowSetImpl crsInternal;
    private Vector<Integer> vecJoinType;
    private Vector<String> vecTableNames;
    private int iMatchKey;
    private String strMatchKey;
    boolean[] supportedJOINs;
    private WebRowSet wrs;
    static final long serialVersionUID = -5590501621560008453L;
    
    public JoinRowSetImpl() throws SQLException {
        this.vecRowSetsInJOIN = new Vector<CachedRowSetImpl>();
        this.crsInternal = new CachedRowSetImpl();
        this.vecJoinType = new Vector<Integer>();
        this.vecTableNames = new Vector<String>();
        this.iMatchKey = -1;
        this.strMatchKey = null;
        this.supportedJOINs = new boolean[] { false, true, false, false, false };
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void addRowSet(final Joinable joinable) throws SQLException {
        boolean b = false;
        boolean b2 = false;
        if (!(joinable instanceof RowSet)) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notinstance").toString());
        }
        CachedRowSetImpl cachedRowSetImpl;
        if (joinable instanceof JdbcRowSetImpl) {
            cachedRowSetImpl = new CachedRowSetImpl();
            cachedRowSetImpl.populate((ResultSet)joinable);
            if (cachedRowSetImpl.size() == 0) {
                throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString());
            }
            try {
                int n = 0;
                for (int n2 = 0; n2 < joinable.getMatchColumnIndexes().length && joinable.getMatchColumnIndexes()[n2] != -1; ++n2) {
                    ++n;
                }
                final int[] matchColumn = new int[n];
                for (int i = 0; i < n; ++i) {
                    matchColumn[i] = joinable.getMatchColumnIndexes()[i];
                }
                cachedRowSetImpl.setMatchColumn(matchColumn);
            }
            catch (final SQLException ex) {}
        }
        else {
            cachedRowSetImpl = (CachedRowSetImpl)joinable;
            if (cachedRowSetImpl.size() == 0) {
                throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString());
            }
        }
        try {
            this.iMatchKey = cachedRowSetImpl.getMatchColumnIndexes()[0];
        }
        catch (final SQLException ex2) {
            b = true;
        }
        try {
            this.strMatchKey = cachedRowSetImpl.getMatchColumnNames()[0];
        }
        catch (final SQLException ex3) {
            b2 = true;
        }
        if (b && b2) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.matchnotset").toString());
        }
        if (b) {
            final ArrayList<Integer> list = new ArrayList<Integer>();
            for (int n3 = 0; n3 < cachedRowSetImpl.getMatchColumnNames().length && (this.strMatchKey = cachedRowSetImpl.getMatchColumnNames()[n3]) != null; ++n3) {
                this.iMatchKey = cachedRowSetImpl.findColumn(this.strMatchKey);
                list.add(this.iMatchKey);
            }
            final int[] matchColumn2 = new int[list.size()];
            for (int j = 0; j < list.size(); ++j) {
                matchColumn2[j] = list.get(j);
            }
            cachedRowSetImpl.setMatchColumn(matchColumn2);
        }
        this.initJOIN(cachedRowSetImpl);
    }
    
    @Override
    public void addRowSet(final RowSet set, final int matchColumn) throws SQLException {
        ((CachedRowSetImpl)set).setMatchColumn(matchColumn);
        this.addRowSet((Joinable)set);
    }
    
    @Override
    public void addRowSet(final RowSet set, final String matchColumn) throws SQLException {
        ((CachedRowSetImpl)set).setMatchColumn(matchColumn);
        this.addRowSet((Joinable)set);
    }
    
    @Override
    public void addRowSet(final RowSet[] array, final int[] array2) throws SQLException {
        if (array.length != array2.length) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString());
        }
        for (int i = 0; i < array.length; ++i) {
            ((CachedRowSetImpl)array[i]).setMatchColumn(array2[i]);
            this.addRowSet((Joinable)array[i]);
        }
    }
    
    @Override
    public void addRowSet(final RowSet[] array, final String[] array2) throws SQLException {
        if (array.length != array2.length) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString());
        }
        for (int i = 0; i < array.length; ++i) {
            ((CachedRowSetImpl)array[i]).setMatchColumn(array2[i]);
            this.addRowSet((Joinable)array[i]);
        }
    }
    
    @Override
    public Collection getRowSets() throws SQLException {
        return this.vecRowSetsInJOIN;
    }
    
    @Override
    public String[] getRowSetNames() throws SQLException {
        final Object[] array = this.vecTableNames.toArray();
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].toString();
        }
        return array2;
    }
    
    @Override
    public CachedRowSet toCachedRowSet() throws SQLException {
        return this.crsInternal;
    }
    
    @Override
    public boolean supportsCrossJoin() {
        return this.supportedJOINs[0];
    }
    
    @Override
    public boolean supportsInnerJoin() {
        return this.supportedJOINs[1];
    }
    
    @Override
    public boolean supportsLeftOuterJoin() {
        return this.supportedJOINs[2];
    }
    
    @Override
    public boolean supportsRightOuterJoin() {
        return this.supportedJOINs[3];
    }
    
    @Override
    public boolean supportsFullJoin() {
        return this.supportedJOINs[4];
    }
    
    @Override
    public void setJoinType(final int n) throws SQLException {
        if (n < 0 || n > 4) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notdefined").toString());
        }
        if (n != 1) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notsupported").toString());
        }
        this.vecJoinType.add(1);
    }
    
    private boolean checkforMatchColumn(final Joinable joinable) throws SQLException {
        return joinable.getMatchColumnIndexes().length > 0;
    }
    
    private void initJOIN(final CachedRowSet set) throws SQLException {
        try {
            final CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)set;
            final CachedRowSetImpl cachedRowSetImpl2 = new CachedRowSetImpl();
            final RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
            if (this.vecRowSetsInJOIN.isEmpty()) {
                (this.crsInternal = (CachedRowSetImpl)set.createCopy()).setMetaData((RowSetMetaData)cachedRowSetImpl.getMetaData());
                this.vecRowSetsInJOIN.add(cachedRowSetImpl);
            }
            else {
                if (this.vecRowSetsInJOIN.size() - this.vecJoinType.size() == 2) {
                    this.setJoinType(1);
                }
                else if (this.vecRowSetsInJOIN.size() - this.vecJoinType.size() == 1) {}
                this.vecTableNames.add(this.crsInternal.getTableName());
                this.vecTableNames.add(cachedRowSetImpl.getTableName());
                final int size = cachedRowSetImpl.size();
                final int size2 = this.crsInternal.size();
                int n = 0;
                for (int n2 = 0; n2 < this.crsInternal.getMatchColumnIndexes().length && this.crsInternal.getMatchColumnIndexes()[n2] != -1; ++n2) {
                    ++n;
                }
                metaData.setColumnCount(this.crsInternal.getMetaData().getColumnCount() + cachedRowSetImpl.getMetaData().getColumnCount() - n);
                cachedRowSetImpl2.setMetaData(metaData);
                this.crsInternal.beforeFirst();
                cachedRowSetImpl.beforeFirst();
                for (int n3 = 1; n3 <= size2 && !this.crsInternal.isAfterLast(); ++n3) {
                    if (this.crsInternal.next()) {
                        cachedRowSetImpl.beforeFirst();
                        for (int i = 1; i <= size; ++i) {
                            if (cachedRowSetImpl.isAfterLast()) {
                                break;
                            }
                            if (cachedRowSetImpl.next()) {
                                boolean b = true;
                                for (int j = 0; j < n; ++j) {
                                    if (!this.crsInternal.getObject(this.crsInternal.getMatchColumnIndexes()[j]).equals(cachedRowSetImpl.getObject(cachedRowSetImpl.getMatchColumnIndexes()[j]))) {
                                        b = false;
                                        break;
                                    }
                                }
                                if (b) {
                                    int n4 = 0;
                                    cachedRowSetImpl2.moveToInsertRow();
                                    int k;
                                    for (k = 1; k <= this.crsInternal.getMetaData().getColumnCount(); ++k) {
                                        boolean b2 = false;
                                        for (int l = 0; l < n; ++l) {
                                            if (k == this.crsInternal.getMatchColumnIndexes()[l]) {
                                                b2 = true;
                                                break;
                                            }
                                        }
                                        if (!b2) {
                                            cachedRowSetImpl2.updateObject(++n4, this.crsInternal.getObject(k));
                                            metaData.setColumnName(n4, this.crsInternal.getMetaData().getColumnName(k));
                                            metaData.setTableName(n4, this.crsInternal.getTableName());
                                            metaData.setColumnType(k, this.crsInternal.getMetaData().getColumnType(k));
                                            metaData.setAutoIncrement(k, this.crsInternal.getMetaData().isAutoIncrement(k));
                                            metaData.setCaseSensitive(k, this.crsInternal.getMetaData().isCaseSensitive(k));
                                            metaData.setCatalogName(k, this.crsInternal.getMetaData().getCatalogName(k));
                                            metaData.setColumnDisplaySize(k, this.crsInternal.getMetaData().getColumnDisplaySize(k));
                                            metaData.setColumnLabel(k, this.crsInternal.getMetaData().getColumnLabel(k));
                                            metaData.setColumnType(k, this.crsInternal.getMetaData().getColumnType(k));
                                            metaData.setColumnTypeName(k, this.crsInternal.getMetaData().getColumnTypeName(k));
                                            metaData.setCurrency(k, this.crsInternal.getMetaData().isCurrency(k));
                                            metaData.setNullable(k, this.crsInternal.getMetaData().isNullable(k));
                                            metaData.setPrecision(k, this.crsInternal.getMetaData().getPrecision(k));
                                            metaData.setScale(k, this.crsInternal.getMetaData().getScale(k));
                                            metaData.setSchemaName(k, this.crsInternal.getMetaData().getSchemaName(k));
                                            metaData.setSearchable(k, this.crsInternal.getMetaData().isSearchable(k));
                                            metaData.setSigned(k, this.crsInternal.getMetaData().isSigned(k));
                                        }
                                        else {
                                            cachedRowSetImpl2.updateObject(++n4, this.crsInternal.getObject(k));
                                            metaData.setColumnName(n4, this.crsInternal.getMetaData().getColumnName(k));
                                            metaData.setTableName(n4, this.crsInternal.getTableName() + "#" + cachedRowSetImpl.getTableName());
                                            metaData.setColumnType(k, this.crsInternal.getMetaData().getColumnType(k));
                                            metaData.setAutoIncrement(k, this.crsInternal.getMetaData().isAutoIncrement(k));
                                            metaData.setCaseSensitive(k, this.crsInternal.getMetaData().isCaseSensitive(k));
                                            metaData.setCatalogName(k, this.crsInternal.getMetaData().getCatalogName(k));
                                            metaData.setColumnDisplaySize(k, this.crsInternal.getMetaData().getColumnDisplaySize(k));
                                            metaData.setColumnLabel(k, this.crsInternal.getMetaData().getColumnLabel(k));
                                            metaData.setColumnType(k, this.crsInternal.getMetaData().getColumnType(k));
                                            metaData.setColumnTypeName(k, this.crsInternal.getMetaData().getColumnTypeName(k));
                                            metaData.setCurrency(k, this.crsInternal.getMetaData().isCurrency(k));
                                            metaData.setNullable(k, this.crsInternal.getMetaData().isNullable(k));
                                            metaData.setPrecision(k, this.crsInternal.getMetaData().getPrecision(k));
                                            metaData.setScale(k, this.crsInternal.getMetaData().getScale(k));
                                            metaData.setSchemaName(k, this.crsInternal.getMetaData().getSchemaName(k));
                                            metaData.setSearchable(k, this.crsInternal.getMetaData().isSearchable(k));
                                            metaData.setSigned(k, this.crsInternal.getMetaData().isSigned(k));
                                        }
                                    }
                                    for (int n5 = 1; n5 <= cachedRowSetImpl.getMetaData().getColumnCount(); ++n5) {
                                        boolean b3 = false;
                                        for (int n6 = 0; n6 < n; ++n6) {
                                            if (n5 == cachedRowSetImpl.getMatchColumnIndexes()[n6]) {
                                                b3 = true;
                                                break;
                                            }
                                        }
                                        if (!b3) {
                                            cachedRowSetImpl2.updateObject(++n4, cachedRowSetImpl.getObject(n5));
                                            metaData.setColumnName(n4, cachedRowSetImpl.getMetaData().getColumnName(n5));
                                            metaData.setTableName(n4, cachedRowSetImpl.getTableName());
                                            metaData.setColumnType(k + n5 - 1, cachedRowSetImpl.getMetaData().getColumnType(n5));
                                            metaData.setAutoIncrement(k + n5 - 1, cachedRowSetImpl.getMetaData().isAutoIncrement(n5));
                                            metaData.setCaseSensitive(k + n5 - 1, cachedRowSetImpl.getMetaData().isCaseSensitive(n5));
                                            metaData.setCatalogName(k + n5 - 1, cachedRowSetImpl.getMetaData().getCatalogName(n5));
                                            metaData.setColumnDisplaySize(k + n5 - 1, cachedRowSetImpl.getMetaData().getColumnDisplaySize(n5));
                                            metaData.setColumnLabel(k + n5 - 1, cachedRowSetImpl.getMetaData().getColumnLabel(n5));
                                            metaData.setColumnType(k + n5 - 1, cachedRowSetImpl.getMetaData().getColumnType(n5));
                                            metaData.setColumnTypeName(k + n5 - 1, cachedRowSetImpl.getMetaData().getColumnTypeName(n5));
                                            metaData.setCurrency(k + n5 - 1, cachedRowSetImpl.getMetaData().isCurrency(n5));
                                            metaData.setNullable(k + n5 - 1, cachedRowSetImpl.getMetaData().isNullable(n5));
                                            metaData.setPrecision(k + n5 - 1, cachedRowSetImpl.getMetaData().getPrecision(n5));
                                            metaData.setScale(k + n5 - 1, cachedRowSetImpl.getMetaData().getScale(n5));
                                            metaData.setSchemaName(k + n5 - 1, cachedRowSetImpl.getMetaData().getSchemaName(n5));
                                            metaData.setSearchable(k + n5 - 1, cachedRowSetImpl.getMetaData().isSearchable(n5));
                                            metaData.setSigned(k + n5 - 1, cachedRowSetImpl.getMetaData().isSigned(n5));
                                        }
                                        else {
                                            --k;
                                        }
                                    }
                                    cachedRowSetImpl2.insertRow();
                                    cachedRowSetImpl2.moveToCurrentRow();
                                }
                            }
                        }
                    }
                }
                cachedRowSetImpl2.setMetaData(metaData);
                cachedRowSetImpl2.setOriginal();
                final int[] matchColumn = new int[n];
                for (int n7 = 0; n7 < n; ++n7) {
                    matchColumn[n7] = this.crsInternal.getMatchColumnIndexes()[n7];
                }
                (this.crsInternal = (CachedRowSetImpl)cachedRowSetImpl2.createCopy()).setMatchColumn(matchColumn);
                this.crsInternal.setMetaData(metaData);
                this.vecRowSetsInJOIN.add(cachedRowSetImpl);
            }
        }
        catch (final SQLException ex) {
            ex.printStackTrace();
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.initerror").toString() + ex);
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.genericerr").toString() + ex2);
        }
    }
    
    @Override
    public String getWhereClause() throws SQLException {
        String concat = "Select ";
        String concat2 = "";
        String concat3 = "";
        final int size = this.vecRowSetsInJOIN.size();
        for (int i = 0; i < size; ++i) {
            final CachedRowSetImpl cachedRowSetImpl = this.vecRowSetsInJOIN.get(i);
            final int columnCount = cachedRowSetImpl.getMetaData().getColumnCount();
            concat2 = concat2.concat(cachedRowSetImpl.getTableName());
            concat3 = concat3.concat(concat2 + ", ");
            for (int j = 1; j < columnCount; concat = concat.concat(concat2 + "." + cachedRowSetImpl.getMetaData().getColumnName(j++)).concat(", ")) {}
        }
        final String concat4 = concat.substring(0, concat.lastIndexOf(",")).concat(" from ").concat(concat3);
        String s = concat4.substring(0, concat4.lastIndexOf(",")).concat(" where ");
        for (int k = 0; k < size; ++k) {
            final String concat5 = s.concat(this.vecRowSetsInJOIN.get(k).getMatchColumnNames()[0]);
            String s2;
            if (k % 2 != 0) {
                s2 = concat5.concat("=");
            }
            else {
                s2 = concat5.concat(" and");
            }
            s = s2.concat(" ");
        }
        return s;
    }
    
    @Override
    public boolean next() throws SQLException {
        return this.crsInternal.next();
    }
    
    @Override
    public void close() throws SQLException {
        this.crsInternal.close();
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this.crsInternal.wasNull();
    }
    
    @Override
    public String getString(final int n) throws SQLException {
        return this.crsInternal.getString(n);
    }
    
    @Override
    public boolean getBoolean(final int n) throws SQLException {
        return this.crsInternal.getBoolean(n);
    }
    
    @Override
    public byte getByte(final int n) throws SQLException {
        return this.crsInternal.getByte(n);
    }
    
    @Override
    public short getShort(final int n) throws SQLException {
        return this.crsInternal.getShort(n);
    }
    
    @Override
    public int getInt(final int n) throws SQLException {
        return this.crsInternal.getInt(n);
    }
    
    @Override
    public long getLong(final int n) throws SQLException {
        return this.crsInternal.getLong(n);
    }
    
    @Override
    public float getFloat(final int n) throws SQLException {
        return this.crsInternal.getFloat(n);
    }
    
    @Override
    public double getDouble(final int n) throws SQLException {
        return this.crsInternal.getDouble(n);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int n, final int n2) throws SQLException {
        return this.crsInternal.getBigDecimal(n);
    }
    
    @Override
    public byte[] getBytes(final int n) throws SQLException {
        return this.crsInternal.getBytes(n);
    }
    
    @Override
    public Date getDate(final int n) throws SQLException {
        return this.crsInternal.getDate(n);
    }
    
    @Override
    public Time getTime(final int n) throws SQLException {
        return this.crsInternal.getTime(n);
    }
    
    @Override
    public Timestamp getTimestamp(final int n) throws SQLException {
        return this.crsInternal.getTimestamp(n);
    }
    
    @Override
    public InputStream getAsciiStream(final int n) throws SQLException {
        return this.crsInternal.getAsciiStream(n);
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int n) throws SQLException {
        return this.crsInternal.getUnicodeStream(n);
    }
    
    @Override
    public InputStream getBinaryStream(final int n) throws SQLException {
        return this.crsInternal.getBinaryStream(n);
    }
    
    @Override
    public String getString(final String s) throws SQLException {
        return this.crsInternal.getString(s);
    }
    
    @Override
    public boolean getBoolean(final String s) throws SQLException {
        return this.crsInternal.getBoolean(s);
    }
    
    @Override
    public byte getByte(final String s) throws SQLException {
        return this.crsInternal.getByte(s);
    }
    
    @Override
    public short getShort(final String s) throws SQLException {
        return this.crsInternal.getShort(s);
    }
    
    @Override
    public int getInt(final String s) throws SQLException {
        return this.crsInternal.getInt(s);
    }
    
    @Override
    public long getLong(final String s) throws SQLException {
        return this.crsInternal.getLong(s);
    }
    
    @Override
    public float getFloat(final String s) throws SQLException {
        return this.crsInternal.getFloat(s);
    }
    
    @Override
    public double getDouble(final String s) throws SQLException {
        return this.crsInternal.getDouble(s);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        return this.crsInternal.getBigDecimal(s);
    }
    
    @Override
    public byte[] getBytes(final String s) throws SQLException {
        return this.crsInternal.getBytes(s);
    }
    
    @Override
    public Date getDate(final String s) throws SQLException {
        return this.crsInternal.getDate(s);
    }
    
    @Override
    public Time getTime(final String s) throws SQLException {
        return this.crsInternal.getTime(s);
    }
    
    @Override
    public Timestamp getTimestamp(final String s) throws SQLException {
        return this.crsInternal.getTimestamp(s);
    }
    
    @Override
    public InputStream getAsciiStream(final String s) throws SQLException {
        return this.crsInternal.getAsciiStream(s);
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String s) throws SQLException {
        return this.crsInternal.getUnicodeStream(s);
    }
    
    @Override
    public InputStream getBinaryStream(final String s) throws SQLException {
        return this.crsInternal.getBinaryStream(s);
    }
    
    @Override
    public SQLWarning getWarnings() {
        return this.crsInternal.getWarnings();
    }
    
    @Override
    public void clearWarnings() {
        this.crsInternal.clearWarnings();
    }
    
    @Override
    public String getCursorName() throws SQLException {
        return this.crsInternal.getCursorName();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.crsInternal.getMetaData();
    }
    
    @Override
    public Object getObject(final int n) throws SQLException {
        return this.crsInternal.getObject(n);
    }
    
    @Override
    public Object getObject(final int n, final Map<String, Class<?>> map) throws SQLException {
        return this.crsInternal.getObject(n, map);
    }
    
    @Override
    public Object getObject(final String s) throws SQLException {
        return this.crsInternal.getObject(s);
    }
    
    @Override
    public Object getObject(final String s, final Map<String, Class<?>> map) throws SQLException {
        return this.crsInternal.getObject(s, map);
    }
    
    @Override
    public Reader getCharacterStream(final int n) throws SQLException {
        return this.crsInternal.getCharacterStream(n);
    }
    
    @Override
    public Reader getCharacterStream(final String s) throws SQLException {
        return this.crsInternal.getCharacterStream(s);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        return this.crsInternal.getBigDecimal(n);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        return this.crsInternal.getBigDecimal(s);
    }
    
    @Override
    public int size() {
        return this.crsInternal.size();
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this.crsInternal.isBeforeFirst();
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        return this.crsInternal.isAfterLast();
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        return this.crsInternal.isFirst();
    }
    
    @Override
    public boolean isLast() throws SQLException {
        return this.crsInternal.isLast();
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        this.crsInternal.beforeFirst();
    }
    
    @Override
    public void afterLast() throws SQLException {
        this.crsInternal.afterLast();
    }
    
    @Override
    public boolean first() throws SQLException {
        return this.crsInternal.first();
    }
    
    @Override
    public boolean last() throws SQLException {
        return this.crsInternal.last();
    }
    
    @Override
    public int getRow() throws SQLException {
        return this.crsInternal.getRow();
    }
    
    @Override
    public boolean absolute(final int n) throws SQLException {
        return this.crsInternal.absolute(n);
    }
    
    @Override
    public boolean relative(final int n) throws SQLException {
        return this.crsInternal.relative(n);
    }
    
    @Override
    public boolean previous() throws SQLException {
        return this.crsInternal.previous();
    }
    
    @Override
    public int findColumn(final String s) throws SQLException {
        return this.crsInternal.findColumn(s);
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        return this.crsInternal.rowUpdated();
    }
    
    @Override
    public boolean columnUpdated(final int n) throws SQLException {
        return this.crsInternal.columnUpdated(n);
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        return this.crsInternal.rowInserted();
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        return this.crsInternal.rowDeleted();
    }
    
    @Override
    public void updateNull(final int n) throws SQLException {
        this.crsInternal.updateNull(n);
    }
    
    @Override
    public void updateBoolean(final int n, final boolean b) throws SQLException {
        this.crsInternal.updateBoolean(n, b);
    }
    
    @Override
    public void updateByte(final int n, final byte b) throws SQLException {
        this.crsInternal.updateByte(n, b);
    }
    
    @Override
    public void updateShort(final int n, final short n2) throws SQLException {
        this.crsInternal.updateShort(n, n2);
    }
    
    @Override
    public void updateInt(final int n, final int n2) throws SQLException {
        this.crsInternal.updateInt(n, n2);
    }
    
    @Override
    public void updateLong(final int n, final long n2) throws SQLException {
        this.crsInternal.updateLong(n, n2);
    }
    
    @Override
    public void updateFloat(final int n, final float n2) throws SQLException {
        this.crsInternal.updateFloat(n, n2);
    }
    
    @Override
    public void updateDouble(final int n, final double n2) throws SQLException {
        this.crsInternal.updateDouble(n, n2);
    }
    
    @Override
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        this.crsInternal.updateBigDecimal(n, bigDecimal);
    }
    
    @Override
    public void updateString(final int n, final String s) throws SQLException {
        this.crsInternal.updateString(n, s);
    }
    
    @Override
    public void updateBytes(final int n, final byte[] array) throws SQLException {
        this.crsInternal.updateBytes(n, array);
    }
    
    @Override
    public void updateDate(final int n, final Date date) throws SQLException {
        this.crsInternal.updateDate(n, date);
    }
    
    @Override
    public void updateTime(final int n, final Time time) throws SQLException {
        this.crsInternal.updateTime(n, time);
    }
    
    @Override
    public void updateTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        this.crsInternal.updateTimestamp(n, timestamp);
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.crsInternal.updateAsciiStream(n, inputStream, n2);
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.crsInternal.updateBinaryStream(n, inputStream, n2);
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        this.crsInternal.updateCharacterStream(n, reader, n2);
    }
    
    @Override
    public void updateObject(final int n, final Object o, final int n2) throws SQLException {
        this.crsInternal.updateObject(n, o, n2);
    }
    
    @Override
    public void updateObject(final int n, final Object o) throws SQLException {
        this.crsInternal.updateObject(n, o);
    }
    
    @Override
    public void updateNull(final String s) throws SQLException {
        this.crsInternal.updateNull(s);
    }
    
    @Override
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        this.crsInternal.updateBoolean(s, b);
    }
    
    @Override
    public void updateByte(final String s, final byte b) throws SQLException {
        this.crsInternal.updateByte(s, b);
    }
    
    @Override
    public void updateShort(final String s, final short n) throws SQLException {
        this.crsInternal.updateShort(s, n);
    }
    
    @Override
    public void updateInt(final String s, final int n) throws SQLException {
        this.crsInternal.updateInt(s, n);
    }
    
    @Override
    public void updateLong(final String s, final long n) throws SQLException {
        this.crsInternal.updateLong(s, n);
    }
    
    @Override
    public void updateFloat(final String s, final float n) throws SQLException {
        this.crsInternal.updateFloat(s, n);
    }
    
    @Override
    public void updateDouble(final String s, final double n) throws SQLException {
        this.crsInternal.updateDouble(s, n);
    }
    
    @Override
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        this.crsInternal.updateBigDecimal(s, bigDecimal);
    }
    
    @Override
    public void updateString(final String s, final String s2) throws SQLException {
        this.crsInternal.updateString(s, s2);
    }
    
    @Override
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        this.crsInternal.updateBytes(s, array);
    }
    
    @Override
    public void updateDate(final String s, final Date date) throws SQLException {
        this.crsInternal.updateDate(s, date);
    }
    
    @Override
    public void updateTime(final String s, final Time time) throws SQLException {
        this.crsInternal.updateTime(s, time);
    }
    
    @Override
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        this.crsInternal.updateTimestamp(s, timestamp);
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.crsInternal.updateAsciiStream(s, inputStream, n);
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.crsInternal.updateBinaryStream(s, inputStream, n);
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        this.crsInternal.updateCharacterStream(s, reader, n);
    }
    
    @Override
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        this.crsInternal.updateObject(s, o, n);
    }
    
    @Override
    public void updateObject(final String s, final Object o) throws SQLException {
        this.crsInternal.updateObject(s, o);
    }
    
    @Override
    public void insertRow() throws SQLException {
        this.crsInternal.insertRow();
    }
    
    @Override
    public void updateRow() throws SQLException {
        this.crsInternal.updateRow();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        this.crsInternal.deleteRow();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        this.crsInternal.refreshRow();
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        this.crsInternal.cancelRowUpdates();
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        this.crsInternal.moveToInsertRow();
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        this.crsInternal.moveToCurrentRow();
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return this.crsInternal.getStatement();
    }
    
    @Override
    public Ref getRef(final int n) throws SQLException {
        return this.crsInternal.getRef(n);
    }
    
    @Override
    public Blob getBlob(final int n) throws SQLException {
        return this.crsInternal.getBlob(n);
    }
    
    @Override
    public Clob getClob(final int n) throws SQLException {
        return this.crsInternal.getClob(n);
    }
    
    @Override
    public Array getArray(final int n) throws SQLException {
        return this.crsInternal.getArray(n);
    }
    
    @Override
    public Ref getRef(final String s) throws SQLException {
        return this.crsInternal.getRef(s);
    }
    
    @Override
    public Blob getBlob(final String s) throws SQLException {
        return this.crsInternal.getBlob(s);
    }
    
    @Override
    public Clob getClob(final String s) throws SQLException {
        return this.crsInternal.getClob(s);
    }
    
    @Override
    public Array getArray(final String s) throws SQLException {
        return this.crsInternal.getArray(s);
    }
    
    @Override
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        return this.crsInternal.getDate(n, calendar);
    }
    
    @Override
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        return this.crsInternal.getDate(s, calendar);
    }
    
    @Override
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        return this.crsInternal.getTime(n, calendar);
    }
    
    @Override
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        return this.crsInternal.getTime(s, calendar);
    }
    
    @Override
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        return this.crsInternal.getTimestamp(n, calendar);
    }
    
    @Override
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        return this.crsInternal.getTimestamp(s, calendar);
    }
    
    @Override
    public void setMetaData(final RowSetMetaData metaData) throws SQLException {
        this.crsInternal.setMetaData(metaData);
    }
    
    @Override
    public ResultSet getOriginal() throws SQLException {
        return this.crsInternal.getOriginal();
    }
    
    @Override
    public ResultSet getOriginalRow() throws SQLException {
        return this.crsInternal.getOriginalRow();
    }
    
    @Override
    public void setOriginalRow() throws SQLException {
        this.crsInternal.setOriginalRow();
    }
    
    @Override
    public int[] getKeyColumns() throws SQLException {
        return this.crsInternal.getKeyColumns();
    }
    
    @Override
    public void setKeyColumns(final int[] keyColumns) throws SQLException {
        this.crsInternal.setKeyColumns(keyColumns);
    }
    
    @Override
    public void updateRef(final int n, final Ref ref) throws SQLException {
        this.crsInternal.updateRef(n, ref);
    }
    
    @Override
    public void updateRef(final String s, final Ref ref) throws SQLException {
        this.crsInternal.updateRef(s, ref);
    }
    
    @Override
    public void updateClob(final int n, final Clob clob) throws SQLException {
        this.crsInternal.updateClob(n, clob);
    }
    
    @Override
    public void updateClob(final String s, final Clob clob) throws SQLException {
        this.crsInternal.updateClob(s, clob);
    }
    
    @Override
    public void updateBlob(final int n, final Blob blob) throws SQLException {
        this.crsInternal.updateBlob(n, blob);
    }
    
    @Override
    public void updateBlob(final String s, final Blob blob) throws SQLException {
        this.crsInternal.updateBlob(s, blob);
    }
    
    @Override
    public void updateArray(final int n, final Array array) throws SQLException {
        this.crsInternal.updateArray(n, array);
    }
    
    @Override
    public void updateArray(final String s, final Array array) throws SQLException {
        this.crsInternal.updateArray(s, array);
    }
    
    @Override
    public void execute() throws SQLException {
        this.crsInternal.execute();
    }
    
    @Override
    public void execute(final Connection connection) throws SQLException {
        this.crsInternal.execute(connection);
    }
    
    @Override
    public URL getURL(final int n) throws SQLException {
        return this.crsInternal.getURL(n);
    }
    
    @Override
    public URL getURL(final String s) throws SQLException {
        return this.crsInternal.getURL(s);
    }
    
    @Override
    public void writeXml(final ResultSet set, final Writer writer) throws SQLException {
        (this.wrs = new WebRowSetImpl()).populate(set);
        this.wrs.writeXml(writer);
    }
    
    @Override
    public void writeXml(final Writer writer) throws SQLException {
        this.createWebRowSet().writeXml(writer);
    }
    
    @Override
    public void readXml(final Reader reader) throws SQLException {
        (this.wrs = new WebRowSetImpl()).readXml(reader);
        this.crsInternal = (CachedRowSetImpl)this.wrs;
    }
    
    @Override
    public void readXml(final InputStream inputStream) throws SQLException, IOException {
        (this.wrs = new WebRowSetImpl()).readXml(inputStream);
        this.crsInternal = (CachedRowSetImpl)this.wrs;
    }
    
    @Override
    public void writeXml(final OutputStream outputStream) throws SQLException, IOException {
        this.createWebRowSet().writeXml(outputStream);
    }
    
    @Override
    public void writeXml(final ResultSet set, final OutputStream outputStream) throws SQLException, IOException {
        (this.wrs = new WebRowSetImpl()).populate(set);
        this.wrs.writeXml(outputStream);
    }
    
    private WebRowSet createWebRowSet() throws SQLException {
        if (this.wrs != null) {
            return this.wrs;
        }
        this.wrs = new WebRowSetImpl();
        this.crsInternal.beforeFirst();
        this.wrs.populate(this.crsInternal);
        return this.wrs;
    }
    
    @Override
    public int getJoinType() throws SQLException {
        if (this.vecJoinType == null) {
            this.setJoinType(1);
        }
        return this.vecJoinType.get(this.vecJoinType.size() - 1);
    }
    
    @Override
    public void addRowSetListener(final RowSetListener rowSetListener) {
        this.crsInternal.addRowSetListener(rowSetListener);
    }
    
    @Override
    public void removeRowSetListener(final RowSetListener rowSetListener) {
        this.crsInternal.removeRowSetListener(rowSetListener);
    }
    
    @Override
    public Collection<?> toCollection() throws SQLException {
        return this.crsInternal.toCollection();
    }
    
    @Override
    public Collection<?> toCollection(final int n) throws SQLException {
        return this.crsInternal.toCollection(n);
    }
    
    @Override
    public Collection<?> toCollection(final String s) throws SQLException {
        return this.crsInternal.toCollection(s);
    }
    
    @Override
    public CachedRowSet createCopySchema() throws SQLException {
        return this.crsInternal.createCopySchema();
    }
    
    @Override
    public void setSyncProvider(final String syncProvider) throws SQLException {
        this.crsInternal.setSyncProvider(syncProvider);
    }
    
    @Override
    public void acceptChanges() throws SyncProviderException {
        this.crsInternal.acceptChanges();
    }
    
    @Override
    public SyncProvider getSyncProvider() throws SQLException {
        return this.crsInternal.getSyncProvider();
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
}
