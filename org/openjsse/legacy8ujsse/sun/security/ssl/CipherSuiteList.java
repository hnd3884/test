package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.io.IOException;
import javax.net.ssl.SSLException;
import java.util.ArrayList;
import java.util.Collection;

final class CipherSuiteList
{
    private final Collection<CipherSuite> cipherSuites;
    private String[] suiteNames;
    private volatile Boolean containsEC;
    
    CipherSuiteList(final Collection<CipherSuite> cipherSuites) {
        this.cipherSuites = cipherSuites;
    }
    
    CipherSuiteList(final CipherSuite suite) {
        (this.cipherSuites = new ArrayList<CipherSuite>(1)).add(suite);
    }
    
    CipherSuiteList(final String[] names) {
        if (names == null) {
            throw new IllegalArgumentException("CipherSuites may not be null");
        }
        this.cipherSuites = new ArrayList<CipherSuite>(names.length);
        for (int i = 0; i < names.length; ++i) {
            final String suiteName = names[i];
            final CipherSuite suite = CipherSuite.valueOf(suiteName);
            if (!suite.isAvailable()) {
                throw new IllegalArgumentException("Cannot support " + suiteName + " with currently installed providers");
            }
            this.cipherSuites.add(suite);
        }
    }
    
    CipherSuiteList(final HandshakeInStream in) throws IOException {
        final byte[] bytes = in.getBytes16();
        if ((bytes.length & 0x1) != 0x0) {
            throw new SSLException("Invalid ClientHello message");
        }
        this.cipherSuites = new ArrayList<CipherSuite>(bytes.length >> 1);
        for (int i = 0; i < bytes.length; i += 2) {
            this.cipherSuites.add(CipherSuite.valueOf(bytes[i], bytes[i + 1]));
        }
    }
    
    boolean contains(final CipherSuite suite) {
        return this.cipherSuites.contains(suite);
    }
    
    boolean containsEC() {
        if (this.containsEC == null) {
            for (final CipherSuite c : this.cipherSuites) {
                if (c.keyExchange.isEC) {
                    this.containsEC = true;
                    return true;
                }
            }
            this.containsEC = false;
        }
        return this.containsEC;
    }
    
    Iterator<CipherSuite> iterator() {
        return this.cipherSuites.iterator();
    }
    
    Collection<CipherSuite> collection() {
        return this.cipherSuites;
    }
    
    int size() {
        return this.cipherSuites.size();
    }
    
    synchronized String[] toStringArray() {
        if (this.suiteNames == null) {
            this.suiteNames = new String[this.cipherSuites.size()];
            int i = 0;
            for (final CipherSuite c : this.cipherSuites) {
                this.suiteNames[i++] = c.name;
            }
        }
        return this.suiteNames.clone();
    }
    
    @Override
    public String toString() {
        return this.cipherSuites.toString();
    }
    
    void send(final HandshakeOutStream s) throws IOException {
        final byte[] suiteBytes = new byte[this.cipherSuites.size() * 2];
        int i = 0;
        for (final CipherSuite c : this.cipherSuites) {
            suiteBytes[i] = (byte)(c.id >> 8);
            suiteBytes[i + 1] = (byte)c.id;
            i += 2;
        }
        s.putBytes16(suiteBytes);
    }
}
