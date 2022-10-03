package java.lang.invoke;

import java.lang.reflect.Field;
import sun.invoke.util.Wrapper;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import java.security.ProtectionDomain;
import jdk.internal.org.objectweb.asm.ClassWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.concurrent.ConcurrentMap;
import java.util.Arrays;
import sun.invoke.util.ValueConversions;

abstract class BoundMethodHandle extends MethodHandle
{
    private static final int FIELD_COUNT_THRESHOLD = 12;
    private static final int FORM_EXPRESSION_THRESHOLD = 24;
    private static final MethodHandles.Lookup LOOKUP;
    static final SpeciesData SPECIES_DATA;
    private static final SpeciesData[] SPECIES_DATA_CACHE;
    
    BoundMethodHandle(final MethodType methodType, final LambdaForm lambdaForm) {
        super(methodType, lambdaForm);
        assert this.speciesData() == speciesData(lambdaForm);
    }
    
    static BoundMethodHandle bindSingle(final MethodType methodType, final LambdaForm lambdaForm, final LambdaForm.BasicType basicType, final Object o) {
        try {
            switch (basicType) {
                case L_TYPE: {
                    return bindSingle(methodType, lambdaForm, o);
                }
                case I_TYPE: {
                    return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(methodType, lambdaForm, ValueConversions.widenSubword(o));
                }
                case J_TYPE: {
                    return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(methodType, lambdaForm, (long)o);
                }
                case F_TYPE: {
                    return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(methodType, lambdaForm, (float)o);
                }
                case D_TYPE: {
                    return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(methodType, lambdaForm, (double)o);
                }
                default: {
                    throw MethodHandleStatics.newInternalError("unexpected xtype: " + basicType);
                }
            }
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.newInternalError(t);
        }
    }
    
    LambdaFormEditor editor() {
        return this.form.editor();
    }
    
    static BoundMethodHandle bindSingle(final MethodType methodType, final LambdaForm lambdaForm, final Object o) {
        return Species_L.make(methodType, lambdaForm, o);
    }
    
    @Override
    BoundMethodHandle bindArgumentL(final int n, final Object o) {
        return this.editor().bindArgumentL(this, n, o);
    }
    
    BoundMethodHandle bindArgumentI(final int n, final int n2) {
        return this.editor().bindArgumentI(this, n, n2);
    }
    
    BoundMethodHandle bindArgumentJ(final int n, final long n2) {
        return this.editor().bindArgumentJ(this, n, n2);
    }
    
    BoundMethodHandle bindArgumentF(final int n, final float n2) {
        return this.editor().bindArgumentF(this, n, n2);
    }
    
    BoundMethodHandle bindArgumentD(final int n, final double n2) {
        return this.editor().bindArgumentD(this, n, n2);
    }
    
    @Override
    BoundMethodHandle rebind() {
        if (!this.tooComplex()) {
            return this;
        }
        return makeReinvoker(this);
    }
    
    private boolean tooComplex() {
        return this.fieldCount() > 12 || this.form.expressionCount() > 24;
    }
    
    static BoundMethodHandle makeReinvoker(final MethodHandle methodHandle) {
        return Species_L.make(methodHandle.type(), DelegatingMethodHandle.makeReinvokerForm(methodHandle, 7, Species_L.SPECIES_DATA, Species_L.SPECIES_DATA.getterFunction(0)), methodHandle);
    }
    
    abstract SpeciesData speciesData();
    
    static SpeciesData speciesData(final LambdaForm lambdaForm) {
        final Object constraint = lambdaForm.names[0].constraint;
        if (constraint instanceof SpeciesData) {
            return (SpeciesData)constraint;
        }
        return SpeciesData.EMPTY;
    }
    
    abstract int fieldCount();
    
    @Override
    Object internalProperties() {
        return "\n& BMH=" + this.internalValues();
    }
    
    @Override
    final Object internalValues() {
        final Object[] array = new Object[this.speciesData().fieldCount()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.arg(i);
        }
        return Arrays.asList(array);
    }
    
    final Object arg(final int n) {
        try {
            switch (this.speciesData().fieldType(n)) {
                case L_TYPE: {
                    return this.speciesData().getters[n].invokeBasic(this);
                }
                case I_TYPE: {
                    return this.speciesData().getters[n].invokeBasic(this);
                }
                case J_TYPE: {
                    return this.speciesData().getters[n].invokeBasic(this);
                }
                case F_TYPE: {
                    return this.speciesData().getters[n].invokeBasic(this);
                }
                case D_TYPE: {
                    return this.speciesData().getters[n].invokeBasic(this);
                }
            }
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.newInternalError(t);
        }
        throw new InternalError("unexpected type: " + this.speciesData().typeChars + "." + n);
    }
    
    @Override
    abstract BoundMethodHandle copyWith(final MethodType p0, final LambdaForm p1);
    
    abstract BoundMethodHandle copyWithExtendL(final MethodType p0, final LambdaForm p1, final Object p2);
    
    abstract BoundMethodHandle copyWithExtendI(final MethodType p0, final LambdaForm p1, final int p2);
    
    abstract BoundMethodHandle copyWithExtendJ(final MethodType p0, final LambdaForm p1, final long p2);
    
    abstract BoundMethodHandle copyWithExtendF(final MethodType p0, final LambdaForm p1, final float p2);
    
    abstract BoundMethodHandle copyWithExtendD(final MethodType p0, final LambdaForm p1, final double p2);
    
    static SpeciesData getSpeciesData(final String s) {
        return get(s);
    }
    
    private static SpeciesData checkCache(final int n, final String s) {
        final int n2 = n - 1;
        final SpeciesData speciesData = BoundMethodHandle.SPECIES_DATA_CACHE[n2];
        if (speciesData != null) {
            return speciesData;
        }
        return BoundMethodHandle.SPECIES_DATA_CACHE[n2] = getSpeciesData(s);
    }
    
    static SpeciesData speciesData_L() {
        return checkCache(1, "L");
    }
    
    static SpeciesData speciesData_LL() {
        return checkCache(2, "LL");
    }
    
    static SpeciesData speciesData_LLL() {
        return checkCache(3, "LLL");
    }
    
    static SpeciesData speciesData_LLLL() {
        return checkCache(4, "LLLL");
    }
    
    static SpeciesData speciesData_LLLLL() {
        return checkCache(5, "LLLLL");
    }
    
    static {
        LOOKUP = MethodHandles.Lookup.IMPL_LOOKUP;
        SPECIES_DATA = SpeciesData.EMPTY;
        SPECIES_DATA_CACHE = new SpeciesData[5];
    }
    
    private static final class Species_L extends BoundMethodHandle
    {
        final Object argL0;
        static final SpeciesData SPECIES_DATA;
        
        private Species_L(final MethodType methodType, final LambdaForm lambdaForm, final Object argL0) {
            super(methodType, lambdaForm);
            this.argL0 = argL0;
        }
        
        @Override
        SpeciesData speciesData() {
            return Species_L.SPECIES_DATA;
        }
        
        @Override
        int fieldCount() {
            return 1;
        }
        
        static BoundMethodHandle make(final MethodType methodType, final LambdaForm lambdaForm, final Object o) {
            return new Species_L(methodType, lambdaForm, o);
        }
        
        @Override
        final BoundMethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
            return new Species_L(methodType, lambdaForm, this.argL0);
        }
        
        @Override
        final BoundMethodHandle copyWithExtendL(final MethodType methodType, final LambdaForm lambdaForm, final Object o) {
            try {
                return Species_L.SPECIES_DATA.extendWith(LambdaForm.BasicType.L_TYPE).constructor().invokeBasic(methodType, lambdaForm, this.argL0, o);
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.uncaughtException(t);
            }
        }
        
        @Override
        final BoundMethodHandle copyWithExtendI(final MethodType methodType, final LambdaForm lambdaForm, final int n) {
            try {
                return Species_L.SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(methodType, lambdaForm, this.argL0, n);
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.uncaughtException(t);
            }
        }
        
        @Override
        final BoundMethodHandle copyWithExtendJ(final MethodType methodType, final LambdaForm lambdaForm, final long n) {
            try {
                return Species_L.SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(methodType, lambdaForm, this.argL0, n);
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.uncaughtException(t);
            }
        }
        
        @Override
        final BoundMethodHandle copyWithExtendF(final MethodType methodType, final LambdaForm lambdaForm, final float n) {
            try {
                return Species_L.SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(methodType, lambdaForm, this.argL0, n);
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.uncaughtException(t);
            }
        }
        
        @Override
        final BoundMethodHandle copyWithExtendD(final MethodType methodType, final LambdaForm lambdaForm, final double n) {
            try {
                return Species_L.SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(methodType, lambdaForm, this.argL0, n);
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.uncaughtException(t);
            }
        }
        
        static {
            SPECIES_DATA = new SpeciesData("L", Species_L.class);
        }
    }
    
    static class SpeciesData
    {
        private final String typeChars;
        private final LambdaForm.BasicType[] typeCodes;
        private final Class<? extends BoundMethodHandle> clazz;
        @Stable
        private final MethodHandle[] constructor;
        @Stable
        private final MethodHandle[] getters;
        @Stable
        private final LambdaForm.NamedFunction[] nominalGetters;
        @Stable
        private final SpeciesData[] extensions;
        static final SpeciesData EMPTY;
        private static final ConcurrentMap<String, SpeciesData> CACHE;
        private static final boolean INIT_DONE;
        
        int fieldCount() {
            return this.typeCodes.length;
        }
        
        LambdaForm.BasicType fieldType(final int n) {
            return this.typeCodes[n];
        }
        
        char fieldTypeChar(final int n) {
            return this.typeChars.charAt(n);
        }
        
        Object fieldSignature() {
            return this.typeChars;
        }
        
        public Class<? extends BoundMethodHandle> fieldHolder() {
            return this.clazz;
        }
        
        @Override
        public String toString() {
            return "SpeciesData<" + this.fieldSignature() + ">";
        }
        
        LambdaForm.NamedFunction getterFunction(final int n) {
            final LambdaForm.NamedFunction namedFunction = this.nominalGetters[n];
            assert namedFunction.memberDeclaringClassOrNull() == this.fieldHolder();
            assert namedFunction.returnType() == this.fieldType(n);
            return namedFunction;
        }
        
        LambdaForm.NamedFunction[] getterFunctions() {
            return this.nominalGetters;
        }
        
        MethodHandle[] getterHandles() {
            return this.getters;
        }
        
        MethodHandle constructor() {
            return this.constructor[0];
        }
        
        SpeciesData(final String typeChars, final Class<? extends BoundMethodHandle> clazz) {
            this.typeChars = typeChars;
            this.typeCodes = LambdaForm.BasicType.basicTypes(typeChars);
            this.clazz = clazz;
            if (!SpeciesData.INIT_DONE) {
                this.constructor = new MethodHandle[1];
                this.getters = new MethodHandle[typeChars.length()];
                this.nominalGetters = new LambdaForm.NamedFunction[typeChars.length()];
            }
            else {
                this.constructor = Factory.makeCtors(clazz, typeChars, null);
                this.getters = Factory.makeGetters(clazz, typeChars, null);
                this.nominalGetters = Factory.makeNominalGetters(typeChars, null, this.getters);
            }
            this.extensions = new SpeciesData[LambdaForm.BasicType.ARG_TYPE_LIMIT];
        }
        
        private void initForBootstrap() {
            assert !SpeciesData.INIT_DONE;
            if (this.constructor() == null) {
                final String typeChars = this.typeChars;
                SpeciesData.CACHE.put(typeChars, this);
                Factory.makeCtors(this.clazz, typeChars, this.constructor);
                Factory.makeGetters(this.clazz, typeChars, this.getters);
                Factory.makeNominalGetters(typeChars, this.nominalGetters, this.getters);
            }
        }
        
        SpeciesData extendWith(final byte b) {
            return this.extendWith(LambdaForm.BasicType.basicType(b));
        }
        
        SpeciesData extendWith(final LambdaForm.BasicType basicType) {
            final int ordinal = basicType.ordinal();
            final SpeciesData speciesData = this.extensions[ordinal];
            if (speciesData != null) {
                return speciesData;
            }
            return this.extensions[ordinal] = get(this.typeChars + basicType.basicTypeChar());
        }
        
        private static SpeciesData get(final String s) {
            return SpeciesData.CACHE.computeIfAbsent(s, new Function<String, SpeciesData>() {
                @Override
                public SpeciesData apply(final String s) {
                    final Class<? extends BoundMethodHandle> concreteBMHClass = Factory.getConcreteBMHClass(s);
                    final SpeciesData speciesData = new SpeciesData(s, concreteBMHClass);
                    Factory.setSpeciesDataToConcreteBMHClass(concreteBMHClass, speciesData);
                    return speciesData;
                }
            });
        }
        
        static boolean speciesDataCachePopulated() {
            final Class<BoundMethodHandle> clazz = BoundMethodHandle.class;
            try {
                for (final Class clazz2 : clazz.getDeclaredClasses()) {
                    if (clazz.isAssignableFrom(clazz2)) {
                        final Class subclass = clazz2.asSubclass(BoundMethodHandle.class);
                        final SpeciesData speciesDataFromConcreteBMHClass = Factory.getSpeciesDataFromConcreteBMHClass(subclass);
                        assert speciesDataFromConcreteBMHClass != null : subclass.getName();
                        assert speciesDataFromConcreteBMHClass.clazz == subclass;
                        assert SpeciesData.CACHE.get(speciesDataFromConcreteBMHClass.typeChars) == speciesDataFromConcreteBMHClass;
                    }
                }
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.newInternalError(t);
            }
            return true;
        }
        
        static {
            EMPTY = new SpeciesData("", BoundMethodHandle.class);
            CACHE = new ConcurrentHashMap<String, SpeciesData>();
            SpeciesData.EMPTY.initForBootstrap();
            Species_L.SPECIES_DATA.initForBootstrap();
            assert speciesDataCachePopulated();
            INIT_DONE = Boolean.TRUE;
        }
    }
    
    static class Factory
    {
        static final String JLO_SIG = "Ljava/lang/Object;";
        static final String JLS_SIG = "Ljava/lang/String;";
        static final String JLC_SIG = "Ljava/lang/Class;";
        static final String MH = "java/lang/invoke/MethodHandle";
        static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
        static final String BMH = "java/lang/invoke/BoundMethodHandle";
        static final String BMH_SIG = "Ljava/lang/invoke/BoundMethodHandle;";
        static final String SPECIES_DATA = "java/lang/invoke/BoundMethodHandle$SpeciesData";
        static final String SPECIES_DATA_SIG = "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
        static final String STABLE_SIG = "Ljava/lang/invoke/Stable;";
        static final String SPECIES_PREFIX_NAME = "Species_";
        static final String SPECIES_PREFIX_PATH = "java/lang/invoke/BoundMethodHandle$Species_";
        static final String BMHSPECIES_DATA_EWI_SIG = "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
        static final String BMHSPECIES_DATA_GFC_SIG = "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
        static final String MYSPECIES_DATA_SIG = "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
        static final String VOID_SIG = "()V";
        static final String INT_SIG = "()I";
        static final String SIG_INCIPIT = "(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;";
        static final String[] E_THROWABLE;
        static final ConcurrentMap<String, Class<? extends BoundMethodHandle>> CLASS_CACHE;
        
        static Class<? extends BoundMethodHandle> getConcreteBMHClass(final String s) {
            return Factory.CLASS_CACHE.computeIfAbsent(s, new Function<String, Class<? extends BoundMethodHandle>>() {
                @Override
                public Class<? extends BoundMethodHandle> apply(final String s) {
                    return Factory.generateConcreteBMHClass(s);
                }
            });
        }
        
        static Class<? extends BoundMethodHandle> generateConcreteBMHClass(final String s) {
            final ClassWriter classWriter = new ClassWriter(3);
            final String shortenSignature = LambdaForm.shortenSignature(s);
            final String string = "java/lang/invoke/BoundMethodHandle$Species_" + shortenSignature;
            final String string2 = "Species_" + shortenSignature;
            classWriter.visit(50, 48, string, null, "java/lang/invoke/BoundMethodHandle", null);
            classWriter.visitSource(string2, null);
            final FieldVisitor visitField = classWriter.visitField(8, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", null, null);
            visitField.visitAnnotation("Ljava/lang/invoke/Stable;", true);
            visitField.visitEnd();
            for (int i = 0; i < s.length(); ++i) {
                final char char1 = s.charAt(i);
                classWriter.visitField(16, makeFieldName(s, i), (char1 == 'L') ? "Ljava/lang/Object;" : String.valueOf(char1), null, null).visitEnd();
            }
            final MethodVisitor visitMethod = classWriter.visitMethod(2, "<init>", makeSignature(s, true), null, null);
            visitMethod.visitCode();
            visitMethod.visitVarInsn(25, 0);
            visitMethod.visitVarInsn(25, 1);
            visitMethod.visitVarInsn(25, 2);
            visitMethod.visitMethodInsn(183, "java/lang/invoke/BoundMethodHandle", "<init>", makeSignature("", true), false);
            for (int j = 0, n = 0; j < s.length(); ++j, ++n) {
                final char char2 = s.charAt(j);
                visitMethod.visitVarInsn(25, 0);
                visitMethod.visitVarInsn(typeLoadOp(char2), n + 3);
                visitMethod.visitFieldInsn(181, string, makeFieldName(s, j), typeSig(char2));
                if (char2 == 'J' || char2 == 'D') {
                    ++n;
                }
            }
            visitMethod.visitInsn(177);
            visitMethod.visitMaxs(0, 0);
            visitMethod.visitEnd();
            final MethodVisitor visitMethod2 = classWriter.visitMethod(16, "speciesData", "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", null, null);
            visitMethod2.visitCode();
            visitMethod2.visitFieldInsn(178, string, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
            visitMethod2.visitInsn(176);
            visitMethod2.visitMaxs(0, 0);
            visitMethod2.visitEnd();
            final MethodVisitor visitMethod3 = classWriter.visitMethod(16, "fieldCount", "()I", null, null);
            visitMethod3.visitCode();
            final int length = s.length();
            if (length <= 5) {
                visitMethod3.visitInsn(3 + length);
            }
            else {
                visitMethod3.visitIntInsn(17, length);
            }
            visitMethod3.visitInsn(172);
            visitMethod3.visitMaxs(0, 0);
            visitMethod3.visitEnd();
            final MethodVisitor visitMethod4 = classWriter.visitMethod(8, "make", makeSignature(s, false), null, null);
            visitMethod4.visitCode();
            visitMethod4.visitTypeInsn(187, string);
            visitMethod4.visitInsn(89);
            visitMethod4.visitVarInsn(25, 0);
            visitMethod4.visitVarInsn(25, 1);
            for (int k = 0, n2 = 0; k < s.length(); ++k, ++n2) {
                final char char3 = s.charAt(k);
                visitMethod4.visitVarInsn(typeLoadOp(char3), n2 + 2);
                if (char3 == 'J' || char3 == 'D') {
                    ++n2;
                }
            }
            visitMethod4.visitMethodInsn(183, string, "<init>", makeSignature(s, true), false);
            visitMethod4.visitInsn(176);
            visitMethod4.visitMaxs(0, 0);
            visitMethod4.visitEnd();
            final MethodVisitor visitMethod5 = classWriter.visitMethod(16, "copyWith", makeSignature("", false), null, null);
            visitMethod5.visitCode();
            visitMethod5.visitTypeInsn(187, string);
            visitMethod5.visitInsn(89);
            visitMethod5.visitVarInsn(25, 1);
            visitMethod5.visitVarInsn(25, 2);
            emitPushFields(s, string, visitMethod5);
            visitMethod5.visitMethodInsn(183, string, "<init>", makeSignature(s, true), false);
            visitMethod5.visitInsn(176);
            visitMethod5.visitMaxs(0, 0);
            visitMethod5.visitEnd();
            for (final LambdaForm.BasicType basicType : LambdaForm.BasicType.ARG_TYPES) {
                final int ordinal = basicType.ordinal();
                final char basicTypeChar = basicType.basicTypeChar();
                final MethodVisitor visitMethod6 = classWriter.visitMethod(16, "copyWithExtend" + basicTypeChar, makeSignature(String.valueOf(basicTypeChar), false), null, Factory.E_THROWABLE);
                visitMethod6.visitCode();
                visitMethod6.visitFieldInsn(178, string, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
                final int n3 = 3 + ordinal;
                assert n3 <= 8;
                visitMethod6.visitInsn(n3);
                visitMethod6.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "extendWith", "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", false);
                visitMethod6.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "constructor", "()Ljava/lang/invoke/MethodHandle;", false);
                visitMethod6.visitVarInsn(25, 1);
                visitMethod6.visitVarInsn(25, 2);
                emitPushFields(s, string, visitMethod6);
                visitMethod6.visitVarInsn(typeLoadOp(basicTypeChar), 3);
                visitMethod6.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", makeSignature(s + basicTypeChar, false), false);
                visitMethod6.visitInsn(176);
                visitMethod6.visitMaxs(0, 0);
                visitMethod6.visitEnd();
            }
            classWriter.visitEnd();
            final byte[] byteArray = classWriter.toByteArray();
            InvokerBytecodeGenerator.maybeDump(string, byteArray);
            return MethodHandleStatics.UNSAFE.defineClass(string, byteArray, 0, byteArray.length, BoundMethodHandle.class.getClassLoader(), null).asSubclass(BoundMethodHandle.class);
        }
        
        private static int typeLoadOp(final char c) {
            switch (c) {
                case 'L': {
                    return 25;
                }
                case 'I': {
                    return 21;
                }
                case 'J': {
                    return 22;
                }
                case 'F': {
                    return 23;
                }
                case 'D': {
                    return 24;
                }
                default: {
                    throw MethodHandleStatics.newInternalError("unrecognized type " + c);
                }
            }
        }
        
        private static void emitPushFields(final String s, final String s2, final MethodVisitor methodVisitor) {
            for (int i = 0; i < s.length(); ++i) {
                final char char1 = s.charAt(i);
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitFieldInsn(180, s2, makeFieldName(s, i), typeSig(char1));
            }
        }
        
        static String typeSig(final char c) {
            return (c == 'L') ? "Ljava/lang/Object;" : String.valueOf(c);
        }
        
        private static MethodHandle makeGetter(final Class<?> clazz, final String s, final int n) {
            final String fieldName = makeFieldName(s, n);
            final Class<?> primitiveType = Wrapper.forBasicType(s.charAt(n)).primitiveType();
            try {
                return BoundMethodHandle.LOOKUP.findGetter(clazz, fieldName, primitiveType);
            }
            catch (final NoSuchFieldException | IllegalAccessException ex) {
                throw MethodHandleStatics.newInternalError((Throwable)ex);
            }
        }
        
        static MethodHandle[] makeGetters(final Class<?> clazz, final String s, MethodHandle[] array) {
            if (array == null) {
                array = new MethodHandle[s.length()];
            }
            for (int i = 0; i < array.length; ++i) {
                array[i] = makeGetter(clazz, s, i);
                assert array[i].internalMemberName().getDeclaringClass() == clazz;
            }
            return array;
        }
        
        static MethodHandle[] makeCtors(final Class<? extends BoundMethodHandle> clazz, final String s, MethodHandle[] array) {
            if (array == null) {
                array = new MethodHandle[] { null };
            }
            if (s.equals("")) {
                return array;
            }
            array[0] = makeCbmhCtor(clazz, s);
            return array;
        }
        
        static LambdaForm.NamedFunction[] makeNominalGetters(final String s, LambdaForm.NamedFunction[] array, final MethodHandle[] array2) {
            if (array == null) {
                array = new LambdaForm.NamedFunction[s.length()];
            }
            for (int i = 0; i < array.length; ++i) {
                array[i] = new LambdaForm.NamedFunction(array2[i]);
            }
            return array;
        }
        
        static SpeciesData getSpeciesDataFromConcreteBMHClass(final Class<? extends BoundMethodHandle> clazz) {
            try {
                return (SpeciesData)clazz.getDeclaredField("SPECIES_DATA").get(null);
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.newInternalError(ex);
            }
        }
        
        static void setSpeciesDataToConcreteBMHClass(final Class<? extends BoundMethodHandle> clazz, final SpeciesData speciesData) {
            try {
                final Field declaredField = clazz.getDeclaredField("SPECIES_DATA");
                assert declaredField.getDeclaredAnnotation(Stable.class) != null;
                declaredField.set(null, speciesData);
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.newInternalError(ex);
            }
        }
        
        private static String makeFieldName(final String s, final int n) {
            assert n >= 0 && n < s.length();
            return "arg" + s.charAt(n) + n;
        }
        
        private static String makeSignature(final String s, final boolean b) {
            final StringBuilder sb = new StringBuilder("(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;");
            final char[] charArray = s.toCharArray();
            for (int length = charArray.length, i = 0; i < length; ++i) {
                sb.append(typeSig(charArray[i]));
            }
            return sb.append(')').append(b ? "V" : "Ljava/lang/invoke/BoundMethodHandle;").toString();
        }
        
        static MethodHandle makeCbmhCtor(final Class<? extends BoundMethodHandle> clazz, final String s) {
            try {
                return BoundMethodHandle.LOOKUP.findStatic(clazz, "make", MethodType.fromMethodDescriptorString(makeSignature(s, false), null));
            }
            catch (final NoSuchMethodException | IllegalAccessException | IllegalArgumentException | TypeNotPresentException ex) {
                throw MethodHandleStatics.newInternalError((Throwable)ex);
            }
        }
        
        static {
            E_THROWABLE = new String[] { "java/lang/Throwable" };
            CLASS_CACHE = new ConcurrentHashMap<String, Class<? extends BoundMethodHandle>>();
        }
    }
}
