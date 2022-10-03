package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.adventnet.persistence.DataObject;

public class CreatePersistenceRequest extends PersistenceRequest
{
    DataObject dataObject;
    
    public CreatePersistenceRequest(final DataObject dataObject) {
        this.dataObject = dataObject;
    }
    
    @Override
    public int getOperationType() {
        return 600;
    }
    
    public DataObject getDataObject() {
        return this.dataObject;
    }
    
    @Override
    public Set<String> getTableList() throws DataAccessException {
        return new HashSet<String>(this.dataObject.getTableNames());
    }
}
