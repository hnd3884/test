package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.JsonUtils;
import org.apache.commons.io.FilenameUtils;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

final class ClientRequestMapper
{
    private static Logger wsFrameworkLogger;
    private Map<String, String> clientHandlerMap;
    
    private ClientRequestMapper() {
        this.clientHandlerMap = new HashMap<String, String>();
        this.populateHandlerMap();
    }
    
    static ClientRequestMapper getInstance() {
        return Loader.clientReqMapperInstance;
    }
    
    private void populateHandlerMap() {
        ClientRequestMapper.wsFrameworkLogger.log(Level.FINEST, "Entered into populateHandlerMap method");
        try {
            String wsSettingsMapperfilePath = FrameworkConfigurations.getSpecificProperty("wssettings", "wssettings_mapper_filepath");
            final String serverHome = System.getProperty("server.home");
            wsSettingsMapperfilePath = serverHome + File.separator + wsSettingsMapperfilePath;
            final Properties filesList = FileAccessUtil.readProperties(wsSettingsMapperfilePath);
            if (filesList != null) {
                final Set<String> filesSet = filesList.stringPropertyNames();
                for (final String module : filesSet) {
                    final String filePath = serverHome + File.separator + filesList.getProperty(module);
                    if (new File(filePath).exists()) {
                        if (FilenameUtils.isExtension(filePath, "json")) {
                            final JSONObject jsonObject = JsonUtils.loadJsonFile(new File(filePath));
                            if (jsonObject != null && jsonObject.has("HandlerMapper")) {
                                final JSONObject handlerMapper = (JSONObject)jsonObject.get("HandlerMapper");
                                final Iterator keys = handlerMapper.keys();
                                while (keys.hasNext()) {
                                    final String key = keys.next().toString();
                                    if (this.clientHandlerMap.containsKey(key)) {
                                        ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "Client type: " + key + " is already used.Use unique key for client Type");
                                    }
                                    else {
                                        this.clientHandlerMap.put(key, handlerMapper.get(key).toString());
                                    }
                                }
                                ClientRequestMapper.wsFrameworkLogger.log(Level.INFO, "Handler map has been populated for module: " + module + " from file: " + filePath);
                            }
                            else {
                                ClientRequestMapper.wsFrameworkLogger.log(Level.WARNING, "Handler mapper is empty");
                            }
                        }
                        else {
                            final Properties confProps = FileAccessUtil.readProperties(filePath);
                            final Set<Object> confSet = ((Hashtable<Object, V>)confProps).keySet();
                            if (confSet.isEmpty()) {
                                ClientRequestMapper.wsFrameworkLogger.log(Level.WARNING, "Handler mapper is empty");
                            }
                            else {
                                for (final Object key2 : confSet) {
                                    if (this.clientHandlerMap.containsKey(String.valueOf(key2))) {
                                        ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "Client type: " + key2 + " is already used.Use unique key for client Type");
                                    }
                                    this.clientHandlerMap.put(String.valueOf(key2), String.valueOf(((Hashtable<K, Object>)confProps).get(key2)));
                                }
                                ClientRequestMapper.wsFrameworkLogger.log(Level.INFO, "Handler map has been populated for module: " + module + " from file " + filePath);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "Exception while populating the handler map from the file", ex);
        }
    }
    
    private String getClientHandler(final String clientType) {
        String handler = null;
        if (!this.clientHandlerMap.isEmpty() && clientType != null) {
            handler = this.clientHandlerMap.get(clientType);
            if (handler == null) {
                ClientRequestMapper.wsFrameworkLogger.log(Level.WARNING, "ClientType not present in the ClientHandler map");
            }
        }
        else if (clientType == null) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.WARNING, "getClientHandler: ClientType passed is NULL");
        }
        else {
            ClientRequestMapper.wsFrameworkLogger.log(Level.WARNING, "getClientHandler: Handler Map is Empty");
        }
        return handler;
    }
    
    ClientManager createClientManager(final ClientDetails clientDetails) {
        ClientManager clientManager = null;
        try {
            final Class cliMgrClass = Class.forName(this.getClientHandler(clientDetails.clientType));
            final Constructor cliMgrCtr = cliMgrClass.getConstructor(ClientDetails.class);
            final Object[] parameters = { clientDetails };
            final Object cliMgrInstance = cliMgrCtr.newInstance(parameters);
            clientManager = (ClientManager)cliMgrInstance;
            ClientRequestMapper.wsFrameworkLogger.log(Level.INFO, "ClientManager instance created for Client " + clientDetails.clientId);
        }
        catch (final ClassNotFoundException ex) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "ClassNotFoundException while creating the ClientManager instance", ex);
        }
        catch (final NoSuchMethodException ex2) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "NoSuchMethodException while creating the ClientManager instance", ex2);
        }
        catch (final SecurityException ex3) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "SecurityException while creating the ClientManager instance", ex3);
        }
        catch (final InstantiationException ex4) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "InstantiationException while creating the ClientManager instance", ex4);
        }
        catch (final IllegalAccessException ex5) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "IllegalAccessException while creating the ClientManager instance", ex5);
        }
        catch (final IllegalArgumentException ex6) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "IllegalArgumentException while creating the ClientManager instance", ex6);
        }
        catch (final InvocationTargetException ex7) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "InvocationTargeteException while creating the ClientManager instance", ex7);
        }
        catch (final Exception ex8) {
            ClientRequestMapper.wsFrameworkLogger.log(Level.SEVERE, "Exception while creating the ClientManager instance", ex8);
        }
        return clientManager;
    }
    
    static {
        ClientRequestMapper.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
    
    private static class Loader
    {
        static ClientRequestMapper clientReqMapperInstance;
        
        static {
            Loader.clientReqMapperInstance = new ClientRequestMapper(null);
        }
    }
}
