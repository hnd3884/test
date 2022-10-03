package org.xbill.DNS.security;

import org.xbill.DNS.Type;
import java.security.GeneralSecurityException;
import org.xbill.DNS.Options;
import java.security.Signature;
import org.xbill.DNS.DNSSEC;
import java.util.Date;
import org.xbill.DNS.RRSIGRecord;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Cache;
import java.util.Iterator;
import org.xbill.DNS.Record;
import java.security.PublicKey;
import org.xbill.DNS.Name;
import java.util.LinkedList;
import java.util.List;
import org.xbill.DNS.DNSKEYRecord;
import java.util.HashMap;
import java.util.Map;
import org.xbill.DNS.Verifier;

public class DNSSECVerifier implements Verifier
{
    private Map trustedKeys;
    
    public DNSSECVerifier() {
        this.trustedKeys = new HashMap();
    }
    
    public synchronized void addTrustedKey(final DNSKEYRecord key) {
        final Name name = key.getName();
        List list = this.trustedKeys.get(name);
        if (list == null) {
            this.trustedKeys.put(name, list = new LinkedList());
        }
        list.add(key);
    }
    
    public void addTrustedKey(final Name name, final int alg, final PublicKey key) {
        final Record rec = KEYConverter.buildRecord(name, 48, 1, 0L, 0, 3, alg, key);
        if (rec != null) {
            this.addTrustedKey((DNSKEYRecord)rec);
        }
    }
    
    private PublicKey findMatchingKey(final Iterator it, final int algorithm, final int footprint) {
        while (it.hasNext()) {
            final DNSKEYRecord keyrec = it.next();
            if (keyrec.getAlgorithm() == algorithm && keyrec.getFootprint() == footprint) {
                return KEYConverter.parseRecord(keyrec);
            }
        }
        return null;
    }
    
    private synchronized PublicKey findTrustedKey(final Name name, final int algorithm, final int footprint) {
        final List list = this.trustedKeys.get(name);
        if (list == null) {
            return null;
        }
        return this.findMatchingKey(list.iterator(), algorithm, footprint);
    }
    
    private PublicKey findCachedKey(final Cache cache, final Name name, final int algorithm, final int footprint) {
        final RRset[] keysets = cache.findAnyRecords(name, 48);
        if (keysets == null) {
            return null;
        }
        final RRset keys = keysets[0];
        return this.findMatchingKey(keys.rrs(), algorithm, footprint);
    }
    
    private PublicKey findKey(final Cache cache, final Name name, final int algorithm, final int footprint) {
        final PublicKey key = this.findTrustedKey(name, algorithm, footprint);
        if (key == null && cache != null) {
            return this.findCachedKey(cache, name, algorithm, footprint);
        }
        return key;
    }
    
    private int verifySIG(final RRset set, final RRSIGRecord sigrec, final Cache cache) {
        final PublicKey key = this.findKey(cache, sigrec.getSigner(), sigrec.getAlgorithm(), sigrec.getFootprint());
        if (key == null) {
            return 0;
        }
        final Date now = new Date();
        if (now.compareTo(sigrec.getExpire()) > 0 || now.compareTo(sigrec.getTimeSigned()) < 0) {
            System.err.println("Outside of validity period");
            return -1;
        }
        final byte[] data = DNSSEC.digestRRset(sigrec, set);
        byte[] sig = null;
        String algString = null;
        switch (sigrec.getAlgorithm()) {
            case 1: {
                sig = sigrec.getSignature();
                algString = "MD5withRSA";
                break;
            }
            case 3: {
                sig = DSASignature.fromDNS(sigrec.getSignature());
                algString = "SHA1withDSA";
                break;
            }
            case 5: {
                sig = sigrec.getSignature();
                algString = "SHA1withRSA";
                break;
            }
            default: {
                return -1;
            }
        }
        try {
            final Signature s = Signature.getInstance(algString);
            s.initVerify(key);
            s.update(data);
            return s.verify(sig) ? 1 : -1;
        }
        catch (final GeneralSecurityException e) {
            if (Options.check("verboseexceptions")) {
                System.err.println("Signing data: " + e);
            }
            return -1;
        }
    }
    
    public int verify(final RRset set, final Cache cache) {
        final Iterator sigs = set.sigs();
        if (Options.check("verbosesec")) {
            System.out.print("Verifying " + set.getName() + "/" + Type.string(set.getType()) + ": ");
        }
        if (!sigs.hasNext()) {
            if (Options.check("verbosesec")) {
                System.out.println("Insecure");
            }
            return 0;
        }
        while (sigs.hasNext()) {
            final RRSIGRecord sigrec = sigs.next();
            if (this.verifySIG(set, sigrec, cache) == 1) {
                if (Options.check("verbosesec")) {
                    System.out.println("Secure");
                }
                return 1;
            }
        }
        if (Options.check("verbosesec")) {
            System.out.println("Failed");
        }
        return -1;
    }
}
