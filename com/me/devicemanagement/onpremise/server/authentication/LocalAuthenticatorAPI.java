package com.me.devicemanagement.onpremise.server.authentication;

import javax.security.auth.login.LoginException;
import com.me.devicemanagement.framework.server.authentication.CredentialAPI;
import com.adventnet.authentication.lm.Authenticator;

public class LocalAuthenticatorAPI extends Authenticator
{
    public LocalAuthenticatorAPI(final CredentialAPI credential) {
        this.loginName = credential.loginName;
        this.domainName = credential.domainName;
        this.password = credential.passWord;
        this.serviceName = credential.serviceName;
    }
    
    public boolean login() throws LoginException {
        return super.authenticate();
    }
}
