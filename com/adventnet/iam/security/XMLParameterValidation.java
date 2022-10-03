package com.adventnet.iam.security;

import java.util.Hashtable;
import com.adventnet.iam.xss.XSSUtil;
import org.json.JSONException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import javax.xml.validation.Validator;
import java.util.Collection;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import org.w3c.dom.Element;
import java.util.logging.Logger;

public class XMLParameterValidation
{
    private static final Logger LOGGER;
    private String xmlString;
    private String xmlSchemaName;
    private String contextName;
    private boolean allowInlineEntityExpansion;
    private static final String CLEARTEXT_PATTERN_NAME = "cleartextpattern";
    
    public XMLParameterValidation(final String contextName, final String xmlString) {
        this.allowInlineEntityExpansion = false;
        this.contextName = contextName;
        this.xmlString = xmlString;
    }
    
    public XMLParameterValidation(final String contextName, final String xmlString, final boolean allowInlineEntityExpansion) {
        this(contextName, xmlString);
        this.allowInlineEntityExpansion = allowInlineEntityExpansion;
    }
    
    public XMLParameterValidation(final String contextName, final String xmlString, final boolean allowInlineEntityExpansion, final String xmlSchemaName) {
        this.allowInlineEntityExpansion = false;
        this.contextName = contextName;
        this.xmlString = xmlString;
        this.allowInlineEntityExpansion = allowInlineEntityExpansion;
        this.xmlSchemaName = xmlSchemaName;
    }
    
    public Element validateXML() {
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("http://xml.org/sax/features/namespaces", true);
        ((Hashtable<String, Boolean>)properties).put("http://apache.org/xml/features/include-comments", false);
        Document xmlDocument = null;
        try {
            xmlDocument = SecurityUtil.createDocumentBuilder(this.allowInlineEntityExpansion, false, properties).parse(new ByteArrayInputStream(this.xmlString.getBytes()));
        }
        catch (final SAXParseException e) {
            XMLParameterValidation.LOGGER.log(Level.SEVERE, "Exception occurred while parsing XML . Exception Message :\"{0}\" Line Number: \"{1}\" Column Number: \"{2}\"", new Object[] { e.getMessage(), e.getLineNumber(), e.getColumnNumber() });
            throw new IAMSecurityException("UNABLE_TO_PARSE_DOCUMENT", e.getMessage(), e.getLineNumber(), e.getColumnNumber());
        }
        catch (final Exception ex) {
            XMLParameterValidation.LOGGER.log(Level.SEVERE, "Exception occurred while parsing XML . Exception Message :\"{0}\"", ex.getMessage());
            throw new IAMSecurityException("UNABLE_TO_PARSE_DOCUMENT");
        }
        this.xmlSchemaValidation(xmlDocument);
        return xmlDocument.getDocumentElement();
    }
    
    private void xmlSchemaValidation(final Document xmlDocument) {
        Schema schemaObj;
        Collection<XSDElementRule> xssOrJsonTypeElementRules;
        if (this.xmlSchemaName != null) {
            final XMLSchemaRule xmlSchemaRule = SecurityFilterProperties.getInstance(this.contextName).getXMLSchemaRule(this.xmlSchemaName);
            if (xmlSchemaRule == null) {
                XMLParameterValidation.LOGGER.log(Level.SEVERE, "XML schema rule not defined for xml-schema name : \"{0}\"", this.xmlSchemaName);
                throw new IAMSecurityException("XML_SCHEMA_RULE_NOT_DEFINED");
            }
            schemaObj = xmlSchemaRule.getSchemaObj();
            xssOrJsonTypeElementRules = xmlSchemaRule.getXssOrJsonTypeElementRules();
        }
        else {
            final String rootElem = xmlDocument.getDocumentElement().getNodeName();
            final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(this.contextName);
            schemaObj = filterProps.getXMLSchema(rootElem);
            if (schemaObj == null) {
                XMLParameterValidation.LOGGER.log(Level.SEVERE, "Schema not defined for the XML has root \"{0}\"", rootElem);
                throw new IAMSecurityException("SCHEMA_NOT_DEFINED");
            }
            xssOrJsonTypeElementRules = filterProps.getXMLSchemaFilterElements(rootElem);
        }
        try {
            final Validator validator = schemaObj.newValidator();
            validator.validate(new DOMSource(xmlDocument));
        }
        catch (final Exception ex) {
            XMLParameterValidation.LOGGER.log(Level.SEVERE, "Improper XML parameter passed. Exception Message: {0}", ex.getMessage());
            throw new IAMSecurityException("XML_VALIDATION_ERROR");
        }
        this.validateFilterElementsAndAttributes(xmlDocument, xssOrJsonTypeElementRules);
    }
    
    private void validateFilterElementsAndAttributes(final Document xmlDocument, final Collection<XSDElementRule> xssOrJsonTypeElementRules) {
        if (xssOrJsonTypeElementRules != null) {
            for (final XSDElementRule elementRule : xssOrJsonTypeElementRules) {
                final NodeList xmlElementNodeList = xmlDocument.getElementsByTagName(elementRule.getName());
                for (int i = 0; i < xmlElementNodeList.getLength(); ++i) {
                    final Node xmlNode = xmlElementNodeList.item(i);
                    if (xmlNode.getNodeType() == 1) {
                        if (elementRule.getType() != null) {
                            this.validateElementValue(xmlNode, elementRule);
                        }
                        if (elementRule.getAttributeRules() != null && xmlNode.hasAttributes()) {
                            final Element xmlElement = (Element)xmlNode;
                            for (final XSDElementRule attributeRule : elementRule.getAttributeRules()) {
                                final String attributeName = attributeRule.getName();
                                if (xmlElement.hasAttribute(attributeName)) {
                                    final String resultAttributeValue = this.validateContent(attributeName, xmlElement.getAttribute(attributeName), attributeRule);
                                    xmlElement.setAttribute(attributeName, resultAttributeValue);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void validateElementValue(final Node xmlNode, final XSDElementRule elementRule) {
        if (xmlNode.hasChildNodes()) {
            final NodeList childNodes = xmlNode.getChildNodes();
            if (childNodes != null) {
                final String nodeName = xmlNode.getNodeName();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    final Node childNode = childNodes.item(i);
                    if (childNode.getNodeType() == 4) {
                        final String value = ((CDATASection)childNode).getData();
                        if (elementRule.getIsApplyFilterToCDATA()) {
                            final String result = this.validateContent(nodeName, value, elementRule);
                            childNode.setTextContent(result);
                        }
                        else if (SecurityUtil.detectXSS(value)) {
                            if ("throwerror".equals(elementRule.getType())) {
                                throw new IAMSecurityException("XSS_DETECTED");
                            }
                            XMLParameterValidation.LOGGER.log(Level.FINE, "XSS Detected in CDATA. CDATA Content is removed");
                            xmlNode.removeChild(childNode);
                        }
                    }
                    if (childNode.getNodeType() == 3) {
                        final String result2 = this.validateContent(nodeName, childNode.getTextContent(), elementRule);
                        childNode.setTextContent(result2);
                    }
                }
            }
        }
    }
    
    private String validateContent(final String name, final String value, final XSDElementRule elementRule) {
        final String type = elementRule.getType();
        Label_0105: {
            if (!"JSONObject".equals(type)) {
                if (!"JSONArray".equals(type)) {
                    break Label_0105;
                }
            }
            try {
                if ("JSONObject".equals(type)) {
                    return JSONTemplateRule.validateJSONObject(name, value, elementRule.getTemplate()).toString();
                }
                return JSONTemplateRule.validateJSONArray(name, value, elementRule.getTemplate()).toString();
            }
            catch (final JSONException e) {
                XMLParameterValidation.LOGGER.log(Level.SEVERE, "JSON parse error - XML Parameter Validation :: XML Element/Attribute Name : {0}, type : {1}", new Object[] { name, elementRule.getType() });
                throw new IAMSecurityException("JSON_PARSE_ERROR");
            }
        }
        final SecurityFilterProperties secFilterProps = SecurityFilterProperties.getInstance(this.contextName);
        if (checkForClearTextPattern(this.contextName, value)) {
            XMLParameterValidation.LOGGER.log(Level.FINE, "XSS DETECT/FILTERING NOT NEEDED AS CLEARTEXT  PARAM : {0} ", name);
            return value;
        }
        if (secFilterProps.isXSSPatternDetectEnabled() && ("throwerror".equalsIgnoreCase(type) || "throw".equals(type))) {
            if (SecurityUtil.detectXSS(value, secFilterProps.isEnableXSSTimeoutMatcher())) {
                XMLParameterValidation.LOGGER.log(Level.FINE, "XSS DETECTED  PARAM_NAME : {0}", new Object[] { name });
                throw new IAMSecurityException("XSS_DETECTED");
            }
            return value;
        }
        else {
            final XSSUtil xssUtil = secFilterProps.getXSSUtil(type);
            if (xssUtil == null) {
                XMLParameterValidation.LOGGER.log(Level.SEVERE, "Invalid XSS filter configuation - XML Parameter validation :: XML Element/Attribute Name : {0} , XSSPatternName : {1}", new Object[] { name, type });
                throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
            }
            if (!secFilterProps.isXSSPatternDetectEnabled() || xssUtil.balanceHtmlTags() || secFilterProps.isXSSPatternDetectForFilterDisabled() || SecurityUtil.detectXSS(value, secFilterProps.isEnableXSSTimeoutMatcher())) {
                return SecurityUtil.filterXSS(this.contextName, value, type, null);
            }
            return value;
        }
    }
    
    private static boolean checkForClearTextPattern(final String contextName, final String value) {
        final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance(contextName);
        final RegexRule regexRule = filterConfig.getRegexRule("cleartextpattern");
        if (regexRule == null) {
            XMLParameterValidation.LOGGER.log(Level.SEVERE, "Pattern is not defined for the regex : {0}.", "cleartextpattern");
            throw new IAMSecurityException("PATTERN_NOT_DEFINED");
        }
        return SecurityUtil.matchPattern(value, regexRule);
    }
    
    static {
        LOGGER = Logger.getLogger(XMLParameterValidation.class.getName());
    }
}
