package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NamingException;

class Header
{
    static final int HEADER_SIZE = 12;
    static final short QR_BIT = Short.MIN_VALUE;
    static final short OPCODE_MASK = 30720;
    static final int OPCODE_SHIFT = 11;
    static final short AA_BIT = 1024;
    static final short TC_BIT = 512;
    static final short RD_BIT = 256;
    static final short RA_BIT = 128;
    static final short RCODE_MASK = 15;
    int xid;
    boolean query;
    int opcode;
    boolean authoritative;
    boolean truncated;
    boolean recursionDesired;
    boolean recursionAvail;
    int rcode;
    int numQuestions;
    int numAnswers;
    int numAuthorities;
    int numAdditionals;
    
    Header(final byte[] array, final int n) throws NamingException {
        this.decode(array, n);
    }
    
    private void decode(final byte[] array, final int n) throws NamingException {
        try {
            int n2 = 0;
            if (n < 12) {
                throw new CommunicationException("DNS error: corrupted message header");
            }
            this.xid = getShort(array, n2);
            n2 += 2;
            final short n3 = (short)getShort(array, n2);
            n2 += 2;
            this.query = ((n3 & 0xFFFF8000) == 0x0);
            this.opcode = (n3 & 0x7800) >>> 11;
            this.authoritative = ((n3 & 0x400) != 0x0);
            this.truncated = ((n3 & 0x200) != 0x0);
            this.recursionDesired = ((n3 & 0x100) != 0x0);
            this.recursionAvail = ((n3 & 0x80) != 0x0);
            this.rcode = (n3 & 0xF);
            this.numQuestions = getShort(array, n2);
            n2 += 2;
            this.numAnswers = getShort(array, n2);
            n2 += 2;
            this.numAuthorities = getShort(array, n2);
            n2 += 2;
            this.numAdditionals = getShort(array, n2);
            n2 += 2;
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new CommunicationException("DNS error: corrupted message header");
        }
    }
    
    private static int getShort(final byte[] array, final int n) {
        return (array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF);
    }
}
