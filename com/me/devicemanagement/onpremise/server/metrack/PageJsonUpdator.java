package com.me.devicemanagement.onpremise.server.metrack;

import org.json.JSONObject;

class PageJsonUpdator implements JsonUpdator
{
    @Override
    public void execute(final JSONObject jsonFromFile, final JSONObject jsonFromServer, final String pageKey) throws Exception {
        final int existingClickCount = jsonFromFile.getInt(pageKey);
        final int currentClickCount = jsonFromServer.getInt(pageKey);
        jsonFromFile.put(pageKey, existingClickCount + currentClickCount);
    }
}
