package com.me.mdm.core.dataprotection.data;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class ConfigurationRule extends EnterpriseRule
{
    Integer configType;
    public static String ruleNameStr;
    public static final int WINDOWS_EDP_POLICY = 3;
    
    public ConfigurationRule(final Integer configType) {
        super(3, ConfigurationRule.ruleNameStr + configType);
        this.configType = configType;
    }
    
    public ConfigurationRule(final Row row, final Integer configType) {
        super(row);
        this.configType = configType;
    }
    
    private Row getRow() {
        final Row row = new Row("EnterpriseConfiguration");
        row.set("CONFIGURATION_TYPE", (Object)this.configType);
        row.set("RULE_ID", (Object)this.ruleID);
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
        ConfigurationRule.ruleNameStr = "configRule;";
    }
}
