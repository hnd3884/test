package com.me.devicemanagement.onpremise.server.authentication;

import javax.security.auth.login.LoginException;
import org.json.JSONException;
import com.adventnet.authentication.lm.Authenticator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.authentication.CredentialAPI;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.authentication.AuthHandlerAPI;
import com.me.devicemanagement.framework.server.authentication.DCUserConstants;

public class UserAuthenticationHandler extends DCUserConstants implements AuthHandlerAPI
{
    public boolean login(final JSONObject loginProp) throws JSONException, LoginException, SyMException {
        final String userName = String.valueOf(loginProp.get("UserName"));
        final String password = String.valueOf(loginProp.get("Password"));
        final int authMode = loginProp.getInt("AuthMode");
        final String domainName = loginProp.optString("DomainName", (String)null);
        final CredentialAPI credential = new CredentialAPI(userName, password, domainName);
        Authenticator authenticator = null;
        switch (authMode) {
            case 2: {
                authenticator = (Authenticator)new ADAuthenticatorAPI(credential, (HttpServletRequest)loginProp.get("HTTPRequest"));
                break;
            }
            case 1: {
                authenticator = new LocalAuthenticatorAPI(credential);
                break;
            }
            default: {
                throw new SyMException(1001, "Unsuported Auth Type Only AD and Local supported", (Throwable)null);
            }
        }
        return authenticator.login();
    }
}
