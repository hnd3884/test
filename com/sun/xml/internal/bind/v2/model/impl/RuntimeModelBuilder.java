package com.sun.xml.internal.bind.v2.model.impl;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.v2.runtime.FilterTransducer;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import javax.xml.namespace.QName;
import javax.activation.MimeType;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.SchemaTypeTransducer;
import com.sun.xml.internal.bind.v2.runtime.InlineBinaryTransducer;
import com.sun.xml.internal.bind.v2.runtime.MimeTypedTransducer;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import java.util.Map;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class RuntimeModelBuilder extends ModelBuilder<Type, Class, Field, Method>
{
    @Nullable
    public final JAXBContextImpl context;
    
    public RuntimeModelBuilder(final JAXBContextImpl context, final RuntimeAnnotationReader annotationReader, final Map<Class, Class> subclassReplacements, final String defaultNamespaceRemap) {
        super(annotationReader, Utils.REFLECTION_NAVIGATOR, subclassReplacements, defaultNamespaceRemap);
        this.context = context;
    }
    
    @Override
    public RuntimeNonElement getClassInfo(final Class clazz, final Locatable upstream) {
        return (RuntimeNonElement)super.getClassInfo(clazz, upstream);
    }
    
    @Override
    public RuntimeNonElement getClassInfo(final Class clazz, final boolean searchForSuperClass, final Locatable upstream) {
        return (RuntimeNonElement)super.getClassInfo(clazz, searchForSuperClass, upstream);
    }
    
    @Override
    protected RuntimeEnumLeafInfoImpl createEnumLeafInfo(final Class clazz, final Locatable upstream) {
        return new RuntimeEnumLeafInfoImpl(this, upstream, clazz);
    }
    
    @Override
    protected RuntimeClassInfoImpl createClassInfo(final Class clazz, final Locatable upstream) {
        return new RuntimeClassInfoImpl(this, upstream, clazz);
    }
    
    public RuntimeElementInfoImpl createElementInfo(final RegistryInfoImpl<Type, Class, Field, Method> registryInfo, final Method method) throws IllegalAnnotationException {
        return new RuntimeElementInfoImpl(this, (RegistryInfoImpl)registryInfo, method);
    }
    
    public RuntimeArrayInfoImpl createArrayInfo(final Locatable upstream, final Type arrayType) {
        return new RuntimeArrayInfoImpl(this, upstream, (Class)arrayType);
    }
    
    @Override
    protected RuntimeTypeInfoSetImpl createTypeInfoSet() {
        return new RuntimeTypeInfoSetImpl((AnnotationReader<Type, Class, Field, Method>)this.reader);
    }
    
    @Override
    public RuntimeTypeInfoSet link() {
        return (RuntimeTypeInfoSet)super.link();
    }
    
    public static Transducer createTransducer(final RuntimeNonElementRef ref) {
        Transducer t = ref.getTarget().getTransducer();
        final RuntimePropertyInfo src = ref.getSource();
        final ID id = src.id();
        if (id == ID.IDREF) {
            return RuntimeBuiltinLeafInfoImpl.STRING;
        }
        if (id == ID.ID) {
            t = new IDTransducerImpl(t);
        }
        final MimeType emt = src.getExpectedMimeType();
        if (emt != null) {
            t = new MimeTypedTransducer(t, emt);
        }
        if (src.inlineBinaryData()) {
            t = new InlineBinaryTransducer(t);
        }
        if (src.getSchemaType() != null) {
            if (src.getSchemaType().equals(createXSSimpleType())) {
                return RuntimeBuiltinLeafInfoImpl.STRING;
            }
            t = new SchemaTypeTransducer(t, src.getSchemaType());
        }
        return t;
    }
    
    private static QName createXSSimpleType() {
        return new QName("http://www.w3.org/2001/XMLSchema", "anySimpleType");
    }
    
    private static final class IDTransducerImpl<ValueT> extends FilterTransducer<ValueT>
    {
        public IDTransducerImpl(final Transducer<ValueT> core) {
            super(core);
        }
        
        @Override
        public ValueT parse(final CharSequence lexical) throws AccessorException, SAXException {
            final String value = WhiteSpaceProcessor.trim(lexical).toString();
            UnmarshallingContext.getInstance().addToIdTable(value);
            return this.core.parse(value);
        }
    }
}
