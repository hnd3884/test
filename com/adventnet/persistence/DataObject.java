package com.adventnet.persistence;

import com.adventnet.ds.query.SortColumn;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import java.io.Serializable;

public interface DataObject extends Serializable, Cloneable
{
    void addRow(final Row p0) throws DataAccessException;
    
    void updateRow(final Row p0) throws DataAccessException;
    
    void updateBlindly(final Row p0) throws DataAccessException;
    
    void deleteRow(final Row p0) throws DataAccessException;
    
    void deleteRows(final String p0, final Criteria p1) throws DataAccessException;
    
    void deleteRows(final String p0, final Row p1) throws DataAccessException;
    
    void deleteRowIgnoreFK(final Row p0) throws DataAccessException;
    
    void deleteRowsIgnoreFK(final String p0, final Criteria p1) throws DataAccessException;
    
    void deleteRowsIgnoreFK(final String p0, final Row p1) throws DataAccessException;
    
    List getTableNames() throws DataAccessException;
    
    boolean containsTable(final String p0) throws DataAccessException;
    
    void addJoin(final Join p0) throws DataAccessException;
    
    boolean hasJoin(final Join p0);
    
    boolean removeJoin(final Join p0);
    
    Iterator getRows(final String p0) throws DataAccessException;
    
    Iterator getRows(final String p0, final Row p1) throws DataAccessException;
    
    Iterator getRows(final String p0, final Row p1, final Join p2) throws DataAccessException;
    
    Iterator getRows(final String p0, final Criteria p1) throws DataAccessException;
    
    Iterator getRows(final String p0, final Criteria p1, final Join p2) throws DataAccessException;
    
    Row getFirstRow(final String p0) throws DataAccessException;
    
    @Deprecated
    Row getFirstRow(final String p0, final Row p1) throws DataAccessException;
    
    @Deprecated
    Row getFirstRow(final String p0, final Row p1, final Join p2) throws DataAccessException;
    
    Row getRow(final String p0) throws DataAccessException;
    
    Row getRow(final String p0, final Row p1) throws DataAccessException;
    
    Row getRow(final String p0, final Row p1, final Join p2) throws DataAccessException;
    
    Iterator get(final String p0, final String p1) throws DataAccessException;
    
    Iterator get(final String p0, final int p1) throws DataAccessException;
    
    Object getFirstValue(final String p0, final String p1) throws DataAccessException;
    
    Object getFirstValue(final String p0, final int p1) throws DataAccessException;
    
    void set(final String p0, final String p1, final Object p2) throws DataAccessException;
    
    void set(final String p0, final int p1, final Object p2) throws DataAccessException;
    
    void set(final String p0, final String p1, final Object p2, final Criteria p3) throws DataAccessException;
    
    void set(final String p0, final int p1, final Object p2, final Criteria p3) throws DataAccessException;
    
    void set(final String p0, final String p1, final Object p2, final Row p3) throws DataAccessException;
    
    void set(final String p0, final int p1, final Object p2, final Row p3) throws DataAccessException;
    
    DataObject getDataObject(final List p0, final Row p1) throws DataAccessException;
    
    List getOperations();
    
    Object clone();
    
    void validate() throws DataAccessException;
    
    void merge(final DataObject p0) throws DataAccessException;
    
    boolean isEmpty();
    
    boolean isValidated();
    
    void append(final DataObject p0) throws DataAccessException;
    
    Row findRow(final Row p0) throws DataAccessException;
    
    DataObject diff(final DataObject p0) throws DataAccessException;
    
    DataObject diff(final DataObject p0, final boolean p1) throws DataAccessException;
    
    int size(final String p0);
    
    Row getRow(final String p0, final Criteria p1) throws DataAccessException;
    
    Row getRow(final String p0, final Criteria p1, final Join p2) throws DataAccessException;
    
    Object getValue(final String p0, final int p1, final Row p2) throws DataAccessException;
    
    Object getValue(final String p0, final String p1, final Row p2) throws DataAccessException;
    
    Object getValue(final String p0, final int p1, final Row p2, final Join p3) throws DataAccessException;
    
    Object getValue(final String p0, final String p1, final Row p2, final Join p3) throws DataAccessException;
    
    Object getValue(final String p0, final int p1, final Criteria p2) throws DataAccessException;
    
    Object getValue(final String p0, final String p1, final Criteria p2) throws DataAccessException;
    
    void sortRows(final String p0, final SortColumn... p1) throws DataAccessException;
    
    DataObject getDataObject(final String p0, final Criteria p1) throws DataAccessException;
    
    Iterator<Row> getAddedRows(final String p0);
    
    Iterator<Row> getUpdatedRows(final String p0);
    
    Iterator<Row> getDeletedRows(final String p0);
}
