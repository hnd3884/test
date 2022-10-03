package com.sun.org.apache.bcel.internal.generic;

import java.util.Iterator;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.SourceFile;
import java.util.ArrayList;
import com.sun.org.apache.bcel.internal.classfile.AccessFlags;

public class ClassGen extends AccessFlags implements Cloneable
{
    private String class_name;
    private String super_class_name;
    private String file_name;
    private int class_name_index;
    private int superclass_name_index;
    private int major;
    private int minor;
    private ConstantPoolGen cp;
    private ArrayList field_vec;
    private ArrayList method_vec;
    private ArrayList attribute_vec;
    private ArrayList interface_vec;
    private ArrayList observers;
    
    public ClassGen(final String class_name, final String super_class_name, final String file_name, final int access_flags, final String[] interfaces, final ConstantPoolGen cp) {
        this.class_name_index = -1;
        this.superclass_name_index = -1;
        this.major = 45;
        this.minor = 3;
        this.field_vec = new ArrayList();
        this.method_vec = new ArrayList();
        this.attribute_vec = new ArrayList();
        this.interface_vec = new ArrayList();
        this.class_name = class_name;
        this.super_class_name = super_class_name;
        this.file_name = file_name;
        this.access_flags = access_flags;
        this.cp = cp;
        if (file_name != null) {
            this.addAttribute(new SourceFile(cp.addUtf8("SourceFile"), 2, cp.addUtf8(file_name), cp.getConstantPool()));
        }
        this.class_name_index = cp.addClass(class_name);
        this.superclass_name_index = cp.addClass(super_class_name);
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                this.addInterface(interfaces[i]);
            }
        }
    }
    
    public ClassGen(final String class_name, final String super_class_name, final String file_name, final int access_flags, final String[] interfaces) {
        this(class_name, super_class_name, file_name, access_flags, interfaces, new ConstantPoolGen());
    }
    
    public ClassGen(final JavaClass clazz) {
        this.class_name_index = -1;
        this.superclass_name_index = -1;
        this.major = 45;
        this.minor = 3;
        this.field_vec = new ArrayList();
        this.method_vec = new ArrayList();
        this.attribute_vec = new ArrayList();
        this.interface_vec = new ArrayList();
        this.class_name_index = clazz.getClassNameIndex();
        this.superclass_name_index = clazz.getSuperclassNameIndex();
        this.class_name = clazz.getClassName();
        this.super_class_name = clazz.getSuperclassName();
        this.file_name = clazz.getSourceFileName();
        this.access_flags = clazz.getAccessFlags();
        this.cp = new ConstantPoolGen(clazz.getConstantPool());
        this.major = clazz.getMajor();
        this.minor = clazz.getMinor();
        final Attribute[] attributes = clazz.getAttributes();
        final Method[] methods = clazz.getMethods();
        final Field[] fields = clazz.getFields();
        final String[] interfaces = clazz.getInterfaceNames();
        for (int i = 0; i < interfaces.length; ++i) {
            this.addInterface(interfaces[i]);
        }
        for (int i = 0; i < attributes.length; ++i) {
            this.addAttribute(attributes[i]);
        }
        for (int i = 0; i < methods.length; ++i) {
            this.addMethod(methods[i]);
        }
        for (int i = 0; i < fields.length; ++i) {
            this.addField(fields[i]);
        }
    }
    
    public JavaClass getJavaClass() {
        final int[] interfaces = this.getInterfaces();
        final Field[] fields = this.getFields();
        final Method[] methods = this.getMethods();
        final Attribute[] attributes = this.getAttributes();
        final ConstantPool cp = this.cp.getFinalConstantPool();
        return new JavaClass(this.class_name_index, this.superclass_name_index, this.file_name, this.major, this.minor, this.access_flags, cp, interfaces, fields, methods, attributes);
    }
    
    public void addInterface(final String name) {
        this.interface_vec.add(name);
    }
    
    public void removeInterface(final String name) {
        this.interface_vec.remove(name);
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public void setMajor(final int major) {
        this.major = major;
    }
    
    public void setMinor(final int minor) {
        this.minor = minor;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public void addAttribute(final Attribute a) {
        this.attribute_vec.add(a);
    }
    
    public void addMethod(final Method m) {
        this.method_vec.add(m);
    }
    
    public void addEmptyConstructor(final int access_flags) {
        final InstructionList il = new InstructionList();
        il.append(InstructionConstants.THIS);
        il.append(new INVOKESPECIAL(this.cp.addMethodref(this.super_class_name, "<init>", "()V")));
        il.append(InstructionConstants.RETURN);
        final MethodGen mg = new MethodGen(access_flags, Type.VOID, Type.NO_ARGS, null, "<init>", this.class_name, il, this.cp);
        mg.setMaxStack(1);
        this.addMethod(mg.getMethod());
    }
    
    public void addField(final Field f) {
        this.field_vec.add(f);
    }
    
    public boolean containsField(final Field f) {
        return this.field_vec.contains(f);
    }
    
    public Field containsField(final String name) {
        for (final Field f : this.field_vec) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }
    
    public Method containsMethod(final String name, final String signature) {
        for (final Method m : this.method_vec) {
            if (m.getName().equals(name) && m.getSignature().equals(signature)) {
                return m;
            }
        }
        return null;
    }
    
    public void removeAttribute(final Attribute a) {
        this.attribute_vec.remove(a);
    }
    
    public void removeMethod(final Method m) {
        this.method_vec.remove(m);
    }
    
    public void replaceMethod(final Method old, final Method new_) {
        if (new_ == null) {
            throw new ClassGenException("Replacement method must not be null");
        }
        final int i = this.method_vec.indexOf(old);
        if (i < 0) {
            this.method_vec.add(new_);
        }
        else {
            this.method_vec.set(i, new_);
        }
    }
    
    public void replaceField(final Field old, final Field new_) {
        if (new_ == null) {
            throw new ClassGenException("Replacement method must not be null");
        }
        final int i = this.field_vec.indexOf(old);
        if (i < 0) {
            this.field_vec.add(new_);
        }
        else {
            this.field_vec.set(i, new_);
        }
    }
    
    public void removeField(final Field f) {
        this.field_vec.remove(f);
    }
    
    public String getClassName() {
        return this.class_name;
    }
    
    public String getSuperclassName() {
        return this.super_class_name;
    }
    
    public String getFileName() {
        return this.file_name;
    }
    
    public void setClassName(final String name) {
        this.class_name = name.replace('/', '.');
        this.class_name_index = this.cp.addClass(name);
    }
    
    public void setSuperclassName(final String name) {
        this.super_class_name = name.replace('/', '.');
        this.superclass_name_index = this.cp.addClass(name);
    }
    
    public Method[] getMethods() {
        final Method[] methods = new Method[this.method_vec.size()];
        this.method_vec.toArray(methods);
        return methods;
    }
    
    public void setMethods(final Method[] methods) {
        this.method_vec.clear();
        for (int m = 0; m < methods.length; ++m) {
            this.addMethod(methods[m]);
        }
    }
    
    public void setMethodAt(final Method method, final int pos) {
        this.method_vec.set(pos, method);
    }
    
    public Method getMethodAt(final int pos) {
        return this.method_vec.get(pos);
    }
    
    public String[] getInterfaceNames() {
        final int size = this.interface_vec.size();
        final String[] interfaces = new String[size];
        this.interface_vec.toArray(interfaces);
        return interfaces;
    }
    
    public int[] getInterfaces() {
        final int size = this.interface_vec.size();
        final int[] interfaces = new int[size];
        for (int i = 0; i < size; ++i) {
            interfaces[i] = this.cp.addClass(this.interface_vec.get(i));
        }
        return interfaces;
    }
    
    public Field[] getFields() {
        final Field[] fields = new Field[this.field_vec.size()];
        this.field_vec.toArray(fields);
        return fields;
    }
    
    public Attribute[] getAttributes() {
        final Attribute[] attributes = new Attribute[this.attribute_vec.size()];
        this.attribute_vec.toArray(attributes);
        return attributes;
    }
    
    public ConstantPoolGen getConstantPool() {
        return this.cp;
    }
    
    public void setConstantPool(final ConstantPoolGen constant_pool) {
        this.cp = constant_pool;
    }
    
    public void setClassNameIndex(final int class_name_index) {
        this.class_name_index = class_name_index;
        this.class_name = this.cp.getConstantPool().getConstantString(class_name_index, (byte)7).replace('/', '.');
    }
    
    public void setSuperclassNameIndex(final int superclass_name_index) {
        this.superclass_name_index = superclass_name_index;
        this.super_class_name = this.cp.getConstantPool().getConstantString(superclass_name_index, (byte)7).replace('/', '.');
    }
    
    public int getSuperclassNameIndex() {
        return this.superclass_name_index;
    }
    
    public int getClassNameIndex() {
        return this.class_name_index;
    }
    
    public void addObserver(final ClassObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList();
        }
        this.observers.add(o);
    }
    
    public void removeObserver(final ClassObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }
    
    public void update() {
        if (this.observers != null) {
            final Iterator e = this.observers.iterator();
            while (e.hasNext()) {
                e.next().notify(this);
            }
        }
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            System.err.println(e);
            return null;
        }
    }
}
