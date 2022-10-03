package jdk.nashorn.internal.codegen.types;

public abstract class BitwiseType extends NumericType implements BytecodeBitwiseOps
{
    private static final long serialVersionUID = 1L;
    
    protected BitwiseType(final String name, final Class<?> clazz, final int weight, final int slots) {
        super(name, clazz, weight, slots);
    }
}
