package com.me.ems.summaryserver.probe.sync.factory;

import com.me.ems.summaryserver.probe.sync.SyncData;
import java.util.List;
import java.util.Map;

public interface SyncAPI
{
    boolean syncPreChecks();
    
    boolean performPreChecks(final long p0) throws Exception;
    
    Map<String, Object> getSyncParameters(final long p0);
    
    int getLastSyncStatus(final long p0, final Map<String, Object> p1);
    
    List<SyncData> getAndWriteAddOrUpdateData(final long p0, final Map<String, Object> p1) throws Exception;
    
    List<SyncData> getAndWriteDeleteData(final long p0, final Map<String, Object> p1) throws Exception;
    
    Map<String, Boolean> postSyncDataToSummaryServer(final long p0, final Map<String, Object> p1, final List<SyncData> p2) throws Exception;
    
    boolean addAuditEntryAndUpdateSyncStatus(final long p0, final Map<String, Object> p1, final Map<String, Boolean> p2);
    
    boolean performPostChecks(final long p0, final boolean p1) throws Exception;
    
    boolean syncPostChecks(final long p0, final Map<String, Object> p1, final boolean p2);
}
