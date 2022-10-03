package com.me.devicemanagement.framework.server.util;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.DBHandler;

public class PersistenceDBHandler implements DBHandler
{
    @Override
    public DataObject add(final DataObject dataObject) throws DataAccessException {
        return SyMUtil.getPersistence().add(dataObject);
    }
    
    @Override
    public DataObject update(final DataObject dataObject) throws DataAccessException {
        return SyMUtil.getPersistence().update(dataObject);
    }
    
    @Override
    public int update(final UpdateQuery updateQuery) throws DataAccessException {
        return SyMUtil.getPersistence().update(updateQuery);
    }
    
    @Override
    public void delete(final Criteria criteria) throws DataAccessException {
        SyMUtil.getPersistence().delete(criteria);
    }
    
    @Override
    public void delete(final Row row) throws DataAccessException {
        SyMUtil.getPersistence().delete(row);
    }
    
    @Override
    public int delete(final DeleteQuery deleteQuery) throws DataAccessException {
        return SyMUtil.getPersistence().delete(deleteQuery);
    }
}
