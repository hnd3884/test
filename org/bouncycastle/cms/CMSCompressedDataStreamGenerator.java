package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.operator.OutputCompressor;
import java.io.OutputStream;

public class CMSCompressedDataStreamGenerator
{
    public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";
    private int _bufferSize;
    
    public void setBufferSize(final int bufferSize) {
        this._bufferSize = bufferSize;
    }
    
    public OutputStream open(final OutputStream outputStream, final OutputCompressor outputCompressor) throws IOException {
        return this.open(CMSObjectIdentifiers.data, outputStream, outputCompressor);
    }
    
    public OutputStream open(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OutputStream outputStream, final OutputCompressor outputCompressor) throws IOException {
        final BERSequenceGenerator berSequenceGenerator = new BERSequenceGenerator(outputStream);
        berSequenceGenerator.addObject((ASN1Encodable)CMSObjectIdentifiers.compressedData);
        final BERSequenceGenerator berSequenceGenerator2 = new BERSequenceGenerator(berSequenceGenerator.getRawOutputStream(), 0, true);
        berSequenceGenerator2.addObject((ASN1Encodable)new ASN1Integer(0L));
        berSequenceGenerator2.addObject((ASN1Encodable)outputCompressor.getAlgorithmIdentifier());
        final BERSequenceGenerator berSequenceGenerator3 = new BERSequenceGenerator(berSequenceGenerator2.getRawOutputStream());
        berSequenceGenerator3.addObject((ASN1Encodable)asn1ObjectIdentifier);
        return new CmsCompressedOutputStream(outputCompressor.getOutputStream(CMSUtils.createBEROctetOutputStream(berSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize)), berSequenceGenerator, berSequenceGenerator2, berSequenceGenerator3);
    }
    
    private class CmsCompressedOutputStream extends OutputStream
    {
        private OutputStream _out;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _eiGen;
        
        CmsCompressedOutputStream(final OutputStream out, final BERSequenceGenerator sGen, final BERSequenceGenerator cGen, final BERSequenceGenerator eiGen) {
            this._out = out;
            this._sGen = sGen;
            this._cGen = cGen;
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
            this._cGen.close();
            this._sGen.close();
        }
    }
}
