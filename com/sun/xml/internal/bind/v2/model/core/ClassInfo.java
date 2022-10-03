package com.sun.xml.internal.bind.v2.model.core;

import java.util.List;

public interface ClassInfo<T, C> extends MaybeElement<T, C>
{
    ClassInfo<T, C> getBaseClass();
    
    C getClazz();
    
    String getName();
    
    List<? extends PropertyInfo<T, C>> getProperties();
    
    boolean hasValueProperty();
    
    PropertyInfo<T, C> getProperty(final String p0);
    
    boolean hasProperties();
    
    boolean isAbstract();
    
    boolean isOrdered();
    
    boolean isFinal();
    
    boolean hasSubClasses();
    
    boolean hasAttributeWildcard();
    
    boolean inheritsAttributeWildcard();
    
    boolean declaresAttributeWildcard();
}
