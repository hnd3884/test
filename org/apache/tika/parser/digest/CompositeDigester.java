package org.apache.tika.parser.digest;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import org.apache.tika.parser.DigestingParser;

public class CompositeDigester implements DigestingParser.Digester
{
    private final DigestingParser.Digester[] digesters;
    
    public CompositeDigester(final DigestingParser.Digester... digesters) {
        this.digesters = digesters;
    }
    
    @Override
    public void digest(final InputStream is, final Metadata m, final ParseContext parseContext) throws IOException {
        final TemporaryResources tmp = new TemporaryResources();
        final TikaInputStream tis = TikaInputStream.get(is, tmp);
        try {
            for (final DigestingParser.Digester digester : this.digesters) {
                digester.digest((InputStream)tis, m, parseContext);
            }
        }
        finally {
            try {
                tmp.dispose();
            }
            catch (final TikaException e) {
                throw new IOException(e);
            }
        }
    }
}
