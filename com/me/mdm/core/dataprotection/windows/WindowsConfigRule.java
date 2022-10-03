package com.me.mdm.core.dataprotection.windows;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.mdm.core.dataprotection.data.ConfigurationRule;

public class WindowsConfigRule extends ConfigurationRule
{
    Integer enforcementLevel;
    Boolean allowUserDecrypt;
    Long dataRecoveryCert;
    
    public WindowsConfigRule(final Integer enforcementLevel, final Boolean allowUserDecrypt, final Long dataRecoveryCert) {
        super(3);
        this.enforcementLevel = enforcementLevel;
        this.allowUserDecrypt = allowUserDecrypt;
        this.dataRecoveryCert = dataRecoveryCert;
    }
    
    public WindowsConfigRule(final Row row, final Row ruleRow) {
        super(row, 3);
        this.enforcementLevel = (Integer)ruleRow.get("ENFORCEMENT_LEVEL");
        this.dataRecoveryCert = (Long)ruleRow.get("DATA_RECOVERY_CERT_ID");
        this.allowUserDecrypt = (Boolean)ruleRow.get("ALLOW_USER_DECRYPTION");
    }
    
    private Row getRow() {
        final Row row = new Row("WindowsEnterpriseConfig");
        row.set("ENFORCEMENT_LEVEL", (Object)this.enforcementLevel);
        row.set("ALLOW_USER_DECRYPTION", (Object)this.allowUserDecrypt);
        row.set("DATA_RECOVERY_CERT_ID", (Object)this.dataRecoveryCert);
        return row;
    }
    
    @Override
    public Row createRowAndUpdateDo(final DataObject dataObject) throws DataAccessException {
        final Row configRow = super.createRowAndUpdateDo(dataObject);
        final Row row = this.getRow();
        row.set("RULE_ID", configRow.get("RULE_ID"));
        dataObject.addRow(row);
        return row;
    }
}
