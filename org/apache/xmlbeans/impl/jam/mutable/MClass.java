package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.JProperty;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JClass;

public interface MClass extends MMember, JClass
{
    void setIsInterface(final boolean p0);
    
    void setIsAnnotationType(final boolean p0);
    
    void setIsEnumType(final boolean p0);
    
    void setSuperclass(final String p0);
    
    void setSuperclassUnqualified(final String p0);
    
    void setSuperclass(final JClass p0);
    
    void addInterface(final String p0);
    
    void addInterfaceUnqualified(final String p0);
    
    void addInterface(final JClass p0);
    
    void removeInterface(final String p0);
    
    void removeInterface(final JClass p0);
    
    MConstructor addNewConstructor();
    
    void removeConstructor(final MConstructor p0);
    
    MConstructor[] getMutableConstructors();
    
    MField addNewField();
    
    void removeField(final MField p0);
    
    MField[] getMutableFields();
    
    MMethod addNewMethod();
    
    void removeMethod(final MMethod p0);
    
    MMethod[] getMutableMethods();
    
    JProperty addNewProperty(final String p0, final JMethod p1, final JMethod p2);
    
    void removeProperty(final JProperty p0);
    
    JProperty addNewDeclaredProperty(final String p0, final JMethod p1, final JMethod p2);
    
    void removeDeclaredProperty(final JProperty p0);
    
    MClass addNewInnerClass(final String p0);
    
    void removeInnerClass(final MClass p0);
}
