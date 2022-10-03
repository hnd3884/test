package com.unboundid.ldif;

import java.util.Collection;
import java.util.HashSet;
import com.unboundid.util.Debug;
import com.unboundid.util.ByteStringBuffer;
import java.util.Iterator;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDIFModifyDNChangeRecord extends LDIFChangeRecord
{
    private static final long serialVersionUID = 5804442145450388071L;
    private final boolean deleteOldRDN;
    private volatile DN parsedNewSuperiorDN;
    private volatile RDN parsedNewRDN;
    private final String newRDN;
    private final String newSuperiorDN;
    
    public LDIFModifyDNChangeRecord(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) {
        this(dn, newRDN, deleteOldRDN, newSuperiorDN, null);
    }
    
    public LDIFModifyDNChangeRecord(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN, final List<Control> controls) {
        super(dn, controls);
        Validator.ensureNotNull(newRDN);
        this.newRDN = newRDN;
        this.deleteOldRDN = deleteOldRDN;
        this.newSuperiorDN = newSuperiorDN;
        this.parsedNewRDN = null;
        this.parsedNewSuperiorDN = null;
    }
    
    public LDIFModifyDNChangeRecord(final ModifyDNRequest modifyDNRequest) {
        super(modifyDNRequest.getDN(), modifyDNRequest.getControlList());
        this.newRDN = modifyDNRequest.getNewRDN();
        this.deleteOldRDN = modifyDNRequest.deleteOldRDN();
        this.newSuperiorDN = modifyDNRequest.getNewSuperiorDN();
        this.parsedNewRDN = null;
        this.parsedNewSuperiorDN = null;
    }
    
    public String getNewRDN() {
        return this.newRDN;
    }
    
    public RDN getParsedNewRDN() throws LDAPException {
        if (this.parsedNewRDN == null) {
            this.parsedNewRDN = new RDN(this.newRDN);
        }
        return this.parsedNewRDN;
    }
    
    public boolean deleteOldRDN() {
        return this.deleteOldRDN;
    }
    
    public String getNewSuperiorDN() {
        return this.newSuperiorDN;
    }
    
    public DN getParsedNewSuperiorDN() throws LDAPException {
        if (this.parsedNewSuperiorDN == null && this.newSuperiorDN != null) {
            this.parsedNewSuperiorDN = new DN(this.newSuperiorDN);
        }
        return this.parsedNewSuperiorDN;
    }
    
    public DN getNewDN() throws LDAPException {
        if (this.newSuperiorDN != null) {
            return new DN(this.getParsedNewRDN(), this.getParsedNewSuperiorDN());
        }
        final DN parentDN = this.getParsedDN().getParent();
        if (parentDN == null) {
            return new DN(new RDN[] { this.getParsedNewRDN() });
        }
        return new DN(this.getParsedNewRDN(), parentDN);
    }
    
    public ModifyDNRequest toModifyDNRequest() {
        return this.toModifyDNRequest(true);
    }
    
    public ModifyDNRequest toModifyDNRequest(final boolean includeControls) {
        final ModifyDNRequest modifyDNRequest = new ModifyDNRequest(this.getDN(), this.newRDN, this.deleteOldRDN, this.newSuperiorDN);
        if (includeControls) {
            modifyDNRequest.setControls(this.getControls());
        }
        return modifyDNRequest;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.MODIFY_DN;
    }
    
    @Override
    public LDIFModifyDNChangeRecord duplicate(final Control... controls) {
        return new LDIFModifyDNChangeRecord(this.getDN(), this.newRDN, this.deleteOldRDN, this.newSuperiorDN, StaticUtils.toList(controls));
    }
    
    @Override
    public LDAPResult processChange(final LDAPInterface connection, final boolean includeControls) throws LDAPException {
        return connection.modifyDN(this.toModifyDNRequest(includeControls));
    }
    
    @Override
    public String[] toLDIF(final int wrapColumn) {
        List<String> ldifLines = new ArrayList<String>(10);
        LDIFChangeRecord.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), ldifLines);
        for (final Control c : this.getControls()) {
            LDIFChangeRecord.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), ldifLines);
        }
        ldifLines.add("changetype: moddn");
        LDIFChangeRecord.encodeNameAndValue("newrdn", new ASN1OctetString(this.newRDN), ldifLines);
        ldifLines.add("deleteoldrdn: " + (this.deleteOldRDN ? "1" : "0"));
        if (this.newSuperiorDN != null) {
            LDIFChangeRecord.encodeNameAndValue("newsuperior", new ASN1OctetString(this.newSuperiorDN), ldifLines);
        }
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
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("moddn"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
        LDIFWriter.encodeNameAndValue("newrdn", new ASN1OctetString(this.newRDN), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
        if (this.deleteOldRDN) {
            LDIFWriter.encodeNameAndValue("deleteoldrdn", new ASN1OctetString("1"), buffer, wrapColumn);
        }
        else {
            LDIFWriter.encodeNameAndValue("deleteoldrdn", new ASN1OctetString("0"), buffer, wrapColumn);
        }
        buffer.append(StaticUtils.EOL_BYTES);
        if (this.newSuperiorDN != null) {
            LDIFWriter.encodeNameAndValue("newsuperior", new ASN1OctetString(this.newSuperiorDN), buffer, wrapColumn);
            buffer.append(StaticUtils.EOL_BYTES);
        }
    }
    
    @Override
    public void toLDIFString(final StringBuilder buffer, final int wrapColumn) {
        LDIFWriter.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        for (final Control c : this.getControls()) {
            LDIFWriter.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), buffer, wrapColumn);
            buffer.append(StaticUtils.EOL);
        }
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("moddn"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        LDIFWriter.encodeNameAndValue("newrdn", new ASN1OctetString(this.newRDN), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        if (this.deleteOldRDN) {
            LDIFWriter.encodeNameAndValue("deleteoldrdn", new ASN1OctetString("1"), buffer, wrapColumn);
        }
        else {
            LDIFWriter.encodeNameAndValue("deleteoldrdn", new ASN1OctetString("0"), buffer, wrapColumn);
        }
        buffer.append(StaticUtils.EOL);
        if (this.newSuperiorDN != null) {
            LDIFWriter.encodeNameAndValue("newsuperior", new ASN1OctetString(this.newSuperiorDN), buffer, wrapColumn);
            buffer.append(StaticUtils.EOL);
        }
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        try {
            hashCode = this.getParsedDN().hashCode() + this.getParsedNewRDN().hashCode();
            if (this.newSuperiorDN != null) {
                hashCode += this.getParsedNewSuperiorDN().hashCode();
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            hashCode = StaticUtils.toLowerCase(this.getDN()).hashCode() + StaticUtils.toLowerCase(this.newRDN).hashCode();
            if (this.newSuperiorDN != null) {
                hashCode += StaticUtils.toLowerCase(this.newSuperiorDN).hashCode();
            }
        }
        if (this.deleteOldRDN) {
            ++hashCode;
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof LDIFModifyDNChangeRecord)) {
            return false;
        }
        final LDIFModifyDNChangeRecord r = (LDIFModifyDNChangeRecord)o;
        final HashSet<Control> c1 = new HashSet<Control>(this.getControls());
        final HashSet<Control> c2 = new HashSet<Control>(r.getControls());
        if (!c1.equals(c2)) {
            return false;
        }
        try {
            if (!this.getParsedDN().equals(r.getParsedDN())) {
                return false;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (!StaticUtils.toLowerCase(this.getDN()).equals(StaticUtils.toLowerCase(r.getDN()))) {
                return false;
            }
        }
        try {
            if (!this.getParsedNewRDN().equals(r.getParsedNewRDN())) {
                return false;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (!StaticUtils.toLowerCase(this.newRDN).equals(StaticUtils.toLowerCase(r.newRDN))) {
                return false;
            }
        }
        if (this.newSuperiorDN == null) {
            if (r.newSuperiorDN != null) {
                return false;
            }
        }
        else {
            if (r.newSuperiorDN == null) {
                return false;
            }
            try {
                if (!this.getParsedNewSuperiorDN().equals(r.getParsedNewSuperiorDN())) {
                    return false;
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (!StaticUtils.toLowerCase(this.newSuperiorDN).equals(StaticUtils.toLowerCase(r.newSuperiorDN))) {
                    return false;
                }
            }
        }
        return this.deleteOldRDN == r.deleteOldRDN;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDIFModifyDNChangeRecord(dn='");
        buffer.append(this.getDN());
        buffer.append("', newRDN='");
        buffer.append(this.newRDN);
        buffer.append("', deleteOldRDN=");
        buffer.append(this.deleteOldRDN);
        if (this.newSuperiorDN != null) {
            buffer.append(", newSuperiorDN='");
            buffer.append(this.newSuperiorDN);
            buffer.append('\'');
        }
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
