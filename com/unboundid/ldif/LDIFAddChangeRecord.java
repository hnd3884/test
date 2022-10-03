package com.unboundid.ldif;

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
import com.unboundid.ldap.sdk.AddRequest;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDIFAddChangeRecord extends LDIFChangeRecord
{
    private static final long serialVersionUID = 4722916031463878423L;
    private final Attribute[] attributes;
    
    public LDIFAddChangeRecord(final String dn, final Attribute... attributes) {
        this(dn, attributes, null);
    }
    
    public LDIFAddChangeRecord(final String dn, final Attribute[] attributes, final List<Control> controls) {
        super(dn, controls);
        Validator.ensureNotNull(attributes);
        Validator.ensureTrue(attributes.length > 0, "LDIFAddChangeRecord.attributes must not be empty.");
        this.attributes = attributes;
    }
    
    public LDIFAddChangeRecord(final String dn, final List<Attribute> attributes) {
        this(dn, attributes, null);
    }
    
    public LDIFAddChangeRecord(final String dn, final List<Attribute> attributes, final List<Control> controls) {
        super(dn, controls);
        Validator.ensureNotNull(attributes);
        Validator.ensureFalse(attributes.isEmpty(), "LDIFAddChangeRecord.attributes must not be empty.");
        attributes.toArray(this.attributes = new Attribute[attributes.size()]);
    }
    
    public LDIFAddChangeRecord(final Entry entry) {
        this(entry, null);
    }
    
    public LDIFAddChangeRecord(final Entry entry, final List<Control> controls) {
        super(entry.getDN(), controls);
        final Collection<Attribute> attrs = entry.getAttributes();
        this.attributes = new Attribute[attrs.size()];
        final Iterator<Attribute> iterator = attrs.iterator();
        for (int i = 0; i < this.attributes.length; ++i) {
            this.attributes[i] = iterator.next();
        }
    }
    
    public LDIFAddChangeRecord(final AddRequest addRequest) {
        super(addRequest.getDN(), addRequest.getControlList());
        final List<Attribute> attrs = addRequest.getAttributes();
        this.attributes = new Attribute[attrs.size()];
        final Iterator<Attribute> iterator = attrs.iterator();
        for (int i = 0; i < this.attributes.length; ++i) {
            this.attributes[i] = iterator.next();
        }
    }
    
    public Attribute[] getAttributes() {
        return this.attributes;
    }
    
    public Entry getEntryToAdd() {
        return new Entry(this.getDN(), this.attributes);
    }
    
    public AddRequest toAddRequest() {
        return this.toAddRequest(true);
    }
    
    public AddRequest toAddRequest(final boolean includeControls) {
        final AddRequest addRequest = new AddRequest(this.getDN(), this.attributes);
        if (includeControls) {
            addRequest.setControls(this.getControls());
        }
        return addRequest;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.ADD;
    }
    
    @Override
    public LDIFAddChangeRecord duplicate(final Control... controls) {
        return new LDIFAddChangeRecord(this.getDN(), this.attributes, StaticUtils.toList(controls));
    }
    
    @Override
    public LDAPResult processChange(final LDAPInterface connection, final boolean includeControls) throws LDAPException {
        return connection.add(this.toAddRequest(includeControls));
    }
    
    @Override
    public String[] toLDIF(final int wrapColumn) {
        List<String> ldifLines = new ArrayList<String>(2 * this.attributes.length);
        LDIFChangeRecord.encodeNameAndValue("dn", new ASN1OctetString(this.getDN()), ldifLines);
        for (final Control c : this.getControls()) {
            LDIFChangeRecord.encodeNameAndValue("control", LDIFChangeRecord.encodeControlString(c), ldifLines);
        }
        ldifLines.add("changetype: add");
        for (final Attribute a : this.attributes) {
            final String name = a.getName();
            for (final ASN1OctetString value : a.getRawValues()) {
                LDIFChangeRecord.encodeNameAndValue(name, value, ldifLines);
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
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("add"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
        for (final Attribute a : this.attributes) {
            final String name = a.getName();
            for (final ASN1OctetString value : a.getRawValues()) {
                LDIFWriter.encodeNameAndValue(name, value, buffer, wrapColumn);
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
        LDIFWriter.encodeNameAndValue("changetype", new ASN1OctetString("add"), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        for (final Attribute a : this.attributes) {
            final String name = a.getName();
            for (final ASN1OctetString value : a.getRawValues()) {
                LDIFWriter.encodeNameAndValue(name, value, buffer, wrapColumn);
                buffer.append(StaticUtils.EOL);
            }
        }
    }
    
    @Override
    public int hashCode() {
        try {
            int hashCode = this.getParsedDN().hashCode();
            for (final Attribute a : this.attributes) {
                hashCode += a.hashCode();
            }
            return hashCode;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new Entry(this.getDN(), this.attributes).hashCode();
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
        if (!(o instanceof LDIFAddChangeRecord)) {
            return false;
        }
        final LDIFAddChangeRecord r = (LDIFAddChangeRecord)o;
        final HashSet<Control> c1 = new HashSet<Control>(this.getControls());
        final HashSet<Control> c2 = new HashSet<Control>(r.getControls());
        if (!c1.equals(c2)) {
            return false;
        }
        final Entry e1 = new Entry(this.getDN(), this.attributes);
        final Entry e2 = new Entry(r.getDN(), r.attributes);
        return e1.equals(e2);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDIFAddChangeRecord(dn='");
        buffer.append(this.getDN());
        buffer.append("', attrs={");
        for (int i = 0; i < this.attributes.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            this.attributes[i].toString(buffer);
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
}
