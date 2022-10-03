package org.apache.commons.httpclient.params;

import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpVersion;

public class DefaultHttpParamsFactory implements HttpParamsFactory
{
    private HttpParams httpParams;
    
    public synchronized HttpParams getDefaultParams() {
        if (this.httpParams == null) {
            this.httpParams = this.createParams();
        }
        return this.httpParams;
    }
    
    protected HttpParams createParams() {
        final HttpClientParams params = new HttpClientParams(null);
        params.setParameter("http.useragent", "Jakarta Commons-HttpClient/3.1");
        params.setVersion(HttpVersion.HTTP_1_1);
        params.setConnectionManagerClass(SimpleHttpConnectionManager.class);
        params.setCookiePolicy("default");
        params.setHttpElementCharset("US-ASCII");
        params.setContentCharset("ISO-8859-1");
        params.setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler());
        final ArrayList datePatterns = new ArrayList();
        datePatterns.addAll(Arrays.asList("EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z"));
        params.setParameter("http.dateparser.patterns", datePatterns);
        String agent = null;
        try {
            agent = System.getProperty("httpclient.useragent");
        }
        catch (final SecurityException ex) {}
        if (agent != null) {
            params.setParameter("http.useragent", agent);
        }
        String preemptiveDefault = null;
        try {
            preemptiveDefault = System.getProperty("httpclient.authentication.preemptive");
        }
        catch (final SecurityException ex2) {}
        if (preemptiveDefault != null) {
            preemptiveDefault = preemptiveDefault.trim().toLowerCase();
            if (preemptiveDefault.equals("true")) {
                params.setParameter("http.authentication.preemptive", Boolean.TRUE);
            }
            else if (preemptiveDefault.equals("false")) {
                params.setParameter("http.authentication.preemptive", Boolean.FALSE);
            }
        }
        String defaultCookiePolicy = null;
        try {
            defaultCookiePolicy = System.getProperty("apache.commons.httpclient.cookiespec");
        }
        catch (final SecurityException ex3) {}
        if (defaultCookiePolicy != null) {
            if ("COMPATIBILITY".equalsIgnoreCase(defaultCookiePolicy)) {
                params.setCookiePolicy("compatibility");
            }
            else if ("NETSCAPE_DRAFT".equalsIgnoreCase(defaultCookiePolicy)) {
                params.setCookiePolicy("netscape");
            }
            else if ("RFC2109".equalsIgnoreCase(defaultCookiePolicy)) {
                params.setCookiePolicy("rfc2109");
            }
        }
        return params;
    }
}
