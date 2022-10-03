package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.InputSource;
import java.io.IOException;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import java.io.InputStream;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.util.xml.SAXParser;

public class SAXParserImpl extends SAXParser
{
    private ParserSAX parser;
    
    public SAXParserImpl() {
        this.parser = new ParserSAX();
    }
    
    @Override
    public XMLReader getXMLReader() throws SAXException {
        return this.parser;
    }
    
    @Override
    public boolean isNamespaceAware() {
        return this.parser.mIsNSAware;
    }
    
    @Override
    public boolean isValidating() {
        return false;
    }
    
    @Override
    public void parse(final InputStream inputStream, final DefaultHandler defaultHandler) throws SAXException, IOException {
        this.parser.parse(inputStream, defaultHandler);
    }
    
    @Override
    public void parse(final InputSource inputSource, final DefaultHandler defaultHandler) throws SAXException, IOException {
        this.parser.parse(inputSource, defaultHandler);
    }
}
