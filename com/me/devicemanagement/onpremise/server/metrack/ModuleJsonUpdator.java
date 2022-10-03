package com.me.devicemanagement.onpremise.server.metrack;

import org.json.JSONObject;

class ModuleJsonUpdator implements JsonUpdator
{
    @Override
    public void execute(final JSONObject jsonFromFile, final JSONObject jsonFromServer, final String moduleKey) throws Exception {
        final JSONObject pageJsonObjFromServer = (JSONObject)jsonFromServer.get(moduleKey);
        final JSONObject pageJsonObjFromFile = (JSONObject)jsonFromFile.get(moduleKey);
        JsonUpdateExecutor.addOrUpdateJSON(new PageJsonUpdator(), pageJsonObjFromFile, pageJsonObjFromServer);
    }
}
