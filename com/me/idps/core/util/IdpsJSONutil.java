package com.me.idps.core.util;

import java.util.Hashtable;
import java.util.Set;
import java.util.Properties;
import java.util.Collection;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;

public class IdpsJSONutil
{
    public org.json.simple.JSONObject convertJSONtoSimpleJSON(final JSONObject jsonObject) throws JSONException {
        final Iterator keyIterator = jsonObject.keys();
        final org.json.simple.JSONObject simpleJSobject = new org.json.simple.JSONObject();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            simpleJSobject.put((Object)key, jsonObject.get(key));
        }
        return simpleJSobject;
    }
    
    public JSONObject convertSimpleJSONtoJSON(final org.json.simple.JSONObject simpleJsonObject) throws JSONException {
        final Iterator keyIterator = simpleJsonObject.keySet().iterator();
        final JSONObject jsObject = new JSONObject();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            final Object val = simpleJsonObject.get((Object)key);
            if (val instanceof JSONArray) {
                jsObject.put(key, (Object)this.convertSimpleJSONarToJSONar((JSONArray)val));
            }
            else if (val instanceof org.json.simple.JSONObject) {
                jsObject.put(key, (Object)this.convertSimpleJSONtoJSON((org.json.simple.JSONObject)val));
            }
            else {
                jsObject.put(key, val);
            }
        }
        return jsObject;
    }
    
    public org.json.JSONArray convertSimpleJSONarToJSONar(final JSONArray val) throws JSONException {
        final org.json.JSONArray jsArray = new org.json.JSONArray();
        for (int i = 0; val != null && i < val.size(); ++i) {
            final Object curObj = val.get(i);
            if (curObj instanceof org.json.simple.JSONObject) {
                jsArray.put((Object)this.convertSimpleJSONtoJSON((org.json.simple.JSONObject)curObj));
            }
            else if (curObj instanceof JSONArray) {
                jsArray.put((Object)this.convertSimpleJSONarToJSONar((JSONArray)curObj));
            }
            else {
                jsArray.put(curObj);
            }
        }
        return jsArray;
    }
    
    public JSONArray convertJSONarToSimpleJSONar(final org.json.JSONArray val) throws JSONException {
        final JSONArray jsArray = new JSONArray();
        for (int i = 0; val != null && i < val.length(); ++i) {
            final Object curObj = val.get(i);
            if (curObj instanceof JSONObject) {
                jsArray.add((Object)this.convertJSONtoSimpleJSON((JSONObject)curObj));
            }
            else if (curObj instanceof org.json.JSONArray) {
                jsArray.add((Object)this.convertJSONarToSimpleJSONar((org.json.JSONArray)curObj));
            }
            else {
                jsArray.add(curObj);
            }
        }
        return jsArray;
    }
    
    public static Object opt(final org.json.simple.JSONObject jsonObject, final String key, final Object defaultValue) {
        if (jsonObject.containsKey((Object)key)) {
            return jsonObject.get((Object)key);
        }
        return defaultValue;
    }
    
    public static ArrayList<Long> convertJSONArrayToArrayList(final JSONArray jsonArray) {
        return new ArrayList<Long>((Collection<? extends Long>)jsonArray);
    }
    
    public static Long[] convertJSONArrayToLongArray(final JSONArray jsonArray) {
        final ArrayList<Long> longArray = convertJSONArrayToArrayList(jsonArray);
        return longArray.toArray(new Long[longArray.size()]);
    }
    
    public static JSONArray convertListToJSONArray(final Long[] resourceIDs) {
        final JSONArray resourceAr = new JSONArray();
        if (resourceIDs != null) {
            for (int i = 0; i < resourceIDs.length; ++i) {
                resourceAr.add((Object)resourceIDs[i]);
            }
        }
        return resourceAr;
    }
    
    public static org.json.simple.JSONObject convertPropertiesToJSONObject(final Properties props) {
        final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
        final Set keys = props.keySet();
        final Iterator itr = keys.iterator();
        while (itr != null && itr.hasNext()) {
            final Object key = itr.next();
            final Object value = ((Hashtable<K, Object>)props).get(key);
            jsonObject.put(key, value);
        }
        return jsonObject;
    }
    
    public static Properties convertJSONObjectToProperties(final org.json.simple.JSONObject jsonObject) {
        final Properties props = new Properties();
        final Set keys = jsonObject.keySet();
        final Iterator itr = keys.iterator();
        while (itr != null && itr.hasNext()) {
            final Object key = itr.next();
            final Object value = jsonObject.get(key);
            props.put(key, value);
        }
        return props;
    }
    
    public static Long optLongForUVH(final JSONObject jsonObject, final String key, final Long defaultValue) {
        if (defaultValue != null) {
            return Long.parseLong(jsonObject.optString(key, String.valueOf(defaultValue)));
        }
        return Long.parseLong(jsonObject.optString(key, "0"));
    }
}
