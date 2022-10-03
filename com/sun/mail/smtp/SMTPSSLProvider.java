package com.sun.mail.smtp;

import javax.mail.Provider;

public class SMTPSSLProvider extends Provider
{
    public SMTPSSLProvider() {
        super(Type.TRANSPORT, "smtps", SMTPSSLTransport.class.getName(), "Oracle", null);
    }
}
