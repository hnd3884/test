package com.sun.org.apache.xml.internal.utils;

import javax.xml.transform.TransformerException;

public interface RawCharacterHandler
{
    void charactersRaw(final char[] p0, final int p1, final int p2) throws TransformerException;
}
