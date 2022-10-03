package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import com.sun.jndi.toolkit.url.Uri;

public final class LdapURL extends Uri
{
    private boolean useSsl;
    private String DN;
    private String attributes;
    private String scope;
    private String filter;
    private String extensions;
    
    public LdapURL(final String s) throws NamingException {
        this.useSsl = false;
        this.DN = null;
        this.attributes = null;
        this.scope = null;
        this.filter = null;
        this.extensions = null;
        try {
            this.init(s);
            this.useSsl = this.scheme.equalsIgnoreCase("ldaps");
            if (!this.scheme.equalsIgnoreCase("ldap") && !this.useSsl) {
                throw new MalformedURLException("Not an LDAP URL: " + s);
            }
            this.parsePathAndQuery();
        }
        catch (final MalformedURLException rootCause) {
            final NamingException ex = new NamingException("Cannot parse url: " + s);
            ex.setRootCause(rootCause);
            throw ex;
        }
        catch (final UnsupportedEncodingException rootCause2) {
            final NamingException ex2 = new NamingException("Cannot parse url: " + s);
            ex2.setRootCause(rootCause2);
            throw ex2;
        }
    }
    
    public boolean useSsl() {
        return this.useSsl;
    }
    
    public String getDN() {
        return this.DN;
    }
    
    public String getAttributes() {
        return this.attributes;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public String getFilter() {
        return this.filter;
    }
    
    public String getExtensions() {
        return this.extensions;
    }
    
    public static String[] fromList(final String s) throws NamingException {
        final String[] array = new String[(s.length() + 1) / 2];
        int n = 0;
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
        while (stringTokenizer.hasMoreTokens()) {
            array[n++] = stringTokenizer.nextToken();
        }
        final String[] array2 = new String[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public static boolean hasQueryComponents(final String s) {
        return s.lastIndexOf(63) != -1;
    }
    
    static String toUrlString(final String s, final int n, final String s2, final boolean b) {
        try {
            String string = (s != null) ? s : "";
            if (string.indexOf(58) != -1 && string.charAt(0) != '[') {
                string = "[" + string + "]";
            }
            final String s3 = (n != -1) ? (":" + n) : "";
            final String s4 = (s2 != null) ? ("/" + UrlUtil.encode(s2, "UTF8")) : "";
            return b ? ("ldaps://" + string + s3 + s4) : ("ldap://" + string + s3 + s4);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 encoding unavailable");
        }
    }
    
    private void parsePathAndQuery() throws MalformedURLException, UnsupportedEncodingException {
        if (this.path.equals("")) {
            return;
        }
        this.DN = (this.path.startsWith("/") ? this.path.substring(1) : this.path);
        if (this.DN.length() > 0) {
            this.DN = UrlUtil.decode(this.DN, "UTF8");
        }
        if (this.query == null || this.query.length() < 2) {
            return;
        }
        final int n = 1;
        final int index = this.query.indexOf(63, n);
        final int n2 = (index == -1) ? this.query.length() : index;
        if (n2 - n > 0) {
            this.attributes = this.query.substring(n, n2);
        }
        final int n3 = n2 + 1;
        if (n3 >= this.query.length()) {
            return;
        }
        final int index2 = this.query.indexOf(63, n3);
        final int n4 = (index2 == -1) ? this.query.length() : index2;
        if (n4 - n3 > 0) {
            this.scope = this.query.substring(n3, n4);
        }
        final int n5 = n4 + 1;
        if (n5 >= this.query.length()) {
            return;
        }
        final int index3 = this.query.indexOf(63, n5);
        final int n6 = (index3 == -1) ? this.query.length() : index3;
        if (n6 - n5 > 0) {
            this.filter = this.query.substring(n5, n6);
            this.filter = UrlUtil.decode(this.filter, "UTF8");
        }
        final int n7 = n6 + 1;
        if (n7 >= this.query.length()) {
            return;
        }
        if (this.query.length() - n7 > 0) {
            this.extensions = this.query.substring(n7);
            this.extensions = UrlUtil.decode(this.extensions, "UTF8");
        }
    }
}
