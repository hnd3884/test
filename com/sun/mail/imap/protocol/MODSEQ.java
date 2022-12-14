package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;

public class MODSEQ implements Item
{
    static final char[] name;
    public int seqnum;
    public long modseq;
    
    public MODSEQ(final FetchResponse r) throws ParsingException {
        this.seqnum = r.getNumber();
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("MODSEQ parse error");
        }
        this.modseq = r.readLong();
        if (!r.isNextNonSpace(')')) {
            throw new ParsingException("MODSEQ parse error");
        }
    }
    
    static {
        name = new char[] { 'M', 'O', 'D', 'S', 'E', 'Q' };
    }
}
