package com.sun.beans.decoder;

final class LongElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return Long.decode(s);
    }
}
