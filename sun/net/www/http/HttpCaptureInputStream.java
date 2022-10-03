package sun.net.www.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class HttpCaptureInputStream extends FilterInputStream
{
    private HttpCapture capture;
    
    public HttpCaptureInputStream(final InputStream inputStream, final HttpCapture capture) {
        super(inputStream);
        this.capture = null;
        this.capture = capture;
    }
    
    @Override
    public int read() throws IOException {
        final int read = super.read();
        this.capture.received(read);
        return read;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.capture.flush();
        }
        catch (final IOException ex) {}
        super.close();
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        final int read = super.read(array);
        for (int i = 0; i < read; ++i) {
            this.capture.received(array[i]);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = super.read(array, n, n2);
        for (int i = 0; i < read; ++i) {
            this.capture.received(array[n + i]);
        }
        return read;
    }
}
