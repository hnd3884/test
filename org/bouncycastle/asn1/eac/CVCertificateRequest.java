package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1ParsingException;
import java.util.Enumeration;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Object;

public class CVCertificateRequest extends ASN1Object
{
    private final ASN1ApplicationSpecific original;
    private CertificateBody certificateBody;
    private byte[] innerSignature;
    private byte[] outerSignature;
    private static final int bodyValid = 1;
    private static final int signValid = 2;
    
    private CVCertificateRequest(final ASN1ApplicationSpecific original) throws IOException {
        this.innerSignature = null;
        this.outerSignature = null;
        this.original = original;
        if (original.isConstructed() && original.getApplicationTag() == 7) {
            final ASN1Sequence instance = ASN1Sequence.getInstance(original.getObject(16));
            this.initCertBody(ASN1ApplicationSpecific.getInstance(instance.getObjectAt(0)));
            this.outerSignature = ASN1ApplicationSpecific.getInstance(instance.getObjectAt(instance.size() - 1)).getContents();
        }
        else {
            this.initCertBody(original);
        }
    }
    
    private void initCertBody(final ASN1ApplicationSpecific asn1ApplicationSpecific) throws IOException {
        if (asn1ApplicationSpecific.getApplicationTag() != 33) {
            throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + asn1ApplicationSpecific.getApplicationTag());
        }
        int n = 0;
        final Enumeration objects = ASN1Sequence.getInstance(asn1ApplicationSpecific.getObject(16)).getObjects();
        while (objects.hasMoreElements()) {
            final ASN1ApplicationSpecific instance = ASN1ApplicationSpecific.getInstance(objects.nextElement());
            switch (instance.getApplicationTag()) {
                case 78: {
                    this.certificateBody = CertificateBody.getInstance(instance);
                    n |= 0x1;
                    continue;
                }
                case 55: {
                    this.innerSignature = instance.getContents();
                    n |= 0x2;
                    continue;
                }
                default: {
                    throw new IOException("Invalid tag, not an CV Certificate Request element:" + instance.getApplicationTag());
                }
            }
        }
        if ((n & 0x3) == 0x0) {
            throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + asn1ApplicationSpecific.getApplicationTag());
        }
    }
    
    public static CVCertificateRequest getInstance(final Object o) {
        if (o instanceof CVCertificateRequest) {
            return (CVCertificateRequest)o;
        }
        if (o != null) {
            try {
                return new CVCertificateRequest(ASN1ApplicationSpecific.getInstance(o));
            }
            catch (final IOException ex) {
                throw new ASN1ParsingException("unable to parse data: " + ex.getMessage(), ex);
            }
        }
        return null;
    }
    
    public CertificateBody getCertificateBody() {
        return this.certificateBody;
    }
    
    public PublicKeyDataObject getPublicKey() {
        return this.certificateBody.getPublicKey();
    }
    
    public byte[] getInnerSignature() {
        return Arrays.clone(this.innerSignature);
    }
    
    public byte[] getOuterSignature() {
        return Arrays.clone(this.outerSignature);
    }
    
    public boolean hasOuterSignature() {
        return this.outerSignature != null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.original != null) {
            return this.original;
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certificateBody);
        try {
            asn1EncodableVector.add(new DERApplicationSpecific(false, 55, new DEROctetString(this.innerSignature)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("unable to convert signature!");
        }
        return new DERApplicationSpecific(33, asn1EncodableVector);
    }
}
