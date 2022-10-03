package jcifs.smb;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.io.OutputStream;
import java.io.InputStream;

public class SmbNamedPipe extends SmbFile
{
    public static final int PIPE_TYPE_RDONLY = 1;
    public static final int PIPE_TYPE_WRONLY = 2;
    public static final int PIPE_TYPE_RDWR = 3;
    public static final int PIPE_TYPE_CALL = 256;
    public static final int PIPE_TYPE_TRANSACT = 512;
    public static final int PIPE_TYPE_DCE_TRANSACT = 1536;
    InputStream pipeIn;
    OutputStream pipeOut;
    int pipeType;
    
    public SmbNamedPipe(final String url, final int pipeType) throws MalformedURLException, UnknownHostException {
        super(url);
        this.pipeType = pipeType;
        this.type = 16;
    }
    
    public SmbNamedPipe(final String url, final int pipeType, final NtlmPasswordAuthentication auth) throws MalformedURLException, UnknownHostException {
        super(url, auth);
        this.pipeType = pipeType;
        this.type = 16;
    }
    
    public SmbNamedPipe(final URL url, final int pipeType, final NtlmPasswordAuthentication auth) throws MalformedURLException, UnknownHostException {
        super(url, auth);
        this.pipeType = pipeType;
        this.type = 16;
    }
    
    public InputStream getNamedPipeInputStream() throws IOException {
        if (this.pipeIn == null) {
            if ((this.pipeType & 0x100) == 0x100 || (this.pipeType & 0x200) == 0x200) {
                this.pipeIn = new TransactNamedPipeInputStream(this);
            }
            else {
                this.pipeIn = new SmbFileInputStream(this, (this.pipeType & 0xFFFF00FF) | 0x20);
            }
        }
        return this.pipeIn;
    }
    
    public OutputStream getNamedPipeOutputStream() throws IOException {
        if (this.pipeOut == null) {
            if ((this.pipeType & 0x100) == 0x100 || (this.pipeType & 0x200) == 0x200) {
                this.pipeOut = new TransactNamedPipeOutputStream(this);
            }
            else {
                this.pipeOut = new SmbFileOutputStream(this, false, (this.pipeType & 0xFFFF00FF) | 0x20);
            }
        }
        return this.pipeOut;
    }
}
