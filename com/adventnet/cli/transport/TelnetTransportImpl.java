package com.adventnet.cli.transport;

import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.util.CLILogMgr;
import com.adventnet.cli.CLISession;
import java.io.IOException;
import com.adventnet.telnet.TelnetSession;

public class TelnetTransportImpl implements CLITransportProvider
{
    TelnetSession telnetSession;
    byte[] readBuffer;
    private boolean checkStatus;
    String matchedPrompt;
    
    public TelnetTransportImpl() {
        this.telnetSession = null;
        this.readBuffer = null;
        this.checkStatus = false;
        this.matchedPrompt = null;
        this.telnetSession = new TelnetSession();
        this.readBuffer = new byte[this.telnetSession.getReadBufferLength()];
    }
    
    public void open(final CLIProtocolOptions cliProtocolOptions) throws Exception, ConnectException, LoginException {
        final TelnetProtocolOptionsImpl telnetProtocolOptionsImpl = (TelnetProtocolOptionsImpl)cliProtocolOptions;
        if (telnetProtocolOptionsImpl.isPerformPing() && !this.checkIfDeviceAlive(telnetProtocolOptionsImpl.getRemoteHost())) {
            throw new ConnectException("Unable to connect. Host " + telnetProtocolOptionsImpl.getRemoteHost() + " could be down");
        }
        try {
            this.telnetSession.setTerminalType(telnetProtocolOptionsImpl.getTerminalType());
            this.telnetSession.connect(telnetProtocolOptionsImpl.getRemoteHost(), telnetProtocolOptionsImpl.getRemotePort());
        }
        catch (final IOException ex) {
            throw new ConnectException("Unable to connect: " + ex.getMessage());
        }
        this.telnetSession.setSocketTimeout(telnetProtocolOptionsImpl.getLoginTimeout());
        if (telnetProtocolOptionsImpl.getLoginPrompt() != null) {
            this.telnetSession.setLoginPrompt(telnetProtocolOptionsImpl.getLoginPrompt());
        }
        if (telnetProtocolOptionsImpl.getPasswdPrompt() != null) {
            this.telnetSession.setPasswdPrompt(telnetProtocolOptionsImpl.getPasswdPrompt());
        }
        if (telnetProtocolOptionsImpl.getConnectionHandler() != null) {
            telnetProtocolOptionsImpl.getConnectionHandler().postConnect(this);
        }
        final String[] promptList = telnetProtocolOptionsImpl.getPromptList();
        if (promptList != null) {
            this.telnetSession.setPromptList(promptList);
        }
        else {
            this.telnetSession.setPrompt(telnetProtocolOptionsImpl.getPrompt());
        }
        final String loginPrefix = telnetProtocolOptionsImpl.getLoginPrefix();
        boolean b = true;
        boolean b2 = true;
        if (loginPrefix != null) {
            final String loginPrefixPrompt = telnetProtocolOptionsImpl.getLoginPrefixPrompt();
            if (loginPrefixPrompt != null && loginPrefixPrompt.length() > 0) {
                final String loginName = telnetProtocolOptionsImpl.getLoginName();
                final String password = telnetProtocolOptionsImpl.getPassword();
                boolean b3 = false;
                boolean b4 = false;
                String s;
                if (loginName != null && loginName.trim().length() > 0) {
                    s = telnetProtocolOptionsImpl.getLoginPrompt();
                    b3 = true;
                }
                else {
                    if (password == null || password.trim().length() <= 0) {
                        throw new LoginException("don't know what to do");
                    }
                    s = telnetProtocolOptionsImpl.getPasswdPrompt();
                    b4 = true;
                }
                final String[] array = { s, loginPrefixPrompt };
                this.checkForMatches(array, this.telnetSession.waitfor(array));
                if (this.matchedPrompt == null || this.matchedPrompt.trim().length() <= 0) {
                    throw new LoginException("Prompt not matching...");
                }
                if (this.matchedPrompt.equals(loginPrefixPrompt)) {
                    final byte[] array2 = { 25 };
                    byte[] bytes;
                    if (loginPrefix.equals("CTRL-Y")) {
                        bytes = array2;
                    }
                    else {
                        bytes = loginPrefix.getBytes();
                    }
                    this.telnetSession.write(bytes);
                }
                else if (this.matchedPrompt.equals(s)) {
                    if (b3) {
                        b = false;
                    }
                    else if (b4) {
                        b2 = false;
                    }
                }
            }
        }
        this.telnetSession.setDebug(CLISession.isSetDebug());
        try {
            if (loginPrefix != null) {
                this.telnetSession.login(telnetProtocolOptionsImpl.getLoginName(), telnetProtocolOptionsImpl.getPassword(), b, b2);
            }
            else {
                this.telnetSession.login(telnetProtocolOptionsImpl.getLoginName(), telnetProtocolOptionsImpl.getPassword());
            }
        }
        catch (final IOException ex2) {
            this.telnetSession.disconnect();
            this.telnetSession = null;
            throw new LoginException("Exception in opening telnet connection to " + telnetProtocolOptionsImpl.getRemoteHost() + " Exception trace " + ex2.getMessage(), ex2);
        }
        this.checkStatus = telnetProtocolOptionsImpl.isCheckServerStatus();
        telnetProtocolOptionsImpl.setInitialMessage(this.telnetSession.getLoginMessage());
        this.telnetSession.setSocketTimeout(100);
        CLILogMgr.setDebugMessage("CLIUSER", "TelnetTransportImpl: session successfully opened", 4, null);
    }
    
    private String removeUnwantedChar(final String s) {
        final char[] array = new char[s.length()];
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 > '\u001f' && char1 != '?' && char1 != ';') {
                if (char1 < '0' || char1 > '9') {
                    array[n] = char1;
                    ++n;
                }
            }
        }
        return String.copyValueOf(array, 0, n);
    }
    
    private boolean checkForMatches(final String[] array, final String s) {
        final String removeUnwantedChar = this.removeUnwantedChar(s);
        for (int i = 0; i < array.length; ++i) {
            final String matchedPrompt = array[i];
            if (this.checkForMatch(matchedPrompt, removeUnwantedChar)) {
                this.matchedPrompt = matchedPrompt;
                return true;
            }
        }
        return false;
    }
    
    private boolean checkForMatch(final String s, final String s2) {
        s2.length();
        return s == null || s2.indexOf(s) >= 0;
    }
    
    public void close() throws Exception {
        this.telnetSession.disconnect();
        this.telnetSession = null;
        CLILogMgr.setDebugMessage("CLIUSER", "TelnetTransportImpl: session closed", 4, null);
    }
    
    public void write(final CLIMessage cliMessage) throws IOException {
        this.telnetSession.setSocketTimeout(200);
        this.telnetSession.setCommandSuffix(cliMessage.getMessageSuffix());
        this.telnetSession.write((cliMessage.getData() + cliMessage.getMessageSuffix()).getBytes());
        CLILogMgr.setDebugMessage("CLIUSER", "TelnetTransportImpl: cmd " + cliMessage.getData() + " sent", 4, null);
    }
    
    public CLIMessage read() throws IOException {
        final String s = null;
        if (!this.checkStatus && !this.telnetSession.isDataAvailable()) {
            return null;
        }
        final int read = this.telnetSession.read(this.readBuffer);
        if (read < 0) {
            throw new IOException("Socket closed by remote host");
        }
        final byte[] data = new byte[read];
        final CLIMessage cliMessage = new CLIMessage(s);
        System.arraycopy(this.readBuffer, 0, data, 0, read);
        cliMessage.setData(data);
        CLILogMgr.setDebugMessage("CLIUSER", "TelnetTransportImpl: data read " + cliMessage.getData(), 4, null);
        return cliMessage;
    }
    
    private boolean checkIfDeviceAlive(final String s) {
        try {
            return (boolean)Class.forName("com.adventnet.nms.util.Ping").getMethod("ping", Class.forName("java.lang.String")).invoke(null, s);
        }
        catch (final Exception ex) {
            return true;
        }
    }
}
