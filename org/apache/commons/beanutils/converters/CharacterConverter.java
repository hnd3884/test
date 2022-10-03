package org.apache.commons.beanutils.converters;

public final class CharacterConverter extends AbstractConverter
{
    public CharacterConverter() {
    }
    
    public CharacterConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Character.class;
    }
    
    @Override
    protected String convertToString(final Object value) {
        final String strValue = value.toString();
        return (strValue.length() == 0) ? "" : strValue.substring(0, 1);
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Exception {
        if (Character.class.equals(type) || Character.TYPE.equals(type)) {
            return type.cast(new Character(value.toString().charAt(0)));
        }
        throw this.conversionException(type, value);
    }
}
