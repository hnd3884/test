package com.microsoft.sqlserver.jdbc;

import java.util.Set;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.io.InputStream;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class SQLServerBulkCSVFileRecord extends SQLServerBulkRecord implements AutoCloseable
{
    private static final long serialVersionUID = 1546487135640225989L;
    private BufferedReader fileReader;
    private InputStreamReader sr;
    private FileInputStream fis;
    private String currentLine;
    private final String delimiter;
    private static final String loggerClassName = "SQLServerBulkCSVFileRecord";
    
    public SQLServerBulkCSVFileRecord(final String fileToParse, final String encoding, final String delimiter, final boolean firstLineIsColumnNames) throws SQLServerException {
        this.currentLine = null;
        this.initLoggerResources();
        if (SQLServerBulkCSVFileRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCSVFileRecord.loggerExternal.entering(this.loggerPackageName, "SQLServerBulkCSVFileRecord", new Object[] { fileToParse, encoding, delimiter, firstLineIsColumnNames });
        }
        if (null == fileToParse) {
            this.throwInvalidArgument("fileToParse");
        }
        else if (null == delimiter) {
            this.throwInvalidArgument("delimiter");
        }
        this.delimiter = delimiter;
        try {
            this.fis = new FileInputStream(fileToParse);
            if (null == encoding || 0 == encoding.length()) {
                this.sr = new InputStreamReader(this.fis);
            }
            else {
                this.sr = new InputStreamReader(this.fis, encoding);
            }
            this.initFileReader(this.sr, encoding, delimiter, firstLineIsColumnNames);
        }
        catch (final UnsupportedEncodingException unsupportedEncoding) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedEncoding"));
            throw new SQLServerException(form.format(new Object[] { encoding }), null, 0, unsupportedEncoding);
        }
        catch (final Exception e) {
            throw new SQLServerException(null, e.getMessage(), null, 0, false);
        }
        this.columnMetadata = new HashMap<Integer, ColumnMetadata>();
        SQLServerBulkCSVFileRecord.loggerExternal.exiting(this.loggerPackageName, "SQLServerBulkCSVFileRecord");
    }
    
    public SQLServerBulkCSVFileRecord(final InputStream fileToParse, final String encoding, final String delimiter, final boolean firstLineIsColumnNames) throws SQLServerException {
        this.currentLine = null;
        this.initLoggerResources();
        if (SQLServerBulkCSVFileRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCSVFileRecord.loggerExternal.entering(this.loggerPackageName, "SQLServerBulkCSVFileRecord", new Object[] { fileToParse, encoding, delimiter, firstLineIsColumnNames });
        }
        if (null == fileToParse) {
            this.throwInvalidArgument("fileToParse");
        }
        else if (null == delimiter) {
            this.throwInvalidArgument("delimiter");
        }
        this.delimiter = delimiter;
        try {
            if (null == encoding || 0 == encoding.length()) {
                this.sr = new InputStreamReader(fileToParse);
            }
            else {
                this.sr = new InputStreamReader(fileToParse, encoding);
            }
            this.initFileReader(this.sr, encoding, delimiter, firstLineIsColumnNames);
        }
        catch (final UnsupportedEncodingException unsupportedEncoding) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedEncoding"));
            throw new SQLServerException(form.format(new Object[] { encoding }), null, 0, unsupportedEncoding);
        }
        catch (final Exception e) {
            throw new SQLServerException(null, e.getMessage(), null, 0, false);
        }
        this.columnMetadata = new HashMap<Integer, ColumnMetadata>();
        if (SQLServerBulkCSVFileRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCSVFileRecord.loggerExternal.exiting(this.loggerPackageName, "SQLServerBulkCSVFileRecord");
        }
    }
    
    public SQLServerBulkCSVFileRecord(final String fileToParse, final String encoding, final boolean firstLineIsColumnNames) throws SQLServerException {
        this(fileToParse, encoding, ",", firstLineIsColumnNames);
    }
    
    public SQLServerBulkCSVFileRecord(final String fileToParse, final boolean firstLineIsColumnNames) throws SQLServerException {
        this(fileToParse, null, ",", firstLineIsColumnNames);
    }
    
    private void initFileReader(final InputStreamReader sr, final String encoding, final String demlimeter, final boolean firstLineIsColumnNames) throws SQLServerException, IOException {
        this.fileReader = new BufferedReader(sr);
        if (firstLineIsColumnNames) {
            this.currentLine = this.fileReader.readLine();
            if (null != this.currentLine) {
                this.columnNames = this.currentLine.split(this.delimiter, -1);
            }
        }
    }
    
    private void initLoggerResources() {
        super.loggerPackageName = "com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord";
    }
    
    @Override
    public void close() throws SQLServerException {
        SQLServerBulkCSVFileRecord.loggerExternal.entering(this.loggerPackageName, "close");
        if (this.fileReader != null) {
            try {
                this.fileReader.close();
            }
            catch (final Exception ex) {}
        }
        if (this.sr != null) {
            try {
                this.sr.close();
            }
            catch (final Exception ex2) {}
        }
        if (this.fis != null) {
            try {
                this.fis.close();
            }
            catch (final Exception ex3) {}
        }
        SQLServerBulkCSVFileRecord.loggerExternal.exiting(this.loggerPackageName, "close");
    }
    
    @Override
    public Object[] getRowData() throws SQLServerException {
        if (null == this.currentLine) {
            return null;
        }
        final String[] data = this.currentLine.split(this.delimiter, -1);
        final Object[] dataRow = new Object[data.length];
        for (final Map.Entry<Integer, ColumnMetadata> pair : this.columnMetadata.entrySet()) {
            final ColumnMetadata cm = pair.getValue();
            if (data.length < pair.getKey() - 1) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                final Object[] msgArgs = { pair.getKey() };
                throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
            }
            if (this.columnNames != null && this.columnNames.length > data.length) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DataSchemaMismatch"));
                final Object[] msgArgs = new Object[0];
                throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
            }
            try {
                if (0 == data[pair.getKey() - 1].length()) {
                    dataRow[pair.getKey() - 1] = null;
                }
                else {
                    switch (cm.columnType) {
                        case 4: {
                            final DecimalFormat decimalFormatter = new DecimalFormat("#");
                            decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                            final String formatedfInput = decimalFormatter.format(Double.parseDouble(data[pair.getKey() - 1]));
                            dataRow[pair.getKey() - 1] = Integer.valueOf(formatedfInput);
                            continue;
                        }
                        case -6:
                        case 5: {
                            final DecimalFormat decimalFormatter = new DecimalFormat("#");
                            decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                            final String formatedfInput = decimalFormatter.format(Double.parseDouble(data[pair.getKey() - 1]));
                            dataRow[pair.getKey() - 1] = Short.valueOf(formatedfInput);
                            continue;
                        }
                        case -5: {
                            final BigDecimal bd = new BigDecimal(data[pair.getKey() - 1].trim());
                            try {
                                dataRow[pair.getKey() - 1] = bd.setScale(0, RoundingMode.DOWN).longValueExact();
                                continue;
                            }
                            catch (final ArithmeticException ex) {
                                final String value = "'" + data[pair.getKey() - 1] + "'";
                                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                                throw new SQLServerException(form2.format(new Object[] { value, JDBCType.of(cm.columnType) }), null, 0, ex);
                            }
                        }
                        case 2:
                        case 3: {
                            final BigDecimal bd = new BigDecimal(data[pair.getKey() - 1].trim());
                            dataRow[pair.getKey() - 1] = bd.setScale(cm.scale, RoundingMode.HALF_UP);
                            continue;
                        }
                        case -7: {
                            try {
                                dataRow[pair.getKey() - 1] = ((0.0 == Double.parseDouble(data[pair.getKey() - 1])) ? Boolean.FALSE : Boolean.TRUE);
                            }
                            catch (final NumberFormatException e) {
                                dataRow[pair.getKey() - 1] = Boolean.parseBoolean(data[pair.getKey() - 1]);
                            }
                            continue;
                        }
                        case 7: {
                            dataRow[pair.getKey() - 1] = Float.parseFloat(data[pair.getKey() - 1]);
                            continue;
                        }
                        case 8: {
                            dataRow[pair.getKey() - 1] = Double.parseDouble(data[pair.getKey() - 1]);
                            continue;
                        }
                        case -4:
                        case -3:
                        case -2:
                        case 2004: {
                            final String binData = data[pair.getKey() - 1].trim();
                            if (binData.startsWith("0x") || binData.startsWith("0X")) {
                                dataRow[pair.getKey() - 1] = binData.substring(2);
                                continue;
                            }
                            dataRow[pair.getKey() - 1] = binData;
                            continue;
                        }
                        case 2013: {
                            OffsetTime offsetTimeValue;
                            if (null != cm.dateTimeFormatter) {
                                offsetTimeValue = OffsetTime.parse(data[pair.getKey() - 1], cm.dateTimeFormatter);
                            }
                            else if (this.timeFormatter != null) {
                                offsetTimeValue = OffsetTime.parse(data[pair.getKey() - 1], this.timeFormatter);
                            }
                            else {
                                offsetTimeValue = OffsetTime.parse(data[pair.getKey() - 1]);
                            }
                            dataRow[pair.getKey() - 1] = offsetTimeValue;
                            continue;
                        }
                        case 2014: {
                            OffsetDateTime offsetDateTimeValue;
                            if (null != cm.dateTimeFormatter) {
                                offsetDateTimeValue = OffsetDateTime.parse(data[pair.getKey() - 1], cm.dateTimeFormatter);
                            }
                            else if (this.dateTimeFormatter != null) {
                                offsetDateTimeValue = OffsetDateTime.parse(data[pair.getKey() - 1], this.dateTimeFormatter);
                            }
                            else {
                                offsetDateTimeValue = OffsetDateTime.parse(data[pair.getKey() - 1]);
                            }
                            dataRow[pair.getKey() - 1] = offsetDateTimeValue;
                            continue;
                        }
                        case 0: {
                            dataRow[pair.getKey() - 1] = null;
                            continue;
                        }
                        default: {
                            dataRow[pair.getKey() - 1] = data[pair.getKey() - 1];
                            continue;
                        }
                    }
                }
            }
            catch (final IllegalArgumentException e2) {
                final String value2 = "'" + data[pair.getKey() - 1] + "'";
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                throw new SQLServerException(form3.format(new Object[] { value2, JDBCType.of(cm.columnType) }), null, 0, e2);
            }
            catch (final ArrayIndexOutOfBoundsException e3) {
                throw new SQLServerException(SQLServerException.getErrString("R_DataSchemaMismatch"), e3);
            }
        }
        return dataRow;
    }
    
    @Override
    void addColumnMetadataInternal(final int positionInSource, final String name, final int jdbcType, final int precision, final int scale, final DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        SQLServerBulkCSVFileRecord.loggerExternal.entering(this.loggerPackageName, "addColumnMetadata", new Object[] { positionInSource, name, jdbcType, precision, scale });
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
                this.columnMetadata.put(positionInSource, new ColumnMetadata(colName, jdbcType, 50, scale, dateTimeFormatter));
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
        SQLServerBulkCSVFileRecord.loggerExternal.exiting(this.loggerPackageName, "addColumnMetadata");
    }
    
    @Override
    public boolean next() throws SQLServerException {
        try {
            this.currentLine = this.fileReader.readLine();
        }
        catch (final IOException e) {
            throw new SQLServerException(e.getMessage(), null, 0, e);
        }
        return null != this.currentLine;
    }
}
