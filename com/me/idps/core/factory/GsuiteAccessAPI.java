package com.me.idps.core.factory;

import org.json.JSONObject;

public interface GsuiteAccessAPI
{
    JSONObject getUsersForIdps(final Long p0) throws Exception;
}
