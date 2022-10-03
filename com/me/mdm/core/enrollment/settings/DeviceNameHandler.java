package com.me.mdm.core.enrollment.settings;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;

public class DeviceNameHandler
{
    public JSONArray createDeviceNameRules(final JSONArray nameRules, final Long userAssignmentRuleID, final DataObject dataObject) throws Exception {
        if (nameRules == null || nameRules.length() == 0) {
            return null;
        }
        for (int i = 0; i < nameRules.length(); ++i) {
            final JSONObject nameRule = nameRules.getJSONObject(i);
            final String deviceRegex = (String)nameRule.get("DEVICE_NAME_REGEX".toLowerCase());
            final Row row = new Row("DeviceNameRule");
            row.set("ON_BOARD_RULE_ID", (Object)userAssignmentRuleID);
            row.set("DEVICE_NAME_REGEX", (Object)deviceRegex);
            dataObject.addRow(row);
        }
        final Iterator iterator = dataObject.getRows("DeviceNameRule");
        final JSONArray devicenameRules = new JSONArray();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            devicenameRules.put((Object)getDeviceNameJSON(row2));
        }
        return devicenameRules;
    }
    
    public void getDeviceNameJoin(final SelectQuery selectQuery) {
        if (selectQuery.getTableList().contains(new Table("OnBoardingRule"))) {
            selectQuery.addJoin(new Join("OnBoardingRule", "DeviceNameRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
        }
    }
    
    protected static JSONObject getDeviceNameJSON(final Row row) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("NAME_RULE_ID".toLowerCase(), row.get("NAME_RULE_ID"));
        jsonObject.put("DEVICE_NAME_REGEX".toLowerCase(), row.get("DEVICE_NAME_REGEX"));
        return jsonObject;
    }
}
