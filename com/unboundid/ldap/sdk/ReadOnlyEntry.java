package com.unboundid.ldap.sdk;

import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ReadOnlyEntry extends Entry
{
    private static final long serialVersionUID = -6482574870325012756L;
    
    public ReadOnlyEntry(final String dn, final Attribute... attributes) {
        this(dn, (Schema)null, attributes);
    }
    
    public ReadOnlyEntry(final String dn, final Schema schema, final Attribute... attributes) {
        super(dn, schema, attributes);
    }
    
    public ReadOnlyEntry(final DN dn, final Attribute... attributes) {
        this(dn, (Schema)null, attributes);
    }
    
    public ReadOnlyEntry(final DN dn, final Schema schema, final Attribute... attributes) {
        super(dn, schema, attributes);
    }
    
    public ReadOnlyEntry(final String dn, final Collection<Attribute> attributes) {
        this(dn, null, attributes);
    }
    
    public ReadOnlyEntry(final String dn, final Schema schema, final Collection<Attribute> attributes) {
        super(dn, schema, attributes);
    }
    
    public ReadOnlyEntry(final DN dn, final Collection<Attribute> attributes) {
        this(dn, null, attributes);
    }
    
    public ReadOnlyEntry(final DN dn, final Schema schema, final Collection<Attribute> attributes) {
        super(dn, schema, attributes);
    }
    
    public ReadOnlyEntry(final Entry entry) {
        super(entry);
    }
    
    public ReadOnlyEntry(final String... ldifLines) throws LDIFException {
        this(null, ldifLines);
    }
    
    public ReadOnlyEntry(final Schema schema, final String... ldifLines) throws LDIFException {
        super(schema, ldifLines);
    }
    
    @Override
    public void setDN(final String dn) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setDN(final DN dn) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAttribute(final Attribute attribute) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAttribute(final String attributeName, final String attributeValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAttribute(final String attributeName, final byte[] attributeValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAttribute(final String attributeName, final String... attributeValues) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAttribute(final String attributeName, final byte[]... attributeValues) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAttribute(final String attributeName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAttributeValue(final String attributeName, final String attributeValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAttributeValue(final String attributeName, final byte[] attributeValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAttributeValues(final String attributeName, final String... attributeValues) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAttributeValues(final String attributeName, final byte[]... attributeValues) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAttribute(final String attributeName, final String attributeValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAttribute(final String attributeName, final byte[] attributeValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAttribute(final String attributeName, final String... attributeValues) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAttribute(final String attributeName, final byte[]... attributeValues) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
