package org.apache.commons.net.smtp;

import org.apache.commons.net.io.DotTerminatedMessageWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.io.IOException;

public class SMTPClient extends SMTP
{
    public SMTPClient() {
    }
    
    public SMTPClient(final String encoding) {
        super(encoding);
    }
    
    public boolean completePendingCommand() throws IOException {
        return SMTPReply.isPositiveCompletion(this.getReply());
    }
    
    public boolean login(final String hostname) throws IOException {
        return SMTPReply.isPositiveCompletion(this.helo(hostname));
    }
    
    public boolean login() throws IOException {
        final InetAddress host = this.getLocalAddress();
        final String name = host.getHostName();
        return name != null && SMTPReply.isPositiveCompletion(this.helo(name));
    }
    
    public boolean setSender(final RelayPath path) throws IOException {
        return SMTPReply.isPositiveCompletion(this.mail(path.toString()));
    }
    
    public boolean setSender(final String address) throws IOException {
        return SMTPReply.isPositiveCompletion(this.mail("<" + address + ">"));
    }
    
    public boolean addRecipient(final RelayPath path) throws IOException {
        return SMTPReply.isPositiveCompletion(this.rcpt(path.toString()));
    }
    
    public boolean addRecipient(final String address) throws IOException {
        return SMTPReply.isPositiveCompletion(this.rcpt("<" + address + ">"));
    }
    
    public Writer sendMessageData() throws IOException {
        if (!SMTPReply.isPositiveIntermediate(this.data())) {
            return null;
        }
        return new DotTerminatedMessageWriter(this._writer);
    }
    
    public boolean sendShortMessageData(final String message) throws IOException {
        final Writer writer = this.sendMessageData();
        if (writer == null) {
            return false;
        }
        writer.write(message);
        writer.close();
        return this.completePendingCommand();
    }
    
    public boolean sendSimpleMessage(final String sender, final String recipient, final String message) throws IOException {
        return this.setSender(sender) && this.addRecipient(recipient) && this.sendShortMessageData(message);
    }
    
    public boolean sendSimpleMessage(final String sender, final String[] recipients, final String message) throws IOException {
        boolean oneSuccess = false;
        if (!this.setSender(sender)) {
            return false;
        }
        for (int count = 0; count < recipients.length; ++count) {
            if (this.addRecipient(recipients[count])) {
                oneSuccess = true;
            }
        }
        return oneSuccess && this.sendShortMessageData(message);
    }
    
    public boolean logout() throws IOException {
        return SMTPReply.isPositiveCompletion(this.quit());
    }
    
    public boolean reset() throws IOException {
        return SMTPReply.isPositiveCompletion(this.rset());
    }
    
    public boolean verify(final String username) throws IOException {
        final int result = this.vrfy(username);
        return result == 250 || result == 251;
    }
    
    public String listHelp() throws IOException {
        if (SMTPReply.isPositiveCompletion(this.help())) {
            return this.getReplyString();
        }
        return null;
    }
    
    public String listHelp(final String command) throws IOException {
        if (SMTPReply.isPositiveCompletion(this.help(command))) {
            return this.getReplyString();
        }
        return null;
    }
    
    public boolean sendNoOp() throws IOException {
        return SMTPReply.isPositiveCompletion(this.noop());
    }
}
