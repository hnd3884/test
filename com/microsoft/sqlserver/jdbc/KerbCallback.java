package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.util.Arrays;
import java.util.Properties;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

public class KerbCallback implements CallbackHandler
{
    private final SQLServerConnection con;
    private String usernameRequested;
    
    KerbCallback(final SQLServerConnection con) {
        this.usernameRequested = null;
        this.con = con;
    }
    
    private static String getAnyOf(final Callback callback, final Properties properties, final String... names) throws UnsupportedCallbackException {
        for (final String name : names) {
            final String val = properties.getProperty(name);
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
        }
        throw new UnsupportedCallbackException(callback, "Cannot get any of properties: " + Arrays.toString(names) + " from con properties");
    }
    
    public String getUsernameRequested() {
        return this.usernameRequested;
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                this.usernameRequested = getAnyOf(callback, this.con.activeConnectionProperties, "user", SQLServerDriverStringProperty.USER.name());
                ((NameCallback)callback).setName(this.usernameRequested);
            }
            else {
                if (!(callback instanceof PasswordCallback)) {
                    throw new UnsupportedCallbackException(callback, "Unrecognized Callback type: " + callback.getClass());
                }
                final String password = getAnyOf(callback, this.con.activeConnectionProperties, "password", SQLServerDriverStringProperty.PASSWORD.name());
                ((PasswordCallback)callback).setPassword(password.toCharArray());
            }
        }
    }
}
