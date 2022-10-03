package com.sun.beans.decoder;

final class DoubleElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return Double.valueOf(s);
    }
}
