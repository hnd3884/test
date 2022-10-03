package com.me.mdm.server.profiles.config;

import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Collection;
import com.me.mdm.server.common.customdata.CustomDataHandler;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class WebContentConfigHandler extends DefaultConfigHandler
{
    public static final Integer PLUGIN_WCF;
    public static final Integer BUILTIN_WCF;
    public static final String PERMITTED_URL_DETAILS_ALIAS = "PERMITTED_URL_DETAILS_ALIAS";
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName);
            final Map<String, String> urlTableMap = new HashMap() {
                {
                    this.put("URLRestrictionDetails", "url_details");
                    this.put("CfgDataItemToUrl", "url_details");
                    this.put("AndroidEFRPPolicy", "url_details");
                    this.put("AppleWCFPermittedURL", "permitted_urls");
                }
            };
            final Set<String> tableNames = urlTableMap.keySet();
            JSONObject webContentFilter = result.getJSONObject(0);
            final Long policyID = webContentFilter.getLong("payload_id");
            if (!dataObject.isEmpty()) {
                for (final String tableName : tableNames) {
                    if (dataObject.containsTable(tableName)) {
                        webContentFilter.put((String)urlTableMap.get(tableName), (Object)this.getUrlLinksForTable(dataObject, tableName, null));
                    }
                }
                if (dataObject.containsTable("AppleWCFConfig")) {
                    final Row appleWCFRow = dataObject.getFirstRow("AppleWCFConfig");
                    JSONUtil.putAll(webContentFilter, MDMDBUtil.rowToJSON(appleWCFRow, new String[] { "CONFIG_DATA_ITEM_ID" }));
                    final JSONObject property = new JSONObject();
                    property.put("return_secret_field_value", (Object)"PASSWORD");
                    webContentFilter.remove("PASSWORD_ID");
                    webContentFilter.remove("PASSWORD");
                    final Long passwordID = (Long)appleWCFRow.get("PASSWORD_ID");
                    webContentFilter = PayloadSecretFieldsHandler.getInstance().replaceSecretFieldIdInDoToApi(property, passwordID, webContentFilter);
                }
                if (dataObject.containsTable("MacWCFKext")) {
                    final Row macWCFRow = dataObject.getFirstRow("MacWCFKext");
                    JSONUtil.putAll(webContentFilter, MDMDBUtil.rowToJSON(macWCFRow, new String[] { "CONFIG_DATA_ITEM_ID" }));
                }
                if (dataObject.containsTable("MDMConfigCustomData")) {
                    webContentFilter.put("custom_data", (Collection)new CustomDataHandler().getCustomData(policyID, dataObject));
                }
            }
            return result;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToAPIJSON WebContentFilter", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getUrlLinksForTable(final DataObject dataObject, final String tableName, final Criteria criteria) throws DataAccessException, JSONException {
        final JSONArray urlDetailsArray = new JSONArray();
        final Iterator<Row> it = (criteria == null) ? dataObject.getRows(tableName) : dataObject.getRows(tableName, criteria);
        while (it.hasNext()) {
            final Row row = it.next();
            final Long urlDetailsID = (Long)row.get("URL_DETAILS_ID");
            final Row urlRow = dataObject.getRow(this.getURLDetailsAlias(tableName), new Criteria(Column.getColumn(this.getURLDetailsAlias(tableName), "URL_DETAILS_ID"), (Object)urlDetailsID, 0));
            final JSONObject urlDetails = new JSONObject();
            urlDetails.put("url", urlRow.get("URL"));
            urlDetails.put("bookmark_title", urlRow.get("BOOKMARK_TITILE"));
            urlDetails.put("bookmark_path", urlRow.get("BOOKMARK_PATH"));
            urlDetailsArray.put((Object)urlDetails);
        }
        return urlDetailsArray;
    }
    
    private String getURLDetailsAlias(final String tableName) {
        if (tableName.equalsIgnoreCase("AppleWCFPermittedURL")) {
            return "PERMITTED_URL_DETAILS_ALIAS";
        }
        return "URLDetails";
    }
    
    static {
        PLUGIN_WCF = 2;
        BUILTIN_WCF = 1;
    }
}
