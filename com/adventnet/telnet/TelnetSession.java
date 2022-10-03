package com.adventnet.telnet;

import java.io.InterruptedIOException;
import com.adventnet.cli.util.CLILogMgr;
import java.net.SocketException;
import java.io.IOException;
import de.mud.telnet.TelnetWrapper;

public class TelnetSession extends TelnetWrapper
{
    private String loginPrompt;
    private String passwdPrompt;
    private String prompt;
    private String loginMessage;
    String[] promptStrings;
    String partialData;
    boolean cmdEcho;
    boolean promptEcho;
    int readBufferLength;
    String lastCommand;
    String cmdSuffix;
    static boolean debugFlag;
    private String termType;
    
    public TelnetSession() {
        this.loginPrompt = "login: ";
        this.passwdPrompt = "Password: ";
        this.prompt = null;
        this.loginMessage = null;
        this.promptStrings = null;
        this.partialData = null;
        this.cmdEcho = true;
        this.promptEcho = true;
        this.readBufferLength = 256;
        this.cmdSuffix = "\n";
        this.termType = "dumb";
    }
    
    public void connect(final String s, final int n) throws IOException {
        super.connect(s, n);
    }
    
    public void connect(final String s) throws IOException {
        super.connect(s, 23);
    }
    
    public void login(final String s, final String s2) throws IOException {
        this.login(s, s2, true, true);
    }
    
    public void login(final String s, final String s2, final boolean b, final boolean b2) throws IOException {
        if ((s == null || s.length() == 0) && (s2 == null || s2.length() == 0)) {
            this.loginMessage = this.waitfor(this.prompt);
            return;
        }
        if (s != null && s.length() > 0 && (s2 == null || s2.length() == 0)) {
            if (b) {
                this.waitfor(this.loginPrompt);
            }
            this.loginMessage = this.send(s, this.prompt);
        }
        else if (s2 != null && s2.length() > 0 && (s == null || s.length() == 0)) {
            if (b2) {
                this.waitfor(this.passwdPrompt);
            }
            this.loginMessage = this.send(s2, this.prompt);
        }
        else {
            if (b) {
                this.waitfor(this.loginPrompt);
            }
            this.send(s, this.passwdPrompt);
            this.loginMessage = this.send(s2, this.prompt);
        }
    }
    
    public String getLoginMessage() {
        return this.loginMessage;
    }
    
    public String getLoginPrompt() {
        return this.loginPrompt;
    }
    
    public void setLoginPrompt(final String loginPrompt) {
        this.loginPrompt = loginPrompt;
    }
    
    public String getPasswdPrompt() {
        return this.passwdPrompt;
    }
    
    public void setPasswdPrompt(final String passwdPrompt) {
        this.passwdPrompt = passwdPrompt;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }
    
    public String[] getPromptList() {
        return this.promptStrings;
    }
    
    public void setPromptList(final String[] promptStrings) {
        this.promptStrings = promptStrings;
    }
    
    public void setSocketTimeout(final int soTimeout) throws SocketException {
        this.socket.setSoTimeout(soTimeout);
    }
    
    public int getSocketTimeout() throws SocketException {
        return this.socket.getSoTimeout();
    }
    
    public String getPartialData() {
        return this.partialData;
    }
    
    public void setCommandEcho(final boolean cmdEcho) {
        this.cmdEcho = cmdEcho;
    }
    
    public boolean isSetCommandEcho() {
        return this.cmdEcho;
    }
    
    public void setPromptEcho(final boolean promptEcho) {
        this.promptEcho = promptEcho;
    }
    
    public boolean isSetPromptEcho() {
        return this.promptEcho;
    }
    
    public int getReadBufferLength() {
        return this.readBufferLength;
    }
    
    public void setReadBufferLength(final int readBufferLength) {
        if (readBufferLength > 256) {
            this.readBufferLength = readBufferLength;
        }
    }
    
    public String waitfor(final String s) throws IOException {
        int n = 0;
        if (s.length() != 0) {
            this.scriptHandler.setup(s);
            final byte[] array = new byte[this.readBufferLength];
            int i = 0;
            String partialData = "";
            if (TelnetSession.debugFlag) {
                CLILogMgr.setDebugMessage("CLIUSER", "Parameter to match:" + s, 4, null);
            }
            while (i >= 0) {
                try {
                    i = this.read(array);
                    if (TelnetSession.debugFlag) {
                        CLILogMgr.setDebugMessage("CLIUSER", "Message rcvd:" + new String(array), 4, null);
                    }
                }
                catch (final InterruptedIOException ex) {
                    if (i > 0) {
                        partialData += new String(array, 0, i);
                    }
                    if (!this.cmdEcho || !this.promptEcho) {
                        partialData = this.truncateResponse(partialData);
                    }
                    if (n != 0) {
                        return partialData;
                    }
                    this.partialData = partialData;
                    throw new IOException("Exception while trying to match for " + s + ex.getMessage());
                }
                if (i > 0) {
                    partialData += new String(array, 0, i);
                    if (!this.scriptHandler.match(array, i)) {
                        continue;
                    }
                    if (TelnetSession.debugFlag) {
                        CLILogMgr.setDebugMessage("CLIUSER", "Parameter match SUCCESSFUL:" + s, 4, null);
                    }
                    this.partialData = null;
                    if (this.socket.getInputStream().available() <= 0) {
                        if (!this.cmdEcho || !this.promptEcho) {
                            partialData = this.truncateResponse(partialData);
                        }
                        return partialData;
                    }
                    n = 1;
                }
            }
            return null;
        }
        throw new IOException(" prompt may be empty");
    }
    
    public String waitfor(final String[] array) throws IOException {
        final int length = array.length;
        final byte[] array2 = new byte[this.readBufferLength];
        int i = 0;
        String partialData = "";
        while (i >= 0) {
            try {
                i = this.read(array2);
            }
            catch (final InterruptedIOException ex) {
                if (i > 0) {
                    partialData += new String(array2, 0, i);
                }
                if (!this.cmdEcho || !this.promptEcho) {
                    partialData = this.truncateResponse(partialData);
                }
                this.partialData = partialData;
                throw new IOException(ex.getMessage());
            }
            if (i > 0) {
                partialData += new String(array2, 0, i);
                for (int j = 0; j < length; ++j) {
                    this.scriptHandler.setup(array[j]);
                    if (this.scriptHandler.match(array2, i)) {
                        this.partialData = null;
                        if (!this.cmdEcho || !this.promptEcho) {
                            this.prompt = array[j];
                            partialData = this.truncateResponse(partialData);
                        }
                        return partialData;
                    }
                }
            }
        }
        return null;
    }
    
    String truncateResponse(final String s) {
        String s2 = s;
        if (!this.cmdEcho && this.lastCommand != null && s.startsWith(this.lastCommand)) {
            s2 = s2.substring(this.lastCommand.length());
        }
        if (!this.promptEcho && this.prompt != null && s.endsWith(this.prompt)) {
            s2 = s2.substring(0, s2.length() - this.prompt.length());
        }
        return s2;
    }
    
    public String send(final String s) throws IOException {
        return this.send(s, this.prompt);
    }
    
    public void setCommandSuffix(final String cmdSuffix) {
        this.cmdSuffix = cmdSuffix;
    }
    
    public String getCommandSuffix() {
        return this.cmdSuffix;
    }
    
    private String send(final String lastCommand, final String s) throws IOException {
        this.lastCommand = lastCommand;
        this.in.skip(this.in.available());
        if (this.cmdSuffix != null) {
            this.write((lastCommand + this.cmdSuffix).getBytes());
        }
        else {
            this.write(lastCommand.getBytes());
        }
        if (TelnetSession.debugFlag) {
            CLILogMgr.setDebugMessage("CLIUSER", "Message sent:" + lastCommand, 4, null);
        }
        if (s != null) {
            return this.waitfor(s);
        }
        if (this.promptStrings != null) {
            return this.waitfor(this.promptStrings);
        }
        return null;
    }
    
    public void setDebug(final boolean debugFlag) {
        TelnetSession.debugFlag = debugFlag;
    }
    
    public boolean isSetDebug() {
        return TelnetSession.debugFlag;
    }
    
    public boolean isDataAvailable() throws IOException {
        return this.socket.getInputStream().available() > 0;
    }
    
    public void setTerminalType(final String termType) {
        this.termType = termType;
    }
    
    public String getTerminalType() {
        return this.termType;
    }
    
    static {
        TelnetSession.debugFlag = false;
    }
}
