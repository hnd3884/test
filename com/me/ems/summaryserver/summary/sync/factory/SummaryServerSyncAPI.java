package com.me.ems.summaryserver.summary.sync.factory;

import com.adventnet.iam.security.UploadedFileItem;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Map;
import java.io.IOException;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.sql.SQLException;
import com.me.ems.summaryserver.common.sync.SyncException;

public interface SummaryServerSyncAPI
{
    void processCSVData(final SyncAPI p0, final Long p1, final long p2, final String p3, final String p4) throws SyncException;
    
    void processJSONDeletionData(final SyncAPI p0, final Long p1, final long p2, final String p3) throws SyncException;
    
    void truncateTable(final String p0) throws SQLException;
    
    void bulkLoadStagingTable(final String p0, final String p1) throws Exception;
    
    void bulkLoadStagingTable(final String p0, final String[] p1, final List<String[]> p2) throws Exception;
    
    void syncSSTableData(final String p0, final Criteria p1, final Long p2);
    
    void writeFile(final String p0, final String p1, final boolean p2) throws IOException;
    
    Map<String, String> getUserDomainDetails(final boolean p0);
    
    String getProbeServerBaseURL(final Long p0);
    
    URLConnection createProbeServerConnection(final Long p0, final Properties p1, final boolean p2, final boolean p3) throws Exception;
    
    String pushMultiPartToProbe(final Long p0, final String p1, final String p2, final Map<String, Object> p3, final Map<String, Object> p4, final Map<String, UploadedFileItem> p5);
}
