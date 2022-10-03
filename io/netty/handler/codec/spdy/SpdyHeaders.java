package io.netty.handler.codec.spdy;

import io.netty.util.AsciiString;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import io.netty.handler.codec.Headers;

public interface SpdyHeaders extends Headers<CharSequence, CharSequence, SpdyHeaders>
{
    String getAsString(final CharSequence p0);
    
    List<String> getAllAsString(final CharSequence p0);
    
    Iterator<Map.Entry<String, String>> iteratorAsString();
    
    boolean contains(final CharSequence p0, final CharSequence p1, final boolean p2);
    
    public static final class HttpNames
    {
        public static final AsciiString HOST;
        public static final AsciiString METHOD;
        public static final AsciiString PATH;
        public static final AsciiString SCHEME;
        public static final AsciiString STATUS;
        public static final AsciiString VERSION;
        
        private HttpNames() {
        }
        
        static {
            HOST = AsciiString.cached(":host");
            METHOD = AsciiString.cached(":method");
            PATH = AsciiString.cached(":path");
            SCHEME = AsciiString.cached(":scheme");
            STATUS = AsciiString.cached(":status");
            VERSION = AsciiString.cached(":version");
        }
    }
}
