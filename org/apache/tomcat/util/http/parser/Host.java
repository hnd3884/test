package org.apache.tomcat.util.http.parser;

import org.apache.tomcat.util.buf.ByteChunk;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;
import org.apache.tomcat.util.buf.MessageBytes;

public class Host
{
    public static int parse(final MessageBytes mb) {
        return parse(new MessageBytesReader(mb));
    }
    
    public static int parse(final String string) {
        return parse(new StringReader(string));
    }
    
    private static int parse(final Reader reader) {
        try {
            reader.mark(1);
            final int first = reader.read();
            reader.reset();
            if (HttpParser.isAlpha(first)) {
                return HttpParser.readHostDomainName(reader);
            }
            if (HttpParser.isNumeric(first)) {
                return HttpParser.readHostIPv4(reader, false);
            }
            if (91 == first) {
                return HttpParser.readHostIPv6(reader);
            }
            throw new IllegalArgumentException();
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
    
    private static class MessageBytesReader extends Reader
    {
        private final byte[] bytes;
        private final int end;
        private int pos;
        private int mark;
        
        public MessageBytesReader(final MessageBytes mb) {
            final ByteChunk bc = mb.getByteChunk();
            this.bytes = bc.getBytes();
            this.pos = bc.getOffset();
            this.end = bc.getEnd();
        }
        
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            for (int i = off; i < off + len; ++i) {
                cbuf[i] = (char)(this.bytes[this.pos++] & 0xFF);
            }
            return len;
        }
        
        @Override
        public void close() throws IOException {
        }
        
        @Override
        public int read() throws IOException {
            if (this.pos < this.end) {
                return this.bytes[this.pos++] & 0xFF;
            }
            return -1;
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public void mark(final int readAheadLimit) throws IOException {
            this.mark = this.pos;
        }
        
        @Override
        public void reset() throws IOException {
            this.pos = this.mark;
        }
    }
}
