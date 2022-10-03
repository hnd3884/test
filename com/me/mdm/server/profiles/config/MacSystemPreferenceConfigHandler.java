package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class MacSystemPreferenceConfigHandler extends DefaultConfigHandler
{
    public static final String ENABLED_PREFERENCES = "ENABLED_PREFERENCES";
    public static final String DISABLED_PREFERENCES = "DISABLED_PREFERENCES";
    public static final String HIDDEN_PREFERENCES = "HIDDEN_PREFERENCES";
    public static Logger logger;
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray rowArray = MDMDBUtil.getRowsAsJSONArray(dataObject, "MacSystemPreferencePolicy");
            final JSONObject result = new JSONObject();
            final Map<Long, String> inversePreferenceMap = this.getRevPreferenceIndexMap(dataObject);
            final List<String> enabledPreferences = new ArrayList<String>();
            final List<String> disabledPreferences = new ArrayList<String>();
            final List<String> hiddenPreferences = new ArrayList<String>();
            for (int i = 0; i < rowArray.length(); ++i) {
                final JSONObject jsonObject = rowArray.getJSONObject(i);
                final String preference = inversePreferenceMap.get(jsonObject.getLong("MAC_SYSTEM_PREFERENCE_ID"));
                final Integer preferenceStatus = jsonObject.getInt("STATUS");
                final List<String> statusList = getPreferenceDictionaries(preferenceStatus);
                if (statusList.contains("ENABLED_PREFERENCES")) {
                    enabledPreferences.add(preference);
                }
                if (statusList.contains("DISABLED_PREFERENCES")) {
                    disabledPreferences.add(preference);
                }
                if (statusList.contains("HIDDEN_PREFERENCES")) {
                    hiddenPreferences.add(preference);
                }
            }
            result.put("ENABLED_PREFERENCES", (Object)new JSONArray((Collection)enabledPreferences));
            result.put("DISABLED_PREFERENCES", (Object)new JSONArray((Collection)disabledPreferences));
            result.put("HIDDEN_PREFERENCES", (Object)new JSONArray((Collection)hiddenPreferences));
            return new JSONArray().put((Object)result);
        }
        catch (final Exception e) {
            MacSystemPreferenceConfigHandler.logger.log(Level.SEVERE, "Exception occurred in DOToAPIJSON Mac system preference", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private static final List<String> getPreferenceDictionaries(final Integer status) {
        final List<String> preferenceDict = new ArrayList<String>();
        final String binary = Integer.toBinaryString(status);
        final int length = binary.length();
        if (binary.charAt(length - 1) == '1') {
            preferenceDict.add("ENABLED_PREFERENCES");
        }
        if (binary.length() > 1 && binary.charAt(length - 2) == '1') {
            preferenceDict.add("DISABLED_PREFERENCES");
        }
        if (binary.length() > 2 && binary.charAt(length - 3) == '1') {
            preferenceDict.add("HIDDEN_PREFERENCES");
        }
        return preferenceDict;
    }
    
    public Map<String, Long> getPreferenceIndexMap(final Long customerID, final Set<String> preferencesSet) throws Exception {
        final Set<String> preferences = new HashSet<String>(preferencesSet);
        final Map<String, Long> preferenceMap = new HashMap<String, Long>();
        final Iterator<Row> iterator = MDMDBUtil.getRows("MacSystemPreferences", new Object[][] { { "PREFERENCE_BUNDLE_ID", preferences.toArray() }, { "CUSTOMER_ID", customerID } });
        if (iterator != null) {
            final Set<String> existingPreferences = new HashSet<String>();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long preferenceID = (Long)row.get("MAC_SYSTEM_PREFERENCE_ID");
                final String preference = (String)row.get("PREFERENCE_BUNDLE_ID");
                existingPreferences.add(preference);
                preferenceMap.put(preference, preferenceID);
            }
            preferences.removeAll(existingPreferences);
        }
        final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
        final Iterator<String> it = preferences.iterator();
        final Map<String, Row> preferenceRowMap = new HashMap<String, Row>();
        while (it.hasNext()) {
            final String preference = it.next();
            final Row row2 = MDMDBUtil.updateRow(dataObject, "MacSystemPreferences", new Object[][] { { "CUSTOMER_ID", customerID }, { "PREFERENCE_BUNDLE_ID", preference } });
            preferenceRowMap.put(preference, row2);
        }
        if (!preferenceRowMap.isEmpty()) {
            MDMUtil.getPersistence().add(dataObject);
            for (final String prefence : preferenceRowMap.keySet()) {
                final Long preferenceID2 = (Long)preferenceRowMap.get(prefence).get("MAC_SYSTEM_PREFERENCE_ID");
                preferenceMap.put(prefence, preferenceID2);
            }
        }
        return preferenceMap;
    }
    
    private Map<Long, String> getRevPreferenceIndexMap(final DataObject dataObject) throws Exception {
        final Iterator<Row> iterator = dataObject.getRows("MacSystemPreferences");
        final Map<Long, String> inverseMap = new HashMap<Long, String>();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            inverseMap.put((Long)row.get("MAC_SYSTEM_PREFERENCE_ID"), (String)row.get("PREFERENCE_BUNDLE_ID"));
        }
        return inverseMap;
    }
    
    static {
        MacSystemPreferenceConfigHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
