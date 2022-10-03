package org.bouncycastle.cms;

import java.util.Iterator;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import java.io.OutputStream;
import org.bouncycastle.util.io.TeeOutputStream;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.CMSAttributes;
import java.util.Collection;
import java.util.ArrayList;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class SignerInformation
{
    private final SignerId sid;
    private final CMSProcessable content;
    private final byte[] signature;
    private final ASN1ObjectIdentifier contentType;
    private final boolean isCounterSignature;
    private AttributeTable signedAttributeValues;
    private AttributeTable unsignedAttributeValues;
    private byte[] resultDigest;
    protected final SignerInfo info;
    protected final AlgorithmIdentifier digestAlgorithm;
    protected final AlgorithmIdentifier encryptionAlgorithm;
    protected final ASN1Set signedAttributeSet;
    protected final ASN1Set unsignedAttributeSet;
    
    SignerInformation(final SignerInfo info, final ASN1ObjectIdentifier contentType, final CMSProcessable content, final byte[] resultDigest) {
        this.info = info;
        this.contentType = contentType;
        this.isCounterSignature = (contentType == null);
        final SignerIdentifier sid = info.getSID();
        if (sid.isTagged()) {
            this.sid = new SignerId(ASN1OctetString.getInstance((Object)sid.getId()).getOctets());
        }
        else {
            final IssuerAndSerialNumber instance = IssuerAndSerialNumber.getInstance((Object)sid.getId());
            this.sid = new SignerId(instance.getName(), instance.getSerialNumber().getValue());
        }
        this.digestAlgorithm = info.getDigestAlgorithm();
        this.signedAttributeSet = info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = info.getDigestEncryptionAlgorithm();
        this.signature = info.getEncryptedDigest().getOctets();
        this.content = content;
        this.resultDigest = resultDigest;
    }
    
    protected SignerInformation(final SignerInformation signerInformation) {
        this.info = signerInformation.info;
        this.contentType = signerInformation.contentType;
        this.isCounterSignature = signerInformation.isCounterSignature();
        this.sid = signerInformation.getSID();
        this.digestAlgorithm = this.info.getDigestAlgorithm();
        this.signedAttributeSet = this.info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = this.info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = this.info.getDigestEncryptionAlgorithm();
        this.signature = this.info.getEncryptedDigest().getOctets();
        this.content = signerInformation.content;
        this.resultDigest = signerInformation.resultDigest;
    }
    
    public boolean isCounterSignature() {
        return this.isCounterSignature;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }
    
    private byte[] encodeObj(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            return asn1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }
    
    public SignerId getSID() {
        return this.sid;
    }
    
    public int getVersion() {
        return this.info.getVersion().getValue().intValue();
    }
    
    public AlgorithmIdentifier getDigestAlgorithmID() {
        return this.digestAlgorithm;
    }
    
    public String getDigestAlgOID() {
        return this.digestAlgorithm.getAlgorithm().getId();
    }
    
    public byte[] getDigestAlgParams() {
        try {
            return this.encodeObj(this.digestAlgorithm.getParameters());
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting digest parameters " + ex);
        }
    }
    
    public byte[] getContentDigest() {
        if (this.resultDigest == null) {
            throw new IllegalStateException("method can only be called after verify.");
        }
        return Arrays.clone(this.resultDigest);
    }
    
    public String getEncryptionAlgOID() {
        return this.encryptionAlgorithm.getAlgorithm().getId();
    }
    
    public byte[] getEncryptionAlgParams() {
        try {
            return this.encodeObj(this.encryptionAlgorithm.getParameters());
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting encryption parameters " + ex);
        }
    }
    
    public AttributeTable getSignedAttributes() {
        if (this.signedAttributeSet != null && this.signedAttributeValues == null) {
            this.signedAttributeValues = new AttributeTable(this.signedAttributeSet);
        }
        return this.signedAttributeValues;
    }
    
    public AttributeTable getUnsignedAttributes() {
        if (this.unsignedAttributeSet != null && this.unsignedAttributeValues == null) {
            this.unsignedAttributeValues = new AttributeTable(this.unsignedAttributeSet);
        }
        return this.unsignedAttributeValues;
    }
    
    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }
    
    public SignerInformationStore getCounterSignatures() {
        final AttributeTable unsignedAttributes = this.getUnsignedAttributes();
        if (unsignedAttributes == null) {
            return new SignerInformationStore(new ArrayList<SignerInformation>(0));
        }
        final ArrayList list = new ArrayList();
        final ASN1EncodableVector all = unsignedAttributes.getAll(CMSAttributes.counterSignature);
        for (int i = 0; i < all.size(); ++i) {
            final ASN1Set attrValues = ((Attribute)all.get(i)).getAttrValues();
            if (attrValues.size() < 1) {}
            final Enumeration objects = attrValues.getObjects();
            while (objects.hasMoreElements()) {
                list.add(new SignerInformation(SignerInfo.getInstance(objects.nextElement()), null, new CMSProcessableByteArray(this.getSignature()), null));
            }
        }
        return new SignerInformationStore(list);
    }
    
    public byte[] getEncodedSignedAttributes() throws IOException {
        if (this.signedAttributeSet != null) {
            return this.signedAttributeSet.getEncoded("DER");
        }
        return null;
    }
    
    private boolean doVerify(final SignerInformationVerifier signerInformationVerifier) throws CMSException {
        final String encryptionAlgName = CMSSignedHelper.INSTANCE.getEncryptionAlgName(this.getEncryptionAlgOID());
        ContentVerifier contentVerifier;
        try {
            contentVerifier = signerInformationVerifier.getContentVerifier(this.encryptionAlgorithm, this.info.getDigestAlgorithm());
        }
        catch (final OperatorCreationException ex) {
            throw new CMSException("can't create content verifier: " + ex.getMessage(), ex);
        }
        try {
            final OutputStream outputStream = contentVerifier.getOutputStream();
            if (this.resultDigest == null) {
                final DigestCalculator digestCalculator = signerInformationVerifier.getDigestCalculator(this.getDigestAlgorithmID());
                if (this.content != null) {
                    final OutputStream outputStream2 = digestCalculator.getOutputStream();
                    if (this.signedAttributeSet == null) {
                        if (contentVerifier instanceof RawContentVerifier) {
                            this.content.write(outputStream2);
                        }
                        else {
                            final TeeOutputStream teeOutputStream = new TeeOutputStream(outputStream2, outputStream);
                            this.content.write((OutputStream)teeOutputStream);
                            ((OutputStream)teeOutputStream).close();
                        }
                    }
                    else {
                        this.content.write(outputStream2);
                        outputStream.write(this.getEncodedSignedAttributes());
                    }
                    outputStream2.close();
                }
                else {
                    if (this.signedAttributeSet == null) {
                        throw new CMSException("data not encapsulated in signature - use detached constructor.");
                    }
                    outputStream.write(this.getEncodedSignedAttributes());
                }
                this.resultDigest = digestCalculator.getDigest();
            }
            else if (this.signedAttributeSet == null) {
                if (this.content != null) {
                    this.content.write(outputStream);
                }
            }
            else {
                outputStream.write(this.getEncodedSignedAttributes());
            }
            outputStream.close();
        }
        catch (final IOException ex2) {
            throw new CMSException("can't process mime object to create signature.", ex2);
        }
        catch (final OperatorCreationException ex3) {
            throw new CMSException("can't create digest calculator: " + ex3.getMessage(), ex3);
        }
        final ASN1Primitive singleValuedSignedAttribute = this.getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
        if (singleValuedSignedAttribute == null) {
            if (!this.isCounterSignature && this.signedAttributeSet != null) {
                throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data");
            }
        }
        else {
            if (this.isCounterSignature) {
                throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute");
            }
            if (!(singleValuedSignedAttribute instanceof ASN1ObjectIdentifier)) {
                throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'");
            }
            if (!((ASN1ObjectIdentifier)singleValuedSignedAttribute).equals((Object)this.contentType)) {
                throw new CMSException("content-type attribute value does not match eContentType");
            }
        }
        final AttributeTable signedAttributes = this.getSignedAttributes();
        final AttributeTable unsignedAttributes = this.getUnsignedAttributes();
        if (unsignedAttributes != null && unsignedAttributes.getAll(CMSAttributes.cmsAlgorithmProtect).size() > 0) {
            throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute");
        }
        if (signedAttributes != null) {
            final ASN1EncodableVector all = signedAttributes.getAll(CMSAttributes.cmsAlgorithmProtect);
            if (all.size() > 1) {
                throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }
            if (all.size() > 0) {
                final Attribute instance = Attribute.getInstance((Object)all.get(0));
                if (instance.getAttrValues().size() != 1) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                }
                final CMSAlgorithmProtection instance2 = CMSAlgorithmProtection.getInstance((Object)instance.getAttributeValues()[0]);
                if (!CMSUtils.isEquivalent(instance2.getDigestAlgorithm(), this.info.getDigestAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                }
                if (!CMSUtils.isEquivalent(instance2.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm");
                }
            }
        }
        final ASN1Primitive singleValuedSignedAttribute2 = this.getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
        if (singleValuedSignedAttribute2 == null) {
            if (this.signedAttributeSet != null) {
                throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present");
            }
        }
        else {
            if (!(singleValuedSignedAttribute2 instanceof ASN1OctetString)) {
                throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'");
            }
            if (!Arrays.constantTimeAreEqual(this.resultDigest, ((ASN1OctetString)singleValuedSignedAttribute2).getOctets())) {
                throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value");
            }
        }
        if (signedAttributes != null && signedAttributes.getAll(CMSAttributes.counterSignature).size() > 0) {
            throw new CMSException("A countersignature attribute MUST NOT be a signed attribute");
        }
        final AttributeTable unsignedAttributes2 = this.getUnsignedAttributes();
        if (unsignedAttributes2 != null) {
            final ASN1EncodableVector all2 = unsignedAttributes2.getAll(CMSAttributes.counterSignature);
            for (int i = 0; i < all2.size(); ++i) {
                if (Attribute.getInstance((Object)all2.get(i)).getAttrValues().size() < 1) {
                    throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue");
                }
            }
        }
        try {
            if (this.signedAttributeSet != null || this.resultDigest == null || !(contentVerifier instanceof RawContentVerifier)) {
                return contentVerifier.verify(this.getSignature());
            }
            final RawContentVerifier rawContentVerifier = (RawContentVerifier)contentVerifier;
            if (encryptionAlgName.equals("RSA")) {
                return rawContentVerifier.verify(new DigestInfo(new AlgorithmIdentifier(this.digestAlgorithm.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), this.resultDigest).getEncoded("DER"), this.getSignature());
            }
            return rawContentVerifier.verify(this.resultDigest, this.getSignature());
        }
        catch (final IOException ex4) {
            throw new CMSException("can't process mime object to create signature.", ex4);
        }
    }
    
    public boolean verify(final SignerInformationVerifier signerInformationVerifier) throws CMSException {
        final Time signingTime = this.getSigningTime();
        if (signerInformationVerifier.hasAssociatedCertificate() && signingTime != null && !signerInformationVerifier.getAssociatedCertificate().isValidOn(signingTime.getDate())) {
            throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime");
        }
        return this.doVerify(signerInformationVerifier);
    }
    
    public SignerInfo toASN1Structure() {
        return this.info;
    }
    
    private ASN1Primitive getSingleValuedSignedAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) throws CMSException {
        final AttributeTable unsignedAttributes = this.getUnsignedAttributes();
        if (unsignedAttributes != null && unsignedAttributes.getAll(asn1ObjectIdentifier).size() > 0) {
            throw new CMSException("The " + s + " attribute MUST NOT be an unsigned attribute");
        }
        final AttributeTable signedAttributes = this.getSignedAttributes();
        if (signedAttributes == null) {
            return null;
        }
        final ASN1EncodableVector all = signedAttributes.getAll(asn1ObjectIdentifier);
        switch (all.size()) {
            case 0: {
                return null;
            }
            case 1: {
                final ASN1Set attrValues = ((Attribute)all.get(0)).getAttrValues();
                if (attrValues.size() != 1) {
                    throw new CMSException("A " + s + " attribute MUST have a single attribute value");
                }
                return attrValues.getObjectAt(0).toASN1Primitive();
            }
            default: {
                throw new CMSException("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the " + s + " attribute");
            }
        }
    }
    
    private Time getSigningTime() throws CMSException {
        final ASN1Primitive singleValuedSignedAttribute = this.getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
        if (singleValuedSignedAttribute == null) {
            return null;
        }
        try {
            return Time.getInstance((Object)singleValuedSignedAttribute);
        }
        catch (final IllegalArgumentException ex) {
            throw new CMSException("signing-time attribute value not a valid 'Time' structure");
        }
    }
    
    public static SignerInformation replaceUnsignedAttributes(final SignerInformation signerInformation, final AttributeTable attributeTable) {
        final SignerInfo info = signerInformation.info;
        Object o = null;
        if (attributeTable != null) {
            o = new DERSet(attributeTable.toASN1EncodableVector());
        }
        return new SignerInformation(new SignerInfo(info.getSID(), info.getDigestAlgorithm(), info.getAuthenticatedAttributes(), info.getDigestEncryptionAlgorithm(), info.getEncryptedDigest(), (ASN1Set)o), signerInformation.contentType, signerInformation.content, null);
    }
    
    public static SignerInformation addCounterSigners(final SignerInformation signerInformation, final SignerInformationStore signerInformationStore) {
        final SignerInfo info = signerInformation.info;
        final AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        ASN1EncodableVector asn1EncodableVector;
        if (unsignedAttributes != null) {
            asn1EncodableVector = unsignedAttributes.toASN1EncodableVector();
        }
        else {
            asn1EncodableVector = new ASN1EncodableVector();
        }
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        final Iterator<SignerInformation> iterator = signerInformationStore.getSigners().iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector2.add((ASN1Encodable)iterator.next().toASN1Structure());
        }
        asn1EncodableVector.add((ASN1Encodable)new Attribute(CMSAttributes.counterSignature, (ASN1Set)new DERSet(asn1EncodableVector2)));
        return new SignerInformation(new SignerInfo(info.getSID(), info.getDigestAlgorithm(), info.getAuthenticatedAttributes(), info.getDigestEncryptionAlgorithm(), info.getEncryptedDigest(), (ASN1Set)new DERSet(asn1EncodableVector)), signerInformation.contentType, signerInformation.content, null);
    }
}
