package com.maverick.sftp;

import com.maverick.util.UnsignedInteger32;
import java.io.IOException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.SshException;
import java.util.Vector;
import java.io.OutputStream;

public class SftpFileOutputStream extends OutputStream
{
    SftpFile e;
    SftpSubsystemChannel d;
    long c;
    Vector b;
    
    public SftpFileOutputStream(final SftpFile e) throws SftpStatusException, SshException {
        this.b = new Vector();
        if (e.getHandle() == null) {
            throw new SftpStatusException(100, "The file does not have a valid handle!");
        }
        if (e.getSFTPChannel() == null) {
            throw new SshException("The file is not attached to an SFTP subsystem!", 4);
        }
        this.e = e;
        this.d = e.getSFTPChannel();
    }
    
    public void write(final byte[] array, int n, int i) throws IOException {
        try {
            while (i > 0) {
                final int min = Math.min(32768, i);
                this.b.addElement(this.d.postWriteRequest(this.e.getHandle(), this.c, array, n, min));
                this.b(100);
                n += min;
                i -= min;
                this.c += min;
            }
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
        catch (final SftpStatusException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    public void write(final int n) throws IOException {
        try {
            this.b.addElement(this.d.postWriteRequest(this.e.getHandle(), this.c, new byte[] { (byte)n }, 0, 1));
            this.b(100);
            ++this.c;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
        catch (final SftpStatusException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    private boolean b(final int n) throws SftpStatusException, SshException {
        if (this.b.size() > n) {
            this.d.getOKRequestStatus(this.b.elementAt(0));
            this.b.removeElementAt(0);
        }
        return this.b.size() > 0;
    }
    
    public void close() throws IOException {
        try {
            while (this.b(0)) {}
            this.e.close();
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
        catch (final SftpStatusException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    protected void finalize() throws IOException {
        if (this.e.getHandle() != null) {
            this.close();
        }
    }
}
