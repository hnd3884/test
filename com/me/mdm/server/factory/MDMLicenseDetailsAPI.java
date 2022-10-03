package com.me.mdm.server.factory;

import org.json.JSONObject;

public interface MDMLicenseDetailsAPI
{
    JSONObject storeLicense(final JSONObject p0) throws Exception;
    
    JSONObject getLicenseDetails(final JSONObject p0) throws Exception;
}
