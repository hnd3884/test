package HTTPClient;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class HttpOutputStream extends OutputStream
{
    private static final NVPair[] empty;
    private int length;
    private int rcvd;
    private Request req;
    private Response resp;
    private OutputStream os;
    private ByteArrayOutputStream bos;
    private NVPair[] trailers;
    private int con_to;
    private boolean ignore;
    
    public HttpOutputStream() {
        this.trailers = HttpOutputStream.empty;
        this.ignore = false;
        this.length = -1;
    }
    
    public HttpOutputStream(final int length) {
        this.trailers = HttpOutputStream.empty;
        this.ignore = false;
        if (length < 0) {
            throw new IllegalArgumentException("Length must be greater equal 0");
        }
        this.length = length;
    }
    
    void goAhead(final Request req, final OutputStream os, final int con_to) {
        this.req = req;
        this.os = os;
        this.con_to = con_to;
        if (os == null) {
            this.bos = new ByteArrayOutputStream();
        }
        Log.write(1, "OutS:  Stream ready for writing");
        if (this.bos != null) {
            Log.write(1, "OutS:  Buffering all data before sending request");
        }
    }
    
    void ignoreData(final Request req) {
        this.req = req;
        this.ignore = true;
    }
    
    synchronized Response getResponse() {
        while (this.resp == null) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
        return this.resp;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public NVPair[] getTrailers() {
        return this.trailers;
    }
    
    public void setTrailers(final NVPair[] trailers) {
        if (trailers != null) {
            this.trailers = trailers;
        }
        else {
            this.trailers = HttpOutputStream.empty;
        }
    }
    
    public void reset() {
        this.rcvd = 0;
        this.req = null;
        this.resp = null;
        this.os = null;
        this.bos = null;
        this.con_to = 0;
        this.ignore = false;
    }
    
    public void write(final int b) throws IOException, IllegalAccessError {
        final byte[] tmp = { (byte)b };
        this.write(tmp, 0, 1);
    }
    
    public synchronized void write(final byte[] buf, final int off, final int len) throws IOException, IllegalAccessError {
        if (this.req == null) {
            throw new IllegalAccessError("Stream not associated with a request");
        }
        if (this.ignore) {
            return;
        }
        IOException ioe = null;
        if (this.length != -1 && this.rcvd + len > this.length) {
            ioe = new IOException("Tried to write too many bytes (" + (this.rcvd + len) + " > " + this.length + ")");
            this.req.getConnection().closeDemux(ioe, false);
            this.req.getConnection().outputFinished();
            throw ioe;
        }
        try {
            if (this.bos != null) {
                this.bos.write(buf, off, len);
            }
            else if (this.length != -1) {
                this.os.write(buf, off, len);
            }
            else {
                this.os.write(Codecs.chunkedEncode(buf, off, len, null, false));
            }
        }
        catch (final IOException ioe) {
            this.req.getConnection().closeDemux(ioe, true);
            this.req.getConnection().outputFinished();
            throw ioe;
        }
        this.rcvd += len;
    }
    
    public synchronized void close() throws IOException, IllegalAccessError {
        if (this.req == null) {
            throw new IllegalAccessError("Stream not associated with a request");
        }
        if (this.ignore) {
            return;
        }
        if (this.bos != null) {
            this.req.setData(this.bos.toByteArray());
            this.req.setStream(null);
            if (this.trailers.length > 0) {
                NVPair[] hdrs = this.req.getHeaders();
                int len = hdrs.length;
                for (int idx = 0; idx < len; ++idx) {
                    if (hdrs[idx].getName().equalsIgnoreCase("Trailer")) {
                        System.arraycopy(hdrs, idx + 1, hdrs, idx, len - idx - 1);
                        --len;
                    }
                }
                hdrs = Util.resizeArray(hdrs, len + this.trailers.length);
                System.arraycopy(this.trailers, 0, hdrs, len, this.trailers.length);
                this.req.setHeaders(hdrs);
            }
            Log.write(1, "OutS:  Sending request");
            try {
                this.resp = this.req.getConnection().sendRequest(this.req, this.con_to);
            }
            catch (final ModuleException me) {
                throw new IOException(me.toString());
            }
            this.notify();
        }
        else {
            if (this.rcvd < this.length) {
                final IOException ioe = new IOException("Premature close: only " + this.rcvd + " bytes written instead of the " + "expected " + this.length);
                this.req.getConnection().closeDemux(ioe, false);
                this.req.getConnection().outputFinished();
                throw ioe;
            }
            try {
                if (this.length == -1) {
                    if (Log.isEnabled(1) && this.trailers.length > 0) {
                        Log.write(1, "OutS:  Sending trailers:");
                        for (int idx = 0; idx < this.trailers.length; ++idx) {
                            Log.write(1, "       " + this.trailers[idx].getName() + ": " + this.trailers[idx].getValue());
                        }
                    }
                    this.os.write(Codecs.chunkedEncode(null, 0, 0, this.trailers, true));
                }
                this.os.flush();
                Log.write(1, "OutS:  All data sent");
            }
            catch (final IOException ioe2) {
                this.req.getConnection().closeDemux(ioe2, true);
                throw ioe2;
            }
            finally {
                this.req.getConnection().outputFinished();
            }
        }
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + "[length=" + this.length + "]";
    }
    
    static {
        empty = new NVPair[0];
    }
}
