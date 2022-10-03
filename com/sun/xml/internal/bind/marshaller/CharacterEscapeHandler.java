package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public interface CharacterEscapeHandler
{
    void escape(final char[] p0, final int p1, final int p2, final boolean p3, final Writer p4) throws IOException;
}
