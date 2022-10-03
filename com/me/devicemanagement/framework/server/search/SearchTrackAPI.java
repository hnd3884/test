package com.me.devicemanagement.framework.server.search;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.Properties;

public interface SearchTrackAPI
{
    void updateDetails(final Properties p0) throws Exception;
    
    void updateSearchUsageDays(final String p0) throws Exception;
    
    void incrementSearchTrackData(final String p0) throws JSONException;
    
    String getDataFromDB(final String p0);
    
    JSONObject getDataAsJsonObject() throws Exception;
    
    void updateSelectedResultDetails(final String p0, final String p1, final JSONObject p2) throws JSONException;
    
    String getIdFromAdvSearchUVHPattern(final Long p0);
    
    void recordSearchParamCount(final long p0);
    
    String encryptString(final String p0);
}
