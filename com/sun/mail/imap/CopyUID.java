package com.sun.mail.imap;

import com.sun.mail.imap.protocol.UIDSet;

public class CopyUID
{
    public long uidvalidity;
    public UIDSet[] src;
    public UIDSet[] dst;
    
    public CopyUID(final long uidvalidity, final UIDSet[] src, final UIDSet[] dst) {
        this.uidvalidity = -1L;
        this.uidvalidity = uidvalidity;
        this.src = src;
        this.dst = dst;
    }
}
