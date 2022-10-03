package com.me.devicemanagement.framework.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.xerces.util.SecurityManager;
import javax.xml.parsers.SAXParser;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileInputStream;
import java.io.File;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.InputSource;

public class XMLUtils
{
    public static void validateXML(final InputSource inputSource) throws SAXException, IOException {
        final XMLReader xmlReaderreader = XMLReaderFactory.createXMLReader();
        xmlReaderreader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        xmlReaderreader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        xmlReaderreader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReaderreader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        xmlReaderreader.parse(inputSource);
    }
    
    public static void validateXML(final URL url) throws IOException, SAXException {
        validateXML(new InputSource(url.toExternalForm()));
    }
    
    public static void validateXML(final String strXml) throws SAXException, IOException {
        validateXML(new InputSource(new StringReader(strXml)));
    }
    
    public static void validateXML(final byte[] data) throws IOException, SAXException {
        validateXML(new String(data));
    }
    
    public static XMLInputFactory getXMLInputFactoryInstance() {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.supportDTD", false);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        return factory;
    }
    
    public static Transformer getTransformerInstance(final InputStream xslfileObj) throws TransformerConfigurationException {
        return getTransformerFactory().newTransformer(new StreamSource(xslfileObj));
    }
    
    public static Transformer getTransformerInstance() throws TransformerConfigurationException {
        return getTransformerFactory().newTransformer();
    }
    
    private static TransformerFactory getTransformerFactory() throws TransformerConfigurationException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        return transformerFactory;
    }
    
    public static Object getJAXBUnmarshalledObject(final String xmlContent, final Class xmlTypeClass) throws SAXException, ParserConfigurationException, JAXBException {
        final Source xmlSource = new SAXSource(getSaxFactoryForUnMarshall().newSAXParser().getXMLReader(), new InputSource(new StringReader(xmlContent)));
        final JAXBContext jc = JAXBContext.newInstance(xmlTypeClass);
        final Unmarshaller um = jc.createUnmarshaller();
        return um.unmarshal(xmlSource);
    }
    
    public static Object getJAXBUnmarshalledObject(final String xmlContent, final String xmlTypeClass) throws SAXException, ParserConfigurationException, JAXBException {
        final Source xmlSource = new SAXSource(getSaxFactoryForUnMarshall().newSAXParser().getXMLReader(), new InputSource(new StringReader(xmlContent)));
        final JAXBContext jc = JAXBContext.newInstance(xmlTypeClass);
        final Unmarshaller um = jc.createUnmarshaller();
        return um.unmarshal(xmlSource);
    }
    
    private static SAXParserFactory getSaxFactoryForUnMarshall() throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return saxParserFactory;
    }
    
    private static InputStream getInputStream(final String xmlFilePath) throws Exception {
        final File xmlFile = new File(xmlFilePath);
        InputStream inputStream;
        if (xmlFile.exists() && xmlFile.isFile()) {
            inputStream = new FileInputStream(xmlFile);
        }
        else {
            inputStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(xmlFilePath);
        }
        return inputStream;
    }
    
    public static Object getJAXBUnmarshalledObjectFromFile(final String xmlFilePath, final Class xmlTypeClass) throws Exception {
        final Source xmlSource = new SAXSource(getSaxFactoryForUnMarshall().newSAXParser().getXMLReader(), new InputSource(getInputStream(xmlFilePath)));
        final JAXBContext jc = JAXBContext.newInstance(xmlTypeClass);
        final Unmarshaller um = jc.createUnmarshaller();
        return um.unmarshal(xmlSource);
    }
    
    public static Object getJAXBUnmarshalledObjectFromFile(final String xmlFilePath, final String xmlTypeClass) throws Exception {
        final Source xmlSource = new SAXSource(getSaxFactoryForUnMarshall().newSAXParser().getXMLReader(), new InputSource(getInputStream(xmlFilePath)));
        final JAXBContext jc = JAXBContext.newInstance(xmlTypeClass);
        final Unmarshaller um = jc.createUnmarshaller();
        return um.unmarshal(xmlSource);
    }
    
    public static SAXParser getSAXParserInstance(final boolean isValidating, final boolean isNamespaceAware) throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(isValidating);
        factory.setNamespaceAware(isNamespaceAware);
        final SAXParser saxParser = factory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        final SAXParser parser = factory.newSAXParser();
        final SecurityManager manager = new SecurityManager();
        manager.setEntityExpansionLimit(1000);
        parser.setProperty("http://apache.org/xml/properties/security-manager", manager);
        return parser;
    }
    
    public static SAXParser getSAXParser() throws Exception {
        return getSAXParserInstance(true, false);
    }
    
    public static DocumentBuilder getDocumentBuilderInstance(final boolean isValidating, final boolean isNamespaceAware) throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(isValidating);
        factory.setNamespaceAware(isNamespaceAware);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        final int billionLaugh = 1000;
        final SecurityManager manager = new SecurityManager();
        manager.setEntityExpansionLimit(billionLaugh);
        factory.setAttribute("http://apache.org/xml/properties/security-manager", manager);
        return factory.newDocumentBuilder();
    }
    
    public static DocumentBuilder getDocumentBuilderInstance() throws ParserConfigurationException {
        return getDocumentBuilderInstance(false, false);
    }
}
