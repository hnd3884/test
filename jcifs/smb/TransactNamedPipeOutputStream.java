package jcifs.smb;

import java.io.IOException;
import java.io.OutputStream;

class TransactNamedPipeOutputStream extends OutputStream
{
    private String path;
    private SmbNamedPipe pipe;
    private byte[] tmp;
    private boolean dcePipe;
    
    TransactNamedPipeOutputStream(final SmbNamedPipe pipe) throws IOException {
        this.tmp = new byte[1];
        this.pipe = pipe;
        this.dcePipe = ((pipe.pipeType & 0x600) == 0x600);
        this.path = pipe.unc;
    }
    
    public void close() throws IOException {
        this.pipe.close();
    }
    
    public void write(final int b) throws IOException {
        this.tmp[0] = (byte)b;
        this.write(this.tmp, 0, 1);
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] b, final int off, int len) throws IOException {
        if (len < 0) {
            len = 0;
        }
        if ((this.pipe.pipeType & 0x100) == 0x100) {
            this.pipe.send(new TransWaitNamedPipe(this.path), new TransWaitNamedPipeResponse());
            this.pipe.send(new TransCallNamedPipe(this.path, b, off, len), new TransCallNamedPipeResponse(this.pipe));
        }
        else if ((this.pipe.pipeType & 0x200) == 0x200) {
            this.pipe.open((this.pipe.pipeType & 0xFF) | 0x20, this.pipe.pipeType >>> 16, 128, 0);
            final TransTransactNamedPipe req = new TransTransactNamedPipe(this.pipe.fid, b, off, len);
            if (this.dcePipe) {
                req.maxDataCount = 1024;
            }
            this.pipe.send(req, new TransTransactNamedPipeResponse(this.pipe));
        }
    }
}
