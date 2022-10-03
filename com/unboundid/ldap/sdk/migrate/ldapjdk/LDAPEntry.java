package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.util.Enumeration;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPEntry implements Serializable
{
    private static final long serialVersionUID = -6285850560316222689L;
    private final String dn;
    private final LDAPAttributeSet attributeSet;
    
    public LDAPEntry() {
        this("", new LDAPAttributeSet());
    }
    
    public LDAPEntry(final String distinguishedName) {
        this(distinguishedName, new LDAPAttributeSet());
    }
    
    public LDAPEntry(final String distinguishedName, final LDAPAttributeSet attrs) {
        this.dn = distinguishedName;
        if (attrs == null) {
            this.attributeSet = new LDAPAttributeSet();
        }
        else {
            this.attributeSet = attrs;
        }
    }
    
    public LDAPEntry(final Entry entry) {
        this.dn = entry.getDN();
        this.attributeSet = new LDAPAttributeSet();
        for (final Attribute a : entry.getAttributes()) {
            this.attributeSet.add(new LDAPAttribute(a));
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public LDAPAttributeSet getAttributeSet() {
        return this.attributeSet;
    }
    
    public LDAPAttributeSet getAttributeSet(final String subtype) {
        return this.attributeSet.getSubset(subtype);
    }
    
    public LDAPAttribute getAttribute(final String attrName) {
        return this.attributeSet.getAttribute(attrName);
    }
    
    public LDAPAttribute getAttribute(final String attrName, final String lang) {
        return this.attributeSet.getAttribute(attrName, lang);
    }
    
    public final Entry toEntry() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(this.attributeSet.size());
        final Enumeration<LDAPAttribute> attrEnum = this.attributeSet.getAttributes();
        while (attrEnum.hasMoreElements()) {
            attrs.add(attrEnum.nextElement().toAttribute());
        }
        return new Entry(this.dn, attrs);
    }
    
    @Override
    public String toString() {
        return this.toEntry().toString();
    }
}
