package sun.misc;

import java.lang.reflect.Array;
import sun.security.action.GetBooleanAction;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.security.AccessController;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Paths;
import java.io.File;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;

public class ProxyGenerator
{
    private static final int CLASSFILE_MAJOR_VERSION = 49;
    private static final int CLASSFILE_MINOR_VERSION = 0;
    private static final int CONSTANT_UTF8 = 1;
    private static final int CONSTANT_UNICODE = 2;
    private static final int CONSTANT_INTEGER = 3;
    private static final int CONSTANT_FLOAT = 4;
    private static final int CONSTANT_LONG = 5;
    private static final int CONSTANT_DOUBLE = 6;
    private static final int CONSTANT_CLASS = 7;
    private static final int CONSTANT_STRING = 8;
    private static final int CONSTANT_FIELD = 9;
    private static final int CONSTANT_METHOD = 10;
    private static final int CONSTANT_INTERFACEMETHOD = 11;
    private static final int CONSTANT_NAMEANDTYPE = 12;
    private static final int ACC_PUBLIC = 1;
    private static final int ACC_PRIVATE = 2;
    private static final int ACC_STATIC = 8;
    private static final int ACC_FINAL = 16;
    private static final int ACC_SUPER = 32;
    private static final int opc_aconst_null = 1;
    private static final int opc_iconst_0 = 3;
    private static final int opc_bipush = 16;
    private static final int opc_sipush = 17;
    private static final int opc_ldc = 18;
    private static final int opc_ldc_w = 19;
    private static final int opc_iload = 21;
    private static final int opc_lload = 22;
    private static final int opc_fload = 23;
    private static final int opc_dload = 24;
    private static final int opc_aload = 25;
    private static final int opc_iload_0 = 26;
    private static final int opc_lload_0 = 30;
    private static final int opc_fload_0 = 34;
    private static final int opc_dload_0 = 38;
    private static final int opc_aload_0 = 42;
    private static final int opc_astore = 58;
    private static final int opc_astore_0 = 75;
    private static final int opc_aastore = 83;
    private static final int opc_pop = 87;
    private static final int opc_dup = 89;
    private static final int opc_ireturn = 172;
    private static final int opc_lreturn = 173;
    private static final int opc_freturn = 174;
    private static final int opc_dreturn = 175;
    private static final int opc_areturn = 176;
    private static final int opc_return = 177;
    private static final int opc_getstatic = 178;
    private static final int opc_putstatic = 179;
    private static final int opc_getfield = 180;
    private static final int opc_invokevirtual = 182;
    private static final int opc_invokespecial = 183;
    private static final int opc_invokestatic = 184;
    private static final int opc_invokeinterface = 185;
    private static final int opc_new = 187;
    private static final int opc_anewarray = 189;
    private static final int opc_athrow = 191;
    private static final int opc_checkcast = 192;
    private static final int opc_wide = 196;
    private static final String superclassName = "java/lang/reflect/Proxy";
    private static final String handlerFieldName = "h";
    private static final boolean saveGeneratedFiles;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;
    private String className;
    private Class<?>[] interfaces;
    private int accessFlags;
    private ConstantPool cp;
    private List<FieldInfo> fields;
    private List<MethodInfo> methods;
    private Map<String, List<ProxyMethod>> proxyMethods;
    private int proxyMethodCount;
    
    public static byte[] generateProxyClass(final String s, final Class<?>[] array) {
        return generateProxyClass(s, array, 49);
    }
    
    public static byte[] generateProxyClass(final String s, final Class<?>[] array, final int n) {
        final byte[] generateClassFile = new ProxyGenerator(s, array, n).generateClassFile();
        if (ProxyGenerator.saveGeneratedFiles) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        final int lastIndex = s.lastIndexOf(46);
                        Path path;
                        if (lastIndex > 0) {
                            final Path value = Paths.get(s.substring(0, lastIndex).replace('.', File.separatorChar), new String[0]);
                            Files.createDirectories(value, (FileAttribute<?>[])new FileAttribute[0]);
                            path = value.resolve(s.substring(lastIndex + 1, s.length()) + ".class");
                        }
                        else {
                            path = Paths.get(s + ".class", new String[0]);
                        }
                        Files.write(path, generateClassFile, new OpenOption[0]);
                        return null;
                    }
                    catch (final IOException ex) {
                        throw new InternalError("I/O exception saving generated file: " + ex);
                    }
                }
            });
        }
        return generateClassFile;
    }
    
    private ProxyGenerator(final String className, final Class<?>[] interfaces, final int accessFlags) {
        this.cp = new ConstantPool();
        this.fields = new ArrayList<FieldInfo>();
        this.methods = new ArrayList<MethodInfo>();
        this.proxyMethods = new HashMap<String, List<ProxyMethod>>();
        this.proxyMethodCount = 0;
        this.className = className;
        this.interfaces = interfaces;
        this.accessFlags = accessFlags;
    }
    
    private byte[] generateClassFile() {
        this.addProxyMethod(ProxyGenerator.hashCodeMethod, Object.class);
        this.addProxyMethod(ProxyGenerator.equalsMethod, Object.class);
        this.addProxyMethod(ProxyGenerator.toStringMethod, Object.class);
        for (final Class<?> clazz : this.interfaces) {
            final Method[] methods = clazz.getMethods();
            for (int length2 = methods.length, j = 0; j < length2; ++j) {
                this.addProxyMethod(methods[j], clazz);
            }
        }
        final Iterator<List<ProxyMethod>> iterator = this.proxyMethods.values().iterator();
        while (iterator.hasNext()) {
            checkReturnTypes(iterator.next());
        }
        try {
            this.methods.add(this.generateConstructor());
            final Iterator<List<ProxyMethod>> iterator2 = this.proxyMethods.values().iterator();
            while (iterator2.hasNext()) {
                for (final ProxyMethod proxyMethod : iterator2.next()) {
                    this.fields.add(new FieldInfo(proxyMethod.methodFieldName, "Ljava/lang/reflect/Method;", 10));
                    this.methods.add(proxyMethod.generateMethod());
                }
            }
            this.methods.add(this.generateStaticInitializer());
        }
        catch (final IOException ex) {
            throw new InternalError("unexpected I/O Exception", ex);
        }
        if (this.methods.size() > 65535) {
            throw new IllegalArgumentException("method limit exceeded");
        }
        if (this.fields.size() > 65535) {
            throw new IllegalArgumentException("field limit exceeded");
        }
        this.cp.getClass(dotToSlash(this.className));
        this.cp.getClass("java/lang/reflect/Proxy");
        final Class<?>[] interfaces2 = this.interfaces;
        for (int length3 = interfaces2.length, k = 0; k < length3; ++k) {
            this.cp.getClass(dotToSlash(interfaces2[k].getName()));
        }
        this.cp.setReadOnly();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(-889275714);
            dataOutputStream.writeShort(0);
            dataOutputStream.writeShort(49);
            this.cp.write(dataOutputStream);
            dataOutputStream.writeShort(this.accessFlags);
            dataOutputStream.writeShort(this.cp.getClass(dotToSlash(this.className)));
            dataOutputStream.writeShort(this.cp.getClass("java/lang/reflect/Proxy"));
            dataOutputStream.writeShort(this.interfaces.length);
            final Class<?>[] interfaces3 = this.interfaces;
            for (int length4 = interfaces3.length, l = 0; l < length4; ++l) {
                dataOutputStream.writeShort(this.cp.getClass(dotToSlash(interfaces3[l].getName())));
            }
            dataOutputStream.writeShort(this.fields.size());
            final Iterator<FieldInfo> iterator4 = this.fields.iterator();
            while (iterator4.hasNext()) {
                iterator4.next().write(dataOutputStream);
            }
            dataOutputStream.writeShort(this.methods.size());
            final Iterator<MethodInfo> iterator5 = this.methods.iterator();
            while (iterator5.hasNext()) {
                iterator5.next().write(dataOutputStream);
            }
            dataOutputStream.writeShort(0);
        }
        catch (final IOException ex2) {
            throw new InternalError("unexpected I/O Exception", ex2);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private void addProxyMethod(final Method method, final Class<?> clazz) {
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        final String string = name + getParameterDescriptors(parameterTypes);
        List list = this.proxyMethods.get(string);
        if (list != null) {
            for (final ProxyMethod proxyMethod : list) {
                if (returnType == proxyMethod.returnType) {
                    final ArrayList list2 = new ArrayList();
                    collectCompatibleTypes(exceptionTypes, proxyMethod.exceptionTypes, list2);
                    collectCompatibleTypes(proxyMethod.exceptionTypes, exceptionTypes, list2);
                    proxyMethod.exceptionTypes = new Class[list2.size()];
                    proxyMethod.exceptionTypes = (Class[])list2.toArray(proxyMethod.exceptionTypes);
                    return;
                }
            }
        }
        else {
            list = new ArrayList(3);
            this.proxyMethods.put(string, list);
        }
        list.add(new ProxyMethod(name, (Class[])parameterTypes, (Class)returnType, (Class[])exceptionTypes, (Class)clazz));
    }
    
    private static void checkReturnTypes(final List<ProxyMethod> list) {
        if (list.size() < 2) {
            return;
        }
        final LinkedList list2 = new LinkedList();
    Label_0026:
        for (final ProxyMethod proxyMethod : list) {
            final Class<?> returnType = proxyMethod.returnType;
            if (returnType.isPrimitive()) {
                throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(proxyMethod.methodName, proxyMethod.parameterTypes) + " but incompatible return types: " + returnType.getName() + " and others");
            }
            int n = 0;
            final ListIterator listIterator = list2.listIterator();
            while (listIterator.hasNext()) {
                final Class clazz = (Class)listIterator.next();
                if (returnType.isAssignableFrom(clazz)) {
                    assert n == 0;
                    continue Label_0026;
                }
                else {
                    if (!clazz.isAssignableFrom(returnType)) {
                        continue;
                    }
                    if (n == 0) {
                        listIterator.set(returnType);
                        n = 1;
                    }
                    else {
                        listIterator.remove();
                    }
                }
            }
            if (n != 0) {
                continue;
            }
            list2.add(returnType);
        }
        if (list2.size() > 1) {
            final ProxyMethod proxyMethod2 = list.get(0);
            throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(proxyMethod2.methodName, proxyMethod2.parameterTypes) + " but incompatible return types: " + list2);
        }
    }
    
    private MethodInfo generateConstructor() throws IOException {
        final MethodInfo methodInfo = new MethodInfo("<init>", "(Ljava/lang/reflect/InvocationHandler;)V", 1);
        final DataOutputStream dataOutputStream = new DataOutputStream(methodInfo.code);
        this.code_aload(0, dataOutputStream);
        this.code_aload(1, dataOutputStream);
        dataOutputStream.writeByte(183);
        dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/reflect/Proxy", "<init>", "(Ljava/lang/reflect/InvocationHandler;)V"));
        dataOutputStream.writeByte(177);
        methodInfo.maxStack = 10;
        methodInfo.maxLocals = 2;
        methodInfo.declaredExceptions = new short[0];
        return methodInfo;
    }
    
    private MethodInfo generateStaticInitializer() throws IOException {
        final MethodInfo methodInfo = new MethodInfo("<clinit>", "()V", 8);
        final int n = 1;
        final short n2 = 0;
        final DataOutputStream dataOutputStream = new DataOutputStream(methodInfo.code);
        final Iterator<List<ProxyMethod>> iterator = this.proxyMethods.values().iterator();
        while (iterator.hasNext()) {
            final Iterator iterator2 = iterator.next().iterator();
            while (iterator2.hasNext()) {
                ((ProxyMethod)iterator2.next()).codeFieldInitialization(dataOutputStream);
            }
        }
        dataOutputStream.writeByte(177);
        final short n3;
        methodInfo.exceptionTable.add(new ExceptionTableEntry(n2, n3, n3 = (short)methodInfo.code.size(), this.cp.getClass("java/lang/NoSuchMethodException")));
        this.code_astore(n, dataOutputStream);
        dataOutputStream.writeByte(187);
        dataOutputStream.writeShort(this.cp.getClass("java/lang/NoSuchMethodError"));
        dataOutputStream.writeByte(89);
        this.code_aload(n, dataOutputStream);
        dataOutputStream.writeByte(182);
        dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
        dataOutputStream.writeByte(183);
        dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V"));
        dataOutputStream.writeByte(191);
        methodInfo.exceptionTable.add(new ExceptionTableEntry(n2, n3, (short)methodInfo.code.size(), this.cp.getClass("java/lang/ClassNotFoundException")));
        this.code_astore(n, dataOutputStream);
        dataOutputStream.writeByte(187);
        dataOutputStream.writeShort(this.cp.getClass("java/lang/NoClassDefFoundError"));
        dataOutputStream.writeByte(89);
        this.code_aload(n, dataOutputStream);
        dataOutputStream.writeByte(182);
        dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
        dataOutputStream.writeByte(183);
        dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V"));
        dataOutputStream.writeByte(191);
        if (methodInfo.code.size() > 65535) {
            throw new IllegalArgumentException("code size limit exceeded");
        }
        methodInfo.maxStack = 10;
        methodInfo.maxLocals = (short)(n + 1);
        methodInfo.declaredExceptions = new short[0];
        return methodInfo;
    }
    
    private void code_iload(final int n, final DataOutputStream dataOutputStream) throws IOException {
        this.codeLocalLoadStore(n, 21, 26, dataOutputStream);
    }
    
    private void code_lload(final int n, final DataOutputStream dataOutputStream) throws IOException {
        this.codeLocalLoadStore(n, 22, 30, dataOutputStream);
    }
    
    private void code_fload(final int n, final DataOutputStream dataOutputStream) throws IOException {
        this.codeLocalLoadStore(n, 23, 34, dataOutputStream);
    }
    
    private void code_dload(final int n, final DataOutputStream dataOutputStream) throws IOException {
        this.codeLocalLoadStore(n, 24, 38, dataOutputStream);
    }
    
    private void code_aload(final int n, final DataOutputStream dataOutputStream) throws IOException {
        this.codeLocalLoadStore(n, 25, 42, dataOutputStream);
    }
    
    private void code_astore(final int n, final DataOutputStream dataOutputStream) throws IOException {
        this.codeLocalLoadStore(n, 58, 75, dataOutputStream);
    }
    
    private void codeLocalLoadStore(final int n, final int n2, final int n3, final DataOutputStream dataOutputStream) throws IOException {
        assert n >= 0 && n <= 65535;
        if (n <= 3) {
            dataOutputStream.writeByte(n3 + n);
        }
        else if (n <= 255) {
            dataOutputStream.writeByte(n2);
            dataOutputStream.writeByte(n & 0xFF);
        }
        else {
            dataOutputStream.writeByte(196);
            dataOutputStream.writeByte(n2);
            dataOutputStream.writeShort(n & 0xFFFF);
        }
    }
    
    private void code_ldc(final int n, final DataOutputStream dataOutputStream) throws IOException {
        assert n >= 0 && n <= 65535;
        if (n <= 255) {
            dataOutputStream.writeByte(18);
            dataOutputStream.writeByte(n & 0xFF);
        }
        else {
            dataOutputStream.writeByte(19);
            dataOutputStream.writeShort(n & 0xFFFF);
        }
    }
    
    private void code_ipush(final int n, final DataOutputStream dataOutputStream) throws IOException {
        if (n >= -1 && n <= 5) {
            dataOutputStream.writeByte(3 + n);
        }
        else if (n >= -128 && n <= 127) {
            dataOutputStream.writeByte(16);
            dataOutputStream.writeByte(n & 0xFF);
        }
        else {
            if (n < -32768 || n > 32767) {
                throw new AssertionError();
            }
            dataOutputStream.writeByte(17);
            dataOutputStream.writeShort(n & 0xFFFF);
        }
    }
    
    private void codeClassForName(final Class<?> clazz, final DataOutputStream dataOutputStream) throws IOException {
        this.code_ldc(this.cp.getString(clazz.getName()), dataOutputStream);
        dataOutputStream.writeByte(184);
        dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;"));
    }
    
    private static String dotToSlash(final String s) {
        return s.replace('.', '/');
    }
    
    private static String getMethodDescriptor(final Class<?>[] array, final Class<?> clazz) {
        return getParameterDescriptors(array) + ((clazz == Void.TYPE) ? "V" : getFieldType(clazz));
    }
    
    private static String getParameterDescriptors(final Class<?>[] array) {
        final StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < array.length; ++i) {
            sb.append(getFieldType(array[i]));
        }
        sb.append(')');
        return sb.toString();
    }
    
    private static String getFieldType(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return PrimitiveTypeInfo.get(clazz).baseTypeString;
        }
        if (clazz.isArray()) {
            return clazz.getName().replace('.', '/');
        }
        return "L" + dotToSlash(clazz.getName()) + ";";
    }
    
    private static String getFriendlyMethodSignature(final String s, final Class<?>[] array) {
        final StringBuilder sb = new StringBuilder(s);
        sb.append('(');
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            Class<?> componentType;
            int n;
            for (componentType = array[i], n = 0; componentType.isArray(); componentType = componentType.getComponentType(), ++n) {}
            sb.append(componentType.getName());
            while (n-- > 0) {
                sb.append("[]");
            }
        }
        sb.append(')');
        return sb.toString();
    }
    
    private static int getWordsPerType(final Class<?> clazz) {
        if (clazz == Long.TYPE || clazz == Double.TYPE) {
            return 2;
        }
        return 1;
    }
    
    private static void collectCompatibleTypes(final Class<?>[] array, final Class<?>[] array2, final List<Class<?>> list) {
        for (final Class<?> clazz : array) {
            if (!list.contains(clazz)) {
                for (int length2 = array2.length, j = 0; j < length2; ++j) {
                    if (array2[j].isAssignableFrom(clazz)) {
                        list.add(clazz);
                        break;
                    }
                }
            }
        }
    }
    
    private static List<Class<?>> computeUniqueCatchList(final Class<?>[] array) {
        final ArrayList list = new ArrayList();
        list.add(Error.class);
        list.add(RuntimeException.class);
        for (final Class<?> clazz : array) {
            if (clazz.isAssignableFrom(Throwable.class)) {
                list.clear();
                break;
            }
            Label_0155: {
                if (Throwable.class.isAssignableFrom(clazz)) {
                    int j = 0;
                    while (j < list.size()) {
                        final Class clazz2 = (Class)list.get(j);
                        if (clazz2.isAssignableFrom(clazz)) {
                            break Label_0155;
                        }
                        if (clazz.isAssignableFrom(clazz2)) {
                            list.remove(j);
                        }
                        else {
                            ++j;
                        }
                    }
                    list.add(clazz);
                }
            }
        }
        return list;
    }
    
    static {
        saveGeneratedFiles = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.misc.ProxyGenerator.saveGeneratedFiles"));
        try {
            ProxyGenerator.hashCodeMethod = Object.class.getMethod("hashCode", (Class<?>[])new Class[0]);
            ProxyGenerator.equalsMethod = Object.class.getMethod("equals", Object.class);
            ProxyGenerator.toStringMethod = Object.class.getMethod("toString", (Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException ex) {
            throw new NoSuchMethodError(ex.getMessage());
        }
    }
    
    private class FieldInfo
    {
        public int accessFlags;
        public String name;
        public String descriptor;
        
        public FieldInfo(final String name, final String descriptor, final int accessFlags) {
            this.name = name;
            this.descriptor = descriptor;
            this.accessFlags = accessFlags;
            ProxyGenerator.this.cp.getUtf8(name);
            ProxyGenerator.this.cp.getUtf8(descriptor);
        }
        
        public void write(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeShort(this.accessFlags);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.name));
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.descriptor));
            dataOutputStream.writeShort(0);
        }
    }
    
    private static class ExceptionTableEntry
    {
        public short startPc;
        public short endPc;
        public short handlerPc;
        public short catchType;
        
        public ExceptionTableEntry(final short startPc, final short endPc, final short handlerPc, final short catchType) {
            this.startPc = startPc;
            this.endPc = endPc;
            this.handlerPc = handlerPc;
            this.catchType = catchType;
        }
    }
    
    private class MethodInfo
    {
        public int accessFlags;
        public String name;
        public String descriptor;
        public short maxStack;
        public short maxLocals;
        public ByteArrayOutputStream code;
        public List<ExceptionTableEntry> exceptionTable;
        public short[] declaredExceptions;
        
        public MethodInfo(final String name, final String descriptor, final int accessFlags) {
            this.code = new ByteArrayOutputStream();
            this.exceptionTable = new ArrayList<ExceptionTableEntry>();
            this.name = name;
            this.descriptor = descriptor;
            this.accessFlags = accessFlags;
            ProxyGenerator.this.cp.getUtf8(name);
            ProxyGenerator.this.cp.getUtf8(descriptor);
            ProxyGenerator.this.cp.getUtf8("Code");
            ProxyGenerator.this.cp.getUtf8("Exceptions");
        }
        
        public void write(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeShort(this.accessFlags);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.name));
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.descriptor));
            dataOutputStream.writeShort(2);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8("Code"));
            dataOutputStream.writeInt(12 + this.code.size() + 8 * this.exceptionTable.size());
            dataOutputStream.writeShort(this.maxStack);
            dataOutputStream.writeShort(this.maxLocals);
            dataOutputStream.writeInt(this.code.size());
            this.code.writeTo(dataOutputStream);
            dataOutputStream.writeShort(this.exceptionTable.size());
            for (final ExceptionTableEntry exceptionTableEntry : this.exceptionTable) {
                dataOutputStream.writeShort(exceptionTableEntry.startPc);
                dataOutputStream.writeShort(exceptionTableEntry.endPc);
                dataOutputStream.writeShort(exceptionTableEntry.handlerPc);
                dataOutputStream.writeShort(exceptionTableEntry.catchType);
            }
            dataOutputStream.writeShort(0);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8("Exceptions"));
            dataOutputStream.writeInt(2 + 2 * this.declaredExceptions.length);
            dataOutputStream.writeShort(this.declaredExceptions.length);
            final short[] declaredExceptions = this.declaredExceptions;
            for (int length = declaredExceptions.length, i = 0; i < length; ++i) {
                dataOutputStream.writeShort(declaredExceptions[i]);
            }
        }
    }
    
    private class ProxyMethod
    {
        public String methodName;
        public Class<?>[] parameterTypes;
        public Class<?> returnType;
        public Class<?>[] exceptionTypes;
        public Class<?> fromClass;
        public String methodFieldName;
        
        private ProxyMethod(final String methodName, final Class<?>[] parameterTypes, final Class<?> returnType, final Class<?>[] exceptionTypes, final Class<?> fromClass) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.exceptionTypes = exceptionTypes;
            this.fromClass = fromClass;
            this.methodFieldName = "m" + ProxyGenerator.this.proxyMethodCount++;
        }
        
        private MethodInfo generateMethod() throws IOException {
            final MethodInfo methodInfo = new MethodInfo(this.methodName, getMethodDescriptor(this.parameterTypes, this.returnType), 17);
            final int[] array = new int[this.parameterTypes.length];
            int n = 1;
            for (int i = 0; i < array.length; ++i) {
                array[i] = n;
                n += getWordsPerType(this.parameterTypes[i]);
            }
            final int n2 = n;
            final short n3 = 0;
            final DataOutputStream dataOutputStream = new DataOutputStream(methodInfo.code);
            ProxyGenerator.this.code_aload(0, dataOutputStream);
            dataOutputStream.writeByte(180);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef("java/lang/reflect/Proxy", "h", "Ljava/lang/reflect/InvocationHandler;"));
            ProxyGenerator.this.code_aload(0, dataOutputStream);
            dataOutputStream.writeByte(178);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
            if (this.parameterTypes.length > 0) {
                ProxyGenerator.this.code_ipush(this.parameterTypes.length, dataOutputStream);
                dataOutputStream.writeByte(189);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Object"));
                for (int j = 0; j < this.parameterTypes.length; ++j) {
                    dataOutputStream.writeByte(89);
                    ProxyGenerator.this.code_ipush(j, dataOutputStream);
                    this.codeWrapArgument(this.parameterTypes[j], array[j], dataOutputStream);
                    dataOutputStream.writeByte(83);
                }
            }
            else {
                dataOutputStream.writeByte(1);
            }
            dataOutputStream.writeByte(185);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getInterfaceMethodRef("java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
            dataOutputStream.writeByte(4);
            dataOutputStream.writeByte(0);
            if (this.returnType == Void.TYPE) {
                dataOutputStream.writeByte(87);
                dataOutputStream.writeByte(177);
            }
            else {
                this.codeUnwrapReturnValue(this.returnType, dataOutputStream);
            }
            final short n5;
            final short n4 = n5 = (short)methodInfo.code.size();
            final List access$1100 = computeUniqueCatchList(this.exceptionTypes);
            if (access$1100.size() > 0) {
                final Iterator iterator = access$1100.iterator();
                while (iterator.hasNext()) {
                    methodInfo.exceptionTable.add(new ExceptionTableEntry(n3, n5, n4, ProxyGenerator.this.cp.getClass(dotToSlash(((Class)iterator.next()).getName()))));
                }
                dataOutputStream.writeByte(191);
                methodInfo.exceptionTable.add(new ExceptionTableEntry(n3, n5, (short)methodInfo.code.size(), ProxyGenerator.this.cp.getClass("java/lang/Throwable")));
                ProxyGenerator.this.code_astore(n2, dataOutputStream);
                dataOutputStream.writeByte(187);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/reflect/UndeclaredThrowableException"));
                dataOutputStream.writeByte(89);
                ProxyGenerator.this.code_aload(n2, dataOutputStream);
                dataOutputStream.writeByte(183);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/reflect/UndeclaredThrowableException", "<init>", "(Ljava/lang/Throwable;)V"));
                dataOutputStream.writeByte(191);
            }
            if (methodInfo.code.size() > 65535) {
                throw new IllegalArgumentException("code size limit exceeded");
            }
            methodInfo.maxStack = 10;
            methodInfo.maxLocals = (short)(n2 + 1);
            methodInfo.declaredExceptions = new short[this.exceptionTypes.length];
            for (int k = 0; k < this.exceptionTypes.length; ++k) {
                methodInfo.declaredExceptions[k] = ProxyGenerator.this.cp.getClass(dotToSlash(this.exceptionTypes[k].getName()));
            }
            return methodInfo;
        }
        
        private void codeWrapArgument(final Class<?> clazz, final int n, final DataOutputStream dataOutputStream) throws IOException {
            if (clazz.isPrimitive()) {
                final PrimitiveTypeInfo value = PrimitiveTypeInfo.get(clazz);
                if (clazz == Integer.TYPE || clazz == Boolean.TYPE || clazz == Byte.TYPE || clazz == Character.TYPE || clazz == Short.TYPE) {
                    ProxyGenerator.this.code_iload(n, dataOutputStream);
                }
                else if (clazz == Long.TYPE) {
                    ProxyGenerator.this.code_lload(n, dataOutputStream);
                }
                else if (clazz == Float.TYPE) {
                    ProxyGenerator.this.code_fload(n, dataOutputStream);
                }
                else {
                    if (clazz != Double.TYPE) {
                        throw new AssertionError();
                    }
                    ProxyGenerator.this.code_dload(n, dataOutputStream);
                }
                dataOutputStream.writeByte(184);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef(value.wrapperClassName, "valueOf", value.wrapperValueOfDesc));
            }
            else {
                ProxyGenerator.this.code_aload(n, dataOutputStream);
            }
        }
        
        private void codeUnwrapReturnValue(final Class<?> clazz, final DataOutputStream dataOutputStream) throws IOException {
            if (clazz.isPrimitive()) {
                final PrimitiveTypeInfo value = PrimitiveTypeInfo.get(clazz);
                dataOutputStream.writeByte(192);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass(value.wrapperClassName));
                dataOutputStream.writeByte(182);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef(value.wrapperClassName, value.unwrapMethodName, value.unwrapMethodDesc));
                if (clazz == Integer.TYPE || clazz == Boolean.TYPE || clazz == Byte.TYPE || clazz == Character.TYPE || clazz == Short.TYPE) {
                    dataOutputStream.writeByte(172);
                }
                else if (clazz == Long.TYPE) {
                    dataOutputStream.writeByte(173);
                }
                else if (clazz == Float.TYPE) {
                    dataOutputStream.writeByte(174);
                }
                else {
                    if (clazz != Double.TYPE) {
                        throw new AssertionError();
                    }
                    dataOutputStream.writeByte(175);
                }
            }
            else {
                dataOutputStream.writeByte(192);
                dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass(dotToSlash(clazz.getName())));
                dataOutputStream.writeByte(176);
            }
        }
        
        private void codeFieldInitialization(final DataOutputStream dataOutputStream) throws IOException {
            ProxyGenerator.this.codeClassForName(this.fromClass, dataOutputStream);
            ProxyGenerator.this.code_ldc(ProxyGenerator.this.cp.getString(this.methodName), dataOutputStream);
            ProxyGenerator.this.code_ipush(this.parameterTypes.length, dataOutputStream);
            dataOutputStream.writeByte(189);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Class"));
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                dataOutputStream.writeByte(89);
                ProxyGenerator.this.code_ipush(i, dataOutputStream);
                if (this.parameterTypes[i].isPrimitive()) {
                    final PrimitiveTypeInfo value = PrimitiveTypeInfo.get(this.parameterTypes[i]);
                    dataOutputStream.writeByte(178);
                    dataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(value.wrapperClassName, "TYPE", "Ljava/lang/Class;"));
                }
                else {
                    ProxyGenerator.this.codeClassForName(this.parameterTypes[i], dataOutputStream);
                }
                dataOutputStream.writeByte(83);
            }
            dataOutputStream.writeByte(182);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"));
            dataOutputStream.writeByte(179);
            dataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
        }
    }
    
    private static class PrimitiveTypeInfo
    {
        public String baseTypeString;
        public String wrapperClassName;
        public String wrapperValueOfDesc;
        public String unwrapMethodName;
        public String unwrapMethodDesc;
        private static Map<Class<?>, PrimitiveTypeInfo> table;
        
        private static void add(final Class<?> clazz, final Class<?> clazz2) {
            PrimitiveTypeInfo.table.put(clazz, new PrimitiveTypeInfo(clazz, clazz2));
        }
        
        private PrimitiveTypeInfo(final Class<?> clazz, final Class<?> clazz2) {
            assert clazz.isPrimitive();
            this.baseTypeString = Array.newInstance(clazz, 0).getClass().getName().substring(1);
            this.wrapperClassName = dotToSlash(clazz2.getName());
            this.wrapperValueOfDesc = "(" + this.baseTypeString + ")L" + this.wrapperClassName + ";";
            this.unwrapMethodName = clazz.getName() + "Value";
            this.unwrapMethodDesc = "()" + this.baseTypeString;
        }
        
        public static PrimitiveTypeInfo get(final Class<?> clazz) {
            return PrimitiveTypeInfo.table.get(clazz);
        }
        
        static {
            PrimitiveTypeInfo.table = new HashMap<Class<?>, PrimitiveTypeInfo>();
            add(Byte.TYPE, Byte.class);
            add(Character.TYPE, Character.class);
            add(Double.TYPE, Double.class);
            add(Float.TYPE, Float.class);
            add(Integer.TYPE, Integer.class);
            add(Long.TYPE, Long.class);
            add(Short.TYPE, Short.class);
            add(Boolean.TYPE, Boolean.class);
        }
    }
    
    private static class ConstantPool
    {
        private List<Entry> pool;
        private Map<Object, Short> map;
        private boolean readOnly;
        
        private ConstantPool() {
            this.pool = new ArrayList<Entry>(32);
            this.map = new HashMap<Object, Short>(16);
            this.readOnly = false;
        }
        
        public short getUtf8(final String s) {
            if (s == null) {
                throw new NullPointerException();
            }
            return this.getValue(s);
        }
        
        public short getInteger(final int n) {
            return this.getValue(new Integer(n));
        }
        
        public short getFloat(final float n) {
            return this.getValue(new Float(n));
        }
        
        public short getClass(final String s) {
            return this.getIndirect(new IndirectEntry(7, this.getUtf8(s)));
        }
        
        public short getString(final String s) {
            return this.getIndirect(new IndirectEntry(8, this.getUtf8(s)));
        }
        
        public short getFieldRef(final String s, final String s2, final String s3) {
            return this.getIndirect(new IndirectEntry(9, this.getClass(s), this.getNameAndType(s2, s3)));
        }
        
        public short getMethodRef(final String s, final String s2, final String s3) {
            return this.getIndirect(new IndirectEntry(10, this.getClass(s), this.getNameAndType(s2, s3)));
        }
        
        public short getInterfaceMethodRef(final String s, final String s2, final String s3) {
            return this.getIndirect(new IndirectEntry(11, this.getClass(s), this.getNameAndType(s2, s3)));
        }
        
        public short getNameAndType(final String s, final String s2) {
            return this.getIndirect(new IndirectEntry(12, this.getUtf8(s), this.getUtf8(s2)));
        }
        
        public void setReadOnly() {
            this.readOnly = true;
        }
        
        public void write(final OutputStream outputStream) throws IOException {
            final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeShort(this.pool.size() + 1);
            final Iterator<Entry> iterator = this.pool.iterator();
            while (iterator.hasNext()) {
                iterator.next().write(dataOutputStream);
            }
        }
        
        private short addEntry(final Entry entry) {
            this.pool.add(entry);
            if (this.pool.size() >= 65535) {
                throw new IllegalArgumentException("constant pool size limit exceeded");
            }
            return (short)this.pool.size();
        }
        
        private short getValue(final Object o) {
            final Short n = this.map.get(o);
            if (n != null) {
                return n;
            }
            if (this.readOnly) {
                throw new InternalError("late constant pool addition: " + o);
            }
            final short addEntry = this.addEntry(new ValueEntry(o));
            this.map.put(o, new Short(addEntry));
            return addEntry;
        }
        
        private short getIndirect(final IndirectEntry indirectEntry) {
            final Short n = this.map.get(indirectEntry);
            if (n != null) {
                return n;
            }
            if (this.readOnly) {
                throw new InternalError("late constant pool addition");
            }
            final short addEntry = this.addEntry(indirectEntry);
            this.map.put(indirectEntry, new Short(addEntry));
            return addEntry;
        }
        
        private abstract static class Entry
        {
            public abstract void write(final DataOutputStream p0) throws IOException;
        }
        
        private static class ValueEntry extends Entry
        {
            private Object value;
            
            public ValueEntry(final Object value) {
                this.value = value;
            }
            
            @Override
            public void write(final DataOutputStream dataOutputStream) throws IOException {
                if (this.value instanceof String) {
                    dataOutputStream.writeByte(1);
                    dataOutputStream.writeUTF((String)this.value);
                }
                else if (this.value instanceof Integer) {
                    dataOutputStream.writeByte(3);
                    dataOutputStream.writeInt((int)this.value);
                }
                else if (this.value instanceof Float) {
                    dataOutputStream.writeByte(4);
                    dataOutputStream.writeFloat((float)this.value);
                }
                else if (this.value instanceof Long) {
                    dataOutputStream.writeByte(5);
                    dataOutputStream.writeLong((long)this.value);
                }
                else {
                    if (!(this.value instanceof Double)) {
                        throw new InternalError("bogus value entry: " + this.value);
                    }
                    dataOutputStream.writeDouble(6.0);
                    dataOutputStream.writeDouble((double)this.value);
                }
            }
        }
        
        private static class IndirectEntry extends Entry
        {
            private int tag;
            private short index0;
            private short index1;
            
            public IndirectEntry(final int tag, final short index0) {
                this.tag = tag;
                this.index0 = index0;
                this.index1 = 0;
            }
            
            public IndirectEntry(final int tag, final short index0, final short index2) {
                this.tag = tag;
                this.index0 = index0;
                this.index1 = index2;
            }
            
            @Override
            public void write(final DataOutputStream dataOutputStream) throws IOException {
                dataOutputStream.writeByte(this.tag);
                dataOutputStream.writeShort(this.index0);
                if (this.tag == 9 || this.tag == 10 || this.tag == 11 || this.tag == 12) {
                    dataOutputStream.writeShort(this.index1);
                }
            }
            
            @Override
            public int hashCode() {
                return this.tag + this.index0 + this.index1;
            }
            
            @Override
            public boolean equals(final Object o) {
                if (o instanceof IndirectEntry) {
                    final IndirectEntry indirectEntry = (IndirectEntry)o;
                    if (this.tag == indirectEntry.tag && this.index0 == indirectEntry.index0 && this.index1 == indirectEntry.index1) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
