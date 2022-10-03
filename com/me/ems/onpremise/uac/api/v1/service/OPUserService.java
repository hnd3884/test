package com.me.ems.onpremise.uac.api.v1.service;

import javax.security.auth.login.LoginException;
import com.adventnet.authentication.lm.Authenticator;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.onpremise.server.authentication.ADAuthenticatorAPI;
import com.me.devicemanagement.onpremise.server.authentication.LocalAuthenticatorAPI;
import com.me.devicemanagement.framework.server.authentication.CredentialAPI;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.ems.framework.uac.api.v1.service.CoreUserService;

public class OPUserService extends CoreUserService
{
    private static Logger logger;
    private static OPUserService oPUserService;
    
    public static OPUserService getInstance() {
        if (OPUserService.oPUserService == null) {
            OPUserService.oPUserService = new OPUserService();
        }
        return OPUserService.oPUserService;
    }
    
    public User validateAndAuthenticateUser(final String userName, final String password, final String domainName, final String authType) throws LoginException {
        final CredentialAPI credential = new CredentialAPI(userName, password, domainName);
        final Authenticator authenticator = (Authenticator)((domainName == null || domainName.equalsIgnoreCase("local")) ? new LocalAuthenticatorAPI(credential) : new ADAuthenticatorAPI(credential, null));
        User dcUser = null;
        if (authenticator.authenticate()) {
            dcUser = this.getLoginDataForUser(userName, domainName);
        }
        return dcUser;
    }
    
    static {
        OPUserService.logger = Logger.getLogger(OPUserService.class.getName());
    }
}
