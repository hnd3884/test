package org.glassfish.jersey.message.internal;

import java.util.Iterator;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;
import java.io.IOException;
import javax.ws.rs.BadRequestException;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;

public abstract class AbstractFormProvider<T> extends AbstractMessageReaderWriterProvider<T>
{
    public <M extends MultivaluedMap<String, String>> M readFrom(final M map, final MediaType mediaType, final boolean decode, final InputStream entityStream) throws IOException {
        final String encoded = AbstractMessageReaderWriterProvider.readFromAsString(entityStream, mediaType);
        final String charsetName = ReaderWriter.getCharset(mediaType).name();
        final StringTokenizer tokenizer = new StringTokenizer(encoded, "&");
        try {
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                final int idx = token.indexOf(61);
                if (idx < 0) {
                    map.add((Object)(decode ? URLDecoder.decode(token, charsetName) : token), (Object)null);
                }
                else {
                    if (idx <= 0) {
                        continue;
                    }
                    if (decode) {
                        map.add((Object)URLDecoder.decode(token.substring(0, idx), charsetName), (Object)URLDecoder.decode(token.substring(idx + 1), charsetName));
                    }
                    else {
                        map.add((Object)token.substring(0, idx), (Object)token.substring(idx + 1));
                    }
                }
            }
            return map;
        }
        catch (final IllegalArgumentException ex) {
            throw new BadRequestException((Throwable)ex);
        }
    }
    
    public <M extends MultivaluedMap<String, String>> void writeTo(final M t, final MediaType mediaType, final OutputStream entityStream) throws IOException {
        final String charsetName = ReaderWriter.getCharset(mediaType).name();
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, List<String>> e : t.entrySet()) {
            for (final String value : e.getValue()) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(e.getKey(), charsetName));
                if (value != null) {
                    sb.append('=');
                    sb.append(URLEncoder.encode(value, charsetName));
                }
            }
        }
        AbstractMessageReaderWriterProvider.writeToAsString(sb.toString(), entityStream, mediaType);
    }
}
