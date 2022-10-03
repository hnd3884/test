package org.xbill.DNS.security;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.io.IOException;
import java.security.interfaces.DSAKey;
import java.security.Signature;
import org.xbill.DNS.DNSSEC;
import org.xbill.DNS.SIGRecord;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.xbill.DNS.Options;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.KEYRecord;
import java.security.PublicKey;
import org.xbill.DNS.Name;
import java.security.PrivateKey;

public class SIG0Signer
{
    private static final short VALIDITY = 300;
    private int algorithm;
    private PrivateKey privateKey;
    private Name name;
    private int footprint;
    
    public SIG0Signer(final int algorithm, final PrivateKey privateKey, final Name name, final int keyFootprint) {
        this.algorithm = (byte)algorithm;
        this.privateKey = privateKey;
        this.name = name;
        this.footprint = keyFootprint;
    }
    
    public SIG0Signer(final int algorithm, final PrivateKey privateKey, final Name name, final PublicKey publicKey) {
        this.algorithm = (byte)algorithm;
        this.privateKey = privateKey;
        this.name = name;
        final Record rec = KEYConverter.buildRecord(name, 25, 1, 0L, 0, 255, algorithm, publicKey);
        final KEYRecord keyRecord = (KEYRecord)rec;
        this.footprint = keyRecord.getFootprint();
    }
    
    public void apply(final Message m, final byte[] old) throws IOException, SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        int validity = Options.intValue("sig0validity");
        if (validity < 0) {
            validity = 300;
        }
        final long now = System.currentTimeMillis();
        final Date timeSigned = new Date(now);
        final Date timeExpires = new Date(now + validity * 1000);
        String algorithmName;
        if (this.algorithm == 3) {
            algorithmName = "SHA1withDSA";
        }
        else if (this.algorithm == 1) {
            algorithmName = "MD5withRSA";
        }
        else {
            if (this.algorithm != 5) {
                throw new NoSuchAlgorithmException("Unknown algorithm");
            }
            algorithmName = "SHA1withRSA";
        }
        final SIGRecord tmpsig = new SIGRecord(Name.root, 255, 0L, 0, this.algorithm, 0L, timeExpires, timeSigned, this.footprint, this.name, null);
        final byte[] outBytes = DNSSEC.digestMessage(tmpsig, m, old);
        final Signature signer = Signature.getInstance(algorithmName);
        signer.initSign(this.privateKey);
        signer.update(outBytes);
        byte[] signature = signer.sign();
        if (this.algorithm == 3) {
            final DSAKey dsakey = (DSAKey)this.privateKey;
            signature = DSASignature.toDNS(dsakey.getParams(), signature);
        }
        final SIGRecord sig = new SIGRecord(Name.root, 255, 0L, 0, this.algorithm, 0L, timeExpires, timeSigned, this.footprint, this.name, signature);
        m.addRecord(sig, 3);
    }
}
