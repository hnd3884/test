package com.me.mdm.server.profiles.config;

import java.util.List;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.security.passcode.MDMManagedPasswordHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class ChromeEthernetConfigHandler extends DefaultConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        JSONArray result = null;
        try {
            if (!dataObject.isEmpty()) {
                result = super.DOToAPIJSON(dataObject, configName, tableName);
                final Iterator subIterator = dataObject.getRows("PayloadWifiEnterprise");
                final JSONObject subConfig = new JSONObject();
                while (subIterator.hasNext()) {
                    String columnName = null;
                    Object columnValue = null;
                    final Row subPayloadRow = subIterator.next();
                    final List columns = subPayloadRow.getColumns();
                    for (int i = 0; i < columns.size(); ++i) {
                        columnName = columns.get(i);
                        columnValue = subPayloadRow.get(columnName);
                        if (columnName != null && columnValue != null && !columnName.equals("CONFIG_DATA_ITEM_ID") && !columnName.equals("PASSWORD")) {
                            if (columnName.equals("PASSWORD_ID")) {
                                final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
                                final String password = MDMManagedPasswordHandler.getMDMManagedPassword((Long)columnValue, customerId);
                                subConfig.put("PASSWORD".toLowerCase(), (Object)password);
                            }
                            else {
                                subConfig.put(columnName.toLowerCase(), columnValue);
                            }
                        }
                    }
                }
                result.getJSONObject(0).put("ethernet_enterprise", (Object)subConfig);
                final JSONObject proxyConfig = new JSONObject();
                final Iterator proxyItr = dataObject.getRows("PayloadProxyConfig");
                while (proxyItr.hasNext()) {
                    String columnName2 = null;
                    Object columnValue2 = null;
                    final Row proxyRow = proxyItr.next();
                    final List columns2 = proxyRow.getColumns();
                    for (int j = 0; j < columns2.size(); ++j) {
                        columnName2 = columns2.get(j);
                        columnValue2 = proxyRow.get(columnName2);
                        if (columnName2 != null && columnValue2 != null && !columnName2.equals("CONFIG_DATA_ITEM_ID")) {
                            proxyConfig.put(columnName2.toLowerCase(), columnValue2);
                        }
                    }
                }
                result.getJSONObject(0).put("proxy_settings", (Object)proxyConfig);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in Chrome Ethernet Config", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
}
