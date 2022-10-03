package com.sun.mail.pop3;

import javax.mail.Provider;

public class POP3SSLProvider extends Provider
{
    public POP3SSLProvider() {
        super(Type.STORE, "pop3s", POP3SSLStore.class.getName(), "Oracle", null);
    }
}
