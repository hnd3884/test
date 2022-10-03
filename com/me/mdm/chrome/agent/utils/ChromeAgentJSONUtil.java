package com.me.mdm.chrome.agent.utils;

import org.json.JSONException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

public class ChromeAgentJSONUtil
{
    public List convertJSONArrayTOList(final JSONArray array) {
        final List arrayList = new ArrayList();
        try {
            for (int i = 0; i < array.length(); ++i) {
                arrayList.add(array.get(i));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMChromeAgentLogger").log(Level.SEVERE, () -> "{" + this.getClass().getCanonicalName() + ".convertJSONArrayTOList[JSONArray]}. Array : " + jsonArray + " Error :" + ex2.getMessage());
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
            Logger.getLogger("MDMChromeAgentLogger").log(Level.SEVERE, () -> "{" + this.getClass().getCanonicalName() + ".convertListToJSONArray[List]}. List : " + list2 + " Error :" + ex2.getMessage());
        }
        return array;
    }
    
    public JSONArray mergeJSONArray(final JSONArray jsonArray1, final JSONArray jsonArray2) {
        JSONArray mergedJson = new JSONArray();
        try {
            final List list1 = this.jsonArrayToList(jsonArray1);
            final List list2 = this.jsonArrayToList(jsonArray2);
            for (final Object o : list1) {
                if (!list2.contains(o)) {
                    list2.add(o);
                }
            }
            mergedJson = new JSONArray((Collection)list2);
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMChromeAgentLogger").log(Level.INFO, "Exception occurred while merging two JSONArray{0}", exp.getMessage());
        }
        return mergedJson;
    }
    
    public ArrayList<String> jsonArrayToList(final JSONArray array) throws JSONException {
        final ArrayList<String> list = new ArrayList<String>();
        if (array != null) {
            for (int i = 0; i < array.length(); ++i) {
                list.add(String.valueOf(array.get(i)));
            }
        }
        return list;
    }
}
