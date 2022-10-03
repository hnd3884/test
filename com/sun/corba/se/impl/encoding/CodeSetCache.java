package com.sun.corba.se.impl.encoding;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.util.WeakHashMap;
import java.util.Map;

class CodeSetCache
{
    private ThreadLocal converterCaches;
    private static final int BTC_CACHE_MAP = 0;
    private static final int CTB_CACHE_MAP = 1;
    
    CodeSetCache() {
        this.converterCaches = new ThreadLocal() {
            public Object initialValue() {
                return new Map[] { new WeakHashMap(), new WeakHashMap() };
            }
        };
    }
    
    CharsetDecoder getByteToCharConverter(final Object o) {
        return ((Map[])this.converterCaches.get())[0].get(o);
    }
    
    CharsetEncoder getCharToByteConverter(final Object o) {
        return ((Map[])this.converterCaches.get())[1].get(o);
    }
    
    CharsetDecoder setConverter(final Object o, final CharsetDecoder charsetDecoder) {
        ((Map[])this.converterCaches.get())[0].put(o, charsetDecoder);
        return charsetDecoder;
    }
    
    CharsetEncoder setConverter(final Object o, final CharsetEncoder charsetEncoder) {
        ((Map[])this.converterCaches.get())[1].put(o, charsetEncoder);
        return charsetEncoder;
    }
}
