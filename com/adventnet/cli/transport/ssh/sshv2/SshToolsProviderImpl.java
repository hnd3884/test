package com.adventnet.cli.transport.ssh.sshv2;

import com.maverick.ssh2.KBIPrompt;
import com.adventnet.cli.CLIMessage;
import java.io.RandomAccessFile;
import com.adventnet.cli.transport.ssh.SshProtocolOptionsImpl;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.util.regex.Pattern;
import com.maverick.ssh.PseudoTerminalModes;
import com.maverick.ssh2.KBIRequestHandler;
import com.maverick.ssh2.KBIAuthentication;
import com.maverick.ssh1.Ssh1Client;
import com.maverick.ssh2.Ssh2Context;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.maverick.ssh.components.ComponentFactory;
import com.maverick.ssh.components.SshKeyPair;
import com.sshtools.publickey.SshPrivateKeyFile;
import java.io.File;
import com.sshtools.publickey.SshPrivateKeyFileFactory;
import com.maverick.ssh.PublicKeyAuthentication;
import com.maverick.ssh.PasswordAuthentication;
import com.maverick.ssh.SshAuthentication;
import com.maverick.ssh.SshTunnel;
import com.maverick.ssh.SshChannel;
import com.adventnet.cli.transport.LoginException;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.SshTransport;
import com.sshtools.net.SocketTransport;
import com.maverick.ssh.components.SshPublicKey;
import java.io.IOException;
import com.adventnet.cli.util.CLILogMgr;
import com.adventnet.cli.transport.ConnectException;
import com.maverick.ssh.HostKeyVerification;
import com.adventnet.cli.ssh.sshv2.SshHostKeyVerification;
import com.maverick.ssh.SshException;
import com.adventnet.cli.transport.Credentials;
import com.maverick.ssh.SshSession;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshConnector;
import com.adventnet.cli.transport.ssh.SshTransportProviderInterface;
import com.maverick.ssh.ChannelAdapter;

public class SshToolsProviderImpl extends ChannelAdapter implements SshTransportProviderInterface
{
    private String response;
    private boolean disconnect;
    private String terminalType;
    private byte[] buffer;
    private int pos;
    static boolean debug;
    private int messageTimeout;
    private int loginTimeout;
    private SshConnector con;
    private String hostname;
    private int port;
    private SshClient sshClient;
    private SshSession session;
    private boolean isOPMSession;
    private Credentials targetCred;
    private Credentials[] credArray;
    private String loginPrefix;
    private String loginPrefixPrompt;
    private boolean loginPrefixHandling;
    
    public SshToolsProviderImpl() throws SshException {
        this.response = "";
        this.disconnect = false;
        this.terminalType = "xterm";
        this.messageTimeout = 5000;
        this.loginTimeout = 15000;
        this.con = null;
        this.hostname = null;
        this.port = 22;
        this.sshClient = null;
        this.session = null;
        this.isOPMSession = false;
        this.targetCred = null;
        this.credArray = null;
        this.loginPrefix = null;
        this.loginPrefixPrompt = null;
        this.loginPrefixHandling = false;
    }
    
    public boolean isConnected() {
        return this.sshClient.isConnected();
    }
    
    public void connect(final String s) throws ConnectException {
        try {
            this.connect(s, 22, (HostKeyVerification)new SshHostKeyVerification());
        }
        catch (final Exception ex) {
            throw new ConnectException(ex.getMessage());
        }
    }
    
    public void connect(final String s, final int n) throws ConnectException {
        try {
            this.connect(s, n, (HostKeyVerification)new SshHostKeyVerification());
        }
        catch (final Exception ex) {
            throw new ConnectException(ex.getMessage());
        }
    }
    
    public void connect(final String hostname, final int port, final HostKeyVerification hostKeyVerification) throws ConnectException {
        this.hostname = hostname;
        this.port = port;
    }
    
    public void disconnect() {
        this.closeSession();
        this.closeSshClient(this.sshClient);
        synchronized (this) {
            this.disconnect = true;
            this.notify();
        }
    }
    
    private void closeSession() {
        try {
            if (this.session != null) {
                this.session.close();
            }
        }
        catch (final Exception ex) {
            CLILogMgr.CLIERR.fail("Exception in closing session", (Throwable)ex);
        }
    }
    
    private void closeSshClient(final SshClient sshClient) {
        try {
            if (sshClient != null && sshClient.isConnected()) {
                sshClient.disconnect();
            }
        }
        catch (final Exception ex) {
            CLILogMgr.CLIERR.fail("Exception in closing sshclient" + sshClient, (Throwable)ex);
        }
    }
    
    public void setTerminalType(final String terminalType) {
        this.terminalType = terminalType;
    }
    
    public String getTerminalType() {
        return this.terminalType;
    }
    
    public void setMessageTimeout(final int messageTimeout) {
        this.messageTimeout = messageTimeout;
    }
    
    int getMessageTimeout() {
        return this.messageTimeout;
    }
    
    public void setSocketTimeout(final int messageTimeout) throws IOException {
        this.setMessageTimeout(messageTimeout);
    }
    
    private void forwardSshChannel(int n) throws Exception {
        if (this.sshClient.isConnected() && this.sshClient.isAuthenticated()) {
            if (this.credArray.length - 1 != n) {
                this.targetCred = this.credArray[++n];
                this.sshToNextTarget(this.sshClient, n);
            }
            else {
                this.initSession(this.sshClient, this.targetCred.getLoginName(), "", false, false);
            }
        }
    }
    
    private void sshToNextTarget(final SshClient sshClient, final int n) throws Exception {
        final String host = this.targetCred.getHost();
        final int port = this.targetCred.getPort();
        final String loginName = this.targetCred.getLoginName();
        final Credentials alternateServer = this.targetCred.getAlternateServer();
        SshTunnel openForwardingChannel = null;
        try {
            (this.con = SshConnector.createInstance()).setKnownHosts((HostKeyVerification)new HostKeyVerification() {
                public boolean verifyHost(final String s, final SshPublicKey sshPublicKey) {
                    return true;
                }
            });
            if (sshClient == null) {
                CLILogMgr.setDebugMessage("CLIUSER", "First connection to the host " + host + ":" + port, 4, null);
                this.sshClient = this.con.connect((SshTransport)new SocketTransport(host, port), loginName, true);
            }
            else {
                CLILogMgr.setDebugMessage("CLIUSER", "Relay server configuration, forwarding connection to  " + this.targetCred.getHost() + ":" + this.targetCred.getPort(), 4, null);
                openForwardingChannel = sshClient.openForwardingChannel(this.targetCred.getHost(), this.targetCred.getPort(), "127.0.0.1", 22, "127.0.0.1", 22, (SshTransport)null, (ChannelEventListener)null);
                this.sshClient = this.con.connect((SshTransport)openForwardingChannel, this.targetCred.getLoginName(), true);
            }
            int authenticate = -1;
            if (!this.sshClient.isAuthenticated()) {
                authenticate = this.sshClient.authenticate(this.getSshAuthentication());
            }
            if (authenticate != 1) {
                throw new LoginException("Connection/authentication to " + host + " failed ");
            }
            if (openForwardingChannel != null) {
                openForwardingChannel.addChannelEventListener((ChannelEventListener)new ChannelEventListener() {
                    public void channelClosed(final SshChannel sshChannel) {
                        SshToolsProviderImpl.this.closeSshClient(sshClient);
                    }
                    
                    public void extendedDataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2, final int n3) {
                    }
                    
                    public void dataSent(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
                    }
                    
                    public void dataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
                    }
                    
                    public void channelOpened(final SshChannel sshChannel) {
                    }
                    
                    public void channelEOF(final SshChannel sshChannel) {
                    }
                    
                    public void channelClosing(final SshChannel sshChannel) {
                    }
                });
            }
            this.forwardSshChannel(n);
        }
        catch (final Exception ex) {
            CLILogMgr.setDebugMessage("CLIUSER", "SSH connection failed, disconnecting currently attempted sshclient ", 4, null);
            this.closeSshClient(this.sshClient);
            CLILogMgr.setDebugMessage("CLIUSER", "SSH connection to the device " + host + " failed. Checking alternate server", 4, ex);
            if (alternateServer != null) {
                CLILogMgr.setDebugMessage("CLIUSER", "Alternate server configuration specified, trying..,  ", 4, null);
                this.targetCred = alternateServer;
                this.sshToNextTarget(sshClient, n);
            }
            else if (alternateServer == null) {
                this.closeSshClient(sshClient);
                throw ex;
            }
        }
    }
    
    private SshAuthentication getSshAuthentication() throws Exception {
        final File pkFile = this.targetCred.getPKFile();
        byte[] array = this.targetCred.getPKContents();
        if (pkFile == null && array == null) {
            final PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
            passwordAuthentication.setPassword(this.targetCred.getPassword());
            return (SshAuthentication)passwordAuthentication;
        }
        if (pkFile != null) {
            array = this.getPKFileContents(pkFile);
        }
        final PublicKeyAuthentication publicKeyAuthentication = new PublicKeyAuthentication();
        final SshPrivateKeyFile parse = SshPrivateKeyFileFactory.parse(array);
        final String pkPassPhrase = this.targetCred.getPKPassPhrase();
        if (parse.isPassphraseProtected() && pkPassPhrase == null) {
            CLILogMgr.setDebugMessage("CLIUSER", "SSH connection via landing server", 4, new Exception("PrivateKey is password protected but Passphrase not supplied"));
            throw new LoginException("PrivateKey is password protected. Passphrase is not set");
        }
        final SshKeyPair keyPair = parse.toKeyPair(pkPassPhrase);
        publicKeyAuthentication.setPrivateKey(keyPair.getPrivateKey());
        publicKeyAuthentication.setPublicKey(keyPair.getPublicKey());
        return (SshAuthentication)publicKeyAuthentication;
    }
    
    private void login(final String s, final byte[] array, final String s2) throws LoginException {
        try {
            final SshPrivateKeyFile parse = SshPrivateKeyFileFactory.parse(array);
            if (parse.isPassphraseProtected() && s2 == null) {
                throw new LoginException("PrivateKey is password protected but passphrase not set in the credential object");
            }
            this.login(s, parse.toKeyPair(s2));
        }
        catch (final Exception ex) {
            throw new LoginException(ex.getMessage());
        }
    }
    
    private void login(final String s, final SshKeyPair sshKeyPair) throws LoginException {
        try {
            (this.con = SshConnector.createInstance()).setKnownHosts((HostKeyVerification)new HostKeyVerification() {
                public boolean verifyHost(final String s, final SshPublicKey sshPublicKey) {
                    return true;
                }
            });
            this.sshClient = this.con.connect((SshTransport)new SocketTransport(this.hostname, this.port), s, true);
            final PublicKeyAuthentication publicKeyAuthentication = new PublicKeyAuthentication();
            if (!this.isOPMSession) {
                final long currentTimeMillis = System.currentTimeMillis();
                int int1 = 5000;
                try {
                    int1 = Integer.parseInt(System.getProperty("ncm.ssh.key.auth.timeout", "5000"));
                }
                catch (final Exception ex) {}
                do {
                    publicKeyAuthentication.setPrivateKey(sshKeyPair.getPrivateKey());
                    publicKeyAuthentication.setPublicKey(sshKeyPair.getPublicKey());
                    if (System.currentTimeMillis() - currentTimeMillis > int1) {
                        break;
                    }
                } while (this.sshClient.authenticate((SshAuthentication)publicKeyAuthentication) != 1 && this.sshClient.isConnected());
            }
            else {
                publicKeyAuthentication.setPrivateKey(sshKeyPair.getPrivateKey());
                publicKeyAuthentication.setPublicKey(sshKeyPair.getPublicKey());
                if (this.sshClient.authenticate((SshAuthentication)publicKeyAuthentication) != 1) {
                    throw new LoginException("Exception in login");
                }
                if (!this.sshClient.isAuthenticated()) {
                    throw new LoginException("Exception in login");
                }
            }
            this.initSession(this.sshClient, s, "", false, false);
        }
        catch (final LoginException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new LoginException(" exception during logging in " + ex3.getMessage(), ex3);
        }
    }
    
    private void addComponentFactory(final ComponentFactory componentFactory, final ComponentFactory componentFactory2, final String s) {
        List<String> list = new ArrayList<String>();
        if (s != null && s.length() > 0) {
            list = Arrays.asList(s.split(","));
        }
        CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl:Before Adding Ciphers to the  CipherList " + componentFactory.list("") + " And OrigSupportedCiphers are" + componentFactory2.list(""), 4, null);
        final String[] split = componentFactory2.list("").split(",");
        for (int i = 0; i < split.length; ++i) {
            final String s2 = split[i];
            if (componentFactory.contains(s2)) {
                if (list.contains(s2)) {
                    CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl:After Going to add Cipher " + s2 + "to the  CipherList " + componentFactory.list(""), 4, null);
                    componentFactory.remove(s2);
                    CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl:After adding Cipher " + s2 + "to the  CipherList " + componentFactory.list(""), 4, null);
                }
            }
            else if (!list.contains(s2)) {
                try {
                    CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl:Going to Remove Cipher " + s2 + "to the  CipherList " + componentFactory.list(""), 4, null);
                    componentFactory.add(s2, (Class)componentFactory2.getInstance(s2).getClass());
                    CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl:After Removing Cipher " + s2 + "to the  CipherList " + componentFactory.list(""), 4, null);
                }
                catch (final SshException ex) {
                    CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl:Exception on adding Cipher " + s2 + "to the  CipherList " + componentFactory.list(""), 4, (Exception)ex);
                }
            }
        }
    }
    
    private void login(final String username, final String s, final boolean b, final String s2, final boolean b2) throws LoginException {
        try {
            (this.con = SshConnector.createInstance()).setKnownHosts((HostKeyVerification)new HostKeyVerification() {
                public boolean verifyHost(final String s, final SshPublicKey sshPublicKey) {
                    return true;
                }
            });
            final SocketTransport socketTransport = new SocketTransport(this.hostname, this.port);
            final String property = System.getProperty("ncm.ssh.setPreferredKeyExchange");
            if (property != null) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Setting Preferred Key Exchange begins:" + property);
                ((Ssh2Context)this.con.getContext(2)).setPreferredKeyExchange(property);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Setting Preferred Key Exchange completed");
            }
            ((Ssh2Context)this.con.getContext(2)).setSendIgnorePacketOnIdle(false);
            final String property2 = System.getProperty("ncm.ssh.set.socketTimeout");
            if (property2 != null) {
                try {
                    final int int1 = Integer.parseInt(property2);
                    CLILogMgr.setDebugMessage("CLIUSER", "Socket Timeout is set as :::: " + int1, 2, null);
                    ((Ssh2Context)this.con.getContext(2)).setSocketTimeout(int1);
                }
                catch (final Exception ex) {}
            }
            this.sshClient = this.con.connect((SshTransport)socketTransport, username, true);
            if (this.sshClient instanceof Ssh1Client) {
                CLILogMgr.setDebugMessage("CLIUSER", "SSH v1 server", 4, null);
            }
            else {
                CLILogMgr.setDebugMessage("CLIUSER", "SSH v2 server", 4, null);
            }
            boolean b3 = true;
            boolean booleanValue = false;
            final String property3 = System.getProperty("ncm.ssh.useKBIAuthenticationforDevices");
            if (property3 != null) {
                CLILogMgr.setDebugMessage("CLIUSER", "######################## useKBIAuthforDevicesStr : " + property3, 2, null);
                final String[] split = property3.split(",");
                for (int i = 0; i < split.length; ++i) {
                    if (this.hostname.equalsIgnoreCase(split[i])) {
                        booleanValue = Boolean.TRUE;
                        break;
                    }
                }
            }
            CLILogMgr.setDebugMessage("CLIUSER", "######################## isUseKBIAuthentication : " + booleanValue + property3, 2, null);
            if (!this.sshClient.isAuthenticated()) {
                b3 = false;
                if (s != null) {
                    if (booleanValue) {
                        final KBIAuthentication kbiAuthentication = new KBIAuthentication();
                        kbiAuthentication.setUsername(username);
                        final KBIRequestHandlerImpl kbiRequestHandler = new KBIRequestHandlerImpl();
                        kbiRequestHandler.setPassword(s);
                        kbiAuthentication.setKBIRequestHandler((KBIRequestHandler)kbiRequestHandler);
                        CLILogMgr.setDebugMessage("CLIUSER", "######################## Authenticating with KBI Authentication begins " + property3, 2, null);
                        if (this.sshClient.authenticate((SshAuthentication)kbiAuthentication) != 1) {
                            CLILogMgr.setDebugMessage("CLIUSER", "######################## Authenticating with KBI Authentication fails " + property3, 2, null);
                            throw new LoginException("Exception during KBI Authentication in login");
                        }
                    }
                    else {
                        final PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
                        passwordAuthentication.setPassword(s);
                        if (this.sshClient.authenticate((SshAuthentication)passwordAuthentication) != 1) {
                            throw new LoginException("Exception in login");
                        }
                    }
                }
                if (!this.sshClient.isAuthenticated()) {
                    throw new LoginException("Exception in login");
                }
            }
            this.initSession(this.sshClient, username, s, b3, b, b2);
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            throw new LoginException(" exception during logging in " + ex2.getMessage());
        }
    }
    
    private void login(final String s, final String s2, final boolean b) throws LoginException {
        this.login(s, s2, b, null, true);
    }
    
    private void initSession(final SshClient sshClient, final String s, final String s2, final boolean b, final boolean b2) throws Exception {
        this.initSession(sshClient, s, s2, b, b2, true);
    }
    
    private void initSession(final SshClient sshClient, String string, String string2, final boolean b, final boolean b2, final boolean b3) throws Exception {
        this.session = sshClient.openSessionChannel((ChannelEventListener)this);
        final PseudoTerminalModes pseudoTerminalModes = new PseudoTerminalModes(sshClient);
        pseudoTerminalModes.setTerminalMode(53, b3);
        this.session.requestPseudoTerminal("vt100", this.isOPMSession ? -1 : 80, 24, 0, 0, pseudoTerminalModes);
        this.session.setAutoConsumeInput(true);
        this.session.startShell();
        if (b) {
            Thread.sleep(1000L);
            if (string != null && string.length() > 0) {
                string += "\r";
                this.session.getOutputStream().write(string.getBytes());
                Thread.sleep(1000L);
            }
            if (string2 != null && string2.length() > 0) {
                string2 += "\r";
                this.session.getOutputStream().write(string2.getBytes());
                Thread.sleep(1000L);
            }
        }
        if (b2) {
            System.out.println("Going to handle loginPrefix handling LoginPrefix :" + this.loginPrefix + " Prefix Prompt:" + this.loginPrefixPrompt);
            Thread.sleep(1000L);
            final byte[] array = { 25 };
            byte[] bytes;
            if (this.loginPrefix.equals("CTRL-Y")) {
                bytes = array;
            }
            else {
                bytes = this.loginPrefix.getBytes();
            }
            this.session.getOutputStream().write(bytes);
        }
    }
    
    public String login(final String s, final String s2, final String s3) throws LoginException {
        String waitfor;
        try {
            this.login(s, s2, this.loginPrefixHandling);
            waitfor = this.waitfor(s3);
        }
        catch (final Exception ex) {
            throw new LoginException(ex.getMessage());
        }
        return waitfor;
    }
    
    private void onSessionData(final byte[] array) {
        synchronized (this) {
            this.response += new String(array);
            this.notify();
        }
    }
    
    private byte[] readData() throws IOException {
        synchronized (this) {
            try {
                this.wait(500L);
            }
            catch (final InterruptedException ex) {
                ex.printStackTrace();
            }
            if (this.disconnect) {
                throw new IOException("socket closed ");
            }
            final byte[] bytes = this.response.getBytes();
            this.response = "";
            return bytes;
        }
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
        int length = 0;
        this.buffer = this.readData();
        if (this.buffer != null) {
            length = this.buffer.length;
        }
        if (length > 0) {
            this.pos = 0;
            if (this.buffer == null || this.buffer.length > 0) {}
            if (this.buffer == null || this.buffer.length <= 0) {
                return 0;
            }
            final int pos = (this.buffer.length <= array.length) ? this.buffer.length : array.length;
            System.arraycopy(this.buffer, 0, array, 0, pos);
            length = (this.pos = pos);
            if (pos == this.buffer.length) {
                this.buffer = null;
                this.pos = 0;
            }
        }
        else {
            this.buffer = null;
        }
        return length;
    }
    
    public void write(final byte[] array) throws IOException {
        try {
            this.sendSessionData(array);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new IOException(ex.getMessage());
        }
    }
    
    public String waitfor(final String s) throws IOException {
        int read = 0;
        final byte[] array = new byte[256];
        String string = "";
        final long currentTimeMillis = System.currentTimeMillis();
        final String property = System.getProperty("ncm.ssh.connection.login.timeoutinms", "15000");
        int int1;
        if (property != null) {
            int1 = Integer.parseInt(property);
            if (int1 > 60000) {
                int1 = 60000;
            }
        }
        else {
            int1 = 5000;
        }
        System.out.println("-------> SshToolsProviderImpl open: waitfor(String) : modified loginTimeout : " + int1);
        for (long n = -1L; read >= 0 && n < int1; n = System.currentTimeMillis() - currentTimeMillis) {
            read = this.read(array);
            if (read > 0) {
                string += new String(array, 0, read);
                if (s == null) {
                    return string;
                }
                if (this.isOPMSession || s.length() == 1) {
                    if (this.isMatch(string.getBytes(), s.getBytes())) {
                        return string;
                    }
                }
                else {
                    try {
                        if (Pattern.compile(s).matcher(string).find()) {
                            return string;
                        }
                    }
                    catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (int1 != 0) {}
        }
        throw new IOException("Read timed out");
    }
    
    boolean isMatch(final byte[] array, final byte[] array2) {
        int n = 0;
        final int length = array2.length;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == array2[n]) {
                if (++n == length) {
                    return true;
                }
            }
            else {
                if (i + length > array.length) {
                    break;
                }
                n = 0;
            }
        }
        return false;
    }
    
    public String waitfor(final String[] array) throws IOException {
        int read = 0;
        final byte[] array2 = new byte[256];
        String string = "";
        for (long currentTimeMillis = System.currentTimeMillis(), n = -1L; read >= 0 && n < this.messageTimeout; n = System.currentTimeMillis() - currentTimeMillis) {
            read = this.read(array2);
            if (read > 0) {
                string += new String(array2, 0, read);
                if (array == null) {
                    return string;
                }
                for (int i = 0; i < array.length; ++i) {
                    if (this.isMatch(string.getBytes(), array[i].getBytes())) {
                        return string;
                    }
                }
            }
            if (this.messageTimeout != 0) {}
        }
        throw new IOException("Read timed out");
    }
    
    private void sendSessionData(final byte[] array) throws IOException {
        this.session.getOutputStream().write(array);
    }
    
    public void open(final CLIProtocolOptions cliProtocolOptions) throws Exception, ConnectException, LoginException {
        final SshProtocolOptionsImpl sshProtocolOptionsImpl = (SshProtocolOptionsImpl)cliProtocolOptions;
        if (sshProtocolOptionsImpl.getLoginPrefix() != null) {
            this.loginPrefix = sshProtocolOptionsImpl.getLoginPrefix();
            this.loginPrefixPrompt = sshProtocolOptionsImpl.getLoginPrefixPrompt();
            this.loginPrefixHandling = true;
        }
        this.loginTimeout = sshProtocolOptionsImpl.getLoginTimeout();
        this.isOPMSession = sshProtocolOptionsImpl.getOPMSession();
        this.setTerminalType(sshProtocolOptionsImpl.getTerminalType());
        this.connect(sshProtocolOptionsImpl.getRemoteHost(), sshProtocolOptionsImpl.getRemotePort());
        String initialMessage = null;
        try {
            this.setMessageTimeout(this.loginTimeout);
            this.credArray = sshProtocolOptionsImpl.getCredentialsArray();
            if (this.credArray != null) {
                this.targetCred = this.credArray[0];
                this.sshToNextTarget(null, 0);
            }
            else {
                final File privateKeyFile = sshProtocolOptionsImpl.getPrivateKeyFile();
                final byte[] privateKeyData = sshProtocolOptionsImpl.getPrivateKeyData();
                final String pkPassPhrase = sshProtocolOptionsImpl.getPKPassPhrase();
                if (privateKeyFile != null) {
                    this.login(sshProtocolOptionsImpl.getLoginName(), this.getPKFileContents(privateKeyFile), pkPassPhrase);
                }
                else if (privateKeyData != null) {
                    this.login(sshProtocolOptionsImpl.getLoginName(), privateKeyData, pkPassPhrase);
                }
                else {
                    this.login(sshProtocolOptionsImpl.getLoginName(), sshProtocolOptionsImpl.getPassword(), this.loginPrefixHandling, sshProtocolOptionsImpl.getBlockedCiphers(), sshProtocolOptionsImpl.getTerminalEcho());
                }
                if (sshProtocolOptionsImpl.getPromptList() != null) {
                    initialMessage = this.waitfor(sshProtocolOptionsImpl.getPromptList());
                }
                else {
                    initialMessage = this.waitfor(sshProtocolOptionsImpl.getPrompt());
                }
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            this.disconnect();
            throw new LoginException(" Login Parameter incorrect" + ex.getMessage());
        }
        sshProtocolOptionsImpl.setInitialMessage(initialMessage);
        CLILogMgr.setDebugMessage("CLIUSER", "SshToolsProviderImpl: session successfully opened", 4, null);
    }
    
    private byte[] getPKFileContents(final File file) throws Exception {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            final byte[] array = new byte[(int)randomAccessFile.length()];
            randomAccessFile.readFully(array);
            return array;
        }
        finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }
    
    public void close() {
        this.disconnect();
        CLILogMgr.setDebugMessage("CLIUSER", "SshTooolsProviderImpl: session closed", 4, null);
    }
    
    public void write(final CLIMessage cliMessage) throws IOException {
        this.write((cliMessage.getData() + cliMessage.getMessageSuffix()).getBytes());
    }
    
    private boolean isDataAvailable() throws IOException {
        return this.session.getInputStream().available() > 0;
    }
    
    public CLIMessage read() throws IOException {
        final String s = null;
        final byte[] array = new byte[256];
        final int read = this.read(array);
        if (read == 0 && !this.isDataAvailable()) {
            return null;
        }
        final byte[] data = new byte[read];
        final CLIMessage cliMessage = new CLIMessage(s);
        System.arraycopy(array, 0, data, 0, read);
        cliMessage.setData(data);
        CLILogMgr.setDebugMessage("CLIUSER", "SshToolsProviderImpl: data read " + cliMessage.getData(), 4, null);
        return cliMessage;
    }
    
    public void dataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
        if (n2 <= 0) {
            return;
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        this.onSessionData(array2);
    }
    
    public void extendedDataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2, final int n3) {
        final int n4 = n2 - n;
        if (n4 <= 0) {
            return;
        }
        final byte[] array2 = new byte[n4];
        System.arraycopy(array, n, array2, 0, n4);
        this.onSessionData(array2);
    }
    
    static {
        SshToolsProviderImpl.debug = false;
    }
    
    public class KBIRequestHandlerImpl implements KBIRequestHandler
    {
        private String password;
        
        public void setPassword(final String password) {
            this.password = password;
        }
        
        public String getPassword() {
            return this.password;
        }
        
        public boolean showPrompts(final String s, final String s2, final KBIPrompt[] array) {
            try {
                Thread.sleep(1000L);
                for (int i = 0; i < array.length; ++i) {
                    final String prompt = array[i].getPrompt();
                    String password = "yes";
                    if (prompt.contains("yes")) {
                        password = "yes";
                    }
                    if (prompt.contains("assword")) {
                        password = this.getPassword();
                    }
                    array[i].setResponse(password);
                    System.out.print(prompt);
                    CLILogMgr.setDebugMessage("CLIUSER", "######################## Authenticating with KBI Authentication prompt :: " + prompt + " ### answer :: " + password, 2, null);
                }
                return true;
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}
