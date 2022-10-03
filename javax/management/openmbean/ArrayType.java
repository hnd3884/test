package javax.management.openmbean;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;

public class ArrayType<T> extends OpenType<T>
{
    static final long serialVersionUID = 720504429830309770L;
    private int dimension;
    private OpenType<?> elementType;
    private boolean primitiveArray;
    private transient Integer myHashCode;
    private transient String myToString;
    private static final int PRIMITIVE_WRAPPER_NAME_INDEX = 0;
    private static final int PRIMITIVE_TYPE_NAME_INDEX = 1;
    private static final int PRIMITIVE_TYPE_KEY_INDEX = 2;
    private static final int PRIMITIVE_OPEN_TYPE_INDEX = 3;
    private static final Object[][] PRIMITIVE_ARRAY_TYPES;
    
    static boolean isPrimitiveContentType(final String s) {
        final Object[][] primitive_ARRAY_TYPES = ArrayType.PRIMITIVE_ARRAY_TYPES;
        for (int length = primitive_ARRAY_TYPES.length, i = 0; i < length; ++i) {
            if (primitive_ARRAY_TYPES[i][2].equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    static String getPrimitiveTypeKey(final String s) {
        for (final Object[] array : ArrayType.PRIMITIVE_ARRAY_TYPES) {
            if (s.equals(array[0])) {
                return (String)array[2];
            }
        }
        return null;
    }
    
    static String getPrimitiveTypeName(final String s) {
        for (final Object[] array : ArrayType.PRIMITIVE_ARRAY_TYPES) {
            if (s.equals(array[0])) {
                return (String)array[1];
            }
        }
        return null;
    }
    
    static SimpleType<?> getPrimitiveOpenType(final String s) {
        for (final Object[] array : ArrayType.PRIMITIVE_ARRAY_TYPES) {
            if (s.equals(array[1])) {
                return (SimpleType)array[3];
            }
        }
        return null;
    }
    
    public ArrayType(final int dimension, final OpenType<?> elementType) throws OpenDataException {
        super(buildArrayClassName(dimension, elementType), buildArrayClassName(dimension, elementType), buildArrayDescription(dimension, elementType));
        this.myHashCode = null;
        this.myToString = null;
        if (elementType.isArray()) {
            final ArrayType arrayType = (ArrayType)elementType;
            this.dimension = arrayType.getDimension() + dimension;
            this.elementType = arrayType.getElementOpenType();
            this.primitiveArray = arrayType.isPrimitiveArray();
        }
        else {
            this.dimension = dimension;
            this.elementType = elementType;
            this.primitiveArray = false;
        }
    }
    
    public ArrayType(final SimpleType<?> elementType, final boolean primitiveArray) throws OpenDataException {
        super(buildArrayClassName(1, elementType, primitiveArray), buildArrayClassName(1, elementType, primitiveArray), buildArrayDescription(1, elementType, primitiveArray), true);
        this.myHashCode = null;
        this.myToString = null;
        this.dimension = 1;
        this.elementType = elementType;
        this.primitiveArray = primitiveArray;
    }
    
    ArrayType(final String s, final String s2, final String s3, final int dimension, final OpenType<?> elementType, final boolean primitiveArray) {
        super(s, s2, s3, true);
        this.myHashCode = null;
        this.myToString = null;
        this.dimension = dimension;
        this.elementType = elementType;
        this.primitiveArray = primitiveArray;
    }
    
    private static String buildArrayClassName(final int n, final OpenType<?> openType) throws OpenDataException {
        boolean primitiveArray = false;
        if (openType.isArray()) {
            primitiveArray = ((ArrayType)openType).isPrimitiveArray();
        }
        return buildArrayClassName(n, openType, primitiveArray);
    }
    
    private static String buildArrayClassName(final int n, final OpenType<?> openType, final boolean b) throws OpenDataException {
        if (n < 1) {
            throw new IllegalArgumentException("Value of argument dimension must be greater than 0");
        }
        final StringBuilder sb = new StringBuilder();
        final String className = openType.getClassName();
        for (int i = 1; i <= n; ++i) {
            sb.append('[');
        }
        if (openType.isArray()) {
            sb.append(className);
        }
        else if (b) {
            final String primitiveTypeKey = getPrimitiveTypeKey(className);
            if (primitiveTypeKey == null) {
                throw new OpenDataException("Element type is not primitive: " + className);
            }
            sb.append(primitiveTypeKey);
        }
        else {
            sb.append("L");
            sb.append(className);
            sb.append(';');
        }
        return sb.toString();
    }
    
    private static String buildArrayDescription(final int n, final OpenType<?> openType) throws OpenDataException {
        boolean primitiveArray = false;
        if (openType.isArray()) {
            primitiveArray = ((ArrayType)openType).isPrimitiveArray();
        }
        return buildArrayDescription(n, openType, primitiveArray);
    }
    
    private static String buildArrayDescription(int n, OpenType<?> elementOpenType, boolean primitiveArray) throws OpenDataException {
        if (elementOpenType.isArray()) {
            final ArrayType arrayType = (ArrayType)elementOpenType;
            n += arrayType.getDimension();
            elementOpenType = arrayType.getElementOpenType();
            primitiveArray = arrayType.isPrimitiveArray();
        }
        final StringBuilder sb = new StringBuilder(n + "-dimension array of ");
        final String className = elementOpenType.getClassName();
        if (primitiveArray) {
            final String primitiveTypeName = getPrimitiveTypeName(className);
            if (primitiveTypeName == null) {
                throw new OpenDataException("Element is not a primitive type: " + className);
            }
            sb.append(primitiveTypeName);
        }
        else {
            sb.append(className);
        }
        return sb.toString();
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public OpenType<?> getElementOpenType() {
        return this.elementType;
    }
    
    public boolean isPrimitiveArray() {
        return this.primitiveArray;
    }
    
    @Override
    public boolean isValue(final Object o) {
        if (o == null) {
            return false;
        }
        final Class<?> class1 = o.getClass();
        final String name = class1.getName();
        return class1.isArray() && (this.getClassName().equals(name) || ((this.elementType.getClassName().equals(TabularData.class.getName()) || this.elementType.getClassName().equals(CompositeData.class.getName())) && Array.newInstance((Class<?>)(this.elementType.getClassName().equals(TabularData.class.getName()) ? TabularData.class : CompositeData.class), new int[this.getDimension()]).getClass().isAssignableFrom(class1) && this.checkElementsType((Object[])o, this.dimension)));
    }
    
    private boolean checkElementsType(final Object[] array, final int n) {
        if (n > 1) {
            for (int i = 0; i < array.length; ++i) {
                if (!this.checkElementsType((Object[])array[i], n - 1)) {
                    return false;
                }
            }
            return true;
        }
        for (int j = 0; j < array.length; ++j) {
            if (array[j] != null && !this.getElementOpenType().isValue(array[j])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    boolean isAssignableFrom(final OpenType<?> openType) {
        if (!(openType instanceof ArrayType)) {
            return false;
        }
        final ArrayType arrayType = (ArrayType)openType;
        return arrayType.getDimension() == this.getDimension() && arrayType.isPrimitiveArray() == this.isPrimitiveArray() && arrayType.getElementOpenType().isAssignableFrom(this.getElementOpenType());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ArrayType)) {
            return false;
        }
        final ArrayType arrayType = (ArrayType)o;
        return this.dimension == arrayType.dimension && this.elementType.equals(arrayType.elementType) && this.primitiveArray == arrayType.primitiveArray;
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            this.myHashCode = 0 + this.dimension + this.elementType.hashCode() + Boolean.valueOf(this.primitiveArray).hashCode();
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = this.getClass().getName() + "(name=" + this.getTypeName() + ",dimension=" + this.dimension + ",elementType=" + this.elementType + ",primitiveArray=" + this.primitiveArray + ")";
        }
        return this.myToString;
    }
    
    public static <E> ArrayType<E[]> getArrayType(final OpenType<E> openType) throws OpenDataException {
        return new ArrayType<E[]>(1, openType);
    }
    
    public static <T> ArrayType<T> getPrimitiveArrayType(final Class<T> clazz) {
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("arrayClass must be an array");
        }
        int n = 1;
        Class<?> clazz2;
        for (clazz2 = clazz.getComponentType(); clazz2.isArray(); clazz2 = clazz2.getComponentType()) {
            ++n;
        }
        final String name = clazz2.getName();
        if (!clazz2.isPrimitive()) {
            throw new IllegalArgumentException("component type of the array must be a primitive type");
        }
        final SimpleType<?> primitiveOpenType = getPrimitiveOpenType(name);
        try {
            ArrayType arrayType = new ArrayType<Object>(primitiveOpenType, true);
            if (n > 1) {
                arrayType = new ArrayType<Object>(n - 1, arrayType);
            }
            return (ArrayType<T>)arrayType;
        }
        catch (final OpenDataException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.primitiveArray) {
            return this.convertFromWrapperToPrimitiveTypes();
        }
        return this;
    }
    
    private <T> ArrayType<T> convertFromWrapperToPrimitiveTypes() {
        String s = this.getClassName();
        String s2 = this.getTypeName();
        String s3 = this.getDescription();
        for (final Object[] array : ArrayType.PRIMITIVE_ARRAY_TYPES) {
            if (s.indexOf((String)array[0]) != -1) {
                s = s.replaceFirst("L" + array[0] + ";", (String)array[2]);
                s2 = s2.replaceFirst("L" + array[0] + ";", (String)array[2]);
                s3 = s3.replaceFirst((String)array[0], (String)array[1]);
                break;
            }
        }
        return new ArrayType<T>(s, s2, s3, this.dimension, this.elementType, this.primitiveArray);
    }
    
    private Object writeReplace() throws ObjectStreamException {
        if (this.primitiveArray) {
            return this.convertFromPrimitiveToWrapperTypes();
        }
        return this;
    }
    
    private <T> ArrayType<T> convertFromPrimitiveToWrapperTypes() {
        String s = this.getClassName();
        String s2 = this.getTypeName();
        String s3 = this.getDescription();
        for (final Object[] array : ArrayType.PRIMITIVE_ARRAY_TYPES) {
            if (s.indexOf((String)array[2]) != -1) {
                s = s.replaceFirst((String)array[2], "L" + array[0] + ";");
                s2 = s2.replaceFirst((String)array[2], "L" + array[0] + ";");
                s3 = s3.replaceFirst((String)array[1], (String)array[0]);
                break;
            }
        }
        return new ArrayType<T>(s, s2, s3, this.dimension, this.elementType, this.primitiveArray);
    }
    
    static {
        PRIMITIVE_ARRAY_TYPES = new Object[][] { { Boolean.class.getName(), Boolean.TYPE.getName(), "Z", SimpleType.BOOLEAN }, { Character.class.getName(), Character.TYPE.getName(), "C", SimpleType.CHARACTER }, { Byte.class.getName(), Byte.TYPE.getName(), "B", SimpleType.BYTE }, { Short.class.getName(), Short.TYPE.getName(), "S", SimpleType.SHORT }, { Integer.class.getName(), Integer.TYPE.getName(), "I", SimpleType.INTEGER }, { Long.class.getName(), Long.TYPE.getName(), "J", SimpleType.LONG }, { Float.class.getName(), Float.TYPE.getName(), "F", SimpleType.FLOAT }, { Double.class.getName(), Double.TYPE.getName(), "D", SimpleType.DOUBLE } };
    }
}
