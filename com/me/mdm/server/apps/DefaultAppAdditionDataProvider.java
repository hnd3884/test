package com.me.mdm.server.apps;

import org.json.JSONObject;

public class DefaultAppAdditionDataProvider extends BaseAppAdditionDataProvider
{
    @Override
    public JSONObject modifyAppAdditionData(final JSONObject appAdditionDetails) throws Exception {
        return appAdditionDetails;
    }
}
