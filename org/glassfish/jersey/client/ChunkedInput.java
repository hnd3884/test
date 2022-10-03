package org.glassfish.jersey.client;

import java.util.Iterator;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.io.Closeable;
import javax.ws.rs.core.GenericType;

public class ChunkedInput<T> extends GenericType<T> implements Closeable
{
    private static final Logger LOGGER;
    private final AtomicBoolean closed;
    private ChunkParser parser;
    private MediaType mediaType;
    private final InputStream inputStream;
    private final Annotation[] annotations;
    private final MultivaluedMap<String, String> headers;
    private final MessageBodyWorkers messageBodyWorkers;
    private final PropertiesDelegate propertiesDelegate;
    
    public static ChunkParser createParser(final String boundary) {
        return new FixedBoundaryParser(boundary.getBytes());
    }
    
    public static ChunkParser createParser(final byte[] boundary) {
        return new FixedBoundaryParser(boundary);
    }
    
    public static ChunkParser createMultiParser(final String... boundaries) {
        return new FixedMultiBoundaryParser(boundaries);
    }
    
    protected ChunkedInput(final Type chunkType, final InputStream inputStream, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> headers, final MessageBodyWorkers messageBodyWorkers, final PropertiesDelegate propertiesDelegate) {
        super(chunkType);
        this.closed = new AtomicBoolean(false);
        this.parser = createParser("\r\n");
        this.inputStream = inputStream;
        this.annotations = annotations;
        this.mediaType = mediaType;
        this.headers = headers;
        this.messageBodyWorkers = messageBodyWorkers;
        this.propertiesDelegate = propertiesDelegate;
    }
    
    public ChunkParser getParser() {
        return this.parser;
    }
    
    public void setParser(final ChunkParser parser) {
        this.parser = parser;
    }
    
    public MediaType getChunkType() {
        return this.mediaType;
    }
    
    public void setChunkType(final MediaType mediaType) throws IllegalArgumentException {
        if (mediaType == null) {
            throw new IllegalArgumentException(LocalizationMessages.CHUNKED_INPUT_MEDIA_TYPE_NULL());
        }
        this.mediaType = mediaType;
    }
    
    public void setChunkType(final String mediaType) throws IllegalArgumentException {
        this.mediaType = MediaType.valueOf(mediaType);
    }
    
    public void close() {
        if (this.closed.compareAndSet(false, true) && this.inputStream != null) {
            try {
                this.inputStream.close();
            }
            catch (final IOException e) {
                ChunkedInput.LOGGER.log(Level.FINE, LocalizationMessages.CHUNKED_INPUT_STREAM_CLOSING_ERROR(), e);
            }
        }
    }
    
    public boolean isClosed() {
        return this.closed.get();
    }
    
    public T read() throws IllegalStateException {
        if (this.closed.get()) {
            throw new IllegalStateException(LocalizationMessages.CHUNKED_INPUT_CLOSED());
        }
        try {
            final byte[] chunk = this.parser.readChunk(this.inputStream);
            if (chunk != null) {
                final ByteArrayInputStream chunkStream = new ByteArrayInputStream(chunk);
                return (T)this.messageBodyWorkers.readFrom(this.getRawType(), this.getType(), this.annotations, this.mediaType, (MultivaluedMap)this.headers, this.propertiesDelegate, (InputStream)chunkStream, (Iterable)Collections.emptyList(), false);
            }
            this.close();
        }
        catch (final IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, e.getMessage(), e);
            this.close();
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ChunkedInput.class.getName());
    }
    
    private abstract static class AbstractBoundaryParser implements ChunkParser
    {
        @Override
        public byte[] readChunk(final InputStream in) throws IOException {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final byte[] delimiterBuffer = new byte[this.getDelimiterBufferSize()];
            int data;
            int dPos;
            do {
                dPos = 0;
                while ((data = in.read()) != -1) {
                    final byte b = (byte)data;
                    byte[] delimiter = this.getDelimiter(b, dPos, delimiterBuffer);
                    if (delimiter != null && b == delimiter[dPos]) {
                        delimiterBuffer[dPos++] = b;
                        if (dPos == delimiter.length) {
                            break;
                        }
                        continue;
                    }
                    else if (dPos > 0) {
                        delimiter = this.getDelimiter(dPos - 1, delimiterBuffer);
                        delimiterBuffer[dPos] = b;
                        final int matched = matchTail(delimiterBuffer, 1, dPos, delimiter);
                        if (matched == 0) {
                            buffer.write(delimiterBuffer, 0, dPos);
                            buffer.write(b);
                            dPos = 0;
                        }
                        else {
                            if (matched == delimiter.length) {
                                break;
                            }
                            buffer.write(delimiterBuffer, 0, dPos + 1 - matched);
                            dPos = matched;
                        }
                    }
                    else {
                        buffer.write(b);
                    }
                }
            } while (data != -1 && buffer.size() == 0);
            if (dPos > 0 && dPos != this.getDelimiter(dPos - 1, delimiterBuffer).length) {
                buffer.write(delimiterBuffer, 0, dPos);
            }
            return (byte[])((buffer.size() > 0) ? buffer.toByteArray() : null);
        }
        
        abstract byte[] getDelimiter(final byte p0, final int p1, final byte[] p2);
        
        abstract byte[] getDelimiter(final int p0, final byte[] p1);
        
        abstract int getDelimiterBufferSize();
        
        private static int matchTail(final byte[] buffer, final int offset, final int length, final byte[] pattern) {
            if (pattern == null) {
                return 0;
            }
            int i = 0;
        Label_0009:
            while (i < length) {
                final int tailLength = length - i;
                for (int j = 0; j < tailLength; ++j) {
                    if (buffer[offset + i + j] != pattern[j]) {
                        ++i;
                        continue Label_0009;
                    }
                }
                return tailLength;
            }
            return 0;
        }
    }
    
    private static class FixedBoundaryParser extends AbstractBoundaryParser
    {
        private final byte[] delimiter;
        
        public FixedBoundaryParser(final byte[] boundary) {
            this.delimiter = Arrays.copyOf(boundary, boundary.length);
        }
        
        @Override
        byte[] getDelimiter(final byte b, final int pos, final byte[] delimiterBuffer) {
            return this.delimiter;
        }
        
        @Override
        byte[] getDelimiter(final int pos, final byte[] delimiterBuffer) {
            return this.delimiter;
        }
        
        @Override
        int getDelimiterBufferSize() {
            return this.delimiter.length;
        }
    }
    
    private static class FixedMultiBoundaryParser extends AbstractBoundaryParser
    {
        private final List<byte[]> delimiters;
        private final int longestDelimiterLength;
        
        public FixedMultiBoundaryParser(final String... boundaries) {
            this.delimiters = new ArrayList<byte[]>();
            for (final String boundary : boundaries) {
                final byte[] boundaryBytes = boundary.getBytes();
                this.delimiters.add(Arrays.copyOf(boundaryBytes, boundaryBytes.length));
            }
            Collections.sort(this.delimiters, new Comparator<byte[]>() {
                @Override
                public int compare(final byte[] o1, final byte[] o2) {
                    return Integer.compare(o1.length, o2.length);
                }
            });
            final byte[] longestDelimiter = this.delimiters.get(this.delimiters.size() - 1);
            this.longestDelimiterLength = longestDelimiter.length;
        }
        
        @Override
        byte[] getDelimiter(final byte b, final int pos, final byte[] delimiterBuffer) {
            final byte[] buffer = Arrays.copyOf(delimiterBuffer, delimiterBuffer.length);
            buffer[pos] = b;
            return this.getDelimiter(pos, buffer);
        }
        
        @Override
        byte[] getDelimiter(final int pos, final byte[] delimiterBuffer) {
            for (final byte[] delimiter : this.delimiters) {
                if (pos > delimiter.length) {
                    continue;
                }
                for (int i = 0; i <= pos && i < delimiter.length; ++i) {
                    if (delimiter[i] != delimiterBuffer[i]) {
                        break;
                    }
                    if (pos == i) {
                        return delimiter;
                    }
                }
            }
            return null;
        }
        
        @Override
        int getDelimiterBufferSize() {
            return this.longestDelimiterLength;
        }
    }
}
