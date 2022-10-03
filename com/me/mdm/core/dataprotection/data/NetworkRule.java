package com.me.mdm.core.dataprotection.data;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class NetworkRule extends EnterpriseRule
{
    public String value;
    public Integer ruleType;
    public static final int PRIMARY_DOMAIN = 1;
    public static final int OTHER_DOMAIN = 2;
    public static final int PROTECTED_DOMAIN = 3;
    public static final int CLOUD_RESOURCE = 4;
    public static final int IP_RANGE = 5;
    public static final int INTERNAL_PROXY_SERVERS = 6;
    public static final int PROXY_SREVERS = 7;
    public static final int NEUTRAL_RESOURCES = 8;
    public static String ruleNameStr;
    
    public NetworkRule(final String value, final int type) {
        super(1, NetworkRule.ruleNameStr + type);
        this.ruleType = type;
        this.value = value;
    }
    
    NetworkRule(final Row row, final Row ruleRow) {
        super(row);
        this.ruleType = (Integer)ruleRow.get("RULE_TYPE");
        this.value = (String)ruleRow.get("VALUE");
    }
    
    @Override
    public String toString() {
        return ", Value : " + this.value + ", RuleType : " + this.ruleType;
    }
    
    private Row getRow() {
        final Row row = new Row("EnterpriseNetworkLimit");
        row.set("RULE_ID", (Object)this.ruleID);
        row.set("RULE_TYPE", (Object)this.ruleType);
        row.set("VALUE", (Object)this.value);
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
        NetworkRule.ruleNameStr = "NetworkRule;";
    }
}
