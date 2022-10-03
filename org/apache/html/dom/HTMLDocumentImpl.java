package org.apache.html.dom;

import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import java.util.Locale;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.html.HTMLFrameSetElement;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLTitleElement;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLHtmlElement;
import org.w3c.dom.Element;
import java.util.Hashtable;
import java.io.StringWriter;
import org.w3c.dom.html.HTMLDocument;
import org.apache.xerces.dom.DocumentImpl;

public class HTMLDocumentImpl extends DocumentImpl implements HTMLDocument
{
    private static final long serialVersionUID = 4285791750126227180L;
    private HTMLCollectionImpl _anchors;
    private HTMLCollectionImpl _forms;
    private HTMLCollectionImpl _images;
    private HTMLCollectionImpl _links;
    private HTMLCollectionImpl _applets;
    private StringWriter _writer;
    private static Hashtable _elementTypesHTML;
    private static final Class[] _elemClassSigHTML;
    
    public HTMLDocumentImpl() {
        populateElementTypes();
    }
    
    public synchronized Element getDocumentElement() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLHtmlElement) {
                return (HTMLElement)node;
            }
        }
        final HTMLHtmlElementImpl htmlHtmlElementImpl = new HTMLHtmlElementImpl(this, "HTML");
        Node nextSibling;
        for (Node firstChild = this.getFirstChild(); firstChild != null; firstChild = nextSibling) {
            nextSibling = firstChild.getNextSibling();
            htmlHtmlElementImpl.appendChild(firstChild);
        }
        this.appendChild(htmlHtmlElementImpl);
        return htmlHtmlElementImpl;
    }
    
    public synchronized HTMLElement getHead() {
        final Element documentElement = this.getDocumentElement();
        Node node;
        synchronized (documentElement) {
            for (node = documentElement.getFirstChild(); node != null && !(node instanceof HTMLHeadElement); node = node.getNextSibling()) {}
            if (node != null) {
                synchronized (node) {
                    Node nextSibling;
                    for (Node firstChild = documentElement.getFirstChild(); firstChild != null && firstChild != node; firstChild = nextSibling) {
                        nextSibling = firstChild.getNextSibling();
                        node.insertBefore(firstChild, node.getFirstChild());
                    }
                }
                return (HTMLElement)node;
            }
            node = new HTMLHeadElementImpl(this, "HEAD");
            documentElement.insertBefore(node, documentElement.getFirstChild());
        }
        return (HTMLElement)node;
    }
    
    public synchronized String getTitle() {
        final NodeList elementsByTagName = this.getHead().getElementsByTagName("TITLE");
        if (elementsByTagName.getLength() > 0) {
            return ((HTMLTitleElement)elementsByTagName.item(0)).getText();
        }
        return "";
    }
    
    public synchronized void setTitle(final String s) {
        final HTMLElement head = this.getHead();
        final NodeList elementsByTagName = head.getElementsByTagName("TITLE");
        if (elementsByTagName.getLength() > 0) {
            final Node item = elementsByTagName.item(0);
            if (item.getParentNode() != head) {
                head.appendChild(item);
            }
            ((HTMLTitleElement)item).setText(s);
        }
        else {
            final HTMLTitleElementImpl htmlTitleElementImpl = new HTMLTitleElementImpl(this, "TITLE");
            htmlTitleElementImpl.setText(s);
            head.appendChild(htmlTitleElementImpl);
        }
    }
    
    public synchronized HTMLElement getBody() {
        final Element documentElement = this.getDocumentElement();
        final HTMLElement head = this.getHead();
        Node node;
        synchronized (documentElement) {
            for (node = head.getNextSibling(); node != null && !(node instanceof HTMLBodyElement) && !(node instanceof HTMLFrameSetElement); node = node.getNextSibling()) {}
            if (node != null) {
                synchronized (node) {
                    Node nextSibling2;
                    for (Node nextSibling = head.getNextSibling(); nextSibling != null && nextSibling != node; nextSibling = nextSibling2) {
                        nextSibling2 = nextSibling.getNextSibling();
                        node.insertBefore(nextSibling, node.getFirstChild());
                    }
                }
                return (HTMLElement)node;
            }
            node = new HTMLBodyElementImpl(this, "BODY");
            documentElement.appendChild(node);
        }
        return (HTMLElement)node;
    }
    
    public synchronized void setBody(final HTMLElement htmlElement) {
        synchronized (htmlElement) {
            final Element documentElement = this.getDocumentElement();
            final HTMLElement head = this.getHead();
            synchronized (documentElement) {
                final NodeList elementsByTagName = this.getElementsByTagName("BODY");
                if (elementsByTagName.getLength() > 0) {
                    final Node item = elementsByTagName.item(0);
                    synchronized (item) {
                        for (Node nextSibling = head; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
                            if (nextSibling instanceof Element) {
                                if (nextSibling != item) {
                                    documentElement.insertBefore(htmlElement, nextSibling);
                                }
                                else {
                                    documentElement.replaceChild(htmlElement, item);
                                }
                                return;
                            }
                        }
                        documentElement.appendChild(htmlElement);
                    }
                    return;
                }
                documentElement.appendChild(htmlElement);
            }
        }
    }
    
    public synchronized Element getElementById(final String s) {
        final Element elementById = super.getElementById(s);
        if (elementById != null) {
            return elementById;
        }
        return this.getElementById(s, this);
    }
    
    public NodeList getElementsByName(final String s) {
        return new NameNodeListImpl(this, s);
    }
    
    public final NodeList getElementsByTagName(final String s) {
        return super.getElementsByTagName(s.toUpperCase(Locale.ENGLISH));
    }
    
    public final NodeList getElementsByTagNameNS(final String s, final String s2) {
        if (s != null && s.length() > 0) {
            return super.getElementsByTagNameNS(s, s2.toUpperCase(Locale.ENGLISH));
        }
        return super.getElementsByTagName(s2.toUpperCase(Locale.ENGLISH));
    }
    
    public Element createElementNS(final String s, final String s2, final String s3) throws DOMException {
        return this.createElementNS(s, s2);
    }
    
    public Element createElementNS(final String s, final String s2) {
        if (s == null || s.length() == 0) {
            return this.createElement(s2);
        }
        return super.createElementNS(s, s2);
    }
    
    public Element createElement(String upperCase) throws DOMException {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        final Class clazz = HTMLDocumentImpl._elementTypesHTML.get(upperCase);
        if (clazz != null) {
            try {
                return (Element)clazz.getConstructor((Class[])HTMLDocumentImpl._elemClassSigHTML).newInstance(this, upperCase);
            }
            catch (final Exception ex) {
                throw new IllegalStateException("HTM15 Tag '" + upperCase + "' associated with an Element class that failed to construct.\n" + upperCase);
            }
        }
        return new HTMLElementImpl(this, upperCase);
    }
    
    public Attr createAttribute(final String s) throws DOMException {
        return super.createAttribute(s.toLowerCase(Locale.ENGLISH));
    }
    
    public String getReferrer() {
        return null;
    }
    
    public String getDomain() {
        return null;
    }
    
    public String getURL() {
        return null;
    }
    
    public String getCookie() {
        return null;
    }
    
    public void setCookie(final String s) {
    }
    
    public HTMLCollection getImages() {
        if (this._images == null) {
            this._images = new HTMLCollectionImpl(this.getBody(), (short)3);
        }
        return this._images;
    }
    
    public HTMLCollection getApplets() {
        if (this._applets == null) {
            this._applets = new HTMLCollectionImpl(this.getBody(), (short)4);
        }
        return this._applets;
    }
    
    public HTMLCollection getLinks() {
        if (this._links == null) {
            this._links = new HTMLCollectionImpl(this.getBody(), (short)5);
        }
        return this._links;
    }
    
    public HTMLCollection getForms() {
        if (this._forms == null) {
            this._forms = new HTMLCollectionImpl(this.getBody(), (short)2);
        }
        return this._forms;
    }
    
    public HTMLCollection getAnchors() {
        if (this._anchors == null) {
            this._anchors = new HTMLCollectionImpl(this.getBody(), (short)1);
        }
        return this._anchors;
    }
    
    public void open() {
        if (this._writer == null) {
            this._writer = new StringWriter();
        }
    }
    
    public void close() {
        if (this._writer != null) {
            this._writer = null;
        }
    }
    
    public void write(final String s) {
        if (this._writer != null) {
            this._writer.write(s);
        }
    }
    
    public void writeln(final String s) {
        if (this._writer != null) {
            this._writer.write(s + "\n");
        }
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLDocumentImpl htmlDocumentImpl = new HTMLDocumentImpl();
        this.callUserDataHandlers(this, htmlDocumentImpl, (short)1);
        this.cloneNode(htmlDocumentImpl, b);
        return htmlDocumentImpl;
    }
    
    protected boolean canRenameElements(final String s, final String s2, final ElementImpl elementImpl) {
        if (elementImpl.getNamespaceURI() != null) {
            return s != null;
        }
        return HTMLDocumentImpl._elementTypesHTML.get(s2.toUpperCase(Locale.ENGLISH)) == HTMLDocumentImpl._elementTypesHTML.get(elementImpl.getTagName());
    }
    
    private Element getElementById(final String s, final Node node) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2 instanceof Element) {
                if (s.equals(((Element)node2).getAttribute("id"))) {
                    return (Element)node2;
                }
                final Element elementById = this.getElementById(s, node2);
                if (elementById != null) {
                    return elementById;
                }
            }
        }
        return null;
    }
    
    private static synchronized void populateElementTypes() {
        if (HTMLDocumentImpl._elementTypesHTML != null) {
            return;
        }
        HTMLDocumentImpl._elementTypesHTML = new Hashtable(63);
        populateElementType("A", "HTMLAnchorElementImpl");
        populateElementType("APPLET", "HTMLAppletElementImpl");
        populateElementType("AREA", "HTMLAreaElementImpl");
        populateElementType("BASE", "HTMLBaseElementImpl");
        populateElementType("BASEFONT", "HTMLBaseFontElementImpl");
        populateElementType("BLOCKQUOTE", "HTMLQuoteElementImpl");
        populateElementType("BODY", "HTMLBodyElementImpl");
        populateElementType("BR", "HTMLBRElementImpl");
        populateElementType("BUTTON", "HTMLButtonElementImpl");
        populateElementType("DEL", "HTMLModElementImpl");
        populateElementType("DIR", "HTMLDirectoryElementImpl");
        populateElementType("DIV", "HTMLDivElementImpl");
        populateElementType("DL", "HTMLDListElementImpl");
        populateElementType("FIELDSET", "HTMLFieldSetElementImpl");
        populateElementType("FONT", "HTMLFontElementImpl");
        populateElementType("FORM", "HTMLFormElementImpl");
        populateElementType("FRAME", "HTMLFrameElementImpl");
        populateElementType("FRAMESET", "HTMLFrameSetElementImpl");
        populateElementType("HEAD", "HTMLHeadElementImpl");
        populateElementType("H1", "HTMLHeadingElementImpl");
        populateElementType("H2", "HTMLHeadingElementImpl");
        populateElementType("H3", "HTMLHeadingElementImpl");
        populateElementType("H4", "HTMLHeadingElementImpl");
        populateElementType("H5", "HTMLHeadingElementImpl");
        populateElementType("H6", "HTMLHeadingElementImpl");
        populateElementType("HR", "HTMLHRElementImpl");
        populateElementType("HTML", "HTMLHtmlElementImpl");
        populateElementType("IFRAME", "HTMLIFrameElementImpl");
        populateElementType("IMG", "HTMLImageElementImpl");
        populateElementType("INPUT", "HTMLInputElementImpl");
        populateElementType("INS", "HTMLModElementImpl");
        populateElementType("ISINDEX", "HTMLIsIndexElementImpl");
        populateElementType("LABEL", "HTMLLabelElementImpl");
        populateElementType("LEGEND", "HTMLLegendElementImpl");
        populateElementType("LI", "HTMLLIElementImpl");
        populateElementType("LINK", "HTMLLinkElementImpl");
        populateElementType("MAP", "HTMLMapElementImpl");
        populateElementType("MENU", "HTMLMenuElementImpl");
        populateElementType("META", "HTMLMetaElementImpl");
        populateElementType("OBJECT", "HTMLObjectElementImpl");
        populateElementType("OL", "HTMLOListElementImpl");
        populateElementType("OPTGROUP", "HTMLOptGroupElementImpl");
        populateElementType("OPTION", "HTMLOptionElementImpl");
        populateElementType("P", "HTMLParagraphElementImpl");
        populateElementType("PARAM", "HTMLParamElementImpl");
        populateElementType("PRE", "HTMLPreElementImpl");
        populateElementType("Q", "HTMLQuoteElementImpl");
        populateElementType("SCRIPT", "HTMLScriptElementImpl");
        populateElementType("SELECT", "HTMLSelectElementImpl");
        populateElementType("STYLE", "HTMLStyleElementImpl");
        populateElementType("TABLE", "HTMLTableElementImpl");
        populateElementType("CAPTION", "HTMLTableCaptionElementImpl");
        populateElementType("TD", "HTMLTableCellElementImpl");
        populateElementType("TH", "HTMLTableCellElementImpl");
        populateElementType("COL", "HTMLTableColElementImpl");
        populateElementType("COLGROUP", "HTMLTableColElementImpl");
        populateElementType("TR", "HTMLTableRowElementImpl");
        populateElementType("TBODY", "HTMLTableSectionElementImpl");
        populateElementType("THEAD", "HTMLTableSectionElementImpl");
        populateElementType("TFOOT", "HTMLTableSectionElementImpl");
        populateElementType("TEXTAREA", "HTMLTextAreaElementImpl");
        populateElementType("TITLE", "HTMLTitleElementImpl");
        populateElementType("UL", "HTMLUListElementImpl");
    }
    
    private static void populateElementType(final String s, final String s2) {
        try {
            HTMLDocumentImpl._elementTypesHTML.put(s, ObjectFactory.findProviderClass("org.apache.html.dom." + s2, HTMLDocumentImpl.class.getClassLoader(), true));
        }
        catch (final Exception ex) {
            throw new RuntimeException("HTM019 OpenXML Error: Could not find or execute class " + s2 + " implementing HTML element " + s + "\n" + s2 + "\t" + s);
        }
    }
    
    static {
        _elemClassSigHTML = new Class[] { HTMLDocumentImpl.class, String.class };
    }
}
