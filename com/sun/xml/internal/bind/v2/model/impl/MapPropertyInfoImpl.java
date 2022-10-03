package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import java.util.Arrays;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.annotation.XmlElementWrapper;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;

class MapPropertyInfoImpl<T, C, F, M> extends PropertyInfoImpl<T, C, F, M> implements MapPropertyInfo<T, C>
{
    private final QName xmlName;
    private boolean nil;
    private final T keyType;
    private final T valueType;
    private NonElement<T, C> keyTypeInfo;
    private NonElement<T, C> valueTypeInfo;
    
    public MapPropertyInfoImpl(final ClassInfoImpl<T, C, F, M> ci, final PropertySeed<T, C, F, M> seed) {
        super(ci, seed);
        final XmlElementWrapper xe = seed.readAnnotation(XmlElementWrapper.class);
        this.xmlName = this.calcXmlName(xe);
        this.nil = (xe != null && xe.nillable());
        final T raw = this.getRawType();
        final T bt = this.nav().getBaseClass(raw, this.nav().asDecl(Map.class));
        assert bt != null;
        if (this.nav().isParameterizedType(bt)) {
            this.keyType = this.nav().getTypeArgument(bt, 0);
            this.valueType = this.nav().getTypeArgument(bt, 1);
        }
        else {
            final T ref = this.nav().ref(Object.class);
            this.valueType = ref;
            this.keyType = ref;
        }
    }
    
    @Override
    public Collection<? extends TypeInfo<T, C>> ref() {
        return Arrays.asList(this.getKeyType(), this.getValueType());
    }
    
    @Override
    public final PropertyKind kind() {
        return PropertyKind.MAP;
    }
    
    @Override
    public QName getXmlName() {
        return this.xmlName;
    }
    
    @Override
    public boolean isCollectionNillable() {
        return this.nil;
    }
    
    @Override
    public NonElement<T, C> getKeyType() {
        if (this.keyTypeInfo == null) {
            this.keyTypeInfo = this.getTarget(this.keyType);
        }
        return this.keyTypeInfo;
    }
    
    @Override
    public NonElement<T, C> getValueType() {
        if (this.valueTypeInfo == null) {
            this.valueTypeInfo = this.getTarget(this.valueType);
        }
        return this.valueTypeInfo;
    }
    
    public NonElement<T, C> getTarget(final T type) {
        assert this.parent.builder != null : "this method must be called during the build stage";
        return this.parent.builder.getTypeInfo(type, this);
    }
}
