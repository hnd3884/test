package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.EmptyStackException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.fastinfoset.Encoder;

public class StAXDocumentSerializer extends Encoder implements XMLStreamWriter, LowLevelFastInfosetStreamWriter
{
    protected StAXManager _manager;
    protected String _encoding;
    protected String _currentLocalName;
    protected String _currentUri;
    protected String _currentPrefix;
    protected boolean _inStartElement;
    protected boolean _isEmptyElement;
    protected String[] _attributesArray;
    protected int _attributesArrayIndex;
    protected boolean[] _nsSupportContextStack;
    protected int _stackCount;
    protected NamespaceContextImplementation _nsContext;
    protected String[] _namespacesArray;
    protected int _namespacesArrayIndex;
    
    public StAXDocumentSerializer() {
        super(true);
        this._inStartElement = false;
        this._isEmptyElement = false;
        this._attributesArray = new String[64];
        this._attributesArrayIndex = 0;
        this._nsSupportContextStack = new boolean[32];
        this._stackCount = -1;
        this._nsContext = new NamespaceContextImplementation();
        this._namespacesArray = new String[16];
        this._namespacesArrayIndex = 0;
        this._manager = new StAXManager(2);
    }
    
    public StAXDocumentSerializer(final OutputStream outputStream) {
        super(true);
        this._inStartElement = false;
        this._isEmptyElement = false;
        this._attributesArray = new String[64];
        this._attributesArrayIndex = 0;
        this._nsSupportContextStack = new boolean[32];
        this._stackCount = -1;
        this._nsContext = new NamespaceContextImplementation();
        this._namespacesArray = new String[16];
        this._namespacesArrayIndex = 0;
        this.setOutputStream(outputStream);
        this._manager = new StAXManager(2);
    }
    
    public StAXDocumentSerializer(final OutputStream outputStream, final StAXManager manager) {
        super(true);
        this._inStartElement = false;
        this._isEmptyElement = false;
        this._attributesArray = new String[64];
        this._attributesArrayIndex = 0;
        this._nsSupportContextStack = new boolean[32];
        this._stackCount = -1;
        this._nsContext = new NamespaceContextImplementation();
        this._namespacesArray = new String[16];
        this._namespacesArrayIndex = 0;
        this.setOutputStream(outputStream);
        this._manager = manager;
    }
    
    @Override
    public void reset() {
        super.reset();
        this._attributesArrayIndex = 0;
        this._namespacesArrayIndex = 0;
        this._nsContext.reset();
        this._stackCount = -1;
        final String s = null;
        this._currentPrefix = s;
        this._currentUri = s;
        this._currentLocalName = null;
        final boolean b = false;
        this._isEmptyElement = b;
        this._inStartElement = b;
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument("finf", "1.0");
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.writeStartDocument("finf", version);
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.reset();
        try {
            this.encodeHeader(false);
            this.encodeInitialVocabulary();
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            while (this._stackCount >= 0) {
                this.writeEndElement();
                --this._stackCount;
            }
            this.encodeDocumentTermination();
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.reset();
    }
    
    @Override
    public void flush() throws XMLStreamException {
        try {
            this._s.flush();
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.writeStartElement("", localName, "");
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeStartElement("", localName, namespaceURI);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.encodeTerminationAndCurrentElement(false);
        this._inStartElement = true;
        this._isEmptyElement = false;
        this._currentLocalName = localName;
        this._currentPrefix = prefix;
        this._currentUri = namespaceURI;
        ++this._stackCount;
        if (this._stackCount == this._nsSupportContextStack.length) {
            final boolean[] nsSupportContextStack = new boolean[this._stackCount * 2];
            System.arraycopy(this._nsSupportContextStack, 0, nsSupportContextStack, 0, this._nsSupportContextStack.length);
            this._nsSupportContextStack = nsSupportContextStack;
        }
        this._nsSupportContextStack[this._stackCount] = false;
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writeEmptyElement("", localName, "");
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeEmptyElement("", localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.encodeTerminationAndCurrentElement(false);
        final boolean b = true;
        this._inStartElement = b;
        this._isEmptyElement = b;
        this._currentLocalName = localName;
        this._currentPrefix = prefix;
        this._currentUri = namespaceURI;
        ++this._stackCount;
        if (this._stackCount == this._nsSupportContextStack.length) {
            final boolean[] nsSupportContextStack = new boolean[this._stackCount * 2];
            System.arraycopy(this._nsSupportContextStack, 0, nsSupportContextStack, 0, this._nsSupportContextStack.length);
            this._nsSupportContextStack = nsSupportContextStack;
        }
        this._nsSupportContextStack[this._stackCount] = false;
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this._inStartElement) {
            this.encodeTerminationAndCurrentElement(false);
        }
        try {
            this.encodeElementTermination();
            if (this._nsSupportContextStack[this._stackCount--]) {
                this._nsContext.popContext();
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
        catch (final EmptyStackException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.writeAttribute("", "", localName, value);
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        String prefix = "";
        if (namespaceURI.length() > 0) {
            prefix = this._nsContext.getNonDefaultPrefix(namespaceURI);
            if (prefix == null || prefix.length() == 0) {
                if (namespaceURI == "http://www.w3.org/2000/xmlns/" || namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                    return;
                }
                throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.URIUnbound", new Object[] { namespaceURI }));
            }
        }
        this.writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        if (!this._inStartElement) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
        }
        if (namespaceURI == "http://www.w3.org/2000/xmlns/" || namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            return;
        }
        if (this._attributesArrayIndex == this._attributesArray.length) {
            final String[] attributesArray = new String[this._attributesArrayIndex * 2];
            System.arraycopy(this._attributesArray, 0, attributesArray, 0, this._attributesArrayIndex);
            this._attributesArray = attributesArray;
        }
        this._attributesArray[this._attributesArrayIndex++] = namespaceURI;
        this._attributesArray[this._attributesArrayIndex++] = prefix;
        this._attributesArray[this._attributesArrayIndex++] = localName;
        this._attributesArray[this._attributesArrayIndex++] = value;
    }
    
    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) {
            this.writeDefaultNamespace(namespaceURI);
        }
        else {
            if (!this._inStartElement) {
                throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
            }
            if (this._namespacesArrayIndex == this._namespacesArray.length) {
                final String[] namespacesArray = new String[this._namespacesArrayIndex * 2];
                System.arraycopy(this._namespacesArray, 0, namespacesArray, 0, this._namespacesArrayIndex);
                this._namespacesArray = namespacesArray;
            }
            this.setPrefix(this._namespacesArray[this._namespacesArrayIndex++] = prefix, this._namespacesArray[this._namespacesArrayIndex++] = namespaceURI);
        }
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        if (!this._inStartElement) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
        }
        if (this._namespacesArrayIndex == this._namespacesArray.length) {
            final String[] namespacesArray = new String[this._namespacesArrayIndex * 2];
            System.arraycopy(this._namespacesArray, 0, namespacesArray, 0, this._namespacesArrayIndex);
            this._namespacesArray = namespacesArray;
        }
        this.setPrefix(this._namespacesArray[this._namespacesArrayIndex++] = "", this._namespacesArray[this._namespacesArrayIndex++] = namespaceURI);
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
        try {
            if (this.getIgnoreComments()) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeComment(data.toCharArray(), 0, data.length());
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, "");
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        try {
            if (this.getIgnoreProcesingInstructions()) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeProcessingInstruction(target, data);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeCData(final String text) throws XMLStreamException {
        try {
            final int length = text.length();
            if (length == 0) {
                return;
            }
            if (length < this._charBuffer.length) {
                if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(text)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                text.getChars(0, length, this._charBuffer, 0);
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(this._charBuffer, 0, length);
            }
            else {
                final char[] ch = text.toCharArray();
                if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(ch, 0, length)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, 0, length);
            }
        }
        catch (final Exception e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
    }
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        try {
            final int length = text.length();
            if (length == 0) {
                return;
            }
            if (length < this._charBuffer.length) {
                if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(text)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                text.getChars(0, length, this._charBuffer, 0);
                this.encodeCharacters(this._charBuffer, 0, length);
            }
            else {
                final char[] ch = text.toCharArray();
                if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(ch, 0, length)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                this.encodeCharactersNoClone(ch, 0, length);
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        try {
            if (len <= 0) {
                return;
            }
            if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(text, start, len)) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeCharacters(text, start, len);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this._nsContext.getPrefix(uri);
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        if (this._stackCount > -1 && !this._nsSupportContextStack[this._stackCount]) {
            this._nsSupportContextStack[this._stackCount] = true;
            this._nsContext.pushContext();
        }
        this._nsContext.declarePrefix(prefix, uri);
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.setPrefix("", uri);
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException("setNamespaceContext");
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this._nsContext;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (this._manager != null) {
            return this._manager.getProperty(name);
        }
        return null;
    }
    
    public void setManager(final StAXManager manager) {
        this._manager = manager;
    }
    
    public void setEncoding(final String encoding) {
        this._encoding = encoding;
    }
    
    public void writeOctets(final byte[] b, final int start, final int len) throws XMLStreamException {
        try {
            if (len == 0) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeCIIOctetAlgorithmData(1, b, start, len);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected void encodeTerminationAndCurrentElement(final boolean terminateAfter) throws XMLStreamException {
        try {
            this.encodeTermination();
            if (this._inStartElement) {
                this._b = 0;
                if (this._attributesArrayIndex > 0) {
                    this._b |= 0x40;
                }
                if (this._namespacesArrayIndex > 0) {
                    this.write(this._b | 0x38);
                    int i = 0;
                    while (i < this._namespacesArrayIndex) {
                        this.encodeNamespaceAttribute(this._namespacesArray[i++], this._namespacesArray[i++]);
                    }
                    this._namespacesArrayIndex = 0;
                    this.write(240);
                    this._b = 0;
                }
                if (this._currentPrefix.length() == 0) {
                    if (this._currentUri.length() == 0) {
                        this._currentUri = this._nsContext.getNamespaceURI("");
                    }
                    else {
                        final String tmpPrefix = this.getPrefix(this._currentUri);
                        if (tmpPrefix != null) {
                            this._currentPrefix = tmpPrefix;
                        }
                    }
                }
                this.encodeElementQualifiedNameOnThirdBit(this._currentUri, this._currentPrefix, this._currentLocalName);
                int i = 0;
                while (i < this._attributesArrayIndex) {
                    this.encodeAttributeQualifiedNameOnSecondBit(this._attributesArray[i++], this._attributesArray[i++], this._attributesArray[i++]);
                    final String value = this._attributesArray[i];
                    this._attributesArray[i++] = null;
                    final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                    this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
                    this._b = 240;
                    this._terminate = true;
                }
                this._attributesArrayIndex = 0;
                this._inStartElement = false;
                if (this._isEmptyElement) {
                    this.encodeElementTermination();
                    if (this._nsSupportContextStack[this._stackCount--]) {
                        this._nsContext.popContext();
                    }
                    this._isEmptyElement = false;
                }
                if (terminateAfter) {
                    this.encodeTermination();
                }
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public final void initiateLowLevelWriting() throws XMLStreamException {
        this.encodeTerminationAndCurrentElement(false);
    }
    
    @Override
    public final int getNextElementIndex() {
        return this._v.elementName.getNextIndex();
    }
    
    @Override
    public final int getNextAttributeIndex() {
        return this._v.attributeName.getNextIndex();
    }
    
    @Override
    public final int getLocalNameIndex() {
        return this._v.localName.getIndex();
    }
    
    @Override
    public final int getNextLocalNameIndex() {
        return this._v.localName.getNextIndex();
    }
    
    @Override
    public final void writeLowLevelTerminationAndMark() throws IOException {
        this.encodeTermination();
        this.mark();
    }
    
    @Override
    public final void writeLowLevelStartElementIndexed(final int type, final int index) throws IOException {
        this._b = type;
        this.encodeNonZeroIntegerOnThirdBit(index);
    }
    
    @Override
    public final boolean writeLowLevelStartElement(final int type, final String prefix, final String localName, final String namespaceURI) throws IOException {
        final boolean isIndexed = this.encodeElement(type, namespaceURI, prefix, localName);
        if (!isIndexed) {
            this.encodeLiteral(type | 0x3C, namespaceURI, prefix, localName);
        }
        return isIndexed;
    }
    
    @Override
    public final void writeLowLevelStartNamespaces() throws IOException {
        this.write(56);
    }
    
    @Override
    public final void writeLowLevelNamespace(final String prefix, final String namespaceName) throws IOException {
        this.encodeNamespaceAttribute(prefix, namespaceName);
    }
    
    @Override
    public final void writeLowLevelEndNamespaces() throws IOException {
        this.write(240);
    }
    
    @Override
    public final void writeLowLevelStartAttributes() throws IOException {
        if (this.hasMark()) {
            final byte[] octetBuffer = this._octetBuffer;
            final int markIndex = this._markIndex;
            octetBuffer[markIndex] |= 0x40;
            this.resetMark();
        }
    }
    
    @Override
    public final void writeLowLevelAttributeIndexed(final int index) throws IOException {
        this.encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
    }
    
    @Override
    public final boolean writeLowLevelAttribute(final String prefix, final String namespaceURI, final String localName) throws IOException {
        final boolean isIndexed = this.encodeAttribute(namespaceURI, prefix, localName);
        if (!isIndexed) {
            this.encodeLiteral(120, namespaceURI, prefix, localName);
        }
        return isIndexed;
    }
    
    @Override
    public final void writeLowLevelAttributeValue(final String value) throws IOException {
        final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
        this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
    }
    
    @Override
    public final void writeLowLevelStartNameLiteral(final int type, final String prefix, final byte[] utf8LocalName, final String namespaceURI) throws IOException {
        this.encodeLiteralHeader(type, namespaceURI, prefix);
        this.encodeNonZeroOctetStringLengthOnSecondBit(utf8LocalName.length);
        this.write(utf8LocalName, 0, utf8LocalName.length);
    }
    
    @Override
    public final void writeLowLevelStartNameLiteral(final int type, final String prefix, final int localNameIndex, final String namespaceURI) throws IOException {
        this.encodeLiteralHeader(type, namespaceURI, prefix);
        this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
    }
    
    @Override
    public final void writeLowLevelEndStartElement() throws IOException {
        if (this.hasMark()) {
            this.resetMark();
        }
        else {
            this._b = 240;
            this._terminate = true;
        }
    }
    
    @Override
    public final void writeLowLevelEndElement() throws IOException {
        this.encodeElementTermination();
    }
    
    @Override
    public final void writeLowLevelText(final char[] text, final int length) throws IOException {
        if (length == 0) {
            return;
        }
        this.encodeTermination();
        this.encodeCharacters(text, 0, length);
    }
    
    @Override
    public final void writeLowLevelText(final String text) throws IOException {
        final int length = text.length();
        if (length == 0) {
            return;
        }
        this.encodeTermination();
        if (length < this._charBuffer.length) {
            text.getChars(0, length, this._charBuffer, 0);
            this.encodeCharacters(this._charBuffer, 0, length);
        }
        else {
            final char[] ch = text.toCharArray();
            this.encodeCharactersNoClone(ch, 0, length);
        }
    }
    
    @Override
    public final void writeLowLevelOctets(final byte[] octets, final int length) throws IOException {
        if (length == 0) {
            return;
        }
        this.encodeTermination();
        this.encodeCIIOctetAlgorithmData(1, octets, 0, length);
    }
    
    private boolean encodeElement(final int type, final String namespaceURI, final String prefix, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(localName);
        for (int i = 0; i < entry._valueIndex; ++i) {
            final QualifiedName name = entry._value[i];
            if ((prefix == name.prefix || prefix.equals(name.prefix)) && (namespaceURI == name.namespaceName || namespaceURI.equals(name.namespaceName))) {
                this._b = type;
                this.encodeNonZeroIntegerOnThirdBit(name.index);
                return true;
            }
        }
        entry.addQualifiedName(new QualifiedName(prefix, namespaceURI, localName, "", this._v.elementName.getNextIndex()));
        return false;
    }
    
    private boolean encodeAttribute(final String namespaceURI, final String prefix, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(localName);
        for (int i = 0; i < entry._valueIndex; ++i) {
            final QualifiedName name = entry._value[i];
            if ((prefix == name.prefix || prefix.equals(name.prefix)) && (namespaceURI == name.namespaceName || namespaceURI.equals(name.namespaceName))) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitZero(name.index);
                return true;
            }
        }
        entry.addQualifiedName(new QualifiedName(prefix, namespaceURI, localName, "", this._v.attributeName.getNextIndex()));
        return false;
    }
    
    private void encodeLiteralHeader(int type, final String namespaceURI, final String prefix) throws IOException {
        if (namespaceURI != "") {
            type |= 0x1;
            if (prefix != "") {
                type |= 0x2;
            }
            this.write(type);
            if (prefix != "") {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(this._v.prefix.get(prefix));
            }
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(this._v.namespaceName.get(namespaceURI));
        }
        else {
            this.write(type);
        }
    }
    
    private void encodeLiteral(final int type, final String namespaceURI, final String prefix, final String localName) throws IOException {
        this.encodeLiteralHeader(type, namespaceURI, prefix);
        final int localNameIndex = this._v.localName.obtainIndex(localName);
        if (localNameIndex == -1) {
            this.encodeNonEmptyOctetStringOnSecondBit(localName);
        }
        else {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        }
    }
}
