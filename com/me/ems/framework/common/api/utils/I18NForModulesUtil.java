package com.me.ems.framework.common.api.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.reflect.Field;
import java.util.ResourceBundle;
import java.io.InputStream;
import com.adventnet.i18n.MultiplePropertiesResourceBundleControl;
import java.util.Properties;
import java.util.AbstractMap;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.Locale;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.HashSet;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Set;
import java.util.Map;

public class I18NForModulesUtil
{
    private static Map<Map.Entry<String, Type>, Set<String>> moduleToI18KeysNMap;
    private static Logger logger;
    
    public static Map<String, String> getI18NForModules(final List<String> modules, Boolean isServer, Boolean isClient) throws Exception {
        final Map<String, String> i18NMap = new HashMap<String, String>();
        try {
            if (I18NForModulesUtil.moduleToI18KeysNMap.isEmpty()) {
                fetchI18NKeysForModules();
            }
            final Map<String, String> allI18NKeys = getAllI18NKeys(ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale());
            if ((modules == null || modules.isEmpty()) && isServer == null && isClient == null) {
                return allI18NKeys;
            }
            if (isServer == null && isClient == null) {
                isServer = Boolean.TRUE;
                isClient = Boolean.TRUE;
            }
            final String productName = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("displayname");
            for (final Map.Entry<String, Type> moduleToType : I18NForModulesUtil.moduleToI18KeysNMap.keySet()) {
                if ((modules == null || modules.contains(moduleToType.getKey()) || modules.contains("all") || modules.isEmpty()) && isServer != null && isServer && moduleToType.getValue() == Type.SERVER) {
                    I18NForModulesUtil.moduleToI18KeysNMap.getOrDefault(moduleToType, new HashSet<String>()).forEach(key -> {
                        String val = map.getOrDefault(key, key);
                        if (val.contains("{productName}")) {
                            val = val.replaceAll("\\{productName}", s);
                        }
                        map2.put(key, val);
                        return;
                    });
                }
                else {
                    if ((modules != null && !modules.contains(moduleToType.getKey()) && !modules.contains("all") && !modules.isEmpty()) || isClient == null || !isClient || moduleToType.getValue() != Type.CLIENT) {
                        continue;
                    }
                    I18NForModulesUtil.moduleToI18KeysNMap.getOrDefault(moduleToType, new HashSet<String>()).forEach(key -> {
                        String val2 = map3.getOrDefault(key, key);
                        if (val2.contains("{productName}")) {
                            val2 = val2.replaceAll("\\{productName}", s2);
                        }
                        map4.put(key, val2);
                        return;
                    });
                }
            }
        }
        catch (final Exception ex) {
            I18NForModulesUtil.logger.log(Level.INFO, "Exception while fetching I18N keys.", ex);
            throw ex;
        }
        return i18NMap;
    }
    
    private static void fetchI18NKeysForModules() throws Exception {
        final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
        final Map<String, String> allI18NKeys = getAllI18NKeys(locale);
        fetchI18NForModules(allI18NKeys);
    }
    
    private static void fetchI18NForModules(final Map<String, String> allI18NKeys) throws Exception {
        final String resourceBundleOrderPropsPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "resourceBundleOrder.properties";
        final Properties resourceBundleOrderProps = FileAccessUtil.readProperties(resourceBundleOrderPropsPath);
        for (int order = 1; order <= resourceBundleOrderProps.size(); ++order) {
            final String resourcePath = resourceBundleOrderProps.getProperty("order" + order);
            final String module = resourcePath.replaceFirst("resources.", "");
            final MultiplePropertiesResourceBundleControl multiplePropertiesResourceBundleControl = ApiFactoryProvider.getUtilAccessAPI().getMultipleResourceBundleControl();
            InputStream inputStream = null;
            inputStream = multiplePropertiesResourceBundleControl.getInputStream(resourcePath + ".ApplicationResources", Locale.US, ClassLoader.getSystemClassLoader(), false);
            if (inputStream != null) {
                final ResourceBundle resourceBundle = new PropertyResourceBundle(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                final Field lookup = resourceBundle.getClass().getDeclaredField("lookup");
                lookup.setAccessible(true);
                final Map<String, String> i18NMap = (Map<String, String>)lookup.get(resourceBundle);
                final Set<String> resultI18NKeys = new HashSet<String>();
                for (final String key : i18NMap.keySet()) {
                    if (allI18NKeys.containsKey(key)) {
                        resultI18NKeys.add(key);
                    }
                }
                I18NForModulesUtil.moduleToI18KeysNMap.put(new AbstractMap.SimpleEntry<String, Type>(module, Type.SERVER), resultI18NKeys);
            }
            inputStream = multiplePropertiesResourceBundleControl.getInputStream(resourcePath + ".JSApplicationResources", Locale.US, ClassLoader.getSystemClassLoader(), false);
            if (inputStream != null) {
                final ResourceBundle resourceBundle = new PropertyResourceBundle(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                final Field lookup = resourceBundle.getClass().getDeclaredField("lookup");
                lookup.setAccessible(true);
                final Map<String, String> i18NMap = (Map<String, String>)lookup.get(resourceBundle);
                final Set<String> resultI18NKeys = new HashSet<String>();
                for (final String key : i18NMap.keySet()) {
                    if (allI18NKeys.containsKey(key)) {
                        resultI18NKeys.add(key);
                    }
                }
                I18NForModulesUtil.moduleToI18KeysNMap.put(new AbstractMap.SimpleEntry<String, Type>(module, Type.CLIENT), resultI18NKeys);
            }
        }
    }
    
    private static Map<String, String> getAllI18NKeys(final Locale locale) throws Exception {
        final ResourceBundle resourceBundle = ApiFactoryProvider.getUtilAccessAPI().newCombinedBundle(locale, ClassLoader.getSystemClassLoader(), false);
        final Field lookup = resourceBundle.getClass().getDeclaredField("lookup");
        lookup.setAccessible(true);
        final Map<String, String> i18NMap = (Map<String, String>)lookup.get(resourceBundle);
        if (!locale.equals(Locale.US)) {
            final ResourceBundle resourceBundleEn = ApiFactoryProvider.getUtilAccessAPI().newCombinedBundle(Locale.US, ClassLoader.getSystemClassLoader(), false);
            final Field lookupEn = resourceBundleEn.getClass().getDeclaredField("lookup");
            lookupEn.setAccessible(true);
            final Map<String, String> i18NMapEn = (Map<String, String>)lookupEn.get(resourceBundleEn);
            final Map<String, String> resultMap = Stream.concat(i18NMapEn.entrySet().stream(), i18NMap.entrySet().stream()).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (i18NKeyEn, i18NKey) -> i18NKey));
            return resultMap;
        }
        return i18NMap;
    }
    
    static {
        I18NForModulesUtil.moduleToI18KeysNMap = new HashMap<Map.Entry<String, Type>, Set<String>>();
        I18NForModulesUtil.logger = Logger.getLogger(I18NForModulesUtil.class.getName());
    }
    
    private enum Type
    {
        SERVER, 
        CLIENT;
    }
}
