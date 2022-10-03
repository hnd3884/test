package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import java.io.Reader;
import org.xml.sax.XMLReader;
import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import java.io.OutputStream;
import java.io.InputStream;

public class XML_SAX_FI extends TransformInputOutput
{
    @Override
    public void parse(final InputStream xml, final OutputStream finf, final String workingDirectory) throws Exception {
        final SAXParser saxParser = this.getParser();
        final SAXDocumentSerializer documentSerializer = this.getSerializer(finf);
        final XMLReader reader = saxParser.getXMLReader();
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", documentSerializer);
        reader.setContentHandler(documentSerializer);
        if (workingDirectory != null) {
            reader.setEntityResolver(TransformInputOutput.createRelativePathResolver(workingDirectory));
        }
        reader.parse(new InputSource(xml));
    }
    
    @Override
    public void parse(final InputStream xml, final OutputStream finf) throws Exception {
        this.parse(xml, finf, null);
    }
    
    public void convert(final Reader reader, final OutputStream finf) throws Exception {
        final InputSource is = new InputSource(reader);
        final SAXParser saxParser = this.getParser();
        final SAXDocumentSerializer documentSerializer = this.getSerializer(finf);
        saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", documentSerializer);
        saxParser.parse(is, documentSerializer);
    }
    
    private SAXParser getParser() {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            return saxParserFactory.newSAXParser();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private SAXDocumentSerializer getSerializer(final OutputStream finf) {
        final SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setOutputStream(finf);
        return documentSerializer;
    }
    
    public static void main(final String[] args) throws Exception {
        final XML_SAX_FI s = new XML_SAX_FI();
        s.parse(args);
    }
}
