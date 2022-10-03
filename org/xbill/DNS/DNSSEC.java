package org.xbill.DNS;

import java.util.Iterator;
import java.util.Arrays;

public class DNSSEC
{
    public static final int RSAMD5 = 1;
    public static final int RSA = 1;
    public static final int DH = 2;
    public static final int DSA = 3;
    public static final int RSASHA1 = 5;
    public static final int Failed = -1;
    public static final int Insecure = 0;
    public static final int Secure = 1;
    
    private DNSSEC() {
    }
    
    private static void digestSIG(final DNSOutput out, final SIGBase sig) {
        out.writeU16(sig.getTypeCovered());
        out.writeU8(sig.getAlgorithm());
        out.writeU8(sig.getLabels());
        out.writeU32(sig.getOrigTTL());
        out.writeU32(sig.getExpire().getTime() / 1000L);
        out.writeU32(sig.getTimeSigned().getTime() / 1000L);
        out.writeU16(sig.getFootprint());
        sig.getSigner().toWireCanonical(out);
    }
    
    public static byte[] digestRRset(final RRSIGRecord sig, final RRset rrset) {
        final DNSOutput out = new DNSOutput();
        digestSIG(out, sig);
        int size = rrset.size();
        final Record[] records = new Record[size];
        final Iterator it = rrset.rrs();
        final Name name = rrset.getName();
        Name wild = null;
        final int sigLabels = sig.getLabels() + 1;
        if (name.labels() > sigLabels) {
            wild = name.wild(name.labels() - sigLabels);
        }
        while (it.hasNext()) {
            Record rec = it.next();
            if (wild != null) {
                rec = rec.withName(wild);
            }
            records[--size] = rec;
        }
        Arrays.sort(records);
        for (int i = 0; i < records.length; ++i) {
            out.writeByteArray(records[i].toWireCanonical());
        }
        return out.toByteArray();
    }
    
    public static byte[] digestMessage(final SIGRecord sig, final Message msg, final byte[] previous) {
        final DNSOutput out = new DNSOutput();
        digestSIG(out, sig);
        if (previous != null) {
            out.writeByteArray(previous);
        }
        msg.toWire(out);
        return out.toByteArray();
    }
    
    public static class Algorithm
    {
        public static final int RSAMD5 = 1;
        public static final int DH = 2;
        public static final int DSA = 3;
        public static final int ECC = 4;
        public static final int RSASHA1 = 5;
        public static final int INDIRECT = 252;
        public static final int PRIVATEDNS = 253;
        public static final int PRIVATEOID = 254;
        private static Mnemonic algs;
        
        private Algorithm() {
        }
        
        public static String string(final int alg) {
            return Algorithm.algs.getText(alg);
        }
        
        public static int value(final String s) {
            return Algorithm.algs.getValue(s);
        }
        
        static {
            (Algorithm.algs = new Mnemonic("DNSSEC algorithm", 2)).setMaximum(255);
            Algorithm.algs.setNumericAllowed(true);
            Algorithm.algs.add(1, "RSAMD5");
            Algorithm.algs.add(2, "DH");
            Algorithm.algs.add(3, "DSA");
            Algorithm.algs.add(4, "ECC");
            Algorithm.algs.add(5, "RSASHA1");
            Algorithm.algs.add(252, "INDIRECT");
            Algorithm.algs.add(253, "PRIVATEDNS");
            Algorithm.algs.add(254, "PRIVATEOID");
        }
    }
}
