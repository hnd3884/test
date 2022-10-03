package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RejectUnindexedSearchRequestControl extends Control
{
    public static final String REJECT_UNINDEXED_SEARCH_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.54";
    private static final long serialVersionUID = 6331056590003014623L;
    
    public RejectUnindexedSearchRequestControl() {
        this(true);
    }
    
    public RejectUnindexedSearchRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.54", isCritical);
    }
    
    public RejectUnindexedSearchRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_REJECT_UNINDEXED_SEARCH_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_REJECT_UNINDEXED_SEARCH_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RejectUnindexedSearchRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
