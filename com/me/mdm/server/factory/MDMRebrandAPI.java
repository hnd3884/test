package com.me.mdm.server.factory;

import org.json.JSONObject;
import com.me.mdm.api.APIRequest;

public interface MDMRebrandAPI
{
    JSONObject getRebrandSettings(final APIRequest p0) throws Exception;
    
    JSONObject saveRebrandSettings(final APIRequest p0) throws Exception;
}
