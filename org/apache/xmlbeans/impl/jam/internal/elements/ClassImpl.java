package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.internal.JamClassLoaderImpl;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.internal.classrefs.UnqualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.JSourcePosition;
import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import java.util.Set;
import java.util.TreeSet;
import java.lang.reflect.Modifier;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.JProperty;
import org.apache.xmlbeans.impl.jam.JMethod;
import java.util.List;
import java.util.Collection;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JPackage;
import org.apache.xmlbeans.impl.jam.provider.JamClassPopulator;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRefContext;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.mutable.MClass;

public class ClassImpl extends MemberImpl implements MClass, JClassRef, JClassRefContext
{
    public static final int NEW = 1;
    public static final int UNPOPULATED = 2;
    public static final int POPULATING = 3;
    public static final int UNINITIALIZED = 4;
    public static final int INITIALIZING = 5;
    public static final int LOADED = 6;
    private int mState;
    private boolean mIsAnnotationType;
    private boolean mIsInterface;
    private boolean mIsEnum;
    private String mPackageName;
    private JClassRef mSuperClassRef;
    private ArrayList mInterfaceRefs;
    private ArrayList mFields;
    private ArrayList mMethods;
    private ArrayList mConstructors;
    private ArrayList mProperties;
    private ArrayList mDeclaredProperties;
    private ArrayList mInnerClasses;
    private String[] mImports;
    private JamClassPopulator mPopulator;
    
    public ClassImpl(final String packageName, final String simpleName, final ElementContext ctx, final String[] importSpecs, final JamClassPopulator populator) {
        super(ctx);
        this.mState = 1;
        this.mIsAnnotationType = false;
        this.mIsInterface = false;
        this.mIsEnum = false;
        this.mPackageName = null;
        this.mSuperClassRef = null;
        this.mInterfaceRefs = null;
        this.mFields = null;
        this.mMethods = null;
        this.mConstructors = null;
        this.mProperties = null;
        this.mDeclaredProperties = null;
        this.mInnerClasses = null;
        this.mImports = null;
        super.setSimpleName(simpleName);
        this.mPackageName = packageName.trim();
        this.mImports = importSpecs;
        this.mPopulator = populator;
        this.setState(2);
    }
    
    public ClassImpl(final String packageName, final String simpleName, final ElementContext ctx, final String[] importSpecs) {
        super(ctx);
        this.mState = 1;
        this.mIsAnnotationType = false;
        this.mIsInterface = false;
        this.mIsEnum = false;
        this.mPackageName = null;
        this.mSuperClassRef = null;
        this.mInterfaceRefs = null;
        this.mFields = null;
        this.mMethods = null;
        this.mConstructors = null;
        this.mProperties = null;
        this.mDeclaredProperties = null;
        this.mInnerClasses = null;
        this.mImports = null;
        super.setSimpleName(simpleName);
        this.mPackageName = packageName.trim();
        this.mImports = importSpecs;
        this.mPopulator = null;
        this.setState(4);
    }
    
    private ClassImpl(final String packageName, final String simpleName, final String[] importSpecs, final ClassImpl parent) {
        super(parent);
        this.mState = 1;
        this.mIsAnnotationType = false;
        this.mIsInterface = false;
        this.mIsEnum = false;
        this.mPackageName = null;
        this.mSuperClassRef = null;
        this.mInterfaceRefs = null;
        this.mFields = null;
        this.mMethods = null;
        this.mConstructors = null;
        this.mProperties = null;
        this.mDeclaredProperties = null;
        this.mInnerClasses = null;
        this.mImports = null;
        super.setSimpleName(simpleName);
        this.mPackageName = packageName.trim();
        this.mImports = importSpecs;
        this.mPopulator = null;
        this.setState(4);
    }
    
    @Override
    public JPackage getContainingPackage() {
        return this.getClassLoader().getPackage(this.mPackageName);
    }
    
    @Override
    public JClass getSuperclass() {
        this.ensureLoaded();
        if (this.mSuperClassRef == null) {
            return null;
        }
        return this.mSuperClassRef.getRefClass();
    }
    
    @Override
    public JClass[] getInterfaces() {
        this.ensureLoaded();
        if (this.mInterfaceRefs == null || this.mInterfaceRefs.size() == 0) {
            return new JClass[0];
        }
        final JClass[] out = new JClass[this.mInterfaceRefs.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = this.mInterfaceRefs.get(i).getRefClass();
        }
        return out;
    }
    
    @Override
    public JField[] getFields() {
        this.ensureLoaded();
        final List list = new ArrayList();
        this.addFieldsRecursively(this, list);
        final JField[] out = new JField[list.size()];
        list.toArray(out);
        return out;
    }
    
    @Override
    public JField[] getDeclaredFields() {
        this.ensureLoaded();
        return this.getMutableFields();
    }
    
    @Override
    public JMethod[] getMethods() {
        this.ensureLoaded();
        final List list = new ArrayList();
        this.addMethodsRecursively(this, list);
        final JMethod[] out = new JMethod[list.size()];
        list.toArray(out);
        return out;
    }
    
    @Override
    public JProperty[] getProperties() {
        this.ensureLoaded();
        if (this.mProperties == null) {
            return new JProperty[0];
        }
        final JProperty[] out = new JProperty[this.mProperties.size()];
        this.mProperties.toArray(out);
        return out;
    }
    
    @Override
    public JProperty[] getDeclaredProperties() {
        this.ensureLoaded();
        if (this.mDeclaredProperties == null) {
            return new JProperty[0];
        }
        final JProperty[] out = new JProperty[this.mDeclaredProperties.size()];
        this.mDeclaredProperties.toArray(out);
        return out;
    }
    
    @Override
    public JMethod[] getDeclaredMethods() {
        this.ensureLoaded();
        return this.getMutableMethods();
    }
    
    @Override
    public JConstructor[] getConstructors() {
        this.ensureLoaded();
        return this.getMutableConstructors();
    }
    
    @Override
    public boolean isInterface() {
        this.ensureLoaded();
        return this.mIsInterface;
    }
    
    @Override
    public boolean isAnnotationType() {
        this.ensureLoaded();
        return this.mIsAnnotationType;
    }
    
    @Override
    public boolean isEnumType() {
        this.ensureLoaded();
        return this.mIsEnum;
    }
    
    @Override
    public int getModifiers() {
        this.ensureLoaded();
        return super.getModifiers();
    }
    
    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }
    
    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }
    
    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }
    
    @Override
    public boolean isAssignableFrom(final JClass arg) {
        this.ensureLoaded();
        if (this.isPrimitiveType() || arg.isPrimitiveType()) {
            return this.getQualifiedName().equals(arg.getQualifiedName());
        }
        return this.isAssignableFromRecursively(arg);
    }
    
    @Override
    public JClass[] getClasses() {
        this.ensureLoaded();
        if (this.mInnerClasses == null) {
            return new JClass[0];
        }
        final JClass[] out = new JClass[this.mInnerClasses.size()];
        this.mInnerClasses.toArray(out);
        return out;
    }
    
    @Override
    public String getFieldDescriptor() {
        return this.getQualifiedName();
    }
    
    @Override
    public JClass forName(final String name) {
        return this.getClassLoader().loadClass(name);
    }
    
    @Override
    public JPackage[] getImportedPackages() {
        this.ensureLoaded();
        final Set set = new TreeSet();
        final JClass[] importedClasses = this.getImportedClasses();
        for (int i = 0; i < importedClasses.length; ++i) {
            final JPackage c = importedClasses[i].getContainingPackage();
            if (c != null) {
                set.add(c);
            }
        }
        final String[] imports = this.getImportSpecs();
        if (imports != null) {
            for (int j = 0; j < imports.length; ++j) {
                if (imports[j].endsWith(".*")) {
                    set.add(this.getClassLoader().getPackage(imports[j].substring(0, imports[j].length() - 2)));
                }
            }
        }
        final JPackage[] array = new JPackage[set.size()];
        set.toArray(array);
        return array;
    }
    
    @Override
    public JClass[] getImportedClasses() {
        this.ensureLoaded();
        final String[] imports = this.getImportSpecs();
        if (imports == null) {
            return new JClass[0];
        }
        final List list = new ArrayList();
        for (int i = 0; i < imports.length; ++i) {
            if (!imports[i].endsWith("*")) {
                list.add(this.getClassLoader().loadClass(imports[i]));
            }
        }
        final JClass[] out = new JClass[list.size()];
        list.toArray(out);
        return out;
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
    public void setSimpleName(final String name) {
        throw new UnsupportedOperationException("Class names cannot be changed");
    }
    
    @Override
    public Class getPrimitiveClass() {
        return null;
    }
    
    @Override
    public boolean isPrimitiveType() {
        return false;
    }
    
    @Override
    public boolean isBuiltinType() {
        return false;
    }
    
    @Override
    public boolean isVoidType() {
        return false;
    }
    
    @Override
    public boolean isUnresolvedType() {
        return false;
    }
    
    @Override
    public boolean isObjectType() {
        return this.getQualifiedName().equals("java.lang.Object");
    }
    
    @Override
    public boolean isArrayType() {
        return false;
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
    public JAnnotation[] getAnnotations() {
        this.ensureLoaded();
        return super.getAnnotations();
    }
    
    @Override
    public JAnnotation getAnnotation(final Class proxyClass) {
        this.ensureLoaded();
        return super.getAnnotation(proxyClass);
    }
    
    @Override
    public JAnnotation getAnnotation(final String named) {
        this.ensureLoaded();
        return super.getAnnotation(named);
    }
    
    @Override
    public JAnnotationValue getAnnotationValue(final String valueId) {
        this.ensureLoaded();
        return super.getAnnotationValue(valueId);
    }
    
    @Override
    public Object getAnnotationProxy(final Class proxyClass) {
        this.ensureLoaded();
        return super.getAnnotationProxy(proxyClass);
    }
    
    @Override
    public JComment getComment() {
        this.ensureLoaded();
        return super.getComment();
    }
    
    @Override
    public JAnnotation[] getAllJavadocTags() {
        this.ensureLoaded();
        return super.getAllJavadocTags();
    }
    
    @Override
    public JSourcePosition getSourcePosition() {
        this.ensureLoaded();
        return super.getSourcePosition();
    }
    
    @Override
    public void setSuperclass(final String qualifiedClassName) {
        if (qualifiedClassName == null) {
            this.mSuperClassRef = null;
        }
        else {
            if (qualifiedClassName.equals(this.getQualifiedName())) {
                throw new IllegalArgumentException("A class cannot be it's own superclass: '" + qualifiedClassName + "'");
            }
            this.mSuperClassRef = QualifiedJClassRef.create(qualifiedClassName, this);
        }
    }
    
    @Override
    public void setSuperclassUnqualified(final String unqualifiedClassName) {
        this.mSuperClassRef = UnqualifiedJClassRef.create(unqualifiedClassName, this);
    }
    
    @Override
    public void setSuperclass(final JClass clazz) {
        if (clazz == null) {
            this.mSuperClassRef = null;
        }
        else {
            this.setSuperclass(clazz.getQualifiedName());
        }
    }
    
    @Override
    public void addInterface(final JClass interf) {
        if (interf == null) {
            throw new IllegalArgumentException("null interf");
        }
        this.addInterface(interf.getQualifiedName());
    }
    
    @Override
    public void addInterface(final String qcName) {
        if (this.mInterfaceRefs == null) {
            this.mInterfaceRefs = new ArrayList();
        }
        if (qcName.equals(this.getQualifiedName())) {
            throw new IllegalArgumentException("A class cannot implement itself: '" + qcName + "'");
        }
        this.mInterfaceRefs.add(QualifiedJClassRef.create(qcName, this));
    }
    
    @Override
    public void addInterfaceUnqualified(final String ucname) {
        if (this.mInterfaceRefs == null) {
            this.mInterfaceRefs = new ArrayList();
        }
        this.mInterfaceRefs.add(UnqualifiedJClassRef.create(ucname, this));
    }
    
    @Override
    public void removeInterface(final JClass interf) {
        if (interf == null) {
            throw new IllegalArgumentException("null interf");
        }
        this.removeInterface(interf.getQualifiedName());
    }
    
    @Override
    public void removeInterface(final String qcname) {
        if (qcname == null) {
            throw new IllegalArgumentException("null classname");
        }
        if (this.mInterfaceRefs == null) {
            return;
        }
        for (int i = 0; i < this.mInterfaceRefs.size(); ++i) {
            if (qcname.equals(this.mInterfaceRefs.get(i).getQualifiedName())) {
                this.mInterfaceRefs.remove(i);
            }
        }
    }
    
    @Override
    public MConstructor addNewConstructor() {
        if (this.mConstructors == null) {
            this.mConstructors = new ArrayList();
        }
        final MConstructor out = new ConstructorImpl(this);
        this.mConstructors.add(out);
        return out;
    }
    
    @Override
    public void removeConstructor(final MConstructor constr) {
        if (this.mConstructors == null) {
            return;
        }
        this.mConstructors.remove(constr);
    }
    
    @Override
    public MConstructor[] getMutableConstructors() {
        if (this.mConstructors == null || this.mConstructors.size() == 0) {
            return new MConstructor[0];
        }
        final MConstructor[] out = new MConstructor[this.mConstructors.size()];
        this.mConstructors.toArray(out);
        return out;
    }
    
    @Override
    public MField addNewField() {
        if (this.mFields == null) {
            this.mFields = new ArrayList();
        }
        final MField out = new FieldImpl(ElementImpl.defaultName(this.mFields.size()), this, "java.lang.Object");
        this.mFields.add(out);
        return out;
    }
    
    @Override
    public void removeField(final MField field) {
        if (this.mFields == null) {
            return;
        }
        this.mFields.remove(field);
    }
    
    @Override
    public MField[] getMutableFields() {
        if (this.mFields == null || this.mFields.size() == 0) {
            return new MField[0];
        }
        final MField[] out = new MField[this.mFields.size()];
        this.mFields.toArray(out);
        return out;
    }
    
    @Override
    public MMethod addNewMethod() {
        if (this.mMethods == null) {
            this.mMethods = new ArrayList();
        }
        final MMethod out = new MethodImpl(ElementImpl.defaultName(this.mMethods.size()), this);
        this.mMethods.add(out);
        return out;
    }
    
    @Override
    public void removeMethod(final MMethod method) {
        if (this.mMethods == null) {
            return;
        }
        this.mMethods.remove(method);
    }
    
    @Override
    public MMethod[] getMutableMethods() {
        if (this.mMethods == null || this.mMethods.size() == 0) {
            return new MMethod[0];
        }
        final MMethod[] out = new MMethod[this.mMethods.size()];
        this.mMethods.toArray(out);
        return out;
    }
    
    @Override
    public JProperty addNewProperty(final String name, final JMethod getter, final JMethod setter) {
        if (this.mProperties == null) {
            this.mProperties = new ArrayList();
        }
        final String typeName = (getter != null) ? getter.getReturnType().getFieldDescriptor() : setter.getParameters()[0].getType().getFieldDescriptor();
        final JProperty out = new PropertyImpl(name, getter, setter, typeName);
        this.mProperties.add(out);
        return out;
    }
    
    @Override
    public void removeProperty(final JProperty p) {
        if (this.mProperties != null) {
            this.mProperties.remove(p);
        }
    }
    
    @Override
    public JProperty addNewDeclaredProperty(final String name, final JMethod getter, final JMethod setter) {
        if (this.mDeclaredProperties == null) {
            this.mDeclaredProperties = new ArrayList();
        }
        final String typeName = (getter != null) ? getter.getReturnType().getFieldDescriptor() : setter.getParameters()[0].getType().getFieldDescriptor();
        final JProperty out = new PropertyImpl(name, getter, setter, typeName);
        this.mDeclaredProperties.add(out);
        return out;
    }
    
    @Override
    public void removeDeclaredProperty(final JProperty p) {
        if (this.mDeclaredProperties != null) {
            this.mDeclaredProperties.remove(p);
        }
    }
    
    @Override
    public MClass addNewInnerClass(String name) {
        int lastDot = name.lastIndexOf(46);
        if (lastDot == -1) {
            lastDot = name.lastIndexOf(36);
        }
        if (lastDot != -1) {
            name = name.substring(lastDot + 1);
        }
        final ClassImpl inner = new ClassImpl(this.mPackageName, this.getSimpleName() + "$" + name, this.getImportSpecs(), this);
        if (this.mInnerClasses == null) {
            this.mInnerClasses = new ArrayList();
        }
        this.mInnerClasses.add(inner);
        inner.setState(6);
        ((JamClassLoaderImpl)this.getClassLoader()).addToCache(inner);
        return inner;
    }
    
    @Override
    public void removeInnerClass(final MClass clazz) {
        if (this.mInnerClasses == null) {
            return;
        }
        this.mInnerClasses.remove(clazz);
    }
    
    @Override
    public void setIsInterface(final boolean b) {
        this.mIsInterface = b;
    }
    
    @Override
    public void setIsAnnotationType(final boolean b) {
        this.mIsAnnotationType = b;
    }
    
    @Override
    public void setIsEnumType(final boolean b) {
        this.mIsEnum = b;
    }
    
    @Override
    public String getQualifiedName() {
        return ((this.mPackageName.length() > 0) ? (this.mPackageName + '.') : "") + this.mSimpleName;
    }
    
    @Override
    public JClass getRefClass() {
        return this;
    }
    
    @Override
    public String getPackageName() {
        return this.mPackageName;
    }
    
    @Override
    public String[] getImportSpecs() {
        this.ensureLoaded();
        if (this.mImports == null) {
            return new String[0];
        }
        return this.mImports;
    }
    
    public void setState(final int state) {
        this.mState = state;
    }
    
    public static void validateClassName(final String className) throws IllegalArgumentException {
        if (className == null) {
            throw new IllegalArgumentException("null class name specified");
        }
        if (!Character.isJavaIdentifierStart(className.charAt(0))) {
            throw new IllegalArgumentException("Invalid first character in class name: " + className);
        }
        for (int i = 1; i < className.length(); ++i) {
            final char c = className.charAt(i);
            if (c == '.') {
                if (className.charAt(i - 1) == '.') {
                    throw new IllegalArgumentException("'..' not allowed in class name: " + className);
                }
                if (i == className.length() - 1) {
                    throw new IllegalArgumentException("'.' not allowed at end of class name: " + className);
                }
            }
            else if (!Character.isJavaIdentifierPart(c)) {
                throw new IllegalArgumentException("Illegal character '" + c + "' in class name: " + className);
            }
        }
    }
    
    private boolean isAssignableFromRecursively(JClass arg) {
        if (this.getQualifiedName().equals(arg.getQualifiedName())) {
            return true;
        }
        final JClass[] interfaces = arg.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                if (this.isAssignableFromRecursively(interfaces[i])) {
                    return true;
                }
            }
        }
        arg = arg.getSuperclass();
        return arg != null && this.isAssignableFromRecursively(arg);
    }
    
    private void addFieldsRecursively(JClass clazz, final Collection out) {
        final JField[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            out.add(fields[i]);
        }
        final JClass[] ints = clazz.getInterfaces();
        for (int j = 0; j < ints.length; ++j) {
            this.addFieldsRecursively(ints[j], out);
        }
        clazz = clazz.getSuperclass();
        if (clazz != null) {
            this.addFieldsRecursively(clazz, out);
        }
    }
    
    private void addMethodsRecursively(JClass clazz, final Collection out) {
        final JMethod[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            out.add(methods[i]);
        }
        final JClass[] ints = clazz.getInterfaces();
        for (int j = 0; j < ints.length; ++j) {
            this.addMethodsRecursively(ints[j], out);
        }
        clazz = clazz.getSuperclass();
        if (clazz != null) {
            this.addMethodsRecursively(clazz, out);
        }
    }
    
    public void ensureLoaded() {
        if (this.mState == 6) {
            return;
        }
        if (this.mState == 2) {
            if (this.mPopulator == null) {
                throw new IllegalStateException("null populator");
            }
            this.setState(3);
            this.mPopulator.populate(this);
            this.setState(4);
        }
        if (this.mState == 4) {
            this.setState(5);
            ((JamClassLoaderImpl)this.getClassLoader()).initialize(this);
        }
        this.setState(6);
    }
}
