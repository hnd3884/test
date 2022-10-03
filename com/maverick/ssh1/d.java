package com.maverick.ssh1;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshContext;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.SshTunnel;

class d extends e implements SshTunnel
{
    String ie;
    int be;
    String de;
    int fe;
    String ee;
    int ge;
    int he;
    SshTransport ce;
    SshContext ae;
    
    d(final SshContext ae, final String ie, final int be, final String de, final int fe, final String ee, final int ge, final int he, final SshTransport ce) throws SshException {
        this.ae = ae;
        this.ie = ie;
        this.be = be;
        this.de = de;
        this.fe = fe;
        this.ee = ee;
        this.ge = ge;
        this.he = he;
        if (this.isX11()) {
            this.ce = new _b(ce);
        }
        else {
            this.ce = ce;
        }
    }
    
    public String getHost() {
        return this.ie;
    }
    
    public int getPort() {
        return this.be;
    }
    
    public String getListeningAddress() {
        return this.de;
    }
    
    public int getListeningPort() {
        return this.fe;
    }
    
    public String getOriginatingHost() {
        return this.ee;
    }
    
    public int getOriginatingPort() {
        return this.ge;
    }
    
    public boolean isLocal() {
        return this.he == 1;
    }
    
    public boolean isX11() {
        return this.he == 3;
    }
    
    public SshTransport getTransport() {
        return this.ce;
    }
    
    public boolean isLocalEOF() {
        return this.isClosed();
    }
    
    public boolean isRemoteEOF() {
        return this.isClosed();
    }
    
    public SshTransport duplicate() throws IOException {
        throw new SshIOException(new SshException("SSH tunnels cannot be duplicated!", 4));
    }
    
    class _b implements SshTransport
    {
        SshTransport gb;
        _c fb;
        
        _b(final SshTransport gb) throws SshException {
            try {
                this.gb = gb;
                this.fb = new _c(gb.getOutputStream());
            }
            catch (final IOException ex) {
                throw new SshException(ex);
            }
        }
        
        public InputStream getInputStream() throws IOException {
            return this.gb.getInputStream();
        }
        
        public OutputStream getOutputStream() {
            return this.fb;
        }
        
        public String getHost() {
            return this.gb.getHost();
        }
        
        public int getPort() {
            return this.gb.getPort();
        }
        
        public SshTransport duplicate() throws IOException {
            return this.gb.duplicate();
        }
        
        public void close() throws IOException {
            this.gb.close();
        }
    }
    
    class _c extends OutputStream
    {
        byte[] f;
        boolean d;
        int c;
        int b;
        int h;
        int g;
        OutputStream e;
        
        _c(final OutputStream e) {
            this.f = new byte[1024];
            this.d = false;
            this.c = 0;
            this.b = 12;
            this.e = e;
        }
        
        public void write(final int n) throws IOException {
            this.write(new byte[] { (byte)n }, 0, 1);
        }
        
        public void write(final byte[] array, int n, int n2) throws IOException {
            try {
                if (com.maverick.ssh1.d.this.isX11() && !this.d) {
                    if (this.c < 12) {
                        final int b = this.b(array, n, n2);
                        n2 -= b;
                        n += b;
                        if (this.b == 0) {
                            if (this.f[0] == 66) {
                                this.h = ((this.f[6] & 0xFF) << 8 | (this.f[7] & 0xFF));
                                this.g = ((this.f[8] & 0xFF) << 8 | (this.f[9] & 0xFF));
                            }
                            else {
                                if (this.f[0] != 108) {
                                    throw new SshIOException(new SshException("Corrupt X11 authentication packet", 3));
                                }
                                this.h = ((this.f[7] & 0xFF) << 8 | (this.f[6] & 0xFF));
                                this.g = ((this.f[9] & 0xFF) << 8 | (this.f[8] & 0xFF));
                            }
                            this.b = (this.h + 3 & 0xFFFFFFFC);
                            this.b += (this.g + 3 & 0xFFFFFFFC);
                            if (this.b + this.c > this.f.length) {
                                throw new SshIOException(new SshException("Corrupt X11 authentication packet", 3));
                            }
                            if (this.b == 0) {
                                throw new SshIOException(new SshException("X11 authentication cookie not found", 3));
                            }
                        }
                    }
                    if (n2 > 0) {
                        final int b2 = this.b(array, n, n2);
                        n2 -= b2;
                        n += b2;
                        if (this.b == 0) {
                            final byte[] x11AuthenticationCookie = com.maverick.ssh1.d.this.ae.getX11AuthenticationCookie();
                            final String s = new String(this.f, 12, this.h);
                            final byte[] array2 = new byte[x11AuthenticationCookie.length];
                            this.h = (this.h + 3 & 0xFFFFFFFC);
                            System.arraycopy(this.f, 12 + this.h, array2, 0, x11AuthenticationCookie.length);
                            if (!"MIT-MAGIC-COOKIE-1".equals(s) || !this.b(x11AuthenticationCookie, array2, x11AuthenticationCookie.length)) {
                                throw new SshIOException(new SshException("Incorrect X11 cookie", 3));
                            }
                            final byte[] x11RealCookie = com.maverick.ssh1.d.this.ae.getX11RealCookie();
                            if (x11RealCookie.length != this.g) {
                                throw new SshIOException(new SshException("Invalid X11 cookie", 3));
                            }
                            System.arraycopy(x11RealCookie, 0, this.f, 12 + this.h, x11RealCookie.length);
                            this.d = true;
                            this.e.write(this.f, 0, this.c);
                            this.f = null;
                        }
                    }
                    if (!this.d || n2 == 0) {
                        return;
                    }
                }
                this.e.write(array, n, n2);
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
        }
        
        private boolean b(final byte[] array, final byte[] array2, final int n) {
            int n2;
            for (n2 = 0; n2 < n && array[n2] == array2[n2]; ++n2) {}
            return n2 == n;
        }
        
        private int b(final byte[] array, final int n, int b) {
            if (b > this.b) {
                System.arraycopy(array, n, this.f, this.c, this.b);
                this.c += this.b;
                b = this.b;
                this.b = 0;
            }
            else {
                System.arraycopy(array, n, this.f, this.c, b);
                this.c += b;
                this.b -= b;
            }
            return b;
        }
    }
}
