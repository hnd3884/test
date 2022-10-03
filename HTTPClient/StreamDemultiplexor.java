package HTTPClient;

import java.net.SocketException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.net.Socket;

class StreamDemultiplexor implements GlobalConstants
{
    private int Protocol;
    private HTTPConnection Connection;
    private BufferedInputStream Stream;
    private Socket Sock;
    private ResponseHandler MarkedForClose;
    private SocketTimeout.TimeoutEntry Timer;
    private static SocketTimeout TimerThread;
    private static Object cleanup;
    private LinkedList RespHandlerList;
    private long chunk_len;
    private int cur_timeout;
    
    StreamDemultiplexor(final int protocol, final Socket sock, final HTTPConnection connection) throws IOException {
        this.Protocol = protocol;
        this.Connection = connection;
        this.RespHandlerList = new LinkedList();
        this.init(sock);
    }
    
    private void init(final Socket sock) throws IOException {
        Log.write(4, "Demux: Initializing Stream Demultiplexor (" + this.hashCode() + ")");
        this.Sock = sock;
        this.Stream = new BufferedInputStream(sock.getInputStream());
        this.MarkedForClose = null;
        this.chunk_len = -1L;
        (this.Timer = StreamDemultiplexor.TimerThread.setTimeout(this)).hyber();
    }
    
    void register(final Response resp_handler, final Request req) throws RetryException {
        synchronized (this.RespHandlerList) {
            if (this.Sock == null) {
                throw new RetryException();
            }
            this.RespHandlerList.addToEnd(new ResponseHandler(resp_handler, req, this));
            monitorexit(this.RespHandlerList);
        }
    }
    
    RespInputStream getStream(final Response resp) {
        ResponseHandler resph;
        synchronized (this.RespHandlerList) {
            for (resph = (ResponseHandler)this.RespHandlerList.enumerate(); resph != null && resph.resp != resp; resph = (ResponseHandler)this.RespHandlerList.next()) {}
            monitorexit(this.RespHandlerList);
        }
        if (resph != null) {
            return resph.stream;
        }
        return null;
    }
    
    void restartTimer() {
        if (this.Timer != null) {
            this.Timer.reset();
        }
    }
    
    int read(final byte[] b, final int off, int len, final ResponseHandler resph, final int timeout) throws IOException {
        if (resph.exception != null) {
            resph.exception.fillInStackTrace();
            throw resph.exception;
        }
        if (resph.eof) {
            return -1;
        }
        ResponseHandler head;
        while ((head = (ResponseHandler)this.RespHandlerList.getFirst()) != null && head != resph) {
            try {
                head.stream.readAll(timeout);
            }
            catch (final IOException ioe) {
                if (ioe instanceof InterruptedIOException) {
                    throw ioe;
                }
                resph.exception.fillInStackTrace();
                throw resph.exception;
            }
        }
        synchronized (this) {
            if (resph.exception != null) {
                resph.exception.fillInStackTrace();
                throw resph.exception;
            }
            if (resph.resp.cd_type != 1) {
                Log.write(4, "Demux: Reading for stream " + resph.stream.hashCode());
            }
            if (this.Timer != null) {
                this.Timer.hyber();
            }
            try {
                int rcvd = -1;
                if (timeout != this.cur_timeout) {
                    Log.write(4, "Demux: Setting timeout to " + timeout + " ms");
                    this.Sock.setSoTimeout(timeout);
                    this.cur_timeout = timeout;
                }
                switch (resph.resp.cd_type) {
                    case 0: {
                        rcvd = this.Stream.read(b, off, len);
                        if (rcvd == -1) {
                            throw new EOFException("Premature EOF encountered");
                        }
                        break;
                    }
                    case 1: {
                        rcvd = -1;
                        this.close(resph);
                        break;
                    }
                    case 2: {
                        rcvd = this.Stream.read(b, off, len);
                        if (rcvd == -1) {
                            this.close(resph);
                            break;
                        }
                        break;
                    }
                    case 3: {
                        final int cl = resph.resp.ContentLength;
                        if (len > cl - resph.stream.count) {
                            len = cl - resph.stream.count;
                        }
                        rcvd = this.Stream.read(b, off, len);
                        if (rcvd == -1) {
                            throw new EOFException("Premature EOF encountered");
                        }
                        if (resph.stream.count + rcvd == cl) {
                            this.close(resph);
                            break;
                        }
                        break;
                    }
                    case 4: {
                        if (this.chunk_len == -1L) {
                            this.chunk_len = Codecs.getChunkLength(this.Stream);
                        }
                        if (this.chunk_len <= 0L) {
                            resph.resp.readTrailers(this.Stream);
                            rcvd = -1;
                            this.close(resph);
                            this.chunk_len = -1L;
                            break;
                        }
                        if (len > this.chunk_len) {
                            len = (int)this.chunk_len;
                        }
                        rcvd = this.Stream.read(b, off, len);
                        if (rcvd == -1) {
                            throw new EOFException("Premature EOF encountered");
                        }
                        this.chunk_len -= rcvd;
                        if (this.chunk_len == 0L) {
                            this.Stream.read();
                            this.Stream.read();
                            this.chunk_len = -1L;
                            break;
                        }
                        break;
                    }
                    case 5: {
                        final byte[] endbndry = resph.getEndBoundary(this.Stream);
                        final int[] end_cmp = resph.getEndCompiled(this.Stream);
                        rcvd = this.Stream.read(b, off, len);
                        if (rcvd == -1) {
                            throw new EOFException("Premature EOF encountered");
                        }
                        final int ovf = this.Stream.pastEnd(endbndry, end_cmp);
                        if (ovf != -1) {
                            rcvd -= ovf;
                            this.close(resph);
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new Error("Internal Error in StreamDemultiplexor: Invalid cd_type " + resph.resp.cd_type);
                    }
                }
                this.restartTimer();
                return rcvd;
            }
            catch (final InterruptedIOException ie) {
                this.restartTimer();
                throw ie;
            }
            catch (final IOException ioe2) {
                Log.write(4, "Demux: ", ioe2);
                this.close(ioe2, true);
                throw resph.exception;
            }
            catch (final ParseException pe) {
                Log.write(4, "Demux: ", pe);
                this.close(new IOException(pe.toString()), true);
                throw resph.exception;
            }
        }
    }
    
    synchronized long skip(final long num, final ResponseHandler resph) throws IOException {
        if (resph.exception != null) {
            resph.exception.fillInStackTrace();
            throw resph.exception;
        }
        if (resph.eof) {
            return 0L;
        }
        final byte[] dummy = new byte[(int)num];
        final int rcvd = this.read(dummy, 0, (int)num, resph, 0);
        if (rcvd == -1) {
            return 0L;
        }
        return rcvd;
    }
    
    synchronized int available(final ResponseHandler resph) throws IOException {
        if (resph != null && resph.exception != null) {
            resph.exception.fillInStackTrace();
            throw resph.exception;
        }
        if (resph != null && resph.eof) {
            return 0;
        }
        final int avail = this.Stream.available();
        if (resph == null) {
            return avail;
        }
        switch (resph.resp.cd_type) {
            case 2: {
                return 0;
            }
            case 1: {
                return (avail > 0) ? 1 : 0;
            }
            case 3: {
                return avail;
            }
            case 4: {
                int cl = resph.resp.ContentLength;
                cl -= resph.stream.count;
                return (avail < cl) ? avail : cl;
            }
            case 5: {
                return avail;
            }
            case 6: {
                return avail;
            }
            default: {
                throw new Error("Internal Error in StreamDemultiplexor: Invalid cd_type " + resph.resp.cd_type);
            }
        }
    }
    
    synchronized void close(final IOException exception, final boolean was_reset) {
        if (this.Sock == null) {
            return;
        }
        Log.write(4, "Demux: Closing all streams and socket (" + this.hashCode() + ")");
        try {
            this.Stream.close();
        }
        catch (final IOException ex) {}
        try {
            this.Sock.close();
        }
        catch (final IOException ex2) {}
        this.Sock = null;
        if (this.Timer != null) {
            this.Timer.kill();
            this.Timer = null;
        }
        this.Connection.DemuxList.remove(this);
        if (exception != null) {
            synchronized (this.RespHandlerList) {
                this.retry_requests(exception, was_reset);
                monitorexit(this.RespHandlerList);
            }
        }
    }
    
    private void retry_requests(final IOException exception, final boolean was_reset) {
        RetryException first = null;
        RetryException prev = null;
        for (ResponseHandler resph = (ResponseHandler)this.RespHandlerList.enumerate(); resph != null; resph = (ResponseHandler)this.RespHandlerList.next()) {
            if (resph.resp.got_headers) {
                resph.exception = exception;
            }
            else {
                final RetryException tmp = new RetryException(exception.getMessage());
                if (first == null) {
                    first = tmp;
                }
                tmp.request = resph.request;
                tmp.response = resph.resp;
                tmp.exception = exception;
                tmp.conn_reset = was_reset;
                tmp.first = first;
                tmp.addToListAfter(prev);
                prev = tmp;
                resph.exception = tmp;
            }
            this.RespHandlerList.remove(resph);
        }
    }
    
    private void close(final ResponseHandler resph) {
        synchronized (this.RespHandlerList) {
            if (resph != this.RespHandlerList.getFirst()) {
                monitorexit(this.RespHandlerList);
                return;
            }
            Log.write(4, "Demux: Closing stream " + resph.stream.hashCode());
            resph.eof = true;
            this.RespHandlerList.remove(resph);
            monitorexit(this.RespHandlerList);
        }
        if (resph == this.MarkedForClose) {
            this.close(new IOException("Premature end of Keep-Alive"), false);
        }
        else {
            this.closeSocketIfAllStreamsClosed();
        }
    }
    
    synchronized void closeSocketIfAllStreamsClosed() {
        synchronized (this.RespHandlerList) {
            for (ResponseHandler resph = (ResponseHandler)this.RespHandlerList.enumerate(); resph != null && resph.stream.closed; resph = (ResponseHandler)this.RespHandlerList.next()) {
                if (resph == this.MarkedForClose) {
                    ResponseHandler tmp;
                    do {
                        tmp = (ResponseHandler)this.RespHandlerList.getFirst();
                        this.RespHandlerList.remove(tmp);
                    } while (tmp != resph);
                    this.close(new IOException("Premature end of Keep-Alive"), false);
                    monitorexit(this.RespHandlerList);
                    return;
                }
            }
            monitorexit(this.RespHandlerList);
        }
    }
    
    synchronized Socket getSocket() {
        if (this.MarkedForClose != null) {
            return null;
        }
        if (this.Timer != null) {
            this.Timer.hyber();
        }
        return this.Sock;
    }
    
    synchronized void markForClose(final Response resp) {
        synchronized (this.RespHandlerList) {
            if (this.RespHandlerList.getFirst() == null) {
                this.close(new IOException("Premature end of Keep-Alive"), false);
                monitorexit(this.RespHandlerList);
                return;
            }
            if (this.Timer != null) {
                this.Timer.kill();
                this.Timer = null;
            }
            ResponseHandler lasth = null;
            for (ResponseHandler resph = (ResponseHandler)this.RespHandlerList.enumerate(); resph != null; resph = (ResponseHandler)this.RespHandlerList.next()) {
                if (resph.resp == resp) {
                    this.MarkedForClose = resph;
                    Log.write(4, "Demux: stream " + resp.inp_stream.hashCode() + " marked for close");
                    this.closeSocketIfAllStreamsClosed();
                    monitorexit(this.RespHandlerList);
                    return;
                }
                if (this.MarkedForClose == resph) {
                    monitorexit(this.RespHandlerList);
                    return;
                }
                lasth = resph;
            }
            if (lasth == null) {
                monitorexit(this.RespHandlerList);
                return;
            }
            this.MarkedForClose = lasth;
            this.closeSocketIfAllStreamsClosed();
            Log.write(4, "Demux: stream " + lasth.stream.hashCode() + " marked for close");
            monitorexit(this.RespHandlerList);
        }
    }
    
    void abort() {
        Log.write(4, "Demux: Aborting socket (" + this.hashCode() + ")");
        synchronized (this.RespHandlerList) {
            for (ResponseHandler resph = (ResponseHandler)this.RespHandlerList.enumerate(); resph != null; resph = (ResponseHandler)this.RespHandlerList.next()) {
                if (resph.resp.http_resp != null) {
                    resph.resp.http_resp.markAborted();
                }
                if (resph.exception == null) {
                    resph.exception = new IOException("Request aborted by user");
                }
            }
            if (this.Sock != null) {
                try {
                    try {
                        this.Sock.setSoLinger(false, 0);
                    }
                    catch (final SocketException ex) {}
                    try {
                        this.Stream.close();
                    }
                    catch (final IOException ex2) {}
                    try {
                        this.Sock.close();
                    }
                    catch (final IOException ex3) {}
                    this.Sock = null;
                    if (this.Timer != null) {
                        this.Timer.kill();
                        this.Timer = null;
                    }
                }
                catch (final NullPointerException ex4) {}
                this.Connection.DemuxList.remove(this);
            }
            monitorexit(this.RespHandlerList);
        }
    }
    
    protected void finalize() throws Throwable {
        this.close(null, false);
        super.finalize();
    }
    
    public String toString() {
        String prot = null;
        switch (this.Protocol) {
            case 0: {
                prot = "HTTP";
                break;
            }
            case 1: {
                prot = "HTTPS";
                break;
            }
            case 2: {
                prot = "SHTTP";
                break;
            }
            case 3: {
                prot = "HTTP_NG";
                break;
            }
            default: {
                throw new Error("HTTPClient Internal Error: invalid protocol " + this.Protocol);
            }
        }
        return String.valueOf(this.getClass().getName()) + "[Protocol=" + prot + "]";
    }
    
    static {
        StreamDemultiplexor.TimerThread = null;
        (StreamDemultiplexor.TimerThread = new SocketTimeout(60)).start();
        StreamDemultiplexor.cleanup = new Object() {
            private final SocketTimeout timer = StreamDemultiplexor.TimerThread;
            
            protected void finalize() {
                this.timer.kill();
            }
        };
    }
}
