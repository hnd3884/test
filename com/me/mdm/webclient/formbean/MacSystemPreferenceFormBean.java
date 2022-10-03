package com.me.mdm.webclient.formbean;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.logging.Level;
import java.util.ArrayList;
import com.me.mdm.server.profiles.config.MacSystemPreferenceConfigHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacSystemPreferenceFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    
    @Override
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws APIHTTPException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws APIHTTPException {
        try {
            final JSONObject inputJSON = dynaActionForm[0];
            this.insertConfigDataItem(inputJSON, dataObject, 0);
            final Long collectionID = (Long)dataObject.getFirstRow("CfgDataToCollection").get("COLLECTION_ID");
            final Long customerID = (Long)MDMDBUtil.getFirstRow("CollnToCustomerRel", new Object[][] { { "COLLECTION_ID", collectionID } }).get("CUSTOMER_ID");
            final Map<String, Integer> preferenceMap = new HashMap<String, Integer>();
            final Set<String> preferences = new HashSet<String>();
            final List<String> dictKeys = new LinkedList<String>();
            dictKeys.add("ENABLED_PREFERENCES");
            dictKeys.add("DISABLED_PREFERENCES");
            dictKeys.add("HIDDEN_PREFERENCES");
            for (final String key : dictKeys) {
                if (!inputJSON.has(key)) {
                    continue;
                }
                preferences.addAll(JSONUtil.getStringListFromJSONArray(inputJSON.getJSONArray(key)));
            }
            final Iterator<String> iterator = preferences.iterator();
            final Map<String, Long> preferenceIndexMap = new MacSystemPreferenceConfigHandler().getPreferenceIndexMap(customerID, preferences);
            while (iterator.hasNext()) {
                final String preference = iterator.next();
                String preferenceStatus = "";
                for (final String key2 : dictKeys) {
                    List<String> dictList = new ArrayList<String>();
                    if (inputJSON.has(key2)) {
                        dictList = JSONUtil.getStringListFromJSONArray(inputJSON.optJSONArray(key2));
                    }
                    final String bit = dictList.contains(preference) ? "1" : "0";
                    preferenceStatus = bit + preferenceStatus;
                }
                preferenceMap.put(preference, Integer.parseInt(preferenceStatus, 2));
            }
            this.addSystemPreference(dataObject, preferenceMap, preferenceIndexMap);
        }
        catch (final Exception e) {
            MacSystemPreferenceFormBean.logger.log(Level.SEVERE, "Exception occured at Mac system preference form bean", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addSystemPreference(final DataObject dataObject, final Map<String, Integer> preferenceMap, final Map<String, Long> preferenceIndexMap) throws Exception {
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        MDMDBUtil.deleteRows(dataObject, "MacSystemPreferencePolicy", new Object[][] { { "CONFIG_DATA_ITEM_ID", configId } });
        for (final String preference : preferenceMap.keySet()) {
            final Integer preferenceStatus = preferenceMap.get(preference);
            final Long preferenceID = preferenceIndexMap.get(preference);
            MDMDBUtil.updateRow(dataObject, "MacSystemPreferencePolicy", new Object[][] { { "CONFIG_DATA_ITEM_ID", configId }, { "MAC_SYSTEM_PREFERENCE_ID", preferenceID }, { "STATUS", preferenceStatus } });
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        final Object configDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Iterator<Row> iterator = configDOFromDB.getRows("MacSystemPreferencePolicy");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Row clonedRow = MDMDBUtil.cloneRow(row, new String[] { "CONFIG_DATA_ITEM_ID" });
            clonedRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
            cloneConfigDO.addRow(clonedRow);
        }
    }
    
    static {
        MacSystemPreferenceFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
