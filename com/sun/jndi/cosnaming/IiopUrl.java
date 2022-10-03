package com.sun.jndi.cosnaming;

import java.util.StringTokenizer;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import javax.naming.NamingException;
import javax.naming.Name;
import java.util.Vector;

public final class IiopUrl
{
    private static final int DEFAULT_IIOPNAME_PORT = 9999;
    private static final int DEFAULT_IIOP_PORT = 900;
    private static final String DEFAULT_HOST = "localhost";
    private Vector<Address> addresses;
    private String stringName;
    
    public Vector<Address> getAddresses() {
        return this.addresses;
    }
    
    public String getStringName() {
        return this.stringName;
    }
    
    public Name getCosName() throws NamingException {
        return CNCtx.parser.parse(this.stringName);
    }
    
    public IiopUrl(final String s) throws MalformedURLException {
        boolean b;
        int n;
        if (s.startsWith("iiopname://")) {
            b = false;
            n = 11;
        }
        else {
            if (!s.startsWith("iiop://")) {
                throw new MalformedURLException("Invalid iiop/iiopname URL: " + s);
            }
            b = true;
            n = 7;
        }
        int n2 = s.indexOf(47, n);
        if (n2 < 0) {
            n2 = s.length();
            this.stringName = "";
        }
        else {
            this.stringName = UrlUtil.decode(s.substring(n2 + 1));
        }
        this.addresses = new Vector<Address>(3);
        if (b) {
            this.addresses.addElement(new Address(s.substring(n, n2), b));
        }
        else {
            final StringTokenizer stringTokenizer = new StringTokenizer(s.substring(n, n2), ",");
            while (stringTokenizer.hasMoreTokens()) {
                this.addresses.addElement(new Address(stringTokenizer.nextToken(), b));
            }
            if (this.addresses.size() == 0) {
                this.addresses.addElement(new Address("", b));
            }
        }
    }
    
    public static class Address
    {
        public int port;
        public int major;
        public int minor;
        public String host;
        
        public Address(final String s, final boolean b) throws MalformedURLException {
            this.port = -1;
            final int index;
            int n;
            if (b || (index = s.indexOf(64)) < 0) {
                this.major = 1;
                this.minor = 0;
                n = 0;
            }
            else {
                final int index2 = s.indexOf(46);
                if (index2 < 0) {
                    throw new MalformedURLException("invalid version: " + s);
                }
                try {
                    this.major = Integer.parseInt(s.substring(0, index2));
                    this.minor = Integer.parseInt(s.substring(index2 + 1, index));
                }
                catch (final NumberFormatException ex) {
                    throw new MalformedURLException("Nonnumeric version: " + s);
                }
                n = index + 1;
            }
            int n2 = s.indexOf(47, n);
            if (n2 < 0) {
                n2 = s.length();
            }
            int n3;
            if (s.startsWith("[", n)) {
                final int index3 = s.indexOf(93, n + 1);
                if (index3 < 0 || index3 > n2) {
                    throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + s);
                }
                this.host = s.substring(n, index3 + 1);
                n3 = index3 + 1;
            }
            else {
                final int index4 = s.indexOf(58, n);
                final int n4 = (index4 < 0 || index4 > n2) ? n2 : index4;
                if (n < n4) {
                    this.host = s.substring(n, n4);
                }
                n3 = n4;
            }
            if (n3 + 1 < n2) {
                if (!s.startsWith(":", n3)) {
                    throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + s);
                }
                ++n3;
                this.port = Integer.parseInt(s.substring(n3, n2));
            }
            if ("".equals(this.host) || this.host == null) {
                this.host = "localhost";
            }
            if (this.port == -1) {
                this.port = (b ? 900 : 9999);
            }
        }
    }
}
