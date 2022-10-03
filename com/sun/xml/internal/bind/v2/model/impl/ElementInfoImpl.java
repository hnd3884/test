package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collections;
import java.util.Collection;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlID;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import com.sun.xml.internal.bind.v2.TODO;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Annotation;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.activation.MimeType;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.istack.internal.FinalArrayList;
import javax.xml.bind.annotation.XmlElementDecl;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;

class ElementInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements ElementInfo<T, C>
{
    private final QName tagName;
    private final NonElement<T, C> contentType;
    private final T tOfJAXBElementT;
    private final T elementType;
    private final ClassInfo<T, C> scope;
    private final XmlElementDecl anno;
    private ElementInfoImpl<T, C, F, M> substitutionHead;
    private FinalArrayList<ElementInfoImpl<T, C, F, M>> substitutionMembers;
    private final M method;
    private final Adapter<T, C> adapter;
    private final boolean isCollection;
    private final ID id;
    private final PropertyImpl property;
    private final MimeType expectedMimeType;
    private final boolean inlineBinary;
    private final QName schemaType;
    
    public ElementInfoImpl(final ModelBuilder<T, C, F, M> builder, final RegistryInfoImpl<T, C, F, M> registry, final M m) throws IllegalAnnotationException {
        super(builder, registry);
        this.method = m;
        this.anno = this.reader().getMethodAnnotation(XmlElementDecl.class, m, this);
        assert this.anno != null;
        assert this.anno instanceof Locatable;
        this.elementType = this.nav().getReturnType(m);
        final T baseClass = this.nav().getBaseClass(this.elementType, this.nav().asDecl(JAXBElement.class));
        if (baseClass == null) {
            throw new IllegalAnnotationException(Messages.XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD.format(this.nav().getMethodName(m)), this.anno);
        }
        this.tagName = this.parseElementName(this.anno);
        final T[] methodParams = this.nav().getMethodParameters(m);
        Adapter<T, C> a = null;
        if (methodParams.length > 0) {
            final XmlJavaTypeAdapter adapter = this.reader().getMethodAnnotation(XmlJavaTypeAdapter.class, m, this);
            if (adapter != null) {
                a = new Adapter<T, C>(adapter, this.reader(), this.nav());
            }
            else {
                final XmlAttachmentRef xsa = this.reader().getMethodAnnotation(XmlAttachmentRef.class, m, this);
                if (xsa != null) {
                    TODO.prototype("in Annotation Processing swaRefAdapter isn't avaialble, so this returns null");
                    a = new Adapter<T, C>(this.owner.nav.asDecl(SwaRefAdapter.class), this.owner.nav);
                }
            }
        }
        this.adapter = a;
        this.tOfJAXBElementT = ((methodParams.length > 0) ? methodParams[0] : this.nav().getTypeArgument(baseClass, 0));
        if (this.adapter == null) {
            final T list = this.nav().getBaseClass(this.tOfJAXBElementT, this.nav().asDecl(List.class));
            if (list == null) {
                this.isCollection = false;
                this.contentType = builder.getTypeInfo(this.tOfJAXBElementT, this);
            }
            else {
                this.isCollection = true;
                this.contentType = builder.getTypeInfo(this.nav().getTypeArgument(list, 0), this);
            }
        }
        else {
            this.contentType = builder.getTypeInfo(this.adapter.defaultType, this);
            this.isCollection = false;
        }
        final T s = this.reader().getClassValue(this.anno, "scope");
        if (this.nav().isSameType(s, this.nav().ref(XmlElementDecl.GLOBAL.class))) {
            this.scope = null;
        }
        else {
            final NonElement<T, C> scp = builder.getClassInfo(this.nav().asDecl(s), this);
            if (!(scp instanceof ClassInfo)) {
                throw new IllegalAnnotationException(Messages.SCOPE_IS_NOT_COMPLEXTYPE.format(this.nav().getTypeName(s)), this.anno);
            }
            this.scope = (ClassInfo)scp;
        }
        this.id = this.calcId();
        this.property = this.createPropertyImpl();
        this.expectedMimeType = Util.calcExpectedMediaType(this.property, builder);
        this.inlineBinary = this.reader().hasMethodAnnotation(XmlInlineBinaryData.class, this.method);
        this.schemaType = Util.calcSchemaType(this.reader(), this.property, registry.registryClass, this.getContentInMemoryType(), this);
    }
    
    final QName parseElementName(final XmlElementDecl e) {
        final String local = e.name();
        String nsUri = e.namespace();
        if (nsUri.equals("##default")) {
            final XmlSchema xs = this.reader().getPackageAnnotation(XmlSchema.class, this.nav().getDeclaringClassForMethod(this.method), this);
            if (xs != null) {
                nsUri = xs.namespace();
            }
            else {
                nsUri = this.builder.defaultNsUri;
            }
        }
        return new QName(nsUri.intern(), local.intern());
    }
    
    protected PropertyImpl createPropertyImpl() {
        return new PropertyImpl();
    }
    
    @Override
    public ElementPropertyInfo<T, C> getProperty() {
        return this.property;
    }
    
    @Override
    public NonElement<T, C> getContentType() {
        return this.contentType;
    }
    
    @Override
    public T getContentInMemoryType() {
        if (this.adapter == null) {
            return this.tOfJAXBElementT;
        }
        return this.adapter.customType;
    }
    
    @Override
    public QName getElementName() {
        return this.tagName;
    }
    
    @Override
    public T getType() {
        return this.elementType;
    }
    
    @Override
    @Deprecated
    public final boolean canBeReferencedByIDREF() {
        return false;
    }
    
    private ID calcId() {
        if (this.reader().hasMethodAnnotation(XmlID.class, this.method)) {
            return ID.ID;
        }
        if (this.reader().hasMethodAnnotation(XmlIDREF.class, this.method)) {
            return ID.IDREF;
        }
        return ID.NONE;
    }
    
    @Override
    public ClassInfo<T, C> getScope() {
        return this.scope;
    }
    
    @Override
    public ElementInfo<T, C> getSubstitutionHead() {
        return this.substitutionHead;
    }
    
    @Override
    public Collection<? extends ElementInfoImpl<T, C, F, M>> getSubstitutionMembers() {
        if (this.substitutionMembers == null) {
            return (Collection<? extends ElementInfoImpl<T, C, F, M>>)Collections.emptyList();
        }
        return this.substitutionMembers;
    }
    
    @Override
    void link() {
        if (this.anno.substitutionHeadName().length() != 0) {
            final QName name = new QName(this.anno.substitutionHeadNamespace(), this.anno.substitutionHeadName());
            this.substitutionHead = this.owner.getElementInfo((C)null, name);
            if (this.substitutionHead == null) {
                this.builder.reportError(new IllegalAnnotationException(Messages.NON_EXISTENT_ELEMENT_MAPPING.format(name.getNamespaceURI(), name.getLocalPart()), this.anno));
            }
            else {
                this.substitutionHead.addSubstitutionMember(this);
            }
        }
        else {
            this.substitutionHead = null;
        }
        super.link();
    }
    
    private void addSubstitutionMember(final ElementInfoImpl<T, C, F, M> child) {
        if (this.substitutionMembers == null) {
            this.substitutionMembers = new FinalArrayList<ElementInfoImpl<T, C, F, M>>();
        }
        this.substitutionMembers.add(child);
    }
    
    @Override
    public Location getLocation() {
        return this.nav().getMethodLocation(this.method);
    }
    
    protected class PropertyImpl implements ElementPropertyInfo<T, C>, TypeRef<T, C>, AnnotationSource
    {
        @Override
        public NonElement<T, C> getTarget() {
            return ElementInfoImpl.this.contentType;
        }
        
        @Override
        public QName getTagName() {
            return ElementInfoImpl.this.tagName;
        }
        
        @Override
        public List<? extends TypeRef<T, C>> getTypes() {
            return Collections.singletonList(this);
        }
        
        @Override
        public List<? extends NonElement<T, C>> ref() {
            return Collections.singletonList(ElementInfoImpl.this.contentType);
        }
        
        @Override
        public QName getXmlName() {
            return ElementInfoImpl.this.tagName;
        }
        
        @Override
        public boolean isCollectionRequired() {
            return false;
        }
        
        @Override
        public boolean isCollectionNillable() {
            return true;
        }
        
        @Override
        public boolean isNillable() {
            return true;
        }
        
        @Override
        public String getDefaultValue() {
            final String v = ElementInfoImpl.this.anno.defaultValue();
            if (v.equals("\u0000")) {
                return null;
            }
            return v;
        }
        
        @Override
        public ElementInfoImpl<T, C, F, M> parent() {
            return ElementInfoImpl.this;
        }
        
        @Override
        public String getName() {
            return "value";
        }
        
        @Override
        public String displayName() {
            return "JAXBElement#value";
        }
        
        @Override
        public boolean isCollection() {
            return ElementInfoImpl.this.isCollection;
        }
        
        @Override
        public boolean isValueList() {
            return ElementInfoImpl.this.isCollection;
        }
        
        @Override
        public boolean isRequired() {
            return true;
        }
        
        @Override
        public PropertyKind kind() {
            return PropertyKind.ELEMENT;
        }
        
        @Override
        public Adapter<T, C> getAdapter() {
            return ElementInfoImpl.this.adapter;
        }
        
        @Override
        public ID id() {
            return ElementInfoImpl.this.id;
        }
        
        @Override
        public MimeType getExpectedMimeType() {
            return ElementInfoImpl.this.expectedMimeType;
        }
        
        @Override
        public QName getSchemaType() {
            return ElementInfoImpl.this.schemaType;
        }
        
        @Override
        public boolean inlineBinaryData() {
            return ElementInfoImpl.this.inlineBinary;
        }
        
        @Override
        public PropertyInfo<T, C> getSource() {
            return this;
        }
        
        @Override
        public <A extends Annotation> A readAnnotation(final Class<A> annotationType) {
            return ElementInfoImpl.this.reader().getMethodAnnotation(annotationType, ElementInfoImpl.this.method, ElementInfoImpl.this);
        }
        
        @Override
        public boolean hasAnnotation(final Class<? extends Annotation> annotationType) {
            return ElementInfoImpl.this.reader().hasMethodAnnotation(annotationType, ElementInfoImpl.this.method);
        }
    }
}
