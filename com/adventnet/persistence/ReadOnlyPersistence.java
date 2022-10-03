package com.adventnet.persistence;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import java.util.List;

public interface ReadOnlyPersistence
{
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
    
    List getDominantPersonalities(final Row p0) throws DataAccessException;
}
