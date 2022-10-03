package com.me.mdm.core.lockdown.data;

import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class LockdownRule
{
    public Long ruleID;
    public Integer ruleType;
    public Long policyID;
    public Integer platform;
    public static final int APP_RULE = 1;
    public static final int CONFIG_RULE = 2;
    protected String ruleNameStr;
    
    public LockdownRule(final int type, final int platform) {
        this.ruleNameStr = "";
        this.ruleType = type;
    }
    
    LockdownRule(final Row row) {
        this.ruleNameStr = "";
        this.policyID = (Long)row.get("POLICY_ID");
        this.ruleID = (Long)row.get("RULE_ID");
        this.ruleType = (Integer)row.get("RULE_TYPE");
        this.platform = (Integer)row.get("PLATFORM_TYPE");
    }
    
    private Row getRow() {
        final Row row = new Row("LockdownRules");
        row.set("RULE_TYPE", (Object)this.ruleType);
        row.set("PLATFORM_TYPE", (Object)this.platform);
        return row;
    }
    
    public Row createRowAndUpdateDo(final DataObject dataObject) throws DataAccessException {
        final Row row = dataObject.getRow("LockdownPolicy");
        final Row ruleRow = this.getRow();
        ruleRow.set("POLICY_ID", row.get("POLICY_ID"));
        dataObject.addRow(ruleRow);
        return ruleRow;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }
}
