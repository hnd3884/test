package com.me.mdm.server.profiles.config;

import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.IOException;
import org.apache.tika.Tika;
import java.io.File;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.persistence.DataObject;

public class FontPayloadConfigHandler extends DefaultConfigHandler
{
    private static final String TRUE_TYPE_FONT_MIME_TYPE = "application/x-font-ttf";
    private static final String OPEN_TYPE_FONT_MIME_TYPE = "application/x-font-otf";
    private DataObject dataObject;
    protected static List<String> fontMIMETypes;
    
    public FontPayloadConfigHandler() {
        this.dataObject = null;
    }
    
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        final JSONArray resultArray = new JSONArray();
        try {
            if (dataObject.containsTable("CfgDataItemToFontRel")) {
                final JSONArray configProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
                final Iterator fontIterator = dataObject.getRows("CfgDataItemToFontRel");
                while (fontIterator.hasNext()) {
                    final JSONObject configJSON = new JSONObject();
                    final Row cfgFontRel = fontIterator.next();
                    final Long payloadId = (Long)cfgFontRel.get("CONFIG_DATA_ITEM_ID");
                    final Long fontId = (Long)cfgFontRel.get("FONT_ID");
                    final Row fontDetailRow = dataObject.getRow("FontDetails", cfgFontRel);
                    final String fontName = (String)fontDetailRow.get("NAME");
                    configJSON.put(this.getSubConfigProperties(configProperties, "CONFIG_DATA_ITEM_ID").getString("alias"), (Object)payloadId);
                    configJSON.put(this.getSubConfigProperties(configProperties, "FONT_ID").getString("alias"), (Object)fontId);
                    configJSON.put(this.getSubConfigProperties(configProperties, "NAME").getString("alias"), (Object)fontName);
                    resultArray.put((Object)configJSON);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in FontPayloadConfigHandler DOToAPIJSON:", (Throwable)e);
        }
        return resultArray;
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            final Long customerId = serverJSON.optLong("CUSTOMER_ID");
            if (serverJSON.has("FONT_FILE_PATH")) {
                final String filePath = serverJSON.getString("FONT_FILE_PATH");
                final String fontContentType = this.getFontContentType(new File(filePath));
                if (this.isFileSizeGreater(filePath, 1048576L)) {
                    throw new APIHTTPException("PAY0001", new Object[0]);
                }
                serverJSON.put("FONT_TYPE", this.getFontTypeFromContentType(fontContentType));
            }
            else if (serverJSON.has("FONT_ID")) {
                final Long fontId = serverJSON.getLong("FONT_ID");
                final Long collectionId = serverJSON.getLong("COLLECTION_ID");
                if (this.checkFontPayloadConfigured(collectionId, fontId)) {
                    throw new APIHTTPException("COM0010", new Object[0]);
                }
                if (!this.checkFontIdForCustomer(fontId, customerId)) {
                    throw new APIHTTPException("COM0008", new Object[0]);
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
    
    private String getFontContentType(final File fontFile) {
        String contentType = null;
        try {
            final Tika tika = new Tika();
            contentType = tika.detect(fontFile);
        }
        catch (final IOException e) {
            this.logger.log(Level.SEVERE, "Exception in verify font file", e);
        }
        return contentType;
    }
    
    protected int getFontTypeFromContentType(final String contentType) throws APIHTTPException {
        if (contentType.equalsIgnoreCase("application/x-font-ttf")) {
            return 1;
        }
        if (contentType.equalsIgnoreCase("application/x-font-otf")) {
            return 2;
        }
        throw new APIHTTPException("PAY0010", new Object[0]);
    }
    
    private boolean checkFontPayloadConfigured(final Long collectionId, final Long fontId) throws APIHTTPException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataItemToFontRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria fontIdCriteria = new Criteria(new Column("CfgDataItemToFontRel", "FONT_ID"), (Object)fontId, 0);
            selectQuery.setCriteria(collectionCriteria.and(fontIdCriteria));
            final int count = MDMDBUtil.getRecordCount(selectQuery, "CfgDataItemToFontRel", "FONT_ID");
            return count > 0;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkingPayload configured", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean checkFontIdForCustomer(final Long fontId, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("FontDetails"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria webClipCriteria = new Criteria(new Column("FontDetails", "FONT_ID"), (Object)fontId, 0);
        final Criteria customerCriteria = new Criteria(new Column("FontDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(webClipCriteria.and(customerCriteria));
        this.dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return !this.dataObject.isEmpty();
    }
    
    static {
        FontPayloadConfigHandler.fontMIMETypes = new ArrayList<String>() {
            {
                this.add("application/x-font-ttf");
                this.add("application/x-font-otf");
            }
        };
    }
}
