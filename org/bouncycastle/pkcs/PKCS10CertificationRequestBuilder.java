package org.bouncycastle.pkcs;

import java.io.OutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import java.util.Iterator;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class PKCS10CertificationRequestBuilder
{
    private SubjectPublicKeyInfo publicKeyInfo;
    private X500Name subject;
    private List attributes;
    private boolean leaveOffEmpty;
    
    public PKCS10CertificationRequestBuilder(final PKCS10CertificationRequestBuilder pkcs10CertificationRequestBuilder) {
        this.attributes = new ArrayList();
        this.leaveOffEmpty = false;
        this.publicKeyInfo = pkcs10CertificationRequestBuilder.publicKeyInfo;
        this.subject = pkcs10CertificationRequestBuilder.subject;
        this.leaveOffEmpty = pkcs10CertificationRequestBuilder.leaveOffEmpty;
        this.attributes = new ArrayList(pkcs10CertificationRequestBuilder.attributes);
    }
    
    public PKCS10CertificationRequestBuilder(final X500Name subject, final SubjectPublicKeyInfo publicKeyInfo) {
        this.attributes = new ArrayList();
        this.leaveOffEmpty = false;
        this.subject = subject;
        this.publicKeyInfo = publicKeyInfo;
    }
    
    public PKCS10CertificationRequestBuilder setAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        final Iterator iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            if (((Attribute)iterator.next()).getAttrType().equals((Object)asn1ObjectIdentifier)) {
                throw new IllegalStateException("Attribute " + asn1ObjectIdentifier.toString() + " is already set");
            }
        }
        this.addAttribute(asn1ObjectIdentifier, asn1Encodable);
        return this;
    }
    
    public PKCS10CertificationRequestBuilder setAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable[] array) {
        final Iterator iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            if (((Attribute)iterator.next()).getAttrType().equals((Object)asn1ObjectIdentifier)) {
                throw new IllegalStateException("Attribute " + asn1ObjectIdentifier.toString() + " is already set");
            }
        }
        this.addAttribute(asn1ObjectIdentifier, array);
        return this;
    }
    
    public PKCS10CertificationRequestBuilder addAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.attributes.add(new Attribute(asn1ObjectIdentifier, (ASN1Set)new DERSet(asn1Encodable)));
        return this;
    }
    
    public PKCS10CertificationRequestBuilder addAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable[] array) {
        this.attributes.add(new Attribute(asn1ObjectIdentifier, (ASN1Set)new DERSet(array)));
        return this;
    }
    
    public PKCS10CertificationRequestBuilder setLeaveOffEmptyAttributes(final boolean leaveOffEmpty) {
        this.leaveOffEmpty = leaveOffEmpty;
        return this;
    }
    
    public PKCS10CertificationRequest build(final ContentSigner contentSigner) {
        CertificationRequestInfo certificationRequestInfo;
        if (this.attributes.isEmpty()) {
            if (this.leaveOffEmpty) {
                certificationRequestInfo = new CertificationRequestInfo(this.subject, this.publicKeyInfo, (ASN1Set)null);
            }
            else {
                certificationRequestInfo = new CertificationRequestInfo(this.subject, this.publicKeyInfo, (ASN1Set)new DERSet());
            }
        }
        else {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            final Iterator iterator = this.attributes.iterator();
            while (iterator.hasNext()) {
                asn1EncodableVector.add((ASN1Encodable)Attribute.getInstance(iterator.next()));
            }
            certificationRequestInfo = new CertificationRequestInfo(this.subject, this.publicKeyInfo, (ASN1Set)new DERSet(asn1EncodableVector));
        }
        try {
            final OutputStream outputStream = contentSigner.getOutputStream();
            outputStream.write(certificationRequestInfo.getEncoded("DER"));
            outputStream.close();
            return new PKCS10CertificationRequest(new CertificationRequest(certificationRequestInfo, contentSigner.getAlgorithmIdentifier(), new DERBitString(contentSigner.getSignature())));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("cannot produce certification request signature");
        }
    }
}
