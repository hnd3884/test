package com.sun.mail.pop3;

import javax.mail.URLName;
import javax.mail.Session;

public class POP3SSLStore extends POP3Store
{
    public POP3SSLStore(final Session session, final URLName url) {
        super(session, url, "pop3s", true);
    }
}
