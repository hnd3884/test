package com.me.mdm.server.factory;

import org.json.JSONObject;

public interface BusinessStoreAccessInterface
{
    JSONObject getSaaSAppDetails(final Long p0, final Long p1) throws Exception;
    
    JSONObject getBusinessStoreRedirectURL(final Long p0, final Long p1) throws Exception;
    
    JSONObject configureBusinessStore(final JSONObject p0) throws Exception;
}
