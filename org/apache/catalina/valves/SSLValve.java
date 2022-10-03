package org.apache.catalina.valves;

import org.apache.juli.logging.LogFactory;
import javax.servlet.ServletException;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import org.apache.tomcat.util.buf.UDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;

public class SSLValve extends ValveBase
{
    private static final Log log;
    private String sslClientCertHeader;
    private String sslClientEscapedCertHeader;
    private String sslCipherHeader;
    private String sslSessionIdHeader;
    private String sslCipherUserKeySizeHeader;
    
    public SSLValve() {
        super(true);
        this.sslClientCertHeader = "ssl_client_cert";
        this.sslClientEscapedCertHeader = "ssl_client_escaped_cert";
        this.sslCipherHeader = "ssl_cipher";
        this.sslSessionIdHeader = "ssl_session_id";
        this.sslCipherUserKeySizeHeader = "ssl_cipher_usekeysize";
    }
    
    public String getSslClientCertHeader() {
        return this.sslClientCertHeader;
    }
    
    public void setSslClientCertHeader(final String sslClientCertHeader) {
        this.sslClientCertHeader = sslClientCertHeader;
    }
    
    public String getSslClientEscapedCertHeader() {
        return this.sslClientEscapedCertHeader;
    }
    
    public void setSslClientEscapedCertHeader(final String sslClientEscapedCertHeader) {
        this.sslClientEscapedCertHeader = sslClientEscapedCertHeader;
    }
    
    public String getSslCipherHeader() {
        return this.sslCipherHeader;
    }
    
    public void setSslCipherHeader(final String sslCipherHeader) {
        this.sslCipherHeader = sslCipherHeader;
    }
    
    public String getSslSessionIdHeader() {
        return this.sslSessionIdHeader;
    }
    
    public void setSslSessionIdHeader(final String sslSessionIdHeader) {
        this.sslSessionIdHeader = sslSessionIdHeader;
    }
    
    public String getSslCipherUserKeySizeHeader() {
        return this.sslCipherUserKeySizeHeader;
    }
    
    public void setSslCipherUserKeySizeHeader(final String sslCipherUserKeySizeHeader) {
        this.sslCipherUserKeySizeHeader = sslCipherUserKeySizeHeader;
    }
    
    public String mygetHeader(final Request request, final String header) {
        final String strcert0 = request.getHeader(header);
        if (strcert0 == null) {
            return null;
        }
        if ("(null)".equals(strcert0)) {
            return null;
        }
        return strcert0;
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        final String headerEscapedValue = this.mygetHeader(request, this.sslClientEscapedCertHeader);
        String headerValue;
        if (headerEscapedValue != null) {
            headerValue = UDecoder.URLDecode(headerEscapedValue, StandardCharsets.ISO_8859_1);
        }
        else {
            headerValue = this.mygetHeader(request, this.sslClientCertHeader);
        }
        if (headerValue != null) {
            headerValue = headerValue.trim();
            if (headerValue.length() > 27) {
                String body = headerValue.substring(27, headerValue.length() - 25);
                body = body.replace(' ', '\n');
                body = body.replace('\t', '\n');
                final String header = "-----BEGIN CERTIFICATE-----\n";
                final String footer = "\n-----END CERTIFICATE-----\n";
                final String strcerts = header.concat(body).concat(footer);
                final ByteArrayInputStream bais = new ByteArrayInputStream(strcerts.getBytes(StandardCharsets.ISO_8859_1));
                X509Certificate[] jsseCerts = null;
                final String providerName = (String)request.getConnector().getProperty("clientCertProvider");
                try {
                    CertificateFactory cf;
                    if (providerName == null) {
                        cf = CertificateFactory.getInstance("X.509");
                    }
                    else {
                        cf = CertificateFactory.getInstance("X.509", providerName);
                    }
                    final X509Certificate cert = (X509Certificate)cf.generateCertificate(bais);
                    jsseCerts = new X509Certificate[] { cert };
                }
                catch (final CertificateException e) {
                    SSLValve.log.warn((Object)SSLValve.sm.getString("sslValve.certError", new Object[] { strcerts }), (Throwable)e);
                }
                catch (final NoSuchProviderException e2) {
                    SSLValve.log.error((Object)SSLValve.sm.getString("sslValve.invalidProvider", new Object[] { providerName }), (Throwable)e2);
                }
                request.setAttribute("javax.servlet.request.X509Certificate", jsseCerts);
            }
        }
        headerValue = this.mygetHeader(request, this.sslCipherHeader);
        if (headerValue != null) {
            request.setAttribute("javax.servlet.request.cipher_suite", headerValue);
        }
        headerValue = this.mygetHeader(request, this.sslSessionIdHeader);
        if (headerValue != null) {
            request.setAttribute("javax.servlet.request.ssl_session_id", headerValue);
        }
        headerValue = this.mygetHeader(request, this.sslCipherUserKeySizeHeader);
        if (headerValue != null) {
            request.setAttribute("javax.servlet.request.key_size", Integer.valueOf(headerValue));
        }
        this.getNext().invoke(request, response);
    }
    
    static {
        log = LogFactory.getLog((Class)SSLValve.class);
    }
}
