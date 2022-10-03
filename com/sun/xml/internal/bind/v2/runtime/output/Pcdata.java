package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public abstract class Pcdata implements CharSequence
{
    public abstract void writeTo(final UTF8XmlOutput p0) throws IOException;
    
    public void writeTo(final char[] buf, final int start) {
        this.toString().getChars(0, this.length(), buf, start);
    }
    
    @Override
    public abstract String toString();
}
