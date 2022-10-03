package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.ContentInfoParser;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.cms.CompressedDataParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.operator.InputExpanderProvider;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class CMSCompressedDataParser extends CMSContentInfoParser
{
    public CMSCompressedDataParser(final byte[] array) throws CMSException {
        this(new ByteArrayInputStream(array));
    }
    
    public CMSCompressedDataParser(final InputStream inputStream) throws CMSException {
        super(inputStream);
    }
    
    public CMSTypedStream getContent(final InputExpanderProvider inputExpanderProvider) throws CMSException {
        try {
            final CompressedDataParser compressedDataParser = new CompressedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
            final ContentInfoParser encapContentInfo = compressedDataParser.getEncapContentInfo();
            return new CMSTypedStream(encapContentInfo.getContentType().getId(), inputExpanderProvider.get(compressedDataParser.getCompressionAlgorithmIdentifier()).getInputStream(((ASN1OctetStringParser)encapContentInfo.getContent(4)).getOctetStream()));
        }
        catch (final IOException ex) {
            throw new CMSException("IOException reading compressed content.", ex);
        }
    }
}
