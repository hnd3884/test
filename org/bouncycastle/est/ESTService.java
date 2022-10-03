package org.bouncycastle.est;

import java.util.HashSet;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.cmc.CMCException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import java.io.IOException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.cert.X509CRLHolder;
import java.io.InputStream;
import org.bouncycastle.cmc.SimplePKIResponse;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import java.net.URL;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;
import java.util.regex.Pattern;
import java.util.Set;

public class ESTService
{
    protected static final String CACERTS = "/cacerts";
    protected static final String SIMPLE_ENROLL = "/simpleenroll";
    protected static final String SIMPLE_REENROLL = "/simplereenroll";
    protected static final String FULLCMC = "/fullcmc";
    protected static final String SERVERGEN = "/serverkeygen";
    protected static final String CSRATTRS = "/csrattrs";
    protected static final Set<String> illegalParts;
    private final String server;
    private final ESTClientProvider clientProvider;
    private static final Pattern pathInvalid;
    
    ESTService(String verifyServer, String verifyLabel, final ESTClientProvider clientProvider) {
        verifyServer = this.verifyServer(verifyServer);
        if (verifyLabel != null) {
            verifyLabel = this.verifyLabel(verifyLabel);
            this.server = "https://" + verifyServer + "/.well-known/est/" + verifyLabel;
        }
        else {
            this.server = "https://" + verifyServer + "/.well-known/est";
        }
        this.clientProvider = clientProvider;
    }
    
    public static X509CertificateHolder[] storeToArray(final Store<X509CertificateHolder> store) {
        return storeToArray(store, null);
    }
    
    public static X509CertificateHolder[] storeToArray(final Store<X509CertificateHolder> store, final Selector<X509CertificateHolder> selector) {
        final Collection matches = store.getMatches((Selector)selector);
        return matches.toArray(new X509CertificateHolder[matches.size()]);
    }
    
    public CACertsResponse getCACerts() throws Exception {
        ESTResponse doRequest = null;
        Throwable t = null;
        CACertsResponse caCertsResponse = null;
        URL url = null;
        try {
            url = new URL(this.server + "/cacerts");
            final ESTClient client = this.clientProvider.makeClient();
            final ESTRequest build = new ESTRequestBuilder("GET", url).withClient(client).build();
            doRequest = client.doRequest(build);
            Store<X509CertificateHolder> certificates = null;
            Store<X509CRLHolder> crLs = null;
            Label_0389: {
                if (doRequest.getStatusCode() == 200) {
                    if (!"application/pkcs7-mime".equals(doRequest.getHeaders().getFirstValue("Content-Type"))) {
                        throw new ESTException("Response : " + url.toString() + "Expecting application/pkcs7-mime " + ((doRequest.getHeaders().getFirstValue("Content-Type") != null) ? (" got " + doRequest.getHeaders().getFirstValue("Content-Type")) : " but was not present."), null, doRequest.getStatusCode(), doRequest.getInputStream());
                    }
                    try {
                        if (doRequest.getContentLength() != null && doRequest.getContentLength() > 0L) {
                            final SimplePKIResponse simplePKIResponse = new SimplePKIResponse(ContentInfo.getInstance((Object)new ASN1InputStream(doRequest.getInputStream()).readObject()));
                            certificates = simplePKIResponse.getCertificates();
                            crLs = simplePKIResponse.getCRLs();
                        }
                        break Label_0389;
                    }
                    catch (final Throwable t2) {
                        throw new ESTException("Decoding CACerts: " + url.toString() + " " + t2.getMessage(), t2, doRequest.getStatusCode(), doRequest.getInputStream());
                    }
                }
                if (doRequest.getStatusCode() != 204) {
                    throw new ESTException("Get CACerts: " + url.toString(), null, doRequest.getStatusCode(), doRequest.getInputStream());
                }
            }
            caCertsResponse = new CACertsResponse(certificates, crLs, build, doRequest.getSource(), this.clientProvider.isTrusted());
        }
        catch (final Throwable t3) {
            if (t3 instanceof ESTException) {
                throw (ESTException)t3;
            }
            throw new ESTException(t3.getMessage(), t3);
        }
        finally {
            if (doRequest != null) {
                try {
                    doRequest.close();
                }
                catch (final Exception ex) {
                    t = ex;
                }
            }
        }
        if (t == null) {
            return caCertsResponse;
        }
        if (t instanceof ESTException) {
            throw t;
        }
        throw new ESTException("Get CACerts: " + url.toString(), t, doRequest.getStatusCode(), null);
    }
    
    public EnrollmentResponse simpleEnroll(final EnrollmentResponse enrollmentResponse) throws Exception {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse doRequest = null;
        try {
            final ESTClient client = this.clientProvider.makeClient();
            doRequest = client.doRequest(new ESTRequestBuilder(enrollmentResponse.getRequestToRetry()).withClient(client).build());
            return this.handleEnrollResponse(doRequest);
        }
        catch (final Throwable t) {
            if (t instanceof ESTException) {
                throw (ESTException)t;
            }
            throw new ESTException(t.getMessage(), t);
        }
        finally {
            if (doRequest != null) {
                doRequest.close();
            }
        }
    }
    
    public EnrollmentResponse simpleEnroll(final boolean b, final PKCS10CertificationRequest pkcs10CertificationRequest, final ESTAuth estAuth) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse doRequest = null;
        try {
            final byte[] bytes = this.annotateRequest(pkcs10CertificationRequest.getEncoded()).getBytes();
            final URL url = new URL(this.server + (b ? "/simplereenroll" : "/simpleenroll"));
            final ESTClient client = this.clientProvider.makeClient();
            final ESTRequestBuilder withClient = new ESTRequestBuilder("POST", url).withData(bytes).withClient(client);
            withClient.addHeader("Content-Type", "application/pkcs10");
            withClient.addHeader("Content-Length", "" + bytes.length);
            withClient.addHeader("Content-Transfer-Encoding", "base64");
            if (estAuth != null) {
                estAuth.applyAuth(withClient);
            }
            doRequest = client.doRequest(withClient.build());
            return this.handleEnrollResponse(doRequest);
        }
        catch (final Throwable t) {
            if (t instanceof ESTException) {
                throw (ESTException)t;
            }
            throw new ESTException(t.getMessage(), t);
        }
        finally {
            if (doRequest != null) {
                doRequest.close();
            }
        }
    }
    
    public EnrollmentResponse simpleEnrollPoP(final boolean b, final PKCS10CertificationRequestBuilder pkcs10CertificationRequestBuilder, final ContentSigner contentSigner, final ESTAuth estAuth) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse doRequest = null;
        try {
            final URL url = new URL(this.server + (b ? "/simplereenroll" : "/simpleenroll"));
            final ESTClient client = this.clientProvider.makeClient();
            final ESTRequestBuilder withConnectionListener = new ESTRequestBuilder("POST", url).withClient(client).withConnectionListener(new ESTSourceConnectionListener() {
                public ESTRequest onConnection(final Source source, final ESTRequest estRequest) throws IOException {
                    if (source instanceof TLSUniqueProvider && ((TLSUniqueProvider)source).isTLSUniqueAvailable()) {
                        final PKCS10CertificationRequestBuilder pkcs10CertificationRequestBuilder = new PKCS10CertificationRequestBuilder(pkcs10CertificationRequestBuilder);
                        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pkcs10CertificationRequestBuilder.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, (ASN1Encodable)new DERPrintableString(Base64.toBase64String(((TLSUniqueProvider)source).getTLSUnique())));
                        byteArrayOutputStream.write(ESTService.this.annotateRequest(pkcs10CertificationRequestBuilder.build(contentSigner).getEncoded()).getBytes());
                        byteArrayOutputStream.flush();
                        final ESTRequestBuilder withData = new ESTRequestBuilder(estRequest).withData(byteArrayOutputStream.toByteArray());
                        withData.setHeader("Content-Type", "application/pkcs10");
                        withData.setHeader("Content-Transfer-Encoding", "base64");
                        withData.setHeader("Content-Length", Long.toString(byteArrayOutputStream.size()));
                        return withData.build();
                    }
                    throw new IOException("Source does not supply TLS unique.");
                }
            });
            if (estAuth != null) {
                estAuth.applyAuth(withConnectionListener);
            }
            doRequest = client.doRequest(withConnectionListener.build());
            return this.handleEnrollResponse(doRequest);
        }
        catch (final Throwable t) {
            if (t instanceof ESTException) {
                throw (ESTException)t;
            }
            throw new ESTException(t.getMessage(), t);
        }
        finally {
            if (doRequest != null) {
                doRequest.close();
            }
        }
    }
    
    protected EnrollmentResponse handleEnrollResponse(final ESTResponse estResponse) throws IOException {
        final ESTRequest originalRequest = estResponse.getOriginalRequest();
        if (estResponse.getStatusCode() == 202) {
            final String header = estResponse.getHeader("Retry-After");
            if (header == null) {
                throw new ESTException("Got Status 202 but not Retry-After header from: " + originalRequest.getURL().toString());
            }
            long time;
            try {
                time = System.currentTimeMillis() + Long.parseLong(header) * 1000L;
            }
            catch (final NumberFormatException ex) {
                try {
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    time = simpleDateFormat.parse(header).getTime();
                }
                catch (final Exception ex2) {
                    throw new ESTException("Unable to parse Retry-After header:" + originalRequest.getURL().toString() + " " + ex2.getMessage(), null, estResponse.getStatusCode(), estResponse.getInputStream());
                }
            }
            return new EnrollmentResponse(null, time, originalRequest, estResponse.getSource());
        }
        else {
            if (estResponse.getStatusCode() == 200) {
                final ASN1InputStream asn1InputStream = new ASN1InputStream(estResponse.getInputStream());
                SimplePKIResponse simplePKIResponse;
                try {
                    simplePKIResponse = new SimplePKIResponse(ContentInfo.getInstance((Object)asn1InputStream.readObject()));
                }
                catch (final CMCException ex3) {
                    throw new ESTException(ex3.getMessage(), ex3.getCause());
                }
                return new EnrollmentResponse(simplePKIResponse.getCertificates(), -1L, null, estResponse.getSource());
            }
            throw new ESTException("Simple Enroll: " + originalRequest.getURL().toString(), null, estResponse.getStatusCode(), estResponse.getInputStream());
        }
    }
    
    public CSRRequestResponse getCSRAttributes() throws ESTException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse doRequest = null;
        CSRAttributesResponse csrAttributesResponse = null;
        Throwable t = null;
        try {
            final URL url = new URL(this.server + "/csrattrs");
            final ESTClient client = this.clientProvider.makeClient();
            final ESTRequest build = new ESTRequestBuilder("GET", url).withClient(client).build();
            doRequest = client.doRequest(build);
            switch (doRequest.getStatusCode()) {
                case 200: {
                    try {
                        if (doRequest.getContentLength() != null && doRequest.getContentLength() > 0L) {
                            csrAttributesResponse = new CSRAttributesResponse(CsrAttrs.getInstance((Object)new ASN1InputStream(doRequest.getInputStream()).readObject()));
                        }
                        break;
                    }
                    catch (final Throwable t2) {
                        throw new ESTException("Decoding CACerts: " + url.toString() + " " + t2.getMessage(), t2, doRequest.getStatusCode(), doRequest.getInputStream());
                    }
                }
                case 204: {
                    csrAttributesResponse = null;
                    break;
                }
                case 404: {
                    csrAttributesResponse = null;
                    break;
                }
                default: {
                    throw new ESTException("CSR Attribute request: " + build.getURL().toString(), null, doRequest.getStatusCode(), doRequest.getInputStream());
                }
            }
        }
        catch (final Throwable t3) {
            if (t3 instanceof ESTException) {
                throw (ESTException)t3;
            }
            throw new ESTException(t3.getMessage(), t3);
        }
        finally {
            if (doRequest != null) {
                try {
                    doRequest.close();
                }
                catch (final Exception ex) {
                    t = ex;
                }
            }
        }
        if (t == null) {
            return new CSRRequestResponse(csrAttributesResponse, doRequest.getSource());
        }
        if (t instanceof ESTException) {
            throw (ESTException)t;
        }
        throw new ESTException(t.getMessage(), t, doRequest.getStatusCode(), null);
    }
    
    private String annotateRequest(final byte[] array) {
        int i = 0;
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        do {
            if (i + 48 < array.length) {
                printWriter.print(Base64.toBase64String(array, i, 48));
                i += 48;
            }
            else {
                printWriter.print(Base64.toBase64String(array, i, array.length - i));
                i = array.length;
            }
            printWriter.print('\n');
        } while (i < array.length);
        printWriter.flush();
        return stringWriter.toString();
    }
    
    private String verifyLabel(String s) {
        while (s.endsWith("/") && s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        while (s.startsWith("/") && s.length() > 0) {
            s = s.substring(1);
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
        }
        if (!ESTService.pathInvalid.matcher(s).matches()) {
            throw new IllegalArgumentException("Server path " + s + " contains invalid characters");
        }
        if (ESTService.illegalParts.contains(s)) {
            throw new IllegalArgumentException("Label " + s + " is a reserved path segment.");
        }
        return s;
    }
    
    private String verifyServer(String substring) {
        try {
            while (substring.endsWith("/") && substring.length() > 0) {
                substring = substring.substring(0, substring.length() - 1);
            }
            if (substring.contains("://")) {
                throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
            }
            final URL url = new URL("https://" + substring);
            if (url.getPath().length() == 0 || url.getPath().equals("/")) {
                return substring;
            }
            throw new IllegalArgumentException("Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily.");
        }
        catch (final Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)ex;
            }
            throw new IllegalArgumentException("Scheme and host is invalid: " + ex.getMessage(), ex);
        }
    }
    
    static {
        (illegalParts = new HashSet<String>()).add("/cacerts".substring(1));
        ESTService.illegalParts.add("/simpleenroll".substring(1));
        ESTService.illegalParts.add("/simplereenroll".substring(1));
        ESTService.illegalParts.add("/fullcmc".substring(1));
        ESTService.illegalParts.add("/serverkeygen".substring(1));
        ESTService.illegalParts.add("/csrattrs".substring(1));
        pathInvalid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;=]+");
    }
}
