package com.me.mdm.webclient.formbean;

import com.me.mdm.server.profiles.windows.WindowsCustomProfileHandler;
import com.me.mdm.server.profiles.CustomProfileHandler;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import java.util.HashSet;
import java.util.List;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MDMWindowsCustomProfileFormBean extends MDMCustomProfileFormBean
{
    @Override
    protected JSONObject getCustomProfileJSON(final JSONObject dynaForm) throws JSONException {
        final JSONObject customProfileJSON = new JSONObject();
        final JSONArray syncMLCommands = dynaForm.getJSONArray("syncml_commands");
        customProfileJSON.put("syncml_commands".toUpperCase(), (Object)syncMLCommands);
        return customProfileJSON;
    }
    
    @Override
    protected List<String> getPayloadList(final JSONObject dynaform) throws PayloadException {
        final HashSet<String> payloadSet = new HashSet<String>();
        try {
            final JSONArray syncMLCommands = dynaform.getJSONArray("syncml_commands".toUpperCase());
            for (int i = 0; i < syncMLCommands.length(); ++i) {
                final JSONObject syncMLCommand = syncMLCommands.getJSONObject(i);
                payloadSet.add(String.valueOf(syncMLCommand.get("LOC_URI")));
            }
        }
        catch (final JSONException ex) {
            MDMWindowsCustomProfileFormBean.logger.log(Level.SEVERE, "Exception in parsing profile", (Throwable)ex);
            throw new PayloadException("PAY0002");
        }
        return new ArrayList<String>(payloadSet);
    }
    
    @Override
    protected CustomProfileHandler getCustomProfileHandler() {
        return new WindowsCustomProfileHandler();
    }
}
