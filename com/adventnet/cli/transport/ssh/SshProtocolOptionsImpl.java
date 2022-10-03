package com.adventnet.cli.transport.ssh;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.cli.transport.Credentials;
import com.adventnet.cli.ConnectionHandler;
import java.io.File;
import java.io.Serializable;
import com.adventnet.cli.transport.CLIProtocolOptions;

public class SshProtocolOptionsImpl implements CLIProtocolOptions, Serializable
{
    private String remoteHost;
    private String loginMessage;
    private int remotePort;
    private String loginName;
    private String password;
    private File privateKeyFile;
    private byte[] privateKeyData;
    private String pkPassPhrase;
    private String prompt;
    String[] promptStrings;
    private String terminalType;
    private boolean terminalEcho;
    private int loginTimeout;
    private boolean isOPMSession;
    private ConnectionHandler chandler;
    private String chandlerName;
    private String loginPrefix;
    private String loginPrefixPrompt;
    private Credentials[] credentialArr;
    private String blockedCiphers;
    
    public SshProtocolOptionsImpl() {
        this.remoteHost = "localhost";
        this.loginMessage = null;
        this.remotePort = 22;
        this.loginName = null;
        this.password = null;
        this.privateKeyFile = null;
        this.privateKeyData = null;
        this.pkPassPhrase = null;
        this.prompt = null;
        this.promptStrings = null;
        this.terminalType = "dumb";
        this.terminalEcho = true;
        this.loginTimeout = 15000;
        this.isOPMSession = false;
        this.chandlerName = null;
        this.loginPrefix = null;
        this.loginPrefixPrompt = null;
        this.credentialArr = null;
        this.blockedCiphers = null;
    }
    
    public void setRemoteHost(final String remoteHost) {
        this.remoteHost = remoteHost;
    }
    
    public String getRemoteHost() {
        return this.remoteHost;
    }
    
    public void setRemotePort(final int remotePort) {
        this.remotePort = remotePort;
    }
    
    public int getRemotePort() {
        return this.remotePort;
    }
    
    public Object getID() {
        return this.remoteHost + "##" + String.valueOf(this.remotePort) + "##" + this.loginName;
    }
    
    public void setLoginName(final String loginName) {
        this.loginName = loginName;
    }
    
    public String getLoginName() {
        return this.loginName;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPrivateKeyFile(final File privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }
    
    public File getPrivateKeyFile() {
        return this.privateKeyFile;
    }
    
    public void setPrivateKeyData(final byte[] privateKeyData) {
        this.privateKeyData = privateKeyData;
    }
    
    public byte[] getPrivateKeyData() {
        return this.privateKeyData;
    }
    
    public void setPrivateKeyData(final String s) {
        this.privateKeyData = s.getBytes();
    }
    
    public void setPKPassPhrase(final String pkPassPhrase) {
        this.pkPassPhrase = pkPassPhrase;
    }
    
    public String getPKPassPhrase() {
        return this.pkPassPhrase;
    }
    
    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }
    
    public void setPromptList(final String[] promptStrings) {
        this.promptStrings = promptStrings;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public String[] getPromptList() {
        return this.promptStrings;
    }
    
    public void setTerminalType(final String terminalType) {
        this.terminalType = terminalType;
    }
    
    public String getTerminalType() {
        return this.terminalType;
    }
    
    public void setTerminalEcho(final boolean terminalEcho) {
        this.terminalEcho = terminalEcho;
    }
    
    public boolean getTerminalEcho() {
        return this.terminalEcho;
    }
    
    public Object clone() {
        final SshProtocolOptionsImpl sshProtocolOptionsImpl = new SshProtocolOptionsImpl();
        if (this.remoteHost != null) {
            sshProtocolOptionsImpl.remoteHost = new String(this.remoteHost);
        }
        sshProtocolOptionsImpl.remotePort = this.remotePort;
        if (this.promptStrings != null) {
            sshProtocolOptionsImpl.promptStrings = this.promptStrings;
        }
        if (this.prompt != null) {
            sshProtocolOptionsImpl.prompt = new String(this.prompt);
        }
        if (this.loginName != null) {
            sshProtocolOptionsImpl.loginName = new String(this.loginName);
        }
        if (this.password != null) {
            sshProtocolOptionsImpl.password = new String(this.password);
        }
        if (this.terminalType != null) {
            sshProtocolOptionsImpl.terminalType = new String(this.terminalType);
        }
        if (this.chandlerName != null) {
            sshProtocolOptionsImpl.chandlerName = this.chandlerName;
        }
        if (this.chandler != null) {
            sshProtocolOptionsImpl.chandler = this.chandler;
        }
        if (this.loginPrefix != null) {
            sshProtocolOptionsImpl.loginPrefix = new String(this.loginPrefix);
        }
        if (this.loginPrefixPrompt != null) {
            sshProtocolOptionsImpl.loginPrefixPrompt = new String(this.loginPrefixPrompt);
        }
        if (this.credentialArr != null) {
            sshProtocolOptionsImpl.credentialArr = this.credentialArr;
        }
        if (this.blockedCiphers != null) {
            sshProtocolOptionsImpl.blockedCiphers = this.blockedCiphers;
        }
        if (this.privateKeyFile != null) {
            sshProtocolOptionsImpl.privateKeyFile = this.privateKeyFile;
        }
        if (this.privateKeyData != null) {
            sshProtocolOptionsImpl.privateKeyData = this.privateKeyData;
        }
        if (this.pkPassPhrase != null) {
            sshProtocolOptionsImpl.pkPassPhrase = this.pkPassPhrase;
        }
        sshProtocolOptionsImpl.loginTimeout = this.loginTimeout;
        sshProtocolOptionsImpl.isOPMSession = this.isOPMSession;
        sshProtocolOptionsImpl.terminalEcho = this.terminalEcho;
        return sshProtocolOptionsImpl;
    }
    
    public String getInitialMessage() {
        return this.loginMessage;
    }
    
    public void setInitialMessage(final String loginMessage) {
        this.loginMessage = loginMessage;
    }
    
    public int getLoginTimeout() {
        return this.loginTimeout;
    }
    
    public void setLoginTimeout(final int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }
    
    public void setOPMSession(final boolean isOPMSession) {
        this.isOPMSession = isOPMSession;
    }
    
    public boolean getOPMSession() {
        return this.isOPMSession;
    }
    
    public void setConnectionHandler(final ConnectionHandler chandler) {
        this.chandler = chandler;
    }
    
    public void setConnectionHandlerClassName(final String chandlerName) {
        this.chandlerName = chandlerName;
    }
    
    public ConnectionHandler getConnectionHandler() {
        if (this.chandler == null && this.chandlerName != null) {
            try {
                this.chandler = (ConnectionHandler)Class.forName(this.chandlerName).newInstance();
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
            catch (final NoClassDefFoundError noClassDefFoundError) {
                return null;
            }
            catch (final InstantiationException ex2) {
                System.err.println(" Exception occured while instantiating connection handler " + ex2.getMessage());
            }
            catch (final IllegalAccessException ex3) {
                System.err.println(" Exception occured while instantiating connection handler " + ex3.getMessage());
            }
        }
        return this.chandler;
    }
    
    public String getLoginPrefix() {
        return this.loginPrefix;
    }
    
    public void setLoginPrefix(final String loginPrefix) {
        this.loginPrefix = loginPrefix;
    }
    
    public String getLoginPrefixPrompt() {
        return this.loginPrefixPrompt;
    }
    
    public void setLoginPrefixPrompt(final String loginPrefixPrompt) {
        this.loginPrefixPrompt = loginPrefixPrompt;
    }
    
    public void setProperties(final Properties properties) {
        if (properties.get("RemoteHost") != null && ((Hashtable<K, Object>)properties).get("RemoteHost").toString().length() > 0) {
            this.setRemoteHost(((Hashtable<K, String>)properties).get("RemoteHost"));
        }
        if (properties.get("RemotePort") != null && ((Hashtable<K, Object>)properties).get("RemotePort").toString().length() > 0) {
            this.setRemotePort(new Integer(((Hashtable<K, Object>)properties).get("RemotePort").toString()));
        }
        if (properties.get("LoginName") != null && ((Hashtable<K, Object>)properties).get("LoginName").toString().length() > 0) {
            this.setLoginName(((Hashtable<K, String>)properties).get("LoginName"));
        }
        if (properties.get("Password") != null && ((Hashtable<K, Object>)properties).get("Password").toString().length() > 0) {
            this.setPassword(((Hashtable<K, String>)properties).get("Password"));
        }
        if (properties.get("PrivateKeyData") != null && ((Hashtable<K, Object>)properties).get("PrivateKeyData").toString().length() > 0) {
            this.setPrivateKeyData(((Hashtable<K, String>)properties).get("PrivateKeyData"));
        }
        if (properties.get("PKPassPhrase") != null && ((Hashtable<K, Object>)properties).get("PKPassPhrase").toString().length() > 0) {
            this.setPKPassPhrase(((Hashtable<K, String>)properties).get("PKPassPhrase"));
        }
        if (properties.get("Prompt") != null && ((Hashtable<K, Object>)properties).get("Prompt").toString().length() > 0) {
            this.setPrompt(((Hashtable<K, String>)properties).get("Prompt"));
        }
        if (properties.get("TerminalType") != null && ((Hashtable<K, Object>)properties).get("TerminalType").toString().length() > 0) {
            this.setTerminalType(((Hashtable<K, String>)properties).get("TerminalType"));
        }
        if (properties.get("LoginTimeout") != null && ((Hashtable<K, Object>)properties).get("LoginTimeout").toString().length() > 0) {
            this.setLoginTimeout(new Integer(((Hashtable<K, Object>)properties).get("LoginTimeout").toString()));
        }
        if (properties.get("ConnectionHandler") != null && ((Hashtable<K, Object>)properties).get("ConnectionHandler").toString().trim().length() > 0) {
            this.setConnectionHandlerClassName(((Hashtable<K, String>)properties).get("ConnectionHandler"));
        }
        if (properties.get("LoginPrefix") != null && ((Hashtable<K, Object>)properties).get("LoginPrefix").toString().length() > 0) {
            this.setLoginPrefix(((Hashtable<K, String>)properties).get("LoginPrefix"));
        }
        if (properties.get("LoginPrefixPrompt") != null && ((Hashtable<K, Object>)properties).get("LoginPrefixPrompt").toString().length() > 0) {
            this.setLoginPrefixPrompt(((Hashtable<K, String>)properties).get("LoginPrefixPrompt"));
        }
        if (properties.get("BlockedCiphers") != null && ((Hashtable<K, Object>)properties).get("BlockedCiphers").toString().length() > 0) {
            this.setBlockedCiphers(((Hashtable<K, String>)properties).get("BlockedCiphers"));
        }
    }
    
    public Properties getProperties() {
        final Properties properties = new Properties();
        if (this.getRemoteHost() != null) {
            properties.setProperty("RemoteHost", this.getRemoteHost());
        }
        properties.setProperty("RemotePort", new Integer(this.getRemotePort()).toString());
        if (this.getLoginName() != null) {
            properties.setProperty("LoginName", this.getLoginName());
        }
        if (this.getPassword() != null) {
            properties.setProperty("Password", this.getPassword());
        }
        if (this.getPrompt() != null) {
            properties.setProperty("Prompt", this.getPrompt());
        }
        if (this.getTerminalType() != null) {
            properties.setProperty("TerminalType", this.getTerminalType());
        }
        properties.setProperty("LoginTimeout", new Integer(this.getLoginTimeout()).toString());
        if (this.getConnectionHandler() != null) {
            properties.setProperty("ConnectionHandler", this.getConnectionHandler().getClass().getName());
        }
        if (this.getLoginPrefix() != null) {
            properties.setProperty("LoginPrefix", this.getLoginPrefix());
        }
        if (this.getLoginPrefixPrompt() != null) {
            properties.setProperty("LoginPrefixPrompt", this.getLoginPrefixPrompt());
        }
        if (this.getBlockedCiphers() != null) {
            properties.setProperty("BlockedCiphers", this.getBlockedCiphers());
        }
        return properties;
    }
    
    public void setCredentialsArray(final Credentials[] credentialArr) {
        this.credentialArr = credentialArr;
    }
    
    public Credentials[] getCredentialsArray() {
        return this.credentialArr;
    }
    
    public void setBlockedCiphers(final String blockedCiphers) {
        this.blockedCiphers = blockedCiphers;
    }
    
    public String getBlockedCiphers() {
        return this.blockedCiphers;
    }
}
