package org.apache.xmlbeans;

import java.util.Collections;
import java.net.URI;
import org.xml.sax.EntityResolver;
import java.util.Set;
import org.xml.sax.XMLReader;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class XmlOptions implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Map _map;
    public static final String GENERATE_JAVA_14 = "1.4";
    public static final String GENERATE_JAVA_15 = "1.5";
    public static final String SAVE_NAMESPACES_FIRST = "SAVE_NAMESPACES_FIRST";
    public static final String SAVE_SYNTHETIC_DOCUMENT_ELEMENT = "SAVE_SYNTHETIC_DOCUMENT_ELEMENT";
    public static final String SAVE_PRETTY_PRINT = "SAVE_PRETTY_PRINT";
    public static final String SAVE_PRETTY_PRINT_INDENT = "SAVE_PRETTY_PRINT_INDENT";
    public static final String SAVE_PRETTY_PRINT_OFFSET = "SAVE_PRETTY_PRINT_OFFSET";
    public static final String SAVE_AGGRESSIVE_NAMESPACES = "SAVE_AGGRESSIVE_NAMESPACES";
    public static final String SAVE_USE_DEFAULT_NAMESPACE = "SAVE_USE_DEFAULT_NAMESPACE";
    public static final String SAVE_IMPLICIT_NAMESPACES = "SAVE_IMPLICIT_NAMESPACES";
    public static final String SAVE_SUGGESTED_PREFIXES = "SAVE_SUGGESTED_PREFIXES";
    public static final String SAVE_FILTER_PROCINST = "SAVE_FILTER_PROCINST";
    public static final String SAVE_USE_OPEN_FRAGMENT = "SAVE_USE_OPEN_FRAGMENT";
    public static final String SAVE_OUTER = "SAVE_OUTER";
    public static final String SAVE_INNER = "SAVE_INNER";
    public static final String SAVE_NO_XML_DECL = "SAVE_NO_XML_DECL";
    public static final String SAVE_SUBSTITUTE_CHARACTERS = "SAVE_SUBSTITUTE_CHARACTERS";
    public static final String SAVE_OPTIMIZE_FOR_SPEED = "SAVE_OPTIMIZE_FOR_SPEED";
    public static final String SAVE_CDATA_LENGTH_THRESHOLD = "SAVE_CDATA_LENGTH_THRESHOLD";
    public static final String SAVE_CDATA_ENTITY_COUNT_THRESHOLD = "SAVE_CDATA_ENTITY_COUNT_THRESHOLD";
    public static final String SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES = "SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES";
    public static final String LOAD_REPLACE_DOCUMENT_ELEMENT = "LOAD_REPLACE_DOCUMENT_ELEMENT";
    public static final String LOAD_STRIP_WHITESPACE = "LOAD_STRIP_WHITESPACE";
    public static final String LOAD_STRIP_COMMENTS = "LOAD_STRIP_COMMENTS";
    public static final String LOAD_STRIP_PROCINSTS = "LOAD_STRIP_PROCINSTS";
    public static final String LOAD_LINE_NUMBERS = "LOAD_LINE_NUMBERS";
    public static final String LOAD_LINE_NUMBERS_END_ELEMENT = "LOAD_LINE_NUMBERS_END_ELEMENT";
    public static final String LOAD_SAVE_CDATA_BOOKMARKS = "LOAD_SAVE_CDATA_BOOKMARKS";
    public static final String LOAD_SUBSTITUTE_NAMESPACES = "LOAD_SUBSTITUTE_NAMESPACES";
    public static final String LOAD_TRIM_TEXT_BUFFER = "LOAD_TRIM_TEXT_BUFFER";
    public static final String LOAD_ADDITIONAL_NAMESPACES = "LOAD_ADDITIONAL_NAMESPACES";
    public static final String LOAD_MESSAGE_DIGEST = "LOAD_MESSAGE_DIGEST";
    public static final String LOAD_USE_DEFAULT_RESOLVER = "LOAD_USE_DEFAULT_RESOLVER";
    public static final String LOAD_USE_XMLREADER = "LOAD_USE_XMLREADER";
    public static final String XQUERY_CURRENT_NODE_VAR = "XQUERY_CURRENT_NODE_VAR";
    public static final String XQUERY_VARIABLE_MAP = "XQUERY_VARIABLE_MAP";
    public static final String CHARACTER_ENCODING = "CHARACTER_ENCODING";
    public static final String ERROR_LISTENER = "ERROR_LISTENER";
    public static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";
    public static final String DOCUMENT_SOURCE_NAME = "DOCUMENT_SOURCE_NAME";
    public static final String COMPILE_SUBSTITUTE_NAMES = "COMPILE_SUBSTITUTE_NAMES";
    public static final String COMPILE_NO_VALIDATION = "COMPILE_NO_VALIDATION";
    public static final String COMPILE_NO_UPA_RULE = "COMPILE_NO_UPA_RULE";
    public static final String COMPILE_NO_PVR_RULE = "COMPILE_NO_PVR_RULE";
    public static final String COMPILE_NO_ANNOTATIONS = "COMPILE_NO_ANNOTATIONS";
    public static final String COMPILE_DOWNLOAD_URLS = "COMPILE_DOWNLOAD_URLS";
    public static final String COMPILE_MDEF_NAMESPACES = "COMPILE_MDEF_NAMESPACES";
    public static final String VALIDATE_ON_SET = "VALIDATE_ON_SET";
    public static final String VALIDATE_TREAT_LAX_AS_SKIP = "VALIDATE_TREAT_LAX_AS_SKIP";
    public static final String VALIDATE_STRICT = "VALIDATE_STRICT";
    public static final String VALIDATE_TEXT_ONLY = "VALIDATE_TEXT_ONLY";
    public static final String UNSYNCHRONIZED = "UNSYNCHRONIZED";
    public static final String ENTITY_RESOLVER = "ENTITY_RESOLVER";
    public static final String BASE_URI = "BASE_URI";
    public static final String SCHEMA_CODE_PRINTER = "SCHEMA_CODE_PRINTER";
    public static final String GENERATE_JAVA_VERSION = "GENERATE_JAVA_VERSION";
    public static final String COPY_USE_NEW_SYNC_DOMAIN = "COPY_USE_NEW_LOCALE";
    public static final String LOAD_ENTITY_BYTES_LIMIT = "LOAD_ENTITY_BYTES_LIMIT";
    public static final String ENTITY_EXPANSION_LIMIT = "ENTITY_EXPANSION_LIMIT";
    public static final String LOAD_DTD_GRAMMAR = "LOAD_DTD_GRAMMAR";
    public static final String LOAD_EXTERNAL_DTD = "LOAD_EXTERNAL_DTD";
    public static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 2048;
    private static final XmlOptions EMPTY_OPTIONS;
    
    public XmlOptions() {
        this._map = new HashMap();
    }
    
    public XmlOptions(final XmlOptions other) {
        this._map = new HashMap();
        if (other != null) {
            this._map.putAll(other._map);
        }
    }
    
    public XmlOptions setSaveNamespacesFirst() {
        return this.set("SAVE_NAMESPACES_FIRST");
    }
    
    public XmlOptions setSavePrettyPrint() {
        return this.set("SAVE_PRETTY_PRINT");
    }
    
    public XmlOptions setSavePrettyPrintIndent(final int indent) {
        return this.set("SAVE_PRETTY_PRINT_INDENT", indent);
    }
    
    public XmlOptions setSavePrettyPrintOffset(final int offset) {
        return this.set("SAVE_PRETTY_PRINT_OFFSET", offset);
    }
    
    public XmlOptions setCharacterEncoding(final String encoding) {
        return this.set("CHARACTER_ENCODING", encoding);
    }
    
    public XmlOptions setDocumentType(final SchemaType type) {
        return this.set("DOCUMENT_TYPE", type);
    }
    
    public XmlOptions setErrorListener(final Collection c) {
        return this.set("ERROR_LISTENER", c);
    }
    
    public XmlOptions setSaveAggressiveNamespaces() {
        return this.set("SAVE_AGGRESSIVE_NAMESPACES");
    }
    
    @Deprecated
    public XmlOptions setSaveAggresiveNamespaces() {
        return this.setSaveAggressiveNamespaces();
    }
    
    public XmlOptions setSaveSyntheticDocumentElement(final QName name) {
        return this.set("SAVE_SYNTHETIC_DOCUMENT_ELEMENT", name);
    }
    
    public XmlOptions setUseDefaultNamespace() {
        return this.set("SAVE_USE_DEFAULT_NAMESPACE");
    }
    
    public XmlOptions setSaveImplicitNamespaces(final Map implicitNamespaces) {
        return this.set("SAVE_IMPLICIT_NAMESPACES", implicitNamespaces);
    }
    
    public XmlOptions setSaveSuggestedPrefixes(final Map suggestedPrefixes) {
        return this.set("SAVE_SUGGESTED_PREFIXES", suggestedPrefixes);
    }
    
    public XmlOptions setSaveFilterProcinst(final String filterProcinst) {
        return this.set("SAVE_FILTER_PROCINST", filterProcinst);
    }
    
    public XmlOptions setSaveSubstituteCharacters(final XmlOptionCharEscapeMap characterReplacementMap) {
        return this.set("SAVE_SUBSTITUTE_CHARACTERS", characterReplacementMap);
    }
    
    public XmlOptions setSaveUseOpenFrag() {
        return this.set("SAVE_USE_OPEN_FRAGMENT");
    }
    
    public XmlOptions setSaveOuter() {
        return this.set("SAVE_OUTER");
    }
    
    public XmlOptions setSaveInner() {
        return this.set("SAVE_INNER");
    }
    
    public XmlOptions setSaveNoXmlDecl() {
        return this.set("SAVE_NO_XML_DECL");
    }
    
    public XmlOptions setSaveCDataLengthThreshold(final int cdataLengthThreshold) {
        return this.set("SAVE_CDATA_LENGTH_THRESHOLD", cdataLengthThreshold);
    }
    
    public XmlOptions setSaveCDataEntityCountThreshold(final int cdataEntityCountThreshold) {
        return this.set("SAVE_CDATA_ENTITY_COUNT_THRESHOLD", cdataEntityCountThreshold);
    }
    
    public XmlOptions setUseCDataBookmarks() {
        return this.set("LOAD_SAVE_CDATA_BOOKMARKS");
    }
    
    public XmlOptions setSaveSaxNoNSDeclsInAttributes() {
        return this.set("SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES");
    }
    
    public XmlOptions setLoadReplaceDocumentElement(final QName replacement) {
        return this.set("LOAD_REPLACE_DOCUMENT_ELEMENT", replacement);
    }
    
    public XmlOptions setLoadStripWhitespace() {
        return this.set("LOAD_STRIP_WHITESPACE");
    }
    
    public XmlOptions setLoadStripComments() {
        return this.set("LOAD_STRIP_COMMENTS");
    }
    
    public XmlOptions setLoadStripProcinsts() {
        return this.set("LOAD_STRIP_PROCINSTS");
    }
    
    public XmlOptions setLoadLineNumbers() {
        return this.set("LOAD_LINE_NUMBERS");
    }
    
    public XmlOptions setLoadLineNumbers(final String option) {
        XmlOptions temp = this.setLoadLineNumbers();
        temp = temp.set(option);
        return temp;
    }
    
    public XmlOptions setLoadSubstituteNamespaces(final Map substNamespaces) {
        return this.set("LOAD_SUBSTITUTE_NAMESPACES", substNamespaces);
    }
    
    public XmlOptions setLoadTrimTextBuffer() {
        return this.set("LOAD_TRIM_TEXT_BUFFER");
    }
    
    public XmlOptions setLoadAdditionalNamespaces(final Map nses) {
        return this.set("LOAD_ADDITIONAL_NAMESPACES", nses);
    }
    
    public XmlOptions setLoadMessageDigest() {
        return this.set("LOAD_MESSAGE_DIGEST");
    }
    
    public XmlOptions setLoadUseDefaultResolver() {
        return this.set("LOAD_USE_DEFAULT_RESOLVER");
    }
    
    public XmlOptions setLoadUseXMLReader(final XMLReader xmlReader) {
        return this.set("LOAD_USE_XMLREADER", xmlReader);
    }
    
    public XmlOptions setXqueryCurrentNodeVar(final String varName) {
        return this.set("XQUERY_CURRENT_NODE_VAR", varName);
    }
    
    public XmlOptions setXqueryVariables(final Map varMap) {
        return this.set("XQUERY_VARIABLE_MAP", varMap);
    }
    
    public XmlOptions setDocumentSourceName(final String documentSourceName) {
        return this.set("DOCUMENT_SOURCE_NAME", documentSourceName);
    }
    
    public XmlOptions setCompileSubstituteNames(final Map nameMap) {
        return this.set("COMPILE_SUBSTITUTE_NAMES", nameMap);
    }
    
    public XmlOptions setCompileNoValidation() {
        return this.set("COMPILE_NO_VALIDATION");
    }
    
    public XmlOptions setCompileNoUpaRule() {
        return this.set("COMPILE_NO_UPA_RULE");
    }
    
    public XmlOptions setCompileNoPvrRule() {
        return this.set("COMPILE_NO_PVR_RULE");
    }
    
    public XmlOptions setCompileNoAnnotations() {
        return this.set("COMPILE_NO_ANNOTATIONS");
    }
    
    public XmlOptions setCompileDownloadUrls() {
        return this.set("COMPILE_DOWNLOAD_URLS");
    }
    
    public XmlOptions setCompileMdefNamespaces(final Set mdefNamespaces) {
        return this.set("COMPILE_MDEF_NAMESPACES", mdefNamespaces);
    }
    
    public XmlOptions setValidateOnSet() {
        return this.set("VALIDATE_ON_SET");
    }
    
    public XmlOptions setValidateTreatLaxAsSkip() {
        return this.set("VALIDATE_TREAT_LAX_AS_SKIP");
    }
    
    public XmlOptions setValidateStrict() {
        return this.set("VALIDATE_STRICT");
    }
    
    public XmlOptions setUnsynchronized() {
        return this.set("UNSYNCHRONIZED");
    }
    
    public XmlOptions setEntityResolver(final EntityResolver resolver) {
        return this.set("ENTITY_RESOLVER", resolver);
    }
    
    public XmlOptions setBaseURI(final URI baseURI) {
        return this.set("BASE_URI", baseURI);
    }
    
    public XmlOptions setSchemaCodePrinter(final SchemaCodePrinter printer) {
        return this.set("SCHEMA_CODE_PRINTER", printer);
    }
    
    public XmlOptions setGenerateJavaVersion(final String source) {
        return this.set("GENERATE_JAVA_VERSION", source);
    }
    
    public XmlOptions setCopyUseNewSynchronizationDomain(final boolean useNewSyncDomain) {
        return this.set("COPY_USE_NEW_LOCALE", useNewSyncDomain);
    }
    
    public XmlOptions setLoadEntityBytesLimit(final int entityBytesLimit) {
        return this.set("LOAD_ENTITY_BYTES_LIMIT", entityBytesLimit);
    }
    
    public XmlOptions setEntityExpansionLimit(final int entityExpansionLimit) {
        return this.set("ENTITY_EXPANSION_LIMIT", entityExpansionLimit);
    }
    
    public XmlOptions setLoadDTDGrammar(final boolean loadDTDGrammar) {
        return this.set("LOAD_DTD_GRAMMAR", loadDTDGrammar);
    }
    
    public XmlOptions setLoadExternalDTD(final boolean loadExternalDTD) {
        return this.set("LOAD_EXTERNAL_DTD", loadExternalDTD);
    }
    
    public static XmlOptions maskNull(final XmlOptions o) {
        return (o == null) ? XmlOptions.EMPTY_OPTIONS : o;
    }
    
    public void put(final Object option) {
        this.put(option, null);
    }
    
    public void put(final Object option, final Object value) {
        this._map.put(option, value);
    }
    
    public void put(final Object option, final int value) {
        this.put(option, new Integer(value));
    }
    
    private XmlOptions set(final Object option) {
        return this.set(option, null);
    }
    
    private XmlOptions set(final Object option, final Object value) {
        this._map.put(option, value);
        return this;
    }
    
    private XmlOptions set(final Object option, final int value) {
        return this.set(option, new Integer(value));
    }
    
    public boolean hasOption(final Object option) {
        return this._map.containsKey(option);
    }
    
    public static boolean hasOption(final XmlOptions options, final Object option) {
        return options != null && options.hasOption(option);
    }
    
    public Object get(final Object option) {
        return this._map.get(option);
    }
    
    public void remove(final Object option) {
        this._map.remove(option);
    }
    
    public static Object safeGet(final XmlOptions o, final Object option) {
        return (o == null) ? null : o.get(option);
    }
    
    static {
        EMPTY_OPTIONS = new XmlOptions();
        XmlOptions.EMPTY_OPTIONS._map = Collections.unmodifiableMap((Map<?, ?>)XmlOptions.EMPTY_OPTIONS._map);
    }
}
