package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Locale;

enum AuthenticationScheme
{
    nativeAuthentication, 
    ntlm, 
    javaKerberos;
    
    static AuthenticationScheme valueOfString(final String value) throws SQLServerException {
        AuthenticationScheme scheme;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(AuthenticationScheme.javaKerberos.toString())) {
            scheme = AuthenticationScheme.javaKerberos;
        }
        else if (value.toLowerCase(Locale.US).equalsIgnoreCase(AuthenticationScheme.nativeAuthentication.toString())) {
            scheme = AuthenticationScheme.nativeAuthentication;
        }
        else {
            if (!value.toLowerCase(Locale.US).equalsIgnoreCase(AuthenticationScheme.ntlm.toString())) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidAuthenticationScheme"));
                final Object[] msgArgs = { value };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            scheme = AuthenticationScheme.ntlm;
        }
        return scheme;
    }
}
