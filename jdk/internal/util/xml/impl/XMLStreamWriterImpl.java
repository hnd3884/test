package jdk.internal.util.xml.impl;

import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import jdk.internal.util.xml.XMLStreamException;
import java.io.OutputStream;
import jdk.internal.util.xml.XMLStreamWriter;

public class XMLStreamWriterImpl implements XMLStreamWriter
{
    static final int STATE_XML_DECL = 1;
    static final int STATE_PROLOG = 2;
    static final int STATE_DTD_DECL = 3;
    static final int STATE_ELEMENT = 4;
    static final int ELEMENT_STARTTAG_OPEN = 10;
    static final int ELEMENT_STARTTAG_CLOSE = 11;
    static final int ELEMENT_ENDTAG_OPEN = 12;
    static final int ELEMENT_ENDTAG_CLOSE = 13;
    public static final char CLOSE_START_TAG = '>';
    public static final char OPEN_START_TAG = '<';
    public static final String OPEN_END_TAG = "</";
    public static final char CLOSE_END_TAG = '>';
    public static final String START_CDATA = "<![CDATA[";
    public static final String END_CDATA = "]]>";
    public static final String CLOSE_EMPTY_ELEMENT = "/>";
    public static final String ENCODING_PREFIX = "&#x";
    public static final char SPACE = ' ';
    public static final char AMPERSAND = '&';
    public static final char DOUBLEQUOT = '\"';
    public static final char SEMICOLON = ';';
    private int _state;
    private Element _currentEle;
    private XMLWriter _writer;
    private String _encoding;
    boolean _escapeCharacters;
    private boolean _doIndent;
    private char[] _lineSep;
    
    public XMLStreamWriterImpl(final OutputStream outputStream) throws XMLStreamException {
        this(outputStream, "UTF-8");
    }
    
    public XMLStreamWriterImpl(final OutputStream outputStream, final String encoding) throws XMLStreamException {
        this._state = 0;
        this._escapeCharacters = true;
        this._doIndent = true;
        this._lineSep = System.getProperty("line.separator").toCharArray();
        Charset charset = null;
        if (encoding == null) {
            this._encoding = "UTF-8";
        }
        else {
            try {
                charset = this.getCharset(encoding);
            }
            catch (final UnsupportedEncodingException ex) {
                throw new XMLStreamException(ex);
            }
            this._encoding = encoding;
        }
        this._writer = new XMLWriter(outputStream, encoding, charset);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(this._encoding, "1.0");
    }
    
    @Override
    public void writeStartDocument(final String s) throws XMLStreamException {
        this.writeStartDocument(this._encoding, s, null);
    }
    
    @Override
    public void writeStartDocument(final String s, final String s2) throws XMLStreamException {
        this.writeStartDocument(s, s2, null);
    }
    
    public void writeStartDocument(final String s, String s2, final String s3) throws XMLStreamException {
        if (this._state > 0) {
            throw new XMLStreamException("XML declaration must be as the first line in the XML document.");
        }
        this._state = 1;
        String encoding = s;
        if (encoding == null) {
            encoding = this._encoding;
        }
        else {
            try {
                this.getCharset(s);
            }
            catch (final UnsupportedEncodingException ex) {
                throw new XMLStreamException(ex);
            }
        }
        if (s2 == null) {
            s2 = "1.0";
        }
        this._writer.write("<?xml version=\"");
        this._writer.write(s2);
        this._writer.write(34);
        if (encoding != null) {
            this._writer.write(" encoding=\"");
            this._writer.write(encoding);
            this._writer.write(34);
        }
        if (s3 != null) {
            this._writer.write(" standalone=\"");
            this._writer.write(s3);
            this._writer.write(34);
        }
        this._writer.write("?>");
        this.writeLineSeparator();
    }
    
    @Override
    public void writeDTD(final String s) throws XMLStreamException {
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        this._writer.write(s);
        this.writeLineSeparator();
    }
    
    @Override
    public void writeStartElement(final String s) throws XMLStreamException {
        if (s == null || s.length() == 0) {
            throw new XMLStreamException("Local Name cannot be null or empty");
        }
        this._state = 4;
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        this._currentEle = new Element(this._currentEle, s, false);
        this.openStartTag();
        this._writer.write(s);
    }
    
    @Override
    public void writeEmptyElement(final String s) throws XMLStreamException {
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        this._currentEle = new Element(this._currentEle, s, true);
        this.openStartTag();
        this._writer.write(s);
    }
    
    @Override
    public void writeAttribute(final String s, final String s2) throws XMLStreamException {
        if (this._currentEle.getState() != 10) {
            throw new XMLStreamException("Attribute not associated with any element");
        }
        this._writer.write(32);
        this._writer.write(s);
        this._writer.write("=\"");
        this.writeXMLContent(s2, true, true);
        this._writer.write(34);
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        while (this._currentEle != null) {
            if (!this._currentEle.isEmpty()) {
                this._writer.write("</");
                this._writer.write(this._currentEle.getLocalName());
                this._writer.write(62);
            }
            this._currentEle = this._currentEle.getParent();
        }
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        if (this._currentEle == null) {
            throw new XMLStreamException("No element was found to write");
        }
        if (this._currentEle.isEmpty()) {
            return;
        }
        this._writer.write("</");
        this._writer.write(this._currentEle.getLocalName());
        this._writer.write(62);
        this.writeLineSeparator();
        this._currentEle = this._currentEle.getParent();
    }
    
    @Override
    public void writeCData(final String s) throws XMLStreamException {
        if (s == null) {
            throw new XMLStreamException("cdata cannot be null");
        }
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        this._writer.write("<![CDATA[");
        this._writer.write(s);
        this._writer.write("]]>");
    }
    
    @Override
    public void writeCharacters(final String s) throws XMLStreamException {
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        this.writeXMLContent(s);
    }
    
    @Override
    public void writeCharacters(final char[] array, final int n, final int n2) throws XMLStreamException {
        if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
        }
        this.writeXMLContent(array, n, n2, this._escapeCharacters);
    }
    
    @Override
    public void close() throws XMLStreamException {
        if (this._writer != null) {
            this._writer.close();
        }
        this._writer = null;
        this._currentEle = null;
        this._state = 0;
    }
    
    @Override
    public void flush() throws XMLStreamException {
        if (this._writer != null) {
            this._writer.flush();
        }
    }
    
    public void setDoIndent(final boolean doIndent) {
        this._doIndent = doIndent;
    }
    
    private void writeXMLContent(final char[] array, final int n, final int n2, final boolean b) throws XMLStreamException {
        if (!b) {
            this._writer.write(array, n, n2);
            return;
        }
        int n3 = n;
        final int n4 = n + n2;
        for (int i = n; i < n4; ++i) {
            final char c = array[i];
            if (!this._writer.canEncode(c)) {
                this._writer.write(array, n3, i - n3);
                this._writer.write("&#x");
                this._writer.write(Integer.toHexString(c));
                this._writer.write(59);
                n3 = i + 1;
            }
            else {
                switch (c) {
                    case '<': {
                        this._writer.write(array, n3, i - n3);
                        this._writer.write("&lt;");
                        n3 = i + 1;
                        break;
                    }
                    case '&': {
                        this._writer.write(array, n3, i - n3);
                        this._writer.write("&amp;");
                        n3 = i + 1;
                        break;
                    }
                    case '>': {
                        this._writer.write(array, n3, i - n3);
                        this._writer.write("&gt;");
                        n3 = i + 1;
                        break;
                    }
                }
            }
        }
        this._writer.write(array, n3, n4 - n3);
    }
    
    private void writeXMLContent(final String s) throws XMLStreamException {
        if (s != null && s.length() > 0) {
            this.writeXMLContent(s, this._escapeCharacters, false);
        }
    }
    
    private void writeXMLContent(final String s, final boolean b, final boolean b2) throws XMLStreamException {
        if (!b) {
            this._writer.write(s);
            return;
        }
        int n = 0;
        final int length = s.length();
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (!this._writer.canEncode(char1)) {
                this._writer.write(s, n, i - n);
                this._writer.write("&#x");
                this._writer.write(Integer.toHexString(char1));
                this._writer.write(59);
                n = i + 1;
            }
            else {
                switch (char1) {
                    case '<': {
                        this._writer.write(s, n, i - n);
                        this._writer.write("&lt;");
                        n = i + 1;
                        break;
                    }
                    case '&': {
                        this._writer.write(s, n, i - n);
                        this._writer.write("&amp;");
                        n = i + 1;
                        break;
                    }
                    case '>': {
                        this._writer.write(s, n, i - n);
                        this._writer.write("&gt;");
                        n = i + 1;
                        break;
                    }
                    case '\"': {
                        this._writer.write(s, n, i - n);
                        if (b2) {
                            this._writer.write("&quot;");
                        }
                        else {
                            this._writer.write(34);
                        }
                        n = i + 1;
                        break;
                    }
                }
            }
        }
        this._writer.write(s, n, length - n);
    }
    
    private void openStartTag() throws XMLStreamException {
        this._currentEle.setState(10);
        this._writer.write(60);
    }
    
    private void closeStartTag() throws XMLStreamException {
        if (this._currentEle.isEmpty()) {
            this._writer.write("/>");
        }
        else {
            this._writer.write(62);
        }
        if (this._currentEle.getParent() == null) {
            this.writeLineSeparator();
        }
        this._currentEle.setState(11);
    }
    
    private void writeLineSeparator() throws XMLStreamException {
        if (this._doIndent) {
            this._writer.write(this._lineSep, 0, this._lineSep.length);
        }
    }
    
    private Charset getCharset(final String s) throws UnsupportedEncodingException {
        if (s.equalsIgnoreCase("UTF-32")) {
            throw new UnsupportedEncodingException("The basic XMLWriter does not support " + s);
        }
        Charset forName;
        try {
            forName = Charset.forName(s);
        }
        catch (final IllegalCharsetNameException | UnsupportedCharsetException ex) {
            throw new UnsupportedEncodingException(s);
        }
        return forName;
    }
    
    protected class Element
    {
        protected Element _parent;
        protected short _Depth;
        boolean _isEmptyElement;
        String _localpart;
        int _state;
        
        public Element() {
            this._isEmptyElement = false;
        }
        
        public Element(final Element parent, final String localpart, final boolean isEmptyElement) {
            this._isEmptyElement = false;
            this._parent = parent;
            this._localpart = localpart;
            this._isEmptyElement = isEmptyElement;
        }
        
        public Element getParent() {
            return this._parent;
        }
        
        public String getLocalName() {
            return this._localpart;
        }
        
        public int getState() {
            return this._state;
        }
        
        public void setState(final int state) {
            this._state = state;
        }
        
        public boolean isEmpty() {
            return this._isEmptyElement;
        }
    }
}
