package com.me.ems.summaryserver.probe.sync.factory;

import com.adventnet.iam.security.UploadedFileItem;
import java.util.Map;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.net.HttpURLConnection;

public interface ProbeSyncAPI
{
    HttpURLConnection processRequest(final String p0, final String p1, final String p2);
    
    URLConnection createSummaryServerConnection(final Properties p0) throws Exception;
    
    URLConnection createSummaryServerConnection(final URL p0, final String p1, final String p2, final String p3, final boolean p4, final boolean p5) throws Exception;
    
    Map<String, String> getUserDomainDetails(final boolean p0);
    
    String pushMultiPartToSummaryServer(final String p0, final String p1, final Map<String, Object> p2, final Map<String, Object> p3, final Map<String, UploadedFileItem> p4) throws Exception;
}
