package com.me.mdm.server.profiles.config;

import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProfileConfigurationUtil
{
    private Logger logger;
    private static JSONObject payloadConfiguration;
    private static JSONObject configPayloadRelation;
    private static JSONObject platformPayloadRelation;
    private static ProfileConfigurationUtil instance;
    private static List<String> excludedConfigPayloadRelationList;
    
    private ProfileConfigurationUtil() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
    }
    
    public static ProfileConfigurationUtil getInstance() {
        if (ProfileConfigurationUtil.instance == null) {
            ProfileConfigurationUtil.instance = new ProfileConfigurationUtil();
        }
        return ProfileConfigurationUtil.instance;
    }
    
    private void loadPayloadConfiguration() {
        this.logger.log(Level.INFO, "starting loading payload-config.json...");
        try {
            final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDMConfiguration" + File.separator + "profile-config.json";
            final File f = new File(filePath);
            if (f.exists()) {
                final InputStream is = new FileInputStream(filePath);
                final String jsonTxt = IOUtils.toString(is, "UTF-8");
                ProfileConfigurationUtil.payloadConfiguration = new JSONObject(jsonTxt);
                final Iterator payloadIterator = ProfileConfigurationUtil.payloadConfiguration.keys();
                ProfileConfigurationUtil.configPayloadRelation = new JSONObject();
                while (payloadIterator.hasNext()) {
                    final String key = payloadIterator.next();
                    if (!ProfileConfigurationUtil.excludedConfigPayloadRelationList.contains(key)) {
                        final JSONObject payloadJson = ProfileConfigurationUtil.payloadConfiguration.getJSONObject(key);
                        ProfileConfigurationUtil.configPayloadRelation.put(String.valueOf(payloadJson.get("config_name")), (Object)key);
                    }
                }
                this.logger.log(Level.INFO, "completed loading payload-config.json...");
            }
            else {
                this.logger.log(Level.SEVERE, "profile-config.json does not exist!!!!!!");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Loading payload-config.json failed .....", e);
        }
    }
    
    public JSONObject getPayloadConfiguration(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getPayloadConfiguration...", (Throwable)e);
        }
        return null;
    }
    
    public JSONArray getPayloadConfigurationProperties(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).getJSONArray("properties");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getPayloadConfiguration...", (Throwable)e);
        }
        return null;
    }
    
    public Object getPayloadConfigurationHandler(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return Class.forName(String.valueOf(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).get("payload_handler"))).newInstance();
            }
        }
        catch (final JSONException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getPayloadConfiguration...", e);
        }
        return null;
    }
    
    public JSONObject getPayloadConfiguration() {
        return ProfileConfigurationUtil.payloadConfiguration;
    }
    
    public String getTableName(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return String.valueOf(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).get("table_name"));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getTableName...", (Throwable)e);
        }
        return null;
    }
    
    public String getBeanName(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return String.valueOf(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).get("bean_name"));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getBeanName...", (Throwable)e);
        }
        return null;
    }
    
    public String getConfigType(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return String.valueOf(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).get("config_type"));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getConfigType...", (Throwable)e);
        }
        return null;
    }
    
    public Integer getConfigID(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).getInt("config_id");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getConfigID...", (Throwable)e);
        }
        return null;
    }
    
    public String getConfigDataIdentifier(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return String.valueOf(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).get("config_data_identifier"));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getConfigDataIdentifier...", (Throwable)e);
        }
        return null;
    }
    
    public String getConfigurationName(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return String.valueOf(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).get("config_name"));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getConfigurationName...", (Throwable)e);
        }
        return null;
    }
    
    public JSONObject getConfigPayloadRelation() {
        return ProfileConfigurationUtil.configPayloadRelation;
    }
    
    public String getPayloadName(final String configName) {
        if (ProfileConfigurationUtil.configPayloadRelation == null || ProfileConfigurationUtil.configPayloadRelation.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.configPayloadRelation.has(configName)) {
                return String.valueOf(ProfileConfigurationUtil.configPayloadRelation.get(configName));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getPayloadName...", (Throwable)e);
        }
        return null;
    }
    
    public List getSupportedPlatform(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return JSONUtil.convertJSONArrayToList(ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).getJSONArray("platform_type"));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getPlatformType...", (Throwable)e);
        }
        return null;
    }
    
    public JSONArray getConfigIdForPlatform(final int platformType) {
        if (ProfileConfigurationUtil.platformPayloadRelation == null || ProfileConfigurationUtil.platformPayloadRelation.length() == 0) {
            this.loadPlatformPayloadRelation();
        }
        return ProfileConfigurationUtil.platformPayloadRelation.optJSONArray(String.valueOf(platformType));
    }
    
    private void loadPlatformPayloadRelation() {
        try {
            if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
                this.loadPayloadConfiguration();
            }
            ProfileConfigurationUtil.platformPayloadRelation = new JSONObject();
            final Iterator configurationIterator = ProfileConfigurationUtil.payloadConfiguration.keys();
            while (configurationIterator.hasNext()) {
                final String key = configurationIterator.next();
                final JSONObject payloadJSON = ProfileConfigurationUtil.payloadConfiguration.getJSONObject(key);
                final Integer platformType = payloadJSON.getInt("platform_type");
                final Integer configId = payloadJSON.getInt("config_id");
                JSONArray platformArray = ProfileConfigurationUtil.platformPayloadRelation.optJSONArray(platformType.toString());
                if (platformArray == null) {
                    platformArray = new JSONArray();
                    platformArray.put((Object)configId);
                    ProfileConfigurationUtil.platformPayloadRelation.put(platformType.toString(), (Object)platformArray);
                }
                else {
                    platformArray.put((Object)configId);
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getPlatformType...", (Throwable)e);
        }
    }
    
    public Integer getAllowedCount(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).getInt("allowed_count");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getAllowedCount...", (Throwable)e);
        }
        return null;
    }
    
    public Boolean checkFileExists(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).optBoolean("file_exists");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getAllowedCount...", (Throwable)e);
        }
        return Boolean.FALSE;
    }
    
    public Boolean checkIfSubPayloadExists(final String payloadName) {
        if (ProfileConfigurationUtil.payloadConfiguration == null || ProfileConfigurationUtil.payloadConfiguration.length() == 0) {
            this.loadPayloadConfiguration();
        }
        try {
            if (ProfileConfigurationUtil.payloadConfiguration.has(payloadName)) {
                return ProfileConfigurationUtil.payloadConfiguration.getJSONObject(payloadName).optBoolean("subpayload_exists");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getAllowedCount...", (Throwable)e);
        }
        return Boolean.FALSE;
    }
    
    static {
        ProfileConfigurationUtil.payloadConfiguration = null;
        ProfileConfigurationUtil.configPayloadRelation = null;
        ProfileConfigurationUtil.platformPayloadRelation = null;
        ProfileConfigurationUtil.excludedConfigPayloadRelationList = new ArrayList<String>() {
            {
                this.add("applockhomescreenpolicy");
                this.add("androidkioskhomescreenpolicy");
            }
        };
    }
}
