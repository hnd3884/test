package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.operator.DigestCalculatorProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.AuthenticatedDataParser;

public class CMSAuthenticatedDataParser extends CMSContentInfoParser
{
    RecipientInformationStore recipientInfoStore;
    AuthenticatedDataParser authData;
    private AlgorithmIdentifier macAlg;
    private byte[] mac;
    private AttributeTable authAttrs;
    private ASN1Set authAttrSet;
    private AttributeTable unauthAttrs;
    private boolean authAttrNotRead;
    private boolean unauthAttrNotRead;
    private OriginatorInformation originatorInfo;
    
    public CMSAuthenticatedDataParser(final byte[] array) throws CMSException, IOException {
        this(new ByteArrayInputStream(array));
    }
    
    public CMSAuthenticatedDataParser(final byte[] array, final DigestCalculatorProvider digestCalculatorProvider) throws CMSException, IOException {
        this(new ByteArrayInputStream(array), digestCalculatorProvider);
    }
    
    public CMSAuthenticatedDataParser(final InputStream inputStream) throws CMSException, IOException {
        this(inputStream, null);
    }
    
    public CMSAuthenticatedDataParser(final InputStream inputStream, final DigestCalculatorProvider digestCalculatorProvider) throws CMSException, IOException {
        super(inputStream);
        this.authAttrNotRead = true;
        this.authData = new AuthenticatedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
        final OriginatorInfo originatorInfo = this.authData.getOriginatorInfo();
        if (originatorInfo != null) {
            this.originatorInfo = new OriginatorInformation(originatorInfo);
        }
        final ASN1Set instance = ASN1Set.getInstance((Object)this.authData.getRecipientInfos().toASN1Primitive());
        this.macAlg = this.authData.getMacAlgorithm();
        final AlgorithmIdentifier digestAlgorithm = this.authData.getDigestAlgorithm();
        if (digestAlgorithm != null) {
            if (digestCalculatorProvider == null) {
                throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
            }
            final CMSProcessableInputStream cmsProcessableInputStream = new CMSProcessableInputStream(((ASN1OctetStringParser)this.authData.getEncapsulatedContentInfo().getContent(4)).getOctetStream());
            try {
                this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(instance, this.macAlg, new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(digestCalculatorProvider.get(digestAlgorithm), cmsProcessableInputStream), new AuthAttributesProvider() {
                    public ASN1Set getAuthAttributes() {
                        try {
                            return CMSAuthenticatedDataParser.this.getAuthAttrSet();
                        }
                        catch (final IOException ex) {
                            throw new IllegalStateException("can't parse authenticated attributes!");
                        }
                    }
                });
            }
            catch (final OperatorCreationException ex) {
                throw new CMSException("unable to create digest calculator: " + ex.getMessage(), ex);
            }
        }
        else {
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(instance, this.macAlg, new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, new CMSProcessableInputStream(((ASN1OctetStringParser)this.authData.getEncapsulatedContentInfo().getContent(4)).getOctetStream())));
        }
    }
    
    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlg;
    }
    
    public String getMacAlgOID() {
        return this.macAlg.getAlgorithm().toString();
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
    
    public byte[] getMac() throws IOException {
        if (this.mac == null) {
            this.getAuthAttrs();
            this.mac = this.authData.getMac().getOctets();
        }
        return Arrays.clone(this.mac);
    }
    
    private ASN1Set getAuthAttrSet() throws IOException {
        if (this.authAttrs == null && this.authAttrNotRead) {
            final ASN1SetParser authAttrs = this.authData.getAuthAttrs();
            if (authAttrs != null) {
                this.authAttrSet = (ASN1Set)authAttrs.toASN1Primitive();
            }
            this.authAttrNotRead = false;
        }
        return this.authAttrSet;
    }
    
    public AttributeTable getAuthAttrs() throws IOException {
        if (this.authAttrs == null && this.authAttrNotRead) {
            final ASN1Set authAttrSet = this.getAuthAttrSet();
            if (authAttrSet != null) {
                this.authAttrs = new AttributeTable(authAttrSet);
            }
        }
        return this.authAttrs;
    }
    
    public AttributeTable getUnauthAttrs() throws IOException {
        if (this.unauthAttrs == null && this.unauthAttrNotRead) {
            final ASN1SetParser unauthAttrs = this.authData.getUnauthAttrs();
            this.unauthAttrNotRead = false;
            if (unauthAttrs != null) {
                final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                ASN1Encodable object;
                while ((object = unauthAttrs.readObject()) != null) {
                    asn1EncodableVector.add((ASN1Encodable)((ASN1SequenceParser)object).toASN1Primitive());
                }
                this.unauthAttrs = new AttributeTable((ASN1Set)new DERSet(asn1EncodableVector));
            }
        }
        return this.unauthAttrs;
    }
    
    private byte[] encodeObj(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            return asn1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }
    
    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance((Object)this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}
