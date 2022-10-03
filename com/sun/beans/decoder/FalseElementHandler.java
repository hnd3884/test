package com.sun.beans.decoder;

final class FalseElementHandler extends NullElementHandler
{
    @Override
    public Object getValue() {
        return Boolean.FALSE;
    }
}
