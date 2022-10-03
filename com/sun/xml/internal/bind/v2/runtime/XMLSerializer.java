package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.util.ValidationEventLocatorExImpl;
import java.util.Collection;
import javax.xml.bind.annotation.XmlNs;
import com.sun.xml.internal.bind.v2.runtime.output.MTOMXmlOutput;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import javax.xml.bind.annotation.DomHandler;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Method;
import javax.xml.namespace.NamespaceContext;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.bind.CycleRecoverable;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.NotIdentifiableEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.ValidationEventHandler;
import com.sun.istack.internal.SAXException2;
import javax.xml.bind.ValidationEvent;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import java.util.HashSet;
import javax.xml.namespace.QName;
import javax.activation.MimeType;
import javax.xml.bind.attachment.AttachmentMarshaller;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.IntData;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import javax.xml.transform.Transformer;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import java.util.Set;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;

public final class XMLSerializer extends Coordinator
{
    public final JAXBContextImpl grammar;
    private XmlOutput out;
    public final NameList nameList;
    public final int[] knownUri2prefixIndexMap;
    private final NamespaceContextImpl nsContext;
    private NamespaceContextImpl.Element nse;
    ThreadLocal<Property> currentProperty;
    private boolean textHasAlreadyPrinted;
    private boolean seenRoot;
    private final MarshallerImpl marshaller;
    private final Set<Object> idReferencedObjects;
    private final Set<Object> objectsWithId;
    private final CollisionCheckStack<Object> cycleDetectionStack;
    private String schemaLocation;
    private String noNsSchemaLocation;
    private Transformer identityTransformer;
    private ContentHandlerAdaptor contentHandlerAdapter;
    private boolean fragment;
    private Base64Data base64Data;
    private final IntData intData;
    public AttachmentMarshaller attachmentMarshaller;
    private MimeType expectedMimeType;
    private boolean inlineBinaryFlag;
    private QName schemaType;
    
    XMLSerializer(final MarshallerImpl _owner) {
        this.currentProperty = new ThreadLocal<Property>();
        this.textHasAlreadyPrinted = false;
        this.seenRoot = false;
        this.idReferencedObjects = new HashSet<Object>();
        this.objectsWithId = new HashSet<Object>();
        this.cycleDetectionStack = new CollisionCheckStack<Object>();
        this.intData = new IntData();
        this.marshaller = _owner;
        this.grammar = this.marshaller.context;
        this.nsContext = new NamespaceContextImpl(this);
        this.nameList = this.marshaller.context.nameList;
        this.knownUri2prefixIndexMap = new int[this.nameList.namespaceURIs.length];
    }
    
    @Deprecated
    public Base64Data getCachedBase64DataInstance() {
        return new Base64Data();
    }
    
    private String getIdFromObject(final Object identifiableObject) throws SAXException, JAXBException {
        return this.grammar.getBeanInfo(identifiableObject, true).getId(identifiableObject, this);
    }
    
    private void handleMissingObjectError(final String fieldName) throws SAXException, IOException, XMLStreamException {
        this.reportMissingObjectError(fieldName);
        this.endNamespaceDecls(null);
        this.endAttributes();
    }
    
    public void reportError(final ValidationEvent ve) throws SAXException {
        ValidationEventHandler handler;
        try {
            handler = this.marshaller.getEventHandler();
        }
        catch (final JAXBException e) {
            throw new SAXException2(e);
        }
        if (handler.handleEvent(ve)) {
            return;
        }
        if (ve.getLinkedException() instanceof Exception) {
            throw new SAXException2((Exception)ve.getLinkedException());
        }
        throw new SAXException2(ve.getMessage());
    }
    
    public final void reportError(final String fieldName, final Throwable t) throws SAXException {
        final ValidationEvent ve = new ValidationEventImpl(1, t.getMessage(), this.getCurrentLocation(fieldName), t);
        this.reportError(ve);
    }
    
    public void startElement(final Name tagName, final Object outerPeer) {
        this.startElement();
        this.nse.setTagName(tagName, outerPeer);
    }
    
    public void startElement(final String nsUri, final String localName, final String preferredPrefix, final Object outerPeer) {
        this.startElement();
        final int idx = this.nsContext.declareNsUri(nsUri, preferredPrefix, false);
        this.nse.setTagName(idx, localName, outerPeer);
    }
    
    public void startElementForce(final String nsUri, final String localName, final String forcedPrefix, final Object outerPeer) {
        this.startElement();
        final int idx = this.nsContext.force(nsUri, forcedPrefix);
        this.nse.setTagName(idx, localName, outerPeer);
    }
    
    public void endNamespaceDecls(final Object innerPeer) throws IOException, XMLStreamException {
        this.nsContext.collectionMode = false;
        this.nse.startElement(this.out, innerPeer);
    }
    
    public void endAttributes() throws SAXException, IOException, XMLStreamException {
        if (!this.seenRoot) {
            this.seenRoot = true;
            if (this.schemaLocation != null || this.noNsSchemaLocation != null) {
                final int p = this.nsContext.getPrefixIndex("http://www.w3.org/2001/XMLSchema-instance");
                if (this.schemaLocation != null) {
                    this.out.attribute(p, "schemaLocation", this.schemaLocation);
                }
                if (this.noNsSchemaLocation != null) {
                    this.out.attribute(p, "noNamespaceSchemaLocation", this.noNsSchemaLocation);
                }
            }
        }
        this.out.endStartTag();
    }
    
    public void endElement() throws SAXException, IOException, XMLStreamException {
        this.nse.endElement(this.out);
        this.nse = this.nse.pop();
        this.textHasAlreadyPrinted = false;
    }
    
    public void leafElement(final Name tagName, final String data, final String fieldName) throws SAXException, IOException, XMLStreamException {
        if (this.seenRoot) {
            this.textHasAlreadyPrinted = false;
            this.nse = this.nse.push();
            this.out.beginStartTag(tagName);
            this.out.endStartTag();
            if (data != null) {
                try {
                    this.out.text(data, false);
                }
                catch (final IllegalArgumentException e) {
                    throw new IllegalArgumentException(Messages.ILLEGAL_CONTENT.format(fieldName, e.getMessage()));
                }
            }
            this.out.endTag(tagName);
            this.nse = this.nse.pop();
        }
        else {
            this.startElement(tagName, null);
            this.endNamespaceDecls(null);
            this.endAttributes();
            try {
                this.out.text(data, false);
            }
            catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException(Messages.ILLEGAL_CONTENT.format(fieldName, e.getMessage()));
            }
            this.endElement();
        }
    }
    
    public void leafElement(final Name tagName, final Pcdata data, final String fieldName) throws SAXException, IOException, XMLStreamException {
        if (this.seenRoot) {
            this.textHasAlreadyPrinted = false;
            this.nse = this.nse.push();
            this.out.beginStartTag(tagName);
            this.out.endStartTag();
            if (data != null) {
                this.out.text(data, false);
            }
            this.out.endTag(tagName);
            this.nse = this.nse.pop();
        }
        else {
            this.startElement(tagName, null);
            this.endNamespaceDecls(null);
            this.endAttributes();
            this.out.text(data, false);
            this.endElement();
        }
    }
    
    public void leafElement(final Name tagName, final int data, final String fieldName) throws SAXException, IOException, XMLStreamException {
        this.intData.reset(data);
        this.leafElement(tagName, this.intData, fieldName);
    }
    
    public void text(final String text, final String fieldName) throws SAXException, IOException, XMLStreamException {
        if (text == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        this.out.text(text, this.textHasAlreadyPrinted);
        this.textHasAlreadyPrinted = true;
    }
    
    public void text(final Pcdata text, final String fieldName) throws SAXException, IOException, XMLStreamException {
        if (text == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        this.out.text(text, this.textHasAlreadyPrinted);
        this.textHasAlreadyPrinted = true;
    }
    
    public void attribute(final String uri, final String local, final String value) throws SAXException {
        int prefix;
        if (uri.length() == 0) {
            prefix = -1;
        }
        else {
            prefix = this.nsContext.getPrefixIndex(uri);
        }
        try {
            this.out.attribute(prefix, local, value);
        }
        catch (final IOException e) {
            throw new SAXException2(e);
        }
        catch (final XMLStreamException e2) {
            throw new SAXException2(e2);
        }
    }
    
    public void attribute(final Name name, final CharSequence value) throws IOException, XMLStreamException {
        this.out.attribute(name, value.toString());
    }
    
    public NamespaceContext2 getNamespaceContext() {
        return this.nsContext;
    }
    
    public String onID(final Object owner, final String value) {
        this.objectsWithId.add(owner);
        return value;
    }
    
    public String onIDREF(final Object obj) throws SAXException {
        String id;
        try {
            id = this.getIdFromObject(obj);
        }
        catch (final JAXBException e) {
            this.reportError(null, e);
            return null;
        }
        this.idReferencedObjects.add(obj);
        if (id == null) {
            this.reportError(new NotIdentifiableEventImpl(1, Messages.NOT_IDENTIFIABLE.format(new Object[0]), new ValidationEventLocatorImpl(obj)));
        }
        return id;
    }
    
    public void childAsRoot(final Object obj) throws JAXBException, IOException, SAXException, XMLStreamException {
        final JaxBeanInfo beanInfo = this.grammar.getBeanInfo(obj, true);
        this.cycleDetectionStack.pushNocheck(obj);
        final boolean lookForLifecycleMethods = beanInfo.lookForLifecycleMethods();
        if (lookForLifecycleMethods) {
            this.fireBeforeMarshalEvents(beanInfo, obj);
        }
        beanInfo.serializeRoot(obj, this);
        if (lookForLifecycleMethods) {
            this.fireAfterMarshalEvents(beanInfo, obj);
        }
        this.cycleDetectionStack.pop();
    }
    
    private Object pushObject(Object obj, final String fieldName) throws SAXException {
        if (!this.cycleDetectionStack.push(obj)) {
            return obj;
        }
        if (!(obj instanceof CycleRecoverable)) {
            this.reportError(new ValidationEventImpl(1, Messages.CYCLE_IN_MARSHALLER.format(this.cycleDetectionStack.getCycleString()), this.getCurrentLocation(fieldName), null));
            return null;
        }
        obj = ((CycleRecoverable)obj).onCycleDetected(new CycleRecoverable.Context() {
            @Override
            public Marshaller getMarshaller() {
                return XMLSerializer.this.marshaller;
            }
        });
        if (obj != null) {
            this.cycleDetectionStack.pop();
            return this.pushObject(obj, fieldName);
        }
        return null;
    }
    
    public final void childAsSoleContent(Object child, final String fieldName) throws SAXException, IOException, XMLStreamException {
        if (child == null) {
            this.handleMissingObjectError(fieldName);
        }
        else {
            child = this.pushObject(child, fieldName);
            if (child == null) {
                this.endNamespaceDecls(null);
                this.endAttributes();
                this.cycleDetectionStack.pop();
            }
            JaxBeanInfo beanInfo;
            try {
                beanInfo = this.grammar.getBeanInfo(child, true);
            }
            catch (final JAXBException e) {
                this.reportError(fieldName, e);
                this.endNamespaceDecls(null);
                this.endAttributes();
                this.cycleDetectionStack.pop();
                return;
            }
            final boolean lookForLifecycleMethods = beanInfo.lookForLifecycleMethods();
            if (lookForLifecycleMethods) {
                this.fireBeforeMarshalEvents(beanInfo, child);
            }
            beanInfo.serializeURIs(child, this);
            this.endNamespaceDecls(child);
            beanInfo.serializeAttributes(child, this);
            this.endAttributes();
            beanInfo.serializeBody(child, this);
            if (lookForLifecycleMethods) {
                this.fireAfterMarshalEvents(beanInfo, child);
            }
            this.cycleDetectionStack.pop();
        }
    }
    
    public final void childAsXsiType(Object child, final String fieldName, final JaxBeanInfo expected, final boolean nillable) throws SAXException, IOException, XMLStreamException {
        if (child == null) {
            this.handleMissingObjectError(fieldName);
        }
        else {
            child = this.pushObject(child, fieldName);
            if (child == null) {
                this.endNamespaceDecls(null);
                this.endAttributes();
                return;
            }
            boolean asExpected = child.getClass() == expected.jaxbType;
            JaxBeanInfo actual = expected;
            QName actualTypeName = null;
            if (asExpected && actual.lookForLifecycleMethods()) {
                this.fireBeforeMarshalEvents(actual, child);
            }
            if (!asExpected) {
                try {
                    actual = this.grammar.getBeanInfo(child, true);
                    if (actual.lookForLifecycleMethods()) {
                        this.fireBeforeMarshalEvents(actual, child);
                    }
                }
                catch (final JAXBException e) {
                    this.reportError(fieldName, e);
                    this.endNamespaceDecls(null);
                    this.endAttributes();
                    return;
                }
                if (actual == expected) {
                    asExpected = true;
                }
                else {
                    actualTypeName = actual.getTypeName(child);
                    if (actualTypeName == null) {
                        this.reportError(new ValidationEventImpl(1, Messages.SUBSTITUTED_BY_ANONYMOUS_TYPE.format(expected.jaxbType.getName(), child.getClass().getName(), actual.jaxbType.getName()), this.getCurrentLocation(fieldName)));
                    }
                    else {
                        this.getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
                        this.getNamespaceContext().declareNamespace(actualTypeName.getNamespaceURI(), null, false);
                    }
                }
            }
            actual.serializeURIs(child, this);
            if (nillable) {
                this.getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
            }
            this.endNamespaceDecls(child);
            if (!asExpected) {
                this.attribute("http://www.w3.org/2001/XMLSchema-instance", "type", DatatypeConverter.printQName(actualTypeName, this.getNamespaceContext()));
            }
            actual.serializeAttributes(child, this);
            final boolean nilDefined = actual.isNilIncluded();
            if (nillable && !nilDefined) {
                this.attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
            }
            this.endAttributes();
            actual.serializeBody(child, this);
            if (actual.lookForLifecycleMethods()) {
                this.fireAfterMarshalEvents(actual, child);
            }
            this.cycleDetectionStack.pop();
        }
    }
    
    private void fireAfterMarshalEvents(final JaxBeanInfo beanInfo, final Object currentTarget) {
        if (beanInfo.hasAfterMarshalMethod()) {
            final Method m = beanInfo.getLifecycleMethods().afterMarshal;
            this.fireMarshalEvent(currentTarget, m);
        }
        final Marshaller.Listener externalListener = this.marshaller.getListener();
        if (externalListener != null) {
            externalListener.afterMarshal(currentTarget);
        }
    }
    
    private void fireBeforeMarshalEvents(final JaxBeanInfo beanInfo, final Object currentTarget) {
        if (beanInfo.hasBeforeMarshalMethod()) {
            final Method m = beanInfo.getLifecycleMethods().beforeMarshal;
            this.fireMarshalEvent(currentTarget, m);
        }
        final Marshaller.Listener externalListener = this.marshaller.getListener();
        if (externalListener != null) {
            externalListener.beforeMarshal(currentTarget);
        }
    }
    
    private void fireMarshalEvent(final Object target, final Method m) {
        try {
            m.invoke(target, this.marshaller);
        }
        catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void attWildcardAsURIs(final Map<QName, String> attributes, final String fieldName) {
        if (attributes == null) {
            return;
        }
        for (final Map.Entry<QName, String> e : attributes.entrySet()) {
            final QName n = e.getKey();
            final String nsUri = n.getNamespaceURI();
            if (nsUri.length() > 0) {
                String p = n.getPrefix();
                if (p.length() == 0) {
                    p = null;
                }
                this.nsContext.declareNsUri(nsUri, p, true);
            }
        }
    }
    
    public void attWildcardAsAttributes(final Map<QName, String> attributes, final String fieldName) throws SAXException {
        if (attributes == null) {
            return;
        }
        for (final Map.Entry<QName, String> e : attributes.entrySet()) {
            final QName n = e.getKey();
            this.attribute(n.getNamespaceURI(), n.getLocalPart(), e.getValue());
        }
    }
    
    public final void writeXsiNilTrue() throws SAXException, IOException, XMLStreamException {
        this.getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
        this.endNamespaceDecls(null);
        this.attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
        this.endAttributes();
    }
    
    public <E> void writeDom(final E element, final DomHandler<E, ?> domHandler, final Object parentBean, final String fieldName) throws SAXException {
        final Source source = domHandler.marshal(element, this);
        if (this.contentHandlerAdapter == null) {
            this.contentHandlerAdapter = new ContentHandlerAdaptor(this);
        }
        try {
            this.getIdentityTransformer().transform(source, new SAXResult(this.contentHandlerAdapter));
        }
        catch (final TransformerException e) {
            this.reportError(fieldName, e);
        }
    }
    
    public Transformer getIdentityTransformer() {
        if (this.identityTransformer == null) {
            this.identityTransformer = JAXBContextImpl.createTransformer(this.grammar.disableSecurityProcessing);
        }
        return this.identityTransformer;
    }
    
    public void setPrefixMapper(final NamespacePrefixMapper prefixMapper) {
        this.nsContext.setPrefixMapper(prefixMapper);
    }
    
    public void startDocument(XmlOutput out, final boolean fragment, final String schemaLocation, final String noNsSchemaLocation) throws IOException, SAXException, XMLStreamException {
        this.pushCoordinator();
        this.nsContext.reset();
        this.nse = this.nsContext.getCurrent();
        if (this.attachmentMarshaller != null && this.attachmentMarshaller.isXOPPackage()) {
            out = new MTOMXmlOutput(out);
        }
        this.out = out;
        this.objectsWithId.clear();
        this.idReferencedObjects.clear();
        this.textHasAlreadyPrinted = false;
        this.seenRoot = false;
        this.schemaLocation = schemaLocation;
        this.noNsSchemaLocation = noNsSchemaLocation;
        this.fragment = fragment;
        this.inlineBinaryFlag = false;
        this.expectedMimeType = null;
        this.cycleDetectionStack.reset();
        out.startDocument(this, fragment, this.knownUri2prefixIndexMap, this.nsContext);
    }
    
    public void endDocument() throws IOException, SAXException, XMLStreamException {
        this.out.endDocument(this.fragment);
    }
    
    public void close() {
        this.out = null;
        this.clearCurrentProperty();
        this.popCoordinator();
    }
    
    public void addInscopeBinding(final String nsUri, final String prefix) {
        this.nsContext.put(nsUri, prefix);
    }
    
    public String getXMIMEContentType() {
        final String v = this.grammar.getXMIMEContentType(this.cycleDetectionStack.peek());
        if (v != null) {
            return v;
        }
        if (this.expectedMimeType != null) {
            return this.expectedMimeType.toString();
        }
        return null;
    }
    
    private void startElement() {
        this.nse = this.nse.push();
        if (!this.seenRoot) {
            if (this.grammar.getXmlNsSet() != null) {
                for (final XmlNs xmlNs : this.grammar.getXmlNsSet()) {
                    this.nsContext.declareNsUri(xmlNs.namespaceURI(), (xmlNs.prefix() == null) ? "" : xmlNs.prefix(), xmlNs.prefix() != null);
                }
            }
            final String[] knownUris = this.nameList.namespaceURIs;
            for (int i = 0; i < knownUris.length; ++i) {
                this.knownUri2prefixIndexMap[i] = this.nsContext.declareNsUri(knownUris[i], null, this.nameList.nsUriCannotBeDefaulted[i]);
            }
            final String[] uris = this.nsContext.getPrefixMapper().getPreDeclaredNamespaceUris();
            if (uris != null) {
                for (final String uri : uris) {
                    if (uri != null) {
                        this.nsContext.declareNsUri(uri, null, false);
                    }
                }
            }
            final String[] pairs = this.nsContext.getPrefixMapper().getPreDeclaredNamespaceUris2();
            if (pairs != null) {
                for (int j = 0; j < pairs.length; j += 2) {
                    final String prefix = pairs[j];
                    final String nsUri = pairs[j + 1];
                    if (prefix != null && nsUri != null) {
                        this.nsContext.put(nsUri, prefix);
                    }
                }
            }
            if (this.schemaLocation != null || this.noNsSchemaLocation != null) {
                this.nsContext.declareNsUri("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
            }
        }
        this.nsContext.collectionMode = true;
        this.textHasAlreadyPrinted = false;
    }
    
    public MimeType setExpectedMimeType(final MimeType expectedMimeType) {
        final MimeType old = this.expectedMimeType;
        this.expectedMimeType = expectedMimeType;
        return old;
    }
    
    public boolean setInlineBinaryFlag(final boolean value) {
        final boolean old = this.inlineBinaryFlag;
        this.inlineBinaryFlag = value;
        return old;
    }
    
    public boolean getInlineBinaryFlag() {
        return this.inlineBinaryFlag;
    }
    
    public QName setSchemaType(final QName st) {
        final QName old = this.schemaType;
        this.schemaType = st;
        return old;
    }
    
    public QName getSchemaType() {
        return this.schemaType;
    }
    
    public void setObjectIdentityCycleDetection(final boolean val) {
        this.cycleDetectionStack.setUseIdentity(val);
    }
    
    public boolean getObjectIdentityCycleDetection() {
        return this.cycleDetectionStack.getUseIdentity();
    }
    
    void reconcileID() throws SAXException {
        this.idReferencedObjects.removeAll(this.objectsWithId);
        for (final Object idObj : this.idReferencedObjects) {
            try {
                final String id = this.getIdFromObject(idObj);
                this.reportError(new NotIdentifiableEventImpl(1, Messages.DANGLING_IDREF.format(id), new ValidationEventLocatorImpl(idObj)));
            }
            catch (final JAXBException ex) {}
        }
        this.idReferencedObjects.clear();
        this.objectsWithId.clear();
    }
    
    public boolean handleError(final Exception e) {
        return this.handleError(e, this.cycleDetectionStack.peek(), null);
    }
    
    public boolean handleError(final Exception e, final Object source, final String fieldName) {
        return this.handleEvent(new ValidationEventImpl(1, e.getMessage(), new ValidationEventLocatorExImpl(source, fieldName), e));
    }
    
    @Override
    public boolean handleEvent(final ValidationEvent event) {
        try {
            return this.marshaller.getEventHandler().handleEvent(event);
        }
        catch (final JAXBException e) {
            throw new Error(e);
        }
    }
    
    private void reportMissingObjectError(final String fieldName) throws SAXException {
        this.reportError(new ValidationEventImpl(1, Messages.MISSING_OBJECT.format(fieldName), this.getCurrentLocation(fieldName), new NullPointerException()));
    }
    
    public void errorMissingId(final Object obj) throws SAXException {
        this.reportError(new ValidationEventImpl(1, Messages.MISSING_ID.format(obj), new ValidationEventLocatorImpl(obj)));
    }
    
    public ValidationEventLocator getCurrentLocation(final String fieldName) {
        return new ValidationEventLocatorExImpl(this.cycleDetectionStack.peek(), fieldName);
    }
    
    @Override
    protected ValidationEventLocator getLocation() {
        return this.getCurrentLocation(null);
    }
    
    public Property getCurrentProperty() {
        return this.currentProperty.get();
    }
    
    public void clearCurrentProperty() {
        if (this.currentProperty != null) {
            this.currentProperty.remove();
        }
    }
    
    public static XMLSerializer getInstance() {
        return (XMLSerializer)Coordinator._getInstance();
    }
}
