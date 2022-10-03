package com.sun.beans.decoder;

final class BooleanElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        if (Boolean.TRUE.toString().equalsIgnoreCase(s)) {
            return Boolean.TRUE;
        }
        if (Boolean.FALSE.toString().equalsIgnoreCase(s)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Unsupported boolean argument: " + s);
    }
}
