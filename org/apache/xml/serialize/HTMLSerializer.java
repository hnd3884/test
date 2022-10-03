package org.apache.xml.serialize;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.AttributeList;
import java.util.Iterator;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.Map;
import java.util.Locale;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.xml.sax.Attributes;
import java.io.OutputStream;
import java.io.Writer;

public class HTMLSerializer extends BaseMarkupSerializer
{
    private boolean _xhtml;
    public static final String XHTMLNamespace = "http://www.w3.org/1999/xhtml";
    private String fUserXHTMLNamespace;
    
    protected HTMLSerializer(final boolean xhtml, final OutputFormat format) {
        super(format);
        this.fUserXHTMLNamespace = null;
        this._xhtml = xhtml;
    }
    
    public HTMLSerializer() {
        this(false, new OutputFormat("html", "ISO-8859-1", false));
    }
    
    public HTMLSerializer(final OutputFormat format) {
        this(false, (format != null) ? format : new OutputFormat("html", "ISO-8859-1", false));
    }
    
    public HTMLSerializer(final Writer writer, final OutputFormat format) {
        this(false, (format != null) ? format : new OutputFormat("html", "ISO-8859-1", false));
        this.setOutputCharStream(writer);
    }
    
    public HTMLSerializer(final OutputStream output, final OutputFormat format) {
        this(false, (format != null) ? format : new OutputFormat("html", "ISO-8859-1", false));
        this.setOutputByteStream(output);
    }
    
    @Override
    public void setOutputFormat(final OutputFormat format) {
        super.setOutputFormat((format != null) ? format : new OutputFormat("html", "ISO-8859-1", false));
    }
    
    public void setXHTMLNamespace(final String newNamespace) {
        this.fUserXHTMLNamespace = newNamespace;
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, String rawName, final Attributes attrs) throws SAXException {
        boolean addNSAttr = false;
        try {
            if (this._printer == null) {
                throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null));
            }
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
                if (!this._started) {
                    this.startDocument((localName == null || localName.length() == 0) ? rawName : localName);
                }
            }
            else {
                if (state.empty) {
                    this._printer.printText('>');
                }
                if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement)) {
                    this._printer.breakLine();
                }
            }
            boolean preserveSpace = state.preserveSpace;
            final boolean hasNamespaceURI = namespaceURI != null && namespaceURI.length() != 0;
            if (rawName == null || rawName.length() == 0) {
                rawName = localName;
                if (hasNamespaceURI) {
                    final String prefix = this.getPrefix(namespaceURI);
                    if (prefix != null && prefix.length() != 0) {
                        rawName = prefix + ":" + localName;
                    }
                }
                addNSAttr = true;
            }
            String htmlName;
            if (!hasNamespaceURI) {
                htmlName = rawName;
            }
            else if (namespaceURI.equals("http://www.w3.org/1999/xhtml") || (this.fUserXHTMLNamespace != null && this.fUserXHTMLNamespace.equals(namespaceURI))) {
                htmlName = localName;
            }
            else {
                htmlName = null;
            }
            this._printer.printText('<');
            if (this._xhtml) {
                this._printer.printText(rawName.toLowerCase(Locale.ENGLISH));
            }
            else {
                this._printer.printText(rawName);
            }
            this._printer.indent();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    this._printer.printSpace();
                    final String name = attrs.getQName(i).toLowerCase(Locale.ENGLISH);
                    String value = attrs.getValue(i);
                    if (this._xhtml || hasNamespaceURI) {
                        if (value == null) {
                            this._printer.printText(name);
                            this._printer.printText("=\"\"");
                        }
                        else {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this.printEscaped(value);
                            this._printer.printText('\"');
                        }
                    }
                    else {
                        if (value == null) {
                            value = "";
                        }
                        if (!this._format.getPreserveEmptyAttributes() && value.length() == 0) {
                            this._printer.printText(name);
                        }
                        else if (HTMLdtd.isURI(rawName, name)) {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this._printer.printText(this.escapeURI(value));
                            this._printer.printText('\"');
                        }
                        else if (HTMLdtd.isBoolean(rawName, name)) {
                            this._printer.printText(name);
                        }
                        else {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this.printEscaped(value);
                            this._printer.printText('\"');
                        }
                    }
                }
            }
            if (htmlName != null && HTMLdtd.isPreserveSpace(htmlName)) {
                preserveSpace = true;
            }
            if (addNSAttr) {
                final Iterator entries = this._prefixes.entrySet().iterator();
                while (entries.hasNext()) {
                    this._printer.printSpace();
                    final Map.Entry entry = entries.next();
                    final String value = entry.getKey();
                    final String name = entry.getValue();
                    if (name.length() == 0) {
                        this._printer.printText("xmlns=\"");
                        this.printEscaped(value);
                        this._printer.printText('\"');
                    }
                    else {
                        this._printer.printText("xmlns:");
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this.printEscaped(value);
                        this._printer.printText('\"');
                    }
                }
            }
            state = this.enterElementState(namespaceURI, localName, rawName, preserveSpace);
            if (htmlName != null && (htmlName.equalsIgnoreCase("A") || htmlName.equalsIgnoreCase("TD"))) {
                state.empty = false;
                this._printer.printText('>');
            }
            if (htmlName != null && (rawName.equalsIgnoreCase("SCRIPT") || rawName.equalsIgnoreCase("STYLE"))) {
                if (this._xhtml) {
                    state.doCData = true;
                    if (rawName.equalsIgnoreCase("STYLE")) {
                        state.unescaped = true;
                    }
                }
                else {
                    state.unescaped = true;
                }
            }
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String rawName) throws SAXException {
        try {
            this.endElementIO(namespaceURI, localName, rawName);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    public void endElementIO(final String namespaceURI, final String localName, final String rawName) throws IOException {
        this._printer.unindent();
        ElementState state = this.getElementState();
        String htmlName;
        if (state.namespaceURI == null || state.namespaceURI.length() == 0) {
            htmlName = state.rawName;
        }
        else if (state.namespaceURI.equals("http://www.w3.org/1999/xhtml") || (this.fUserXHTMLNamespace != null && this.fUserXHTMLNamespace.equals(state.namespaceURI))) {
            htmlName = state.localName;
        }
        else {
            htmlName = null;
        }
        if (this._xhtml) {
            if (state.empty) {
                this._printer.printText(" />");
            }
            else {
                if (state.inCData) {
                    this._printer.printText("]]>");
                }
                this._printer.printText("</");
                this._printer.printText(state.rawName.toLowerCase(Locale.ENGLISH));
                this._printer.printText('>');
            }
        }
        else {
            if (state.empty) {
                this._printer.printText('>');
            }
            if (htmlName == null || !HTMLdtd.isOnlyOpening(htmlName)) {
                if (this._indenting && !state.preserveSpace && state.afterElement) {
                    this._printer.breakLine();
                }
                if (state.inCData) {
                    this._printer.printText("]]>");
                }
                this._printer.printText("</");
                this._printer.printText(state.rawName);
                this._printer.printText('>');
            }
        }
        state = this.leaveElementState();
        if (htmlName == null || (!htmlName.equalsIgnoreCase("A") && !htmlName.equalsIgnoreCase("TD"))) {
            state.afterElement = true;
        }
        state.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }
    
    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        try {
            final ElementState state = this.content();
            state.doCData = false;
            super.characters(chars, start, length);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    public void startElement(final String tagName, final AttributeList attrs) throws SAXException {
        try {
            if (this._printer == null) {
                throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null));
            }
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
                if (!this._started) {
                    this.startDocument(tagName);
                }
            }
            else {
                if (state.empty) {
                    this._printer.printText('>');
                }
                if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement)) {
                    this._printer.breakLine();
                }
            }
            boolean preserveSpace = state.preserveSpace;
            this._printer.printText('<');
            if (this._xhtml) {
                this._printer.printText(tagName.toLowerCase(Locale.ENGLISH));
            }
            else {
                this._printer.printText(tagName);
            }
            this._printer.indent();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    this._printer.printSpace();
                    final String name = attrs.getName(i).toLowerCase(Locale.ENGLISH);
                    String value = attrs.getValue(i);
                    if (this._xhtml) {
                        if (value == null) {
                            this._printer.printText(name);
                            this._printer.printText("=\"\"");
                        }
                        else {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this.printEscaped(value);
                            this._printer.printText('\"');
                        }
                    }
                    else {
                        if (value == null) {
                            value = "";
                        }
                        if (!this._format.getPreserveEmptyAttributes() && value.length() == 0) {
                            this._printer.printText(name);
                        }
                        else if (HTMLdtd.isURI(tagName, name)) {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this._printer.printText(this.escapeURI(value));
                            this._printer.printText('\"');
                        }
                        else if (HTMLdtd.isBoolean(tagName, name)) {
                            this._printer.printText(name);
                        }
                        else {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this.printEscaped(value);
                            this._printer.printText('\"');
                        }
                    }
                }
            }
            if (HTMLdtd.isPreserveSpace(tagName)) {
                preserveSpace = true;
            }
            state = this.enterElementState(null, null, tagName, preserveSpace);
            if (tagName.equalsIgnoreCase("A") || tagName.equalsIgnoreCase("TD")) {
                state.empty = false;
                this._printer.printText('>');
            }
            if (tagName.equalsIgnoreCase("SCRIPT") || tagName.equalsIgnoreCase("STYLE")) {
                if (this._xhtml) {
                    state.doCData = true;
                    if (tagName.equalsIgnoreCase("STYLE")) {
                        state.unescaped = true;
                    }
                }
                else {
                    state.unescaped = true;
                }
            }
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    public void endElement(final String tagName) throws SAXException {
        this.endElement(null, null, tagName);
    }
    
    protected void startDocument(final String rootTagName) throws IOException {
        this._printer.leaveDTD();
        if (!this._started) {
            if (this._docTypePublicId == null && this._docTypeSystemId == null) {
                if (this._xhtml) {
                    this._docTypePublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
                    this._docTypeSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
                }
                else {
                    this._docTypePublicId = "-//W3C//DTD HTML 4.01//EN";
                    this._docTypeSystemId = "http://www.w3.org/TR/html4/strict.dtd";
                }
            }
            if (!this._format.getOmitDocumentType()) {
                if (this._docTypePublicId != null && (!this._xhtml || this._docTypeSystemId != null)) {
                    if (this._xhtml) {
                        this._printer.printText("<!DOCTYPE html PUBLIC ");
                    }
                    else {
                        this._printer.printText("<!DOCTYPE HTML PUBLIC ");
                    }
                    this.printDoctypeURL(this._docTypePublicId);
                    if (this._docTypeSystemId != null) {
                        if (this._indenting) {
                            this._printer.breakLine();
                            this._printer.printText("                      ");
                        }
                        else {
                            this._printer.printText(' ');
                        }
                        this.printDoctypeURL(this._docTypeSystemId);
                    }
                    this._printer.printText('>');
                    this._printer.breakLine();
                }
                else if (this._docTypeSystemId != null) {
                    if (this._xhtml) {
                        this._printer.printText("<!DOCTYPE html SYSTEM ");
                    }
                    else {
                        this._printer.printText("<!DOCTYPE HTML SYSTEM ");
                    }
                    this.printDoctypeURL(this._docTypeSystemId);
                    this._printer.printText('>');
                    this._printer.breakLine();
                }
            }
        }
        this._started = true;
        this.serializePreRoot();
    }
    
    @Override
    protected void serializeElement(final Element elem) throws IOException {
        final String tagName = elem.getTagName();
        ElementState state = this.getElementState();
        if (this.isDocumentState()) {
            if (!this._started) {
                this.startDocument(tagName);
            }
        }
        else {
            if (state.empty) {
                this._printer.printText('>');
            }
            if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement)) {
                this._printer.breakLine();
            }
        }
        boolean preserveSpace = state.preserveSpace;
        this._printer.printText('<');
        if (this._xhtml) {
            this._printer.printText(tagName.toLowerCase(Locale.ENGLISH));
        }
        else {
            this._printer.printText(tagName);
        }
        this._printer.indent();
        final NamedNodeMap attrMap = elem.getAttributes();
        if (attrMap != null) {
            for (int i = 0; i < attrMap.getLength(); ++i) {
                final Attr attr = (Attr)attrMap.item(i);
                final String name = attr.getName().toLowerCase(Locale.ENGLISH);
                String value = attr.getValue();
                if (attr.getSpecified()) {
                    this._printer.printSpace();
                    if (this._xhtml) {
                        if (value == null) {
                            this._printer.printText(name);
                            this._printer.printText("=\"\"");
                        }
                        else {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this.printEscaped(value);
                            this._printer.printText('\"');
                        }
                    }
                    else {
                        if (value == null) {
                            value = "";
                        }
                        if (!this._format.getPreserveEmptyAttributes() && value.length() == 0) {
                            this._printer.printText(name);
                        }
                        else if (HTMLdtd.isURI(tagName, name)) {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this._printer.printText(this.escapeURI(value));
                            this._printer.printText('\"');
                        }
                        else if (HTMLdtd.isBoolean(tagName, name)) {
                            this._printer.printText(name);
                        }
                        else {
                            this._printer.printText(name);
                            this._printer.printText("=\"");
                            this.printEscaped(value);
                            this._printer.printText('\"');
                        }
                    }
                }
            }
        }
        if (HTMLdtd.isPreserveSpace(tagName)) {
            preserveSpace = true;
        }
        if (elem.hasChildNodes() || !HTMLdtd.isEmptyTag(tagName)) {
            state = this.enterElementState(null, null, tagName, preserveSpace);
            if (tagName.equalsIgnoreCase("A") || tagName.equalsIgnoreCase("TD")) {
                state.empty = false;
                this._printer.printText('>');
            }
            if (tagName.equalsIgnoreCase("SCRIPT") || tagName.equalsIgnoreCase("STYLE")) {
                if (this._xhtml) {
                    state.doCData = true;
                    if (tagName.equalsIgnoreCase("STYLE")) {
                        state.unescaped = true;
                    }
                }
                else {
                    state.unescaped = true;
                }
            }
            for (Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                this.serializeNode(child);
            }
            this.endElementIO(null, null, tagName);
        }
        else {
            this._printer.unindent();
            if (this._xhtml) {
                this._printer.printText(" />");
            }
            else {
                this._printer.printText('>');
            }
            state.afterElement = true;
            state.empty = false;
            if (this.isDocumentState()) {
                this._printer.flush();
            }
        }
    }
    
    @Override
    protected void characters(final String text) throws IOException {
        this.content();
        super.characters(text);
    }
    
    @Override
    protected String getEntityRef(final int ch) {
        return HTMLdtd.fromChar(ch);
    }
    
    protected String escapeURI(final String uri) {
        final int index = uri.indexOf("\"");
        if (index >= 0) {
            return uri.substring(0, index);
        }
        return uri;
    }
}
