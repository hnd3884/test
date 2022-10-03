package com.adventnet.model.table;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.model.Model;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;

public interface CVTableModel extends TableNavigatorModel, Model
{
    public static final int TABLE_LIST_COLUMN = -999;
    
    Column[] getColumns();
    
    void setColumns(final Column[] p0);
    
    Object getRow(final long p0);
    
    DataObject getDataObjectForRow(final int[] p0) throws DataAccessException;
    
    int getColumnSQLClass(final int p0);
    
    String getColSQLClass(final int p0);
}
