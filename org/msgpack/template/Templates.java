package org.msgpack.template;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.nio.ByteBuffer;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.msgpack.type.Value;

public final class Templates
{
    public static final Template<Value> TValue;
    public static final Template<Byte> TByte;
    public static final Template<Short> TShort;
    public static final Template<Integer> TInteger;
    public static final Template<Long> TLong;
    public static final Template<Character> TCharacter;
    public static final Template<BigInteger> TBigInteger;
    public static final Template<BigDecimal> TBigDecimal;
    public static final Template<Float> TFloat;
    public static final Template<Double> TDouble;
    public static final Template<Boolean> TBoolean;
    public static final Template<String> TString;
    public static final Template<byte[]> TByteArray;
    public static final Template<ByteBuffer> TByteBuffer;
    public static final Template<Date> TDate;
    
    public static <T> Template<T> tNotNullable(final Template<T> innerTemplate) {
        return new NotNullableTemplate<T>(innerTemplate);
    }
    
    public static <E> Template<List<E>> tList(final Template<E> elementTemplate) {
        return (Template<List<E>>)new ListTemplate((Template<Object>)elementTemplate);
    }
    
    public static <K, V> Template<Map<K, V>> tMap(final Template<K> keyTemplate, final Template<V> valueTemplate) {
        return (Template<Map<K, V>>)new MapTemplate((Template<Object>)keyTemplate, (Template<Object>)valueTemplate);
    }
    
    public static <E> Template<Collection<E>> tCollection(final Template<E> elementTemplate) {
        return (Template<Collection<E>>)new CollectionTemplate((Template<Object>)elementTemplate);
    }
    
    public static <E extends Enum> Template<E> tOrdinalEnum(final Class<E> enumClass) {
        return new OrdinalEnumTemplate<E>(enumClass);
    }
    
    @Deprecated
    public static Template tByte() {
        return Templates.TByte;
    }
    
    @Deprecated
    public static Template tShort() {
        return Templates.TShort;
    }
    
    @Deprecated
    public static Template tInteger() {
        return Templates.TInteger;
    }
    
    @Deprecated
    public static Template tLong() {
        return Templates.TLong;
    }
    
    @Deprecated
    public static Template tCharacter() {
        return Templates.TCharacter;
    }
    
    @Deprecated
    public static Template tBigInteger() {
        return Templates.TBigInteger;
    }
    
    @Deprecated
    public static Template tBigDecimal() {
        return Templates.TBigDecimal;
    }
    
    @Deprecated
    public static Template tFloat() {
        return Templates.TFloat;
    }
    
    @Deprecated
    public static Template tDouble() {
        return Templates.TDouble;
    }
    
    @Deprecated
    public static Template tBoolean() {
        return Templates.TBoolean;
    }
    
    @Deprecated
    public static Template tString() {
        return Templates.TString;
    }
    
    @Deprecated
    public static Template tByteArray() {
        return Templates.TByteArray;
    }
    
    @Deprecated
    public static Template tByteBuffer() {
        return Templates.TByteBuffer;
    }
    
    @Deprecated
    public static Template tDate() {
        return Templates.TDate;
    }
    
    static {
        TValue = ValueTemplate.getInstance();
        TByte = ByteTemplate.getInstance();
        TShort = ShortTemplate.getInstance();
        TInteger = IntegerTemplate.getInstance();
        TLong = LongTemplate.getInstance();
        TCharacter = CharacterTemplate.getInstance();
        TBigInteger = BigIntegerTemplate.getInstance();
        TBigDecimal = BigDecimalTemplate.getInstance();
        TFloat = FloatTemplate.getInstance();
        TDouble = DoubleTemplate.getInstance();
        TBoolean = BooleanTemplate.getInstance();
        TString = StringTemplate.getInstance();
        TByteArray = ByteArrayTemplate.getInstance();
        TByteBuffer = ByteBufferTemplate.getInstance();
        TDate = DateTemplate.getInstance();
    }
}
