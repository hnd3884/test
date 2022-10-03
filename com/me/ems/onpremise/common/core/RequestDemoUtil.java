package com.me.ems.onpremise.common.core;

import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.HashMap;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.CreatorDataPost;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.onpremise.server.metrack.EvaluatorTrackerUtil;
import java.util.Map;
import java.util.logging.Logger;

public class RequestDemoUtil implements RequestDemoHandler
{
    private static Logger logger;
    private Map<String, String> countriesConfData;
    private static EvaluatorTrackerUtil trackerUtil;
    private static EvaluatorAPI evaluatorAPI;
    
    public RequestDemoUtil() {
        this.countriesConfData = new LinkedHashMap<String, String>();
    }
    
    public static void skipRequestDemoPage(final Long loginID) {
        RequestDemoUtil.trackerUtil.addOrIncrementClickCountForTrialUsers("SkipRequestDemoCount", String.valueOf(loginID));
        try {
            final Long userID = DMUserHandler.getUserIdForLoginId(loginID);
            if (SyMUtil.getUserParameter(userID, "requestDemoSkippedTime") == null) {
                SyMUtil.updateUserParameter(userID, "requestDemoSkippedTime", String.valueOf(System.currentTimeMillis()));
            }
        }
        catch (final Exception ex) {
            RequestDemoUtil.logger.log(Level.SEVERE, "Exception in updating userParameter : ", ex);
        }
        RequestDemoUtil.logger.log(Level.INFO, " Skip Count updated in JSON ");
    }
    
    public static void neverShowRequestDemoPageAgain(final Long loginID) {
        RequestDemoUtil.trackerUtil.addOrIncrementClickCountForTrialUsers("neverShowAgain", loginID.toString());
        RequestDemoUtil.logger.log(Level.INFO, " Never show again is made for request demo page ");
    }
    
    public static Boolean isRequestDemoPageNeeded(final Long loginID) throws Exception {
        final JSONObject demoRegisteredUsersJSON = RequestDemoUtil.evaluatorAPI.getJSONFromFileForModule("RequestDemoRegisteredUser");
        final int demoRegisteredUsersfromJSON = RequestDemoUtil.trackerUtil.getCount(String.valueOf(loginID), "RequestDemoRegisteredUser");
        if (!demoRegisteredUsersJSON.has(String.valueOf(loginID)) && demoRegisteredUsersfromJSON == 0) {
            final JSONObject skipDemoJSON = RequestDemoUtil.evaluatorAPI.getJSONFromFileForModule("SkipRequestDemoCount");
            final JSONObject neverShowDemoFile = RequestDemoUtil.evaluatorAPI.getJSONFromFileForModule("neverShowAgain");
            final int neverShowDemoJSON = RequestDemoUtil.trackerUtil.getCount(String.valueOf(loginID), "neverShowAgain");
            if (!neverShowDemoFile.has(String.valueOf(loginID)) && neverShowDemoJSON == 0 && !isWithinSkipTime(loginID)) {
                int skipCountFromFile = 0;
                final int skipCountFromJSON = RequestDemoUtil.trackerUtil.getCount(String.valueOf(loginID), "SkipRequestDemoCount");
                if (skipDemoJSON.has(String.valueOf(loginID))) {
                    skipCountFromFile = skipDemoJSON.optInt(String.valueOf(loginID));
                }
                RequestDemoUtil.logger.log(Level.INFO, " Skip Count from JSON : " + skipCountFromJSON);
                RequestDemoUtil.logger.log(Level.INFO, " Skip Count from File : " + skipCountFromFile);
                if (skipCountFromFile + skipCountFromJSON < 7) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
    
    private static Boolean isWithinSkipTime(final Long loginID) {
        Boolean isWithinTimeLimit;
        try {
            final Long userID = DMUserHandler.getUserIdForLoginId(loginID);
            final String lastRequestDemoSkippedTime = SyMUtil.getUserParameter(userID, "requestDemoSkippedTime");
            final long currentTime = System.currentTimeMillis();
            if (lastRequestDemoSkippedTime != null) {
                final long requestDemoSkippedTime = Long.parseLong(lastRequestDemoSkippedTime);
                final long timeLimit = currentTime - requestDemoSkippedTime;
                isWithinTimeLimit = ((timeLimit < 10800000L) ? Boolean.TRUE : Boolean.FALSE);
                if (!isWithinTimeLimit) {
                    SyMUtil.deleteUserParameter(userID, "requestDemoSkippedTime");
                }
            }
            else {
                final String loggedInTimeStr = SyMUtil.getUserParameter(userID, "lastLoginTime");
                if (loggedInTimeStr != null) {
                    final long loggedInTime = Long.parseLong(loggedInTimeStr);
                    final long timeLimit2 = currentTime - loggedInTime;
                    isWithinTimeLimit = ((timeLimit2 < 300000L) ? Boolean.TRUE : Boolean.FALSE);
                }
                else {
                    isWithinTimeLimit = Boolean.FALSE;
                }
            }
        }
        catch (final Exception ex) {
            RequestDemoUtil.logger.log(Level.SEVERE, "Exception occurred while fetching skipped time : ", ex);
            isWithinTimeLimit = Boolean.FALSE;
        }
        return isWithinTimeLimit;
    }
    
    @Override
    public int registerRequestDemo(final Map<String, Object> detailsMap) throws Exception {
        final String loginName = detailsMap.get("loginName");
        CreatorDataPost.getInstance().resetCodes();
        final Map<String, Object> productSpecificMap = ApiFactoryProvider.getRequestDemoHandler().productSpecificHandling(detailsMap);
        final Properties creatorProperties = productSpecificMap.get("creatorProperties");
        if (creatorProperties != null) {
            CreatorDataPost.creator_auth_token = productSpecificMap.get("request_demo_creator_auth_token");
            CreatorDataPost.creator_form_name = creatorProperties.getProperty("request_demo_creator_form_name");
            CreatorDataPost.creator_owner_name = creatorProperties.getProperty("request_demo_creator_owner_name");
            CreatorDataPost.creator_application_name = creatorProperties.getProperty("request_demo_creator_application_name");
        }
        final JSONObject submitJSONObject = new JSONObject();
        final List<String> fieldList = CreatorDataPost.xml_field_list = productSpecificMap.get("fieldList");
        final String name = detailsMap.get("name");
        final String email = detailsMap.get("email");
        final String phone = detailsMap.get("telephoneNumber");
        final String countryCode = detailsMap.get("countryCode");
        final String dateTime = detailsMap.get("dateForDemo");
        final String demoTimeFieldName = productSpecificMap.get("demoTimeFieldName");
        final Integer offset = detailsMap.get("offset");
        submitJSONObject.put("Name", (Object)name);
        submitJSONObject.put("Email", (Object)email);
        submitJSONObject.put("Phone", (Object)phone);
        if (countryCode != null) {
            submitJSONObject.put("Country_Code", (Object)countryCode);
            submitJSONObject.put("Country", (Object)this.countriesConfData.get(countryCode));
        }
        String preferredTime = null;
        if (dateTime != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
            final Date calcScTime = sdf.parse(dateTime);
            final Long scheduledTime = calcScTime.getTime();
            final String demoDateFieldName = productSpecificMap.get("demoDateFieldName");
            submitJSONObject.put(demoDateFieldName, (Object)scheduledTime.toString());
            preferredTime = dateTime.substring(dateTime.indexOf(",") + 1).trim();
        }
        submitJSONObject.put(demoTimeFieldName, (Object)preferredTime);
        final Properties additionalProperties = ApiFactoryProvider.getRequestDemoHandler().getAdditionalPropsToPost(detailsMap);
        for (final Map.Entry<Object, Object> entry : additionalProperties.entrySet()) {
            submitJSONObject.put((String)entry.getKey(), entry.getValue());
        }
        submitJSONObject.put("BrowserTimeZone", (Object)offset.toString());
        submitJSONObject.put("LoggedInUser", (Object)loginName);
        final String submitContentString = submitJSONObject.toString();
        final String source = this.replacePlaceHolders(productSpecificMap.get("Source"));
        submitJSONObject.put("Source", (Object)source);
        submitJSONObject.put("PerDtl", (Object)submitContentString);
        final List<Object> list = new ArrayList<Object>();
        for (final String keys : fieldList) {
            list.add(submitJSONObject.get(keys));
        }
        final List<List> valuesList = new ArrayList<List>();
        valuesList.add(list);
        CreatorDataPost.xml_filed_values = valuesList;
        RequestDemoUtil.logger.log(Level.INFO, "Posting data to creator: " + list);
        final int statusCode = CreatorDataPost.getInstance().submitCreatorData();
        return statusCode;
    }
    
    @Override
    public List<Map<String, Object>> getCountries() throws Exception {
        final List<String> consentNeededCountriesList = Arrays.asList(RequestDemoUtil.CONSENT_NEEDED_COUNTRIES);
        if (this.countriesConfData.isEmpty()) {
            final String fileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "countries.conf";
            this.countriesConfData = this.getMapFromProps(fileName);
        }
        final List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>(this.countriesConfData.size());
        for (final Map.Entry<String, String> entry : this.countriesConfData.entrySet()) {
            final Map<String, Object> tempMap = new HashMap<String, Object>(4);
            final String countryCode = entry.getKey();
            final boolean consentNeeded = consentNeededCountriesList.contains(countryCode);
            tempMap.put("countryCode", countryCode);
            tempMap.put("countryName", entry.getValue());
            tempMap.put("consentNeeded", consentNeeded);
            if (countryCode.equals("US")) {
                final String usStateListFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "USStateList.conf";
                final Map<String, String> usStateConfData = this.getMapFromProps(usStateListFileName);
                final List<Map<String, Object>> finalStateList = new ArrayList<Map<String, Object>>(usStateConfData.size());
                for (final Map.Entry<String, String> usStateEntry : usStateConfData.entrySet()) {
                    final Map<String, Object> tempStateMap = new HashMap<String, Object>(2);
                    tempStateMap.put("stateName", usStateEntry.getValue());
                    tempStateMap.put("stateCode", usStateEntry.getKey());
                    finalStateList.add(tempStateMap);
                }
                tempMap.put("states", finalStateList);
            }
            finalList.add(tempMap);
        }
        return finalList;
    }
    
    public Map<String, String> getMapFromProps(final String propsFileName) throws Exception {
        final Properties props = FileAccessUtil.readProperties(propsFileName);
        final Map<String, String> temporaryMap = new HashMap<String, String>((Map<? extends String, ? extends String>)props);
        return temporaryMap.entrySet().stream().sorted((Comparator<? super Object>)Map.Entry.comparingByValue()).collect((Collector<? super Object, ?, Map<String, String>>)Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue, (key1, key2) -> key1, (Supplier<R>)LinkedHashMap::new));
    }
    
    @Override
    public Map<String, Object> productSpecificHandling(final Map<String, Object> detailsMap) throws Exception {
        final String fileName = com.me.devicemanagement.framework.server.util.SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "creator_properties.conf";
        final Map<String, Object> productSpecificMap = new HashMap<String, Object>();
        final Properties creatorProperties = FileAccessUtil.readProperties(fileName);
        productSpecificMap.put("request_demo_creator_auth_token", creatorProperties.getProperty("request_demo_creator_auth_token"));
        productSpecificMap.put("creatorProperties", creatorProperties);
        final List<String> fieldList = new ArrayList<String>();
        fieldList.add("Email");
        fieldList.add("PerDtl");
        fieldList.add("Source");
        productSpecificMap.put("fieldList", fieldList);
        productSpecificMap.put("demoDateFieldName", "DemoDate");
        productSpecificMap.put("demoTimeFieldName", "perferedTime");
        productSpecificMap.put("Source", "");
        return productSpecificMap;
    }
    
    @Override
    public Properties getAdditionalPropsToPost(final Map<String, Object> detailsMap) throws Exception {
        return new Properties();
    }
    
    private String replacePlaceHolders(String source) {
        try {
            final Map<String, String> productProperties = SyMUtil.getProductLoaderProperties();
            for (final String key : productProperties.keySet()) {
                if (source.contains("{".concat(key).concat("}"))) {
                    source = source.replace("{".concat(key).concat("}"), productProperties.get(key));
                }
            }
        }
        catch (final Exception ex) {
            RequestDemoUtil.logger.log(Level.WARNING, "Exception occurred while fetching product properties " + ex);
        }
        return source;
    }
    
    static {
        RequestDemoUtil.logger = Logger.getLogger(RequestDemoUtil.class.getName());
        RequestDemoUtil.trackerUtil = EvaluatorTrackerUtil.getInstance();
        RequestDemoUtil.evaluatorAPI = ApiFactoryProvider.getEvaluatorAPI();
    }
}
