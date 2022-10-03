package com.me.devicemanagement.onpremise.server.metrack;

import org.json.JSONObject;

interface JsonUpdator
{
    void execute(final JSONObject p0, final JSONObject p1, final String p2) throws Exception;
}
