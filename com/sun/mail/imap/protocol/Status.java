package com.sun.mail.imap.protocol;

import java.util.Locale;
import java.util.HashMap;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.util.Map;

public class Status
{
    public String mbox;
    public int total;
    public int recent;
    public long uidnext;
    public long uidvalidity;
    public int unseen;
    public long highestmodseq;
    public Map<String, Long> items;
    static final String[] standardItems;
    
    public Status(final Response r) throws ParsingException {
        this.mbox = null;
        this.total = -1;
        this.recent = -1;
        this.uidnext = -1L;
        this.uidvalidity = -1L;
        this.unseen = -1;
        this.highestmodseq = -1L;
        this.mbox = r.readAtomString();
        if (!r.supportsUtf8()) {
            this.mbox = BASE64MailboxDecoder.decode(this.mbox);
        }
        final StringBuilder buffer = new StringBuilder();
        boolean onlySpaces = true;
        while (r.peekByte() != 40 && r.peekByte() != 0) {
            final char next = (char)r.readByte();
            buffer.append(next);
            if (next != ' ') {
                onlySpaces = false;
            }
        }
        if (!onlySpaces) {
            this.mbox = (this.mbox + (Object)buffer).trim();
        }
        if (r.readByte() != 40) {
            throw new ParsingException("parse error in STATUS");
        }
        do {
            final String attr = r.readAtom();
            if (attr == null) {
                throw new ParsingException("parse error in STATUS");
            }
            if (attr.equalsIgnoreCase("MESSAGES")) {
                this.total = r.readNumber();
            }
            else if (attr.equalsIgnoreCase("RECENT")) {
                this.recent = r.readNumber();
            }
            else if (attr.equalsIgnoreCase("UIDNEXT")) {
                this.uidnext = r.readLong();
            }
            else if (attr.equalsIgnoreCase("UIDVALIDITY")) {
                this.uidvalidity = r.readLong();
            }
            else if (attr.equalsIgnoreCase("UNSEEN")) {
                this.unseen = r.readNumber();
            }
            else if (attr.equalsIgnoreCase("HIGHESTMODSEQ")) {
                this.highestmodseq = r.readLong();
            }
            else {
                if (this.items == null) {
                    this.items = new HashMap<String, Long>();
                }
                this.items.put(attr.toUpperCase(Locale.ENGLISH), r.readLong());
            }
        } while (!r.isNextNonSpace(')'));
    }
    
    public long getItem(String item) {
        item = item.toUpperCase(Locale.ENGLISH);
        long ret = -1L;
        final Long v;
        if (this.items != null && (v = this.items.get(item)) != null) {
            ret = v;
        }
        else if (item.equals("MESSAGES")) {
            ret = this.total;
        }
        else if (item.equals("RECENT")) {
            ret = this.recent;
        }
        else if (item.equals("UIDNEXT")) {
            ret = this.uidnext;
        }
        else if (item.equals("UIDVALIDITY")) {
            ret = this.uidvalidity;
        }
        else if (item.equals("UNSEEN")) {
            ret = this.unseen;
        }
        else if (item.equals("HIGHESTMODSEQ")) {
            ret = this.highestmodseq;
        }
        return ret;
    }
    
    public static void add(final Status s1, final Status s2) {
        if (s2.total != -1) {
            s1.total = s2.total;
        }
        if (s2.recent != -1) {
            s1.recent = s2.recent;
        }
        if (s2.uidnext != -1L) {
            s1.uidnext = s2.uidnext;
        }
        if (s2.uidvalidity != -1L) {
            s1.uidvalidity = s2.uidvalidity;
        }
        if (s2.unseen != -1) {
            s1.unseen = s2.unseen;
        }
        if (s2.highestmodseq != -1L) {
            s1.highestmodseq = s2.highestmodseq;
        }
        if (s1.items == null) {
            s1.items = s2.items;
        }
        else if (s2.items != null) {
            s1.items.putAll(s2.items);
        }
    }
    
    static {
        standardItems = new String[] { "MESSAGES", "RECENT", "UNSEEN", "UIDNEXT", "UIDVALIDITY" };
    }
}
