package com.sun.mail.imap.protocol;

import com.sun.mail.util.PropUtil;
import java.util.List;
import java.util.ArrayList;
import com.sun.mail.iap.Response;
import java.text.ParseException;
import com.sun.mail.iap.ParsingException;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.InternetAddress;
import java.util.Date;

public class ENVELOPE implements Item
{
    static final char[] name;
    public int msgno;
    public Date date;
    public String subject;
    public InternetAddress[] from;
    public InternetAddress[] sender;
    public InternetAddress[] replyTo;
    public InternetAddress[] to;
    public InternetAddress[] cc;
    public InternetAddress[] bcc;
    public String inReplyTo;
    public String messageId;
    private static final MailDateFormat mailDateFormat;
    private static final boolean parseDebug;
    
    public ENVELOPE(final FetchResponse r) throws ParsingException {
        this.date = null;
        if (ENVELOPE.parseDebug) {
            System.out.println("parse ENVELOPE");
        }
        this.msgno = r.getNumber();
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("ENVELOPE parse error");
        }
        final String s = r.readString();
        if (s != null) {
            try {
                synchronized (ENVELOPE.mailDateFormat) {
                    this.date = ENVELOPE.mailDateFormat.parse(s);
                }
            }
            catch (final ParseException ex) {}
        }
        if (ENVELOPE.parseDebug) {
            System.out.println("  Date: " + this.date);
        }
        this.subject = r.readString();
        if (ENVELOPE.parseDebug) {
            System.out.println("  Subject: " + this.subject);
        }
        if (ENVELOPE.parseDebug) {
            System.out.println("  From addresses:");
        }
        this.from = this.parseAddressList(r);
        if (ENVELOPE.parseDebug) {
            System.out.println("  Sender addresses:");
        }
        this.sender = this.parseAddressList(r);
        if (ENVELOPE.parseDebug) {
            System.out.println("  Reply-To addresses:");
        }
        this.replyTo = this.parseAddressList(r);
        if (ENVELOPE.parseDebug) {
            System.out.println("  To addresses:");
        }
        this.to = this.parseAddressList(r);
        if (ENVELOPE.parseDebug) {
            System.out.println("  Cc addresses:");
        }
        this.cc = this.parseAddressList(r);
        if (ENVELOPE.parseDebug) {
            System.out.println("  Bcc addresses:");
        }
        this.bcc = this.parseAddressList(r);
        this.inReplyTo = r.readString();
        if (ENVELOPE.parseDebug) {
            System.out.println("  In-Reply-To: " + this.inReplyTo);
        }
        this.messageId = r.readString();
        if (ENVELOPE.parseDebug) {
            System.out.println("  Message-ID: " + this.messageId);
        }
        if (!r.isNextNonSpace(')')) {
            throw new ParsingException("ENVELOPE parse error");
        }
    }
    
    private InternetAddress[] parseAddressList(final Response r) throws ParsingException {
        r.skipSpaces();
        final byte b = r.readByte();
        if (b == 40) {
            if (r.isNextNonSpace(')')) {
                return null;
            }
            final List<InternetAddress> v = new ArrayList<InternetAddress>();
            do {
                final IMAPAddress a = new IMAPAddress(r);
                if (ENVELOPE.parseDebug) {
                    System.out.println("    Address: " + a);
                }
                if (!a.isEndOfGroup()) {
                    v.add(a);
                }
            } while (!r.isNextNonSpace(')'));
            return v.toArray(new InternetAddress[v.size()]);
        }
        else {
            if (b == 78 || b == 110) {
                r.skip(2);
                return null;
            }
            throw new ParsingException("ADDRESS parse error");
        }
    }
    
    static {
        name = new char[] { 'E', 'N', 'V', 'E', 'L', 'O', 'P', 'E' };
        mailDateFormat = new MailDateFormat();
        parseDebug = PropUtil.getBooleanSystemProperty("mail.imap.parse.debug", false);
    }
}
