package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.transform.Transformer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceException;
import javax.xml.transform.Result;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.HashMap;
import javax.xml.transform.Source;
import java.util.List;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.util.Map;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;

public final class MexEntityResolver implements XMLEntityResolver
{
    private final Map<String, SDDocumentSource> wsdls;
    
    public MexEntityResolver(final List<? extends Source> wsdls) throws IOException {
        this.wsdls = new HashMap<String, SDDocumentSource>();
        final Transformer transformer = XmlUtil.newTransformer();
        for (final Source source : wsdls) {
            final XMLStreamBufferResult xsbr = new XMLStreamBufferResult();
            try {
                transformer.transform(source, xsbr);
            }
            catch (final TransformerException e) {
                throw new WebServiceException(e);
            }
            final String systemId = source.getSystemId();
            if (systemId != null) {
                final SDDocumentSource doc = SDDocumentSource.create(JAXWSUtils.getFileOrURL(systemId), xsbr.getXMLStreamBuffer());
                this.wsdls.put(systemId, doc);
            }
        }
    }
    
    @Override
    public Parser resolveEntity(final String publicId, final String systemId) throws SAXException, IOException, XMLStreamException {
        if (systemId != null) {
            final SDDocumentSource src = this.wsdls.get(systemId);
            if (src != null) {
                return new Parser(src);
            }
        }
        return null;
    }
}
