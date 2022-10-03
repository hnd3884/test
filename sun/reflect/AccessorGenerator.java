package sun.reflect;

import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

class AccessorGenerator implements ClassFileConstants
{
    static final Unsafe unsafe;
    protected static final short S0 = 0;
    protected static final short S1 = 1;
    protected static final short S2 = 2;
    protected static final short S3 = 3;
    protected static final short S4 = 4;
    protected static final short S5 = 5;
    protected static final short S6 = 6;
    protected ClassFileAssembler asm;
    protected int modifiers;
    protected short thisClass;
    protected short superClass;
    protected short targetClass;
    protected short throwableClass;
    protected short classCastClass;
    protected short nullPointerClass;
    protected short illegalArgumentClass;
    protected short invocationTargetClass;
    protected short initIdx;
    protected short initNameAndTypeIdx;
    protected short initStringNameAndTypeIdx;
    protected short nullPointerCtorIdx;
    protected short illegalArgumentCtorIdx;
    protected short illegalArgumentStringCtorIdx;
    protected short invocationTargetCtorIdx;
    protected short superCtorIdx;
    protected short objectClass;
    protected short toStringIdx;
    protected short codeIdx;
    protected short exceptionsIdx;
    protected short booleanIdx;
    protected short booleanCtorIdx;
    protected short booleanUnboxIdx;
    protected short byteIdx;
    protected short byteCtorIdx;
    protected short byteUnboxIdx;
    protected short characterIdx;
    protected short characterCtorIdx;
    protected short characterUnboxIdx;
    protected short doubleIdx;
    protected short doubleCtorIdx;
    protected short doubleUnboxIdx;
    protected short floatIdx;
    protected short floatCtorIdx;
    protected short floatUnboxIdx;
    protected short integerIdx;
    protected short integerCtorIdx;
    protected short integerUnboxIdx;
    protected short longIdx;
    protected short longCtorIdx;
    protected short longUnboxIdx;
    protected short shortIdx;
    protected short shortCtorIdx;
    protected short shortUnboxIdx;
    protected final short NUM_COMMON_CPOOL_ENTRIES = 30;
    protected final short NUM_BOXING_CPOOL_ENTRIES = 72;
    protected static final Class<?>[] primitiveTypes;
    private ClassFileAssembler illegalArgumentCodeBuffer;
    
    protected void emitCommonConstantPoolEntries() {
        this.asm.emitConstantPoolUTF8("java/lang/Throwable");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.throwableClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/ClassCastException");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.classCastClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/NullPointerException");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.nullPointerClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/IllegalArgumentException");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.illegalArgumentClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/reflect/InvocationTargetException");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.invocationTargetClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("<init>");
        this.initIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("()V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.initNameAndTypeIdx = this.asm.cpi();
        this.asm.emitConstantPoolMethodref(this.nullPointerClass, this.initNameAndTypeIdx);
        this.nullPointerCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initNameAndTypeIdx);
        this.illegalArgumentCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(Ljava/lang/String;)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.initStringNameAndTypeIdx = this.asm.cpi();
        this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initStringNameAndTypeIdx);
        this.illegalArgumentStringCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(Ljava/lang/Throwable;)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(this.invocationTargetClass, this.asm.cpi());
        this.invocationTargetCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolMethodref(this.superClass, this.initNameAndTypeIdx);
        this.superCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Object");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.objectClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("toString");
        this.asm.emitConstantPoolUTF8("()Ljava/lang/String;");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(this.objectClass, this.asm.cpi());
        this.toStringIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("Code");
        this.codeIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("Exceptions");
        this.exceptionsIdx = this.asm.cpi();
    }
    
    protected void emitBoxingContantPoolEntries() {
        this.asm.emitConstantPoolUTF8("java/lang/Boolean");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.booleanIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(Z)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.booleanCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("booleanValue");
        this.asm.emitConstantPoolUTF8("()Z");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.booleanUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Byte");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.byteIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(B)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.byteCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("byteValue");
        this.asm.emitConstantPoolUTF8("()B");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.byteUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Character");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.characterIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(C)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.characterCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("charValue");
        this.asm.emitConstantPoolUTF8("()C");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.characterUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Double");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.doubleIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(D)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.doubleCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("doubleValue");
        this.asm.emitConstantPoolUTF8("()D");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.doubleUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Float");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.floatIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(F)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.floatCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("floatValue");
        this.asm.emitConstantPoolUTF8("()F");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.floatUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Integer");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.integerIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(I)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.integerCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("intValue");
        this.asm.emitConstantPoolUTF8("()I");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.integerUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Long");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.longIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(J)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.longCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("longValue");
        this.asm.emitConstantPoolUTF8("()J");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.longUnboxIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("java/lang/Short");
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.shortIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("(S)V");
        this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
        this.shortCtorIdx = this.asm.cpi();
        this.asm.emitConstantPoolUTF8("shortValue");
        this.asm.emitConstantPoolUTF8("()S");
        this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
        this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
        this.shortUnboxIdx = this.asm.cpi();
    }
    
    protected static short add(final short n, final short n2) {
        return (short)(n + n2);
    }
    
    protected static short sub(final short n, final short n2) {
        return (short)(n - n2);
    }
    
    protected boolean isStatic() {
        return Modifier.isStatic(this.modifiers);
    }
    
    protected boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers);
    }
    
    protected static String getClassName(final Class<?> clazz, final boolean b) {
        if (clazz.isPrimitive()) {
            if (clazz == Boolean.TYPE) {
                return "Z";
            }
            if (clazz == Byte.TYPE) {
                return "B";
            }
            if (clazz == Character.TYPE) {
                return "C";
            }
            if (clazz == Double.TYPE) {
                return "D";
            }
            if (clazz == Float.TYPE) {
                return "F";
            }
            if (clazz == Integer.TYPE) {
                return "I";
            }
            if (clazz == Long.TYPE) {
                return "J";
            }
            if (clazz == Short.TYPE) {
                return "S";
            }
            if (clazz == Void.TYPE) {
                return "V";
            }
            throw new InternalError("Should have found primitive type");
        }
        else {
            if (clazz.isArray()) {
                return "[" + getClassName(clazz.getComponentType(), true);
            }
            if (b) {
                return internalize("L" + clazz.getName() + ";");
            }
            return internalize(clazz.getName());
        }
    }
    
    private static String internalize(final String s) {
        return s.replace('.', '/');
    }
    
    protected void emitConstructor() {
        final ClassFileAssembler classFileAssembler = new ClassFileAssembler();
        classFileAssembler.setMaxLocals(1);
        classFileAssembler.opc_aload_0();
        classFileAssembler.opc_invokespecial(this.superCtorIdx, 0, 0);
        classFileAssembler.opc_return();
        this.emitMethod(this.initIdx, classFileAssembler.getMaxLocals(), classFileAssembler, null, null);
    }
    
    protected void emitMethod(final short n, final int n2, final ClassFileAssembler classFileAssembler, final ClassFileAssembler classFileAssembler2, final short[] array) {
        final short length = classFileAssembler.getLength();
        int length2 = 0;
        if (classFileAssembler2 != null) {
            length2 = classFileAssembler2.getLength();
            if (length2 % 8 != 0) {
                throw new IllegalArgumentException("Illegal exception table");
            }
        }
        final int n3 = 12 + length + length2;
        final int n4 = length2 / 8;
        this.asm.emitShort((short)1);
        this.asm.emitShort(n);
        this.asm.emitShort(add(n, (short)1));
        if (array == null) {
            this.asm.emitShort((short)1);
        }
        else {
            this.asm.emitShort((short)2);
        }
        this.asm.emitShort(this.codeIdx);
        this.asm.emitInt(n3);
        this.asm.emitShort(classFileAssembler.getMaxStack());
        this.asm.emitShort((short)Math.max(n2, classFileAssembler.getMaxLocals()));
        this.asm.emitInt(length);
        this.asm.append(classFileAssembler);
        this.asm.emitShort((short)n4);
        if (classFileAssembler2 != null) {
            this.asm.append(classFileAssembler2);
        }
        this.asm.emitShort((short)0);
        if (array != null) {
            this.asm.emitShort(this.exceptionsIdx);
            this.asm.emitInt(2 + 2 * array.length);
            this.asm.emitShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.asm.emitShort(array[i]);
            }
        }
    }
    
    protected short indexForPrimitiveType(final Class<?> clazz) {
        if (clazz == Boolean.TYPE) {
            return this.booleanIdx;
        }
        if (clazz == Byte.TYPE) {
            return this.byteIdx;
        }
        if (clazz == Character.TYPE) {
            return this.characterIdx;
        }
        if (clazz == Double.TYPE) {
            return this.doubleIdx;
        }
        if (clazz == Float.TYPE) {
            return this.floatIdx;
        }
        if (clazz == Integer.TYPE) {
            return this.integerIdx;
        }
        if (clazz == Long.TYPE) {
            return this.longIdx;
        }
        if (clazz == Short.TYPE) {
            return this.shortIdx;
        }
        throw new InternalError("Should have found primitive type");
    }
    
    protected short ctorIndexForPrimitiveType(final Class<?> clazz) {
        if (clazz == Boolean.TYPE) {
            return this.booleanCtorIdx;
        }
        if (clazz == Byte.TYPE) {
            return this.byteCtorIdx;
        }
        if (clazz == Character.TYPE) {
            return this.characterCtorIdx;
        }
        if (clazz == Double.TYPE) {
            return this.doubleCtorIdx;
        }
        if (clazz == Float.TYPE) {
            return this.floatCtorIdx;
        }
        if (clazz == Integer.TYPE) {
            return this.integerCtorIdx;
        }
        if (clazz == Long.TYPE) {
            return this.longCtorIdx;
        }
        if (clazz == Short.TYPE) {
            return this.shortCtorIdx;
        }
        throw new InternalError("Should have found primitive type");
    }
    
    protected static boolean canWidenTo(final Class<?> clazz, final Class<?> clazz2) {
        if (!clazz.isPrimitive()) {
            return false;
        }
        if (clazz == Boolean.TYPE) {
            if (clazz2 == Boolean.TYPE) {
                return true;
            }
        }
        else if (clazz == Byte.TYPE) {
            if (clazz2 == Byte.TYPE || clazz2 == Short.TYPE || clazz2 == Integer.TYPE || clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Short.TYPE) {
            if (clazz2 == Short.TYPE || clazz2 == Integer.TYPE || clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Character.TYPE) {
            if (clazz2 == Character.TYPE || clazz2 == Integer.TYPE || clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Integer.TYPE) {
            if (clazz2 == Integer.TYPE || clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Long.TYPE) {
            if (clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Float.TYPE) {
            if (clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Double.TYPE && clazz2 == Double.TYPE) {
            return true;
        }
        return false;
    }
    
    protected static void emitWideningBytecodeForPrimitiveConversion(final ClassFileAssembler classFileAssembler, final Class<?> clazz, final Class<?> clazz2) {
        if (clazz == Byte.TYPE || clazz == Short.TYPE || clazz == Character.TYPE || clazz == Integer.TYPE) {
            if (clazz2 == Long.TYPE) {
                classFileAssembler.opc_i2l();
            }
            else if (clazz2 == Float.TYPE) {
                classFileAssembler.opc_i2f();
            }
            else if (clazz2 == Double.TYPE) {
                classFileAssembler.opc_i2d();
            }
        }
        else if (clazz == Long.TYPE) {
            if (clazz2 == Float.TYPE) {
                classFileAssembler.opc_l2f();
            }
            else if (clazz2 == Double.TYPE) {
                classFileAssembler.opc_l2d();
            }
        }
        else if (clazz == Float.TYPE && clazz2 == Double.TYPE) {
            classFileAssembler.opc_f2d();
        }
    }
    
    protected short unboxingMethodForPrimitiveType(final Class<?> clazz) {
        if (clazz == Boolean.TYPE) {
            return this.booleanUnboxIdx;
        }
        if (clazz == Byte.TYPE) {
            return this.byteUnboxIdx;
        }
        if (clazz == Character.TYPE) {
            return this.characterUnboxIdx;
        }
        if (clazz == Short.TYPE) {
            return this.shortUnboxIdx;
        }
        if (clazz == Integer.TYPE) {
            return this.integerUnboxIdx;
        }
        if (clazz == Long.TYPE) {
            return this.longUnboxIdx;
        }
        if (clazz == Float.TYPE) {
            return this.floatUnboxIdx;
        }
        if (clazz == Double.TYPE) {
            return this.doubleUnboxIdx;
        }
        throw new InternalError("Illegal primitive type " + clazz.getName());
    }
    
    protected static boolean isPrimitive(final Class<?> clazz) {
        return clazz.isPrimitive() && clazz != Void.TYPE;
    }
    
    protected int typeSizeInStackSlots(final Class<?> clazz) {
        if (clazz == Void.TYPE) {
            return 0;
        }
        if (clazz == Long.TYPE || clazz == Double.TYPE) {
            return 2;
        }
        return 1;
    }
    
    protected ClassFileAssembler illegalArgumentCodeBuffer() {
        if (this.illegalArgumentCodeBuffer == null) {
            (this.illegalArgumentCodeBuffer = new ClassFileAssembler()).opc_new(this.illegalArgumentClass);
            this.illegalArgumentCodeBuffer.opc_dup();
            this.illegalArgumentCodeBuffer.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
            this.illegalArgumentCodeBuffer.opc_athrow();
        }
        return this.illegalArgumentCodeBuffer;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        primitiveTypes = new Class[] { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
    }
}
