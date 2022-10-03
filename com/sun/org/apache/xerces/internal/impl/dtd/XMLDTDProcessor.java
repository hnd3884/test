package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.util.Iterator;
import java.util.Map;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import java.util.ArrayList;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

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
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        final boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
        if (!parser_settings) {
            this.reset();
            return;
        }
        this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
        this.fDTDValidation = !componentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
        this.fWarnDuplicateAttdef = componentManager.getFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", false);
        this.fWarnOnUndeclaredElemdef = componentManager.getFeature("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", false);
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
        try {
            this.fValidator = (XMLDTDValidator)componentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd", null);
        }
        catch (final ClassCastException e) {
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
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLDTDProcessor.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLDTDProcessor.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLDTDProcessor.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDTDProcessor.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLDTDProcessor.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLDTDProcessor.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDTDProcessor.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLDTDProcessor.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public void setDTDHandler(final XMLDTDHandler dtdHandler) {
        this.fDTDHandler = dtdHandler;
    }
    
    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    @Override
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler dtdContentModelHandler) {
        this.fDTDContentModelHandler = dtdContentModelHandler;
    }
    
    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startExternalSubset(identifier, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(identifier, augs);
        }
    }
    
    @Override
    public void endExternalSubset(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endExternalSubset(augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augs);
        }
    }
    
    protected static void checkStandaloneEntityRef(final String name, final DTDGrammar grammar, final XMLEntityDecl tempEntityDecl, final XMLErrorReporter errorReporter) throws XNIException {
        final int entIndex = grammar.getEntityDeclIndex(name);
        if (entIndex > -1) {
            grammar.getEntityDecl(entIndex, tempEntityDecl);
            if (tempEntityDecl.inExternal) {
                errorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { name }, (short)1);
            }
        }
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.comment(text, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(text, augs);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.processingInstruction(target, data, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(target, data, augs);
        }
    }
    
    @Override
    public void startDTD(final XMLLocator locator, final Augmentations augs) throws XNIException {
        this.fNDataDeclNotations.clear();
        this.fDTDElementDecls.clear();
        if (!this.fGrammarBucket.getActiveGrammar().isImmutable()) {
            this.fDTDGrammar = this.fGrammarBucket.getActiveGrammar();
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startDTD(locator, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(locator, augs);
        }
    }
    
    @Override
    public void ignoredCharacters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.ignoredCharacters(text, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(text, augs);
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.textDecl(version, encoding, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.textDecl(version, encoding, augs);
        }
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fPerformValidation && this.fDTDGrammar != null && this.fGrammarBucket.getStandalone()) {
            checkStandaloneEntityRef(name, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startParameterEntity(name, identifier, encoding, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(name, identifier, encoding, augs);
        }
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endParameterEntity(name, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(name, augs);
        }
    }
    
    @Override
    public void elementDecl(final String name, final String contentModel, final Augmentations augs) throws XNIException {
        if (this.fValidation) {
            if (this.fDTDElementDecls.contains(name)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_ALREADY_DECLARED", new Object[] { name }, (short)1);
            }
            else {
                this.fDTDElementDecls.add(name);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.elementDecl(name, contentModel, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(name, contentModel, augs);
        }
    }
    
    @Override
    public void startAttlist(final String elementName, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startAttlist(elementName, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(elementName, augs);
        }
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augs) throws XNIException {
        if (type != XMLSymbols.fCDATASymbol && defaultValue != null) {
            this.normalizeDefaultAttrValue(defaultValue);
        }
        if (this.fValidation) {
            boolean duplicateAttributeDef = false;
            final DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            final int elementIndex = grammar.getElementDeclIndex(elementName);
            if (grammar.getAttributeDeclIndex(elementIndex, attributeName) != -1) {
                duplicateAttributeDef = true;
                if (this.fWarnDuplicateAttdef) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION", new Object[] { elementName, attributeName }, (short)0);
                }
            }
            if (type == XMLSymbols.fIDSymbol) {
                if (defaultValue != null && defaultValue.length != 0 && (defaultType == null || (defaultType != XMLSymbols.fIMPLIEDSymbol && defaultType != XMLSymbols.fREQUIREDSymbol))) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDDefaultTypeInvalid", new Object[] { attributeName }, (short)1);
                }
                if (!this.fTableOfIDAttributeNames.containsKey(elementName)) {
                    this.fTableOfIDAttributeNames.put(elementName, attributeName);
                }
                else if (!duplicateAttributeDef) {
                    final String previousIDAttributeName = this.fTableOfIDAttributeNames.get(elementName);
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_ID_ATTRIBUTE", new Object[] { elementName, previousIDAttributeName, attributeName }, (short)1);
                }
            }
            if (type == XMLSymbols.fNOTATIONSymbol) {
                for (int i = 0; i < enumeration.length; ++i) {
                    this.fNotationEnumVals.put(enumeration[i], attributeName);
                }
                if (!this.fTableOfNOTATIONAttributeNames.containsKey(elementName)) {
                    this.fTableOfNOTATIONAttributeNames.put(elementName, attributeName);
                }
                else if (!duplicateAttributeDef) {
                    final String previousNOTATIONAttributeName = this.fTableOfNOTATIONAttributeNames.get(elementName);
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_NOTATION_ATTRIBUTE", new Object[] { elementName, previousNOTATIONAttributeName, attributeName }, (short)1);
                }
            }
            Label_0466: {
                if (type == XMLSymbols.fENUMERATIONSymbol || type == XMLSymbols.fNOTATIONSymbol) {
                    for (int i = 0; i < enumeration.length; ++i) {
                        for (int j = i + 1; j < enumeration.length; ++j) {
                            if (enumeration[i].equals(enumeration[j])) {
                                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", (type == XMLSymbols.fENUMERATIONSymbol) ? "MSG_DISTINCT_TOKENS_IN_ENUMERATION" : "MSG_DISTINCT_NOTATION_IN_ENUMERATION", new Object[] { elementName, enumeration[i], attributeName }, (short)1);
                                break Label_0466;
                            }
                        }
                    }
                }
            }
            boolean ok = true;
            if (defaultValue != null && (defaultType == null || (defaultType != null && defaultType == XMLSymbols.fFIXEDSymbol))) {
                final String value = defaultValue.toString();
                if (type == XMLSymbols.fNMTOKENSSymbol || type == XMLSymbols.fENTITIESSymbol || type == XMLSymbols.fIDREFSSymbol) {
                    final StringTokenizer tokenizer = new StringTokenizer(value, " ");
                    if (tokenizer.hasMoreTokens()) {
                        do {
                            final String nmtoken = tokenizer.nextToken();
                            if (type == XMLSymbols.fNMTOKENSSymbol) {
                                if (!this.isValidNmtoken(nmtoken)) {
                                    ok = false;
                                    break;
                                }
                            }
                            else if ((type == XMLSymbols.fENTITIESSymbol || type == XMLSymbols.fIDREFSSymbol) && !this.isValidName(nmtoken)) {
                                ok = false;
                                break;
                            }
                        } while (tokenizer.hasMoreTokens());
                    }
                }
                else {
                    if (type == XMLSymbols.fENTITYSymbol || type == XMLSymbols.fIDSymbol || type == XMLSymbols.fIDREFSymbol || type == XMLSymbols.fNOTATIONSymbol) {
                        if (!this.isValidName(value)) {
                            ok = false;
                        }
                    }
                    else if ((type == XMLSymbols.fNMTOKENSymbol || type == XMLSymbols.fENUMERATIONSymbol) && !this.isValidNmtoken(value)) {
                        ok = false;
                    }
                    if (type == XMLSymbols.fNOTATIONSymbol || type == XMLSymbols.fENUMERATIONSymbol) {
                        ok = false;
                        for (int k = 0; k < enumeration.length; ++k) {
                            if (defaultValue.equals(enumeration[k])) {
                                ok = true;
                            }
                        }
                    }
                }
                if (!ok) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATT_DEFAULT_INVALID", new Object[] { attributeName, value }, (short)1);
                }
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augs);
        }
    }
    
    @Override
    public void endAttlist(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endAttlist(augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augs);
        }
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augs) throws XNIException {
        final DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
        final int index = grammar.getEntityDeclIndex(name);
        if (index == -1) {
            if (this.fDTDGrammar != null) {
                this.fDTDGrammar.internalEntityDecl(name, text, nonNormalizedText, augs);
            }
            if (this.fDTDHandler != null) {
                this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augs);
            }
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        final DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
        final int index = grammar.getEntityDeclIndex(name);
        if (index == -1) {
            if (this.fDTDGrammar != null) {
                this.fDTDGrammar.externalEntityDecl(name, identifier, augs);
            }
            if (this.fDTDHandler != null) {
                this.fDTDHandler.externalEntityDecl(name, identifier, augs);
            }
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augs) throws XNIException {
        if (this.fValidation) {
            this.fNDataDeclNotations.put(name, notation);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.unparsedEntityDecl(name, identifier, notation, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augs);
        }
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        if (this.fValidation) {
            final DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            if (grammar.getNotationDeclIndex(name) != -1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UniqueNotationName", new Object[] { name }, (short)1);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.notationDecl(name, identifier, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(name, identifier, augs);
        }
    }
    
    @Override
    public void startConditional(final short type, final Augmentations augs) throws XNIException {
        this.fInDTDIgnore = (type == 1);
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startConditional(type, augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(type, augs);
        }
    }
    
    @Override
    public void endConditional(final Augmentations augs) throws XNIException {
        this.fInDTDIgnore = false;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endConditional(augs);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augs);
        }
    }
    
    @Override
    public void endDTD(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endDTD(augs);
            if (this.fGrammarPool != null) {
                this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { this.fDTDGrammar });
            }
        }
        if (this.fValidation) {
            final DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            for (final Map.Entry entry : this.fNDataDeclNotations.entrySet()) {
                final String notation = entry.getValue();
                if (grammar.getNotationDeclIndex(notation) == -1) {
                    final String entity = entry.getKey();
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_UNPARSED_ENTITYDECL", new Object[] { entity, notation }, (short)1);
                }
            }
            for (final Map.Entry entry2 : this.fNotationEnumVals.entrySet()) {
                final String notation2 = entry2.getKey();
                if (grammar.getNotationDeclIndex(notation2) == -1) {
                    final String attributeName = entry2.getValue();
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_NOTATIONTYPE_ATTRIBUTE", new Object[] { attributeName, notation2 }, (short)1);
                }
            }
            for (final Map.Entry entry3 : this.fTableOfNOTATIONAttributeNames.entrySet()) {
                final String elementName = entry3.getKey();
                final int elementIndex = grammar.getElementDeclIndex(elementName);
                if (grammar.getContentSpecType(elementIndex) == 1) {
                    final String attributeName2 = entry3.getValue();
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NoNotationOnEmptyElement", new Object[] { elementName, attributeName2 }, (short)1);
                }
            }
            this.fTableOfIDAttributeNames = null;
            this.fTableOfNOTATIONAttributeNames = null;
            if (this.fWarnOnUndeclaredElemdef) {
                this.checkDeclaredElements(grammar);
            }
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augs);
        }
    }
    
    @Override
    public void setDTDSource(final XMLDTDSource source) {
        this.fDTDSource = source;
    }
    
    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    @Override
    public void setDTDContentModelSource(final XMLDTDContentModelSource source) {
        this.fDTDContentModelSource = source;
    }
    
    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }
    
    @Override
    public void startContentModel(final String elementName, final Augmentations augs) throws XNIException {
        if (this.fValidation) {
            this.fDTDElementDeclName = elementName;
            this.fMixedElementTypes.clear();
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startContentModel(elementName, augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startContentModel(elementName, augs);
        }
    }
    
    @Override
    public void any(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.any(augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.any(augs);
        }
    }
    
    @Override
    public void empty(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.empty(augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.empty(augs);
        }
    }
    
    @Override
    public void startGroup(final Augmentations augs) throws XNIException {
        this.fMixed = false;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startGroup(augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startGroup(augs);
        }
    }
    
    @Override
    public void pcdata(final Augmentations augs) {
        this.fMixed = true;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.pcdata(augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.pcdata(augs);
        }
    }
    
    @Override
    public void element(final String elementName, final Augmentations augs) throws XNIException {
        if (this.fMixed && this.fValidation) {
            if (this.fMixedElementTypes.contains(elementName)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "DuplicateTypeInMixedContent", new Object[] { this.fDTDElementDeclName, elementName }, (short)1);
            }
            else {
                this.fMixedElementTypes.add(elementName);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.element(elementName, augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.element(elementName, augs);
        }
    }
    
    @Override
    public void separator(final short separator, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.separator(separator, augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.separator(separator, augs);
        }
    }
    
    @Override
    public void occurrence(final short occurrence, final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.occurrence(occurrence, augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.occurrence(occurrence, augs);
        }
    }
    
    @Override
    public void endGroup(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endGroup(augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endGroup(augs);
        }
    }
    
    @Override
    public void endContentModel(final Augmentations augs) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endContentModel(augs);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endContentModel(augs);
        }
    }
    
    private boolean normalizeDefaultAttrValue(final XMLString value) {
        boolean skipSpace = true;
        int current = value.offset;
        final int end = value.offset + value.length;
        for (int i = value.offset; i < end; ++i) {
            if (value.ch[i] == ' ') {
                if (!skipSpace) {
                    value.ch[current++] = ' ';
                    skipSpace = true;
                }
            }
            else {
                if (current != i) {
                    value.ch[current] = value.ch[i];
                }
                ++current;
                skipSpace = false;
            }
        }
        if (current != end) {
            if (skipSpace) {
                --current;
            }
            value.length = current - value.offset;
            return true;
        }
        return false;
    }
    
    protected boolean isValidNmtoken(final String nmtoken) {
        return XMLChar.isValidNmtoken(nmtoken);
    }
    
    protected boolean isValidName(final String name) {
        return XMLChar.isValidName(name);
    }
    
    private void checkDeclaredElements(final DTDGrammar grammar) {
        int elementIndex = grammar.getFirstElementDeclIndex();
        final XMLContentSpec contentSpec = new XMLContentSpec();
        while (elementIndex >= 0) {
            final int type = grammar.getContentSpecType(elementIndex);
            if (type == 3 || type == 2) {
                this.checkDeclaredElements(grammar, elementIndex, grammar.getContentSpecIndex(elementIndex), contentSpec);
            }
            elementIndex = grammar.getNextElementDeclIndex(elementIndex);
        }
    }
    
    private void checkDeclaredElements(final DTDGrammar grammar, final int elementIndex, final int contentSpecIndex, final XMLContentSpec contentSpec) {
        grammar.getContentSpec(contentSpecIndex, contentSpec);
        if (contentSpec.type == 0) {
            final String value = (String)contentSpec.value;
            if (value != null && grammar.getElementDeclIndex(value) == -1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UndeclaredElementInContentSpec", new Object[] { grammar.getElementDeclName(elementIndex).rawname, value }, (short)0);
            }
        }
        else if (contentSpec.type == 4 || contentSpec.type == 5) {
            final int leftNode = ((int[])contentSpec.value)[0];
            final int rightNode = ((int[])contentSpec.otherValue)[0];
            this.checkDeclaredElements(grammar, elementIndex, leftNode, contentSpec);
            this.checkDeclaredElements(grammar, elementIndex, rightNode, contentSpec);
        }
        else if (contentSpec.type == 2 || contentSpec.type == 1 || contentSpec.type == 3) {
            final int leftNode = ((int[])contentSpec.value)[0];
            this.checkDeclaredElements(grammar, elementIndex, leftNode, contentSpec);
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs" };
        FEATURE_DEFAULTS = new Boolean[] { null, Boolean.FALSE, Boolean.FALSE, null };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null };
    }
}
