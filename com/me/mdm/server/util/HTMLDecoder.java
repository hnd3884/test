package com.me.mdm.server.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.parser.ParseException;
import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONArray;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class HTMLDecoder
{
    public static Logger logger;
    
    public static JSONObject decodeCurrentConfigJson(final JSONObject configurationJSON, final ArrayList<String> passwordFields) {
        try {
            final String configName = (String)configurationJSON.get("CURRENT_CONFIG");
            JSONObject jsonObject = configurationJSON.getJSONObject(configName);
            jsonObject = HtmlDecodingofjson(jsonObject, passwordFields);
            configurationJSON.put(configName, (Object)jsonObject);
            HTMLDecoder.logger.log(Level.INFO, "Current config json is {0}", configName);
        }
        catch (final Exception ex) {
            Logger.getLogger(HTMLDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return configurationJSON;
    }
    
    public static JSONObject HtmlDecodingofjson(JSONObject jsonObject, final ArrayList<String> passwordFields) throws JSONException, ParseException {
        final Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (jsonObject.get((String)obj) instanceof JSONArray) {
                parseArray(jsonObject.get((String)obj), passwordFields);
            }
            else {
                if (!(jsonObject.get((String)obj) instanceof JSONObject)) {
                    continue;
                }
                final JSONObject childjsonObject = HtmlDecodingofjson((JSONObject)jsonObject.get((String)obj), passwordFields);
                jsonObject.put((String)obj, (Object)childjsonObject);
            }
        }
        jsonObject = decodingHTML(jsonObject, passwordFields);
        HTMLDecoder.logger.log(Level.INFO, "Decoding of password field successfully completed");
        return jsonObject;
    }
    
    public static JSONObject decodingHTML(final JSONObject jsonObject, final ArrayList<String> passwordFields) {
        for (final Object passwordkey : passwordFields) {
            if (jsonObject.has((String)passwordkey)) {
                try {
                    final String encodedPassword = (String)jsonObject.get((String)passwordkey);
                    final String decodedPassword = StringEscapeUtils.unescapeHtml(encodedPassword);
                    jsonObject.put((String)passwordkey, (Object)decodedPassword);
                }
                catch (final JSONException ex) {
                    Logger.getLogger(HTMLDecoder.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                }
            }
        }
        return jsonObject;
    }
    
    public static void parseArray(final Object arrObject, final ArrayList<String> passwordFields) throws ParseException, JSONException {
        final JSONArray jsonArr = (JSONArray)arrObject;
        for (int k = 0; k < jsonArr.length(); ++k) {
            if (jsonArr.get(k) instanceof JSONObject) {
                HtmlDecodingofjson((JSONObject)jsonArr.get(k), passwordFields);
            }
        }
    }
    
    static {
        HTMLDecoder.logger = Logger.getLogger("HTMLDecoder");
    }
}
