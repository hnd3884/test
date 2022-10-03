package com.me.devicemanagement.framework.server.mailmanager;

import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;

public class MailServerAuthenticator extends Authenticator
{
    private String username;
    private String password;
    
    public MailServerAuthenticator(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
    
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }
}
