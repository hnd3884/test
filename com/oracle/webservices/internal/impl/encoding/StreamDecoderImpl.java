package com.oracle.webservices.internal.impl.encoding;

import java.io.IOException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.encoding.StreamSOAPCodec;
import java.io.Closeable;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.io.InputStream;
import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;

public class StreamDecoderImpl implements StreamDecoder
{
    @Override
    public Message decode(final InputStream in, final String charset, final AttachmentSet att, final SOAPVersion soapVersion) throws IOException {
        XMLStreamReader reader = XMLStreamReaderFactory.create(null, in, charset, true);
        reader = new TidyXMLStreamReader(reader, in);
        return StreamSOAPCodec.decode(soapVersion, reader, att);
    }
}
