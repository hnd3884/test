package org.apache.commons.beanutils.converters;

public final class ClassConverter extends AbstractConverter
{
    public ClassConverter() {
    }
    
    public ClassConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Class.class;
    }
    
    @Override
    protected String convertToString(final Object value) {
        return (value instanceof Class) ? ((Class)value).getName() : value.toString();
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Class.class.equals(type)) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                try {
                    return type.cast(classLoader.loadClass(value.toString()));
                }
                catch (final ClassNotFoundException ex) {}
            }
            classLoader = ClassConverter.class.getClassLoader();
            return type.cast(classLoader.loadClass(value.toString()));
        }
        throw this.conversionException(type, value);
    }
}
