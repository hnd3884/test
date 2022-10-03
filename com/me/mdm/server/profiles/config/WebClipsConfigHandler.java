package com.me.mdm.server.profiles.config;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class WebClipsConfigHandler extends DefaultConfigHandler
{
    private static final long WEBCLIPSIMAGESIZE = 1048576L;
    private DataObject dataObject;
    
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            final JSONArray result = new JSONArray();
            if (dataObject.containsTable(tableName)) {
                final Iterator<Row> rows = dataObject.getRows(tableName);
                while (rows.hasNext()) {
                    final JSONObject config = new JSONObject();
                    final Row row = rows.next();
                    String columnName = null;
                    Object columnValue = null;
                    JSONObject property = null;
                    final Long webClipPolicyId = (Long)row.get("WEBCLIP_POLICY_ID");
                    final Long configDataItem = (Long)row.get("CONFIG_DATA_ITEM_ID");
                    final Criteria webClipCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipPolicyId, 0);
                    final Row webClipRow = dataObject.getRow("WebClipPolicies", webClipCriteria);
                    final List columns = webClipRow.getColumns();
                    for (int i = 0; i < columns.size(); ++i) {
                        columnName = columns.get(i);
                        property = super.getDetailsForColName(configName, columnName);
                        columnValue = webClipRow.get(columnName);
                        if (property != null && property.has("alias") && columnValue != null && !columnValue.equals("")) {
                            columnValue = this.transformTableValueToApiValue(dataObject, columnName, columnValue, tableName, configName);
                            if (property.has("type") && String.valueOf(property.get("type")).equals("File")) {
                                if (!((String)columnValue).startsWith("/mdm/webclips/")) {
                                    columnValue = "/mdm/webclips/" + columnValue;
                                }
                                config.put(String.valueOf(property.get("alias")), this.constructFileUrl(columnValue));
                            }
                            else {
                                config.put(String.valueOf(property.get("alias")), columnValue);
                            }
                        }
                    }
                    config.put(super.getDetailsForColName(configName, "CONFIG_DATA_ITEM_ID").getString("alias"), (Object)configDataItem);
                    result.put((Object)config);
                }
            }
            return result;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            final String webClips = serverJSON.optString("WEBCLIPS_FILE_UPLOAD", "");
            if (!MDMStringUtils.isEmpty(webClips) && this.isImageSizeGreater(webClips)) {
                throw new APIHTTPException("PAY0001", new Object[0]);
            }
            if (serverJSON.has("WEBCLIP_POLICY_ID")) {
                final Long collectionId = serverJSON.optLong("COLLECTION_ID");
                final Long webClipPoliciesId = serverJSON.optLong("WEBCLIP_POLICY_ID");
                final Long customerId = serverJSON.optLong("CUSTOMER_ID");
                final Long configId = serverJSON.optLong("CONFIG_ID");
                final Long configDataItemId = serverJSON.optLong("CONFIG_DATA_ITEM_ID");
                if (!this.checkWebClipIdForCustomer(webClipPoliciesId, customerId)) {
                    throw new APIHTTPException("COM0008", new Object[0]);
                }
                if (this.checkWebClipPolicyIdConfiguredForCollection(collectionId, webClipPoliciesId, configId, configDataItemId)) {
                    final Row webClipRow = this.dataObject.getRow("WebClipPolicies");
                    final String webClipName = (String)webClipRow.get("WEBCLIP_NAME");
                    throw new APIHTTPException("PAY0013", new Object[] { webClipName });
                }
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean checkWebClipPolicyIdConfiguredForCollection(final Long collectionId, final Long webClipPoliciesId, final Long configId, final Long configDataItemId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "WebClipToConfigRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria configCriteria = new Criteria(new Column("WebClipToConfigRel", "WEBCLIP_POLICY_ID"), (Object)webClipPoliciesId, 0);
        Criteria configIdCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configId, 0);
        if (configDataItemId > 0L) {
            configIdCriteria = configCriteria.and(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 1));
        }
        selectQuery.setCriteria(collectionCriteria.and(configCriteria).and(configIdCriteria));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    private boolean checkWebClipIdForCustomer(final Long webClipPoliciesId, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria webClipCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipPoliciesId, 0);
        final Criteria customerCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(webClipCriteria.and(customerCriteria));
        this.dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return !this.dataObject.isEmpty();
    }
    
    @Override
    protected boolean isImageSizeGreater(final String source) {
        final long uploadedImageSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(source);
        return 1048576L < uploadedImageSize;
    }
}
