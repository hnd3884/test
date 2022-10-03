package java.lang.invoke;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.invoke.util.VerifyAccess;
import java.lang.reflect.Modifier;
import sun.invoke.util.BytecodeDescriptor;
import java.lang.reflect.Member;

final class MemberName implements Member, Cloneable
{
    private Class<?> clazz;
    private String name;
    private Object type;
    private int flags;
    private Object resolution;
    private static final int MH_INVOKE_MODS = 273;
    static final int BRIDGE = 64;
    static final int VARARGS = 128;
    static final int SYNTHETIC = 4096;
    static final int ANNOTATION = 8192;
    static final int ENUM = 16384;
    static final String CONSTRUCTOR_NAME = "<init>";
    static final int RECOGNIZED_MODIFIERS = 65535;
    static final int IS_METHOD = 65536;
    static final int IS_CONSTRUCTOR = 131072;
    static final int IS_FIELD = 262144;
    static final int IS_TYPE = 524288;
    static final int CALLER_SENSITIVE = 1048576;
    static final int ALL_ACCESS = 7;
    static final int ALL_KINDS = 983040;
    static final int IS_INVOCABLE = 196608;
    static final int IS_FIELD_OR_METHOD = 327680;
    static final int SEARCH_ALL_SUPERS = 3145728;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    @Override
    public Class<?> getDeclaringClass() {
        return this.clazz;
    }
    
    public ClassLoader getClassLoader() {
        return this.clazz.getClassLoader();
    }
    
    @Override
    public String getName() {
        if (this.name == null) {
            this.expandFromVM();
            if (this.name == null) {
                return null;
            }
        }
        return this.name;
    }
    
    public MethodType getMethodOrFieldType() {
        if (this.isInvocable()) {
            return this.getMethodType();
        }
        if (this.isGetter()) {
            return MethodType.methodType(this.getFieldType());
        }
        if (this.isSetter()) {
            return MethodType.methodType(Void.TYPE, this.getFieldType());
        }
        throw new InternalError("not a method or field: " + this);
    }
    
    public MethodType getMethodType() {
        if (this.type == null) {
            this.expandFromVM();
            if (this.type == null) {
                return null;
            }
        }
        if (!this.isInvocable()) {
            throw MethodHandleStatics.newIllegalArgumentException("not invocable, no method type");
        }
        final Object type = this.type;
        if (type instanceof MethodType) {
            return (MethodType)type;
        }
        synchronized (this) {
            if (this.type instanceof String) {
                this.type = MethodType.fromMethodDescriptorString((String)this.type, this.getClassLoader());
            }
            else if (this.type instanceof Object[]) {
                final Object[] array = (Object[])this.type;
                this.type = MethodType.methodType((Class<?>)array[0], (Class<?>[])array[1]);
            }
            assert this.type instanceof MethodType : "bad method type " + this.type;
        }
        return (MethodType)this.type;
    }
    
    public MethodType getInvocationType() {
        final MethodType methodOrFieldType = this.getMethodOrFieldType();
        if (this.isConstructor() && this.getReferenceKind() == 8) {
            return methodOrFieldType.changeReturnType(this.clazz);
        }
        if (!this.isStatic()) {
            return methodOrFieldType.insertParameterTypes(0, this.clazz);
        }
        return methodOrFieldType;
    }
    
    public Class<?>[] getParameterTypes() {
        return this.getMethodType().parameterArray();
    }
    
    public Class<?> getReturnType() {
        return this.getMethodType().returnType();
    }
    
    public Class<?> getFieldType() {
        if (this.type == null) {
            this.expandFromVM();
            if (this.type == null) {
                return null;
            }
        }
        if (this.isInvocable()) {
            throw MethodHandleStatics.newIllegalArgumentException("not a field or nested class, no simple type");
        }
        final Object type = this.type;
        if (type instanceof Class) {
            return (Class<?>)type;
        }
        synchronized (this) {
            if (this.type instanceof String) {
                this.type = MethodType.fromMethodDescriptorString("()" + (String)this.type, this.getClassLoader()).returnType();
            }
            assert this.type instanceof Class : "bad field type " + this.type;
        }
        return (Class)this.type;
    }
    
    public Object getType() {
        return this.isInvocable() ? this.getMethodType() : this.getFieldType();
    }
    
    public String getSignature() {
        if (this.type == null) {
            this.expandFromVM();
            if (this.type == null) {
                return null;
            }
        }
        if (this.isInvocable()) {
            return BytecodeDescriptor.unparse(this.getMethodType());
        }
        return BytecodeDescriptor.unparse(this.getFieldType());
    }
    
    @Override
    public int getModifiers() {
        return this.flags & 0xFFFF;
    }
    
    public byte getReferenceKind() {
        return (byte)(this.flags >>> 24 & 0xF);
    }
    
    private boolean referenceKindIsConsistent() {
        final byte referenceKind = this.getReferenceKind();
        if (referenceKind == 0) {
            return this.isType();
        }
        if (this.isField()) {
            assert this.staticIsConsistent();
            assert MethodHandleNatives.refKindIsField(referenceKind);
        }
        else if (this.isConstructor()) {
            assert referenceKind == 7;
        }
        else if (this.isMethod()) {
            assert this.staticIsConsistent();
            assert MethodHandleNatives.refKindIsMethod(referenceKind);
            if (this.clazz.isInterface() && !MemberName.$assertionsDisabled && referenceKind != 9 && referenceKind != 6 && referenceKind != 7 && (referenceKind != 5 || !this.isObjectPublicMethod())) {
                throw new AssertionError();
            }
        }
        else {
            assert false;
        }
        return true;
    }
    
    private boolean isObjectPublicMethod() {
        if (this.clazz == Object.class) {
            return true;
        }
        final MethodType methodType = this.getMethodType();
        return (this.name.equals("toString") && methodType.returnType() == String.class && methodType.parameterCount() == 0) || (this.name.equals("hashCode") && methodType.returnType() == Integer.TYPE && methodType.parameterCount() == 0) || (this.name.equals("equals") && methodType.returnType() == Boolean.TYPE && methodType.parameterCount() == 1 && methodType.parameterType(0) == Object.class);
    }
    
    boolean referenceKindIsConsistentWith(final int n) {
        final byte referenceKind = this.getReferenceKind();
        if (referenceKind == n) {
            return true;
        }
        switch (n) {
            case 9: {
                assert referenceKind == 7 : this;
                return true;
            }
            case 5:
            case 8: {
                assert referenceKind == 7 : this;
                return true;
            }
            default: {
                assert false : this + " != " + MethodHandleNatives.refKindName((byte)n);
                return true;
            }
        }
    }
    
    private boolean staticIsConsistent() {
        return MethodHandleNatives.refKindIsStatic(this.getReferenceKind()) == this.isStatic() || this.getModifiers() == 0;
    }
    
    private boolean vminfoIsConsistent() {
        final byte referenceKind = this.getReferenceKind();
        assert this.isResolved();
        final Object memberVMInfo = MethodHandleNatives.getMemberVMInfo(this);
        assert memberVMInfo instanceof Object[];
        final long longValue = (long)((Object[])memberVMInfo)[0];
        final Object o = ((Object[])memberVMInfo)[1];
        if (MethodHandleNatives.refKindIsField(referenceKind)) {
            assert longValue >= 0L : longValue + ":" + this;
            assert o instanceof Class;
        }
        else {
            if (MethodHandleNatives.refKindDoesDispatch(referenceKind)) {
                assert longValue >= 0L : longValue + ":" + this;
            }
            else {
                assert longValue < 0L : longValue;
            }
            assert o instanceof MemberName : o + " in " + this;
        }
        return true;
    }
    
    private MemberName changeReferenceKind(final byte b, final byte b2) {
        assert this.getReferenceKind() == b2;
        assert MethodHandleNatives.refKindIsValid(b);
        this.flags += b - b2 << 24;
        return this;
    }
    
    private boolean testFlags(final int n, final int n2) {
        return (this.flags & n) == n2;
    }
    
    private boolean testAllFlags(final int n) {
        return this.testFlags(n, n);
    }
    
    private boolean testAnyFlags(final int n) {
        return !this.testFlags(n, 0);
    }
    
    public boolean isMethodHandleInvoke() {
        return this.testFlags(280, 272) && this.clazz == MethodHandle.class && isMethodHandleInvokeName(this.name);
    }
    
    public static boolean isMethodHandleInvokeName(final String s) {
        switch (s) {
            case "invoke":
            case "invokeExact": {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isStatic() {
        return Modifier.isStatic(this.flags);
    }
    
    public boolean isPublic() {
        return Modifier.isPublic(this.flags);
    }
    
    public boolean isPrivate() {
        return Modifier.isPrivate(this.flags);
    }
    
    public boolean isProtected() {
        return Modifier.isProtected(this.flags);
    }
    
    public boolean isFinal() {
        return Modifier.isFinal(this.flags);
    }
    
    public boolean canBeStaticallyBound() {
        return Modifier.isFinal(this.flags | this.clazz.getModifiers());
    }
    
    public boolean isVolatile() {
        return Modifier.isVolatile(this.flags);
    }
    
    public boolean isAbstract() {
        return Modifier.isAbstract(this.flags);
    }
    
    public boolean isNative() {
        return Modifier.isNative(this.flags);
    }
    
    public boolean isBridge() {
        return this.testAllFlags(65600);
    }
    
    public boolean isVarargs() {
        return this.testAllFlags(128) && this.isInvocable();
    }
    
    @Override
    public boolean isSynthetic() {
        return this.testAllFlags(4096);
    }
    
    public boolean isInvocable() {
        return this.testAnyFlags(196608);
    }
    
    public boolean isFieldOrMethod() {
        return this.testAnyFlags(327680);
    }
    
    public boolean isMethod() {
        return this.testAllFlags(65536);
    }
    
    public boolean isConstructor() {
        return this.testAllFlags(131072);
    }
    
    public boolean isField() {
        return this.testAllFlags(262144);
    }
    
    public boolean isType() {
        return this.testAllFlags(524288);
    }
    
    public boolean isPackage() {
        return !this.testAnyFlags(7);
    }
    
    public boolean isCallerSensitive() {
        return this.testAllFlags(1048576);
    }
    
    public boolean isAccessibleFrom(final Class<?> clazz) {
        return VerifyAccess.isMemberAccessible(this.getDeclaringClass(), this.getDeclaringClass(), this.flags, clazz, 15);
    }
    
    private void init(final Class<?> clazz, final String name, final Object type, final int flags) {
        this.clazz = clazz;
        this.name = name;
        this.type = type;
        this.flags = flags;
        assert this.testAnyFlags(983040);
        assert this.resolution == null;
    }
    
    private void expandFromVM() {
        if (this.type != null) {
            return;
        }
        if (!this.isResolved()) {
            return;
        }
        MethodHandleNatives.expand(this);
    }
    
    private static int flagsMods(final int n, final int n2, final byte b) {
        assert (n & 0xFFFF) == 0x0;
        assert (n2 & 0xFFFF0000) == 0x0;
        assert (b & 0xFFFFFFF0) == 0x0;
        return n | n2 | b << 24;
    }
    
    public MemberName(final Method method) {
        this(method, false);
    }
    
    public MemberName(final Method method, final boolean b) {
        method.getClass();
        MethodHandleNatives.init(this, method);
        if (this.clazz == null) {
            if (method.getDeclaringClass() == MethodHandle.class && isMethodHandleInvokeName(method.getName())) {
                this.init(MethodHandle.class, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()), flagsMods(65536, method.getModifiers(), (byte)5));
                if (this.isMethodHandleInvoke()) {
                    return;
                }
            }
            throw new LinkageError(method.toString());
        }
        assert this.isResolved() && this.clazz != null;
        this.name = method.getName();
        if (this.type == null) {
            this.type = new Object[] { method.getReturnType(), method.getParameterTypes() };
        }
        if (b) {
            if (this.isAbstract()) {
                throw new AbstractMethodError(this.toString());
            }
            if (this.getReferenceKind() == 5) {
                this.changeReferenceKind((byte)7, (byte)5);
            }
            else if (this.getReferenceKind() == 9) {
                this.changeReferenceKind((byte)7, (byte)9);
            }
        }
    }
    
    public MemberName asSpecial() {
        switch (this.getReferenceKind()) {
            case 7: {
                return this;
            }
            case 5: {
                return this.clone().changeReferenceKind((byte)7, (byte)5);
            }
            case 9: {
                return this.clone().changeReferenceKind((byte)7, (byte)9);
            }
            case 8: {
                return this.clone().changeReferenceKind((byte)7, (byte)8);
            }
            default: {
                throw new IllegalArgumentException(this.toString());
            }
        }
    }
    
    public MemberName asConstructor() {
        switch (this.getReferenceKind()) {
            case 7: {
                return this.clone().changeReferenceKind((byte)8, (byte)7);
            }
            case 8: {
                return this;
            }
            default: {
                throw new IllegalArgumentException(this.toString());
            }
        }
    }
    
    public MemberName asNormalOriginal() {
        final byte b = (byte)(this.clazz.isInterface() ? 9 : 5);
        byte referenceKind;
        final byte b2 = referenceKind = this.getReferenceKind();
        switch (b2) {
            case 5:
            case 7:
            case 9: {
                referenceKind = b;
                break;
            }
        }
        if (referenceKind == b2) {
            return this;
        }
        final MemberName changeReferenceKind = this.clone().changeReferenceKind(referenceKind, b2);
        assert this.referenceKindIsConsistentWith(changeReferenceKind.getReferenceKind());
        return changeReferenceKind;
    }
    
    public MemberName(final Constructor<?> constructor) {
        constructor.getClass();
        MethodHandleNatives.init(this, constructor);
        assert this.isResolved() && this.clazz != null;
        this.name = "<init>";
        if (this.type == null) {
            this.type = new Object[] { Void.TYPE, constructor.getParameterTypes() };
        }
    }
    
    public MemberName(final Field field) {
        this(field, false);
    }
    
    public MemberName(final Field field, final boolean b) {
        field.getClass();
        MethodHandleNatives.init(this, field);
        assert this.isResolved() && this.clazz != null;
        this.name = field.getName();
        this.type = field.getType();
        final byte referenceKind = this.getReferenceKind();
        assert referenceKind == (this.isStatic() ? 2 : 1);
        if (b) {
            this.changeReferenceKind((byte)(referenceKind + 2), referenceKind);
        }
    }
    
    public boolean isGetter() {
        return MethodHandleNatives.refKindIsGetter(this.getReferenceKind());
    }
    
    public boolean isSetter() {
        return MethodHandleNatives.refKindIsSetter(this.getReferenceKind());
    }
    
    public MemberName asSetter() {
        final byte referenceKind = this.getReferenceKind();
        assert MethodHandleNatives.refKindIsGetter(referenceKind);
        return this.clone().changeReferenceKind((byte)(referenceKind + 2), referenceKind);
    }
    
    public MemberName(final Class<?> clazz) {
        this.init(clazz.getDeclaringClass(), clazz.getSimpleName(), clazz, flagsMods(524288, clazz.getModifiers(), (byte)0));
        this.initResolved(true);
    }
    
    static MemberName makeMethodHandleInvoke(final String s, final MethodType methodType) {
        return makeMethodHandleInvoke(s, methodType, 4369);
    }
    
    static MemberName makeMethodHandleInvoke(final String s, final MethodType methodType, final int n) {
        final MemberName memberName2;
        final MemberName memberName = memberName2 = new MemberName(MethodHandle.class, s, methodType, (byte)5);
        memberName2.flags |= n;
        assert memberName.isMethodHandleInvoke() : memberName;
        return memberName;
    }
    
    MemberName() {
    }
    
    @Override
    protected MemberName clone() {
        try {
            return (MemberName)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw MethodHandleStatics.newInternalError(ex);
        }
    }
    
    public MemberName getDefinition() {
        if (!this.isResolved()) {
            throw new IllegalStateException("must be resolved: " + this);
        }
        if (this.isType()) {
            return this;
        }
        final MemberName clone = this.clone();
        clone.clazz = null;
        clone.type = null;
        clone.name = null;
        ((MemberName)(clone.resolution = clone)).expandFromVM();
        assert clone.getName().equals(this.getName());
        return clone;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.clazz, this.getReferenceKind(), this.name, this.getType());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MemberName && this.equals((MemberName)o);
    }
    
    public boolean equals(final MemberName memberName) {
        return this == memberName || (memberName != null && this.clazz == memberName.clazz && this.getReferenceKind() == memberName.getReferenceKind() && Objects.equals(this.name, memberName.name) && Objects.equals(this.getType(), memberName.getType()));
    }
    
    public MemberName(final Class<?> clazz, final String s, final Class<?> clazz2, final byte b) {
        this.init(clazz, s, clazz2, flagsMods(262144, 0, b));
        this.initResolved(false);
    }
    
    public MemberName(final Class<?> clazz, final String s, final MethodType methodType, final byte b) {
        this.init(clazz, s, methodType, flagsMods((s != null && s.equals("<init>")) ? 131072 : 65536, 0, b));
        this.initResolved(false);
    }
    
    public MemberName(final byte b, final Class<?> clazz, final String s, final Object o) {
        int n;
        if (MethodHandleNatives.refKindIsField(b)) {
            n = 262144;
            if (!(o instanceof Class)) {
                throw MethodHandleStatics.newIllegalArgumentException("not a field type");
            }
        }
        else if (MethodHandleNatives.refKindIsMethod(b)) {
            n = 65536;
            if (!(o instanceof MethodType)) {
                throw MethodHandleStatics.newIllegalArgumentException("not a method type");
            }
        }
        else {
            if (b != 8) {
                throw MethodHandleStatics.newIllegalArgumentException("bad reference kind " + b);
            }
            n = 131072;
            if (!(o instanceof MethodType) || !"<init>".equals(s)) {
                throw MethodHandleStatics.newIllegalArgumentException("not a constructor type or name");
            }
        }
        this.init(clazz, s, o, flagsMods(n, 0, b));
        this.initResolved(false);
    }
    
    public boolean hasReceiverTypeDispatch() {
        return MethodHandleNatives.refKindDoesDispatch(this.getReferenceKind());
    }
    
    public boolean isResolved() {
        return this.resolution == null;
    }
    
    private void initResolved(final boolean b) {
        assert this.resolution == null;
        if (!b) {
            this.resolution = this;
        }
        assert this.isResolved() == b;
    }
    
    void checkForTypeAlias(final Class<?> clazz) {
        if (this.isInvocable()) {
            Object o;
            if (this.type instanceof MethodType) {
                o = this.type;
            }
            else {
                o = (this.type = this.getMethodType());
            }
            if (((MethodType)o).erase() == o) {
                return;
            }
            if (VerifyAccess.isTypeVisible((MethodType)o, clazz)) {
                return;
            }
            throw new LinkageError("bad method type alias: " + o + " not visible from " + clazz);
        }
        else {
            Object o2;
            if (this.type instanceof Class) {
                o2 = this.type;
            }
            else {
                o2 = (this.type = this.getFieldType());
            }
            if (VerifyAccess.isTypeVisible((Class<?>)o2, clazz)) {
                return;
            }
            throw new LinkageError("bad field type alias: " + o2 + " not visible from " + clazz);
        }
    }
    
    @Override
    public String toString() {
        if (this.isType()) {
            return this.type.toString();
        }
        final StringBuilder sb = new StringBuilder();
        if (this.getDeclaringClass() != null) {
            sb.append(getName(this.clazz));
            sb.append('.');
        }
        final String name = this.getName();
        sb.append((name == null) ? "*" : name);
        final Object type = this.getType();
        if (!this.isInvocable()) {
            sb.append('/');
            sb.append((type == null) ? "*" : getName(type));
        }
        else {
            sb.append((type == null) ? "(*)*" : getName(type));
        }
        final byte referenceKind = this.getReferenceKind();
        if (referenceKind != 0) {
            sb.append('/');
            sb.append(MethodHandleNatives.refKindName(referenceKind));
        }
        return sb.toString();
    }
    
    private static String getName(final Object o) {
        if (o instanceof Class) {
            return ((Class)o).getName();
        }
        return String.valueOf(o);
    }
    
    public IllegalAccessException makeAccessException(String s, final Object o) {
        s = s + ": " + this.toString();
        if (o != null) {
            s = s + ", from " + o;
        }
        return new IllegalAccessException(s);
    }
    
    private String message() {
        if (this.isResolved()) {
            return "no access";
        }
        if (this.isConstructor()) {
            return "no such constructor";
        }
        if (this.isMethod()) {
            return "no such method";
        }
        return "no such field";
    }
    
    public ReflectiveOperationException makeAccessException() {
        final String string = this.message() + ": " + this.toString();
        ReflectiveOperationException ex;
        if (this.isResolved() || (!(this.resolution instanceof NoSuchMethodError) && !(this.resolution instanceof NoSuchFieldError))) {
            ex = new IllegalAccessException(string);
        }
        else if (this.isConstructor()) {
            ex = new NoSuchMethodException(string);
        }
        else if (this.isMethod()) {
            ex = new NoSuchMethodException(string);
        }
        else {
            ex = new NoSuchFieldException(string);
        }
        if (this.resolution instanceof Throwable) {
            ex.initCause((Throwable)this.resolution);
        }
        return ex;
    }
    
    static Factory getFactory() {
        return Factory.INSTANCE;
    }
    
    static class Factory
    {
        static Factory INSTANCE;
        private static int ALLOWED_FLAGS;
        
        private Factory() {
        }
        
        List<MemberName> getMembers(final Class<?> clazz, final String s, final Object o, int n, final Class<?> clazz2) {
            n &= Factory.ALLOWED_FLAGS;
            String unparse = null;
            if (o != null) {
                unparse = BytecodeDescriptor.unparse(o);
                if (unparse.startsWith("(")) {
                    n &= 0xFFF3FFFF;
                }
                else {
                    n &= 0xFFF4FFFF;
                }
            }
            MemberName[] array = newMemberBuffer((s == null) ? 10 : ((o == null) ? 4 : 1));
            int n2 = 0;
            ArrayList<MemberName[]> list = null;
            int members;
            while (true) {
                members = MethodHandleNatives.getMembers(clazz, s, unparse, n, clazz2, n2, array);
                if (members <= array.length) {
                    break;
                }
                n2 += array.length;
                final int n3 = members - array.length;
                if (list == null) {
                    list = new ArrayList<MemberName[]>(1);
                }
                list.add(array);
                array = newMemberBuffer(Math.min(8192, Math.max(Math.max(array.length, n3), n2 / 4)));
            }
            if (members < 0) {
                members = 0;
            }
            final ArrayList list2 = new ArrayList<Object>(n2 + members);
            if (list != null) {
                final Iterator<MemberName[]> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Collections.addAll((Collection<? super MemberName>)list2, (MemberName[])iterator.next());
                }
            }
            list2.addAll((Collection<? extends T>)Arrays.asList(array).subList(0, members));
            if (o != null && o != unparse) {
                final Iterator<? super T> iterator2 = list2.iterator();
                while (iterator2.hasNext()) {
                    if (!o.equals(((MemberName)iterator2.next()).getType())) {
                        iterator2.remove();
                    }
                }
            }
            return (List<MemberName>)list2;
        }
        
        private MemberName resolve(final byte b, final MemberName memberName, final Class<?> clazz) {
            MemberName memberName2 = memberName.clone();
            assert b == memberName2.getReferenceKind();
            try {
                memberName2 = MethodHandleNatives.resolve(memberName2, clazz);
                memberName2.checkForTypeAlias(memberName2.getDeclaringClass());
                memberName2.resolution = null;
            }
            catch (final ClassNotFoundException | LinkageError classNotFoundException | LinkageError) {
                assert !memberName2.isResolved();
                memberName2.resolution = classNotFoundException | LinkageError;
                return memberName2;
            }
            assert memberName2.referenceKindIsConsistent();
            memberName2.initResolved(true);
            assert memberName2.vminfoIsConsistent();
            return memberName2;
        }
        
        public <NoSuchMemberException extends ReflectiveOperationException> MemberName resolveOrFail(final byte b, final MemberName memberName, final Class<?> clazz, final Class<NoSuchMemberException> clazz2) throws IllegalAccessException, NoSuchMemberException, ReflectiveOperationException {
            final MemberName resolve = this.resolve(b, memberName, clazz);
            if (resolve.isResolved()) {
                return resolve;
            }
            final ReflectiveOperationException accessException = resolve.makeAccessException();
            if (accessException instanceof IllegalAccessException) {
                throw (IllegalAccessException)accessException;
            }
            throw clazz2.cast(accessException);
        }
        
        public MemberName resolveOrNull(final byte b, final MemberName memberName, final Class<?> clazz) {
            final MemberName resolve = this.resolve(b, memberName, clazz);
            if (resolve.isResolved()) {
                return resolve;
            }
            return null;
        }
        
        public List<MemberName> getMethods(final Class<?> clazz, final boolean b, final Class<?> clazz2) {
            return this.getMethods(clazz, b, null, null, clazz2);
        }
        
        public List<MemberName> getMethods(final Class<?> clazz, final boolean b, final String s, final MethodType methodType, final Class<?> clazz2) {
            return this.getMembers(clazz, s, methodType, 0x10000 | (b ? 3145728 : 0), clazz2);
        }
        
        public List<MemberName> getConstructors(final Class<?> clazz, final Class<?> clazz2) {
            return this.getMembers(clazz, null, null, 131072, clazz2);
        }
        
        public List<MemberName> getFields(final Class<?> clazz, final boolean b, final Class<?> clazz2) {
            return this.getFields(clazz, b, null, null, clazz2);
        }
        
        public List<MemberName> getFields(final Class<?> clazz, final boolean b, final String s, final Class<?> clazz2, final Class<?> clazz3) {
            return this.getMembers(clazz, s, clazz2, 0x40000 | (b ? 3145728 : 0), clazz3);
        }
        
        public List<MemberName> getNestedTypes(final Class<?> clazz, final boolean b, final Class<?> clazz2) {
            return this.getMembers(clazz, null, null, 0x80000 | (b ? 3145728 : 0), clazz2);
        }
        
        private static MemberName[] newMemberBuffer(final int n) {
            final MemberName[] array = new MemberName[n];
            for (int i = 0; i < n; ++i) {
                array[i] = new MemberName();
            }
            return array;
        }
        
        static {
            Factory.INSTANCE = new Factory();
            Factory.ALLOWED_FLAGS = 983040;
        }
    }
}
