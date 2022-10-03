package com.me.devicemanagement.onpremise.server.common;

import com.me.devicemanagement.onpremise.server.general.CountryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.simple.JSONArray;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.Set;
import java.util.logging.Logger;

public class UpdatesAnalyzer
{
    private static Logger logger;
    
    public JSONObject constructServerJSON(final Set uniqueUpdateKeys) {
        UpdatesAnalyzer.logger.log(Level.FINE, "Going to ConstructServerJSON");
        final JSONObject serverJSON = new JSONObject();
        try {
            for (final String conditionKey : uniqueUpdateKeys) {
                final Object conditionValue = this.fetchServerValueFor(conditionKey);
                final String dataType = this.fetchDataTypeFor(conditionKey);
                if (conditionValue != null) {
                    final JSONObject conditionDetails = new JSONObject();
                    conditionDetails.put((Object)"Value", conditionValue);
                    conditionDetails.put((Object)"DataType", (Object)dataType);
                    serverJSON.put((Object)conditionKey, (Object)conditionDetails);
                }
            }
        }
        catch (final Exception e) {
            UpdatesAnalyzer.logger.log(Level.SEVERE, "Exception while constructing ServerJSON", e);
        }
        return serverJSON;
    }
    
    public Set getUniqueConditionKeys(final JSONObject updatesJSON) {
        UpdatesAnalyzer.logger.log(Level.FINE, "Going to getUniqueConditionKeys");
        final Set<String> uniqueKeys = new HashSet<String>();
        String messageLabel = "MajorVersion";
        this.getUniqueConditionKeys(updatesJSON, uniqueKeys, messageLabel);
        messageLabel = "ServicePack";
        this.getUniqueConditionKeys(updatesJSON, uniqueKeys, messageLabel);
        messageLabel = "HotFix";
        this.getUniqueConditionKeys(updatesJSON, uniqueKeys, messageLabel);
        messageLabel = "FlashMsg";
        this.getUniqueConditionKeys(updatesJSON, uniqueKeys, messageLabel);
        UpdatesAnalyzer.logger.log(Level.FINE, "Unique Keys", uniqueKeys);
        return uniqueKeys;
    }
    
    public void getUniqueConditionKeys(final JSONObject updatesJSON, final Set uniqueKeys, final String messageLabel) {
        UpdatesAnalyzer.logger.log(Level.FINE, "Going to get uniqueConditionKeys For Label" + messageLabel);
        try {
            final JSONArray messageArray = (JSONArray)updatesJSON.get((Object)messageLabel);
            for (int i = 0; i < messageArray.size(); ++i) {
                final JSONObject msgObject = (JSONObject)messageArray.get(i);
                final JSONArray conditionArray = (JSONArray)msgObject.get((Object)"Condition");
                for (int j = 0; j < conditionArray.size(); ++j) {
                    final JSONObject conditionObject = (JSONObject)conditionArray.get(j);
                    String conditionKey = (String)conditionObject.get((Object)"Key");
                    conditionKey = conditionKey.toLowerCase();
                    uniqueKeys.add(conditionKey);
                }
            }
        }
        catch (final Exception e) {
            UpdatesAnalyzer.logger.log(Level.SEVERE, "Exception while to get uniqueConditionKeys For Label" + messageLabel);
        }
    }
    
    public Object fetchServerValueFor(final String conditionKey) {
        UpdatesAnalyzer.logger.log(Level.FINE, "Going to Fetch the Server Value for Key:" + conditionKey);
        switch (conditionKey) {
            case "db": {
                return DBUtil.getActiveDBName();
            }
            case "licensetype": {
                return LicenseProvider.getInstance().getLicenseType();
            }
            case "licenseedition": {
                return LicenseProvider.getInstance().getProductType();
            }
            case "initialbuild": {
                return UpdatesAnalyzerUtil.getInstance().getInitialInstalledBuild();
            }
            case "nodayssinceinstallation": {
                return UpdatesAnalyzerUtil.getInstance().getInstalledBeforeInDays();
            }
            case "country": {
                return CountryProvider.getInstance().countryCodeFromDefaultTimeZoneID().toUpperCase();
            }
            default: {
                return null;
            }
        }
    }
    
    public String fetchDataTypeFor(final String conditionKey) {
        UpdatesAnalyzer.logger.log(Level.FINE, "Going to Fetch DataType for conditon:" + conditionKey);
        switch (conditionKey) {
            case "db":
            case "licensetype":
            case "licenseedition":
            case "country": {
                return "String";
            }
            case "initialbuild":
            case "nodayssinceinstallation": {
                return "Long";
            }
            default: {
                return "";
            }
        }
    }
    
    public boolean compareJSONS(final JSONObject updatesMsgJSON, final JSONObject serverJSON) {
        UpdatesAnalyzer.logger.log(Level.FINE, "Going to CompareJSONS");
        boolean isConditionMatched = Boolean.FALSE;
        final JSONArray conditionArray = (JSONArray)updatesMsgJSON.get((Object)"Condition");
        for (int i = 0; i < conditionArray.size(); ++i) {
            final JSONObject conditionObj = (JSONObject)conditionArray.get(i);
            String conditionKey = (String)conditionObj.get((Object)"Key");
            String comparator = (String)conditionObj.get((Object)"Comparator");
            final Object conditionValue = conditionObj.get((Object)"Value");
            conditionKey = conditionKey.toLowerCase();
            final JSONObject serverDetails = (JSONObject)serverJSON.get((Object)conditionKey);
            String conditionDataType = null;
            Object serverValue = null;
            if (serverDetails != null) {
                conditionDataType = (String)serverDetails.get((Object)"DataType");
                serverValue = serverDetails.get((Object)"Value");
            }
            UpdatesAnalyzer.logger.log(Level.FINE, "Condition Key:" + conditionKey, " Condition Value:" + conditionValue + " ServerValue:" + serverValue);
            isConditionMatched = ((serverDetails == null || serverValue == null || conditionValue.equals("") || conditionValue.toString().equalsIgnoreCase("all")) ? Boolean.TRUE : Boolean.FALSE);
            if (!isConditionMatched) {
                UpdatesAnalyzer.logger.log(Level.FINE, "Going to Check Conditon as both ServerValue and Condition Value are valid");
                comparator = ((comparator == null) ? comparator : comparator.toLowerCase());
                final String s = conditionDataType;
                switch (s) {
                    case "String": {
                        isConditionMatched = this.compareStringValues(conditionValue, serverValue, comparator);
                        break;
                    }
                    case "Long": {
                        isConditionMatched = this.compareLongValues(conditionValue, serverValue, comparator, conditionObj);
                        break;
                    }
                }
            }
            if (!isConditionMatched) {
                UpdatesAnalyzer.logger.log(Level.FINE, "Skipping rest of Conditions in array, as this condition Fails");
                return isConditionMatched;
            }
        }
        return isConditionMatched;
    }
    
    public boolean compareStringValues(final Object conditionValue, final Object serverValue, final String comparator) {
        String condnValue = String.valueOf(conditionValue);
        String serValue = String.valueOf(serverValue);
        condnValue = condnValue.trim();
        serValue = serValue.trim();
        switch (comparator) {
            case "equal": {
                final String[] split;
                final String[] condnValueArr = split = condnValue.split(",");
                for (final String conditionVal : split) {
                    if (serValue.equalsIgnoreCase(conditionVal)) {
                        return Boolean.TRUE;
                    }
                }
                return Boolean.FALSE;
            }
            case "contains": {
                serValue = serValue.toLowerCase();
                condnValue = condnValue.toLowerCase();
                return (serValue.indexOf(condnValue) != -1) ? Boolean.TRUE : Boolean.FALSE;
            }
            case "containedin": {
                final String[] split2;
                final String[] serverValArr = split2 = serValue.split(",");
                for (String serverVal : split2) {
                    serverVal = serverVal.toLowerCase();
                    final String[] split3;
                    final String[] condnValArr = split3 = condnValue.split(",");
                    for (String condnVal : split3) {
                        condnVal = condnVal.toLowerCase();
                        if (serverVal.indexOf(condnVal) != -1) {
                            return Boolean.TRUE;
                        }
                    }
                }
                return Boolean.FALSE;
            }
            default: {
                return Boolean.FALSE;
            }
        }
    }
    
    public boolean compareLongValues(final Object conditionValue, final Object serverValue, final String comparator, final JSONObject conditionObj) {
        final Long condnValue = Long.valueOf(conditionValue.toString());
        final Long serValue = Long.valueOf(serverValue.toString());
        switch (comparator) {
            case "equal": {
                return condnValue.equals(serValue) ? Boolean.TRUE : Boolean.FALSE;
            }
            case "greaterthan": {
                return (condnValue < serValue) ? Boolean.TRUE : Boolean.FALSE;
            }
            case "greaterthanequal": {
                return (condnValue <= serValue) ? Boolean.TRUE : Boolean.FALSE;
            }
            case "lessthan": {
                return (condnValue > serValue) ? Boolean.TRUE : Boolean.FALSE;
            }
            case "lessthanequal": {
                return (condnValue >= serValue) ? Boolean.TRUE : Boolean.FALSE;
            }
            case "between": {
                final Long condnValue2 = Long.valueOf((String)conditionObj.get((Object)"Value2"));
                return (condnValue > serValue && condnValue2 < serValue) ? Boolean.TRUE : Boolean.FALSE;
            }
            default: {
                return Boolean.FALSE;
            }
        }
    }
    
    static {
        UpdatesAnalyzer.logger = Logger.getLogger(UpdatesAnalyzer.class.getName());
    }
}
