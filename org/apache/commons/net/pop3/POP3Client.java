package org.apache.commons.net.pop3;

import org.apache.commons.net.io.DotTerminatedMessageReader;
import java.io.Reader;
import java.util.ListIterator;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.IOException;
import java.util.StringTokenizer;

public class POP3Client extends POP3
{
    private static POP3MessageInfo __parseStatus(final String line) {
        final StringTokenizer tokenizer = new StringTokenizer(line);
        if (!tokenizer.hasMoreElements()) {
            return null;
        }
        int num;
        int size = num = 0;
        try {
            num = Integer.parseInt(tokenizer.nextToken());
            if (!tokenizer.hasMoreElements()) {
                return null;
            }
            size = Integer.parseInt(tokenizer.nextToken());
        }
        catch (final NumberFormatException e) {
            return null;
        }
        return new POP3MessageInfo(num, size);
    }
    
    private static POP3MessageInfo __parseUID(String line) {
        final StringTokenizer tokenizer = new StringTokenizer(line);
        if (!tokenizer.hasMoreElements()) {
            return null;
        }
        int num = 0;
        try {
            num = Integer.parseInt(tokenizer.nextToken());
            if (!tokenizer.hasMoreElements()) {
                return null;
            }
            line = tokenizer.nextToken();
        }
        catch (final NumberFormatException e) {
            return null;
        }
        return new POP3MessageInfo(num, line);
    }
    
    public boolean capa() throws IOException {
        if (this.sendCommand(12) == 0) {
            this.getAdditionalReply();
            return true;
        }
        return false;
    }
    
    public boolean login(final String username, final String password) throws IOException {
        if (this.getState() != 0) {
            return false;
        }
        if (this.sendCommand(0, username) != 0) {
            return false;
        }
        if (this.sendCommand(1, password) != 0) {
            return false;
        }
        this.setState(1);
        return true;
    }
    
    public boolean login(final String username, String timestamp, final String secret) throws IOException, NoSuchAlgorithmException {
        if (this.getState() != 0) {
            return false;
        }
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        timestamp += secret;
        final byte[] digest = md5.digest(timestamp.getBytes(this.getCharset()));
        final StringBuilder digestBuffer = new StringBuilder(128);
        for (int i = 0; i < digest.length; ++i) {
            final int digit = digest[i] & 0xFF;
            if (digit <= 15) {
                digestBuffer.append("0");
            }
            digestBuffer.append(Integer.toHexString(digit));
        }
        final StringBuilder buffer = new StringBuilder(256);
        buffer.append(username);
        buffer.append(' ');
        buffer.append(digestBuffer.toString());
        if (this.sendCommand(9, buffer.toString()) != 0) {
            return false;
        }
        this.setState(1);
        return true;
    }
    
    public boolean logout() throws IOException {
        if (this.getState() == 1) {
            this.setState(2);
        }
        this.sendCommand(2);
        return this._replyCode == 0;
    }
    
    public boolean noop() throws IOException {
        return this.getState() == 1 && this.sendCommand(7) == 0;
    }
    
    public boolean deleteMessage(final int messageId) throws IOException {
        return this.getState() == 1 && this.sendCommand(6, Integer.toString(messageId)) == 0;
    }
    
    public boolean reset() throws IOException {
        return this.getState() == 1 && this.sendCommand(8) == 0;
    }
    
    public POP3MessageInfo status() throws IOException {
        if (this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(3) != 0) {
            return null;
        }
        return __parseStatus(this._lastReplyLine.substring(3));
    }
    
    public POP3MessageInfo listMessage(final int messageId) throws IOException {
        if (this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(4, Integer.toString(messageId)) != 0) {
            return null;
        }
        return __parseStatus(this._lastReplyLine.substring(3));
    }
    
    public POP3MessageInfo[] listMessages() throws IOException {
        if (this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(4) != 0) {
            return null;
        }
        this.getAdditionalReply();
        final POP3MessageInfo[] messages = new POP3MessageInfo[this._replyLines.size() - 2];
        final ListIterator<String> en = this._replyLines.listIterator(1);
        for (int line = 0; line < messages.length; ++line) {
            messages[line] = __parseStatus(en.next());
        }
        return messages;
    }
    
    public POP3MessageInfo listUniqueIdentifier(final int messageId) throws IOException {
        if (this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(11, Integer.toString(messageId)) != 0) {
            return null;
        }
        return __parseUID(this._lastReplyLine.substring(3));
    }
    
    public POP3MessageInfo[] listUniqueIdentifiers() throws IOException {
        if (this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(11) != 0) {
            return null;
        }
        this.getAdditionalReply();
        final POP3MessageInfo[] messages = new POP3MessageInfo[this._replyLines.size() - 2];
        final ListIterator<String> en = this._replyLines.listIterator(1);
        for (int line = 0; line < messages.length; ++line) {
            messages[line] = __parseUID(en.next());
        }
        return messages;
    }
    
    public Reader retrieveMessage(final int messageId) throws IOException {
        if (this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(5, Integer.toString(messageId)) != 0) {
            return null;
        }
        return new DotTerminatedMessageReader(this._reader);
    }
    
    public Reader retrieveMessageTop(final int messageId, final int numLines) throws IOException {
        if (numLines < 0 || this.getState() != 1) {
            return null;
        }
        if (this.sendCommand(10, Integer.toString(messageId) + " " + Integer.toString(numLines)) != 0) {
            return null;
        }
        return new DotTerminatedMessageReader(this._reader);
    }
}
