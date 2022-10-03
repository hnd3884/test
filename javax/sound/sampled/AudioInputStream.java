package javax.sound.sampled;

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream
{
    private InputStream stream;
    protected AudioFormat format;
    protected long frameLength;
    protected int frameSize;
    protected long framePos;
    private long markpos;
    private byte[] pushBackBuffer;
    private int pushBackLen;
    private byte[] markPushBackBuffer;
    private int markPushBackLen;
    
    public AudioInputStream(final InputStream stream, final AudioFormat format, final long frameLength) {
        this.pushBackBuffer = null;
        this.pushBackLen = 0;
        this.markPushBackBuffer = null;
        this.markPushBackLen = 0;
        this.format = format;
        this.frameLength = frameLength;
        this.frameSize = format.getFrameSize();
        if (this.frameSize == -1 || this.frameSize <= 0) {
            this.frameSize = 1;
        }
        this.stream = stream;
        this.framePos = 0L;
        this.markpos = 0L;
    }
    
    public AudioInputStream(final TargetDataLine targetDataLine) {
        this.pushBackBuffer = null;
        this.pushBackLen = 0;
        this.markPushBackBuffer = null;
        this.markPushBackLen = 0;
        final TargetDataLineInputStream stream = new TargetDataLineInputStream(targetDataLine);
        this.format = targetDataLine.getFormat();
        this.frameLength = -1L;
        this.frameSize = this.format.getFrameSize();
        if (this.frameSize == -1 || this.frameSize <= 0) {
            this.frameSize = 1;
        }
        this.stream = stream;
        this.framePos = 0L;
        this.markpos = 0L;
    }
    
    public AudioFormat getFormat() {
        return this.format;
    }
    
    public long getFrameLength() {
        return this.frameLength;
    }
    
    @Override
    public int read() throws IOException {
        if (this.frameSize != 1) {
            throw new IOException("cannot read a single byte if frame size > 1");
        }
        final byte[] array = { 0 };
        if (this.read(array) <= 0) {
            return -1;
        }
        return array[0] & 0xFF;
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, int n2) throws IOException {
        if (n2 % this.frameSize != 0) {
            n2 -= n2 % this.frameSize;
            if (n2 == 0) {
                return 0;
            }
        }
        if (this.frameLength != -1L) {
            if (this.framePos >= this.frameLength) {
                return -1;
            }
            if (n2 / this.frameSize > this.frameLength - this.framePos) {
                n2 = (int)(this.frameLength - this.framePos) * this.frameSize;
            }
        }
        int n3 = 0;
        int n4 = n;
        if (this.pushBackLen > 0 && n2 >= this.pushBackLen) {
            System.arraycopy(this.pushBackBuffer, 0, array, n, this.pushBackLen);
            n4 += this.pushBackLen;
            n2 -= this.pushBackLen;
            n3 += this.pushBackLen;
            this.pushBackLen = 0;
        }
        final int read = this.stream.read(array, n4, n2);
        if (read == -1) {
            return -1;
        }
        if (read > 0) {
            n3 += read;
        }
        if (n3 > 0) {
            this.pushBackLen = n3 % this.frameSize;
            if (this.pushBackLen > 0) {
                if (this.pushBackBuffer == null) {
                    this.pushBackBuffer = new byte[this.frameSize];
                }
                System.arraycopy(array, n + n3 - this.pushBackLen, this.pushBackBuffer, 0, this.pushBackLen);
                n3 -= this.pushBackLen;
            }
            this.framePos += n3 / this.frameSize;
        }
        return n3;
    }
    
    @Override
    public long skip(long n) throws IOException {
        if (n % this.frameSize != 0L) {
            n -= n % this.frameSize;
        }
        if (this.frameLength != -1L && n / this.frameSize > this.frameLength - this.framePos) {
            n = (this.frameLength - this.framePos) * this.frameSize;
        }
        final long skip = this.stream.skip(n);
        if (skip % this.frameSize != 0L) {
            throw new IOException("Could not skip an integer number of frames.");
        }
        if (skip >= 0L) {
            this.framePos += skip / this.frameSize;
        }
        return skip;
    }
    
    @Override
    public int available() throws IOException {
        final int available = this.stream.available();
        if (this.frameLength != -1L && available / this.frameSize > this.frameLength - this.framePos) {
            return (int)(this.frameLength - this.framePos) * this.frameSize;
        }
        return available;
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
    
    @Override
    public void mark(final int n) {
        this.stream.mark(n);
        if (this.markSupported()) {
            this.markpos = this.framePos;
            this.markPushBackLen = this.pushBackLen;
            if (this.markPushBackLen > 0) {
                if (this.markPushBackBuffer == null) {
                    this.markPushBackBuffer = new byte[this.frameSize];
                }
                System.arraycopy(this.pushBackBuffer, 0, this.markPushBackBuffer, 0, this.markPushBackLen);
            }
        }
    }
    
    @Override
    public void reset() throws IOException {
        this.stream.reset();
        this.framePos = this.markpos;
        this.pushBackLen = this.markPushBackLen;
        if (this.pushBackLen > 0) {
            if (this.pushBackBuffer == null) {
                this.pushBackBuffer = new byte[this.frameSize - 1];
            }
            System.arraycopy(this.markPushBackBuffer, 0, this.pushBackBuffer, 0, this.pushBackLen);
        }
    }
    
    @Override
    public boolean markSupported() {
        return this.stream.markSupported();
    }
    
    private class TargetDataLineInputStream extends InputStream
    {
        TargetDataLine line;
        
        TargetDataLineInputStream(final TargetDataLine line) {
            this.line = line;
        }
        
        @Override
        public int available() throws IOException {
            return this.line.available();
        }
        
        @Override
        public void close() throws IOException {
            if (this.line.isActive()) {
                this.line.flush();
                this.line.stop();
            }
            this.line.close();
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            if (this.read(array, 0, 1) == -1) {
                return -1;
            }
            int n = array[0];
            if (this.line.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
                n += 128;
            }
            return n;
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            try {
                return this.line.read(array, n, n2);
            }
            catch (final IllegalArgumentException ex) {
                throw new IOException(ex.getMessage());
            }
        }
    }
}
