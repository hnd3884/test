package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class MacLoginWindowConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        final JSONArray resultArray = new JSONArray();
        try {
            final Iterator iterator = dataObject.getRows("MacLoginWindow");
            while (iterator.hasNext()) {
                final Row loginWindowRow = iterator.next();
                final Long configDataItemId = (Long)loginWindowRow.get("CONFIG_DATA_ITEM_ID");
                final JSONObject configObject = new JSONObject();
                this.addConfigForRow(loginWindowRow, dataObject, configName, configObject, "MacLoginWindow");
                final Row loginSettingRow = dataObject.getRow("MacLoginWindowSettings", loginWindowRow);
                this.addConfigForRow(loginSettingRow, dataObject, configName, configObject, "MacLoginWindowSettings");
                final Row screenSaverSetting = dataObject.getRow("MacScreenSaverSettings", new Criteria(new Column("MacScreenSaverSettings", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                this.addConfigForRow(screenSaverSetting, dataObject, configName, configObject, "MacScreenSaverSettings");
                resultArray.put((Object)configObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Mac login window config handler", ex);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        return resultArray;
    }
}
