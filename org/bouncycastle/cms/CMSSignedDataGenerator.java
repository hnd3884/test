package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.BEROctetString;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.ArrayList;
import java.util.List;

public class CMSSignedDataGenerator extends CMSSignedGenerator
{
    private List signerInfs;
    
    public CMSSignedDataGenerator() {
        this.signerInfs = new ArrayList();
    }
    
    public CMSSignedData generate(final CMSTypedData cmsTypedData) throws CMSException {
        return this.generate(cmsTypedData, false);
    }
    
    public CMSSignedData generate(final CMSTypedData cmsTypedData, final boolean b) throws CMSException {
        if (!this.signerInfs.isEmpty()) {
            throw new IllegalStateException("this method can only be used with SignerInfoGenerator");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        this.digests.clear();
        for (final SignerInformation signerInformation : this._signers) {
            asn1EncodableVector.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(signerInformation.getDigestAlgorithmID()));
            asn1EncodableVector2.add((ASN1Encodable)signerInformation.toASN1Structure());
        }
        final ASN1ObjectIdentifier contentType = cmsTypedData.getContentType();
        Object o = null;
        if (cmsTypedData.getContent() != null) {
            ByteArrayOutputStream byteArrayOutputStream = null;
            if (b) {
                byteArrayOutputStream = new ByteArrayOutputStream();
            }
            final OutputStream safeOutputStream = CMSUtils.getSafeOutputStream(CMSUtils.attachSignersToOutputStream(this.signerGens, byteArrayOutputStream));
            try {
                cmsTypedData.write(safeOutputStream);
                safeOutputStream.close();
            }
            catch (final IOException ex) {
                throw new CMSException("data processing exception: " + ex.getMessage(), ex);
            }
            if (b) {
                o = new BEROctetString(byteArrayOutputStream.toByteArray());
            }
        }
        for (final SignerInfoGenerator signerInfoGenerator : this.signerGens) {
            final SignerInfo generate = signerInfoGenerator.generate(contentType);
            asn1EncodableVector.add((ASN1Encodable)generate.getDigestAlgorithm());
            asn1EncodableVector2.add((ASN1Encodable)generate);
            final byte[] calculatedDigest = signerInfoGenerator.getCalculatedDigest();
            if (calculatedDigest != null) {
                this.digests.put(generate.getDigestAlgorithm().getAlgorithm().getId(), calculatedDigest);
            }
        }
        ASN1Set berSetFromList = null;
        if (this.certs.size() != 0) {
            berSetFromList = CMSUtils.createBerSetFromList(this.certs);
        }
        ASN1Set berSetFromList2 = null;
        if (this.crls.size() != 0) {
            berSetFromList2 = CMSUtils.createBerSetFromList(this.crls);
        }
        return new CMSSignedData(cmsTypedData, new ContentInfo(CMSObjectIdentifiers.signedData, (ASN1Encodable)new SignedData((ASN1Set)new DERSet(asn1EncodableVector), new ContentInfo(contentType, (ASN1Encodable)o), berSetFromList, berSetFromList2, (ASN1Set)new DERSet(asn1EncodableVector2))));
    }
    
    public SignerInformationStore generateCounterSigners(final SignerInformation signerInformation) throws CMSException {
        return this.generate(new CMSProcessableByteArray(null, signerInformation.getSignature()), false).getSignerInfos();
    }
}
