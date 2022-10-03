package com.me.mdm.api.command.mac;

import java.util.Set;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;

public class DeviceRestartOptionsAPI
{
    public static DeviceRestartOptions getRestartOptions(final long resourceId) throws DataAccessException {
        final Criteria resourceIdCriteria = new Criteria(new Column("DeviceRestartOptions", "RESOURCE_ID"), (Object)resourceId, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("DeviceRestartOptions", resourceIdCriteria);
        Boolean isNotifyUser = null;
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("DeviceRestartOptions");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String optionName = (String)row.get("OPTION_NAME");
                final String optionValue = (String)row.get("OPTION_VALUE");
                if (optionName.equalsIgnoreCase("NotifyUser")) {
                    isNotifyUser = Boolean.parseBoolean(optionValue);
                }
            }
        }
        if (isNotifyUser == null) {
            return null;
        }
        return new DeviceRestartOptions(isNotifyUser);
    }
    
    public static void addOrUpdateRestartOptions(final long resourceId, final Map<String, String> optionsMap) throws DataAccessException {
        final String[] optionKeys = optionsMap.keySet().toArray(new String[0]);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceRestartOptions"));
        final Criteria resourceIdCriteria = new Criteria(new Column("DeviceRestartOptions", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria optionKeysCriteria = new Criteria(new Column("DeviceRestartOptions", "OPTION_NAME"), (Object)optionKeys, 8);
        selectQuery.setCriteria(resourceIdCriteria.and(optionKeysCriteria));
        selectQuery.addSelectColumn(new Column("DeviceRestartOptions", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            updateExistingRestartOptions(optionsMap, dataObject);
        }
        if (!optionsMap.isEmpty()) {
            addNewOptionsForResource(resourceId, optionsMap, dataObject);
        }
        SyMUtil.getPersistence().update(dataObject);
    }
    
    private static void updateExistingRestartOptions(final Map<String, String> optionsMap, final DataObject dataObject) throws DataAccessException {
        final Iterator iterator = dataObject.getRows("DeviceRestartOptions");
        while (iterator.hasNext()) {
            final Row existingOptionRow = iterator.next();
            final String optionName = (String)existingOptionRow.get("OPTION_NAME");
            final String newOptionValue = optionsMap.get(optionName);
            existingOptionRow.set("OPTION_VALUE", (Object)newOptionValue);
            dataObject.updateRow(existingOptionRow);
            optionsMap.remove(optionName);
        }
    }
    
    private static void addNewOptionsForResource(final long resourceId, final Map<String, String> optionsMap, final DataObject dataObject) throws DataAccessException {
        final Set<Map.Entry<String, String>> newOptions = optionsMap.entrySet();
        for (final Map.Entry<String, String> option : newOptions) {
            final Row row = new Row("DeviceRestartOptions");
            row.set("RESOURCE_ID", (Object)resourceId);
            row.set("OPTION_NAME", (Object)option.getKey());
            row.set("OPTION_VALUE", (Object)option.getValue());
            dataObject.addRow(row);
        }
    }
}
