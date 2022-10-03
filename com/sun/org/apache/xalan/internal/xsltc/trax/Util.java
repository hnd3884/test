package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.io.Reader;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stax.StAXSource;
import org.xml.sax.XMLReader;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMSource;
import org.xml.sax.SAXNotSupportedException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import jdk.xml.internal.JdkXmlUtils;
import jdk.xml.internal.JdkXmlFeatures;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.transform.Source;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;

public final class Util
{
    private static final String property = "org.xml.sax.driver";
    
    public static String baseName(final String name) {
        return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.baseName(name);
    }
    
    public static String noExtName(final String name) {
        return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.noExtName(name);
    }
    
    public static String toJavaName(final String name) {
        return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.toJavaName(name);
    }
    
    public static InputSource getInputSource(final XSLTC xsltc, final Source source) throws TransformerConfigurationException {
        InputSource input = null;
        final String systemId = source.getSystemId();
        try {
            if (source instanceof SAXSource) {
                final SAXSource sax = (SAXSource)source;
                input = sax.getInputSource();
                try {
                    XMLReader reader = sax.getXMLReader();
                    if (reader == null) {
                        final boolean overrideDefaultParser = xsltc.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
                        reader = JdkXmlUtils.getXMLReader(overrideDefaultParser, xsltc.isSecureProcessing());
                    }
                    else {
                        reader.setFeature("http://xml.org/sax/features/namespaces", true);
                        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
                    }
                    try {
                        reader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"));
                    }
                    catch (final SAXNotRecognizedException e) {
                        XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", e);
                    }
                    String lastProperty = "";
                    try {
                        final XMLSecurityManager securityManager = (XMLSecurityManager)xsltc.getProperty("http://apache.org/xml/properties/security-manager");
                        if (securityManager != null) {
                            for (final XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
                                lastProperty = limit.apiProperty();
                                reader.setProperty(lastProperty, securityManager.getLimitValueAsString(limit));
                            }
                            if (securityManager.printEntityCountInfo()) {
                                lastProperty = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
                                reader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
                            }
                        }
                    }
                    catch (final SAXException se) {
                        XMLSecurityManager.printWarning(reader.getClass().getName(), lastProperty, se);
                    }
                    xsltc.setXMLReader(reader);
                }
                catch (final SAXNotRecognizedException snre) {
                    throw new TransformerConfigurationException("SAXNotRecognizedException ", snre);
                }
                catch (final SAXNotSupportedException snse) {
                    throw new TransformerConfigurationException("SAXNotSupportedException ", snse);
                }
            }
            else if (source instanceof DOMSource) {
                final DOMSource domsrc = (DOMSource)source;
                final Document dom = (Document)domsrc.getNode();
                final DOM2SAX dom2sax = new DOM2SAX(dom);
                xsltc.setXMLReader(dom2sax);
                input = SAXSource.sourceToInputSource(source);
                if (input == null) {
                    input = new InputSource(domsrc.getSystemId());
                }
            }
            else if (source instanceof StAXSource) {
                final StAXSource staxSource = (StAXSource)source;
                StAXEvent2SAX staxevent2sax = null;
                StAXStream2SAX staxStream2SAX = null;
                if (staxSource.getXMLEventReader() != null) {
                    final XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
                    staxevent2sax = new StAXEvent2SAX(xmlEventReader);
                    xsltc.setXMLReader(staxevent2sax);
                }
                else if (staxSource.getXMLStreamReader() != null) {
                    final XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
                    staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
                    xsltc.setXMLReader(staxStream2SAX);
                }
                input = SAXSource.sourceToInputSource(source);
                if (input == null) {
                    input = new InputSource(staxSource.getSystemId());
                }
            }
            else {
                if (!(source instanceof StreamSource)) {
                    final ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_SOURCE_ERR");
                    throw new TransformerConfigurationException(err.toString());
                }
                final StreamSource stream = (StreamSource)source;
                final InputStream istream = stream.getInputStream();
                final Reader reader2 = stream.getReader();
                xsltc.setXMLReader(null);
                if (istream != null) {
                    input = new InputSource(istream);
                }
                else if (reader2 != null) {
                    input = new InputSource(reader2);
                }
                else {
                    input = new InputSource(systemId);
                }
            }
            input.setSystemId(systemId);
        }
        catch (final NullPointerException e2) {
            final ErrorMsg err2 = new ErrorMsg("JAXP_NO_SOURCE_ERR", "TransformerFactory.newTemplates()");
            throw new TransformerConfigurationException(err2.toString());
        }
        catch (final SecurityException e3) {
            final ErrorMsg err2 = new ErrorMsg("FILE_ACCESS_ERR", systemId);
            throw new TransformerConfigurationException(err2.toString());
        }
        return input;
    }
}
