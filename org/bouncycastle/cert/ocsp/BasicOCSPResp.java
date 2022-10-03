package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import java.util.Date;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.util.Encodable;

public class BasicOCSPResp implements Encodable
{
    private BasicOCSPResponse resp;
    private ResponseData data;
    private Extensions extensions;
    
    public BasicOCSPResp(final BasicOCSPResponse resp) {
        this.resp = resp;
        this.data = resp.getTbsResponseData();
        this.extensions = Extensions.getInstance((Object)resp.getTbsResponseData().getResponseExtensions());
    }
    
    public byte[] getTBSResponseData() {
        try {
            return this.resp.getTbsResponseData().getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public AlgorithmIdentifier getSignatureAlgorithmID() {
        return this.resp.getSignatureAlgorithm();
    }
    
    public int getVersion() {
        return this.data.getVersion().getValue().intValue() + 1;
    }
    
    public RespID getResponderId() {
        return new RespID(this.data.getResponderID());
    }
    
    public Date getProducedAt() {
        return OCSPUtils.extractDate(this.data.getProducedAt());
    }
    
    public SingleResp[] getResponses() {
        final ASN1Sequence responses = this.data.getResponses();
        final SingleResp[] array = new SingleResp[responses.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = new SingleResp(SingleResponse.getInstance((Object)responses.getObjectAt(i)));
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
        return this.resp.getSignatureAlgorithm().getAlgorithm();
    }
    
    public byte[] getSignature() {
        return this.resp.getSignature().getOctets();
    }
    
    public X509CertificateHolder[] getCerts() {
        if (this.resp.getCerts() == null) {
            return OCSPUtils.EMPTY_CERTS;
        }
        final ASN1Sequence certs = this.resp.getCerts();
        if (certs != null) {
            final X509CertificateHolder[] array = new X509CertificateHolder[certs.size()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = new X509CertificateHolder(Certificate.getInstance((Object)certs.getObjectAt(i)));
            }
            return array;
        }
        return OCSPUtils.EMPTY_CERTS;
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws OCSPException {
        try {
            final ContentVerifier value = contentVerifierProvider.get(this.resp.getSignatureAlgorithm());
            final OutputStream outputStream = value.getOutputStream();
            outputStream.write(this.resp.getTbsResponseData().getEncoded("DER"));
            outputStream.close();
            return value.verify(this.getSignature());
        }
        catch (final Exception ex) {
            throw new OCSPException("exception processing sig: " + ex, ex);
        }
    }
    
    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof BasicOCSPResp && this.resp.equals((Object)((BasicOCSPResp)o).resp));
    }
    
    @Override
    public int hashCode() {
        return this.resp.hashCode();
    }
}
