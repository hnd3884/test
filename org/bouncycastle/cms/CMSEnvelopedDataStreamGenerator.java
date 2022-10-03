package org.bouncycastle.cms;

import java.util.Map;
import java.util.HashMap;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.BERSequenceGenerator;
import java.io.IOException;
import java.util.Iterator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.operator.OutputEncryptor;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Set;

public class CMSEnvelopedDataStreamGenerator extends CMSEnvelopedGenerator
{
    private ASN1Set _unprotectedAttributes;
    private int _bufferSize;
    private boolean _berEncodeRecipientSet;
    
    public CMSEnvelopedDataStreamGenerator() {
        this._unprotectedAttributes = null;
    }
    
    public void setBufferSize(final int bufferSize) {
        this._bufferSize = bufferSize;
    }
    
    public void setBEREncodeRecipients(final boolean berEncodeRecipientSet) {
        this._berEncodeRecipientSet = berEncodeRecipientSet;
    }
    
    private ASN1Integer getVersion() {
        if (this.originatorInfo != null || this._unprotectedAttributes != null) {
            return new ASN1Integer(2L);
        }
        return new ASN1Integer(0L);
    }
    
    private OutputStream doOpen(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final OutputEncryptor outputEncryptor) throws IOException, CMSException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final GenericKey key = outputEncryptor.getKey();
        final Iterator iterator = this.recipientInfoGenerators.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)((RecipientInfoGenerator)iterator.next()).generate(key));
        }
        return this.open(asn1ObjectIdentifier, outputStream, asn1EncodableVector, outputEncryptor);
    }
    
    protected OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final ASN1EncodableVector asn1EncodableVector, final OutputEncryptor outputEncryptor) throws IOException {
        final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
        berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.envelopedData);
        final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
        berSequenceGenerator2.addObject((ASN1Encodable)this.getVersion());
        if (this.originatorInfo != null) {
            berSequenceGenerator2.addObject((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
        }
        if (this._berEncodeRecipientSet) {
            berSequenceGenerator2.getRawOutputStream().write(new BERSet(asn1EncodableVector).getEncoded());
        }
        else {
            berSequenceGenerator2.getRawOutputStream().write(new DERSet(asn1EncodableVector).getEncoded());
        }
        final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
        berSequenceGenerator3.addObject((ASN1Encodable)asn1ObjectIdentifier);
        berSequenceGenerator3.getRawOutputStream().write(outputEncryptor.getAlgorithmIdentifier().getEncoded());
        return new CmsEnvelopedDataOutputStream(outputEncryptor.getOutputStream(CMSUtils.createBEROctetOutputStream(berSequenceGenerator3.getRawOutputStream(), 0, false, this._bufferSize)), berSequenceGenerator, berSequenceGenerator2, berSequenceGenerator3);
    }
    
    protected OutputStream open(final OutputStream outputStream, final ASN1EncodableVector asn1EncodableVector, final OutputEncryptor outputEncryptor) throws CMSException {
        try {
            final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
            berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.envelopedData);
            final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
            Object o;
            if (this._berEncodeRecipientSet) {
                o = new BERSet(asn1EncodableVector);
            }
            else {
                o = new DERSet(asn1EncodableVector);
            }
            berSequenceGenerator2.addObject((ASN1Encodable)new ASN1Integer((long)EnvelopedData.calculateVersion(this.originatorInfo, (ASN1Set)o, this._unprotectedAttributes)));
            if (this.originatorInfo != null) {
                berSequenceGenerator2.addObject((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
            }
            berSequenceGenerator2.getRawOutputStream().write(((ASN1Set)o).getEncoded());
            final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
            berSequenceGenerator3.addObject((ASN1Encodable)CMSObjectIdentifiers.data);
            berSequenceGenerator3.getRawOutputStream().write(outputEncryptor.getAlgorithmIdentifier().getEncoded());
            return new CmsEnvelopedDataOutputStream(outputEncryptor.getOutputStream(CMSUtils.createBEROctetOutputStream(berSequenceGenerator3.getRawOutputStream(), 0, false, this._bufferSize)), berSequenceGenerator, berSequenceGenerator2, berSequenceGenerator3);
        }
        catch (final IOException ex) {
            throw new CMSException("exception decoding algorithm parameters.", ex);
        }
    }
    
    public OutputStream open(final OutputStream outputStream, final OutputEncryptor outputEncryptor) throws CMSException, IOException {
        return this.doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), outputStream, outputEncryptor);
    }
    
    public OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final OutputEncryptor outputEncryptor) throws CMSException, IOException {
        return this.doOpen(asn1ObjectIdentifier, outputStream, outputEncryptor);
    }
    
    private class CmsEnvelopedDataOutputStream extends OutputStream
    {
        private OutputStream _out;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _envGen;
        private BERSequenceGenerator _eiGen;
        
        public CmsEnvelopedDataOutputStream(final OutputStream out, final BERSequenceGenerator cGen, final BERSequenceGenerator envGen, final BERSequenceGenerator eiGen) {
            this._out = out;
            this._cGen = cGen;
            this._envGen = envGen;
            this._eiGen = eiGen;
        }
        
        @Override
        public void write(final int n) throws IOException {
            this._out.write(n);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this._out.write(array, n, n2);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this._out.write(array);
        }
        
        @Override
        public void close() throws IOException {
            this._out.close();
            this._eiGen.close();
            if (CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator != null) {
                this._envGen.addObject((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)new BERSet(CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator.getAttributes(new HashMap()).toASN1EncodableVector())));
            }
            this._envGen.close();
            this._cGen.close();
        }
    }
}
