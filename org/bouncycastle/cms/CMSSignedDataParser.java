package org.bouncycastle.cms;

import org.bouncycastle.util.io.Streams;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.BERSetParser;
import org.bouncycastle.asn1.DERTaggedObject;
import java.util.List;
import org.bouncycastle.asn1.ASN1Generator;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import java.io.OutputStream;
import org.bouncycastle.util.Store;
import java.util.Iterator;
import java.util.Collection;
import org.bouncycastle.asn1.cms.SignerInfo;
import java.util.ArrayList;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1SetParser;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import java.util.Collections;
import org.bouncycastle.operator.OperatorCreationException;
import java.util.HashSet;
import java.util.HashMap;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Set;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.SignedDataParser;

public class CMSSignedDataParser extends CMSContentInfoParser
{
    private static final CMSSignedHelper HELPER;
    private SignedDataParser _signedData;
    private ASN1ObjectIdentifier _signedContentType;
    private CMSTypedStream _signedContent;
    private Map digests;
    private Set<AlgorithmIdentifier> digestAlgorithms;
    private SignerInformationStore _signerInfoStore;
    private ASN1Set _certSet;
    private ASN1Set _crlSet;
    private boolean _isCertCrlParsed;
    
    public CMSSignedDataParser(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array) throws CMSException {
        this(digestCalculatorProvider, new ByteArrayInputStream(array));
    }
    
    public CMSSignedDataParser(final DigestCalculatorProvider digestCalculatorProvider, final CMSTypedStream cmsTypedStream, final byte[] array) throws CMSException {
        this(digestCalculatorProvider, cmsTypedStream, new ByteArrayInputStream(array));
    }
    
    public CMSSignedDataParser(final DigestCalculatorProvider digestCalculatorProvider, final InputStream inputStream) throws CMSException {
        this(digestCalculatorProvider, null, inputStream);
    }
    
    public CMSSignedDataParser(final DigestCalculatorProvider digestCalculatorProvider, final CMSTypedStream signedContent, final InputStream inputStream) throws CMSException {
        super(inputStream);
        try {
            this._signedContent = signedContent;
            this._signedData = SignedDataParser.getInstance((Object)this._contentInfo.getContent(16));
            this.digests = new HashMap();
            final ASN1SetParser digestAlgorithms = this._signedData.getDigestAlgorithms();
            final HashSet set = new HashSet();
            ASN1Encodable object;
            while ((object = digestAlgorithms.readObject()) != null) {
                final AlgorithmIdentifier instance = AlgorithmIdentifier.getInstance((Object)object);
                set.add(instance);
                try {
                    final DigestCalculator value = digestCalculatorProvider.get(instance);
                    if (value == null) {
                        continue;
                    }
                    this.digests.put(instance.getAlgorithm(), value);
                }
                catch (final OperatorCreationException ex) {}
            }
            this.digestAlgorithms = (Set<AlgorithmIdentifier>)Collections.unmodifiableSet((Set<?>)set);
            final ContentInfoParser encapContentInfo = this._signedData.getEncapContentInfo();
            final ASN1Encodable content = encapContentInfo.getContent(4);
            if (content instanceof ASN1OctetStringParser) {
                final CMSTypedStream signedContent2 = new CMSTypedStream(encapContentInfo.getContentType(), ((ASN1OctetStringParser)content).getOctetStream());
                if (this._signedContent == null) {
                    this._signedContent = signedContent2;
                }
                else {
                    signedContent2.drain();
                }
            }
            else if (content != null) {
                final PKCS7TypedStream signedContent3 = new PKCS7TypedStream(encapContentInfo.getContentType(), content);
                if (this._signedContent == null) {
                    this._signedContent = signedContent3;
                }
                else {
                    signedContent3.drain();
                }
            }
            if (signedContent == null) {
                this._signedContentType = encapContentInfo.getContentType();
            }
            else {
                this._signedContentType = this._signedContent.getContentType();
            }
        }
        catch (final IOException ex2) {
            throw new CMSException("io exception: " + ex2.getMessage(), ex2);
        }
    }
    
    public int getVersion() {
        return this._signedData.getVersion().getValue().intValue();
    }
    
    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        return this.digestAlgorithms;
    }
    
    public SignerInformationStore getSignerInfos() throws CMSException {
        if (this._signerInfoStore == null) {
            this.populateCertCrlSets();
            final ArrayList list = new ArrayList();
            final HashMap hashMap = new HashMap();
            for (final Object next : this.digests.keySet()) {
                hashMap.put(next, ((DigestCalculator)this.digests.get(next)).getDigest());
            }
            try {
                ASN1Encodable object;
                while ((object = this._signedData.getSignerInfos().readObject()) != null) {
                    final SignerInfo instance = SignerInfo.getInstance((Object)object.toASN1Primitive());
                    list.add(new SignerInformation(instance, this._signedContentType, null, (byte[])hashMap.get(instance.getDigestAlgorithm().getAlgorithm())));
                }
            }
            catch (final IOException ex) {
                throw new CMSException("io exception: " + ex.getMessage(), ex);
            }
            this._signerInfoStore = new SignerInformationStore(list);
        }
        return this._signerInfoStore;
    }
    
    public Store getCertificates() throws CMSException {
        this.populateCertCrlSets();
        return CMSSignedDataParser.HELPER.getCertificates(this._certSet);
    }
    
    public Store getCRLs() throws CMSException {
        this.populateCertCrlSets();
        return CMSSignedDataParser.HELPER.getCRLs(this._crlSet);
    }
    
    public Store getAttributeCertificates() throws CMSException {
        this.populateCertCrlSets();
        return CMSSignedDataParser.HELPER.getAttributeCertificates(this._certSet);
    }
    
    public Store getOtherRevocationInfo(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        this.populateCertCrlSets();
        return CMSSignedDataParser.HELPER.getOtherRevocationInfo(asn1ObjectIdentifier, this._crlSet);
    }
    
    private void populateCertCrlSets() throws CMSException {
        if (this._isCertCrlParsed) {
            return;
        }
        this._isCertCrlParsed = true;
        try {
            this._certSet = getASN1Set(this._signedData.getCertificates());
            this._crlSet = getASN1Set(this._signedData.getCrls());
        }
        catch (final IOException ex) {
            throw new CMSException("problem parsing cert/crl sets", ex);
        }
    }
    
    public String getSignedContentTypeOID() {
        return this._signedContentType.getId();
    }
    
    public CMSTypedStream getSignedContent() {
        if (this._signedContent == null) {
            return null;
        }
        return new CMSTypedStream(this._signedContent.getContentType(), CMSUtils.attachDigestsToInputStream(this.digests.values(), this._signedContent.getContentStream()));
    }
    
    public static OutputStream replaceSigners(final InputStream inputStream, final SignerInformationStore signerInformationStore, final OutputStream outputStream) throws CMSException, IOException {
        final SignedDataParser instance = SignedDataParser.getInstance((Object)new ContentInfoParser((ASN1SequenceParser)new ASN1StreamParser(inputStream).readObject()).getContent(16));
        final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
        berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.signedData);
        final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
        berSequenceGenerator2.addObject((ASN1Encodable)instance.getVersion());
        instance.getDigestAlgorithms().toASN1Primitive();
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Iterator<SignerInformation> iterator = signerInformationStore.getSigners().iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(iterator.next().getDigestAlgorithmID()));
        }
        berSequenceGenerator2.getRawOutputStream().write(new DERSet(asn1EncodableVector).getEncoded());
        final ContentInfoParser encapContentInfo = instance.getEncapContentInfo();
        final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
        berSequenceGenerator3.addObject((ASN1Encodable)encapContentInfo.getContentType());
        pipeEncapsulatedOctetString(encapContentInfo, berSequenceGenerator3.getRawOutputStream());
        berSequenceGenerator3.close();
        writeSetToGeneratorTagged((ASN1Generator)berSequenceGenerator2, instance.getCertificates(), 0);
        writeSetToGeneratorTagged((ASN1Generator)berSequenceGenerator2, instance.getCrls(), 1);
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        final Iterator<SignerInformation> iterator2 = signerInformationStore.getSigners().iterator();
        while (iterator2.hasNext()) {
            asn1EncodableVector2.add((ASN1Encodable)iterator2.next().toASN1Structure());
        }
        berSequenceGenerator2.getRawOutputStream().write(new DERSet(asn1EncodableVector2).getEncoded());
        berSequenceGenerator2.close();
        berSequenceGenerator.close();
        return outputStream;
    }
    
    public static OutputStream replaceCertificatesAndCRLs(final InputStream inputStream, final Store store, final Store store2, final Store store3, final OutputStream outputStream) throws CMSException, IOException {
        final SignedDataParser instance = SignedDataParser.getInstance((Object)new ContentInfoParser((ASN1SequenceParser)new ASN1StreamParser(inputStream).readObject()).getContent(16));
        final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
        berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.signedData);
        final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
        berSequenceGenerator2.addObject((ASN1Encodable)instance.getVersion());
        berSequenceGenerator2.getRawOutputStream().write(instance.getDigestAlgorithms().toASN1Primitive().getEncoded());
        final ContentInfoParser encapContentInfo = instance.getEncapContentInfo();
        final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
        berSequenceGenerator3.addObject((ASN1Encodable)encapContentInfo.getContentType());
        pipeEncapsulatedOctetString(encapContentInfo, berSequenceGenerator3.getRawOutputStream());
        berSequenceGenerator3.close();
        getASN1Set(instance.getCertificates());
        getASN1Set(instance.getCrls());
        if (store != null || store3 != null) {
            final ArrayList list = new ArrayList();
            if (store != null) {
                list.addAll(CMSUtils.getCertificatesFromStore(store));
            }
            if (store3 != null) {
                list.addAll(CMSUtils.getAttributeCertificatesFromStore(store3));
            }
            final ASN1Set berSetFromList = CMSUtils.createBerSetFromList(list);
            if (berSetFromList.size() > 0) {
                berSequenceGenerator2.getRawOutputStream().write(new DERTaggedObject(false, 0, (ASN1Encodable)berSetFromList).getEncoded());
            }
        }
        if (store2 != null) {
            final ASN1Set berSetFromList2 = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(store2));
            if (berSetFromList2.size() > 0) {
                berSequenceGenerator2.getRawOutputStream().write(new DERTaggedObject(false, 1, (ASN1Encodable)berSetFromList2).getEncoded());
            }
        }
        berSequenceGenerator2.getRawOutputStream().write(instance.getSignerInfos().toASN1Primitive().getEncoded());
        berSequenceGenerator2.close();
        berSequenceGenerator.close();
        return outputStream;
    }
    
    private static void writeSetToGeneratorTagged(final ASN1Generator asn1Generator, final ASN1SetParser asn1SetParser, final int n) throws IOException {
        final ASN1Set asn1Set = getASN1Set(asn1SetParser);
        if (asn1Set != null) {
            if (asn1SetParser instanceof BERSetParser) {
                asn1Generator.getRawOutputStream().write(new BERTaggedObject(false, n, (ASN1Encodable)asn1Set).getEncoded());
            }
            else {
                asn1Generator.getRawOutputStream().write(new DERTaggedObject(false, n, (ASN1Encodable)asn1Set).getEncoded());
            }
        }
    }
    
    private static ASN1Set getASN1Set(final ASN1SetParser asn1SetParser) {
        return (asn1SetParser == null) ? null : ASN1Set.getInstance((Object)asn1SetParser.toASN1Primitive());
    }
    
    private static void pipeEncapsulatedOctetString(final ContentInfoParser contentInfoParser, final OutputStream outputStream) throws IOException {
        final ASN1OctetStringParser asn1OctetStringParser = (ASN1OctetStringParser)contentInfoParser.getContent(4);
        if (asn1OctetStringParser != null) {
            pipeOctetString(asn1OctetStringParser, outputStream);
        }
    }
    
    private static void pipeOctetString(final ASN1OctetStringParser asn1OctetStringParser, final OutputStream outputStream) throws IOException {
        final OutputStream berOctetOutputStream = CMSUtils.createBEROctetOutputStream(outputStream, 0, true, 0);
        Streams.pipeAll(asn1OctetStringParser.getOctetStream(), berOctetOutputStream);
        berOctetOutputStream.close();
    }
    
    static {
        HELPER = CMSSignedHelper.INSTANCE;
    }
}
