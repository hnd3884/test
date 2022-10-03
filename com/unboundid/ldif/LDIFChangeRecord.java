package com.unboundid.ldif;

import com.unboundid.util.ByteStringBuffer;
import java.util.StringTokenizer;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Collections;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class LDIFChangeRecord implements LDIFRecord
{
    private static final long serialVersionUID = 6917212392170911115L;
    private final List<Control> controls;
    private volatile DN parsedDN;
    private final String dn;
    
    protected LDIFChangeRecord(final String dn, final List<Control> controls) {
        Validator.ensureNotNull(dn);
        this.dn = dn;
        this.parsedDN = null;
        if (controls == null) {
            this.controls = Collections.emptyList();
        }
        else {
            this.controls = Collections.unmodifiableList((List<? extends Control>)controls);
        }
    }
    
    @Override
    public final String getDN() {
        return this.dn;
    }
    
    @Override
    public final DN getParsedDN() throws LDAPException {
        if (this.parsedDN == null) {
            this.parsedDN = new DN(this.dn);
        }
        return this.parsedDN;
    }
    
    public abstract ChangeType getChangeType();
    
    public List<Control> getControls() {
        return this.controls;
    }
    
    public abstract LDIFChangeRecord duplicate(final Control... p0);
    
    public final LDAPResult processChange(final LDAPInterface connection) throws LDAPException {
        return this.processChange(connection, true);
    }
    
    public abstract LDAPResult processChange(final LDAPInterface p0, final boolean p1) throws LDAPException;
    
    final Entry toEntry() throws LDIFException {
        return new Entry(this.toLDIF());
    }
    
    @Override
    public final String[] toLDIF() {
        return this.toLDIF(0);
    }
    
    @Override
    public abstract String[] toLDIF(final int p0);
    
    static void encodeNameAndValue(final String name, final ASN1OctetString value, final List<String> lines) {
        final String line = LDIFWriter.encodeNameAndValue(name, value);
        if (LDIFWriter.commentAboutBase64EncodedValues() && line.startsWith(name + "::")) {
            final StringTokenizer tokenizer = new StringTokenizer(line, "\r\n");
            while (tokenizer.hasMoreTokens()) {
                lines.add(tokenizer.nextToken());
            }
        }
        else {
            lines.add(line);
        }
    }
    
    @Override
    public final void toLDIF(final ByteStringBuffer buffer) {
        this.toLDIF(buffer, 0);
    }
    
    @Override
    public abstract void toLDIF(final ByteStringBuffer p0, final int p1);
    
    @Override
    public final String toLDIFString() {
        final StringBuilder buffer = new StringBuilder();
        this.toLDIFString(buffer, 0);
        return buffer.toString();
    }
    
    @Override
    public final String toLDIFString(final int wrapColumn) {
        final StringBuilder buffer = new StringBuilder();
        this.toLDIFString(buffer, wrapColumn);
        return buffer.toString();
    }
    
    @Override
    public final void toLDIFString(final StringBuilder buffer) {
        this.toLDIFString(buffer, 0);
    }
    
    @Override
    public abstract void toLDIFString(final StringBuilder p0, final int p1);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(final Object p0);
    
    static ASN1OctetString encodeControlString(final Control c) {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        buffer.append((CharSequence)c.getOID());
        if (c.isCritical()) {
            buffer.append((CharSequence)" true");
        }
        else {
            buffer.append((CharSequence)" false");
        }
        final ASN1OctetString value = c.getValue();
        if (value != null) {
            LDIFWriter.encodeValue(value, buffer);
        }
        return buffer.toByteString().toASN1OctetString();
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public abstract void toString(final StringBuilder p0);
}
