package com.me.mdm.webclient.formbean;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.profiles.config.MacEnergySaverPolicyConfigHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacEnergySaverFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    
    @Override
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (int executionOrder = dynaActionForm.length, i = 0; i < executionOrder; ++i) {
                final JSONObject inputJSON = dynaActionForm[0];
                this.insertConfigDataItem(inputJSON, dataObject, 0);
                final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
                this.deleteEnergyConfigRows(configId, dataObject);
                MDMDBUtil.updateRow(dataObject, "MacEnergySettingsPolicy", new Object[][] { { "CONFIG_DATA_ITEM_ID", configId }, { "DESTROY_FV_STANDBY", inputJSON.optBoolean("DESTROY_FV_STANDBY") }, { "SLEEP_DISABLED", inputJSON.optBoolean("SLEEP_DISABLED") } });
                if (inputJSON.has("DESKTOP_SETTINGS")) {
                    this.addEnergyConfigSettings(configId, "DESKTOP_SETTINGS", inputJSON.getJSONObject("DESKTOP_SETTINGS"), dataObject);
                }
                if (inputJSON.has("PORTABLE_ACPOWER_SETTINGS")) {
                    this.addEnergyConfigSettings(configId, "PORTABLE_ACPOWER_SETTINGS", inputJSON.getJSONObject("PORTABLE_ACPOWER_SETTINGS"), dataObject);
                }
                if (inputJSON.has("PORTABLE_BATTERY_SETTINGS")) {
                    this.addEnergyConfigSettings(configId, "PORTABLE_BATTERY_SETTINGS", inputJSON.getJSONObject("PORTABLE_BATTERY_SETTINGS"), dataObject);
                }
                if (inputJSON.has("SCHEDULE_SETTINGS_POWERON")) {
                    this.addEnergySchedule(configId, inputJSON.getJSONObject("SCHEDULE_SETTINGS_POWERON"), dataObject, "SCHEDULE_SETTINGS_POWERON");
                }
                if (inputJSON.has("SCHEDULE_SETTINGS_POWEROFF")) {
                    this.addEnergySchedule(configId, inputJSON.getJSONObject("SCHEDULE_SETTINGS_POWEROFF"), dataObject, "SCHEDULE_SETTINGS_POWEROFF");
                }
            }
        }
        catch (final Exception e) {
            MacEnergySaverFormBean.logger.log(Level.SEVERE, "Exception occured at energy saver form bean", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addEnergyConfigSettings(final Object configDataID, final String systemType, final JSONObject jsonObject, final DataObject dataObject) throws Exception {
        MDMDBUtil.updateRow(dataObject, "MacEnergyConfigurations", new Object[][] { { "CONFIG_DATA_ITEM_ID", configDataID }, { "DISK_SLEEP_TIME", jsonObject.optInt("DISK_SLEEP_TIME") }, { "DYNAMIC_POWER_SETUP", jsonObject.optBoolean("DYNAMIC_POWER_SETUP") }, { "DISPLAY_SLEEP_TIME", jsonObject.optInt("DISPLAY_SLEEP_TIME") }, { "REDUCE_PROCESSOR_SPEED", jsonObject.optBoolean("REDUCE_PROCESSOR_SPEED") }, { "SYSTEM_SLEEP_TIME", jsonObject.optInt("SYSTEM_SLEEP_TIME") }, { "SYSTEM_TYPE", MacEnergySaverPolicyConfigHandler.ENERGY_CONFIGURATION_SYSTEM_MAP.get(systemType) }, { "WAKE_ON_LAN", jsonObject.optBoolean("WAKE_ON_LAN") }, { "WAKE_ON_MODEM_RING", jsonObject.optBoolean("WAKE_ON_MODEM_RING") }, { "RESTART_AFTER_POWER_LOSS", jsonObject.optBoolean("RESTART_AFTER_POWER_LOSS") } });
    }
    
    private void addEnergySchedule(final Object policyID, final JSONObject jsonObject, final DataObject dataObject, final String scheduleType) throws Exception {
        if (!scheduleType.equalsIgnoreCase(new MacEnergySaverPolicyConfigHandler().getScheduleKeyName(jsonObject.getInt("EVENT_TYPE")))) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        MDMDBUtil.updateRow(dataObject, "MacEnergySchedule", new Object[][] { { "CONFIG_DATA_ITEM_ID", policyID }, { "EVENT_TYPE", jsonObject.getInt("EVENT_TYPE") }, { "TIME_IN_MINUTES", jsonObject.getInt("TIME_IN_MINUTES") }, { "WEEKDAY", jsonObject.getInt("WEEKDAY") } });
    }
    
    private void deleteEnergyConfigRows(final Object policyID, final DataObject dataObject) throws Exception {
        MDMDBUtil.deleteRows(dataObject, "MacEnergySchedule", new Object[][] { { "CONFIG_DATA_ITEM_ID", policyID } });
        MDMDBUtil.deleteRows(dataObject, "MacEnergyConfigurations", new Object[][] { { "CONFIG_DATA_ITEM_ID", policyID } });
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        if (configDOFromDB.containsTable("MacEnergyConfigurations")) {
            final Iterator<Row> iterator = configDOFromDB.getRows("MacEnergyConfigurations");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Row clonedRow = MDMDBUtil.cloneRow(row, new String[] { "CONFIG_DATA_ITEM_ID" });
                clonedRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                cloneConfigDO.addRow(clonedRow);
            }
        }
        if (configDOFromDB.containsTable("MacEnergySchedule")) {
            final Iterator<Row> iterator = configDOFromDB.getRows("MacEnergySchedule");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Row clonedRow = MDMDBUtil.cloneRow(row, new String[] { "CONFIG_DATA_ITEM_ID" });
                clonedRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                cloneConfigDO.addRow(clonedRow);
            }
        }
    }
    
    static {
        MacEnergySaverFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
