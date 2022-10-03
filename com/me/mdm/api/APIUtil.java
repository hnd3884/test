package com.me.mdm.api;

import java.io.FileInputStream;
import com.me.mdm.server.role.RBDAUtil;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Date;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Comparator;
import java.util.MissingResourceException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.delta.DeltaTokenUtil;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import com.adventnet.i18n.I18N;
import java.util.Locale;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.sql.SQLException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.io.IOException;
import java.io.File;
import org.apache.tika.Tika;
import java.util.Arrays;
import java.util.Properties;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.ArrayList;
import java.util.Collection;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.paging.PagingUtil;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class APIUtil
{
    protected static Logger logger;
    private static Map<String, String> serverToAPIMap;
    private static Map<String, String> apiToServerMap;
    private static List<String> allowedImageMimeType;
    private static List<String> allowedCertMimeType;
    private static List<String> allowedAppMimeType;
    private static List<String> allowedCsvMimeType;
    private static List<String> allowedHtmlMimeType;
    private static List<String> allowedContentMgmtMimeType;
    private static List<String> allowedJsonMimeType;
    private static List<String> allowedXmlMimeType;
    private static List<String> allowedFontMimeType;
    private static List<String> allowedZipMimeType;
    private static List<String> allowedCustomProfileMimeType;
    
    public static APIUtil getNewInstance() {
        return new APIUtil();
    }
    
    public static String getRequestURL(final JSONObject request) {
        try {
            return String.valueOf(request.getJSONObject("msg_header").get("request_url"));
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception in getRequestURL", (Throwable)e);
            return null;
        }
    }
    
    public JSONObject wrapServerJSONToUserJSON(final JSONObject json) throws Exception {
        final JSONObject newJSON = new JSONObject();
        json.remove("BEAN_NAME");
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            if (json.get(key) instanceof JSONObject) {
                final JSONObject valueJSON = this.wrapServerJSONToUserJSON(json.getJSONObject(key));
                newJSON.put(key.toLowerCase(), (Object)valueJSON);
            }
            else if (json.get(key) instanceof JSONArray) {
                newJSON.put(key.toLowerCase(), (Object)this.wrapServerJSONToUserJSON(json.getJSONArray(key)));
            }
            else if (json.get(key) instanceof String) {
                try {
                    final JSONArray array = new JSONArray(String.valueOf(json.get(key)));
                    newJSON.put(key.toLowerCase(), (Object)this.wrapServerJSONToUserJSON(array));
                }
                catch (final Exception ex) {
                    newJSON.put(key.toLowerCase(), (Object)String.valueOf(json.get(key)));
                }
            }
            else if (json.get(key) instanceof Boolean) {
                newJSON.put(key.toLowerCase(), json.get(key));
            }
            else {
                newJSON.put(key.toLowerCase(), (Object)String.valueOf(json.get(key)));
            }
        }
        return newJSON;
    }
    
    public JSONArray wrapServerJSONToUserJSON(final JSONArray valueJSONArray) throws JSONException, Exception {
        final JSONArray newvalueJSONArray = new JSONArray();
        for (int i = 0; i < valueJSONArray.length(); ++i) {
            if (valueJSONArray.get(i) instanceof JSONObject) {
                newvalueJSONArray.put((Object)this.wrapServerJSONToUserJSON(valueJSONArray.getJSONObject(i)));
            }
            else if (valueJSONArray.get(i) instanceof JSONArray) {
                newvalueJSONArray.put((Object)this.wrapServerJSONToUserJSON(valueJSONArray.getJSONArray(i)));
            }
            else {
                newvalueJSONArray.put(valueJSONArray.get(i));
            }
        }
        return newvalueJSONArray;
    }
    
    public JSONObject wrapServerJSONToCaseInsensitiveUserJSON(final JSONObject json) throws Exception {
        final JSONObject newJSON = new JSONObject();
        json.remove("BEAN_NAME");
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            if (json.get(key) instanceof JSONObject) {
                final JSONObject valueJSON = this.wrapServerJSONToCaseInsensitiveUserJSON(json.getJSONObject(key));
                newJSON.put(key, (Object)valueJSON);
            }
            else if (json.get(key) instanceof JSONArray) {
                newJSON.put(key, (Object)this.wrapServerJSONToCaseInsensitiveUserJSON(json.getJSONArray(key)));
            }
            else if (json.get(key) instanceof String) {
                try {
                    final JSONArray array = new JSONArray(String.valueOf(json.get(key)));
                    newJSON.put(key, (Object)this.wrapServerJSONToCaseInsensitiveUserJSON(array));
                }
                catch (final Exception ex) {
                    newJSON.put(key, (Object)String.valueOf(json.get(key)));
                }
            }
            else if (json.get(key) instanceof Boolean) {
                newJSON.put(key, json.get(key));
            }
            else {
                newJSON.put(key, (Object)String.valueOf(json.get(key)));
            }
        }
        return newJSON;
    }
    
    public JSONArray wrapServerJSONToCaseInsensitiveUserJSON(final JSONArray valueJSONArray) throws JSONException, Exception {
        final JSONArray newvalueJSONArray = new JSONArray();
        for (int i = 0; i < valueJSONArray.length(); ++i) {
            if (valueJSONArray.get(i) instanceof JSONObject) {
                newvalueJSONArray.put((Object)this.wrapServerJSONToCaseInsensitiveUserJSON(valueJSONArray.getJSONObject(i)));
            }
            else if (valueJSONArray.get(i) instanceof JSONArray) {
                newvalueJSONArray.put((Object)this.wrapServerJSONToCaseInsensitiveUserJSON(valueJSONArray.getJSONArray(i)));
            }
            else {
                newvalueJSONArray.put(valueJSONArray.get(i));
            }
        }
        return newvalueJSONArray;
    }
    
    public JSONObject wrapUserJSONToServerJSON(final JSONObject json) throws JSONException, Exception {
        final JSONObject newJSON = new JSONObject();
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            if (json.get(key) instanceof JSONObject) {
                final JSONObject valueJSON = this.wrapUserJSONToServerJSON(json.getJSONObject(key));
                newJSON.put(key.toLowerCase(), (Object)valueJSON);
            }
            else if (json.get(key) instanceof JSONArray) {
                newJSON.put(key.toLowerCase(), (Object)this.wrapUserJSONToServerJSON(json.getJSONArray(key)));
            }
            else if (json.get(key) instanceof String && !key.equalsIgnoreCase("APP_CONFIGURATION") && !key.equalsIgnoreCase("value")) {
                try {
                    final JSONArray array = new JSONArray(String.valueOf(json.get(key)));
                    newJSON.put(key.toLowerCase(), (Object)this.wrapUserJSONToServerJSON(array));
                    APIUtil.logger.log(Level.INFO, "Key for which the values have been type casted to JSONArray: {0}", key);
                }
                catch (final Exception ex) {
                    newJSON.put(key.toLowerCase(), json.get(key));
                }
            }
            else {
                newJSON.put(key.toLowerCase(), json.get(key));
            }
        }
        return newJSON;
    }
    
    public JSONArray wrapUserJSONToServerJSON(final JSONArray valueJSONArray) throws JSONException, Exception {
        final JSONArray newvalueJSONArray = new JSONArray();
        for (int i = 0; i < valueJSONArray.length(); ++i) {
            if (valueJSONArray.get(i) instanceof JSONObject) {
                newvalueJSONArray.put((Object)this.wrapUserJSONToServerJSON(valueJSONArray.getJSONObject(i)));
            }
            else if (valueJSONArray.get(i) instanceof JSONArray) {
                newvalueJSONArray.put((Object)this.wrapUserJSONToServerJSON(valueJSONArray.getJSONArray(i)));
            }
            else {
                newvalueJSONArray.put(valueJSONArray.get(i));
            }
        }
        return newvalueJSONArray;
    }
    
    public JSONObject wrapUserJSONToCaseInsensitiveServerJSON(final JSONObject json) throws JSONException, Exception {
        final JSONObject newJSON = new JSONObject();
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            if (json.get(key) instanceof JSONObject) {
                final JSONObject valueJSON = this.wrapUserJSONToCaseInsensitiveServerJSON(json.getJSONObject(key));
                newJSON.put(key, (Object)valueJSON);
            }
            else if (json.get(key) instanceof JSONArray) {
                newJSON.put(key, (Object)this.wrapUserJSONToCaseInsensitiveServerJSON(json.getJSONArray(key)));
            }
            else if (json.get(key) instanceof String && !key.equalsIgnoreCase("APP_CONFIGURATION")) {
                try {
                    final JSONArray array = new JSONArray(String.valueOf(json.get(key)));
                    newJSON.put(key, (Object)this.wrapUserJSONToCaseInsensitiveServerJSON(array));
                }
                catch (final Exception ex) {
                    newJSON.put(key, json.get(key));
                }
            }
            else {
                newJSON.put(key, json.get(key));
            }
        }
        return newJSON;
    }
    
    private JSONArray wrapUserJSONToCaseInsensitiveServerJSON(final JSONArray valueJSONArray) throws JSONException, Exception {
        final JSONArray newvalueJSONArray = new JSONArray();
        for (int i = 0; i < valueJSONArray.length(); ++i) {
            if (valueJSONArray.get(i) instanceof JSONObject) {
                newvalueJSONArray.put((Object)this.wrapUserJSONToCaseInsensitiveServerJSON(valueJSONArray.getJSONObject(i)));
            }
            else if (valueJSONArray.get(i) instanceof JSONArray) {
                newvalueJSONArray.put((Object)this.wrapUserJSONToCaseInsensitiveServerJSON(valueJSONArray.getJSONArray(i)));
            }
            else {
                newvalueJSONArray.put(valueJSONArray.get(i));
            }
        }
        return newvalueJSONArray;
    }
    
    public JSONObject getResponseForEmptyList(final String moduleName) throws Exception {
        final JSONObject response = new JSONObject();
        response.put("status", 200);
        final JSONObject module = new JSONObject();
        module.put(moduleName, (Object)new JSONArray());
        response.put("RESPONSE", (Object)module);
        return response;
    }
    
    public PagingUtil getPagingParams(final JSONObject request) {
        try {
            final String requestURL = String.valueOf(request.getJSONObject("msg_header").get("request_url"));
            final String skipToken = request.getJSONObject("msg_header").getJSONObject("filters").optString("skip-token", (String)null);
            if (skipToken != null) {
                return new PagingUtil(skipToken, requestURL);
            }
            final Integer limit = request.getJSONObject("msg_header").getJSONObject("filters").optInt("limit", 50);
            final Integer offset = request.getJSONObject("msg_header").getJSONObject("filters").optInt("offset", 0);
            final String orderBy = request.getJSONObject("msg_header").getJSONObject("filters").optString("orderby", (String)null);
            final String sortorder = request.getJSONObject("msg_header").getJSONObject("filters").optString("sortorder", "asc");
            final String searchField = request.getJSONObject("msg_header").getJSONObject("filters").optString("searchfield", (String)null);
            final String searchKey = request.getJSONObject("msg_header").getJSONObject("filters").optString("searchkey", (String)null);
            return new PagingUtil(limit, offset, orderBy, sortorder, searchField, searchKey, requestURL);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "Exception when getting paging params ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static String getCommaSeperatedString(final Collection<Long> list) {
        if (list.size() == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (final Long t : list) {
            builder.append(t);
            builder.append(",");
        }
        return builder.toString().trim().substring(0, builder.lastIndexOf(","));
    }
    
    public List<Long> getInvalidManagedUserIds(final JSONArray mUserIds) throws Exception {
        final List<Long> userids = new ArrayList<Long>();
        for (int i = 0; i < mUserIds.length(); ++i) {
            userids.add(mUserIds.getLong(i));
        }
        return this.getInvalidManagedUserIds(userids);
    }
    
    public List<Long> getInvalidManagedUserIds(final Collection<Long> mUserIds) throws Exception {
        final List<Long> invalid = new ArrayList<Long>();
        for (final Long mUserId : mUserIds) {
            final HashMap user = ManagedUserHandler.getInstance().getManagedUserDetails(mUserId);
            if (user == null || user.size() == 0) {
                invalid.add(mUserId);
            }
        }
        return invalid;
    }
    
    public static ArrayList loadPropertiesAsList(final Properties properties, final String key) {
        ArrayList valueList = null;
        if (properties.containsKey(key)) {
            valueList = new ArrayList((Collection<? extends E>)Arrays.asList(properties.getProperty(key).split(",")));
        }
        return valueList;
    }
    
    public static boolean isAllowedImageMimeType(final String contentType) {
        return APIUtil.allowedImageMimeType.contains(contentType);
    }
    
    public static boolean isAllowedZipMimeType(final String contentType) {
        return APIUtil.allowedZipMimeType.contains(contentType);
    }
    
    public static boolean isAllowedImageMimeTypeForFile(final String filePath) throws Exception {
        final Tika tika = new Tika();
        final File file = new File(filePath);
        final String contentType = tika.detect(file);
        return APIUtil.allowedImageMimeType.contains(contentType);
    }
    
    public static boolean isAllowedCertificateMimeType(final File certFile) throws IOException {
        final Tika tika = new Tika();
        final String contentType = tika.detect(certFile);
        if (!APIUtil.allowedCertMimeType.contains(contentType)) {
            final String fileName = certFile.getName();
            if (contentType.equals("text/plain") && !fileName.contains(".pem") && !fileName.contains(".crt") && !fileName.contains(".key") && !fileName.contains(".cer") && !fileName.contains(".p7b")) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAllowedCertificateMimeType(final String contentType) {
        return APIUtil.allowedCertMimeType.contains(contentType);
    }
    
    public static boolean isAllowedApplicationMimeType(final String contentType) {
        return APIUtil.allowedAppMimeType.contains(contentType);
    }
    
    public static boolean isAllowedCSVMimeType(final String contentType) {
        return APIUtil.allowedCsvMimeType.contains(contentType);
    }
    
    public static boolean isAllowedHTMLMimeType(final String contentType) {
        return APIUtil.allowedHtmlMimeType.contains(contentType);
    }
    
    public static boolean isAllowedContentMgmtMimeType(final String contentType) {
        return APIUtil.allowedContentMgmtMimeType.contains(contentType);
    }
    
    public static boolean isAllowedJSONMimeType(final String contentType) {
        return APIUtil.allowedJsonMimeType.contains(contentType);
    }
    
    public static boolean isAllowedXmlMimeType(final String contentType) {
        return APIUtil.allowedXmlMimeType.contains(contentType);
    }
    
    public static boolean isAllowedCustomProfileType(final String contentType) {
        return APIUtil.allowedCustomProfileMimeType.contains(contentType);
    }
    
    public static boolean isAllowedFontType(final String contentType) {
        return APIUtil.allowedFontMimeType.contains(contentType);
    }
    
    public static boolean isValidFileName(final String fileName) {
        return fileName.matches("^([\\w\\^&amp;&apos;\\@\\{\\}\\[\\]\\,\\$\\=\\!\\-\\#\\(\\)\\.%\\+\\~\\_\\` \\P{InBasicLatin}]+)$");
    }
    
    public static boolean isAllowedContentType(final String contentType) {
        return contentType.matches("text\\/csv|application\\/xhtml\\+xml|text\\/html|application\\/vnd\\.android\\.package-archive|application\\/x-itunes-ipa|text\\/plain|application\\/x-zip|application\\/x-bzip2|application\\/zip|application\\/x-gtar|application\\/octet-stream|application\\/xml|image\\/jpg|image\\/png|image\\/gif|image\\/x-xbitmap|image\\/x-xpixmap|image\\/x-png|image\\/ief|image\\/jpeg|image\\/tiff|image\\/rgb|image\\/x-rgb|image\\/g3fax|image\\/x-icon|image\\/bmp|image\\/x-ms-bmp|image\\/vnd.microsoft.icon|image\\/icns|application\\/json|application\\/x-ms-installer|application\\/pkcs10|application\\/pkix-cert|application\\/pkix-crl|application\\/pkcs7-mime|application\\/x-x509-ca-cert|application\\/x-pkcs12|application\\/x-pkcs7-certificates|application\\/x-pkcs7-certreqresp|application\\/vnd.ms-excel|application\\/pkix-cert|application\\/x-java-keystore|application\\/x-silverlight-app\\/x-plist,application\\/x-apple-aspen-config|application\\\\/pkcs7-signature");
    }
    
    public static void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception ex) {
            APIUtil.logger.log(Level.WARNING, "Exception occurred while closing dataset....", ex);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            APIUtil.logger.log(Level.WARNING, "Exception occurred in closing connection....", ex);
        }
    }
    
    public JSONObject getJSONObjectFromDS(final DataSet ds, final SelectQuery selectQuery) throws SQLException, JSONException {
        final List<Column> columns = selectQuery.getSelectColumns();
        final JSONObject jsonObject = new JSONObject();
        for (final Column column : columns) {
            if (column.getColumnAlias() != null) {
                jsonObject.put(column.getColumnAlias(), (Object)String.valueOf(ds.getValue(column.getColumnAlias())));
            }
            else {
                jsonObject.put(column.getColumnName(), (Object)String.valueOf(ds.getValue(column.getColumnName())));
            }
        }
        return jsonObject;
    }
    
    public String getFileURL(final String fileLoc) {
        return fileLoc.replace("\\", "/");
    }
    
    public String getDownloadableFileURL(final String fileLoc) {
        return fileLoc.replace("\\", "/");
    }
    
    public static Long getResourceID(final JSONObject apiRequest, final String resourceKey) throws JSONException {
        try {
            return JSONUtil.optLongForUVH(apiRequest.getJSONObject("msg_header").getJSONObject("resource_identifier"), resourceKey, Long.valueOf(-1L));
        }
        catch (final NumberFormatException e) {
            APIUtil.logger.log(Level.WARNING, "NumberFormatException in getResourceID on processing : \"{0}\" as Long", apiRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").opt(resourceKey));
            return -1L;
        }
        catch (final Exception e2) {
            APIUtil.logger.log(Level.SEVERE, "exception  in getResourceID", e2);
            return -1L;
        }
    }
    
    public static void addResourceID(final JSONObject apiRequest, final String resourceKey, final Long resourceID) throws JSONException {
        apiRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").put(resourceKey, (Object)resourceID);
    }
    
    public static String getResourceIDString(final JSONObject apiRequest, final String resourceKey) throws JSONException {
        return JSONUtil.getString(apiRequest.getJSONObject("msg_header").getJSONObject("resource_identifier"), resourceKey, null);
    }
    
    public static Long getCustomerID(final JSONObject apiRequest) throws APIHTTPException {
        try {
            if (apiRequest.getJSONObject("msg_header").getJSONObject("filters").has("customer_id")) {
                return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("customer_id");
            }
            throw new APIHTTPException("COM0022", new Object[0]);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception in getCustomerID()", (Throwable)e);
            return -1L;
        }
    }
    
    public static Long optCustomerID(final JSONObject apiRequest) throws APIHTTPException {
        try {
            if (apiRequest.getJSONObject("msg_header").getJSONObject("filters").has("customer_id")) {
                return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("customer_id");
            }
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception in getCustomerID()", (Throwable)e);
        }
        return -1L;
    }
    
    public static Long getUserID(final JSONObject apiRequest) {
        try {
            return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("user_id");
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception in getUserID()", (Throwable)e);
            return -1L;
        }
    }
    
    public static String getUserName(final JSONObject apiRequest) {
        try {
            return String.valueOf(apiRequest.getJSONObject("msg_header").getJSONObject("filters").get("user_name"));
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception in getUserName()", (Throwable)e);
            return null;
        }
    }
    
    public static Long getLoginID(final JSONObject apiRequest) {
        try {
            return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("login_id");
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception in getLoginID()", (Throwable)e);
            return -1L;
        }
    }
    
    public static Boolean getBooleanFilter(final JSONObject request, final String key) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters").optBoolean(key);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public static Boolean getBooleanFilter(final JSONObject request, final String key, final Boolean defaultValue) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters").optBoolean(key, (boolean)defaultValue);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public static Integer getIntegerFilter(final JSONObject request, final String key) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters").optInt(key, -1);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public static Long getLongFilter(final JSONObject request, final String key) {
        try {
            final JSONObject filters = request.getJSONObject("msg_header").getJSONObject("filters");
            Long value = -1L;
            if (filters.has(key)) {
                value = Long.valueOf(String.valueOf(filters.get(key)));
            }
            return value;
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public static String getStringFilter(final JSONObject request, final String key) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters").optString(key, (String)null);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public static String optStringFilter(final JSONObject request, final String key, final String defaultValue) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters").optString(key, defaultValue);
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public static String getEnglishString(final String key, final Object... args) {
        try {
            final Locale locale = new Locale("en", "US");
            final ResourceBundle bundle = I18N.getResourceBundleFromCache("ApplicationResources", locale);
            final String val = bundle.getString(key);
            return MessageFormat.format(val, args);
        }
        catch (final Exception e) {
            APIUtil.logger.log(Level.SEVERE, "Error while Retrieving I18N message", e);
        }
        finally {
            I18N.resetRequestLocale();
        }
        return null;
    }
    
    public static String getLocalizedString(final String key, final Locale locale, final Object... args) {
        try {
            if (locale != null) {
                final ResourceBundle bundle = I18N.getResourceBundleFromCache("ApplicationResources", locale);
                final String val = bundle.getString(key);
                return MessageFormat.format(val, args);
            }
            return I18N.getMsg(key, args);
        }
        catch (final Exception e) {
            APIUtil.logger.log(Level.SEVERE, "Error while Retrieving I18N message", e);
        }
        finally {
            I18N.resetRequestLocale();
        }
        return key;
    }
    
    public DeltaTokenUtil getDeltaTokenForAPIRequest(final JSONObject request) {
        try {
            final String requestURL = String.valueOf(request.getJSONObject("msg_header").get("request_url"));
            final String deltaToken = request.getJSONObject("msg_header").getJSONObject("filters").optString("delta-token", (String)null);
            if (deltaToken != null) {
                return new DeltaTokenUtil(deltaToken, requestURL);
            }
            return null;
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "Exception when getting delta token ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getParameterForIndex(final String[] paths, final int index) {
        try {
            if (index < paths.length) {
                return paths[index];
            }
        }
        catch (final Exception e) {
            APIUtil.logger.log(Level.SEVERE, e, () -> "Exception while getting parameter for the index" + n + ": {0}");
        }
        return null;
    }
    
    public boolean checkRolesForCurrentUser(final String[] configuredRoles) {
        try {
            boolean isAllowed = false;
            final List<String> userRoles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            for (final String role : configuredRoles) {
                if (isAllowed) {
                    break;
                }
                isAllowed = userRoles.contains(role);
            }
            return isAllowed;
        }
        catch (final Exception e) {
            APIUtil.logger.log(Level.SEVERE, "Exception while fetching roles for user", e);
            return false;
        }
    }
    
    public JSONArray getCountryNames() {
        JSONArray countryArray = null;
        final Locale[] availLocales = Locale.getAvailableLocales();
        String iso = null;
        String countryCode = null;
        String countryName = null;
        countryArray = new JSONArray();
        final List<JSONObject> countryList = new ArrayList<JSONObject>();
        JSONObject jsonObject = null;
        final List<String> availableCountries = new ArrayList<String>();
        for (final Locale locale : availLocales) {
            try {
                iso = locale.getISO3Country();
                countryCode = locale.getCountry();
                countryName = locale.getDisplayCountry();
                jsonObject = new JSONObject();
                if (iso != null && countryCode != null && !"".equals(iso) && !"".equals(countryCode) && !availableCountries.contains(countryName)) {
                    jsonObject.put("countryCode", (Object)countryCode);
                    jsonObject.put("countryName", (Object)countryName);
                    availableCountries.add(countryName);
                    countryList.add(jsonObject);
                }
            }
            catch (final MissingResourceException ex) {
                APIUtil.logger.log(Level.WARNING, "Error in APIUtil.getCountryNames() {0}", ex.getMessage());
            }
            catch (final Exception ex2) {
                APIUtil.logger.log(Level.WARNING, "Error in APIUtil.getCountryNames() {0}", ex2.getMessage());
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        if (!countryList.isEmpty()) {
            Collections.sort(countryList, new Comparator<JSONObject>() {
                @Override
                public int compare(final JSONObject o1, final JSONObject o2) {
                    try {
                        return String.valueOf(o1.get("countryName")).compareTo(String.valueOf(o2.get("countryName")));
                    }
                    catch (final JSONException e) {
                        APIUtil.logger.log(Level.WARNING, "Error in sorting countryNames");
                        return 0;
                    }
                }
            });
        }
        for (final JSONObject country : countryList) {
            countryArray.put((Object)country);
        }
        return countryArray;
    }
    
    public static String getPortalString(final int platformType) {
        switch (platformType) {
            case 1: {
                return "ABM/ASM";
            }
            case 2: {
                return "Managed Google Play";
            }
            case 3: {
                return "Windows Business Store";
            }
            default: {
                return "Portal";
            }
        }
    }
    
    public static JSONArray getJSONArrayFromList(final List list) {
        final JSONArray jsonArray = new JSONArray();
        for (final Object object : list) {
            JSONObject jsonObjectFromMap = new JSONObject();
            if (object instanceof Hashtable) {
                jsonObjectFromMap = getJSONObjectFromHashTable((Hashtable)object);
            }
            else if (object instanceof HashMap) {
                jsonObjectFromMap = getJSONObjectFromMap((Map)object);
            }
            jsonArray.put((Object)jsonObjectFromMap);
        }
        return jsonArray;
    }
    
    public static JSONObject getJSONObjectFromMap(final Map map) {
        try {
            final JSONObject jsonObject = new JSONObject();
            if (map instanceof LinkedHashMap) {
                final String mapString = map.toString();
                final String[] collection = mapString.split(",");
                for (int i = 0; i < collection.length; ++i) {
                    final String[] temp = collection[i].split("=");
                    jsonObject.put(temp[0], (Object)temp[1]);
                }
            }
            else {
                final Set keySet = map.keySet();
                for (final Object o : keySet) {
                    final String key = (String)o;
                    jsonObject.put(key.toLowerCase(), map.get(key));
                }
            }
            return jsonObject;
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "Exception while getting JSONObjectFromMap", (Throwable)e);
            return null;
        }
    }
    
    public static JSONObject getJSONObjectFromHashTable(final Hashtable hashTable) {
        try {
            final JSONObject jsonObject = new JSONObject();
            final Set keySet = hashTable.keySet();
            for (final Object o : keySet) {
                final String key = (String)o;
                jsonObject.put(key.toLowerCase(), hashTable.get(key));
            }
            return jsonObject;
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "Exception while getting getJSONObjectFromHashTable", (Throwable)e);
            return null;
        }
    }
    
    public static JSONObject getJSONObjectFromProperties(final Properties properties) {
        try {
            final JSONObject jsonObject = new JSONObject();
            final Set keySet = properties.keySet();
            for (final Object o : keySet) {
                final String key = (String)o;
                jsonObject.put(key, ((Hashtable<K, Object>)properties).get(key));
            }
            return jsonObject;
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "Exception while getting getJSONObjectFromProperties", (Throwable)e);
            return null;
        }
    }
    
    public static JSONObject invertJSONObject(final JSONObject jsonObject) throws JSONException {
        final Iterator iterator = jsonObject.keys();
        final JSONObject responseJSON = new JSONObject();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            final Object value = jsonObject.get(key);
            if (responseJSON.has(String.valueOf(value))) {
                throw new JSONException("Key " + value + " already exist");
            }
            responseJSON.put(String.valueOf(value), (Object)key);
        }
        return responseJSON;
    }
    
    public static String getURLDecodedStringFilter(final JSONObject request, final String key, final String encoding) {
        String decodedString = null;
        try {
            decodedString = request.getJSONObject("msg_header").getJSONObject("filters").optString(key, (String)null);
            if (!MDMStringUtils.isEmpty(decodedString)) {
                decodedString = URLDecoder.decode(decodedString, encoding);
            }
        }
        catch (final JSONException | UnsupportedEncodingException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", e);
        }
        return decodedString;
    }
    
    public static String getURLDecodedStringFilter(final JSONObject request, final String key) {
        String decodedString = null;
        try {
            decodedString = request.getJSONObject("msg_header").getJSONObject("filters").optString(key, (String)null);
            if (!MDMStringUtils.isEmpty(decodedString)) {
                decodedString = URLDecoder.decode(decodedString, "UTF-8");
            }
        }
        catch (final JSONException | UnsupportedEncodingException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", e);
        }
        return decodedString;
    }
    
    public static JSONObject parseIOSAppConfig(final InputStream inputStream) throws JSONException {
        JSONObject responseJSON;
        try {
            responseJSON = new AppConfigDataHandler().parseIosAppConfig(inputStream);
            if (responseJSON.has("Error")) {
                throw new APIHTTPException("COM0015", new Object[] { String.valueOf(responseJSON.get("Error")) });
            }
        }
        catch (final Exception e) {
            APIUtil.logger.log(Level.SEVERE, "Exception in parsing IOS Config", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    private static Map<String, String> getServerToAPIMap() {
        return APIUtil.serverToAPIMap;
    }
    
    private static Map<String, String> getApiToServerMap() {
        return APIUtil.apiToServerMap;
    }
    
    public JSONObject convertAPIJSONtoServerJSON(final JSONObject apiJSON) throws JSONException {
        final Iterator<String> keys = apiJSON.keys();
        final JSONObject result = new JSONObject();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (getApiToServerMap().containsKey(key)) {
                result.put((String)getApiToServerMap().get(key), apiJSON.get(key));
            }
        }
        return result;
    }
    
    public JSONObject convertServerJSONtoAPIJSON(final JSONObject serverJSON) throws JSONException {
        final Iterator<String> keys = serverJSON.keys();
        final JSONObject result = new JSONObject();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (getServerToAPIMap().containsKey(key)) {
                result.put((String)getServerToAPIMap().get(key), serverJSON.get(key));
            }
        }
        return result;
    }
    
    public JSONObject formatSchedulerDetailsToJSON(final JSONObject schedulerObj, final HashMap schedule, final String scheduleName) throws Exception {
        String scheduleTypeExtraText = "";
        String startTimeText = "";
        String startTimeExtraText = "";
        final Long datetime = new Long(System.currentTimeMillis());
        final SchedulerInfo scheduler = new SchedulerInfo();
        final String schType = schedule.get("schedType");
        final int hours = schedule.get("exeHours");
        final int minutes = schedule.get("exeMinutes");
        String updateTime = null;
        String hourStr = null;
        if (hours < 10) {
            hourStr = "0" + hours;
        }
        else {
            hourStr = hours + "";
        }
        if (minutes < 10) {
            updateTime = hourStr + ":0" + minutes;
        }
        else {
            updateTime = hourStr + ":" + minutes;
        }
        String dateOfExec = "";
        if (schType.equalsIgnoreCase("Once") || schType.equalsIgnoreCase("Daily")) {
            final int day = schedule.get("startDate");
            final int month = schedule.get("startMonth");
            final int year = schedule.get("startYear");
            dateOfExec = ((month < 9) ? dateOfExec.concat("0" + (month + 1) + "/") : dateOfExec.concat("" + (month + 1) + "/"));
            dateOfExec = ((day < 10) ? dateOfExec.concat("0" + day + "/") : dateOfExec.concat("" + day + "/"));
            dateOfExec = dateOfExec.concat("" + year);
            if (schType.equalsIgnoreCase("Once")) {
                scheduleTypeExtraText = "Once";
                schedulerObj.put("onceTime", (Object)(dateOfExec + ", " + updateTime));
            }
            else {
                schedulerObj.put("dailyTime", (Object)(dateOfExec + ", " + updateTime));
                final String intervalType = schedule.get("dailyIntervalType");
                schedulerObj.put("dailyIntervalType", (Object)intervalType);
                if (intervalType.equals("weekDays")) {
                    scheduleTypeExtraText = I18N.getMsg("dc.common.scheduler.on_week_days", new Object[0]);
                }
                else if (intervalType.equals("alternativeDays")) {
                    scheduleTypeExtraText = I18N.getMsg("dc.common.scheduler.alternative_days", new Object[0]);
                }
                else {
                    scheduleTypeExtraText = I18N.getMsg("dc.common.EVERYDAY", new Object[0]);
                }
            }
        }
        else if (schType.equalsIgnoreCase("Weekly")) {
            schedulerObj.put("weeklyTime", (Object)updateTime);
            int[] days = new int[7];
            days = schedule.get("daysOfWeek");
            String weekDays = "";
            for (int j = 0; j < days.length; ++j) {
                weekDays = weekDays + days[j] + ",";
                weekDays = weekDays.trim();
            }
            if (weekDays.charAt(weekDays.length() - 1) == ',') {
                weekDays = weekDays.substring(0, weekDays.length() - 1);
            }
            schedulerObj.put("daysOfWeek", (Object)weekDays);
            scheduleTypeExtraText = scheduler.getWeekDaysString(weekDays);
        }
        else {
            schedulerObj.put("monthlyTime", (Object)updateTime);
            final int[] months = schedule.get("months");
            String monthList = "";
            for (int j = 0; j < months.length; ++j) {
                monthList = monthList + months[j] + ",";
                monthList = monthList.trim();
            }
            if (monthList.charAt(monthList.length() - 1) == ',') {
                monthList = monthList.substring(0, monthList.length() - 1);
            }
            schedulerObj.put("monthsList", (Object)monthList);
            scheduleTypeExtraText = scheduler.getMonthString(monthList);
            final String monthlyPerform = schedule.get("monthlyPerform");
            schedulerObj.put("monthlyPerform", (Object)monthlyPerform);
            startTimeExtraText = I18N.getMsg("dc.common.scheduler.during", new Object[0]) + " :";
            if (monthlyPerform.equals("WeekDay")) {
                final String monthlyWeekDay = schedule.get("monthlyWeekDay") + "";
                final int[] monthlyWeek = schedule.get("monthlyWeekNum");
                String monthlyWeekNum = "";
                for (int k = 0; k < monthlyWeek.length; ++k) {
                    monthlyWeekNum = monthlyWeekNum + monthlyWeek[k] + ",";
                    monthlyWeekNum = monthlyWeekNum.trim();
                }
                if (monthlyWeekNum.charAt(monthlyWeekNum.length() - 1) == ',') {
                    monthlyWeekNum = monthlyWeekNum.substring(0, monthlyWeekNum.length() - 1);
                }
                schedulerObj.put("monthlyWeekDay", (Object)monthlyWeekDay);
                schedulerObj.put("monthlyWeekNum", (Object)monthlyWeekNum);
                startTimeExtraText = startTimeExtraText + " " + scheduler.getWeekNumString(monthlyWeekNum) + " " + scheduler.getDaysString(monthlyWeekDay) + " of selected Months";
            }
            else {
                final String monthlyDate = schedule.get("dates") + "";
                schedulerObj.put("monthlyDay", (Object)monthlyDate);
                final Object extraText = startTimeExtraText + " " + scheduler.getDateString(monthlyDate);
                startTimeExtraText = I18N.getMsg("dc.common.java.selected_months", new Object[] { extraText });
            }
        }
        schedulerObj.put("scheduleType", (Object)schType);
        schedulerObj.put("scheduleTypeExtraText", (Object)scheduleTypeExtraText);
        schedulerObj.put("startTimeExtraText", (Object)startTimeExtraText);
        if (!schType.equals("")) {
            if (schType.equals("Once")) {
                startTimeText = String.valueOf(schedulerObj.get("onceTime"));
            }
            else if (schType.equals("Daily")) {
                startTimeText = String.valueOf(schedulerObj.get("dailyTime"));
            }
            else if (schType.equals("Weekly")) {
                startTimeText = String.valueOf(schedulerObj.get("weeklyTime"));
            }
            else {
                startTimeText = String.valueOf(schedulerObj.get("monthlyTime"));
            }
            final Long nextRunTime = ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(scheduleName);
            if (nextRunTime == null || nextRunTime == -1L) {
                schedulerObj.put("nextRunTime", (Object)"--");
            }
            else if (nextRunTime < datetime) {
                schedulerObj.put("nextRunTime", (Object)I18N.getMsg("dc.common.Disabled", new Object[0]));
            }
            else {
                schedulerObj.put("nextRunTime", (Object)Utils.getServerTimeInString(nextRunTime));
            }
            schedulerObj.put("startTimeText", (Object)startTimeText);
        }
        return schedulerObj;
    }
    
    public static JSONObject getFilters(final JSONObject request) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters");
        }
        catch (final JSONException e) {
            APIUtil.logger.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
    
    public String getAPITimeFromMillis(final Long currentMillis) {
        final Date date = new Date(currentMillis);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DMUserHandler.getUserTimeFormat() + "Z");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID()));
        return simpleDateFormat.format(date);
    }
    
    public String getPredefinedURL(final String url, final String tab) {
        if (tab != null && tab.equalsIgnoreCase("mdm")) {
            return "predefined.".concat(url);
        }
        return "#/uems/mdm/reports/predefined/".concat(url);
    }
    
    public boolean hasUserAllDeviceScopeGroup(final JSONObject request, final Boolean isGroup) {
        final Boolean isMDMAdmin = this.checkRolesForCurrentUser(new String[] { "All_Managed_Mobile_Devices" });
        final Long loginId = getLoginID(request);
        final Boolean isRBDAGroupCheck = RBDAUtil.getInstance().hasRBDAGroupCheck(loginId, isGroup);
        return isMDMAdmin || isRBDAGroupCheck;
    }
    
    static {
        APIUtil.logger = Logger.getLogger("MDMApiLogger");
        APIUtil.serverToAPIMap = new HashMap<String, String>();
        APIUtil.apiToServerMap = new HashMap<String, String>();
        APIUtil.logger.log(Level.INFO, "starting loading mdmContentType.properties...");
        try {
            final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "mdmContentType.properties";
            final File f = new File(filePath);
            if (f.exists()) {
                final InputStream is = new FileInputStream(filePath);
                final Properties properties = new Properties();
                properties.load(is);
                APIUtil.allowedImageMimeType = loadPropertiesAsList(properties, "image");
                APIUtil.allowedCertMimeType = loadPropertiesAsList(properties, "certificate");
                APIUtil.allowedAppMimeType = loadPropertiesAsList(properties, "enterprise_app");
                APIUtil.allowedCsvMimeType = loadPropertiesAsList(properties, "csv");
                APIUtil.allowedContentMgmtMimeType = loadPropertiesAsList(properties, "content_mgmt");
                APIUtil.allowedJsonMimeType = loadPropertiesAsList(properties, "json");
                APIUtil.allowedHtmlMimeType = loadPropertiesAsList(properties, "html");
                APIUtil.allowedXmlMimeType = loadPropertiesAsList(properties, "xml");
                APIUtil.allowedFontMimeType = loadPropertiesAsList(properties, "mac_font");
                APIUtil.allowedZipMimeType = loadPropertiesAsList(properties, "zip");
                (APIUtil.allowedCustomProfileMimeType = loadPropertiesAsList(properties, "custom_profile")).addAll(APIUtil.allowedXmlMimeType);
                APIUtil.logger.log(Level.INFO, "completed loading mdmContentType.properties...");
            }
            else {
                APIUtil.logger.log(Level.SEVERE, "mdmContentType.properties does not exist!!!!!!");
            }
        }
        catch (final IOException e) {
            APIUtil.logger.log(Level.SEVERE, "Loading mdmContentType.properties failed .....", e);
        }
        APIUtil.serverToAPIMap.put("scheduleType", "schedule_type");
        APIUtil.serverToAPIMap.put("dailyTime", "daily_time");
        APIUtil.serverToAPIMap.put("dailyIntervalType", "daily_interval_type");
        APIUtil.serverToAPIMap.put("weeklyTime", "weekly_time");
        APIUtil.serverToAPIMap.put("daysOfWeek", "days_of_week");
        APIUtil.serverToAPIMap.put("monthlyTime", "monthly_time");
        APIUtil.serverToAPIMap.put("monthlyPerform", "monthly_perform");
        APIUtil.serverToAPIMap.put("monthlyWeekDay", "monthly_week_day");
        APIUtil.serverToAPIMap.put("monthlyWeekNum", "monthly_week_num");
        APIUtil.serverToAPIMap.put("monthlyDay", "monthly_day");
        APIUtil.serverToAPIMap.put("monthsList", "months_list");
        APIUtil.serverToAPIMap.put("schedulerDisabled", "scheduler_disabled");
        APIUtil.serverToAPIMap.put("taskTimeZone", "task_time_zone");
        APIUtil.apiToServerMap.put("schedule_type", "scheduleType");
        APIUtil.apiToServerMap.put("daily_time", "dailyTime");
        APIUtil.apiToServerMap.put("daily_interval_type", "dailyIntervalType");
        APIUtil.apiToServerMap.put("weekly_time", "weeklyTime");
        APIUtil.apiToServerMap.put("days_of_week", "daysOfWeek");
        APIUtil.apiToServerMap.put("monthly_time", "monthlyTime");
        APIUtil.apiToServerMap.put("monthly_perform", "monthlyPerform");
        APIUtil.apiToServerMap.put("monthly_week_day", "monthlyWeekDay");
        APIUtil.apiToServerMap.put("monthly_week_num", "monthlyWeekNum");
        APIUtil.apiToServerMap.put("monthly_day", "monthlyDay");
        APIUtil.apiToServerMap.put("months_list", "monthsList");
        APIUtil.apiToServerMap.put("scheduler_disabled", "schedulerDisabled");
        APIUtil.apiToServerMap.put("task_time_zone", "taskTimeZone");
    }
}
