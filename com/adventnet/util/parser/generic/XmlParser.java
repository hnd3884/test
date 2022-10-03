package com.adventnet.util.parser.generic;

import java.io.File;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

class XmlParser
{
    int level;
    Document doc;
    DocumentBuilder docBuilder;
    String xmlFile;
    ParsedInfo parsedInfo;
    
    XmlParser() throws ParserConfigurationException {
        this.level = 0;
        this.doc = null;
        this.docBuilder = null;
        this.xmlFile = null;
        this.parsedInfo = null;
        this.initXmlParser();
    }
    
    void initXmlParser() throws ParserConfigurationException, FactoryConfigurationError {
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    
    void parseXml(final InputStream inputStream) throws IOException, SAXException, IllegalArgumentException {
        if (this.docBuilder == null) {
            System.out.println("docBuilder is NULL");
        }
        this.doc = this.docBuilder.parse(inputStream);
    }
    
    void parseXml(final File file) throws IOException, SAXException, IllegalArgumentException {
        if (this.docBuilder == null) {
            System.out.println("docbuilder is null");
        }
        this.doc = this.docBuilder.parse(file);
    }
    
    void parseXml() throws IOException, SAXException, IllegalArgumentException {
        File file = null;
        if (this.xmlFile != null) {
            file = new File(this.xmlFile);
        }
        this.parseXml(file);
    }
    
    void setXmlFile(final String xmlFile) {
        this.xmlFile = xmlFile;
    }
    
    Document getDocument() {
        return this.doc;
    }
    
    ParsedInfo getParsedInfo() {
        (this.parsedInfo = new ParsedInfo(this.doc)).getTokens();
        return this.parsedInfo;
    }
}
