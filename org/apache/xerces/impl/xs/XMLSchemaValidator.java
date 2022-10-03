package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.xs.EqualityHelper;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSObjectList;
import java.util.ArrayList;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.URI;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.XSValue;
import org.apache.xerces.util.AugmentationsImpl;
import java.util.Iterator;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.XPathMatcher;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.impl.xs.identity.Selector;
import org.apache.xerces.xni.parser.XMLParseException;
import java.util.Vector;
import org.apache.xerces.impl.xs.assertion.XSAssertConstants;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.impl.xs.identity.FieldActivator;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.dv.xs.TypeValidatorHelper;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xs.XSTypeDefinition;
import javax.xml.namespace.QName;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLComponent;

public class XMLSchemaValidator extends XMLSchemaValidatorBase implements XMLComponent, XMLDocumentFilter, RevalidationHandler
{
    public String[] getRecognizedFeatures() {
        return XMLSchemaValidator.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
    }
    
    public String[] getRecognizedProperties() {
        return XMLSchemaValidator.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/properties/validation/schema/root-type-definition")) {
            if (o == null) {
                this.fRootTypeQName = null;
                this.fRootTypeDefinition = null;
            }
            else if (o instanceof QName) {
                this.fRootTypeQName = (QName)o;
                this.fRootTypeDefinition = null;
            }
            else {
                this.fRootTypeDefinition = (XSTypeDefinition)o;
                this.fRootTypeQName = null;
            }
        }
        else if (s.equals("http://apache.org/xml/properties/validation/schema/root-element-declaration")) {
            if (o == null) {
                this.fRootElementDeclQName = null;
                this.fRootElementDeclaration = null;
            }
            else if (o instanceof QName) {
                this.fRootElementDeclQName = (QName)o;
                this.fRootElementDeclaration = null;
            }
            else {
                this.fRootElementDeclaration = (XSElementDecl)o;
                this.fRootElementDeclQName = null;
            }
        }
        else if (s.equals("http://apache.org/xml/properties/validation/schema/version")) {
            this.fSchemaLoader.setProperty("http://apache.org/xml/properties/validation/schema/version", o);
            this.fSchemaVersion = this.fSchemaLoader.getSchemaVersion();
            this.fXSConstraints = this.fSchemaLoader.getXSConstraints();
            if (this.fSchemaVersion == 4) {
                if (this.fIDContext == null) {
                    this.fIDContext = new IDContext();
                }
                this.fValidationState.setIDContext(this.fIDContext);
            }
            else {
                this.fValidationState.setIDContext(null);
                this.fValidationState.setDatatypeXMLVersion((short)1);
            }
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLSchemaValidator.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLSchemaValidator.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLSchemaValidator.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLSchemaValidator.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLSchemaValidator.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLSchemaValidator.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDocumentSource(final XMLDocumentSource fDocumentSource) {
        this.fDocumentSource = fDocumentSource;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startDocument(final XMLLocator fLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
        this.fValidationState.setNamespaceSupport(namespaceContext);
        this.fState4XsiType.setNamespaceSupport(namespaceContext);
        this.fState4ApplyDefault.setNamespaceSupport(namespaceContext);
        this.handleStartDocument(this.fLocator = fLocator, s);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(fLocator, s, namespaceContext, augmentations);
        }
        this.fNamespaceContext = namespaceContext;
        this.fAssertionValidator = new XSDAssertionValidator(this);
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(s, s2, s3, augmentations);
        }
        if (this.fSchemaVersion == 4 && this.fDatatypeXMLVersion == null) {
            this.fValidationState.setDatatypeXMLVersion((short)("1.0".equals(s) ? 1 : 2));
        }
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(s, s2, s3, augmentations);
        }
    }
    
    public void startElement(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        final Augmentations handleStartElement = this.handleStartElement(qName, xmlAttributes, augmentations);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(qName, xmlAttributes, handleStartElement);
        }
    }
    
    public void emptyElement(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        Augmentations augmentations2 = this.handleStartElement(qName, xmlAttributes, augmentations);
        this.fDefaultValue = null;
        if (this.fElementDepth != -2) {
            augmentations2 = this.handleEndElement(qName, augmentations2);
        }
        if (this.fDocumentHandler != null) {
            if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
                this.fDocumentHandler.emptyElement(qName, xmlAttributes, augmentations2);
            }
            else {
                this.fDocumentHandler.startElement(qName, xmlAttributes, augmentations2);
                this.fDocumentHandler.characters(this.fDefaultValue, null);
                this.fDocumentHandler.endElement(qName, augmentations2);
            }
        }
    }
    
    public void characters(XMLString handleCharacters, final Augmentations augmentations) throws XNIException {
        handleCharacters = this.handleCharacters(handleCharacters);
        if (this.fDocumentHandler != null) {
            if (this.fNormalizeData && this.fUnionType) {
                if (augmentations != null) {
                    this.fDocumentHandler.characters(this.fEmptyXMLStr, augmentations);
                }
            }
            else {
                this.fDocumentHandler.characters(handleCharacters, augmentations);
            }
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.handleIgnorableWhitespace(xmlString);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(xmlString, augmentations);
        }
    }
    
    public void endElement(final org.apache.xerces.xni.QName qName, final Augmentations augmentations) throws XNIException {
        this.fDefaultValue = null;
        final Augmentations handleEndElement = this.handleEndElement(qName, augmentations);
        if (this.fDocumentHandler != null) {
            if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
                this.fDocumentHandler.endElement(qName, handleEndElement);
            }
            else {
                this.fDocumentHandler.characters(this.fDefaultValue, null);
                this.fDocumentHandler.endElement(qName, handleEndElement);
            }
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        this.fInCDATA = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        this.fInCDATA = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        this.handleEndDocument();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augmentations);
        }
        this.fLocator = null;
        this.fAssertionValidator = null;
    }
    
    public boolean characterData(final String s, final Augmentations augmentations) {
        this.fSawText = (this.fSawText || s.length() > 0);
        if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(s, this.fWhiteSpace == 2);
            this.fBuffer.append(this.fNormalizedStr.ch, this.fNormalizedStr.offset, this.fNormalizedStr.length);
        }
        else if (this.fAppendBuffer) {
            this.fBuffer.append(s);
        }
        boolean b = true;
        if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15 && ((XSComplexTypeDecl)this.fCurrentType).fContentType == 2) {
            for (int i = 0; i < s.length(); ++i) {
                if (!XMLChar.isSpace(s.charAt(i))) {
                    b = false;
                    this.fSawCharacters = true;
                    break;
                }
            }
        }
        return b;
    }
    
    public void elementDefault(final String s) {
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        this.fEntityRef = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(s, s2, augmentations);
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fSchemaVersion == 4 && this.fCommentsAndPIsForAssert) {
            this.fAssertionValidator.comment(xmlString);
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(xmlString, augmentations);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fSchemaVersion == 4 && this.fCommentsAndPIsForAssert) {
            this.fAssertionValidator.processingInstruction(s, xmlString);
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(s, xmlString, augmentations);
        }
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        this.fEntityRef = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(s, augmentations);
        }
    }
    
    public XMLSchemaValidator() {
        this.fState4XsiType.setExtraChecking(false);
        this.fState4ApplyDefault.setFacetChecking(false);
        this.fSchemaVersion = this.fSchemaLoader.getSchemaVersion();
        this.fXSConstraints = this.fSchemaLoader.getXSConstraints();
        this.fTypeAlternativeValidator = new XSDTypeAlternativeValidator(this);
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        this.fIdConstraint = false;
        this.fLocationPairs.clear();
        this.fExpandedLocationPairs.clear();
        this.fValidationState.resetIDTables();
        this.fSchemaLoader.reset(xmlComponentManager);
        this.fCurrentElemDecl = null;
        this.fCurrentCM = null;
        this.fCurrCMState = null;
        this.fSkipValidationDepth = -1;
        this.fNFullValidationDepth = -1;
        this.fNNoneValidationDepth = -1;
        this.fElementDepth = -1;
        this.fSubElement = false;
        this.fSchemaDynamicValidation = false;
        this.fEntityRef = false;
        this.fInCDATA = false;
        this.fMatcherStack.clear();
        this.fXSIErrorReporter.reset((XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
        boolean feature;
        try {
            feature = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings");
        }
        catch (final XMLConfigurationException ex) {
            feature = true;
        }
        if (!feature) {
            this.fValidationManager.addValidationState(this.fValidationState);
            this.nodeFactory.reset();
            XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
            return;
        }
        this.nodeFactory.reset(xmlComponentManager);
        final SymbolTable symbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        if (symbolTable != this.fSymbolTable) {
            this.fSymbolTable = symbolTable;
        }
        try {
            this.fNamespaceGrowth = xmlComponentManager.getFeature("http://apache.org/xml/features/namespace-growth");
        }
        catch (final XMLConfigurationException ex2) {
            this.fNamespaceGrowth = false;
        }
        try {
            this.fDynamicValidation = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/dynamic");
        }
        catch (final XMLConfigurationException ex3) {
            this.fDynamicValidation = false;
        }
        if (this.fDynamicValidation) {
            this.fDoValidation = true;
        }
        else {
            try {
                this.fDoValidation = xmlComponentManager.getFeature("http://xml.org/sax/features/validation");
            }
            catch (final XMLConfigurationException ex4) {
                this.fDoValidation = false;
            }
        }
        if (this.fDoValidation) {
            try {
                this.fDoValidation = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema");
            }
            catch (final XMLConfigurationException ex5) {}
        }
        try {
            this.fFullChecking = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking");
        }
        catch (final XMLConfigurationException ex6) {
            this.fFullChecking = false;
        }
        try {
            this.fNormalizeData = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema/normalized-value");
        }
        catch (final XMLConfigurationException ex7) {
            this.fNormalizeData = false;
        }
        try {
            this.fSchemaElementDefault = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema/element-default");
        }
        catch (final XMLConfigurationException ex8) {
            this.fSchemaElementDefault = false;
        }
        try {
            this.fAugPSVI = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi");
        }
        catch (final XMLConfigurationException ex9) {
            this.fAugPSVI = true;
        }
        try {
            this.fSchemaType = (String)xmlComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
        }
        catch (final XMLConfigurationException ex10) {
            this.fSchemaType = null;
        }
        try {
            this.fUseGrammarPoolOnly = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only");
        }
        catch (final XMLConfigurationException ex11) {
            this.fUseGrammarPoolOnly = false;
        }
        this.fEntityResolver = (XMLEntityResolver)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        if (this.fIDContext != null) {
            this.fIDContext.clear();
        }
        final TypeValidatorHelper instance = TypeValidatorHelper.getInstance(this.fSchemaVersion);
        (this.fValidationManager = (ValidationManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager")).addValidationState(this.fValidationState);
        this.fValidationState.setSymbolTable(this.fSymbolTable);
        this.fValidationState.setTypeValidatorHelper(instance);
        try {
            final Object property = xmlComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/root-type-definition");
            if (property == null) {
                this.fRootTypeQName = null;
                this.fRootTypeDefinition = null;
            }
            else if (property instanceof QName) {
                this.fRootTypeQName = (QName)property;
                this.fRootTypeDefinition = null;
            }
            else {
                this.fRootTypeDefinition = (XSTypeDefinition)property;
                this.fRootTypeQName = null;
            }
        }
        catch (final XMLConfigurationException ex12) {
            this.fRootTypeQName = null;
            this.fRootTypeDefinition = null;
        }
        try {
            final Object property2 = xmlComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/root-element-declaration");
            if (property2 == null) {
                this.fRootElementDeclQName = null;
                this.fRootElementDeclaration = null;
            }
            else if (property2 instanceof QName) {
                this.fRootElementDeclQName = (QName)property2;
                this.fRootElementDeclaration = null;
            }
            else {
                this.fRootElementDeclaration = (XSElementDecl)property2;
                this.fRootElementDeclQName = null;
            }
        }
        catch (final XMLConfigurationException ex13) {
            this.fRootElementDeclQName = null;
            this.fRootElementDeclaration = null;
        }
        boolean feature2;
        try {
            feature2 = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl");
        }
        catch (final XMLConfigurationException ex14) {
            feature2 = false;
        }
        this.fIgnoreXSITypeDepth = (feature2 ? 0 : -1);
        try {
            this.fIDCChecking = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/identity-constraint-checking");
        }
        catch (final XMLConfigurationException ex15) {
            this.fIDCChecking = true;
        }
        try {
            this.fValidationState.setIdIdrefChecking(xmlComponentManager.getFeature("http://apache.org/xml/features/validation/id-idref-checking"));
        }
        catch (final XMLConfigurationException ex16) {
            this.fValidationState.setIdIdrefChecking(true);
        }
        try {
            this.fValidationState.setUnparsedEntityChecking(xmlComponentManager.getFeature("http://apache.org/xml/features/validation/unparsed-entity-checking"));
        }
        catch (final XMLConfigurationException ex17) {
            this.fValidationState.setUnparsedEntityChecking(true);
        }
        try {
            this.fTypeAlternativesChecking = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/type-alternative-checking");
        }
        catch (final XMLConfigurationException ex18) {
            this.fTypeAlternativesChecking = true;
        }
        try {
            this.fCommentsAndPIsForAssert = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/assert-comments-and-pi-checking");
        }
        catch (final XMLConfigurationException ex19) {
            this.fCommentsAndPIsForAssert = true;
        }
        try {
            this.fExternalSchemas = (String)xmlComponentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
            this.fExternalNoNamespaceSchema = (String)xmlComponentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
        }
        catch (final XMLConfigurationException ex20) {
            this.fExternalSchemas = null;
            this.fExternalNoNamespaceSchema = null;
        }
        if (this.fSchemaVersion == 4) {
            try {
                final Object property3 = xmlComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version");
                if (property3 instanceof String) {
                    this.fDatatypeXMLVersion = (String)property3;
                    if ("1.1".equals(property3)) {
                        this.fValidationState.setDatatypeXMLVersion((short)2);
                    }
                    else {
                        this.fValidationState.setDatatypeXMLVersion((short)1);
                    }
                }
            }
            catch (final XMLConfigurationException ex21) {
                this.fDatatypeXMLVersion = null;
                this.fValidationState.setDatatypeXMLVersion((short)1);
            }
        }
        XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
        try {
            this.fJaxpSchemaSource = xmlComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
        }
        catch (final XMLConfigurationException ex22) {
            this.fJaxpSchemaSource = null;
        }
        try {
            this.fGrammarPool = (XMLGrammarPool)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException ex23) {
            this.fGrammarPool = null;
        }
        this.fState4XsiType.setSymbolTable(symbolTable);
        this.fState4ApplyDefault.setSymbolTable(symbolTable);
        this.fState4XsiType.setTypeValidatorHelper(instance);
        this.fState4ApplyDefault.setTypeValidatorHelper(instance);
    }
    
    void ensureStackCapacity() {
        if (this.fElementDepth == this.fElemDeclStack.length) {
            final int n = this.fElementDepth + 8;
            final boolean[] fSubElementStack = new boolean[n];
            System.arraycopy(this.fSubElementStack, 0, fSubElementStack, 0, this.fElementDepth);
            this.fSubElementStack = fSubElementStack;
            final XSElementDecl[] fElemDeclStack = new XSElementDecl[n];
            System.arraycopy(this.fElemDeclStack, 0, fElemDeclStack, 0, this.fElementDepth);
            this.fElemDeclStack = fElemDeclStack;
            final boolean[] fNilStack = new boolean[n];
            System.arraycopy(this.fNilStack, 0, fNilStack, 0, this.fElementDepth);
            this.fNilStack = fNilStack;
            final XSNotationDecl[] fNotationStack = new XSNotationDecl[n];
            System.arraycopy(this.fNotationStack, 0, fNotationStack, 0, this.fElementDepth);
            this.fNotationStack = fNotationStack;
            final XSTypeDefinition[] fTypeStack = new XSTypeDefinition[n];
            System.arraycopy(this.fTypeStack, 0, fTypeStack, 0, this.fElementDepth);
            this.fTypeStack = fTypeStack;
            final XSCMValidator[] fcmStack = new XSCMValidator[n];
            System.arraycopy(this.fCMStack, 0, fcmStack, 0, this.fElementDepth);
            this.fCMStack = fcmStack;
            final boolean[] fSawTextStack = new boolean[n];
            System.arraycopy(this.fSawTextStack, 0, fSawTextStack, 0, this.fElementDepth);
            this.fSawTextStack = fSawTextStack;
            final boolean[] fStringContent = new boolean[n];
            System.arraycopy(this.fStringContent, 0, fStringContent, 0, this.fElementDepth);
            this.fStringContent = fStringContent;
            final boolean[] fStrictAssessStack = new boolean[n];
            System.arraycopy(this.fStrictAssessStack, 0, fStrictAssessStack, 0, this.fElementDepth);
            this.fStrictAssessStack = fStrictAssessStack;
            final int[][] fcmStateStack = new int[n][];
            System.arraycopy(this.fCMStateStack, 0, fcmStateStack, 0, this.fElementDepth);
            this.fCMStateStack = fcmStateStack;
        }
    }
    
    void handleStartDocument(final XMLLocator xmlLocator, final String s) {
        if (this.fIDCChecking) {
            this.fValueStoreCache.startDocument();
        }
        if (this.fAugPSVI) {
            this.fCurrentPSVI.fGrammars = null;
            this.fCurrentPSVI.fSchemaInformation = null;
        }
    }
    
    void handleEndDocument() {
        if (this.fIDCChecking) {
            this.fValueStoreCache.endDocument();
        }
    }
    
    XMLString handleCharacters(XMLString fNormalizedStr) {
        if (this.fSkipValidationDepth >= 0) {
            if (this.fSchemaVersion == 4) {
                this.fAssertionValidator.characterDataHandler(fNormalizedStr);
            }
            return fNormalizedStr;
        }
        this.fSawText = (this.fSawText || fNormalizedStr.length > 0);
        if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(fNormalizedStr, this.fWhiteSpace == 2);
            fNormalizedStr = this.fNormalizedStr;
        }
        if (this.fAppendBuffer) {
            this.fBuffer.append(fNormalizedStr.ch, fNormalizedStr.offset, fNormalizedStr.length);
        }
        if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15 && ((XSComplexTypeDecl)this.fCurrentType).fContentType == 2) {
            for (int i = fNormalizedStr.offset; i < fNormalizedStr.offset + fNormalizedStr.length; ++i) {
                if (!XMLChar.isSpace(fNormalizedStr.ch[i])) {
                    this.fSawCharacters = true;
                    break;
                }
            }
        }
        if (this.fSchemaVersion == 4) {
            this.fAssertionValidator.characterDataHandler(fNormalizedStr);
        }
        return fNormalizedStr;
    }
    
    private void normalizeWhitespace(final XMLString xmlString, final boolean b) {
        int n = b ? 1 : 0;
        int n2 = 0;
        boolean b2 = false;
        boolean fTrailing = false;
        final int n3 = xmlString.offset + xmlString.length;
        if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < xmlString.length + 1) {
            this.fNormalizedStr.ch = new char[xmlString.length + 1];
        }
        this.fNormalizedStr.offset = 1;
        this.fNormalizedStr.length = 1;
        for (int i = xmlString.offset; i < n3; ++i) {
            final char c = xmlString.ch[i];
            if (XMLChar.isSpace(c)) {
                if (n == 0) {
                    this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
                    n = (b ? 1 : 0);
                }
                if (n2 == 0) {
                    b2 = true;
                }
            }
            else {
                this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
                n = 0;
                n2 = 1;
            }
        }
        if (n != 0) {
            if (this.fNormalizedStr.length > 1) {
                final XMLString fNormalizedStr = this.fNormalizedStr;
                --fNormalizedStr.length;
                fTrailing = true;
            }
            else if (b2 && !this.fFirstChunk) {
                fTrailing = true;
            }
        }
        if (this.fNormalizedStr.length > 1 && !this.fFirstChunk && this.fWhiteSpace == 2) {
            if (this.fTrailing) {
                this.fNormalizedStr.offset = 0;
                this.fNormalizedStr.ch[0] = ' ';
            }
            else if (b2) {
                this.fNormalizedStr.offset = 0;
                this.fNormalizedStr.ch[0] = ' ';
            }
        }
        final XMLString fNormalizedStr2 = this.fNormalizedStr;
        fNormalizedStr2.length -= this.fNormalizedStr.offset;
        if ((this.fTrailing = fTrailing) || n2 != 0) {
            this.fFirstChunk = false;
        }
    }
    
    private void normalizeWhitespace(final String s, final boolean b) {
        int n = b ? 1 : 0;
        final int length = s.length();
        if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < length) {
            this.fNormalizedStr.ch = new char[length];
        }
        this.fNormalizedStr.offset = 0;
        this.fNormalizedStr.length = 0;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (XMLChar.isSpace(char1)) {
                if (n == 0) {
                    this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
                    n = (b ? 1 : 0);
                }
            }
            else {
                this.fNormalizedStr.ch[this.fNormalizedStr.length++] = char1;
                n = 0;
            }
        }
        if (n != 0 && this.fNormalizedStr.length != 0) {
            final XMLString fNormalizedStr = this.fNormalizedStr;
            --fNormalizedStr.length;
        }
    }
    
    void handleIgnorableWhitespace(final XMLString xmlString) {
        if (this.fSkipValidationDepth >= 0) {
            return;
        }
    }
    
    Augmentations handleStartElement(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, Augmentations augmentations) {
        if (this.fElementDepth == -1 && this.fValidationManager.isGrammarFound() && this.fSchemaType == null) {
            this.fSchemaDynamicValidation = true;
        }
        if (this.fSchemaVersion == 4) {
            this.fIsAssertProcessingNeededForSTUnionAttrs.clear();
        }
        if (!this.fUseGrammarPoolOnly) {
            this.storeLocations(xmlAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION), xmlAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION));
        }
        if (this.fSkipValidationDepth >= 0) {
            ++this.fElementDepth;
            if (this.fAugPSVI) {
                augmentations = this.getEmptyAugs(augmentations);
            }
            if (this.fSchemaVersion == 4) {
                this.assertionValidatorStartElementDelegate(qName, xmlAttributes, augmentations);
            }
            return augmentations;
        }
        Object oneTransition = null;
        if (this.fCurrentCM != null) {
            oneTransition = this.fCurrentCM.oneTransition(qName, this.fCurrCMState, this.fSubGroupHandler, this);
            if (this.fCurrCMState[0] == -1) {
                final Vector whatCanGoHere;
                if (((XSComplexTypeDecl)this.fCurrentType).fParticle != null && (whatCanGoHere = this.fCurrentCM.whatCanGoHere(this.fCurrCMState)).size() > 0) {
                    final String expectedStr = this.expectedStr(whatCanGoHere);
                    final int[] occurenceInfo = this.fCurrentCM.occurenceInfo(this.fCurrCMState);
                    final String s = (qName.uri != null) ? ("{\"" + qName.uri + '\"' + ":" + qName.localpart + "}") : qName.localpart;
                    if (occurenceInfo != null) {
                        final int n = occurenceInfo[0];
                        final int n2 = occurenceInfo[1];
                        final int n3 = occurenceInfo[2];
                        if (n3 < n) {
                            final int n4 = n - n3;
                            if (n4 > 1) {
                                this.reportSchemaError("cvc-complex-type.2.4.h", new Object[] { qName.rawname, this.fCurrentCM.getTermName(occurenceInfo[3]), Integer.toString(n), Integer.toString(n4) });
                            }
                            else {
                                this.reportSchemaError("cvc-complex-type.2.4.g", new Object[] { qName.rawname, this.fCurrentCM.getTermName(occurenceInfo[3]), Integer.toString(n) });
                            }
                        }
                        else if (n3 >= n2 && n2 != -1) {
                            this.reportSchemaError("cvc-complex-type.2.4.e", new Object[] { qName.rawname, expectedStr, Integer.toString(n2) });
                        }
                        else {
                            this.reportSchemaError("cvc-complex-type.2.4.a", new Object[] { s, expectedStr });
                        }
                    }
                    else {
                        this.reportSchemaError("cvc-complex-type.2.4.a", new Object[] { s, expectedStr });
                    }
                }
                else {
                    final int[] occurenceInfo2 = this.fCurrentCM.occurenceInfo(this.fCurrCMState);
                    if (occurenceInfo2 != null) {
                        final int n5 = occurenceInfo2[1];
                        if (occurenceInfo2[2] >= n5 && n5 != -1) {
                            this.reportSchemaError("cvc-complex-type.2.4.f", new Object[] { this.fCurrentCM.getTermName(occurenceInfo2[3]), Integer.toString(n5) });
                        }
                        else {
                            this.reportSchemaError("cvc-complex-type.2.4.d", new Object[] { qName.rawname });
                        }
                    }
                    else {
                        this.reportSchemaError("cvc-complex-type.2.4.d", new Object[] { qName.rawname });
                    }
                }
            }
        }
        if (this.fElementDepth != -1) {
            this.ensureStackCapacity();
            this.fSubElementStack[this.fElementDepth] = true;
            this.fSubElement = false;
            this.fElemDeclStack[this.fElementDepth] = this.fCurrentElemDecl;
            this.fNilStack[this.fElementDepth] = this.fNil;
            this.fNotationStack[this.fElementDepth] = this.fNotation;
            this.fTypeStack[this.fElementDepth] = this.fCurrentType;
            this.fStrictAssessStack[this.fElementDepth] = this.fStrictAssess;
            this.fCMStack[this.fElementDepth] = this.fCurrentCM;
            this.fCMStateStack[this.fElementDepth] = this.fCurrCMState;
            this.fSawTextStack[this.fElementDepth] = this.fSawText;
            this.fStringContent[this.fElementDepth] = this.fSawCharacters;
        }
        ++this.fElementDepth;
        this.fCurrentElemDecl = null;
        XSWildcardDecl xsWildcardDecl = null;
        this.fCurrentType = null;
        this.fStrictAssess = true;
        this.fNil = false;
        this.fNotation = null;
        this.fBuffer.setLength(0);
        this.fSawText = false;
        this.fSawCharacters = false;
        if (oneTransition != null) {
            if (oneTransition instanceof XSElementDecl) {
                this.fCurrentElemDecl = (XSElementDecl)oneTransition;
            }
            else if (oneTransition instanceof XSOpenContentDecl) {
                xsWildcardDecl = (XSWildcardDecl)((XSOpenContentDecl)oneTransition).getWildcard();
            }
            else {
                xsWildcardDecl = (XSWildcardDecl)oneTransition;
            }
        }
        if (xsWildcardDecl != null && xsWildcardDecl.fProcessContents == 2) {
            this.fSkipValidationDepth = this.fElementDepth;
            if (this.fAugPSVI) {
                augmentations = this.getEmptyAugs(augmentations);
            }
            if (this.fSchemaVersion == 4) {
                this.assertionValidatorStartElementDelegate(qName, xmlAttributes, augmentations);
            }
            return augmentations;
        }
        if (this.fElementDepth == 0) {
            if (this.fRootElementDeclaration != null) {
                this.checkElementMatchesRootElementDecl(this.fCurrentElemDecl = this.fRootElementDeclaration, qName);
            }
            else if (this.fRootElementDeclQName != null) {
                this.processRootElementDeclQName(this.fRootElementDeclQName, qName);
            }
            else if (this.fRootTypeDefinition != null) {
                this.fCurrentType = this.fRootTypeDefinition;
            }
            else if (this.fRootTypeQName != null) {
                this.processRootTypeQName(this.fRootTypeQName);
            }
        }
        if (this.fCurrentType == null) {
            if (this.fCurrentElemDecl == null) {
                final SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)5, qName.uri, null, qName, xmlAttributes);
                if (schemaGrammar != null) {
                    this.fCurrentElemDecl = schemaGrammar.getGlobalElementDecl(qName.localpart);
                }
            }
            if (this.fCurrentElemDecl != null) {
                this.fCurrentType = this.fCurrentElemDecl.fType;
            }
        }
        if (this.fTypeAlternativesChecking && this.fCurrentElemDecl != null) {
            this.fTypeAlternative = this.fTypeAlternativeValidator.getTypeAlternative(this.fCurrentElemDecl, qName, xmlAttributes, this.fInheritableAttrList, this.fNamespaceContext, this.fLocator.getExpandedSystemId());
            if (this.fTypeAlternative != null) {
                this.fCurrentType = this.fTypeAlternative.getTypeDefinition();
            }
        }
        if (this.fElementDepth == this.fIgnoreXSITypeDepth && this.fCurrentElemDecl == null) {
            ++this.fIgnoreXSITypeDepth;
        }
        String value = null;
        if (this.fElementDepth >= this.fIgnoreXSITypeDepth) {
            value = xmlAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);
        }
        final boolean b = this.fSchemaVersion == 4;
        boolean b2 = false;
        if (this.fCurrentType == null && value == null) {
            if (this.fElementDepth == 0) {
                if (this.fDynamicValidation || this.fSchemaDynamicValidation) {
                    if (this.fDocumentSource != null) {
                        this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
                        if (this.fDocumentHandler != null) {
                            this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
                        }
                        this.fElementDepth = -2;
                        return augmentations;
                    }
                    this.fSkipValidationDepth = this.fElementDepth;
                    if (this.fAugPSVI) {
                        augmentations = this.getEmptyAugs(augmentations);
                    }
                    return augmentations;
                }
                else {
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-elt.1.a", new Object[] { qName.rawname }, (short)1);
                }
            }
            else if (xsWildcardDecl != null && xsWildcardDecl.fProcessContents == 1) {
                this.reportSchemaError("cvc-complex-type.2.4.c", new Object[] { qName.rawname });
            }
            this.fCurrentType = SchemaGrammar.getXSAnyType(this.fSchemaVersion);
            this.fStrictAssess = false;
            this.fNFullValidationDepth = this.fElementDepth;
            this.fAppendBuffer = false;
            if (b) {
                b2 = true;
            }
            else {
                this.fXSIErrorReporter.pushContext();
            }
        }
        else {
            if (b) {
                b2 = true;
            }
            else {
                this.fXSIErrorReporter.pushContext();
            }
            if (value != null) {
                final XSTypeDefinition fCurrentType = this.fCurrentType;
                if (b) {
                    if (this.fXSITypeErrors.size() > 0) {
                        this.fXSITypeErrors.clear();
                    }
                    this.fCurrentType = this.getAndCheckXsiType(qName, value, xmlAttributes, this.fXSITypeErrors);
                }
                else {
                    this.fCurrentType = this.getAndCheckXsiType(qName, value, xmlAttributes);
                }
                if (this.fCurrentType == null) {
                    if (fCurrentType == null) {
                        this.fCurrentType = SchemaGrammar.getXSAnyType(this.fSchemaVersion);
                    }
                    else {
                        this.fCurrentType = fCurrentType;
                    }
                }
            }
            this.fNNoneValidationDepth = this.fElementDepth;
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                this.fAppendBuffer = true;
            }
            else if (this.fCurrentType.getTypeCategory() == 16) {
                this.fAppendBuffer = true;
            }
            else {
                this.fAppendBuffer = (((XSComplexTypeDecl)this.fCurrentType).fContentType == 1);
            }
        }
        if (b) {
            if (xsWildcardDecl != null && this.fCurrentCM != null) {
                final XSElementDecl locallyDeclaredType = this.findLocallyDeclaredType(qName, this.fCurrentCM, this.fTypeStack[this.fElementDepth - 1].getBaseType());
                if (locallyDeclaredType != null) {
                    final XSTypeDefinition typeDefinition = locallyDeclaredType.getTypeDefinition();
                    if (this.fCurrentType != typeDefinition) {
                        short fBlock = locallyDeclaredType.fBlock;
                        if (typeDefinition.getTypeCategory() == 15) {
                            fBlock |= ((XSComplexTypeDecl)typeDefinition).fBlock;
                        }
                        if (!this.fXSConstraints.checkTypeDerivationOk(this.fCurrentType, typeDefinition, fBlock)) {
                            this.reportSchemaError("cos-element-consistent.4.a", new Object[] { qName.rawname, this.fCurrentType, typeDefinition.getName() });
                        }
                    }
                }
            }
            if (b2) {
                this.fXSIErrorReporter.pushContext();
                final int size = this.fXSITypeErrors.size();
                if (size > 0) {
                    for (int i = 0; i < size; ++i) {
                        this.reportSchemaError((String)this.fXSITypeErrors.get(i), (Object[])this.fXSITypeErrors.get(++i));
                    }
                    this.fXSITypeErrors.clear();
                }
            }
        }
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getAbstract()) {
            this.reportSchemaError("cvc-elt.2", new Object[] { qName.rawname });
        }
        if (this.fElementDepth == 0) {
            this.fValidationRoot = qName.rawname;
        }
        if (this.fNormalizeData) {
            this.fFirstChunk = true;
            this.fTrailing = false;
            this.fUnionType = false;
            this.fWhiteSpace = -1;
        }
        if (this.fCurrentType.getTypeCategory() == 15) {
            final XSComplexTypeDecl xsComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
            if (xsComplexTypeDecl.getAbstract()) {
                this.reportSchemaError("cvc-type.2", new Object[] { qName.rawname });
            }
            if (this.fNormalizeData && xsComplexTypeDecl.fContentType == 1) {
                if (xsComplexTypeDecl.fXSSimpleType.getVariety() == 3) {
                    this.fUnionType = true;
                }
                else {
                    try {
                        this.fWhiteSpace = xsComplexTypeDecl.fXSSimpleType.getWhitespace();
                    }
                    catch (final DatatypeException ex) {}
                }
            }
        }
        else if (this.fNormalizeData) {
            final XSSimpleType xsSimpleType = (XSSimpleType)this.fCurrentType;
            if (xsSimpleType.getVariety() == 3) {
                this.fUnionType = true;
            }
            else {
                try {
                    this.fWhiteSpace = xsSimpleType.getWhitespace();
                }
                catch (final DatatypeException ex2) {}
            }
        }
        this.fCurrentCM = null;
        if (this.fCurrentType.getTypeCategory() == 15) {
            this.fCurrentCM = ((XSComplexTypeDecl)this.fCurrentType).getContentModel(this.fCMBuilder);
        }
        this.fCurrCMState = null;
        if (this.fCurrentCM != null) {
            this.fCurrCMState = this.fCurrentCM.startContentModel();
        }
        final String value2 = xmlAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL);
        if (value2 != null && this.fCurrentElemDecl != null) {
            this.fNil = this.getXsiNil(qName, value2);
        }
        XSAttributeGroupDecl attrGrp = null;
        if (this.fCurrentType.getTypeCategory() == 15) {
            attrGrp = ((XSComplexTypeDecl)this.fCurrentType).getAttrGrp();
        }
        if (this.fIDCChecking) {
            this.fValueStoreCache.startElement();
            this.fMatcherStack.pushContext();
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fIDCPos > 0) {
                this.fIdConstraint = true;
                this.fValueStoreCache.initValueStoresFor(this.fCurrentElemDecl, this);
            }
        }
        if (this.fSchemaVersion == 4) {
            this.fIDContext.pushContext();
        }
        this.processAttributes(qName, xmlAttributes, attrGrp);
        if (attrGrp != null) {
            this.addDefaultAttributes(qName, xmlAttributes, attrGrp);
        }
        if (this.fSchemaVersion == 4) {
            this.fAssertionValidator.extraCheckForSTUnionAssertsAttrs(xmlAttributes);
        }
        if (this.fSchemaVersion == 4) {
            this.fIDContext.setCurrentScopeToParent();
        }
        for (int matcherCount = this.fMatcherStack.getMatcherCount(), j = 0; j < matcherCount; ++j) {
            this.fMatcherStack.getMatcherAt(j).startElement(qName, xmlAttributes);
        }
        if (this.fAugPSVI) {
            augmentations = this.getEmptyAugs(augmentations);
            this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
            this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
            this.fCurrentPSVI.fNotation = this.fNotation;
            this.fCurrentPSVI.fNil = this.fNil;
            if (this.fSchemaVersion == 4) {
                this.fCurrentPSVI.fTypeAlternative = this.fTypeAlternative;
                this.fInhrAttrCountStack.push(this.fInheritableAttrList.size());
                this.fCurrentPSVI.fInheritedAttributes = this.fTypeAlternativeValidator.getInheritedAttributesForPSVI();
                this.fCurrentPSVI.fFailedAssertions = this.fFailedAssertions;
            }
        }
        if (this.fSchemaVersion == 4) {
            this.fTypeAlternativeValidator.saveInheritableAttributes(this.fCurrentElemDecl, xmlAttributes);
            final XMLAttributesImpl xmlAttributesImpl = (XMLAttributesImpl)xmlAttributes;
            for (int k = 0; k < xmlAttributesImpl.getLength(); ++k) {
                xmlAttributesImpl.getAugmentations(k).putItem(XSAssertConstants.isAssertProcNeededForUnionAttr, this.fIsAssertProcessingNeededForSTUnionAttrs.get(k));
            }
            this.assertionValidatorStartElementDelegate(qName, xmlAttributes, augmentations);
        }
        return augmentations;
    }
    
    private XSElementDecl findLocallyDeclaredType(final org.apache.xerces.xni.QName qName, XSCMValidator contentModel, final XSTypeDefinition xsTypeDefinition) {
        XSElementDecl matchingElemDecl = null;
        if (contentModel != null) {
            matchingElemDecl = contentModel.findMatchingElemDecl(qName, this.fSubGroupHandler);
        }
        if (matchingElemDecl == null && xsTypeDefinition.getTypeCategory() != 16 && xsTypeDefinition != SchemaGrammar.getXSAnyType(this.fSchemaVersion)) {
            contentModel = ((XSComplexTypeDecl)xsTypeDefinition).getContentModel(this.fCMBuilder);
            return this.findLocallyDeclaredType(qName, contentModel, xsTypeDefinition.getBaseType());
        }
        return matchingElemDecl;
    }
    
    private void assertionValidatorStartElementDelegate(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) {
        try {
            this.fAssertionValidator.handleStartElement(qName, xmlAttributes);
        }
        catch (final Exception ex) {
            throw new XMLParseException(this.fLocator, ex.getMessage());
        }
    }
    
    Augmentations handleEndElement(final org.apache.xerces.xni.QName qName, Augmentations augmentations) {
        if (this.fSkipValidationDepth >= 0) {
            if (this.fSkipValidationDepth == this.fElementDepth && this.fSkipValidationDepth > 0) {
                this.fNFullValidationDepth = this.fSkipValidationDepth - 1;
                this.fSkipValidationDepth = -1;
                --this.fElementDepth;
                this.fSubElement = this.fSubElementStack[this.fElementDepth];
                this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
                this.fNil = this.fNilStack[this.fElementDepth];
                this.fNotation = this.fNotationStack[this.fElementDepth];
                this.fCurrentType = this.fTypeStack[this.fElementDepth];
                this.fCurrentCM = this.fCMStack[this.fElementDepth];
                this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
                this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
                this.fSawText = this.fSawTextStack[this.fElementDepth];
                this.fSawCharacters = this.fStringContent[this.fElementDepth];
            }
            else {
                --this.fElementDepth;
            }
            if (this.fElementDepth == -1 && this.fFullChecking && !this.fUseGrammarPoolOnly) {
                this.fXSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }
            if (this.fAugPSVI) {
                augmentations = this.getEmptyAugs(augmentations);
            }
            if (this.fSchemaVersion == 4) {
                this.assertionValidatorEndElementDelegate(qName);
            }
            return augmentations;
        }
        this.processElementContent(qName);
        if (this.fSchemaVersion == 4) {
            this.fIDContext.popContext();
        }
        if (this.fIDCChecking) {
            final int matcherCount = this.fMatcherStack.getMatcherCount();
            for (int i = matcherCount - 1; i >= 0; --i) {
                final XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
                if (this.fCurrentElemDecl == null) {
                    matcher.endElement(qName, this.fCurrentType, false, this.fValidatedInfo.actualValue, this.fValidatedInfo.actualValueType, this.fValidatedInfo.itemValueTypes);
                }
                else {
                    matcher.endElement(qName, this.fCurrentType, this.fCurrentElemDecl.getNillable(), (this.fDefaultValue == null) ? this.fValidatedInfo.actualValue : this.fCurrentElemDecl.fDefault.actualValue, (this.fDefaultValue == null) ? this.fValidatedInfo.actualValueType : this.fCurrentElemDecl.fDefault.actualValueType, (this.fDefaultValue == null) ? this.fValidatedInfo.itemValueTypes : this.fCurrentElemDecl.fDefault.itemValueTypes);
                }
            }
            if (this.fMatcherStack.size() > 0) {
                this.fMatcherStack.popContext();
            }
            final int matcherCount2 = this.fMatcherStack.getMatcherCount();
            for (int j = matcherCount - 1; j >= matcherCount2; --j) {
                final XPathMatcher matcher2 = this.fMatcherStack.getMatcherAt(j);
                if (matcher2 instanceof Selector.Matcher) {
                    final Selector.Matcher matcher3 = (Selector.Matcher)matcher2;
                    final IdentityConstraint identityConstraint;
                    if ((identityConstraint = matcher3.getIdentityConstraint()) != null && identityConstraint.getCategory() != 2) {
                        this.fValueStoreCache.transplant(identityConstraint, matcher3.getInitialDepth());
                    }
                }
            }
            for (int k = matcherCount - 1; k >= matcherCount2; --k) {
                final XPathMatcher matcher4 = this.fMatcherStack.getMatcherAt(k);
                if (matcher4 instanceof Selector.Matcher) {
                    final Selector.Matcher matcher5 = (Selector.Matcher)matcher4;
                    final IdentityConstraint identityConstraint2;
                    if ((identityConstraint2 = matcher5.getIdentityConstraint()) != null && identityConstraint2.getCategory() == 2) {
                        final ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint2, matcher5.getInitialDepth());
                        if (valueStore != null && valueStore.fHasValue) {
                            valueStore.endDocumentFragment();
                        }
                    }
                }
            }
            this.fValueStoreCache.endElement();
        }
        if (this.fSchemaVersion == 4) {
            try {
                this.assertionValidatorEndElementDelegate(qName);
            }
            catch (final Exception ex) {
                throw new XMLParseException(this.fLocator, ex.getMessage());
            }
        }
        if (this.fElementDepth < this.fIgnoreXSITypeDepth) {
            --this.fIgnoreXSITypeDepth;
        }
        final SchemaGrammar[] array = null;
        if (this.fElementDepth == 0) {
            final Iterator checkIDRefID = this.fValidationState.checkIDRefID();
            this.fValidationState.resetIDTables();
            if (checkIDRefID != null) {
                while (checkIDRefID.hasNext()) {
                    this.reportSchemaError("cvc-id.1", new Object[] { checkIDRefID.next() });
                }
            }
            if (this.fFullChecking && !this.fUseGrammarPoolOnly) {
                this.fXSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }
            final SchemaGrammar[] grammars = this.fGrammarBucket.getGrammars();
            if (this.fGrammarPool != null) {
                for (int l = 0; l < grammars.length; ++l) {
                    grammars[l].setImmutable(true);
                }
                this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", grammars);
            }
            augmentations = this.endElementPSVI(true, grammars, augmentations);
        }
        else {
            augmentations = this.endElementPSVI(false, array, augmentations);
            --this.fElementDepth;
            this.fSubElement = this.fSubElementStack[this.fElementDepth];
            this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
            this.fNil = this.fNilStack[this.fElementDepth];
            this.fNotation = this.fNotationStack[this.fElementDepth];
            this.fCurrentType = this.fTypeStack[this.fElementDepth];
            this.fCurrentCM = this.fCMStack[this.fElementDepth];
            this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
            this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
            this.fSawText = this.fSawTextStack[this.fElementDepth];
            this.fSawCharacters = this.fStringContent[this.fElementDepth];
            this.fWhiteSpace = -1;
            this.fAppendBuffer = false;
            this.fUnionType = false;
        }
        return augmentations;
    }
    
    private void assertionValidatorEndElementDelegate(final org.apache.xerces.xni.QName qName) {
        final AugmentationsImpl augmentationsImpl = new AugmentationsImpl();
        final ElementPSVImpl elementPSVImpl = new ElementPSVImpl();
        elementPSVImpl.fDeclaration = this.fCurrentElemDecl;
        elementPSVImpl.fTypeDecl = this.fCurrentType;
        elementPSVImpl.fNotation = this.fNotation;
        elementPSVImpl.fGrammars = this.fGrammarBucket.getGrammars();
        augmentationsImpl.putItem("ELEMENT_PSVI", elementPSVImpl);
        augmentationsImpl.putItem(XSAssertConstants.isAssertProcNeededForUnionElem, this.fIsAssertProcessingNeededForSTUnionElem);
        this.fAssertionValidator.handleEndElement(qName, augmentationsImpl);
        this.fFailedAssertions = elementPSVImpl.fFailedAssertions;
        if (this.fAugPSVI && this.fIsAssertProcessingNeededForSTUnionElem) {
            this.fValidatedInfo.memberType = elementPSVImpl.fValue.memberType;
        }
        this.fIsAssertProcessingNeededForSTUnionElem = true;
    }
    
    final Augmentations endElementPSVI(final boolean b, final SchemaGrammar[] fGrammars, Augmentations emptyAugs) {
        if (this.fAugPSVI) {
            emptyAugs = this.getEmptyAugs(emptyAugs);
            this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
            this.fCurrentPSVI.fNotation = this.fNotation;
            this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
            this.fCurrentPSVI.fNil = this.fNil;
            if (this.fSchemaVersion == 4) {
                this.fCurrentPSVI.fTypeAlternative = this.fTypeAlternative;
                ObjectList inheritedAttributesForPSVI = null;
                if (this.fInhrAttrCountStack.size() > 0) {
                    this.fInheritableAttrList.setSize(this.fInhrAttrCountStack.pop());
                    inheritedAttributesForPSVI = this.fTypeAlternativeValidator.getInheritedAttributesForPSVI();
                }
                this.fCurrentPSVI.fInheritedAttributes = inheritedAttributesForPSVI;
                this.fCurrentPSVI.fFailedAssertions = this.fFailedAssertions;
            }
            if (this.fElementDepth > this.fNFullValidationDepth) {
                this.fCurrentPSVI.fValidationAttempted = 2;
            }
            else if (this.fElementDepth > this.fNNoneValidationDepth) {
                this.fCurrentPSVI.fValidationAttempted = 0;
            }
            else {
                this.fCurrentPSVI.fValidationAttempted = 1;
            }
            if (this.fNFullValidationDepth == this.fElementDepth) {
                this.fNFullValidationDepth = this.fElementDepth - 1;
            }
            if (this.fNNoneValidationDepth == this.fElementDepth) {
                this.fNNoneValidationDepth = this.fElementDepth - 1;
            }
            if (this.fDefaultValue != null) {
                this.fCurrentPSVI.fSpecified = true;
            }
            this.fCurrentPSVI.fValue.copyFrom(this.fValidatedInfo);
            if (this.fStrictAssess) {
                final String[] mergeContext = this.fXSIErrorReporter.mergeContext();
                this.fCurrentPSVI.fErrors = mergeContext;
                this.fCurrentPSVI.fValidity = (short)((mergeContext == null) ? 2 : 1);
            }
            else {
                this.fCurrentPSVI.fValidity = 0;
                this.fXSIErrorReporter.popContext();
            }
            if (b) {
                this.fCurrentPSVI.fGrammars = fGrammars;
                this.fCurrentPSVI.fSchemaInformation = null;
            }
        }
        return emptyAugs;
    }
    
    Augmentations getEmptyAugs(Augmentations fAugmentations) {
        if (fAugmentations == null) {
            fAugmentations = this.fAugmentations;
            fAugmentations.removeAllItems();
        }
        fAugmentations.putItem("ELEMENT_PSVI", this.fCurrentPSVI);
        this.fCurrentPSVI.reset();
        return fAugmentations;
    }
    
    void storeLocations(final String s, String expandSystemId) {
        if (s != null && !XMLSchemaLoader.tokenizeSchemaLocationStr(s, this.fLocationPairs, (this.fLocator == null) ? null : this.fLocator.getExpandedSystemId())) {
            this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { s }, (short)0);
        }
        if (expandSystemId != null) {
            XMLSchemaLoader.LocationArray locationArray = this.fLocationPairs.get(XMLSymbols.EMPTY_STRING);
            if (locationArray == null) {
                locationArray = new XMLSchemaLoader.LocationArray();
                this.fLocationPairs.put(XMLSymbols.EMPTY_STRING, locationArray);
            }
            if (this.fLocator != null) {
                try {
                    expandSystemId = XMLEntityManager.expandSystemId(expandSystemId, this.fLocator.getExpandedSystemId(), false);
                }
                catch (final URI.MalformedURIException ex) {}
            }
            locationArray.addLocation(expandSystemId);
        }
    }
    
    private boolean isValidBuiltInTypeName(final String s) {
        return this.fSchemaVersion != 2 || (!s.equals("duration") && !s.equals("yearMonthDuration") && !s.equals("dayTimeDuration"));
    }
    
    XSTypeDefinition getAndCheckXsiType(final org.apache.xerces.xni.QName qName, final String s, final XMLAttributes xmlAttributes) {
        org.apache.xerces.xni.QName qName2;
        try {
            qName2 = (org.apache.xerces.xni.QName)this.fQNameDV.validate(s, this.fValidationState, null);
        }
        catch (final InvalidDatatypeValueException ex) {
            this.reportSchemaError(ex.getKey(), ex.getArgs());
            this.reportSchemaError("cvc-elt.4.1", new Object[] { qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, s });
            return null;
        }
        XSTypeDefinition xsTypeDefinition = null;
        if (qName2.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.isValidBuiltInTypeName(qName2.localpart)) {
            xsTypeDefinition = SchemaGrammar.getS4SGrammar(this.fSchemaVersion).getGlobalTypeDecl(qName2.localpart);
        }
        if (xsTypeDefinition == null) {
            final SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)7, qName2.uri, qName, qName2, xmlAttributes);
            if (schemaGrammar != null) {
                xsTypeDefinition = schemaGrammar.getGlobalTypeDecl(qName2.localpart);
            }
        }
        if (xsTypeDefinition == null) {
            this.reportSchemaError("cvc-elt.4.2", new Object[] { qName.rawname, s });
            return null;
        }
        if (this.fCurrentType != null) {
            short fBlock = 0;
            if (this.fCurrentElemDecl != null) {
                fBlock = this.fCurrentElemDecl.fBlock;
            }
            if (this.fCurrentType.getTypeCategory() == 15) {
                fBlock |= ((XSComplexTypeDecl)this.fCurrentType).fBlock;
            }
            if (!this.fXSConstraints.checkTypeDerivationOk(xsTypeDefinition, this.fCurrentType, fBlock)) {
                this.reportSchemaError("cvc-elt.4.3", new Object[] { qName.rawname, s, XS11TypeHelper.getSchemaTypeName(this.fCurrentType) });
            }
        }
        return xsTypeDefinition;
    }
    
    XSTypeDefinition getAndCheckXsiType(final org.apache.xerces.xni.QName qName, final String s, final XMLAttributes xmlAttributes, final ArrayList list) {
        org.apache.xerces.xni.QName qName2;
        try {
            qName2 = (org.apache.xerces.xni.QName)this.fQNameDV.validate(s, this.fValidationState, null);
        }
        catch (final InvalidDatatypeValueException ex) {
            list.add(ex.getKey());
            list.add(ex.getArgs());
            list.add("cvc-elt.4.1");
            list.add(new Object[] { qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, s });
            return null;
        }
        XSTypeDefinition xsTypeDefinition = null;
        if (qName2.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.isValidBuiltInTypeName(qName2.localpart)) {
            xsTypeDefinition = SchemaGrammar.getS4SGrammar(this.fSchemaVersion).getGlobalTypeDecl(qName2.localpart);
        }
        if (xsTypeDefinition == null) {
            final SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)7, qName2.uri, qName, qName2, xmlAttributes);
            if (schemaGrammar != null) {
                xsTypeDefinition = schemaGrammar.getGlobalTypeDecl(qName2.localpart);
            }
        }
        if (xsTypeDefinition == null) {
            list.add("cvc-elt.4.2");
            list.add(new Object[] { qName.rawname, s });
            return null;
        }
        if (this.fCurrentType != null) {
            short fBlock = 0;
            if (this.fCurrentElemDecl != null) {
                fBlock = this.fCurrentElemDecl.fBlock;
            }
            if (this.fCurrentType.getTypeCategory() == 15) {
                fBlock |= ((XSComplexTypeDecl)this.fCurrentType).fBlock;
            }
            if (!this.fXSConstraints.checkTypeDerivationOk(xsTypeDefinition, this.fCurrentType, fBlock)) {
                list.add("cvc-elt.4.3");
                list.add(new Object[] { qName.rawname, s, XS11TypeHelper.getSchemaTypeName(this.fCurrentType) });
            }
        }
        return xsTypeDefinition;
    }
    
    boolean getXsiNil(final org.apache.xerces.xni.QName qName, final String s) {
        if (this.fCurrentElemDecl != null && !this.fCurrentElemDecl.getNillable()) {
            this.reportSchemaError("cvc-elt.3.1", new Object[] { qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
        }
        else {
            final String trim = XMLChar.trim(s);
            if (trim.equals("true") || trim.equals("1")) {
                if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                    this.reportSchemaError("cvc-elt.3.2.2", new Object[] { qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
                }
                return true;
            }
        }
        return false;
    }
    
    boolean allowAttribute(final XSWildcardDecl xsWildcardDecl, final org.apache.xerces.xni.QName qName, final SchemaGrammar schemaGrammar) {
        return xsWildcardDecl.allowQName(qName) && (schemaGrammar == null || !xsWildcardDecl.fDisallowedDefined || schemaGrammar.getGlobalAttributeDecl(qName.localpart) == null);
    }
    
    void processAttributes(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, final XSAttributeGroupDecl xsAttributeGroupDecl) {
        Object fName = null;
        final int length = xmlAttributes.getLength();
        Object o = null;
        final boolean b = this.fCurrentType == null || this.fCurrentType.getTypeCategory() == 16;
        XSObjectList attributeUses = null;
        int length2 = 0;
        XSWildcardDecl fAttributeWC = null;
        if (!b) {
            attributeUses = xsAttributeGroupDecl.getAttributeUses();
            length2 = attributeUses.getLength();
            fAttributeWC = xsAttributeGroupDecl.fAttributeWC;
        }
        for (int i = 0; i < length; ++i) {
            xmlAttributes.getName(i, this.fTempQName);
            if (this.fAugPSVI || this.fIdConstraint) {
                final Augmentations augmentations = xmlAttributes.getAugmentations(i);
                o = augmentations.getItem("ATTRIBUTE_PSVI");
                if (o != null) {
                    ((AttributePSVImpl)o).reset();
                }
                else {
                    o = new AttributePSVImpl();
                    augmentations.putItem("ATTRIBUTE_PSVI", o);
                }
                ((AttributePSVImpl)o).fValidationContext = this.fValidationRoot;
            }
            if (this.fTempQName.uri == SchemaSymbols.URI_XSI) {
                XSAttributeDecl xsAttributeDecl = null;
                if (this.fTempQName.localpart == SchemaSymbols.XSI_TYPE) {
                    xsAttributeDecl = XMLSchemaValidator.XSI_TYPE;
                }
                else if (this.fTempQName.localpart == SchemaSymbols.XSI_NIL) {
                    xsAttributeDecl = XMLSchemaValidator.XSI_NIL;
                }
                else if (this.fTempQName.localpart == SchemaSymbols.XSI_SCHEMALOCATION) {
                    xsAttributeDecl = XMLSchemaValidator.XSI_SCHEMALOCATION;
                }
                else if (this.fTempQName.localpart == SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION) {
                    xsAttributeDecl = XMLSchemaValidator.XSI_NONAMESPACESCHEMALOCATION;
                }
                if (xsAttributeDecl != null) {
                    this.processOneAttribute(qName, xmlAttributes, i, xsAttributeDecl, null, (AttributePSVImpl)o);
                    continue;
                }
            }
            if (this.fTempQName.rawname != XMLSymbols.PREFIX_XMLNS) {
                if (!this.fTempQName.rawname.startsWith("xmlns:")) {
                    if (b) {
                        this.reportSchemaError("cvc-type.3.1.1", new Object[] { qName.rawname, this.fTempQName.rawname });
                    }
                    else {
                        XSAttributeUseImpl xsAttributeUseImpl = null;
                        for (int j = 0; j < length2; ++j) {
                            final XSAttributeUseImpl xsAttributeUseImpl2 = (XSAttributeUseImpl)attributeUses.item(j);
                            if (xsAttributeUseImpl2.fAttrDecl.fName == this.fTempQName.localpart && xsAttributeUseImpl2.fAttrDecl.fTargetNamespace == this.fTempQName.uri) {
                                xsAttributeUseImpl = xsAttributeUseImpl2;
                                break;
                            }
                        }
                        if (xsAttributeUseImpl == null) {
                            final SchemaGrammar schemaGrammar = (this.fSchemaVersion < 4) ? null : this.findSchemaGrammar((short)6, this.fTempQName.uri, qName, this.fTempQName, xmlAttributes);
                            if (fAttributeWC == null || !this.allowAttribute(fAttributeWC, this.fTempQName, schemaGrammar)) {
                                this.reportSchemaError("cvc-complex-type.3.2.2", new Object[] { qName.rawname, this.fTempQName.rawname });
                                this.fNFullValidationDepth = this.fElementDepth;
                                continue;
                            }
                        }
                        XSAttributeDecl xsAttributeDecl2 = null;
                        if (xsAttributeUseImpl != null) {
                            xsAttributeDecl2 = xsAttributeUseImpl.fAttrDecl;
                        }
                        else {
                            if (fAttributeWC.fProcessContents == 2) {
                                continue;
                            }
                            final SchemaGrammar schemaGrammar2 = this.findSchemaGrammar((short)6, this.fTempQName.uri, qName, this.fTempQName, xmlAttributes);
                            if (schemaGrammar2 != null) {
                                xsAttributeDecl2 = schemaGrammar2.getGlobalAttributeDecl(this.fTempQName.localpart);
                            }
                            if (xsAttributeDecl2 == null) {
                                if (fAttributeWC.fProcessContents == 1) {
                                    this.reportSchemaError("cvc-complex-type.3.2.2", new Object[] { qName.rawname, this.fTempQName.rawname });
                                }
                                continue;
                            }
                            else if (this.fSchemaVersion < 4 && xsAttributeDecl2.fType.getTypeCategory() == 16 && xsAttributeDecl2.fType.isIDType()) {
                                if (fName != null) {
                                    this.reportSchemaError("cvc-complex-type.5.1", new Object[] { qName.rawname, xsAttributeDecl2.fName, fName });
                                }
                                else {
                                    fName = xsAttributeDecl2.fName;
                                }
                            }
                        }
                        this.processOneAttribute(qName, xmlAttributes, i, xsAttributeDecl2, xsAttributeUseImpl, (AttributePSVImpl)o);
                    }
                }
            }
        }
        if (!b && xsAttributeGroupDecl.fIDAttrName != null && fName != null) {
            this.reportSchemaError("cvc-complex-type.5.2", new Object[] { qName.rawname, fName, xsAttributeGroupDecl.fIDAttrName });
        }
    }
    
    void processOneAttribute(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, final int n, final XSAttributeDecl fDeclaration, final XSAttributeUseImpl xsAttributeUseImpl, final AttributePSVImpl attributePSVImpl) {
        final String value = xmlAttributes.getValue(n);
        this.fXSIErrorReporter.pushContext();
        final XSSimpleType fType = fDeclaration.fType;
        Object validate = null;
        try {
            validate = fType.validate(value, this.fValidationState, this.fValidatedInfo);
            if (this.fNormalizeData) {
                xmlAttributes.setValue(n, this.fValidatedInfo.normalizedValue);
            }
            if (fType.getVariety() == 1 && fType.getPrimitiveKind() == 20) {
                final org.apache.xerces.xni.QName qName2 = (org.apache.xerces.xni.QName)validate;
                final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(qName2.uri);
                if (grammar != null) {
                    this.fNotation = grammar.getGlobalNotationDecl(qName2.localpart);
                }
            }
        }
        catch (final InvalidDatatypeValueException ex) {
            this.reportSchemaError(ex.getKey(), ex.getArgs());
            this.reportSchemaError("cvc-attribute.3", new Object[] { qName.rawname, this.fTempQName.rawname, value, (fType instanceof XSSimpleTypeDecl) ? ((XSSimpleTypeDecl)fType).getTypeName() : fType.getName() });
        }
        if (validate != null && fDeclaration.getConstraintType() == 2 && !EqualityHelper.isEqual(this.fValidatedInfo, fDeclaration.fDefault, this.fSchemaVersion)) {
            this.reportSchemaError("cvc-attribute.4", new Object[] { qName.rawname, this.fTempQName.rawname, value, fDeclaration.fDefault.stringValue() });
        }
        if (validate != null && xsAttributeUseImpl != null && xsAttributeUseImpl.fConstraintType == 2 && !EqualityHelper.isEqual(this.fValidatedInfo, xsAttributeUseImpl.fDefault, this.fSchemaVersion)) {
            this.reportSchemaError("cvc-complex-type.3.1", new Object[] { qName.rawname, this.fTempQName.rawname, value, xsAttributeUseImpl.fDefault.stringValue() });
        }
        if (this.fIdConstraint) {
            attributePSVImpl.fValue.copyFrom(this.fValidatedInfo);
        }
        if (this.fAugPSVI) {
            attributePSVImpl.fDeclaration = fDeclaration;
            attributePSVImpl.fTypeDecl = fType;
            attributePSVImpl.fValue.copyFrom(this.fValidatedInfo);
            attributePSVImpl.fValidationAttempted = 2;
            this.fNNoneValidationDepth = this.fElementDepth;
            final String[] mergeContext = this.fXSIErrorReporter.mergeContext();
            attributePSVImpl.fErrors = mergeContext;
            attributePSVImpl.fValidity = (short)((mergeContext == null) ? 2 : 1);
        }
    }
    
    void addDefaultAttributes(final org.apache.xerces.xni.QName qName, final XMLAttributes xmlAttributes, final XSAttributeGroupDecl xsAttributeGroupDecl) {
        final XSObjectList attributeUses = xsAttributeGroupDecl.getAttributeUses();
        for (int length = attributeUses.getLength(), i = 0; i < length; ++i) {
            final XSAttributeUseImpl xsAttributeUseImpl = (XSAttributeUseImpl)attributeUses.item(i);
            final XSAttributeDecl fAttrDecl = xsAttributeUseImpl.fAttrDecl;
            short n = xsAttributeUseImpl.fConstraintType;
            ValidatedInfo validatedInfo = xsAttributeUseImpl.fDefault;
            if (n == 0) {
                n = fAttrDecl.getConstraintType();
                validatedInfo = fAttrDecl.fDefault;
            }
            final boolean b = xmlAttributes.getValue(fAttrDecl.fTargetNamespace, fAttrDecl.fName) != null;
            if (xsAttributeUseImpl.fUse == 1 && !b) {
                this.reportSchemaError("cvc-complex-type.4", new Object[] { qName.rawname, fAttrDecl.fName });
            }
            if (!b && n != 0) {
                final XSSimpleType fType = fAttrDecl.fType;
                final boolean needFacetChecking = this.fValidationState.needFacetChecking();
                try {
                    this.fValidationState.setFacetChecking(false);
                    fType.validate(this.fValidationState, validatedInfo);
                }
                catch (final InvalidDatatypeValueException ex) {
                    this.reportSchemaError(ex.getKey(), ex.getArgs());
                }
                this.fValidationState.setFacetChecking(needFacetChecking);
                final org.apache.xerces.xni.QName qName2 = new org.apache.xerces.xni.QName(null, fAttrDecl.fName, fAttrDecl.fName, fAttrDecl.fTargetNamespace);
                final String s = (validatedInfo != null) ? validatedInfo.stringValue() : "";
                int n2;
                if (xmlAttributes instanceof XMLAttributesImpl) {
                    final XMLAttributesImpl xmlAttributesImpl = (XMLAttributesImpl)xmlAttributes;
                    n2 = xmlAttributesImpl.getLength();
                    xmlAttributesImpl.addAttributeNS(qName2, "CDATA", s);
                }
                else {
                    n2 = xmlAttributes.addAttribute(qName2, "CDATA", s);
                }
                if (this.fAugPSVI) {
                    final Augmentations augmentations = xmlAttributes.getAugmentations(n2);
                    final AttributePSVImpl attributePSVImpl = new AttributePSVImpl();
                    augmentations.putItem("ATTRIBUTE_PSVI", attributePSVImpl);
                    attributePSVImpl.fDeclaration = fAttrDecl;
                    attributePSVImpl.fTypeDecl = fAttrDecl.fType;
                    attributePSVImpl.fValue.copyFrom(validatedInfo);
                    attributePSVImpl.fValidationContext = this.fValidationRoot;
                    attributePSVImpl.fValidity = 2;
                    attributePSVImpl.fValidationAttempted = 2;
                    attributePSVImpl.fSpecified = true;
                }
            }
        }
    }
    
    void processElementContent(final org.apache.xerces.xni.QName qName) {
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fDefault != null && !this.fSawText && !this.fSubElement && !this.fNil) {
            final String stringValue = this.fCurrentElemDecl.fDefault.stringValue();
            final int length = stringValue.length();
            if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < length) {
                this.fNormalizedStr.ch = new char[length];
            }
            stringValue.getChars(0, length, this.fNormalizedStr.ch, 0);
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.length = length;
            this.fDefaultValue = this.fNormalizedStr;
        }
        this.fValidatedInfo.normalizedValue = null;
        if (this.fNil && (this.fSubElement || this.fSawText)) {
            this.reportSchemaError("cvc-elt.3.2.1", new Object[] { qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
        }
        this.fValidatedInfo.reset();
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() != 0 && !this.fSubElement && !this.fSawText && !this.fNil) {
            if (this.fCurrentType != this.fCurrentElemDecl.fType && this.fXSConstraints.ElementDefaultValidImmediate(this.fCurrentType, this.fCurrentElemDecl.fDefault.stringValue(), this.fState4XsiType, null) == null) {
                this.reportSchemaError("cvc-elt.5.1.1", new Object[] { qName.rawname, this.fCurrentType.getName(), this.fCurrentElemDecl.fDefault.stringValue() });
            }
            this.elementLocallyValidType(qName, this.fCurrentElemDecl.fDefault.stringValue());
        }
        else {
            final Object elementLocallyValidType = this.elementLocallyValidType(qName, this.fBuffer);
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2 && !this.fNil) {
                final String string = this.fBuffer.toString();
                if (this.fSubElement) {
                    this.reportSchemaError("cvc-elt.5.2.2.1", new Object[] { qName.rawname });
                }
                if (this.fCurrentType.getTypeCategory() == 15) {
                    final XSComplexTypeDecl xsComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
                    if (xsComplexTypeDecl.fContentType == 3) {
                        if (!this.fCurrentElemDecl.fDefault.normalizedValue.equals(string)) {
                            this.reportSchemaError("cvc-elt.5.2.2.2.1", new Object[] { qName.rawname, string, this.fCurrentElemDecl.fDefault.normalizedValue });
                        }
                    }
                    else if (xsComplexTypeDecl.fContentType == 1 && elementLocallyValidType != null && !EqualityHelper.isEqual(this.fValidatedInfo, this.fCurrentElemDecl.fDefault, this.fSchemaVersion)) {
                        this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { qName.rawname, string, this.fCurrentElemDecl.fDefault.stringValue() });
                    }
                }
                else if (this.fCurrentType.getTypeCategory() == 16 && elementLocallyValidType != null && !EqualityHelper.isEqual(this.fValidatedInfo, this.fCurrentElemDecl.fDefault, this.fSchemaVersion)) {
                    this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { qName.rawname, string, this.fCurrentElemDecl.fDefault.stringValue() });
                }
            }
        }
        if (this.fDefaultValue == null && this.fNormalizeData && this.fDocumentHandler != null && this.fUnionType) {
            String s = this.fValidatedInfo.normalizedValue;
            if (s == null) {
                s = this.fBuffer.toString();
            }
            final int length2 = s.length();
            if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < length2) {
                this.fNormalizedStr.ch = new char[length2];
            }
            s.getChars(0, length2, this.fNormalizedStr.ch, 0);
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.length = length2;
            this.fDocumentHandler.characters(this.fNormalizedStr, null);
        }
    }
    
    Object elementLocallyValidType(final org.apache.xerces.xni.QName qName, final Object o) {
        if (this.fCurrentType == null) {
            return null;
        }
        Object o2 = null;
        if (this.fCurrentType.getTypeCategory() == 16) {
            if (this.fSubElement) {
                this.reportSchemaError("cvc-type.3.1.2", new Object[] { qName.rawname });
            }
            if (!this.fNil) {
                final XSSimpleType xsSimpleType = (XSSimpleType)this.fCurrentType;
                try {
                    if (!this.fNormalizeData || this.fUnionType) {
                        this.fValidationState.setNormalizationRequired(true);
                    }
                    o2 = xsSimpleType.validate(o, this.fValidationState, this.fValidatedInfo);
                    if (this.fSchemaVersion == 4) {
                        this.fAssertionValidator.extraCheckForSTUnionAssertsElem(xsSimpleType, String.valueOf(o), this.fValidatedInfo);
                    }
                }
                catch (final InvalidDatatypeValueException ex) {
                    this.fIsAssertProcessingNeededForSTUnionElem = false;
                    this.reportSchemaError(ex.getKey(), ex.getArgs());
                    this.reportSchemaError("cvc-type.3.1.3", new Object[] { qName.rawname, o });
                }
            }
        }
        else {
            o2 = this.elementLocallyValidComplexType(qName, o);
        }
        return o2;
    }
    
    Object elementLocallyValidComplexType(final org.apache.xerces.xni.QName qName, final Object o) {
        Object validate = null;
        final XSComplexTypeDecl xsComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
        if (!this.fNil) {
            if (xsComplexTypeDecl.fContentType == 0 && (this.fSubElement || this.fSawText)) {
                this.reportSchemaError("cvc-complex-type.2.1", new Object[] { qName.rawname });
            }
            else if (xsComplexTypeDecl.fContentType == 1) {
                if (this.fSubElement) {
                    this.reportSchemaError("cvc-complex-type.2.2", new Object[] { qName.rawname });
                }
                final XSSimpleType fxsSimpleType = xsComplexTypeDecl.fXSSimpleType;
                try {
                    if (!this.fNormalizeData || this.fUnionType) {
                        this.fValidationState.setNormalizationRequired(true);
                    }
                    validate = fxsSimpleType.validate(o, this.fValidationState, this.fValidatedInfo);
                    if (this.fSchemaVersion == 4) {
                        this.fAssertionValidator.extraCheckForSTUnionAssertsElem(fxsSimpleType, String.valueOf(o), this.fValidatedInfo);
                    }
                }
                catch (final InvalidDatatypeValueException ex) {
                    this.fIsAssertProcessingNeededForSTUnionElem = false;
                    this.reportSchemaError(ex.getKey(), ex.getArgs());
                    this.reportSchemaError("cvc-complex-type.2.2", new Object[] { qName.rawname });
                }
            }
            else if (xsComplexTypeDecl.fContentType == 2 && this.fSawCharacters) {
                this.reportSchemaError("cvc-complex-type.2.3", new Object[] { qName.rawname });
            }
            if ((xsComplexTypeDecl.fContentType == 2 || xsComplexTypeDecl.fContentType == 3) && this.fCurrCMState[0] >= 0 && !this.fCurrentCM.endContentModel(this.fCurrCMState)) {
                final String expectedStr = this.expectedStr(this.fCurrentCM.whatCanGoHere(this.fCurrCMState));
                final int[] occurenceInfo = this.fCurrentCM.occurenceInfo(this.fCurrCMState);
                if (occurenceInfo != null) {
                    final int n = occurenceInfo[0];
                    final int n2 = occurenceInfo[2];
                    if (n2 < n) {
                        final int n3 = n - n2;
                        if (n3 > 1) {
                            this.reportSchemaError("cvc-complex-type.2.4.j", new Object[] { qName.rawname, this.fCurrentCM.getTermName(occurenceInfo[3]), Integer.toString(n), Integer.toString(n3) });
                        }
                        else {
                            this.reportSchemaError("cvc-complex-type.2.4.i", new Object[] { qName.rawname, this.fCurrentCM.getTermName(occurenceInfo[3]), Integer.toString(n) });
                        }
                    }
                    else {
                        this.reportSchemaError("cvc-complex-type.2.4.b", new Object[] { qName.rawname, expectedStr });
                    }
                }
                else {
                    this.reportSchemaError("cvc-complex-type.2.4.b", new Object[] { qName.rawname, expectedStr });
                }
            }
        }
        return validate;
    }
    
    void processRootTypeQName(final QName qName) {
        String addSymbol = this.fSymbolTable.addSymbol(qName.getNamespaceURI());
        if (addSymbol != null && addSymbol.equals("")) {
            addSymbol = null;
        }
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(addSymbol)) {
            final String localPart = qName.getLocalPart();
            if (this.isValidBuiltInTypeName(localPart)) {
                this.fCurrentType = SchemaGrammar.getS4SGrammar(this.fSchemaVersion).getGlobalTypeDecl(localPart);
            }
        }
        else {
            final SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)5, addSymbol, null, null, null);
            if (schemaGrammar != null) {
                this.fCurrentType = schemaGrammar.getGlobalTypeDecl(qName.getLocalPart());
            }
        }
        if (this.fCurrentType == null) {
            this.reportSchemaError("cvc-type.1", new Object[] { qName.getPrefix().equals("") ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart()) });
        }
    }
    
    void processRootElementDeclQName(final QName qName, final org.apache.xerces.xni.QName qName2) {
        String addSymbol = this.fSymbolTable.addSymbol(qName.getNamespaceURI());
        if (addSymbol != null && addSymbol.equals("")) {
            addSymbol = null;
        }
        final SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)5, addSymbol, null, null, null);
        if (schemaGrammar != null) {
            this.fCurrentElemDecl = schemaGrammar.getGlobalElementDecl(qName.getLocalPart());
        }
        if (this.fCurrentElemDecl == null) {
            this.reportSchemaError("cvc-elt.1.a", new Object[] { qName.getPrefix().equals("") ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart()) });
        }
        else {
            this.checkElementMatchesRootElementDecl(this.fCurrentElemDecl, qName2);
        }
    }
    
    void checkElementMatchesRootElementDecl(final XSElementDecl xsElementDecl, final org.apache.xerces.xni.QName qName) {
        if (qName.localpart != xsElementDecl.fName || qName.uri != xsElementDecl.fTargetNamespace) {
            this.reportSchemaError("cvc-elt.1.b", new Object[] { qName.rawname, xsElementDecl.fName });
        }
    }
    
    private String expectedStr(final Vector vector) {
        final StringBuffer sb = new StringBuffer("{");
        for (int size = vector.size(), i = 0; i < size; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(vector.elementAt(i).toString());
        }
        sb.append('}');
        return sb.toString();
    }
}
