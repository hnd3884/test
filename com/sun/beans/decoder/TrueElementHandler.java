package com.sun.beans.decoder;

final class TrueElementHandler extends NullElementHandler
{
    @Override
    public Object getValue() {
        return Boolean.TRUE;
    }
}
