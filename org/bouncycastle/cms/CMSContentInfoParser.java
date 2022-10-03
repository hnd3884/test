package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.ContentInfoParser;

public class CMSContentInfoParser
{
    protected ContentInfoParser _contentInfo;
    protected InputStream _data;
    
    protected CMSContentInfoParser(final InputStream data) throws CMSException {
        this._data = data;
        try {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)new ASN1StreamParser(data).readObject();
            if (asn1SequenceParser == null) {
                throw new CMSException("No content found.");
            }
            this._contentInfo = new ContentInfoParser(asn1SequenceParser);
        }
        catch (final IOException ex) {
            throw new CMSException("IOException reading content.", ex);
        }
        catch (final ClassCastException ex2) {
            throw new CMSException("Unexpected object reading content.", ex2);
        }
    }
    
    public void close() throws IOException {
        this._data.close();
    }
}
