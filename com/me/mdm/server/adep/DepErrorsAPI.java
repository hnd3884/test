package com.me.mdm.server.adep;

import org.json.JSONObject;

public interface DepErrorsAPI
{
    int getErrorCause(final int p0);
    
    JSONObject getErrorRemarkArgs(final int p0);
}
