package com.adventnet.persistence;

import java.util.List;

public interface OperationHandler
{
    void addRow(final int p0, final Row p1) throws DataAccessException;
    
    void addRow(final int p0, final Row p1, final int p2) throws DataAccessException;
    
    void setDataObject(final WritableDataObject p0);
    
    DataObject getDataObject() throws DataAccessException;
    
    void setBulkTableNames(final List p0);
    
    void setTableNames(final List p0);
    
    List getBulkTableNames();
    
    List getTableNames();
    
    List getOrigTableNames();
    
    boolean isBulk();
    
    void filterDataObject(final DataObject p0) throws DataAccessException;
    
    void suspend();
    
    void resume();
}
