package com.sun.mail.imap;

import javax.mail.Provider;

public class IMAPProvider extends Provider
{
    public IMAPProvider() {
        super(Type.STORE, "imap", IMAPStore.class.getName(), "Oracle", null);
    }
}
