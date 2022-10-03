package com.adventnet.mfw.message;

import com.adventnet.persistence.OperationInfo;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class DataObjectFilter implements MessageFilter
{
    private static final Logger LOGGER;
    List tableNames;
    int operationType;
    String dataSource;
    
    public DataObjectFilter() {
        this.tableNames = null;
        this.operationType = -1;
        this.dataSource = null;
    }
    
    public void setTableList(final List tbNames) {
        this.tableNames = tbNames;
    }
    
    public void setOperationType(final int oType) {
        this.operationType = oType;
    }
    
    public void setDSName(final String dsName) {
        this.dataSource = dsName;
    }
    
    @Override
    public boolean matches(final Object obj) {
        DataObjectFilter.LOGGER.log(Level.FINEST, "Object in DataObjectFilter.matches :: {0}", obj);
        try {
            final OperationInfo incomingOI = (OperationInfo)obj;
            if (this.operationType != -1 && this.operationType != incomingOI.getOperation()) {
                if (incomingOI.getOperation() == 1 && this.operationType != 4 && this.operationType != 5) {
                    DataObjectFilter.LOGGER.log(Level.FINEST, "operationType doesnot match");
                    return false;
                }
                if (incomingOI.getOperation() == 2 && this.operationType != 4 && this.operationType != 6) {
                    DataObjectFilter.LOGGER.log(Level.FINEST, "operationType doesnot match");
                    return false;
                }
                if (incomingOI.getOperation() == 3 && this.operationType != 5 && this.operationType != 6) {
                    DataObjectFilter.LOGGER.log(Level.FINEST, "operationType doesnot match");
                    return false;
                }
            }
            if (this.dataSource != null && this.dataSource.equalsIgnoreCase(incomingOI.getDSName())) {
                DataObjectFilter.LOGGER.log(Level.FINEST, "dataSource doesnot match");
                return false;
            }
            return this.isTableNameMatches(this.tableNames, incomingOI.getTableNames());
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception thrown while checking the filter.matches for the Object :: " + obj, e);
        }
    }
    
    protected boolean isTableNameMatches(final List<String> tableNames, final List<String> incomingTableNames) {
        if (tableNames == null) {
            return true;
        }
        for (int i = 0; i < tableNames.size(); ++i) {
            if (incomingTableNames.contains(tableNames.get(i))) {
                return true;
            }
        }
        DataObjectFilter.LOGGER.log(Level.FINEST, "dataObject doesnot match");
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(DataObjectFilter.class.getName());
    }
}
