package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.persistence.WritableDataObject;
import java.util.Set;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataObject;

public class ModifyPersistenceRequest extends PersistenceRequest
{
    DataObject dataObject;
    UpdateQuery updateQuery;
    
    public ModifyPersistenceRequest(final DataObject dataObject) {
        this.dataObject = dataObject;
    }
    
    public ModifyPersistenceRequest(final DataObject dataObject, final UpdateQuery updateQuery) {
        this.dataObject = dataObject;
        this.updateQuery = updateQuery;
    }
    
    @Override
    public int getOperationType() {
        return 601;
    }
    
    public DataObject getDataObject() {
        return this.dataObject;
    }
    
    public UpdateQuery getUpdateQuery() {
        return this.updateQuery;
    }
    
    @Override
    public Set<String> getTableList() throws DataAccessException {
        if (this.dataObject != null) {
            return new HashSet<String>(((WritableDataObject)this.dataObject).getModifiedTables());
        }
        if (this.updateQuery != null) {
            final Set<String> table = new HashSet<String>(1);
            table.add(this.updateQuery.getTableName());
            return table;
        }
        return null;
    }
}
