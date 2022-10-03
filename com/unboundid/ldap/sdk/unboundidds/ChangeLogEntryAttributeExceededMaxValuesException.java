package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ChangeLogEntryAttributeExceededMaxValuesException extends LDAPSDKException
{
    private static final long serialVersionUID = -9108989779921909512L;
    private final ChangeLogEntryAttributeExceededMaxValuesCount attrInfo;
    
    public ChangeLogEntryAttributeExceededMaxValuesException(final String message, final ChangeLogEntryAttributeExceededMaxValuesCount attrInfo) {
        super(message);
        this.attrInfo = attrInfo;
    }
    
    public ChangeLogEntryAttributeExceededMaxValuesCount getAttributeInfo() {
        return this.attrInfo;
    }
}
