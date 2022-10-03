package com.me.ems.summaryserver.summary.sync.factory;

import com.adventnet.ds.query.DeleteQuery;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.HashMap;
import java.util.List;
import com.me.ems.summaryserver.common.sync.SyncException;

public interface SyncAPI
{
    void parseCSVFile(final String p0) throws SyncException;
    
    List<String[]> getCsvData() throws SyncException;
    
    String[] getCsvHeaders();
    
    void validateData(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException;
    
    void resolveConflicts(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException;
    
    void resolveConflictValues(final Long p0, final String p1, final String[] p2, final List<String[]> p3, final HashMap<String, HashMap> p4) throws Exception;
    
    void populateDataFromCSVFile(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException;
    
    void postProcessConflictData(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws Exception;
    
    void preProcessData(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException;
    
    void postProcessData(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException, MetaDataException, DataAccessException;
    
    JSONObject getDeletionJSON(final String p0) throws SyncException;
    
    void validateJSONData(final JSONObject p0) throws SyncException;
    
    HashMap<String, HashMap> getDeleteDataFromJSON(final JSONObject p0) throws SyncException;
    
    HashMap resolveDeletionConflicts(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3) throws SyncException, DataAccessException;
    
    List<DeleteQuery> getDeleteQuery(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3) throws SyncException;
    
    void postProcessConflictDeletionData(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3, final HashMap<String, ArrayList> p4) throws SyncException, DataAccessException;
    
    void performDeletion(final ArrayList<DeleteQuery> p0) throws SyncException;
    
    void processDataForProbeMappingTable(final String p0, final String p1, final Long p2, final String[] p3, final List<String[]> p4) throws MetaDataException, DataAccessException;
    
    void preProcessDeleteData(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3) throws SyncException;
    
    void postProcessDeleteData(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3) throws SyncException;
}
