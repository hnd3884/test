package com.me.mdm.core.enrollment.settings;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;

public class DeviceTypeHandler
{
    public JSONArray createDeviceTypeRules(final JSONArray deviceTypeRules, final Long userAssignmentRuleID, final DataObject dataObject) throws Exception {
        if (deviceTypeRules == null || deviceTypeRules.length() == 0) {
            return null;
        }
        for (int i = 0; i < deviceTypeRules.length(); ++i) {
            final JSONObject deviceRule = deviceTypeRules.getJSONObject(i);
            final Integer modelType = (Integer)deviceRule.get("MODEL_TYPE".toLowerCase());
            final Row row = new Row("DeviceTypeRule");
            row.set("ON_BOARD_RULE_ID", (Object)userAssignmentRuleID);
            row.set("MODEL_TYPE", (Object)modelType);
            dataObject.addRow(row);
        }
        final Iterator iterator = dataObject.getRows("DeviceTypeRule");
        final JSONArray devicetypeArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            devicetypeArray.put((Object)getDeviceTypeJSON(row2));
        }
        return devicetypeArray;
    }
    
    public void getDeviceTypeJoin(final SelectQuery selectQuery) {
        if (selectQuery.getTableList().contains(new Table("OnBoardingRule"))) {
            selectQuery.addJoin(new Join("OnBoardingRule", "DeviceTypeRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
        }
    }
    
    protected static JSONObject getDeviceTypeJSON(final Row row) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("MODEL_RULE_ID".toLowerCase(), row.get("MODEL_RULE_ID"));
        jsonObject.put("MODEL_TYPE".toLowerCase(), row.get("MODEL_TYPE"));
        return jsonObject;
    }
}
