package com.me.mdm.server.profiles;

import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.HashSet;
import org.json.JSONException;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.devicemanagement.framework.server.config.ConfigUtil;
import org.json.JSONObject;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class MDMProfileResponseListenerHandler
{
    public static final int SUCCESS_HANDLER = 1;
    public static final int FAILURE_HANDLER = 2;
    public static final String IS_NOTIFY = "isNotify";
    public static final String COMMAND_UUIDS = "commandUUIDs";
    private static final Logger LOGGER;
    private static MDMProfileResponseListenerHandler listener;
    private static final HashMap<Integer, String> GET_CONFIG_LISTENER;
    private static final List<String> GENERAL_RESPONSE_LIST;
    private static final List<String> GENERAL_REMOVE_RESPONSE_LIST;
    private static final HashMap<Integer, String> GET_REMOVE_CONFIG_LISTENER;
    
    public static MDMProfileResponseListenerHandler getInstance() {
        if (MDMProfileResponseListenerHandler.listener == null) {
            MDMProfileResponseListenerHandler.listener = new MDMProfileResponseListenerHandler();
        }
        return MDMProfileResponseListenerHandler.listener;
    }
    
    public void invokeProfileListener(final JSONObject params) {
        try {
            final Long collectionId = Long.parseLong(params.optString("collectionId"));
            final Long resourceId = params.optLong("resourceId");
            final List configIdList = ConfigUtil.getConfigIds(collectionId);
            params.put("configList", (Object)configIdList);
            final List configDoList = MDMConfigUtil.getConfigurationDataItems(collectionId);
            params.put("configDoList", (Object)configDoList);
            final JSONObject commandObject = new JSONObject();
            boolean isNotify = false;
            final List<String> configClassList = new ArrayList<String>();
            if (configIdList != null && !configIdList.isEmpty()) {
                for (final Object configId : configIdList) {
                    final String className = MDMProfileResponseListenerHandler.GET_CONFIG_LISTENER.get(configId);
                    if (!MDMStringUtils.isEmpty(className)) {
                        configClassList.add(className);
                    }
                }
            }
            final JSONObject configObject = this.procressListeners(configClassList, params);
            this.addCommandUUIDObject(commandObject, configObject);
            if (configObject.optBoolean("isNotify", false)) {
                isNotify = true;
            }
            final JSONObject generalObject = this.procressListeners(MDMProfileResponseListenerHandler.GENERAL_RESPONSE_LIST, params);
            this.addCommandUUIDObject(commandObject, generalObject);
            if (generalObject.optBoolean("isNotify", false)) {
                isNotify = true;
            }
            this.addCommandToDevice(commandObject, isNotify, resourceId);
        }
        catch (final Exception ex) {
            MDMProfileResponseListenerHandler.LOGGER.log(Level.SEVERE, "Exception in invoker listner", ex);
        }
    }
    
    public void invokeRemoveProfileListener(final JSONObject params) {
        try {
            final Long collectionId = Long.parseLong(params.optString("collectionId"));
            final Long resourceId = params.optLong("resourceId");
            final List configIdList = ConfigUtil.getConfigIds(collectionId);
            params.put("configList", (Collection)configIdList);
            final List configDoList = MDMConfigUtil.getConfigurationDataItems(collectionId);
            params.put("configDoList", (Object)configDoList);
            boolean isNotify = false;
            final JSONObject commandObject = new JSONObject();
            final List<String> configClassList = new ArrayList<String>();
            if (configIdList != null && !configIdList.isEmpty()) {
                for (final Object configId : configIdList) {
                    final String className = MDMProfileResponseListenerHandler.GET_REMOVE_CONFIG_LISTENER.get(configId);
                    if (!MDMStringUtils.isEmpty(className)) {
                        configClassList.add(className);
                    }
                }
            }
            final JSONObject configObject = this.procressListeners(configClassList, params);
            this.addCommandUUIDObject(commandObject, configObject);
            if (configObject.optBoolean("isNotify", false)) {
                isNotify = true;
            }
            final JSONObject generalObject = this.procressListeners(MDMProfileResponseListenerHandler.GENERAL_REMOVE_RESPONSE_LIST, params);
            this.addCommandUUIDObject(commandObject, generalObject);
            if (generalObject.optBoolean("isNotify", false)) {
                isNotify = true;
            }
            this.addCommandToDevice(commandObject, isNotify, resourceId);
        }
        catch (final Exception ex) {
            MDMProfileResponseListenerHandler.LOGGER.log(Level.SEVERE, "Exception in invoker listner", ex);
        }
    }
    
    private JSONObject procressListeners(final List<String> classList, final JSONObject params) {
        final JSONObject processedObject = new JSONObject();
        try {
            for (final String className : classList) {
                try {
                    final int handler = params.optInt("handler");
                    if (MDMStringUtils.isEmpty(className)) {
                        continue;
                    }
                    boolean isNotify = false;
                    final MDMProfileResponseListener profileResponseListener = (MDMProfileResponseListener)Class.forName(className).newInstance();
                    JSONObject responseObject = null;
                    switch (handler) {
                        case 1: {
                            responseObject = profileResponseListener.successHandler(params);
                            break;
                        }
                        case 2: {
                            responseObject = profileResponseListener.failureHandler(params);
                            break;
                        }
                    }
                    isNotify = profileResponseListener.isNotify(params);
                    if (responseObject == null || responseObject.length() <= 0) {
                        continue;
                    }
                    final JSONObject commandUUIDsJSON = responseObject.optJSONObject("commandUUIDs");
                    if (commandUUIDsJSON == null || commandUUIDsJSON.length() <= 0) {
                        continue;
                    }
                    final Iterator commandIterator = commandUUIDsJSON.keys();
                    while (commandIterator.hasNext()) {
                        final String key = commandIterator.next();
                        final JSONArray commandUUIDs = commandUUIDsJSON.optJSONArray(key);
                        if (processedObject.has("commandUUIDs")) {
                            final JSONObject processedCommandObject = processedObject.optJSONObject("commandUUIDs");
                            if (processedCommandObject.has(key)) {
                                final JSONArray processedCommandArray = processedCommandObject.optJSONArray(key);
                                JSONUtil.putAll(processedCommandArray, commandUUIDs);
                            }
                            else {
                                processedCommandObject.put(key, (Object)commandUUIDs);
                            }
                        }
                        else {
                            final JSONObject commandJSON = new JSONObject();
                            commandJSON.put(key, (Object)commandUUIDs);
                            processedObject.put("commandUUIDs", (Object)commandJSON);
                        }
                    }
                    if (!isNotify) {
                        continue;
                    }
                    processedObject.put("isNotify", true);
                }
                catch (final ClassNotFoundException e) {
                    MDMProfileResponseListenerHandler.LOGGER.log(Level.INFO, "No class for profile Configuration");
                }
            }
        }
        catch (final Exception e2) {
            MDMProfileResponseListenerHandler.LOGGER.log(Level.SEVERE, "Exception in process Listeners", e2);
        }
        return processedObject;
    }
    
    private void addCommandUUIDObject(final JSONObject commandUUIDObject, final JSONObject listenerResponseObject) {
        try {
            final JSONObject configListenersCommandJSON = listenerResponseObject.optJSONObject("commandUUIDs");
            if (configListenersCommandJSON != null && configListenersCommandJSON.length() > 0) {
                final Iterator commandIterator = configListenersCommandJSON.keys();
                while (commandIterator.hasNext()) {
                    final String key = commandIterator.next();
                    final JSONArray configListenerCommandUUIDs = configListenersCommandJSON.optJSONArray(key);
                    if (configListenerCommandUUIDs != null && configListenerCommandUUIDs.length() > 0) {
                        if (commandUUIDObject.has(key)) {
                            final JSONArray commandUUIDS = commandUUIDObject.optJSONArray(key);
                            JSONUtil.putAll(commandUUIDS, configListenerCommandUUIDs);
                        }
                        else {
                            commandUUIDObject.put(key, (Object)configListenerCommandUUIDs);
                        }
                    }
                }
            }
        }
        catch (final JSONException e) {
            MDMProfileResponseListenerHandler.LOGGER.log(Level.SEVERE, "Exception in add command UUID object", (Throwable)e);
        }
    }
    
    private void addCommandToDevice(final JSONObject commandObject, final boolean isNotify, final Long resourceId) {
        try {
            if (commandObject.length() > 0) {
                final Iterator commandObjectIterator = commandObject.keys();
                while (commandObjectIterator.hasNext()) {
                    final String key = commandObjectIterator.next();
                    final JSONArray commandUUIDs = commandObject.optJSONArray(key);
                    if (commandUUIDs != null && commandUUIDs.length() > 0) {
                        final List<Long> commandList = new ArrayList<Long>();
                        final List commandUUIDsList = JSONUtil.getInstance().convertStringJSONArrayTOList(commandUUIDs);
                        final HashSet commandUUIDsHash = new HashSet(commandUUIDsList);
                        MDMProfileResponseListenerHandler.LOGGER.log(Level.INFO, "Going to add the commandUUID from response listener:{0}", new Object[] { commandUUIDsHash });
                        for (final String commandUUID : commandUUIDsHash) {
                            commandList.add(DeviceCommandRepository.getInstance().getCommandID(commandUUID));
                        }
                        if (commandList.size() <= 0) {
                            continue;
                        }
                        final List<Long> resourceList = new ArrayList<Long>();
                        resourceList.add(resourceId);
                        DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList, Integer.parseInt(key));
                    }
                }
            }
            if (isNotify) {
                final List<Long> resourceList2 = new ArrayList<Long>();
                resourceList2.add(resourceId);
                NotificationHandler.getInstance().SendNotification(resourceList2);
            }
        }
        catch (final Exception e) {
            MDMProfileResponseListenerHandler.LOGGER.log(Level.SEVERE, "Exception in adding commands to device", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        MDMProfileResponseListenerHandler.listener = null;
        GET_CONFIG_LISTENER = new HashMap<Integer, String>() {
            {
                this.put(173, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSRestrictionResponseListener");
                this.put(186, "com.me.mdm.server.profiles.android.configresponseprocessor.AndroidRestrictionResponseListener");
                this.put(604, "com.me.mdm.server.profiles.windows.configresponseprocessor.WindowsRestrictionResponseListener");
                this.put(515, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSCertificateResponseListener");
                this.put(772, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSCertificateResponseListener");
                this.put(555, "com.me.mdm.server.profiles.android.configresponseprocessor.AndroidCertificateResponseListener");
                this.put(607, "com.me.mdm.server.profiles.windows.configresponseprocessor.WindowsCertificateResponseListener");
                this.put(518, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSWallpaperResponseListener");
                this.put(770, "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFileVaultSuccessHandler");
                this.put(177, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSWifiProfileResponseListener");
                this.put(774, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSWifiProfileResponseListener");
                this.put(565, "com.me.mdm.server.profiles.android.configresponseprocessor.AndroidEFRPResponseListener");
                this.put(172, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSPasscodeResponseListener");
            }
        };
        GENERAL_RESPONSE_LIST = new ArrayList<String>() {
            {
                this.add("com.me.mdm.server.profiles.ios.configresponseprocessor.IOSWifiRestrictionResponseListener");
            }
        };
        GENERAL_REMOVE_RESPONSE_LIST = new ArrayList<String>() {
            {
                this.add("com.me.mdm.server.profiles.ios.configresponseprocessor.IOSWifiRestrictionResponseListener");
                this.add("com.me.mdm.server.profiles.ios.configresponseprocessor.IOSPasscodeRestrictionResponseListener");
            }
        };
        GET_REMOVE_CONFIG_LISTENER = new HashMap<Integer, String>() {
            {
                this.put(173, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSRemoveRestrictionResponseListener");
                this.put(186, "com.me.mdm.server.profiles.android.configresponseprocessor.AndroidRemoveRestrictionResponseListener");
                this.put(604, "com.me.mdm.server.profiles.windows.configresponseprocessor.WindowsRemoveRestrictionResponseListener");
                this.put(515, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSRemoveCertificateResponseListener");
                this.put(772, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSRemoveCertificateResponseListener");
                this.put(555, "com.me.mdm.server.profiles.android.configresponseprocessor.AndroidRemoveCertificateResponseListener");
                this.put(607, "com.me.mdm.server.profiles.windows.configresponseprocessor.WindowsRemoveCertificateResponseListener");
                this.put(770, "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFileVaultRemoveSuccessHandler");
                this.put(565, "com.me.mdm.server.profiles.android.configresponseprocessor.AndroidEFRPResponseListener");
                this.put(172, "com.me.mdm.server.profiles.ios.configresponseprocessor.IOSRemovePasscodeResponseListener");
            }
        };
    }
}
