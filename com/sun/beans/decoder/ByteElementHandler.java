package com.sun.beans.decoder;

final class ByteElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return Byte.decode(s);
    }
}
