package com.microsoft.sqlserver.jdbc;

import java.sql.SQLType;
import java.net.URL;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.RowId;
import java.io.Reader;
import java.sql.Array;
import java.sql.Ref;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.util.UUID;
import microsoft.sql.DateTimeOffset;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.ResultSetMetaData;
import java.sql.Date;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.SQLXML;
import java.util.Calendar;
import java.sql.SQLWarning;
import java.io.IOException;
import java.text.MessageFormat;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.HashMap;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import java.util.Map;
import java.io.Closeable;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;

public class SQLServerResultSet implements ISQLServerResultSet, Serializable
{
    private static final long serialVersionUID = -1624082547992040463L;
    private static final AtomicInteger lastResultSetID;
    private final String traceID;
    static final Logger logger;
    protected static final Logger loggerExternal;
    private final String loggingClassName;
    private final SQLServerStatement stmt;
    private final int maxRows;
    private SQLServerResultSetMetaData metaData;
    private boolean isClosed;
    private final int serverCursorId;
    private int fetchDirection;
    private int fetchSize;
    private boolean isOnInsertRow;
    private boolean lastValueWasNull;
    private int lastColumnIndex;
    private boolean areNullCompressedColumnsInitialized;
    private RowType resultSetCurrentRowType;
    private transient Closeable activeStream;
    private SQLServerLob activeLOB;
    private final ScrollWindow scrollWindow;
    private static final int BEFORE_FIRST_ROW = 0;
    private static final int AFTER_LAST_ROW = -1;
    private static final int UNKNOWN_ROW = -2;
    private int currentRow;
    private boolean updatedCurrentRow;
    private final Map<String, Integer> columnNames;
    private boolean deletedCurrentRow;
    static final int UNKNOWN_ROW_COUNT = -3;
    private int rowCount;
    private final Column[] columns;
    private CekTable cekTable;
    private TDSReader tdsReader;
    private final FetchBuffer fetchBuffer;
    private SQLServerException rowErrorException;
    private int numFetchedRows;
    
    private static int nextResultSetID() {
        return SQLServerResultSet.lastResultSetID.incrementAndGet();
    }
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    String logCursorState() {
        return " currentRow:" + this.currentRow + " numFetchedRows:" + this.numFetchedRows + " rowCount:" + this.rowCount;
    }
    
    String getClassNameLogging() {
        return this.loggingClassName;
    }
    
    protected int getServerCursorId() {
        return this.serverCursorId;
    }
    
    final RowType getCurrentRowType() {
        return this.resultSetCurrentRowType;
    }
    
    final void setCurrentRowType(final RowType rowType) {
        this.resultSetCurrentRowType = rowType;
    }
    
    final boolean getUpdatedCurrentRow() {
        return this.updatedCurrentRow;
    }
    
    final void setUpdatedCurrentRow(final boolean rowUpdated) {
        this.updatedCurrentRow = rowUpdated;
    }
    
    final boolean getDeletedCurrentRow() {
        return this.deletedCurrentRow;
    }
    
    final void setDeletedCurrentRow(final boolean rowDeleted) {
        this.deletedCurrentRow = rowDeleted;
    }
    
    CekTable getCekTable() {
        return this.cekTable;
    }
    
    final void setColumnName(final int index, final String name) {
        this.columns[index - 1].setColumnName(name);
    }
    
    private void skipColumns(final int columnsToSkip, final boolean discardValues) throws SQLServerException {
        assert this.lastColumnIndex >= 1;
        assert 0 <= columnsToSkip && columnsToSkip <= this.columns.length;
        for (int columnsSkipped = 0; columnsSkipped < columnsToSkip; ++columnsSkipped) {
            final Column column = this.getColumn(this.lastColumnIndex++);
            column.skipValue(this.tdsReader, discardValues && this.isForwardOnly());
            if (discardValues) {
                column.clear();
            }
        }
    }
    
    protected TDSReader getTDSReader() {
        return this.tdsReader;
    }
    
    @Override
    public SensitivityClassification getSensitivityClassification() {
        return this.tdsReader.sensitivityClassification;
    }
    
    SQLServerResultSet(final SQLServerStatement stmtIn) throws SQLServerException {
        this.isClosed = false;
        this.isOnInsertRow = false;
        this.lastValueWasNull = false;
        this.areNullCompressedColumnsInitialized = false;
        this.resultSetCurrentRowType = RowType.UNKNOWN;
        this.currentRow = 0;
        this.updatedCurrentRow = false;
        this.columnNames = new HashMap<String, Integer>();
        this.deletedCurrentRow = false;
        this.cekTable = null;
        this.rowErrorException = null;
        final int resultSetID = nextResultSetID();
        this.loggingClassName = "com.microsoft.sqlserver.jdbc.SQLServerResultSet:" + resultSetID;
        this.traceID = "SQLServerResultSet:" + resultSetID;
        this.stmt = stmtIn;
        this.maxRows = stmtIn.maxRows;
        this.fetchSize = stmtIn.nFetchSize;
        this.fetchDirection = stmtIn.nFetchDirection;
        final CursorInitializer initializer = stmtIn.executedSqlDirectly ? new ClientCursorInitializer() : new ServerCursorInitializer(stmtIn);
        TDSParser.parse(stmtIn.resultsReader(), initializer);
        this.columns = initializer.buildColumns();
        this.rowCount = initializer.getRowCount();
        this.serverCursorId = initializer.getServerCursorId();
        this.tdsReader = ((0 == this.serverCursorId) ? stmtIn.resultsReader() : null);
        this.fetchBuffer = new FetchBuffer();
        this.scrollWindow = (this.isForwardOnly() ? null : new ScrollWindow(this.fetchSize));
        this.numFetchedRows = 0;
        stmtIn.incrResultSetCount();
        if (SQLServerResultSet.logger.isLoggable(Level.FINE)) {
            SQLServerResultSet.logger.fine(this.toString() + " created by (" + this.stmt.toString() + ")");
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "isWrapperFor");
        final boolean f = iface.isInstance(this);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "isWrapperFor", f);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "unwrap");
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "unwrap", t);
        return t;
    }
    
    void checkClosed() throws SQLServerException {
        if (this.isClosed) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_resultsetClosed"), null, false);
        }
        this.stmt.checkClosed();
        if (null != this.rowErrorException) {
            throw this.rowErrorException;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "isClosed");
        final boolean result = this.isClosed || this.stmt.isClosed();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "isClosed", result);
        return result;
    }
    
    private void throwNotScrollable() throws SQLException {
        SQLServerException.makeFromDriverError(this.stmt.connection, this, SQLServerException.getErrString("R_requestedOpNotSupportedOnForward"), null, true);
    }
    
    protected boolean isForwardOnly() {
        return 2003 == this.stmt.getSQLResultSetType() || 2004 == this.stmt.getSQLResultSetType();
    }
    
    private boolean isDynamic() {
        return 0 != this.serverCursorId && 2 == this.stmt.getCursorType();
    }
    
    private void verifyResultSetIsScrollable() throws SQLException {
        if (this.isForwardOnly()) {
            this.throwNotScrollable();
        }
    }
    
    private void throwNotUpdatable() throws SQLServerException {
        SQLServerException.makeFromDriverError(this.stmt.connection, this, SQLServerException.getErrString("R_resultsetNotUpdatable"), null, true);
    }
    
    private void verifyResultSetIsUpdatable() throws SQLServerException {
        if (1007 == this.stmt.resultSetConcurrency || 0 == this.serverCursorId) {
            this.throwNotUpdatable();
        }
    }
    
    private boolean hasCurrentRow() {
        return 0 != this.currentRow && -1 != this.currentRow;
    }
    
    private void verifyResultSetHasCurrentRow() throws SQLServerException {
        if (!this.hasCurrentRow()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_resultsetNoCurrentRow"), null, true);
        }
    }
    
    private void verifyCurrentRowIsNotDeleted(final String errResource) throws SQLServerException {
        if (this.currentRowDeleted()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString(errResource), null, true);
        }
    }
    
    private void verifyValidColumnIndex(final int index) throws SQLServerException {
        int nCols = this.columns.length;
        if (0 != this.serverCursorId) {
            --nCols;
        }
        if (index < 1 || index > nCols) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            final Object[] msgArgs = { index };
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), "07009", false);
        }
    }
    
    private void verifyResultSetIsNotOnInsertRow() throws SQLServerException {
        if (this.isOnInsertRow) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_mustNotBeOnInsertRow"), null, true);
        }
    }
    
    private void throwUnsupportedCursorOp() throws SQLServerException {
        SQLServerException.makeFromDriverError(this.stmt.connection, this, SQLServerException.getErrString("R_unsupportedCursorOperation"), null, true);
    }
    
    private void closeInternal() {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        this.discardFetchBuffer();
        this.closeServerCursor();
        this.metaData = null;
        this.stmt.decrResultSetCount();
    }
    
    @Override
    public void close() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "close");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.closeInternal();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "close");
    }
    
    @Override
    public int findColumn(final String userProvidedColumnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "findColumn", userProvidedColumnName);
        this.checkClosed();
        final Integer value = this.columnNames.get(userProvidedColumnName);
        if (null != value) {
            return value;
        }
        for (int i = 0; i < this.columns.length; ++i) {
            if (this.columns[i].getColumnName().equals(userProvidedColumnName)) {
                this.columnNames.put(userProvidedColumnName, i + 1);
                SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "findColumn", i + 1);
                return i + 1;
            }
        }
        for (int i = 0; i < this.columns.length; ++i) {
            if (this.columns[i].getColumnName().equalsIgnoreCase(userProvidedColumnName)) {
                this.columnNames.put(userProvidedColumnName, i + 1);
                SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "findColumn", i + 1);
                return i + 1;
            }
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumnName"));
        final Object[] msgArgs = { userProvidedColumnName };
        SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), "07009", false);
        return 0;
    }
    
    final int getColumnCount() {
        int nCols = this.columns.length;
        if (0 != this.serverCursorId) {
            --nCols;
        }
        return nCols;
    }
    
    final Column getColumn(final int columnIndex) throws SQLServerException {
        if (null != this.activeStream) {
            try {
                this.fillLOBs();
                this.activeStream.close();
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
            }
            finally {
                this.activeStream = null;
            }
        }
        return this.columns[columnIndex - 1];
    }
    
    private void initializeNullCompressedColumns() throws SQLServerException {
        if (this.resultSetCurrentRowType.equals(RowType.NBCROW) && !this.areNullCompressedColumnsInitialized) {
            int columnNo = 0;
            for (int noOfBytes = (this.columns.length - 1 >> 3) + 1, byteNo = 0; byteNo < noOfBytes; ++byteNo) {
                final int byteValue = this.tdsReader.readUnsignedByte();
                if (byteValue == 0) {
                    columnNo += 8;
                }
                else {
                    for (int bitNo = 0; bitNo < 8 && columnNo < this.columns.length; ++bitNo, ++columnNo) {
                        if ((byteValue & 1 << bitNo) != 0x0) {
                            this.columns[columnNo].initFromCompressedNull();
                        }
                    }
                }
            }
            this.areNullCompressedColumnsInitialized = true;
        }
    }
    
    private Column loadColumn(final int index) throws SQLServerException {
        assert 1 <= index && index <= this.columns.length;
        this.initializeNullCompressedColumns();
        if (index > this.lastColumnIndex && !this.columns[index - 1].isInitialized()) {
            this.skipColumns(index - this.lastColumnIndex, false);
        }
        return this.getColumn(index);
    }
    
    @Override
    public void clearWarnings() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "clearWarnings");
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "clearWarnings");
    }
    
    private void moverInit() throws SQLServerException {
        this.fillLOBs();
        this.cancelInsert();
        this.cancelUpdates();
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "relative", rows);
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + " rows:" + rows + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.verifyResultSetHasCurrentRow();
        this.moverInit();
        this.moveRelative(rows);
        final boolean value = this.hasCurrentRow();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "relative", value);
        return value;
    }
    
    private void moveRelative(final int rowsToMove) throws SQLServerException {
        assert this.hasCurrentRow();
        if (0 == rowsToMove) {
            return;
        }
        if (rowsToMove > 0) {
            this.moveForward(rowsToMove);
        }
        else {
            this.moveBackward(rowsToMove);
        }
    }
    
    private void moveForward(int rowsToMove) throws SQLServerException {
        assert this.hasCurrentRow();
        assert rowsToMove > 0;
        if (this.scrollWindow.getRow() + rowsToMove <= this.scrollWindow.getMaxRows()) {
            int rowsMoved = 0;
            while (rowsToMove > 0 && this.scrollWindow.next(this)) {
                ++rowsMoved;
                --rowsToMove;
            }
            this.updateCurrentRow(rowsMoved);
            if (0 == rowsToMove) {
                return;
            }
        }
        assert rowsToMove > 0;
        if (0 == this.serverCursorId) {
            assert -2 != this.currentRow;
            this.currentRow = this.clientMoveAbsolute(this.currentRow + rowsToMove);
        }
        else {
            if (1 == rowsToMove) {
                this.doServerFetch(2, 0, this.fetchSize);
            }
            else {
                this.doServerFetch(32, rowsToMove + this.scrollWindow.getRow() - 1, this.fetchSize);
            }
            if (!this.scrollWindow.next(this)) {
                this.currentRow = -1;
                return;
            }
            this.updateCurrentRow(rowsToMove);
        }
    }
    
    private void moveBackward(final int rowsToMove) throws SQLServerException {
        assert this.hasCurrentRow();
        assert rowsToMove < 0;
        if (this.scrollWindow.getRow() + rowsToMove >= 1) {
            for (int rowsMoved = 0; rowsMoved > rowsToMove; --rowsMoved) {
                this.scrollWindow.previous(this);
            }
            this.updateCurrentRow(rowsToMove);
            return;
        }
        if (0 != this.serverCursorId) {
            if (-1 == rowsToMove) {
                this.doServerFetch(512, 0, this.fetchSize);
                if (!this.scrollWindow.next(this)) {
                    this.currentRow = 0;
                    return;
                }
                while (this.scrollWindow.next(this)) {}
                this.scrollWindow.previous(this);
            }
            else {
                this.doServerFetch(32, rowsToMove + this.scrollWindow.getRow() - 1, this.fetchSize);
                if (!this.scrollWindow.next(this)) {
                    this.currentRow = 0;
                    return;
                }
            }
            this.updateCurrentRow(rowsToMove);
            return;
        }
        assert -2 != this.currentRow;
        if (this.currentRow + rowsToMove < 1) {
            this.moveBeforeFirst();
        }
        else {
            this.currentRow = this.clientMoveAbsolute(this.currentRow + rowsToMove);
        }
    }
    
    private void updateCurrentRow(final int rowsToMove) {
        if (-2 != this.currentRow) {
            assert this.currentRow >= 1;
            this.currentRow += rowsToMove;
            assert this.currentRow >= 1;
        }
    }
    
    @Override
    public boolean next() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "next");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.moverInit();
        if (-1 == this.currentRow) {
            SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "next", false);
            return false;
        }
        if (!this.isForwardOnly()) {
            if (0 == this.currentRow) {
                this.moveFirst();
            }
            else {
                this.moveForward(1);
            }
            final boolean value = this.hasCurrentRow();
            SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "next", value);
            return value;
        }
        if (0 != this.serverCursorId && this.maxRows > 0 && this.currentRow == this.maxRows) {
            this.currentRow = -1;
            SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "next", false);
            return false;
        }
        if (!this.fetchBufferNext()) {
            if (0 != this.serverCursorId) {
                this.doServerFetch(2, 0, this.fetchSize);
                if (this.fetchBufferNext()) {
                    if (0 == this.currentRow) {
                        this.currentRow = 1;
                    }
                    else {
                        this.updateCurrentRow(1);
                    }
                    assert this.currentRow <= this.maxRows;
                    SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "next", true);
                    return true;
                }
            }
            if (-3 == this.rowCount) {
                this.rowCount = this.currentRow;
            }
            if (this.stmt.resultsReader().peekTokenType() == 171) {
                this.stmt.startResults();
                this.stmt.getNextResult(false);
            }
            this.currentRow = -1;
            SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "next", false);
            return false;
        }
        if (0 == this.currentRow) {
            this.currentRow = 1;
        }
        else {
            this.updateCurrentRow(1);
        }
        assert this.currentRow <= this.maxRows;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "next", true);
        return true;
    }
    
    @Override
    public boolean wasNull() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "wasNull");
        this.checkClosed();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "wasNull", this.lastValueWasNull);
        return this.lastValueWasNull;
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "isBeforeFirst");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        if (0 != this.serverCursorId) {
            switch (this.stmt.getCursorType()) {
                case 4: {
                    this.throwNotScrollable();
                    break;
                }
                case 2: {
                    this.throwUnsupportedCursorOp();
                    break;
                }
                case 16: {
                    this.throwNotScrollable();
                    break;
                }
            }
        }
        if (this.isOnInsertRow) {
            return false;
        }
        if (0 != this.currentRow) {
            return false;
        }
        if (0 == this.serverCursorId) {
            return this.fetchBufferHasRows();
        }
        assert this.rowCount >= 0;
        final boolean value = this.rowCount > 0;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "isBeforeFirst", value);
        return value;
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "isAfterLast");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        if (0 != this.serverCursorId) {
            this.verifyResultSetIsScrollable();
            if (2 == this.stmt.getCursorType() && !this.isForwardOnly()) {
                this.throwUnsupportedCursorOp();
            }
        }
        if (this.isOnInsertRow) {
            return false;
        }
        assert -3 != this.rowCount;
        final boolean value = -1 == this.currentRow && this.rowCount > 0;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "isAfterLast", value);
        return value;
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "isFirst");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (this.isDynamic()) {
            this.throwUnsupportedCursorOp();
        }
        if (this.isOnInsertRow) {
            return false;
        }
        assert -2 != this.currentRow;
        final boolean value = 1 == this.currentRow;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "isFirst", value);
        return value;
    }
    
    @Override
    public boolean isLast() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "isLast");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (this.isDynamic()) {
            this.throwUnsupportedCursorOp();
        }
        if (this.isOnInsertRow) {
            return false;
        }
        if (!this.hasCurrentRow()) {
            return false;
        }
        assert this.currentRow >= 1;
        if (-3 != this.rowCount) {
            assert this.currentRow <= this.rowCount;
            return this.currentRow == this.rowCount;
        }
        else {
            assert 0 == this.serverCursorId;
            final boolean isLast = !this.next();
            this.previous();
            SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "isLast", isLast);
            return isLast;
        }
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "beforeFirst");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveBeforeFirst();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "beforeFirst");
    }
    
    private void moveBeforeFirst() throws SQLServerException {
        if (0 == this.serverCursorId) {
            this.fetchBufferBeforeFirst();
            this.scrollWindow.clear();
        }
        else {
            this.doServerFetch(1, 0, 0);
        }
        this.currentRow = 0;
    }
    
    @Override
    public void afterLast() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "afterLast");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveAfterLast();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "afterLast");
    }
    
    private void moveAfterLast() throws SQLServerException {
        assert !this.isForwardOnly();
        if (0 == this.serverCursorId) {
            this.clientMoveAfterLast();
        }
        else {
            this.doServerFetch(8, 0, 0);
        }
        this.currentRow = -1;
    }
    
    @Override
    public boolean first() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "first");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveFirst();
        final boolean value = this.hasCurrentRow();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "first", value);
        return value;
    }
    
    private void moveFirst() throws SQLServerException {
        if (0 == this.serverCursorId) {
            this.moveBeforeFirst();
        }
        else {
            this.doServerFetch(1, 0, this.fetchSize);
        }
        if (!this.scrollWindow.next(this)) {
            this.currentRow = -1;
            return;
        }
        this.currentRow = (this.isDynamic() ? -2 : 1);
    }
    
    @Override
    public boolean last() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "last");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveLast();
        final boolean value = this.hasCurrentRow();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "last", value);
        return value;
    }
    
    private void moveLast() throws SQLServerException {
        if (0 == this.serverCursorId) {
            this.currentRow = this.clientMoveAbsolute(-1);
            return;
        }
        this.doServerFetch(8, 0, this.fetchSize);
        if (!this.scrollWindow.next(this)) {
            this.currentRow = -1;
            return;
        }
        while (this.scrollWindow.next(this)) {}
        this.scrollWindow.previous(this);
        this.currentRow = (this.isDynamic() ? -2 : this.rowCount);
    }
    
    @Override
    public int getRow() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getRow");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        if (this.isDynamic() && !this.isForwardOnly()) {
            this.throwUnsupportedCursorOp();
        }
        if (!this.hasCurrentRow() || this.isOnInsertRow) {
            return 0;
        }
        assert this.currentRow >= 1;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getRow", this.currentRow);
        return this.currentRow;
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "absolute");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + " row:" + row + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (this.isDynamic()) {
            this.throwUnsupportedCursorOp();
        }
        this.moverInit();
        this.moveAbsolute(row);
        final boolean value = this.hasCurrentRow();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "absolute", value);
        return value;
    }
    
    private void moveAbsolute(final int row) throws SQLServerException {
        assert -2 != this.currentRow;
        assert !this.isDynamic();
        switch (row) {
            case 0: {
                this.moveBeforeFirst();
                return;
            }
            case 1: {
                this.moveFirst();
                return;
            }
            case -1: {
                this.moveLast();
                return;
            }
            default: {
                if (this.hasCurrentRow()) {
                    assert this.currentRow >= 1;
                    if (row > 0) {
                        this.moveRelative(row - this.currentRow);
                        return;
                    }
                    if (-3 != this.rowCount) {
                        assert row < 0;
                        this.moveRelative(this.rowCount + row + 1 - this.currentRow);
                        return;
                    }
                }
                if (0 == this.serverCursorId) {
                    this.currentRow = this.clientMoveAbsolute(row);
                    return;
                }
                this.doServerFetch(16, row, this.fetchSize);
                if (!this.scrollWindow.next(this)) {
                    this.currentRow = ((row < 0) ? 0 : -1);
                    return;
                }
                if (row > 0) {
                    this.currentRow = row;
                }
                else {
                    assert row < 0;
                    assert this.rowCount + row + 1 >= 1;
                    this.currentRow = this.rowCount + row + 1;
                }
            }
        }
    }
    
    private boolean fetchBufferHasRows() throws SQLServerException {
        assert 0 == this.serverCursorId;
        assert null != this.tdsReader;
        assert this.lastColumnIndex >= 0;
        if (this.lastColumnIndex >= 1) {
            return true;
        }
        final int tdsTokenType = this.tdsReader.peekTokenType();
        return 209 == tdsTokenType || 210 == tdsTokenType || 171 == tdsTokenType || 170 == tdsTokenType;
    }
    
    final void discardCurrentRow() throws SQLServerException {
        assert this.lastColumnIndex >= 0;
        this.updatedCurrentRow = false;
        this.deletedCurrentRow = false;
        if (this.lastColumnIndex >= 1) {
            this.initializeNullCompressedColumns();
            for (int columnIndex = 1; columnIndex < this.lastColumnIndex; ++columnIndex) {
                this.getColumn(columnIndex).clear();
            }
            this.skipColumns(this.columns.length + 1 - this.lastColumnIndex, true);
        }
        this.resultSetCurrentRowType = RowType.UNKNOWN;
        this.areNullCompressedColumnsInitialized = false;
    }
    
    final int fetchBufferGetRow() {
        if (this.isForwardOnly()) {
            return this.numFetchedRows;
        }
        return this.scrollWindow.getRow();
    }
    
    final void fetchBufferBeforeFirst() throws SQLServerException {
        assert 0 == this.serverCursorId;
        assert null != this.tdsReader;
        this.discardCurrentRow();
        this.fetchBuffer.reset();
        this.lastColumnIndex = 0;
    }
    
    final TDSReaderMark fetchBufferMark() {
        assert null != this.tdsReader;
        return this.tdsReader.mark();
    }
    
    final void fetchBufferReset(final TDSReaderMark mark) throws SQLServerException {
        assert null != this.tdsReader;
        assert null != mark;
        this.discardCurrentRow();
        this.tdsReader.reset(mark);
        this.lastColumnIndex = 1;
    }
    
    final boolean fetchBufferNext() throws SQLServerException {
        if (null == this.tdsReader) {
            return false;
        }
        this.discardCurrentRow();
        RowType fetchBufferCurrentRowType = RowType.UNKNOWN;
        try {
            fetchBufferCurrentRowType = this.fetchBuffer.nextRow();
            if (fetchBufferCurrentRowType.equals(RowType.UNKNOWN)) {
                return false;
            }
        }
        catch (final SQLServerException e) {
            this.currentRow = -1;
            throw this.rowErrorException = e;
        }
        finally {
            this.lastColumnIndex = 0;
            this.resultSetCurrentRowType = fetchBufferCurrentRowType;
        }
        ++this.numFetchedRows;
        this.lastColumnIndex = 1;
        return true;
    }
    
    private void clientMoveAfterLast() throws SQLServerException {
        assert -2 != this.currentRow;
        int rowsSkipped = 0;
        while (this.fetchBufferNext()) {
            ++rowsSkipped;
        }
        if (-3 == this.rowCount) {
            assert -1 != this.currentRow;
            this.rowCount = ((0 == this.currentRow) ? 0 : this.currentRow) + rowsSkipped;
        }
    }
    
    private int clientMoveAbsolute(int row) throws SQLServerException {
        assert 0 == this.serverCursorId;
        this.scrollWindow.clear();
        if (row < 0) {
            if (-3 == this.rowCount) {
                this.clientMoveAfterLast();
                this.currentRow = -1;
            }
            assert this.rowCount >= 0;
            if (this.rowCount + row < 0) {
                this.moveBeforeFirst();
                return 0;
            }
            row = this.rowCount + row + 1;
        }
        assert row > 0;
        if (-1 == this.currentRow || row <= this.currentRow) {
            this.moveBeforeFirst();
        }
        assert this.currentRow < row;
        while (this.currentRow != row) {
            if (!this.fetchBufferNext()) {
                if (-3 == this.rowCount) {
                    this.rowCount = this.currentRow;
                }
                return -1;
            }
            if (0 == this.currentRow) {
                this.currentRow = 1;
            }
            else {
                this.updateCurrentRow(1);
            }
        }
        return row;
    }
    
    @Override
    public boolean previous() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "previous");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        if (0 == this.currentRow) {
            return false;
        }
        if (-1 == this.currentRow) {
            this.moveLast();
        }
        else {
            this.moveBackward(-1);
        }
        final boolean value = this.hasCurrentRow();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "previous", value);
        return value;
    }
    
    private void cancelInsert() {
        if (this.isOnInsertRow) {
            this.isOnInsertRow = false;
            this.clearColumnsValues();
        }
    }
    
    final void clearColumnsValues() {
        for (final Column column : this.columns) {
            column.cancelUpdates();
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getWarnings");
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getWarnings", null);
        return null;
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "setFetchDirection", direction);
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if ((1000 != direction && 1001 != direction && 1002 != direction) || (1000 != direction && (2003 == this.stmt.resultSetType || 2004 == this.stmt.resultSetType))) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidFetchDirection"));
            final Object[] msgArgs = { direction };
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), null, false);
        }
        this.fetchDirection = direction;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "setFetchDirection");
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFetchDirection");
        this.checkClosed();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFetchDirection", this.fetchDirection);
        return this.fetchDirection;
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "setFetchSize", rows);
        this.checkClosed();
        if (rows < 0) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_invalidFetchSize"), null, false);
        }
        this.fetchSize = ((0 == rows) ? this.stmt.defaultFetchSize : rows);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "setFetchSize");
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFetchSize");
        this.checkClosed();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", this.fetchSize);
        return this.fetchSize;
    }
    
    @Override
    public int getType() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getType");
        this.checkClosed();
        final int value = this.stmt.getResultSetType();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getType", value);
        return value;
    }
    
    @Override
    public int getConcurrency() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getConcurrency");
        this.checkClosed();
        final int value = this.stmt.getResultSetConcurrency();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getConcurrency", value);
        return value;
    }
    
    Column getterGetColumn(final int index) throws SQLServerException {
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantGetColumnValueFromDeletedRow");
        this.verifyValidColumnIndex(index);
        if (this.updatedCurrentRow) {
            this.doRefreshRow();
            this.verifyResultSetHasCurrentRow();
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + " Getting Column:" + index);
        }
        this.fillLOBs();
        return this.loadColumn(index);
    }
    
    private Object getValue(final int columnIndex, final JDBCType jdbcType) throws SQLServerException {
        return this.getValue(columnIndex, jdbcType, null, null);
    }
    
    private Object getValue(final int columnIndex, final JDBCType jdbcType, final Calendar cal) throws SQLServerException {
        return this.getValue(columnIndex, jdbcType, null, cal);
    }
    
    private Object getValue(final int columnIndex, final JDBCType jdbcType, final InputStreamGetterArgs getterArgs) throws SQLServerException {
        return this.getValue(columnIndex, jdbcType, getterArgs, null);
    }
    
    private Object getValue(final int columnIndex, final JDBCType jdbcType, final InputStreamGetterArgs getterArgs, final Calendar cal) throws SQLServerException {
        final Object o = this.getterGetColumn(columnIndex).getValue(jdbcType, getterArgs, cal, this.tdsReader);
        this.lastValueWasNull = (null == o);
        return o;
    }
    
    void setInternalVariantType(final int columnIndex, final SqlVariant type) throws SQLServerException {
        this.getterGetColumn(columnIndex).setInternalVariant(type);
    }
    
    SqlVariant getVariantInternalType(final int columnIndex) throws SQLServerException {
        return this.getterGetColumn(columnIndex).getInternalVariant();
    }
    
    private Object getStream(final int columnIndex, final StreamType streamType) throws SQLServerException {
        final Object value = this.getValue(columnIndex, streamType.getJDBCType(), new InputStreamGetterArgs(streamType, this.stmt.getExecProps().isResponseBufferingAdaptive(), this.isForwardOnly(), this.toString()));
        this.activeStream = (Closeable)value;
        return value;
    }
    
    private SQLXML getSQLXMLInternal(final int columnIndex) throws SQLServerException {
        final SQLServerSQLXML value = (SQLServerSQLXML)this.getValue(columnIndex, JDBCType.SQLXML, new InputStreamGetterArgs(StreamType.SQLXML, this.stmt.getExecProps().isResponseBufferingAdaptive(), this.isForwardOnly(), this.toString()));
        if (null != value) {
            this.activeStream = value.getStream();
        }
        return value;
    }
    
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", columnIndex);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(columnIndex, StreamType.ASCII);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }
    
    @Override
    public InputStream getAsciiStream(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", columnName);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(this.findColumn(columnName), StreamType.ASCII);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", new Object[] { columnIndex, scale });
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String columnName, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "columnName", new Object[] { columnName, scale });
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", columnIndex);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(columnIndex, StreamType.BINARY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }
    
    @Override
    public InputStream getBinaryStream(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", columnName);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(this.findColumn(columnName), StreamType.BINARY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }
    
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBoolean", columnIndex);
        this.checkClosed();
        final Boolean value = (Boolean)this.getValue(columnIndex, JDBCType.BIT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value && value;
    }
    
    @Override
    public boolean getBoolean(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBoolean", columnName);
        this.checkClosed();
        final Boolean value = (Boolean)this.getValue(this.findColumn(columnName), JDBCType.BIT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value && value;
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getByte", columnIndex);
        this.checkClosed();
        final Short value = (Short)this.getValue(columnIndex, JDBCType.TINYINT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getByte", value);
        return (byte)((null != value) ? value.byteValue() : 0);
    }
    
    @Override
    public byte getByte(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getByte", columnName);
        this.checkClosed();
        final Short value = (Short)this.getValue(this.findColumn(columnName), JDBCType.TINYINT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getByte", value);
        return (byte)((null != value) ? value.byteValue() : 0);
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBytes", columnIndex);
        this.checkClosed();
        final byte[] value = (byte[])this.getValue(columnIndex, JDBCType.BINARY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }
    
    @Override
    public byte[] getBytes(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBytes", columnName);
        this.checkClosed();
        final byte[] value = (byte[])this.getValue(this.findColumn(columnName), JDBCType.BINARY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }
    
    @Override
    public Date getDate(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDate", columnIndex);
        this.checkClosed();
        final Date value = (Date)this.getValue(columnIndex, JDBCType.DATE);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public Date getDate(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDate", columnName);
        this.checkClosed();
        final Date value = (Date)this.getValue(this.findColumn(columnName), JDBCType.DATE);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[] { columnIndex, cal });
        }
        this.checkClosed();
        final Date value = (Date)this.getValue(columnIndex, JDBCType.DATE, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public Date getDate(final String colName, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[] { colName, cal });
        }
        this.checkClosed();
        final Date value = (Date)this.getValue(this.findColumn(colName), JDBCType.DATE, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDouble", columnIndex);
        this.checkClosed();
        final Double value = (Double)this.getValue(columnIndex, JDBCType.DOUBLE);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return (null != value) ? value : 0.0;
    }
    
    @Override
    public double getDouble(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDouble", columnName);
        this.checkClosed();
        final Double value = (Double)this.getValue(this.findColumn(columnName), JDBCType.DOUBLE);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return (null != value) ? value : 0.0;
    }
    
    @Override
    public float getFloat(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnIndex);
        this.checkClosed();
        final Float value = (Float)this.getValue(columnIndex, JDBCType.REAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return (null != value) ? value : 0.0f;
    }
    
    @Override
    public float getFloat(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnName);
        this.checkClosed();
        final Float value = (Float)this.getValue(this.findColumn(columnName), JDBCType.REAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return (null != value) ? value : 0.0f;
    }
    
    @Override
    public Geometry getGeometry(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnIndex);
        this.checkClosed();
        final Geometry value = (Geometry)this.getValue(columnIndex, JDBCType.GEOMETRY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }
    
    @Override
    public Geometry getGeometry(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnName);
        this.checkClosed();
        final Geometry value = (Geometry)this.getValue(this.findColumn(columnName), JDBCType.GEOMETRY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }
    
    @Override
    public Geography getGeography(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnIndex);
        this.checkClosed();
        final Geography value = (Geography)this.getValue(columnIndex, JDBCType.GEOGRAPHY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }
    
    @Override
    public Geography getGeography(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnName);
        this.checkClosed();
        final Geography value = (Geography)this.getValue(this.findColumn(columnName), JDBCType.GEOGRAPHY);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }
    
    @Override
    public int getInt(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getInt", columnIndex);
        this.checkClosed();
        final Integer value = (Integer)this.getValue(columnIndex, JDBCType.INTEGER);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return (null != value) ? value : 0;
    }
    
    @Override
    public int getInt(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getInt", columnName);
        this.checkClosed();
        final Integer value = (Integer)this.getValue(this.findColumn(columnName), JDBCType.INTEGER);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return (null != value) ? value : 0;
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getLong", columnIndex);
        this.checkClosed();
        final Long value = (Long)this.getValue(columnIndex, JDBCType.BIGINT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return (null != value) ? value : 0L;
    }
    
    @Override
    public long getLong(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getLong", columnName);
        this.checkClosed();
        final Long value = (Long)this.getValue(this.findColumn(columnName), JDBCType.BIGINT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return (null != value) ? value : 0L;
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getMetaData");
        this.checkClosed();
        if (this.metaData == null) {
            this.metaData = new SQLServerResultSetMetaData(this.stmt.connection, this);
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getMetaData", this.metaData);
        return this.metaData;
    }
    
    @Override
    public Object getObject(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getObject", columnIndex);
        this.checkClosed();
        final Object value = this.getValue(columnIndex, this.getterGetColumn(columnIndex).getTypeInfo().getSSType().getJDBCType());
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getObject", columnIndex);
        this.checkClosed();
        Object returnValue;
        if (type == String.class) {
            returnValue = this.getString(columnIndex);
        }
        else if (type == Byte.class) {
            final byte byteValue = this.getByte(columnIndex);
            returnValue = (this.wasNull() ? null : Byte.valueOf(byteValue));
        }
        else if (type == Short.class) {
            final short shortValue = this.getShort(columnIndex);
            returnValue = (this.wasNull() ? null : Short.valueOf(shortValue));
        }
        else if (type == Integer.class) {
            final int intValue = this.getInt(columnIndex);
            returnValue = (this.wasNull() ? null : Integer.valueOf(intValue));
        }
        else if (type == Long.class) {
            final long longValue = this.getLong(columnIndex);
            returnValue = (this.wasNull() ? null : Long.valueOf(longValue));
        }
        else if (type == BigDecimal.class) {
            returnValue = this.getBigDecimal(columnIndex);
        }
        else if (type == Boolean.class) {
            final boolean booleanValue = this.getBoolean(columnIndex);
            returnValue = (this.wasNull() ? null : Boolean.valueOf(booleanValue));
        }
        else if (type == Date.class) {
            returnValue = this.getDate(columnIndex);
        }
        else if (type == Time.class) {
            returnValue = this.getTime(columnIndex);
        }
        else if (type == Timestamp.class) {
            returnValue = this.getTimestamp(columnIndex);
        }
        else if (type == LocalDateTime.class || type == LocalDate.class || type == LocalTime.class) {
            final LocalDateTime ldt = this.getLocalDateTime(columnIndex);
            if (null == ldt) {
                returnValue = null;
            }
            else if (type == LocalDateTime.class) {
                returnValue = ldt;
            }
            else if (type == LocalDate.class) {
                returnValue = ldt.toLocalDate();
            }
            else {
                returnValue = ldt.toLocalTime();
            }
        }
        else if (type == OffsetDateTime.class) {
            final DateTimeOffset dateTimeOffset = this.getDateTimeOffset(columnIndex);
            if (dateTimeOffset == null) {
                returnValue = null;
            }
            else {
                returnValue = dateTimeOffset.getOffsetDateTime();
            }
        }
        else if (type == OffsetTime.class) {
            final DateTimeOffset dateTimeOffset = this.getDateTimeOffset(columnIndex);
            if (dateTimeOffset == null) {
                returnValue = null;
            }
            else {
                returnValue = dateTimeOffset.getOffsetDateTime().toOffsetTime();
            }
        }
        else if (type == DateTimeOffset.class) {
            returnValue = this.getDateTimeOffset(columnIndex);
        }
        else if (type == UUID.class) {
            final byte[] guid = this.getBytes(columnIndex);
            returnValue = ((guid != null) ? Util.readGUIDtoUUID(guid) : null);
        }
        else if (type == SQLXML.class) {
            returnValue = this.getSQLXML(columnIndex);
        }
        else if (type == Blob.class) {
            returnValue = this.getBlob(columnIndex);
        }
        else if (type == Clob.class) {
            returnValue = this.getClob(columnIndex);
        }
        else if (type == NClob.class) {
            returnValue = this.getNClob(columnIndex);
        }
        else if (type == byte[].class) {
            returnValue = this.getBytes(columnIndex);
        }
        else if (type == Float.class) {
            final float floatValue = this.getFloat(columnIndex);
            returnValue = (this.wasNull() ? null : Float.valueOf(floatValue));
        }
        else {
            if (type != Double.class) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionTo"));
                final Object[] msgArgs = { type };
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
            }
            final double doubleValue = this.getDouble(columnIndex);
            returnValue = (this.wasNull() ? null : Double.valueOf(doubleValue));
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getObject", columnIndex);
        return type.cast(returnValue);
    }
    
    @Override
    public Object getObject(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getObject", columnName);
        this.checkClosed();
        final Object value = this.getObject(this.findColumn(columnName));
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }
    
    @Override
    public <T> T getObject(final String columnName, final Class<T> type) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getObject", columnName);
        this.checkClosed();
        final T value = this.getObject(this.findColumn(columnName), type);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getShort", columnIndex);
        this.checkClosed();
        final Short value = (Short)this.getValue(columnIndex, JDBCType.SMALLINT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return (short)((null != value) ? ((short)value) : 0);
    }
    
    @Override
    public short getShort(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getShort", columnName);
        this.checkClosed();
        final Short value = (Short)this.getValue(this.findColumn(columnName), JDBCType.SMALLINT);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return (short)((null != value) ? ((short)value) : 0);
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getString", columnIndex);
        this.checkClosed();
        String value = null;
        final Object objectValue = this.getValue(columnIndex, JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }
    
    @Override
    public String getString(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getString", columnName);
        this.checkClosed();
        String value = null;
        final Object objectValue = this.getValue(this.findColumn(columnName), JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getNString", columnIndex);
        this.checkClosed();
        final String value = (String)this.getValue(columnIndex, JDBCType.NCHAR);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getNString", columnLabel);
        this.checkClosed();
        final String value = (String)this.getValue(this.findColumn(columnLabel), JDBCType.NCHAR);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }
    
    @Override
    public String getUniqueIdentifier(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getUniqueIdentifier", columnIndex);
        this.checkClosed();
        final String value = (String)this.getValue(columnIndex, JDBCType.GUID);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getUniqueIdentifier", value);
        return value;
    }
    
    @Override
    public String getUniqueIdentifier(final String columnLabel) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getUniqueIdentifier", columnLabel);
        this.checkClosed();
        final String value = (String)this.getValue(this.findColumn(columnLabel), JDBCType.GUID);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getUniqueIdentifier", value);
        return value;
    }
    
    @Override
    public Time getTime(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTime", columnIndex);
        this.checkClosed();
        final Time value = (Time)this.getValue(columnIndex, JDBCType.TIME);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Time getTime(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTime", columnName);
        this.checkClosed();
        final Time value = (Time)this.getValue(this.findColumn(columnName), JDBCType.TIME);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[] { columnIndex, cal });
        }
        this.checkClosed();
        final Time value = (Time)this.getValue(columnIndex, JDBCType.TIME, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Time getTime(final String colName, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[] { colName, cal });
        }
        this.checkClosed();
        final Time value = (Time)this.getValue(this.findColumn(colName), JDBCType.TIME, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", columnIndex);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", columnName);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(columnName), JDBCType.TIMESTAMP);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", new Object[] { columnIndex, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTimeStamp", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final String colName, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", new Object[] { colName, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(colName), JDBCType.TIMESTAMP, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    LocalDateTime getLocalDateTime(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getLocalDateTime", columnIndex);
        this.checkClosed();
        final LocalDateTime value = (LocalDateTime)this.getValue(columnIndex, JDBCType.LOCALDATETIME);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getLocalDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", columnIndex);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", columnName);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(columnName), JDBCType.TIMESTAMP);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final int columnIndex, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[] { columnIndex, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final String colName, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[] { colName, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(colName), JDBCType.TIMESTAMP, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", columnIndex);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", columnName);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(columnName), JDBCType.TIMESTAMP);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final int columnIndex, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[] { columnIndex, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final String colName, final Calendar cal) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[] { colName, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(colName), JDBCType.TIMESTAMP, cal);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public DateTimeOffset getDateTimeOffset(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", columnIndex);
        this.checkClosed();
        if (!this.stmt.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        final DateTimeOffset value = (DateTimeOffset)this.getValue(columnIndex, JDBCType.DATETIMEOFFSET);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }
    
    @Override
    public DateTimeOffset getDateTimeOffset(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", columnName);
        this.checkClosed();
        if (!this.stmt.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        final DateTimeOffset value = (DateTimeOffset)this.getValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getUnicodeStream", columnIndex);
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String columnName) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getUnicodeStream", columnName);
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public Object getObject(final int i, final Map<String, Class<?>> map) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getObject", new Object[] { i, map });
        }
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public Ref getRef(final int i) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getRef");
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public Blob getBlob(final int i) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBlob", i);
        this.checkClosed();
        final Blob value = (Blob)this.getValue(i, JDBCType.BLOB);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        this.activeLOB = (SQLServerLob)value;
        return value;
    }
    
    @Override
    public Blob getBlob(final String colName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBlob", colName);
        this.checkClosed();
        final Blob value = (Blob)this.getValue(this.findColumn(colName), JDBCType.BLOB);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        this.activeLOB = (SQLServerLob)value;
        return value;
    }
    
    @Override
    public Clob getClob(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getClob", columnIndex);
        this.checkClosed();
        final Clob value = (Clob)this.getValue(columnIndex, JDBCType.CLOB);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getClob", value);
        this.activeLOB = (SQLServerLob)value;
        return value;
    }
    
    @Override
    public Clob getClob(final String colName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getClob", colName);
        this.checkClosed();
        final Clob value = (Clob)this.getValue(this.findColumn(colName), JDBCType.CLOB);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getClob", value);
        this.activeLOB = (SQLServerLob)value;
        return value;
    }
    
    @Override
    public NClob getNClob(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getNClob", columnIndex);
        this.checkClosed();
        final NClob value = (NClob)this.getValue(columnIndex, JDBCType.NCLOB);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getNClob", value);
        this.activeLOB = (SQLServerLob)value;
        return value;
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getNClob", columnLabel);
        this.checkClosed();
        final NClob value = (NClob)this.getValue(this.findColumn(columnLabel), JDBCType.NCLOB);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getNClob", value);
        this.activeLOB = (SQLServerLob)value;
        return value;
    }
    
    @Override
    public Array getArray(final int i) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public Object getObject(final String colName, final Map<String, Class<?>> map) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public Ref getRef(final String colName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public Array getArray(final String colName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public String getCursorName() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getCursorName");
        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_positionedUpdatesNotSupported"), null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getCursorName", null);
        return null;
    }
    
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", columnIndex);
        this.checkClosed();
        final Reader value = (Reader)this.getStream(columnIndex, StreamType.CHARACTER);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getCharacterStream", value);
        return value;
    }
    
    @Override
    public Reader getCharacterStream(final String columnName) throws SQLException {
        this.checkClosed();
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", columnName);
        final Reader value = (Reader)this.getStream(this.findColumn(columnName), StreamType.CHARACTER);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getCharacterStream", value);
        return value;
    }
    
    @Override
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", columnIndex);
        this.checkClosed();
        final Reader value = (Reader)this.getStream(columnIndex, StreamType.NCHARACTER);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", value);
        return value;
    }
    
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", columnLabel);
        this.checkClosed();
        final Reader value = (Reader)this.getStream(this.findColumn(columnLabel), StreamType.NCHARACTER);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", value);
        return value;
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", columnIndex);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnName) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", columnName);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Override
    public BigDecimal getMoney(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getMoney", columnIndex);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }
    
    @Override
    public BigDecimal getMoney(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getMoney", columnName);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }
    
    @Override
    public BigDecimal getSmallMoney(final int columnIndex) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", columnIndex);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }
    
    @Override
    public BigDecimal getSmallMoney(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", columnName);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }
    
    @Override
    public RowId getRowId(final int columnIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", columnIndex);
        final SQLXML xml = this.getSQLXMLInternal(columnIndex);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", xml);
        return xml;
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", columnLabel);
        final SQLXML xml = this.getSQLXMLInternal(this.findColumn(columnLabel));
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", xml);
        return xml;
    }
    
    @Override
    public boolean rowUpdated() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "rowUpdated");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "rowUpdated", false);
        return false;
    }
    
    @Override
    public boolean rowInserted() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "rowInserted");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "rowInserted", false);
        return false;
    }
    
    @Override
    public boolean rowDeleted() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "rowDeleted");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        if (this.isOnInsertRow || !this.hasCurrentRow()) {
            return false;
        }
        final boolean deleted = this.currentRowDeleted();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "rowDeleted", deleted);
        return deleted;
    }
    
    private boolean currentRowDeleted() throws SQLServerException {
        assert this.hasCurrentRow();
        assert null != this.tdsReader;
        return this.deletedCurrentRow || (0 != this.serverCursorId && 2 == this.loadColumn(this.columns.length).getInt(this.tdsReader));
    }
    
    private Column updaterGetColumn(final int index) throws SQLServerException {
        this.verifyResultSetIsUpdatable();
        this.verifyValidColumnIndex(index);
        if (!this.columns[index - 1].isUpdatable()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_cantUpdateColumn"), "07009", false);
        }
        if (!this.isOnInsertRow) {
            if (!this.hasCurrentRow()) {
                SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_resultsetNoCurrentRow"), null, true);
            }
            this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        }
        return this.getColumn(index);
    }
    
    private void updateValue(final int columnIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final boolean forceEncrypt) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(jdbcType, value, javaType, null, null, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, forceEncrypt, columnIndex);
    }
    
    private void updateValue(final int columnIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final Calendar cal, final boolean forceEncrypt) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(jdbcType, value, javaType, null, cal, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, forceEncrypt, columnIndex);
    }
    
    private void updateValue(final int columnIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final Integer precision, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(jdbcType, value, javaType, null, null, scale, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, precision, forceEncrypt, columnIndex);
    }
    
    private void updateStream(final int columnIndex, final StreamType streamType, final Object value, final JavaType javaType, final long length) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(streamType.getJDBCType(), value, javaType, new StreamSetterArgs(streamType, length), null, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, false, columnIndex);
    }
    
    private void updateSQLXMLInternal(final int columnIndex, final SQLXML value) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(JDBCType.SQLXML, value, JavaType.SQLXML, new StreamSetterArgs(StreamType.SQLXML, -1L), null, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, false, columnIndex);
    }
    
    @Override
    public void updateNull(final int index) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNull", index);
        this.checkClosed();
        this.updateValue(index, this.updaterGetColumn(index).getTypeInfo().getSSType().getJDBCType(), null, JavaType.OBJECT, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNull");
    }
    
    @Override
    public void updateBoolean(final int index, final boolean x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIT, x, JavaType.BOOLEAN, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }
    
    @Override
    public void updateBoolean(final int index, final boolean x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIT, x, JavaType.BOOLEAN, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }
    
    @Override
    public void updateByte(final int index, final byte x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TINYINT, x, JavaType.BYTE, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }
    
    @Override
    public void updateByte(final int index, final byte x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TINYINT, x, JavaType.BYTE, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }
    
    @Override
    public void updateShort(final int index, final short x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLINT, x, JavaType.SHORT, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }
    
    @Override
    public void updateShort(final int index, final short x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLINT, x, JavaType.SHORT, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }
    
    @Override
    public void updateInt(final int index, final int x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.INTEGER, x, JavaType.INTEGER, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }
    
    @Override
    public void updateInt(final int index, final int x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.INTEGER, x, JavaType.INTEGER, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }
    
    @Override
    public void updateLong(final int index, final long x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIGINT, x, JavaType.LONG, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }
    
    @Override
    public void updateLong(final int index, final long x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIGINT, x, JavaType.LONG, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }
    
    @Override
    public void updateFloat(final int index, final float x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.REAL, x, JavaType.FLOAT, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }
    
    @Override
    public void updateFloat(final int index, final float x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.REAL, x, JavaType.FLOAT, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }
    
    @Override
    public void updateDouble(final int index, final double x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DOUBLE, x, JavaType.DOUBLE, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }
    
    @Override
    public void updateDouble(final int index, final double x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DOUBLE, x, JavaType.DOUBLE, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }
    
    @Override
    public void updateMoney(final int index, final BigDecimal x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.MONEY, x, JavaType.BIGDECIMAL, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }
    
    @Override
    public void updateMoney(final int index, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.MONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }
    
    @Override
    public void updateMoney(final String columnName, final BigDecimal x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.MONEY, x, JavaType.BIGDECIMAL, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }
    
    @Override
    public void updateMoney(final String columnName, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.MONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }
    
    @Override
    public void updateSmallMoney(final int index, final BigDecimal x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }
    
    @Override
    public void updateSmallMoney(final int index, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }
    
    @Override
    public void updateSmallMoney(final String columnName, final BigDecimal x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }
    
    @Override
    public void updateSmallMoney(final String columnName, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }
    
    @Override
    public void updateBigDecimal(final int index, final BigDecimal x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateBigDecimal(final int index, final BigDecimal x, final Integer precision, final Integer scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateBigDecimal(final int index, final BigDecimal x, final Integer precision, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateString(final int columnIndex, final String stringValue) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[] { columnIndex, stringValue });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.VARCHAR, stringValue, JavaType.STRING, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }
    
    @Override
    public void updateString(final int columnIndex, final String stringValue, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[] { columnIndex, stringValue, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.VARCHAR, stringValue, JavaType.STRING, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }
    
    @Override
    public void updateNString(final int columnIndex, final String nString) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[] { columnIndex, nString });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.NVARCHAR, nString, JavaType.STRING, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }
    
    @Override
    public void updateNString(final int columnIndex, final String nString, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[] { columnIndex, nString, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.NVARCHAR, nString, JavaType.STRING, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }
    
    @Override
    public void updateNString(final String columnLabel, final String nString) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[] { columnLabel, nString });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnLabel), JDBCType.NVARCHAR, nString, JavaType.STRING, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }
    
    @Override
    public void updateNString(final String columnLabel, final String nString, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[] { columnLabel, nString, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnLabel), JDBCType.NVARCHAR, nString, JavaType.STRING, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }
    
    @Override
    public void updateBytes(final int index, final byte[] x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BINARY, x, JavaType.BYTEARRAY, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }
    
    @Override
    public void updateBytes(final int index, final byte[] x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BINARY, x, JavaType.BYTEARRAY, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }
    
    @Override
    public void updateDate(final int index, final Date x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATE, x, JavaType.DATE, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }
    
    @Override
    public void updateDate(final int index, final Date x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATE, x, JavaType.DATE, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }
    
    @Override
    public void updateTime(final int index, final Time x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIME, x, JavaType.TIME, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }
    
    @Override
    public void updateTime(final int index, final Time x, final Integer scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIME, x, JavaType.TIME, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }
    
    @Override
    public void updateTime(final int index, final Time x, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIME, x, JavaType.TIME, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }
    
    @Override
    public void updateTimestamp(final int index, final Timestamp x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }
    
    @Override
    public void updateTimestamp(final int index, final Timestamp x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }
    
    @Override
    public void updateTimestamp(final int index, final Timestamp x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }
    
    @Override
    public void updateDateTime(final int index, final Timestamp x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIME, x, JavaType.TIMESTAMP, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }
    
    @Override
    public void updateDateTime(final int index, final Timestamp x, final Integer scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }
    
    @Override
    public void updateDateTime(final int index, final Timestamp x, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }
    
    @Override
    public void updateSmallDateTime(final int index, final Timestamp x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }
    
    @Override
    public void updateSmallDateTime(final int index, final Timestamp x, final Integer scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }
    
    @Override
    public void updateSmallDateTime(final int index, final Timestamp x, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }
    
    @Override
    public void updateDateTimeOffset(final int index, final DateTimeOffset x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }
    
    @Override
    public void updateDateTimeOffset(final int index, final DateTimeOffset x, final Integer scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }
    
    @Override
    public void updateDateTimeOffset(final int index, final DateTimeOffset x, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }
    
    @Override
    public void updateUniqueIdentifier(final int index, final String x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[] { index, x });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.GUID, x, JavaType.STRING, null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }
    
    @Override
    public void updateUniqueIdentifier(final int index, final String x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.GUID, x, JavaType.STRING, null, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[] { columnIndex, x });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }
    
    @Override
    public void updateAsciiStream(final int index, final InputStream x, final int length) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[] { index, x, length });
        }
        this.checkClosed();
        this.updateStream(index, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[] { columnIndex, x, length });
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[] { columnLabel, x });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.ASCII, x, JavaType.INPUTSTREAM, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }
    
    @Override
    public void updateAsciiStream(final String columnName, final InputStream x, final int length) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[] { columnName, x, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }
    
    @Override
    public void updateAsciiStream(final String columnName, final InputStream streamValue, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[] { columnName, streamValue, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.ASCII, streamValue, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[] { columnIndex, x });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream streamValue, final int length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[] { columnIndex, streamValue, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, streamValue, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[] { columnIndex, x, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[] { columnLabel, x });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, x, JavaType.INPUTSTREAM, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }
    
    @Override
    public void updateBinaryStream(final String columnName, final InputStream streamValue, final int length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[] { columnName, streamValue, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.BINARY, streamValue, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[] { columnLabel, x, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[] { columnIndex, x });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, x, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader readerValue, final int length) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[] { columnIndex, readerValue, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, readerValue, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[] { columnIndex, x, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, x, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[] { columnLabel, reader });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }
    
    @Override
    public void updateCharacterStream(final String columnName, final Reader readerValue, final int length) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[] { columnName, readerValue, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.CHARACTER, readerValue, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[] { columnLabel, reader, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[] { columnIndex, x });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, x, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[] { columnIndex, x, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, x, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[] { columnLabel, reader });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[] { columnLabel, reader, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }
    
    @Override
    public void updateObject(final int index, final Object obj) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, obj });
        }
        this.checkClosed();
        this.updateObject(index, obj, null, null, null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final int index, final Object x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateObject(index, x, scale, null, null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final int index, final Object x, final int precision, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, x, scale });
        }
        this.checkClosed();
        this.updateObject(index, x, scale, null, precision, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final int index, final Object x, final int precision, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateObject(index, x, scale, null, precision, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    protected final void updateObject(final int index, final Object x, final Integer scale, JDBCType jdbcType, final Integer precision, final boolean forceEncrypt) throws SQLServerException {
        final Column column = this.updaterGetColumn(index);
        final SSType ssType = column.getTypeInfo().getSSType();
        if (null == x) {
            if (null == jdbcType || jdbcType.isUnsupported()) {
                jdbcType = ssType.getJDBCType();
            }
            column.updateValue(jdbcType, x, JavaType.OBJECT, null, null, scale, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, precision, forceEncrypt, index);
        }
        else {
            final JavaType javaType = JavaType.of(x);
            final JDBCType objectJdbcType = javaType.getJDBCType(ssType, ssType.getJDBCType());
            if (null == jdbcType) {
                jdbcType = objectJdbcType;
            }
            else if (!objectJdbcType.convertsTo(jdbcType)) {
                DataTypes.throwConversionError(objectJdbcType.toString(), jdbcType.toString());
            }
            StreamSetterArgs streamSetterArgs = null;
            switch (javaType) {
                case READER: {
                    streamSetterArgs = new StreamSetterArgs(StreamType.CHARACTER, -1L);
                    break;
                }
                case INPUTSTREAM: {
                    streamSetterArgs = new StreamSetterArgs(jdbcType.isTextual() ? StreamType.CHARACTER : StreamType.BINARY, -1L);
                    break;
                }
                case SQLXML: {
                    streamSetterArgs = new StreamSetterArgs(StreamType.SQLXML, -1L);
                    break;
                }
            }
            column.updateValue(jdbcType, x, javaType, streamSetterArgs, null, scale, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, precision, forceEncrypt, index);
        }
    }
    
    @Override
    public void updateNull(final String columnName) throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNull", columnName);
        this.checkClosed();
        final int columnIndex = this.findColumn(columnName);
        this.updateValue(columnIndex, this.updaterGetColumn(columnIndex).getTypeInfo().getSSType().getJDBCType(), null, JavaType.OBJECT, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNull");
    }
    
    @Override
    public void updateBoolean(final String columnName, final boolean x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIT, x, JavaType.BOOLEAN, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }
    
    @Override
    public void updateBoolean(final String columnName, final boolean x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIT, x, JavaType.BOOLEAN, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }
    
    @Override
    public void updateByte(final String columnName, final byte x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTE, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }
    
    @Override
    public void updateByte(final String columnName, final byte x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTE, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }
    
    @Override
    public void updateShort(final String columnName, final short x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLINT, x, JavaType.SHORT, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }
    
    @Override
    public void updateShort(final String columnName, final short x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLINT, x, JavaType.SHORT, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }
    
    @Override
    public void updateInt(final String columnName, final int x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.INTEGER, x, JavaType.INTEGER, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }
    
    @Override
    public void updateInt(final String columnName, final int x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.INTEGER, x, JavaType.INTEGER, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }
    
    @Override
    public void updateLong(final String columnName, final long x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIGINT, x, JavaType.LONG, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }
    
    @Override
    public void updateLong(final String columnName, final long x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIGINT, x, JavaType.LONG, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }
    
    @Override
    public void updateFloat(final String columnName, final float x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.REAL, x, JavaType.FLOAT, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }
    
    @Override
    public void updateFloat(final String columnName, final float x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.REAL, x, JavaType.FLOAT, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }
    
    @Override
    public void updateDouble(final String columnName, final double x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DOUBLE, x, JavaType.DOUBLE, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }
    
    @Override
    public void updateDouble(final String columnName, final double x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DOUBLE, x, JavaType.DOUBLE, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x, final Integer precision, final Integer scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { columnName, x, precision, scale });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x, final Integer precision, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[] { columnName, x, precision, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }
    
    @Override
    public void updateString(final String columnName, final String x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.VARCHAR, x, JavaType.STRING, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }
    
    @Override
    public void updateString(final String columnName, final String x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.VARCHAR, x, JavaType.STRING, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }
    
    @Override
    public void updateBytes(final String columnName, final byte[] x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTEARRAY, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }
    
    @Override
    public void updateBytes(final String columnName, final byte[] x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTEARRAY, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }
    
    @Override
    public void updateDate(final String columnName, final Date x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATE, x, JavaType.DATE, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }
    
    @Override
    public void updateDate(final String columnName, final Date x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATE, x, JavaType.DATE, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }
    
    @Override
    public void updateTime(final String columnName, final Time x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIME, x, JavaType.TIME, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }
    
    @Override
    public void updateTime(final String columnName, final Time x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[] { columnName, x, scale });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIME, x, JavaType.TIME, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }
    
    @Override
    public void updateTime(final String columnName, final Time x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[] { columnName, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIME, x, JavaType.TIME, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }
    
    @Override
    public void updateTimestamp(final String columnName, final Timestamp x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }
    
    @Override
    public void updateTimestamp(final String columnName, final Timestamp x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[] { columnName, x, scale });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }
    
    @Override
    public void updateTimestamp(final String columnName, final Timestamp x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[] { columnName, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }
    
    @Override
    public void updateDateTime(final String columnName, final Timestamp x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIME, x, JavaType.TIMESTAMP, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }
    
    @Override
    public void updateDateTime(final String columnName, final Timestamp x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[] { columnName, x, scale });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }
    
    @Override
    public void updateDateTime(final String columnName, final Timestamp x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[] { columnName, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }
    
    @Override
    public void updateSmallDateTime(final String columnName, final Timestamp x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }
    
    @Override
    public void updateSmallDateTime(final String columnName, final Timestamp x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[] { columnName, x, scale });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }
    
    @Override
    public void updateSmallDateTime(final String columnName, final Timestamp x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[] { columnName, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }
    
    @Override
    public void updateDateTimeOffset(final String columnName, final DateTimeOffset x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }
    
    @Override
    public void updateDateTimeOffset(final String columnName, final DateTimeOffset x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[] { columnName, x, scale });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }
    
    @Override
    public void updateDateTimeOffset(final String columnName, final DateTimeOffset x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[] { columnName, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }
    
    @Override
    public void updateUniqueIdentifier(final String columnName, final String x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.GUID, x, JavaType.STRING, null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }
    
    @Override
    public void updateUniqueIdentifier(final String columnName, final String x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[] { columnName, x, forceEncrypt });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.GUID, x, JavaType.STRING, null, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }
    
    @Override
    public void updateObject(final String columnName, final Object x, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, x, scale });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, scale, null, null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final String columnName, final Object x, final int precision, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, x, precision, scale });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, scale, null, precision, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final String columnName, final Object x, final int precision, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, x, precision, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, scale, null, precision, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final String columnName, final Object x) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, x });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, null, null, null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSQLXML", new Object[] { columnIndex, xmlObject });
        }
        this.updateSQLXMLInternal(columnIndex, xmlObject);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSQLXML");
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML x) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateSQLXML", new Object[] { columnLabel, x });
        }
        this.updateSQLXMLInternal(this.findColumn(columnLabel), x);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateSQLXML");
    }
    
    @Override
    public int getHoldability() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getHoldability");
        this.checkClosed();
        final int holdability = (0 == this.stmt.getServerCursorId()) ? 1 : this.stmt.getExecProps().getHoldability();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getHoldability", holdability);
        return holdability;
    }
    
    @Override
    public void insertRow() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "insertRow");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        if (!this.isOnInsertRow) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_mustBeOnInsertRow"), null, true);
        }
        Column tableColumn = null;
        for (final Column column : this.columns) {
            if (column.hasUpdates()) {
                tableColumn = column;
                break;
            }
            if (null == tableColumn && column.isUpdatable()) {
                tableColumn = column;
            }
        }
        if (null == tableColumn) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_noColumnParameterValue"), null, true);
        }
        assert tableColumn.isUpdatable();
        assert null != tableColumn.getTableName();
        final class InsertRowRPC extends TDSCommand
        {
            private static final long serialVersionUID = 1L;
            final String tableName = tableColumn.getTableName().asEscapedString();
            
            InsertRowRPC() {
                super("InsertRowRPC", 0, 0);
            }
            
            @Override
            final boolean doExecute() throws SQLServerException {
                SQLServerResultSet.this.doInsertRowRPC(this, this.tableName);
                return true;
            }
        }
        this.stmt.executeCommand(new InsertRowRPC());
        if (-3 != this.rowCount) {
            ++this.rowCount;
        }
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "insertRow");
    }
    
    private void doInsertRowRPC(final TDSCommand command, final String tableName) throws SQLServerException {
        assert 0 != this.serverCursorId;
        assert null != tableName;
        assert tableName.length() > 0;
        final TDSWriter tdsWriter = command.startRequest((byte)3);
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)1);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeRPCInt(null, this.serverCursorId, false);
        tdsWriter.writeRPCInt(null, 4, false);
        tdsWriter.writeRPCInt(null, this.fetchBufferGetRow(), false);
        if (this.hasUpdatedColumns()) {
            tdsWriter.writeRPCStringUnicode(tableName);
            for (final Column column : this.columns) {
                column.sendByRPC(tdsWriter, this.stmt.connection);
            }
        }
        else {
            tdsWriter.writeRPCStringUnicode("");
            tdsWriter.writeRPCStringUnicode("INSERT INTO " + tableName + " DEFAULT VALUES");
        }
        TDSParser.parse(command.startResponse(), command.getLogContext());
    }
    
    @Override
    public void updateRow() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateRow");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        if (!this.hasUpdatedColumns()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_noColumnParameterValue"), null, true);
        }
        try {
            final class UpdateRowRPC extends TDSCommand
            {
                private static final long serialVersionUID = 1L;
                
                UpdateRowRPC() {
                    super("UpdateRowRPC", 0, 0);
                }
                
                @Override
                final boolean doExecute() throws SQLServerException {
                    SQLServerResultSet.this.doUpdateRowRPC(this);
                    return true;
                }
            }
            this.stmt.executeCommand(new UpdateRowRPC());
        }
        finally {
            this.cancelUpdates();
        }
        this.updatedCurrentRow = true;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateRow");
    }
    
    private void doUpdateRowRPC(final TDSCommand command) throws SQLServerException {
        assert 0 != this.serverCursorId;
        final TDSWriter tdsWriter = command.startRequest((byte)3);
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)1);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeRPCInt(null, this.serverCursorId, false);
        tdsWriter.writeRPCInt(null, 33, false);
        tdsWriter.writeRPCInt(null, this.fetchBufferGetRow(), false);
        tdsWriter.writeRPCStringUnicode("");
        assert this.hasUpdatedColumns();
        for (final Column column : this.columns) {
            column.sendByRPC(tdsWriter, this.stmt.connection);
        }
        TDSParser.parse(command.startResponse(), command.getLogContext());
    }
    
    final boolean hasUpdatedColumns() {
        for (final Column column : this.columns) {
            if (column.hasUpdates()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void deleteRow() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "deleteRow");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        try {
            final class DeleteRowRPC extends TDSCommand
            {
                private static final long serialVersionUID = 1L;
                
                DeleteRowRPC() {
                    super("DeleteRowRPC", 0, 0);
                }
                
                @Override
                final boolean doExecute() throws SQLServerException {
                    SQLServerResultSet.this.doDeleteRowRPC(this);
                    return true;
                }
            }
            this.stmt.executeCommand(new DeleteRowRPC());
        }
        finally {
            this.cancelUpdates();
        }
        this.deletedCurrentRow = true;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "deleteRow");
    }
    
    private void doDeleteRowRPC(final TDSCommand command) throws SQLServerException {
        assert 0 != this.serverCursorId;
        final TDSWriter tdsWriter = command.startRequest((byte)3);
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)1);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeRPCInt(null, this.serverCursorId, false);
        tdsWriter.writeRPCInt(null, 34, false);
        tdsWriter.writeRPCInt(null, this.fetchBufferGetRow(), false);
        tdsWriter.writeRPCStringUnicode("");
        TDSParser.parse(command.startResponse(), command.getLogContext());
    }
    
    @Override
    public void refreshRow() throws SQLException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "refreshRow");
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerResultSet.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        if (1004 == this.stmt.getResultSetType() || 0 == this.serverCursorId) {
            return;
        }
        this.cancelUpdates();
        this.doRefreshRow();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "refreshRow");
    }
    
    private void doRefreshRow() throws SQLServerException {
        assert this.hasCurrentRow();
        final int fetchBufferSavedRow = this.fetchBufferGetRow();
        this.doServerFetch(128, 0, 0);
        int fetchBufferRestoredRow;
        for (fetchBufferRestoredRow = 0; fetchBufferRestoredRow < fetchBufferSavedRow; ++fetchBufferRestoredRow) {
            if (this.isForwardOnly()) {
                if (!this.fetchBufferNext()) {
                    break;
                }
            }
            else if (!this.scrollWindow.next(this)) {
                break;
            }
        }
        if (fetchBufferRestoredRow < fetchBufferSavedRow) {
            this.currentRow = -1;
            return;
        }
        this.updatedCurrentRow = false;
    }
    
    private void cancelUpdates() {
        if (!this.isOnInsertRow) {
            this.clearColumnsValues();
        }
    }
    
    @Override
    public void cancelRowUpdates() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "cancelRowUpdates");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.cancelUpdates();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "cancelRowUpdates");
    }
    
    @Override
    public void moveToInsertRow() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "moveToInsertRow");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.cancelUpdates();
        this.isOnInsertRow = true;
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "moveToInsertRow");
    }
    
    @Override
    public void moveToCurrentRow() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "moveToCurrentRow");
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.cancelInsert();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "moveToCurrentRow");
    }
    
    @Override
    public Statement getStatement() throws SQLServerException {
        SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "getStatement");
        this.checkClosed();
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "getStatement", this.stmt);
        return this.stmt;
    }
    
    @Override
    public void updateClob(final int columnIndex, final Clob clobValue) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnIndex, clobValue });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.CLOB, clobValue, JavaType.CLOB, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnIndex, reader });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnIndex, reader, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }
    
    @Override
    public void updateClob(final String columnName, final Clob clobValue) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnName, clobValue });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.CLOB, clobValue, JavaType.CLOB, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnLabel, reader });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnLabel, reader, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[] { columnIndex, nClob });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.NCLOB, nClob, JavaType.NCLOB, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[] { columnIndex, reader });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[] { columnIndex, reader, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, reader, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[] { columnLabel, nClob });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnLabel), JDBCType.NCLOB, nClob, JavaType.NCLOB, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[] { columnLabel, reader });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[] { columnLabel, reader, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }
    
    @Override
    public void updateBlob(final int columnIndex, final Blob blobValue) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[] { columnIndex, blobValue });
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.BLOB, blobValue, JavaType.BLOB, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[] { columnIndex, inputStream });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[] { columnIndex, inputStream, length });
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }
    
    @Override
    public void updateBlob(final String columnName, final Blob blobValue) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[] { columnName, blobValue });
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BLOB, blobValue, JavaType.BLOB, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[] { columnLabel, inputStream });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, -1L);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[] { columnLabel, inputStream, length });
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }
    
    @Override
    public void updateArray(final int columnIndex, final Array x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }
    
    @Override
    public void updateArray(final String columnName, final Array x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }
    
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }
    
    @Override
    public void updateRef(final String columnName, final Ref x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }
    
    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    @Override
    public URL getURL(final String sColumn) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }
    
    final void doServerFetch(final int fetchType, final int startRow, final int numRows) throws SQLServerException {
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(this.toString() + " fetchType:" + fetchType + " startRow:" + startRow + " numRows:" + numRows);
        }
        this.discardFetchBuffer();
        this.fetchBuffer.init();
        final CursorFetchCommand cursorFetch = new CursorFetchCommand(this.serverCursorId, fetchType, startRow, numRows);
        this.stmt.executeCommand(cursorFetch);
        this.numFetchedRows = 0;
        this.resultSetCurrentRowType = RowType.UNKNOWN;
        this.areNullCompressedColumnsInitialized = false;
        this.lastColumnIndex = 0;
        if (null != this.scrollWindow && 128 != fetchType) {
            this.scrollWindow.resize(this.fetchSize);
        }
        if (numRows >= 0) {
            if (startRow >= 0) {
                return;
            }
        }
        try {
            while (this.scrollWindow.next(this)) {}
        }
        catch (final SQLException e) {
            if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                SQLServerResultSet.logger.finer(this.toString() + " Ignored exception from row error during server cursor fixup: " + e.getMessage());
            }
        }
        if (this.fetchBuffer.needsServerCursorFixup()) {
            this.doServerFetch(1, 0, 0);
            return;
        }
        this.scrollWindow.reset();
    }
    
    private void fillLOBs() {
        if (null != this.activeLOB) {
            try {
                this.activeLOB.fillFromStream();
            }
            catch (final SQLException e) {
                if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                    SQLServerResultSet.logger.finer(this.toString() + "Filling Lobs before closing: " + e.getMessage());
                }
            }
            finally {
                this.activeLOB = null;
            }
        }
    }
    
    private void discardFetchBuffer() {
        this.fillLOBs();
        this.fetchBuffer.clearStartMark();
        if (null != this.scrollWindow) {
            this.scrollWindow.clear();
        }
        try {
            while (this.fetchBufferNext()) {}
        }
        catch (final SQLServerException e) {
            if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                SQLServerResultSet.logger.finer(this + " Encountered exception discarding fetch buffer: " + e.getMessage());
            }
        }
    }
    
    final void closeServerCursor() {
        if (0 == this.serverCursorId) {
            return;
        }
        if (this.stmt.connection.isSessionUnAvailable()) {
            if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                SQLServerResultSet.logger.finer(this + ": Not closing cursor:" + this.serverCursorId + "; connection is already closed.");
            }
        }
        else {
            if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                SQLServerResultSet.logger.finer(this.toString() + " Closing cursor:" + this.serverCursorId);
            }
            try {
                final class CloseServerCursorCommand extends UninterruptableTDSCommand
                {
                    private static final long serialVersionUID = 1L;
                    
                    CloseServerCursorCommand() {
                        super("closeServerCursor");
                    }
                    
                    @Override
                    final boolean doExecute() throws SQLServerException {
                        final TDSWriter tdsWriter = this.startRequest((byte)3);
                        tdsWriter.writeShort((short)(-1));
                        tdsWriter.writeShort((short)9);
                        tdsWriter.writeByte((byte)0);
                        tdsWriter.writeByte((byte)0);
                        tdsWriter.sendEnclavePackage(null, null);
                        tdsWriter.writeRPCInt(null, SQLServerResultSet.this.serverCursorId, false);
                        TDSParser.parse(this.startResponse(), this.getLogContext());
                        return true;
                    }
                }
                this.stmt.executeCommand(new CloseServerCursorCommand());
            }
            catch (final SQLServerException e) {
                if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                    SQLServerResultSet.logger.finer(this.toString() + " Ignored error closing cursor:" + this.serverCursorId + " " + e.getMessage());
                }
            }
            if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
                SQLServerResultSet.logger.finer(this.toString() + " Closed cursor:" + this.serverCursorId);
            }
        }
    }
    
    @Override
    public void updateObject(final int index, final Object obj, final SQLType targetSqlType) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, obj, targetSqlType });
        }
        this.checkClosed();
        this.updateObject(index, obj, null, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final int index, final Object obj, final SQLType targetSqlType, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, obj, targetSqlType, scale });
        }
        this.checkClosed();
        this.updateObject(index, obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final int index, final Object obj, final SQLType targetSqlType, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { index, obj, targetSqlType, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateObject(index, obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final String columnName, final Object obj, final SQLType targetSqlType, final int scale) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, obj, targetSqlType, scale });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final String columnName, final Object obj, final SQLType targetSqlType, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, obj, targetSqlType, scale, forceEncrypt });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, forceEncrypt);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    @Override
    public void updateObject(final String columnName, final Object obj, final SQLType targetSqlType) throws SQLServerException {
        if (SQLServerResultSet.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerResultSet.loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[] { columnName, obj, targetSqlType });
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), obj, null, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        SQLServerResultSet.loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }
    
    static {
        lastResultSetID = new AtomicInteger(0);
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerResultSet");
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.ResultSet");
    }
    
    private final class FetchBuffer
    {
        private final FetchBufferTokenHandler fetchBufferTokenHandler;
        private TDSReaderMark startMark;
        private RowType fetchBufferCurrentRowType;
        private boolean done;
        private boolean needsServerCursorFixup;
        
        final void clearStartMark() {
            this.startMark = null;
        }
        
        final boolean needsServerCursorFixup() {
            return this.needsServerCursorFixup;
        }
        
        FetchBuffer() {
            this.fetchBufferTokenHandler = new FetchBufferTokenHandler();
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            this.init();
        }
        
        final void ensureStartMark() {
            if (null == this.startMark && !SQLServerResultSet.this.isForwardOnly()) {
                if (SQLServerResultSet.logger.isLoggable(Level.FINEST)) {
                    SQLServerResultSet.logger.finest(this.toString() + " Setting fetch buffer start mark");
                }
                this.startMark = SQLServerResultSet.this.tdsReader.mark();
            }
        }
        
        final void reset() {
            assert null != SQLServerResultSet.this.tdsReader;
            assert null != this.startMark;
            SQLServerResultSet.this.tdsReader.reset(this.startMark);
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            this.done = false;
        }
        
        final void init() {
            this.startMark = ((0 == SQLServerResultSet.this.serverCursorId && !SQLServerResultSet.this.isForwardOnly()) ? SQLServerResultSet.this.tdsReader.mark() : null);
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            this.done = false;
            this.needsServerCursorFixup = false;
        }
        
        final RowType nextRow() throws SQLServerException {
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            while (null != SQLServerResultSet.this.tdsReader && !this.done && this.fetchBufferCurrentRowType.equals(RowType.UNKNOWN)) {
                TDSParser.parse(SQLServerResultSet.this.tdsReader, this.fetchBufferTokenHandler);
            }
            if (this.fetchBufferCurrentRowType.equals(RowType.UNKNOWN) && null != this.fetchBufferTokenHandler.getDatabaseError()) {
                SQLServerException.makeFromDatabaseError(SQLServerResultSet.this.stmt.connection, null, this.fetchBufferTokenHandler.getDatabaseError().getErrorMessage(), this.fetchBufferTokenHandler.getDatabaseError(), false);
            }
            return this.fetchBufferCurrentRowType;
        }
        
        private final class FetchBufferTokenHandler extends TDSTokenHandler
        {
            static final /* synthetic */ boolean $assertionsDisabled;
            
            FetchBufferTokenHandler() {
                super("FetchBufferTokenHandler");
            }
            
            @Override
            boolean onColMetaData(final TDSReader tdsReader) throws SQLServerException {
                new StreamColumns(Util.shouldHonorAEForRead(SQLServerResultSet.this.stmt.stmtColumnEncriptionSetting, SQLServerResultSet.this.stmt.connection)).setFromTDS(tdsReader);
                return true;
            }
            
            @Override
            boolean onRow(final TDSReader tdsReader) throws SQLServerException {
                FetchBuffer.this.ensureStartMark();
                if (209 != tdsReader.readUnsignedByte() && !FetchBufferTokenHandler.$assertionsDisabled) {
                    throw new AssertionError();
                }
                FetchBuffer.this.fetchBufferCurrentRowType = RowType.ROW;
                return false;
            }
            
            @Override
            boolean onNBCRow(final TDSReader tdsReader) throws SQLServerException {
                FetchBuffer.this.ensureStartMark();
                if (210 != tdsReader.readUnsignedByte() && !FetchBufferTokenHandler.$assertionsDisabled) {
                    throw new AssertionError();
                }
                FetchBuffer.this.fetchBufferCurrentRowType = RowType.NBCROW;
                return false;
            }
            
            @Override
            boolean onDone(final TDSReader tdsReader) throws SQLServerException {
                FetchBuffer.this.ensureStartMark();
                final StreamDone doneToken = new StreamDone();
                doneToken.setFromTDS(tdsReader);
                FetchBuffer.this.done = true;
                return 0 != SQLServerResultSet.this.serverCursorId;
            }
            
            @Override
            boolean onRetStatus(final TDSReader tdsReader) throws SQLServerException {
                final StreamRetStatus retStatusToken = new StreamRetStatus();
                retStatusToken.setFromTDS(tdsReader);
                FetchBuffer.this.needsServerCursorFixup = (2 == retStatusToken.getStatus());
                return true;
            }
            
            @Override
            void onEOF(final TDSReader tdsReader) throws SQLServerException {
                super.onEOF(tdsReader);
                FetchBuffer.this.done = true;
            }
        }
    }
    
    private final class CursorFetchCommand extends TDSCommand
    {
        private static final long serialVersionUID = 1L;
        private final int serverCursorId;
        private int fetchType;
        private int startRow;
        private int numRows;
        
        CursorFetchCommand(final int serverCursorId, final int fetchType, final int startRow, final int numRows) {
            super("doServerFetch", SQLServerResultSet.this.stmt.queryTimeout, SQLServerResultSet.this.stmt.cancelQueryTimeoutSeconds);
            this.serverCursorId = serverCursorId;
            this.fetchType = fetchType;
            this.startRow = startRow;
            this.numRows = numRows;
        }
        
        @Override
        final boolean doExecute() throws SQLServerException {
            final TDSWriter tdsWriter = this.startRequest((byte)3);
            tdsWriter.writeShort((short)(-1));
            tdsWriter.writeShort((short)7);
            tdsWriter.writeByte((byte)2);
            tdsWriter.writeByte((byte)0);
            tdsWriter.sendEnclavePackage(null, null);
            tdsWriter.writeRPCInt(null, this.serverCursorId, false);
            tdsWriter.writeRPCInt(null, this.fetchType, false);
            tdsWriter.writeRPCInt(null, this.startRow, false);
            tdsWriter.writeRPCInt(null, this.numRows, false);
            SQLServerResultSet.this.tdsReader = this.startResponse(SQLServerResultSet.this.isForwardOnly() && 1007 != SQLServerResultSet.this.stmt.resultSetConcurrency && SQLServerResultSet.this.stmt.getExecProps().wasResponseBufferingSet() && SQLServerResultSet.this.stmt.getExecProps().isResponseBufferingAdaptive());
            return false;
        }
        
        @Override
        final void processResponse(final TDSReader responseTDSReader) throws SQLServerException {
            SQLServerResultSet.this.tdsReader = responseTDSReader;
            SQLServerResultSet.this.discardFetchBuffer();
        }
    }
}
