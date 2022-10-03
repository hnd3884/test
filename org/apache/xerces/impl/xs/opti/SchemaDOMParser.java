package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.w3c.dom.Document;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xs.datatypes.XSDecimal;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;

public class SchemaDOMParser extends DefaultXMLDocumentHandler
{
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    public static final String GENERATE_SYNTHETIC_ANNOTATION = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected XMLLocator fLocator;
    protected NamespaceContext fNamespaceContext;
    SchemaDOM schemaDOM;
    XMLParserConfiguration config;
    private ElementImpl fCurrentAnnotationElement;
    private int fAnnotationDepth;
    private int fInnerAnnotationDepth;
    private int fDepth;
    XMLErrorReporter fErrorReporter;
    private boolean fGenerateSyntheticAnnotation;
    private BooleanStack fHasNonSchemaAttributes;
    private BooleanStack fSawAnnotation;
    private XMLAttributes fEmptyAttr;
    private XSDecimal fSupportedVersion;
    private int fIgnoreDepth;
    private boolean fPerformConditionalInclusion;
    private SchemaConditionalIncludeHelper schemaCondlInclHelper;
    
    public SchemaDOMParser(final XMLParserConfiguration config) {
        this.fNamespaceContext = null;
        this.fAnnotationDepth = -1;
        this.fInnerAnnotationDepth = -1;
        this.fDepth = -1;
        this.fGenerateSyntheticAnnotation = false;
        this.fHasNonSchemaAttributes = new BooleanStack();
        this.fSawAnnotation = new BooleanStack();
        this.fEmptyAttr = new XMLAttributesImpl();
        this.fIgnoreDepth = -1;
        this.fPerformConditionalInclusion = true;
        this.schemaCondlInclHelper = new SchemaConditionalIncludeHelper();
        (this.config = config).setDocumentHandler(this);
        config.setDTDHandler(this);
        config.setDTDContentModelHandler(this);
    }
    
    public void startDocument(final XMLLocator fLocator, final String s, final NamespaceContext fNamespaceContext, final Augmentations augmentations) throws XNIException {
        this.fErrorReporter = (XMLErrorReporter)this.config.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fGenerateSyntheticAnnotation = this.config.getFeature("http://apache.org/xml/features/generate-synthetic-annotations");
        this.fHasNonSchemaAttributes.clear();
        this.fSawAnnotation.clear();
        this.schemaDOM = new SchemaDOM();
        this.fCurrentAnnotationElement = null;
        this.fAnnotationDepth = -1;
        this.fInnerAnnotationDepth = -1;
        this.fDepth = -1;
        this.fIgnoreDepth = -1;
        this.fLocator = fLocator;
        this.fNamespaceContext = fNamespaceContext;
        this.schemaDOM.setDocumentURI(fLocator.getExpandedSystemId());
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fAnnotationDepth > -1 && this.fIgnoreDepth == -1) {
            this.schemaDOM.comment(xmlString);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fAnnotationDepth > -1 && this.fIgnoreDepth == -1) {
            this.schemaDOM.processingInstruction(s, xmlString);
        }
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fIgnoreDepth > -1) {
            return;
        }
        if (this.fInnerAnnotationDepth == -1) {
            for (int i = xmlString.offset; i < xmlString.offset + xmlString.length; ++i) {
                if (!XMLChar.isSpace(xmlString.ch[i])) {
                    this.fErrorReporter.reportError(this.fLocator, "http://www.w3.org/TR/xml-schema-1", "s4s-elt-character", new Object[] { new String(xmlString.ch, i, xmlString.length + xmlString.offset - i) }, (short)1);
                    break;
                }
            }
        }
        else {
            this.schemaDOM.characters(xmlString);
        }
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        if (this.fPerformConditionalInclusion) {
            if (this.fIgnoreDepth > -1) {
                ++this.fIgnoreDepth;
                return;
            }
            if (this.fDepth > -1) {
                this.checkVersionControlAttributes(qName, xmlAttributes);
                if (this.fIgnoreDepth > -1) {
                    return;
                }
            }
        }
        if (this.fAnnotationDepth == -1) {
            if (qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && qName.localpart == SchemaSymbols.ELT_ANNOTATION) {
                if (this.fGenerateSyntheticAnnotation) {
                    if (this.fSawAnnotation.size() > 0) {
                        this.fSawAnnotation.pop();
                    }
                    this.fSawAnnotation.push(true);
                }
                this.fAnnotationDepth = this.fDepth;
                this.schemaDOM.startAnnotation(qName, xmlAttributes, this.fNamespaceContext);
                this.fCurrentAnnotationElement = this.schemaDOM.startElement(qName, xmlAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
                return;
            }
            if (qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
                this.fSawAnnotation.push(false);
                this.fHasNonSchemaAttributes.push(this.hasNonSchemaAttributes(qName, xmlAttributes));
            }
        }
        else {
            if (this.fDepth != this.fAnnotationDepth + 1) {
                this.schemaDOM.startAnnotationElement(qName, xmlAttributes);
                return;
            }
            this.fInnerAnnotationDepth = this.fDepth;
            this.schemaDOM.startAnnotationElement(qName, xmlAttributes);
        }
        this.schemaDOM.startElement(qName, xmlAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (this.fPerformConditionalInclusion) {
            if (this.fIgnoreDepth > -1) {
                return;
            }
            if (this.fDepth > -1) {
                final boolean checkVersionControlAttributes = this.checkVersionControlAttributes(qName, xmlAttributes);
                if (this.fIgnoreDepth > -1) {
                    if (checkVersionControlAttributes) {
                        --this.fIgnoreDepth;
                    }
                    return;
                }
            }
        }
        if (this.fGenerateSyntheticAnnotation && this.fAnnotationDepth == -1 && qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && qName.localpart != SchemaSymbols.ELT_ANNOTATION && this.hasNonSchemaAttributes(qName, xmlAttributes)) {
            this.schemaDOM.startElement(qName, xmlAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
            xmlAttributes.removeAllAttributes();
            final String prefix = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
            final String s = (prefix.length() == 0) ? SchemaSymbols.ELT_ANNOTATION : (prefix + ':' + SchemaSymbols.ELT_ANNOTATION);
            this.schemaDOM.startAnnotation(s, xmlAttributes, this.fNamespaceContext);
            final String s2 = (prefix.length() == 0) ? SchemaSymbols.ELT_DOCUMENTATION : (prefix + ':' + SchemaSymbols.ELT_DOCUMENTATION);
            this.schemaDOM.startAnnotationElement(s2, xmlAttributes);
            this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
            this.schemaDOM.endSyntheticAnnotationElement(s2, false);
            this.schemaDOM.endSyntheticAnnotationElement(s, true);
            this.schemaDOM.endElement();
            return;
        }
        if (this.fAnnotationDepth == -1) {
            if (qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && qName.localpart == SchemaSymbols.ELT_ANNOTATION) {
                this.schemaDOM.startAnnotation(qName, xmlAttributes, this.fNamespaceContext);
            }
        }
        else {
            this.schemaDOM.startAnnotationElement(qName, xmlAttributes);
        }
        final ElementImpl emptyElement = this.schemaDOM.emptyElement(qName, xmlAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
        if (this.fAnnotationDepth == -1) {
            if (qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && qName.localpart == SchemaSymbols.ELT_ANNOTATION) {
                this.schemaDOM.endAnnotation(qName, emptyElement);
            }
        }
        else {
            this.schemaDOM.endAnnotationElement(qName);
        }
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (this.fIgnoreDepth == -1) {
            if (this.fAnnotationDepth > -1) {
                if (this.fInnerAnnotationDepth == this.fDepth) {
                    this.fInnerAnnotationDepth = -1;
                    this.schemaDOM.endAnnotationElement(qName);
                    this.schemaDOM.endElement();
                }
                else if (this.fAnnotationDepth == this.fDepth) {
                    this.fAnnotationDepth = -1;
                    this.schemaDOM.endAnnotation(qName, this.fCurrentAnnotationElement);
                    this.schemaDOM.endElement();
                }
                else {
                    this.schemaDOM.endAnnotationElement(qName);
                }
            }
            else {
                if (qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
                    final boolean pop = this.fHasNonSchemaAttributes.pop();
                    final boolean pop2 = this.fSawAnnotation.pop();
                    if (pop && !pop2) {
                        final String prefix = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
                        final String s = (prefix.length() == 0) ? SchemaSymbols.ELT_ANNOTATION : (prefix + ':' + SchemaSymbols.ELT_ANNOTATION);
                        this.schemaDOM.startAnnotation(s, this.fEmptyAttr, this.fNamespaceContext);
                        final String s2 = (prefix.length() == 0) ? SchemaSymbols.ELT_DOCUMENTATION : (prefix + ':' + SchemaSymbols.ELT_DOCUMENTATION);
                        this.schemaDOM.startAnnotationElement(s2, this.fEmptyAttr);
                        this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
                        this.schemaDOM.endSyntheticAnnotationElement(s2, false);
                        this.schemaDOM.endSyntheticAnnotationElement(s, true);
                    }
                }
                this.schemaDOM.endElement();
            }
        }
        else {
            --this.fIgnoreDepth;
        }
        --this.fDepth;
    }
    
    private boolean hasNonSchemaAttributes(final QName qName, final XMLAttributes xmlAttributes) {
        for (int length = xmlAttributes.getLength(), i = 0; i < length; ++i) {
            final String uri = xmlAttributes.getURI(i);
            if (uri != null && uri != SchemaSymbols.URI_SCHEMAFORSCHEMA && uri != NamespaceContext.XMLNS_URI && (uri != NamespaceContext.XML_URI || xmlAttributes.getQName(i) != SchemaSymbols.ATT_XML_LANG || qName.localpart != SchemaSymbols.ELT_SCHEMA)) {
                return true;
            }
        }
        return false;
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fAnnotationDepth != -1 && this.fIgnoreDepth == -1) {
            this.schemaDOM.characters(xmlString);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fAnnotationDepth != -1 && this.fIgnoreDepth == -1) {
            this.schemaDOM.startAnnotationCDATA();
        }
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fAnnotationDepth != -1 && this.fIgnoreDepth == -1) {
            this.schemaDOM.endAnnotationCDATA();
        }
    }
    
    public Document getDocument() {
        return this.schemaDOM;
    }
    
    public void setFeature(final String s, final boolean b) {
        this.config.setFeature(s, b);
    }
    
    public boolean getFeature(final String s) {
        return this.config.getFeature(s);
    }
    
    public void setProperty(final String s, final Object o) {
        this.config.setProperty(s, o);
    }
    
    public Object getProperty(final String s) {
        return this.config.getProperty(s);
    }
    
    public void setEntityResolver(final XMLEntityResolver entityResolver) {
        this.config.setEntityResolver(entityResolver);
    }
    
    public void parse(final XMLInputSource xmlInputSource) throws IOException {
        this.config.parse(xmlInputSource);
    }
    
    public void reset() {
        ((SchemaParsingConfig)this.config).reset();
    }
    
    public void resetNodePool() {
        ((SchemaParsingConfig)this.config).resetNodePool();
    }
    
    public void setSupportedVersion(final XSDecimal fSupportedVersion) {
        this.fSupportedVersion = fSupportedVersion;
    }
    
    private boolean checkVersionControlAttributes(final QName qName, final XMLAttributes xmlAttributes) {
        BigDecimal bigDecimal = null;
        BigDecimal bigDecimal2 = null;
        List tokenizeQNameListString = null;
        List tokenizeQNameListString2 = null;
        List tokenizeQNameListString3 = null;
        List tokenizeQNameListString4 = null;
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            if (SchemaSymbols.URI_SCHEMAVERSION.equals(xmlAttributes.getURI(i))) {
                final String localName = xmlAttributes.getLocalName(i);
                final String value = xmlAttributes.getValue(i);
                if (SchemaSymbols.ATT_MINVERSION.equals(localName)) {
                    try {
                        bigDecimal = new BigDecimal(value);
                    }
                    catch (final NumberFormatException ex) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "s4s-att-invalid-value", new Object[] { qName.localpart, localName, value }, (short)1);
                    }
                }
                else if (SchemaSymbols.ATT_MAXVERSION.equals(localName)) {
                    try {
                        bigDecimal2 = new BigDecimal(value);
                    }
                    catch (final NumberFormatException ex2) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "s4s-att-invalid-value", new Object[] { qName.localpart, localName, value }, (short)1);
                    }
                }
                else if (SchemaSymbols.ATT_TYPEAVAILABLE.equals(localName)) {
                    tokenizeQNameListString = this.tokenizeQNameListString(value);
                }
                else if (SchemaSymbols.ATT_TYPEUNAVAILABLE.equals(localName)) {
                    tokenizeQNameListString2 = this.tokenizeQNameListString(value);
                }
                else if (SchemaSymbols.ATT_FACETAVAILABLE.equals(localName)) {
                    tokenizeQNameListString3 = this.tokenizeQNameListString(value);
                }
                else if (SchemaSymbols.ATT_FACETUNAVAILABLE.equals(localName)) {
                    tokenizeQNameListString4 = this.tokenizeQNameListString(value);
                }
                else {
                    this.fErrorReporter.reportError(this.fLocator, "http://www.w3.org/TR/xml-schema-1", "src-cip.1", new Object[] { localName }, (short)0);
                }
            }
        }
        final boolean b = (bigDecimal != null || bigDecimal2 != null) && this.isSchemaLangVersionAllowsExclude(bigDecimal, bigDecimal2);
        final boolean b2 = tokenizeQNameListString != null && this.isTypeAndFacetAvailableAllowsExclude(tokenizeQNameListString, (short)0, (short)2);
        final boolean b3 = tokenizeQNameListString2 != null && this.isTypeAndFacetAvailableAllowsExclude(tokenizeQNameListString2, (short)0, (short)3);
        final boolean b4 = tokenizeQNameListString3 != null && this.isTypeAndFacetAvailableAllowsExclude(tokenizeQNameListString3, (short)1, (short)2);
        final boolean b5 = tokenizeQNameListString4 != null && this.isTypeAndFacetAvailableAllowsExclude(tokenizeQNameListString4, (short)1, (short)3);
        return b || b2 || b3 || b4 || b5;
    }
    
    private boolean isSchemaLangVersionAllowsExclude(final BigDecimal bigDecimal, final BigDecimal bigDecimal2) {
        boolean b = false;
        if (bigDecimal != null && bigDecimal2 != null) {
            if (bigDecimal.compareTo(this.fSupportedVersion.getBigDecimal()) > 0 || bigDecimal2.compareTo(this.fSupportedVersion.getBigDecimal()) != 1) {
                ++this.fIgnoreDepth;
                b = true;
            }
        }
        else if (bigDecimal != null && bigDecimal2 == null) {
            if (bigDecimal.compareTo(this.fSupportedVersion.getBigDecimal()) > 0) {
                ++this.fIgnoreDepth;
                b = true;
            }
        }
        else if (bigDecimal == null && bigDecimal2 != null && bigDecimal2.compareTo(this.fSupportedVersion.getBigDecimal()) != 1) {
            ++this.fIgnoreDepth;
            b = true;
        }
        return b;
    }
    
    private boolean isTypeAndFacetAvailableAllowsExclude(final List list, final short n, final short n2) {
        boolean b = false;
        boolean b2 = true;
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            String uri = null;
            String substring;
            if (s.indexOf(58) != -1) {
                substring = s.substring(s.indexOf(58) + 1);
                uri = this.fNamespaceContext.getURI(s.substring(0, s.indexOf(58)).intern());
            }
            else {
                substring = s;
            }
            if (n == 0) {
                if (n2 == 2 && !this.schemaCondlInclHelper.isTypeSupported(substring, uri)) {
                    b = true;
                    break;
                }
                if (n2 == 3 && !this.schemaCondlInclHelper.isTypeSupported(substring, uri)) {
                    b2 = false;
                    break;
                }
                continue;
            }
            else {
                if (n != 1) {
                    continue;
                }
                if (n2 == 2 && !this.schemaCondlInclHelper.isFacetSupported(substring, uri)) {
                    b = true;
                    break;
                }
                if (n2 == 3 && !this.schemaCondlInclHelper.isFacetSupported(substring, uri)) {
                    b2 = false;
                    break;
                }
                continue;
            }
        }
        if (n2 == 2) {
            if (b) {
                ++this.fIgnoreDepth;
            }
            return b;
        }
        if (b2) {
            ++this.fIgnoreDepth;
        }
        return b2;
    }
    
    private List tokenizeQNameListString(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
        final ArrayList list = new ArrayList(stringTokenizer.countTokens());
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            XS11TypeHelper.validateQNameValue(nextToken, this.fNamespaceContext, this.fErrorReporter);
            list.add((Object)nextToken);
        }
        return list;
    }
    
    private static final class BooleanStack
    {
        private int fDepth;
        private boolean[] fData;
        
        public BooleanStack() {
        }
        
        public int size() {
            return this.fDepth;
        }
        
        public void push(final boolean b) {
            this.ensureCapacity(this.fDepth + 1);
            this.fData[this.fDepth++] = b;
        }
        
        public boolean pop() {
            final boolean[] fData = this.fData;
            final int fDepth = this.fDepth - 1;
            this.fDepth = fDepth;
            return fData[fDepth];
        }
        
        public void clear() {
            this.fDepth = 0;
        }
        
        private void ensureCapacity(final int n) {
            if (this.fData == null) {
                this.fData = new boolean[32];
            }
            else if (this.fData.length <= n) {
                final boolean[] fData = new boolean[this.fData.length * 2];
                System.arraycopy(this.fData, 0, fData, 0, this.fData.length);
                this.fData = fData;
            }
        }
    }
}
