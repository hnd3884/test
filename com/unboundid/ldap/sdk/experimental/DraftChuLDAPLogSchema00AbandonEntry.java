package com.unboundid.ldap.sdk.experimental;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00AbandonEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_ID_TO_ABANDON = "reqId";
    private static final long serialVersionUID = -5205545654036097510L;
    private final int idToAbandon;
    
    public DraftChuLDAPLogSchema00AbandonEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.ABANDON);
        final String idString = entry.getAttributeValue("reqId");
        if (idString == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqId"));
        }
        try {
            this.idToAbandon = Integer.parseInt(idString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_ABANDON_ID_ERROR.get(entry.getDN(), "reqId", idString), e);
        }
    }
    
    public int getIDToAbandon() {
        return this.idToAbandon;
    }
}
