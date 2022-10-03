package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00ModifyDNEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_DELETE_OLD_RDN = "reqDeleteOldRDN";
    public static final String ATTR_NEW_RDN = "reqNewRDN";
    public static final String ATTR_NEW_SUPERIOR_DN = "reqNewSuperior";
    private static final long serialVersionUID = 5891004379538957384L;
    private final boolean deleteOldRDN;
    private final String newRDN;
    private final String newSuperiorDN;
    
    public DraftChuLDAPLogSchema00ModifyDNEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.MODIFY_DN);
        this.newRDN = entry.getAttributeValue("reqNewRDN");
        if (this.newRDN == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqNewRDN"));
        }
        final String deleteOldRDNString = entry.getAttributeValue("reqDeleteOldRDN");
        if (deleteOldRDNString == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqDeleteOldRDN"));
        }
        final String lowerDeleteOldRDN = StaticUtils.toLowerCase(deleteOldRDNString);
        if (lowerDeleteOldRDN.equals("true")) {
            this.deleteOldRDN = true;
        }
        else {
            if (!lowerDeleteOldRDN.equals("false")) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_DN_DELETE_OLD_RDN_ERROR.get(entry.getDN(), "reqDeleteOldRDN", deleteOldRDNString));
            }
            this.deleteOldRDN = false;
        }
        this.newSuperiorDN = entry.getAttributeValue("reqNewSuperior");
    }
    
    public String getNewRDN() {
        return this.newRDN;
    }
    
    public boolean deleteOldRDN() {
        return this.deleteOldRDN;
    }
    
    public String getNewSuperiorDN() {
        return this.newSuperiorDN;
    }
    
    public ModifyDNRequest toModifyDNRequest() {
        return new ModifyDNRequest(this.getTargetEntryDN(), this.newRDN, this.deleteOldRDN, this.newSuperiorDN, this.getRequestControlArray());
    }
}
