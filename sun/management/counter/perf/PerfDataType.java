package sun.management.counter.perf;

import java.io.UnsupportedEncodingException;

class PerfDataType
{
    private final String name;
    private final byte value;
    private final int size;
    public static final PerfDataType BOOLEAN;
    public static final PerfDataType CHAR;
    public static final PerfDataType FLOAT;
    public static final PerfDataType DOUBLE;
    public static final PerfDataType BYTE;
    public static final PerfDataType SHORT;
    public static final PerfDataType INT;
    public static final PerfDataType LONG;
    public static final PerfDataType ILLEGAL;
    private static PerfDataType[] basicTypes;
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public byte byteValue() {
        return this.value;
    }
    
    public int size() {
        return this.size;
    }
    
    public static PerfDataType toPerfDataType(final byte b) {
        for (int i = 0; i < PerfDataType.basicTypes.length; ++i) {
            if (PerfDataType.basicTypes[i].byteValue() == b) {
                return PerfDataType.basicTypes[i];
            }
        }
        return PerfDataType.ILLEGAL;
    }
    
    private PerfDataType(final String name, final String s, final int size) {
        this.name = name;
        this.size = size;
        try {
            this.value = s.getBytes("UTF-8")[0];
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError("Unknown encoding", ex);
        }
    }
    
    static {
        BOOLEAN = new PerfDataType("boolean", "Z", 1);
        CHAR = new PerfDataType("char", "C", 1);
        FLOAT = new PerfDataType("float", "F", 8);
        DOUBLE = new PerfDataType("double", "D", 8);
        BYTE = new PerfDataType("byte", "B", 1);
        SHORT = new PerfDataType("short", "S", 2);
        INT = new PerfDataType("int", "I", 4);
        LONG = new PerfDataType("long", "J", 8);
        ILLEGAL = new PerfDataType("illegal", "X", 0);
        PerfDataType.basicTypes = new PerfDataType[] { PerfDataType.LONG, PerfDataType.BYTE, PerfDataType.BOOLEAN, PerfDataType.CHAR, PerfDataType.FLOAT, PerfDataType.DOUBLE, PerfDataType.SHORT, PerfDataType.INT };
    }
}
