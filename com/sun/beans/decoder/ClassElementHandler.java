package com.sun.beans.decoder;

final class ClassElementHandler extends StringElementHandler
{
    public Object getValue(final String s) {
        return this.getOwner().findClass(s);
    }
}
