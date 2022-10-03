package com.adventnet.persistence;

import java.util.logging.Level;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DefaultOperationHandler implements OperationHandler
{
    private static String CLASS_NAME;
    private static final Logger LOGGER;
    private int maxRowsPerTable;
    private int maxRowsPerOperation;
    private Map tableCounter;
    private WritableDataObject dObj;
    private List tableNames;
    private List origTableNames;
    private List bulkTables;
    private int totalCount;
    private boolean isSuspended;
    private WritableDataObject filterDO;
    
    public DefaultOperationHandler() {
        this.maxRowsPerTable = 50;
        this.maxRowsPerOperation = 1000;
        this.tableCounter = new HashMap();
        this.tableNames = new ArrayList();
        this.origTableNames = new ArrayList();
        this.bulkTables = new ArrayList();
        this.totalCount = 0;
        this.isSuspended = false;
    }
    
    public DefaultOperationHandler(final Properties props) {
        this.maxRowsPerTable = 50;
        this.maxRowsPerOperation = 1000;
        this.tableCounter = new HashMap();
        this.tableNames = new ArrayList();
        this.origTableNames = new ArrayList();
        this.bulkTables = new ArrayList();
        this.totalCount = 0;
        this.isSuspended = false;
        final String perTab = props.getProperty("max-rows-per-table");
        if (perTab != null) {
            try {
                this.maxRowsPerTable = Integer.parseInt(perTab);
            }
            catch (final NumberFormatException nfe) {
                DefaultOperationHandler.LOGGER.log(Level.WARNING, "Invalid number given for max-rows-per-table. Defaulting to 50", nfe);
                this.maxRowsPerTable = 50;
            }
        }
        final String perOp = props.getProperty("max-rows-per-operation");
        if (perOp != null) {
            try {
                this.maxRowsPerOperation = Integer.parseInt(perOp);
            }
            catch (final NumberFormatException nfe2) {
                DefaultOperationHandler.LOGGER.log(Level.WARNING, "Invalid number given for max-rows-per-operation. Defaulting to 1000", nfe2);
                this.maxRowsPerOperation = 1000;
            }
        }
    }
    
    @Override
    public void suspend() {
        DefaultOperationHandler.LOGGER.log(Level.FINEST, "Operation suspended");
        this.isSuspended = true;
    }
    
    @Override
    public void resume() {
        DefaultOperationHandler.LOGGER.log(Level.FINEST, "Operation resumed");
        this.isSuspended = false;
    }
    
    @Override
    public void addRow(final int operation, final Row row) throws DataAccessException {
        this.addRow(operation, row, -1);
    }
    
    @Override
    public void addRow(final int operation, final Row row, final int constraints) throws DataAccessException {
        if (this.isSuspended) {
            DefaultOperationHandler.LOGGER.log(Level.FINEST, "Operation is suspended hence not adding this row :: [{0}]", row);
            return;
        }
        if (operation == 3) {
            if (constraints == 1) {
                this.filterRow(operation, row);
            }
            else {
                this.filterRow(2, row);
            }
            this.dObj = this.filterDO;
            return;
        }
        this.updateTableLists(row);
        if (this.dObj == null) {
            this.dObj = new WritableDataObject();
        }
        final Row rr = (Row)row.clone();
        if (operation == 1) {
            this.dObj.addRow(rr);
        }
        else if (operation == 2) {
            this.dObj.add(rr);
            this.dObj.updateRow(rr);
        }
    }
    
    private void updateTableLists(final Row row) {
        final String tName = row.getTableName();
        final String origTableName = row.getOriginalTableName();
        if (!this.tableNames.contains(tName)) {
            this.tableNames.add(tName);
        }
        if (!this.origTableNames.contains(origTableName)) {
            this.origTableNames.add(origTableName);
        }
    }
    
    private void filterRow(final int operation, Row row) throws DataAccessException {
        final String tabName = row.getTableName();
        this.updateTableLists(row);
        ++this.totalCount;
        if (this.totalCount > this.maxRowsPerOperation) {
            if (this.totalCount == this.maxRowsPerOperation + 1) {
                DefaultOperationHandler.LOGGER.log(Level.FINEST, "Number of rows affected, {0}, in the operation exceeded the limit {1}. Hence cleaning up the data.", new Object[] { new Integer(this.totalCount), new Integer(this.maxRowsPerOperation) });
                this.filterDO = null;
            }
            else {
                DefaultOperationHandler.LOGGER.log(Level.FINEST, "Number of rows affected, {0}, in the operation exceeded the limit {1}. Hence ignoring the row {2}", new Object[] { new Integer(this.totalCount), new Integer(this.maxRowsPerOperation), row });
            }
            return;
        }
        if (this.bulkTables.contains(tabName)) {
            DefaultOperationHandler.LOGGER.log(Level.FINEST, "Operation is bulk w.r.t. table {0}. Hence ignoring the row {1}", new Object[] { tabName, row });
            return;
        }
        this.incrementCount(tabName);
        final Counter tabCount = this.tableCounter.get(tabName);
        if (tabCount.getValue() > this.maxRowsPerTable) {
            DefaultOperationHandler.LOGGER.log(Level.FINEST, "Number of rows affected in the table {0} exceeded the limit {1}. Hence ignoring the row {2}", new Object[] { tabName, new Integer(this.maxRowsPerTable), row });
            this.bulkTables.add(tabName);
            this.filterDO.deleteRow(row);
            this.filterDO.deleteActionInfo(tabName);
            return;
        }
        if (this.filterDO == null) {
            this.filterDO = new WritableDataObject();
        }
        row = (Row)row.clone();
        if (operation == 1) {
            this.filterDO.addRow(row);
        }
        else if (operation == 2) {
            this.filterDO.add(row);
            this.filterDO.updateRow(row);
        }
        else if (operation == 3) {
            this.filterDO.add(row);
            this.filterDO.deleteRow(row);
        }
    }
    
    @Override
    public void filterDataObject(final DataObject allDO) throws DataAccessException {
        if (allDO != null) {
            this.filterDO = new WritableDataObject();
            this.totalCount = 0;
            this.tableCounter = new HashMap();
            final List operations = allDO.getOperations();
            if (!operations.isEmpty()) {
                this.tableNames = new ArrayList();
                this.origTableNames = new ArrayList();
                for (int size = operations.size(), i = 0; i < size; ++i) {
                    final ActionInfo info = operations.get(i);
                    final Row row = info.getValue();
                    final int operation = info.getOperation();
                    this.filterRow(operation, row);
                }
                this.dObj = this.filterDO;
            }
            else {
                DefaultOperationHandler.LOGGER.log(Level.FINER, "ATTN : OPERATIONS in Notification DO is empty");
            }
        }
    }
    
    @Override
    public void setDataObject(final WritableDataObject dObj) {
        this.dObj = dObj;
    }
    
    @Override
    public DataObject getDataObject() throws DataAccessException {
        if (this.dObj == null || (this.dObj.getTableNames().isEmpty() && this.dObj.getOperations().isEmpty())) {
            return null;
        }
        this.dObj.clearIndices();
        return this.dObj;
    }
    
    @Override
    public List getTableNames() {
        return this.tableNames;
    }
    
    @Override
    public List getOrigTableNames() {
        return this.origTableNames;
    }
    
    @Override
    public List getBulkTableNames() {
        return this.bulkTables;
    }
    
    @Override
    public void setTableNames(final List tableNames) {
        this.tableNames = tableNames;
    }
    
    @Override
    public void setBulkTableNames(final List bulkTableNames) {
        this.bulkTables = bulkTableNames;
    }
    
    @Override
    public boolean isBulk() {
        return this.totalCount > this.maxRowsPerOperation || this.bulkTables.size() > 0;
    }
    
    private void initCounting(final DataObject dObj) {
        final List operations = dObj.getOperations();
        for (int size = operations.size(), i = 0; i < size; ++i) {
            final ActionInfo actionInfo = operations.get(i);
            final Row row = actionInfo.getValue();
            final String tableName = row.getTableName();
            ++this.totalCount;
            if (!this.bulkTables.contains(tableName)) {
                this.incrementCount(tableName);
            }
        }
    }
    
    private void incrementCount(final String tableName) {
        Counter tabCount = this.tableCounter.get(tableName);
        if (tabCount == null) {
            tabCount = new Counter();
            tabCount.increment();
            this.tableCounter.put(tableName, tabCount);
        }
        else {
            tabCount.increment();
        }
    }
    
    static {
        DefaultOperationHandler.CLASS_NAME = DefaultOperationHandler.class.getName();
        LOGGER = Logger.getLogger(DefaultOperationHandler.CLASS_NAME);
    }
    
    class Counter
    {
        private int count;
        
        int getValue() {
            return this.count;
        }
        
        void increment() {
            ++this.count;
        }
    }
}
