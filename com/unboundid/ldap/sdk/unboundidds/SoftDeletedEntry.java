package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Entry;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ReadOnlyEntry;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SoftDeletedEntry extends ReadOnlyEntry
{
    public static final String ATTR_SOFT_DELETE_FROM_DN = "ds-soft-delete-from-dn";
    public static final String ATTR_SOFT_DELETE_REQUESTER_DN = "ds-soft-delete-requester-dn";
    public static final String ATTR_SOFT_DELETE_REQUESTER_IP_ADDRESS = "ds-soft-delete-requester-ip-address";
    public static final String ATTR_SOFT_DELETE_TIMESTAMP = "ds-soft-delete-timestamp";
    public static final String OC_SOFT_DELETED_ENTRY = "ds-soft-delete-entry";
    private static final long serialVersionUID = -3450703461178674797L;
    private final Date softDeleteTimestamp;
    private final String softDeleteFromDN;
    private final String softDeleteRequesterDN;
    private final String softDeleteRequesterIPAddress;
    
    public SoftDeletedEntry(final Entry entry) throws LDAPException {
        super(entry);
        if (!entry.hasObjectClass("ds-soft-delete-entry")) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, UnboundIDDSMessages.ERR_SOFT_DELETED_ENTRY_MISSING_OC.get(entry.getDN()));
        }
        this.softDeleteFromDN = entry.getAttributeValue("ds-soft-delete-from-dn");
        this.softDeleteTimestamp = entry.getAttributeValueAsDate("ds-soft-delete-timestamp");
        this.softDeleteRequesterDN = entry.getAttributeValue("ds-soft-delete-requester-dn");
        this.softDeleteRequesterIPAddress = entry.getAttributeValue("ds-soft-delete-requester-ip-address");
        if (this.softDeleteFromDN == null) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, UnboundIDDSMessages.ERR_SOFT_DELETED_ENTRY_MISSING_FROM_DN.get(entry.getDN()));
        }
    }
    
    public String getSoftDeleteFromDN() {
        return this.softDeleteFromDN;
    }
    
    public Date getSoftDeleteTimestamp() {
        return this.softDeleteTimestamp;
    }
    
    public String getSoftDeleteRequesterDN() {
        return this.softDeleteRequesterDN;
    }
    
    public String getSoftDeleteRequesterIPAddress() {
        return this.softDeleteRequesterIPAddress;
    }
    
    public ReadOnlyEntry getUndeletedEntry() {
        final Entry e = this.duplicate();
        e.setDN(this.softDeleteFromDN);
        e.removeAttributeValue("objectClass", "ds-soft-delete-entry");
        e.removeAttribute("ds-soft-delete-from-dn");
        e.removeAttribute("ds-soft-delete-timestamp");
        e.removeAttribute("ds-soft-delete-requester-dn");
        e.removeAttribute("ds-soft-delete-requester-ip-address");
        return new ReadOnlyEntry(e);
    }
    
    public static boolean isSoftDeletedEntry(final Entry entry) {
        return entry.hasObjectClass("ds-soft-delete-entry") && entry.hasAttribute("ds-soft-delete-from-dn");
    }
}
