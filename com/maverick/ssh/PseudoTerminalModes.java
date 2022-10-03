package com.maverick.ssh;

import java.io.IOException;
import com.maverick.util.ByteArrayWriter;

public class PseudoTerminalModes
{
    public static final int VINTR = 1;
    public static final int VQUIT = 2;
    public static final int VERASE = 3;
    public static final int VKILL = 4;
    public static final int VEOF = 5;
    public static final int VEOL = 6;
    public static final int VEOL2 = 7;
    public static final int VSTART = 8;
    public static final int VSTOP = 9;
    public static final int VSUSP = 10;
    public static final int VDSUSP = 11;
    public static final int VREPRINT = 12;
    public static final int VWERASE = 13;
    public static final int VLNEXT = 14;
    public static final int VFLUSH = 15;
    public static final int VSWITCH = 16;
    public static final int VSTATUS = 17;
    public static final int VDISCARD = 18;
    public static final int IGNPAR = 30;
    public static final int PARMRK = 31;
    public static final int INPCK = 32;
    public static final int ISTRIP = 33;
    public static final int INLCR = 34;
    public static final int IGNCR = 35;
    public static final int ICRNL = 36;
    public static final int IUCLC = 37;
    public static final int IXON = 38;
    public static final int IXANY = 39;
    public static final int IXOFF = 40;
    public static final int IMAXBEL = 41;
    public static final int ISIG = 50;
    public static final int ICANON = 51;
    public static final int XCASE = 52;
    public static final int ECHO = 53;
    public static final int ECHOE = 54;
    public static final int ECHOK = 55;
    public static final int ECHONL = 56;
    public static final int NOFLSH = 57;
    public static final int TOSTOP = 58;
    public static final int IEXTEN = 59;
    public static final int ECHOCTL = 60;
    public static final int ECHOKE = 61;
    public static final int PENDIN = 62;
    public static final int OPOST = 70;
    public static final int OLCUC = 71;
    public static final int ONLCR = 72;
    public static final int OCRNL = 73;
    public static final int ONOCR = 74;
    public static final int ONLRET = 75;
    public static final int CS7 = 90;
    public static final int CS8 = 91;
    public static final int PARENB = 92;
    public static final int PARODD = 93;
    public static final int TTY_OP_ISPEED = 128;
    public static final int TTY_OP_OSPEED = 129;
    ByteArrayWriter c;
    int d;
    byte[] b;
    
    public PseudoTerminalModes(final SshClient sshClient) {
        this.c = new ByteArrayWriter();
        this.d = sshClient.getVersion();
    }
    
    public void reset() {
        this.b = null;
        this.c.reset();
    }
    
    public void setTerminalMode(final int n, final int n2) throws SshException {
        try {
            this.c.write(n);
            if (this.d == 1 && n <= 127) {
                this.c.write(n2);
            }
            else {
                this.c.writeInt(n2);
            }
        }
        catch (final IOException ex) {
            throw new SshException(5, ex);
        }
    }
    
    public void setTerminalMode(final int n, final boolean b) throws SshException {
        this.setTerminalMode(n, b ? 1 : 0);
    }
    
    public byte[] toByteArray() {
        if (this.b == null) {
            this.c.write(0);
            return this.b = this.c.toByteArray();
        }
        return this.b;
    }
}
