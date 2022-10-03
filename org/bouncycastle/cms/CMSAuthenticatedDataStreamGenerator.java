package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import org.bouncycastle.util.io.TeeOutputStream;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.io.OutputStream;
import org.bouncycastle.operator.MacCalculator;

public class CMSAuthenticatedDataStreamGenerator extends CMSAuthenticatedGenerator
{
    private int bufferSize;
    private boolean berEncodeRecipientSet;
    private MacCalculator macCalculator;
    
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public void setBEREncodeRecipients(final boolean berEncodeRecipientSet) {
        this.berEncodeRecipientSet = berEncodeRecipientSet;
    }
    
    public OutputStream open(final OutputStream outputStream, final MacCalculator macCalculator) throws CMSException {
        return this.open(CMSObjectIdentifiers.data, outputStream, macCalculator);
    }
    
    public OutputStream open(final OutputStream outputStream, final MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        return this.open(CMSObjectIdentifiers.data, outputStream, macCalculator, digestCalculator);
    }
    
    public OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final MacCalculator macCalculator) throws CMSException {
        return this.open(asn1ObjectIdentifier, outputStream, macCalculator, null);
    }
    
    public OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        this.macCalculator = macCalculator;
        try {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            final Iterator iterator = this.recipientInfoGenerators.iterator();
            while (iterator.hasNext()) {
                asn1EncodableVector.add((ASN1Encodable)((RecipientInfoGenerator)iterator.next()).generate(macCalculator.getKey()));
            }
            final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
            berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.authenticatedData);
            final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
            berSequenceGenerator2.addObject((ASN1Encodable)new ASN1Integer((long)AuthenticatedData.calculateVersion(this.originatorInfo)));
            if (this.originatorInfo != null) {
                berSequenceGenerator2.addObject((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
            }
            if (this.berEncodeRecipientSet) {
                berSequenceGenerator2.getRawOutputStream().write(new BERSet(asn1EncodableVector).getEncoded());
            }
            else {
                berSequenceGenerator2.getRawOutputStream().write(new DERSet(asn1EncodableVector).getEncoded());
            }
            berSequenceGenerator2.getRawOutputStream().write(macCalculator.getAlgorithmIdentifier().getEncoded());
            if (digestCalculator != null) {
                berSequenceGenerator2.addObject((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)digestCalculator.getAlgorithmIdentifier()));
            }
            final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
            berSequenceGenerator3.addObject((ASN1Encodable)asn1ObjectIdentifier);
            final OutputStream berOctetOutputStream = CMSUtils.createBEROctetOutputStream(berSequenceGenerator3.getRawOutputStream(), 0, false, this.bufferSize);
            TeeOutputStream teeOutputStream;
            if (digestCalculator != null) {
                teeOutputStream = new TeeOutputStream(berOctetOutputStream, digestCalculator.getOutputStream());
            }
            else {
                teeOutputStream = new TeeOutputStream(berOctetOutputStream, macCalculator.getOutputStream());
            }
            return new CmsAuthenticatedDataOutputStream(macCalculator, digestCalculator, asn1ObjectIdentifier, (OutputStream)teeOutputStream, berSequenceGenerator, berSequenceGenerator2, berSequenceGenerator3);
        }
        catch (final IOException ex) {
            throw new CMSException("exception decoding algorithm parameters.", ex);
        }
    }
    
    private class CmsAuthenticatedDataOutputStream extends OutputStream
    {
        private OutputStream dataStream;
        private BERSequenceGenerator cGen;
        private BERSequenceGenerator envGen;
        private BERSequenceGenerator eiGen;
        private MacCalculator macCalculator;
        private DigestCalculator digestCalculator;
        private ASN1ObjectIdentifier contentType;
        
        public CmsAuthenticatedDataOutputStream(final MacCalculator macCalculator, final DigestCalculator digestCalculator, final ASN1ObjectIdentifier contentType, final OutputStream dataStream, final BERSequenceGenerator cGen, final BERSequenceGenerator envGen, final BERSequenceGenerator eiGen) {
            this.macCalculator = macCalculator;
            this.digestCalculator = digestCalculator;
            this.contentType = contentType;
            this.dataStream = dataStream;
            this.cGen = cGen;
            this.envGen = envGen;
            this.eiGen = eiGen;
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.dataStream.write(n);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.dataStream.write(array, n, n2);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.dataStream.write(array);
        }
        
        @Override
        public void close() throws IOException {
            this.dataStream.close();
            this.eiGen.close();
            Map<Object, Object> map;
            if (this.digestCalculator != null) {
                map = Collections.unmodifiableMap((Map<?, ?>)CMSAuthenticatedDataStreamGenerator.this.getBaseParameters(this.contentType, this.digestCalculator.getAlgorithmIdentifier(), this.macCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
                if (CMSAuthenticatedDataStreamGenerator.this.authGen == null) {
                    CMSAuthenticatedDataStreamGenerator.this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
                }
                final DERSet set = new DERSet(CMSAuthenticatedDataStreamGenerator.this.authGen.getAttributes(map).toASN1EncodableVector());
                final OutputStream outputStream = this.macCalculator.getOutputStream();
                outputStream.write(((ASN1Set)set).getEncoded("DER"));
                outputStream.close();
                this.envGen.addObject((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)set));
            }
            else {
                map = Collections.unmodifiableMap((Map<?, ?>)new HashMap<Object, Object>());
            }
            this.envGen.addObject((ASN1Encodable)new DEROctetString(this.macCalculator.getMac()));
            if (CMSAuthenticatedDataStreamGenerator.this.unauthGen != null) {
                this.envGen.addObject((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)new BERSet(CMSAuthenticatedDataStreamGenerator.this.unauthGen.getAttributes(map).toASN1EncodableVector())));
            }
            this.envGen.close();
            this.cGen.close();
        }
    }
}
