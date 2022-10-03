package com.adventnet.persistence;

import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.SQLException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import java.rmi.RemoteException;

public interface PersistenceRemote
{
    DataObject add(final DataObject p0) throws DataAccessException, RemoteException;
    
    DataObject constructDataObject() throws DataAccessException, RemoteException;
    
    void delete(final Criteria p0) throws DataAccessException, RemoteException;
    
    void delete(final Row p0) throws DataAccessException, RemoteException;
    
    DataObject get(final String p0, final Row p1) throws DataAccessException, RemoteException;
    
    DataObject get(final String p0, final List p1) throws DataAccessException, RemoteException;
    
    DataObject get(final String p0, final Criteria p1) throws DataAccessException, RemoteException;
    
    DataObject get(final List p0, final Criteria p1) throws DataAccessException, RemoteException;
    
    DataObject get(final List p0, final List p1) throws DataAccessException, RemoteException;
    
    DataObject get(final List p0, final Row p1) throws DataAccessException, RemoteException;
    
    DataObject get(final List p0, final List p1, final Criteria p2) throws DataAccessException, RemoteException;
    
    DataObject get(final SelectQuery p0) throws DataAccessException, RemoteException;
    
    DataObject getForPersonalities(final List p0, final Criteria p1) throws DataAccessException, RemoteException;
    
    DataObject getForPersonalities(final List p0, final List p1) throws DataAccessException, RemoteException;
    
    DataObject getForPersonalities(final List p0, final Row p1) throws DataAccessException, RemoteException;
    
    DataObject getForPersonalities(final List p0, final List p1, final Row p2) throws DataAccessException, RemoteException;
    
    DataObject getForPersonalities(final List p0, final List p1, final List p2) throws DataAccessException, RemoteException;
    
    DataObject getForPersonalities(final List p0, final List p1, final Criteria p2) throws DataAccessException, RemoteException;
    
    DataObject getForPersonality(final String p0, final Criteria p1) throws DataAccessException, RemoteException;
    
    DataObject getForPersonality(final String p0, final List p1) throws DataAccessException, RemoteException;
    
    DataObject getForPersonality(final String p0, final Row p1) throws DataAccessException, RemoteException;
    
    List getPersonalities(final Row p0) throws DataAccessException, RemoteException;
    
    DataObject getCompleteData(final Row p0) throws DataAccessException, RemoteException;
    
    DataObject getPrimaryKeys(final String p0, final Criteria p1) throws DataAccessException, RemoteException;
    
    boolean isInstanceOf(final Row p0, final List p1) throws DataAccessException, RemoteException;
    
    boolean isInstanceOf(final Row p0, final String p1) throws DataAccessException, RemoteException;
    
    DataObject update(final DataObject p0) throws DataAccessException, RemoteException;
    
    void update(final UpdateQuery p0) throws DataAccessException, RemoteException;
    
    List getDominantPersonalities(final Row p0) throws DataAccessException, RemoteException;
    
    DataObject fillGeneratedValues(final DataObject p0) throws DataAccessException, RemoteException;
    
    void dropTables(final String p0) throws DataAccessException, SQLException, RemoteException;
    
    void createTables(final String p0) throws DataAccessException, SQLException, RemoteException;
    
    void createTables(final List p0) throws DataAccessException, SQLException, RemoteException;
    
    void createTable(final String p0, final TableDefinition p1) throws DataAccessException, SQLException, RemoteException;
    
    void alterTable(final AlterTableQuery p0) throws DataAccessException, SQLException, RemoteException;
    
    void dropTable(final String p0) throws DataAccessException, SQLException, RemoteException;
}
