package com.me.tools.zcutil;

import java.net.PasswordAuthentication;
import java.net.Authenticator;

public class ProxyAuthenticator extends Authenticator
{
    private String username;
    private String password;
    
    public ProxyAuthenticator(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password.toCharArray());
    }
}
