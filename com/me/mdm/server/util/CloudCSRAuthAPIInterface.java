package com.me.mdm.server.util;

import org.json.JSONObject;

public interface CloudCSRAuthAPIInterface
{
    String getAuthKey();
    
    default JSONObject getPublicKeyLatestVersion() {
        return null;
    }
}
