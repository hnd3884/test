package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class ManagedDomainConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        JSONArray result = null;
        try {
            final String tableName = ProfileConfigurationUtil.getInstance().getTableName(configName);
            final int configID = ProfileConfigurationUtil.getInstance().getConfigID(configName);
            if (dataObject.containsTable("ConfigData")) {
                final Row row = dataObject.getFirstRow("ConfigData");
                final int configIDT = Integer.valueOf(String.valueOf(row.get("CONFIG_ID")));
                if (configID != configIDT) {
                    return null;
                }
            }
            if (dataObject.containsTable(tableName)) {
                final Iterator<Row> rows = dataObject.getRows(tableName);
                if (rows.hasNext()) {
                    final Row row2 = rows.next();
                    result = new JSONArray();
                    final JSONObject obj = new JSONObject();
                    obj.put("payload_id", row2.get("CONFIG_DATA_ITEM_ID"));
                    obj.put("payload_type", 517);
                    result.put((Object)obj);
                }
            }
            if (result != null) {
                for (int i = 0; i < result.length(); ++i) {
                    try {
                        final JSONObject obj2 = result.getJSONObject(i);
                        if (!dataObject.isEmpty()) {
                            final JSONArray urlDetailsArray = new JSONArray();
                            final Iterator it = dataObject.getRows("ManagedWebDomainURLDetails");
                            while (it.hasNext()) {
                                final JSONObject urlDetails = new JSONObject();
                                final Row urlDetailsRow = it.next();
                                urlDetails.put("url", urlDetailsRow.get("URL"));
                                urlDetailsArray.put((Object)urlDetails);
                            }
                            obj2.put("url_details", (Object)urlDetailsArray);
                            this.logger.log(Level.INFO, "obj.toString() {0}", obj2.toString());
                        }
                    }
                    catch (final Exception exp) {
                        Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in managed domain", exp);
                        throw new APIHTTPException("COM0004", new Object[0]);
                    }
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in managed domain", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
}
