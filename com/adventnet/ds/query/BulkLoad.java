package com.adventnet.ds.query;

import com.adventnet.db.persistence.metadata.AllowedValues;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Set;
import java.util.Arrays;
import java.io.IOException;
import com.adventnet.db.adapter.DTTransformationUtil;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.concurrent.Callable;
import java.util.TimerTask;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Calendar;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.sql.SQLException;
import com.adventnet.db.persistence.SequenceGenerator;
import java.util.HashMap;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.concurrent.atomic.AtomicBoolean;
import com.adventnet.db.api.RelationalAPI;
import java.util.Timer;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.db.adapter.DBAdapter;
import java.sql.Connection;
import java.util.logging.Logger;

public class BulkLoad implements AutoCloseable
{
    private static final Logger LOGGER;
    private Connection connection;
    private DBAdapter dbAdapter;
    private String tableName;
    private int sizeOfBufferInMB;
    private int idleTimeOutInSecs;
    private Object[] rowLvlByteValues;
    private BulkInsertObject bulkInsertObject;
    private Timer t1;
    private RelationalAPI relApi;
    private boolean initStream;
    private boolean isConnectionCreated;
    private AtomicBoolean isFlushed;
    private boolean empty;
    private boolean check;
    private boolean isFirstRow;
    private boolean bulkHalt;
    private boolean fillUvg;
    private long lastWrite;
    private boolean fillDefaultValue;
    private TableDefinition tableDef;
    private List<String> colNames;
    private Throwable error;
    private boolean keyOpen;
    public boolean timerHaltInvoked;
    public static final byte DELIMITER = 9;
    public static final byte LINETERMINATOR = 13;
    public long counter;
    public int psBatchSize;
    public boolean createTempTable;
    public boolean containsEncryptedColumns;
    private String dbName;
    private int[] recomputedIndex;
    private boolean isJsondc;
    private boolean isArchivedTable;
    private String archivedTableName;
    HashMap<String, SequenceGenerator> map;
    int index;
    
    public BulkLoad(final String tableName) throws SQLException, MetaDataException {
        this(tableName, null, null);
    }
    
    public BulkLoad(final String tableName, final Connection connection, final DBAdapter dba) throws SQLException, MetaDataException {
        this(tableName, null, connection, dba);
    }
    
    public BulkLoad(final String liveTableName, final String archivedTableName, final Connection connection, final DBAdapter dba) throws SQLException, MetaDataException {
        this.tableName = "";
        this.sizeOfBufferInMB = 1;
        this.idleTimeOutInSecs = 300;
        this.relApi = RelationalAPI.getInstance();
        this.initStream = Boolean.FALSE;
        this.isConnectionCreated = Boolean.FALSE;
        this.isFlushed = new AtomicBoolean(true);
        this.empty = Boolean.TRUE;
        this.check = Boolean.FALSE;
        this.isFirstRow = Boolean.TRUE;
        this.bulkHalt = Boolean.TRUE;
        this.fillUvg = Boolean.FALSE;
        this.lastWrite = 0L;
        this.fillDefaultValue = Boolean.FALSE;
        this.tableDef = null;
        this.colNames = null;
        this.error = null;
        this.keyOpen = Boolean.FALSE;
        this.timerHaltInvoked = Boolean.FALSE;
        this.counter = 0L;
        this.psBatchSize = 500;
        this.createTempTable = false;
        this.containsEncryptedColumns = false;
        this.dbName = null;
        this.isJsondc = false;
        this.isArchivedTable = false;
        this.archivedTableName = null;
        this.map = null;
        this.index = -1;
        BulkLoad.LOGGER.log(Level.FINE, "*********************************************************************************************************************");
        BulkLoad.LOGGER.log(Level.FINE, "BulkLoad Program Commencing...(using 3 argument constructor)");
        this.initConnection(connection);
        this.setDBAdapter(dba);
        this.tableName = liveTableName;
        this.dbName = PersistenceInitializer.getConfigurationValue("DBName");
        this.lastWrite = Calendar.getInstance().getTimeInMillis();
        if (archivedTableName != null) {
            this.isArchivedTable = true;
            this.archivedTableName = archivedTableName;
        }
        this.initDef();
    }
    
    public String getArchivedTableName() {
        return this.archivedTableName;
    }
    
    public void setDBName(final String name) {
        this.dbName = name;
    }
    
    public String getDBName() {
        return this.dbName;
    }
    
    public boolean isArchivedTable() {
        return this.isArchivedTable;
    }
    
    public void setColumnNames(final List<String> columnNames) {
        this.colNames = columnNames;
    }
    
    public List<String> getColumnNames() {
        return this.colNames;
    }
    
    private void initDef() throws MetaDataException {
        try {
            this.tableDef = MetaDataUtil.getTableDefinitionByName(this.tableName);
            if (this.tableDef != null) {
                this.colNames = this.tableDef.getColumnNames();
                this.createTempTable(false);
                if (this.tableDef.getPhysicalColumns() != null && !this.tableDef.getPhysicalColumns().isEmpty()) {
                    this.isJsondc = true;
                }
            }
        }
        catch (final MetaDataException e) {
            e.printStackTrace();
        }
    }
    
    public String geDBName() {
        return this.dbName;
    }
    
    private void setDBAdapter(final DBAdapter dba) {
        if (null != dba) {
            this.dbAdapter = dba;
            return;
        }
        this.dbAdapter = this.relApi.getDBAdapter();
    }
    
    private void initConnection(final Connection connection) throws SQLException {
        if (null == connection) {
            this.connection = RelationalAPI.getInstance().getConnection();
            this.isConnectionCreated = true;
            return;
        }
        this.connection = connection;
    }
    
    public void setBufferSize(final int size) {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "BufferSize should be set before setting the bulk-insert values");
            return;
        }
        this.sizeOfBufferInMB = size;
    }
    
    public void setIdleTimeOut(final int timeout) {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "setIdleTimeOut should be set before setting the bulk-insert values");
            return;
        }
        this.idleTimeOutInSecs = timeout;
    }
    
    public void enableDataTypeCheck(final Boolean check) {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "enableDataTypeCheck should be set before setting the bulk-insert values");
            return;
        }
        this.check = check;
    }
    
    public void createTempTable(final Boolean createTempTable) {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "createTempTable should be set before setting the bulk-insert values");
            return;
        }
        if (null != this.tableDef) {
            if (this.tableDef.getEncryptedColumnNames().size() > 0) {
                BulkLoad.LOGGER.info("Setting createTempTable to true since table has encrypted columns, to avoid data inconsistency/corruption!");
                this.createTempTable = true;
            }
        }
        else {
            this.createTempTable = createTempTable;
        }
    }
    
    public void setBatchSize(final int batchSize) {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "setBatchSize should be set before setting the bulk-insert values");
            return;
        }
        this.psBatchSize = batchSize;
    }
    
    public void setAutoFillUVG(final boolean fill) throws Exception {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "setBatchSize should be set before setting the bulk-insert values");
            return;
        }
        this.fillUvg = fill;
        if (this.fillUvg && this.tableDef != null) {
            this.fetchUVGDetails();
        }
    }
    
    public void setFillDefaultValues(final boolean fillDefaultValue) {
        if (this.bulkInsertObject != null) {
            BulkLoad.LOGGER.log(Level.WARNING, "setFillDefaultValues should be set before setting the bulk-insert values");
            return;
        }
        this.fillDefaultValue = fillDefaultValue;
    }
    
    private void fetchUVGDetails() throws Exception {
        ColumnDefinition cd = null;
        this.map = new HashMap<String, SequenceGenerator>();
        for (final String colName : this.colNames) {
            cd = this.tableDef.getColumnDefinitionByName(colName);
            if (cd != null && null != cd.getUniqueValueGeneration()) {
                this.map.put(cd.getColumnName(), this.getSequence(cd.getUniqueValueGeneration()));
            }
        }
    }
    
    private synchronized SequenceGenerator getSequence(final UniqueValueGeneration uvgName) throws Exception {
        SequenceGenerator gen = SequenceGeneratorRepository.get(uvgName.getGeneratorName());
        if (gen == null) {
            try {
                SequenceGeneratorRepository.initGeneratorValues(this.tableDef);
                gen = SequenceGeneratorRepository.get(uvgName.getGeneratorName());
            }
            catch (final Exception e) {
                throw new DataAccessException("Problem in initializing the SequenceGenerator for the tableName : " + this.tableName, e);
            }
        }
        return gen;
    }
    
    private void checkStream() throws Exception {
        try {
            if (!this.initStream) {
                this.bulkInsertObject = this.dbAdapter.createBulkInsertObject(this);
                this.initStream = true;
                this.isFlushed.set(false);
                this.rowLvlByteValues = new Object[this.bulkInsertObject.getColNames().size()];
                this.resetValues();
                final Callable<?> bulkExecTask = new BulkExecutor(this);
                this.dbAdapter.getBulkThreadExecutor().submit(bulkExecTask);
                BulkLoad.LOGGER.log(Level.INFO, "*** Bulk Load Operations Thread Started :: [{0}] ***", this.getTableName());
                (this.t1 = new Timer()).schedule(new HaltTask(this), 0L, this.idleTimeOutInSecs * 1000);
            }
        }
        catch (final Exception e) {
            BulkLoad.LOGGER.log(Level.INFO, "Problem While Creating Stream!!!");
            e.printStackTrace();
            throw e;
        }
    }
    
    private void resetValues() throws Exception {
        ColumnDefinition cDef = null;
        for (final String colName : this.colNames) {
            if (this.tableDef != null) {
                cDef = this.tableDef.getColumnDefinitionByName(colName);
                if (this.fillDefaultValue && null != cDef.getDefaultValue()) {
                    this.rowLvlByteValues[this.getActualColumnPosition(this.colNames.indexOf(colName) + 1) - 1] = cDef.getDefaultValue();
                }
                else {
                    this.rowLvlByteValues[this.getActualColumnPosition(this.colNames.indexOf(colName) + 1) - 1] = null;
                }
            }
            else {
                this.rowLvlByteValues[this.getActualColumnPosition(this.colNames.indexOf(colName) + 1) - 1] = null;
            }
        }
    }
    
    private void writeData(final int columnPosition, Object value) throws Exception {
        int actualPosition = 0;
        try {
            actualPosition = this.getActualColumnPosition(columnPosition);
        }
        catch (final ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
            throw new ArrayIndexOutOfBoundsException("Column Position Out of Bound! Please correct the same and restart the Bulk Operation... Invalid Column Position :: " + columnPosition);
        }
        if (actualPosition > this.rowLvlByteValues.length || actualPosition < 1) {
            throw new ArrayIndexOutOfBoundsException("Column Position Out of Bound! Please correct the same and restart the Bulk Operation... Invalid Column Position :: " + actualPosition);
        }
        this.checkForInterrupt();
        if (this.tableDef != null) {
            final String columnName = this.colNames.get(columnPosition - 1);
            this.validateAllowedValues(this.tableDef.getColumnDefinitionByName(columnName), value);
            final String datatype = this.bulkInsertObject.getColTypeNames().get(actualPosition - 1);
            if (DataTypeUtil.isUDT(datatype)) {
                if (!DataTypeManager.getDataTypeDefinition(datatype).getMeta().processInput()) {
                    throw new SQLException("Input value can't be set for an UDT column with processInput, false. TableName, ColumnName : [ " + this.tableName + ", " + columnName + " ]");
                }
                try {
                    value = DTTransformationUtil.transform(this.tableName, columnName, value, datatype, this.dbName);
                }
                catch (final Exception e) {
                    throw new SQLException("Exception while transforming data." + e);
                }
            }
            if (this.isJsondc) {
                final int physicalColumnIndex = this.recomputedIndex.length - (this.colNames.size() - this.bulkInsertObject.getColNames().size());
                if (actualPosition == physicalColumnIndex && this.dbAdapter.getDCAdapter(this.tableDef.getDynamicColumnType()) != null) {
                    value = this.dbAdapter.getDCAdapter(this.tableDef.getDynamicColumnType()).getModifiedObjectForDynamicColumn(this.rowLvlByteValues[actualPosition - 1], columnName, value);
                }
            }
        }
        this.rowLvlByteValues[actualPosition - 1] = value;
    }
    
    private int getActualColumnPosition(final int columnPosition) throws Exception {
        return this.recomputedIndex[columnPosition - 1];
    }
    
    public void flush() throws Exception {
        this.checkForInterrupt();
        this.bulkHalt = Boolean.FALSE;
        try {
            if (!this.initStream && this.fillUvg) {
                this.checkStream();
            }
            if (!this.initStream) {
                BulkLoad.LOGGER.log(Level.WARNING, "Cannot call flush() without adding any rows!!!");
                throw new IOException("No Rows added!!! Hence cannot call flush()...");
            }
            while (!this.bulkInsertObject.isReadyToWrite() && this.isFirstRow) {
                Thread.sleep(100L);
                BulkLoad.LOGGER.fine("Waiting for readyToWrite !" + this.getTableName());
            }
            this.isFirstRow = Boolean.FALSE;
            if (this.fillUvg) {
                final Set<?> keys = this.map.keySet();
                for (final String columnName : keys) {
                    if (null != this.rowLvlByteValues[this.colNames.indexOf(columnName)]) {
                        BulkLoad.LOGGER.log(Level.WARNING, "Cannot set value for an UVG column!!! Replacing with UVG value.");
                    }
                    this.rowLvlByteValues[this.colNames.indexOf(columnName)] = this.map.get(columnName).nextValue();
                }
            }
            if (this.arrayNotEmpty()) {
                ++this.counter;
                this.empty = false;
                BulkLoad.LOGGER.log(Level.FINE, "Table :: [{0}], Row :: [{1}] ", new Object[] { this.tableName, Arrays.asList(this.rowLvlByteValues) });
                this.dbAdapter.addBatch(this.rowLvlByteValues, this);
                this.resetValues();
                this.lastWrite = Calendar.getInstance().getTimeInMillis();
                this.bulkHalt = Boolean.TRUE;
                return;
            }
            this.empty = true;
        }
        catch (final InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        catch (final IOException e2) {
            if (!e2.getMessage().toLowerCase().contains("read end dead")) {
                e2.printStackTrace();
                throw e2;
            }
            BulkLoad.LOGGER.log(Level.INFO, "Source closed abruptly!!! Source >>> ");
            e2.printStackTrace();
        }
        catch (final SQLException e3) {
            e3.printStackTrace();
            throw e3;
        }
    }
    
    private boolean arrayNotEmpty() {
        int nullCounter = 0;
        if (null != this.rowLvlByteValues) {
            for (final Object obj : this.rowLvlByteValues) {
                if (null == obj) {
                    ++nullCounter;
                }
            }
            if (nullCounter != this.rowLvlByteValues.length) {
                return true;
            }
        }
        BulkLoad.LOGGER.log(Level.INFO, "Row contains only NULL! Hence ignored.");
        return false;
    }
    
    @Override
    public void close() throws Exception {
        this.close(false);
    }
    
    private void close(final boolean forceClose) throws Exception {
        try {
            this.checkForInterrupt();
            if (this.empty && this.isFlushed.get()) {
                if (this.initStream) {
                    throw new IOException("Row(s) not flushed!!! Use forceClose() instead! BulkLoad Operation Incomplete!!!");
                }
                BulkLoad.LOGGER.log(Level.INFO, "No row(s) added... and flush not invoked!!!");
            }
            else {
                if (this.empty && !this.isFlushed.get()) {
                    this.dbAdapter.closeBulkInsertObject(this);
                    throw new IOException("Row(s) not flushed!!! Use forceClose() instead! BulkLoad Operation Incomplete!!!");
                }
                if (!this.empty) {
                    this.dbAdapter.closeBulkInsertObject(this);
                    this.bulkHalt = Boolean.FALSE;
                    BulkLoad.LOGGER.log(Level.FINE, "DataBufferStream closed!!!");
                    this.empty = true;
                }
            }
            BulkLoad.LOGGER.fine("forceClose :: " + forceClose + " :: " + this.getTableName());
            BulkLoad.LOGGER.fine("isFlushed :: " + this.isFlushed.get() + " :: " + this.getTableName());
            if (!forceClose) {
                while (!this.isFlushed.get()) {
                    BulkLoad.LOGGER.fine("Waiting for isFlushed !" + this.getTableName());
                    if (null != this.error) {
                        final SQLException sqe = new SQLException(this.error.getMessage());
                        sqe.initCause(this.error);
                        throw sqe;
                    }
                    Thread.sleep(100L);
                }
            }
        }
        finally {
            if (null != this.bulkInsertObject) {
                this.getBulkInsertObject().exceptionCaused.getAndSet(true);
            }
            this.closeConnection();
            if (this.initStream) {
                this.t1.cancel();
            }
            BulkLoad.LOGGER.log(Level.FINE, "*********************************************************************************************************************");
        }
        BulkLoad.LOGGER.fine(" Is BIO NULL :: " + (null == this.bulkInsertObject));
        if (null != this.bulkInsertObject && null != this.bulkInsertObject.getError()) {
            BulkLoad.LOGGER.info("Stored Exception Thrown From BulkLoad :: ");
            throw this.bulkInsertObject.getError();
        }
        BulkLoad.LOGGER.fine("Exception Object is empty!!! :: [" + this.getTableName() + "]");
    }
    
    public void forceClose() throws Exception {
        this.close(true);
    }
    
    private void closeConnection() throws SQLException {
        if (null != this.getConnection()) {
            if (this.isConnectionCreated) {
                this.getConnection().close();
                BulkLoad.LOGGER.log(Level.INFO, "*** Connection Closed [{0}] ***", this.tableName);
            }
            else {
                BulkLoad.LOGGER.log(Level.INFO, "*** Connection will not be closed by BulkLoad [{0}] ***", this.tableName);
            }
        }
        else {
            BulkLoad.LOGGER.log(Level.INFO, "Stream Already Closed");
            Thread.dumpStack();
        }
    }
    
    private void checkForInterrupt() throws Exception {
        if (this.timerHaltInvoked) {
            throw new Exception("Timer Interrupted!!! Timeout exceeded : " + this.idleTimeOutInSecs + " secs");
        }
    }
    
    public void setInt(final int columnPosition, final Integer value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setInt(final String columnName, final Integer value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setLong(final int columnPosition, final Long value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setLong(final String columnName, final Long value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setDecimal(final int columnPosition, final BigDecimal value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setDecimal(final String columnName, final BigDecimal value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setTinyInt(final int columnPosition, final Short value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setTinyInt(final String columnName, final Short value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setInputStream(final int columnPosition, final InputStream value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setInputStream(final String columnName, final InputStream value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setObject(final int columnPosition, final Object value) throws Exception {
        this.checkStream();
        if (null != value) {
            if (this.check) {
                this.bulkInsertObject.checkDataType(this.getActualColumnPosition(columnPosition), value);
            }
            this.writeData(columnPosition, value);
        }
    }
    
    public void setObject(final String columnName, final Object value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.indexCheck(columnName);
            if (this.check) {
                this.bulkInsertObject.checkDataType(this.getActualColumnPosition(this.index), value);
            }
            this.writeData(this.index, value);
        }
    }
    
    public void setBoolean(final int columnPosition, final Boolean value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setBoolean(final String columnName, final Boolean value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setBytes(final int columnPosition, final byte[] value) throws Exception {
        this.checkStream();
        if (null != value) {
            if (this.check) {
                this.bulkInsertObject.checkDataType(columnPosition, value);
            }
            this.writeData(columnPosition, value);
        }
    }
    
    public void setBytes(final String columnName, final byte[] value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.indexCheck(columnName);
            if (this.check) {
                this.bulkInsertObject.checkDataType(this.index, value);
            }
            this.writeData(this.index, value);
        }
    }
    
    public void setDate(final int columnPosition, final Date value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setDate(final String columnName, final Date value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setTime(final int columnPosition, final Time value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setTime(final String columnName, final Time value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setDouble(final int columnPosition, final Double value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setDouble(final String columnName, final Double value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setFloat(final int columnPosition, final Float value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setFloat(final String columnName, final Float value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    public void setString(final int columnPosition, final String value) throws Exception {
        this.checkStream();
        if (null != value) {
            if (this.check) {
                this.bulkInsertObject.checkDataType(columnPosition, value);
            }
            this.writeData(columnPosition, value);
        }
    }
    
    public void setString(final String columnName, final String value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.indexCheck(columnName);
            if (this.check) {
                this.bulkInsertObject.checkDataType(this.index, value);
            }
            this.writeData(this.index, value);
        }
    }
    
    public void setTimeStamp(final int columnPosition, final Timestamp value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(columnPosition, value);
        }
    }
    
    public void setTimeStamp(final String columnName, final Timestamp value) throws Exception {
        this.checkStream();
        if (null != value) {
            this.writeData(this.indexCheck(columnName), value);
        }
    }
    
    private int getIndex(final List<String> colNames, final String colName) {
        int indexOfCol = 0;
        for (int i = 0; i < colNames.size(); ++i) {
            if (colNames.get(i).equalsIgnoreCase(colName)) {
                indexOfCol = i + 1;
                break;
            }
        }
        return indexOfCol;
    }
    
    private int indexCheck(final String columnName) throws Exception {
        if (this.tableDef != null) {
            this.index = this.colNames.indexOf(columnName) + 1;
        }
        else {
            this.index = this.getIndex(this.bulkInsertObject.getColNames(), columnName);
        }
        if (this.index == 0) {
            throw new ArrayIndexOutOfBoundsException("Column index out of bound for Column: " + columnName + " in table: " + this.tableName + ". Please check whether such a column exist & it's index");
        }
        return this.index;
    }
    
    public int getBuffersize() {
        return this.sizeOfBufferInMB;
    }
    
    public BulkInsertObject getBulkInsertObject() {
        return this.bulkInsertObject;
    }
    
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public boolean isDataCheckEnabled() {
        return this.check;
    }
    
    public void setCloseKey(final Boolean keyOpen) {
        this.keyOpen = keyOpen;
    }
    
    public boolean canCloseKey() {
        return this.keyOpen;
    }
    
    private void validateAllowedValues(final ColumnDefinition colDefn, final Object value) throws MetaDataException {
        final AllowedValues allowedVal = colDefn.getAllowedValues();
        if (null != allowedVal) {
            allowedVal.validateValue(MetaDataUtil.convert(String.valueOf(value), colDefn.getDataType()));
        }
    }
    
    public int[] getRecomputedIndices() {
        return this.recomputedIndex;
    }
    
    public void setRecomputedIndices(final int[] recomputedIndices) {
        this.recomputedIndex = recomputedIndices;
    }
    
    static {
        LOGGER = Logger.getLogger(BulkLoad.class.getName());
    }
    
    class BulkExecutor implements Callable<Boolean>
    {
        BulkLoad bulk;
        
        public BulkExecutor(final BulkLoad bulk) {
            this.bulk = bulk;
        }
        
        @Override
        public Boolean call() throws Exception {
            try {
                BulkLoad.LOGGER.log(Level.FINE, "TableName :: [{0}]", BulkLoad.this.getTableName());
                BulkLoad.LOGGER.log(Level.FINE, "Column Names :: [{0}]", this.bulk.bulkInsertObject.getColNames().toString());
                BulkLoad.LOGGER.log(Level.FINE, "Concurrent load started");
                this.bulk.dbAdapter.execBulk(this.bulk);
                this.bulk.isFlushed.getAndSet(true);
                BulkLoad.LOGGER.fine("isFlushed set!!!" + this.bulk.isFlushed.get() + " :: " + this.bulk.getTableName());
            }
            catch (final Throwable e) {
                final SQLException sqe = new SQLException(e.getMessage());
                sqe.initCause(e);
                this.bulk.error = e;
                this.bulk.getBulkInsertObject().exceptionCaused.getAndSet(true);
                throw sqe;
            }
            return true;
        }
    }
    
    class HaltTask extends TimerTask
    {
        BulkLoad bulk;
        
        HaltTask(final BulkLoad bulk) {
            this.bulk = null;
            this.bulk = bulk;
        }
        
        @Override
        public void run() {
            try {
                if (Calendar.getInstance().getTimeInMillis() - BulkLoad.this.lastWrite > BulkLoad.this.idleTimeOutInSecs * 1000 && this.bulk.bulkHalt) {
                    BulkLoad.LOGGER.log(Level.INFO, "Inside TimeOut Check! " + this.bulk.getTableName());
                    this.bulk.dbAdapter.closeBulkInsertObject(this.bulk);
                    for (int i = 0; i < BulkLoad.this.rowLvlByteValues.length; ++i) {
                        BulkLoad.this.rowLvlByteValues[i] = null;
                    }
                    this.bulk.getBulkInsertObject().exceptionCaused.getAndSet(true);
                    BulkLoad.this.closeConnection();
                    BulkLoad.this.t1.cancel();
                    this.bulk.timerHaltInvoked = Boolean.TRUE;
                    BulkLoad.LOGGER.log(Level.INFO, "Terminating BulkLoad Operations!!!");
                }
            }
            catch (final Exception e) {
                this.bulk.getBulkInsertObject().exceptionCaused.getAndSet(true);
                e.printStackTrace();
            }
        }
    }
}
