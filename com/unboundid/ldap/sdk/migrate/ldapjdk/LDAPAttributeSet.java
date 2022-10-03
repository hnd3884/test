package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPAttributeSet implements Serializable
{
    private static final long serialVersionUID = -4872457565092606186L;
    private final ArrayList<LDAPAttribute> attributes;
    
    public LDAPAttributeSet() {
        this.attributes = new ArrayList<LDAPAttribute>(20);
    }
    
    public LDAPAttributeSet(final LDAPAttribute[] attrs) {
        this.attributes = new ArrayList<LDAPAttribute>(Arrays.asList(attrs));
    }
    
    private LDAPAttributeSet(final ArrayList<LDAPAttribute> attrs) {
        this.attributes = new ArrayList<LDAPAttribute>(attrs);
    }
    
    public Enumeration<LDAPAttribute> getAttributes() {
        return new IterableEnumeration<LDAPAttribute>(this.attributes);
    }
    
    public LDAPAttributeSet getSubset(final String subtype) {
        final ArrayList<LDAPAttribute> subset = new ArrayList<LDAPAttribute>(this.attributes.size());
        for (final LDAPAttribute a : this.attributes) {
            if (a.hasSubtype(subtype)) {
                subset.add(a);
            }
        }
        return new LDAPAttributeSet(subset);
    }
    
    public LDAPAttribute getAttribute(final String attrName) {
        for (final LDAPAttribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attrName)) {
                return a;
            }
        }
        return null;
    }
    
    public LDAPAttribute getAttribute(final String attrName, final String lang) {
        if (lang == null) {
            return this.getAttribute(attrName);
        }
        final String lowerLang = StaticUtils.toLowerCase(lang);
        for (final LDAPAttribute a : this.attributes) {
            if (a.getBaseName().equalsIgnoreCase(attrName)) {
                final String[] subtypes = a.getSubtypes();
                if (subtypes == null) {
                    continue;
                }
                for (final String s : subtypes) {
                    final String lowerOption = StaticUtils.toLowerCase(s);
                    if (lowerOption.equals(lowerLang) || lowerOption.startsWith(lang + '-')) {
                        return a;
                    }
                }
            }
        }
        return null;
    }
    
    public LDAPAttribute elementAt(final int index) throws IndexOutOfBoundsException {
        return this.attributes.get(index);
    }
    
    public void add(final LDAPAttribute attr) {
        for (final LDAPAttribute a : this.attributes) {
            if (attr.getName().equalsIgnoreCase(a.getName())) {
                for (final byte[] value : attr.getByteValueArray()) {
                    a.addValue(value);
                }
                return;
            }
        }
        this.attributes.add(attr);
    }
    
    public void remove(final String name) {
        final Iterator<LDAPAttribute> iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            final LDAPAttribute a = iterator.next();
            if (name.equalsIgnoreCase(a.getName())) {
                iterator.remove();
            }
        }
    }
    
    public void removeElementAt(final int index) throws IndexOutOfBoundsException {
        this.attributes.remove(index);
    }
    
    public int size() {
        return this.attributes.size();
    }
    
    public LDAPAttributeSet duplicate() {
        return new LDAPAttributeSet(this.attributes);
    }
    
    @Override
    public String toString() {
        return this.attributes.toString();
    }
}
