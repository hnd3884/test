package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.bcel.internal.util.ClassQueue;
import com.sun.org.apache.bcel.internal.util.ClassVector;
import java.util.StringTokenizer;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import com.sun.org.apache.bcel.internal.util.Repository;

public class JavaClass extends AccessFlags implements Cloneable, Node
{
    private String file_name;
    private String package_name;
    private String source_file_name;
    private int class_name_index;
    private int superclass_name_index;
    private String class_name;
    private String superclass_name;
    private int major;
    private int minor;
    private ConstantPool constant_pool;
    private int[] interfaces;
    private String[] interface_names;
    private Field[] fields;
    private Method[] methods;
    private Attribute[] attributes;
    private byte source;
    public static final byte HEAP = 1;
    public static final byte FILE = 2;
    public static final byte ZIP = 3;
    static boolean debug;
    static char sep;
    private transient Repository repository;
    
    public JavaClass(final int class_name_index, final int superclass_name_index, final String file_name, final int major, final int minor, final int access_flags, final ConstantPool constant_pool, int[] interfaces, Field[] fields, Method[] methods, final Attribute[] attributes, final byte source) {
        this.source_file_name = "<Unknown>";
        this.source = 1;
        this.repository = SyntheticRepository.getInstance();
        if (interfaces == null) {
            interfaces = new int[0];
        }
        if (attributes == null) {
            this.attributes = new Attribute[0];
        }
        if (fields == null) {
            fields = new Field[0];
        }
        if (methods == null) {
            methods = new Method[0];
        }
        this.class_name_index = class_name_index;
        this.superclass_name_index = superclass_name_index;
        this.file_name = file_name;
        this.major = major;
        this.minor = minor;
        this.access_flags = access_flags;
        this.constant_pool = constant_pool;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.attributes = attributes;
        this.source = source;
        for (int i = 0; i < attributes.length; ++i) {
            if (attributes[i] instanceof SourceFile) {
                this.source_file_name = ((SourceFile)attributes[i]).getSourceFileName();
                break;
            }
        }
        this.class_name = constant_pool.getConstantString(class_name_index, (byte)7);
        this.class_name = Utility.compactClassName(this.class_name, false);
        final int index = this.class_name.lastIndexOf(46);
        if (index < 0) {
            this.package_name = "";
        }
        else {
            this.package_name = this.class_name.substring(0, index);
        }
        if (superclass_name_index > 0) {
            this.superclass_name = constant_pool.getConstantString(superclass_name_index, (byte)7);
            this.superclass_name = Utility.compactClassName(this.superclass_name, false);
        }
        else {
            this.superclass_name = "java.lang.Object";
        }
        this.interface_names = new String[interfaces.length];
        for (int j = 0; j < interfaces.length; ++j) {
            final String str = constant_pool.getConstantString(interfaces[j], (byte)7);
            this.interface_names[j] = Utility.compactClassName(str, false);
        }
    }
    
    public JavaClass(final int class_name_index, final int superclass_name_index, final String file_name, final int major, final int minor, final int access_flags, final ConstantPool constant_pool, final int[] interfaces, final Field[] fields, final Method[] methods, final Attribute[] attributes) {
        this(class_name_index, superclass_name_index, file_name, major, minor, access_flags, constant_pool, interfaces, fields, methods, attributes, (byte)1);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitJavaClass(this);
    }
    
    static final void Debug(final String str) {
        if (JavaClass.debug) {
            System.out.println(str);
        }
    }
    
    public void dump(final File file) throws IOException {
        final String parent = file.getParent();
        if (parent != null) {
            final File dir = new File(parent);
            if (dir != null) {
                dir.mkdirs();
            }
        }
        this.dump(new DataOutputStream(new FileOutputStream(file)));
    }
    
    public void dump(final String file_name) throws IOException {
        this.dump(new File(file_name));
    }
    
    public byte[] getBytes() {
        final ByteArrayOutputStream s = new ByteArrayOutputStream();
        final DataOutputStream ds = new DataOutputStream(s);
        try {
            this.dump(ds);
        }
        catch (final IOException e) {
            e.printStackTrace();
            try {
                ds.close();
            }
            catch (final IOException e2) {
                e2.printStackTrace();
            }
        }
        finally {
            try {
                ds.close();
            }
            catch (final IOException e3) {
                e3.printStackTrace();
            }
        }
        return s.toByteArray();
    }
    
    public void dump(final OutputStream file) throws IOException {
        this.dump(new DataOutputStream(file));
    }
    
    public void dump(final DataOutputStream file) throws IOException {
        file.writeInt(-889275714);
        file.writeShort(this.minor);
        file.writeShort(this.major);
        this.constant_pool.dump(file);
        file.writeShort(this.access_flags);
        file.writeShort(this.class_name_index);
        file.writeShort(this.superclass_name_index);
        file.writeShort(this.interfaces.length);
        for (int i = 0; i < this.interfaces.length; ++i) {
            file.writeShort(this.interfaces[i]);
        }
        file.writeShort(this.fields.length);
        for (int i = 0; i < this.fields.length; ++i) {
            this.fields[i].dump(file);
        }
        file.writeShort(this.methods.length);
        for (int i = 0; i < this.methods.length; ++i) {
            this.methods[i].dump(file);
        }
        if (this.attributes != null) {
            file.writeShort(this.attributes.length);
            for (int i = 0; i < this.attributes.length; ++i) {
                this.attributes[i].dump(file);
            }
        }
        else {
            file.writeShort(0);
        }
        file.close();
    }
    
    public Attribute[] getAttributes() {
        return this.attributes;
    }
    
    public String getClassName() {
        return this.class_name;
    }
    
    public String getPackageName() {
        return this.package_name;
    }
    
    public int getClassNameIndex() {
        return this.class_name_index;
    }
    
    public ConstantPool getConstantPool() {
        return this.constant_pool;
    }
    
    public Field[] getFields() {
        return this.fields;
    }
    
    public String getFileName() {
        return this.file_name;
    }
    
    public String[] getInterfaceNames() {
        return this.interface_names;
    }
    
    public int[] getInterfaceIndices() {
        return this.interfaces;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public Method[] getMethods() {
        return this.methods;
    }
    
    public Method getMethod(final java.lang.reflect.Method m) {
        for (int i = 0; i < this.methods.length; ++i) {
            final Method method = this.methods[i];
            if (m.getName().equals(method.getName()) && m.getModifiers() == method.getModifiers() && Type.getSignature(m).equals(method.getSignature())) {
                return method;
            }
        }
        return null;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public String getSourceFileName() {
        return this.source_file_name;
    }
    
    public String getSuperclassName() {
        return this.superclass_name;
    }
    
    public int getSuperclassNameIndex() {
        return this.superclass_name_index;
    }
    
    public void setAttributes(final Attribute[] attributes) {
        this.attributes = attributes;
    }
    
    public void setClassName(final String class_name) {
        this.class_name = class_name;
    }
    
    public void setClassNameIndex(final int class_name_index) {
        this.class_name_index = class_name_index;
    }
    
    public void setConstantPool(final ConstantPool constant_pool) {
        this.constant_pool = constant_pool;
    }
    
    public void setFields(final Field[] fields) {
        this.fields = fields;
    }
    
    public void setFileName(final String file_name) {
        this.file_name = file_name;
    }
    
    public void setInterfaceNames(final String[] interface_names) {
        this.interface_names = interface_names;
    }
    
    public void setInterfaces(final int[] interfaces) {
        this.interfaces = interfaces;
    }
    
    public void setMajor(final int major) {
        this.major = major;
    }
    
    public void setMethods(final Method[] methods) {
        this.methods = methods;
    }
    
    public void setMinor(final int minor) {
        this.minor = minor;
    }
    
    public void setSourceFileName(final String source_file_name) {
        this.source_file_name = source_file_name;
    }
    
    public void setSuperclassName(final String superclass_name) {
        this.superclass_name = superclass_name;
    }
    
    public void setSuperclassNameIndex(final int superclass_name_index) {
        this.superclass_name_index = superclass_name_index;
    }
    
    @Override
    public String toString() {
        String access = Utility.accessToString(this.access_flags, true);
        access = (access.equals("") ? "" : (access + " "));
        final StringBuffer buf = new StringBuffer(access + Utility.classOrInterface(this.access_flags) + " " + this.class_name + " extends " + Utility.compactClassName(this.superclass_name, false) + '\n');
        final int size = this.interfaces.length;
        if (size > 0) {
            buf.append("implements\t\t");
            for (int i = 0; i < size; ++i) {
                buf.append(this.interface_names[i]);
                if (i < size - 1) {
                    buf.append(", ");
                }
            }
            buf.append('\n');
        }
        buf.append("filename\t\t" + this.file_name + '\n');
        buf.append("compiled from\t\t" + this.source_file_name + '\n');
        buf.append("compiler version\t" + this.major + "." + this.minor + '\n');
        buf.append("access flags\t\t" + this.access_flags + '\n');
        buf.append("constant pool\t\t" + this.constant_pool.getLength() + " entries\n");
        buf.append("ACC_SUPER flag\t\t" + this.isSuper() + "\n");
        if (this.attributes.length > 0) {
            buf.append("\nAttribute(s):\n");
            for (int i = 0; i < this.attributes.length; ++i) {
                buf.append(indent(this.attributes[i]));
            }
        }
        if (this.fields.length > 0) {
            buf.append("\n" + this.fields.length + " fields:\n");
            for (int i = 0; i < this.fields.length; ++i) {
                buf.append("\t" + this.fields[i] + '\n');
            }
        }
        if (this.methods.length > 0) {
            buf.append("\n" + this.methods.length + " methods:\n");
            for (int i = 0; i < this.methods.length; ++i) {
                buf.append("\t" + this.methods[i] + '\n');
            }
        }
        return buf.toString();
    }
    
    private static final String indent(final Object obj) {
        final StringTokenizer tok = new StringTokenizer(obj.toString(), "\n");
        final StringBuffer buf = new StringBuffer();
        while (tok.hasMoreTokens()) {
            buf.append("\t" + tok.nextToken() + "\n");
        }
        return buf.toString();
    }
    
    public JavaClass copy() {
        JavaClass c = null;
        try {
            c = (JavaClass)this.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        c.constant_pool = this.constant_pool.copy();
        c.interfaces = this.interfaces.clone();
        c.interface_names = this.interface_names.clone();
        c.fields = new Field[this.fields.length];
        for (int i = 0; i < this.fields.length; ++i) {
            c.fields[i] = this.fields[i].copy(c.constant_pool);
        }
        c.methods = new Method[this.methods.length];
        for (int i = 0; i < this.methods.length; ++i) {
            c.methods[i] = this.methods[i].copy(c.constant_pool);
        }
        c.attributes = new Attribute[this.attributes.length];
        for (int i = 0; i < this.attributes.length; ++i) {
            c.attributes[i] = this.attributes[i].copy(c.constant_pool);
        }
        return c;
    }
    
    public final boolean isSuper() {
        return (this.access_flags & 0x20) != 0x0;
    }
    
    public final boolean isClass() {
        return (this.access_flags & 0x200) == 0x0;
    }
    
    public final byte getSource() {
        return this.source;
    }
    
    public Repository getRepository() {
        return this.repository;
    }
    
    public void setRepository(final Repository repository) {
        this.repository = repository;
    }
    
    public final boolean instanceOf(final JavaClass super_class) {
        if (this.equals(super_class)) {
            return true;
        }
        final JavaClass[] super_classes = this.getSuperClasses();
        for (int i = 0; i < super_classes.length; ++i) {
            if (super_classes[i].equals(super_class)) {
                return true;
            }
        }
        return super_class.isInterface() && this.implementationOf(super_class);
    }
    
    public boolean implementationOf(final JavaClass inter) {
        if (!inter.isInterface()) {
            throw new IllegalArgumentException(inter.getClassName() + " is no interface");
        }
        if (this.equals(inter)) {
            return true;
        }
        final JavaClass[] super_interfaces = this.getAllInterfaces();
        for (int i = 0; i < super_interfaces.length; ++i) {
            if (super_interfaces[i].equals(inter)) {
                return true;
            }
        }
        return false;
    }
    
    public JavaClass getSuperClass() {
        if ("java.lang.Object".equals(this.getClassName())) {
            return null;
        }
        try {
            return this.repository.loadClass(this.getSuperclassName());
        }
        catch (final ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public JavaClass[] getSuperClasses() {
        JavaClass clazz = this;
        final ClassVector vec = new ClassVector();
        for (clazz = clazz.getSuperClass(); clazz != null; clazz = clazz.getSuperClass()) {
            vec.addElement(clazz);
        }
        return vec.toArray();
    }
    
    public JavaClass[] getInterfaces() {
        final String[] interfaces = this.getInterfaceNames();
        final JavaClass[] classes = new JavaClass[interfaces.length];
        try {
            for (int i = 0; i < interfaces.length; ++i) {
                classes[i] = this.repository.loadClass(interfaces[i]);
            }
        }
        catch (final ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
        return classes;
    }
    
    public JavaClass[] getAllInterfaces() {
        final ClassQueue queue = new ClassQueue();
        final ClassVector vec = new ClassVector();
        queue.enqueue(this);
        while (!queue.empty()) {
            final JavaClass clazz = queue.dequeue();
            final JavaClass souper = clazz.getSuperClass();
            final JavaClass[] interfaces = clazz.getInterfaces();
            if (clazz.isInterface()) {
                vec.addElement(clazz);
            }
            else if (souper != null) {
                queue.enqueue(souper);
            }
            for (int i = 0; i < interfaces.length; ++i) {
                queue.enqueue(interfaces[i]);
            }
        }
        return vec.toArray();
    }
    
    static {
        JavaClass.debug = false;
        JavaClass.sep = '/';
        String debug = null;
        String sep = null;
        try {
            debug = SecuritySupport.getSystemProperty("JavaClass.debug");
            sep = SecuritySupport.getSystemProperty("file.separator");
        }
        catch (final SecurityException ex) {}
        if (debug != null) {
            JavaClass.debug = new Boolean(debug);
        }
        if (sep != null) {
            try {
                JavaClass.sep = sep.charAt(0);
            }
            catch (final StringIndexOutOfBoundsException ex2) {}
        }
    }
}
