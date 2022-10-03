package org.apache.xerces.impl.dtd;

import org.apache.xerces.util.XMLChar;
import java.util.Iterator;
import java.util.Map;
import org.apache.xerces.xni.grammars.Grammar;
import java.util.StringTokenizer;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import java.util.Locale;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLDTDContentModelFilter;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLComponent;

public class XMLDTDProcessor implements XMLComponent, XMLDTDFilter, XMLDTDContentModelFilter
{
    private static final int TOP_LEVEL_SCOPE = -1;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected boolean fValidation;
    protected boolean fDTDValidation;
    protected boolean fWarnDuplicateAttdef;
    protected boolean fWarnOnUndeclaredElemdef;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected DTDGrammarBucket fGrammarBucket;
    protected XMLDTDValidator fValidator;
    protected XMLGrammarPool fGrammarPool;
    protected Locale fLocale;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDSource fDTDSource;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLDTDContentModelSource fDTDContentModelSource;
    protected DTDGrammar fDTDGrammar;
    private boolean fPerformValidation;
    protected boolean fInDTDIgnore;
    private boolean fMixed;
    private final XMLEntityDecl fEntityDecl;
    private final HashMap fNDataDeclNotations;
    private String fDTDElementDeclName;
    private final ArrayList fMixedElementTypes;
    private final ArrayList fDTDElementDecls;
    private HashMap fTableOfIDAttributeNames;
    private HashMap fTableOfNOTATIONAttributeNames;
    private HashMap fNotationEnumVals;
    
    public XMLDTDProcessor() {
        this.fEntityDecl = new XMLEntityDecl();
        this.fNDataDeclNotations = new HashMap();
        this.fDTDElementDeclName = null;
        this.fMixedElementTypes = new ArrayList();
        this.fDTDElementDecls = new ArrayList();
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        boolean feature;
        try {
            feature = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings");
        }
        catch (final XMLConfigurationException ex) {
            feature = true;
        }
        if (!feature) {
            this.reset();
            return;
        }
        try {
            this.fValidation = xmlComponentManager.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final XMLConfigurationException ex2) {
            this.fValidation = false;
        }
        try {
            this.fDTDValidation = !xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema");
        }
        catch (final XMLConfigurationException ex3) {
            this.fDTDValidation = true;
        }
        try {
            this.fWarnDuplicateAttdef = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef");
        }
        catch (final XMLConfigurationException ex4) {
            this.fWarnDuplicateAttdef = false;
        }
        try {
            this.fWarnOnUndeclaredElemdef = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef");
        }
        catch (final XMLConfigurationException ex5) {
            this.fWarnOnUndeclaredElemdef = false;
        }
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        try {
            this.fGrammarPool = (XMLGrammarPool)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException ex6) {
            this.fGrammarPool = null;
        }
        try {
            this.fValidator = (XMLDTDValidator)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd");
        }
        catch (final XMLConfigurationException ex7) {
            this.fValidator = null;
        }
        catch (final ClassCastException ex8) {
            this.fValidator = null;
        }
        if (this.fValidator != null) {
            this.fGrammarBucket = this.fValidator.getGrammarBucket();
        }
        else {
            this.fGrammarBucket = null;
        }
        this.reset();
    }
    
    protected void reset() {
        this.fDTDGrammar = null;
        this.fInDTDIgnore = false;
        this.fNDataDeclNotations.clear();
        if (this.fValidation) {
            if (this.fNotationEnumVals == null) {
                this.fNotationEnumVals = new HashMap();
            }
            this.fNotationEnumVals.clear();
            this.fTableOfIDAttributeNames = new HashMap();
            this.fTableOfNOTATIONAttributeNames = new HashMap();
        }
    }
    
    public String[] getRecognizedFeatures() {
        return XMLDTDProcessor.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
    }
    
    public String[] getRecognizedProperties() {
        return XMLDTDProcessor.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLDTDProcessor.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDTDProcessor.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLDTDProcessor.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLDTDProcessor.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDTDProcessor.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLDTDProcessor.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public void setDTDHandler(final XMLDTDHandler fdtdHandler) {
        this.fDTDHandler = fdtdHandler;
    }
    
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler fdtdContentModelHandler) {
        this.fDTDContentModelHandler = fdtdContentModelHandler;
    }
    
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startExternalSubset(xmlResourceIdentifier, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(xmlResourceIdentifier, augmentations);
        }
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endExternalSubset(augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }
    
    protected static void checkStandaloneEntityRef(final String s, final DTDGrammar dtdGrammar, final XMLEntityDecl xmlEntityDecl, final XMLErrorReporter xmlErrorReporter) throws XNIException {
        final int entityDeclIndex = dtdGrammar.getEntityDeclIndex(s);
        if (entityDeclIndex > -1) {
            dtdGrammar.getEntityDecl(entityDeclIndex, xmlEntityDecl);
            if (xmlEntityDecl.inExternal) {
                xmlErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { s }, (short)1);
            }
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.comment(xmlString, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(xmlString, augmentations);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.processingInstruction(s, xmlString, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(s, xmlString, augmentations);
        }
    }
    
    public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
        this.fNDataDeclNotations.clear();
        this.fDTDElementDecls.clear();
        if (!this.fGrammarBucket.getActiveGrammar().isImmutable()) {
            this.fDTDGrammar = this.fGrammarBucket.getActiveGrammar();
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startDTD(xmlLocator, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(xmlLocator, augmentations);
        }
    }
    
    public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.ignoredCharacters(xmlString, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(xmlString, augmentations);
        }
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.textDecl(s, s2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.textDecl(s, s2, augmentations);
        }
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fDTDGrammar != null && this.fGrammarBucket.getStandalone()) {
            checkStandaloneEntityRef(s, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startParameterEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endParameterEntity(s, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(s, augmentations);
        }
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            if (this.fDTDElementDecls.contains(s)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_ALREADY_DECLARED", new Object[] { s }, (short)1);
            }
            else {
                this.fDTDElementDecls.add(s);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.elementDecl(s, s2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(s, s2, augmentations);
        }
    }
    
    public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startAttlist(s, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(s, augmentations);
        }
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (s3 != XMLSymbols.fCDATASymbol && xmlString != null) {
            this.normalizeDefaultAttrValue(xmlString);
        }
        if (this.fValidation) {
            boolean b = false;
            final DTDGrammar dtdGrammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            if (dtdGrammar.getAttributeDeclIndex(dtdGrammar.getElementDeclIndex(s), s2) != -1) {
                b = true;
                if (this.fWarnDuplicateAttdef) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION", new Object[] { s, s2 }, (short)0);
                }
            }
            if (s3 == XMLSymbols.fIDSymbol) {
                if (xmlString != null && xmlString.length != 0 && (s4 == null || (s4 != XMLSymbols.fIMPLIEDSymbol && s4 != XMLSymbols.fREQUIREDSymbol))) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDDefaultTypeInvalid", new Object[] { s2 }, (short)1);
                }
                if (!this.fTableOfIDAttributeNames.containsKey(s)) {
                    this.fTableOfIDAttributeNames.put(s, s2);
                }
                else if (!b) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_ID_ATTRIBUTE", new Object[] { s, this.fTableOfIDAttributeNames.get(s), s2 }, (short)1);
                }
            }
            if (s3 == XMLSymbols.fNOTATIONSymbol) {
                for (int i = 0; i < array.length; ++i) {
                    this.fNotationEnumVals.put(array[i], s2);
                }
                if (!this.fTableOfNOTATIONAttributeNames.containsKey(s)) {
                    this.fTableOfNOTATIONAttributeNames.put(s, s2);
                }
                else if (!b) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_NOTATION_ATTRIBUTE", new Object[] { s, this.fTableOfNOTATIONAttributeNames.get(s), s2 }, (short)1);
                }
            }
            Label_0466: {
                if (s3 == XMLSymbols.fENUMERATIONSymbol || s3 == XMLSymbols.fNOTATIONSymbol) {
                    for (int j = 0; j < array.length; ++j) {
                        for (int k = j + 1; k < array.length; ++k) {
                            if (array[j].equals(array[k])) {
                                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", (s3 == XMLSymbols.fENUMERATIONSymbol) ? "MSG_DISTINCT_TOKENS_IN_ENUMERATION" : "MSG_DISTINCT_NOTATION_IN_ENUMERATION", new Object[] { s, array[j], s2 }, (short)1);
                                break Label_0466;
                            }
                        }
                    }
                }
            }
            boolean b2 = true;
            if (xmlString != null && (s4 == null || (s4 != null && s4 == XMLSymbols.fFIXEDSymbol))) {
                final String string = xmlString.toString();
                if (s3 == XMLSymbols.fNMTOKENSSymbol || s3 == XMLSymbols.fENTITIESSymbol || s3 == XMLSymbols.fIDREFSSymbol) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                    if (stringTokenizer.hasMoreTokens()) {
                        do {
                            final String nextToken = stringTokenizer.nextToken();
                            if (s3 == XMLSymbols.fNMTOKENSSymbol) {
                                if (!this.isValidNmtoken(nextToken)) {
                                    b2 = false;
                                    break;
                                }
                                continue;
                            }
                            else {
                                if ((s3 == XMLSymbols.fENTITIESSymbol || s3 == XMLSymbols.fIDREFSSymbol) && !this.isValidName(nextToken)) {
                                    b2 = false;
                                    break;
                                }
                                continue;
                            }
                        } while (stringTokenizer.hasMoreTokens());
                    }
                }
                else {
                    if (s3 == XMLSymbols.fENTITYSymbol || s3 == XMLSymbols.fIDSymbol || s3 == XMLSymbols.fIDREFSymbol || s3 == XMLSymbols.fNOTATIONSymbol) {
                        if (!this.isValidName(string)) {
                            b2 = false;
                        }
                    }
                    else if ((s3 == XMLSymbols.fNMTOKENSymbol || s3 == XMLSymbols.fENUMERATIONSymbol) && !this.isValidNmtoken(string)) {
                        b2 = false;
                    }
                    if (s3 == XMLSymbols.fNOTATIONSymbol || s3 == XMLSymbols.fENUMERATIONSymbol) {
                        b2 = false;
                        for (int l = 0; l < array.length; ++l) {
                            if (xmlString.equals(array[l])) {
                                b2 = true;
                            }
                        }
                    }
                }
                if (!b2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATT_DEFAULT_INVALID", new Object[] { s2, string }, (short)1);
                }
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.attributeDecl(s, s2, s3, array, s4, xmlString, xmlString2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(s, s2, s3, array, s4, xmlString, xmlString2, augmentations);
        }
    }
    
    public void endAttlist(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endAttlist(augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (((this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar()).getEntityDeclIndex(s) == -1) {
            if (this.fDTDGrammar != null) {
                this.fDTDGrammar.internalEntityDecl(s, xmlString, xmlString2, augmentations);
            }
            if (this.fDTDHandler != null) {
                this.fDTDHandler.internalEntityDecl(s, xmlString, xmlString2, augmentations);
            }
        }
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (((this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar()).getEntityDeclIndex(s) == -1) {
            if (this.fDTDGrammar != null) {
                this.fDTDGrammar.externalEntityDecl(s, xmlResourceIdentifier, augmentations);
            }
            if (this.fDTDHandler != null) {
                this.fDTDHandler.externalEntityDecl(s, xmlResourceIdentifier, augmentations);
            }
        }
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            this.fNDataDeclNotations.put(s, s2);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.unparsedEntityDecl(s, xmlResourceIdentifier, s2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fValidation && ((this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar()).getNotationDeclIndex(s) != -1) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UniqueNotationName", new Object[] { s }, (short)1);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.notationDecl(s, xmlResourceIdentifier, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(s, xmlResourceIdentifier, augmentations);
        }
    }
    
    public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
        this.fInDTDIgnore = (n == 1);
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startConditional(n, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(n, augmentations);
        }
    }
    
    public void endConditional(final Augmentations augmentations) throws XNIException {
        this.fInDTDIgnore = false;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endConditional(augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endDTD(augmentations);
            if (this.fGrammarPool != null) {
                this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { this.fDTDGrammar });
            }
        }
        if (this.fValidation) {
            final DTDGrammar dtdGrammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            final Iterator iterator = this.fNDataDeclNotations.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                final String s = (String)entry.getValue();
                if (dtdGrammar.getNotationDeclIndex(s) == -1) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_UNPARSED_ENTITYDECL", new Object[] { entry.getKey(), s }, (short)1);
                }
            }
            final Iterator iterator2 = this.fNotationEnumVals.entrySet().iterator();
            while (iterator2.hasNext()) {
                final Map.Entry entry2 = (Map.Entry)iterator2.next();
                final String s2 = (String)entry2.getKey();
                if (dtdGrammar.getNotationDeclIndex(s2) == -1) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_NOTATIONTYPE_ATTRIBUTE", new Object[] { entry2.getValue(), s2 }, (short)1);
                }
            }
            final Iterator iterator3 = this.fTableOfNOTATIONAttributeNames.entrySet().iterator();
            while (iterator3.hasNext()) {
                final Map.Entry entry3 = (Map.Entry)iterator3.next();
                final String s3 = (String)entry3.getKey();
                if (dtdGrammar.getContentSpecType(dtdGrammar.getElementDeclIndex(s3)) == 1) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NoNotationOnEmptyElement", new Object[] { s3, entry3.getValue() }, (short)1);
                }
            }
            this.fTableOfIDAttributeNames = null;
            this.fTableOfNOTATIONAttributeNames = null;
            if (this.fWarnOnUndeclaredElemdef) {
                this.checkDeclaredElements(dtdGrammar);
            }
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
    }
    
    public void setDTDSource(final XMLDTDSource fdtdSource) {
        this.fDTDSource = fdtdSource;
    }
    
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    public void setDTDContentModelSource(final XMLDTDContentModelSource fdtdContentModelSource) {
        this.fDTDContentModelSource = fdtdContentModelSource;
    }
    
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }
    
    public void startContentModel(final String fdtdElementDeclName, final Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            this.fDTDElementDeclName = fdtdElementDeclName;
            this.fMixedElementTypes.clear();
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startContentModel(fdtdElementDeclName, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startContentModel(fdtdElementDeclName, augmentations);
        }
    }
    
    public void any(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.any(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.any(augmentations);
        }
    }
    
    public void empty(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.empty(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.empty(augmentations);
        }
    }
    
    public void startGroup(final Augmentations augmentations) throws XNIException {
        this.fMixed = false;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startGroup(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startGroup(augmentations);
        }
    }
    
    public void pcdata(final Augmentations augmentations) {
        this.fMixed = true;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.pcdata(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.pcdata(augmentations);
        }
    }
    
    public void element(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fMixed && this.fValidation) {
            if (this.fMixedElementTypes.contains(s)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "DuplicateTypeInMixedContent", new Object[] { this.fDTDElementDeclName, s }, (short)1);
            }
            else {
                this.fMixedElementTypes.add(s);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.element(s, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.element(s, augmentations);
        }
    }
    
    public void separator(final short n, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.separator(n, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.separator(n, augmentations);
        }
    }
    
    public void occurrence(final short n, final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.occurrence(n, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.occurrence(n, augmentations);
        }
    }
    
    public void endGroup(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endGroup(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endGroup(augmentations);
        }
    }
    
    public void endContentModel(final Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endContentModel(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endContentModel(augmentations);
        }
    }
    
    private boolean normalizeDefaultAttrValue(final XMLString xmlString) {
        int n = 1;
        int offset = xmlString.offset;
        final int n2 = xmlString.offset + xmlString.length;
        for (int i = xmlString.offset; i < n2; ++i) {
            if (xmlString.ch[i] == ' ') {
                if (n == 0) {
                    xmlString.ch[offset++] = ' ';
                    n = 1;
                }
            }
            else {
                if (offset != i) {
                    xmlString.ch[offset] = xmlString.ch[i];
                }
                ++offset;
                n = 0;
            }
        }
        if (offset != n2) {
            if (n != 0) {
                --offset;
            }
            xmlString.length = offset - xmlString.offset;
            return true;
        }
        return false;
    }
    
    protected boolean isValidNmtoken(final String s) {
        return XMLChar.isValidNmtoken(s);
    }
    
    protected boolean isValidName(final String s) {
        return XMLChar.isValidName(s);
    }
    
    private void checkDeclaredElements(final DTDGrammar dtdGrammar) {
        int i = dtdGrammar.getFirstElementDeclIndex();
        final XMLContentSpec xmlContentSpec = new XMLContentSpec();
        while (i >= 0) {
            final short contentSpecType = dtdGrammar.getContentSpecType(i);
            if (contentSpecType == 3 || contentSpecType == 2) {
                this.checkDeclaredElements(dtdGrammar, i, dtdGrammar.getContentSpecIndex(i), xmlContentSpec);
            }
            i = dtdGrammar.getNextElementDeclIndex(i);
        }
    }
    
    private void checkDeclaredElements(final DTDGrammar dtdGrammar, final int n, final int n2, final XMLContentSpec xmlContentSpec) {
        dtdGrammar.getContentSpec(n2, xmlContentSpec);
        if (xmlContentSpec.type == 0) {
            final String s = (String)xmlContentSpec.value;
            if (s != null && dtdGrammar.getElementDeclIndex(s) == -1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UndeclaredElementInContentSpec", new Object[] { dtdGrammar.getElementDeclName(n).rawname, s }, (short)0);
            }
        }
        else if (xmlContentSpec.type == 4 || xmlContentSpec.type == 5) {
            final int n3 = ((int[])xmlContentSpec.value)[0];
            final int n4 = ((int[])xmlContentSpec.otherValue)[0];
            this.checkDeclaredElements(dtdGrammar, n, n3, xmlContentSpec);
            this.checkDeclaredElements(dtdGrammar, n, n4, xmlContentSpec);
        }
        else if (xmlContentSpec.type == 2 || xmlContentSpec.type == 1 || xmlContentSpec.type == 3) {
            this.checkDeclaredElements(dtdGrammar, n, ((int[])xmlContentSpec.value)[0], xmlContentSpec);
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs" };
        FEATURE_DEFAULTS = new Boolean[] { null, Boolean.FALSE, Boolean.FALSE, null };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null };
    }
}
