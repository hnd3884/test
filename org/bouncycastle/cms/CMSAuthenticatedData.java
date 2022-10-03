package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import java.io.InputStream;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.util.Encodable;

public class CMSAuthenticatedData implements Encodable
{
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier macAlg;
    private ASN1Set authAttrs;
    private ASN1Set unauthAttrs;
    private byte[] mac;
    private OriginatorInformation originatorInfo;
    
    public CMSAuthenticatedData(final byte[] array) throws CMSException {
        this(CMSUtils.readContentInfo(array));
    }
    
    public CMSAuthenticatedData(final byte[] array, final DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this(CMSUtils.readContentInfo(array), digestCalculatorProvider);
    }
    
    public CMSAuthenticatedData(final InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }
    
    public CMSAuthenticatedData(final InputStream inputStream, final DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream), digestCalculatorProvider);
    }
    
    public CMSAuthenticatedData(final ContentInfo contentInfo) throws CMSException {
        this(contentInfo, null);
    }
    
    public CMSAuthenticatedData(final ContentInfo contentInfo, final DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this.contentInfo = contentInfo;
        final AuthenticatedData instance = AuthenticatedData.getInstance((Object)contentInfo.getContent());
        if (instance.getOriginatorInfo() != null) {
            this.originatorInfo = new OriginatorInformation(instance.getOriginatorInfo());
        }
        final ASN1Set recipientInfos = instance.getRecipientInfos();
        this.macAlg = instance.getMacAlgorithm();
        this.authAttrs = instance.getAuthAttrs();
        this.mac = instance.getMac().getOctets();
        this.unauthAttrs = instance.getUnauthAttrs();
        final CMSProcessableByteArray cmsProcessableByteArray = new CMSProcessableByteArray(ASN1OctetString.getInstance((Object)instance.getEncapsulatedContentInfo().getContent()).getOctets());
        if (this.authAttrs != null) {
            if (digestCalculatorProvider == null) {
                throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
            }
            final ASN1EncodableVector all = new AttributeTable(this.authAttrs).getAll(CMSAttributes.cmsAlgorithmProtect);
            if (all.size() > 1) {
                throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }
            if (all.size() > 0) {
                final Attribute instance2 = Attribute.getInstance((Object)all.get(0));
                if (instance2.getAttrValues().size() != 1) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                }
                final CMSAlgorithmProtection instance3 = CMSAlgorithmProtection.getInstance((Object)instance2.getAttributeValues()[0]);
                if (!CMSUtils.isEquivalent(instance3.getDigestAlgorithm(), instance.getDigestAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                }
                if (!CMSUtils.isEquivalent(instance3.getMacAlgorithm(), this.macAlg)) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for macAlgorithm");
                }
            }
            try {
                this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(digestCalculatorProvider.get(instance.getDigestAlgorithm()), cmsProcessableByteArray), new AuthAttributesProvider() {
                    public ASN1Set getAuthAttributes() {
                        return CMSAuthenticatedData.this.authAttrs;
                    }
                });
            }
            catch (final OperatorCreationException ex) {
                throw new CMSException("unable to create digest calculator: " + ex.getMessage(), ex);
            }
        }
        else {
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, cmsProcessableByteArray));
        }
    }
    
    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }
    
    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }
    
    private byte[] encodeObj(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            return asn1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlg;
    }
    
    public String getMacAlgOID() {
        return this.macAlg.getAlgorithm().getId();
    }
    
    public byte[] getMacAlgParams() {
        try {
            return this.encodeObj(this.macAlg.getParameters());
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting encryption parameters " + ex);
        }
    }
    
    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }
    
    @Deprecated
    public ContentInfo getContentInfo() {
        return this.contentInfo;
    }
    
    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
    
    public AttributeTable getAuthAttrs() {
        if (this.authAttrs == null) {
            return null;
        }
        return new AttributeTable(this.authAttrs);
    }
    
    public AttributeTable getUnauthAttrs() {
        if (this.unauthAttrs == null) {
            return null;
        }
        return new AttributeTable(this.unauthAttrs);
    }
    
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
    
    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance((Object)this.getAuthAttrs().get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}
