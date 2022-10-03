package com.sun.beans.decoder;

final class ShortElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return Short.decode(s);
    }
}
