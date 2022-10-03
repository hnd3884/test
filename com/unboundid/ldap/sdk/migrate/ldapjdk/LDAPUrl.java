package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import java.net.MalformedURLException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPUrl implements Serializable
{
    private static final long serialVersionUID = -1716384037873600695L;
    private final LDAPURL ldapURL;
    
    public LDAPUrl(final String url) throws MalformedURLException {
        try {
            this.ldapURL = new LDAPURL(url);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new MalformedURLException(le.getMessage());
        }
    }
    
    public LDAPUrl(final String host, final int port, final String dn) throws RuntimeException {
        try {
            final DN dnObject = (dn == null) ? null : new DN(dn);
            this.ldapURL = new LDAPURL("ldap", host, port, dnObject, null, null, null);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new RuntimeException(e);
        }
    }
    
    public LDAPUrl(final String host, final int port, final String dn, final String[] attributes, final int scope, final String filter) throws RuntimeException {
        try {
            final DN dnObject = (dn == null) ? null : new DN(dn);
            final SearchScope scopeObject = SearchScope.valueOf(scope);
            final Filter filterObject = Filter.create(filter);
            this.ldapURL = new LDAPURL("ldap", host, port, dnObject, attributes, scopeObject, filterObject);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new RuntimeException(e);
        }
    }
    
    public LDAPUrl(final String host, final int port, final String dn, final Enumeration<String> attributes, final int scope, final String filter) throws RuntimeException {
        try {
            final DN dnObject = (dn == null) ? null : new DN(dn);
            final SearchScope scopeObject = SearchScope.valueOf(scope);
            final Filter filterObject = Filter.create(filter);
            String[] attrs;
            if (attributes == null) {
                attrs = null;
            }
            else {
                final ArrayList<String> attrList = new ArrayList<String>(10);
                while (attributes.hasMoreElements()) {
                    attrList.add(attributes.nextElement());
                }
                attrs = new String[attrList.size()];
                attrList.toArray(attrs);
            }
            this.ldapURL = new LDAPURL("ldap", host, port, dnObject, attrs, scopeObject, filterObject);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new RuntimeException(e);
        }
    }
    
    public LDAPUrl(final LDAPURL ldapURL) {
        this.ldapURL = ldapURL;
    }
    
    public String getHost() {
        return this.ldapURL.getHost();
    }
    
    public int getPort() {
        return this.ldapURL.getPort();
    }
    
    public String getDN() {
        if (this.ldapURL.baseDNProvided()) {
            return this.ldapURL.getBaseDN().toString();
        }
        return null;
    }
    
    public Enumeration<String> getAttributes() {
        final String[] attributes = this.ldapURL.getAttributes();
        if (attributes.length == 0) {
            return null;
        }
        return new IterableEnumeration<String>(Arrays.asList(attributes));
    }
    
    public String[] getAttributeArray() {
        final String[] attributes = this.ldapURL.getAttributes();
        if (attributes.length == 0) {
            return null;
        }
        return attributes;
    }
    
    public int getScope() {
        return this.ldapURL.getScope().intValue();
    }
    
    public String getFilter() {
        return this.ldapURL.getFilter().toString();
    }
    
    @Override
    public int hashCode() {
        return this.ldapURL.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof LDAPUrl && this.ldapURL.equals(((LDAPUrl)o).ldapURL);
    }
    
    public String getUrl() {
        return this.ldapURL.toString();
    }
    
    public final LDAPURL toLDAPURL() {
        return this.ldapURL;
    }
    
    @Override
    public String toString() {
        return this.ldapURL.toString();
    }
}
