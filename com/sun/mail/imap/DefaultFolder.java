package com.sun.mail.imap;

import javax.mail.Message;
import javax.mail.MethodNotSupportedException;
import javax.mail.MessagingException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.ListInfo;
import javax.mail.Folder;

public class DefaultFolder extends IMAPFolder
{
    protected DefaultFolder(final IMAPStore store) {
        super("", '\uffff', store, null);
        this.exists = true;
        this.type = 2;
    }
    
    @Override
    public synchronized String getName() {
        return this.fullName;
    }
    
    @Override
    public Folder getParent() {
        return null;
    }
    
    @Override
    public synchronized Folder[] list(final String pattern) throws MessagingException {
        ListInfo[] li = null;
        li = (ListInfo[])this.doCommand(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.list("", pattern);
            }
        });
        if (li == null) {
            return new Folder[0];
        }
        final IMAPFolder[] folders = new IMAPFolder[li.length];
        for (int i = 0; i < folders.length; ++i) {
            folders[i] = ((IMAPStore)this.store).newIMAPFolder(li[i]);
        }
        return folders;
    }
    
    @Override
    public synchronized Folder[] listSubscribed(final String pattern) throws MessagingException {
        ListInfo[] li = null;
        li = (ListInfo[])this.doCommand(new ProtocolCommand() {
            @Override
            public Object doCommand(final IMAPProtocol p) throws ProtocolException {
                return p.lsub("", pattern);
            }
        });
        if (li == null) {
            return new Folder[0];
        }
        final IMAPFolder[] folders = new IMAPFolder[li.length];
        for (int i = 0; i < folders.length; ++i) {
            folders[i] = ((IMAPStore)this.store).newIMAPFolder(li[i]);
        }
        return folders;
    }
    
    @Override
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }
    
    @Override
    public Folder getFolder(final String name) throws MessagingException {
        return ((IMAPStore)this.store).newIMAPFolder(name, '\uffff');
    }
    
    @Override
    public boolean delete(final boolean recurse) throws MessagingException {
        throw new MethodNotSupportedException("Cannot delete Default Folder");
    }
    
    @Override
    public boolean renameTo(final Folder f) throws MessagingException {
        throw new MethodNotSupportedException("Cannot rename Default Folder");
    }
    
    @Override
    public void appendMessages(final Message[] msgs) throws MessagingException {
        throw new MethodNotSupportedException("Cannot append to Default Folder");
    }
    
    @Override
    public Message[] expunge() throws MessagingException {
        throw new MethodNotSupportedException("Cannot expunge Default Folder");
    }
}
