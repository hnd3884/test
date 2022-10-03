package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public class FastInfosetUtil
{
    public static XMLStreamReader createFIStreamReader(final InputStream in) {
        if (FastInfosetReflection.fiStAXDocumentParser_new == null) {
            throw new XMLReaderException("fastinfoset.noImplementation", new Object[0]);
        }
        try {
            final Object sdp = FastInfosetReflection.fiStAXDocumentParser_new.newInstance(new Object[0]);
            FastInfosetReflection.fiStAXDocumentParser_setStringInterning.invoke(sdp, Boolean.TRUE);
            FastInfosetReflection.fiStAXDocumentParser_setInputStream.invoke(sdp, in);
            return (XMLStreamReader)sdp;
        }
        catch (final Exception e) {
            throw new XMLStreamReaderException(e);
        }
    }
}
