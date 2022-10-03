package com.me.mdm.server.factory;

import org.json.JSONException;
import org.json.JSONObject;

public interface MDMWinAppExtractorAPI
{
    JSONObject getMSIProperties(final String p0) throws JSONException;
}
