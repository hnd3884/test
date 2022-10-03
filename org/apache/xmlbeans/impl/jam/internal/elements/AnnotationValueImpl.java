package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.JAnnotationValue;

public class AnnotationValueImpl implements JAnnotationValue
{
    private Object mValue;
    private JClassRef mType;
    private String mName;
    private ElementContext mContext;
    
    public AnnotationValueImpl(final ElementContext ctx, final String name, final Object value, final JClass type) {
        this.mValue = null;
        this.mType = null;
        if (ctx == null) {
            throw new IllegalArgumentException("null ctx");
        }
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (type == null) {
            throw new IllegalArgumentException("null type");
        }
        if (value.getClass().isArray()) {
            this.mValue = ensureArrayWrapped(value);
        }
        else {
            this.mValue = value;
        }
        this.mContext = ctx;
        this.mName = name;
        this.mType = QualifiedJClassRef.create(type);
    }
    
    @Override
    public boolean isDefaultValueUsed() {
        throw new IllegalStateException("NYI");
    }
    
    @Override
    public String getName() {
        return this.mName;
    }
    
    @Override
    public JClass getType() {
        return this.mType.getRefClass();
    }
    
    @Override
    public JAnnotation asAnnotation() {
        if (this.mValue instanceof JAnnotation) {
            return (JAnnotation)this.mValue;
        }
        return null;
    }
    
    @Override
    public JClass asClass() {
        if (this.mValue instanceof JClass) {
            return (JClass)this.mValue;
        }
        return null;
    }
    
    @Override
    public String asString() {
        if (this.mValue == null) {
            return null;
        }
        return this.mValue.toString();
    }
    
    @Override
    public int asInt() throws NumberFormatException {
        if (this.mValue == null) {
            return 0;
        }
        if (this.mValue instanceof Number) {
            return ((Number)this.mValue).intValue();
        }
        try {
            return Integer.parseInt(this.mValue.toString().trim());
        }
        catch (final NumberFormatException nfe) {
            return 0;
        }
    }
    
    @Override
    public boolean asBoolean() throws IllegalArgumentException {
        return this.mValue != null && Boolean.valueOf(this.mValue.toString().trim());
    }
    
    @Override
    public long asLong() throws NumberFormatException {
        if (this.mValue == null) {
            return 0L;
        }
        if (this.mValue instanceof Number) {
            return ((Number)this.mValue).longValue();
        }
        try {
            return Long.parseLong(this.mValue.toString().trim());
        }
        catch (final NumberFormatException nfe) {
            return 0L;
        }
    }
    
    @Override
    public short asShort() throws NumberFormatException {
        if (this.mValue == null) {
            return 0;
        }
        if (this.mValue instanceof Number) {
            return ((Number)this.mValue).shortValue();
        }
        try {
            return Short.parseShort(this.mValue.toString().trim());
        }
        catch (final NumberFormatException nfe) {
            return 0;
        }
    }
    
    @Override
    public double asDouble() throws NumberFormatException {
        if (this.mValue == null) {
            return 0.0;
        }
        if (this.mValue instanceof Number) {
            return ((Number)this.mValue).doubleValue();
        }
        try {
            return Double.parseDouble(this.mValue.toString().trim());
        }
        catch (final NumberFormatException nfe) {
            return 0.0;
        }
    }
    
    @Override
    public float asFloat() throws NumberFormatException {
        if (this.mValue == null) {
            return 0.0f;
        }
        if (this.mValue instanceof Number) {
            return ((Number)this.mValue).floatValue();
        }
        try {
            return Float.parseFloat(this.mValue.toString().trim());
        }
        catch (final NumberFormatException nfe) {
            return 0.0f;
        }
    }
    
    @Override
    public byte asByte() throws NumberFormatException {
        if (this.mValue == null) {
            return 0;
        }
        if (this.mValue instanceof Number) {
            return ((Number)this.mValue).byteValue();
        }
        try {
            return Byte.parseByte(this.mValue.toString().trim());
        }
        catch (final NumberFormatException nfe) {
            return 0;
        }
    }
    
    @Override
    public char asChar() throws IllegalArgumentException {
        if (this.mValue == null) {
            return '\0';
        }
        if (this.mValue instanceof Character) {
            return (char)this.mValue;
        }
        this.mValue = this.mValue.toString();
        return (((String)this.mValue).length() == 0) ? '\0' : ((String)this.mValue).charAt(0);
    }
    
    @Override
    public JClass[] asClassArray() {
        if (this.mValue instanceof JClass[]) {
            return (JClass[])this.mValue;
        }
        return null;
    }
    
    @Override
    public JAnnotation[] asAnnotationArray() {
        if (this.mValue instanceof JAnnotation[]) {
            return (JAnnotation[])this.mValue;
        }
        return null;
    }
    
    @Override
    public String[] asStringArray() {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final String[] out = new String[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element on " + this.getName());
                out[i] = "";
            }
            else {
                out[i] = ((Object[])this.mValue)[i].toString();
            }
        }
        return out;
    }
    
    @Override
    public int[] asIntArray() throws NumberFormatException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final int[] out = new int[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = 0;
            }
            else {
                out[i] = Integer.parseInt(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public boolean[] asBooleanArray() throws IllegalArgumentException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final boolean[] out = new boolean[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = false;
            }
            else {
                out[i] = Boolean.valueOf(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public short[] asShortArray() throws NumberFormatException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final short[] out = new short[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = 0;
            }
            else {
                out[i] = Short.parseShort(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public long[] asLongArray() throws NumberFormatException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final long[] out = new long[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = 0L;
            }
            else {
                out[i] = Long.parseLong(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public double[] asDoubleArray() throws NumberFormatException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final double[] out = new double[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = 0.0;
            }
            else {
                out[i] = Double.parseDouble(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public float[] asFloatArray() throws NumberFormatException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final float[] out = new float[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = 0.0f;
            }
            else {
                out[i] = Float.parseFloat(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public byte[] asByteArray() throws NumberFormatException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final byte[] out = new byte[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = 0;
            }
            else {
                out[i] = Byte.parseByte(((Object[])this.mValue)[i].toString());
            }
        }
        return out;
    }
    
    @Override
    public char[] asCharArray() throws IllegalArgumentException {
        if (!this.mValue.getClass().isArray()) {
            return null;
        }
        final char[] out = new char[((Object[])this.mValue).length];
        for (int i = 0; i < out.length; ++i) {
            if (((Object[])this.mValue)[i] == null) {
                this.mContext.getLogger().error("Null annotation value array element " + i + " on " + this.getName());
                out[i] = '\0';
            }
            else {
                out[i] = ((Object[])this.mValue)[i].toString().charAt(0);
            }
        }
        return out;
    }
    
    private static final Object[] ensureArrayWrapped(final Object o) {
        if (o instanceof Object[]) {
            return (Object[])o;
        }
        if (o instanceof int[]) {
            final int dims = ((int[])o).length;
            final Integer[] out = new Integer[dims];
            for (int i = 0; i < dims; ++i) {
                out[i] = new Integer(((int[])o)[i]);
            }
            return out;
        }
        if (o instanceof boolean[]) {
            final int dims = ((boolean[])o).length;
            final Boolean[] out2 = new Boolean[dims];
            for (int i = 0; i < dims; ++i) {
                out2[i] = ((boolean[])o)[i];
            }
            return out2;
        }
        if (o instanceof byte[]) {
            final int dims = ((byte[])o).length;
            final Byte[] out3 = new Byte[dims];
            for (int i = 0; i < dims; ++i) {
                out3[i] = new Byte(((byte[])o)[i]);
            }
            return out3;
        }
        if (o instanceof char[]) {
            final int dims = ((char[])o).length;
            final Character[] out4 = new Character[dims];
            for (int i = 0; i < dims; ++i) {
                out4[i] = new Character(((char[])o)[i]);
            }
            return out4;
        }
        if (o instanceof float[]) {
            final int dims = ((float[])o).length;
            final Float[] out5 = new Float[dims];
            for (int i = 0; i < dims; ++i) {
                out5[i] = new Float(((float[])o)[i]);
            }
            return out5;
        }
        if (o instanceof double[]) {
            final int dims = ((double[])o).length;
            final Double[] out6 = new Double[dims];
            for (int i = 0; i < dims; ++i) {
                out6[i] = new Double(((double[])o)[i]);
            }
            return out6;
        }
        if (o instanceof long[]) {
            final int dims = ((long[])o).length;
            final Long[] out7 = new Long[dims];
            for (int i = 0; i < dims; ++i) {
                out7[i] = new Long(((long[])o)[i]);
            }
            return out7;
        }
        if (o instanceof short[]) {
            final int dims = ((short[])o).length;
            final Short[] out8 = new Short[dims];
            for (int i = 0; i < dims; ++i) {
                out8[i] = new Short(((short[])o)[i]);
            }
            return out8;
        }
        throw new IllegalStateException("Unknown array type " + o.getClass());
    }
    
    @Override
    public Object getValue() {
        return this.mValue;
    }
}
