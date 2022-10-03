package org.cyberneko.html.filters;

import java.util.Locale;
import org.apache.xerces.util.XMLChar;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.XMLStringBuffer;
import org.cyberneko.html.HTMLAugmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.NamespaceContext;
import org.cyberneko.html.HTMLEventInfo;

public class Purifier extends DefaultFilter
{
    public static final String SYNTHESIZED_NAMESPACE_PREFX = "http://cyberneko.org/html/ns/synthesized/";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    private static final String[] RECOGNIZED_FEATURES;
    protected static final HTMLEventInfo SYNTHESIZED_ITEM;
    protected boolean fNamespaces;
    protected boolean fAugmentations;
    protected boolean fSeenDoctype;
    protected boolean fSeenRootElement;
    protected boolean fInCDATASection;
    protected String fPublicId;
    protected String fSystemId;
    protected NamespaceContext fNamespaceContext;
    protected int fSynthesizedNamespaceCount;
    private QName fQName;
    private final HTMLAugmentations fInfosetAugs;
    private final XMLStringBuffer fStringBuffer;
    
    public Purifier() {
        this.fQName = new QName();
        this.fInfosetAugs = new HTMLAugmentations();
        this.fStringBuffer = new XMLStringBuffer();
    }
    
    public void reset(final XMLComponentManager manager) throws XMLConfigurationException {
        this.fInCDATASection = false;
        this.fNamespaces = manager.getFeature("http://xml.org/sax/features/namespaces");
        this.fAugmentations = manager.getFeature("http://cyberneko.org/html/features/augmentations");
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final Augmentations augs) throws XNIException {
        this.fNamespaceContext = (NamespaceContext)(this.fNamespaces ? new NamespaceBinder.NamespaceSupport() : null);
        this.fSynthesizedNamespaceCount = 0;
        this.handleStartDocument();
        super.startDocument(locator, encoding, augs);
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        this.fNamespaceContext = nscontext;
        this.fSynthesizedNamespaceCount = 0;
        this.handleStartDocument();
        super.startDocument(locator, encoding, nscontext, augs);
    }
    
    public void xmlDecl(String version, String encoding, String standalone, final Augmentations augs) throws XNIException {
        if (version == null || !version.equals("1.0")) {
            version = "1.0";
        }
        if (encoding != null && encoding.length() == 0) {
            encoding = null;
        }
        if (standalone != null) {
            if (!standalone.equalsIgnoreCase("true") && !standalone.equalsIgnoreCase("false")) {
                standalone = null;
            }
            else {
                standalone = standalone.toLowerCase();
            }
        }
        super.xmlDecl(version, encoding, standalone, augs);
    }
    
    public void comment(XMLString text, final Augmentations augs) throws XNIException {
        final StringBuffer str = new StringBuffer(this.purifyText(text).toString());
        final int length = str.length();
        for (int i = length - 1; i >= 0; --i) {
            final char c = str.charAt(i);
            if (c == '-') {
                str.insert(i + 1, ' ');
            }
        }
        this.fStringBuffer.length = 0;
        this.fStringBuffer.append(str.toString());
        text = (XMLString)this.fStringBuffer;
        super.comment(text, augs);
    }
    
    public void processingInstruction(String target, XMLString data, final Augmentations augs) throws XNIException {
        target = this.purifyName(target, true);
        data = this.purifyText(data);
        super.processingInstruction(target, data, augs);
    }
    
    public void doctypeDecl(final String root, final String pubid, final String sysid, final Augmentations augs) throws XNIException {
        this.fSeenDoctype = true;
        this.fPublicId = pubid;
        this.fSystemId = sysid;
        if (this.fPublicId != null && this.fSystemId == null) {
            this.fSystemId = "";
        }
    }
    
    public void startElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.handleStartElement(element, attrs);
        super.startElement(element, attrs, augs);
    }
    
    public void emptyElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.handleStartElement(element, attrs);
        super.emptyElement(element, attrs, augs);
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATASection = true;
        super.startCDATA(augs);
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
        super.endCDATA(augs);
    }
    
    public void characters(XMLString text, final Augmentations augs) throws XNIException {
        text = this.purifyText(text);
        if (this.fInCDATASection) {
            final StringBuffer str = new StringBuffer(text.toString());
            final int length = str.length();
            for (int i = length - 1; i >= 0; --i) {
                final char c = str.charAt(i);
                if (c == ']') {
                    str.insert(i + 1, ' ');
                }
            }
            this.fStringBuffer.length = 0;
            this.fStringBuffer.append(str.toString());
            text = (XMLString)this.fStringBuffer;
        }
        super.characters(text, augs);
    }
    
    public void endElement(QName element, final Augmentations augs) throws XNIException {
        element = this.purifyQName(element);
        if (this.fNamespaces && element.prefix != null && element.uri == null) {
            element.uri = this.fNamespaceContext.getURI(element.prefix);
        }
        super.endElement(element, augs);
    }
    
    protected void handleStartDocument() {
        this.fSeenDoctype = false;
        this.fSeenRootElement = false;
    }
    
    protected void handleStartElement(QName element, final XMLAttributes attrs) {
        element = this.purifyQName(element);
        final int attrCount = (attrs != null) ? attrs.getLength() : 0;
        for (int i = attrCount - 1; i >= 0; --i) {
            attrs.getName(i, this.fQName);
            attrs.setName(i, this.purifyQName(this.fQName));
            if (this.fNamespaces && !this.fQName.rawname.equals("xmlns") && !this.fQName.rawname.startsWith("xmlns:")) {
                attrs.getName(i, this.fQName);
                if (this.fQName.prefix != null && this.fQName.uri == null) {
                    this.synthesizeBinding(attrs, this.fQName.prefix);
                }
            }
        }
        if (this.fNamespaces && element.prefix != null && element.uri == null) {
            this.synthesizeBinding(attrs, element.prefix);
        }
        if (!this.fSeenRootElement && this.fSeenDoctype) {
            final Augmentations augs = this.synthesizedAugs();
            super.doctypeDecl(element.rawname, this.fPublicId, this.fSystemId, augs);
        }
        this.fSeenRootElement = true;
    }
    
    protected void synthesizeBinding(final XMLAttributes attrs, final String ns) {
        final String prefix = "xmlns";
        final String localpart = ns;
        final String qname = prefix + ':' + localpart;
        final String uri = "http://cyberneko.org/html/properties/namespaces-uri";
        final String atype = "CDATA";
        final String avalue = "http://cyberneko.org/html/ns/synthesized/" + this.fSynthesizedNamespaceCount++;
        this.fQName.setValues(prefix, localpart, qname, uri);
        attrs.addAttribute(this.fQName, atype, avalue);
        XercesBridge.getInstance().NamespaceContext_declarePrefix(this.fNamespaceContext, ns, avalue);
    }
    
    protected final Augmentations synthesizedAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem("http://cyberneko.org/html/features/augmentations", Purifier.SYNTHESIZED_ITEM);
        }
        return (Augmentations)augs;
    }
    
    protected QName purifyQName(final QName qname) {
        qname.prefix = this.purifyName(qname.prefix, true);
        qname.localpart = this.purifyName(qname.localpart, true);
        qname.rawname = this.purifyName(qname.rawname, false);
        return qname;
    }
    
    protected String purifyName(final String name, final boolean localpart) {
        if (name == null) {
            return name;
        }
        final StringBuffer str = new StringBuffer();
        final int length = name.length();
        boolean seenColon = localpart;
        for (int i = 0; i < length; ++i) {
            final char c = name.charAt(i);
            if (i == 0) {
                if (!XMLChar.isNameStart((int)c)) {
                    str.append("_u" + toHexString(c, 4) + "_");
                }
                else {
                    str.append(c);
                }
            }
            else {
                if ((this.fNamespaces && c == ':' && seenColon) || !XMLChar.isName((int)c)) {
                    str.append("_u" + toHexString(c, 4) + "_");
                }
                else {
                    str.append(c);
                }
                seenColon = (seenColon || c == ':');
            }
        }
        return str.toString();
    }
    
    protected XMLString purifyText(final XMLString text) {
        this.fStringBuffer.length = 0;
        for (int i = 0; i < text.length; ++i) {
            final char c = text.ch[text.offset + i];
            if (XMLChar.isInvalid((int)c)) {
                this.fStringBuffer.append("\\u" + toHexString(c, 4));
            }
            else {
                this.fStringBuffer.append(c);
            }
        }
        return (XMLString)this.fStringBuffer;
    }
    
    protected static String toHexString(final int c, final int padlen) {
        final StringBuffer str = new StringBuffer(padlen);
        str.append(Integer.toHexString(c));
        for (int len = padlen - str.length(), i = 0; i < len; ++i) {
            str.insert(0, '0');
        }
        return str.toString().toUpperCase(Locale.ENGLISH);
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/augmentations" };
        SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
    }
}
