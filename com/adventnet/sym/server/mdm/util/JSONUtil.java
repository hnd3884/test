package com.adventnet.sym.server.mdm.util;

import com.me.mdm.api.error.APIHTTPException;
import java.util.Hashtable;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import java.util.Set;
import java.io.UnsupportedEncodingException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.BufferedReader;
import java.util.Properties;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import org.json.JSONException;
import java.util.logging.Level;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.idps.core.util.IdpsJSONutil;

public class JSONUtil extends IdpsJSONutil
{
    private static JSONUtil jsonUtil;
    private static final Logger LOGGER;
    
    public JSONObject parseJSONFromRequest(final HttpServletRequest request) {
        try {
            return new JSONObject(((org.json.simple.JSONObject)new JSONParser().parse((Reader)request.getReader())).toJSONString());
        }
        catch (final JSONException ex) {
            JSONUtil.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final IOException ex2) {
            JSONUtil.LOGGER.log(Level.SEVERE, null, ex2);
        }
        catch (final ParseException ex3) {
            JSONUtil.LOGGER.log(Level.SEVERE, null, (Throwable)ex3);
        }
        return new JSONObject();
    }
    
    public String checkAndUpdateTheValue(final JSONObject source, final String key, final String defaultValue) {
        try {
            return String.valueOf(source.get(key));
        }
        catch (final Exception ex) {
            JSONUtil.LOGGER.log(Level.INFO, () -> "{" + this.getClass().getCanonicalName() + ".checkAndUpdateTheValue}. " + "Key-Value Not Found JSONException raised. [Key : " + s + "]. Error = " + ex2.getMessage());
            return defaultValue;
        }
    }
    
    public String checkAndUpdateTheValue(final HashMap<String, String> source, final String key, final String defaultValue) {
        return source.containsKey(key) ? source.get(key) : defaultValue;
    }
    
    public List convertJSONArrayTOList(final JSONArray array) {
        final List arrayList = new ArrayList();
        try {
            for (int i = 0; i < array.length(); ++i) {
                arrayList.add(array.get(i));
            }
        }
        catch (final Exception ex) {
            JSONUtil.LOGGER.log(Level.SEVERE, () -> "{" + this.getClass().getCanonicalName() + ".convertJSONArrayTOList[JSONArray]}. Array : " + jsonArray + " Error :" + ex2.getMessage());
        }
        return arrayList;
    }
    
    public List<Long> convertLongJSONArrayTOList(final JSONArray array) {
        final List arrayList = new ArrayList();
        try {
            for (int i = 0; i < array.length(); ++i) {
                arrayList.add(Long.valueOf(array.get(i).toString()));
            }
        }
        catch (final Exception ex) {
            JSONUtil.LOGGER.log(Level.SEVERE, () -> "{" + this.getClass().getCanonicalName() + ".convertJSONArrayTOList[JSONArray]}. Array : " + jsonArray + " Error :" + ex2.getMessage());
        }
        return arrayList;
    }
    
    public List<String> convertStringJSONArrayTOList(final JSONArray array) {
        final List arrayList = new ArrayList();
        try {
            for (int i = 0; i < array.length(); ++i) {
                arrayList.add(array.get(i).toString());
            }
        }
        catch (final Exception ex) {
            JSONUtil.LOGGER.log(Level.SEVERE, "Exception when converting stringjsonarray to list", ex);
        }
        return arrayList;
    }
    
    public JSONArray convertListToJSONArray(final List list) {
        final JSONArray array = new JSONArray();
        try {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                array.put(iterator.next());
            }
        }
        catch (final Exception ex) {
            JSONUtil.LOGGER.log(Level.SEVERE, () -> "{" + this.getClass().getCanonicalName() + ".convertListToJSONArray[List]}. List : " + list2 + " Error :" + ex2.getMessage());
        }
        return array;
    }
    
    public static JSONUtil getInstance() {
        return (JSONUtil.jsonUtil == null) ? (JSONUtil.jsonUtil = new JSONUtil()) : JSONUtil.jsonUtil;
    }
    
    public HashMap ConvertToSameDataTypeHash(final JSONObject jsonOject) throws JSONException {
        final HashMap hmap = new HashMap();
        final Iterator<String> myIter = jsonOject.keys();
        while (myIter.hasNext()) {
            final String key = myIter.next();
            if (jsonOject.opt(key) == null || jsonOject.opt(key) == JSONObject.NULL) {
                hmap.put(key, null);
            }
            else {
                hmap.put(key, jsonOject.get(key));
            }
        }
        return hmap;
    }
    
    public HashMap ConvertJSONObjectToHash(final JSONObject jsonOject) throws JSONException {
        final HashMap<String, String> hmap = new HashMap<String, String>();
        final Iterator<String> myIter = jsonOject.keys();
        while (myIter.hasNext()) {
            final String key = myIter.next().toString();
            final String value = String.valueOf(jsonOject.get(key));
            hmap.put(key, value);
            JSONUtil.LOGGER.log(Level.FINEST, "Key : {0}", key);
        }
        return hmap;
    }
    
    public HashMap ConvertJSONObjectToHash(final JSONObject jsonOject, final String jsonName) throws JSONException {
        final JSONObject agentRequest = jsonOject.getJSONObject(jsonName);
        final HashMap<String, String> hmap = new HashMap<String, String>();
        final Iterator<String> myIter = agentRequest.keys();
        while (myIter.hasNext()) {
            final String key = myIter.next().toString();
            final String value = String.valueOf(agentRequest.get(key));
            hmap.put(key, value);
            JSONUtil.LOGGER.log(Level.INFO, "Key : {0}", key);
            JSONUtil.LOGGER.log(Level.INFO, "value : {0}", value);
        }
        JSONUtil.LOGGER.log(Level.INFO, "Hash Map Output : {0}", hmap.toString());
        return hmap;
    }
    
    public Properties getPropertiesFromJSON(final JSONObject jsonObject) throws JSONException {
        final Properties prop = new Properties();
        final Iterator<String> myIter = jsonObject.keys();
        while (myIter.hasNext()) {
            final String key = myIter.next().toString();
            final String value = String.valueOf(jsonObject.get(key));
            ((Hashtable<String, String>)prop).put(key, value);
            JSONUtil.LOGGER.log(Level.FINE, "Key : {0}", key);
        }
        return prop;
    }
    
    public static String getString(final JSONObject jsonObject, final String key, final String defaultValue) {
        String returnValue = null;
        try {
            if (jsonObject.isNull(key)) {
                returnValue = defaultValue;
            }
            else {
                returnValue = String.valueOf(jsonObject.get(key));
            }
        }
        catch (final Exception exp) {
            JSONUtil.LOGGER.log(Level.WARNING, "Exception occured while extracting the data from jsonOject \n", exp);
        }
        return returnValue;
    }
    
    public JSONObject getJSONObject(final HttpServletRequest request) throws IOException, JSONException {
        final BufferedReader reader = (BufferedReader)MDMUtil.getInstance().getProperEncodedReader(request, null);
        String responseContent = "";
        String readContent = null;
        while ((readContent = reader.readLine()) != null && readContent.length() != 0) {
            responseContent += readContent;
        }
        if (!this.isValidJSON(responseContent)) {
            throw new JSONException("request is not JSON");
        }
        final JSONObject jsonObject = new JSONObject(responseContent);
        return jsonObject;
    }
    
    public boolean isValidJSON(final String jsonData) {
        return SyMUtil.isValidJSON(jsonData);
    }
    
    public void decodeAllKeysInJSON(final JSONObject json) throws JSONException, UnsupportedEncodingException {
        final Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (json.get(key) instanceof String) {
                json.put(key, (Object)MDMUtil.getInstance().decodeURIComponentEquivalent(String.valueOf(json.get(key))));
            }
        }
    }
    
    public void decodeAllKeysInJSON(final org.json.simple.JSONObject json) throws JSONException, UnsupportedEncodingException {
        final Set<String> keySet = json.keySet();
        for (final String key : keySet) {
            if (json.get((Object)key) instanceof String) {
                json.put((Object)key, (Object)MDMUtil.getInstance().decodeURIComponentEquivalent(String.valueOf(json.get((Object)key))));
            }
        }
    }
    
    public void decodeKeyInJSON(final JSONObject json, final String key) throws JSONException, UnsupportedEncodingException {
        if (json.has(key)) {
            json.put(key, (Object)MDMUtil.getInstance().decodeURIComponentEquivalent(String.valueOf(json.get(key))));
        }
    }
    
    public void decodeKeyInJSON(final org.json.simple.JSONObject json, final String key) throws JSONException, UnsupportedEncodingException {
        if (json.containsKey((Object)key)) {
            json.put((Object)key, (Object)MDMUtil.getInstance().decodeURIComponentEquivalent(String.valueOf(json.get((Object)key))));
        }
    }
    
    public static JSONObject mapToJSON(final Map map) throws JSONException {
        final String jsonStr = new JSONObject(map).toString();
        return new JSONObject(jsonStr);
    }
    
    public JSONArray insertElementInJSONArray(final JSONArray arr, final Object e, final int index) throws JSONException {
        for (int i = arr.length(); i > index; --i) {
            arr.put(i, arr.get(i - 1));
        }
        arr.put(index, e);
        return arr;
    }
    
    public JSONArray subJSONArray(final JSONArray jSONArray, final int startIndex, final int offset) throws JSONException {
        if (jSONArray != null) {
            final JSONArray subArray = new JSONArray();
            for (int i = startIndex; i < jSONArray.length() && i < startIndex + offset; ++i) {
                subArray.put(jSONArray.get(i));
            }
            return subArray;
        }
        return null;
    }
    
    public JSONObject getJSONFromFile(final String fileName) throws Exception {
        final InputStream in = ApiFactoryProvider.getFileAccessAPI().readFile(fileName);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        final StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        in.close();
        return new JSONObject(out.toString());
    }
    
    public static void putAll(final JSONObject firstJSON, final JSONObject secondJSON) throws JSONException {
        final Iterator<String> keys = secondJSON.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            firstJSON.put(key, secondJSON.get(key));
        }
    }
    
    public static Object optObject(final JSONObject json, final String key, final boolean caseSensitive) {
        try {
            final Iterator<String> jsonKeys = json.keys();
            while (jsonKeys.hasNext()) {
                final String jsonKey = jsonKeys.next();
                if ((!caseSensitive && jsonKey.equalsIgnoreCase(key)) || (caseSensitive && jsonKey.equals(key))) {
                    return json.get(jsonKey);
                }
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(JSONUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return null;
    }
    
    public List convertLongToString(final List list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                Object value = list.get(i);
                if (value instanceof Long) {
                    list.add(i, String.valueOf(value));
                }
                else if (value instanceof JSONObject) {
                    try {
                        value = this.convertLongToString((JSONObject)value);
                        list.add(i, String.valueOf(value));
                    }
                    catch (final Exception e) {
                        JSONUtil.LOGGER.log(Level.SEVERE, "Exception while trying to cast JSON ", e);
                    }
                }
                else if (value instanceof JSONArray) {
                    try {
                        value = this.convertLongToString((JSONArray)value);
                        list.add(i, String.valueOf(value));
                    }
                    catch (final Exception e) {
                        JSONUtil.LOGGER.log(Level.SEVERE, "Exception while trying to cast JSON ", e);
                    }
                }
            }
        }
        return list;
    }
    
    public JSONObject convertLongToString(final JSONObject responseJSON) throws Exception {
        final Iterator keyItr = responseJSON.keys();
        while (keyItr.hasNext()) {
            final String key = keyItr.next();
            Object value = responseJSON.get(key);
            if (value instanceof Long) {
                responseJSON.put(key, (Object)String.valueOf(value));
            }
            else if (value instanceof JSONObject) {
                value = this.convertLongToString((JSONObject)value);
                responseJSON.put(key, value);
            }
            else {
                if (!(value instanceof JSONArray)) {
                    continue;
                }
                value = this.convertLongToString((JSONArray)value);
                responseJSON.put(key, value);
            }
        }
        return responseJSON;
    }
    
    public JSONArray convertLongToString(final JSONArray responseJSONArray) throws Exception {
        for (int i = 0; i < responseJSONArray.length(); ++i) {
            Object arrayValue = responseJSONArray.get(i);
            if (arrayValue instanceof Long) {
                responseJSONArray.put(i, (Object)String.valueOf(arrayValue));
            }
            else if (arrayValue instanceof JSONObject) {
                arrayValue = this.convertLongToString((JSONObject)arrayValue);
                responseJSONArray.put(i, arrayValue);
            }
            else if (arrayValue instanceof JSONArray) {
                arrayValue = this.convertLongToString((JSONArray)arrayValue);
                responseJSONArray.put(i, arrayValue);
            }
            else if (arrayValue instanceof Hashtable) {
                arrayValue = this.convertLongToString((Hashtable)arrayValue);
                responseJSONArray.put(i, arrayValue);
            }
        }
        return responseJSONArray;
    }
    
    public org.json.simple.JSONObject convertLongToString(final org.json.simple.JSONObject responseJSON) throws JSONException {
        for (final String key : responseJSON.keySet()) {
            Object value = responseJSON.get((Object)key);
            if (value instanceof Long) {
                responseJSON.put((Object)key, (Object)String.valueOf(value));
            }
            else if (value instanceof org.json.simple.JSONObject) {
                value = this.convertLongToString((org.json.simple.JSONObject)value);
                responseJSON.put((Object)key, value);
            }
            else {
                if (!(value instanceof org.json.simple.JSONArray)) {
                    continue;
                }
                value = this.convertLongToString((org.json.simple.JSONArray)value);
                responseJSON.put((Object)key, value);
            }
        }
        return responseJSON;
    }
    
    public org.json.simple.JSONArray convertLongToString(final org.json.simple.JSONArray responseJSONArray) throws JSONException {
        for (int i = 0; i < responseJSONArray.size(); ++i) {
            Object arrayValue = responseJSONArray.get(i);
            if (arrayValue instanceof Long) {
                responseJSONArray.set(i, (Object)String.valueOf(arrayValue));
            }
            else if (arrayValue instanceof org.json.simple.JSONObject) {
                arrayValue = this.convertLongToString((org.json.simple.JSONObject)arrayValue);
                responseJSONArray.set(i, arrayValue);
            }
            else if (arrayValue instanceof org.json.simple.JSONArray) {
                arrayValue = this.convertLongToString((org.json.simple.JSONArray)arrayValue);
                responseJSONArray.set(i, arrayValue);
            }
        }
        return responseJSONArray;
    }
    
    public Hashtable convertLongToString(final Hashtable hashtable) throws Exception {
        final Set keySet = hashtable.keySet();
        for (final String key : keySet) {
            Object value = hashtable.get(key);
            if (value instanceof Long) {
                hashtable.put(key, String.valueOf(value));
            }
            else if (value instanceof JSONObject) {
                value = this.convertLongToString((JSONObject)value);
                hashtable.put(key, value);
            }
            else {
                if (!(value instanceof JSONArray)) {
                    continue;
                }
                value = this.convertLongToString((JSONArray)value);
                hashtable.put(key, value);
            }
        }
        return hashtable;
    }
    
    public static Long optLongForUVH(final JSONArray jsonArray, final int index, final Long defaultValue) {
        if (defaultValue != null) {
            return Long.parseLong(jsonArray.optString(index, String.valueOf(defaultValue)));
        }
        return Long.parseLong(jsonArray.optString(index, "0"));
    }
    
    public static void renameKey(final JSONObject json, final String oldKey, final String newKey) throws JSONException {
        json.put(newKey, json.get(oldKey));
        json.remove(oldKey);
    }
    
    public static JSONObject toJSON(final String key, final Object value) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(key, value);
        return json;
    }
    
    public static String optStringIgnoreKeyCase(final JSONObject json, final String key) {
        String value = null;
        final Iterator keyItr = json.keys();
        while (keyItr.hasNext()) {
            final String jsonKey = keyItr.next();
            if (jsonKey.equalsIgnoreCase(key)) {
                try {
                    final Object valueObj = json.get(jsonKey);
                    value = valueObj.toString();
                }
                catch (final JSONException ex) {}
            }
        }
        return value;
    }
    
    public static String optStringIgnoreKeyCase(final JSONObject json, final String key, final String defaultValue) {
        String value = defaultValue;
        final Iterator keyItr = json.keys();
        while (keyItr.hasNext()) {
            final String jsonKey = keyItr.next();
            if (jsonKey.equalsIgnoreCase(key)) {
                try {
                    final Object valueObj = json.get(jsonKey);
                    value = valueObj.toString();
                }
                catch (final JSONException ex) {}
            }
        }
        return value;
    }
    
    public static Long optLong(final JSONObject json, final String key, final Long defaulValue) {
        return (json.opt(key) != null) ? Long.parseLong(json.opt(key).toString()) : defaulValue;
    }
    
    public static Long optLong(final JSONObject json, final String key) {
        if (json.has(key)) {
            return (json.get(key) == null) ? null : Long.valueOf(Long.parseLong(json.get(key).toString()));
        }
        return null;
    }
    
    public JSONObject changeJSONKeyCase(final JSONObject json, final int caseType) throws JSONException {
        final JSONObject newJSON = new JSONObject();
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            final String modifiedKey = (caseType == 1) ? key.toUpperCase() : key.toLowerCase();
            if (json.get(key) instanceof JSONObject) {
                final JSONObject valueJSON = this.changeJSONKeyCase(json.getJSONObject(key), caseType);
                newJSON.put(modifiedKey, (Object)valueJSON);
            }
            else if (json.get(key) instanceof JSONArray) {
                newJSON.put(modifiedKey, (Object)this.changeJSONKeyCase(json.getJSONArray(key), caseType));
            }
            else {
                newJSON.put(modifiedKey, json.get(key));
            }
        }
        return newJSON;
    }
    
    public JSONArray changeJSONKeyCase(final JSONArray valueJSONArray, final int caseType) throws JSONException {
        final JSONArray newvalueJSONArray = new JSONArray();
        for (int i = 0; i < valueJSONArray.length(); ++i) {
            if (valueJSONArray.get(i) instanceof JSONObject) {
                newvalueJSONArray.put((Object)this.changeJSONKeyCase(valueJSONArray.getJSONObject(i), caseType));
            }
            else if (valueJSONArray.get(i) instanceof JSONArray) {
                newvalueJSONArray.put((Object)this.changeJSONKeyCase(valueJSONArray.getJSONArray(i), caseType));
            }
            else {
                newvalueJSONArray.put(valueJSONArray.get(i));
            }
        }
        return newvalueJSONArray;
    }
    
    public static HashMap<String, Object> convertJSONtoMap(final JSONObject requestJSON) throws JSONException {
        final HashMap<String, Object> hashMap = new HashMap<String, Object>();
        final Iterator<String> jsonKeys = requestJSON.keys();
        while (jsonKeys.hasNext()) {
            final String key = jsonKeys.next();
            Object value = requestJSON.get(key);
            if (value instanceof JSONArray) {
                value = convertJSONArrayToList((JSONArray)value);
            }
            else if (value instanceof JSONObject) {
                value = convertJSONtoMap((JSONObject)value);
            }
            hashMap.put(key, value);
        }
        return hashMap;
    }
    
    public static List<Object> convertJSONArrayToList(final JSONArray requestJSONArray) throws JSONException {
        final List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < requestJSONArray.length(); ++i) {
            Object value = requestJSONArray.get(i);
            if (value instanceof JSONArray) {
                value = convertJSONArrayToList((JSONArray)value);
            }
            else if (value instanceof JSONObject) {
                value = convertJSONtoMap((JSONObject)value);
            }
            list.add(value);
        }
        return list;
    }
    
    public JSONArray removeByPos(final JSONArray jsonArray, final int pos) {
        if (jsonArray == null) {
            return null;
        }
        final JSONArray newJSONArray = new JSONArray();
        for (int size = jsonArray.length(), i = 0; i < size; ++i) {
            if (i != pos) {
                try {
                    newJSONArray.put(jsonArray.get(i));
                }
                catch (final JSONException e) {
                    JSONUtil.LOGGER.log(Level.SEVERE, "Exception while trying to Insert into JSONArray ", (Throwable)e);
                    return null;
                }
            }
        }
        return newJSONArray;
    }
    
    public static JSONObject mergeJSONObjects(final JSONObject json1, final JSONObject json2) {
        JSONObject mergedJSON;
        try {
            mergedJSON = new JSONObject(json1, getNames(json1));
            for (final String key : getNames(json2)) {
                mergedJSON.put(key, json2.get(key));
            }
        }
        catch (final JSONException e) {
            JSONUtil.LOGGER.log(Level.SEVERE, "Exception occurred in mergeJSONObjects", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return mergedJSON;
    }
    
    public static void putAll(final JSONArray array1, final JSONArray array2) {
        try {
            for (int i = 0; i < array2.length(); ++i) {
                array1.put(array2.get(i));
            }
        }
        catch (final JSONException e) {
            JSONUtil.LOGGER.log(Level.SEVERE, "Exception in put all json array", (Throwable)e);
        }
    }
    
    public static JSONArray mergeJSONArray(final JSONArray array1, final JSONArray array2) throws JSONException {
        try {
            final JSONArray mergeArray = new JSONArray(array1.toString());
            for (int i = 0; i < array2.length(); ++i) {
                mergeArray.put(array2.get(i));
            }
            return mergeArray;
        }
        catch (final JSONException e) {
            JSONUtil.LOGGER.log(Level.SEVERE, "Exception in merge json array", (Throwable)e);
            throw e;
        }
    }
    
    public static String[] getNames(final JSONObject jsonObject) {
        final int length = jsonObject.length();
        if (length == 0) {
            return new String[0];
        }
        final Iterator iterator = jsonObject.keys();
        final String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext()) {
            names[i] = iterator.next();
            ++i;
        }
        return names;
    }
    
    public static JSONObject changeKey(final JSONObject requestJSON, final String newKey, final String oldKey) throws JSONException {
        if (requestJSON.has(oldKey)) {
            requestJSON.put(newKey, requestJSON.get(oldKey));
            requestJSON.remove(oldKey);
        }
        return requestJSON;
    }
    
    public String convertJSONArrayToString(final JSONArray array) throws JSONException {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length(); ++i) {
            builder.append(array.get(i));
            builder.append(",");
        }
        return builder.toString();
    }
    
    public static boolean findInJSONArray(final JSONArray array, final String searchValue) {
        for (int i = 0; i < array.length(); ++i) {
            final String value = array.optString(i);
            if (value.equalsIgnoreCase(searchValue)) {
                return true;
            }
        }
        return false;
    }
    
    public JSONArray convertListToStringJSONArray(final List list) {
        final JSONArray array = new JSONArray();
        try {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                array.put((Object)String.valueOf(iterator.next()));
            }
        }
        catch (final Exception ex) {
            JSONUtil.LOGGER.log(Level.SEVERE, () -> "{" + this.getClass().getCanonicalName() + ".convertListToStringJSONArray[List]}. List : " + list2 + " Error :" + ex2.getMessage());
        }
        return array;
    }
    
    public static Boolean checkValueExistsInJSONArray(final JSONArray jsonArray, final Object value, final String keyToCompareForArrayOfJSONObjects) throws JSONException {
        Boolean isExists = Boolean.FALSE;
        for (int idx = 0; idx < jsonArray.length(); ++idx) {
            Object jsonValue = jsonArray.get(idx);
            if (jsonValue instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject)jsonValue;
                jsonValue = jsonObject.get(keyToCompareForArrayOfJSONObjects);
            }
            if (value.equals(jsonValue)) {
                isExists = Boolean.TRUE;
                break;
            }
        }
        return isExists;
    }
    
    public static JSONArray convertToLongJSONArray(final JSONArray jsonArray) throws JSONException {
        final JSONArray resultJSONArr = new JSONArray();
        for (int idx = 0; idx < jsonArray.length(); ++idx) {
            resultJSONArr.put(jsonArray.getLong(idx));
        }
        return resultJSONArr;
    }
    
    public static String getString(final JSONObject jsonObject, final String key) throws JSONException {
        return String.valueOf(jsonObject.get(key));
    }
    
    public static String optString(final JSONObject jsonObject, final String key) {
        return String.valueOf(jsonObject.opt(key));
    }
    
    public static Integer optInteger(final JSONObject jsonObject, final String key) {
        if (jsonObject.has(key)) {
            return (jsonObject.get(key) == null) ? null : Integer.valueOf(Integer.parseInt(jsonObject.get(key).toString()));
        }
        return null;
    }
    
    public static Boolean optBoolean(final JSONObject json, final String key) {
        if (json.has(key)) {
            return Boolean.parseBoolean(json.get(key).toString());
        }
        return false;
    }
    
    public static String optString(final JSONObject jsonObject, final String key, final String defaultValue) {
        final String value = optString(jsonObject, key);
        return (value == null) ? defaultValue : value;
    }
    
    public static List getValuesOfJSONObject(final JSONObject jsonObject) throws JSONException {
        final List jsonValues = new ArrayList();
        final Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            jsonValues.add(jsonObject.get((String)keys.next()));
        }
        return jsonValues;
    }
    
    public static JSONArray convertCommaSeparatedStringToJSONArray(final String string) {
        final String[] strArr = string.split(",");
        final JSONArray jsonArray = new JSONArray();
        for (final String str : strArr) {
            jsonArray.put((Object)str);
        }
        return jsonArray;
    }
    
    public static List<String> getStringListFromJSONArray(final JSONArray array) {
        final List<String> stringList = new ArrayList<String>();
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.length(); ++i) {
            stringList.add(array.getString(i));
        }
        return stringList;
    }
    
    public static JSONArray changeKey(final JSONArray requestJSONAray, final String newKey, final String oldKey) throws JSONException {
        for (final JSONObject requestJSON : requestJSONAray) {
            if (requestJSON.has(oldKey)) {
                requestJSON.put(newKey, requestJSON.get(oldKey));
                requestJSON.remove(oldKey);
            }
        }
        return requestJSONAray;
    }
    
    static {
        JSONUtil.jsonUtil = null;
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
