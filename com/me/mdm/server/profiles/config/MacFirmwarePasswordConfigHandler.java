package com.me.mdm.server.profiles.config;

import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class MacFirmwarePasswordConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, JSONObject apiJSON) throws APIHTTPException {
        apiJSON = super.apiJSONToServerJSON(configName, apiJSON);
        try {
            Long newPassword = null;
            Long oldPassword = null;
            newPassword = JSONUtil.optLongForUVH(apiJSON, "FIRMWARE_NEW_PASSWORD", Long.valueOf(-1L));
            oldPassword = JSONUtil.optLongForUVH(apiJSON, "FIRMWARE_OLD_PASSWORD", Long.valueOf(-1L));
            if (newPassword == -1L) {
                apiJSON.put("FIRMWARE_NEW_PASSWORD", (Object)null);
            }
            if (oldPassword == -1L) {
                apiJSON.put("FIRMWARE_OLD_PASSWORD", (Object)null);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in MacFirmwarePasswordConfigHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return apiJSON;
    }
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        JSONArray finalJSONArray = null;
        finalJSONArray = super.DOToAPIJSON(dataObject, configName, "MacFirmwarePolicy");
        return finalJSONArray;
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        super.validateServerJSON(serverJSON);
        final int type = serverJSON.optInt("FIRMWARE_PASSWORD_TYPE", -1);
        if (type == -1) {
            throw new APIHTTPException("MACFIRMWARE003", new Object[0]);
        }
        Long newPasword = null;
        if (type == 1) {
            newPasword = JSONUtil.optLongForUVH(serverJSON, "FIRMWARE_NEW_PASSWORD", Long.valueOf(-1L));
            if (newPasword == -1L) {
                throw new APIHTTPException("MACFIRMWARE001", new Object[0]);
            }
        }
    }
}
