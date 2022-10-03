package com.adventnet.iam.security;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthHeader
{
    private static final Logger LOGGER;
    public static final String SCHEME_CREDENTIAL_DELIMITER = "\\s";
    private String scheme;
    private String credential;
    
    public AuthHeader(String header) {
        this.scheme = null;
        this.credential = null;
        header = header.trim();
        final String[] authorizationValues = header.split("\\s");
        final int length = authorizationValues.length;
        if (length == 2) {
            this.scheme = authorizationValues[0];
            this.credential = authorizationValues[1];
        }
        else {
            AuthHeader.LOGGER.log(Level.SEVERE, "Authorization Header with invalid value passed. Length after split : {0}", length);
        }
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public String getCredential() {
        return this.credential;
    }
    
    static {
        LOGGER = Logger.getLogger(AuthHeader.class.getName());
    }
}
