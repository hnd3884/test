package io.netty.handler.codec.http2;

import io.netty.util.AsciiString;
import java.util.Map;
import java.util.Iterator;
import io.netty.handler.codec.Headers;

public interface Http2Headers extends Headers<CharSequence, CharSequence, Http2Headers>
{
    Iterator<Map.Entry<CharSequence, CharSequence>> iterator();
    
    Iterator<CharSequence> valueIterator(final CharSequence p0);
    
    Http2Headers method(final CharSequence p0);
    
    Http2Headers scheme(final CharSequence p0);
    
    Http2Headers authority(final CharSequence p0);
    
    Http2Headers path(final CharSequence p0);
    
    Http2Headers status(final CharSequence p0);
    
    CharSequence method();
    
    CharSequence scheme();
    
    CharSequence authority();
    
    CharSequence path();
    
    CharSequence status();
    
    boolean contains(final CharSequence p0, final CharSequence p1, final boolean p2);
    
    public enum PseudoHeaderName
    {
        METHOD(":method", true), 
        SCHEME(":scheme", true), 
        AUTHORITY(":authority", true), 
        PATH(":path", true), 
        STATUS(":status", false), 
        PROTOCOL(":protocol", true);
        
        private static final char PSEUDO_HEADER_PREFIX = ':';
        private static final byte PSEUDO_HEADER_PREFIX_BYTE = 58;
        private final AsciiString value;
        private final boolean requestOnly;
        private static final CharSequenceMap<PseudoHeaderName> PSEUDO_HEADERS;
        
        private PseudoHeaderName(final String value, final boolean requestOnly) {
            this.value = AsciiString.cached(value);
            this.requestOnly = requestOnly;
        }
        
        public AsciiString value() {
            return this.value;
        }
        
        public static boolean hasPseudoHeaderFormat(final CharSequence headerName) {
            if (headerName instanceof AsciiString) {
                final AsciiString asciiHeaderName = (AsciiString)headerName;
                return asciiHeaderName.length() > 0 && asciiHeaderName.byteAt(0) == 58;
            }
            return headerName.length() > 0 && headerName.charAt(0) == ':';
        }
        
        public static boolean isPseudoHeader(final CharSequence header) {
            return PseudoHeaderName.PSEUDO_HEADERS.contains(header);
        }
        
        public static PseudoHeaderName getPseudoHeader(final CharSequence header) {
            return PseudoHeaderName.PSEUDO_HEADERS.get(header);
        }
        
        public boolean isRequestOnly() {
            return this.requestOnly;
        }
        
        static {
            PSEUDO_HEADERS = new CharSequenceMap<PseudoHeaderName>();
            for (final PseudoHeaderName pseudoHeader : values()) {
                PseudoHeaderName.PSEUDO_HEADERS.add(pseudoHeader.value(), pseudoHeader);
            }
        }
    }
}
