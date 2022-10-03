package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import com.sun.jndi.toolkit.url.Uri;

public class DnsUrl extends Uri
{
    private String domain;
    
    public static DnsUrl[] fromList(final String s) throws MalformedURLException {
        final DnsUrl[] array = new DnsUrl[(s.length() + 1) / 2];
        int n = 0;
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
        while (stringTokenizer.hasMoreTokens()) {
            array[n++] = new DnsUrl(stringTokenizer.nextToken());
        }
        final DnsUrl[] array2 = new DnsUrl[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public DnsUrl(final String s) throws MalformedURLException {
        super(s);
        if (!this.scheme.equals("dns")) {
            throw new MalformedURLException(s + " is not a valid DNS pseudo-URL");
        }
        this.domain = (this.path.startsWith("/") ? this.path.substring(1) : this.path);
        this.domain = (this.domain.equals("") ? "." : UrlUtil.decode(this.domain));
    }
    
    public String getDomain() {
        return this.domain;
    }
}
