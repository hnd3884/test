package com.me.mdm.core.dataprotection.data;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class AppRule extends EnterpriseRule
{
    public String appIdentifier;
    public Integer appType;
    public Boolean isAllowed;
    public static String ruleNameStr;
    public static final int MODERN_APP = 1;
    public static final int LEGACY_APP = 2;
    
    public AppRule(final String value, final int type, final Boolean isAllowed) {
        super(2, AppRule.ruleNameStr + type);
        this.appType = type;
        this.appIdentifier = value;
        this.isAllowed = isAllowed;
    }
    
    AppRule(final Row row, final Row appRow) {
        super(row);
        this.appIdentifier = (String)appRow.get("APP_IDENTIFIER");
        this.appType = (Integer)appRow.get("APP_TYPE");
        this.isAllowed = (Boolean)appRow.get("IS_ALLOWED");
    }
    
    @Override
    public String toString() {
        return "AppRuleID : , Value : " + this.appIdentifier + ", AppType : " + this.appType + ", isAllowed : " + this.isAllowed;
    }
    
    private Row getRow() {
        final Row row = new Row("EnterpriseApplications");
        row.set("APP_IDENTIFIER", (Object)this.appIdentifier);
        row.set("IS_ALLOWED", (Object)this.isAllowed);
        row.set("APP_TYPE", (Object)this.appType);
        return row;
    }
    
    @Override
    public Row createRowAndUpdateDo(final DataObject dataObject) throws DataAccessException {
        final Row ruleRow = super.createRowAndUpdateDo(dataObject);
        final Row row = this.getRow();
        row.set("RULE_ID", ruleRow.get("RULE_ID"));
        dataObject.addRow(row);
        return row;
    }
    
    static {
        AppRule.ruleNameStr = "AppRule;";
    }
}
