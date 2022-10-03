package com.adventnet.iam.security;

import java.util.Collection;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import java.util.ArrayList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSParticle;
import java.util.List;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XSLoaderImpl;
import javax.xml.validation.SchemaFactory;
import java.util.logging.Level;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSElementDeclaration;
import java.io.File;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.util.logging.Logger;
import java.util.Map;
import javax.xml.validation.Schema;

public class XMLSchemaRule
{
    private String name;
    private String schemaFile;
    private Schema schemaObj;
    private Map<String, XSDElementRule> xssOrJsonTypeElementsMap;
    private static final Logger LOGGER;
    private static final int MAX_RECURSION = 10;
    
    public XMLSchemaRule(final Element element) {
        this.xssOrJsonTypeElementsMap = new HashMap<String, XSDElementRule>();
        this.setName(SecurityUtil.getValidValue(element.getAttribute("name"), null));
        final String schemaFile = element.getAttribute("schema-file");
        if (!SecurityUtil.isValid(schemaFile)) {
            throw new RuntimeException("schema-file attribute value is empty/invalid in xml-schema rule : '" + this.name + "'");
        }
        SecurityFilterProperties.validateFileName(schemaFile, "schema", "xsd");
        this.schemaFile = schemaFile;
    }
    
    void initialiseXMLSchema(final SecurityFilterProperties filterProps) {
        final String xsdFile = SecurityUtil.getSecurityConfigurationDir() + File.separator + this.schemaFile;
        final boolean enabledXMLSchemaVersion11 = filterProps.isEnabledXMLSchemaVersion11();
        this.schemaObj = parseXMLSchema(new File(xsdFile), enabledXMLSchemaVersion11, filterProps.isEnabledXSD11CTAFullXpathChecking());
        final XSNamedMap xsMap = getElementDeclarationsAsMap(xsdFile, enabledXMLSchemaVersion11);
        if (xsMap.getLength() == 0) {
            return;
        }
        if (xsMap.getLength() > 1) {
            throw new RuntimeException("More than one root element is not allowed in xsd :'" + xsdFile + "' in xml-schema rule : '" + this.name + "'");
        }
        final XSElementDeclaration elementDecl = (XSElementDeclaration)xsMap.item(0);
        final int recursionCount = 0;
        processElementDecl(elementDecl, this.xssOrJsonTypeElementsMap, recursionCount);
    }
    
    private static Schema parseXMLSchema(final File xsdFile, final boolean xmlSchemaVersion11, final boolean ctaFullXPathChecking) {
        try {
            final SchemaFactory schemaFactory = xmlSchemaVersion11 ? SecurityUtil.createXMLSchema11Factory(ctaFullXPathChecking) : SecurityUtil.createXMLSchemaFactory();
            return schemaFactory.newSchema(xsdFile);
        }
        catch (final Exception ex) {
            XMLSchemaRule.LOGGER.log(Level.SEVERE, "Error while parsing xml schema : {0} , enabledXmlSchemaVersion1.1 : \"{1}\",  ctaFullXPathChecking : \"{2}\" and Exception Message : \"{3}\"", new Object[] { xsdFile.getPath(), xmlSchemaVersion11, ctaFullXPathChecking, ex.getMessage() });
            throw new RuntimeException("INVALID_SCHEMA");
        }
    }
    
    private static XSNamedMap getElementDeclarationsAsMap(final String xsdFile, final boolean xmlSchemaVersion11) {
        XMLSchemaRule.LOGGER.log(Level.FINE, "Parsing schema file :: {0}", new Object[] { xsdFile });
        final XSLoaderImpl xsLoader = new XSLoaderImpl();
        if (xmlSchemaVersion11) {
            xsLoader.setParameter("http://apache.org/xml/properties/validation/schema/version", (Object)Constants.W3C_XML_SCHEMA11_NS_URI);
        }
        final XSModel xsModel = xsLoader.loadURI(xsdFile);
        return xsModel.getComponents((short)2);
    }
    
    static void initialiseXMLSchema(final SecurityFilterProperties filterProps, final File schemaFile) {
        final boolean enabledXMLSchemaVersion11 = filterProps.isEnabledXMLSchemaVersion11();
        final Schema schemaObj = parseXMLSchema(schemaFile, enabledXMLSchemaVersion11, filterProps.isEnabledXSD11CTAFullXpathChecking());
        final XSNamedMap xsMap = getElementDeclarationsAsMap(schemaFile.getAbsolutePath(), enabledXMLSchemaVersion11);
        for (int i = 0; i < xsMap.getLength(); ++i) {
            final XSElementDeclaration elementDecl = (XSElementDeclaration)xsMap.item(i);
            XMLSchemaRule.LOGGER.log(Level.FINE, "Element name ::" + elementDecl.getName());
            final Map<String, XSDElementRule> elementRuleMap = new HashMap<String, XSDElementRule>();
            final int recursionCount = 0;
            processElementDecl(elementDecl, elementRuleMap, recursionCount);
            final String rootElement = elementDecl.getName();
            filterProps.addXMLSchema(rootElement, schemaObj);
            filterProps.addXMLSchemaFilterElements(rootElement, elementRuleMap.values());
        }
    }
    
    static void processElementDecl(final XSElementDeclaration elementDecl, final Map<String, XSDElementRule> elementRuleMap, int recursionCount) {
        final XSTypeDefinition elementType = elementDecl.getTypeDefinition();
        Label_0383: {
            switch (elementType.getTypeCategory()) {
                case 16: {
                    final String type = (elementType.getName() == null) ? elementType.getBaseType().getName() : elementType.getName();
                    if (isXssOrJsonType(type)) {
                        final XSDElementRule elementRule = getXSDElementRule(elementDecl, type);
                        addElementRule(elementRule, elementRuleMap);
                    }
                    recursionCount = 0;
                    break;
                }
                case 15: {
                    final XSComplexTypeDefinition complexType = (XSComplexTypeDefinition)elementType;
                    final List<XSDElementRule> attributeRulesList = processAttributeDeclaration(complexType);
                    switch (complexType.getContentType()) {
                        case 1: {
                            final String datatype = complexType.getSimpleType().getName();
                            XSDElementRule elementRule2 = null;
                            if (isXssOrJsonType(datatype)) {
                                elementRule2 = getXSDElementRule(elementDecl, datatype);
                            }
                            else if (attributeRulesList.size() > 0) {
                                elementRule2 = new XSDElementRule(elementDecl.getName());
                            }
                            if (elementRule2 != null) {
                                elementRule2.setAttributeRules(attributeRulesList);
                                addElementRule(elementRule2, elementRuleMap);
                            }
                            recursionCount = 0;
                            break Label_0383;
                        }
                        case 2: {
                            if (++recursionCount > 10) {
                                XMLSchemaRule.LOGGER.log(Level.SEVERE, "Detected maximum recursive elements. Element :: {0}, Recursion Count :: {1}", new Object[] { elementDecl.getName(), recursionCount });
                                break Label_0383;
                            }
                            if (attributeRulesList.size() > 0) {
                                final XSDElementRule elemRule = new XSDElementRule(elementDecl.getName());
                                elemRule.setAttributeRules(attributeRulesList);
                                addElementRule(elemRule, elementRuleMap);
                            }
                            processXSParticle(complexType.getParticle(), elementRuleMap, recursionCount);
                            break Label_0383;
                        }
                        case 0: {
                            if (attributeRulesList.size() > 0) {
                                final XSDElementRule elemRule = new XSDElementRule(elementDecl.getName());
                                elemRule.setAttributeRules(attributeRulesList);
                                addElementRule(elemRule, elementRuleMap);
                            }
                            recursionCount = 0;
                            break Label_0383;
                        }
                        case 3: {
                            throw new RuntimeException("Complex Type with mixed content 'mixed=\"true\"' not supported in xml schema");
                        }
                    }
                    break;
                }
            }
        }
    }
    
    private static void processXSParticle(final XSParticle xsParticle, final Map<String, XSDElementRule> elementRuleMap, final int recursionCount) {
        final XSTerm xsTerm = xsParticle.getTerm();
        switch (xsTerm.getType()) {
            case 2: {
                processElementDecl((XSElementDeclaration)xsTerm, elementRuleMap, recursionCount);
                break;
            }
            case 7: {
                final XSModelGroup xsGroup = (XSModelGroup)xsTerm;
                final XSObjectList xsParticleList = xsGroup.getParticles();
                for (int i = 0; i < xsParticleList.getLength(); ++i) {
                    processXSParticle((XSParticle)xsParticleList.item(i), elementRuleMap, recursionCount);
                }
                break;
            }
        }
    }
    
    private static List<XSDElementRule> processAttributeDeclaration(final XSComplexTypeDefinition complexType) {
        final List<XSDElementRule> attributeRulesList = new ArrayList<XSDElementRule>();
        final XSObjectList xsAttributeList = complexType.getAttributeUses();
        for (int i = 0; i < xsAttributeList.getLength(); ++i) {
            final XSAttributeUse xsAttribute = (XSAttributeUse)xsAttributeList.item(i);
            final XSAttributeDeclaration attributeDecl = xsAttribute.getAttrDeclaration();
            final String type = attributeDecl.getTypeDefinition().getName();
            if (isXssOrJsonType(type)) {
                final XSDElementRule attributeRule = new XSDElementRule(attributeDecl.getName(), type);
                final String annotationStr = getAnnotationString(attributeDecl.getAnnotations());
                if (annotationStr != null && annotationStr.indexOf("zs:template=") > 0) {
                    final String templateName = getJsonTemplateName(annotationStr);
                    attributeRule.setTemplate(templateName);
                }
                attributeRule.validateConfiguration();
                attributeRulesList.add(attributeRule);
            }
        }
        return attributeRulesList;
    }
    
    private static boolean isXssOrJsonType(final String type) {
        return type != null && ("throwerror".equals(type) || type.indexOf("htmlfilter") != -1 || type.indexOf("antisamyfilter") != -1 || "JSONObject".equals(type) || "JSONArray".equals(type));
    }
    
    private static void addElementRule(final XSDElementRule elementRule, final Map<String, XSDElementRule> elementRuleMap) {
        if (!elementRuleMap.containsKey(elementRule.getName())) {
            elementRuleMap.put(elementRule.getName(), elementRule);
        }
        else {
            XMLSchemaRule.LOGGER.log(Level.SEVERE, "Element Name '" + elementRule.getName() + "' already defined in schema");
        }
    }
    
    private static XSDElementRule getXSDElementRule(final XSElementDeclaration elementDecl, final String type) {
        final XSDElementRule elementRule = new XSDElementRule(elementDecl.getName(), type);
        final String annotation = getAnnotationString(elementDecl.getAnnotations());
        if (annotation != null && annotation.indexOf("zs:template") > 0) {
            final String templateName = getJsonTemplateName(annotation);
            elementRule.setTemplate(templateName);
        }
        if (annotation != null && annotation.contains("zs:filterCDATA=\"true\"")) {
            elementRule.setIsApplyFilterToCDATA(true);
        }
        elementRule.validateConfiguration();
        return elementRule;
    }
    
    private static String getAnnotationString(final XSObjectList annotations) {
        if (annotations.size() > 0) {
            final XSAnnotation annotation = (XSAnnotation)annotations.get(0);
            return annotation.getAnnotationString();
        }
        return null;
    }
    
    private static String getJsonTemplateName(final String annotationStr) {
        Document document = null;
        try {
            document = SecurityUtil.getDocumentBuilder().parse(new ByteArrayInputStream(annotationStr.getBytes()));
        }
        catch (final Exception e) {
            throw new RuntimeException("Unable to parse Annotation String ::" + annotationStr);
        }
        final Element element = document.getDocumentElement();
        return element.getAttribute("zs:template");
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getSchemaFile() {
        return this.schemaFile;
    }
    
    public void setSchemaFile(final String schemaFile) {
        this.schemaFile = schemaFile;
    }
    
    public Schema getSchemaObj() {
        return this.schemaObj;
    }
    
    public void setSchemaObj(final Schema schemaObj) {
        this.schemaObj = schemaObj;
    }
    
    public Collection<XSDElementRule> getXssOrJsonTypeElementRules() {
        return this.xssOrJsonTypeElementsMap.values();
    }
    
    @Override
    public String toString() {
        return "XMLSchemaRule :: schemaName : \"" + this.name + "\" schemaFile : \"" + this.schemaFile;
    }
    
    static {
        LOGGER = Logger.getLogger(XMLSchemaRule.class.getName());
    }
}
