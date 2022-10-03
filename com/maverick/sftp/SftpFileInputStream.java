package com.maverick.sftp;

import com.maverick.util.UnsignedInteger32;
import com.maverick.ssh.SshIOException;
import java.io.IOException;
import com.maverick.ssh.SshException;
import java.util.Vector;
import java.io.InputStream;

public class SftpFileInputStream extends InputStream
{
    SftpFile g;
    SftpSubsystemChannel f;
    long c;
    Vector b;
    SftpMessage h;
    int e;
    long d;
    boolean i;
    
    public SftpFileInputStream(final SftpFile sftpFile) throws SftpStatusException, SshException {
        this(sftpFile, 0L);
    }
    
    public SftpFileInputStream(final SftpFile g, final long c) throws SftpStatusException, SshException {
        this.b = new Vector();
        this.i = false;
        if (g.getHandle() == null) {
            throw new SftpStatusException(100, "The file does not have a valid handle!");
        }
        if (g.getSFTPChannel() == null) {
            throw new SshException("The file is not attached to an SFTP subsystem!", 4);
        }
        this.g = g;
        this.c = c;
        this.f = g.getSFTPChannel();
        this.d = g.getAttributes().getSize().longValue() - c;
        try {
            this.c();
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    public int read(final byte[] array, int n, int n2) throws IOException {
        try {
            if (this.i && this.e == 0) {
                return -1;
            }
            int n3;
            int min;
            for (n3 = 0; n3 < n2 && !this.i; n3 += min, n2 -= min, n += min) {
                if (this.h == null || this.e == 0) {
                    this.c();
                    if (this.i && n3 == 0) {
                        return -1;
                    }
                }
                if (this.h == null) {
                    throw new IOException("Failed to obtain file data or status from the SFTP server!");
                }
                min = Math.min(this.e, n2);
                System.arraycopy(this.h.array(), this.h.getPosition(), array, n, min);
                this.e -= min;
                this.h.skip(min);
                if (this.e == 0) {
                    this.c();
                }
            }
            this.d -= n3;
            return n3;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
        catch (final SftpStatusException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    private void c() throws SshException, IOException, SftpStatusException {
        this.b();
        this.h = this.f.c(this.b.elementAt(0));
        this.b.removeElementAt(0);
        if (this.h.getType() == 103) {
            this.e = (int)this.h.readInt();
            return;
        }
        if (this.h.getType() != 101) {
            this.close();
            throw new IOException("The server responded with an unexpected SFTP protocol message! type=" + this.h.getType());
        }
        final int n = (int)this.h.readInt();
        if (n == 1) {
            this.i = true;
            return;
        }
        if (this.f.getVersion() >= 3) {
            throw new IOException(this.h.readString().trim());
        }
        throw new IOException("Unexpected status " + n);
    }
    
    private void b() throws SftpStatusException, SshException {
        while (this.b.size() < 100) {
            this.b.addElement(this.f.postReadRequest(this.g.getHandle(), this.c, 32768));
            this.c += 32768L;
        }
    }
    
    public int available() {
        return this.e;
    }
    
    public int read() throws IOException {
        final byte[] array = { 0 };
        if (this.read(array) == 1) {
            return array[0] & 0xFF;
        }
        return -1;
    }
    
    public void close() throws IOException {
        try {
            this.g.close();
            while (this.b.size() > 0) {
                final UnsignedInteger32 unsignedInteger32 = this.b.elementAt(0);
                this.b.removeElementAt(0);
                this.f.c(unsignedInteger32);
            }
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
        catch (final SftpStatusException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    protected void finalize() throws IOException {
        if (this.g.getHandle() != null) {
            this.close();
        }
    }
}
