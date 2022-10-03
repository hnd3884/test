package com.me.ems.framework.server.quicklaunch.core;

import java.util.regex.Matcher;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import com.adventnet.i18n.I18N;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.nio.file.Paths;
import java.util.List;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Level;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.parser.JSONParser;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import java.util.Map;

public class QuickLaunchUtil
{
    private static Map<String, Object> memCache;
    private static JSONArray configArray;
    private static Map<String, QuickLaunchHandler> handlerObjectMap;
    private static Logger logger;
    
    public static JSONArray getConfigArray() {
        if (QuickLaunchUtil.configArray == null) {
            try {
                final JSONParser parser = new JSONParser();
                QuickLaunchUtil.configArray = (JSONArray)parser.parse((Reader)new FileReader(SyMUtil.getInstallationDir().concat(File.separator).concat("conf/quicklaunch-configurations.json")));
            }
            catch (final Exception ex) {
                QuickLaunchUtil.logger.log(Level.SEVERE, "Exception in getting quickLaunchConfigurations ", ex);
            }
        }
        return QuickLaunchUtil.configArray;
    }
    
    public static List<Map<String, Object>> getQuickLaunchLinks(final String basePath, final User user) throws Exception {
        final String fullPath = SyMUtil.getInstallationDir().concat(File.separator).concat(basePath);
        final String fullDirectoryPath = Paths.get(fullPath, new String[0]).getParent().toAbsolutePath().toString();
        final JSONParser parser = new JSONParser();
        List<Map<String, Object>> moduleArray = QuickLaunchUtil.memCache.get(basePath);
        if (moduleArray == null) {
            try {
                moduleArray = (List)parser.parse((Reader)new FileReader(fullPath));
                QuickLaunchUtil.memCache.put(basePath, moduleArray);
            }
            catch (final FileNotFoundException ex) {
                QuickLaunchUtil.logger.log(Level.SEVERE, "Exception in reading file in " + fullPath, ex);
                return Collections.emptyList();
            }
        }
        final List<Map<String, Object>> quickLaunchList = new ArrayList<Map<String, Object>>();
        for (final Map<String, Object> headerObject : moduleArray) {
            final Map<String, Object> finalAttributeMap = new HashMap<String, Object>();
            if (headerObject.containsKey("includeFile")) {
                final String filePath = headerObject.get("includeFile");
                final List<Map<String, Object>> moduleList = getQuickLaunchLinks(filePath, user);
                if (moduleList.isEmpty()) {
                    continue;
                }
                quickLaunchList.addAll(moduleList);
            }
            else {
                final Map<String, Object> finalData = isApplicable(headerObject, user);
                final boolean isApplicable = finalData.get("isApplicable");
                finalData.remove("isApplicable");
                if (!isApplicable) {
                    continue;
                }
                final String id = finalData.get("id");
                final String displayName = I18N.getMsg((String)finalData.get("displayName"), new Object[0]);
                final List<String> modulesList = finalData.get("moduleFiles");
                final List<Map<String, Object>> enabledModuleList = new ArrayList<Map<String, Object>>(modulesList.size());
                final String moduleFilePath = fullDirectoryPath.concat(File.separator).concat("modules").concat(File.separator);
                for (String file : modulesList) {
                    file = moduleFilePath.concat(file);
                    Map<String, Object> moduleObj = QuickLaunchUtil.memCache.get(file);
                    if (moduleObj == null) {
                        final FileReader fileReader = new FileReader(file);
                        moduleObj = (Map)parser.parse((Reader)fileReader);
                        QuickLaunchUtil.memCache.put(file, moduleObj);
                    }
                    final Map<String, Object> moduleMap = returnRoleAndEnvironmentBasedSubLinks(moduleObj, user);
                    if (!moduleMap.isEmpty()) {
                        enabledModuleList.add(moduleMap);
                    }
                }
                if (displayName != null) {
                    finalAttributeMap.put("displayName", displayName);
                }
                if (headerObject.containsKey("iconURL")) {
                    String iconURL = headerObject.get("iconURL");
                    if (!iconURL.isEmpty()) {
                        final Map<String, String> productProperties = SyMUtil.getProductLoaderProperties();
                        iconURL = replacePlaceHolders(iconURL, productProperties, Pattern.compile("\\{(.*?)}"));
                        finalAttributeMap.put("iconURL", iconURL);
                    }
                }
                finalAttributeMap.put("id", id);
                finalAttributeMap.put("subLinks", enabledModuleList);
                quickLaunchList.add(finalAttributeMap);
            }
        }
        return quickLaunchList;
    }
    
    private static Map<String, Object> returnRoleAndEnvironmentBasedSubLinks(final Map<String, Object> moduleObject, final User user) throws Exception {
        final Map<String, Object> clonedData = isApplicable(moduleObject, user);
        final boolean isApplicable = clonedData.get("isApplicable");
        clonedData.remove("isApplicable");
        if (isApplicable) {
            return processSubLinks(clonedData, user);
        }
        return Collections.emptyMap();
    }
    
    private static Map<String, Object> processSubLinks(final Map<String, Object> moduleObject, final User user) throws Exception {
        final Map<String, Object> subLinkMap = new HashMap<String, Object>();
        subLinkMap.put("id", moduleObject.get("id"));
        final String displayName = I18N.getMsg((String)moduleObject.get("displayName"), new Object[0]);
        if (displayName != null) {
            subLinkMap.put("displayName", displayName);
        }
        if (moduleObject.containsKey("subLinks")) {
            final List<Map<String, Object>> roleAndEnvironmentBasedLinks = new ArrayList<Map<String, Object>>();
            final List<Map<String, Object>> sublinksArray = moduleObject.get("subLinks");
            for (final Map<String, Object> subLinkObject : sublinksArray) {
                final Map<String, Object> moduleMap = returnRoleAndEnvironmentBasedSubLinks(subLinkObject, user);
                if (!moduleMap.isEmpty()) {
                    roleAndEnvironmentBasedLinks.add(moduleMap);
                }
            }
            subLinkMap.put("subLinks", roleAndEnvironmentBasedLinks);
        }
        if (moduleObject.containsKey("url")) {
            String url = moduleObject.get("url");
            if (url.contains("{") && url.contains("}")) {
                final Map<String, String> productProperties = SyMUtil.getProductLoaderProperties();
                url = replacePlaceHolders(url, productProperties, Pattern.compile("\\{(.*?)}"));
            }
            subLinkMap.put("url", url);
        }
        if (moduleObject.containsKey("iconURL")) {
            String iconURL = moduleObject.get("iconURL");
            if (!iconURL.isEmpty()) {
                final Map<String, String> productProperties = SyMUtil.getProductLoaderProperties();
                iconURL = replacePlaceHolders(iconURL, productProperties, Pattern.compile("\\{(.*?)}"));
                subLinkMap.put("iconURL", iconURL);
            }
        }
        return subLinkMap;
    }
    
    public static boolean checkLinkApplicableForUser(final User user, final String roles) {
        List<String> orConditionRoleList = null;
        boolean finalResult;
        if (roles.contains("&&")) {
            String conditionString = "";
            final List<String> andConditionRoleList = Arrays.stream(roles.split("&&")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
            for (final String orRole : andConditionRoleList) {
                if (orRole.contains(",")) {
                    orConditionRoleList = Arrays.stream(orRole.split(",")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
                    conditionString = orRole;
                }
            }
            andConditionRoleList.remove(conditionString);
            final AtomicBoolean negate = new AtomicBoolean(false);
            final boolean andCondition = andConditionRoleList.stream().peek(role -> atomicBoolean.set(role.contains("!"))).map(role -> role.replace("!", "")).allMatch(role -> user2.isUserInRole(role) != atomicBoolean2.get());
            if (orConditionRoleList != null) {
                final boolean orCondition = orConditionRoleList.stream().peek(role -> atomicBoolean3.set(role.contains("!"))).map(role -> role.replace("!", "")).map((Function<? super Object, ? extends Boolean>)user::isUserInRole).filter(isQuickLaunchApplicable -> isQuickLaunchApplicable != atomicBoolean4.get()).findFirst().orElse(false);
                finalResult = (orCondition && andCondition);
            }
            else {
                finalResult = andCondition;
            }
        }
        else {
            final AtomicBoolean negate2 = new AtomicBoolean(false);
            orConditionRoleList = Arrays.asList(roles.split(","));
            finalResult = orConditionRoleList.stream().peek(role -> atomicBoolean5.set(role.contains("!"))).map(role -> role.replace("!", "")).map((Function<? super Object, ? extends Boolean>)user::isUserInRole).filter(isQuickLaunchApplicable -> isQuickLaunchApplicable != atomicBoolean6.get()).findFirst().orElse(false);
        }
        return finalResult;
    }
    
    private static boolean handleEnvironment(final String environment) {
        if (environment.isEmpty()) {
            return true;
        }
        final List<String> allowedEnvironment = Arrays.stream(environment.split(",")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        final boolean isSAS = CustomerInfoUtil.isSAS();
        String currentEnvironment;
        if (isSAS && isMSP) {
            currentEnvironment = "SASMSP";
        }
        else if (isSAS) {
            currentEnvironment = "SAS";
        }
        else if (isMSP) {
            currentEnvironment = "MSP";
        }
        else {
            currentEnvironment = "Enterprise";
        }
        return allowedEnvironment.contains(currentEnvironment);
    }
    
    private static Map<String, Object> customCheck(final Map<String, Object> moduleObject, final User user) throws Exception {
        if (QuickLaunchUtil.handlerObjectMap == null) {
            QuickLaunchUtil.handlerObjectMap = new HashMap<String, QuickLaunchHandler>();
        }
        final String customCheckClass = moduleObject.get("customClass");
        if (customCheckClass != null && !customCheckClass.isEmpty()) {
            try {
                Map<String, Object> clonedData = moduleObject.entrySet().stream().collect(Collectors.toMap((Function<? super Object, ? extends String>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue, (key1, key2) -> key1));
                if (!QuickLaunchUtil.handlerObjectMap.containsKey(customCheckClass)) {
                    final QuickLaunchHandler customHandler = (QuickLaunchHandler)Class.forName(customCheckClass).newInstance();
                    if (customHandler == null) {
                        return clonedData;
                    }
                    QuickLaunchUtil.handlerObjectMap.put(customCheckClass, customHandler);
                }
                final QuickLaunchHandler customHandler = QuickLaunchUtil.handlerObjectMap.get(customCheckClass);
                clonedData = customHandler.customHandling(clonedData, user);
                clonedData.put("isApplicable", !clonedData.isEmpty());
                return clonedData;
            }
            catch (final Exception ex) {
                QuickLaunchUtil.logger.log(Level.SEVERE, "Exception caught in customCheck", ex);
                throw ex;
            }
        }
        return moduleObject;
    }
    
    private static boolean handleLicenseType(final String licenseTypes) {
        if (licenseTypes.isEmpty()) {
            return Boolean.TRUE;
        }
        final List<String> allowedLicenseType = Arrays.stream(licenseTypes.split(",")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
        final String currentLicenseType = LicenseProvider.getInstance().getLicenseType();
        return allowedLicenseType.contains(currentLicenseType);
    }
    
    private static boolean handleProductCodes(final String productCodes) {
        if (productCodes.isEmpty()) {
            return Boolean.TRUE;
        }
        final List<String> allowedProductCodes = Arrays.stream(productCodes.split(",")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
        final String currentProductCode = ProductUrlLoader.getInstance().getValue("productcode");
        return allowedProductCodes.contains(currentProductCode);
    }
    
    public static Map<String, Object> isApplicable(final Map<String, Object> moduleObject, final User user) throws Exception {
        final Map<String, Object> returnMap = customCheck(moduleObject, user);
        boolean isUserApplicable = !moduleObject.containsKey("roles") || checkLinkApplicableForUser(user, moduleObject.get("roles"));
        boolean isEnvironmentApplicable = !moduleObject.containsKey("environment") || handleEnvironment(moduleObject.get("environment"));
        final boolean customCheck = !moduleObject.containsKey("customClass") || returnMap.getOrDefault("isApplicable", Boolean.TRUE);
        boolean isLicenseApplicable = !moduleObject.containsKey("licenseType") || handleLicenseType(moduleObject.get("licenseType"));
        boolean isProductCodeApplicable = !moduleObject.containsKey("productCodes") || handleProductCodes(moduleObject.get("productCodes"));
        if (moduleObject.containsKey("negate")) {
            final String negateKeyString = moduleObject.get("negate");
            final List<String> negateKeys = Arrays.stream(negateKeyString.split(",")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
            for (final String s : negateKeys) {
                final String negateKey = s;
                switch (s) {
                    case "environment": {
                        isEnvironmentApplicable = !isEnvironmentApplicable;
                        continue;
                    }
                    case "licenseType": {
                        isLicenseApplicable = !isLicenseApplicable;
                        continue;
                    }
                    case "roles": {
                        isUserApplicable = !isUserApplicable;
                        continue;
                    }
                    case "productCodes": {
                        isProductCodeApplicable = !isProductCodeApplicable;
                        continue;
                    }
                }
            }
        }
        returnMap.put("isApplicable", isUserApplicable && isEnvironmentApplicable && isLicenseApplicable && customCheck && isProductCodeApplicable);
        return returnMap;
    }
    
    public static String replacePlaceHolders(String source, final Map<String, String> productProperties, final Pattern regexPattern) {
        try {
            final Matcher placeHolderMatcher = regexPattern.matcher(source);
            while (placeHolderMatcher.find()) {
                final String sourceString = placeHolderMatcher.group(0);
                String targetString = placeHolderMatcher.group(1);
                targetString = (targetString.equals("did") ? "DID" : targetString);
                source = source.replace(sourceString, productProperties.get(targetString));
            }
        }
        catch (final Exception ex) {
            QuickLaunchUtil.logger.log(Level.WARNING, "Exception occurred while fetching product properties ", ex);
        }
        return source;
    }
    
    static {
        QuickLaunchUtil.memCache = new HashMap<String, Object>();
        QuickLaunchUtil.logger = Logger.getLogger(QuickLaunchUtil.class.getName());
    }
}
