package com.adventnet.iam.xss;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import org.w3c.tidy.TidyMessageListener;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.iam.security.IAMSecurityException;
import org.w3c.tidy.Tidy;
import java.util.logging.Logger;

public class JTidyXSSFilter extends XSSFilter
{
    public static final Logger LOGGER;
    private Tidy tidyParse;
    
    public JTidyXSSFilter() {
        this.tidyParse = null;
    }
    
    @Override
    void SET_ALTERED() {
        if (!this.altered) {
            this.altered = true;
            if (this.xssFilterConfig.xssDetectThrowError) {
                throw new IAMSecurityException("XSS_DETECTED");
            }
        }
    }
    
    @Override
    public String filterXSS(final String domain, final String value, final String encoding) {
        if (this.xssFilterConfig == null) {
            this.logger.log(Level.SEVERE, "No XSS Filter Configuration for - {0}", XSSUtil.getLogString(value));
            return value;
        }
        return this.filterOrBalanceHtmlContent(domain, value, true);
    }
    
    @Override
    void init(final Properties prop, final XSSFilterConfiguration xssFilterConfig) {
        (this.tidyParse = new Tidy()).setTidyMark(false);
        this.tidyParse.setXHTML(false);
        this.tidyParse.setTrimEmptyElements(false);
        this.tidyParse.setPrintBodyOnly(false);
        this.tidyParse.setDocType("omit");
        this.tidyParse.setForceOutput(true);
        this.tidyParse.setXmlPIs(false);
        this.tidyParse.setShowWarnings(true);
        this.tidyParse.setQuiet(true);
        this.tidyParse.setInputEncoding("UTF-8");
        final JTidyErrorWriter jerrWriter = new JTidyErrorWriter(System.err, true, this, prop.getProperty("jtidy-critical-parser-errorcodes"));
        this.tidyParse.setMessageListener((TidyMessageListener)jerrWriter);
        this.tidyParse.setErrout((PrintWriter)jerrWriter);
        super.init(prop, xssFilterConfig);
    }
    
    public String checkAndReplaceTags(String value, String response) {
        if (value != null) {
            value = value.toLowerCase();
            for (final TAGS tag : TAGS.values()) {
                if (!value.contains(tag.startTag())) {
                    response = (response.contains(tag.startTag()) ? response.replaceAll(tag.startTag(), "") : response);
                    response = (response.contains(tag.endTag()) ? response.replaceAll(tag.endTag(), "") : response);
                }
            }
            return response.trim();
        }
        return null;
    }
    
    public String filterOrBalanceHtmlContent(final String domain, final String value, final boolean isFilter) {
        try {
            final Document document = this.tidyParse.parseDOM((InputStream)new ByteArrayInputStream(value.getBytes()), (OutputStream)new ByteArrayOutputStream());
            if (document != null) {
                if (isFilter) {
                    this.removeNode(document, (short)7);
                    this.parseContentAndFilterXSS(XSSUtil.isEnableTrustedDomainScriptTags() ? domain : null, document);
                }
                final ByteArrayOutputStream responseOut = new ByteArrayOutputStream();
                this.tidyParse.pprint(document, (OutputStream)responseOut);
                final String response = this.checkAndReplaceTags(value, responseOut.toString());
                JTidyXSSFilter.LOGGER.log(Level.FINE, "The filtered String is {0}", response);
                return response;
            }
        }
        catch (final Exception e) {
            if (e.getClass().getSimpleName().equals("IAMSecurityException")) {
                final IAMSecurityException ise = (IAMSecurityException)e;
                if (ise.getErrorCode().equals("XSS_DETECTED")) {
                    throw ise;
                }
            }
            JTidyXSSFilter.LOGGER.log(Level.WARNING, "Exception at JTidyXSSFilter - {0}", new Object[] { e.getMessage() });
        }
        return null;
    }
    
    public void removeNode(final Node node, final short removeNodeType) {
        if (node.hasChildNodes()) {
            final Node[] lc = convertNodeListToArray(node.getChildNodes());
            for (int i = 0; i < lc.length; ++i) {
                final Node chnode = lc[i];
                if (chnode.getNodeType() == removeNodeType) {
                    final String nodeValue = chnode.getNodeValue();
                    node.removeChild(chnode);
                    this.ELEMENT_REMOVED(nodeValue);
                    JTidyXSSFilter.LOGGER.log(Level.FINE, "Removing Porcessing Instruction Node - {0} ", new Object[] { nodeValue });
                }
                else if (chnode.hasChildNodes()) {
                    this.removeNode(chnode, removeNodeType);
                }
            }
        }
    }
    
    public static Node[] convertNodeListToArray(final NodeList list) {
        final int length = list.getLength();
        final Node[] copy = new Node[length];
        for (int n = 0; n < length; ++n) {
            copy[n] = list.item(n);
        }
        return copy;
    }
    
    public void parseContentAndFilterXSS(final String domain, final Node parentNode) {
        final Node[] lc = convertNodeListToArray(parentNode.getChildNodes());
        for (int k = 0; k < lc.length; ++k) {
            final Node node = lc[k];
            JTidyXSSFilter.LOGGER.log(Level.FINE, "Child Node - {0}  Value - {1}  Type - {2} ", new Object[] { node.getNodeName(), node.getNodeValue(), node.getNodeType() });
            if (node != null) {
                final String content = node.getNodeValue();
                boolean isEntered = false;
                if (content != null) {
                    final short type = node.getNodeType();
                    String value = content;
                    if (type == 3) {
                        isEntered = true;
                        value = ((node.getParentNode().getNodeName().equalsIgnoreCase("style") && this.cssUtil != null) ? this.cssUtil.cleanStyleSheet(value) : value);
                        if (value == null) {
                            JTidyXSSFilter.LOGGER.log(Level.FINE, "Removing style Element");
                            parentNode.removeChild(node);
                            this.ELEMENT_REMOVED("style");
                            continue;
                        }
                    }
                    if (type == 8 || type == 4) {
                        isEntered = true;
                        final boolean removeContent = (type == 8) ? this.xssFilterConfig.removeComments : this.xssFilterConfig.removeCData;
                        if (removeContent || XSSUtil.detect(value, false)) {
                            JTidyXSSFilter.LOGGER.log(Level.FINE, "Removing Content.");
                            parentNode.removeChild(node);
                            this.ELEMENT_REMOVED(node.getNodeName());
                            continue;
                        }
                    }
                    else {
                        final boolean isXss = this.xssFilterConfig.getRemoveElementValue(value);
                        if (isXss) {
                            value = "";
                        }
                    }
                    if (!content.equals(value)) {
                        JTidyXSSFilter.LOGGER.log(Level.FINE, "Altering Element - {0} value - {1}  ", new Object[] { node.getNodeName(), value });
                        this.SET_ALTERED();
                        node.setNodeValue(value);
                    }
                }
                if (!isEntered && node.getNodeName() != null) {
                    final String elementName = node.getNodeName().toLowerCase();
                    if ("script".equalsIgnoreCase(elementName) && domain != null) {
                        final String srcValue = ((Element)node).getAttribute("src");
                        if (this.xssFilterConfig.isAllowedScript(domain, srcValue)) {
                            continue;
                        }
                    }
                    final XSSFilterConfiguration.AttributeValue insertAttrValue = this.xssFilterConfig.getInsertAttribute(elementName);
                    if (insertAttrValue != null) {
                        JTidyXSSFilter.LOGGER.log(Level.FINE, "Inserted Attribute Element - {0}  Attribute - {1}  AttributeValue - {2} ", new Object[] { node.getNodeName(), insertAttrValue.name, insertAttrValue.value });
                        ((Element)node).setAttribute(insertAttrValue.name, insertAttrValue.value);
                        this.SET_ALTERED();
                    }
                    if (this.xssFilterConfig.isRemoveElement(elementName)) {
                        JTidyXSSFilter.LOGGER.log(Level.FINE, "From removeElementNames: Removing Element - {0}  ", elementName);
                        parentNode.removeChild(node);
                        this.ELEMENT_REMOVED(elementName);
                        continue;
                    }
                    if (node.hasAttributes()) {
                        final NamedNodeMap attributes = node.getAttributes();
                        for (int i = 0; i < attributes.getLength(); ++i) {
                            final Node attribute = attributes.item(i);
                            final String attrname = attribute.getNodeName().toLowerCase();
                            String attrvalue = attribute.getNodeValue().toLowerCase();
                            final int removeType = this.xssFilterConfig.removeXSSElementOrAttribute(elementName, attrname, attrvalue);
                            if (removeType == 2) {
                                JTidyXSSFilter.LOGGER.log(Level.FINE, "Removing Attribute: Element - {0}  Attribute - {1}  AttributeValue - {2} ", new Object[] { elementName, attrname, attrvalue });
                                ((Element)node).removeAttribute(attrname);
                                this.SET_ALTERED();
                            }
                            else {
                                if (removeType == 1) {
                                    JTidyXSSFilter.LOGGER.log(Level.FINE, "Removing Element - {0}  ", elementName);
                                    parentNode.removeChild(node);
                                    this.ELEMENT_REMOVED(elementName);
                                    break;
                                }
                                if (attrname.equals("style") && this.cssUtil != null) {
                                    attrvalue = this.cssUtil.cleanStyleDeclaration(attrvalue);
                                    if (attrvalue != null) {
                                        JTidyXSSFilter.LOGGER.log(Level.FINE, "Altering style attribute - {0}  ", attrvalue);
                                        ((Element)node).setAttribute(attrname, attrvalue);
                                    }
                                    else {
                                        JTidyXSSFilter.LOGGER.log(Level.FINE, "Removing style attribute - {0}  ", attrvalue);
                                        ((Element)node).removeAttribute(attrname);
                                    }
                                }
                            }
                        }
                    }
                }
                if (node.hasChildNodes()) {
                    this.parseContentAndFilterXSS(domain, node);
                }
            }
        }
    }
    
    @Override
    public String balanceHTMLContent(final String content, final String encoding) {
        return this.filterOrBalanceHtmlContent(null, content, false);
    }
    
    @Override
    public void reset() {
        super.reset();
    }
    
    static {
        LOGGER = Logger.getLogger(JTidyXSSFilter.class.getName());
    }
    
    private enum TAGS
    {
        HTML, 
        HEAD, 
        TITLE, 
        BODY;
        
        public String startTag() {
            return "<" + super.toString().toLowerCase() + ">";
        }
        
        public String endTag() {
            return "</" + super.toString().toLowerCase() + ">";
        }
    }
}
