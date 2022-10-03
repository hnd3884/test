package com.adventnet.db.persistence.metadata;

import com.adventnet.ds.query.SelectQuery;
import java.util.LinkedHashMap;
import java.net.URL;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.Enumeration;

public interface MetaDataRemote
{
    Enumeration getAllTableDefinitions() throws MetaDataException;
    
    List getTableDefinitions() throws MetaDataException;
    
    TableDefinition getTableDefinitionByName(final String p0) throws MetaDataException;
    
    ForeignKeyDefinition getForeignKeyDefinitionByName(final String p0) throws MetaDataException;
    
    DataDictionary getDataDictionary(final String p0) throws MetaDataException;
    
    List getAllRelatedTableDefinitions(final String p0) throws MetaDataException;
    
    List getForeignKeys(final String p0, final String p1) throws MetaDataException;
    
    String getDefinedTableName(final String p0) throws MetaDataException;
    
    String getDefinedColumnName(final String p0, final String p1) throws MetaDataException;
    
    DataObject getPersonalityConfiguration(final String p0) throws DataAccessException;
    
    List getConstituentTables(final String p0) throws DataAccessException;
    
    List getConstituentTables(final List p0) throws DataAccessException;
    
    List getContainedPersonalities(final String p0) throws DataAccessException;
    
    List getPersonalities(final List p0) throws DataAccessException;
    
    List getDominantPersonalities(final List p0) throws DataAccessException;
    
    String getDominantTableForPersonality(final String p0) throws DataAccessException;
    
    DataObject initializePersonalityConfiguration(final String p0, final URL p1) throws DataAccessException;
    
    void addPersonalities(final String p0, final DataObject p1) throws DataAccessException;
    
    List getPersonalityNames(final String p0) throws DataAccessException;
    
    DataObject getEntirePersonalityConfiguration(final String p0) throws DataAccessException;
    
    void removePersonality(final String p0) throws DataAccessException;
    
    void removePersonalityConfiguration(final String p0) throws DataAccessException;
    
    String getModuleNameOfTable(final String p0) throws MetaDataException;
    
    boolean isIndexed(final String p0) throws DataAccessException;
    
    LinkedHashMap getSelectQueryTemplates(final String p0) throws DataAccessException;
    
    boolean isPartOfPersonality(final String p0) throws DataAccessException;
    
    boolean isFKPartOfPersonality(final String p0) throws DataAccessException;
    
    SelectQuery getSelectQuery(final String p0) throws DataAccessException;
    
    String getDominantTable(final String p0) throws DataAccessException;
    
    DataObject getEntireConfigForDominantTable(final String p0) throws DataAccessException;
}
