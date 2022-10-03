package com.me.mdm.server.profiles.config;

import java.io.InputStream;
import java.util.Iterator;
import java.io.IOException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class WindowsCustomProfileConfigHandler extends DefaultConfigHandler
{
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            final JSONArray response = super.DOToAPIJSON(dataObject, configName, tableName);
            if (response.length() <= 0) {
                return response;
            }
            final JSONObject responseObject = response.getJSONObject(0);
            final JSONArray syncMLCommands = new JSONArray();
            final Iterator iterator = dataObject.getRows("WindowsCustomProfilesData");
            while (iterator.hasNext()) {
                final Row profileDataRow = iterator.next();
                final JSONObject syncMLCommand = new JSONObject();
                final String locURI = (String)profileDataRow.get("LOC_URI");
                final int actionType = (int)profileDataRow.get("ACTION_TYPE");
                final int dataType = (int)profileDataRow.get("DATA_TYPE");
                final int position = (int)profileDataRow.get("POSITION");
                String data = (String)profileDataRow.get("DATA");
                final String name = (String)profileDataRow.get("NAME");
                final Long customProfileDataID = (Long)profileDataRow.get("CUSTOM_PROFILE_DATA_ID");
                String dataBlob = null;
                final Row extnRow = dataObject.getRow("WindowsCustomProfilesDataExtn", new Criteria(Column.getColumn("WindowsCustomProfilesDataExtn", "CUSTOM_PROFILE_DATA_ID"), (Object)customProfileDataID, 0));
                if (extnRow != null) {
                    final InputStream inputStream = (ByteArrayInputStream)extnRow.get("DATA_BLOB");
                    dataBlob = IOUtils.toString(inputStream);
                }
                syncMLCommand.put("LOC_URI".toLowerCase(), (Object)locURI);
                syncMLCommand.put("ACTION_TYPE".toLowerCase(), actionType);
                syncMLCommand.put("POSITION".toLowerCase(), position);
                syncMLCommand.put("CUSTOM_PROFILE_DATA_ID".toLowerCase(), (Object)customProfileDataID);
                if (dataType != -1) {
                    syncMLCommand.put("DATA_TYPE".toLowerCase(), dataType);
                }
                if (!MDMStringUtils.isEmpty(data) || !MDMStringUtils.isEmpty(dataBlob)) {
                    if (!MDMStringUtils.isEmpty(dataBlob)) {
                        data = dataBlob;
                        syncMLCommand.put("is_blob", true);
                    }
                    data = new String(Base64.decodeBase64(data));
                    syncMLCommand.put("DATA".toLowerCase(), (Object)data);
                }
                if (!MDMStringUtils.isEmpty(name)) {
                    syncMLCommand.put("NAME".toLowerCase(), (Object)name);
                }
                syncMLCommands.put((Object)syncMLCommand);
            }
            responseObject.put("syncml_commands", (Object)syncMLCommands);
            return response;
        }
        catch (final DataAccessException | JSONException | IOException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToAPIJSON WindowsCustomProfileHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            final JSONArray syncMLCommands = serverJSON.getJSONArray("syncml_commands");
            for (int it = 0; it < syncMLCommands.length(); ++it) {
                final JSONObject syncMLCommand = syncMLCommands.getJSONObject(it);
                final int actionType = syncMLCommand.getInt("ACTION_TYPE".toLowerCase());
                if (!syncMLCommand.optBoolean("is_blob", (boolean)Boolean.FALSE) || syncMLCommand.optBoolean("is_modified", (boolean)Boolean.FALSE)) {
                    if ((actionType == 0 || actionType == 1) && ((!syncMLCommand.has("DATA".toLowerCase()) && !syncMLCommand.has("data_file_id")) || !syncMLCommand.has("DATA_TYPE".toLowerCase()))) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                    if ((syncMLCommand.has("DATA".toLowerCase()) && !syncMLCommand.has("DATA_TYPE".toLowerCase())) || (!syncMLCommand.has("DATA".toLowerCase()) && !syncMLCommand.has("data_file_id") && syncMLCommand.has("DATA_TYPE".toLowerCase()))) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                }
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception occured in validateServerJson WindowsCustomProfileHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
