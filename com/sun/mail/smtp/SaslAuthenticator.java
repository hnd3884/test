package com.sun.mail.smtp;

import javax.mail.MessagingException;

public interface SaslAuthenticator
{
    boolean authenticate(final String[] p0, final String p1, final String p2, final String p3, final String p4) throws MessagingException;
}
