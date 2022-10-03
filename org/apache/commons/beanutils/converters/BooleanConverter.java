package org.apache.commons.beanutils.converters;

public final class BooleanConverter extends AbstractConverter
{
    @Deprecated
    public static final Object NO_DEFAULT;
    private String[] trueStrings;
    private String[] falseStrings;
    
    public BooleanConverter() {
        this.trueStrings = new String[] { "true", "yes", "y", "on", "1" };
        this.falseStrings = new String[] { "false", "no", "n", "off", "0" };
    }
    
    public BooleanConverter(final Object defaultValue) {
        this.trueStrings = new String[] { "true", "yes", "y", "on", "1" };
        this.falseStrings = new String[] { "false", "no", "n", "off", "0" };
        if (defaultValue != BooleanConverter.NO_DEFAULT) {
            this.setDefaultValue(defaultValue);
        }
    }
    
    public BooleanConverter(final String[] trueStrings, final String[] falseStrings) {
        this.trueStrings = new String[] { "true", "yes", "y", "on", "1" };
        this.falseStrings = new String[] { "false", "no", "n", "off", "0" };
        this.trueStrings = copyStrings(trueStrings);
        this.falseStrings = copyStrings(falseStrings);
    }
    
    public BooleanConverter(final String[] trueStrings, final String[] falseStrings, final Object defaultValue) {
        this.trueStrings = new String[] { "true", "yes", "y", "on", "1" };
        this.falseStrings = new String[] { "false", "no", "n", "off", "0" };
        this.trueStrings = copyStrings(trueStrings);
        this.falseStrings = copyStrings(falseStrings);
        if (defaultValue != BooleanConverter.NO_DEFAULT) {
            this.setDefaultValue(defaultValue);
        }
    }
    
    @Override
    protected Class<Boolean> getDefaultType() {
        return Boolean.class;
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            final String stringValue = value.toString().toLowerCase();
            for (final String trueString : this.trueStrings) {
                if (trueString.equals(stringValue)) {
                    return type.cast(Boolean.TRUE);
                }
            }
            for (final String falseString : this.falseStrings) {
                if (falseString.equals(stringValue)) {
                    return type.cast(Boolean.FALSE);
                }
            }
        }
        throw this.conversionException(type, value);
    }
    
    private static String[] copyStrings(final String[] src) {
        final String[] dst = new String[src.length];
        for (int i = 0; i < src.length; ++i) {
            dst[i] = src[i].toLowerCase();
        }
        return dst;
    }
    
    static {
        NO_DEFAULT = new Object();
    }
}
