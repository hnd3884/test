package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import javax.activation.DataHandler;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import javax.xml.stream.XMLStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class XOPUtils
{
    private static final MimePartProvider nullMimePartProvider;
    
    private XOPUtils() {
    }
    
    public static String getContentIDFromURL(final String url) {
        if (url.startsWith("cid:")) {
            try {
                return URLDecoder.decode(url.substring(4), "ascii");
            }
            catch (final UnsupportedEncodingException ex) {
                throw new Error(ex);
            }
        }
        throw new IllegalArgumentException("The URL doesn't use the cid scheme");
    }
    
    public static String getURLForContentID(final String contentID) {
        return "cid:" + contentID.replaceAll("%", "%25");
    }
    
    public static XOPEncodedStream getXOPEncodedStream(final XMLStreamReader reader) {
        if (reader instanceof XOPEncodingStreamReader) {
            return new XOPEncodedStream(reader, (MimePartProvider)reader);
        }
        if (reader instanceof XOPDecodingStreamReader) {
            return ((XOPDecodingStreamReader)reader).getXOPEncodedStream();
        }
        if (XMLStreamReaderUtils.getDataHandlerReader(reader) != null) {
            final XOPEncodingStreamReader wrapper = new XOPEncodingStreamReader(reader, ContentIDGenerator.DEFAULT, OptimizationPolicy.ALL);
            return new XOPEncodedStream(wrapper, wrapper);
        }
        return new XOPEncodedStream(reader, XOPUtils.nullMimePartProvider);
    }
    
    static {
        nullMimePartProvider = new MimePartProvider() {
            public boolean isLoaded(final String contentID) {
                throw new IllegalArgumentException("There are no MIME parts!");
            }
            
            public DataHandler getDataHandler(final String contentID) throws IOException {
                throw new IllegalArgumentException("There are no MIME parts!");
            }
        };
    }
}
