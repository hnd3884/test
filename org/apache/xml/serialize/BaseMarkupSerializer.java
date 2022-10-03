package org.apache.xml.serialize;

import org.apache.xerces.dom.DOMLocatorImpl;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.DOMError;
import java.lang.reflect.Method;
import org.w3c.dom.DocumentType;
import org.xml.sax.Locator;
import org.apache.xerces.util.XMLChar;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.apache.xerces.dom.DOMMessageFormatter;
import java.io.IOException;
import org.w3c.dom.Node;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.ls.LSSerializerFilter;
import org.apache.xerces.dom.DOMErrorImpl;
import org.w3c.dom.DOMErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;

public abstract class BaseMarkupSerializer implements ContentHandler, DocumentHandler, LexicalHandler, DTDHandler, DeclHandler, DOMSerializer, Serializer
{
    protected short features;
    protected DOMErrorHandler fDOMErrorHandler;
    protected final DOMErrorImpl fDOMError;
    protected LSSerializerFilter fDOMFilter;
    protected EncodingInfo _encodingInfo;
    private ElementState[] _elementStates;
    private int _elementStateCount;
    private Vector _preRoot;
    protected boolean _started;
    private boolean _prepared;
    protected Hashtable _prefixes;
    protected String _docTypePublicId;
    protected String _docTypeSystemId;
    protected OutputFormat _format;
    protected Printer _printer;
    protected boolean _indenting;
    protected final StringBuffer fStrBuffer;
    private Writer _writer;
    private OutputStream _output;
    protected Node fCurrentNode;
    
    protected BaseMarkupSerializer(final OutputFormat format) {
        this.features = -1;
        this.fDOMError = new DOMErrorImpl();
        this.fStrBuffer = new StringBuffer(40);
        this.fCurrentNode = null;
        this._elementStates = new ElementState[10];
        for (int i = 0; i < this._elementStates.length; ++i) {
            this._elementStates[i] = new ElementState();
        }
        this._format = format;
    }
    
    public DocumentHandler asDocumentHandler() throws IOException {
        this.prepare();
        return this;
    }
    
    public ContentHandler asContentHandler() throws IOException {
        this.prepare();
        return this;
    }
    
    public DOMSerializer asDOMSerializer() throws IOException {
        this.prepare();
        return this;
    }
    
    public void setOutputByteStream(final OutputStream output) {
        if (output == null) {
            throw new NullPointerException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[] { "output" }));
        }
        this._output = output;
        this._writer = null;
        this.reset();
    }
    
    public void setOutputCharStream(final Writer writer) {
        if (writer == null) {
            throw new NullPointerException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[] { "writer" }));
        }
        this._writer = writer;
        this._output = null;
        this.reset();
    }
    
    public void setOutputFormat(final OutputFormat format) {
        if (format == null) {
            throw new NullPointerException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[] { "format" }));
        }
        this._format = format;
        this.reset();
    }
    
    public boolean reset() {
        if (this._elementStateCount > 1) {
            throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResetInMiddle", null));
        }
        this._prepared = false;
        this.fCurrentNode = null;
        this.fStrBuffer.setLength(0);
        return true;
    }
    
    protected void cleanup() {
        this.fCurrentNode = null;
    }
    
    protected void prepare() throws IOException {
        if (this._prepared) {
            return;
        }
        if (this._writer == null && this._output == null) {
            throw new IOException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null));
        }
        this._encodingInfo = this._format.getEncodingInfo();
        if (this._output != null) {
            this._writer = this._encodingInfo.getWriter(this._output);
        }
        if (this._format.getIndenting()) {
            this._indenting = true;
            this._printer = new IndentPrinter(this._writer, this._format);
        }
        else {
            this._indenting = false;
            this._printer = new Printer(this._writer, this._format);
        }
        this._elementStateCount = 0;
        final ElementState elementState = this._elementStates[0];
        elementState.namespaceURI = null;
        elementState.localName = null;
        elementState.rawName = null;
        elementState.preserveSpace = this._format.getPreserveSpace();
        elementState.empty = true;
        elementState.afterElement = false;
        elementState.afterComment = false;
        final ElementState elementState2 = elementState;
        final ElementState elementState3 = elementState;
        final boolean b = false;
        elementState3.inCData = b;
        elementState2.doCData = b;
        elementState.prefixes = null;
        this._docTypePublicId = this._format.getDoctypePublic();
        this._docTypeSystemId = this._format.getDoctypeSystem();
        this._started = false;
        this._prepared = true;
    }
    
    public void serialize(final Element element) throws IOException {
        this.reset();
        this.prepare();
        this.serializeNode(element);
        this.cleanup();
        this._printer.flush();
        if (this._printer.getException() != null) {
            throw this._printer.getException();
        }
    }
    
    public void serialize(final DocumentFragment documentFragment) throws IOException {
        this.reset();
        this.prepare();
        this.serializeNode(documentFragment);
        this.cleanup();
        this._printer.flush();
        if (this._printer.getException() != null) {
            throw this._printer.getException();
        }
    }
    
    public void serialize(final Document document) throws IOException {
        this.reset();
        this.prepare();
        this.serializeNode(document);
        this.serializePreRoot();
        this.cleanup();
        this._printer.flush();
        if (this._printer.getException() != null) {
            throw this._printer.getException();
        }
    }
    
    public void startDocument() throws SAXException {
        try {
            this.prepare();
        }
        catch (final IOException ex) {
            throw new SAXException(ex.toString());
        }
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        try {
            final ElementState content = this.content();
            if (content.inCData || content.doCData) {
                if (!content.inCData) {
                    this._printer.printText("<![CDATA[");
                    content.inCData = true;
                }
                final int nextIndent = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                for (int n3 = n + n2, i = n; i < n3; ++i) {
                    final char c = array[i];
                    if (c == ']' && i + 2 < n3 && array[i + 1] == ']' && array[i + 2] == '>') {
                        this._printer.printText("]]]]><![CDATA[>");
                        i += 2;
                    }
                    else if (!XMLChar.isValid(c)) {
                        if (++i < n3) {
                            this.surrogates(c, array[i], true);
                        }
                        else {
                            this.fatalError("The character '" + c + "' is an invalid XML character");
                        }
                    }
                    else if ((c >= ' ' && this._encodingInfo.isPrintable(c) && c != '\u007f') || c == '\n' || c == '\r' || c == '\t') {
                        this._printer.printText(c);
                    }
                    else {
                        this._printer.printText("]]>&#x");
                        this._printer.printText(Integer.toHexString(c));
                        this._printer.printText(";<![CDATA[");
                    }
                }
                this._printer.setNextIndent(nextIndent);
            }
            else if (content.preserveSpace) {
                final int nextIndent2 = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                this.printText(array, n, n2, true, content.unescaped);
                this._printer.setNextIndent(nextIndent2);
            }
            else {
                this.printText(array, n, n2, false, content.unescaped);
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, int n2) throws SAXException {
        try {
            this.content();
            if (this._indenting) {
                this._printer.setThisIndent(0);
                int n3 = n;
                while (n2-- > 0) {
                    this._printer.printText(array[n3]);
                    ++n3;
                }
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public final void processingInstruction(final String s, final String s2) throws SAXException {
        try {
            this.processingInstructionIO(s, s2);
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void processingInstructionIO(final String s, final String s2) throws IOException {
        final ElementState content = this.content();
        final int index = s.indexOf("?>");
        if (index >= 0) {
            this.fStrBuffer.append("<?").append(s.substring(0, index));
        }
        else {
            this.fStrBuffer.append("<?").append(s);
        }
        if (s2 != null) {
            this.fStrBuffer.append(' ');
            final int index2 = s2.indexOf("?>");
            if (index2 >= 0) {
                this.fStrBuffer.append(s2.substring(0, index2));
            }
            else {
                this.fStrBuffer.append(s2);
            }
        }
        this.fStrBuffer.append("?>");
        if (this.isDocumentState()) {
            if (this._preRoot == null) {
                this._preRoot = new Vector();
            }
            this._preRoot.addElement(this.fStrBuffer.toString());
        }
        else {
            this._printer.indent();
            this.printText(this.fStrBuffer.toString(), true, true);
            this._printer.unindent();
            if (this._indenting) {
                content.afterElement = true;
            }
        }
        this.fStrBuffer.setLength(0);
    }
    
    public void comment(final char[] array, final int n, final int n2) throws SAXException {
        try {
            this.comment(new String(array, n, n2));
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void comment(final String s) throws IOException {
        if (this._format.getOmitComments()) {
            return;
        }
        final ElementState content = this.content();
        final int index = s.indexOf("-->");
        if (index >= 0) {
            this.fStrBuffer.append("<!--").append(s.substring(0, index)).append("-->");
        }
        else {
            this.fStrBuffer.append("<!--").append(s).append("-->");
        }
        if (this.isDocumentState()) {
            if (this._preRoot == null) {
                this._preRoot = new Vector();
            }
            this._preRoot.addElement(this.fStrBuffer.toString());
        }
        else {
            if (this._indenting && !content.preserveSpace) {
                this._printer.breakLine();
            }
            this._printer.indent();
            this.printText(this.fStrBuffer.toString(), true, true);
            this._printer.unindent();
            if (this._indenting) {
                content.afterElement = true;
            }
        }
        this.fStrBuffer.setLength(0);
        content.afterComment = true;
        content.afterElement = false;
    }
    
    public void startCDATA() {
        this.getElementState().doCData = true;
    }
    
    public void endCDATA() {
        this.getElementState().doCData = false;
    }
    
    public void startNonEscaping() {
        this.getElementState().unescaped = true;
    }
    
    public void endNonEscaping() {
        this.getElementState().unescaped = false;
    }
    
    public void startPreserving() {
        this.getElementState().preserveSpace = true;
    }
    
    public void endPreserving() {
        this.getElementState().preserveSpace = false;
    }
    
    public void endDocument() throws SAXException {
        try {
            this.serializePreRoot();
            this._printer.flush();
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void startEntity(final String s) {
    }
    
    public void endEntity(final String s) {
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void skippedEntity(final String s) throws SAXException {
        try {
            this.endCDATA();
            this.content();
            this._printer.printText('&');
            this._printer.printText(s);
            this._printer.printText(';');
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
        if (this._prefixes == null) {
            this._prefixes = new Hashtable();
        }
        this._prefixes.put(s2, (s == null) ? "" : s);
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
    }
    
    public final void startDTD(final String s, final String docTypePublicId, final String docTypeSystemId) throws SAXException {
        try {
            this._printer.enterDTD();
            this._docTypePublicId = docTypePublicId;
            this._docTypeSystemId = docTypeSystemId;
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void endDTD() {
    }
    
    public void elementDecl(final String s, final String s2) throws SAXException {
        try {
            this._printer.enterDTD();
            this._printer.printText("<!ELEMENT ");
            this._printer.printText(s);
            this._printer.printText(' ');
            this._printer.printText(s2);
            this._printer.printText('>');
            if (this._indenting) {
                this._printer.breakLine();
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String s4, final String s5) throws SAXException {
        try {
            this._printer.enterDTD();
            this._printer.printText("<!ATTLIST ");
            this._printer.printText(s);
            this._printer.printText(' ');
            this._printer.printText(s2);
            this._printer.printText(' ');
            this._printer.printText(s3);
            if (s4 != null) {
                this._printer.printText(' ');
                this._printer.printText(s4);
            }
            if (s5 != null) {
                this._printer.printText(" \"");
                this.printEscaped(s5);
                this._printer.printText('\"');
            }
            this._printer.printText('>');
            if (this._indenting) {
                this._printer.breakLine();
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void internalEntityDecl(final String s, final String s2) throws SAXException {
        try {
            this._printer.enterDTD();
            this._printer.printText("<!ENTITY ");
            this._printer.printText(s);
            this._printer.printText(" \"");
            this.printEscaped(s2);
            this._printer.printText("\">");
            if (this._indenting) {
                this._printer.breakLine();
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void externalEntityDecl(final String s, final String s2, final String s3) throws SAXException {
        try {
            this._printer.enterDTD();
            this.unparsedEntityDecl(s, s2, s3, null);
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) throws SAXException {
        try {
            this._printer.enterDTD();
            if (s2 == null) {
                this._printer.printText("<!ENTITY ");
                this._printer.printText(s);
                this._printer.printText(" SYSTEM ");
                this.printDoctypeURL(s3);
            }
            else {
                this._printer.printText("<!ENTITY ");
                this._printer.printText(s);
                this._printer.printText(" PUBLIC ");
                this.printDoctypeURL(s2);
                this._printer.printText(' ');
                this.printDoctypeURL(s3);
            }
            if (s4 != null) {
                this._printer.printText(" NDATA ");
                this._printer.printText(s4);
            }
            this._printer.printText('>');
            if (this._indenting) {
                this._printer.breakLine();
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void notationDecl(final String s, final String s2, final String s3) throws SAXException {
        try {
            this._printer.enterDTD();
            if (s2 != null) {
                this._printer.printText("<!NOTATION ");
                this._printer.printText(s);
                this._printer.printText(" PUBLIC ");
                this.printDoctypeURL(s2);
                if (s3 != null) {
                    this._printer.printText(' ');
                    this.printDoctypeURL(s3);
                }
            }
            else {
                this._printer.printText("<!NOTATION ");
                this._printer.printText(s);
                this._printer.printText(" SYSTEM ");
                this.printDoctypeURL(s3);
            }
            this._printer.printText('>');
            if (this._indenting) {
                this._printer.breakLine();
            }
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    protected void serializeNode(final Node fCurrentNode) throws IOException {
        this.fCurrentNode = fCurrentNode;
        Label_1017: {
            switch (fCurrentNode.getNodeType()) {
                case 3: {
                    final String nodeValue = fCurrentNode.getNodeValue();
                    if (nodeValue == null) {
                        break;
                    }
                    if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x4) != 0x0) {
                        switch (this.fDOMFilter.acceptNode(fCurrentNode)) {
                            case 2:
                            case 3: {
                                break Label_1017;
                            }
                            default: {
                                this.characters(nodeValue);
                                break Label_1017;
                            }
                        }
                    }
                    else {
                        if (!this._indenting || this.getElementState().preserveSpace || nodeValue.replace('\n', ' ').trim().length() != 0) {
                            this.characters(nodeValue);
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 4: {
                    final String nodeValue2 = fCurrentNode.getNodeValue();
                    if ((this.features & 0x8) == 0x0) {
                        this.characters(nodeValue2);
                        break;
                    }
                    if (nodeValue2 != null) {
                        if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x8) != 0x0) {
                            switch (this.fDOMFilter.acceptNode(fCurrentNode)) {
                                case 2:
                                case 3: {
                                    return;
                                }
                            }
                        }
                        this.startCDATA();
                        this.characters(nodeValue2);
                        this.endCDATA();
                        break;
                    }
                    break;
                }
                case 8: {
                    if (this._format.getOmitComments()) {
                        break;
                    }
                    final String nodeValue3 = fCurrentNode.getNodeValue();
                    if (nodeValue3 != null) {
                        if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x80) != 0x0) {
                            switch (this.fDOMFilter.acceptNode(fCurrentNode)) {
                                case 2:
                                case 3: {
                                    return;
                                }
                            }
                        }
                        this.comment(nodeValue3);
                        break;
                    }
                    break;
                }
                case 5: {
                    this.endCDATA();
                    this.content();
                    if ((this.features & 0x4) != 0x0 || fCurrentNode.getFirstChild() == null) {
                        if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x10) != 0x0) {
                            switch (this.fDOMFilter.acceptNode(fCurrentNode)) {
                                case 2: {
                                    return;
                                }
                                case 3: {
                                    for (Node node = fCurrentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                                        this.serializeNode(node);
                                    }
                                    return;
                                }
                            }
                        }
                        this.checkUnboundNamespacePrefixedNode(fCurrentNode);
                        this._printer.printText("&");
                        this._printer.printText(fCurrentNode.getNodeName());
                        this._printer.printText(";");
                        break;
                    }
                    for (Node node2 = fCurrentNode.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                        this.serializeNode(node2);
                    }
                    break;
                }
                case 7: {
                    if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x40) != 0x0) {
                        switch (this.fDOMFilter.acceptNode(fCurrentNode)) {
                            case 2:
                            case 3: {
                                return;
                            }
                        }
                    }
                    this.processingInstructionIO(fCurrentNode.getNodeName(), fCurrentNode.getNodeValue());
                    break;
                }
                case 1: {
                    if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x1) != 0x0) {
                        switch (this.fDOMFilter.acceptNode(fCurrentNode)) {
                            case 2: {
                                return;
                            }
                            case 3: {
                                for (Node node3 = fCurrentNode.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                                    this.serializeNode(node3);
                                }
                                return;
                            }
                        }
                    }
                    this.serializeElement((Element)fCurrentNode);
                    break;
                }
                case 9: {
                    final DocumentType doctype = ((Document)fCurrentNode).getDoctype();
                    if (doctype != null) {
                        try {
                            this._printer.enterDTD();
                            this._docTypePublicId = doctype.getPublicId();
                            this._docTypeSystemId = doctype.getSystemId();
                            final String internalSubset = doctype.getInternalSubset();
                            if (internalSubset != null && internalSubset.length() > 0) {
                                this._printer.printText(internalSubset);
                            }
                            this.endDTD();
                        }
                        catch (final NoSuchMethodError noSuchMethodError) {
                            final Class<? extends DocumentType> class1 = doctype.getClass();
                            String docTypePublicId = null;
                            String docTypeSystemId = null;
                            try {
                                final Method method = class1.getMethod("getPublicId", (Class[])null);
                                if (method.getReturnType().equals(String.class)) {
                                    docTypePublicId = (String)method.invoke(doctype, (Object[])null);
                                }
                            }
                            catch (final Exception ex) {}
                            try {
                                final Method method2 = class1.getMethod("getSystemId", (Class[])null);
                                if (method2.getReturnType().equals(String.class)) {
                                    docTypeSystemId = (String)method2.invoke(doctype, (Object[])null);
                                }
                            }
                            catch (final Exception ex2) {}
                            this._printer.enterDTD();
                            this._docTypePublicId = docTypePublicId;
                            this._docTypeSystemId = docTypeSystemId;
                            this.endDTD();
                        }
                    }
                }
                case 11: {
                    for (Node node4 = fCurrentNode.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                        this.serializeNode(node4);
                    }
                    break;
                }
            }
        }
    }
    
    protected ElementState content() throws IOException {
        final ElementState elementState = this.getElementState();
        if (!this.isDocumentState()) {
            if (elementState.inCData && !elementState.doCData) {
                this._printer.printText("]]>");
                elementState.inCData = false;
            }
            if (elementState.empty) {
                this._printer.printText('>');
                elementState.empty = false;
            }
            elementState.afterElement = false;
            elementState.afterComment = false;
        }
        return elementState;
    }
    
    protected void characters(final String s) throws IOException {
        final ElementState content = this.content();
        if (content.inCData || content.doCData) {
            if (!content.inCData) {
                this._printer.printText("<![CDATA[");
                content.inCData = true;
            }
            final int nextIndent = this._printer.getNextIndent();
            this._printer.setNextIndent(0);
            this.printCDATAText(s);
            this._printer.setNextIndent(nextIndent);
        }
        else if (content.preserveSpace) {
            final int nextIndent2 = this._printer.getNextIndent();
            this._printer.setNextIndent(0);
            this.printText(s, true, content.unescaped);
            this._printer.setNextIndent(nextIndent2);
        }
        else {
            this.printText(s, false, content.unescaped);
        }
    }
    
    protected abstract String getEntityRef(final int p0);
    
    protected abstract void serializeElement(final Element p0) throws IOException;
    
    protected void serializePreRoot() throws IOException {
        if (this._preRoot != null) {
            for (int i = 0; i < this._preRoot.size(); ++i) {
                this.printText((String)this._preRoot.elementAt(i), true, true);
                if (this._indenting) {
                    this._printer.breakLine();
                }
            }
            this._preRoot.removeAllElements();
        }
    }
    
    protected void printCDATAText(final String s) throws IOException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == ']' && i + 2 < length && s.charAt(i + 1) == ']' && s.charAt(i + 2) == '>') {
                if (this.fDOMErrorHandler != null) {
                    if ((this.features & 0x10) == 0x0) {
                        final String formatMessage = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "EndingCDATA", null);
                        if ((this.features & 0x2) != 0x0) {
                            this.modifyDOMError(formatMessage, (short)3, "wf-invalid-character", this.fCurrentNode);
                            this.fDOMErrorHandler.handleError(this.fDOMError);
                            throw new LSException((short)82, formatMessage);
                        }
                        this.modifyDOMError(formatMessage, (short)2, "cdata-section-not-splitted", this.fCurrentNode);
                        if (!this.fDOMErrorHandler.handleError(this.fDOMError)) {
                            throw new LSException((short)82, formatMessage);
                        }
                    }
                    else {
                        this.modifyDOMError(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SplittingCDATA", null), (short)1, null, this.fCurrentNode);
                        this.fDOMErrorHandler.handleError(this.fDOMError);
                    }
                }
                this._printer.printText("]]]]><![CDATA[>");
                i += 2;
            }
            else if (!XMLChar.isValid(char1)) {
                if (++i < length) {
                    this.surrogates(char1, s.charAt(i), true);
                }
                else {
                    this.fatalError("The character '" + char1 + "' is an invalid XML character");
                }
            }
            else if ((char1 >= ' ' && this._encodingInfo.isPrintable(char1) && char1 != '\u007f') || char1 == '\n' || char1 == '\r' || char1 == '\t') {
                this._printer.printText(char1);
            }
            else {
                this._printer.printText("]]>&#x");
                this._printer.printText(Integer.toHexString(char1));
                this._printer.printText(";<![CDATA[");
            }
        }
    }
    
    protected void surrogates(final int n, final int n2, final boolean b) throws IOException {
        if (XMLChar.isHighSurrogate(n)) {
            if (!XMLChar.isLowSurrogate(n2)) {
                this.fatalError("The character '" + (char)n2 + "' is an invalid XML character");
            }
            else {
                final int supplemental = XMLChar.supplemental((char)n, (char)n2);
                if (!XMLChar.isValid(supplemental)) {
                    this.fatalError("The character '" + (char)supplemental + "' is an invalid XML character");
                }
                else if (b && this.content().inCData) {
                    this._printer.printText("]]>&#x");
                    this._printer.printText(Integer.toHexString(supplemental));
                    this._printer.printText(";<![CDATA[");
                }
                else {
                    this.printHex(supplemental);
                }
            }
        }
        else {
            this.fatalError("The character '" + (char)n + "' is an invalid XML character");
        }
    }
    
    protected void printText(final char[] array, int n, int n2, final boolean b, final boolean b2) throws IOException {
        if (b) {
            while (n2-- > 0) {
                final char c = array[n];
                ++n;
                if (c == '\n' || c == '\r' || b2) {
                    this._printer.printText(c);
                }
                else {
                    this.printEscaped(c);
                }
            }
        }
        else {
            while (n2-- > 0) {
                final char c2 = array[n];
                ++n;
                if (c2 == ' ' || c2 == '\f' || c2 == '\t' || c2 == '\n' || c2 == '\r') {
                    this._printer.printSpace();
                }
                else if (b2) {
                    this._printer.printText(c2);
                }
                else {
                    this.printEscaped(c2);
                }
            }
        }
    }
    
    protected void printText(final String s, final boolean b, final boolean b2) throws IOException {
        if (b) {
            for (int i = 0; i < s.length(); ++i) {
                final char char1 = s.charAt(i);
                if (char1 == '\n' || char1 == '\r' || b2) {
                    this._printer.printText(char1);
                }
                else {
                    this.printEscaped(char1);
                }
            }
        }
        else {
            for (int j = 0; j < s.length(); ++j) {
                final char char2 = s.charAt(j);
                if (char2 == ' ' || char2 == '\f' || char2 == '\t' || char2 == '\n' || char2 == '\r') {
                    this._printer.printSpace();
                }
                else if (b2) {
                    this._printer.printText(char2);
                }
                else {
                    this.printEscaped(char2);
                }
            }
        }
    }
    
    protected void printDoctypeURL(final String s) throws IOException {
        this._printer.printText('\"');
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '\"' || s.charAt(i) < ' ' || s.charAt(i) > '\u007f') {
                this._printer.printText('%');
                this._printer.printText(Integer.toHexString(s.charAt(i)));
            }
            else {
                this._printer.printText(s.charAt(i));
            }
        }
        this._printer.printText('\"');
    }
    
    protected void printEscaped(final int n) throws IOException {
        final String entityRef = this.getEntityRef(n);
        if (entityRef != null) {
            this._printer.printText('&');
            this._printer.printText(entityRef);
            this._printer.printText(';');
        }
        else if ((n >= 32 && this._encodingInfo.isPrintable((char)n) && n != 127) || n == 10 || n == 13 || n == 9) {
            if (n < 65536) {
                this._printer.printText((char)n);
            }
            else {
                this._printer.printText((char)((n - 65536 >> 10) + 55296));
                this._printer.printText((char)((n - 65536 & 0x3FF) + 56320));
            }
        }
        else {
            this.printHex(n);
        }
    }
    
    final void printHex(final int n) throws IOException {
        this._printer.printText("&#x");
        this._printer.printText(Integer.toHexString(n));
        this._printer.printText(';');
    }
    
    protected void printEscaped(final String s) throws IOException {
        for (int i = 0; i < s.length(); ++i) {
            int char1 = s.charAt(i);
            if ((char1 & 0xFC00) == 0xD800 && i + 1 < s.length()) {
                final char char2 = s.charAt(i + 1);
                if ((char2 & '\ufc00') == 0xDC00) {
                    char1 = 65536 + (char1 - 55296 << 10) + char2 - 56320;
                    ++i;
                }
            }
            this.printEscaped(char1);
        }
    }
    
    protected ElementState getElementState() {
        return this._elementStates[this._elementStateCount];
    }
    
    protected ElementState enterElementState(final String namespaceURI, final String localName, final String rawName, final boolean preserveSpace) {
        if (this._elementStateCount + 1 == this._elementStates.length) {
            final ElementState[] elementStates = new ElementState[this._elementStates.length + 10];
            for (int i = 0; i < this._elementStates.length; ++i) {
                elementStates[i] = this._elementStates[i];
            }
            for (int j = this._elementStates.length; j < elementStates.length; ++j) {
                elementStates[j] = new ElementState();
            }
            this._elementStates = elementStates;
        }
        ++this._elementStateCount;
        final ElementState elementState = this._elementStates[this._elementStateCount];
        elementState.namespaceURI = namespaceURI;
        elementState.localName = localName;
        elementState.rawName = rawName;
        elementState.preserveSpace = preserveSpace;
        elementState.empty = true;
        elementState.afterElement = false;
        elementState.afterComment = false;
        final ElementState elementState2 = elementState;
        final ElementState elementState3 = elementState;
        final boolean b = false;
        elementState3.inCData = b;
        elementState2.doCData = b;
        elementState.unescaped = false;
        elementState.prefixes = this._prefixes;
        this._prefixes = null;
        return elementState;
    }
    
    protected ElementState leaveElementState() {
        if (this._elementStateCount > 0) {
            this._prefixes = null;
            --this._elementStateCount;
            return this._elementStates[this._elementStateCount];
        }
        throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "Internal", null));
    }
    
    protected boolean isDocumentState() {
        return this._elementStateCount == 0;
    }
    
    final void clearDocumentState() {
        this._elementStateCount = 0;
    }
    
    protected String getPrefix(final String s) {
        if (this._prefixes != null) {
            final String s2 = this._prefixes.get(s);
            if (s2 != null) {
                return s2;
            }
        }
        if (this._elementStateCount == 0) {
            return null;
        }
        for (int i = this._elementStateCount; i > 0; --i) {
            if (this._elementStates[i].prefixes != null) {
                final String s3 = this._elementStates[i].prefixes.get(s);
                if (s3 != null) {
                    return s3;
                }
            }
        }
        return null;
    }
    
    protected DOMError modifyDOMError(final String fMessage, final short fSeverity, final String fType, final Node node) {
        this.fDOMError.reset();
        this.fDOMError.fMessage = fMessage;
        this.fDOMError.fType = fType;
        this.fDOMError.fSeverity = fSeverity;
        this.fDOMError.fLocator = new DOMLocatorImpl(-1, -1, -1, node, null);
        return this.fDOMError;
    }
    
    protected void fatalError(final String s) throws IOException {
        if (this.fDOMErrorHandler != null) {
            this.modifyDOMError(s, (short)3, null, this.fCurrentNode);
            this.fDOMErrorHandler.handleError(this.fDOMError);
            return;
        }
        throw new IOException(s);
    }
    
    protected void checkUnboundNamespacePrefixedNode(final Node node) throws IOException {
    }
}
