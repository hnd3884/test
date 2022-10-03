package com.me.mdm.server.profiles.config;

import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.persistence.Row;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import java.util.Map;
import java.util.logging.Logger;

public class MacEnergySaverPolicyConfigHandler extends DefaultConfigHandler
{
    public static Logger logger;
    public static final String DESKTOP_SETTINGS = "DESKTOP_SETTINGS";
    public static final String PORTABLE_ACPOWER_SETTINGS = "PORTABLE_ACPOWER_SETTINGS";
    public static final String PORTABLE_BATTERY_SETTINGS = "PORTABLE_BATTERY_SETTINGS";
    public static final int DESKTOP_SETTING_ID = 1;
    public static final int PORTABLE_ACPOWER_SETTING_ID = 2;
    public static final int PORTABLE_BATTERY_SETTING_ID = 3;
    public static final String SCHEDULE_SETTINGS_POWERON = "SCHEDULE_SETTINGS_POWERON";
    public static final String SCHEDULE_SETTINGS_POWEROFF = "SCHEDULE_SETTINGS_POWEROFF";
    public static final int WAKE_SCHEDULE_ID = 1;
    public static final int POWERON_SCHEDULE_ID = 2;
    public static final int WAKE_POWERON_SCHEDULE_ID = 3;
    public static final int SLEEP_SCHEDULE_ID = 4;
    public static final int SHUTDOWN_SCHEDULE_ID = 5;
    public static final int RESTART_SCHEDULE_ID = 6;
    public static final Map<String, Integer> ENERGY_CONFIGURATION_SYSTEM_MAP;
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName);
            for (int i = 0; i < result.length(); ++i) {
                final JSONObject jsonObject = result.getJSONObject(0);
                final Long policyID = jsonObject.getLong("payload_id");
                this.addEnergySchedule(jsonObject, dataObject, policyID);
                this.addEnergyConfigurations(jsonObject, dataObject, policyID);
            }
            return result;
        }
        catch (final Exception e) {
            MacEnergySaverPolicyConfigHandler.logger.log(Level.SEVERE, "Exception occurred in DOToAPIJSON macOS energy saver policy", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addEnergyConfigurations(final JSONObject jsonObject, final DataObject dataObject, final Long policyID) throws Exception {
        jsonObject.remove("DESKTOP_SETTINGS".toLowerCase());
        jsonObject.remove("PORTABLE_ACPOWER_SETTINGS".toLowerCase());
        jsonObject.remove("PORTABLE_BATTERY_SETTINGS".toLowerCase());
        final Criteria criteria = new Criteria(Column.getColumn("MacEnergyConfigurations", "CONFIG_DATA_ITEM_ID"), (Object)policyID, 0);
        final Iterator<Row> iterator = dataObject.getRows("MacEnergyConfigurations", criteria);
        final Map<String, Integer> energyConfigMap = MacEnergySaverPolicyConfigHandler.ENERGY_CONFIGURATION_SYSTEM_MAP;
        final Map<Integer, String> inverseEnergyMap = energyConfigMap.entrySet().stream().collect(Collectors.toMap((Function<? super Object, ? extends Integer>)Map.Entry::getValue, (Function<? super Object, ? extends String>)Map.Entry::getKey));
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final JSONObject configJSON = MDMDBUtil.rowToJSON(row, new String[] { "CONFIG_DATA_ITEM_ID", "SYSTEM_TYPE" });
            final Integer systemType = (Integer)row.get("SYSTEM_TYPE");
            jsonObject.put((String)inverseEnergyMap.get(systemType), (Object)configJSON);
        }
    }
    
    private void addEnergySchedule(final JSONObject jsonObject, final DataObject dataObject, final Long policyID) throws Exception {
        jsonObject.remove("SCHEDULE_SETTINGS_POWERON".toLowerCase());
        jsonObject.remove("SCHEDULE_SETTINGS_POWEROFF".toLowerCase());
        final Criteria criteria = new Criteria(Column.getColumn("MacEnergySchedule", "CONFIG_DATA_ITEM_ID"), (Object)policyID, 0);
        final Iterator<Row> iterator = dataObject.getRows("MacEnergySchedule", criteria);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final JSONObject configJSON = MDMDBUtil.rowToJSON(row, new String[] { "CONFIG_DATA_ITEM_ID" });
            final Integer eventType = (Integer)row.get("EVENT_TYPE");
            jsonObject.put(this.getScheduleKeyName(eventType), (Object)configJSON);
        }
    }
    
    public String getScheduleKeyName(final Integer eventType) {
        switch (eventType) {
            case 1:
            case 2:
            case 3: {
                return "SCHEDULE_SETTINGS_POWERON";
            }
            case 4:
            case 5:
            case 6: {
                return "SCHEDULE_SETTINGS_POWEROFF";
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        MacEnergySaverPolicyConfigHandler.logger = Logger.getLogger("MDMConfigLogger");
        ENERGY_CONFIGURATION_SYSTEM_MAP = new HashMap<String, Integer>() {
            {
                this.put("DESKTOP_SETTINGS", 1);
                this.put("PORTABLE_ACPOWER_SETTINGS", 2);
                this.put("PORTABLE_BATTERY_SETTINGS", 3);
            }
        };
    }
}
