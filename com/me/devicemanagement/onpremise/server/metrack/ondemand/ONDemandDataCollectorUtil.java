package com.me.devicemanagement.onpremise.server.metrack.ondemand;

import java.util.Hashtable;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.simple.JSONArray;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class ONDemandDataCollectorUtil
{
    private static Logger logger;
    private static String sourceClass;
    private static ONDemandDataCollectorUtil onDemandDataCollectorUtil;
    private static Properties onDemandDataCollectorConf;
    
    public static ONDemandDataCollectorUtil getInstance() {
        if (ONDemandDataCollectorUtil.onDemandDataCollectorUtil == null) {
            ONDemandDataCollectorUtil.onDemandDataCollectorUtil = new ONDemandDataCollectorUtil();
        }
        return ONDemandDataCollectorUtil.onDemandDataCollectorUtil;
    }
    
    public String getZCApplicationName() {
        return this.getValueFromDataColleatorProductspecificConf("zc_application_name");
    }
    
    public String getTasksZCViewName() {
        return this.getValueFromDataColleatorProductspecificConf("tasks_zcview_name");
    }
    
    public String getZCKey() {
        return this.getValueFromDataColleatorProductspecificConf("zc_key");
    }
    
    public String getZCOwnerName() {
        return this.getValueFromDataColleatorProductspecificConf("zc_owner_name");
    }
    
    public String getZCCriteriaColumnName() {
        return this.getValueFromDataColleatorProductspecificConf("zc_criteria_column_name");
    }
    
    public String getLastTasksGetTime() {
        try {
            final Properties properties = FileAccessUtil.readProperties(this.getDataColleatorCustomerspecificConfDir());
            if (properties.containsKey("last_tasks_fetch_time") && !"".equalsIgnoreCase(((Hashtable<K, Object>)properties).get("last_tasks_fetch_time").toString().trim())) {
                final String lastTasksFetchTime = ((Hashtable<K, Object>)properties).get("last_tasks_fetch_time").toString().trim();
                final Long lastTasksFetchTimeAsLong = Long.valueOf(lastTasksFetchTime);
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                return simpleDateFormat.format(lastTasksFetchTimeAsLong);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollectorUtil.logger, ONDemandDataCollectorUtil.sourceClass, "getLastTasksGetTime", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    public boolean ifAValueExistsINJSONArray(final JSONArray jsonArray, final String value) {
        return jsonArray.contains((Object)value);
    }
    
    public void updateLastTasksGetTime(final Long time) {
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("last_tasks_fetch_time", String.valueOf(time));
            FileAccessUtil.storeProperties(properties, this.getDataColleatorCustomerspecificConfDir(), true);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollectorUtil.logger, ONDemandDataCollectorUtil.sourceClass, "updateLastTasksGetTime", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public String getValueFromDataColleatorProductspecificConf(final String key) {
        try {
            if (ONDemandDataCollectorUtil.onDemandDataCollectorConf == null || ONDemandDataCollectorUtil.onDemandDataCollectorConf.isEmpty()) {
                ONDemandDataCollectorUtil.onDemandDataCollectorConf = FileAccessUtil.readProperties(this.getDataColleatorProductspecificConfDir());
            }
            if (ONDemandDataCollectorUtil.onDemandDataCollectorConf.containsKey(key) && !"".equalsIgnoreCase(((Hashtable<K, Object>)ONDemandDataCollectorUtil.onDemandDataCollectorConf).get(key).toString())) {
                return ((Hashtable<K, Object>)ONDemandDataCollectorUtil.onDemandDataCollectorConf).get(key).toString().trim();
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollectorUtil.logger, ONDemandDataCollectorUtil.sourceClass, "getValueFromDataColleatorProductspecificConf", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    public boolean isONDemandDataCollectorEnabled() {
        try {
            if (this.getZCApplicationName() != null && this.getTasksZCViewName() != null && this.getZCKey() != null && this.getZCOwnerName() != null && this.getZCCriteriaColumnName() != null) {
                return true;
            }
            SyMLogger.info(ONDemandDataCollectorUtil.logger, ONDemandDataCollectorUtil.sourceClass, "isONDemandDataCollectorEnabled", "ondemanad data collector settings is not properly configured");
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollectorUtil.logger, ONDemandDataCollectorUtil.sourceClass, "isONDemandDataCollectorEnabled", "Exception occurred : ", (Throwable)e);
        }
        return false;
    }
    
    public String getFileFullDir(final String fileDirFromServerHome) {
        return ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + fileDirFromServerHome;
    }
    
    public String getMETrackDir() {
        return ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "METracking";
    }
    
    public String getDataColleatorProductspecificConfDir() {
        return this.getONDemandDataCollectorDir() + File.separator + "datacollector-productspecific.conf";
    }
    
    public String getDataColleatorCustomerspecificConfDir() {
        return this.getONDemandDataCollectorDir() + File.separator + "datacollector-customerspecific.conf";
    }
    
    public String getONDemandDataCollectorDir() {
        return this.getMETrackDir() + File.separator + "ONDemand";
    }
    
    static {
        ONDemandDataCollectorUtil.logger = Logger.getLogger("METrackLog");
        ONDemandDataCollectorUtil.sourceClass = "ONDemandDataCollectorUtil";
        ONDemandDataCollectorUtil.onDemandDataCollectorUtil = null;
        ONDemandDataCollectorUtil.onDemandDataCollectorConf = new Properties();
    }
}
