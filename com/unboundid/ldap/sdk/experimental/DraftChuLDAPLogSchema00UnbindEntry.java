package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00UnbindEntry extends DraftChuLDAPLogSchema00Entry
{
    private static final long serialVersionUID = -1596182705806691625L;
    
    public DraftChuLDAPLogSchema00UnbindEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.UNBIND);
    }
}
