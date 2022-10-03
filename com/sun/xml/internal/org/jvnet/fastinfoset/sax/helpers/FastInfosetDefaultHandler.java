package com.sun.xml.internal.org.jvnet.fastinfoset.sax.helpers;

import org.xml.sax.SAXException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class FastInfosetDefaultHandler extends DefaultHandler implements LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler
{
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void octets(final String URI, final int algorithm, final byte[] b, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void object(final String URI, final int algorithm, final Object o) throws SAXException {
    }
    
    @Override
    public void booleans(final boolean[] b, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void bytes(final byte[] b, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void shorts(final short[] s, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void ints(final int[] i, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void longs(final long[] l, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void floats(final float[] f, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void doubles(final double[] d, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void uuids(final long[] msblsb, final int start, final int length) throws SAXException {
    }
}
