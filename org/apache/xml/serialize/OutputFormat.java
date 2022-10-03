package org.apache.xml.serialize;

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
        this._standalone = false;
        this._lineSeparator = "\n";
        this._lineWidth = 72;
        this._preserve = false;
        this._preserveEmptyAttributes = false;
        this.setMethod(method);
        this.setEncoding(encoding);
        this.setIndenting(indenting);
    }
    
    public OutputFormat(final Document document) {
        this._indent = 0;
        this._encoding = "UTF-8";
        this._encodingInfo = null;
        this._allowJavaNames = false;
        this._omitXmlDeclaration = false;
        this._omitDoctype = false;
        this._omitComments = false;
        this._standalone = false;
        this._lineSeparator = "\n";
        this._lineWidth = 72;
        this._preserve = false;
        this._preserveEmptyAttributes = false;
        this.setMethod(whichMethod(document));
        this.setDoctype(whichDoctypePublic(document), whichDoctypeSystem(document));
        this.setMediaType(whichMediaType(this.getMethod()));
    }
    
    public OutputFormat(final Document document, final String encoding, final boolean indenting) {
        this(document);
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
    
    public void setIndenting(final boolean b) {
        if (b) {
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
    
    public void setEncoding(final EncodingInfo encodingInfo) {
        this._encoding = encodingInfo.getIANAName();
        this._encodingInfo = encodingInfo;
    }
    
    public EncodingInfo getEncodingInfo() throws UnsupportedEncodingException {
        if (this._encodingInfo == null) {
            this._encodingInfo = Encodings.getEncodingInfo(this._encoding, this._allowJavaNames);
        }
        return this._encodingInfo;
    }
    
    public void setAllowJavaNames(final boolean allowJavaNames) {
        this._allowJavaNames = allowJavaNames;
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
    
    public void setDoctype(final String doctypePublic, final String doctypeSystem) {
        this._doctypePublic = doctypePublic;
        this._doctypeSystem = doctypeSystem;
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
    
    public void setOmitComments(final boolean omitComments) {
        this._omitComments = omitComments;
    }
    
    public boolean getOmitDocumentType() {
        return this._omitDoctype;
    }
    
    public void setOmitDocumentType(final boolean omitDoctype) {
        this._omitDoctype = omitDoctype;
    }
    
    public boolean getOmitXMLDeclaration() {
        return this._omitXmlDeclaration;
    }
    
    public void setOmitXMLDeclaration(final boolean omitXmlDeclaration) {
        this._omitXmlDeclaration = omitXmlDeclaration;
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
    
    public boolean isCDataElement(final String s) {
        if (this._cdataElements == null) {
            return false;
        }
        for (int i = 0; i < this._cdataElements.length; ++i) {
            if (this._cdataElements[i].equals(s)) {
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
    
    public boolean isNonEscapingElement(final String s) {
        if (this._nonEscapingElements == null) {
            return false;
        }
        for (int i = 0; i < this._nonEscapingElements.length; ++i) {
            if (this._nonEscapingElements[i].equals(s)) {
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
    
    public void setPreserveEmptyAttributes(final boolean preserveEmptyAttributes) {
        this._preserveEmptyAttributes = preserveEmptyAttributes;
    }
    
    public char getLastPrintable() {
        if (this.getEncoding() != null && this.getEncoding().equalsIgnoreCase("ASCII")) {
            return '\u00ff';
        }
        return '\uffff';
    }
    
    public static String whichMethod(final Document document) {
        if (document instanceof HTMLDocument) {
            return "html";
        }
        Node node = document.getFirstChild();
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
                    final String nodeValue = node.getNodeValue();
                    for (int i = 0; i < nodeValue.length(); ++i) {
                        if (nodeValue.charAt(i) != ' ' && nodeValue.charAt(i) != '\n' && nodeValue.charAt(i) != '\t' && nodeValue.charAt(i) != '\r') {
                            return "xml";
                        }
                    }
                }
                node = node.getNextSibling();
            }
        }
        return "xml";
    }
    
    public static String whichDoctypePublic(final Document document) {
        final DocumentType doctype = document.getDoctype();
        if (doctype != null) {
            try {
                return doctype.getPublicId();
            }
            catch (final Error error) {}
        }
        if (document instanceof HTMLDocument) {
            return "-//W3C//DTD XHTML 1.0 Strict//EN";
        }
        return null;
    }
    
    public static String whichDoctypeSystem(final Document document) {
        final DocumentType doctype = document.getDoctype();
        if (doctype != null) {
            try {
                return doctype.getSystemId();
            }
            catch (final Error error) {}
        }
        if (document instanceof HTMLDocument) {
            return "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
        }
        return null;
    }
    
    public static String whichMediaType(final String s) {
        if (s.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (s.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (s.equalsIgnoreCase("xhtml")) {
            return "text/html";
        }
        if (s.equalsIgnoreCase("text")) {
            return "text/plain";
        }
        if (s.equalsIgnoreCase("fop")) {
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
