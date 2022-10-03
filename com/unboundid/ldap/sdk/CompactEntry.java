package com.unboundid.ldap.sdk;

import com.unboundid.util.ByteStringBuffer;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class CompactEntry implements Serializable
{
    private static final long serialVersionUID = 8067151651120794058L;
    private final CompactAttribute[] attributes;
    private int hashCode;
    private final String dn;
    
    public CompactEntry(final Entry entry) {
        Validator.ensureNotNull(entry);
        this.dn = entry.getDN();
        this.hashCode = -1;
        final Collection<Attribute> attrs = entry.getAttributes();
        this.attributes = new CompactAttribute[attrs.size()];
        final Iterator<Attribute> iterator = attrs.iterator();
        for (int i = 0; i < this.attributes.length; ++i) {
            this.attributes[i] = new CompactAttribute(iterator.next());
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public DN getParsedDN() throws LDAPException {
        return new DN(this.dn);
    }
    
    public RDN getRDN() throws LDAPException {
        return this.getParsedDN().getRDN();
    }
    
    public DN getParentDN() throws LDAPException {
        return this.getParsedDN().getParent();
    }
    
    public String getParentDNString() throws LDAPException {
        return this.getParsedDN().getParentString();
    }
    
    public boolean hasAttribute(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        for (final CompactAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        for (final CompactAttribute a : this.attributes) {
            if (a.toAttribute().equals(attribute)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttributeValue(final String attributeName, final String attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        for (final CompactAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName) && a.toAttribute().hasValue(attributeValue)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttributeValue(final String attributeName, final byte[] attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        for (final CompactAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName) && a.toAttribute().hasValue(attributeValue)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasObjectClass(final String objectClassName) {
        return this.hasAttributeValue("objectClass", objectClassName);
    }
    
    public Collection<Attribute> getAttributes() {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(this.attributes.length);
        for (final CompactAttribute a : this.attributes) {
            attrList.add(a.toAttribute());
        }
        return Collections.unmodifiableCollection((Collection<? extends Attribute>)attrList);
    }
    
    public Attribute getAttribute(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        for (final CompactAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName)) {
                return a.toAttribute();
            }
        }
        return null;
    }
    
    public List<Attribute> getAttributesWithOptions(final String baseName, final Set<String> options) {
        return this.toEntry().getAttributesWithOptions(baseName, options);
    }
    
    public String getAttributeValue(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final CompactAttribute[] arr$ = this.attributes;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final CompactAttribute a = arr$[i$];
            if (a.getName().equalsIgnoreCase(attributeName)) {
                final String[] values = a.getStringValues();
                if (values.length > 0) {
                    return values[0];
                }
                return null;
            }
            else {
                ++i$;
            }
        }
        return null;
    }
    
    public byte[] getAttributeValueBytes(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final CompactAttribute[] arr$ = this.attributes;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final CompactAttribute a = arr$[i$];
            if (a.getName().equalsIgnoreCase(attributeName)) {
                final byte[][] values = a.getByteValues();
                if (values.length > 0) {
                    return values[0];
                }
                return null;
            }
            else {
                ++i$;
            }
        }
        return null;
    }
    
    public Boolean getAttributeValueAsBoolean(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.getAttribute(attributeName);
        if (a == null) {
            return null;
        }
        return a.getValueAsBoolean();
    }
    
    public Date getAttributeValueAsDate(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.getAttribute(attributeName);
        if (a == null) {
            return null;
        }
        return a.getValueAsDate();
    }
    
    public DN getAttributeValueAsDN(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.getAttribute(attributeName);
        if (a == null) {
            return null;
        }
        return a.getValueAsDN();
    }
    
    public Integer getAttributeValueAsInteger(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.getAttribute(attributeName);
        if (a == null) {
            return null;
        }
        return a.getValueAsInteger();
    }
    
    public Long getAttributeValueAsLong(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.getAttribute(attributeName);
        if (a == null) {
            return null;
        }
        return a.getValueAsLong();
    }
    
    public String[] getAttributeValues(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        for (final CompactAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName)) {
                return a.getStringValues();
            }
        }
        return null;
    }
    
    public byte[][] getAttributeValueByteArrays(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        for (final CompactAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName)) {
                return a.getByteValues();
            }
        }
        return null;
    }
    
    public Attribute getObjectClassAttribute() {
        return this.getAttribute("objectClass");
    }
    
    public String[] getObjectClassValues() {
        return this.getAttributeValues("objectClass");
    }
    
    public Entry toEntry() {
        final Attribute[] attrs = new Attribute[this.attributes.length];
        for (int i = 0; i < this.attributes.length; ++i) {
            attrs[i] = this.attributes[i].toAttribute();
        }
        return new Entry(this.dn, attrs);
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.toEntry().hashCode();
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof CompactEntry && this.toEntry().equals(((CompactEntry)o).toEntry());
    }
    
    public String[] toLDIF() {
        return this.toLDIF(0);
    }
    
    public String[] toLDIF(final int wrapColumn) {
        return this.toEntry().toLDIF(wrapColumn);
    }
    
    public void toLDIF(final ByteStringBuffer buffer) {
        this.toLDIF(buffer, 0);
    }
    
    public void toLDIF(final ByteStringBuffer buffer, final int wrapColumn) {
        this.toEntry().toLDIF(buffer, wrapColumn);
    }
    
    public String toLDIFString() {
        final StringBuilder buffer = new StringBuilder();
        this.toLDIFString(buffer, 0);
        return buffer.toString();
    }
    
    public String toLDIFString(final int wrapColumn) {
        final StringBuilder buffer = new StringBuilder();
        this.toLDIFString(buffer, wrapColumn);
        return buffer.toString();
    }
    
    public void toLDIFString(final StringBuilder buffer) {
        this.toLDIFString(buffer, 0);
    }
    
    public void toLDIFString(final StringBuilder buffer, final int wrapColumn) {
        this.toEntry().toLDIFString(buffer, wrapColumn);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("Entry(dn='");
        buffer.append(this.dn);
        buffer.append("', attributes={");
        for (int i = 0; i < this.attributes.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            this.attributes[i].toAttribute().toString(buffer);
        }
        buffer.append("})");
    }
}
