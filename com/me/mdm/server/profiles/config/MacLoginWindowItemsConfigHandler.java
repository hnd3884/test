package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class MacLoginWindowItemsConfigHandler extends DefaultConfigHandler
{
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        try {
            if (!dataObject.isEmpty() && configJSON.has("payload_id")) {
                final JSONArray configProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
                final JSONObject loginItemObject = this.getSubConfigProperties(configProperties, "MacLoginWindowItems");
                final JSONArray macLoginItemJSON = loginItemObject.getJSONArray("properties");
                final long configDataItemId = configJSON.getLong("payload_id");
                final Iterator iterator = dataObject.getRows("MacLoginWindowItems", new Criteria(new Column("MacLoginWindowItems", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                final JSONArray loginItemArray = new JSONArray();
                while (iterator.hasNext()) {
                    final Row loginItemRow = iterator.next();
                    final JSONObject loginItemJSON = new JSONObject();
                    for (int i = 0; i < macLoginItemJSON.length(); ++i) {
                        final JSONObject eachObject = macLoginItemJSON.getJSONObject(i);
                        final String columnName = eachObject.getString("name");
                        final Object columnValue = loginItemRow.get(columnName);
                        loginItemJSON.put(eachObject.getString("alias"), columnValue);
                    }
                    loginItemArray.put((Object)loginItemJSON);
                }
                configJSON.put(loginItemObject.getString("alias"), (Object)loginItemArray);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in check and add inner json", e);
            throw e;
        }
    }
}
