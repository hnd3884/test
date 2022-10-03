package com.sun.mail.imap;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.event.MessageCountEvent;

public class MessageVanishedEvent extends MessageCountEvent
{
    private long[] uids;
    private static final Message[] noMessages;
    private static final long serialVersionUID = 2142028010250024922L;
    
    public MessageVanishedEvent(final Folder folder, final long[] uids) {
        super(folder, 2, true, MessageVanishedEvent.noMessages);
        this.uids = uids;
    }
    
    public long[] getUIDs() {
        return this.uids;
    }
    
    static {
        noMessages = new Message[0];
    }
}
