package com.adventnet.persistence;

import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import java.util.List;

public abstract class AbstractCallbackHandler implements MigrationCallbackHandler
{
    @Override
    public abstract void handleMigration(final DataObject p0, final List<String> p1);
    
    @Override
    public abstract void handleMigrationForDelete(final Criteria p0);
    
    @Override
    public abstract void handleMigrationForUpdate(final UpdateQuery p0);
    
    @Override
    public abstract void handleMigrationForDelete(final DeleteQuery p0);
    
    @Override
    public DataObject getDataObject(final UpdateQuery updateQuery) throws DataAccessException {
        final DataObject updatedDO = DataAccess.get(updateQuery.getTableName(), updateQuery.getCriteria());
        final Map<Column, Object> updateValues = updateQuery.getUpdateColumns();
        for (final Column column : updateValues.keySet()) {
            final Object updateValue = updateValues.get(column);
            updatedDO.set(updateQuery.getTableName(), column.getColumnName(), updateValue);
        }
        ((WritableDataObject)updatedDO).makeImmutable();
        return updatedDO;
    }
    
    @Override
    public DataObject getDataObject(final Criteria deleteCriteria) throws DataAccessException {
        final String tableName = deleteCriteria.getColumn().getTableAlias();
        final DataObject deletedDO = DataAccess.get(tableName, deleteCriteria);
        deletedDO.deleteRows(tableName, deleteCriteria);
        ((WritableDataObject)deletedDO).makeImmutable();
        return deletedDO;
    }
    
    @Override
    public abstract void handleMigrationForTemplateInstanceCreation(final String p0, final TableDefinition p1);
}
