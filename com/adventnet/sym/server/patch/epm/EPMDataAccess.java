package com.adventnet.sym.server.patch.epm;

import com.adventnet.persistence.Row;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;

public interface EPMDataAccess
{
    DataObject add(final DataObject p0) throws SyMException;
    
    DataObject constructDataObject() throws SyMException;
    
    DataObject get(final SelectQuery p0) throws DataAccessException;
    
    DataObject get(final String p0, final Criteria p1) throws DataAccessException;
    
    RelationalAPI getRelationalAPIInstance();
    
    void delete(final Row p0) throws SyMException;
    
    void delete(final Criteria p0) throws DataAccessException;
    
    void delete(final String p0, final Criteria p1) throws SyMException;
    
    DataObject update(final DataObject p0) throws SyMException;
}
