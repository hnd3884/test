package com.sun.xml.internal.ws.encoding.fastinfoset;

import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;

public final class FastInfosetStreamReaderRecyclable extends StAXDocumentParser implements XMLStreamReaderFactory.RecycleAware
{
    private static final FastInfosetStreamReaderFactory READER_FACTORY;
    
    public FastInfosetStreamReaderRecyclable() {
    }
    
    public FastInfosetStreamReaderRecyclable(final InputStream in) {
        super(in);
    }
    
    @Override
    public void onRecycled() {
        FastInfosetStreamReaderRecyclable.READER_FACTORY.doRecycle(this);
    }
    
    static {
        READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();
    }
}
