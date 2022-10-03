package org.bouncycastle.pkcs;

import java.io.OutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.operator.ContentVerifierProvider;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.Attribute;

public class PKCS10CertificationRequest
{
    private static Attribute[] EMPTY_ARRAY;
    private CertificationRequest certificationRequest;
    
    private static CertificationRequest parseBytes(final byte[] array) throws IOException {
        try {
            return CertificationRequest.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final ClassCastException ex) {
            throw new PKCSIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new PKCSIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public PKCS10CertificationRequest(final CertificationRequest certificationRequest) {
        this.certificationRequest = certificationRequest;
    }
    
    public PKCS10CertificationRequest(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public CertificationRequest toASN1Structure() {
        return this.certificationRequest;
    }
    
    public X500Name getSubject() {
        return X500Name.getInstance((Object)this.certificationRequest.getCertificationRequestInfo().getSubject());
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.certificationRequest.getSignatureAlgorithm();
    }
    
    public byte[] getSignature() {
        return this.certificationRequest.getSignature().getOctets();
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();
    }
    
    public Attribute[] getAttributes() {
        final ASN1Set attributes = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attributes == null) {
            return PKCS10CertificationRequest.EMPTY_ARRAY;
        }
        final Attribute[] array = new Attribute[attributes.size()];
        for (int i = 0; i != attributes.size(); ++i) {
            array[i] = Attribute.getInstance((Object)attributes.getObjectAt(i));
        }
        return array;
    }
    
    public Attribute[] getAttributes(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final ASN1Set attributes = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attributes == null) {
            return PKCS10CertificationRequest.EMPTY_ARRAY;
        }
        final ArrayList list = new ArrayList();
        for (int i = 0; i != attributes.size(); ++i) {
            final Attribute instance = Attribute.getInstance((Object)attributes.getObjectAt(i));
            if (instance.getAttrType().equals((Object)asn1ObjectIdentifier)) {
                list.add(instance);
            }
        }
        if (list.size() == 0) {
            return PKCS10CertificationRequest.EMPTY_ARRAY;
        }
        return (Attribute[])list.toArray(new Attribute[list.size()]);
    }
    
    public byte[] getEncoded() throws IOException {
        return this.certificationRequest.getEncoded();
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        final CertificationRequestInfo certificationRequestInfo = this.certificationRequest.getCertificationRequestInfo();
        ContentVerifier value;
        try {
            value = contentVerifierProvider.get(this.certificationRequest.getSignatureAlgorithm());
            final OutputStream outputStream = value.getOutputStream();
            outputStream.write(certificationRequestInfo.getEncoded("DER"));
            outputStream.close();
        }
        catch (final Exception ex) {
            throw new PKCSException("unable to process signature: " + ex.getMessage(), ex);
        }
        return value.verify(this.getSignature());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof PKCS10CertificationRequest && this.toASN1Structure().equals((Object)((PKCS10CertificationRequest)o).toASN1Structure()));
    }
    
    @Override
    public int hashCode() {
        return this.toASN1Structure().hashCode();
    }
    
    static {
        PKCS10CertificationRequest.EMPTY_ARRAY = new Attribute[0];
    }
}
