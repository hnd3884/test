package org.apache.http.impl.entity;

import org.apache.http.HttpException;
import org.apache.http.HeaderElement;
import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.util.Args;
import org.apache.http.HttpMessage;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.entity.ContentLengthStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxContentLengthStrategy implements ContentLengthStrategy
{
    public static final LaxContentLengthStrategy INSTANCE;
    private final int implicitLen;
    
    public LaxContentLengthStrategy(final int implicitLen) {
        this.implicitLen = implicitLen;
    }
    
    public LaxContentLengthStrategy() {
        this(-1);
    }
    
    @Override
    public long determineLength(final HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        final Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            HeaderElement[] encodings;
            try {
                encodings = transferEncodingHeader.getElements();
            }
            catch (final ParseException px) {
                throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, px);
            }
            final int len = encodings.length;
            if ("identity".equalsIgnoreCase(transferEncodingHeader.getValue())) {
                return -1L;
            }
            if (len > 0 && "chunked".equalsIgnoreCase(encodings[len - 1].getName())) {
                return -2L;
            }
            return -1L;
        }
        else {
            final Header contentLengthHeader = message.getFirstHeader("Content-Length");
            if (contentLengthHeader != null) {
                long contentLen = -1L;
                final Header[] headers = message.getHeaders("Content-Length");
                int i = headers.length - 1;
                while (i >= 0) {
                    final Header header = headers[i];
                    try {
                        contentLen = Long.parseLong(header.getValue());
                    }
                    catch (final NumberFormatException ignore) {
                        --i;
                        continue;
                    }
                    break;
                }
                return (contentLen >= 0L) ? contentLen : -1L;
            }
            return this.implicitLen;
        }
    }
    
    static {
        INSTANCE = new LaxContentLengthStrategy();
    }
}
