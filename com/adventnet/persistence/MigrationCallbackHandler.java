package com.adventnet.persistence;

import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Criteria;
import java.util.List;

public interface MigrationCallbackHandler
{
    void handleMigration(final DataObject p0, final List<String> p1);
    
    void handleMigrationForDelete(final Criteria p0);
    
    void handleMigrationForDelete(final DeleteQuery p0);
    
    void handleMigrationForUpdate(final UpdateQuery p0);
    
    DataObject getDataObject(final Criteria p0) throws DataAccessException;
    
    DataObject getDataObject(final UpdateQuery p0) throws DataAccessException;
    
    void handleMigrationForTemplateInstanceCreation(final String p0, final TableDefinition p1);
}
