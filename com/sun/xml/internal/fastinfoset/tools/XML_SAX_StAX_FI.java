package com.sun.xml.internal.fastinfoset.tools;

import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import java.io.OutputStream;
import java.io.InputStream;

public class XML_SAX_StAX_FI extends TransformInputOutput
{
    @Override
    public void parse(final InputStream xml, final OutputStream finf, final String workingDirectory) throws Exception {
        final StAXDocumentSerializer documentSerializer = new StAXDocumentSerializer();
        documentSerializer.setOutputStream(finf);
        final SAX2StAXWriter saxTostax = new SAX2StAXWriter(documentSerializer);
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", saxTostax);
        reader.setContentHandler(saxTostax);
        if (workingDirectory != null) {
            reader.setEntityResolver(TransformInputOutput.createRelativePathResolver(workingDirectory));
        }
        reader.parse(new InputSource(xml));
        xml.close();
        finf.close();
    }
    
    @Override
    public void parse(final InputStream xml, final OutputStream finf) throws Exception {
        this.parse(xml, finf, null);
    }
    
    public static void main(final String[] args) throws Exception {
        final XML_SAX_StAX_FI s = new XML_SAX_StAX_FI();
        s.parse(args);
    }
}
