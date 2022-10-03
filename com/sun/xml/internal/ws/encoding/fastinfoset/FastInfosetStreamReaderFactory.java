package com.sun.xml.internal.ws.encoding.fastinfoset;

import java.io.Reader;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;

public final class FastInfosetStreamReaderFactory extends XMLStreamReaderFactory
{
    private static final FastInfosetStreamReaderFactory factory;
    private ThreadLocal<StAXDocumentParser> pool;
    
    public FastInfosetStreamReaderFactory() {
        this.pool = new ThreadLocal<StAXDocumentParser>();
    }
    
    public static FastInfosetStreamReaderFactory getInstance() {
        return FastInfosetStreamReaderFactory.factory;
    }
    
    @Override
    public XMLStreamReader doCreate(final String systemId, final InputStream in, final boolean rejectDTDs) {
        final StAXDocumentParser parser = this.fetch();
        if (parser == null) {
            return FastInfosetCodec.createNewStreamReaderRecyclable(in, false);
        }
        parser.setInputStream(in);
        return parser;
    }
    
    @Override
    public XMLStreamReader doCreate(final String systemId, final Reader reader, final boolean rejectDTDs) {
        throw new UnsupportedOperationException();
    }
    
    private StAXDocumentParser fetch() {
        final StAXDocumentParser parser = this.pool.get();
        this.pool.set(null);
        return parser;
    }
    
    @Override
    public void doRecycle(final XMLStreamReader r) {
        if (r instanceof StAXDocumentParser) {
            this.pool.set((StAXDocumentParser)r);
        }
    }
    
    static {
        factory = new FastInfosetStreamReaderFactory();
    }
}
