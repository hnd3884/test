package org.apache.jasper.optimizations;

import org.apache.jasper.compiler.StringInterpreterFactory;

public class StringInterpreterEnum extends StringInterpreterFactory.DefaultStringInterpreter
{
    @Override
    protected String coerceToOtherType(final Class<?> c, final String s, final boolean isNamedAttribute) {
        if (c.isEnum() && !isNamedAttribute) {
            final Enum<?> enumValue = Enum.valueOf(c, s);
            return c.getName() + "." + enumValue.name();
        }
        return null;
    }
}
