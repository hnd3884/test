package com.me.mdm.core.lockdown.data;

import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class WindowsLockdownConfig extends LockdownRule
{
    public String associatedUser;
    public String autoLogonApp;
    public Boolean isAutoCreateUser;
    public Boolean autoDistributeApps;
    
    public WindowsLockdownConfig() {
        super(2, 3);
    }
    
    public WindowsLockdownConfig(final Row row, final Row ruleRow) {
        super(row);
        this.associatedUser = (String)ruleRow.get("ASSOCIATED_USER");
        this.autoLogonApp = (String)ruleRow.get("AUTO_LOGON_APP");
        this.isAutoCreateUser = (Boolean)ruleRow.get("AUTO_CREATE_USER");
        this.autoDistributeApps = (Boolean)ruleRow.get("AUTO_DISTRIBUTE_APPS");
    }
    
    private Row getRow() {
        final Row row = new Row("WindowsLockdownConfig");
        row.set("ASSOCIATED_USER", (Object)this.associatedUser);
        row.set("AUTO_CREATE_USER", (Object)this.isAutoCreateUser);
        row.set("AUTO_LOGON_APP", (Object)this.autoLogonApp);
        row.set("AUTO_DISTRIBUTE_APPS", (Object)this.autoDistributeApps);
        return row;
    }
    
    @Override
    public Row createRowAndUpdateDo(final DataObject dataObject) throws DataAccessException {
        final Row ruleRow = super.createRowAndUpdateDo(dataObject);
        final Row windowsConfigRow = this.getRow();
        windowsConfigRow.set("RULE_ID", ruleRow.get("RULE_ID"));
        dataObject.addRow(windowsConfigRow);
        return windowsConfigRow;
    }
    
    @Override
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("associated_user", (Object)this.associatedUser);
        jsonObject.put("auto_create_user", (Object)this.isAutoCreateUser);
        jsonObject.put("auto_logon_app", (Object)this.autoLogonApp);
        jsonObject.put("auto_distribute_apps", (Object)this.autoDistributeApps);
        return jsonObject;
    }
}
