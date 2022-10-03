package jcifs.dcerpc;

import jcifs.util.Encdec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import jcifs.smb.NtlmPasswordAuthentication;
import java.io.OutputStream;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbNamedPipe;

public class DcerpcPipeHandle extends DcerpcHandle
{
    SmbNamedPipe pipe;
    SmbFileInputStream in;
    OutputStream out;
    boolean isStart;
    
    public DcerpcPipeHandle(String url, final NtlmPasswordAuthentication auth) throws UnknownHostException, MalformedURLException, DcerpcException {
        this.out = null;
        this.isStart = true;
        this.binding = DcerpcHandle.parseBinding(url);
        url = "smb://" + this.binding.server + "/IPC$/" + this.binding.endpoint.substring(6);
        this.pipe = new SmbNamedPipe(url, 27198979, auth);
    }
    
    protected void doSendFragment(final byte[] buf, final int off, final int length) throws IOException {
        this.in = (SmbFileInputStream)this.pipe.getNamedPipeInputStream();
        (this.out = this.pipe.getNamedPipeOutputStream()).write(buf, off, length);
    }
    
    protected void doReceiveFragment(final byte[] buf) throws IOException {
        if (buf.length < this.max_recv) {
            throw new IllegalArgumentException("buffer too small");
        }
        int off;
        if (this.isStart) {
            off = this.in.read(buf, 0, 1024);
        }
        else {
            off = this.in.readDirect(buf, 0, buf.length);
        }
        if (buf[0] != 5 && buf[1] != 0) {
            throw new IOException("Unexpected DCERPC PDU header");
        }
        final int flags = buf[3] & 0xFF;
        this.isStart = ((flags & 0x2) == 0x2);
        final int length = Encdec.dec_uint16le(buf, 8);
        if (length > this.max_recv) {
            throw new IOException("Unexpected fragment length: " + length);
        }
        while (off < length) {
            off += this.in.readDirect(buf, off, length - off);
        }
    }
    
    public void close() throws IOException {
        if (this.out != null) {
            this.out.close();
        }
    }
}
