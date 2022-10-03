package com.sun.mail.pop3;

import javax.mail.Provider;

public class POP3Provider extends Provider
{
    public POP3Provider() {
        super(Type.STORE, "pop3", POP3Store.class.getName(), "Oracle", null);
    }
}
