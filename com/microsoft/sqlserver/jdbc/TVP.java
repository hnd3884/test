package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.sql.ResultSet;

class TVP
{
    String TVPName;
    String TVP_owningSchema;
    String TVP_dbName;
    ResultSet sourceResultSet;
    SQLServerDataTable sourceDataTable;
    Map<Integer, SQLServerMetaData> columnMetadata;
    Iterator<Map.Entry<Integer, Object[]>> sourceDataTableRowIterator;
    ISQLServerDataRecord sourceRecord;
    TVPType tvpType;
    Set<String> columnNames;
    
    void initTVP(final TVPType type, final String tvpPartName) throws SQLServerException {
        this.tvpType = type;
        this.columnMetadata = new LinkedHashMap<Integer, SQLServerMetaData>();
        this.parseTypeName(tvpPartName);
    }
    
    TVP(final String tvpPartName) throws SQLServerException {
        this.sourceResultSet = null;
        this.sourceDataTable = null;
        this.columnMetadata = null;
        this.sourceDataTableRowIterator = null;
        this.sourceRecord = null;
        this.tvpType = null;
        this.columnNames = null;
        this.initTVP(TVPType.Null, tvpPartName);
    }
    
    TVP(String tvpPartName, final SQLServerDataTable tvpDataTable) throws SQLServerException {
        this.sourceResultSet = null;
        this.sourceDataTable = null;
        this.columnMetadata = null;
        this.sourceDataTableRowIterator = null;
        this.sourceRecord = null;
        this.tvpType = null;
        this.columnNames = null;
        if (tvpPartName == null) {
            tvpPartName = tvpDataTable.getTvpName();
        }
        this.initTVP(TVPType.SQLServerDataTable, tvpPartName);
        this.sourceDataTable = tvpDataTable;
        this.sourceDataTableRowIterator = this.sourceDataTable.getIterator();
        this.populateMetadataFromDataTable();
    }
    
    TVP(final String tvpPartName, final ResultSet tvpResultSet) throws SQLServerException {
        this.sourceResultSet = null;
        this.sourceDataTable = null;
        this.columnMetadata = null;
        this.sourceDataTableRowIterator = null;
        this.sourceRecord = null;
        this.tvpType = null;
        this.columnNames = null;
        this.initTVP(TVPType.ResultSet, tvpPartName);
        this.sourceResultSet = tvpResultSet;
        this.populateMetadataFromResultSet();
    }
    
    TVP(final String tvpPartName, final ISQLServerDataRecord tvpRecord) throws SQLServerException {
        this.sourceResultSet = null;
        this.sourceDataTable = null;
        this.columnMetadata = null;
        this.sourceDataTableRowIterator = null;
        this.sourceRecord = null;
        this.tvpType = null;
        this.columnNames = null;
        this.initTVP(TVPType.ISQLServerDataRecord, tvpPartName);
        this.sourceRecord = tvpRecord;
        this.columnNames = new HashSet<String>();
        this.populateMetadataFromDataRecord();
        this.validateOrderProperty();
    }
    
    boolean isNull() {
        return TVPType.Null == this.tvpType;
    }
    
    Object[] getRowData() throws SQLServerException {
        if (TVPType.ResultSet == this.tvpType) {
            final int colCount = this.columnMetadata.size();
            final Object[] rowData = new Object[colCount];
            for (int i = 0; i < colCount; ++i) {
                try {
                    if (92 == this.sourceResultSet.getMetaData().getColumnType(i + 1)) {
                        rowData[i] = this.sourceResultSet.getTimestamp(i + 1);
                    }
                    else {
                        rowData[i] = this.sourceResultSet.getObject(i + 1);
                    }
                }
                catch (final SQLException e) {
                    throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                }
            }
            return rowData;
        }
        if (TVPType.SQLServerDataTable == this.tvpType) {
            final Map.Entry<Integer, Object[]> rowPair = this.sourceDataTableRowIterator.next();
            return rowPair.getValue();
        }
        return this.sourceRecord.getRowData();
    }
    
    boolean next() throws SQLServerException {
        if (TVPType.ResultSet == this.tvpType) {
            try {
                return this.sourceResultSet.next();
            }
            catch (final SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
            }
        }
        if (TVPType.SQLServerDataTable == this.tvpType) {
            return this.sourceDataTableRowIterator.hasNext();
        }
        return null != this.sourceRecord && this.sourceRecord.next();
    }
    
    void populateMetadataFromDataTable() throws SQLServerException {
        if (null != this.sourceDataTable) {
            final Map<Integer, SQLServerDataColumn> dataTableMetaData = this.sourceDataTable.getColumnMetadata();
            if (null == dataTableMetaData || dataTableMetaData.isEmpty()) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPEmptyMetadata"), (Throwable)null);
            }
            dataTableMetaData.entrySet().forEach(E -> this.columnMetadata.put(E.getKey(), new SQLServerMetaData(E.getValue().columnName, E.getValue().javaSqlType, E.getValue().precision, E.getValue().scale)));
        }
    }
    
    void populateMetadataFromResultSet() throws SQLServerException {
        if (null != this.sourceResultSet) {
            try {
                final ResultSetMetaData rsmd = this.sourceResultSet.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); ++i) {
                    final SQLServerMetaData columnMetaData = new SQLServerMetaData(rsmd.getColumnName(i + 1), rsmd.getColumnType(i + 1), rsmd.getPrecision(i + 1), rsmd.getScale(i + 1));
                    this.columnMetadata.put(i, columnMetaData);
                }
            }
            catch (final SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
            }
        }
    }
    
    void populateMetadataFromDataRecord() throws SQLServerException {
        if (null != this.sourceRecord) {
            if (0 >= this.sourceRecord.getColumnCount()) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPEmptyMetadata"), (Throwable)null);
            }
            for (int i = 0; i < this.sourceRecord.getColumnCount(); ++i) {
                Util.checkDuplicateColumnName(this.sourceRecord.getColumnMetaData(i + 1).columnName, this.columnNames);
                final SQLServerMetaData metaData = new SQLServerMetaData(this.sourceRecord.getColumnMetaData(i + 1));
                this.columnMetadata.put(i, metaData);
            }
        }
    }
    
    void validateOrderProperty() throws SQLServerException {
        final int columnCount = this.columnMetadata.size();
        final boolean[] sortOrdinalSpecified = new boolean[columnCount];
        int maxSortOrdinal = -1;
        int sortCount = 0;
        for (final Map.Entry<Integer, SQLServerMetaData> columnPair : this.columnMetadata.entrySet()) {
            final SQLServerSortOrder columnSortOrder = columnPair.getValue().sortOrder;
            final int columnSortOrdinal = columnPair.getValue().sortOrdinal;
            if (SQLServerSortOrder.Unspecified != columnSortOrder) {
                if (columnCount <= columnSortOrdinal) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPSortOrdinalGreaterThanFieldCount"));
                    throw new SQLServerException(form.format(new Object[] { columnSortOrdinal, columnPair.getKey() }), null, 0, null);
                }
                if (sortOrdinalSpecified[columnSortOrdinal]) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPDuplicateSortOrdinal"));
                    throw new SQLServerException(form.format(new Object[] { columnSortOrdinal }), null, 0, null);
                }
                sortOrdinalSpecified[columnSortOrdinal] = true;
                if (columnSortOrdinal > maxSortOrdinal) {
                    maxSortOrdinal = columnSortOrdinal;
                }
                ++sortCount;
            }
        }
        if (0 < sortCount && maxSortOrdinal >= sortCount) {
            int i;
            for (i = 0; i < sortCount && sortOrdinalSpecified[i]; ++i) {}
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_TVPMissingSortOrdinal"));
            throw new SQLServerException(form2.format(new Object[] { i }), null, 0, null);
        }
    }
    
    void parseTypeName(final String name) throws SQLServerException {
        final String leftQuote = "[\"";
        final String rightQuote = "]\"";
        final char separator = '.';
        final int limit = 3;
        final String[] parsedNames = new String[limit];
        int stringCount = 0;
        if (null == name || 0 == name.length()) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTVPName"));
            final Object[] msgArgs = new Object[0];
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        final StringBuilder sb = new StringBuilder(name.length());
        StringBuilder whitespaceSB = null;
        char rightQuoteChar = ' ';
        MPIState state = MPIState.MPI_Value;
        for (int index = 0; index < name.length(); ++index) {
            final char testchar = name.charAt(index);
            switch (state) {
                case MPI_Value: {
                    if (Character.isWhitespace(testchar)) {
                        break;
                    }
                    if (testchar == separator) {
                        parsedNames[stringCount] = "";
                        ++stringCount;
                        break;
                    }
                    final int quoteIndex;
                    if (-1 != (quoteIndex = leftQuote.indexOf(testchar))) {
                        rightQuoteChar = rightQuote.charAt(quoteIndex);
                        sb.setLength(0);
                        state = MPIState.MPI_ParseQuote;
                        break;
                    }
                    if (-1 != rightQuote.indexOf(testchar)) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                        throw new SQLServerException(null, form2.format(new Object[0]), null, 0, false);
                    }
                    sb.setLength(0);
                    sb.append(testchar);
                    state = MPIState.MPI_ParseNonQuote;
                    break;
                }
                case MPI_ParseNonQuote: {
                    if (testchar == separator) {
                        parsedNames[stringCount] = sb.toString();
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_Value;
                        break;
                    }
                    if (-1 != rightQuote.indexOf(testchar) || -1 != leftQuote.indexOf(testchar)) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                        throw new SQLServerException(null, form2.format(new Object[0]), null, 0, false);
                    }
                    if (Character.isWhitespace(testchar)) {
                        parsedNames[stringCount] = sb.toString();
                        if (null == whitespaceSB) {
                            whitespaceSB = new StringBuilder();
                        }
                        whitespaceSB.setLength(0);
                        whitespaceSB.append(testchar);
                        state = MPIState.MPI_LookForNextCharOrSeparator;
                        break;
                    }
                    sb.append(testchar);
                    break;
                }
                case MPI_LookForNextCharOrSeparator: {
                    if (Character.isWhitespace(testchar)) {
                        if (null == whitespaceSB) {
                            whitespaceSB = new StringBuilder();
                        }
                        whitespaceSB.append(testchar);
                        break;
                    }
                    if (testchar == separator) {
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_Value;
                        break;
                    }
                    sb.append((CharSequence)whitespaceSB);
                    sb.append(testchar);
                    parsedNames[stringCount] = sb.toString();
                    state = MPIState.MPI_ParseNonQuote;
                    break;
                }
                case MPI_ParseQuote: {
                    if (testchar == rightQuoteChar) {
                        state = MPIState.MPI_RightQuote;
                        break;
                    }
                    sb.append(testchar);
                    break;
                }
                case MPI_RightQuote: {
                    if (testchar == rightQuoteChar) {
                        sb.append(testchar);
                        state = MPIState.MPI_ParseQuote;
                        break;
                    }
                    if (testchar == separator) {
                        parsedNames[stringCount] = sb.toString();
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_Value;
                        break;
                    }
                    if (!Character.isWhitespace(testchar)) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                        throw new SQLServerException(null, form2.format(new Object[0]), null, 0, false);
                    }
                    parsedNames[stringCount] = sb.toString();
                    state = MPIState.MPI_LookForSeparator;
                    break;
                }
                case MPI_LookForSeparator: {
                    if (Character.isWhitespace(testchar)) {
                        break;
                    }
                    if (testchar == separator) {
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_Value;
                        break;
                    }
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                    throw new SQLServerException(null, form2.format(new Object[0]), null, 0, false);
                }
            }
        }
        if (stringCount > limit - 1) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
            throw new SQLServerException(null, form3.format(new Object[0]), null, 0, false);
        }
        switch (state) {
            case MPI_Value:
            case MPI_LookForNextCharOrSeparator:
            case MPI_LookForSeparator: {
                break;
            }
            case MPI_ParseNonQuote:
            case MPI_RightQuote: {
                parsedNames[stringCount] = sb.toString();
                break;
            }
            default: {
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                throw new SQLServerException(null, form3.format(new Object[0]), null, 0, false);
            }
        }
        if (parsedNames[0] == null) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
            throw new SQLServerException(null, form3.format(new Object[0]), null, 0, false);
        }
        final int offset = limit - stringCount - 1;
        if (offset > 0) {
            for (int x = limit - 1; x >= offset; --x) {
                parsedNames[x] = parsedNames[x - offset];
                parsedNames[x - offset] = null;
            }
        }
        this.TVPName = parsedNames[2];
        this.TVP_owningSchema = parsedNames[1];
        this.TVP_dbName = parsedNames[0];
    }
    
    private int incrementStringCount(final String[] ary, int position) throws SQLServerException {
        ++position;
        final int limit = ary.length;
        if (position >= limit) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
            throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
        }
        ary[position] = new String();
        return position;
    }
    
    String getTVPName() {
        return this.TVPName;
    }
    
    String getDbNameTVP() {
        return this.TVP_dbName;
    }
    
    String getOwningSchemaNameTVP() {
        return this.TVP_owningSchema;
    }
    
    int getTVPColumnCount() {
        return this.columnMetadata.size();
    }
    
    Map<Integer, SQLServerMetaData> getColumnMetadata() {
        return this.columnMetadata;
    }
    
    enum MPIState
    {
        MPI_Value, 
        MPI_ParseNonQuote, 
        MPI_LookForSeparator, 
        MPI_LookForNextCharOrSeparator, 
        MPI_ParseQuote, 
        MPI_RightQuote;
    }
}
