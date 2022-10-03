package com.adventnet.persistence;

import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.SQLException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Criteria;

public interface Persistence
{
    DataObject add(final DataObject p0) throws DataAccessException;
    
    DataObject constructDataObject() throws DataAccessException;
    
    void delete(final Criteria p0) throws DataAccessException;
    
    void delete(final Row p0) throws DataAccessException;
    
    int delete(final DeleteQuery p0) throws DataAccessException;
    
    DataObject get(final String p0, final Row p1) throws DataAccessException;
    
    DataObject get(final String p0, final List p1) throws DataAccessException;
    
    DataObject get(final String p0, final Criteria p1) throws DataAccessException;
    
    DataObject get(final List p0, final Criteria p1) throws DataAccessException;
    
    DataObject get(final List p0, final List p1) throws DataAccessException;
    
    DataObject get(final List p0, final Row p1) throws DataAccessException;
    
    DataObject get(final List p0, final List p1, final Criteria p2) throws DataAccessException;
    
    DataObject get(final SelectQuery p0) throws DataAccessException;
    
    DataObject getForPersonalities(final List p0, final Criteria p1) throws DataAccessException;
    
    DataObject getForPersonalities(final List p0, final List p1) throws DataAccessException;
    
    DataObject getForPersonalities(final List p0, final Row p1) throws DataAccessException;
    
    DataObject getForPersonalities(final List p0, final List p1, final Row p2) throws DataAccessException;
    
    DataObject getForPersonalities(final List p0, final List p1, final List p2) throws DataAccessException;
    
    DataObject getForPersonalities(final List p0, final List p1, final Criteria p2) throws DataAccessException;
    
    DataObject getForPersonality(final String p0, final Criteria p1) throws DataAccessException;
    
    DataObject getForPersonality(final String p0, final List p1) throws DataAccessException;
    
    DataObject getForPersonality(final String p0, final Row p1) throws DataAccessException;
    
    List getPersonalities(final Row p0) throws DataAccessException;
    
    DataObject getCompleteData(final Row p0) throws DataAccessException;
    
    DataObject getPrimaryKeys(final String p0, final Criteria p1) throws DataAccessException;
    
    boolean isInstanceOf(final Row p0, final List p1) throws DataAccessException;
    
    boolean isInstanceOf(final Row p0, final String p1) throws DataAccessException;
    
    DataObject update(final DataObject p0) throws DataAccessException;
    
    int update(final UpdateQuery p0) throws DataAccessException;
    
    List getDominantPersonalities(final Row p0) throws DataAccessException;
    
    DataObject fillGeneratedValues(final DataObject p0) throws DataAccessException;
    
    void dropTables(final String p0) throws DataAccessException, SQLException;
    
    void createTables(final String p0) throws DataAccessException, SQLException;
    
    void createTables(final List p0) throws DataAccessException, SQLException;
    
    void createTable(final String p0, final TableDefinition p1) throws DataAccessException, SQLException;
    
    void alterTable(final AlterTableQuery p0) throws DataAccessException, SQLException;
    
    void dropTable(final String p0) throws DataAccessException, SQLException;
    
    void addDataType(final DataTypeDefinition p0) throws DataAccessException;
}
