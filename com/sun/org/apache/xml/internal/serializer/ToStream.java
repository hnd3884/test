package com.sun.org.apache.xml.internal.serializer;

import java.util.Hashtable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import java.util.StringTokenizer;
import java.util.Vector;
import org.xml.sax.Attributes;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.Properties;
import java.lang.reflect.Method;

public abstract class ToStream extends SerializerBase
{
    private static final String COMMENT_BEGIN = "<!--";
    private static final String COMMENT_END = "-->";
    protected BoolStack m_disableOutputEscapingStates;
    EncodingInfo m_encodingInfo;
    Method m_canConvertMeth;
    boolean m_triedToGetConverter;
    Object m_charToByteConverter;
    protected BoolStack m_preserves;
    protected boolean m_ispreserve;
    protected boolean m_isprevtext;
    protected int m_maxCharacter;
    protected char[] m_lineSep;
    protected boolean m_lineSepUse;
    protected int m_lineSepLen;
    protected CharInfo m_charInfo;
    boolean m_shouldFlush;
    protected boolean m_spaceBeforeClose;
    boolean m_startNewLine;
    protected boolean m_inDoctype;
    boolean m_isUTF8;
    protected Properties m_format;
    protected boolean m_cdataStartCalled;
    private boolean m_expandDTDEntities;
    private char m_highSurrogate;
    private boolean m_escaping;
    
    public ToStream() {
        this.m_disableOutputEscapingStates = new BoolStack();
        this.m_encodingInfo = new EncodingInfo(null, null);
        this.m_triedToGetConverter = false;
        this.m_charToByteConverter = null;
        this.m_preserves = new BoolStack();
        this.m_ispreserve = false;
        this.m_isprevtext = false;
        this.m_maxCharacter = Encodings.getLastPrintable();
        this.m_lineSep = SecuritySupport.getSystemProperty("line.separator").toCharArray();
        this.m_lineSepUse = true;
        this.m_lineSepLen = this.m_lineSep.length;
        this.m_shouldFlush = true;
        this.m_spaceBeforeClose = false;
        this.m_inDoctype = false;
        this.m_isUTF8 = false;
        this.m_cdataStartCalled = false;
        this.m_expandDTDEntities = true;
        this.m_highSurrogate = '\0';
        this.m_escaping = true;
    }
    
    protected void closeCDATA() throws SAXException {
        try {
            this.m_writer.write("]]>");
            this.m_cdataTagOpen = false;
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void serialize(final Node node) throws IOException {
        try {
            final TreeWalker walker = new TreeWalker(this);
            walker.traverse(node);
        }
        catch (final SAXException se) {
            throw new WrappedRuntimeException(se);
        }
    }
    
    static final boolean isUTF16Surrogate(final char c) {
        return (c & '\ufc00') == 0xD800;
    }
    
    protected final void flushWriter() throws SAXException {
        final Writer writer = this.m_writer;
        if (null != writer) {
            try {
                if (writer instanceof WriterToUTF8Buffered) {
                    if (this.m_shouldFlush) {
                        ((WriterToUTF8Buffered)writer).flush();
                    }
                    else {
                        ((WriterToUTF8Buffered)writer).flushBuffer();
                    }
                }
                if (writer instanceof WriterToASCI) {
                    if (this.m_shouldFlush) {
                        writer.flush();
                    }
                }
                else {
                    writer.flush();
                }
            }
            catch (final IOException ioe) {
                throw new SAXException(ioe);
            }
        }
    }
    
    @Override
    public OutputStream getOutputStream() {
        if (this.m_writer instanceof WriterToUTF8Buffered) {
            return ((WriterToUTF8Buffered)this.m_writer).getOutputStream();
        }
        if (this.m_writer instanceof WriterToASCI) {
            return ((WriterToASCI)this.m_writer).getOutputStream();
        }
        return null;
    }
    
    @Override
    public void elementDecl(final String name, final String model) throws SAXException {
        if (this.m_inExternalDTD) {
            return;
        }
        try {
            final Writer writer = this.m_writer;
            this.DTDprolog();
            writer.write("<!ELEMENT ");
            writer.write(name);
            writer.write(32);
            writer.write(model);
            writer.write(62);
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void internalEntityDecl(final String name, final String value) throws SAXException {
        if (this.m_inExternalDTD) {
            return;
        }
        try {
            this.DTDprolog();
            this.outputEntityDecl(name, value);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    void outputEntityDecl(final String name, final String value) throws IOException {
        final Writer writer = this.m_writer;
        writer.write("<!ENTITY ");
        writer.write(name);
        writer.write(" \"");
        writer.write(value);
        writer.write("\">");
        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    }
    
    protected final void outputLineSep() throws IOException {
        this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    }
    
    @Override
    public void setOutputFormat(final Properties format) {
        final boolean shouldFlush = this.m_shouldFlush;
        this.init(this.m_writer, format, false, false);
        this.m_shouldFlush = shouldFlush;
    }
    
    private synchronized void init(final Writer writer, final Properties format, final boolean defaultProperties, final boolean shouldFlush) {
        this.m_shouldFlush = shouldFlush;
        if (this.m_tracer != null && !(writer instanceof SerializerTraceWriter)) {
            this.m_writer = new SerializerTraceWriter(writer, this.m_tracer);
        }
        else {
            this.m_writer = writer;
        }
        this.setCdataSectionElements("cdata-section-elements", this.m_format = format);
        this.setIndentAmount(OutputPropertyUtils.getIntProperty("{http://xml.apache.org/xalan}indent-amount", format));
        this.setIndent(OutputPropertyUtils.getBooleanProperty("indent", format));
        final String sep = format.getProperty("{http://xml.apache.org/xalan}line-separator");
        if (sep != null) {
            this.m_lineSep = sep.toCharArray();
            this.m_lineSepLen = sep.length();
        }
        final boolean shouldNotWriteXMLHeader = OutputPropertyUtils.getBooleanProperty("omit-xml-declaration", format);
        this.setOmitXMLDeclaration(shouldNotWriteXMLHeader);
        this.setDoctypeSystem(format.getProperty("doctype-system"));
        final String doctypePublic = format.getProperty("doctype-public");
        this.setDoctypePublic(doctypePublic);
        if (format.get("standalone") != null) {
            final String val = format.getProperty("standalone");
            if (defaultProperties) {
                this.setStandaloneInternal(val);
            }
            else {
                this.setStandalone(val);
            }
        }
        this.setMediaType(format.getProperty("media-type"));
        if (null != doctypePublic && doctypePublic.startsWith("-//W3C//DTD XHTML")) {
            this.m_spaceBeforeClose = true;
        }
        String version = this.getVersion();
        if (null == version) {
            version = format.getProperty("version");
            this.setVersion(version);
        }
        String encoding = this.getEncoding();
        if (null == encoding) {
            encoding = Encodings.getMimeEncoding(format.getProperty("encoding"));
            this.setEncoding(encoding);
        }
        this.m_isUTF8 = encoding.equals("UTF-8");
        final String entitiesFileName = ((Hashtable<K, String>)format).get("{http://xml.apache.org/xalan}entities");
        if (null != entitiesFileName) {
            final String method = ((Hashtable<K, String>)format).get("method");
            this.m_charInfo = CharInfo.getCharInfo(entitiesFileName, method);
        }
    }
    
    private synchronized void init(final Writer writer, final Properties format) {
        this.init(writer, format, false, false);
    }
    
    protected synchronized void init(final OutputStream output, final Properties format, final boolean defaultProperties) throws UnsupportedEncodingException {
        String encoding = this.getEncoding();
        if (encoding == null) {
            encoding = Encodings.getMimeEncoding(format.getProperty("encoding"));
            this.setEncoding(encoding);
        }
        if (encoding.equalsIgnoreCase("UTF-8")) {
            this.m_isUTF8 = true;
            this.init(new WriterToUTF8Buffered(output), format, defaultProperties, true);
        }
        else if (encoding.equals("WINDOWS-1250") || encoding.equals("US-ASCII") || encoding.equals("ASCII")) {
            this.init(new WriterToASCI(output), format, defaultProperties, true);
        }
        else {
            Writer osw;
            try {
                osw = Encodings.getWriter(output, encoding);
            }
            catch (final UnsupportedEncodingException uee) {
                System.out.println("Warning: encoding \"" + encoding + "\" not supported, using " + "UTF-8");
                encoding = "UTF-8";
                this.setEncoding(encoding);
                osw = Encodings.getWriter(output, encoding);
            }
            this.init(osw, format, defaultProperties, true);
        }
    }
    
    @Override
    public Properties getOutputFormat() {
        return this.m_format;
    }
    
    @Override
    public void setWriter(final Writer writer) {
        if (this.m_tracer != null && !(writer instanceof SerializerTraceWriter)) {
            this.m_writer = new SerializerTraceWriter(writer, this.m_tracer);
        }
        else {
            this.m_writer = writer;
        }
    }
    
    public boolean setLineSepUse(final boolean use_sytem_line_break) {
        final boolean oldValue = this.m_lineSepUse;
        this.m_lineSepUse = use_sytem_line_break;
        return oldValue;
    }
    
    @Override
    public void setOutputStream(final OutputStream output) {
        try {
            Properties format;
            if (null == this.m_format) {
                format = OutputPropertiesFactory.getDefaultMethodProperties("xml");
            }
            else {
                format = this.m_format;
            }
            this.init(output, format, true);
        }
        catch (final UnsupportedEncodingException ex) {}
    }
    
    @Override
    public boolean setEscaping(final boolean escape) {
        final boolean temp = this.m_escaping;
        this.m_escaping = escape;
        return temp;
    }
    
    protected void indent(final int depth) throws IOException {
        if (this.m_startNewLine) {
            this.outputLineSep();
        }
        if (this.m_indentAmount > 0) {
            this.printSpace(depth * this.m_indentAmount);
        }
    }
    
    protected void indent() throws IOException {
        this.indent(this.m_elemContext.m_currentElemDepth);
    }
    
    private void printSpace(final int n) throws IOException {
        final Writer writer = this.m_writer;
        for (int i = 0; i < n; ++i) {
            writer.write(32);
        }
    }
    
    @Override
    public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String value) throws SAXException {
        if (this.m_inExternalDTD) {
            return;
        }
        try {
            final Writer writer = this.m_writer;
            this.DTDprolog();
            writer.write("<!ATTLIST ");
            writer.write(eName);
            writer.write(32);
            writer.write(aName);
            writer.write(32);
            writer.write(type);
            if (valueDefault != null) {
                writer.write(32);
                writer.write(valueDefault);
            }
            writer.write(62);
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public Writer getWriter() {
        return this.m_writer;
    }
    
    @Override
    public void externalEntityDecl(final String name, final String publicId, final String systemId) throws SAXException {
        try {
            this.DTDprolog();
            this.m_writer.write("<!ENTITY ");
            this.m_writer.write(name);
            if (publicId != null) {
                this.m_writer.write(" PUBLIC \"");
                this.m_writer.write(publicId);
            }
            else {
                this.m_writer.write(" SYSTEM \"");
                this.m_writer.write(systemId);
            }
            this.m_writer.write("\" >");
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    protected boolean escapingNotNeeded(final char ch) {
        boolean ret;
        if (ch < '\u007f') {
            ret = (ch >= ' ' || '\n' == ch || '\r' == ch || '\t' == ch);
        }
        else {
            ret = this.m_encodingInfo.isInEncoding(ch);
        }
        return ret;
    }
    
    protected int writeUTF16Surrogate(final char c, final char[] ch, final int i, final int end) throws IOException, SAXException {
        int status = -1;
        if (i + 1 >= end) {
            this.m_highSurrogate = c;
            return status;
        }
        char high;
        char low;
        if (this.m_highSurrogate == '\0') {
            high = c;
            low = ch[i + 1];
            status = 0;
        }
        else {
            high = this.m_highSurrogate;
            low = c;
            this.m_highSurrogate = '\0';
        }
        if (!Encodings.isLowUTF16Surrogate(low)) {
            this.throwIOE(high, low);
        }
        final Writer writer = this.m_writer;
        if (this.m_encodingInfo.isInEncoding(high, low)) {
            writer.write(new char[] { high, low }, 0, 2);
        }
        else {
            final String encoding = this.getEncoding();
            if (encoding != null) {
                status = this.writeCharRef(writer, high, low);
            }
            else {
                writer.write(new char[] { high, low }, 0, 2);
            }
        }
        return status;
    }
    
    protected int accumDefaultEntity(final Writer writer, final char ch, final int i, final char[] chars, final int len, final boolean fromTextNode, final boolean escLF) throws IOException {
        if (!escLF && '\n' == ch) {
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        else {
            if ((!fromTextNode || !this.m_charInfo.isSpecialTextChar(ch)) && (fromTextNode || !this.m_charInfo.isSpecialAttrChar(ch))) {
                return i;
            }
            final String outputStringForChar = this.m_charInfo.getOutputStringForChar(ch);
            if (null == outputStringForChar) {
                return i;
            }
            writer.write(outputStringForChar);
        }
        return i + 1;
    }
    
    void writeNormalizedChars(final char[] ch, final int start, final int length, final boolean isCData, final boolean useSystemLineSeparator) throws IOException, SAXException {
        final Writer writer = this.m_writer;
        for (int end = start + length, i = start; i < end; ++i) {
            final char c = ch[i];
            if ('\n' == c && useSystemLineSeparator) {
                writer.write(this.m_lineSep, 0, this.m_lineSepLen);
            }
            else if (isCData && !this.escapingNotNeeded(c)) {
                i = this.handleEscaping(writer, c, ch, i, end);
            }
            else if (isCData && i < end - 2 && ']' == c && ']' == ch[i + 1] && '>' == ch[i + 2]) {
                writer.write("]]]]><![CDATA[>");
                i += 2;
            }
            else if (this.escapingNotNeeded(c)) {
                if (isCData && !this.m_cdataTagOpen) {
                    writer.write("<![CDATA[");
                    this.m_cdataTagOpen = true;
                }
                writer.write(c);
            }
            else {
                i = this.handleEscaping(writer, c, ch, i, end);
            }
        }
    }
    
    private int handleEscaping(final Writer writer, final char c, final char[] ch, int i, final int end) throws IOException, SAXException {
        if (Encodings.isHighUTF16Surrogate(c) || Encodings.isLowUTF16Surrogate(c)) {
            if (this.writeUTF16Surrogate(c, ch, i, end) >= 0 && Encodings.isHighUTF16Surrogate(c)) {
                ++i;
            }
        }
        else {
            this.writeCharRef(writer, c);
        }
        return i;
    }
    
    public void endNonEscaping() throws SAXException {
        this.m_disableOutputEscapingStates.pop();
    }
    
    public void startNonEscaping() throws SAXException {
        this.m_disableOutputEscapingStates.push(true);
    }
    
    protected void cdata(final char[] ch, final int start, final int length) throws SAXException {
        try {
            final int old_start = start;
            if (this.m_elemContext.m_startTagOpen) {
                this.closeStartTag();
                this.m_elemContext.m_startTagOpen = false;
            }
            this.m_ispreserve = true;
            if (!this.m_cdataTagOpen && this.shouldIndent()) {
                this.indent();
            }
            final boolean writeCDataBrackets = length >= 1 && this.escapingNotNeeded(ch[start]);
            if (writeCDataBrackets && !this.m_cdataTagOpen) {
                this.m_writer.write("<![CDATA[");
                this.m_cdataTagOpen = true;
            }
            if (this.isEscapingDisabled()) {
                this.charactersRaw(ch, start, length);
            }
            else {
                this.writeNormalizedChars(ch, start, length, true, this.m_lineSepUse);
            }
            if (writeCDataBrackets && ch[start + length - 1] == ']') {
                this.closeCDATA();
            }
            if (this.m_tracer != null) {
                super.fireCDATAEvent(ch, old_start, length);
            }
        }
        catch (final IOException ioe) {
            throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), ioe);
        }
    }
    
    private boolean isEscapingDisabled() {
        return this.m_disableOutputEscapingStates.peekOrFalse();
    }
    
    protected void charactersRaw(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        try {
            if (this.m_elemContext.m_startTagOpen) {
                this.closeStartTag();
                this.m_elemContext.m_startTagOpen = false;
            }
            this.m_ispreserve = true;
            this.m_writer.write(ch, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        if (length == 0 || (this.m_inEntityRef && !this.m_expandDTDEntities)) {
            return;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        else if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
        }
        if (this.m_cdataStartCalled || this.m_elemContext.m_isCdataSection) {
            this.cdata(chars, start, length);
            return;
        }
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        if (this.m_disableOutputEscapingStates.peekOrFalse() || !this.m_escaping) {
            this.charactersRaw(chars, start, length);
            if (this.m_tracer != null) {
                super.fireCharEvent(chars, start, length);
            }
            return;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        try {
            final int end = start + length;
            int lastDirty = start - 1;
            int i;
            char ch1;
            for (i = start; i < end && ((ch1 = chars[i]) == ' ' || (ch1 == '\n' && this.m_lineSepUse) || ch1 == '\r' || ch1 == '\t'); ++i) {
                if (!this.m_charInfo.isTextASCIIClean(ch1)) {
                    lastDirty = (i = this.processDirty(chars, end, i, ch1, lastDirty, true));
                }
            }
            if (i < end) {
                this.m_ispreserve = true;
            }
            final boolean isXML10 = "1.0".equals(this.getVersion());
            while (i < end) {
                char ch2;
                while (i < end && (ch2 = chars[i]) < '\u007f' && this.m_charInfo.isTextASCIIClean(ch2)) {
                    ++i;
                }
                if (i == end) {
                    break;
                }
                final char ch3 = chars[i];
                if (isCharacterInC0orC1Range(ch3) || (!isXML10 && isNELorLSEPCharacter(ch3)) || !this.escapingNotNeeded(ch3) || this.m_charInfo.isSpecialTextChar(ch3)) {
                    if ('\"' != ch3) {
                        lastDirty = (i = this.processDirty(chars, end, i, ch3, lastDirty, true));
                    }
                }
                ++i;
            }
            final int startClean = lastDirty + 1;
            if (i > startClean) {
                final int lengthClean = i - startClean;
                this.m_writer.write(chars, startClean, lengthClean);
            }
            this.m_isprevtext = true;
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        if (this.m_tracer != null) {
            super.fireCharEvent(chars, start, length);
        }
    }
    
    private static boolean isCharacterInC0orC1Range(final char ch) {
        return ch != '\t' && ch != '\n' && ch != '\r' && ((ch >= '\u007f' && ch <= '\u009f') || (ch >= '\u0001' && ch <= '\u001f'));
    }
    
    private static boolean isNELorLSEPCharacter(final char ch) {
        return ch == '\u0085' || ch == '\u2028';
    }
    
    private int processDirty(final char[] chars, final int end, int i, final char ch, final int lastDirty, final boolean fromTextNode) throws IOException, SAXException {
        int startClean = lastDirty + 1;
        if (i > startClean) {
            final int lengthClean = i - startClean;
            this.m_writer.write(chars, startClean, lengthClean);
        }
        if ('\n' == ch && fromTextNode) {
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        else {
            startClean = this.accumDefaultEscape(this.m_writer, ch, i, chars, end, fromTextNode, false);
            i = startClean - 1;
        }
        return i;
    }
    
    @Override
    public void characters(final String s) throws SAXException {
        if (this.m_inEntityRef && !this.m_expandDTDEntities) {
            return;
        }
        final int length = s.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        s.getChars(0, length, this.m_charsBuff, 0);
        this.characters(this.m_charsBuff, 0, length);
    }
    
    protected int accumDefaultEscape(final Writer writer, final char ch, int i, final char[] chars, final int len, final boolean fromTextNode, final boolean escLF) throws IOException, SAXException {
        int pos = this.accumDefaultEntity(writer, ch, i, chars, len, fromTextNode, escLF);
        if (i == pos) {
            if (this.m_highSurrogate != '\0') {
                if (!Encodings.isLowUTF16Surrogate(ch)) {
                    this.throwIOE(this.m_highSurrogate, ch);
                }
                this.writeCharRef(writer, this.m_highSurrogate, ch);
                this.m_highSurrogate = '\0';
                return ++pos;
            }
            if (Encodings.isHighUTF16Surrogate(ch)) {
                if (i + 1 >= len) {
                    this.m_highSurrogate = ch;
                    ++pos;
                }
                else {
                    final char next = chars[++i];
                    if (!Encodings.isLowUTF16Surrogate(next)) {
                        this.throwIOE(ch, next);
                    }
                    this.writeCharRef(writer, ch, next);
                    pos += 2;
                }
            }
            else {
                if (isCharacterInC0orC1Range(ch) || ("1.1".equals(this.getVersion()) && isNELorLSEPCharacter(ch))) {
                    this.writeCharRef(writer, ch);
                }
                else if ((!this.escapingNotNeeded(ch) || (fromTextNode && this.m_charInfo.isSpecialTextChar(ch)) || (!fromTextNode && this.m_charInfo.isSpecialAttrChar(ch))) && this.m_elemContext.m_currentElemDepth > 0) {
                    this.writeCharRef(writer, ch);
                }
                else {
                    writer.write(ch);
                }
                ++pos;
            }
        }
        return pos;
    }
    
    private void writeCharRef(final Writer writer, final char c) throws IOException, SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        writer.write("&#");
        writer.write(Integer.toString(c));
        writer.write(59);
    }
    
    private int writeCharRef(final Writer writer, final char high, final char low) throws IOException, SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        final int codePoint = Encodings.toCodePoint(high, low);
        writer.write("&#");
        writer.write(Integer.toString(codePoint));
        writer.write(59);
        return codePoint;
    }
    
    private void throwIOE(final char ch, final char next) throws IOException {
        throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(ch) + " " + Integer.toHexString(next) }));
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String name, final Attributes atts) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        else if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        try {
            if (this.m_needToOutputDocTypeDecl && null != this.getDoctypeSystem()) {
                this.outputDocTypeDecl(name, true);
            }
            this.m_needToOutputDocTypeDecl = false;
            if (this.m_elemContext.m_startTagOpen) {
                this.closeStartTag();
                this.m_elemContext.m_startTagOpen = false;
            }
            if (namespaceURI != null) {
                this.ensurePrefixIsDeclared(namespaceURI, name);
            }
            this.m_ispreserve = false;
            if (this.shouldIndent() && this.m_startNewLine) {
                this.indent();
            }
            this.m_startNewLine = true;
            final Writer writer = this.m_writer;
            writer.write(60);
            writer.write(name);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        if (atts != null) {
            this.addAttributes(atts);
        }
        this.m_elemContext = this.m_elemContext.push(namespaceURI, localName, name);
        this.m_isprevtext = false;
        if (this.m_tracer != null) {
            this.firePseudoAttributes();
        }
    }
    
    @Override
    public void startElement(final String elementNamespaceURI, final String elementLocalName, final String elementName) throws SAXException {
        this.startElement(elementNamespaceURI, elementLocalName, elementName, null);
    }
    
    @Override
    public void startElement(final String elementName) throws SAXException {
        this.startElement(null, null, elementName, null);
    }
    
    void outputDocTypeDecl(final String name, boolean closeDecl) throws SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        try {
            final Writer writer = this.m_writer;
            writer.write("<!DOCTYPE ");
            writer.write(name);
            final String doctypePublic = this.getDoctypePublic();
            if (null != doctypePublic) {
                writer.write(" PUBLIC \"");
                writer.write(doctypePublic);
                writer.write(34);
            }
            final String doctypeSystem = this.getDoctypeSystem();
            if (null != doctypeSystem) {
                if (null == doctypePublic) {
                    writer.write(" SYSTEM \"");
                }
                else {
                    writer.write(" \"");
                }
                writer.write(doctypeSystem);
                if (closeDecl) {
                    writer.write("\">");
                    writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                    closeDecl = false;
                }
                else {
                    writer.write(34);
                }
            }
            final boolean dothis = false;
            if (dothis && closeDecl) {
                writer.write(62);
                writer.write(this.m_lineSep, 0, this.m_lineSepLen);
            }
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    public void processAttributes(final Writer writer, final int nAttrs) throws IOException, SAXException {
        final String encoding = this.getEncoding();
        for (int i = 0; i < nAttrs; ++i) {
            final String name = this.m_attributes.getQName(i);
            final String value = this.m_attributes.getValue(i);
            writer.write(32);
            writer.write(name);
            writer.write("=\"");
            this.writeAttrString(writer, value, encoding);
            writer.write(34);
        }
    }
    
    public void writeAttrString(final Writer writer, final String string, final String encoding) throws IOException, SAXException {
        final int len = string.length();
        if (len > this.m_attrBuff.length) {
            this.m_attrBuff = new char[len * 2 + 1];
        }
        string.getChars(0, len, this.m_attrBuff, 0);
        final char[] stringChars = this.m_attrBuff;
        int i = 0;
        while (i < len) {
            final char ch = stringChars[i];
            if (this.escapingNotNeeded(ch) && !this.m_charInfo.isSpecialAttrChar(ch)) {
                writer.write(ch);
                ++i;
            }
            else {
                i = this.accumDefaultEscape(writer, ch, i, stringChars, len, false, true);
            }
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String name) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        this.m_prefixMap.popNamespaces(this.m_elemContext.m_currentElemDepth, null);
        try {
            final Writer writer = this.m_writer;
            if (this.m_elemContext.m_startTagOpen) {
                if (this.m_tracer != null) {
                    super.fireStartElem(this.m_elemContext.m_elementName);
                }
                final int nAttrs = this.m_attributes.getLength();
                if (nAttrs > 0) {
                    this.processAttributes(this.m_writer, nAttrs);
                    this.m_attributes.clear();
                }
                if (this.m_spaceBeforeClose) {
                    writer.write(" />");
                }
                else {
                    writer.write("/>");
                }
            }
            else {
                if (this.m_cdataTagOpen) {
                    this.closeCDATA();
                }
                if (this.shouldIndent()) {
                    this.indent(this.m_elemContext.m_currentElemDepth - 1);
                }
                writer.write(60);
                writer.write(47);
                writer.write(name);
                writer.write(62);
            }
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        if (!this.m_elemContext.m_startTagOpen && this.m_doIndent) {
            this.m_ispreserve = (!this.m_preserves.isEmpty() && this.m_preserves.pop());
        }
        this.m_isprevtext = false;
        if (this.m_tracer != null) {
            super.fireEndElem(name);
        }
        this.m_elemContext = this.m_elemContext.m_prev;
    }
    
    @Override
    public void endElement(final String name) throws SAXException {
        this.endElement(null, null, name);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }
    
    @Override
    public boolean startPrefixMapping(final String prefix, final String uri, final boolean shouldFlush) throws SAXException {
        int pushDepth;
        if (shouldFlush) {
            this.flushPending();
            pushDepth = this.m_elemContext.m_currentElemDepth + 1;
        }
        else {
            pushDepth = this.m_elemContext.m_currentElemDepth;
        }
        final boolean pushed = this.m_prefixMap.pushNamespace(prefix, uri, pushDepth);
        if (pushed) {
            if ("".equals(prefix)) {
                final String name = "xmlns";
                this.addAttributeAlways("http://www.w3.org/2000/xmlns/", name, name, "CDATA", uri, false);
            }
            else if (!"".equals(uri)) {
                final String name = "xmlns:" + prefix;
                this.addAttributeAlways("http://www.w3.org/2000/xmlns/", prefix, name, "CDATA", uri, false);
            }
        }
        return pushed;
    }
    
    @Override
    public void comment(final char[] ch, int start, final int length) throws SAXException {
        final int start_old = start;
        if (this.m_inEntityRef) {
            return;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        else if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        try {
            if (this.shouldIndent() && this.m_isStandalone) {
                this.indent();
            }
            final int limit = start + length;
            boolean wasDash = false;
            if (this.m_cdataTagOpen) {
                this.closeCDATA();
            }
            if (this.shouldIndent() && !this.m_isStandalone) {
                this.indent();
            }
            final Writer writer = this.m_writer;
            writer.write("<!--");
            for (int i = start; i < limit; ++i) {
                if (wasDash && ch[i] == '-') {
                    writer.write(ch, start, i - start);
                    writer.write(" -");
                    start = i + 1;
                }
                wasDash = (ch[i] == '-');
            }
            if (length > 0) {
                final int remainingChars = limit - start;
                if (remainingChars > 0) {
                    writer.write(ch, start, remainingChars);
                }
                if (ch[limit - 1] == '-') {
                    writer.write(32);
                }
            }
            writer.write("-->");
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        this.m_startNewLine = true;
        if (this.m_tracer != null) {
            super.fireCommentEvent(ch, start_old, length);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        this.m_cdataStartCalled = false;
    }
    
    @Override
    public void endDTD() throws SAXException {
        try {
            if (this.m_needToCallStartDocument) {
                return;
            }
            if (this.m_needToOutputDocTypeDecl) {
                this.outputDocTypeDecl(this.m_elemContext.m_elementName, false);
                this.m_needToOutputDocTypeDecl = false;
            }
            final Writer writer = this.m_writer;
            if (!this.m_inDoctype) {
                writer.write("]>");
            }
            else {
                writer.write(62);
            }
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (0 == length) {
            return;
        }
        this.characters(ch, start, length);
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.m_cdataStartCalled = true;
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
        if (name.equals("[dtd]")) {
            this.m_inExternalDTD = true;
        }
        if (!this.m_expandDTDEntities && !this.m_inExternalDTD) {
            this.startNonEscaping();
            this.characters("&" + name + ';');
            this.endNonEscaping();
        }
        this.m_inEntityRef = true;
    }
    
    protected void closeStartTag() throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            try {
                if (this.m_tracer != null) {
                    super.fireStartElem(this.m_elemContext.m_elementName);
                }
                final int nAttrs = this.m_attributes.getLength();
                if (nAttrs > 0) {
                    this.processAttributes(this.m_writer, nAttrs);
                    this.m_attributes.clear();
                }
                this.m_writer.write(62);
            }
            catch (final IOException e) {
                throw new SAXException(e);
            }
            if (this.m_cdataSectionElements != null) {
                this.m_elemContext.m_isCdataSection = this.isCdataSection();
            }
            if (this.m_doIndent) {
                this.m_isprevtext = false;
                this.m_preserves.push(this.m_ispreserve);
            }
        }
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        this.setDoctypeSystem(systemId);
        this.setDoctypePublic(publicId);
        this.m_elemContext.m_elementName = name;
        this.m_inDoctype = true;
    }
    
    @Override
    public int getIndentAmount() {
        return this.m_indentAmount;
    }
    
    @Override
    public void setIndentAmount(final int m_indentAmount) {
        this.m_indentAmount = m_indentAmount;
    }
    
    protected boolean shouldIndent() {
        return this.m_doIndent && !this.m_ispreserve && !this.m_isprevtext && (this.m_elemContext.m_currentElemDepth > 0 || this.m_isStandalone);
    }
    
    private void setCdataSectionElements(final String key, final Properties props) {
        final String s = props.getProperty(key);
        if (null != s) {
            final Vector v = new Vector();
            final int l = s.length();
            boolean inCurly = false;
            final StringBuffer buf = new StringBuffer();
            for (int i = 0; i < l; ++i) {
                final char c = s.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (!inCurly) {
                        if (buf.length() > 0) {
                            this.addCdataSectionElement(buf.toString(), v);
                            buf.setLength(0);
                        }
                        continue;
                    }
                }
                else if ('{' == c) {
                    inCurly = true;
                }
                else if ('}' == c) {
                    inCurly = false;
                }
                buf.append(c);
            }
            if (buf.length() > 0) {
                this.addCdataSectionElement(buf.toString(), v);
                buf.setLength(0);
            }
            this.setCdataSectionElements(v);
        }
    }
    
    private void addCdataSectionElement(final String URI_and_localName, final Vector v) {
        final StringTokenizer tokenizer = new StringTokenizer(URI_and_localName, "{}", false);
        final String s1 = tokenizer.nextToken();
        final String s2 = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        if (null == s2) {
            v.addElement(null);
            v.addElement(s1);
        }
        else {
            v.addElement(s1);
            v.addElement(s2);
        }
    }
    
    @Override
    public void setCdataSectionElements(final Vector URI_and_localNames) {
        this.m_cdataSectionElements = URI_and_localNames;
    }
    
    protected String ensureAttributesNamespaceIsDeclared(final String ns, final String localName, final String rawName) throws SAXException {
        if (ns == null || ns.length() <= 0) {
            return null;
        }
        int index = 0;
        final String prefixFromRawName = ((index = rawName.indexOf(":")) < 0) ? "" : rawName.substring(0, index);
        if (index <= 0) {
            String prefix = this.m_prefixMap.lookupPrefix(ns);
            if (prefix == null) {
                prefix = this.m_prefixMap.generateNextPrefix();
                this.startPrefixMapping(prefix, ns, false);
                this.addAttribute("http://www.w3.org/2000/xmlns/", prefix, "xmlns:" + prefix, "CDATA", ns, false);
            }
            return prefix;
        }
        final String uri = this.m_prefixMap.lookupNamespace(prefixFromRawName);
        if (uri != null && uri.equals(ns)) {
            return null;
        }
        this.startPrefixMapping(prefixFromRawName, ns, false);
        this.addAttribute("http://www.w3.org/2000/xmlns/", prefixFromRawName, "xmlns:" + prefixFromRawName, "CDATA", ns, false);
        return prefixFromRawName;
    }
    
    void ensurePrefixIsDeclared(final String ns, final String rawName) throws SAXException {
        if (ns != null && ns.length() > 0) {
            final int index;
            final boolean no_prefix = (index = rawName.indexOf(":")) < 0;
            final String prefix = no_prefix ? "" : rawName.substring(0, index);
            if (null != prefix) {
                final String foundURI = this.m_prefixMap.lookupNamespace(prefix);
                if (null == foundURI || !foundURI.equals(ns)) {
                    this.startPrefixMapping(prefix, ns);
                    this.addAttributeAlways("http://www.w3.org/2000/xmlns/", no_prefix ? "xmlns" : prefix, no_prefix ? "xmlns" : ("xmlns:" + prefix), "CDATA", ns, false);
                }
            }
        }
    }
    
    @Override
    public void flushPending() throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
            this.m_cdataTagOpen = false;
        }
    }
    
    @Override
    public void setContentHandler(final ContentHandler ch) {
    }
    
    @Override
    public boolean addAttributeAlways(final String uri, final String localName, String rawName, final String type, final String value, final boolean xslAttribute) {
        final int index = this.m_attributes.getIndex(rawName);
        boolean was_added;
        if (index >= 0) {
            String old_value = null;
            if (this.m_tracer != null) {
                old_value = this.m_attributes.getValue(index);
                if (value.equals(old_value)) {
                    old_value = null;
                }
            }
            this.m_attributes.setValue(index, value);
            was_added = false;
            if (old_value != null) {
                this.firePseudoAttributes();
            }
        }
        else {
            if (xslAttribute) {
                final int colonIndex = rawName.indexOf(58);
                if (colonIndex > 0) {
                    String prefix = rawName.substring(0, colonIndex);
                    final NamespaceMappings.MappingRecord existing_mapping = this.m_prefixMap.getMappingFromPrefix(prefix);
                    if (existing_mapping != null && existing_mapping.m_declarationDepth == this.m_elemContext.m_currentElemDepth && !existing_mapping.m_uri.equals(uri)) {
                        prefix = this.m_prefixMap.lookupPrefix(uri);
                        if (prefix == null) {
                            prefix = this.m_prefixMap.generateNextPrefix();
                        }
                        rawName = prefix + ':' + localName;
                    }
                }
                try {
                    this.ensureAttributesNamespaceIsDeclared(uri, localName, rawName);
                }
                catch (final SAXException e) {
                    e.printStackTrace();
                }
            }
            this.m_attributes.addAttribute(uri, localName, rawName, type, value);
            was_added = true;
            if (this.m_tracer != null) {
                this.firePseudoAttributes();
            }
        }
        return was_added;
    }
    
    protected void firePseudoAttributes() {
        if (this.m_tracer != null) {
            try {
                this.m_writer.flush();
                final StringBuffer sb = new StringBuffer();
                final int nAttrs = this.m_attributes.getLength();
                if (nAttrs > 0) {
                    final Writer writer = new WritertoStringBuffer(sb);
                    this.processAttributes(writer, nAttrs);
                }
                sb.append('>');
                final char[] ch = sb.toString().toCharArray();
                this.m_tracer.fireGenerateEvent(11, ch, 0, ch.length);
            }
            catch (final IOException ex) {}
            catch (final SAXException ex2) {}
        }
    }
    
    @Override
    public void setTransformer(final Transformer transformer) {
        super.setTransformer(transformer);
        if (this.m_tracer != null && !(this.m_writer instanceof SerializerTraceWriter)) {
            this.m_writer = new SerializerTraceWriter(this.m_writer, this.m_tracer);
        }
    }
    
    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToStream();
            wasReset = true;
        }
        return wasReset;
    }
    
    private void resetToStream() {
        this.m_cdataStartCalled = false;
        this.m_disableOutputEscapingStates.clear();
        this.m_escaping = true;
        this.m_inDoctype = false;
        this.m_ispreserve = false;
        this.m_ispreserve = false;
        this.m_isprevtext = false;
        this.m_isUTF8 = false;
        this.m_preserves.clear();
        this.m_shouldFlush = true;
        this.m_spaceBeforeClose = false;
        this.m_startNewLine = false;
        this.m_lineSepUse = true;
        this.m_expandDTDEntities = true;
    }
    
    @Override
    public void setEncoding(final String encoding) {
        final String old = this.getEncoding();
        super.setEncoding(encoding);
        if (old == null || !old.equals(encoding)) {
            this.m_encodingInfo = Encodings.getEncodingInfo(encoding);
            if (encoding != null && this.m_encodingInfo.name == null) {
                final String msg = Utils.messages.createMessage("ER_ENCODING_NOT_SUPPORTED", new Object[] { encoding });
                try {
                    final Transformer tran = super.getTransformer();
                    if (tran != null) {
                        final ErrorListener errHandler = tran.getErrorListener();
                        if (null != errHandler && this.m_sourceLocator != null) {
                            errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
                        }
                        else {
                            System.out.println(msg);
                        }
                    }
                    else {
                        System.out.println(msg);
                    }
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    @Override
    public void notationDecl(final String name, final String pubID, final String sysID) throws SAXException {
        try {
            this.DTDprolog();
            this.m_writer.write("<!NOTATION ");
            this.m_writer.write(name);
            if (pubID != null) {
                this.m_writer.write(" PUBLIC \"");
                this.m_writer.write(pubID);
            }
            else {
                this.m_writer.write(" SYSTEM \"");
                this.m_writer.write(sysID);
            }
            this.m_writer.write("\" >");
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String pubID, final String sysID, final String notationName) throws SAXException {
        try {
            this.DTDprolog();
            this.m_writer.write("<!ENTITY ");
            this.m_writer.write(name);
            if (pubID != null) {
                this.m_writer.write(" PUBLIC \"");
                this.m_writer.write(pubID);
            }
            else {
                this.m_writer.write(" SYSTEM \"");
                this.m_writer.write(sysID);
            }
            this.m_writer.write("\" NDATA ");
            this.m_writer.write(notationName);
            this.m_writer.write(" >");
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private void DTDprolog() throws SAXException, IOException {
        final Writer writer = this.m_writer;
        if (this.m_needToOutputDocTypeDecl) {
            this.outputDocTypeDecl(this.m_elemContext.m_elementName, false);
            this.m_needToOutputDocTypeDecl = false;
        }
        if (this.m_inDoctype) {
            writer.write(" [");
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
            this.m_inDoctype = false;
        }
    }
    
    @Override
    public void setDTDEntityExpansion(final boolean expand) {
        this.m_expandDTDEntities = expand;
    }
    
    private class WritertoStringBuffer extends Writer
    {
        private final StringBuffer m_stringbuf;
        
        WritertoStringBuffer(final StringBuffer sb) {
            this.m_stringbuf = sb;
        }
        
        @Override
        public void write(final char[] arg0, final int arg1, final int arg2) throws IOException {
            this.m_stringbuf.append(arg0, arg1, arg2);
        }
        
        @Override
        public void flush() throws IOException {
        }
        
        @Override
        public void close() throws IOException {
        }
        
        @Override
        public void write(final int i) {
            this.m_stringbuf.append((char)i);
        }
        
        @Override
        public void write(final String s) {
            this.m_stringbuf.append(s);
        }
    }
    
    static final class BoolStack
    {
        private boolean[] m_values;
        private int m_allocatedSize;
        private int m_index;
        
        public BoolStack() {
            this(32);
        }
        
        public BoolStack(final int size) {
            this.m_allocatedSize = size;
            this.m_values = new boolean[size];
            this.m_index = -1;
        }
        
        public final int size() {
            return this.m_index + 1;
        }
        
        public final void clear() {
            this.m_index = -1;
        }
        
        public final boolean push(final boolean val) {
            if (this.m_index == this.m_allocatedSize - 1) {
                this.grow();
            }
            return this.m_values[++this.m_index] = val;
        }
        
        public final boolean pop() {
            return this.m_values[this.m_index--];
        }
        
        public final boolean popAndTop() {
            --this.m_index;
            return this.m_index >= 0 && this.m_values[this.m_index];
        }
        
        public final void setTop(final boolean b) {
            this.m_values[this.m_index] = b;
        }
        
        public final boolean peek() {
            return this.m_values[this.m_index];
        }
        
        public final boolean peekOrFalse() {
            return this.m_index > -1 && this.m_values[this.m_index];
        }
        
        public final boolean peekOrTrue() {
            return this.m_index <= -1 || this.m_values[this.m_index];
        }
        
        public boolean isEmpty() {
            return this.m_index == -1;
        }
        
        private void grow() {
            this.m_allocatedSize *= 2;
            final boolean[] newVector = new boolean[this.m_allocatedSize];
            System.arraycopy(this.m_values, 0, newVector, 0, this.m_index + 1);
            this.m_values = newVector;
        }
    }
}
