package com.unboundid.ldif;

import java.util.Collection;
import java.util.HashSet;
import com.unboundid.util.Debug;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ChangeType;
import java.util.Iterator;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDIFModifyChangeRecord extends LDIFChangeRecord
{
    public static final String PROPERTY_ALWAYS_INCLUDE_TRAILING_DASH = "com.unboundid.ldif.modify.alwaysIncludeTrailingDash";
    private static boolean alwaysIncludeTrailingDash;
    private static final long serialVersionUID = -7558098319600288036L;
    private final Modification[] modifications;
    
    public LDIFModifyChangeRecord(final String dn, final Modification... modifications) {
        this(dn, modifications, null);
    }
    
    public LDIFModifyChangeRecord(final String dn, final Modification[] modifications, final List<Control> controls) {
        super(dn, controls);
        Validator.ensureNotNull(modifications);
        Validator.ensureTrue(modifications.length > 0, "LDIFModifyChangeRecord.modifications must not be empty.");
        this.modifications = modifications;
    }
    
    public LDIFModifyChangeRecord(final String dn, final List<Modification> modifications) {
        this(dn, modifications, null);
    }
    
    public LDIFModifyChangeRecord(final String dn, final List<Modification> modifications, final List<Control> controls) {
        super(dn, controls);
        Validator.ensureNotNull(modifications);
        Validator.ensureFalse(modifications.isEmpty(), "LDIFModifyChangeRecord.modifications must not be empty.");
        modifications.toArray(this.modifications = new Modification[modifications.size()]);
    }
    
    public LDIFModifyChangeRecord(final ModifyRequest modifyRequest) {
        super(modifyRequest.getDN(), modifyRequest.getControlList());
        final List<Modification> mods = modifyRequest.getModifications();
        this.modifications = new Modification[mods.size()];
        final Iterator<Modification> iterator = mods.iterator();
        for (int i = 0; i < this.modifications.length; ++i) {
            this.modifications[i] = iterator.next();
        }
    }
    
    public static boolean alwaysIncludeTrailingDash() {
        return LDIFModifyChangeRecord.alwaysIncludeTrailingDash;
    }
    
    public static void setAlwaysIncludeTrailingDash(final boolean alwaysIncludeTrailingDash) {
        LDIFModifyChangeRecord.alwaysIncludeTrailingDash = alwaysIncludeTrailingDash;
    }
    
    public Modification[] getModifications() {
        return this.modifications;
    }
    
    public ModifyRequest toModifyRequest() {
        return this.toModifyRequest(true);
    }
    
    public ModifyRequest toModifyRequest(final boolean includeControls) {
        final ModifyRequest modifyRequest = new ModifyRequest(this.getDN(), this.modifications);
        if (includeControls) {
            modifyRequest.setControls(this.getControls());
        }
        return modifyRequest;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.MODIFY;
    }
    
    @Override
    public LDIFModifyChangeRecord duplicate(final Control... controls) {
        return new LDIFModifyChangeRecord(this.getDN(), this.modifications, StaticUtils.toList(controls));
    }
    
    @Override
    public LDAPResult processChange(final LDAPInterface connection, final boolean includeControls) throws LDAPException {
        return connection.modify(this.toModifyRequest(includeControls));
    }
    
    @Override
    public String[] toLDIF(final int wrapColumn) {
        List<String> ldifLines = new ArrayList<String>(this.modifications.length * 4);
        LDIFChangeRecord.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), ldifLines);
        for (final Control c : this.getControls()) {
            LDIFChangeRecord.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), ldifLines);
        }
        ldifLines.add("changetype: modify");
        for (int i = 0; i < this.modifications.length; ++i) {
            final String attrName = this.modifications[i].getAttributeName();
            switch (this.modifications[i].getModificationType().intValue()) {
                case 0: {
                    ldifLines.add("add: " + attrName);
                    break;
                }
                case 1: {
                    ldifLines.add("delete: " + attrName);
                    break;
                }
                case 2: {
                    ldifLines.add("replace: " + attrName);
                    break;
                }
                case 3: {
                    ldifLines.add("increment: " + attrName);
                    break;
                }
                default: {
                    continue;
                }
            }
            for (final ASN1OctetString value : this.modifications[i].getRawValues()) {
                LDIFChangeRecord.encodeNameAndValue(attrName, value, ldifLines);
            }
            if (LDIFModifyChangeRecord.alwaysIncludeTrailingDash || i < this.modifications.length - 1) {
                ldifLines.add("-");
            }
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
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("modify"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
        for (int i = 0; i < this.modifications.length; ++i) {
            final String attrName = this.modifications[i].getAttributeName();
            switch (this.modifications[i].getModificationType().intValue()) {
                case 0: {
                    LDIFWriter.encodeNameAndValue("add", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL_BYTES);
                    break;
                }
                case 1: {
                    LDIFWriter.encodeNameAndValue("delete", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL_BYTES);
                    break;
                }
                case 2: {
                    LDIFWriter.encodeNameAndValue("replace", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL_BYTES);
                    break;
                }
                case 3: {
                    LDIFWriter.encodeNameAndValue("increment", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL_BYTES);
                    break;
                }
                default: {
                    continue;
                }
            }
            for (final ASN1OctetString value : this.modifications[i].getRawValues()) {
                LDIFWriter.encodeNameAndValue(attrName, value, buffer, wrapColumn);
                buffer.append(StaticUtils.EOL_BYTES);
            }
            if (LDIFModifyChangeRecord.alwaysIncludeTrailingDash || i < this.modifications.length - 1) {
                buffer.append('-');
                buffer.append(StaticUtils.EOL_BYTES);
            }
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
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("modify"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        for (int i = 0; i < this.modifications.length; ++i) {
            final String attrName = this.modifications[i].getAttributeName();
            switch (this.modifications[i].getModificationType().intValue()) {
                case 0: {
                    LDIFWriter.encodeNameAndValue("add", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL);
                    break;
                }
                case 1: {
                    LDIFWriter.encodeNameAndValue("delete", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL);
                    break;
                }
                case 2: {
                    LDIFWriter.encodeNameAndValue("replace", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL);
                    break;
                }
                case 3: {
                    LDIFWriter.encodeNameAndValue("increment", new ASN1OctetString(attrName), buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL);
                    break;
                }
                default: {
                    continue;
                }
            }
            for (final ASN1OctetString value : this.modifications[i].getRawValues()) {
                LDIFWriter.encodeNameAndValue(attrName, value, buffer, wrapColumn);
                buffer.append(StaticUtils.EOL);
            }
            if (LDIFModifyChangeRecord.alwaysIncludeTrailingDash || i < this.modifications.length - 1) {
                buffer.append('-');
                buffer.append(StaticUtils.EOL);
            }
        }
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        try {
            hashCode = this.getParsedDN().hashCode();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            hashCode = StaticUtils.toLowerCase(this.getDN()).hashCode();
        }
        for (final Modification m : this.modifications) {
            hashCode += m.hashCode();
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
        if (!(o instanceof LDIFModifyChangeRecord)) {
            return false;
        }
        final LDIFModifyChangeRecord r = (LDIFModifyChangeRecord)o;
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
        if (this.modifications.length != r.modifications.length) {
            return false;
        }
        for (int i = 0; i < this.modifications.length; ++i) {
            if (!this.modifications[i].equals(r.modifications[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDIFModifyChangeRecord(dn='");
        buffer.append(this.getDN());
        buffer.append("', mods={");
        for (int i = 0; i < this.modifications.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            this.modifications[i].toString(buffer);
        }
        buffer.append('}');
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
    
    static {
        LDIFModifyChangeRecord.alwaysIncludeTrailingDash = true;
        final String propValue = StaticUtils.getSystemProperty("com.unboundid.ldif.modify.alwaysIncludeTrailingDash");
        if (propValue != null && propValue.equalsIgnoreCase("false")) {
            LDIFModifyChangeRecord.alwaysIncludeTrailingDash = false;
        }
    }
}
