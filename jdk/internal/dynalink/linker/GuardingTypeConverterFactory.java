package jdk.internal.dynalink.linker;

public interface GuardingTypeConverterFactory
{
    GuardedTypeConversion convertToType(final Class<?> p0, final Class<?> p1) throws Exception;
}
