package com.sun.mail.pop3;

import javax.mail.Message;
import javax.mail.Flags;
import javax.mail.MethodNotSupportedException;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Folder;

public class DefaultFolder extends Folder
{
    DefaultFolder(final POP3Store store) {
        super(store);
    }
    
    @Override
    public String getName() {
        return "";
    }
    
    @Override
    public String getFullName() {
        return "";
    }
    
    @Override
    public Folder getParent() {
        return null;
    }
    
    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public Folder[] list(final String pattern) throws MessagingException {
        final Folder[] f = { this.getInbox() };
        return f;
    }
    
    @Override
    public char getSeparator() {
        return '/';
    }
    
    @Override
    public int getType() {
        return 2;
    }
    
    @Override
    public boolean create(final int type) throws MessagingException {
        return false;
    }
    
    @Override
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }
    
    @Override
    public Folder getFolder(final String name) throws MessagingException {
        if (!name.equalsIgnoreCase("INBOX")) {
            throw new MessagingException("only INBOX supported");
        }
        return this.getInbox();
    }
    
    protected Folder getInbox() throws MessagingException {
        return this.getStore().getFolder("INBOX");
    }
    
    @Override
    public boolean delete(final boolean recurse) throws MessagingException {
        throw new MethodNotSupportedException("delete");
    }
    
    @Override
    public boolean renameTo(final Folder f) throws MessagingException {
        throw new MethodNotSupportedException("renameTo");
    }
    
    @Override
    public void open(final int mode) throws MessagingException {
        throw new MethodNotSupportedException("open");
    }
    
    @Override
    public void close(final boolean expunge) throws MessagingException {
        throw new MethodNotSupportedException("close");
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
    
    @Override
    public Flags getPermanentFlags() {
        return new Flags();
    }
    
    @Override
    public int getMessageCount() throws MessagingException {
        return 0;
    }
    
    @Override
    public Message getMessage(final int msgno) throws MessagingException {
        throw new MethodNotSupportedException("getMessage");
    }
    
    @Override
    public void appendMessages(final Message[] msgs) throws MessagingException {
        throw new MethodNotSupportedException("Append not supported");
    }
    
    @Override
    public Message[] expunge() throws MessagingException {
        throw new MethodNotSupportedException("expunge");
    }
}
