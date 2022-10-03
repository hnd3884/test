package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.impl.ModelBuilder;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import java.util.Collections;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.util.Which;
import java.util.List;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import org.w3c.dom.Node;
import javax.xml.bind.Binder;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
import com.sun.xml.internal.bind.api.ErrorListener;
import org.xml.sax.SAXParseException;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import com.sun.xml.internal.txw2.output.ResultFactory;
import javax.xml.transform.Result;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Validator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.TransformerConfigurationException;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.Transformer;
import java.util.TreeSet;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAttachmentRef;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.xml.internal.bind.api.CompositeStructure;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import java.util.LinkedHashMap;
import java.util.Comparator;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import javax.xml.bind.annotation.XmlNs;
import java.util.Set;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import java.lang.ref.WeakReference;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import com.sun.istack.internal.Pool;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import javax.xml.namespace.QName;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javax.xml.parsers.DocumentBuilder;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import java.util.Map;
import com.sun.xml.internal.bind.api.JAXBRIContext;

public final class JAXBContextImpl extends JAXBRIContext
{
    private final Map<TypeReference, Bridge> bridges;
    private static DocumentBuilder db;
    private final QNameMap<JaxBeanInfo> rootMap;
    private final HashMap<QName, JaxBeanInfo> typeMap;
    private final Map<Class, JaxBeanInfo> beanInfoMap;
    protected Map<RuntimeTypeInfo, JaxBeanInfo> beanInfos;
    private final Map<Class, Map<QName, ElementBeanInfoImpl>> elements;
    public final Pool<Marshaller> marshallerPool;
    public final Pool<Unmarshaller> unmarshallerPool;
    public NameBuilder nameBuilder;
    public final NameList nameList;
    private final String defaultNsUri;
    private final Class[] classes;
    protected final boolean c14nSupport;
    public final boolean xmlAccessorFactorySupport;
    public final boolean allNillable;
    public final boolean retainPropertyInfo;
    public final boolean supressAccessorWarnings;
    public final boolean improvedXsiTypeHandling;
    public final boolean disableSecurityProcessing;
    private WeakReference<RuntimeTypeInfoSet> typeInfoSetCache;
    @NotNull
    private RuntimeAnnotationReader annotationReader;
    private boolean hasSwaRef;
    @NotNull
    private final Map<Class, Class> subclassReplacements;
    public final boolean fastBoot;
    private Set<XmlNs> xmlNsSet;
    private Encoded[] utf8nameTable;
    private static final Comparator<QName> QNAME_COMPARATOR;
    
    public Set<XmlNs> getXmlNsSet() {
        return this.xmlNsSet;
    }
    
    private JAXBContextImpl(final JAXBContextBuilder builder) throws JAXBException {
        this.bridges = new LinkedHashMap<TypeReference, Bridge>();
        this.rootMap = new QNameMap<JaxBeanInfo>();
        this.typeMap = new HashMap<QName, JaxBeanInfo>();
        this.beanInfoMap = new LinkedHashMap<Class, JaxBeanInfo>();
        this.beanInfos = new LinkedHashMap<RuntimeTypeInfo, JaxBeanInfo>();
        this.elements = new LinkedHashMap<Class, Map<QName, ElementBeanInfoImpl>>();
        this.marshallerPool = new Pool.Impl<Marshaller>() {
            @NotNull
            @Override
            protected Marshaller create() {
                return JAXBContextImpl.this.createMarshaller();
            }
        };
        this.unmarshallerPool = new Pool.Impl<Unmarshaller>() {
            @NotNull
            @Override
            protected Unmarshaller create() {
                return JAXBContextImpl.this.createUnmarshaller();
            }
        };
        this.nameBuilder = new NameBuilder();
        this.xmlNsSet = null;
        this.defaultNsUri = builder.defaultNsUri;
        this.retainPropertyInfo = builder.retainPropertyInfo;
        this.annotationReader = builder.annotationReader;
        this.subclassReplacements = builder.subclassReplacements;
        this.c14nSupport = builder.c14nSupport;
        this.classes = builder.classes;
        this.xmlAccessorFactorySupport = builder.xmlAccessorFactorySupport;
        this.allNillable = builder.allNillable;
        this.supressAccessorWarnings = builder.supressAccessorWarnings;
        this.improvedXsiTypeHandling = builder.improvedXsiTypeHandling;
        this.disableSecurityProcessing = builder.disableSecurityProcessing;
        final Collection<TypeReference> typeRefs = builder.typeRefs;
        boolean fastB;
        try {
            fastB = Boolean.getBoolean(JAXBContextImpl.class.getName() + ".fastBoot");
        }
        catch (final SecurityException e) {
            fastB = false;
        }
        this.fastBoot = fastB;
        final RuntimeTypeInfoSet typeSet = this.getTypeInfoSet();
        this.elements.put(null, new LinkedHashMap<QName, ElementBeanInfoImpl>());
        for (final RuntimeBuiltinLeafInfo leaf : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos) {
            final LeafBeanInfoImpl<?> bi = new LeafBeanInfoImpl<Object>(this, leaf);
            this.beanInfoMap.put(leaf.getClazz(), bi);
            for (final QName t : bi.getTypeNames()) {
                this.typeMap.put(t, bi);
            }
        }
        for (final RuntimeEnumLeafInfo e2 : typeSet.enums().values()) {
            final JaxBeanInfo<?> bi2 = this.getOrCreate(e2);
            for (final QName qn : bi2.getTypeNames()) {
                this.typeMap.put(qn, bi2);
            }
            if (e2.isElement()) {
                this.rootMap.put(e2.getElementName(), bi2);
            }
        }
        for (final RuntimeArrayInfo a : typeSet.arrays().values()) {
            final JaxBeanInfo<?> ai = this.getOrCreate(a);
            for (final QName qn : ai.getTypeNames()) {
                this.typeMap.put(qn, ai);
            }
        }
        for (final Map.Entry<Class, ? extends RuntimeClassInfo> e3 : typeSet.beans().entrySet()) {
            final ClassBeanInfoImpl<?> bi3 = this.getOrCreate((RuntimeClassInfo)e3.getValue());
            final XmlSchema xs = ((AnnotationReader<T, Class, F, M>)this.annotationReader).getPackageAnnotation(XmlSchema.class, e3.getKey(), null);
            if (xs != null && xs.xmlns() != null && xs.xmlns().length > 0) {
                if (this.xmlNsSet == null) {
                    this.xmlNsSet = new HashSet<XmlNs>();
                }
                this.xmlNsSet.addAll(Arrays.asList(xs.xmlns()));
            }
            if (bi3.isElement()) {
                this.rootMap.put(((RuntimeClassInfo)e3.getValue()).getElementName(), bi3);
            }
            for (final QName qn2 : bi3.getTypeNames()) {
                this.typeMap.put(qn2, bi3);
            }
        }
        for (final RuntimeElementInfo n : typeSet.getAllElements()) {
            final ElementBeanInfoImpl bi4 = this.getOrCreate(n);
            if (n.getScope() == null) {
                this.rootMap.put(n.getElementName(), bi4);
            }
            final RuntimeClassInfo scope = n.getScope();
            final Class scopeClazz = (scope == null) ? null : ((ClassInfo<T, Class>)scope).getClazz();
            Map<QName, ElementBeanInfoImpl> m = this.elements.get(scopeClazz);
            if (m == null) {
                m = new LinkedHashMap<QName, ElementBeanInfoImpl>();
                this.elements.put(scopeClazz, m);
            }
            m.put(n.getElementName(), bi4);
        }
        this.beanInfoMap.put(JAXBElement.class, new ElementBeanInfoImpl(this));
        this.beanInfoMap.put(CompositeStructure.class, new CompositeStructureBeanInfo(this));
        this.getOrCreate(typeSet.getAnyTypeInfo());
        for (final JaxBeanInfo bi5 : this.beanInfos.values()) {
            bi5.link(this);
        }
        for (final Map.Entry<Class, Class> e4 : RuntimeUtil.primitiveToBox.entrySet()) {
            this.beanInfoMap.put(e4.getKey(), this.beanInfoMap.get(e4.getValue()));
        }
        final Navigator<Type, Class, Field, Method> nav = (Navigator<Type, Class, Field, Method>)typeSet.getNavigator();
        for (final TypeReference tr : typeRefs) {
            final XmlJavaTypeAdapter xjta = tr.get(XmlJavaTypeAdapter.class);
            Adapter<Type, Class> a2 = null;
            final XmlList xl = tr.get(XmlList.class);
            Class erasedType = nav.erasure(tr.type);
            if (xjta != null) {
                a2 = new Adapter<Type, Class>(xjta.value(), nav);
            }
            if (tr.get(XmlAttachmentRef.class) != null) {
                a2 = new Adapter<Type, Class>(SwaRefAdapter.class, nav);
                this.hasSwaRef = true;
            }
            if (a2 != null) {
                erasedType = nav.erasure(a2.defaultType);
            }
            final Name name = this.nameBuilder.createElementName(tr.tagName);
            InternalBridge bridge;
            if (xl == null) {
                bridge = new BridgeImpl(this, name, this.getBeanInfo((Class<Object>)erasedType, true), tr);
            }
            else {
                bridge = new BridgeImpl(this, name, new ValueListBeanInfoImpl(this, erasedType), tr);
            }
            if (a2 != null) {
                bridge = new BridgeAdapter(bridge, a2.adapterType);
            }
            this.bridges.put(tr, bridge);
        }
        this.nameList = this.nameBuilder.conclude();
        for (final JaxBeanInfo bi6 : this.beanInfos.values()) {
            bi6.wrapUp();
        }
        this.nameBuilder = null;
        this.beanInfos = null;
    }
    
    @Override
    public boolean hasSwaRef() {
        return this.hasSwaRef;
    }
    
    @Override
    public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
        try {
            return this.getTypeInfoSet();
        }
        catch (final IllegalAnnotationsException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public RuntimeTypeInfoSet getTypeInfoSet() throws IllegalAnnotationsException {
        if (this.typeInfoSetCache != null) {
            final RuntimeTypeInfoSet r = this.typeInfoSetCache.get();
            if (r != null) {
                return r;
            }
        }
        final RuntimeModelBuilder builder = new RuntimeModelBuilder(this, this.annotationReader, this.subclassReplacements, this.defaultNsUri);
        final IllegalAnnotationsException.Builder errorHandler = new IllegalAnnotationsException.Builder();
        builder.setErrorHandler(errorHandler);
        for (final Class c : this.classes) {
            if (c != CompositeStructure.class) {
                ((ModelBuilder<Type, Class, F, M>)builder).getTypeInfo(new Ref<Type, Class>(c));
            }
        }
        this.hasSwaRef |= builder.hasSwaRef;
        final RuntimeTypeInfoSet r2 = builder.link();
        errorHandler.check();
        assert r2 != null : "if no error was reported, the link must be a success";
        this.typeInfoSetCache = new WeakReference<RuntimeTypeInfoSet>(r2);
        return r2;
    }
    
    public ElementBeanInfoImpl getElement(final Class scope, final QName name) {
        Map<QName, ElementBeanInfoImpl> m = this.elements.get(scope);
        if (m != null) {
            final ElementBeanInfoImpl bi = m.get(name);
            if (bi != null) {
                return bi;
            }
        }
        m = this.elements.get(null);
        return m.get(name);
    }
    
    private ElementBeanInfoImpl getOrCreate(final RuntimeElementInfo rei) {
        final JaxBeanInfo bi = this.beanInfos.get(rei);
        if (bi != null) {
            return (ElementBeanInfoImpl)bi;
        }
        return new ElementBeanInfoImpl(this, rei);
    }
    
    protected JaxBeanInfo getOrCreate(final RuntimeEnumLeafInfo eli) {
        JaxBeanInfo bi = this.beanInfos.get(eli);
        if (bi != null) {
            return bi;
        }
        bi = new LeafBeanInfoImpl(this, eli);
        this.beanInfoMap.put(bi.jaxbType, bi);
        return bi;
    }
    
    protected ClassBeanInfoImpl getOrCreate(final RuntimeClassInfo ci) {
        ClassBeanInfoImpl bi = this.beanInfos.get(ci);
        if (bi != null) {
            return bi;
        }
        bi = new ClassBeanInfoImpl(this, ci);
        this.beanInfoMap.put(bi.jaxbType, bi);
        return bi;
    }
    
    protected JaxBeanInfo getOrCreate(final RuntimeArrayInfo ai) {
        JaxBeanInfo abi = this.beanInfos.get(ai);
        if (abi != null) {
            return abi;
        }
        abi = new ArrayBeanInfoImpl(this, ai);
        this.beanInfoMap.put(ai.getType(), abi);
        return abi;
    }
    
    public JaxBeanInfo getOrCreate(final RuntimeTypeInfo e) {
        if (e instanceof RuntimeElementInfo) {
            return this.getOrCreate((RuntimeElementInfo)e);
        }
        if (e instanceof RuntimeClassInfo) {
            return this.getOrCreate((RuntimeClassInfo)e);
        }
        if (e instanceof RuntimeLeafInfo) {
            final JaxBeanInfo bi = this.beanInfos.get(e);
            assert bi != null;
            return bi;
        }
        else {
            if (e instanceof RuntimeArrayInfo) {
                return this.getOrCreate((RuntimeArrayInfo)e);
            }
            if (e.getType() == Object.class) {
                JaxBeanInfo bi = this.beanInfoMap.get(Object.class);
                if (bi == null) {
                    bi = new AnyTypeBeanInfo(this, e);
                    this.beanInfoMap.put(Object.class, bi);
                }
                return bi;
            }
            throw new IllegalArgumentException();
        }
    }
    
    public final JaxBeanInfo getBeanInfo(final Object o) {
        for (Class c = o.getClass(); c != Object.class; c = c.getSuperclass()) {
            final JaxBeanInfo bi = this.beanInfoMap.get(c);
            if (bi != null) {
                return bi;
            }
        }
        if (o instanceof Element) {
            return this.beanInfoMap.get(Object.class);
        }
        for (final Class c2 : o.getClass().getInterfaces()) {
            final JaxBeanInfo bi2 = this.beanInfoMap.get(c2);
            if (bi2 != null) {
                return bi2;
            }
        }
        return null;
    }
    
    public final JaxBeanInfo getBeanInfo(final Object o, final boolean fatal) throws JAXBException {
        final JaxBeanInfo bi = this.getBeanInfo(o);
        if (bi != null) {
            return bi;
        }
        if (!fatal) {
            return null;
        }
        if (o instanceof Document) {
            throw new JAXBException(Messages.ELEMENT_NEEDED_BUT_FOUND_DOCUMENT.format(o.getClass()));
        }
        throw new JAXBException(Messages.UNKNOWN_CLASS.format(o.getClass()));
    }
    
    public final <T> JaxBeanInfo<T> getBeanInfo(final Class<T> clazz) {
        return this.beanInfoMap.get(clazz);
    }
    
    public final <T> JaxBeanInfo<T> getBeanInfo(final Class<T> clazz, final boolean fatal) throws JAXBException {
        final JaxBeanInfo<T> bi = this.getBeanInfo(clazz);
        if (bi != null) {
            return bi;
        }
        if (fatal) {
            throw new JAXBException(clazz.getName() + " is not known to this context");
        }
        return null;
    }
    
    public final Loader selectRootLoader(final UnmarshallingContext.State state, final TagName tag) {
        final JaxBeanInfo beanInfo = this.rootMap.get(tag.uri, tag.local);
        if (beanInfo == null) {
            return null;
        }
        return beanInfo.getLoader(this, true);
    }
    
    public JaxBeanInfo getGlobalType(final QName name) {
        return this.typeMap.get(name);
    }
    
    public String getNearestTypeName(final QName name) {
        final String[] all = new String[this.typeMap.size()];
        int i = 0;
        for (final QName qn : this.typeMap.keySet()) {
            if (qn.getLocalPart().equals(name.getLocalPart())) {
                return qn.toString();
            }
            all[i++] = qn.toString();
        }
        final String nearest = EditDistance.findNearest(name.toString(), all);
        if (EditDistance.editDistance(nearest, name.toString()) > 10) {
            return null;
        }
        return nearest;
    }
    
    public Set<QName> getValidRootNames() {
        final Set<QName> r = new TreeSet<QName>(JAXBContextImpl.QNAME_COMPARATOR);
        for (final QNameMap.Entry e : this.rootMap.entrySet()) {
            r.add(e.createQName());
        }
        return r;
    }
    
    public synchronized Encoded[] getUTF8NameTable() {
        if (this.utf8nameTable == null) {
            final Encoded[] x = new Encoded[this.nameList.localNames.length];
            for (int i = 0; i < x.length; ++i) {
                final Encoded e = new Encoded(this.nameList.localNames[i]);
                e.compact();
                x[i] = e;
            }
            this.utf8nameTable = x;
        }
        return this.utf8nameTable;
    }
    
    public int getNumberOfLocalNames() {
        return this.nameList.localNames.length;
    }
    
    public int getNumberOfElementNames() {
        return this.nameList.numberOfElementNames;
    }
    
    public int getNumberOfAttributeNames() {
        return this.nameList.numberOfAttributeNames;
    }
    
    static Transformer createTransformer(final boolean disableSecureProcessing) {
        try {
            final SAXTransformerFactory tf = (SAXTransformerFactory)XmlFactory.createTransformerFactory(disableSecureProcessing);
            return tf.newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new Error(e);
        }
    }
    
    public static TransformerHandler createTransformerHandler(final boolean disableSecureProcessing) {
        try {
            final SAXTransformerFactory tf = (SAXTransformerFactory)XmlFactory.createTransformerFactory(disableSecureProcessing);
            return tf.newTransformerHandler();
        }
        catch (final TransformerConfigurationException e) {
            throw new Error(e);
        }
    }
    
    static Document createDom(final boolean disableSecurityProcessing) {
        synchronized (JAXBContextImpl.class) {
            if (JAXBContextImpl.db == null) {
                try {
                    final DocumentBuilderFactory dbf = XmlFactory.createDocumentBuilderFactory(disableSecurityProcessing);
                    JAXBContextImpl.db = dbf.newDocumentBuilder();
                }
                catch (final ParserConfigurationException e) {
                    throw new FactoryConfigurationError(e);
                }
            }
            return JAXBContextImpl.db.newDocument();
        }
    }
    
    @Override
    public MarshallerImpl createMarshaller() {
        return new MarshallerImpl(this, null);
    }
    
    @Override
    public UnmarshallerImpl createUnmarshaller() {
        return new UnmarshallerImpl(this, null);
    }
    
    @Override
    public Validator createValidator() {
        throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED_IN_2_0.format(new Object[0]));
    }
    
    @Override
    public JAXBIntrospector createJAXBIntrospector() {
        return new JAXBIntrospector() {
            @Override
            public boolean isElement(final Object object) {
                return this.getElementName(object) != null;
            }
            
            @Override
            public QName getElementName(final Object jaxbElement) {
                try {
                    return JAXBContextImpl.this.getElementName(jaxbElement);
                }
                catch (final JAXBException e) {
                    return null;
                }
            }
        };
    }
    
    private NonElement<Type, Class> getXmlType(final RuntimeTypeInfoSet tis, final TypeReference tr) {
        if (tr == null) {
            throw new IllegalArgumentException();
        }
        final XmlJavaTypeAdapter xjta = tr.get(XmlJavaTypeAdapter.class);
        final XmlList xl = tr.get(XmlList.class);
        final Ref<Type, Class> ref = new Ref<Type, Class>(this.annotationReader, (Navigator<Type, Class, ?, ?>)tis.getNavigator(), tr.type, xjta, xl);
        return (NonElement<Type, Class>)((TypeInfoSet<Type, Class, F, M>)tis).getTypeInfo(ref);
    }
    
    @Override
    public void generateEpisode(final Result output) {
        if (output == null) {
            throw new IllegalArgumentException();
        }
        this.createSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(output));
    }
    
    @Override
    public void generateSchema(final SchemaOutputResolver outputResolver) throws IOException {
        if (outputResolver == null) {
            throw new IOException(Messages.NULL_OUTPUT_RESOLVER.format(new Object[0]));
        }
        final SAXParseException[] e = { null };
        final SAXParseException[] w = { null };
        this.createSchemaGenerator().write(outputResolver, new ErrorListener() {
            @Override
            public void error(final SAXParseException exception) {
                e[0] = exception;
            }
            
            @Override
            public void fatalError(final SAXParseException exception) {
                e[0] = exception;
            }
            
            @Override
            public void warning(final SAXParseException exception) {
                w[0] = exception;
            }
            
            @Override
            public void info(final SAXParseException exception) {
            }
        });
        if (e[0] != null) {
            final IOException x = new IOException(Messages.FAILED_TO_GENERATE_SCHEMA.format(new Object[0]));
            x.initCause(e[0]);
            throw x;
        }
        if (w[0] != null) {
            final IOException x = new IOException(Messages.ERROR_PROCESSING_SCHEMA.format(new Object[0]));
            x.initCause(w[0]);
            throw x;
        }
    }
    
    private XmlSchemaGenerator<Type, Class, Field, Method> createSchemaGenerator() {
        RuntimeTypeInfoSet tis;
        try {
            tis = this.getTypeInfoSet();
        }
        catch (final IllegalAnnotationsException e) {
            throw new AssertionError((Object)e);
        }
        final XmlSchemaGenerator<Type, Class, Field, Method> xsdgen = new XmlSchemaGenerator<Type, Class, Field, Method>((Navigator<Type, Class, Field, Method>)tis.getNavigator(), tis);
        final Set<QName> rootTagNames = new HashSet<QName>();
        for (final RuntimeElementInfo ei : tis.getAllElements()) {
            rootTagNames.add(ei.getElementName());
        }
        for (final RuntimeClassInfo ci : tis.beans().values()) {
            if (ci.isElement()) {
                rootTagNames.add(ci.asElement().getElementName());
            }
        }
        for (final TypeReference tr : this.bridges.keySet()) {
            if (rootTagNames.contains(tr.tagName)) {
                continue;
            }
            if (tr.type == Void.TYPE || tr.type == Void.class) {
                xsdgen.add(tr.tagName, false, (NonElement<Type, Class>)null);
            }
            else {
                if (tr.type == CompositeStructure.class) {
                    continue;
                }
                final NonElement<Type, Class> typeInfo = this.getXmlType(tis, tr);
                xsdgen.add(tr.tagName, !tis.getNavigator().isPrimitive(tr.type), typeInfo);
            }
        }
        return xsdgen;
    }
    
    @Override
    public QName getTypeName(final TypeReference tr) {
        try {
            final NonElement<Type, Class> xt = this.getXmlType(this.getTypeInfoSet(), tr);
            if (xt == null) {
                throw new IllegalArgumentException();
            }
            return xt.getTypeName();
        }
        catch (final IllegalAnnotationsException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    @Override
    public <T> Binder<T> createBinder(final Class<T> domType) {
        if (domType == Node.class) {
            return (Binder<T>)this.createBinder();
        }
        return super.createBinder(domType);
    }
    
    @Override
    public Binder<Node> createBinder() {
        return new BinderImpl<Node>(this, new DOMScanner());
    }
    
    @Override
    public QName getElementName(final Object o) throws JAXBException {
        final JaxBeanInfo bi = this.getBeanInfo(o, true);
        if (!bi.isElement()) {
            return null;
        }
        return new QName(bi.getElementNamespaceURI(o), bi.getElementLocalName(o));
    }
    
    @Override
    public QName getElementName(final Class o) throws JAXBException {
        final JaxBeanInfo bi = this.getBeanInfo((Class<Object>)o, true);
        if (!bi.isElement()) {
            return null;
        }
        return new QName(bi.getElementNamespaceURI(o), bi.getElementLocalName(o));
    }
    
    @Override
    public Bridge createBridge(final TypeReference ref) {
        return this.bridges.get(ref);
    }
    
    @NotNull
    @Override
    public BridgeContext createBridgeContext() {
        return new BridgeContextImpl(this);
    }
    
    @Override
    public RawAccessor getElementPropertyAccessor(final Class wrapperBean, final String nsUri, final String localName) throws JAXBException {
        final JaxBeanInfo bi = this.getBeanInfo((Class<Object>)wrapperBean, true);
        if (!(bi instanceof ClassBeanInfoImpl)) {
            throw new JAXBException(wrapperBean + " is not a bean");
        }
        for (ClassBeanInfoImpl cb = (ClassBeanInfoImpl)bi; cb != null; cb = cb.superClazz) {
            for (final Property p : cb.properties) {
                final Accessor acc = p.getElementPropertyAccessor(nsUri, localName);
                if (acc != null) {
                    return new RawAccessor() {
                        @Override
                        public Object get(final Object bean) throws AccessorException {
                            return acc.getUnadapted(bean);
                        }
                        
                        @Override
                        public void set(final Object bean, final Object value) throws AccessorException {
                            acc.setUnadapted(bean, value);
                        }
                    };
                }
            }
        }
        throw new JAXBException(new QName(nsUri, localName) + " is not a valid property on " + wrapperBean);
    }
    
    @Override
    public List<String> getKnownNamespaceURIs() {
        return Arrays.asList(this.nameList.namespaceURIs);
    }
    
    @Override
    public String getBuildId() {
        final Package pkg = this.getClass().getPackage();
        if (pkg == null) {
            return null;
        }
        return pkg.getImplementationVersion();
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(Which.which(this.getClass()) + " Build-Id: " + this.getBuildId());
        buf.append("\nClasses known to this context:\n");
        final Set<String> names = new TreeSet<String>();
        for (final Class key : this.beanInfoMap.keySet()) {
            names.add(key.getName());
        }
        for (final String name : names) {
            buf.append("  ").append(name).append('\n');
        }
        return buf.toString();
    }
    
    public String getXMIMEContentType(final Object o) {
        final JaxBeanInfo bi = this.getBeanInfo(o);
        if (!(bi instanceof ClassBeanInfoImpl)) {
            return null;
        }
        final ClassBeanInfoImpl cb = (ClassBeanInfoImpl)bi;
        for (final Property p : cb.properties) {
            if (p instanceof AttributeProperty) {
                final AttributeProperty ap = (AttributeProperty)p;
                if (ap.attName.equals("http://www.w3.org/2005/05/xmlmime", "contentType")) {
                    try {
                        return (String)ap.xacc.print((BeanT)o);
                    }
                    catch (final AccessorException e) {
                        return null;
                    }
                    catch (final SAXException e2) {
                        return null;
                    }
                    catch (final ClassCastException e3) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    public JAXBContextImpl createAugmented(final Class<?> clazz) throws JAXBException {
        final Class[] newList = new Class[this.classes.length + 1];
        System.arraycopy(this.classes, 0, newList, 0, this.classes.length);
        newList[this.classes.length] = clazz;
        final JAXBContextBuilder builder = new JAXBContextBuilder(this);
        builder.setClasses(newList);
        return builder.build();
    }
    
    static {
        QNAME_COMPARATOR = new Comparator<QName>() {
            @Override
            public int compare(final QName lhs, final QName rhs) {
                final int r = lhs.getLocalPart().compareTo(rhs.getLocalPart());
                if (r != 0) {
                    return r;
                }
                return lhs.getNamespaceURI().compareTo(rhs.getNamespaceURI());
            }
        };
    }
    
    public static class JAXBContextBuilder
    {
        private boolean retainPropertyInfo;
        private boolean supressAccessorWarnings;
        private String defaultNsUri;
        @NotNull
        private RuntimeAnnotationReader annotationReader;
        @NotNull
        private Map<Class, Class> subclassReplacements;
        private boolean c14nSupport;
        private Class[] classes;
        private Collection<TypeReference> typeRefs;
        private boolean xmlAccessorFactorySupport;
        private boolean allNillable;
        private boolean improvedXsiTypeHandling;
        private boolean disableSecurityProcessing;
        
        public JAXBContextBuilder() {
            this.retainPropertyInfo = false;
            this.supressAccessorWarnings = false;
            this.defaultNsUri = "";
            this.annotationReader = new RuntimeInlineAnnotationReader();
            this.subclassReplacements = (Map<Class, Class>)Collections.emptyMap();
            this.c14nSupport = false;
            this.xmlAccessorFactorySupport = false;
            this.improvedXsiTypeHandling = true;
            this.disableSecurityProcessing = true;
        }
        
        public JAXBContextBuilder(final JAXBContextImpl baseImpl) {
            this.retainPropertyInfo = false;
            this.supressAccessorWarnings = false;
            this.defaultNsUri = "";
            this.annotationReader = new RuntimeInlineAnnotationReader();
            this.subclassReplacements = (Map<Class, Class>)Collections.emptyMap();
            this.c14nSupport = false;
            this.xmlAccessorFactorySupport = false;
            this.improvedXsiTypeHandling = true;
            this.disableSecurityProcessing = true;
            this.supressAccessorWarnings = baseImpl.supressAccessorWarnings;
            this.retainPropertyInfo = baseImpl.retainPropertyInfo;
            this.defaultNsUri = baseImpl.defaultNsUri;
            this.annotationReader = baseImpl.annotationReader;
            this.subclassReplacements = baseImpl.subclassReplacements;
            this.c14nSupport = baseImpl.c14nSupport;
            this.classes = baseImpl.classes;
            this.typeRefs = baseImpl.bridges.keySet();
            this.xmlAccessorFactorySupport = baseImpl.xmlAccessorFactorySupport;
            this.allNillable = baseImpl.allNillable;
            this.disableSecurityProcessing = baseImpl.disableSecurityProcessing;
        }
        
        public JAXBContextBuilder setRetainPropertyInfo(final boolean val) {
            this.retainPropertyInfo = val;
            return this;
        }
        
        public JAXBContextBuilder setSupressAccessorWarnings(final boolean val) {
            this.supressAccessorWarnings = val;
            return this;
        }
        
        public JAXBContextBuilder setC14NSupport(final boolean val) {
            this.c14nSupport = val;
            return this;
        }
        
        public JAXBContextBuilder setXmlAccessorFactorySupport(final boolean val) {
            this.xmlAccessorFactorySupport = val;
            return this;
        }
        
        public JAXBContextBuilder setDefaultNsUri(final String val) {
            this.defaultNsUri = val;
            return this;
        }
        
        public JAXBContextBuilder setAllNillable(final boolean val) {
            this.allNillable = val;
            return this;
        }
        
        public JAXBContextBuilder setClasses(final Class[] val) {
            this.classes = val;
            return this;
        }
        
        public JAXBContextBuilder setAnnotationReader(final RuntimeAnnotationReader val) {
            this.annotationReader = val;
            return this;
        }
        
        public JAXBContextBuilder setSubclassReplacements(final Map<Class, Class> val) {
            this.subclassReplacements = val;
            return this;
        }
        
        public JAXBContextBuilder setTypeRefs(final Collection<TypeReference> val) {
            this.typeRefs = val;
            return this;
        }
        
        public JAXBContextBuilder setImprovedXsiTypeHandling(final boolean val) {
            this.improvedXsiTypeHandling = val;
            return this;
        }
        
        public JAXBContextBuilder setDisableSecurityProcessing(final boolean val) {
            this.disableSecurityProcessing = val;
            return this;
        }
        
        public JAXBContextImpl build() throws JAXBException {
            if (this.defaultNsUri == null) {
                this.defaultNsUri = "";
            }
            if (this.subclassReplacements == null) {
                this.subclassReplacements = (Map<Class, Class>)Collections.emptyMap();
            }
            if (this.annotationReader == null) {
                this.annotationReader = new RuntimeInlineAnnotationReader();
            }
            if (this.typeRefs == null) {
                this.typeRefs = (Collection<TypeReference>)Collections.emptyList();
            }
            return new JAXBContextImpl(this, null);
        }
    }
}
