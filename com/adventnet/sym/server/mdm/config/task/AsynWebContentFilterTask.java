package com.adventnet.sym.server.mdm.config.task;

import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class AsynWebContentFilterTask extends CSVTask
{
    private Logger logger;
    
    public AsynWebContentFilterTask() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    protected void performOperation(final JSONObject json) throws Exception {
        this.logger.log(Level.INFO, "Async bulk url entry started");
        final ProfileFacade profileFacade = new ProfileFacade();
        final JSONObject validatedURLS = this.getURLDetails();
        final Long profileID = (Long)json.get((Object)"profile_id");
        final Long collectionID = (Long)json.get((Object)"collection_id");
        final org.json.JSONObject jsonObject = new org.json.JSONObject();
        jsonObject.put("url_details", validatedURLS.get((Object)"urlDetails"));
        jsonObject.put("url_filter_type", (boolean)json.get((Object)"url_filter_type"));
        jsonObject.put("create_bookmarks", (boolean)json.get((Object)"create_bookmarks"));
        jsonObject.put("enable_auto_filter", (boolean)json.get((Object)"enable_auto_filter"));
        jsonObject.put("upload_type", 2);
        jsonObject.put("PROFILE_COLLECTION_STATUS", (int)DBUtil.getValueFromDB("CollectionStatus", "COLLECTION_ID", (Object)collectionID, "PROFILE_COLLECTION_STATUS"));
        jsonObject.put("PLATFORM_TYPE", new ProfileUtil().getPlatformType(profileID));
        jsonObject.put("payload_name", json.get((Object)"payload_name"));
        jsonObject.put("malicious_content_filter", (boolean)json.get((Object)"malicious_content_filter"));
        if (json.containsKey((Object)"permitted_urls")) {
            jsonObject.put("permitted_urls", (Object)json.get((Object)"permitted_urls"));
        }
        final Long configDataItemID = (Long)json.get((Object)"config_data_item_id");
        if (configDataItemID > 0L) {
            jsonObject.put("CONFIG_DATA_ITEM_ID", (long)json.get((Object)"config_data_item_id"));
        }
        profileFacade.addOrModifyConfigDataItem(jsonObject, collectionID, this.customerID, this.userID);
        this.setFailureCount(this.customerID);
    }
    
    private JSONObject getURLDetails() throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray urlDetails = new JSONArray();
        final JSONArray invalidEntries = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("URLDetailsImportInfo"));
        selectQuery.addSelectColumn(Column.getColumn("URLDetailsImportInfo", "URL"));
        selectQuery.addSelectColumn(Column.getColumn("URLDetailsImportInfo", "URL_DETAILS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("URLDetailsImportInfo", "BOOKMARK_TITLE"));
        selectQuery.addSelectColumn(Column.getColumn("URLDetailsImportInfo", "BOOKMARK_PATH"));
        selectQuery.addSelectColumn(Column.getColumn("URLDetailsImportInfo", "ERROR_REMARKS"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("URLDetailsImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0));
        try {
            final DataObject dataObject = DataAccess.get(selectQuery);
            int processed = 0;
            final Iterator<Row> rows = dataObject.getRows("URLDetailsImportInfo");
            while (rows.hasNext()) {
                ++processed;
                final Row row = rows.next();
                final String url = (String)row.get("URL");
                String path = (String)row.get("BOOKMARK_PATH");
                String title = (String)row.get("BOOKMARK_TITLE");
                final String errorText = this.validateEntries(url, path, title);
                if (errorText == null) {
                    final JSONObject json = new JSONObject();
                    json.put((Object)"url", (Object)url);
                    if (title == null) {
                        title = "";
                    }
                    json.put((Object)"bookmark_title", (Object)title);
                    if (path == null) {
                        path = "";
                    }
                    json.put((Object)"bookmark_path", (Object)path);
                    urlDetails.put((Map)json);
                }
                else {
                    row.set("ERROR_REMARKS", (Object)errorText);
                    dataObject.updateRow(row);
                }
            }
            CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("WebContentFilter"), String.valueOf(processed), (long)this.customerID);
            jsonObject.put((Object)"urlDetails", (Object)urlDetails);
            DataAccess.update(dataObject);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "error temp table entry validation", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "error temp table entry validation", e2);
            throw e2;
        }
        return jsonObject;
    }
    
    private String validateEntries(final String url, final String path, final String title) {
        final Pattern urlPattern = Pattern.compile("^([a-zA-Z0-9\\s\\+?!,()@&amp;%.\\-:#_*\\./\\\\=]+)$");
        final Pattern safeStringPattern = Pattern.compile("^[a-zA-Z0-9\\s\\+?!,()@&amp;%.\\-:_*\\./\\\\=\\P{InBasicLatin}]+$");
        if (url == null || !urlPattern.matcher(url).matches()) {
            return "mdm.msg.profl.wcf.bulk.invalidUrl";
        }
        if (path != null && !safeStringPattern.matcher(path).matches()) {
            return "mdm.msg.profl.wcf.bulk.invalidPath";
        }
        if (title == null || safeStringPattern.matcher(title).matches()) {
            return null;
        }
        return "mdm.msg.profl.wcf.bulk.invalidTitle";
    }
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put((Object)"malicious_content_filter", (Object)Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("malicious_content_filter")));
        jsonObj.put((Object)"profile_id", (Object)Long.parseLong(((Hashtable<K, String>)taskProps).get("profile_id")));
        jsonObj.put((Object)"collection_id", (Object)Long.parseLong(((Hashtable<K, String>)taskProps).get("collection_id")));
        jsonObj.put((Object)"payload_name", (Object)((Hashtable<K, String>)taskProps).get("payload_name"));
        jsonObj.put((Object)"config_data_item_id", (Object)Long.parseLong(((Hashtable<K, String>)taskProps).get("config_data_item_id")));
        jsonObj.put((Object)"url_filter_type", (Object)Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("url_filter_type")));
        jsonObj.put((Object)"create_bookmarks", (Object)Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("create_bookmarks")));
        jsonObj.put((Object)"enable_auto_filter", (Object)Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("enable_auto_filter")));
        if (taskProps.containsKey("permitted_urls")) {
            jsonObj.put((Object)"permitted_urls", (Object)new JSONArray((String)((Hashtable<K, String>)taskProps).get("permitted_urls")));
        }
        return jsonObj;
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("URLDetailsImportInfo"));
            final Column countColumn = Column.getColumn("URLDetailsImportInfo", "URL_DETAILS_ID").count();
            countColumn.setColumnAlias("URL_DETAILS_ID");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("URLDetailsImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("URLDetailsImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel("WebContentFilter"), (Object)String.valueOf(ds.getValue("URL_DETAILS_ID")));
            jsonObj.put((Object)CSVProcessor.getStatusLabel("WebContentFilter"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            long totalCount = 0L;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getTotalLabel("WebContentFilter"), customerID);
            if (countStr != null) {
                totalCount = Long.parseLong(countStr);
            }
            final long failureCount = new Long(ds.getValue("URL_DETAILS_ID").toString());
            final long successCount = totalCount - failureCount;
            this.logger.log(Level.INFO, " Async bulk url entries completed");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error setting failure count", ex);
            throw ex;
        }
    }
}
