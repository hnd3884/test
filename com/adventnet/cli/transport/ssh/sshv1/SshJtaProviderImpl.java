package com.adventnet.cli.transport.ssh.sshv1;

import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.transport.ssh.SshProtocolOptionsImpl;
import com.adventnet.cli.transport.CLIProtocolOptions;
import de.mud.telnet.ScriptHandler;
import java.net.SocketException;
import com.adventnet.cli.transport.LoginException;
import com.adventnet.cli.transport.ConnectException;
import com.adventnet.cli.util.CLILogMgr;
import java.awt.Dimension;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import de.mud.ssh.SshIO;
import com.adventnet.cli.transport.ssh.SshTransportProviderInterface;

public class SshJtaProviderImpl implements SshTransportProviderInterface
{
    private SshIO handler;
    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private String terminalType;
    private byte[] buffer;
    private int pos;
    
    public SshJtaProviderImpl() {
        this.terminalType = "xterm";
    }
    
    private void initHandler() {
        this.handler = new SshIO() {
            public String getTerminalType() {
                return SshJtaProviderImpl.this.terminalType;
            }
            
            public Dimension getWindowSize() {
                return new Dimension(-1, 25);
            }
            
            public void setLocalEcho(final boolean b) {
            }
            
            public void write(final byte[] array) throws IOException {
                SshJtaProviderImpl.this.out.write(array);
            }
        };
    }
    
    public void connect(final String s, final int n) throws ConnectException {
        try {
            this.initHandler();
            this.socket = new Socket(s, n);
            this.in = this.socket.getInputStream();
            this.out = this.socket.getOutputStream();
            CLILogMgr.setDebugMessage("CLIUSER", "SshJtaProviderImpl: connecting to " + s, 4, null);
        }
        catch (final Exception ex) {
            try {
                this.disconnect();
            }
            catch (final Exception ex2) {
                System.err.println(ex2.getMessage());
            }
            throw new ConnectException(ex.getMessage());
        }
    }
    
    public void disconnect() throws IOException {
        if (this.socket != null) {
            this.socket.close();
        }
        this.handler.disconnect();
        CLILogMgr.setDebugMessage("CLIUSER", "SshJtaProviderImpl: disconnecting", 4, null);
    }
    
    public void login(final String login, final String password) throws LoginException {
        this.handler.setLogin(login);
        this.handler.setPassword(password);
    }
    
    public String login(final String s, final String s2, final String s3) throws LoginException {
        String waitfor;
        try {
            this.login(s, s2);
            waitfor = this.waitfor(s3);
        }
        catch (final Exception ex) {
            throw new LoginException(ex.getMessage());
        }
        return waitfor;
    }
    
    public int read(final byte[] array) throws IOException {
        if (this.buffer != null) {
            final int n = (this.buffer.length - this.pos <= array.length) ? (this.buffer.length - this.pos) : array.length;
            System.arraycopy(this.buffer, this.pos, array, 0, n);
            if (this.pos + n < this.buffer.length) {
                this.pos += n;
            }
            else {
                this.buffer = null;
            }
            return n;
        }
        int read = this.in.read(array);
        if (read > 0) {
            final byte[] array2 = new byte[read];
            System.arraycopy(array, 0, array2, 0, read);
            this.pos = 0;
            this.buffer = this.handler.handleSSH(array2);
            if (this.buffer == null || this.buffer.length <= 0) {
                return 0;
            }
            final String s = new String(this.buffer);
            if (s.trim().startsWith("Login & password not accepted")) {
                throw new IOException(s);
            }
            final int pos = (this.buffer.length <= array.length) ? this.buffer.length : array.length;
            System.arraycopy(this.buffer, 0, array, 0, pos);
            read = (this.pos = pos);
            if (pos == this.buffer.length) {
                this.buffer = null;
                this.pos = 0;
            }
        }
        return read;
    }
    
    public void setTerminalType(final String terminalType) {
        this.terminalType = terminalType;
    }
    
    public String getTerminalType() {
        return this.terminalType;
    }
    
    public Dimension getWindowSize() {
        return new Dimension(80, 25);
    }
    
    public void setLocalEcho(final boolean b) {
        System.out.println("local echo " + (b ? "on" : "off"));
    }
    
    public void write(final byte[] array) throws IOException {
        this.handler.sendData(new String(array));
    }
    
    public void write(final String s) throws IOException {
        this.handler.sendData(s);
    }
    
    public void setSocketTimeout(final int soTimeout) throws IOException {
        try {
            this.socket.setSoTimeout(soTimeout);
        }
        catch (final SocketException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public int getSocketTimeout() throws SocketException {
        return this.socket.getSoTimeout();
    }
    
    public String waitfor(final String[] array) throws IOException {
        final ScriptHandler[] array2 = new ScriptHandler[array.length];
        for (int i = 0; i < array.length; ++i) {
            (array2[i] = new ScriptHandler()).setup(array[i]);
        }
        final byte[] array3 = new byte[256];
        int j = 0;
        final StringBuffer sb = new StringBuffer();
        while (j >= 0) {
            j = this.read(array3);
            if (j > 0) {
                final String s = new String(array3, 0, j);
                CLILogMgr.setDebugMessage("CLIUSER", "SshJtaProviderImpl: " + s, 4, null);
                sb.append(s);
                for (int k = 0; k < array2.length; ++k) {
                    if (array2[k].match(array3, j)) {
                        return sb.toString();
                    }
                }
            }
        }
        return null;
    }
    
    public String waitfor(final String s) throws IOException {
        return this.waitfor(new String[] { s });
    }
    
    public void open(final CLIProtocolOptions cliProtocolOptions) throws Exception, ConnectException, LoginException {
        final SshProtocolOptionsImpl sshProtocolOptionsImpl = (SshProtocolOptionsImpl)cliProtocolOptions;
        this.setTerminalType(sshProtocolOptionsImpl.getTerminalType());
        try {
            this.connect(sshProtocolOptionsImpl.getRemoteHost(), sshProtocolOptionsImpl.getRemotePort());
        }
        catch (final Exception ex) {
            throw new ConnectException("Unable to connect: " + ex.getMessage());
        }
        String waitfor;
        try {
            this.setSocketTimeout(sshProtocolOptionsImpl.getLoginTimeout());
            this.login(sshProtocolOptionsImpl.getLoginName(), sshProtocolOptionsImpl.getPassword());
            waitfor = this.waitfor(sshProtocolOptionsImpl.getPrompt());
        }
        catch (final IOException ex2) {
            this.disconnect();
            throw new LoginException(" Login Parameter incorrect" + ex2.getMessage());
        }
        sshProtocolOptionsImpl.setInitialMessage(waitfor);
        this.setSocketTimeout(100);
        CLILogMgr.setDebugMessage("CLIUSER", "SshJtaProviderImpl: session successfully opened", 4, null);
    }
    
    public void close() throws IOException {
        this.disconnect();
        CLILogMgr.setDebugMessage("CLIUSER", "SshJtaProviderImpl: session closed", 4, null);
    }
    
    public void write(final CLIMessage cliMessage) throws IOException {
        this.setSocketTimeout(200);
        this.write(cliMessage.getData() + cliMessage.getMessageSuffix());
    }
    
    boolean isDataAvailable() throws IOException {
        return this.socket.getInputStream().available() > 0;
    }
    
    public CLIMessage read() throws IOException {
        final byte[] array = new byte[256];
        final String s = null;
        final int read = this.read(array);
        if (read <= 0 && !this.isDataAvailable()) {
            return null;
        }
        final byte[] data = new byte[read];
        final CLIMessage cliMessage = new CLIMessage(s);
        System.arraycopy(array, 0, data, 0, read);
        cliMessage.setData(data);
        CLILogMgr.setDebugMessage("CLIUSER", "SshJtaProviderImpl: data read " + cliMessage.getData(), 4, null);
        return cliMessage;
    }
}
