package com.me.mdm.core.dataprotection.data;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class EnterpriseRule
{
    Long ruleID;
    Integer ruleType;
    String ruleName;
    public static final int NETWROK_RULE = 1;
    public static final int APP_RULE = 2;
    public static final int CONFIG_RULE = 3;
    protected String ruleNameStr;
    
    public EnterpriseRule(final int type, final String name) {
        this.ruleNameStr = "";
        this.ruleType = type;
        this.ruleName = name;
    }
    
    EnterpriseRule(final Row row) {
        this.ruleNameStr = "";
        this.ruleName = (String)row.get("RULE_NAME");
        this.ruleID = (Long)row.get("RULE_ID");
        this.ruleType = (Integer)row.get("RULE_TYPE");
    }
    
    private Row getRow() {
        final Row row = new Row("EnterpriseRules");
        row.set("RULE_NAME", (Object)this.ruleName);
        row.set("RULE_TYPE", (Object)this.ruleType);
        row.set("CREATION_TIME", (Object)System.currentTimeMillis());
        row.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        return row;
    }
    
    public Row createRowAndUpdateDo(final DataObject dataObject) throws DataAccessException {
        final Row row = dataObject.getRow("CorporatePolicy");
        final Row ruleToPolicy = new Row("RuleToPolicy");
        final Row ruleRow = this.getRow();
        dataObject.addRow(ruleRow);
        ruleToPolicy.set("RULE_ID", ruleRow.get("RULE_ID"));
        ruleToPolicy.set("POLICY_ID", row.get("POLICY_ID"));
        dataObject.addRow(ruleToPolicy);
        return ruleRow;
    }
}
