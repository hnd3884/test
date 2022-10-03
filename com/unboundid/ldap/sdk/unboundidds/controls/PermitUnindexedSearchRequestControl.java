package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PermitUnindexedSearchRequestControl extends Control
{
    public static final String PERMIT_UNINDEXED_SEARCH_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.55";
    private static final long serialVersionUID = 7192052212547454117L;
    
    public PermitUnindexedSearchRequestControl() {
        this(false);
    }
    
    public PermitUnindexedSearchRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.55", isCritical);
    }
    
    public PermitUnindexedSearchRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PERMIT_UNINDEXED_SEARCH_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PERMIT_UNINDEXED_SEARCH_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PermitUnindexedSearchRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
