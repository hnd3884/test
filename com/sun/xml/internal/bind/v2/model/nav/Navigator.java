package com.sun.xml.internal.bind.v2.model.nav;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;

public interface Navigator<T, C, F, M>
{
    C getSuperClass(final C p0);
    
    T getBaseClass(final T p0, final C p1);
    
    String getClassName(final C p0);
    
    String getTypeName(final T p0);
    
    String getClassShortName(final C p0);
    
    Collection<? extends F> getDeclaredFields(final C p0);
    
    F getDeclaredField(final C p0, final String p1);
    
    Collection<? extends M> getDeclaredMethods(final C p0);
    
    C getDeclaringClassForField(final F p0);
    
    C getDeclaringClassForMethod(final M p0);
    
    T getFieldType(final F p0);
    
    String getFieldName(final F p0);
    
    String getMethodName(final M p0);
    
    T getReturnType(final M p0);
    
    T[] getMethodParameters(final M p0);
    
    boolean isStaticMethod(final M p0);
    
    boolean isSubClassOf(final T p0, final T p1);
    
    T ref(final Class p0);
    
    T use(final C p0);
    
    C asDecl(final T p0);
    
    C asDecl(final Class p0);
    
    boolean isArray(final T p0);
    
    boolean isArrayButNotByteArray(final T p0);
    
    T getComponentType(final T p0);
    
    T getTypeArgument(final T p0, final int p1);
    
    boolean isParameterizedType(final T p0);
    
    boolean isPrimitive(final T p0);
    
    T getPrimitive(final Class p0);
    
    Location getClassLocation(final C p0);
    
    Location getFieldLocation(final F p0);
    
    Location getMethodLocation(final M p0);
    
    boolean hasDefaultConstructor(final C p0);
    
    boolean isStaticField(final F p0);
    
    boolean isPublicMethod(final M p0);
    
    boolean isFinalMethod(final M p0);
    
    boolean isPublicField(final F p0);
    
    boolean isEnum(final C p0);
    
     <P> T erasure(final T p0);
    
    boolean isAbstract(final C p0);
    
    boolean isFinal(final C p0);
    
    F[] getEnumConstants(final C p0);
    
    T getVoidType();
    
    String getPackageName(final C p0);
    
    C loadObjectFactory(final C p0, final String p1);
    
    boolean isBridgeMethod(final M p0);
    
    boolean isOverriding(final M p0, final C p1);
    
    boolean isInterface(final C p0);
    
    boolean isTransient(final F p0);
    
    boolean isInnerClass(final C p0);
    
    boolean isSameType(final T p0, final T p1);
}
