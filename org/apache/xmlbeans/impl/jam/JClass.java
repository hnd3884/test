package org.apache.xmlbeans.impl.jam;

public interface JClass extends JMember
{
    JPackage getContainingPackage();
    
    JClass getSuperclass();
    
    JClass[] getInterfaces();
    
    JField[] getFields();
    
    JField[] getDeclaredFields();
    
    JMethod[] getMethods();
    
    JMethod[] getDeclaredMethods();
    
    JConstructor[] getConstructors();
    
    JProperty[] getProperties();
    
    JProperty[] getDeclaredProperties();
    
    boolean isInterface();
    
    boolean isAnnotationType();
    
    boolean isPrimitiveType();
    
    boolean isBuiltinType();
    
    Class getPrimitiveClass();
    
    boolean isFinal();
    
    boolean isStatic();
    
    boolean isAbstract();
    
    boolean isVoidType();
    
    boolean isObjectType();
    
    boolean isArrayType();
    
    JClass getArrayComponentType();
    
    int getArrayDimensions();
    
    boolean isAssignableFrom(final JClass p0);
    
    boolean equals(final Object p0);
    
    JClass[] getClasses();
    
    JClass getContainingClass();
    
    String getFieldDescriptor();
    
    boolean isEnumType();
    
    JamClassLoader getClassLoader();
    
    JClass forName(final String p0);
    
    JClass[] getImportedClasses();
    
    JPackage[] getImportedPackages();
    
    boolean isUnresolvedType();
}
