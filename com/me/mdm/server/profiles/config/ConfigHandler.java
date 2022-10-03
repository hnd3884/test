package com.me.mdm.server.profiles.config;

import java.util.List;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public interface ConfigHandler
{
    JSONObject apiJSONToServerJSON(final String p0, final JSONObject p1) throws APIHTTPException;
    
    JSONArray DOToAPIJSON(final DataObject p0, final String p1) throws APIHTTPException;
    
    JSONObject DOToAPIJSON(final DataObject p0) throws APIHTTPException;
    
    void validateServerJSON(final JSONObject p0) throws APIHTTPException;
    
    boolean deletePayloadFile(final DataObject p0, final Long p1);
    
    void deletePayloads(final Long p0) throws Exception;
    
    void deletePayloadItems(final Long p0, final JSONObject p1, final Long p2) throws Exception;
    
    boolean deleteSubPayloadsIfPresent(final List p0) throws Exception;
}
