package com.adventnet.iam.security;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import org.w3c.dom.Element;
import javax.servlet.http.HttpServletRequest;
import org.xml.sax.EntityResolver;
import com.adventnet.iam.xss.XSSUtil;
import java.util.logging.Logger;

public class XMLXSSRemover
{
    static final Logger logger;
    String xssPatternName;
    String xmlString;
    XSSUtil xssUtil;
    EntityResolver dummyEntityResolver;
    private boolean allowInlineEntityExpansion;
    
    public XMLXSSRemover(final SecurityRequestWrapper request, final String xmlString, final String xssPatternName) {
        this.xssPatternName = null;
        this.xmlString = null;
        this.xssUtil = null;
        this.dummyEntityResolver = null;
        this.allowInlineEntityExpansion = false;
        this.xmlString = xmlString;
        final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        if ("escape".equalsIgnoreCase(xssPatternName) || ("throwerror".equalsIgnoreCase(xssPatternName) && filterConfig.isXSSPatternDetectEnabled())) {
            this.xssPatternName = xssPatternName;
        }
        else {
            this.xssUtil = filterConfig.getXSSUtil(xssPatternName);
            if (this.xssUtil == null) {
                throw new IAMSecurityException("XSS Pattern definition for the pattern name '" + xssPatternName + "' is not found : ");
            }
        }
        this.dummyEntityResolver = new DummyEntityResolver();
    }
    
    public XMLXSSRemover(final SecurityRequestWrapper request, final String xmlString, final String xssPatternName, final boolean allowInlineEntityExpansion) {
        this(request, xmlString, xssPatternName);
        this.allowInlineEntityExpansion = allowInlineEntityExpansion;
    }
    
    public Element filterXSS() {
        Document xmlDocument = null;
        try {
            xmlDocument = SecurityUtil.createDocumentBuilder(this.allowInlineEntityExpansion, false, null).parse(new ByteArrayInputStream(this.xmlString.getBytes()));
        }
        catch (final Exception ex) {
            XMLXSSRemover.logger.log(Level.SEVERE, "Exception occurred while parsing XML . Exception Message :\"{0}\"", ex.getMessage());
            throw new IAMSecurityException("UNABLE_TO_PARSE_DOCUMENT");
        }
        final Element root = xmlDocument.getDocumentElement();
        this.processElement(root);
        return root;
    }
    
    private void processChildNodes(final NodeList lists) {
        if (lists != null && lists.getLength() > 0) {
            for (int i = 0; i < lists.getLength(); ++i) {
                if (lists.item(i).getNodeType() == 1) {
                    final Element element = (Element)lists.item(i);
                    this.processElement(element);
                }
                else if (lists.item(i).getNodeType() == 3) {
                    this.processNode(lists.item(i));
                }
            }
        }
    }
    
    private void processNode(final Node item) {
        if (item.getNodeType() == 2) {
            item.setNodeValue(this.filterXSSInContent(item.getNodeValue()));
        }
        else if (item.getNodeType() == 3) {
            final String textContent = item.getTextContent();
            item.setTextContent(this.filterXSSInContent(textContent));
        }
    }
    
    public void processElement(final Element ele) {
        if (ele.hasAttributes()) {
            final NamedNodeMap map = ele.getAttributes();
            for (int i = 0; i < map.getLength(); ++i) {
                this.processNode(map.item(i));
            }
        }
        if (ele.hasChildNodes()) {
            this.processChildNodes(ele.getChildNodes());
        }
    }
    
    private String filterXSSInContent(final String nodeValue) {
        if (this.xssUtil != null) {
            return this.xssUtil.filterXSS(nodeValue);
        }
        if ("escape".equalsIgnoreCase(this.xssPatternName)) {
            return SecurityUtil.escapeHTMLTags(nodeValue);
        }
        if (!"throwerror".equalsIgnoreCase(this.xssPatternName)) {
            return nodeValue;
        }
        if (SecurityUtil.detectXSS(nodeValue)) {
            throw new IAMSecurityException("XSS_DETECTED");
        }
        return nodeValue;
    }
    
    static {
        logger = Logger.getLogger(XMLXSSRemover.class.getName());
    }
}
