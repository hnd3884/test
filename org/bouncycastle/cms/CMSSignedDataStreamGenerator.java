package org.bouncycastle.cms;

import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.cms.SignerInfo;
import java.util.List;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import java.util.Iterator;
import java.util.Collection;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.io.IOException;
import java.io.OutputStream;

public class CMSSignedDataStreamGenerator extends CMSSignedGenerator
{
    private int _bufferSize;
    
    public void setBufferSize(final int bufferSize) {
        this._bufferSize = bufferSize;
    }
    
    public OutputStream open(final OutputStream outputStream) throws IOException {
        return this.open(outputStream, false);
    }
    
    public OutputStream open(final OutputStream outputStream, final boolean b) throws IOException {
        return this.open(CMSObjectIdentifiers.data, outputStream, b);
    }
    
    public OutputStream open(final OutputStream outputStream, final boolean b, final OutputStream outputStream2) throws IOException {
        return this.open(CMSObjectIdentifiers.data, outputStream, b, outputStream2);
    }
    
    public OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final boolean b) throws IOException {
        return this.open(asn1ObjectIdentifier, outputStream, b, null);
    }
    
    public OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final boolean b, final OutputStream outputStream2) throws IOException {
        final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
        berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.signedData);
        final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
        berSequenceGenerator2.addObject((ASN1Encodable)this.calculateVersion(asn1ObjectIdentifier));
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Iterator iterator = this._signers.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(((SignerInformation)iterator.next()).getDigestAlgorithmID()));
        }
        final Iterator iterator2 = this.signerGens.iterator();
        while (iterator2.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)((SignerInfoGenerator)iterator2.next()).getDigestAlgorithm());
        }
        berSequenceGenerator2.getRawOutputStream().write(new DERSet(asn1EncodableVector).getEncoded());
        final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
        berSequenceGenerator3.addObject((ASN1Encodable)asn1ObjectIdentifier);
        return new CmsSignedDataOutputStream(CMSUtils.attachSignersToOutputStream(this.signerGens, CMSUtils.getSafeTeeOutputStream(outputStream2, b ? CMSUtils.createBEROctetOutputStream(berSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize) : null)), asn1ObjectIdentifier, berSequenceGenerator, berSequenceGenerator2, berSequenceGenerator3);
    }
    
    private ASN1Integer calculateVersion(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        if (this.certs != null) {
            for (final Object next : this.certs) {
                if (next instanceof ASN1TaggedObject) {
                    final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)next;
                    if (asn1TaggedObject.getTagNo() == 1) {
                        b3 = true;
                    }
                    else if (asn1TaggedObject.getTagNo() == 2) {
                        b4 = true;
                    }
                    else {
                        if (asn1TaggedObject.getTagNo() != 3) {
                            continue;
                        }
                        b = true;
                    }
                }
            }
        }
        if (b) {
            return new ASN1Integer(5L);
        }
        if (this.crls != null) {
            final Iterator iterator2 = this.crls.iterator();
            while (iterator2.hasNext()) {
                if (iterator2.next() instanceof ASN1TaggedObject) {
                    b2 = true;
                }
            }
        }
        if (b2) {
            return new ASN1Integer(5L);
        }
        if (b4) {
            return new ASN1Integer(4L);
        }
        if (b3) {
            return new ASN1Integer(3L);
        }
        if (this.checkForVersion3(this._signers, this.signerGens)) {
            return new ASN1Integer(3L);
        }
        if (!CMSObjectIdentifiers.data.equals((Object)asn1ObjectIdentifier)) {
            return new ASN1Integer(3L);
        }
        return new ASN1Integer(1L);
    }
    
    private boolean checkForVersion3(final List list, final List list2) {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (SignerInfo.getInstance((Object)((SignerInformation)iterator.next()).toASN1Structure()).getVersion().getValue().intValue() == 3) {
                return true;
            }
        }
        final Iterator iterator2 = list2.iterator();
        while (iterator2.hasNext()) {
            if (((SignerInfoGenerator)iterator2.next()).getGeneratedVersion() == 3) {
                return true;
            }
        }
        return false;
    }
    
    private class CmsSignedDataOutputStream extends OutputStream
    {
        private OutputStream _out;
        private ASN1ObjectIdentifier _contentOID;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _sigGen;
        private BERSequenceGenerator _eiGen;
        
        public CmsSignedDataOutputStream(final OutputStream out, final ASN1ObjectIdentifier contentOID, final BERSequenceGenerator sGen, final BERSequenceGenerator sigGen, final BERSequenceGenerator eiGen) {
            this._out = out;
            this._contentOID = contentOID;
            this._sGen = sGen;
            this._sigGen = sigGen;
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
            CMSSignedDataStreamGenerator.this.digests.clear();
            if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 0, (ASN1Encodable)CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs)).getEncoded());
            }
            if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 1, (ASN1Encodable)CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls)).getEncoded());
            }
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            for (final SignerInfoGenerator signerInfoGenerator : CMSSignedDataStreamGenerator.this.signerGens) {
                try {
                    asn1EncodableVector.add((ASN1Encodable)signerInfoGenerator.generate(this._contentOID));
                    CMSSignedDataStreamGenerator.this.digests.put(signerInfoGenerator.getDigestAlgorithm().getAlgorithm().getId(), signerInfoGenerator.getCalculatedDigest());
                }
                catch (final CMSException ex) {
                    throw new CMSStreamException("exception generating signers: " + ex.getMessage(), ex);
                }
            }
            final Iterator iterator2 = CMSSignedDataStreamGenerator.this._signers.iterator();
            while (iterator2.hasNext()) {
                asn1EncodableVector.add((ASN1Encodable)((SignerInformation)iterator2.next()).toASN1Structure());
            }
            this._sigGen.getRawOutputStream().write(new DERSet(asn1EncodableVector).getEncoded());
            this._sigGen.close();
            this._sGen.close();
        }
    }
}
