package org.apache.axiom.om.impl.common.serializer.push.sax;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.util.sax.AbstractXMLReader;

public class XMLReaderImpl extends AbstractXMLReader
{
    private final AxiomContainer root;
    private final boolean cache;
    
    public XMLReaderImpl(final AxiomContainer root, final boolean cache) {
        this.root = root;
        this.cache = cache;
    }
    
    public void parse(final InputSource input) throws IOException, SAXException {
        this.parse();
    }
    
    public void parse(final String systemId) throws IOException, SAXException {
        this.parse();
    }
    
    private void parse() throws SAXException {
        try {
            this.root.internalSerialize(new SAXSerializer((OMSerializable)this.root, this.contentHandler, this.lexicalHandler), new OMOutputFormat(), this.cache);
        }
        catch (final OutputException ex) {
            throw (SAXException)ex.getCause();
        }
    }
}
