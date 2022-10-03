package com.sun.xml.internal.fastinfoset.algorithm;

public final class BuiltInEncodingAlgorithmFactory
{
    private static final BuiltInEncodingAlgorithm[] table;
    public static final HexadecimalEncodingAlgorithm hexadecimalEncodingAlgorithm;
    public static final BASE64EncodingAlgorithm base64EncodingAlgorithm;
    public static final BooleanEncodingAlgorithm booleanEncodingAlgorithm;
    public static final ShortEncodingAlgorithm shortEncodingAlgorithm;
    public static final IntEncodingAlgorithm intEncodingAlgorithm;
    public static final LongEncodingAlgorithm longEncodingAlgorithm;
    public static final FloatEncodingAlgorithm floatEncodingAlgorithm;
    public static final DoubleEncodingAlgorithm doubleEncodingAlgorithm;
    public static final UUIDEncodingAlgorithm uuidEncodingAlgorithm;
    
    public static BuiltInEncodingAlgorithm getAlgorithm(final int index) {
        return BuiltInEncodingAlgorithmFactory.table[index];
    }
    
    static {
        table = new BuiltInEncodingAlgorithm[10];
        hexadecimalEncodingAlgorithm = new HexadecimalEncodingAlgorithm();
        base64EncodingAlgorithm = new BASE64EncodingAlgorithm();
        booleanEncodingAlgorithm = new BooleanEncodingAlgorithm();
        shortEncodingAlgorithm = new ShortEncodingAlgorithm();
        intEncodingAlgorithm = new IntEncodingAlgorithm();
        longEncodingAlgorithm = new LongEncodingAlgorithm();
        floatEncodingAlgorithm = new FloatEncodingAlgorithm();
        doubleEncodingAlgorithm = new DoubleEncodingAlgorithm();
        uuidEncodingAlgorithm = new UUIDEncodingAlgorithm();
        BuiltInEncodingAlgorithmFactory.table[0] = BuiltInEncodingAlgorithmFactory.hexadecimalEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[1] = BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[2] = BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[3] = BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[4] = BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[5] = BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[6] = BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[7] = BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[8] = BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm;
    }
}
