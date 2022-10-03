package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.BEROctetString;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataGenerator
{
    public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";
    
    public CMSCompressedData generate(final CMSTypedData cmsTypedData, final OutputCompressor outputCompressor) throws CMSException {
        AlgorithmIdentifier algorithmIdentifier;
        BEROctetString berOctetString;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStream outputStream = outputCompressor.getOutputStream(byteArrayOutputStream);
            cmsTypedData.write(outputStream);
            outputStream.close();
            algorithmIdentifier = outputCompressor.getAlgorithmIdentifier();
            berOctetString = new BEROctetString(byteArrayOutputStream.toByteArray());
        }
        catch (final IOException ex) {
            throw new CMSException("exception encoding data.", ex);
        }
        return new CMSCompressedData(new ContentInfo(CMSObjectIdentifiers.compressedData, (ASN1Encodable)new CompressedData(algorithmIdentifier, new ContentInfo(cmsTypedData.getContentType(), (ASN1Encodable)berOctetString))));
    }
}
