package com.adventnet.cli.transport;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.File;

public class Credentials
{
    private static final String PROMPT_COLON = ":";
    private static final String PROMPT_DOLLAR = "$";
    private static final String PROMPT_HASH = "#";
    public static final String PROTOCOL_TELNET = "TELNET";
    public static final String PROTOCOL_SSH = "SSH";
    public static final int DEFAULT_TELNET_PORT = 23;
    public static final int DEFAULT_SSH_PORT = 22;
    private Credentials alternate;
    private String host;
    private String protocol;
    private int port;
    private String loginName;
    private String password;
    private File privateKeyFile;
    private byte[] privateKeyContents;
    private String pkPassPhrase;
    private String[] loginPrompt;
    private String[] passwordPrompt;
    private String[] prompt;
    private String commandSuffix;
    private String suCommand;
    private String rootLogin;
    private String rootPassword;
    private String[] rootLoginPrompt;
    private String[] rootPasswordPrompt;
    private String[] rootPrompt;
    
    public Credentials(final String s, String trim, final String s2, final String s3, final String s4, final String[] prompt) throws UnsupportedEncodingException {
        this.alternate = null;
        this.host = null;
        this.protocol = "SSH";
        this.port = -1;
        this.loginName = "";
        this.password = "";
        this.privateKeyFile = null;
        this.privateKeyContents = null;
        this.pkPassPhrase = null;
        this.loginPrompt = new String[] { ":" };
        this.passwordPrompt = new String[] { ":" };
        this.prompt = new String[] { "$" };
        this.commandSuffix = "\r\n";
        this.suCommand = null;
        this.rootLogin = null;
        this.rootPassword = null;
        this.rootLoginPrompt = new String[] { ":" };
        this.rootPasswordPrompt = new String[] { ":" };
        this.rootPrompt = new String[] { "#" };
        if (s == null) {
            throw new IllegalArgumentException("Protocol can not be null");
        }
        if (s.equalsIgnoreCase("TELNET")) {
            this.protocol = "TELNET";
        }
        else {
            if (!s.equalsIgnoreCase("SSH")) {
                throw new IllegalArgumentException("Protocol should be either 'SSH' or 'TELNET'");
            }
            this.protocol = "SSH";
        }
        if (trim != null && !trim.equals("")) {
            trim = trim.trim();
            this.host = ((trim.indexOf(":") > -1) ? ("[" + trim + "]") : trim);
            if (s2 != null) {
                this.loginName = URLEncoder.encode(s2, "UTF-8");
            }
            if (s3 != null) {
                this.password = URLEncoder.encode(s3, "UTF-8");
            }
            int port;
            try {
                port = ((s4 != null && !"".equals(s4)) ? Integer.parseInt(s4) : -1);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("Port number should not containg alphabets or special characters");
            }
            if (port > 0 && port <= 65535) {
                this.port = port;
            }
            else {
                if (this.port != -1) {
                    throw new IllegalArgumentException("Invalid port number");
                }
                if (s.equalsIgnoreCase("TELNET")) {
                    this.port = 23;
                }
                else {
                    this.port = 22;
                }
            }
            if (prompt != null && prompt.length > 0) {
                this.prompt = prompt;
            }
            return;
        }
        throw new IllegalArgumentException("Host name can not be null and can not be empty");
    }
    
    public void setLoginName(final String s) throws UnsupportedEncodingException {
        if (s != null) {
            this.loginName = URLEncoder.encode(s, "UTF-8");
        }
    }
    
    public String getLoginName() throws UnsupportedEncodingException {
        if (this.loginName != null) {
            return URLDecoder.decode(this.loginName, "UTF-8");
        }
        return null;
    }
    
    public void setHost(String trim) {
        if (trim != null && !trim.equals("")) {
            trim = trim.trim();
            this.host = ((trim.indexOf(":") > -1) ? ("[" + trim + "]") : trim);
            return;
        }
        throw new IllegalArgumentException("Host name can not be null and can not be empty");
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setPassword(final String s) throws UnsupportedEncodingException {
        if (s != null) {
            this.password = URLEncoder.encode(s, "UTF-8");
        }
    }
    
    public String getPassword() throws UnsupportedEncodingException {
        if (this.password != null) {
            return URLDecoder.decode(this.password, "UTF-8");
        }
        return null;
    }
    
    public void setPKFile(final File privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }
    
    public File getPKFile() {
        return this.privateKeyFile;
    }
    
    public void setPKContents(final byte[] privateKeyContents) {
        this.privateKeyContents = privateKeyContents;
    }
    
    public byte[] getPKContents() {
        return this.privateKeyContents;
    }
    
    public void setPKPassPhrase(final String pkPassPhrase) {
        this.pkPassPhrase = pkPassPhrase;
    }
    
    public String getPKPassPhrase() {
        return this.pkPassPhrase;
    }
    
    public void setLoginPrompt(final String[] loginPrompt) {
        if (loginPrompt != null && loginPrompt.length > 0) {
            this.loginPrompt = loginPrompt;
        }
    }
    
    public String[] getLoginPrompt() {
        return this.loginPrompt;
    }
    
    public void setPasswordPrompt(final String[] passwordPrompt) {
        if (passwordPrompt != null && passwordPrompt.length > 0) {
            this.passwordPrompt = passwordPrompt;
        }
    }
    
    public String[] getPasswordPrompt() {
        return this.passwordPrompt;
    }
    
    public void setProtocol(final String s) {
        if (s != null) {
            if (s.equalsIgnoreCase("TELNET")) {
                if (this.port == -1) {
                    this.port = 23;
                }
                this.protocol = "TELNET";
            }
            else {
                if (!s.equalsIgnoreCase("SSH")) {
                    throw new IllegalArgumentException("Protocol should be either 'SSH' or 'TELNET'");
                }
                if (this.port == -1) {
                    this.port = 22;
                }
                this.protocol = "SSH";
            }
            return;
        }
        throw new IllegalArgumentException("Protocol can not be null");
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public void setPort(final int port) {
        if (port > 0 && port <= 65535) {
            this.port = port;
            return;
        }
        throw new IllegalArgumentException("Invalid port number");
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPrompt(final String[] prompt) {
        if (prompt != null && prompt.length > 0) {
            this.prompt = prompt;
        }
    }
    
    public String[] getPrompt() {
        return this.prompt;
    }
    
    public void setCommandSuffix(final String commandSuffix) {
        if (commandSuffix == null) {
            this.commandSuffix = "\r\n";
        }
        else {
            this.commandSuffix = commandSuffix;
        }
    }
    
    public String getCommandSuffix() {
        return this.commandSuffix;
    }
    
    public void setSuperUserCommand(final String s) throws UnsupportedEncodingException {
        if (s != null && !s.equals("")) {
            this.suCommand = URLEncoder.encode(s, "UTF-8");
        }
    }
    
    public String getSuperUserCommand() throws UnsupportedEncodingException {
        if (this.suCommand != null) {
            return URLDecoder.decode(this.suCommand, "UTF-8");
        }
        return null;
    }
    
    public void setRootLoginName(final String s) throws UnsupportedEncodingException {
        if (s != null && !s.equals("")) {
            this.rootLogin = URLEncoder.encode(s, "UTF-8");
        }
    }
    
    public String getRootLoginName() throws UnsupportedEncodingException {
        if (this.rootLogin != null) {
            return URLDecoder.decode(this.rootLogin, "UTF-8");
        }
        return null;
    }
    
    public void setRootPassword(final String s) throws UnsupportedEncodingException {
        if (s != null && !s.equals("")) {
            this.rootPassword = URLEncoder.encode(s, "UTF-8");
        }
    }
    
    public String getRootPassword() throws UnsupportedEncodingException {
        if (this.rootPassword != null) {
            return URLDecoder.decode(this.rootPassword, "UTF-8");
        }
        return null;
    }
    
    public void setRootLoginPrompt(final String[] rootLoginPrompt) {
        if (rootLoginPrompt != null && rootLoginPrompt.length > 0) {
            this.rootLoginPrompt = rootLoginPrompt;
        }
    }
    
    public String[] getRootLoginPrompt() {
        return this.rootLoginPrompt;
    }
    
    public void setRootPasswordPrompt(final String[] rootPasswordPrompt) {
        if (rootPasswordPrompt != null && rootPasswordPrompt.length > 0) {
            this.rootPasswordPrompt = rootPasswordPrompt;
        }
    }
    
    public String[] getRootPasswordPrompt() {
        return this.rootPasswordPrompt;
    }
    
    public void setRootPrompt(final String[] rootPrompt) {
        if (rootPrompt != null && rootPrompt.length > 0) {
            this.rootPrompt = rootPrompt;
        }
    }
    
    public String[] getRootPrompt() {
        return this.rootPrompt;
    }
    
    public void setAlternateServer(final Credentials alternate) {
        this.alternate = alternate;
    }
    
    public Credentials getAlternateServer() {
        return this.alternate;
    }
}
