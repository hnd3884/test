package com.sun.org.apache.xml.internal.serialize;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import java.io.UnsupportedEncodingException;
import org.w3c.dom.Document;

public class OutputFormat
{
    private String _method;
    private String _version;
    private int _indent;
    private String _encoding;
    private EncodingInfo _encodingInfo;
    private boolean _allowJavaNames;
    private String _mediaType;
    private String _doctypeSystem;
    private String _doctypePublic;
    private boolean _omitXmlDeclaration;
    private boolean _omitDoctype;
    private boolean _omitComments;
    private boolean _stripComments;
    private boolean _standalone;
    private String[] _cdataElements;
    private String[] _nonEscapingElements;
    private String _lineSeparator;
    private int _lineWidth;
    private boolean _preserve;
    private boolean _preserveEmptyAttributes;
    
    public OutputFormat() {
        this._indent = 0;
        this._encoding = "UTF-8";
        this._encodingInfo = null;
        this._allowJavaNames = false;
        this._omitXmlDeclaration = false;
        this._omitDoctype = false;
        this._omitComments = false;
        this._stripComments = false;
        this._standalone = false;
        this._lineSeparator = "\n";
        this._lineWidth = 72;
        this._preserve = false;
        this._preserveEmptyAttributes = false;
    }
    
    public OutputFormat(final String method, final String encoding, final boolean indenting) {
        this._indent = 0;
        this._encoding = "UTF-8";
        this._encodingInfo = null;
        this._allowJavaNames = false;
        this._omitXmlDeclaration = false;
        this._omitDoctype = false;
        this._omitComments = false;
        this._stripComments = false;
        this._standalone = false;
        this._lineSeparator = "\n";
        this._lineWidth = 72;
        this._preserve = false;
        this._preserveEmptyAttributes = false;
        this.setMethod(method);
        this.setEncoding(encoding);
        this.setIndenting(indenting);
    }
    
    public OutputFormat(final Document doc) {
        this._indent = 0;
        this._encoding = "UTF-8";
        this._encodingInfo = null;
        this._allowJavaNames = false;
        this._omitXmlDeclaration = false;
        this._omitDoctype = false;
        this._omitComments = false;
        this._stripComments = false;
        this._standalone = false;
        this._lineSeparator = "\n";
        this._lineWidth = 72;
        this._preserve = false;
        this._preserveEmptyAttributes = false;
        this.setMethod(whichMethod(doc));
        this.setDoctype(whichDoctypePublic(doc), whichDoctypeSystem(doc));
        this.setMediaType(whichMediaType(this.getMethod()));
    }
    
    public OutputFormat(final Document doc, final String encoding, final boolean indenting) {
        this(doc);
        this.setEncoding(encoding);
        this.setIndenting(indenting);
    }
    
    public String getMethod() {
        return this._method;
    }
    
    public void setMethod(final String method) {
        this._method = method;
    }
    
    public String getVersion() {
        return this._version;
    }
    
    public void setVersion(final String version) {
        this._version = version;
    }
    
    public int getIndent() {
        return this._indent;
    }
    
    public boolean getIndenting() {
        return this._indent > 0;
    }
    
    public void setIndent(final int indent) {
        if (indent < 0) {
            this._indent = 0;
        }
        else {
            this._indent = indent;
        }
    }
    
    public void setIndenting(final boolean on) {
        if (on) {
            this._indent = 4;
            this._lineWidth = 72;
        }
        else {
            this._indent = 0;
            this._lineWidth = 0;
        }
    }
    
    public String getEncoding() {
        return this._encoding;
    }
    
    public void setEncoding(final String encoding) {
        this._encoding = encoding;
        this._encodingInfo = null;
    }
    
    public void setEncoding(final EncodingInfo encInfo) {
        this._encoding = encInfo.getIANAName();
        this._encodingInfo = encInfo;
    }
    
    public EncodingInfo getEncodingInfo() throws UnsupportedEncodingException {
        if (this._encodingInfo == null) {
            this._encodingInfo = Encodings.getEncodingInfo(this._encoding, this._allowJavaNames);
        }
        return this._encodingInfo;
    }
    
    public void setAllowJavaNames(final boolean allow) {
        this._allowJavaNames = allow;
    }
    
    public boolean setAllowJavaNames() {
        return this._allowJavaNames;
    }
    
    public String getMediaType() {
        return this._mediaType;
    }
    
    public void setMediaType(final String mediaType) {
        this._mediaType = mediaType;
    }
    
    public void setDoctype(final String publicId, final String systemId) {
        this._doctypePublic = publicId;
        this._doctypeSystem = systemId;
    }
    
    public String getDoctypePublic() {
        return this._doctypePublic;
    }
    
    public String getDoctypeSystem() {
        return this._doctypeSystem;
    }
    
    public boolean getOmitComments() {
        return this._omitComments;
    }
    
    public void setOmitComments(final boolean omit) {
        this._omitComments = omit;
    }
    
    public boolean getOmitDocumentType() {
        return this._omitDoctype;
    }
    
    public void setOmitDocumentType(final boolean omit) {
        this._omitDoctype = omit;
    }
    
    public boolean getOmitXMLDeclaration() {
        return this._omitXmlDeclaration;
    }
    
    public void setOmitXMLDeclaration(final boolean omit) {
        this._omitXmlDeclaration = omit;
    }
    
    public boolean getStandalone() {
        return this._standalone;
    }
    
    public void setStandalone(final boolean standalone) {
        this._standalone = standalone;
    }
    
    public String[] getCDataElements() {
        return this._cdataElements;
    }
    
    public boolean isCDataElement(final String tagName) {
        if (this._cdataElements == null) {
            return false;
        }
        for (int i = 0; i < this._cdataElements.length; ++i) {
            if (this._cdataElements[i].equals(tagName)) {
                return true;
            }
        }
        return false;
    }
    
    public void setCDataElements(final String[] cdataElements) {
        this._cdataElements = cdataElements;
    }
    
    public String[] getNonEscapingElements() {
        return this._nonEscapingElements;
    }
    
    public boolean isNonEscapingElement(final String tagName) {
        if (this._nonEscapingElements == null) {
            return false;
        }
        for (int i = 0; i < this._nonEscapingElements.length; ++i) {
            if (this._nonEscapingElements[i].equals(tagName)) {
                return true;
            }
        }
        return false;
    }
    
    public void setNonEscapingElements(final String[] nonEscapingElements) {
        this._nonEscapingElements = nonEscapingElements;
    }
    
    public String getLineSeparator() {
        return this._lineSeparator;
    }
    
    public void setLineSeparator(final String lineSeparator) {
        if (lineSeparator == null) {
            this._lineSeparator = "\n";
        }
        else {
            this._lineSeparator = lineSeparator;
        }
    }
    
    public boolean getPreserveSpace() {
        return this._preserve;
    }
    
    public void setPreserveSpace(final boolean preserve) {
        this._preserve = preserve;
    }
    
    public int getLineWidth() {
        return this._lineWidth;
    }
    
    public void setLineWidth(final int lineWidth) {
        if (lineWidth <= 0) {
            this._lineWidth = 0;
        }
        else {
            this._lineWidth = lineWidth;
        }
    }
    
    public boolean getPreserveEmptyAttributes() {
        return this._preserveEmptyAttributes;
    }
    
    public void setPreserveEmptyAttributes(final boolean preserve) {
        this._preserveEmptyAttributes = preserve;
    }
    
    public char getLastPrintable() {
        if (this.getEncoding() != null && this.getEncoding().equalsIgnoreCase("ASCII")) {
            return '\u00ff';
        }
        return '\uffff';
    }
    
    public static String whichMethod(final Document doc) {
        if (doc instanceof HTMLDocument) {
            return "html";
        }
        Node node = doc.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == 1) {
                if (node.getNodeName().equalsIgnoreCase("html")) {
                    return "html";
                }
                if (node.getNodeName().equalsIgnoreCase("root")) {
                    return "fop";
                }
                return "xml";
            }
            else {
                if (node.getNodeType() == 3) {
                    final String value = node.getNodeValue();
                    for (int i = 0; i < value.length(); ++i) {
                        if (value.charAt(i) != ' ' && value.charAt(i) != '\n' && value.charAt(i) != '\t' && value.charAt(i) != '\r') {
                            return "xml";
                        }
                    }
                }
                node = node.getNextSibling();
            }
        }
        return "xml";
    }
    
    public static String whichDoctypePublic(final Document doc) {
        final DocumentType doctype = doc.getDoctype();
        if (doctype != null) {
            try {
                return doctype.getPublicId();
            }
            catch (final Error error) {}
        }
        if (doc instanceof HTMLDocument) {
            return "-//W3C//DTD XHTML 1.0 Strict//EN";
        }
        return null;
    }
    
    public static String whichDoctypeSystem(final Document doc) {
        final DocumentType doctype = doc.getDoctype();
        if (doctype != null) {
            try {
                return doctype.getSystemId();
            }
            catch (final Error error) {}
        }
        if (doc instanceof HTMLDocument) {
            return "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
        }
        return null;
    }
    
    public static String whichMediaType(final String method) {
        if (method.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (method.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (method.equalsIgnoreCase("xhtml")) {
            return "text/html";
        }
        if (method.equalsIgnoreCase("text")) {
            return "text/plain";
        }
        if (method.equalsIgnoreCase("fop")) {
            return "application/pdf";
        }
        return null;
    }
    
    public static class DTD
    {
        public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
        public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
        public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
        public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    }
    
    public static class Defaults
    {
        public static final int Indent = 4;
        public static final String Encoding = "UTF-8";
        public static final int LineWidth = 72;
    }
}
