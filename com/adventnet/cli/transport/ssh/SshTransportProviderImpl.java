package com.adventnet.cli.transport.ssh;

import java.io.IOException;
import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.transport.LoginException;
import com.adventnet.cli.util.CLILogMgr;
import com.adventnet.cli.transport.ConnectException;
import com.adventnet.cli.transport.CLIProtocolOptions;
import com.adventnet.cli.transport.CLITransportProvider;

public class SshTransportProviderImpl implements CLITransportProvider
{
    private double sshVersion;
    private SshTransportProviderInterface provider;
    
    public SshTransportProviderImpl() {
        this.sshVersion = 0.0;
        this.provider = null;
    }
    
    public void open(final CLIProtocolOptions cliProtocolOptions) throws Exception, ConnectException, LoginException {
        try {
            this.provider = (SshTransportProviderInterface)Class.forName("com.adventnet.cli.transport.ssh.sshv2.SshToolsProviderImpl").newInstance();
        }
        catch (final Exception ex) {
            throw new ConnectException("Exception while creating SshJtaProviderImpl " + ex.getMessage());
        }
        this.provider.open(cliProtocolOptions);
        CLILogMgr.setDebugMessage("CLIUSER", "SshTransportProviderImpl: session successfully opened", 4, null);
    }
    
    public void close() throws Exception {
        this.provider.close();
        this.provider = null;
        CLILogMgr.setDebugMessage("CLIUSER", "SshTransportProviderImpl: session closed", 4, null);
    }
    
    public void write(final CLIMessage cliMessage) throws IOException {
        try {
            this.provider.write(cliMessage);
            CLILogMgr.setDebugMessage("CLIUSER", "SshTransportProviderImpl: cmd " + cliMessage.getData() + " sent", 4, null);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new IOException(ex.getMessage());
        }
    }
    
    public CLIMessage read() throws IOException {
        final CLIMessage read = this.provider.read();
        CLILogMgr.setDebugMessage("CLIUSER", "SshTransportProviderImpl: data read " + read.getData(), 4, null);
        return read;
    }
    
    public void connect(final String s, final int n) throws ConnectException {
        this.connect(s, n, null);
    }
    
    public void connect(final String s, final int n, final String terminalType) throws ConnectException {
        try {
            this.provider = (SshTransportProviderInterface)Class.forName("com.adventnet.cli.transport.ssh.sshv2.SshToolsProviderImpl").newInstance();
        }
        catch (final Exception ex) {
            throw new ConnectException("Exception while creating SshJtaProviderImpl " + ex.getMessage());
        }
        if (terminalType != null) {
            this.setTerminalType(terminalType);
        }
        this.provider.connect(s, n);
    }
    
    public String login(final String s, final String s2, final String s3) throws IOException {
        String login;
        try {
            login = this.provider.login(s, s2, s3);
            System.out.println(" log message " + login);
        }
        catch (final LoginException ex) {
            throw new IOException(ex.getMessage());
        }
        return login;
    }
    
    public void setSocketTimeout(final int socketTimeout) throws IOException {
        try {
            this.provider.setSocketTimeout(socketTimeout);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void write(final byte[] array) throws IOException {
        this.provider.write(array);
    }
    
    public int read(final byte[] array) throws IOException {
        return this.provider.read(array);
    }
    
    public void setTerminalType(final String terminalType) {
        this.provider.setTerminalType(terminalType);
    }
}
