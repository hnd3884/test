package com.me.devicemanagement.framework.server.authentication;

public class CredentialAPI
{
    public String loginName;
    public String domainName;
    public String passWord;
    public String serviceName;
    
    public CredentialAPI(final String loginName, final String passWord) {
        this.loginName = loginName;
        this.passWord = passWord;
        this.domainName = null;
        this.serviceName = "System";
    }
    
    public CredentialAPI(final String loginName, final String passWord, final String domainName) {
        this(loginName, passWord);
        this.domainName = domainName;
    }
}
