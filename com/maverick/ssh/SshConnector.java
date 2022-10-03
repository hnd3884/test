package com.maverick.ssh;

import java.io.InputStream;
import java.util.Date;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import com.maverick.events.EventServiceImplementation;
import com.maverick.events.EventListener;

public final class SshConnector
{
    public static final int SSH1 = 1;
    public static final int SSH2 = 2;
    static b e;
    String d;
    String g;
    SshContext b;
    SshContext i;
    int c;
    String h;
    public static Throwable initException;
    boolean f;
    
    SshConnector() {
        this.d = "J2SSH_Maverick_1.4.48_";
        this.g = "J2SSH_Maverick_1.4.48_";
        this.c = 0;
        this.h = "J2SSH";
        this.f = false;
        try {
            this.b = (SshContext)Class.forName("com.maverick.ssh1.Ssh1Context").newInstance();
            this.c |= 0x1;
        }
        catch (final Throwable t) {}
        try {
            this.i = (SshContext)Class.forName("com.maverick.ssh2.Ssh2Context").newInstance();
            this.c |= 0x2;
        }
        catch (final Throwable initException) {
            SshConnector.initException = initException;
        }
        this.d += ((SshConnector.e.c() == null) ? "" : ("_" + SshConnector.e.c()));
    }
    
    public static SshConnector getInstance() throws SshException {
        switch (SshConnector.e.d() & 0x1F) {
            case 4: {
                return new SshConnector();
            }
            case 2: {
                throw new SshException("Your license is invalid!", 11);
            }
            case 1: {
                throw new SshException("Your license has expired! visit http://www.sshtools.com to purchase a license", 11);
            }
            case 8: {
                throw new SshException("This copy of J2SSH Maverick is not licensed!", 11);
            }
            case 16: {
                throw new SshException("Your subscription has expired! visit http://www.sshtools.com to purchase a subscription", 11);
            }
            default: {
                throw new SshException("Unexpected license status!", 11);
            }
        }
    }
    
    public static void addEventListener(final EventListener eventListener) {
        EventServiceImplementation.getInstance().addListener("", eventListener);
    }
    
    public static void addEventListener(final String s, final EventListener eventListener) {
        EventServiceImplementation.getInstance().addListener(s, eventListener);
    }
    
    public static void removeEventListener(final String s) {
        EventServiceImplementation.getInstance().removeListener(s);
    }
    
    public final boolean isLicensed() {
        return (SshConnector.e.f() & 0x4) != 0x0;
    }
    
    public final void enableFIPSMode() throws SshException {
        this.b.enableFIPSMode();
        this.i.enableFIPSMode();
        this.f = true;
    }
    
    static void b(final String s) {
        SshConnector.e.b(s);
    }
    
    public SshContext getContext(final int n) throws SshException {
        if ((n & 0x1) == 0x0 && (n & 0x2) == 0x0) {
            throw new SshException("SshContext.getContext(int) requires value of either SSH1 or SSH2", 4);
        }
        if (n == 1 && (this.c & 0x1) != 0x0) {
            return this.b;
        }
        if (n == 2 && (this.c & 0x2) != 0x0) {
            return this.i;
        }
        throw new SshException(((n == 1) ? "SSH1" : "SSH2") + " context is not available because it is not supported by this configuration", 4);
    }
    
    public void setSupportedVersions(final int c) throws SshException {
        if ((c & 0x1) != 0x0 && this.b == null) {
            throw new SshException("SSH1 protocol support is not installed!", 4);
        }
        if ((c & 0x2) != 0x0 && this.i == null) {
            throw new SshException("SSH2 protocol support is not installed!" + SshConnector.initException.getMessage() + SshConnector.initException.getClass(), 4);
        }
        if ((c & 0x1) == 0x0 && (c & 0x2) == 0x0) {
            throw new SshException("You must specify at least one supported version of the SSH protocol!", 4);
        }
        this.c = c;
    }
    
    public int getSupportedVersions() {
        return this.c;
    }
    
    public void setKnownHosts(final HostKeyVerification hostKeyVerification) {
        if ((this.c & 0x1) != 0x0 && this.b != null) {
            this.b.setHostKeyVerification(hostKeyVerification);
        }
        if ((this.c & 0x2) != 0x0 && this.i != null) {
            this.i.setHostKeyVerification(hostKeyVerification);
        }
    }
    
    public SshClient connect(final SshTransport sshTransport, final String s) throws SshException {
        return this.connect(sshTransport, s, false, null);
    }
    
    public SshClient connect(final SshTransport sshTransport, final String s, final boolean b) throws SshException {
        return this.connect(sshTransport, s, b, null);
    }
    
    public SshClient connect(final SshTransport sshTransport, final String s, final SshContext sshContext) throws SshException {
        return this.connect(sshTransport, s, false, sshContext);
    }
    
    public void setSoftwareVersionComments(final String d) {
        this.d = d;
    }
    
    public SshClient connect(SshTransport duplicate, final String s, final boolean b, final SshContext sshContext) throws SshException {
        if (SshConnector.e.b() == 65536 && duplicate.getHost() != "127.0.0.1" && duplicate.getHost() != "0:0:0:0:0:0:0:1" && duplicate.getHost() != "::1" && duplicate.getHost() != "localhost") {
            final String g = SshConnector.e.g();
            StringTokenizer stringTokenizer;
            int n;
            String trim;
            for (stringTokenizer = new StringTokenizer(g, ","), n = 0; stringTokenizer.hasMoreTokens() && n == 0; n = 1) {
                trim = stringTokenizer.nextToken().trim();
                if ((trim.startsWith("*.") && duplicate.getHost().endsWith(trim.substring(2))) || trim.equalsIgnoreCase(duplicate.getHost())) {}
            }
            if (n == 0) {
                throw new SshException("You are not licensed to connect to " + duplicate.getHost() + " [VALID HOSTS " + g + "]", 11);
            }
        }
        if (SshConnector.e.b() == 131072) {
            final String g2 = SshConnector.e.g();
            final StringTokenizer stringTokenizer2 = new StringTokenizer(g2, ",");
            boolean equals = false;
            Socket socket = null;
            try {
                Label_0375: {
                    if (!Socket.class.isAssignableFrom(duplicate.getClass())) {
                        if (duplicate.getClass().isAssignableFrom(Class.forName("com.sshtools.net.SocketWrapper"))) {
                            try {
                                socket = (Socket)duplicate.getClass().getMethod("getSocket", (Class<?>[])null).invoke(duplicate, (Object[])null);
                                break Label_0375;
                            }
                            catch (final Exception ex) {
                                throw new SshException("Error attempting to determine localhost for licensing: " + ex.getMessage(), 11);
                            }
                        }
                        throw new SshException("You are not licensed to connect using non-socket transports", 11);
                    }
                    socket = (Socket)duplicate;
                }
            }
            catch (final ClassNotFoundException ex2) {
                throw new SshException("Error attempting to determine localhost for licensing: " + ex2.getMessage(), 11);
            }
            if (socket.getLocalAddress().getHostAddress() != "127.0.0.1" && socket.getLocalAddress().getHostAddress() != "0:0:0:0:0:0:0:1" && socket.getLocalAddress().getHostAddress() != "::1") {
                while (stringTokenizer2.hasMoreTokens() && !equals) {
                    equals = socket.getLocalAddress().getHostAddress().equals(stringTokenizer2.nextToken());
                }
                if (!equals) {
                    throw new SshException("You are not licensed to connect through " + socket.getLocalAddress().getHostAddress() + " [VALID HOSTS " + g2 + "]", 11);
                }
            }
        }
        Throwable t = null;
        String s2 = null;
        if ((this.c & 0x2) != 0x0 && (this.i != null || (sshContext != null && sshContext.getClass().getName().equals("com.maverick.ssh2.Ssh2Context")))) {
            String s3 = "SSH-2.0-" + this.d.replace(' ', '_');
            if (s3.length() > 253) {
                s3 = s3.substring(0, 253);
            }
            final String string = s3 + "\r\n";
            try {
                duplicate.getOutputStream().write(string.getBytes());
            }
            catch (final Throwable t2) {
                t = t2;
            }
            s2 = this.b(duplicate);
            if ((this.c(s2) & 0x2) != 0x0) {
                try {
                    final SshClient sshClient = (SshClient)Class.forName("com.maverick.ssh2.Ssh2Client").newInstance();
                    sshClient.connect(duplicate, (this.i == null) ? sshContext : this.i, this, s, string.trim(), s2, b);
                    return sshClient;
                }
                catch (final Throwable t3) {
                    t = t3;
                }
                finally {
                    if (t != null) {
                        if (t instanceof SshException) {
                            throw (SshException)t;
                        }
                        throw new SshException((t.getMessage() != null) ? t.getMessage() : t.getClass().getName(), 10, t);
                    }
                }
            }
            else {
                try {
                    duplicate.close();
                }
                catch (final IOException ex3) {}
                if (SshConnector.e.b() == 262144) {
                    throw new SshException("Failed to negotiate a version with the server! SSH1 is not supported by your license", 10);
                }
                try {
                    duplicate = duplicate.duplicate();
                }
                catch (final IOException ex4) {
                    throw new SshException("Failed to duplicate transport for SSH1 attempt", ex4);
                }
            }
        }
        if (SshConnector.e.b() == 262144) {
            throw new SshException("Failed to negotiate a version with the server! SSH1 is not supported by your license", 10);
        }
        try {
            if (this.f) {
                throw new SshException("Could not connect using SSH2 and FIPS mode is enabled so will not try SSH1", 10);
            }
            final String string2 = "SSH-1.5-" + this.d.replace(' ', '_') + "\n";
            duplicate.getOutputStream().write(string2.getBytes());
            s2 = this.b(duplicate);
            if ((this.b != null || (sshContext != null && sshContext.getClass().getName().equals("com.maverick.ssh1.Ssh1Context"))) && (this.c & 0x1) != 0x0 && (this.c(s2) & 0x1) != 0x0) {
                final SshClient sshClient2 = (SshClient)Class.forName("com.maverick.ssh1.Ssh1Client").newInstance();
                if (this.d.length() > 40) {
                    this.d = this.d.substring(0, 40);
                }
                sshClient2.connect(duplicate, (this.b == null) ? sshContext : this.b, this, s, string2.trim(), s2.trim(), b);
                return sshClient2;
            }
        }
        catch (final Throwable t4) {
            t = t4;
        }
        try {
            duplicate.close();
        }
        catch (final IOException ex5) {}
        if (t == null) {
            throw new SshException("Failed to negotiate a version with the server! supported=" + this.getSupportedVersions() + " id=" + ((s2 == null) ? "" : s2), 10);
        }
        if (t instanceof SshException) {
            throw (SshException)t;
        }
        throw new SshException((t.getMessage() != null) ? t.getMessage() : t.getClass().getName(), 10);
    }
    
    public static String getVersion() {
        return "1.4.48";
    }
    
    public static Date getReleaseDate() {
        return new Date(1374169522052L);
    }
    
    public int determineVersion(final SshTransport sshTransport) throws SshException {
        final int c = this.c(this.b(sshTransport));
        try {
            sshTransport.close();
        }
        catch (final IOException ex) {}
        return c;
    }
    
    String b(final SshTransport sshTransport) throws SshException {
        try {
            String string = "";
            final InputStream inputStream = sshTransport.getInputStream();
            final int n = 255;
            while (!string.startsWith("SSH-")) {
                final StringBuffer sb = new StringBuffer(n);
                int read;
                while ((read = inputStream.read()) != 10 && sb.length() < n && read > -1) {
                    if (read == 13) {
                        continue;
                    }
                    sb.append((char)read);
                }
                if (read == -1) {
                    throw new SshException("Failed to read remote identification " + sb.toString(), 10);
                }
                string = sb.toString();
            }
            return string;
        }
        catch (final Throwable t) {
            throw new SshException(t, 10);
        }
    }
    
    int c(final String s) throws SshException {
        final int index = s.indexOf("-");
        final String substring = s.substring(index + 1, s.indexOf("-", index + 1));
        if (substring.equals("2.0")) {
            return 2;
        }
        if (substring.equals("1.99")) {
            return 3;
        }
        if (substring.equals("1.5")) {
            return 1;
        }
        if (substring.equals("2.99")) {
            return 2;
        }
        throw new SshException("Unsupported version " + substring + " detected!", 10);
    }
    
    public String getProduct() {
        return this.h;
    }
    
    public void setProduct(final String h) {
        this.h = h;
    }
    
    static {
        SshConnector.e = new b();
        SshConnector.initException = null;
    }
}
