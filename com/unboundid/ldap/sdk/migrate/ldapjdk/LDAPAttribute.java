package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPAttribute implements Serializable
{
    private static final long serialVersionUID = 839217229050750570L;
    private Attribute attribute;
    
    public LDAPAttribute(final Attribute attr) {
        this.attribute = attr;
    }
    
    public LDAPAttribute(final LDAPAttribute attr) {
        this.attribute = attr.attribute;
    }
    
    public LDAPAttribute(final String attrName) {
        this.attribute = new Attribute(attrName);
    }
    
    public LDAPAttribute(final String attrName, final byte[] attrBytes) {
        this.attribute = new Attribute(attrName, attrBytes);
    }
    
    public LDAPAttribute(final String attrName, final String attrString) {
        this.attribute = new Attribute(attrName, attrString);
    }
    
    public LDAPAttribute(final String attrName, final String[] attrStrings) {
        this.attribute = new Attribute(attrName, attrStrings);
    }
    
    public String getName() {
        return this.attribute.getName();
    }
    
    public String getBaseName() {
        return this.attribute.getBaseName();
    }
    
    public static String getBaseName(final String attrName) {
        return Attribute.getBaseName(attrName);
    }
    
    public String[] getSubtypes() {
        final Set<String> optionSet = this.attribute.getOptions();
        if (optionSet.isEmpty()) {
            return null;
        }
        final String[] options = new String[optionSet.size()];
        return optionSet.toArray(options);
    }
    
    public static String[] getSubtypes(final String attrName) {
        return new LDAPAttribute(attrName).getSubtypes();
    }
    
    public String getLangSubtype() {
        for (final String s : this.attribute.getOptions()) {
            final String lowerName = StaticUtils.toLowerCase(s);
            if (lowerName.startsWith("lang-")) {
                return s;
            }
        }
        return null;
    }
    
    public boolean hasSubtype(final String subtype) {
        return this.attribute.hasOption(subtype);
    }
    
    public boolean hasSubtypes(final String[] subtypes) {
        for (final String s : subtypes) {
            if (!this.attribute.hasOption(s)) {
                return false;
            }
        }
        return true;
    }
    
    public Enumeration<String> getStringValues() {
        return new IterableEnumeration<String>(Arrays.asList(this.attribute.getValues()));
    }
    
    public String[] getStringValueArray() {
        return this.attribute.getValues();
    }
    
    public Enumeration<byte[]> getByteValues() {
        return new IterableEnumeration<byte[]>(Arrays.asList(this.attribute.getValueByteArrays()));
    }
    
    public byte[][] getByteValueArray() {
        return this.attribute.getValueByteArrays();
    }
    
    public void addValue(final String attrString) {
        this.attribute = Attribute.mergeAttributes(this.attribute, new Attribute(this.attribute.getName(), attrString));
    }
    
    public void addValue(final byte[] attrBytes) {
        this.attribute = Attribute.mergeAttributes(this.attribute, new Attribute(this.attribute.getName(), attrBytes));
    }
    
    public void removeValue(final String attrValue) {
        this.attribute = Attribute.removeValues(this.attribute, new Attribute(this.attribute.getName(), attrValue));
    }
    
    public void removeValue(final byte[] attrValue) {
        this.attribute = Attribute.removeValues(this.attribute, new Attribute(this.attribute.getName(), attrValue));
    }
    
    public int size() {
        return this.attribute.size();
    }
    
    public final Attribute toAttribute() {
        return this.attribute;
    }
    
    @Override
    public String toString() {
        return this.attribute.toString();
    }
}
