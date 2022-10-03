package com.me.devicemanagement.onpremise.server.authentication;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.authentication.CredentialAPI;
import com.adventnet.authentication.lm.ADAuthenticator;

public class ADAuthenticatorAPI extends ADAuthenticator
{
    public ADAuthenticatorAPI(final CredentialAPI credential, final HttpServletRequest request) {
        this.loginName = credential.loginName;
        this.password = credential.passWord;
        this.serviceName = credential.serviceName;
        this.request = request;
    }
    
    public boolean login() throws LoginException {
        return super.authenticate();
    }
}
