package com.me.devicemanagement.onpremise.server.metrack;

import java.util.Hashtable;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.json.JSONException;
import java.util.Map;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class METrackerUtil
{
    private static Logger logger;
    private static String sourceClass;
    private static final String GLOBAL_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    public static void incrementMETrackParams(final String paramName) {
        incrementMETrackParams(paramName, 1);
    }
    
    public static void incrementMETrackParams(final String paramName, final int incrementBy) {
        final String sourceMethod = "incrementMETrackParams - overloaded";
        try {
            final Column col = Column.getColumn("METrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject meParamsDO = SyMUtil.getPersistence().get("METrackParams", criteria);
            if (meParamsDO.isEmpty()) {
                addMETrackParams(paramName, String.valueOf(incrementBy), meParamsDO);
            }
            else {
                String paramValue = String.valueOf(meParamsDO.getFirstRow("METrackParams").get("PARAM_VALUE"));
                try {
                    paramValue = String.valueOf(Integer.parseInt(paramValue) + incrementBy);
                }
                catch (final Exception e) {
                    paramValue = String.valueOf(incrementBy);
                }
                updateMETrackParams(paramName, paramValue, meParamsDO);
            }
        }
        catch (final DataAccessException e2) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e2);
        }
    }
    
    public static void addOrUpdateMETrackParams(final String paramName, final String paramValue) {
        final String sourceMethod = "addOrUpdateMETrackParams";
        try {
            final Column col = Column.getColumn("METrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject meParamsDO = SyMUtil.getPersistence().get("METrackParams", criteria);
            if (meParamsDO.isEmpty()) {
                addMETrackParams(paramName, paramValue, meParamsDO);
            }
            else {
                updateMETrackParams(paramName, paramValue, meParamsDO);
            }
        }
        catch (final DataAccessException e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
        }
    }
    
    private static void addMETrackParams(final String paramName, final String paramValue, final DataObject meParamsDO) {
        final String sourceMethod = "addMETrackParams";
        try {
            final Row paramRow = new Row("METrackParams");
            paramRow.set("PARAM_NAME", (Object)paramName);
            paramRow.set("PARAM_VALUE", (Object)paramValue);
            paramRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            meParamsDO.addRow(paramRow);
            SyMUtil.getPersistence().add(meParamsDO);
            SyMLogger.debug(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
        }
        catch (final DataAccessException e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
        }
    }
    
    private static void updateMETrackParams(final String paramName, final String paramValue, final DataObject meParamsDO) {
        final String sourceMethod = "addMETrackParams";
        try {
            final Row paramRow = meParamsDO.getFirstRow("METrackParams");
            paramRow.set("PARAM_VALUE", (Object)paramValue);
            paramRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            meParamsDO.updateRow(paramRow);
            SyMUtil.getPersistence().update(meParamsDO);
            SyMLogger.debug(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
        }
        catch (final DataAccessException e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
        }
    }
    
    public static Properties getMETrackParams(final String paramName) {
        final String sourceMethod = "getMETrackParam";
        final Properties meParamProps = new Properties();
        try {
            final Column col = Column.getColumn("METrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject dobj = SyMUtil.getPersistence().get("METrackParams", criteria);
            if (dobj.isEmpty()) {
                return meParamProps;
            }
            final Row meTrackParamRow = dobj.getFirstRow("METrackParams");
            final String formattedDate = getDateFromTimestamp(new Date(Long.valueOf(String.valueOf(meTrackParamRow.get("LAST_UPDATED_TIME")))));
            meParamProps.setProperty(String.valueOf(meTrackParamRow.get("PARAM_NAME")), String.valueOf(meTrackParamRow.get("PARAM_VALUE")));
            if (formattedDate != null) {
                meParamProps.setProperty("LAST_MODIFIED_TIME", formattedDate);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred.", (Throwable)ex);
        }
        return meParamProps;
    }
    
    public static Properties getMETrackParam(final Properties prop, final String paramName, final String dateDisplayName) {
        final String sourceMethod = "getMETrackParam";
        try {
            final Column col = Column.getColumn("METrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject dobj = SyMUtil.getPersistence().get("METrackParams", criteria);
            if (dobj.isEmpty()) {
                return prop;
            }
            final Row meTrackParamRow = dobj.getFirstRow("METrackParams");
            prop.setProperty(String.valueOf(meTrackParamRow.get("PARAM_NAME")), String.valueOf(meTrackParamRow.get("PARAM_VALUE")));
            if (paramName != null && paramName.equals("TotUserLoginCount")) {
                final String lastLogin = String.valueOf(meTrackParamRow.get("LAST_UPDATED_TIME"));
                if (lastLogin != null) {
                    prop.setProperty(dateDisplayName, lastLogin);
                }
            }
            else {
                final String formattedDate = getDateFromTimestamp(new Date(Long.valueOf(String.valueOf(meTrackParamRow.get("LAST_UPDATED_TIME")))));
                if (formattedDate != null && dateDisplayName != null) {
                    prop.setProperty(dateDisplayName, formattedDate);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred.", (Throwable)ex);
        }
        return prop;
    }
    
    public static String getTimeZone() {
        return new SimpleDateFormat("zzz").format(new Date());
    }
    
    public static String getFormattedDate(final String dateString, final String dateFormat) {
        final String sourceMethod = "getFormattedDate";
        try {
            final DateFormat formatter = new SimpleDateFormat(dateFormat);
            final Date date = formatter.parse(dateString);
            return getDateFromTimestamp(date);
        }
        catch (final ParseException e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    private static String getDateFromTimestamp(final Date date) {
        String formattedDate = "";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        formattedDate = sdf.format(date);
        return formattedDate;
    }
    
    public static String getDateFromTimestamp(final Long timestamp) {
        String formattedDate = "";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        formattedDate = sdf.format(new Date(timestamp));
        return formattedDate;
    }
    
    public static String getGlobalBoolean(final String string) {
        if (string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("enabled") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("0")) {
            return "yes";
        }
        if (string.equalsIgnoreCase("no") || string.equalsIgnoreCase("disabled") || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("1")) {
            return "no";
        }
        return string;
    }
    
    public static int getDOSize(final DataObject dobj, final String tableName) {
        return getDOSize(dobj, tableName, null);
    }
    
    public static int getDOSize(final DataObject dobj, final String tableName, final Criteria criteria) {
        int size = 0;
        try {
            size = ((criteria != null) ? getIteratorSize(dobj.getRows(tableName, criteria)) : (dobj.isEmpty() ? 0 : dobj.size(tableName)));
        }
        catch (final Exception ex) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getDOSize", "Exception : ", (Throwable)ex);
        }
        return size;
    }
    
    public static Integer getIteratorSize(final Iterator itr) {
        Integer size = 0;
        while (itr.hasNext()) {
            itr.next();
            ++size;
        }
        return size;
    }
    
    public static int getConfigCount(final int configId, final int configType) {
        int ConfigCount = 0;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Collection"));
            query.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            Criteria crit = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configId, 0);
            crit = crit.and(new Criteria(Column.getColumn("ConfigData", "CONFIG_TYPE"), (Object)configType, 0));
            query.setCriteria(crit);
            ConfigCount = DBUtil.getRecordCount(query, "Collection", "COLLECTION_ID");
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getConfigCount for configId ::" + configId, "Exception : ", (Throwable)e);
        }
        return ConfigCount;
    }
    
    public static JSONObject createJSONObject(final Properties inputProps) {
        final String sourceMethod = "createJSONObject";
        String propKey = "";
        String propValue = "";
        final Iterator itr = inputProps.entrySet().iterator();
        final JSONObject requestDetails = new JSONObject();
        try {
            while (itr.hasNext()) {
                final Map.Entry entry = itr.next();
                propKey = entry.getKey().toString();
                propValue = entry.getValue().toString();
                requestDetails.put(propKey, (Object)propValue);
            }
            return requestDetails;
        }
        catch (final JSONException e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "JSONException : ", (Throwable)e);
            return requestDetails;
        }
        catch (final Exception e2) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Error: ", (Throwable)e2);
            return requestDetails;
        }
    }
    
    public static SelectQuery getConfigAlertCount(final int userType, final int platForm) {
        SelectQuery query = null;
        try {
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("AlertSettings"));
            Criteria joinCrt = new Criteria(Column.getColumn("AlertSettings", "ALERT_NAME"), (Object)"config-alert", 0);
            joinCrt = joinCrt.and(new Criteria(Column.getColumn("AlertSettings", "ALERT_PARAM_ID"), (Object)Column.getColumn("Collection", "COLLECTION_ID"), 0));
            joinCrt = joinCrt.and(new Criteria(new Column("Collection", "COLLECTION_TYPE"), (Object)userType, 0));
            joinCrt = joinCrt.and(new Criteria(new Column("Collection", "PLATFORM_ID"), (Object)platForm, 0));
            query.addJoin(new Join("AlertSettings", "Collection", joinCrt, 2));
            query.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria joinCrtConfig = new Criteria(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"), (Object)Column.getColumn("ConfigData", "CONFIG_DATA_ID"), 0);
            query.addJoin(new Join("CfgDataToCollection", "ConfigData", joinCrtConfig, 2));
            final Column notifyCollnCount = new Column("AlertSettings", "ALERT_PARAM_ID").distinct().count();
            final Column configData = new Column("ConfigData", "CONFIG_ID");
            query.addGroupByColumn(new Column("ConfigData", "CONFIG_ID"));
            query.addSelectColumn(configData);
            query.addSelectColumn(notifyCollnCount);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getConfigAlertCount for config notification enabled count", "Exception : ", (Throwable)e);
        }
        return query;
    }
    
    public static String getMEDCTrackId() {
        String meTrackID = "--";
        FileInputStream fin = null;
        try {
            final String baseDir = System.getProperty("server.home");
            final String confDir = baseDir + File.separator + "conf";
            final Properties prop = new Properties();
            fin = new FileInputStream(confDir + File.separator + "ZohoCreator.properties");
            prop.load(fin);
            meTrackID = ((Hashtable<K, Object>)prop).get("ID").toString();
        }
        catch (final IOException e) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getMEDCTrackId", "Exception occurred (Zoho Creator file missing) : ", (Throwable)e);
        }
        catch (final NullPointerException e2) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getMEDCTrackId", "Exception occurred (METracking Id not available) : ", (Throwable)e2);
        }
        catch (final Exception e3) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getMEDCTrackId", "Exception occurred : ", (Throwable)e3);
        }
        finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch (final IOException e4) {
                SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, "getMEDCTrackId", "IOException occurred : ", (Throwable)e4);
            }
        }
        return meTrackID;
    }
    
    public static JSONObject getMETrackParamsStartsWith(final String key) {
        final String sourceMethod = "getMETrackParamsStartsWith";
        final JSONObject meParamJSON = new JSONObject();
        try {
            final Column col = Column.getColumn("METrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)key, 10, false);
            final DataObject dobj = SyMUtil.getPersistence().get("METrackParams", criteria);
            final Iterator iterator = dobj.getRows("METrackParams");
            if (dobj.isEmpty()) {
                return meParamJSON;
            }
            while (iterator.hasNext()) {
                final Row meTrackParamRow = iterator.next();
                meParamJSON.put(String.valueOf(meTrackParamRow.get("PARAM_NAME")), (Object)String.valueOf(meTrackParamRow.get("PARAM_VALUE")));
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(METrackerUtil.logger, METrackerUtil.sourceClass, sourceMethod, "Exception occurred.", (Throwable)ex);
        }
        return meParamJSON;
    }
    
    static {
        METrackerUtil.logger = Logger.getLogger("METrackLog");
        METrackerUtil.sourceClass = "METrackerUtil";
    }
}
