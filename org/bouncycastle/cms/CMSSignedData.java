package org.bouncycastle.cms;

import java.util.List;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Iterator;
import org.bouncycastle.operator.OperatorCreationException;
import java.util.Enumeration;
import java.util.Collections;
import java.util.HashSet;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Set;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;
import org.bouncycastle.asn1.ASN1Set;
import java.util.Collection;
import org.bouncycastle.asn1.cms.SignerInfo;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import java.util.Map;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.util.Encodable;

public class CMSSignedData implements Encodable
{
    private static final CMSSignedHelper HELPER;
    SignedData signedData;
    ContentInfo contentInfo;
    CMSTypedData signedContent;
    SignerInformationStore signerInfoStore;
    private Map hashes;
    
    private CMSSignedData(final CMSSignedData cmsSignedData) {
        this.signedData = cmsSignedData.signedData;
        this.contentInfo = cmsSignedData.contentInfo;
        this.signedContent = cmsSignedData.signedContent;
        this.signerInfoStore = cmsSignedData.signerInfoStore;
    }
    
    public CMSSignedData(final byte[] array) throws CMSException {
        this(CMSUtils.readContentInfo(array));
    }
    
    public CMSSignedData(final CMSProcessable cmsProcessable, final byte[] array) throws CMSException {
        this(cmsProcessable, CMSUtils.readContentInfo(array));
    }
    
    public CMSSignedData(final Map map, final byte[] array) throws CMSException {
        this(map, CMSUtils.readContentInfo(array));
    }
    
    public CMSSignedData(final CMSProcessable cmsProcessable, final InputStream inputStream) throws CMSException {
        this(cmsProcessable, CMSUtils.readContentInfo((InputStream)new ASN1InputStream(inputStream)));
    }
    
    public CMSSignedData(final InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }
    
    public CMSSignedData(final CMSProcessable cmsProcessable, final ContentInfo contentInfo) throws CMSException {
        if (cmsProcessable instanceof CMSTypedData) {
            this.signedContent = (CMSTypedData)cmsProcessable;
        }
        else {
            this.signedContent = new CMSTypedData() {
                public ASN1ObjectIdentifier getContentType() {
                    return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
                }
                
                public void write(final OutputStream outputStream) throws IOException, CMSException {
                    cmsProcessable.write(outputStream);
                }
                
                public Object getContent() {
                    return cmsProcessable.getContent();
                }
            };
        }
        this.contentInfo = contentInfo;
        this.signedData = this.getSignedData();
    }
    
    public CMSSignedData(final Map hashes, final ContentInfo contentInfo) throws CMSException {
        this.hashes = hashes;
        this.contentInfo = contentInfo;
        this.signedData = this.getSignedData();
    }
    
    public CMSSignedData(final ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        this.signedData = this.getSignedData();
        final ASN1Encodable content = this.signedData.getEncapContentInfo().getContent();
        if (content != null) {
            if (content instanceof ASN1OctetString) {
                this.signedContent = new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString)content).getOctets());
            }
            else {
                this.signedContent = new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), content);
            }
        }
        else {
            this.signedContent = null;
        }
    }
    
    private SignedData getSignedData() throws CMSException {
        try {
            return SignedData.getInstance((Object)this.contentInfo.getContent());
        }
        catch (final ClassCastException ex) {
            throw new CMSException("Malformed content.", ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CMSException("Malformed content.", ex2);
        }
    }
    
    public int getVersion() {
        return this.signedData.getVersion().getValue().intValue();
    }
    
    public SignerInformationStore getSignerInfos() {
        if (this.signerInfoStore == null) {
            final ASN1Set signerInfos = this.signedData.getSignerInfos();
            final ArrayList list = new ArrayList();
            for (int i = 0; i != signerInfos.size(); ++i) {
                final SignerInfo instance = SignerInfo.getInstance((Object)signerInfos.getObjectAt(i));
                final ASN1ObjectIdentifier contentType = this.signedData.getEncapContentInfo().getContentType();
                if (this.hashes == null) {
                    list.add(new SignerInformation(instance, contentType, this.signedContent, null));
                }
                else {
                    list.add(new SignerInformation(instance, contentType, null, (this.hashes.keySet().iterator().next() instanceof String) ? this.hashes.get(instance.getDigestAlgorithm().getAlgorithm().getId()) : this.hashes.get(instance.getDigestAlgorithm().getAlgorithm())));
                }
            }
            this.signerInfoStore = new SignerInformationStore(list);
        }
        return this.signerInfoStore;
    }
    
    public boolean isDetachedSignature() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() > 0;
    }
    
    public boolean isCertificateManagementMessage() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() == 0;
    }
    
    public Store<X509CertificateHolder> getCertificates() {
        return (Store<X509CertificateHolder>)CMSSignedData.HELPER.getCertificates(this.signedData.getCertificates());
    }
    
    public Store<X509CRLHolder> getCRLs() {
        return (Store<X509CRLHolder>)CMSSignedData.HELPER.getCRLs(this.signedData.getCRLs());
    }
    
    public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
        return (Store<X509AttributeCertificateHolder>)CMSSignedData.HELPER.getAttributeCertificates(this.signedData.getCertificates());
    }
    
    public Store getOtherRevocationInfo(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSSignedData.HELPER.getOtherRevocationInfo(asn1ObjectIdentifier, this.signedData.getCRLs());
    }
    
    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        final HashSet set = new HashSet(this.signedData.getDigestAlgorithms().size());
        final Enumeration objects = this.signedData.getDigestAlgorithms().getObjects();
        while (objects.hasMoreElements()) {
            set.add(AlgorithmIdentifier.getInstance(objects.nextElement()));
        }
        return (Set<AlgorithmIdentifier>)Collections.unmodifiableSet((Set<?>)set);
    }
    
    public String getSignedContentTypeOID() {
        return this.signedData.getEncapContentInfo().getContentType().getId();
    }
    
    public CMSTypedData getSignedContent() {
        return this.signedContent;
    }
    
    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
    
    public boolean verifySignatures(final SignerInformationVerifierProvider signerInformationVerifierProvider) throws CMSException {
        return this.verifySignatures(signerInformationVerifierProvider, false);
    }
    
    public boolean verifySignatures(final SignerInformationVerifierProvider signerInformationVerifierProvider, final boolean b) throws CMSException {
        for (final SignerInformation signerInformation : this.getSignerInfos().getSigners()) {
            try {
                if (!signerInformation.verify(signerInformationVerifierProvider.get(signerInformation.getSID()))) {
                    return false;
                }
                if (b) {
                    continue;
                }
                final Iterator<SignerInformation> iterator2 = signerInformation.getCounterSignatures().getSigners().iterator();
                while (iterator2.hasNext()) {
                    if (!this.verifyCounterSignature(iterator2.next(), signerInformationVerifierProvider)) {
                        return false;
                    }
                }
            }
            catch (final OperatorCreationException ex) {
                throw new CMSException("failure in verifier provider: " + ex.getMessage(), ex);
            }
        }
        return true;
    }
    
    private boolean verifyCounterSignature(final SignerInformation signerInformation, final SignerInformationVerifierProvider signerInformationVerifierProvider) throws OperatorCreationException, CMSException {
        if (!signerInformation.verify(signerInformationVerifierProvider.get(signerInformation.getSID()))) {
            return false;
        }
        final Iterator<SignerInformation> iterator = signerInformation.getCounterSignatures().getSigners().iterator();
        while (iterator.hasNext()) {
            if (!this.verifyCounterSignature(iterator.next(), signerInformationVerifierProvider)) {
                return false;
            }
        }
        return true;
    }
    
    public static CMSSignedData replaceSigners(final CMSSignedData cmsSignedData, final SignerInformationStore signerInfoStore) {
        final CMSSignedData cmsSignedData2 = new CMSSignedData(cmsSignedData);
        cmsSignedData2.signerInfoStore = signerInfoStore;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        for (final SignerInformation signerInformation : signerInfoStore.getSigners()) {
            asn1EncodableVector.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(signerInformation.getDigestAlgorithmID()));
            asn1EncodableVector2.add((ASN1Encodable)signerInformation.toASN1Structure());
        }
        final DERSet set = new DERSet(asn1EncodableVector);
        final DERSet set2 = new DERSet(asn1EncodableVector2);
        final ASN1Sequence asn1Sequence = (ASN1Sequence)cmsSignedData.signedData.toASN1Primitive();
        final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
        asn1EncodableVector3.add(asn1Sequence.getObjectAt(0));
        asn1EncodableVector3.add((ASN1Encodable)set);
        for (int i = 2; i != asn1Sequence.size() - 1; ++i) {
            asn1EncodableVector3.add(asn1Sequence.getObjectAt(i));
        }
        asn1EncodableVector3.add((ASN1Encodable)set2);
        cmsSignedData2.signedData = SignedData.getInstance((Object)new BERSequence(asn1EncodableVector3));
        cmsSignedData2.contentInfo = new ContentInfo(cmsSignedData2.contentInfo.getContentType(), (ASN1Encodable)cmsSignedData2.signedData);
        return cmsSignedData2;
    }
    
    public static CMSSignedData replaceCertificatesAndCRLs(final CMSSignedData cmsSignedData, final Store store, final Store store2, final Store store3) throws CMSException {
        final CMSSignedData cmsSignedData2 = new CMSSignedData(cmsSignedData);
        ASN1Set set = null;
        ASN1Set set2 = null;
        if (store != null || store2 != null) {
            final ArrayList list = new ArrayList();
            if (store != null) {
                list.addAll(CMSUtils.getCertificatesFromStore(store));
            }
            if (store2 != null) {
                list.addAll(CMSUtils.getAttributeCertificatesFromStore(store2));
            }
            final ASN1Set berSetFromList = CMSUtils.createBerSetFromList(list);
            if (berSetFromList.size() != 0) {
                set = berSetFromList;
            }
        }
        if (store3 != null) {
            final ASN1Set berSetFromList2 = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(store3));
            if (berSetFromList2.size() != 0) {
                set2 = berSetFromList2;
            }
        }
        cmsSignedData2.signedData = new SignedData(cmsSignedData.signedData.getDigestAlgorithms(), cmsSignedData.signedData.getEncapContentInfo(), set, set2, cmsSignedData.signedData.getSignerInfos());
        cmsSignedData2.contentInfo = new ContentInfo(cmsSignedData2.contentInfo.getContentType(), (ASN1Encodable)cmsSignedData2.signedData);
        return cmsSignedData2;
    }
    
    static {
        HELPER = CMSSignedHelper.INSTANCE;
    }
}
