package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.JAXBElement;
import org.xml.sax.helpers.LocatorImpl;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import java.util.Collection;
import com.sun.xml.internal.bind.api.AccessorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.Locator;
import com.sun.istack.internal.SAXParseException2;
import javax.xml.bind.ValidationEvent;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import javax.xml.bind.UnmarshalException;
import java.lang.reflect.Method;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.util.HashMap;
import java.util.Map;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.istack.internal.NotNull;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;

public final class UnmarshallingContext extends Coordinator implements NamespaceContext, ValidationEventHandler, ErrorHandler, XmlVisitor, TextPredictor
{
    private static final Logger logger;
    private final State root;
    private State current;
    private static final LocatorEx DUMMY_INSTANCE;
    @NotNull
    private LocatorEx locator;
    private Object result;
    private JaxBeanInfo expectedType;
    private IDResolver idResolver;
    private boolean isUnmarshalInProgress;
    private boolean aborted;
    public final UnmarshallerImpl parent;
    private final AssociationMap assoc;
    private boolean isInplaceMode;
    private InfosetScanner scanner;
    private Object currentElement;
    private NamespaceContext environmentNamespaceContext;
    @Nullable
    public ClassResolver classResolver;
    @Nullable
    public ClassLoader classLoader;
    private static volatile int errorsCounter;
    private final Map<Class, Factory> factories;
    private Patcher[] patchers;
    private int patchersLen;
    private String[] nsBind;
    private int nsLen;
    private Scope[] scopes;
    private int scopeTop;
    private static final Loader DEFAULT_ROOT_LOADER;
    private static final Loader EXPECTED_TYPE_ROOT_LOADER;
    
    public UnmarshallingContext(final UnmarshallerImpl _parent, final AssociationMap assoc) {
        this.locator = UnmarshallingContext.DUMMY_INSTANCE;
        this.isUnmarshalInProgress = true;
        this.aborted = false;
        this.factories = new HashMap<Class, Factory>();
        this.patchers = null;
        this.patchersLen = 0;
        this.nsBind = new String[16];
        this.nsLen = 0;
        this.scopes = new Scope[16];
        this.scopeTop = 0;
        for (int i = 0; i < this.scopes.length; ++i) {
            this.scopes[i] = new Scope(this);
        }
        this.parent = _parent;
        this.assoc = assoc;
        final State state = new State((State)null);
        this.current = state;
        this.root = state;
    }
    
    public void reset(final InfosetScanner scanner, final boolean isInplaceMode, final JaxBeanInfo expectedType, final IDResolver idResolver) {
        this.scanner = scanner;
        this.isInplaceMode = isInplaceMode;
        this.expectedType = expectedType;
        this.idResolver = idResolver;
    }
    
    public JAXBContextImpl getJAXBContext() {
        return this.parent.context;
    }
    
    public State getCurrentState() {
        return this.current;
    }
    
    public Loader selectRootLoader(final State state, final TagName tag) throws SAXException {
        try {
            final Loader l = this.getJAXBContext().selectRootLoader(state, tag);
            if (l != null) {
                return l;
            }
            if (this.classResolver != null) {
                final Class<?> clazz = this.classResolver.resolveElementName(tag.uri, tag.local);
                if (clazz != null) {
                    final JAXBContextImpl enhanced = this.getJAXBContext().createAugmented(clazz);
                    final JaxBeanInfo<?> bi = enhanced.getBeanInfo(clazz);
                    return bi.getLoader(enhanced, true);
                }
            }
        }
        catch (final RuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.handleError(e2);
        }
        return null;
    }
    
    public void clearStates() {
        State last;
        for (last = this.current; last.next != null; last = last.next) {}
        while (last.prev != null) {
            last.loader = null;
            last.nil = false;
            last.receiver = null;
            last.intercepter = null;
            last.elementDefaultValue = null;
            last.target = null;
            last = last.prev;
            last.next.prev = null;
            last.next = null;
        }
        this.current = last;
    }
    
    public void setFactories(final Object factoryInstances) {
        this.factories.clear();
        if (factoryInstances == null) {
            return;
        }
        if (factoryInstances instanceof Object[]) {
            for (final Object factory : (Object[])factoryInstances) {
                this.addFactory(factory);
            }
        }
        else {
            this.addFactory(factoryInstances);
        }
    }
    
    private void addFactory(final Object factory) {
        for (final Method m : factory.getClass().getMethods()) {
            if (m.getName().startsWith("create")) {
                if (m.getParameterTypes().length <= 0) {
                    final Class type = m.getReturnType();
                    this.factories.put(type, new Factory(factory, m));
                }
            }
        }
    }
    
    @Override
    public void startDocument(final LocatorEx locator, final NamespaceContext nsContext) throws SAXException {
        if (locator != null) {
            this.locator = locator;
        }
        this.environmentNamespaceContext = nsContext;
        this.result = null;
        this.current = this.root;
        this.patchersLen = 0;
        this.aborted = false;
        this.isUnmarshalInProgress = true;
        this.nsLen = 0;
        if (this.expectedType != null) {
            this.root.loader = UnmarshallingContext.EXPECTED_TYPE_ROOT_LOADER;
        }
        else {
            this.root.loader = UnmarshallingContext.DEFAULT_ROOT_LOADER;
        }
        this.idResolver.startDocument(this);
    }
    
    @Override
    public void startElement(final TagName tagName) throws SAXException {
        this.pushCoordinator();
        try {
            this._startElement(tagName);
        }
        finally {
            this.popCoordinator();
        }
    }
    
    private void _startElement(final TagName tagName) throws SAXException {
        if (this.assoc != null) {
            this.currentElement = this.scanner.getCurrentElement();
        }
        final Loader h = this.current.loader;
        this.current.push();
        h.childElement(this.current, tagName);
        assert this.current.loader != null;
        this.current.loader.startElement(this.current, tagName);
    }
    
    @Override
    public void text(CharSequence pcdata) throws SAXException {
        this.pushCoordinator();
        try {
            if (this.current.elementDefaultValue != null && pcdata.length() == 0) {
                pcdata = this.current.elementDefaultValue;
            }
            this.current.loader.text(this.current, pcdata);
        }
        finally {
            this.popCoordinator();
        }
    }
    
    @Override
    public final void endElement(final TagName tagName) throws SAXException {
        this.pushCoordinator();
        try {
            final State child = this.current;
            child.loader.leaveElement(child, tagName);
            Object target = child.target;
            final Receiver recv = child.receiver;
            final Intercepter intercepter = child.intercepter;
            child.pop();
            if (intercepter != null) {
                target = intercepter.intercept(this.current, target);
            }
            if (recv != null) {
                recv.receive(this.current, target);
            }
        }
        finally {
            this.popCoordinator();
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.runPatchers();
        this.idResolver.endDocument();
        this.isUnmarshalInProgress = false;
        this.currentElement = null;
        this.locator = UnmarshallingContext.DUMMY_INSTANCE;
        this.environmentNamespaceContext = null;
        assert this.root == this.current;
    }
    
    @Deprecated
    @Override
    public boolean expectText() {
        return this.current.loader.expectText;
    }
    
    @Deprecated
    @Override
    public TextPredictor getPredictor() {
        return this;
    }
    
    @Override
    public UnmarshallingContext getContext() {
        return this;
    }
    
    public Object getResult() throws UnmarshalException {
        if (this.isUnmarshalInProgress) {
            throw new IllegalStateException();
        }
        if (!this.aborted) {
            return this.result;
        }
        throw new UnmarshalException((String)null);
    }
    
    void clearResult() {
        if (this.isUnmarshalInProgress) {
            throw new IllegalStateException();
        }
        this.result = null;
    }
    
    public Object createInstance(final Class<?> clazz) throws SAXException {
        if (!this.factories.isEmpty()) {
            final Factory factory = this.factories.get(clazz);
            if (factory != null) {
                return factory.createInstance();
            }
        }
        return ClassFactory.create(clazz);
    }
    
    public Object createInstance(final JaxBeanInfo beanInfo) throws SAXException {
        if (!this.factories.isEmpty()) {
            final Factory factory = this.factories.get(beanInfo.jaxbType);
            if (factory != null) {
                return factory.createInstance();
            }
        }
        try {
            return beanInfo.createInstance(this);
        }
        catch (final IllegalAccessException e) {
            Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e, false);
        }
        catch (final InvocationTargetException e2) {
            Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e2, false);
        }
        catch (final InstantiationException e3) {
            Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e3, false);
        }
        return null;
    }
    
    public void handleEvent(final ValidationEvent event, final boolean canRecover) throws SAXException {
        final ValidationEventHandler eventHandler = this.parent.getEventHandler();
        final boolean recover = eventHandler.handleEvent(event);
        if (!recover) {
            this.aborted = true;
        }
        if (!canRecover || !recover) {
            throw new SAXParseException2(event.getMessage(), this.locator, new UnmarshalException(event.getMessage(), event.getLinkedException()));
        }
    }
    
    @Override
    public boolean handleEvent(final ValidationEvent event) {
        try {
            final boolean recover = this.parent.getEventHandler().handleEvent(event);
            if (!recover) {
                this.aborted = true;
            }
            return recover;
        }
        catch (final RuntimeException re) {
            return false;
        }
    }
    
    public void handleError(final Exception e) throws SAXException {
        this.handleError(e, true);
    }
    
    public void handleError(final Exception e, final boolean canRecover) throws SAXException {
        this.handleEvent(new ValidationEventImpl(1, e.getMessage(), this.locator.getLocation(), e), canRecover);
    }
    
    public void handleError(final String msg) {
        this.handleEvent(new ValidationEventImpl(1, msg, this.locator.getLocation()));
    }
    
    @Override
    protected ValidationEventLocator getLocation() {
        return this.locator.getLocation();
    }
    
    public LocatorEx getLocator() {
        return this.locator;
    }
    
    public void errorUnresolvedIDREF(final Object bean, final String idref, final LocatorEx loc) throws SAXException {
        this.handleEvent(new ValidationEventImpl(1, Messages.UNRESOLVED_IDREF.format(idref), loc.getLocation()), true);
    }
    
    public void addPatcher(final Patcher job) {
        if (this.patchers == null) {
            this.patchers = new Patcher[32];
        }
        if (this.patchers.length == this.patchersLen) {
            final Patcher[] buf = new Patcher[this.patchersLen * 2];
            System.arraycopy(this.patchers, 0, buf, 0, this.patchersLen);
            this.patchers = buf;
        }
        this.patchers[this.patchersLen++] = job;
    }
    
    private void runPatchers() throws SAXException {
        if (this.patchers != null) {
            for (int i = 0; i < this.patchersLen; ++i) {
                this.patchers[i].run();
                this.patchers[i] = null;
            }
        }
    }
    
    public String addToIdTable(final String id) throws SAXException {
        Object o = this.current.target;
        if (o == null) {
            o = this.current.prev.target;
        }
        this.idResolver.bind(id, o);
        return id;
    }
    
    public Callable getObjectFromId(final String id, final Class targetType) throws SAXException {
        return this.idResolver.resolve(id, targetType);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        if (this.nsBind.length == this.nsLen) {
            final String[] n = new String[this.nsLen * 2];
            System.arraycopy(this.nsBind, 0, n, 0, this.nsLen);
            this.nsBind = n;
        }
        this.nsBind[this.nsLen++] = prefix;
        this.nsBind[this.nsLen++] = uri;
    }
    
    @Override
    public void endPrefixMapping(final String prefix) {
        this.nsLen -= 2;
    }
    
    private String resolveNamespacePrefix(final String prefix) {
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (prefix.equals(this.nsBind[i])) {
                return this.nsBind[i + 1];
            }
        }
        if (this.environmentNamespaceContext != null) {
            return this.environmentNamespaceContext.getNamespaceURI(prefix.intern());
        }
        if (prefix.equals("")) {
            return "";
        }
        return null;
    }
    
    public String[] getNewlyDeclaredPrefixes() {
        return this.getPrefixList(this.current.prev.numNsDecl);
    }
    
    public String[] getAllDeclaredPrefixes() {
        return this.getPrefixList(0);
    }
    
    private String[] getPrefixList(final int startIndex) {
        final int size = (this.current.numNsDecl - startIndex) / 2;
        final String[] r = new String[size];
        for (int i = 0; i < r.length; ++i) {
            r[i] = this.nsBind[startIndex + i * 2];
        }
        return r;
    }
    
    @Override
    public Iterator<String> getPrefixes(final String uri) {
        return Collections.unmodifiableList((List<? extends String>)this.getAllPrefixesInList(uri)).iterator();
    }
    
    private List<String> getAllPrefixesInList(final String uri) {
        final List<String> a = new ArrayList<String>();
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            a.add("xml");
            return a;
        }
        if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            a.add("xmlns");
            return a;
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
                a.add(this.nsBind[i]);
            }
        }
        return a;
    }
    
    @Override
    public String getPrefix(final String uri) {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
                return this.nsBind[i];
            }
        }
        if (this.environmentNamespaceContext != null) {
            return this.environmentNamespaceContext.getPrefix(uri);
        }
        return null;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return this.resolveNamespacePrefix(prefix);
    }
    
    public void startScope(final int frameSize) {
        this.scopeTop += frameSize;
        if (this.scopeTop >= this.scopes.length) {
            final Scope[] s = new Scope[Math.max(this.scopeTop + 1, this.scopes.length * 2)];
            System.arraycopy(this.scopes, 0, s, 0, this.scopes.length);
            for (int i = this.scopes.length; i < s.length; ++i) {
                s[i] = new Scope(this);
            }
            this.scopes = s;
        }
    }
    
    public void endScope(int frameSize) throws SAXException {
        try {
            while (frameSize > 0) {
                this.scopes[this.scopeTop].finish();
                --frameSize;
                --this.scopeTop;
            }
        }
        catch (final AccessorException e) {
            this.handleError(e);
            while (frameSize > 0) {
                this.scopes[this.scopeTop--] = new Scope(this);
                --frameSize;
            }
        }
    }
    
    public Scope getScope(final int offset) {
        return this.scopes[this.scopeTop - offset];
    }
    
    public void recordInnerPeer(final Object innerPeer) {
        if (this.assoc != null) {
            this.assoc.addInner(this.currentElement, innerPeer);
        }
    }
    
    public Object getInnerPeer() {
        if (this.assoc != null && this.isInplaceMode) {
            return this.assoc.getInnerPeer(this.currentElement);
        }
        return null;
    }
    
    public void recordOuterPeer(final Object outerPeer) {
        if (this.assoc != null) {
            this.assoc.addOuter(this.currentElement, outerPeer);
        }
    }
    
    public Object getOuterPeer() {
        if (this.assoc != null && this.isInplaceMode) {
            return this.assoc.getOuterPeer(this.currentElement);
        }
        return null;
    }
    
    public String getXMIMEContentType() {
        final Object t = this.current.target;
        if (t == null) {
            return null;
        }
        return this.getJAXBContext().getXMIMEContentType(t);
    }
    
    public static UnmarshallingContext getInstance() {
        return (UnmarshallingContext)Coordinator._getInstance();
    }
    
    public Collection<QName> getCurrentExpectedElements() {
        this.pushCoordinator();
        try {
            final State s = this.getCurrentState();
            final Loader l = s.loader;
            return (l != null) ? l.getExpectedChildElements() : null;
        }
        finally {
            this.popCoordinator();
        }
    }
    
    public Collection<QName> getCurrentExpectedAttributes() {
        this.pushCoordinator();
        try {
            final State s = this.getCurrentState();
            final Loader l = s.loader;
            return (l != null) ? l.getExpectedAttributes() : null;
        }
        finally {
            this.popCoordinator();
        }
    }
    
    public StructureLoader getStructureLoader() {
        if (this.current.loader instanceof StructureLoader) {
            return (StructureLoader)this.current.loader;
        }
        return null;
    }
    
    public boolean shouldErrorBeReported() throws SAXException {
        if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
            return true;
        }
        if (UnmarshallingContext.errorsCounter >= 0) {
            --UnmarshallingContext.errorsCounter;
            if (UnmarshallingContext.errorsCounter == 0) {
                this.handleEvent(new ValidationEventImpl(0, Messages.ERRORS_LIMIT_EXCEEDED.format(new Object[0]), this.getLocator().getLocation(), null), true);
            }
        }
        return UnmarshallingContext.errorsCounter >= 0;
    }
    
    static {
        logger = Logger.getLogger(UnmarshallingContext.class.getName());
        final LocatorImpl loc = new LocatorImpl();
        loc.setPublicId(null);
        loc.setSystemId(null);
        loc.setLineNumber(-1);
        loc.setColumnNumber(-1);
        DUMMY_INSTANCE = new LocatorExWrapper(loc);
        UnmarshallingContext.errorsCounter = 10;
        DEFAULT_ROOT_LOADER = new DefaultRootLoader();
        EXPECTED_TYPE_ROOT_LOADER = new ExpectedTypeRootLoader();
    }
    
    public final class State
    {
        private Loader loader;
        private Receiver receiver;
        private Intercepter intercepter;
        private Object target;
        private Object backup;
        private int numNsDecl;
        private String elementDefaultValue;
        private State prev;
        private State next;
        private boolean nil;
        private boolean mixed;
        
        public UnmarshallingContext getContext() {
            return UnmarshallingContext.this;
        }
        
        private State(final State prev) {
            this.nil = false;
            this.mixed = false;
            this.prev = prev;
            if (prev != null) {
                prev.next = this;
                if (prev.mixed) {
                    this.mixed = true;
                }
            }
        }
        
        private void push() {
            if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
                UnmarshallingContext.logger.log(Level.FINEST, "State.push");
            }
            if (this.next == null) {
                assert UnmarshallingContext.this.current == this;
                this.next = new State(this);
            }
            this.nil = false;
            final State n = this.next;
            n.numNsDecl = UnmarshallingContext.this.nsLen;
            UnmarshallingContext.this.current = n;
        }
        
        private void pop() {
            if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
                UnmarshallingContext.logger.log(Level.FINEST, "State.pop");
            }
            assert this.prev != null;
            this.loader = null;
            this.nil = false;
            this.mixed = false;
            this.receiver = null;
            this.intercepter = null;
            this.elementDefaultValue = null;
            this.target = null;
            UnmarshallingContext.this.current = this.prev;
            this.next = null;
        }
        
        public boolean isMixed() {
            return this.mixed;
        }
        
        public Object getTarget() {
            return this.target;
        }
        
        public void setLoader(final Loader loader) {
            if (loader instanceof StructureLoader) {
                this.mixed = !((StructureLoader)loader).getBeanInfo().hasElementOnlyContentModel();
            }
            this.loader = loader;
        }
        
        public void setReceiver(final Receiver receiver) {
            this.receiver = receiver;
        }
        
        public State getPrev() {
            return this.prev;
        }
        
        public void setIntercepter(final Intercepter intercepter) {
            this.intercepter = intercepter;
        }
        
        public void setBackup(final Object backup) {
            this.backup = backup;
        }
        
        public void setTarget(final Object target) {
            this.target = target;
        }
        
        public Object getBackup() {
            return this.backup;
        }
        
        public boolean isNil() {
            return this.nil;
        }
        
        public void setNil(final boolean nil) {
            this.nil = nil;
        }
        
        public Loader getLoader() {
            return this.loader;
        }
        
        public String getElementDefaultValue() {
            return this.elementDefaultValue;
        }
        
        public void setElementDefaultValue(final String elementDefaultValue) {
            this.elementDefaultValue = elementDefaultValue;
        }
    }
    
    private static class Factory
    {
        private final Object factorInstance;
        private final Method method;
        
        public Factory(final Object factorInstance, final Method method) {
            this.factorInstance = factorInstance;
            this.method = method;
        }
        
        public Object createInstance() throws SAXException {
            try {
                return this.method.invoke(this.factorInstance, new Object[0]);
            }
            catch (final IllegalAccessException e) {
                UnmarshallingContext.getInstance().handleError(e, false);
            }
            catch (final InvocationTargetException e2) {
                UnmarshallingContext.getInstance().handleError(e2, false);
            }
            return null;
        }
    }
    
    private static final class DefaultRootLoader extends Loader implements Receiver
    {
        @Override
        public void childElement(final State state, final TagName ea) throws SAXException {
            final Loader loader = state.getContext().selectRootLoader(state, ea);
            if (loader != null) {
                state.loader = loader;
                state.receiver = this;
                return;
            }
            final JaxBeanInfo beanInfo = XsiTypeLoader.parseXsiType(state, ea, null);
            if (beanInfo == null) {
                this.reportUnexpectedChildElement(ea, false);
                return;
            }
            state.loader = beanInfo.getLoader(null, false);
            state.prev.backup = new JAXBElement(ea.createQName(), Object.class, null);
            state.receiver = this;
        }
        
        @Override
        public Collection<QName> getExpectedChildElements() {
            return UnmarshallingContext.getInstance().getJAXBContext().getValidRootNames();
        }
        
        @Override
        public void receive(final State state, Object o) {
            if (state.backup != null) {
                ((JAXBElement)state.backup).setValue(o);
                o = state.backup;
            }
            if (state.nil) {
                ((JAXBElement)o).setNil(true);
            }
            state.getContext().result = o;
        }
    }
    
    private static final class ExpectedTypeRootLoader extends Loader implements Receiver
    {
        @Override
        public void childElement(final State state, final TagName ea) {
            final UnmarshallingContext context = state.getContext();
            final QName qn = new QName(ea.uri, ea.local);
            state.prev.target = new JAXBElement(qn, (Class<Object>)context.expectedType.jaxbType, null, null);
            state.receiver = this;
            state.loader = new XsiNilLoader(context.expectedType.getLoader(null, true));
        }
        
        @Override
        public void receive(final State state, final Object o) {
            final JAXBElement e = (JAXBElement)state.target;
            e.setValue(o);
            state.getContext().recordOuterPeer(e);
            state.getContext().result = e;
        }
    }
}
