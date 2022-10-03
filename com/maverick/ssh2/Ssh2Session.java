package com.maverick.ssh2;

import com.maverick.util.ByteArrayReader;
import com.maverick.events.Event;
import com.maverick.events.EventServiceImplementation;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.PseudoTerminalModes;
import java.io.InputStream;
import com.maverick.ssh.SshException;
import com.maverick.ssh.message.SshChannelMessage;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshSession;

public class Ssh2Session extends Ssh2Channel implements SshSession
{
    _d pd;
    boolean od;
    int qd;
    String nd;
    Ssh2Client md;
    
    public Ssh2Session(final int n, final int n2, final Ssh2Client md) {
        super("session", n, n2);
        this.od = false;
        this.qd = Integer.MIN_VALUE;
        this.nd = "";
        this.md = md;
        this.pd = this.createExtendedDataStream();
    }
    
    public SshClient getClient() {
        return this.md;
    }
    
    protected void processExtendedData(final int n, final int n2, final SshChannelMessage sshChannelMessage) throws SshException {
        super.processExtendedData(n, n2, sshChannelMessage);
        if (n == 1) {
            this.pd.b(n2, sshChannelMessage);
        }
    }
    
    public InputStream getStderrInputStream() {
        return this.pd;
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4) throws SshException {
        return this.requestPseudoTerminal(s, n, n2, n3, n4, new byte[] { 0 });
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4, final PseudoTerminalModes pseudoTerminalModes) throws SshException {
        return this.requestPseudoTerminal(s, n, n2, n3, n4, pseudoTerminalModes.toByteArray());
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4, final byte[] array) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeInt(n2);
            byteArrayWriter.writeInt(n3);
            byteArrayWriter.writeInt(n4);
            byteArrayWriter.writeBinaryString(array);
            return this.sendRequest("pty-req", true, byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean startShell() throws SshException {
        final boolean sendRequest = this.sendRequest("shell", true, null);
        if (sendRequest) {
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 23, true));
        }
        else {
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 24, false));
        }
        super.wb = true;
        return sendRequest;
    }
    
    public boolean executeCommand(final String s) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            final boolean sendRequest = this.sendRequest("exec", true, byteArrayWriter.toByteArray());
            if (sendRequest) {
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 30, true).addAttribute("COMMAND", s));
            }
            else {
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 30, false).addAttribute("COMMAND", s));
            }
            super.wb = true;
            return sendRequest;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean executeCommand(final String s, final String s2) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s, s2);
            final boolean sendRequest = this.sendRequest("exec", true, byteArrayWriter.toByteArray());
            if (sendRequest) {
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 30, true).addAttribute("COMMAND", s));
            }
            else {
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 30, false).addAttribute("COMMAND", s));
            }
            super.wb = true;
            return sendRequest;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean startSubsystem(final String s) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            final boolean sendRequest = this.sendRequest("subsystem", true, byteArrayWriter.toByteArray());
            if (sendRequest) {
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 1001, true).addAttribute("COMMAND", s));
            }
            else {
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 1001, false).addAttribute("COMMAND", s));
            }
            super.wb = true;
            return sendRequest;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    boolean b(final boolean b, final String s, final String s2, final int n) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeBoolean(b);
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeString(s2);
            byteArrayWriter.writeInt(n);
            return this.sendRequest("x11-req", true, byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean setEnvironmentVariable(final String s, final String s2) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeString(s2);
            return this.sendRequest("env", true, byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public void changeTerminalDimensions(final int n, final int n2, final int n3, final int n4) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeInt(n2);
            byteArrayWriter.writeInt(n4);
            byteArrayWriter.writeInt(n3);
            this.sendRequest("window-change", false, byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean isFlowControlEnabled() {
        return this.od;
    }
    
    public void signal(final String s) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            this.sendRequest("signal", false, byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    protected void channelRequest(final String s, final boolean b, final byte[] array) throws SshException {
        try {
            if (s.equals("exit-status") && array != null) {
                this.qd = (int)ByteArrayReader.readInt(array, 0);
            }
            if (s.equals("exit-signal") && array != null) {
                final ByteArrayReader byteArrayReader = new ByteArrayReader(array, 0, array.length);
                this.nd = "Signal=" + byteArrayReader.readString() + " CoreDump=" + String.valueOf(byteArrayReader.read() != 0) + " Message=" + byteArrayReader.readString();
            }
            if (s.equals("xon-xoff")) {
                this.od = (array != null && array[0] != 0);
            }
            super.channelRequest(s, b, array);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public int exitCode() {
        return this.qd;
    }
    
    protected void checkCloseStatus(boolean b) {
        if (!b) {
            try {
                if (super.ms.nextMessage(super.pb, Integer.parseInt(System.getProperty("maverick.remoteCloseTimeoutMs", "5000"))) != null) {
                    b = true;
                }
            }
            catch (final Exception ex) {}
        }
        super.checkCloseStatus(b);
    }
    
    public boolean hasExitSignal() {
        return !this.nd.equals("");
    }
    
    public String getExitSignalInfo() {
        return this.nd;
    }
}
