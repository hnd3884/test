package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface PrimitiveTypeContentHandler
{
    void booleans(final boolean[] p0, final int p1, final int p2) throws SAXException;
    
    void bytes(final byte[] p0, final int p1, final int p2) throws SAXException;
    
    void shorts(final short[] p0, final int p1, final int p2) throws SAXException;
    
    void ints(final int[] p0, final int p1, final int p2) throws SAXException;
    
    void longs(final long[] p0, final int p1, final int p2) throws SAXException;
    
    void floats(final float[] p0, final int p1, final int p2) throws SAXException;
    
    void doubles(final double[] p0, final int p1, final int p2) throws SAXException;
    
    void uuids(final long[] p0, final int p1, final int p2) throws SAXException;
}
