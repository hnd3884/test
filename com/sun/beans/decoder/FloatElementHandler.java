package com.sun.beans.decoder;

final class FloatElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return Float.valueOf(s);
    }
}
