package com.me.devicemanagement.framework.utils;

import java.util.Collection;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.logging.Logger;

public class ArrayUtil
{
    private static Logger logger;
    
    public static ArrayList<Object> convert(final JSONArray jsonArray) throws JSONException {
        final ArrayList<Object> list = new ArrayList<Object>();
        try {
            for (int index = 0, arrayLength = jsonArray.length(); index < arrayLength; ++index) {
                list.add(jsonArray.get(index));
            }
        }
        catch (final JSONException e) {
            ArrayUtil.logger.log(Level.SEVERE, "Exception occurred in convert", (Throwable)e);
            throw e;
        }
        return list;
    }
    
    public static JSONArray convert(final Collection<Object> list) {
        return new JSONArray((Collection)list);
    }
    
    static {
        ArrayUtil.logger = Logger.getLogger(ArrayUtil.class.getName());
    }
}
