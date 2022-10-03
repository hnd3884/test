package com.adventnet.persistence.personality.internal;

import java.util.Map;
import com.adventnet.ds.query.SelectQuery;
import java.util.LinkedHashMap;
import com.adventnet.persistence.DataObject;
import java.net.URL;
import com.adventnet.persistence.DataAccessException;
import java.util.List;

public interface PCInfo extends Cloneable
{
    List<String> getConstituentTables(final String p0) throws DataAccessException;
    
    List<String> getConstituentTables(final List<String> p0) throws DataAccessException;
    
    List<String> getContainedPersonalities(final String p0) throws DataAccessException;
    
    List<String> getPersonalities(final List<String> p0) throws DataAccessException;
    
    List<String> getDominantPersonalities(final List<String> p0) throws DataAccessException;
    
    String getDominantTableForPersonality(final String p0) throws DataAccessException;
    
    DataObject initializePersonalityConfiguration(final String p0, final URL p1) throws DataAccessException;
    
    void addPersonalities(final String p0, final DataObject p1) throws DataAccessException;
    
    List<String> getPersonalityNames(final String p0) throws DataAccessException;
    
    boolean removePersonality(final String p0, final boolean p1) throws DataAccessException;
    
    void removePersonalityConfiguration(final String p0, final boolean p1) throws DataAccessException;
    
    boolean isIndexed(final String p0) throws DataAccessException;
    
    LinkedHashMap getSelectQueryTemplates(final String p0) throws DataAccessException;
    
    boolean isPartOfPersonality(final String p0) throws DataAccessException;
    
    SelectQuery getSelectQuery(final String p0) throws DataAccessException;
    
    String getDominantTable(final String p0) throws DataAccessException;
    
    boolean isFKPartOfPersonality(final String p0) throws DataAccessException;
    
    boolean isPartOfIndexedPersonality(final String p0) throws DataAccessException;
    
    List<String> getMandatoryConstituentTables(final String p0) throws DataAccessException;
    
    Map<String, String> getFKsForConstituentTables(final String p0) throws DataAccessException;
    
    List<String> getAllPersonalities(final String p0) throws DataAccessException;
    
    Object clone();
}
