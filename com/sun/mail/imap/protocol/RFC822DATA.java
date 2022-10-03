package com.sun.mail.imap.protocol;

import java.io.ByteArrayInputStream;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.ByteArray;

public class RFC822DATA implements Item
{
    static final char[] name;
    private final int msgno;
    private final ByteArray data;
    private final boolean isHeader;
    
    public RFC822DATA(final FetchResponse r) throws ParsingException {
        this(r, false);
    }
    
    public RFC822DATA(final FetchResponse r, final boolean isHeader) throws ParsingException {
        this.isHeader = isHeader;
        this.msgno = r.getNumber();
        r.skipSpaces();
        this.data = r.readByteArray();
    }
    
    public ByteArray getByteArray() {
        return this.data;
    }
    
    public ByteArrayInputStream getByteArrayInputStream() {
        if (this.data != null) {
            return this.data.toByteArrayInputStream();
        }
        return null;
    }
    
    public boolean isHeader() {
        return this.isHeader;
    }
    
    static {
        name = new char[] { 'R', 'F', 'C', '8', '2', '2' };
    }
}
