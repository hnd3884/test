package org.jscep.client.verification;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Hex;
import java.security.cert.CertificateEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.security.Security;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;

public final class ConsoleCertificateVerifier implements CertificateVerifier
{
    @Override
    public boolean verify(final X509Certificate cert) {
        final List<String> algs = new ArrayList<String>(Security.getAlgorithms(MessageDigest.class.getSimpleName()));
        Collections.sort(algs);
        int max = 0;
        for (final String alg : algs) {
            if (alg.length() > max) {
                max = alg.length();
            }
        }
        for (final String alg : algs) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance(alg);
            }
            catch (final NoSuchAlgorithmException e) {
                return false;
            }
            byte[] hash;
            try {
                hash = digest.digest(cert.getEncoded());
            }
            catch (final CertificateEncodingException e2) {
                return false;
            }
            System.out.format("%" + max + "s: %s%n", alg, Hex.encodeHexString(hash));
        }
        final Scanner scanner = new Scanner(System.in, Charset.defaultCharset().name()).useDelimiter(String.format("%n", new Object[0]));
        while (true) {
            System.out.format("%nAccept? (Y/N): [N]%n", new Object[0]);
            try {
                final String answer = scanner.next();
                System.out.println(answer);
                if (answer.equals("Y")) {
                    return true;
                }
                if (answer.equals("N")) {
                    return false;
                }
                continue;
            }
            catch (final NoSuchElementException e3) {
                return false;
            }
        }
    }
}
