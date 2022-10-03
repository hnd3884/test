package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Constructor;

public abstract class PropertyFactory
{
    private static final Constructor<? extends Property>[] propImpls;
    
    private PropertyFactory() {
    }
    
    public static Property create(final JAXBContextImpl grammar, final RuntimePropertyInfo info) {
        final PropertyKind kind = info.kind();
        switch (kind) {
            case ATTRIBUTE: {
                return new AttributeProperty(grammar, (RuntimeAttributePropertyInfo)info);
            }
            case VALUE: {
                return new ValueProperty(grammar, (RuntimeValuePropertyInfo)info);
            }
            case ELEMENT: {
                if (((RuntimeElementPropertyInfo)info).isValueList()) {
                    return new ListElementProperty(grammar, (RuntimeElementPropertyInfo)info);
                }
                break;
            }
            case REFERENCE:
            case MAP: {
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        final boolean isCollection = info.isCollection();
        final boolean isLeaf = isLeaf(info);
        final Constructor<? extends Property> c = PropertyFactory.propImpls[(isLeaf ? 0 : 6) + (isCollection ? 3 : 0) + kind.propertyIndex];
        try {
            return (Property)c.newInstance(grammar, info);
        }
        catch (final InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }
        catch (final IllegalAccessException e2) {
            throw new IllegalAccessError(e2.getMessage());
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new AssertionError((Object)t);
        }
    }
    
    static boolean isLeaf(final RuntimePropertyInfo info) {
        final Collection<? extends RuntimeTypeInfo> types = info.ref();
        if (types.size() != 1) {
            return false;
        }
        final RuntimeTypeInfo rti = (RuntimeTypeInfo)types.iterator().next();
        return rti instanceof RuntimeNonElement && (info.id() == ID.IDREF || (((RuntimeNonElement)rti).getTransducer() != null && info.getIndividualType().equals(((TypeInfo<Object, C>)rti).getType())));
    }
    
    static {
        final Class<? extends Property>[] implClasses = { SingleElementLeafProperty.class, null, null, ArrayElementLeafProperty.class, null, null, SingleElementNodeProperty.class, SingleReferenceNodeProperty.class, SingleMapNodeProperty.class, ArrayElementNodeProperty.class, ArrayReferenceNodeProperty.class, null };
        propImpls = new Constructor[implClasses.length];
        for (int i = 0; i < PropertyFactory.propImpls.length; ++i) {
            if (implClasses[i] != null) {
                PropertyFactory.propImpls[i] = (Constructor<? extends Property>)implClasses[i].getConstructors()[0];
            }
        }
    }
}
