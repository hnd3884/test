package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public final class SQLServerResultSetMetaData implements ISQLServerResultSetMetaData
{
    private static final long serialVersionUID = -5747558730471411712L;
    private SQLServerConnection con;
    private final SQLServerResultSet rs;
    private static final Logger logger;
    private static final AtomicInteger baseID;
    private final String traceID;
    
    private static int nextInstanceID() {
        return SQLServerResultSetMetaData.baseID.incrementAndGet();
    }
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    SQLServerResultSetMetaData(final SQLServerConnection con, final SQLServerResultSet rs) {
        this.traceID = " SQLServerResultSetMetaData:" + nextInstanceID();
        this.con = con;
        this.rs = rs;
        assert rs != null;
        if (SQLServerResultSetMetaData.logger.isLoggable(Level.FINE)) {
            SQLServerResultSetMetaData.logger.fine(this.toString() + " created by (" + rs.toString() + ")");
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        final boolean f = iface.isInstance(this);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        return t;
    }
    
    @Override
    public String getCatalogName(final int column) throws SQLServerException {
        return this.rs.getColumn(column).getTableName().getDatabaseName();
    }
    
    @Override
    public int getColumnCount() throws SQLServerException {
        return this.rs.getColumnCount();
    }
    
    @Override
    public int getColumnDisplaySize(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getDisplaySize();
        }
        return this.rs.getColumn(column).getTypeInfo().getDisplaySize();
    }
    
    @Override
    public String getColumnLabel(final int column) throws SQLServerException {
        return this.rs.getColumn(column).getColumnName();
    }
    
    @Override
    public String getColumnName(final int column) throws SQLServerException {
        return this.rs.getColumn(column).getColumnName();
    }
    
    @Override
    public int getColumnType(final int column) throws SQLServerException {
        TypeInfo typeInfo = this.rs.getColumn(column).getTypeInfo();
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            typeInfo = cryptoMetadata.getBaseTypeInfo();
        }
        JDBCType jdbcType = typeInfo.getSSType().getJDBCType();
        final SSType sqlType = typeInfo.getSSType();
        if (SSType.SQL_VARIANT == sqlType) {
            jdbcType = JDBCType.SQL_VARIANT;
        }
        if (SSType.UDT == sqlType) {
            if (typeInfo.getSSTypeName().equalsIgnoreCase(SSType.GEOMETRY.name())) {
                jdbcType = JDBCType.GEOMETRY;
            }
            if (typeInfo.getSSTypeName().equalsIgnoreCase(SSType.GEOGRAPHY.name())) {
                jdbcType = JDBCType.GEOGRAPHY;
            }
        }
        int r = jdbcType.asJavaSqlType();
        if (this.con.isKatmaiOrLater()) {
            switch (sqlType) {
                case VARCHARMAX: {
                    r = SSType.VARCHAR.getJDBCType().asJavaSqlType();
                    break;
                }
                case NVARCHARMAX: {
                    r = SSType.NVARCHAR.getJDBCType().asJavaSqlType();
                    break;
                }
                case VARBINARYMAX: {
                    r = SSType.VARBINARY.getJDBCType().asJavaSqlType();
                    break;
                }
                case DATETIME:
                case SMALLDATETIME: {
                    r = SSType.DATETIME2.getJDBCType().asJavaSqlType();
                    break;
                }
                case MONEY:
                case SMALLMONEY: {
                    r = SSType.DECIMAL.getJDBCType().asJavaSqlType();
                    break;
                }
                case GUID: {
                    r = SSType.CHAR.getJDBCType().asJavaSqlType();
                    break;
                }
            }
        }
        return r;
    }
    
    @Override
    public String getColumnTypeName(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getSSTypeName();
        }
        return this.rs.getColumn(column).getTypeInfo().getSSTypeName();
    }
    
    @Override
    public int getPrecision(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getPrecision();
        }
        return this.rs.getColumn(column).getTypeInfo().getPrecision();
    }
    
    @Override
    public int getScale(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getScale();
        }
        return this.rs.getColumn(column).getTypeInfo().getScale();
    }
    
    @Override
    public String getSchemaName(final int column) throws SQLServerException {
        return this.rs.getColumn(column).getTableName().getSchemaName();
    }
    
    @Override
    public String getTableName(final int column) throws SQLServerException {
        return this.rs.getColumn(column).getTableName().getObjectName();
    }
    
    @Override
    public boolean isAutoIncrement(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isIdentity();
        }
        return this.rs.getColumn(column).getTypeInfo().isIdentity();
    }
    
    @Override
    public boolean isCaseSensitive(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isCaseSensitive();
        }
        return this.rs.getColumn(column).getTypeInfo().isCaseSensitive();
    }
    
    @Override
    public boolean isCurrency(final int column) throws SQLServerException {
        SSType ssType = this.rs.getColumn(column).getTypeInfo().getSSType();
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            ssType = cryptoMetadata.getBaseTypeInfo().getSSType();
        }
        return SSType.MONEY == ssType || SSType.SMALLMONEY == ssType;
    }
    
    @Override
    public boolean isDefinitelyWritable(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return TypeInfo.UPDATABLE_READ_WRITE == cryptoMetadata.getBaseTypeInfo().getUpdatability();
        }
        return TypeInfo.UPDATABLE_READ_WRITE == this.rs.getColumn(column).getTypeInfo().getUpdatability();
    }
    
    @Override
    public int isNullable(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isNullable() ? 1 : 0;
        }
        return this.rs.getColumn(column).getTypeInfo().isNullable() ? 1 : 0;
    }
    
    @Override
    public boolean isReadOnly(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return TypeInfo.UPDATABLE_READ_ONLY == cryptoMetadata.getBaseTypeInfo().getUpdatability();
        }
        return TypeInfo.UPDATABLE_READ_ONLY == this.rs.getColumn(column).getTypeInfo().getUpdatability();
    }
    
    @Override
    public boolean isSearchable(final int column) throws SQLServerException {
        SSType ssType = null;
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            ssType = cryptoMetadata.getBaseTypeInfo().getSSType();
        }
        else {
            ssType = this.rs.getColumn(column).getTypeInfo().getSSType();
        }
        switch (ssType) {
            case IMAGE:
            case TEXT:
            case NTEXT:
            case UDT:
            case XML: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public boolean isSigned(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType().isSigned();
        }
        return this.rs.getColumn(column).getTypeInfo().getSSType().getJDBCType().isSigned();
    }
    
    @Override
    public boolean isSparseColumnSet(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isSparseColumnSet();
        }
        return this.rs.getColumn(column).getTypeInfo().isSparseColumnSet();
    }
    
    @Override
    public boolean isWritable(final int column) throws SQLServerException {
        int updatability = -1;
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            updatability = cryptoMetadata.getBaseTypeInfo().getUpdatability();
        }
        else {
            updatability = this.rs.getColumn(column).getTypeInfo().getUpdatability();
        }
        return TypeInfo.UPDATABLE_READ_WRITE == updatability || TypeInfo.UPDATABLE_UNKNOWN == updatability;
    }
    
    @Override
    public String getColumnClassName(final int column) throws SQLServerException {
        final CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType().className();
        }
        return this.rs.getColumn(column).getTypeInfo().getSSType().getJDBCType().className();
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerResultSetMetaData");
        baseID = new AtomicInteger(0);
    }
}
