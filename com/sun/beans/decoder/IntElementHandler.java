package com.sun.beans.decoder;

final class IntElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return Integer.decode(s);
    }
}
