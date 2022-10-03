package org.apache.commons.beanutils;

import java.util.Collection;
import org.apache.commons.beanutils.converters.ConverterFacade;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.URLConverter;
import java.net.URL;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import java.sql.Timestamp;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import java.sql.Time;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.FileConverter;
import java.io.File;
import org.apache.commons.beanutils.converters.CalendarConverter;
import java.util.Calendar;
import org.apache.commons.beanutils.converters.DateConverter;
import java.util.Date;
import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import java.math.BigInteger;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import java.math.BigDecimal;
import java.lang.reflect.Array;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class ConvertUtilsBean
{
    private static final Integer ZERO;
    private static final Character SPACE;
    private final WeakFastHashMap<Class<?>, Converter> converters;
    private final Log log;
    @Deprecated
    private Boolean defaultBoolean;
    @Deprecated
    private Byte defaultByte;
    @Deprecated
    private Character defaultCharacter;
    @Deprecated
    private Double defaultDouble;
    @Deprecated
    private Float defaultFloat;
    @Deprecated
    private Integer defaultInteger;
    @Deprecated
    private Long defaultLong;
    @Deprecated
    private static Short defaultShort;
    
    protected static ConvertUtilsBean getInstance() {
        return BeanUtilsBean.getInstance().getConvertUtils();
    }
    
    public ConvertUtilsBean() {
        this.converters = new WeakFastHashMap<Class<?>, Converter>();
        this.log = LogFactory.getLog((Class)ConvertUtils.class);
        this.defaultBoolean = Boolean.FALSE;
        this.defaultByte = new Byte((byte)0);
        this.defaultCharacter = new Character(' ');
        this.defaultDouble = new Double(0.0);
        this.defaultFloat = new Float(0.0f);
        this.defaultInteger = new Integer(0);
        this.defaultLong = new Long(0L);
        this.converters.setFast(false);
        this.deregister();
        this.converters.setFast(true);
    }
    
    @Deprecated
    public boolean getDefaultBoolean() {
        return this.defaultBoolean;
    }
    
    @Deprecated
    public void setDefaultBoolean(final boolean newDefaultBoolean) {
        this.defaultBoolean = (newDefaultBoolean ? Boolean.TRUE : Boolean.FALSE);
        this.register(new BooleanConverter(this.defaultBoolean), Boolean.TYPE);
        this.register(new BooleanConverter(this.defaultBoolean), Boolean.class);
    }
    
    @Deprecated
    public byte getDefaultByte() {
        return this.defaultByte;
    }
    
    @Deprecated
    public void setDefaultByte(final byte newDefaultByte) {
        this.defaultByte = new Byte(newDefaultByte);
        this.register(new ByteConverter(this.defaultByte), Byte.TYPE);
        this.register(new ByteConverter(this.defaultByte), Byte.class);
    }
    
    @Deprecated
    public char getDefaultCharacter() {
        return this.defaultCharacter;
    }
    
    @Deprecated
    public void setDefaultCharacter(final char newDefaultCharacter) {
        this.defaultCharacter = new Character(newDefaultCharacter);
        this.register(new CharacterConverter(this.defaultCharacter), Character.TYPE);
        this.register(new CharacterConverter(this.defaultCharacter), Character.class);
    }
    
    @Deprecated
    public double getDefaultDouble() {
        return this.defaultDouble;
    }
    
    @Deprecated
    public void setDefaultDouble(final double newDefaultDouble) {
        this.defaultDouble = new Double(newDefaultDouble);
        this.register(new DoubleConverter(this.defaultDouble), Double.TYPE);
        this.register(new DoubleConverter(this.defaultDouble), Double.class);
    }
    
    @Deprecated
    public float getDefaultFloat() {
        return this.defaultFloat;
    }
    
    @Deprecated
    public void setDefaultFloat(final float newDefaultFloat) {
        this.defaultFloat = new Float(newDefaultFloat);
        this.register(new FloatConverter(this.defaultFloat), Float.TYPE);
        this.register(new FloatConverter(this.defaultFloat), Float.class);
    }
    
    @Deprecated
    public int getDefaultInteger() {
        return this.defaultInteger;
    }
    
    @Deprecated
    public void setDefaultInteger(final int newDefaultInteger) {
        this.defaultInteger = new Integer(newDefaultInteger);
        this.register(new IntegerConverter(this.defaultInteger), Integer.TYPE);
        this.register(new IntegerConverter(this.defaultInteger), Integer.class);
    }
    
    @Deprecated
    public long getDefaultLong() {
        return this.defaultLong;
    }
    
    @Deprecated
    public void setDefaultLong(final long newDefaultLong) {
        this.defaultLong = new Long(newDefaultLong);
        this.register(new LongConverter(this.defaultLong), Long.TYPE);
        this.register(new LongConverter(this.defaultLong), Long.class);
    }
    
    @Deprecated
    public short getDefaultShort() {
        return ConvertUtilsBean.defaultShort;
    }
    
    @Deprecated
    public void setDefaultShort(final short newDefaultShort) {
        ConvertUtilsBean.defaultShort = new Short(newDefaultShort);
        this.register(new ShortConverter(ConvertUtilsBean.defaultShort), Short.TYPE);
        this.register(new ShortConverter(ConvertUtilsBean.defaultShort), Short.class);
    }
    
    public String convert(Object value) {
        if (value == null) {
            return null;
        }
        if (!value.getClass().isArray()) {
            final Converter converter = this.lookup(String.class);
            return converter.convert(String.class, value);
        }
        if (Array.getLength(value) < 1) {
            return null;
        }
        value = Array.get(value, 0);
        if (value == null) {
            return null;
        }
        final Converter converter = this.lookup(String.class);
        return converter.convert(String.class, value);
    }
    
    public Object convert(final String value, final Class<?> clazz) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Convert string '" + value + "' to class '" + clazz.getName() + "'"));
        }
        Converter converter = this.lookup(clazz);
        if (converter == null) {
            converter = this.lookup(String.class);
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("  Using converter " + converter));
        }
        return converter.convert(clazz, value);
    }
    
    public Object convert(final String[] values, final Class<?> clazz) {
        Class<?> type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Convert String[" + values.length + "] to class '" + type.getName() + "[]'"));
        }
        Converter converter = this.lookup(type);
        if (converter == null) {
            converter = this.lookup(String.class);
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("  Using converter " + converter));
        }
        final Object array = Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; ++i) {
            Array.set(array, i, converter.convert(type, values[i]));
        }
        return array;
    }
    
    public Object convert(final Object value, final Class<?> targetType) {
        final Class<?> sourceType = (value == null) ? null : value.getClass();
        if (this.log.isDebugEnabled()) {
            if (value == null) {
                this.log.debug((Object)("Convert null value to type '" + targetType.getName() + "'"));
            }
            else {
                this.log.debug((Object)("Convert type '" + sourceType.getName() + "' value '" + value + "' to type '" + targetType.getName() + "'"));
            }
        }
        Object converted = value;
        Converter converter = this.lookup(sourceType, targetType);
        if (converter != null) {
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)("  Using converter " + converter));
            }
            converted = converter.convert(targetType, value);
        }
        if (String.class.equals(targetType) && converted != null && !(converted instanceof String)) {
            converter = this.lookup(String.class);
            if (converter != null) {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("  Using converter " + converter));
                }
                converted = converter.convert(String.class, converted);
            }
            if (converted != null && !(converted instanceof String)) {
                converted = converted.toString();
            }
        }
        return converted;
    }
    
    public void deregister() {
        this.converters.clear();
        this.registerPrimitives(false);
        this.registerStandard(false, false);
        this.registerOther(true);
        this.registerArrays(false, 0);
        this.register(BigDecimal.class, new BigDecimalConverter());
        this.register(BigInteger.class, new BigIntegerConverter());
    }
    
    public void register(final boolean throwException, final boolean defaultNull, final int defaultArraySize) {
        this.registerPrimitives(throwException);
        this.registerStandard(throwException, defaultNull);
        this.registerOther(throwException);
        this.registerArrays(throwException, defaultArraySize);
    }
    
    private void registerPrimitives(final boolean throwException) {
        this.register(Boolean.TYPE, throwException ? new BooleanConverter() : new BooleanConverter(Boolean.FALSE));
        this.register(Byte.TYPE, throwException ? new ByteConverter() : new ByteConverter(ConvertUtilsBean.ZERO));
        this.register(Character.TYPE, throwException ? new CharacterConverter() : new CharacterConverter(ConvertUtilsBean.SPACE));
        this.register(Double.TYPE, throwException ? new DoubleConverter() : new DoubleConverter(ConvertUtilsBean.ZERO));
        this.register(Float.TYPE, throwException ? new FloatConverter() : new FloatConverter(ConvertUtilsBean.ZERO));
        this.register(Integer.TYPE, throwException ? new IntegerConverter() : new IntegerConverter(ConvertUtilsBean.ZERO));
        this.register(Long.TYPE, throwException ? new LongConverter() : new LongConverter(ConvertUtilsBean.ZERO));
        this.register(Short.TYPE, throwException ? new ShortConverter() : new ShortConverter(ConvertUtilsBean.ZERO));
    }
    
    private void registerStandard(final boolean throwException, final boolean defaultNull) {
        final Number defaultNumber = defaultNull ? null : ConvertUtilsBean.ZERO;
        final BigDecimal bigDecDeflt = defaultNull ? null : new BigDecimal("0.0");
        final BigInteger bigIntDeflt = defaultNull ? null : new BigInteger("0");
        final Boolean booleanDefault = defaultNull ? null : Boolean.FALSE;
        final Character charDefault = defaultNull ? null : ConvertUtilsBean.SPACE;
        final String stringDefault = defaultNull ? null : "";
        this.register(BigDecimal.class, throwException ? new BigDecimalConverter() : new BigDecimalConverter(bigDecDeflt));
        this.register(BigInteger.class, throwException ? new BigIntegerConverter() : new BigIntegerConverter(bigIntDeflt));
        this.register(Boolean.class, throwException ? new BooleanConverter() : new BooleanConverter(booleanDefault));
        this.register(Byte.class, throwException ? new ByteConverter() : new ByteConverter(defaultNumber));
        this.register(Character.class, throwException ? new CharacterConverter() : new CharacterConverter(charDefault));
        this.register(Double.class, throwException ? new DoubleConverter() : new DoubleConverter(defaultNumber));
        this.register(Float.class, throwException ? new FloatConverter() : new FloatConverter(defaultNumber));
        this.register(Integer.class, throwException ? new IntegerConverter() : new IntegerConverter(defaultNumber));
        this.register(Long.class, throwException ? new LongConverter() : new LongConverter(defaultNumber));
        this.register(Short.class, throwException ? new ShortConverter() : new ShortConverter(defaultNumber));
        this.register(String.class, throwException ? new StringConverter() : new StringConverter(stringDefault));
    }
    
    private void registerOther(final boolean throwException) {
        this.register(Class.class, throwException ? new ClassConverter() : new ClassConverter(null));
        this.register(Date.class, throwException ? new DateConverter() : new DateConverter(null));
        this.register(Calendar.class, throwException ? new CalendarConverter() : new CalendarConverter(null));
        this.register(File.class, throwException ? new FileConverter() : new FileConverter(null));
        this.register(java.sql.Date.class, throwException ? new SqlDateConverter() : new SqlDateConverter(null));
        this.register(Time.class, throwException ? new SqlTimeConverter() : new SqlTimeConverter(null));
        this.register(Timestamp.class, throwException ? new SqlTimestampConverter() : new SqlTimestampConverter(null));
        this.register(URL.class, throwException ? new URLConverter() : new URLConverter(null));
    }
    
    private void registerArrays(final boolean throwException, final int defaultArraySize) {
        this.registerArrayConverter(Boolean.TYPE, new BooleanConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Byte.TYPE, new ByteConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Character.TYPE, new CharacterConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Double.TYPE, new DoubleConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Float.TYPE, new FloatConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Integer.TYPE, new IntegerConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Long.TYPE, new LongConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Short.TYPE, new ShortConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(BigDecimal.class, new BigDecimalConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(BigInteger.class, new BigIntegerConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Boolean.class, new BooleanConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Byte.class, new ByteConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Character.class, new CharacterConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Double.class, new DoubleConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Float.class, new FloatConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Integer.class, new IntegerConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Long.class, new LongConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Short.class, new ShortConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(String.class, new StringConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Class.class, new ClassConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Date.class, new DateConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Calendar.class, new DateConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(File.class, new FileConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(java.sql.Date.class, new SqlDateConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Time.class, new SqlTimeConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Timestamp.class, new SqlTimestampConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(URL.class, new URLConverter(), throwException, defaultArraySize);
    }
    
    private void registerArrayConverter(final Class<?> componentType, final Converter componentConverter, final boolean throwException, final int defaultArraySize) {
        final Class<?> arrayType = Array.newInstance(componentType, 0).getClass();
        Converter arrayConverter = null;
        if (throwException) {
            arrayConverter = new ArrayConverter(arrayType, componentConverter);
        }
        else {
            arrayConverter = new ArrayConverter(arrayType, componentConverter, defaultArraySize);
        }
        this.register(arrayType, arrayConverter);
    }
    
    private void register(final Class<?> clazz, final Converter converter) {
        this.register(new ConverterFacade(converter), clazz);
    }
    
    public void deregister(final Class<?> clazz) {
        this.converters.remove(clazz);
    }
    
    public Converter lookup(final Class<?> clazz) {
        return this.converters.get(clazz);
    }
    
    public Converter lookup(final Class<?> sourceType, final Class<?> targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("Target type is missing");
        }
        if (sourceType == null) {
            return this.lookup(targetType);
        }
        Converter converter = null;
        if (targetType == String.class) {
            converter = this.lookup(sourceType);
            if (converter == null && (sourceType.isArray() || Collection.class.isAssignableFrom(sourceType))) {
                converter = this.lookup(String[].class);
            }
            if (converter == null) {
                converter = this.lookup(String.class);
            }
            return converter;
        }
        if (targetType == String[].class) {
            if (sourceType.isArray() || Collection.class.isAssignableFrom(sourceType)) {
                converter = this.lookup(sourceType);
            }
            if (converter == null) {
                converter = this.lookup(String[].class);
            }
            return converter;
        }
        return this.lookup(targetType);
    }
    
    public void register(final Converter converter, final Class<?> clazz) {
        this.converters.put(clazz, converter);
    }
    
    static {
        ZERO = new Integer(0);
        SPACE = new Character(' ');
        ConvertUtilsBean.defaultShort = new Short((short)0);
    }
}
