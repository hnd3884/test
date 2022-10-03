package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Any;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.impl.ClassInfoImpl;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeDefParticle;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttrDecls;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import java.util.ArrayList;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleRestrictionModel;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType;
import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import com.sun.xml.internal.bind.v2.model.core.MaybeElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import com.sun.xml.internal.txw2.TxwException;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeHost;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.istack.internal.Nullable;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import com.sun.istack.internal.NotNull;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.util.StackRecorder;
import java.util.logging.Level;
import javax.xml.bind.SchemaOutputResolver;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.bind.v2.schemagen.episode.Bindings;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import javax.xml.bind.annotation.XmlElement;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.api.CompositeStructure;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import java.util.TreeMap;
import java.util.Comparator;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.api.ErrorListener;
import java.util.Map;
import java.util.logging.Logger;

public final class XmlSchemaGenerator<T, C, F, M>
{
    private static final Logger logger;
    private final Map<String, Namespace> namespaces;
    private ErrorListener errorListener;
    private Navigator<T, C, F, M> navigator;
    private final TypeInfoSet<T, C, F, M> types;
    private final NonElement<T, C> stringType;
    private final NonElement<T, C> anyType;
    private final CollisionCheckStack<ClassInfo<T, C>> collisionChecker;
    private static final Comparator<String> NAMESPACE_COMPARATOR;
    private static final String newline = "\n";
    
    public XmlSchemaGenerator(final Navigator<T, C, F, M> navigator, final TypeInfoSet<T, C, F, M> types) {
        this.namespaces = new TreeMap<String, Namespace>(XmlSchemaGenerator.NAMESPACE_COMPARATOR);
        this.collisionChecker = new CollisionCheckStack<ClassInfo<T, C>>();
        this.navigator = navigator;
        this.types = types;
        this.stringType = types.getTypeInfo(navigator.ref(String.class));
        this.anyType = types.getAnyTypeInfo();
        for (final ClassInfo<T, C> ci : types.beans().values()) {
            this.add(ci);
        }
        for (final ElementInfo<T, C> ei1 : types.getElementMappings(null).values()) {
            this.add(ei1);
        }
        for (final EnumLeafInfo<T, C> ei2 : types.enums().values()) {
            this.add(ei2);
        }
        for (final ArrayInfo<T, C> a : types.arrays().values()) {
            this.add(a);
        }
    }
    
    private Namespace getNamespace(final String uri) {
        Namespace n = this.namespaces.get(uri);
        if (n == null) {
            this.namespaces.put(uri, n = new Namespace(uri));
        }
        return n;
    }
    
    public void add(final ClassInfo<T, C> clazz) {
        assert clazz != null;
        String nsUri = null;
        if (clazz.getClazz() == this.navigator.asDecl(CompositeStructure.class)) {
            return;
        }
        if (clazz.isElement()) {
            nsUri = clazz.getElementName().getNamespaceURI();
            final Namespace ns = this.getNamespace(nsUri);
            ns.classes.add(clazz);
            ns.addDependencyTo(clazz.getTypeName());
            this.add(clazz.getElementName(), false, clazz);
        }
        final QName tn = clazz.getTypeName();
        if (tn != null) {
            nsUri = tn.getNamespaceURI();
        }
        else if (nsUri == null) {
            return;
        }
        final Namespace n = this.getNamespace(nsUri);
        n.classes.add(clazz);
        for (final PropertyInfo<T, C> p : clazz.getProperties()) {
            n.processForeignNamespaces(p, 1);
            if (p instanceof AttributePropertyInfo) {
                final AttributePropertyInfo<T, C> ap = (AttributePropertyInfo)p;
                final String aUri = ap.getXmlName().getNamespaceURI();
                if (aUri.length() > 0) {
                    this.getNamespace(aUri).addGlobalAttribute(ap);
                    n.addDependencyTo(ap.getXmlName());
                }
            }
            if (p instanceof ElementPropertyInfo) {
                final ElementPropertyInfo<T, C> ep = (ElementPropertyInfo)p;
                for (final TypeRef<T, C> tref : ep.getTypes()) {
                    final String eUri = tref.getTagName().getNamespaceURI();
                    if (eUri.length() > 0 && !eUri.equals(n.uri)) {
                        this.getNamespace(eUri).addGlobalElement(tref);
                        n.addDependencyTo(tref.getTagName());
                    }
                }
            }
            if (this.generateSwaRefAdapter(p)) {
                n.useSwaRef = true;
            }
            final MimeType mimeType = p.getExpectedMimeType();
            if (mimeType != null) {
                n.useMimeNs = true;
            }
        }
        final ClassInfo<T, C> bc = clazz.getBaseClass();
        if (bc != null) {
            this.add(bc);
            n.addDependencyTo(bc.getTypeName());
        }
    }
    
    public void add(final ElementInfo<T, C> elem) {
        assert elem != null;
        boolean nillable = false;
        final QName name = elem.getElementName();
        final Namespace n = this.getNamespace(name.getNamespaceURI());
        ElementInfo ei;
        if (elem.getScope() != null) {
            ei = this.types.getElementInfo(elem.getScope().getClazz(), name);
        }
        else {
            ei = this.types.getElementInfo(null, name);
        }
        final XmlElement xmlElem = (XmlElement)ei.getProperty().readAnnotation(XmlElement.class);
        nillable = (xmlElem != null && xmlElem.nillable());
        n.elementDecls.put(name.getLocalPart(), new Namespace.ElementWithType(nillable, elem.getContentType()));
        n.processForeignNamespaces(elem.getProperty(), 1);
    }
    
    public void add(final EnumLeafInfo<T, C> envm) {
        assert envm != null;
        String nsUri = null;
        if (envm.isElement()) {
            nsUri = envm.getElementName().getNamespaceURI();
            final Namespace ns = this.getNamespace(nsUri);
            ns.enums.add(envm);
            ns.addDependencyTo(envm.getTypeName());
            this.add(envm.getElementName(), false, envm);
        }
        final QName typeName = envm.getTypeName();
        if (typeName != null) {
            nsUri = typeName.getNamespaceURI();
        }
        else if (nsUri == null) {
            return;
        }
        final Namespace n = this.getNamespace(nsUri);
        n.enums.add(envm);
        n.addDependencyTo(envm.getBaseType().getTypeName());
    }
    
    public void add(final ArrayInfo<T, C> a) {
        assert a != null;
        final String namespaceURI = a.getTypeName().getNamespaceURI();
        final Namespace n = this.getNamespace(namespaceURI);
        n.arrays.add(a);
        n.addDependencyTo(a.getItemType().getTypeName());
    }
    
    public void add(final QName tagName, final boolean isNillable, final NonElement<T, C> type) {
        if (type != null && type.getType() == this.navigator.ref(CompositeStructure.class)) {
            return;
        }
        final Namespace n = this.getNamespace(tagName.getNamespaceURI());
        n.elementDecls.put(tagName.getLocalPart(), new Namespace.ElementWithType(isNillable, type));
        if (type != null) {
            n.addDependencyTo(type.getTypeName());
        }
    }
    
    public void writeEpisodeFile(final XmlSerializer out) {
        final Bindings root = TXW.create(Bindings.class, out);
        if (this.namespaces.containsKey("")) {
            root._namespace("http://java.sun.com/xml/ns/jaxb", "jaxb");
        }
        root.version("2.1");
        for (final Map.Entry<String, Namespace> e : this.namespaces.entrySet()) {
            final Bindings group = root.bindings();
            final String tns = e.getKey();
            String prefix;
            if (!tns.equals("")) {
                group._namespace(tns, "tns");
                prefix = "tns:";
            }
            else {
                prefix = "";
            }
            group.scd("x-schema::" + (tns.equals("") ? "" : "tns"));
            group.schemaBindings().map(false);
            for (final ClassInfo<T, C> ci : e.getValue().classes) {
                if (ci.getTypeName() == null) {
                    continue;
                }
                if (ci.getTypeName().getNamespaceURI().equals(tns)) {
                    final Bindings child = group.bindings();
                    child.scd('~' + prefix + ci.getTypeName().getLocalPart());
                    child.klass().ref(ci.getName());
                }
                if (!ci.isElement() || !ci.getElementName().getNamespaceURI().equals(tns)) {
                    continue;
                }
                final Bindings child = group.bindings();
                child.scd(prefix + ci.getElementName().getLocalPart());
                child.klass().ref(ci.getName());
            }
            for (final EnumLeafInfo<T, C> en : e.getValue().enums) {
                if (en.getTypeName() == null) {
                    continue;
                }
                final Bindings child = group.bindings();
                child.scd('~' + prefix + en.getTypeName().getLocalPart());
                child.klass().ref(this.navigator.getClassName(en.getClazz()));
            }
            group.commit(true);
        }
        root.commit();
    }
    
    public void write(SchemaOutputResolver resolver, final ErrorListener errorListener) throws IOException {
        if (resolver == null) {
            throw new IllegalArgumentException();
        }
        if (XmlSchemaGenerator.logger.isLoggable(Level.FINE)) {
            XmlSchemaGenerator.logger.log(Level.FINE, "Writing XML Schema for " + this.toString(), new StackRecorder());
        }
        resolver = new FoolProofResolver(resolver);
        this.errorListener = errorListener;
        final Map<String, String> schemaLocations = this.types.getSchemaLocations();
        final Map<Namespace, Result> out = new HashMap<Namespace, Result>();
        final Map<Namespace, String> systemIds = new HashMap<Namespace, String>();
        this.namespaces.remove("http://www.w3.org/2001/XMLSchema");
        for (final Namespace n : this.namespaces.values()) {
            final String schemaLocation = schemaLocations.get(n.uri);
            if (schemaLocation != null) {
                systemIds.put(n, schemaLocation);
            }
            else {
                final Result output = resolver.createOutput(n.uri, "schema" + (out.size() + 1) + ".xsd");
                if (output != null) {
                    out.put(n, output);
                    systemIds.put(n, output.getSystemId());
                }
            }
            n.resetWritten();
        }
        for (final Map.Entry<Namespace, Result> e : out.entrySet()) {
            final Result result = e.getValue();
            e.getKey().writeTo(result, systemIds);
            if (result instanceof StreamResult) {
                final OutputStream outputStream = ((StreamResult)result).getOutputStream();
                if (outputStream != null) {
                    outputStream.close();
                }
                else {
                    final Writer writer = ((StreamResult)result).getWriter();
                    if (writer == null) {
                        continue;
                    }
                    writer.close();
                }
            }
        }
    }
    
    private boolean generateSwaRefAdapter(final NonElementRef<T, C> typeRef) {
        return this.generateSwaRefAdapter(typeRef.getSource());
    }
    
    private boolean generateSwaRefAdapter(final PropertyInfo<T, C> prop) {
        final Adapter<T, C> adapter = prop.getAdapter();
        if (adapter == null) {
            return false;
        }
        final Object o = this.navigator.asDecl(SwaRefAdapter.class);
        return o != null && o.equals(adapter.adapterType);
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        for (final Namespace ns : this.namespaces.values()) {
            if (buf.length() > 0) {
                buf.append(',');
            }
            buf.append(ns.uri).append('=').append(ns);
        }
        return super.toString() + '[' + (Object)buf + ']';
    }
    
    private static String getProcessContentsModeName(final WildcardMode wc) {
        switch (wc) {
            case LAX:
            case SKIP: {
                return wc.name().toLowerCase();
            }
            case STRICT: {
                return null;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    protected static String relativize(final String uri, final String baseUri) {
        try {
            assert uri != null;
            if (baseUri == null) {
                return uri;
            }
            final URI theUri = new URI(Util.escapeURI(uri));
            final URI theBaseUri = new URI(Util.escapeURI(baseUri));
            if (theUri.isOpaque() || theBaseUri.isOpaque()) {
                return uri;
            }
            if (!Util.equalsIgnoreCase(theUri.getScheme(), theBaseUri.getScheme()) || !Util.equal(theUri.getAuthority(), theBaseUri.getAuthority())) {
                return uri;
            }
            final String uriPath = theUri.getPath();
            String basePath = theBaseUri.getPath();
            if (!basePath.endsWith("/")) {
                basePath = Util.normalizeUriPath(basePath);
            }
            if (uriPath.equals(basePath)) {
                return ".";
            }
            final String relPath = calculateRelativePath(uriPath, basePath, fixNull(theUri.getScheme()).equals("file"));
            if (relPath == null) {
                return uri;
            }
            final StringBuilder relUri = new StringBuilder();
            relUri.append(relPath);
            if (theUri.getQuery() != null) {
                relUri.append('?').append(theUri.getQuery());
            }
            if (theUri.getFragment() != null) {
                relUri.append('#').append(theUri.getFragment());
            }
            return relUri.toString();
        }
        catch (final URISyntaxException e) {
            throw new InternalError("Error escaping one of these uris:\n\t" + uri + "\n\t" + baseUri);
        }
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    private static String calculateRelativePath(final String uri, final String base, final boolean fileUrl) {
        final boolean onWindows = File.pathSeparatorChar == ';';
        if (base == null) {
            return null;
        }
        if ((fileUrl && onWindows && startsWithIgnoreCase(uri, base)) || uri.startsWith(base)) {
            return uri.substring(base.length());
        }
        return "../" + calculateRelativePath(uri, Util.getParentUriPath(base), fileUrl);
    }
    
    private static boolean startsWithIgnoreCase(final String s, final String t) {
        return s.toUpperCase().startsWith(t.toUpperCase());
    }
    
    static {
        logger = com.sun.xml.internal.bind.Util.getClassLogger();
        NAMESPACE_COMPARATOR = new Comparator<String>() {
            @Override
            public int compare(final String lhs, final String rhs) {
                return -lhs.compareTo(rhs);
            }
        };
    }
    
    private class Namespace
    {
        @NotNull
        final String uri;
        private final Set<Namespace> depends;
        private boolean selfReference;
        private final Set<ClassInfo<T, C>> classes;
        private final Set<EnumLeafInfo<T, C>> enums;
        private final Set<ArrayInfo<T, C>> arrays;
        private final MultiMap<String, AttributePropertyInfo<T, C>> attributeDecls;
        private final MultiMap<String, ElementDeclaration> elementDecls;
        private Form attributeFormDefault;
        private Form elementFormDefault;
        private boolean useSwaRef;
        private boolean useMimeNs;
        private final Set<ClassInfo> written;
        
        public Namespace(final String uri) {
            this.depends = new LinkedHashSet<Namespace>();
            this.classes = new LinkedHashSet<ClassInfo<T, C>>();
            this.enums = new LinkedHashSet<EnumLeafInfo<T, C>>();
            this.arrays = new LinkedHashSet<ArrayInfo<T, C>>();
            this.attributeDecls = new MultiMap<String, AttributePropertyInfo<T, C>>((AttributePropertyInfo<T, C>)null);
            this.elementDecls = new MultiMap<String, ElementDeclaration>(new ElementWithType(true, XmlSchemaGenerator.this.anyType));
            this.written = new HashSet<ClassInfo>();
            this.uri = uri;
            assert !XmlSchemaGenerator.this.namespaces.containsKey(uri);
            XmlSchemaGenerator.this.namespaces.put(uri, this);
        }
        
        void resetWritten() {
            this.written.clear();
        }
        
        private void processForeignNamespaces(final PropertyInfo<T, C> p, int processingDepth) {
            for (final TypeInfo<T, C> t : p.ref()) {
                if (t instanceof ClassInfo && processingDepth > 0) {
                    final List<PropertyInfo> l = ((ClassInfo)t).getProperties();
                    for (final PropertyInfo subp : l) {
                        this.processForeignNamespaces(subp, --processingDepth);
                    }
                }
                if (t instanceof Element) {
                    this.addDependencyTo(((Element)t).getElementName());
                }
                if (t instanceof NonElement) {
                    this.addDependencyTo(((NonElement)t).getTypeName());
                }
            }
        }
        
        private void addDependencyTo(@Nullable final QName qname) {
            if (qname == null) {
                return;
            }
            final String nsUri = qname.getNamespaceURI();
            if (nsUri.equals("http://www.w3.org/2001/XMLSchema")) {
                return;
            }
            if (nsUri.equals(this.uri)) {
                this.selfReference = true;
                return;
            }
            this.depends.add(XmlSchemaGenerator.this.getNamespace(nsUri));
        }
        
        private void writeTo(final Result result, final Map<Namespace, String> systemIds) throws IOException {
            try {
                final Schema schema = TXW.create(Schema.class, ResultFactory.createSerializer(result));
                final Map<String, String> xmlNs = XmlSchemaGenerator.this.types.getXmlNs(this.uri);
                for (final Map.Entry<String, String> e : xmlNs.entrySet()) {
                    schema._namespace(e.getValue(), e.getKey());
                }
                if (this.useSwaRef) {
                    schema._namespace("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef");
                }
                if (this.useMimeNs) {
                    schema._namespace("http://www.w3.org/2005/05/xmlmime", "xmime");
                }
                (this.attributeFormDefault = Form.get(XmlSchemaGenerator.this.types.getAttributeFormDefault(this.uri))).declare("attributeFormDefault", schema);
                (this.elementFormDefault = Form.get(XmlSchemaGenerator.this.types.getElementFormDefault(this.uri))).declare("elementFormDefault", schema);
                if (!xmlNs.containsValue("http://www.w3.org/2001/XMLSchema") && !xmlNs.containsKey("xs")) {
                    schema._namespace("http://www.w3.org/2001/XMLSchema", "xs");
                }
                schema.version("1.0");
                if (this.uri.length() != 0) {
                    schema.targetNamespace(this.uri);
                }
                for (final Namespace ns : this.depends) {
                    schema._namespace(ns.uri);
                }
                if (this.selfReference && this.uri.length() != 0) {
                    schema._namespace(this.uri, "tns");
                }
                schema._pcdata("\n");
                for (final Namespace n : this.depends) {
                    final Import imp = schema._import();
                    if (n.uri.length() != 0) {
                        imp.namespace(n.uri);
                    }
                    final String refSystemId = systemIds.get(n);
                    if (refSystemId != null && !refSystemId.equals("")) {
                        imp.schemaLocation(XmlSchemaGenerator.relativize(refSystemId, result.getSystemId()));
                    }
                    schema._pcdata("\n");
                }
                if (this.useSwaRef) {
                    schema._import().namespace("http://ws-i.org/profiles/basic/1.1/xsd").schemaLocation("http://ws-i.org/profiles/basic/1.1/swaref.xsd");
                }
                if (this.useMimeNs) {
                    schema._import().namespace("http://www.w3.org/2005/05/xmlmime").schemaLocation("http://www.w3.org/2005/05/xmlmime");
                }
                for (final Map.Entry<String, ElementDeclaration> e2 : this.elementDecls.entrySet()) {
                    e2.getValue().writeTo(e2.getKey(), schema);
                    schema._pcdata("\n");
                }
                for (final ClassInfo<T, C> c : this.classes) {
                    if (c.getTypeName() == null) {
                        continue;
                    }
                    if (this.uri.equals(c.getTypeName().getNamespaceURI())) {
                        this.writeClass(c, schema);
                    }
                    schema._pcdata("\n");
                }
                for (final EnumLeafInfo<T, C> e3 : this.enums) {
                    if (e3.getTypeName() == null) {
                        continue;
                    }
                    if (this.uri.equals(e3.getTypeName().getNamespaceURI())) {
                        this.writeEnum(e3, schema);
                    }
                    schema._pcdata("\n");
                }
                for (final ArrayInfo<T, C> a : this.arrays) {
                    this.writeArray(a, schema);
                    schema._pcdata("\n");
                }
                for (final Map.Entry<String, AttributePropertyInfo<T, C>> e4 : this.attributeDecls.entrySet()) {
                    final TopLevelAttribute a2 = schema.attribute();
                    a2.name(e4.getKey());
                    if (e4.getValue() == null) {
                        this.writeTypeRef(a2, XmlSchemaGenerator.this.stringType, "type");
                    }
                    else {
                        this.writeAttributeTypeRef(e4.getValue(), a2);
                    }
                    schema._pcdata("\n");
                }
                schema.commit();
            }
            catch (final TxwException e5) {
                XmlSchemaGenerator.logger.log(Level.INFO, e5.getMessage(), e5);
                throw new IOException(e5.getMessage());
            }
        }
        
        private void writeTypeRef(final TypeHost th, final NonElementRef<T, C> typeRef, final String refAttName) {
            switch (typeRef.getSource().id()) {
                case ID: {
                    th._attribute(refAttName, new QName("http://www.w3.org/2001/XMLSchema", "ID"));
                    return;
                }
                case IDREF: {
                    th._attribute(refAttName, new QName("http://www.w3.org/2001/XMLSchema", "IDREF"));
                    return;
                }
                case NONE: {
                    final MimeType mimeType = typeRef.getSource().getExpectedMimeType();
                    if (mimeType != null) {
                        th._attribute(new QName("http://www.w3.org/2005/05/xmlmime", "expectedContentTypes", "xmime"), mimeType.toString());
                    }
                    if (XmlSchemaGenerator.this.generateSwaRefAdapter(typeRef)) {
                        th._attribute(refAttName, new QName("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef", "ref"));
                        return;
                    }
                    if (typeRef.getSource().getSchemaType() != null) {
                        th._attribute(refAttName, typeRef.getSource().getSchemaType());
                        return;
                    }
                    this.writeTypeRef(th, typeRef.getTarget(), refAttName);
                    return;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        private void writeTypeRef(final TypeHost th, final NonElement<T, C> type, final String refAttName) {
            Element e = null;
            if (type instanceof MaybeElement) {
                final MaybeElement me = (MaybeElement)type;
                final boolean isElement = me.isElement();
                if (isElement) {
                    e = me.asElement();
                }
            }
            if (type instanceof Element) {
                e = (Element)type;
            }
            if (type.getTypeName() == null) {
                if (e != null && e.getElementName() != null) {
                    th.block();
                    if (type instanceof ClassInfo) {
                        this.writeClass((ClassInfo)type, th);
                    }
                    else {
                        this.writeEnum((EnumLeafInfo)type, (SimpleTypeHost)th);
                    }
                }
                else {
                    th.block();
                    if (type instanceof ClassInfo) {
                        if (XmlSchemaGenerator.this.collisionChecker.push(type)) {
                            XmlSchemaGenerator.this.errorListener.warning(new SAXParseException(Messages.ANONYMOUS_TYPE_CYCLE.format(XmlSchemaGenerator.this.collisionChecker.getCycleString()), (Locator)null));
                        }
                        else {
                            this.writeClass((ClassInfo)type, th);
                        }
                        XmlSchemaGenerator.this.collisionChecker.pop();
                    }
                    else {
                        this.writeEnum((EnumLeafInfo)type, (SimpleTypeHost)th);
                    }
                }
            }
            else {
                th._attribute(refAttName, type.getTypeName());
            }
        }
        
        private void writeArray(final ArrayInfo<T, C> a, final Schema schema) {
            final ComplexType ct = schema.complexType().name(a.getTypeName().getLocalPart());
            ct._final("#all");
            final LocalElement le = ct.sequence().element().name("item");
            le.type(a.getItemType().getTypeName());
            le.minOccurs(0).maxOccurs("unbounded");
            le.nillable(true);
            ct.commit();
        }
        
        private void writeEnum(final EnumLeafInfo<T, C> e, final SimpleTypeHost th) {
            final SimpleType st = th.simpleType();
            this.writeName(e, st);
            final SimpleRestrictionModel base = st.restriction();
            this.writeTypeRef(base, e.getBaseType(), "base");
            for (final EnumConstant c : e.getConstants()) {
                base.enumeration().value(c.getLexicalValue());
            }
            st.commit();
        }
        
        private void writeClass(final ClassInfo<T, C> c, final TypeHost parent) {
            if (this.written.contains(c)) {
                return;
            }
            this.written.add(c);
            if (!this.containsValueProp(c)) {
                final ComplexType ct = ((ComplexTypeHost)parent).complexType();
                this.writeName(c, ct);
                if (c.isFinal()) {
                    ct._final("extension restriction");
                }
                if (c.isAbstract()) {
                    ct._abstract(true);
                }
                AttrDecls contentModel = ct;
                TypeDefParticle contentModelOwner = ct;
                final ClassInfo<T, C> bc = c.getBaseClass();
                if (bc != null) {
                    if (bc.hasValueProperty()) {
                        final SimpleExtension se = (SimpleExtension)(contentModel = ct.simpleContent().extension());
                        contentModelOwner = null;
                        se.base(bc.getTypeName());
                    }
                    else {
                        final ComplexExtension ce = (ComplexExtension)(contentModelOwner = (TypeDefParticle)(contentModel = ct.complexContent().extension()));
                        ce.base(bc.getTypeName());
                    }
                }
                if (contentModelOwner != null) {
                    final ArrayList<Tree> children = new ArrayList<Tree>();
                    for (final PropertyInfo<T, C> p : c.getProperties()) {
                        if (p instanceof ReferencePropertyInfo && ((ReferencePropertyInfo)p).isMixed()) {
                            ct.mixed(true);
                        }
                        final Tree t = this.buildPropertyContentModel(p);
                        if (t != null) {
                            children.add(t);
                        }
                    }
                    final Tree top = Tree.makeGroup(c.isOrdered() ? GroupKind.SEQUENCE : GroupKind.ALL, children);
                    top.write(contentModelOwner);
                }
                for (final PropertyInfo<T, C> p2 : c.getProperties()) {
                    if (p2 instanceof AttributePropertyInfo) {
                        this.handleAttributeProp((AttributePropertyInfo)p2, contentModel);
                    }
                }
                if (c.hasAttributeWildcard()) {
                    contentModel.anyAttribute().namespace("##other").processContents("skip");
                }
                ct.commit();
                return;
            }
            if (c.getProperties().size() == 1) {
                final ValuePropertyInfo<T, C> vp = (ValuePropertyInfo<T, C>)c.getProperties().get(0);
                final SimpleType st = ((SimpleTypeHost)parent).simpleType();
                this.writeName(c, st);
                if (vp.isCollection()) {
                    this.writeTypeRef(st.list(), vp.getTarget(), "itemType");
                }
                else {
                    this.writeTypeRef(st.restriction(), vp.getTarget(), "base");
                }
                return;
            }
            final ComplexType ct = ((ComplexTypeHost)parent).complexType();
            this.writeName(c, ct);
            if (c.isFinal()) {
                ct._final("extension restriction");
            }
            final SimpleExtension se2 = ct.simpleContent().extension();
            se2.block();
            for (final PropertyInfo<T, C> p3 : c.getProperties()) {
                switch (p3.kind()) {
                    case ATTRIBUTE: {
                        this.handleAttributeProp((AttributePropertyInfo)p3, se2);
                        continue;
                    }
                    case VALUE: {
                        TODO.checkSpec("what if vp.isCollection() == true?");
                        final ValuePropertyInfo vp2 = (ValuePropertyInfo)p3;
                        se2.base(vp2.getTarget().getTypeName());
                        continue;
                    }
                    default: {
                        assert false;
                        throw new IllegalStateException();
                    }
                }
            }
            se2.commit();
            TODO.schemaGenerator("figure out what to do if bc != null");
            TODO.checkSpec("handle sec 8.9.5.2, bullet #4");
        }
        
        private void writeName(final NonElement<T, C> c, final TypedXmlWriter xw) {
            final QName tn = c.getTypeName();
            if (tn != null) {
                xw._attribute("name", tn.getLocalPart());
            }
        }
        
        private boolean containsValueProp(final ClassInfo<T, C> c) {
            for (final PropertyInfo p : c.getProperties()) {
                if (p instanceof ValuePropertyInfo) {
                    return true;
                }
            }
            return false;
        }
        
        private Tree buildPropertyContentModel(final PropertyInfo<T, C> p) {
            switch (p.kind()) {
                case ELEMENT: {
                    return this.handleElementProp((ElementPropertyInfo)p);
                }
                case ATTRIBUTE: {
                    return null;
                }
                case REFERENCE: {
                    return this.handleReferenceProp((ReferencePropertyInfo)p);
                }
                case MAP: {
                    return this.handleMapProp((MapPropertyInfo)p);
                }
                case VALUE: {
                    assert false;
                    throw new IllegalStateException();
                }
                default: {
                    assert false;
                    throw new IllegalStateException();
                }
            }
        }
        
        private Tree handleElementProp(final ElementPropertyInfo<T, C> ep) {
            if (ep.isValueList()) {
                return new Tree.Term() {
                    @Override
                    protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                        final TypeRef<T, C> t = (TypeRef<T, C>)ep.getTypes().get(0);
                        final LocalElement e = parent.element();
                        e.block();
                        final QName tn = t.getTagName();
                        e.name(tn.getLocalPart());
                        final com.sun.xml.internal.bind.v2.schemagen.xmlschema.List lst = e.simpleType().list();
                        Namespace.this.writeTypeRef(lst, t, "itemType");
                        Namespace.this.elementFormDefault.writeForm(e, tn);
                        this.writeOccurs(e, isOptional || !ep.isRequired(), repeated);
                    }
                };
            }
            final ArrayList<Tree> children = new ArrayList<Tree>();
            for (final TypeRef<T, C> t : ep.getTypes()) {
                children.add(new Tree.Term() {
                    @Override
                    protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                        final LocalElement e = parent.element();
                        final QName tn = t.getTagName();
                        final PropertyInfo propInfo = t.getSource();
                        final TypeInfo parentInfo = (propInfo == null) ? null : propInfo.parent();
                        if (Namespace.this.canBeDirectElementRef(t, tn, parentInfo)) {
                            if (!t.getTarget().isSimpleType() && t.getTarget() instanceof ClassInfo && XmlSchemaGenerator.this.collisionChecker.findDuplicate(t.getTarget())) {
                                e.ref(new QName(Namespace.this.uri, tn.getLocalPart()));
                            }
                            else {
                                QName elemName = null;
                                if (t.getTarget() instanceof Element) {
                                    final Element te = (Element)t.getTarget();
                                    elemName = te.getElementName();
                                }
                                final Collection<TypeInfo> refs = propInfo.ref();
                                final TypeInfo ti;
                                if (refs != null && !refs.isEmpty() && elemName != null && ((ti = refs.iterator().next()) == null || ti instanceof ClassInfoImpl)) {
                                    final ClassInfoImpl cImpl = (ClassInfoImpl)ti;
                                    if (cImpl != null && cImpl.getElementName() != null) {
                                        e.ref(new QName(cImpl.getElementName().getNamespaceURI(), tn.getLocalPart()));
                                    }
                                    else {
                                        e.ref(new QName("", tn.getLocalPart()));
                                    }
                                }
                                else {
                                    e.ref(tn);
                                }
                            }
                        }
                        else {
                            e.name(tn.getLocalPart());
                            Namespace.this.writeTypeRef(e, t, "type");
                            Namespace.this.elementFormDefault.writeForm(e, tn);
                        }
                        if (t.isNillable()) {
                            e.nillable(true);
                        }
                        if (t.getDefaultValue() != null) {
                            e._default(t.getDefaultValue());
                        }
                        this.writeOccurs(e, isOptional, repeated);
                    }
                });
            }
            final Tree choice = Tree.makeGroup(GroupKind.CHOICE, children).makeOptional(!ep.isRequired()).makeRepeated(ep.isCollection());
            final QName ename = ep.getXmlName();
            if (ename != null) {
                return new Tree.Term() {
                    @Override
                    protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                        final LocalElement e = parent.element();
                        if (ename.getNamespaceURI().length() > 0 && !ename.getNamespaceURI().equals(Namespace.this.uri)) {
                            e.ref(new QName(ename.getNamespaceURI(), ename.getLocalPart()));
                            return;
                        }
                        e.name(ename.getLocalPart());
                        Namespace.this.elementFormDefault.writeForm(e, ename);
                        if (ep.isCollectionNillable()) {
                            e.nillable(true);
                        }
                        this.writeOccurs(e, !ep.isCollectionRequired(), repeated);
                        final ComplexType p = e.complexType();
                        choice.write(p);
                    }
                };
            }
            return choice;
        }
        
        private boolean canBeDirectElementRef(final TypeRef<T, C> t, final QName tn, final TypeInfo parentInfo) {
            Element te = null;
            ClassInfo ci = null;
            QName targetTagName = null;
            if (t.isNillable() || t.getDefaultValue() != null) {
                return false;
            }
            if (t.getTarget() instanceof Element) {
                te = (Element)t.getTarget();
                targetTagName = te.getElementName();
                if (te instanceof ClassInfo) {
                    ci = (ClassInfo)te;
                }
            }
            final String nsUri = tn.getNamespaceURI();
            return (!nsUri.equals(this.uri) && nsUri.length() > 0 && (!(parentInfo instanceof ClassInfo) || ((ClassInfo)parentInfo).getTypeName() != null)) || (ci != null && targetTagName != null && te.getScope() == null && targetTagName.getNamespaceURI() == null && targetTagName.equals(tn)) || (te != null && targetTagName != null && targetTagName.equals(tn));
        }
        
        private void handleAttributeProp(final AttributePropertyInfo<T, C> ap, final AttrDecls attr) {
            final LocalAttribute localAttribute = attr.attribute();
            final String attrURI = ap.getXmlName().getNamespaceURI();
            if (attrURI.equals("")) {
                localAttribute.name(ap.getXmlName().getLocalPart());
                this.writeAttributeTypeRef(ap, localAttribute);
                this.attributeFormDefault.writeForm(localAttribute, ap.getXmlName());
            }
            else {
                localAttribute.ref(ap.getXmlName());
            }
            if (ap.isRequired()) {
                localAttribute.use("required");
            }
        }
        
        private void writeAttributeTypeRef(final AttributePropertyInfo<T, C> ap, final AttributeType a) {
            if (ap.isCollection()) {
                this.writeTypeRef(a.simpleType().list(), ap, "itemType");
            }
            else {
                this.writeTypeRef(a, ap, "type");
            }
        }
        
        private Tree handleReferenceProp(final ReferencePropertyInfo<T, C> rp) {
            final ArrayList<Tree> children = new ArrayList<Tree>();
            for (final Element<T, C> e : rp.getElements()) {
                children.add(new Tree.Term() {
                    @Override
                    protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                        final LocalElement eref = parent.element();
                        boolean local = false;
                        final QName en = e.getElementName();
                        if (e.getScope() != null) {
                            final boolean qualified = en.getNamespaceURI().equals(Namespace.this.uri);
                            final boolean unqualified = en.getNamespaceURI().equals("");
                            if (qualified || unqualified) {
                                if (unqualified) {
                                    if (Namespace.this.elementFormDefault.isEffectivelyQualified) {
                                        eref.form("unqualified");
                                    }
                                }
                                else if (!Namespace.this.elementFormDefault.isEffectivelyQualified) {
                                    eref.form("qualified");
                                }
                                local = true;
                                eref.name(en.getLocalPart());
                                if (e instanceof ClassInfo) {
                                    Namespace.this.writeTypeRef(eref, (NonElement)e, "type");
                                }
                                else {
                                    Namespace.this.writeTypeRef(eref, ((ElementInfo)e).getContentType(), "type");
                                }
                            }
                        }
                        if (!local) {
                            eref.ref(en);
                        }
                        this.writeOccurs(eref, isOptional, repeated);
                    }
                });
            }
            final WildcardMode wc = rp.getWildcard();
            if (wc != null) {
                children.add(new Tree.Term() {
                    @Override
                    protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                        final Any any = parent.any();
                        final String pcmode = getProcessContentsModeName(wc);
                        if (pcmode != null) {
                            any.processContents(pcmode);
                        }
                        any.namespace("##other");
                        this.writeOccurs(any, isOptional, repeated);
                    }
                });
            }
            final Tree choice = Tree.makeGroup(GroupKind.CHOICE, children).makeRepeated(rp.isCollection()).makeOptional(!rp.isRequired());
            final QName ename = rp.getXmlName();
            if (ename != null) {
                return new Tree.Term() {
                    @Override
                    protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                        final LocalElement e = parent.element().name(ename.getLocalPart());
                        Namespace.this.elementFormDefault.writeForm(e, ename);
                        if (rp.isCollectionNillable()) {
                            e.nillable(true);
                        }
                        this.writeOccurs(e, true, repeated);
                        final ComplexType p = e.complexType();
                        choice.write(p);
                    }
                };
            }
            return choice;
        }
        
        private Tree handleMapProp(final MapPropertyInfo<T, C> mp) {
            return new Tree.Term() {
                @Override
                protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
                    final QName ename = mp.getXmlName();
                    LocalElement e = parent.element();
                    Namespace.this.elementFormDefault.writeForm(e, ename);
                    if (mp.isCollectionNillable()) {
                        e.nillable(true);
                    }
                    e = e.name(ename.getLocalPart());
                    this.writeOccurs(e, isOptional, repeated);
                    final ComplexType p = e.complexType();
                    e = p.sequence().element();
                    e.name("entry").minOccurs(0).maxOccurs("unbounded");
                    final ExplicitGroup seq = e.complexType().sequence();
                    Namespace.this.writeKeyOrValue(seq, "key", mp.getKeyType());
                    Namespace.this.writeKeyOrValue(seq, "value", mp.getValueType());
                }
            };
        }
        
        private void writeKeyOrValue(final ExplicitGroup seq, final String tagName, final NonElement<T, C> typeRef) {
            final LocalElement key = seq.element().name(tagName);
            key.minOccurs(0);
            this.writeTypeRef(key, typeRef, "type");
        }
        
        public void addGlobalAttribute(final AttributePropertyInfo<T, C> ap) {
            this.attributeDecls.put(ap.getXmlName().getLocalPart(), ap);
            this.addDependencyTo(ap.getTarget().getTypeName());
        }
        
        public void addGlobalElement(final TypeRef<T, C> tref) {
            this.elementDecls.put(tref.getTagName().getLocalPart(), new ElementWithType(false, tref.getTarget()));
            this.addDependencyTo(tref.getTarget().getTypeName());
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("[classes=").append(this.classes);
            buf.append(",elementDecls=").append(this.elementDecls);
            buf.append(",enums=").append(this.enums);
            buf.append("]");
            return super.toString();
        }
        
        abstract class ElementDeclaration
        {
            @Override
            public abstract boolean equals(final Object p0);
            
            @Override
            public abstract int hashCode();
            
            public abstract void writeTo(final String p0, final Schema p1);
        }
        
        class ElementWithType extends ElementDeclaration
        {
            private final boolean nillable;
            private final NonElement<T, C> type;
            
            public ElementWithType(final boolean nillable, final NonElement<T, C> type) {
                this.type = type;
                this.nillable = nillable;
            }
            
            @Override
            public void writeTo(final String localName, final Schema schema) {
                final TopLevelElement e = schema.element().name(localName);
                if (this.nillable) {
                    e.nillable(true);
                }
                if (this.type != null) {
                    Namespace.this.writeTypeRef(e, this.type, "type");
                }
                else {
                    e.complexType();
                }
                e.commit();
            }
            
            @Override
            public boolean equals(final Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                final ElementWithType that = (ElementWithType)o;
                return this.type.equals(that.type);
            }
            
            @Override
            public int hashCode() {
                return this.type.hashCode();
            }
        }
    }
}
