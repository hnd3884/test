package org.xbill.DNS.security;

import java.security.cert.X509Certificate;
import org.xbill.DNS.Name;
import java.security.cert.CertificateException;
import org.xbill.DNS.Options;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import org.xbill.DNS.CERTRecord;

public class CERTConverter
{
    public static Certificate parseRecord(final CERTRecord r) {
        final int type = r.getCertType();
        final byte[] data = r.getCert();
        try {
            switch (type) {
                case 1: {
                    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    final ByteArrayInputStream bs = new ByteArrayInputStream(data);
                    final Certificate cert = cf.generateCertificate(bs);
                    return cert;
                }
                default: {
                    return null;
                }
            }
        }
        catch (final CertificateException e) {
            if (Options.check("verboseexceptions")) {
                System.err.println("Cert parse exception:" + e);
            }
            return null;
        }
    }
    
    public static CERTRecord buildRecord(final Name name, final int dclass, final long ttl, final Certificate cert, final int tag, final int alg) {
        try {
            if (cert instanceof X509Certificate) {
                final int type = 1;
                final byte[] data = cert.getEncoded();
                return new CERTRecord(name, dclass, ttl, type, tag, alg, data);
            }
            return null;
        }
        catch (final CertificateException e) {
            if (Options.check("verboseexceptions")) {
                System.err.println("Cert build exception:" + e);
            }
            return null;
        }
    }
    
    public static CERTRecord buildRecord(final Name name, final int dclass, final long ttl, final Certificate cert) {
        return buildRecord(name, dclass, ttl, cert, 0, 0);
    }
}
