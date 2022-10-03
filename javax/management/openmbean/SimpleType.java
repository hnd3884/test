package javax.management.openmbean;

import java.util.HashMap;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import java.util.Map;
import javax.management.ObjectName;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;

public final class SimpleType<T> extends OpenType<T>
{
    static final long serialVersionUID = 2215577471957694503L;
    public static final SimpleType<Void> VOID;
    public static final SimpleType<Boolean> BOOLEAN;
    public static final SimpleType<Character> CHARACTER;
    public static final SimpleType<Byte> BYTE;
    public static final SimpleType<Short> SHORT;
    public static final SimpleType<Integer> INTEGER;
    public static final SimpleType<Long> LONG;
    public static final SimpleType<Float> FLOAT;
    public static final SimpleType<Double> DOUBLE;
    public static final SimpleType<String> STRING;
    public static final SimpleType<BigDecimal> BIGDECIMAL;
    public static final SimpleType<BigInteger> BIGINTEGER;
    public static final SimpleType<Date> DATE;
    public static final SimpleType<ObjectName> OBJECTNAME;
    private static final SimpleType<?>[] typeArray;
    private transient Integer myHashCode;
    private transient String myToString;
    private static final Map<SimpleType<?>, SimpleType<?>> canonicalTypes;
    
    private SimpleType(final Class<T> clazz) {
        super(clazz.getName(), clazz.getName(), clazz.getName(), false);
        this.myHashCode = null;
        this.myToString = null;
    }
    
    @Override
    public boolean isValue(final Object o) {
        return o != null && this.getClassName().equals(o.getClass().getName());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof SimpleType && this.getClassName().equals(((SimpleType)o).getClassName());
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            this.myHashCode = this.getClassName().hashCode();
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = this.getClass().getName() + "(name=" + this.getTypeName() + ")";
        }
        return this.myToString;
    }
    
    public Object readResolve() throws ObjectStreamException {
        final SimpleType simpleType = SimpleType.canonicalTypes.get(this);
        if (simpleType == null) {
            throw new InvalidObjectException("Invalid SimpleType: " + this);
        }
        return simpleType;
    }
    
    static {
        VOID = new SimpleType<Void>(Void.class);
        BOOLEAN = new SimpleType<Boolean>(Boolean.class);
        CHARACTER = new SimpleType<Character>(Character.class);
        BYTE = new SimpleType<Byte>(Byte.class);
        SHORT = new SimpleType<Short>(Short.class);
        INTEGER = new SimpleType<Integer>(Integer.class);
        LONG = new SimpleType<Long>(Long.class);
        FLOAT = new SimpleType<Float>(Float.class);
        DOUBLE = new SimpleType<Double>(Double.class);
        STRING = new SimpleType<String>(String.class);
        BIGDECIMAL = new SimpleType<BigDecimal>(BigDecimal.class);
        BIGINTEGER = new SimpleType<BigInteger>(BigInteger.class);
        DATE = new SimpleType<Date>(Date.class);
        OBJECTNAME = new SimpleType<ObjectName>(ObjectName.class);
        typeArray = new SimpleType[] { SimpleType.VOID, SimpleType.BOOLEAN, SimpleType.CHARACTER, SimpleType.BYTE, SimpleType.SHORT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.FLOAT, SimpleType.DOUBLE, SimpleType.STRING, SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.DATE, SimpleType.OBJECTNAME };
        canonicalTypes = new HashMap<SimpleType<?>, SimpleType<?>>();
        for (int i = 0; i < SimpleType.typeArray.length; ++i) {
            final SimpleType<?> simpleType = SimpleType.typeArray[i];
            SimpleType.canonicalTypes.put(simpleType, simpleType);
        }
    }
}
