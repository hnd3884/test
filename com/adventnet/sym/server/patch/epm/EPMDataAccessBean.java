package com.adventnet.sym.server.patch.epm;

import com.adventnet.persistence.Row;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;

public class EPMDataAccessBean implements EPMDataAccess
{
    @Override
    public DataObject add(final DataObject dataObject) throws SyMException {
        throw new SyMException(1002, "Cannot add data to EnterprisePatchManager tables", (Throwable)null);
    }
    
    @Override
    public DataObject constructDataObject() throws SyMException {
        throw new SyMException(1002, "Cannot allow to construct DataObject on EnterprisePatchManager tables", (Throwable)null);
    }
    
    @Override
    public DataObject get(final SelectQuery sq) throws DataAccessException {
        return DataAccess.get(sq);
    }
    
    @Override
    public DataObject get(final String tableName, final Criteria condition) throws DataAccessException {
        return DataAccess.get(tableName, condition);
    }
    
    @Override
    public RelationalAPI getRelationalAPIInstance() {
        return RelationalAPI.getInstance();
    }
    
    @Override
    public void delete(final Row row) throws SyMException {
        throw new SyMException(1002, "Cannot delete row from EnterprisePatchManager tables", (Throwable)null);
    }
    
    @Override
    public void delete(final Criteria condition) throws DataAccessException {
        DataAccess.delete(condition);
    }
    
    @Override
    public void delete(final String tableName, final Criteria criteria) throws SyMException {
        throw new SyMException(1002, "Cannot delete row from EnterprisePatchManager tables", (Throwable)null);
    }
    
    @Override
    public DataObject update(final DataObject dataObject) throws SyMException {
        throw new SyMException(1002, "Cannot update row on EnterprisePatchManager tables", (Throwable)null);
    }
}
