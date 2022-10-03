package sun.security.provider.certpath;

import java.util.Map;
import java.security.cert.CRLReason;
import sun.security.x509.GeneralName;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.URIName;
import sun.security.x509.AccessDescription;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.security.cert.CertPath;
import sun.security.x509.PKIXExtensions;
import java.security.cert.CertificateException;
import sun.security.x509.X509CertImpl;
import java.security.cert.TrustAnchor;
import java.security.cert.Extension;
import java.util.List;
import java.security.cert.CertPathValidatorException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;

public final class OCSP
{
    private static final Debug debug;
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    private static final int CONNECT_TIMEOUT;
    
    private static int initializeTimeout() {
        final Integer n = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("com.sun.security.ocsp.timeout"));
        if (n == null || n < 0) {
            return 15000;
        }
        return n * 1000;
    }
    
    private OCSP() {
    }
    
    public static RevocationStatus check(final X509Certificate x509Certificate, final X509Certificate x509Certificate2, final URI uri, final X509Certificate x509Certificate3, final Date date) throws IOException, CertPathValidatorException {
        return check(x509Certificate, x509Certificate2, uri, x509Certificate3, date, Collections.emptyList(), "generic");
    }
    
    public static RevocationStatus check(final X509Certificate x509Certificate, final X509Certificate x509Certificate2, final URI uri, final X509Certificate x509Certificate3, final Date date, final List<Extension> list, final String s) throws IOException, CertPathValidatorException {
        return check(x509Certificate, uri, null, x509Certificate2, x509Certificate3, date, list, s);
    }
    
    public static RevocationStatus check(final X509Certificate x509Certificate, final URI uri, final TrustAnchor trustAnchor, final X509Certificate x509Certificate2, final X509Certificate x509Certificate3, final Date date, final List<Extension> list, final String s) throws IOException, CertPathValidatorException {
        CertId certId;
        try {
            certId = new CertId(x509Certificate2, X509CertImpl.toImpl(x509Certificate).getSerialNumberObject());
        }
        catch (final CertificateException | IOException ex) {
            throw new CertPathValidatorException("Exception while encoding OCSPRequest", (Throwable)ex);
        }
        return check(Collections.singletonList(certId), uri, new OCSPResponse.IssuerInfo(trustAnchor, x509Certificate2), x509Certificate3, date, list, s).getSingleResponse(certId);
    }
    
    static OCSPResponse check(final List<CertId> list, final URI uri, final OCSPResponse.IssuerInfo issuerInfo, final X509Certificate x509Certificate, final Date date, final List<Extension> list2, final String s) throws IOException, CertPathValidatorException {
        byte[] value = null;
        for (final Extension extension : list2) {
            if (extension.getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
                value = extension.getValue();
            }
        }
        OCSPResponse ocspResponse;
        try {
            ocspResponse = new OCSPResponse(getOCSPBytes(list, uri, list2));
            ocspResponse.verify(list, issuerInfo, x509Certificate, date, value, s);
        }
        catch (final IOException ex) {
            throw new CertPathValidatorException("Unable to determine revocation status due to network error", ex, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
        }
        return ocspResponse;
    }
    
    public static byte[] getOCSPBytes(final List<CertId> list, final URI uri, final List<Extension> list2) throws IOException {
        final byte[] encodeBytes = new OCSPRequest(list, list2).encodeBytes();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte[] array = null;
        try {
            final URL url = uri.toURL();
            if (OCSP.debug != null) {
                OCSP.debug.println("connecting to OCSP service at: " + url);
            }
            final HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(OCSP.CONNECT_TIMEOUT);
            httpURLConnection.setReadTimeout(OCSP.CONNECT_TIMEOUT);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-type", "application/ocsp-request");
            httpURLConnection.setRequestProperty("Content-length", String.valueOf(encodeBytes.length));
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(encodeBytes);
            outputStream.flush();
            if (OCSP.debug != null && httpURLConnection.getResponseCode() != 200) {
                OCSP.debug.println("Received HTTP error: " + httpURLConnection.getResponseCode() + " - " + httpURLConnection.getResponseMessage());
            }
            inputStream = httpURLConnection.getInputStream();
            int contentLength = httpURLConnection.getContentLength();
            if (contentLength == -1) {
                contentLength = Integer.MAX_VALUE;
            }
            array = new byte[(contentLength > 2048) ? 2048 : contentLength];
            int i = 0;
            while (i < contentLength) {
                final int read = inputStream.read(array, i, array.length - i);
                if (read < 0) {
                    break;
                }
                i += read;
                if (i < array.length || i >= contentLength) {
                    continue;
                }
                array = Arrays.copyOf(array, i * 2);
            }
            array = Arrays.copyOf(array, i);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex) {
                    throw ex;
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (final IOException ex2) {
                    throw ex2;
                }
            }
        }
        return array;
    }
    
    public static URI getResponderURI(final X509Certificate x509Certificate) {
        try {
            return getResponderURI(X509CertImpl.toImpl(x509Certificate));
        }
        catch (final CertificateException ex) {
            return null;
        }
    }
    
    static URI getResponderURI(final X509CertImpl x509CertImpl) {
        final AuthorityInfoAccessExtension authorityInfoAccessExtension = x509CertImpl.getAuthorityInfoAccessExtension();
        if (authorityInfoAccessExtension == null) {
            return null;
        }
        for (final AccessDescription accessDescription : authorityInfoAccessExtension.getAccessDescriptions()) {
            if (accessDescription.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id)) {
                final GeneralName accessLocation = accessDescription.getAccessLocation();
                if (accessLocation.getType() == 6) {
                    return ((URIName)accessLocation.getName()).getURI();
                }
                continue;
            }
        }
        return null;
    }
    
    static {
        debug = Debug.getInstance("certpath");
        CONNECT_TIMEOUT = initializeTimeout();
    }
    
    public interface RevocationStatus
    {
        CertStatus getCertStatus();
        
        Date getRevocationTime();
        
        CRLReason getRevocationReason();
        
        Map<String, Extension> getSingleExtensions();
        
        public enum CertStatus
        {
            GOOD, 
            REVOKED, 
            UNKNOWN;
        }
    }
}
