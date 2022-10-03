package com.unboundid.ldif;

import java.util.Collection;
import java.util.HashSet;
import com.unboundid.util.Debug;
import com.unboundid.util.ByteStringBuffer;
import java.util.Iterator;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDIFDeleteChangeRecord extends LDIFChangeRecord
{
    private static final long serialVersionUID = 9173178539060889790L;
    
    public LDIFDeleteChangeRecord(final String dn) {
        this(dn, null);
    }
    
    public LDIFDeleteChangeRecord(final String dn, final List<Control> controls) {
        super(dn, controls);
    }
    
    public LDIFDeleteChangeRecord(final DeleteRequest deleteRequest) {
        super(deleteRequest.getDN(), deleteRequest.getControlList());
    }
    
    public DeleteRequest toDeleteRequest() {
        return this.toDeleteRequest(true);
    }
    
    public DeleteRequest toDeleteRequest(final boolean includeControls) {
        final DeleteRequest deleteRequest = new DeleteRequest(this.getDN());
        if (includeControls) {
            deleteRequest.setControls(this.getControls());
        }
        return deleteRequest;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.DELETE;
    }
    
    @Override
    public LDIFDeleteChangeRecord duplicate(final Control... controls) {
        return new LDIFDeleteChangeRecord(this.getDN(), StaticUtils.toList(controls));
    }
    
    @Override
    public LDAPResult processChange(final LDAPInterface connection, final boolean includeControls) throws LDAPException {
        return connection.delete(this.toDeleteRequest(includeControls));
    }
    
    @Override
    public String[] toLDIF(final int wrapColumn) {
        List<String> ldifLines = new ArrayList<String>(5);
        LDIFChangeRecord.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), ldifLines);
        for (final Control c : this.getControls()) {
            LDIFChangeRecord.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), ldifLines);
        }
        ldifLines.add("changetype: delete");
        if (wrapColumn > 2) {
            ldifLines = LDIFWriter.wrapLines(wrapColumn, ldifLines);
        }
        final String[] ldifArray = new String[ldifLines.size()];
        ldifLines.toArray(ldifArray);
        return ldifArray;
    }
    
    @Override
    public void toLDIF(final ByteStringBuffer buffer, final int wrapColumn) {
        LDIFWriter.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
        for (final Control c : this.getControls()) {
            LDIFWriter.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), buffer, wrapColumn);
            buffer.append(StaticUtils.EOL_BYTES);
        }
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("delete"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
    }
    
    @Override
    public void toLDIFString(final StringBuilder buffer, final int wrapColumn) {
        LDIFWriter.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        for (final Control c : this.getControls()) {
            LDIFWriter.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), buffer, wrapColumn);
            buffer.append(StaticUtils.EOL);
        }
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("delete"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
    }
    
    @Override
    public int hashCode() {
        try {
            return this.getParsedDN().hashCode();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return StaticUtils.toLowerCase(this.getDN()).hashCode();
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof LDIFDeleteChangeRecord)) {
            return false;
        }
        final LDIFDeleteChangeRecord r = (LDIFDeleteChangeRecord)o;
        final HashSet<Control> c1 = new HashSet<Control>(this.getControls());
        final HashSet<Control> c2 = new HashSet<Control>(r.getControls());
        if (!c1.equals(c2)) {
            return false;
        }
        try {
            return this.getParsedDN().equals(r.getParsedDN());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return StaticUtils.toLowerCase(this.getDN()).equals(StaticUtils.toLowerCase(r.getDN()));
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDIFDeleteChangeRecord(dn='");
        buffer.append(this.getDN());
        buffer.append('\'');
        final List<Control> controls = this.getControls();
        if (!controls.isEmpty()) {
            buffer.append(", controls={");
            final Iterator<Control> iterator = controls.iterator();
            while (iterator.hasNext()) {
                iterator.next().toString(buffer);
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
