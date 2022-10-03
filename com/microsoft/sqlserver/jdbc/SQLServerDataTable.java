package com.microsoft.sqlserver.jdbc;

import java.util.Arrays;
import java.util.UUID;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import microsoft.sql.DateTimeOffset;
import java.util.Date;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;

public final class SQLServerDataTable
{
    int rowCount;
    int columnCount;
    Map<Integer, SQLServerDataColumn> columnMetadata;
    Set<String> columnNames;
    Map<Integer, Object[]> rows;
    private String tvpName;
    
    public SQLServerDataTable() throws SQLServerException {
        this.rowCount = 0;
        this.columnCount = 0;
        this.columnMetadata = null;
        this.columnNames = null;
        this.rows = null;
        this.tvpName = null;
        this.columnMetadata = new LinkedHashMap<Integer, SQLServerDataColumn>();
        this.columnNames = new HashSet<String>();
        this.rows = new HashMap<Integer, Object[]>();
    }
    
    public synchronized void clear() {
        this.rowCount = 0;
        this.columnCount = 0;
        this.columnMetadata.clear();
        this.columnNames.clear();
        this.rows.clear();
    }
    
    public synchronized Iterator<Map.Entry<Integer, Object[]>> getIterator() {
        if (null != this.rows && null != this.rows.entrySet()) {
            return this.rows.entrySet().iterator();
        }
        return null;
    }
    
    public synchronized void addColumnMetadata(final String columnName, final int sqlType) throws SQLServerException {
        Util.checkDuplicateColumnName(columnName, this.columnNames);
        this.columnMetadata.put(this.columnCount++, new SQLServerDataColumn(columnName, sqlType));
    }
    
    public synchronized void addColumnMetadata(final SQLServerDataColumn column) throws SQLServerException {
        Util.checkDuplicateColumnName(column.columnName, this.columnNames);
        this.columnMetadata.put(this.columnCount++, column);
    }
    
    public synchronized void addRow(final Object... values) throws SQLServerException {
        try {
            final int columnCount = this.columnMetadata.size();
            if (null != values && values.length > columnCount) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_moreDataInRowThanColumnInTVP"));
                final Object[] msgArgs = new Object[0];
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            final Iterator<Map.Entry<Integer, SQLServerDataColumn>> columnsIterator = this.columnMetadata.entrySet().iterator();
            final Object[] rowValues = new Object[columnCount];
            int currentColumn = 0;
            while (columnsIterator.hasNext()) {
                Object val = null;
                if (null != values && currentColumn < values.length && null != values[currentColumn]) {
                    val = values[currentColumn];
                }
                ++currentColumn;
                final Map.Entry<Integer, SQLServerDataColumn> pair = columnsIterator.next();
                final JDBCType jdbcType = JDBCType.of(pair.getValue().javaSqlType);
                this.internalAddrow(jdbcType, val, rowValues, pair);
            }
            this.rows.put(this.rowCount++, rowValues);
        }
        catch (final NumberFormatException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e);
        }
        catch (final ClassCastException e2) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e2);
        }
    }
    
    private void internalAddrow(final JDBCType jdbcType, Object val, final Object[] rowValues, final Map.Entry<Integer, SQLServerDataColumn> pair) throws SQLServerException {
        final int key = pair.getKey();
        if (null != val) {
            final SQLServerDataColumn currentColumnMetadata = pair.getValue();
            switch (jdbcType) {
                case BIGINT: {
                    rowValues[key] = ((val instanceof Long) ? val : Long.valueOf(Long.parseLong(val.toString())));
                    break;
                }
                case BIT: {
                    if (val instanceof Boolean) {
                        rowValues[key] = val;
                        break;
                    }
                    final String valString = val.toString();
                    if ("0".equals(valString) || valString.equalsIgnoreCase(Boolean.FALSE.toString())) {
                        rowValues[key] = Boolean.FALSE;
                    }
                    else {
                        if (!"1".equals(valString) && !valString.equalsIgnoreCase(Boolean.TRUE.toString())) {
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPInvalidColumnValue"));
                            final Object[] msgArgs = { jdbcType };
                            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                        }
                        rowValues[key] = Boolean.TRUE;
                    }
                    break;
                }
                case INTEGER: {
                    rowValues[key] = ((val instanceof Integer) ? val : Integer.valueOf(Integer.parseInt(val.toString())));
                    break;
                }
                case SMALLINT:
                case TINYINT: {
                    rowValues[key] = ((val instanceof Short) ? val : Short.valueOf(Short.parseShort(val.toString())));
                    break;
                }
                case DECIMAL:
                case NUMERIC: {
                    BigDecimal bd = null;
                    boolean isColumnMetadataUpdated = false;
                    bd = new BigDecimal(val.toString());
                    final int precision = Util.getValueLengthBaseOnJavaType(bd, JavaType.of(bd), null, null, jdbcType);
                    if (bd.scale() > currentColumnMetadata.scale) {
                        currentColumnMetadata.scale = bd.scale();
                        isColumnMetadataUpdated = true;
                    }
                    if (precision > currentColumnMetadata.precision) {
                        currentColumnMetadata.precision = precision;
                        isColumnMetadataUpdated = true;
                    }
                    final int numberOfDigitsIntegerPart = precision - bd.scale();
                    if (numberOfDigitsIntegerPart > currentColumnMetadata.numberOfDigitsIntegerPart) {
                        currentColumnMetadata.numberOfDigitsIntegerPart = numberOfDigitsIntegerPart;
                        isColumnMetadataUpdated = true;
                    }
                    if (isColumnMetadataUpdated) {
                        currentColumnMetadata.precision = currentColumnMetadata.scale + currentColumnMetadata.numberOfDigitsIntegerPart;
                        this.columnMetadata.put(pair.getKey(), currentColumnMetadata);
                    }
                    rowValues[key] = bd;
                    break;
                }
                case DOUBLE: {
                    rowValues[key] = ((val instanceof Double) ? val : Double.valueOf(Double.parseDouble(val.toString())));
                    break;
                }
                case FLOAT:
                case REAL: {
                    rowValues[key] = ((val instanceof Float) ? val : Float.valueOf(Float.parseFloat(val.toString())));
                    break;
                }
                case TIMESTAMP_WITH_TIMEZONE:
                case TIME_WITH_TIMEZONE:
                case DATE:
                case TIME:
                case TIMESTAMP:
                case DATETIMEOFFSET:
                case DATETIME:
                case SMALLDATETIME: {
                    if (val instanceof Date || val instanceof DateTimeOffset || val instanceof OffsetDateTime || val instanceof OffsetTime) {
                        rowValues[key] = val.toString();
                        break;
                    }
                    rowValues[key] = val;
                    break;
                }
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY: {
                    final int nValueLen = ((byte[])val).length;
                    if (nValueLen > currentColumnMetadata.precision) {
                        currentColumnMetadata.precision = nValueLen;
                        this.columnMetadata.put(pair.getKey(), currentColumnMetadata);
                    }
                    rowValues[key] = val;
                    break;
                }
                case CHAR:
                case VARCHAR:
                case NCHAR:
                case NVARCHAR:
                case LONGVARCHAR:
                case LONGNVARCHAR:
                case SQLXML: {
                    if (val instanceof UUID) {
                        val = val.toString();
                    }
                    final int nValueLen = 2 * ((String)val).length();
                    if (nValueLen > currentColumnMetadata.precision) {
                        currentColumnMetadata.precision = nValueLen;
                        this.columnMetadata.put(pair.getKey(), currentColumnMetadata);
                    }
                    rowValues[key] = val;
                    break;
                }
                case SQL_VARIANT: {
                    final JavaType javaType = JavaType.of(val);
                    final JDBCType internalJDBCType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
                    this.internalAddrow(internalJDBCType, val, rowValues, pair);
                    break;
                }
                default: {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_unsupportedDataTypeTVP"));
                    final Object[] msgArgs2 = { jdbcType };
                    throw new SQLServerException(null, form2.format(msgArgs2), null, 0, false);
                }
            }
        }
        else {
            rowValues[key] = null;
            if (jdbcType == JDBCType.SQL_VARIANT) {
                throw new SQLServerException(SQLServerException.getErrString("R_invalidValueForTVPWithSQLVariant"), (Throwable)null);
            }
        }
    }
    
    public synchronized Map<Integer, SQLServerDataColumn> getColumnMetadata() {
        return this.columnMetadata;
    }
    
    public String getTvpName() {
        return this.tvpName;
    }
    
    public void setTvpName(final String tvpName) {
        this.tvpName = tvpName;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.rowCount;
        hash = 31 * hash + this.columnCount;
        hash = 31 * hash + ((null != this.columnMetadata) ? this.columnMetadata.hashCode() : 0);
        hash = 31 * hash + ((null != this.columnNames) ? this.columnNames.hashCode() : 0);
        hash = 31 * hash + this.getRowsHashCode();
        hash = 31 * hash + ((null != this.tvpName) ? this.tvpName.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null != object && object.getClass() == SQLServerDataTable.class) {
            final SQLServerDataTable aSQLServerDataTable = (SQLServerDataTable)object;
            if (this.hashCode() == aSQLServerDataTable.hashCode()) {
                final boolean equalColumnMetadata = this.columnMetadata.equals(aSQLServerDataTable.columnMetadata);
                final boolean equalColumnNames = this.columnNames.equals(aSQLServerDataTable.columnNames);
                final boolean equalRowData = this.compareRows(aSQLServerDataTable.rows);
                return this.rowCount == aSQLServerDataTable.rowCount && this.columnCount == aSQLServerDataTable.columnCount && this.tvpName == aSQLServerDataTable.tvpName && equalColumnMetadata && equalColumnNames && equalRowData;
            }
        }
        return false;
    }
    
    private int getRowsHashCode() {
        if (null == this.rows) {
            return 0;
        }
        int h = 0;
        for (final Map.Entry<Integer, Object[]> entry : this.rows.entrySet()) {
            h += (entry.getKey() ^ Arrays.hashCode(entry.getValue()));
        }
        return h;
    }
    
    private boolean compareRows(final Map<Integer, Object[]> otherRows) {
        if (this.rows == otherRows) {
            return true;
        }
        if (this.rows.size() != otherRows.size()) {
            return false;
        }
        try {
            for (final Map.Entry<Integer, Object[]> e : this.rows.entrySet()) {
                final Integer key = e.getKey();
                final Object[] value = e.getValue();
                if (null == value) {
                    if (null != otherRows.get(key) || !otherRows.containsKey(key)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!Arrays.equals(value, otherRows.get(key))) {
                        return false;
                    }
                    continue;
                }
            }
        }
        catch (final ClassCastException unused) {
            return false;
        }
        catch (final NullPointerException unused2) {
            return false;
        }
        return true;
    }
}
