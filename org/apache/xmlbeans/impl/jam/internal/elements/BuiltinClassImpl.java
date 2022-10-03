package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.JProperty;
import org.apache.xmlbeans.impl.jam.JPackage;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.JSourcePosition;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.mutable.MClass;

public abstract class BuiltinClassImpl extends AnnotatedElementImpl implements MClass
{
    protected BuiltinClassImpl(final ElementContext ctx) {
        super(ctx);
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getQualifiedName() {
        return this.mSimpleName;
    }
    
    @Override
    public String getFieldDescriptor() {
        return this.mSimpleName;
    }
    
    @Override
    public int getModifiers() {
        return Object.class.getModifiers();
    }
    
    @Override
    public boolean isPublic() {
        return true;
    }
    
    @Override
    public boolean isPackagePrivate() {
        return false;
    }
    
    @Override
    public boolean isProtected() {
        return false;
    }
    
    @Override
    public boolean isPrivate() {
        return false;
    }
    
    @Override
    public JSourcePosition getSourcePosition() {
        return null;
    }
    
    @Override
    public JClass getContainingClass() {
        return null;
    }
    
    @Override
    public JClass forName(final String fd) {
        return this.getClassLoader().loadClass(fd);
    }
    
    @Override
    public JClass getArrayComponentType() {
        return null;
    }
    
    @Override
    public int getArrayDimensions() {
        return 0;
    }
    
    @Override
    public JClass getSuperclass() {
        return null;
    }
    
    @Override
    public JClass[] getInterfaces() {
        return BuiltinClassImpl.NO_CLASS;
    }
    
    @Override
    public JField[] getFields() {
        return BuiltinClassImpl.NO_FIELD;
    }
    
    @Override
    public JField[] getDeclaredFields() {
        return BuiltinClassImpl.NO_FIELD;
    }
    
    @Override
    public JConstructor[] getConstructors() {
        return BuiltinClassImpl.NO_CONSTRUCTOR;
    }
    
    @Override
    public JMethod[] getMethods() {
        return BuiltinClassImpl.NO_METHOD;
    }
    
    @Override
    public JMethod[] getDeclaredMethods() {
        return BuiltinClassImpl.NO_METHOD;
    }
    
    @Override
    public JPackage getContainingPackage() {
        return null;
    }
    
    @Override
    public boolean isInterface() {
        return false;
    }
    
    @Override
    public boolean isArrayType() {
        return false;
    }
    
    @Override
    public boolean isAnnotationType() {
        return false;
    }
    
    @Override
    public boolean isPrimitiveType() {
        return false;
    }
    
    @Override
    public boolean isBuiltinType() {
        return true;
    }
    
    @Override
    public boolean isUnresolvedType() {
        return false;
    }
    
    @Override
    public boolean isObjectType() {
        return false;
    }
    
    @Override
    public boolean isVoidType() {
        return false;
    }
    
    @Override
    public boolean isEnumType() {
        return false;
    }
    
    @Override
    public Class getPrimitiveClass() {
        return null;
    }
    
    @Override
    public boolean isAbstract() {
        return false;
    }
    
    @Override
    public boolean isFinal() {
        return false;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public JClass[] getClasses() {
        return BuiltinClassImpl.NO_CLASS;
    }
    
    @Override
    public JProperty[] getProperties() {
        return BuiltinClassImpl.NO_PROPERTY;
    }
    
    @Override
    public JProperty[] getDeclaredProperties() {
        return BuiltinClassImpl.NO_PROPERTY;
    }
    
    @Override
    public JPackage[] getImportedPackages() {
        return BuiltinClassImpl.NO_PACKAGE;
    }
    
    @Override
    public JClass[] getImportedClasses() {
        return BuiltinClassImpl.NO_CLASS;
    }
    
    @Override
    public MField[] getMutableFields() {
        return BuiltinClassImpl.NO_FIELD;
    }
    
    @Override
    public MConstructor[] getMutableConstructors() {
        return BuiltinClassImpl.NO_CONSTRUCTOR;
    }
    
    @Override
    public MMethod[] getMutableMethods() {
        return BuiltinClassImpl.NO_METHOD;
    }
    
    @Override
    public void setSimpleName(final String s) {
        this.nocando();
    }
    
    @Override
    public void setIsAnnotationType(final boolean b) {
        this.nocando();
    }
    
    @Override
    public void setIsInterface(final boolean b) {
        this.nocando();
    }
    
    public void setIsUnresolvedType(final boolean b) {
        this.nocando();
    }
    
    @Override
    public void setIsEnumType(final boolean b) {
        this.nocando();
    }
    
    @Override
    public void setSuperclass(final String qualifiedClassName) {
        this.nocando();
    }
    
    @Override
    public void setSuperclassUnqualified(final String unqualifiedClassName) {
        this.nocando();
    }
    
    @Override
    public void setSuperclass(final JClass clazz) {
        this.nocando();
    }
    
    @Override
    public void addInterface(final String className) {
        this.nocando();
    }
    
    @Override
    public void addInterfaceUnqualified(final String unqualifiedClassName) {
        this.nocando();
    }
    
    @Override
    public void addInterface(final JClass interf) {
        this.nocando();
    }
    
    @Override
    public void removeInterface(final String className) {
        this.nocando();
    }
    
    @Override
    public void removeInterface(final JClass interf) {
        this.nocando();
    }
    
    @Override
    public MConstructor addNewConstructor() {
        this.nocando();
        return null;
    }
    
    @Override
    public void removeConstructor(final MConstructor constr) {
        this.nocando();
    }
    
    @Override
    public MField addNewField() {
        this.nocando();
        return null;
    }
    
    @Override
    public void removeField(final MField field) {
        this.nocando();
    }
    
    @Override
    public MMethod addNewMethod() {
        this.nocando();
        return null;
    }
    
    @Override
    public void removeMethod(final MMethod method) {
        this.nocando();
    }
    
    @Override
    public void setModifiers(final int modifiers) {
        this.nocando();
    }
    
    @Override
    public MClass addNewInnerClass(final String named) {
        this.nocando();
        return null;
    }
    
    @Override
    public void removeInnerClass(final MClass inner) {
        this.nocando();
    }
    
    @Override
    public JProperty addNewProperty(final String name, final JMethod m, final JMethod x) {
        this.nocando();
        return null;
    }
    
    @Override
    public void removeProperty(final JProperty prop) {
        this.nocando();
    }
    
    @Override
    public JProperty addNewDeclaredProperty(final String name, final JMethod m, final JMethod x) {
        this.nocando();
        return null;
    }
    
    @Override
    public void removeDeclaredProperty(final JProperty prop) {
        this.nocando();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof JClass && ((JClass)o).getFieldDescriptor().equals(this.getFieldDescriptor());
    }
    
    @Override
    public int hashCode() {
        return this.getFieldDescriptor().hashCode();
    }
    
    protected void reallySetSimpleName(final String name) {
        super.setSimpleName(name);
    }
    
    private void nocando() {
        throw new UnsupportedOperationException("Cannot alter builtin types");
    }
}
