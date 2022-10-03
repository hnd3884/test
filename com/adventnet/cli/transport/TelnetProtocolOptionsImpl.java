package com.adventnet.cli.transport;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.cli.ConnectionHandler;
import java.io.Serializable;

public class TelnetProtocolOptionsImpl implements CLIProtocolOptions, Serializable
{
    String remoteHost;
    private String loginMessage;
    int remotePort;
    String loginName;
    String password;
    String prompt;
    String[] promptStrings;
    String loginPrompt;
    String passwdPrompt;
    int loginTimeout;
    private boolean performPing;
    private String termType;
    private boolean checkStatus;
    private ConnectionHandler chandler;
    private String chandlerName;
    private String loginPrefix;
    private String loginPrefixPrompt;
    
    public TelnetProtocolOptionsImpl() {
        this.remoteHost = "localhost";
        this.loginMessage = null;
        this.remotePort = 23;
        this.loginName = null;
        this.password = null;
        this.prompt = null;
        this.promptStrings = null;
        this.loginPrompt = null;
        this.passwdPrompt = null;
        this.loginTimeout = 15000;
        this.performPing = false;
        this.termType = "dumb";
        this.checkStatus = false;
        this.chandlerName = null;
        this.loginPrefix = null;
        this.loginPrefixPrompt = null;
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
    
    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public void setPromptList(final String[] promptStrings) {
        this.promptStrings = promptStrings;
    }
    
    public String[] getPromptList() {
        return this.promptStrings;
    }
    
    public Object clone() {
        final TelnetProtocolOptionsImpl telnetProtocolOptionsImpl = new TelnetProtocolOptionsImpl();
        if (this.remoteHost != null) {
            telnetProtocolOptionsImpl.remoteHost = new String(this.remoteHost);
        }
        telnetProtocolOptionsImpl.remotePort = this.remotePort;
        if (this.promptStrings != null) {
            telnetProtocolOptionsImpl.promptStrings = this.promptStrings;
        }
        if (this.prompt != null) {
            telnetProtocolOptionsImpl.prompt = new String(this.prompt);
        }
        if (this.loginName != null) {
            telnetProtocolOptionsImpl.loginName = new String(this.loginName);
        }
        if (this.password != null) {
            telnetProtocolOptionsImpl.password = new String(this.password);
        }
        if (this.loginPrompt != null) {
            telnetProtocolOptionsImpl.loginPrompt = new String(this.loginPrompt);
        }
        if (this.passwdPrompt != null) {
            telnetProtocolOptionsImpl.passwdPrompt = new String(this.passwdPrompt);
        }
        if (this.loginPrefix != null) {
            telnetProtocolOptionsImpl.loginPrefix = new String(this.loginPrefix);
        }
        if (this.loginPrefixPrompt != null) {
            telnetProtocolOptionsImpl.loginPrefixPrompt = new String(this.loginPrefixPrompt);
        }
        telnetProtocolOptionsImpl.loginTimeout = this.loginTimeout;
        telnetProtocolOptionsImpl.performPing = this.performPing;
        telnetProtocolOptionsImpl.checkStatus = this.checkStatus;
        if (this.termType != null) {
            telnetProtocolOptionsImpl.termType = new String(this.termType);
        }
        if (this.chandlerName != null) {
            telnetProtocolOptionsImpl.chandlerName = this.chandlerName;
        }
        if (this.chandler != null) {
            telnetProtocolOptionsImpl.chandler = this.chandler;
        }
        return telnetProtocolOptionsImpl;
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
    
    public int getLoginTimeout() {
        return this.loginTimeout;
    }
    
    public void setLoginTimeout(final int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }
    
    public String getInitialMessage() {
        return this.loginMessage;
    }
    
    public void setInitialMessage(final String loginMessage) {
        this.loginMessage = loginMessage;
    }
    
    public boolean isPerformPing() {
        return this.performPing;
    }
    
    public void setPerformPing(final boolean performPing) {
        this.performPing = performPing;
    }
    
    public void setTerminalType(final String termType) {
        this.termType = termType;
    }
    
    public String getTerminalType() {
        return this.termType;
    }
    
    public void checkServerStatus(final boolean checkStatus) {
        this.checkStatus = checkStatus;
    }
    
    public boolean isCheckServerStatus() {
        return this.checkStatus;
    }
    
    public void setProperties(final Properties properties) {
        if (properties.get("RemoteHost") != null && ((Hashtable<K, Object>)properties).get("RemoteHost").toString().trim().length() > 0) {
            this.setRemoteHost(((Hashtable<K, String>)properties).get("RemoteHost"));
        }
        if (properties.get("RemotePort") != null && ((Hashtable<K, Object>)properties).get("RemotePort").toString().trim().length() > 0) {
            this.setRemotePort(new Integer(((Hashtable<K, Object>)properties).get("RemotePort").toString()));
        }
        if (properties.get("LoginName") != null && ((Hashtable<K, Object>)properties).get("LoginName").toString().trim().length() > 0) {
            this.setLoginName(((Hashtable<K, String>)properties).get("LoginName"));
        }
        if (properties.get("Password") != null && ((Hashtable<K, Object>)properties).get("Password").toString().trim().length() > 0) {
            this.setPassword(((Hashtable<K, String>)properties).get("Password"));
        }
        if (properties.get("Prompt") != null && ((Hashtable<K, Object>)properties).get("Prompt").toString().trim().length() > 0) {
            this.setPrompt(((Hashtable<K, String>)properties).get("Prompt"));
        }
        if (properties.get("LoginPrompt") != null && ((Hashtable<K, Object>)properties).get("LoginPrompt").toString().trim().length() > 0) {
            this.setLoginPrompt(((Hashtable<K, String>)properties).get("LoginPrompt"));
        }
        if (properties.get("PasswdPrompt") != null && ((Hashtable<K, Object>)properties).get("PasswdPrompt").toString().length() > 0) {
            this.setPasswdPrompt(((Hashtable<K, String>)properties).get("PasswdPrompt"));
        }
        if (properties.get("LoginTimeout") != null && ((Hashtable<K, Object>)properties).get("LoginTimeout").toString().trim().length() > 0) {
            this.setLoginTimeout(new Integer(((Hashtable<K, Object>)properties).get("LoginTimeout").toString().trim()));
        }
        if (properties.get("PerformPing") != null && ((Hashtable<K, Object>)properties).get("PerformPing").toString().trim().length() > 0) {
            this.setPerformPing(new Boolean(((Hashtable<K, Object>)properties).remove("PerformPing").toString()));
        }
        if (properties.get("TerminalType") != null && ((Hashtable<K, Object>)properties).get("TerminalType").toString().trim().length() > 0) {
            this.setTerminalType(((Hashtable<K, String>)properties).get("TerminalType"));
        }
        if (properties.get("CheckServerStatus") != null && ((Hashtable<K, Object>)properties).get("CheckServerStatus").toString().trim().length() > 0) {
            this.checkServerStatus(new Boolean(((Hashtable<K, Object>)properties).remove("CheckServerStatus").toString()));
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
        if (this.getLoginPrompt() != null) {
            properties.setProperty("LoginPrompt", this.getLoginPrompt());
        }
        if (this.getPasswdPrompt() != null) {
            properties.setProperty("PasswdPrompt", this.getPasswdPrompt());
        }
        properties.setProperty("LoginTimeout", new Integer(this.getLoginTimeout()).toString());
        properties.setProperty("PerformPing", new Boolean(this.isPerformPing()).toString());
        if (this.getTerminalType() != null) {
            properties.setProperty("TerminalType", this.getTerminalType());
        }
        properties.setProperty("CheckServerStatus", new Boolean(this.isCheckServerStatus()).toString());
        if (this.getConnectionHandler() != null) {
            properties.setProperty("ConnectionHandler", this.getConnectionHandler().getClass().getName());
        }
        if (this.getLoginPrefix() != null) {
            properties.setProperty("LoginPrefix", this.getLoginPrefix());
        }
        if (this.getLoginPrefixPrompt() != null) {
            properties.setProperty("LoginPrefixPrompt", this.getLoginPrefixPrompt());
        }
        return properties;
    }
    
    public void setConnectionHandler(final ConnectionHandler chandler) {
        this.chandler = chandler;
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
    
    public void setConnectionHandlerClassName(final String chandlerName) {
        this.chandlerName = chandlerName;
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
}
