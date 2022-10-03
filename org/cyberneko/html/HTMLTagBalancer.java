package org.cyberneko.html;

import java.util.Locale;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import java.util.Collection;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.util.ArrayList;
import org.apache.xerces.util.XMLAttributesImpl;
import java.util.List;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;

public class HTMLTagBalancer implements XMLDocumentFilter, HTMLComponent
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    protected static final String DOCUMENT_FRAGMENT_DEPRECATED = "http://cyberneko.org/html/features/document-fragment";
    protected static final String DOCUMENT_FRAGMENT = "http://cyberneko.org/html/features/balance-tags/document-fragment";
    protected static final String IGNORE_OUTSIDE_CONTENT = "http://cyberneko.org/html/features/balance-tags/ignore-outside-content";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] RECOGNIZED_FEATURES_DEFAULTS;
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    public static final String FRAGMENT_CONTEXT_STACK = "http://cyberneko.org/html/properties/balance-tags/fragment-context-stack";
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] RECOGNIZED_PROPERTIES_DEFAULTS;
    protected static final short NAMES_NO_CHANGE = 0;
    protected static final short NAMES_MATCH = 0;
    protected static final short NAMES_UPPERCASE = 1;
    protected static final short NAMES_LOWERCASE = 2;
    protected static final HTMLEventInfo SYNTHESIZED_ITEM;
    protected boolean fNamespaces;
    protected boolean fAugmentations;
    protected boolean fReportErrors;
    protected boolean fDocumentFragment;
    protected boolean fIgnoreOutsideContent;
    protected boolean fAllowSelfclosingIframe;
    protected boolean fAllowSelfclosingTags;
    protected short fNamesElems;
    protected short fNamesAttrs;
    protected HTMLErrorReporter fErrorReporter;
    protected XMLDocumentSource fDocumentSource;
    protected XMLDocumentHandler fDocumentHandler;
    protected final InfoStack fElementStack;
    protected final InfoStack fInlineStack;
    protected boolean fSeenAnything;
    protected boolean fSeenDoctype;
    protected boolean fSeenRootElement;
    protected boolean fSeenRootElementEnd;
    protected boolean fSeenHeadElement;
    protected boolean fSeenBodyElement;
    private boolean fSeenBodyElementEnd;
    private boolean fSeenFramesetElement;
    protected boolean fOpenedForm;
    private final QName fQName;
    private final XMLAttributes fEmptyAttrs;
    private final HTMLAugmentations fInfosetAugs;
    protected HTMLTagBalancingListener tagBalancingListener;
    private LostText lostText_;
    private boolean forcedStartElement_;
    private boolean forcedEndElement_;
    private QName[] fragmentContextStack_;
    private int fragmentContextStackSize_;
    private List endElementsBuffer_;
    
    public HTMLTagBalancer() {
        this.fElementStack = new InfoStack();
        this.fInlineStack = new InfoStack();
        this.fQName = new QName();
        this.fEmptyAttrs = (XMLAttributes)new XMLAttributesImpl();
        this.fInfosetAugs = new HTMLAugmentations();
        this.lostText_ = new LostText();
        this.forcedStartElement_ = false;
        this.forcedEndElement_ = true;
        this.fragmentContextStack_ = null;
        this.fragmentContextStackSize_ = 0;
        this.endElementsBuffer_ = new ArrayList();
    }
    
    public Boolean getFeatureDefault(final String featureId) {
        for (int length = (HTMLTagBalancer.RECOGNIZED_FEATURES != null) ? HTMLTagBalancer.RECOGNIZED_FEATURES.length : 0, i = 0; i < length; ++i) {
            if (HTMLTagBalancer.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return HTMLTagBalancer.RECOGNIZED_FEATURES_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String propertyId) {
        for (int length = (HTMLTagBalancer.RECOGNIZED_PROPERTIES != null) ? HTMLTagBalancer.RECOGNIZED_PROPERTIES.length : 0, i = 0; i < length; ++i) {
            if (HTMLTagBalancer.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return HTMLTagBalancer.RECOGNIZED_PROPERTIES_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public String[] getRecognizedFeatures() {
        return HTMLTagBalancer.RECOGNIZED_FEATURES;
    }
    
    public String[] getRecognizedProperties() {
        return HTMLTagBalancer.RECOGNIZED_PROPERTIES;
    }
    
    public void reset(final XMLComponentManager manager) throws XMLConfigurationException {
        this.fNamespaces = manager.getFeature("http://xml.org/sax/features/namespaces");
        this.fAugmentations = manager.getFeature("http://cyberneko.org/html/features/augmentations");
        this.fReportErrors = manager.getFeature("http://cyberneko.org/html/features/report-errors");
        this.fDocumentFragment = (manager.getFeature("http://cyberneko.org/html/features/balance-tags/document-fragment") || manager.getFeature("http://cyberneko.org/html/features/document-fragment"));
        this.fIgnoreOutsideContent = manager.getFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content");
        this.fAllowSelfclosingIframe = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe");
        this.fAllowSelfclosingTags = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags");
        this.fNamesElems = getNamesValue(String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/names/elems")));
        this.fNamesAttrs = getNamesValue(String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/names/attrs")));
        this.fErrorReporter = (HTMLErrorReporter)manager.getProperty("http://cyberneko.org/html/properties/error-reporter");
        this.fragmentContextStack_ = (QName[])manager.getProperty("http://cyberneko.org/html/properties/balance-tags/fragment-context-stack");
        this.fSeenAnything = false;
        this.fSeenDoctype = false;
        this.fSeenRootElement = false;
        this.fSeenRootElementEnd = false;
        this.fSeenHeadElement = false;
        this.fSeenBodyElement = false;
        this.fSeenBodyElementEnd = false;
        this.fSeenFramesetElement = false;
    }
    
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        if (featureId.equals("http://cyberneko.org/html/features/augmentations")) {
            this.fAugmentations = state;
            return;
        }
        if (featureId.equals("http://cyberneko.org/html/features/report-errors")) {
            this.fReportErrors = state;
            return;
        }
        if (featureId.equals("http://cyberneko.org/html/features/balance-tags/ignore-outside-content")) {
            this.fIgnoreOutsideContent = state;
        }
    }
    
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.equals("http://cyberneko.org/html/properties/names/elems")) {
            this.fNamesElems = getNamesValue(String.valueOf(value));
            return;
        }
        if (propertyId.equals("http://cyberneko.org/html/properties/names/attrs")) {
            this.fNamesAttrs = getNamesValue(String.valueOf(value));
        }
    }
    
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        this.fElementStack.top = 0;
        if (this.fragmentContextStack_ != null) {
            this.fragmentContextStackSize_ = this.fragmentContextStack_.length;
            for (int i = 0; i < this.fragmentContextStack_.length; ++i) {
                final QName name = this.fragmentContextStack_[i];
                final HTMLElements.Element elt = HTMLElements.getElement(name.localpart);
                this.fElementStack.push(new Info(elt, name));
            }
        }
        else {
            this.fragmentContextStackSize_ = 0;
        }
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startDocument(this.fDocumentHandler, locator, encoding, nscontext, augs);
        }
    }
    
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        if (!this.fSeenAnything && this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }
    
    public void doctypeDecl(final String rootElementName, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fReportErrors) {
            if (this.fSeenRootElement) {
                this.fErrorReporter.reportError("HTML2010", null);
            }
            else if (this.fSeenDoctype) {
                this.fErrorReporter.reportError("HTML2011", null);
            }
        }
        if (!this.fSeenRootElement && !this.fSeenDoctype) {
            this.fSeenDoctype = true;
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.doctypeDecl(rootElementName, publicId, systemId, augs);
            }
        }
    }
    
    public void endDocument(final Augmentations augs) throws XNIException {
        this.fIgnoreOutsideContent = true;
        this.consumeBufferedEndElements();
        if (!this.fSeenRootElement && !this.fDocumentFragment) {
            if (this.fReportErrors) {
                this.fErrorReporter.reportError("HTML2000", null);
            }
            if (this.fDocumentHandler != null) {
                this.fSeenRootElementEnd = false;
                this.forceStartBody();
                final String body = modifyName("body", this.fNamesElems);
                this.fQName.setValues((String)null, body, body, (String)null);
                this.callEndElement(this.fQName, this.synthesizedAugs());
                final String ename = modifyName("html", this.fNamesElems);
                this.fQName.setValues((String)null, ename, ename, (String)null);
                this.callEndElement(this.fQName, this.synthesizedAugs());
            }
        }
        else {
            for (int length = this.fElementStack.top - this.fragmentContextStackSize_, i = 0; i < length; ++i) {
                final Info info = this.fElementStack.pop();
                if (this.fReportErrors) {
                    final String ename2 = info.qname.rawname;
                    this.fErrorReporter.reportWarning("HTML2001", new Object[] { ename2 });
                }
                if (this.fDocumentHandler != null) {
                    this.callEndElement(info.qname, this.synthesizedAugs());
                }
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
        }
    }
    
    private void consumeBufferedEndElements() {
        final List toConsume = new ArrayList(this.endElementsBuffer_);
        this.endElementsBuffer_.clear();
        for (int i = 0; i < toConsume.size(); ++i) {
            final ElementEntry entry = toConsume.get(i);
            this.forcedEndElement_ = true;
            this.endElement(entry.name_, entry.augs_);
        }
        this.endElementsBuffer_.clear();
    }
    
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(text, augs);
        }
    }
    
    private void consumeEarlyTextIfNeeded() {
        if (!this.lostText_.isEmpty()) {
            if (!this.fSeenBodyElement) {
                this.forceStartBody();
            }
            this.lostText_.refeed((XMLDocumentHandler)this);
        }
    }
    
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }
    
    public void startElement(final QName elem, XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        final boolean isForcedCreation = this.forcedStartElement_;
        this.forcedStartElement_ = false;
        if (this.fSeenRootElementEnd) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        final HTMLElements.Element element = this.getElement(elem);
        final short elementCode = element.code;
        if (isForcedCreation && (elementCode == 102 || elementCode == 92)) {
            return;
        }
        if (this.fSeenRootElement && elementCode == 46) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        if (this.fSeenFramesetElement && elementCode != 36 && elementCode != 37 && elementCode != 70) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        if (elementCode == 44) {
            if (this.fSeenHeadElement) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fSeenHeadElement = true;
        }
        else if (elementCode == 37) {
            if (!this.fSeenHeadElement) {
                final QName head = this.createQName("head");
                this.forceStartElement(head, null, this.synthesizedAugs());
                this.endElement(head, this.synthesizedAugs());
            }
            this.consumeBufferedEndElements();
            this.fSeenFramesetElement = true;
        }
        else if (elementCode == 14) {
            if (!this.fSeenHeadElement) {
                final QName head = this.createQName("head");
                this.forceStartElement(head, null, this.synthesizedAugs());
                this.endElement(head, this.synthesizedAugs());
            }
            this.consumeBufferedEndElements();
            if (this.fSeenBodyElement) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fSeenBodyElement = true;
        }
        else if (elementCode == 35) {
            if (this.fOpenedForm) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fOpenedForm = true;
        }
        else if (elementCode == 118) {
            this.consumeBufferedEndElements();
        }
        Label_0631: {
            if (element.parent != null) {
                final HTMLElements.Element preferedParent = element.parent[0];
                if (this.fDocumentFragment) {
                    if (preferedParent.code == 44) {
                        break Label_0631;
                    }
                    if (preferedParent.code == 14) {
                        break Label_0631;
                    }
                }
                if (!this.fSeenRootElement && !this.fDocumentFragment) {
                    String pname = preferedParent.name;
                    pname = modifyName(pname, this.fNamesElems);
                    if (this.fReportErrors) {
                        final String ename = elem.rawname;
                        this.fErrorReporter.reportWarning("HTML2002", new Object[] { ename, pname });
                    }
                    final QName qname = new QName((String)null, pname, pname, (String)null);
                    final boolean parentCreated = this.forceStartElement(qname, null, this.synthesizedAugs());
                    if (!parentCreated) {
                        if (!isForcedCreation) {
                            this.notifyDiscardedStartElement(elem, attrs, augs);
                        }
                        return;
                    }
                }
                else if (preferedParent.code != 44 || (!this.fSeenBodyElement && !this.fDocumentFragment)) {
                    final int depth = this.getParentDepth(element.parent, element.bounds);
                    if (depth == -1) {
                        final String pname2 = modifyName(preferedParent.name, this.fNamesElems);
                        final QName qname2 = new QName((String)null, pname2, pname2, (String)null);
                        if (this.fReportErrors) {
                            final String ename2 = elem.rawname;
                            this.fErrorReporter.reportWarning("HTML2004", new Object[] { ename2, pname2 });
                        }
                        final boolean parentCreated2 = this.forceStartElement(qname2, null, this.synthesizedAugs());
                        if (!parentCreated2) {
                            if (!isForcedCreation) {
                                this.notifyDiscardedStartElement(elem, attrs, augs);
                            }
                            return;
                        }
                    }
                }
            }
        }
        int depth2 = 0;
        if (element.flags == 0) {
            final int length = this.fElementStack.top;
            this.fInlineStack.top = 0;
            for (int i = length - 1; i >= 0; --i) {
                final Info info = this.fElementStack.data[i];
                if (!info.element.isInline()) {
                    break;
                }
                this.fInlineStack.push(info);
                this.endElement(info.qname, this.synthesizedAugs());
            }
            depth2 = this.fInlineStack.top;
        }
        if (this.fElementStack.top > 1 && this.fElementStack.peek().element.code == 90) {
            final Info info2 = this.fElementStack.pop();
            if (this.fDocumentHandler != null) {
                this.callEndElement(info2.qname, this.synthesizedAugs());
            }
        }
        if (element.closes != null) {
            int length = this.fElementStack.top;
            for (int i = length - 1; i >= 0; --i) {
                Info info = this.fElementStack.data[i];
                if (element.closes(info.element.code)) {
                    if (this.fReportErrors) {
                        final String ename2 = elem.rawname;
                        final String iname = info.qname.rawname;
                        this.fErrorReporter.reportWarning("HTML2005", new Object[] { ename2, iname });
                    }
                    for (int j = length - 1; j >= i; --j) {
                        info = this.fElementStack.pop();
                        if (this.fDocumentHandler != null) {
                            this.callEndElement(info.qname, this.synthesizedAugs());
                        }
                    }
                    length = i;
                }
                else {
                    if (info.element.isBlock()) {
                        break;
                    }
                    if (element.isParent(info.element)) {
                        break;
                    }
                }
            }
        }
        this.fSeenRootElement = true;
        if (element != null && element.isEmpty()) {
            if (attrs == null) {
                attrs = this.emptyAttributes();
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.emptyElement(elem, attrs, augs);
            }
        }
        else {
            final boolean inline = element != null && element.isInline();
            this.fElementStack.push(new Info(element, elem, inline ? attrs : null));
            if (attrs == null) {
                attrs = this.emptyAttributes();
            }
            if (this.fDocumentHandler != null) {
                this.callStartElement(elem, attrs, augs);
            }
        }
        for (int k = 0; k < depth2; ++k) {
            final Info info3 = this.fInlineStack.pop();
            this.forceStartElement(info3.qname, info3.attributes, this.synthesizedAugs());
        }
        if (elementCode == 14) {
            this.lostText_.refeed((XMLDocumentHandler)this);
        }
    }
    
    private boolean forceStartElement(final QName elem, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.forcedStartElement_ = true;
        this.startElement(elem, attrs, augs);
        return this.fElementStack.top > 0 && elem.equals((Object)this.fElementStack.peek().qname);
    }
    
    private QName createQName(String tagName) {
        tagName = modifyName(tagName, this.fNamesElems);
        return new QName((String)null, tagName, tagName, "http://www.w3.org/1999/xhtml");
    }
    
    public void emptyElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.startElement(element, attrs, augs);
        final HTMLElements.Element elem = this.getElement(element);
        if (elem.isEmpty() || this.fAllowSelfclosingTags || elem.code == 118 || (elem.code == 48 && this.fAllowSelfclosingIframe)) {
            this.endElement(element, augs);
        }
    }
    
    public void startGeneralEntity(final String name, final XMLResourceIdentifier id, final String encoding, final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (!this.fDocumentFragment) {
            boolean insertBody = !this.fSeenRootElement;
            if (!insertBody) {
                final Info info = this.fElementStack.peek();
                if (info.element.code == 44 || info.element.code == 46) {
                    final String hname = modifyName("head", this.fNamesElems);
                    final String bname = modifyName("body", this.fNamesElems);
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML2009", new Object[] { hname, bname });
                    }
                    this.fQName.setValues((String)null, hname, hname, (String)null);
                    this.endElement(this.fQName, this.synthesizedAugs());
                    insertBody = true;
                }
            }
            if (insertBody) {
                this.forceStartBody();
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(name, id, encoding, augs);
        }
    }
    
    private void forceStartBody() {
        final QName body = this.createQName("body");
        if (this.fReportErrors) {
            this.fErrorReporter.reportWarning("HTML2006", new Object[] { body.localpart });
        }
        this.forceStartElement(body, null, this.synthesizedAugs());
    }
    
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }
    
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd || this.fSeenBodyElementEnd) {
            return;
        }
        if (this.fElementStack.top == 0 && !this.fDocumentFragment) {
            this.lostText_.add(text, augs);
            return;
        }
        boolean whitespace = true;
        for (int i = 0; i < text.length; ++i) {
            if (!Character.isWhitespace(text.ch[text.offset + i])) {
                whitespace = false;
                break;
            }
        }
        if (!this.fDocumentFragment) {
            if (!this.fSeenRootElement) {
                if (whitespace) {
                    return;
                }
                this.forceStartBody();
            }
            if (whitespace && (this.fElementStack.top < 2 || this.endElementsBuffer_.size() == 1)) {
                return;
            }
            if (!whitespace) {
                final Info info = this.fElementStack.peek();
                if (info.element.code == 44 || info.element.code == 46) {
                    final String hname = modifyName("head", this.fNamesElems);
                    final String bname = modifyName("body", this.fNamesElems);
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML2009", new Object[] { hname, bname });
                    }
                    this.forceStartBody();
                }
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(text, augs);
        }
    }
    
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        final boolean forcedEndElement = this.forcedEndElement_;
        if (this.fSeenRootElementEnd) {
            this.notifyDiscardedEndElement(element, augs);
            return;
        }
        final HTMLElements.Element elem = this.getElement(element);
        if (!this.fIgnoreOutsideContent && (elem.code == 14 || elem.code == 46)) {
            this.endElementsBuffer_.add(new ElementEntry(element, augs));
            return;
        }
        if (this.fSeenFramesetElement && elem.code != 36 && elem.code != 37) {
            this.notifyDiscardedEndElement(element, augs);
            return;
        }
        if (elem.code == 46) {
            this.fSeenRootElementEnd = true;
        }
        else if (this.fIgnoreOutsideContent) {
            if (elem.code == 14) {
                this.fSeenBodyElementEnd = true;
            }
            else if (this.fSeenBodyElementEnd) {
                this.notifyDiscardedEndElement(element, augs);
                return;
            }
        }
        else if (elem.code == 35) {
            this.fOpenedForm = false;
        }
        else if (elem.code == 44 && !forcedEndElement) {
            this.endElementsBuffer_.add(new ElementEntry(element, augs));
            return;
        }
        final int depth = this.getElementDepth(elem);
        if (depth == -1) {
            if (elem.code == 77) {
                this.forceStartElement(element, this.emptyAttributes(), this.synthesizedAugs());
                this.endElement(element, augs);
            }
            else if (!elem.isEmpty()) {
                this.notifyDiscardedEndElement(element, augs);
            }
            return;
        }
        if (depth > 1 && elem.isInline()) {
            final int size = this.fElementStack.top;
            this.fInlineStack.top = 0;
            for (int i = 0; i < depth - 1; ++i) {
                final Info info = this.fElementStack.data[size - i - 1];
                final HTMLElements.Element pelem = info.element;
                if (pelem.isInline() || pelem.code == 34) {
                    this.fInlineStack.push(info);
                }
            }
        }
        for (int j = 0; j < depth; ++j) {
            final Info info2 = this.fElementStack.pop();
            if (this.fReportErrors && j < depth - 1) {
                final String ename = modifyName(element.rawname, this.fNamesElems);
                final String iname = info2.qname.rawname;
                this.fErrorReporter.reportWarning("HTML2007", new Object[] { ename, iname });
            }
            if (this.fDocumentHandler != null) {
                this.callEndElement(info2.qname, (j < depth - 1) ? this.synthesizedAugs() : augs);
            }
        }
        if (depth > 1) {
            for (int size = this.fInlineStack.top, i = 0; i < size; ++i) {
                final Info info = this.fInlineStack.pop();
                final XMLAttributes attributes = info.attributes;
                if (this.fReportErrors) {
                    final String iname2 = info.qname.rawname;
                    this.fErrorReporter.reportWarning("HTML2008", new Object[] { iname2 });
                }
                this.forceStartElement(info.qname, attributes, this.synthesizedAugs());
            }
        }
    }
    
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }
    
    public void startPrefixMapping(final String prefix, final String uri, final Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
        }
    }
    
    public void endPrefixMapping(final String prefix, final Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
        }
    }
    
    protected HTMLElements.Element getElement(final QName elementName) {
        String name = elementName.rawname;
        if (this.fNamespaces && "http://www.w3.org/1999/xhtml".equals(elementName.uri)) {
            final int index = name.indexOf(58);
            if (index != -1) {
                name = name.substring(index + 1);
            }
        }
        return HTMLElements.getElement(name);
    }
    
    protected final void callStartElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.fDocumentHandler.startElement(element, attrs, augs);
    }
    
    protected final void callEndElement(final QName element, final Augmentations augs) throws XNIException {
        this.fDocumentHandler.endElement(element, augs);
    }
    
    protected final int getElementDepth(final HTMLElements.Element element) {
        final boolean container = element.isContainer();
        final short elementCode = element.code;
        final boolean tableBodyOrHtml = elementCode == 102 || elementCode == 14 || elementCode == 46;
        int depth = -1;
        for (int i = this.fElementStack.top - 1; i >= this.fragmentContextStackSize_; --i) {
            final Info info = this.fElementStack.data[i];
            if (info.element.code == element.code && (elementCode != 118 || (elementCode == 118 && element.name.equals(info.element.name)))) {
                depth = this.fElementStack.top - i;
                break;
            }
            if (!container && info.element.isBlock()) {
                break;
            }
            if (info.element.code == 102 && !tableBodyOrHtml) {
                return -1;
            }
            if (element.isParent(info.element)) {
                break;
            }
        }
        return depth;
    }
    
    protected int getParentDepth(final HTMLElements.Element[] parents, final short bounds) {
        if (parents != null) {
            for (int i = this.fElementStack.top - 1; i >= 0; --i) {
                final Info info = this.fElementStack.data[i];
                if (info.element.code == bounds) {
                    break;
                }
                for (int j = 0; j < parents.length; ++j) {
                    if (info.element.code == parents[j].code) {
                        return this.fElementStack.top - i;
                    }
                }
            }
        }
        return -1;
    }
    
    protected final XMLAttributes emptyAttributes() {
        this.fEmptyAttrs.removeAllAttributes();
        return this.fEmptyAttrs;
    }
    
    protected final Augmentations synthesizedAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem("http://cyberneko.org/html/features/augmentations", HTMLTagBalancer.SYNTHESIZED_ITEM);
        }
        return (Augmentations)augs;
    }
    
    protected static final String modifyName(final String name, final short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ENGLISH);
            }
            case 2: {
                return name.toLowerCase(Locale.ENGLISH);
            }
            default: {
                return name;
            }
        }
    }
    
    protected static final short getNamesValue(final String value) {
        if (value.equals("lower")) {
            return 2;
        }
        if (value.equals("upper")) {
            return 1;
        }
        return 0;
    }
    
    void setTagBalancingListener(final HTMLTagBalancingListener tagBalancingListener) {
        this.tagBalancingListener = tagBalancingListener;
    }
    
    private void notifyDiscardedStartElement(final QName elem, final XMLAttributes attrs, final Augmentations augs) {
        if (this.tagBalancingListener != null) {
            this.tagBalancingListener.ignoredStartElement(elem, attrs, augs);
        }
    }
    
    private void notifyDiscardedEndElement(final QName element, final Augmentations augs) {
        if (this.tagBalancingListener != null) {
            this.tagBalancingListener.ignoredEndElement(element, augs);
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/augmentations", "http://cyberneko.org/html/features/report-errors", "http://cyberneko.org/html/features/document-fragment", "http://cyberneko.org/html/features/balance-tags/document-fragment", "http://cyberneko.org/html/features/balance-tags/ignore-outside-content" };
        RECOGNIZED_FEATURES_DEFAULTS = new Boolean[] { null, null, null, null, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/error-reporter", "http://cyberneko.org/html/properties/balance-tags/fragment-context-stack" };
        RECOGNIZED_PROPERTIES_DEFAULTS = new Object[] { null, null, null, null };
        SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
    }
    
    public static class Info
    {
        public HTMLElements.Element element;
        public QName qname;
        public XMLAttributes attributes;
        
        public Info(final HTMLElements.Element element, final QName qname) {
            this(element, qname, null);
        }
        
        public Info(final HTMLElements.Element element, final QName qname, final XMLAttributes attributes) {
            this.element = element;
            this.qname = new QName(qname);
            if (attributes != null) {
                final int length = attributes.getLength();
                if (length > 0) {
                    final QName aqname = new QName();
                    final XMLAttributes newattrs = (XMLAttributes)new XMLAttributesImpl();
                    for (int i = 0; i < length; ++i) {
                        attributes.getName(i, aqname);
                        final String type = attributes.getType(i);
                        final String value = attributes.getValue(i);
                        final String nonNormalizedValue = attributes.getNonNormalizedValue(i);
                        final boolean specified = attributes.isSpecified(i);
                        newattrs.addAttribute(aqname, type, value);
                        newattrs.setNonNormalizedValue(i, nonNormalizedValue);
                        newattrs.setSpecified(i, specified);
                    }
                    this.attributes = newattrs;
                }
            }
        }
        
        @Override
        public String toString() {
            return super.toString() + this.qname;
        }
    }
    
    public static class InfoStack
    {
        public int top;
        public Info[] data;
        
        public InfoStack() {
            this.data = new Info[10];
        }
        
        public void push(final Info info) {
            if (this.top == this.data.length) {
                final Info[] newarray = new Info[this.top + 10];
                System.arraycopy(this.data, 0, newarray, 0, this.top);
                this.data = newarray;
            }
            this.data[this.top++] = info;
        }
        
        public Info peek() {
            return this.data[this.top - 1];
        }
        
        public Info pop() {
            final Info[] data = this.data;
            final int top = this.top - 1;
            this.top = top;
            return data[top];
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("InfoStack(");
            for (int i = this.top - 1; i >= 0; --i) {
                sb.append(this.data[i]);
                if (i != 0) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
    
    static class ElementEntry
    {
        private final QName name_;
        private final Augmentations augs_;
        
        ElementEntry(final QName element, final Augmentations augs) {
            this.name_ = new QName(element);
            this.augs_ = (Augmentations)((augs == null) ? null : new HTMLAugmentations(augs));
        }
    }
}
