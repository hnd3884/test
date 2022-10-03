package com.sun.mail.imap;

import javax.mail.Provider;

public class IMAPSSLProvider extends Provider
{
    public IMAPSSLProvider() {
        super(Type.STORE, "imaps", IMAPSSLStore.class.getName(), "Oracle", null);
    }
}
