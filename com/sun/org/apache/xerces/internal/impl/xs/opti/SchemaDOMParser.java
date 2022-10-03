package com.sun.org.apache.xerces.internal.impl.xs.opti;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;

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
    
    public SchemaDOMParser(final XMLParserConfiguration config) {
        this.fNamespaceContext = null;
        this.fAnnotationDepth = -1;
        this.fInnerAnnotationDepth = -1;
        this.fDepth = -1;
        this.fGenerateSyntheticAnnotation = false;
        this.fHasNonSchemaAttributes = new BooleanStack();
        this.fSawAnnotation = new BooleanStack();
        this.fEmptyAttr = new XMLAttributesImpl();
        (this.config = config).setDocumentHandler(this);
        config.setDTDHandler(this);
        config.setDTDContentModelHandler(this);
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.fErrorReporter = (XMLErrorReporter)this.config.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fGenerateSyntheticAnnotation = this.config.getFeature("http://apache.org/xml/features/generate-synthetic-annotations");
        this.fHasNonSchemaAttributes.clear();
        this.fSawAnnotation.clear();
        this.schemaDOM = new SchemaDOM();
        this.fCurrentAnnotationElement = null;
        this.fAnnotationDepth = -1;
        this.fInnerAnnotationDepth = -1;
        this.fDepth = -1;
        this.fLocator = locator;
        this.fNamespaceContext = namespaceContext;
        this.schemaDOM.setDocumentURI(locator.getExpandedSystemId());
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fAnnotationDepth > -1) {
            this.schemaDOM.comment(text);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fAnnotationDepth > -1) {
            this.schemaDOM.processingInstruction(target, data);
        }
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fInnerAnnotationDepth == -1) {
            for (int i = text.offset; i < text.offset + text.length; ++i) {
                if (!XMLChar.isSpace(text.ch[i])) {
                    final String txt = new String(text.ch, i, text.length + text.offset - i);
                    this.fErrorReporter.reportError(this.fLocator, "http://www.w3.org/TR/xml-schema-1", "s4s-elt-character", new Object[] { txt }, (short)1);
                    break;
                }
            }
        }
        else {
            this.schemaDOM.characters(text);
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        ++this.fDepth;
        if (this.fAnnotationDepth == -1) {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart == SchemaSymbols.ELT_ANNOTATION) {
                if (this.fGenerateSyntheticAnnotation) {
                    if (this.fSawAnnotation.size() > 0) {
                        this.fSawAnnotation.pop();
                    }
                    this.fSawAnnotation.push(true);
                }
                this.fAnnotationDepth = this.fDepth;
                this.schemaDOM.startAnnotation(element, attributes, this.fNamespaceContext);
                this.fCurrentAnnotationElement = this.schemaDOM.startElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
                return;
            }
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
                this.fSawAnnotation.push(false);
                this.fHasNonSchemaAttributes.push(this.hasNonSchemaAttributes(element, attributes));
            }
        }
        else {
            if (this.fDepth != this.fAnnotationDepth + 1) {
                this.schemaDOM.startAnnotationElement(element, attributes);
                return;
            }
            this.fInnerAnnotationDepth = this.fDepth;
            this.schemaDOM.startAnnotationElement(element, attributes);
        }
        this.schemaDOM.startElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fGenerateSyntheticAnnotation && this.fAnnotationDepth == -1 && element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart != SchemaSymbols.ELT_ANNOTATION && this.hasNonSchemaAttributes(element, attributes)) {
            this.schemaDOM.startElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
            attributes.removeAllAttributes();
            final String schemaPrefix = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
            final String annRawName = (schemaPrefix.length() == 0) ? SchemaSymbols.ELT_ANNOTATION : (schemaPrefix + ':' + SchemaSymbols.ELT_ANNOTATION);
            this.schemaDOM.startAnnotation(annRawName, attributes, this.fNamespaceContext);
            final String elemRawName = (schemaPrefix.length() == 0) ? SchemaSymbols.ELT_DOCUMENTATION : (schemaPrefix + ':' + SchemaSymbols.ELT_DOCUMENTATION);
            this.schemaDOM.startAnnotationElement(elemRawName, attributes);
            this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
            this.schemaDOM.endSyntheticAnnotationElement(elemRawName, false);
            this.schemaDOM.endSyntheticAnnotationElement(annRawName, true);
            this.schemaDOM.endElement();
            return;
        }
        if (this.fAnnotationDepth == -1) {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart == SchemaSymbols.ELT_ANNOTATION) {
                this.schemaDOM.startAnnotation(element, attributes, this.fNamespaceContext);
            }
        }
        else {
            this.schemaDOM.startAnnotationElement(element, attributes);
        }
        final ElementImpl newElem = this.schemaDOM.emptyElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
        if (this.fAnnotationDepth == -1) {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart == SchemaSymbols.ELT_ANNOTATION) {
                this.schemaDOM.endAnnotation(element, newElem);
            }
        }
        else {
            this.schemaDOM.endAnnotationElement(element);
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.fAnnotationDepth > -1) {
            if (this.fInnerAnnotationDepth == this.fDepth) {
                this.fInnerAnnotationDepth = -1;
                this.schemaDOM.endAnnotationElement(element);
                this.schemaDOM.endElement();
            }
            else if (this.fAnnotationDepth == this.fDepth) {
                this.fAnnotationDepth = -1;
                this.schemaDOM.endAnnotation(element, this.fCurrentAnnotationElement);
                this.schemaDOM.endElement();
            }
            else {
                this.schemaDOM.endAnnotationElement(element);
            }
        }
        else {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
                final boolean value = this.fHasNonSchemaAttributes.pop();
                final boolean sawann = this.fSawAnnotation.pop();
                if (value && !sawann) {
                    final String schemaPrefix = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
                    final String annRawName = (schemaPrefix.length() == 0) ? SchemaSymbols.ELT_ANNOTATION : (schemaPrefix + ':' + SchemaSymbols.ELT_ANNOTATION);
                    this.schemaDOM.startAnnotation(annRawName, this.fEmptyAttr, this.fNamespaceContext);
                    final String elemRawName = (schemaPrefix.length() == 0) ? SchemaSymbols.ELT_DOCUMENTATION : (schemaPrefix + ':' + SchemaSymbols.ELT_DOCUMENTATION);
                    this.schemaDOM.startAnnotationElement(elemRawName, this.fEmptyAttr);
                    this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
                    this.schemaDOM.endSyntheticAnnotationElement(elemRawName, false);
                    this.schemaDOM.endSyntheticAnnotationElement(annRawName, true);
                }
            }
            this.schemaDOM.endElement();
        }
        --this.fDepth;
    }
    
    private boolean hasNonSchemaAttributes(final QName element, final XMLAttributes attributes) {
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final String uri = attributes.getURI(i);
            if (uri != null && uri != SchemaSymbols.URI_SCHEMAFORSCHEMA && uri != NamespaceContext.XMLNS_URI && (uri != NamespaceContext.XML_URI || attributes.getQName(i) != SchemaSymbols.ATT_XML_LANG || element.localpart != SchemaSymbols.ELT_SCHEMA)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fAnnotationDepth != -1) {
            this.schemaDOM.characters(text);
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (this.fAnnotationDepth != -1) {
            this.schemaDOM.startAnnotationCDATA();
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fAnnotationDepth != -1) {
            this.schemaDOM.endAnnotationCDATA();
        }
    }
    
    public Document getDocument() {
        return this.schemaDOM;
    }
    
    public void setFeature(final String featureId, final boolean state) {
        this.config.setFeature(featureId, state);
    }
    
    public boolean getFeature(final String featureId) {
        return this.config.getFeature(featureId);
    }
    
    public void setProperty(final String propertyId, final Object value) {
        this.config.setProperty(propertyId, value);
    }
    
    public Object getProperty(final String propertyId) {
        return this.config.getProperty(propertyId);
    }
    
    public void setEntityResolver(final XMLEntityResolver er) {
        this.config.setEntityResolver(er);
    }
    
    public void parse(final XMLInputSource inputSource) throws IOException {
        this.config.parse(inputSource);
    }
    
    public void reset() {
        ((SchemaParsingConfig)this.config).reset();
    }
    
    public void resetNodePool() {
        ((SchemaParsingConfig)this.config).resetNodePool();
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
        
        public void push(final boolean value) {
            this.ensureCapacity(this.fDepth + 1);
            this.fData[this.fDepth++] = value;
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
        
        private void ensureCapacity(final int size) {
            if (this.fData == null) {
                this.fData = new boolean[32];
            }
            else if (this.fData.length <= size) {
                final boolean[] newdata = new boolean[this.fData.length * 2];
                System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
                this.fData = newdata;
            }
        }
    }
}
