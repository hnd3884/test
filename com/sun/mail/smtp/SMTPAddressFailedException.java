package com.sun.mail.smtp;

import javax.mail.internet.InternetAddress;
import javax.mail.SendFailedException;

public class SMTPAddressFailedException extends SendFailedException
{
    protected InternetAddress addr;
    protected String cmd;
    protected int rc;
    private static final long serialVersionUID = 804831199768630097L;
    
    public SMTPAddressFailedException(final InternetAddress addr, final String cmd, final int rc, final String err) {
        super(err);
        this.addr = addr;
        this.cmd = cmd;
        this.rc = rc;
    }
    
    public InternetAddress getAddress() {
        return this.addr;
    }
    
    public String getCommand() {
        return this.cmd;
    }
    
    public int getReturnCode() {
        return this.rc;
    }
}
