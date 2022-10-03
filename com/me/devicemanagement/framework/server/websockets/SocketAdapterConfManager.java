package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import com.me.devicemanagement.framework.utils.JsonUtils;
import org.apache.commons.io.FilenameUtils;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public final class SocketAdapterConfManager
{
    private static Logger wsFrameworkLogger;
    private static final int DEFAULT_MAX_BINARY_BUFFER_SIZE = 900000;
    private static final int DEFAULT_MAX_TEXT_BUFFER_SIZE = 2000000;
    private static final int DEFAULT_TICKET_EXPIRY_TIME = 1;
    private int maxBinaryBufferSize;
    private int maxTextBufferSize;
    private int ticketExpiryTime;
    private JSONObject maxBinaryBufferSizeObj;
    private JSONObject maxTextBufferSizeObj;
    private JSONObject paramName;
    private JSONObject authType;
    
    public static SocketAdapterConfManager getInstance() {
        return Loader.socketConfManagerInstance;
    }
    
    private SocketAdapterConfManager() {
        this.maxBinaryBufferSizeObj = new JSONObject();
        this.maxTextBufferSizeObj = new JSONObject();
        this.paramName = new JSONObject();
        this.authType = new JSONObject();
        this.maxBinaryBufferSize = 900000;
        this.maxTextBufferSize = 2000000;
        this.ticketExpiryTime = 1;
        this.loadWSSettingsConfData();
        this.loadWSConfForClientType();
    }
    
    private void loadWSSettingsConfData() {
        SocketAdapterConfManager.wsFrameworkLogger.log(Level.FINEST, "Entered into loadWSSettingsConfData method");
        try {
            final Properties confProps = new Properties();
            final InputStream inputStream = new FileInputStream(Constants.ConfConstants.WS_CONF_FILE_PATH);
            confProps.load(inputStream);
            final String strMaxBinaryBufferSize = ((Hashtable<K, String>)confProps).get("MaxBinaryBufferSize");
            final String strMaxTextBufferSize = ((Hashtable<K, String>)confProps).get("MaxTextBufferSize");
            final String strTicketExpiryTime = ((Hashtable<K, String>)confProps).get("TicketExpiryTime");
            try {
                if (strMaxBinaryBufferSize != null) {
                    this.maxBinaryBufferSize = Integer.valueOf(strMaxBinaryBufferSize);
                    SocketAdapterConfManager.wsFrameworkLogger.log(Level.INFO, "loadWSSettingsConfData: \tMaxBinaryBufferSize - " + this.maxBinaryBufferSize);
                }
            }
            catch (final Exception ex) {
                SocketAdapterConfManager.wsFrameworkLogger.log(Level.SEVERE, "loadWSSettingsConfData: Exception while reading maxBinaryBufferSize - Setting default value", ex);
            }
            try {
                if (strMaxTextBufferSize != null) {
                    this.maxTextBufferSize = Integer.valueOf(strMaxTextBufferSize);
                    SocketAdapterConfManager.wsFrameworkLogger.log(Level.INFO, "loadWSSettingsConfData: \tMaxTextBufferSize - " + this.maxTextBufferSize);
                }
            }
            catch (final Exception ex) {
                SocketAdapterConfManager.wsFrameworkLogger.log(Level.SEVERE, "loadWSSettingsConfData: Exception while reading maxBinaryBufferSize - Setting default value", ex);
            }
            try {
                if (strTicketExpiryTime != null) {
                    this.ticketExpiryTime = Integer.valueOf(strTicketExpiryTime);
                    SocketAdapterConfManager.wsFrameworkLogger.log(Level.INFO, "loadWSSettingsConfData: \tTicketExpiryTime - {0}", this.ticketExpiryTime);
                }
            }
            catch (final Exception ex) {
                SocketAdapterConfManager.wsFrameworkLogger.log(Level.SEVERE, "loadWSSettingsConfData: Exception while reading TicketExpiryTime - Setting default value", ex);
            }
        }
        catch (final Exception ex2) {
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while loading configuration entries from the file", ex2);
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.INFO, "Default value will be considered for unread values");
        }
    }
    
    private void loadWSConfForClientType() {
        SocketAdapterConfManager.wsFrameworkLogger.log(Level.FINEST, "Entered into loadWSConfForClientType method");
        try {
            String wsSettingsMapperfilePath = FrameworkConfigurations.getSpecificProperty("wssettings", "wssettings_mapper_filepath");
            final String serverHome = System.getProperty("server.home");
            wsSettingsMapperfilePath = serverHome + File.separator + wsSettingsMapperfilePath;
            if (new File(wsSettingsMapperfilePath).exists()) {
                final Properties filesList = FileAccessUtil.readProperties(wsSettingsMapperfilePath);
                if (filesList != null) {
                    final Set<String> filesSet = filesList.stringPropertyNames();
                    for (final String module : filesSet) {
                        final String filePath = serverHome + File.separator + filesList.getProperty(module);
                        if (new File(filePath).exists() && FilenameUtils.isExtension(filePath, "json")) {
                            final JSONObject jsonObject = JsonUtils.loadJsonFile(new File(filePath));
                            if (jsonObject.has("MaxBinaryBufferSize")) {
                                this.mergeJSON(this.maxBinaryBufferSizeObj, jsonObject.getJSONObject("MaxBinaryBufferSize"));
                            }
                            if (jsonObject.has("MaxTextBufferSize")) {
                                this.mergeJSON(this.maxTextBufferSizeObj, jsonObject.getJSONObject("MaxTextBufferSize"));
                            }
                            if (jsonObject.has("ParamName")) {
                                this.mergeJSON(this.paramName, jsonObject.getJSONObject("ParamName"));
                            }
                            if (jsonObject.has("AuthType")) {
                                this.mergeJSON(this.authType, jsonObject.getJSONObject("AuthType"));
                            }
                            SocketAdapterConfManager.wsFrameworkLogger.log(Level.INFO, "WSSettings for client type is loaded for {0} module from file {1}", new Object[] { module, filePath });
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.WARNING, "Exception While loading WSSettings for client type", e);
        }
    }
    
    private JSONObject mergeJSON(final JSONObject base, final JSONObject merge) throws Exception {
        final Iterator itr = merge.keys();
        while (itr.hasNext()) {
            final String key = itr.next().toString();
            if (base.has(key)) {
                SocketAdapterConfManager.wsFrameworkLogger.log(Level.WARNING, "Client Type: " + key + "is already used.Use Unique key for Client Type");
            }
            else {
                base.put(key, merge.get(key));
            }
        }
        return base;
    }
    
    public int getMaxBinaryMessageBufferSize(final String clientType) {
        try {
            if (this.maxBinaryBufferSizeObj.has(clientType)) {
                return Integer.parseInt((String)this.maxBinaryBufferSizeObj.get(clientType));
            }
        }
        catch (final Exception e) {
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.WARNING, "Exception while getting maxBinaryMessageBufferSize for clientType", e);
        }
        return this.maxBinaryBufferSize;
    }
    
    public int getMaxTextMessageBufferSize(final String clientType) {
        try {
            if (this.maxTextBufferSizeObj.has(clientType)) {
                return Integer.parseInt((String)this.maxTextBufferSizeObj.get(clientType));
            }
        }
        catch (final Exception e) {
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.WARNING, "Exception while getting maxTextMessageBufferSize for clientType", e);
        }
        return this.maxTextBufferSize;
    }
    
    public int getTicketExpiryTime() {
        return this.ticketExpiryTime;
    }
    
    public boolean getAuthType(final String clientType) throws IOException {
        boolean authType = false;
        try {
            if (this.authType.has(clientType)) {
                authType = Boolean.parseBoolean((String)this.authType.get(clientType));
            }
        }
        catch (final Exception e) {
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.WARNING, "Exception while getting authType for clientType", e);
        }
        return authType;
    }
    
    public String getParamName(final String clientType) throws IOException {
        String paramName = null;
        try {
            if (this.paramName.has(clientType)) {
                paramName = (String)this.paramName.get(clientType);
            }
        }
        catch (final Exception e) {
            SocketAdapterConfManager.wsFrameworkLogger.log(Level.WARNING, "Exception while getting paramName for clientType", e);
        }
        return paramName;
    }
    
    static {
        SocketAdapterConfManager.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
    
    private static class Loader
    {
        static SocketAdapterConfManager socketConfManagerInstance;
        
        static {
            Loader.socketConfManagerInstance = new SocketAdapterConfManager(null);
        }
    }
}
