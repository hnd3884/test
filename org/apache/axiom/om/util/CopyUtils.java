package org.apache.axiom.om.util;

import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPEnvelope;

public class CopyUtils
{
    private CopyUtils() {
    }
    
    @Deprecated
    public static SOAPEnvelope copy(final SOAPEnvelope sourceEnv) {
        final SOAPCloneOptions options = new SOAPCloneOptions();
        options.setFetchDataHandlers(true);
        options.setPreserveModel(true);
        options.setCopyOMDataSources(true);
        return (SOAPEnvelope)sourceEnv.clone(options);
    }
    
    public static void reader2writer(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        final OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(reader);
        try {
            final OMDocument omDocument = builder.getDocument();
            final Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                final OMNode omNode = it.next();
                omNode.serializeAndConsume(writer);
            }
        }
        finally {
            builder.close();
        }
    }
}
