package com.microsoft.sqlserver.jdbc;

import java.util.Set;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.text.MessageFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;

public class SQLServerBulkBatchInsertRecord extends SQLServerBulkRecord
{
    private static final long serialVersionUID = -955998113956445541L;
    private List<Parameter[]> batchParam;
    private int batchParamIndex;
    private List<String> columnList;
    private List<String> valueList;
    private static final String loggerClassName = "SQLServerBulkBatchInsertRecord";
    
    public SQLServerBulkBatchInsertRecord(final ArrayList<Parameter[]> batchParam, final ArrayList<String> columnList, final ArrayList<String> valueList, final String encoding) throws SQLServerException {
        this.batchParamIndex = -1;
        this.initLoggerResources();
        if (SQLServerBulkBatchInsertRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkBatchInsertRecord.loggerExternal.entering(this.loggerPackageName, "SQLServerBulkBatchInsertRecord", new Object[] { batchParam, encoding });
        }
        if (null == batchParam) {
            this.throwInvalidArgument("batchParam");
        }
        if (null == valueList) {
            this.throwInvalidArgument("valueList");
        }
        this.batchParam = batchParam;
        this.columnList = columnList;
        this.valueList = valueList;
        this.columnMetadata = new HashMap<Integer, ColumnMetadata>();
        SQLServerBulkBatchInsertRecord.loggerExternal.exiting(this.loggerPackageName, "SQLServerBulkBatchInsertRecord");
    }
    
    private void initLoggerResources() {
        super.loggerPackageName = "com.microsoft.sqlserver.jdbc.SQLServerBulkBatchInsertRecord";
    }
    
    private Object convertValue(final ColumnMetadata cm, final Object data) throws SQLServerException {
        switch (cm.columnType) {
            case 4: {
                final DecimalFormat decimalFormatter = new DecimalFormat("#");
                decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                final String formatedfInput = decimalFormatter.format(Double.parseDouble(data.toString()));
                return Integer.valueOf(formatedfInput);
            }
            case -6:
            case 5: {
                final DecimalFormat decimalFormatter = new DecimalFormat("#");
                decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                final String formatedfInput = decimalFormatter.format(Double.parseDouble(data.toString()));
                return Short.valueOf(formatedfInput);
            }
            case -5: {
                final BigDecimal bd = new BigDecimal(data.toString().trim());
                try {
                    return bd.setScale(0, RoundingMode.DOWN).longValueExact();
                }
                catch (final ArithmeticException ex) {
                    final String value = "'" + data + "'";
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                    throw new SQLServerException(form.format(new Object[] { value, JDBCType.of(cm.columnType) }), null, 0, ex);
                }
            }
            case 2:
            case 3: {
                final BigDecimal bd = new BigDecimal(data.toString().trim());
                return bd.setScale(cm.scale, RoundingMode.HALF_UP);
            }
            case -7: {
                try {
                    return (0.0 == Double.parseDouble(data.toString())) ? Boolean.FALSE : Boolean.TRUE;
                }
                catch (final NumberFormatException e) {
                    return Boolean.parseBoolean(data.toString());
                }
            }
            case 7: {
                return Float.parseFloat(data.toString());
            }
            case 8: {
                return Double.parseDouble(data.toString());
            }
            case -4:
            case -3:
            case -2:
            case 2004: {
                if (data instanceof byte[]) {
                    return data;
                }
                final String binData = data.toString().trim();
                if (binData.startsWith("0x") || binData.startsWith("0X")) {
                    return binData.substring(2);
                }
                return binData;
            }
            case 2013: {
                OffsetTime offsetTimeValue;
                if (null != cm.dateTimeFormatter) {
                    offsetTimeValue = OffsetTime.parse(data.toString(), cm.dateTimeFormatter);
                }
                else if (this.timeFormatter != null) {
                    offsetTimeValue = OffsetTime.parse(data.toString(), this.timeFormatter);
                }
                else {
                    offsetTimeValue = OffsetTime.parse(data.toString());
                }
                return offsetTimeValue;
            }
            case 2014: {
                OffsetDateTime offsetDateTimeValue;
                if (null != cm.dateTimeFormatter) {
                    offsetDateTimeValue = OffsetDateTime.parse(data.toString(), cm.dateTimeFormatter);
                }
                else if (this.dateTimeFormatter != null) {
                    offsetDateTimeValue = OffsetDateTime.parse(data.toString(), this.dateTimeFormatter);
                }
                else {
                    offsetDateTimeValue = OffsetDateTime.parse(data.toString());
                }
                return offsetDateTimeValue;
            }
            case 0: {
                return null;
            }
            default: {
                return data;
            }
        }
    }
    
    private String removeSingleQuote(final String s) {
        final int len = s.length();
        return (s.charAt(0) == '\'' && s.charAt(len - 1) == '\'') ? s.substring(1, len - 1) : s;
    }
    
    @Override
    public Object[] getRowData() throws SQLServerException {
        final Object[] data = new Object[this.columnMetadata.size()];
        int valueIndex = 0;
        int columnListIndex = 0;
        if (null != this.columnList && this.columnList.size() != this.valueList.size()) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DataSchemaMismatch"));
            final Object[] msgArgs = new Object[0];
            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
        }
        for (final Map.Entry<Integer, ColumnMetadata> pair : this.columnMetadata.entrySet()) {
            final int index = pair.getKey() - 1;
            Object rowData;
            if (null == this.columnList || this.columnList.size() == 0) {
                final String valueData = this.valueList.get(index);
                if ("?".equalsIgnoreCase(valueData)) {
                    rowData = this.batchParam.get(this.batchParamIndex)[valueIndex++].getSetterValue();
                }
                else if ("null".equalsIgnoreCase(valueData)) {
                    rowData = null;
                }
                else {
                    rowData = this.removeSingleQuote(valueData);
                }
            }
            else if (this.columnList.size() > columnListIndex && this.columnList.get(columnListIndex).equalsIgnoreCase(this.columnMetadata.get(index + 1).columnName)) {
                final String valueData = this.valueList.get(columnListIndex);
                if ("?".equalsIgnoreCase(valueData)) {
                    rowData = this.batchParam.get(this.batchParamIndex)[valueIndex++].getSetterValue();
                }
                else if ("null".equalsIgnoreCase(valueData)) {
                    rowData = null;
                }
                else {
                    rowData = this.removeSingleQuote(valueData);
                }
                ++columnListIndex;
            }
            else {
                rowData = null;
            }
            try {
                if (null == rowData) {
                    data[index] = null;
                }
                else if (0 == rowData.toString().length()) {
                    data[index] = "";
                }
                else {
                    data[index] = this.convertValue(pair.getValue(), rowData);
                }
            }
            catch (final IllegalArgumentException e) {
                final String value = "'" + rowData + "'";
                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                throw new SQLServerException(form2.format(new Object[] { value, JDBCType.of(pair.getValue().columnType) }), null, 0, e);
            }
            catch (final ArrayIndexOutOfBoundsException e2) {
                throw new SQLServerException(SQLServerException.getErrString("R_DataSchemaMismatch"), e2);
            }
        }
        return data;
    }
    
    @Override
    void addColumnMetadataInternal(final int positionInSource, final String name, final int jdbcType, final int precision, final int scale, final DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        if (SQLServerBulkBatchInsertRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkBatchInsertRecord.loggerExternal.entering(this.loggerPackageName, "addColumnMetadata", new Object[] { positionInSource, name, jdbcType, precision, scale });
        }
        String colName = "";
        if (0 >= positionInSource) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumnOrdinal"));
            final Object[] msgArgs = { positionInSource };
            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
        }
        if (null != name) {
            colName = name.trim();
        }
        else if (null != this.columnNames && this.columnNames.length >= positionInSource) {
            colName = this.columnNames[positionInSource - 1];
        }
        if (null != this.columnNames && positionInSource > this.columnNames.length) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
            final Object[] msgArgs = { positionInSource };
            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
        }
        this.checkDuplicateColumnName(positionInSource, name);
        switch (jdbcType) {
            case -155:
            case 91:
            case 92:
            case 93: {
                this.columnMetadata.put(positionInSource, new ColumnMetadata(colName, jdbcType, precision, scale, dateTimeFormatter));
                break;
            }
            case 2009: {
                this.columnMetadata.put(positionInSource, new ColumnMetadata(colName, -16, precision, scale, dateTimeFormatter));
                break;
            }
            case 6: {
                this.columnMetadata.put(positionInSource, new ColumnMetadata(colName, 8, precision, scale, dateTimeFormatter));
                break;
            }
            case 16: {
                this.columnMetadata.put(positionInSource, new ColumnMetadata(colName, -7, precision, scale, dateTimeFormatter));
                break;
            }
            default: {
                this.columnMetadata.put(positionInSource, new ColumnMetadata(colName, jdbcType, precision, scale, dateTimeFormatter));
                break;
            }
        }
        SQLServerBulkBatchInsertRecord.loggerExternal.exiting(this.loggerPackageName, "addColumnMetadata");
    }
    
    @Override
    public boolean next() throws SQLServerException {
        ++this.batchParamIndex;
        return this.batchParamIndex < this.batchParam.size();
    }
}
