package com.me.devicemanagement.framework.server.factory;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;

public interface DBHandler
{
    DataObject add(final DataObject p0) throws DataAccessException;
    
    DataObject update(final DataObject p0) throws DataAccessException;
    
    int update(final UpdateQuery p0) throws DataAccessException;
    
    void delete(final Criteria p0) throws DataAccessException;
    
    void delete(final Row p0) throws DataAccessException;
    
    int delete(final DeleteQuery p0) throws DataAccessException;
}
