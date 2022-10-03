package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.model.annotation.FieldLocatable;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import java.util.EnumMap;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.util.Map;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class RuntimeEnumLeafInfoImpl<T extends Enum<T>, B> extends EnumLeafInfoImpl<Type, Class, Field, Method> implements RuntimeEnumLeafInfo, Transducer<T>
{
    private final Transducer<B> baseXducer;
    private final Map<B, T> parseMap;
    private final Map<T, B> printMap;
    
    @Override
    public Transducer<T> getTransducer() {
        return this;
    }
    
    RuntimeEnumLeafInfoImpl(final RuntimeModelBuilder builder, final Locatable upstream, final Class<T> enumType) {
        super((ModelBuilder<Class<T>, Class<T>, Object, Object>)builder, upstream, enumType, enumType);
        this.parseMap = new HashMap<B, T>();
        this.printMap = new EnumMap<T, B>(enumType);
        this.baseXducer = ((RuntimeNonElement)this.baseType).getTransducer();
    }
    
    public RuntimeEnumConstantImpl createEnumConstant(final String name, final String literal, final Field constant, final EnumConstantImpl<Type, Class, Field, Method> last) {
        T t;
        try {
            try {
                constant.setAccessible(true);
            }
            catch (final SecurityException ex) {}
            t = (T)constant.get(null);
        }
        catch (final IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        B b = null;
        try {
            b = this.baseXducer.parse(literal);
        }
        catch (final Exception e2) {
            this.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ENUM_VALUE.format(literal, this.baseType.getType().toString()), e2, new FieldLocatable<Object>(this, constant, this.nav())));
        }
        this.parseMap.put(b, t);
        this.printMap.put(t, b);
        return new RuntimeEnumConstantImpl(this, name, literal, last);
    }
    
    @Override
    public QName[] getTypeNames() {
        return new QName[] { this.getTypeName() };
    }
    
    @Override
    public boolean isDefault() {
        return false;
    }
    
    @Override
    public Class getClazz() {
        return (Class)this.clazz;
    }
    
    @Override
    public boolean useNamespace() {
        return this.baseXducer.useNamespace();
    }
    
    @Override
    public void declareNamespace(final T t, final XMLSerializer w) throws AccessorException {
        this.baseXducer.declareNamespace(this.printMap.get(t), w);
    }
    
    @Override
    public CharSequence print(final T t) throws AccessorException {
        return this.baseXducer.print(this.printMap.get(t));
    }
    
    @Override
    public T parse(final CharSequence lexical) throws AccessorException, SAXException {
        B b = this.baseXducer.parse(lexical);
        if (this.tokenStringType) {
            b = (B)((String)b).trim();
        }
        return this.parseMap.get(b);
    }
    
    @Override
    public void writeText(final XMLSerializer w, final T t, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        this.baseXducer.writeText(w, this.printMap.get(t), fieldName);
    }
    
    @Override
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        this.baseXducer.writeLeafElement(w, tagName, this.printMap.get(o), fieldName);
    }
    
    @Override
    public QName getTypeName(final T instance) {
        return null;
    }
}
