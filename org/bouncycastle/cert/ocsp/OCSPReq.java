package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.x509.Certificate;
import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.cert.CertIOException;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.cert.X509CertificateHolder;

public class OCSPReq
{
    private static final X509CertificateHolder[] EMPTY_CERTS;
    private OCSPRequest req;
    private Extensions extensions;
    
    public OCSPReq(final OCSPRequest req) {
        this.req = req;
        this.extensions = req.getTbsRequest().getRequestExtensions();
    }
    
    public OCSPReq(final byte[] array) throws IOException {
        this(new ASN1InputStream(array));
    }
    
    private OCSPReq(final ASN1InputStream asn1InputStream) throws IOException {
        try {
            this.req = OCSPRequest.getInstance((Object)asn1InputStream.readObject());
            if (this.req == null) {
                throw new CertIOException("malformed request: no request data found");
            }
            this.extensions = this.req.getTbsRequest().getRequestExtensions();
        }
        catch (final IllegalArgumentException ex) {
            throw new CertIOException("malformed request: " + ex.getMessage(), ex);
        }
        catch (final ClassCastException ex2) {
            throw new CertIOException("malformed request: " + ex2.getMessage(), ex2);
        }
        catch (final ASN1Exception ex3) {
            throw new CertIOException("malformed request: " + ex3.getMessage(), (Throwable)ex3);
        }
    }
    
    public int getVersionNumber() {
        return this.req.getTbsRequest().getVersion().getValue().intValue() + 1;
    }
    
    public GeneralName getRequestorName() {
        return GeneralName.getInstance((Object)this.req.getTbsRequest().getRequestorName());
    }
    
    public Req[] getRequestList() {
        final ASN1Sequence requestList = this.req.getTbsRequest().getRequestList();
        final Req[] array = new Req[requestList.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = new Req(Request.getInstance((Object)requestList.getObjectAt(i)));
        }
        return array;
    }
    
    public boolean hasExtensions() {
        return this.extensions != null;
    }
    
    public Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public List getExtensionOIDs() {
        return OCSPUtils.getExtensionOIDs(this.extensions);
    }
    
    public Set getCriticalExtensionOIDs() {
        return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
    }
    
    public ASN1ObjectIdentifier getSignatureAlgOID() {
        if (!this.isSigned()) {
            return null;
        }
        return this.req.getOptionalSignature().getSignatureAlgorithm().getAlgorithm();
    }
    
    public byte[] getSignature() {
        if (!this.isSigned()) {
            return null;
        }
        return this.req.getOptionalSignature().getSignature().getOctets();
    }
    
    public X509CertificateHolder[] getCerts() {
        if (this.req.getOptionalSignature() == null) {
            return OCSPReq.EMPTY_CERTS;
        }
        final ASN1Sequence certs = this.req.getOptionalSignature().getCerts();
        if (certs != null) {
            final X509CertificateHolder[] array = new X509CertificateHolder[certs.size()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = new X509CertificateHolder(Certificate.getInstance((Object)certs.getObjectAt(i)));
            }
            return array;
        }
        return OCSPReq.EMPTY_CERTS;
    }
    
    public boolean isSigned() {
        return this.req.getOptionalSignature() != null;
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws OCSPException {
        if (!this.isSigned()) {
            throw new OCSPException("attempt to verify signature on unsigned object");
        }
        try {
            final ContentVerifier value = contentVerifierProvider.get(this.req.getOptionalSignature().getSignatureAlgorithm());
            value.getOutputStream().write(this.req.getTbsRequest().getEncoded("DER"));
            return value.verify(this.getSignature());
        }
        catch (final Exception ex) {
            throw new OCSPException("exception processing signature: " + ex, ex);
        }
    }
    
    public byte[] getEncoded() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ASN1OutputStream((OutputStream)byteArrayOutputStream).writeObject((ASN1Encodable)this.req);
        return byteArrayOutputStream.toByteArray();
    }
    
    static {
        EMPTY_CERTS = new X509CertificateHolder[0];
    }
}
