package org.apache.lucene.queryparser.xml;

import java.util.Enumeration;
import org.w3c.dom.Element;
import javax.xml.transform.Transformer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.Result;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerException;
import java.util.Properties;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.InputStream;
import javax.xml.transform.Templates;
import java.util.HashMap;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilderFactory;

public class QueryTemplateManager
{
    static final DocumentBuilderFactory dbf;
    static final TransformerFactory tFactory;
    HashMap<String, Templates> compiledTemplatesCache;
    Templates defaultCompiledTemplates;
    
    public QueryTemplateManager() {
        this.compiledTemplatesCache = new HashMap<String, Templates>();
        this.defaultCompiledTemplates = null;
    }
    
    public QueryTemplateManager(final InputStream xslIs) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        this.compiledTemplatesCache = new HashMap<String, Templates>();
        this.defaultCompiledTemplates = null;
        this.addDefaultQueryTemplate(xslIs);
    }
    
    public void addDefaultQueryTemplate(final InputStream xslIs) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        this.defaultCompiledTemplates = getTemplates(xslIs);
    }
    
    public void addQueryTemplate(final String name, final InputStream xslIs) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        this.compiledTemplatesCache.put(name, getTemplates(xslIs));
    }
    
    public String getQueryAsXmlString(final Properties formProperties, final String queryTemplateName) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        final Templates ts = this.compiledTemplatesCache.get(queryTemplateName);
        return getQueryAsXmlString(formProperties, ts);
    }
    
    public Document getQueryAsDOM(final Properties formProperties, final String queryTemplateName) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        final Templates ts = this.compiledTemplatesCache.get(queryTemplateName);
        return getQueryAsDOM(formProperties, ts);
    }
    
    public String getQueryAsXmlString(final Properties formProperties) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        return getQueryAsXmlString(formProperties, this.defaultCompiledTemplates);
    }
    
    public Document getQueryAsDOM(final Properties formProperties) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        return getQueryAsDOM(formProperties, this.defaultCompiledTemplates);
    }
    
    public static String getQueryAsXmlString(final Properties formProperties, final Templates template) throws ParserConfigurationException, TransformerException {
        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);
        transformCriteria(formProperties, template, result);
        return writer.toString();
    }
    
    public static String getQueryAsXmlString(final Properties formProperties, final InputStream xslIs) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);
        transformCriteria(formProperties, xslIs, result);
        return writer.toString();
    }
    
    public static Document getQueryAsDOM(final Properties formProperties, final Templates template) throws ParserConfigurationException, TransformerException {
        final DOMResult result = new DOMResult();
        transformCriteria(formProperties, template, result);
        return (Document)result.getNode();
    }
    
    public static Document getQueryAsDOM(final Properties formProperties, final InputStream xslIs) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        final DOMResult result = new DOMResult();
        transformCriteria(formProperties, xslIs, result);
        return (Document)result.getNode();
    }
    
    public static void transformCriteria(final Properties formProperties, final InputStream xslIs, final Result result) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        QueryTemplateManager.dbf.setNamespaceAware(true);
        final DocumentBuilder builder = QueryTemplateManager.dbf.newDocumentBuilder();
        final Document xslDoc = builder.parse(xslIs);
        final DOMSource ds = new DOMSource(xslDoc);
        Transformer transformer = null;
        synchronized (QueryTemplateManager.tFactory) {
            transformer = QueryTemplateManager.tFactory.newTransformer(ds);
        }
        transformCriteria(formProperties, transformer, result);
    }
    
    public static void transformCriteria(final Properties formProperties, final Templates template, final Result result) throws ParserConfigurationException, TransformerException {
        transformCriteria(formProperties, template.newTransformer(), result);
    }
    
    public static void transformCriteria(final Properties formProperties, final Transformer transformer, final Result result) throws ParserConfigurationException, TransformerException {
        QueryTemplateManager.dbf.setNamespaceAware(true);
        final DocumentBuilder db = QueryTemplateManager.dbf.newDocumentBuilder();
        final Document doc = db.newDocument();
        final Element root = doc.createElement("Document");
        doc.appendChild(root);
        final Enumeration<?> keysEnum = formProperties.propertyNames();
        while (keysEnum.hasMoreElements()) {
            final String propName = keysEnum.nextElement().toString();
            final String value = formProperties.getProperty(propName);
            if (value != null && value.length() > 0) {
                DOMUtils.insertChild(root, propName, value);
            }
        }
        final DOMSource xml = new DOMSource(doc);
        transformer.transform(xml, result);
    }
    
    public static Templates getTemplates(final InputStream xslIs) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {
        QueryTemplateManager.dbf.setNamespaceAware(true);
        final DocumentBuilder builder = QueryTemplateManager.dbf.newDocumentBuilder();
        final Document xslDoc = builder.parse(xslIs);
        final DOMSource ds = new DOMSource(xslDoc);
        return QueryTemplateManager.tFactory.newTemplates(ds);
    }
    
    static {
        dbf = DocumentBuilderFactory.newInstance();
        tFactory = TransformerFactory.newInstance();
    }
}
