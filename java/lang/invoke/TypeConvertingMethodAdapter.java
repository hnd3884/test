package java.lang.invoke;

import sun.invoke.util.BytecodeDescriptor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.Wrapper;
import jdk.internal.org.objectweb.asm.MethodVisitor;

class TypeConvertingMethodAdapter extends MethodVisitor
{
    private static final int NUM_WRAPPERS;
    private static final String NAME_OBJECT = "java/lang/Object";
    private static final String WRAPPER_PREFIX = "Ljava/lang/";
    private static final String NAME_BOX_METHOD = "valueOf";
    private static final int[][] wideningOpcodes;
    private static final Wrapper[] FROM_WRAPPER_NAME;
    private static final Wrapper[] FROM_TYPE_SORT;
    
    TypeConvertingMethodAdapter(final MethodVisitor methodVisitor) {
        super(327680, methodVisitor);
    }
    
    private static void initWidening(final Wrapper wrapper, final int n, final Wrapper... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            TypeConvertingMethodAdapter.wideningOpcodes[array[i].ordinal()][wrapper.ordinal()] = n;
        }
    }
    
    private static int hashWrapperName(final String s) {
        if (s.length() < 3) {
            return 0;
        }
        return ('\u0003' * s.charAt(1) + s.charAt(2)) % 16;
    }
    
    private Wrapper wrapperOrNullFromDescriptor(final String s) {
        if (!s.startsWith("Ljava/lang/")) {
            return null;
        }
        final String substring = s.substring("Ljava/lang/".length(), s.length() - 1);
        final Wrapper wrapper = TypeConvertingMethodAdapter.FROM_WRAPPER_NAME[hashWrapperName(substring)];
        if (wrapper == null || wrapper.wrapperSimpleName().equals(substring)) {
            return wrapper;
        }
        return null;
    }
    
    private static String wrapperName(final Wrapper wrapper) {
        return "java/lang/" + wrapper.wrapperSimpleName();
    }
    
    private static String unboxMethod(final Wrapper wrapper) {
        return wrapper.primitiveSimpleName() + "Value";
    }
    
    private static String boxingDescriptor(final Wrapper wrapper) {
        return String.format("(%s)L%s;", wrapper.basicTypeChar(), wrapperName(wrapper));
    }
    
    private static String unboxingDescriptor(final Wrapper wrapper) {
        return "()" + wrapper.basicTypeChar();
    }
    
    void boxIfTypePrimitive(final Type type) {
        final Wrapper wrapper = TypeConvertingMethodAdapter.FROM_TYPE_SORT[type.getSort()];
        if (wrapper != null) {
            this.box(wrapper);
        }
    }
    
    void widen(final Wrapper wrapper, final Wrapper wrapper2) {
        if (wrapper != wrapper2) {
            final int n = TypeConvertingMethodAdapter.wideningOpcodes[wrapper.ordinal()][wrapper2.ordinal()];
            if (n != 0) {
                this.visitInsn(n);
            }
        }
    }
    
    void box(final Wrapper wrapper) {
        this.visitMethodInsn(184, wrapperName(wrapper), "valueOf", boxingDescriptor(wrapper), false);
    }
    
    void unbox(final String s, final Wrapper wrapper) {
        this.visitMethodInsn(182, s, unboxMethod(wrapper), unboxingDescriptor(wrapper), false);
    }
    
    private String descriptorToName(final String s) {
        final int n = s.length() - 1;
        if (s.charAt(0) == 'L' && s.charAt(n) == ';') {
            return s.substring(1, n);
        }
        return s;
    }
    
    void cast(final String s, final String s2) {
        final String descriptorToName = this.descriptorToName(s);
        final String descriptorToName2 = this.descriptorToName(s2);
        if (!descriptorToName2.equals(descriptorToName) && !descriptorToName2.equals("java/lang/Object")) {
            this.visitTypeInsn(192, descriptorToName2);
        }
    }
    
    private boolean isPrimitive(final Wrapper wrapper) {
        return wrapper != Wrapper.OBJECT;
    }
    
    private Wrapper toWrapper(final String s) {
        char char1 = s.charAt(0);
        if (char1 == '[' || char1 == '(') {
            char1 = 'L';
        }
        return Wrapper.forBasicType(char1);
    }
    
    void convertType(final Class<?> clazz, final Class<?> clazz2, final Class<?> clazz3) {
        if (clazz.equals(clazz2) && clazz.equals(clazz3)) {
            return;
        }
        if (clazz == Void.TYPE || clazz2 == Void.TYPE) {
            return;
        }
        if (clazz.isPrimitive()) {
            final Wrapper forPrimitiveType = Wrapper.forPrimitiveType(clazz);
            if (clazz2.isPrimitive()) {
                this.widen(forPrimitiveType, Wrapper.forPrimitiveType(clazz2));
            }
            else {
                final String unparse = BytecodeDescriptor.unparse(clazz2);
                final Wrapper wrapperOrNullFromDescriptor = this.wrapperOrNullFromDescriptor(unparse);
                if (wrapperOrNullFromDescriptor != null) {
                    this.widen(forPrimitiveType, wrapperOrNullFromDescriptor);
                    this.box(wrapperOrNullFromDescriptor);
                }
                else {
                    this.box(forPrimitiveType);
                    this.cast(wrapperName(forPrimitiveType), unparse);
                }
            }
        }
        else {
            final String unparse2 = BytecodeDescriptor.unparse(clazz);
            String unparse3;
            if (clazz3.isPrimitive()) {
                unparse3 = unparse2;
            }
            else {
                unparse3 = BytecodeDescriptor.unparse(clazz3);
                this.cast(unparse2, unparse3);
            }
            final String unparse4 = BytecodeDescriptor.unparse(clazz2);
            if (clazz2.isPrimitive()) {
                final Wrapper wrapper = this.toWrapper(unparse4);
                final Wrapper wrapperOrNullFromDescriptor2 = this.wrapperOrNullFromDescriptor(unparse3);
                if (wrapperOrNullFromDescriptor2 != null) {
                    if (wrapperOrNullFromDescriptor2.isSigned() || wrapperOrNullFromDescriptor2.isFloating()) {
                        this.unbox(wrapperName(wrapperOrNullFromDescriptor2), wrapper);
                    }
                    else {
                        this.unbox(wrapperName(wrapperOrNullFromDescriptor2), wrapperOrNullFromDescriptor2);
                        this.widen(wrapperOrNullFromDescriptor2, wrapper);
                    }
                }
                else {
                    String wrapperName;
                    if (wrapper.isSigned() || wrapper.isFloating()) {
                        wrapperName = "java/lang/Number";
                    }
                    else {
                        wrapperName = wrapperName(wrapper);
                    }
                    this.cast(unparse3, wrapperName);
                    this.unbox(wrapperName, wrapper);
                }
            }
            else {
                this.cast(unparse3, unparse4);
            }
        }
    }
    
    void iconst(final int n) {
        if (n >= -1 && n <= 5) {
            this.mv.visitInsn(3 + n);
        }
        else if (n >= -128 && n <= 127) {
            this.mv.visitIntInsn(16, n);
        }
        else if (n >= -32768 && n <= 32767) {
            this.mv.visitIntInsn(17, n);
        }
        else {
            this.mv.visitLdcInsn(n);
        }
    }
    
    static {
        NUM_WRAPPERS = Wrapper.values().length;
        wideningOpcodes = new int[TypeConvertingMethodAdapter.NUM_WRAPPERS][TypeConvertingMethodAdapter.NUM_WRAPPERS];
        FROM_WRAPPER_NAME = new Wrapper[16];
        FROM_TYPE_SORT = new Wrapper[16];
        for (final Wrapper wrapper : Wrapper.values()) {
            if (wrapper.basicTypeChar() != 'L') {
                final int hashWrapperName = hashWrapperName(wrapper.wrapperSimpleName());
                assert TypeConvertingMethodAdapter.FROM_WRAPPER_NAME[hashWrapperName] == null;
                TypeConvertingMethodAdapter.FROM_WRAPPER_NAME[hashWrapperName] = wrapper;
            }
        }
        for (int j = 0; j < TypeConvertingMethodAdapter.NUM_WRAPPERS; ++j) {
            for (int k = 0; k < TypeConvertingMethodAdapter.NUM_WRAPPERS; ++k) {
                TypeConvertingMethodAdapter.wideningOpcodes[j][k] = 0;
            }
        }
        initWidening(Wrapper.LONG, 133, Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR);
        initWidening(Wrapper.LONG, 140, Wrapper.FLOAT);
        initWidening(Wrapper.FLOAT, 134, Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR);
        initWidening(Wrapper.FLOAT, 137, Wrapper.LONG);
        initWidening(Wrapper.DOUBLE, 135, Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR);
        initWidening(Wrapper.DOUBLE, 141, Wrapper.FLOAT);
        initWidening(Wrapper.DOUBLE, 138, Wrapper.LONG);
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[3] = Wrapper.BYTE;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[4] = Wrapper.SHORT;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[5] = Wrapper.INT;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[7] = Wrapper.LONG;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[2] = Wrapper.CHAR;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[6] = Wrapper.FLOAT;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[8] = Wrapper.DOUBLE;
        TypeConvertingMethodAdapter.FROM_TYPE_SORT[1] = Wrapper.BOOLEAN;
    }
}
