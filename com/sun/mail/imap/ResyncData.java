package com.sun.mail.imap;

import com.sun.mail.imap.protocol.UIDSet;

public class ResyncData
{
    private long uidvalidity;
    private long modseq;
    private UIDSet[] uids;
    public static final ResyncData CONDSTORE;
    
    public ResyncData(final long uidvalidity, final long modseq) {
        this.uidvalidity = -1L;
        this.modseq = -1L;
        this.uids = null;
        this.uidvalidity = uidvalidity;
        this.modseq = modseq;
        this.uids = null;
    }
    
    public ResyncData(final long uidvalidity, final long modseq, final long uidFirst, final long uidLast) {
        this.uidvalidity = -1L;
        this.modseq = -1L;
        this.uids = null;
        this.uidvalidity = uidvalidity;
        this.modseq = modseq;
        this.uids = new UIDSet[] { new UIDSet(uidFirst, uidLast) };
    }
    
    public ResyncData(final long uidvalidity, final long modseq, final long[] uids) {
        this.uidvalidity = -1L;
        this.modseq = -1L;
        this.uids = null;
        this.uidvalidity = uidvalidity;
        this.modseq = modseq;
        this.uids = UIDSet.createUIDSets(uids);
    }
    
    public long getUIDValidity() {
        return this.uidvalidity;
    }
    
    public long getModSeq() {
        return this.modseq;
    }
    
    UIDSet[] getUIDSet() {
        return this.uids;
    }
    
    static {
        CONDSTORE = new ResyncData(-1L, -1L);
    }
}
