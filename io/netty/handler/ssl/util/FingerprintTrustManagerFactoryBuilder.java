package io.netty.handler.ssl.util;

import java.util.Iterator;
import java.util.Arrays;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

public final class FingerprintTrustManagerFactoryBuilder
{
    private final String algorithm;
    private final List<String> fingerprints;
    
    FingerprintTrustManagerFactoryBuilder(final String algorithm) {
        this.fingerprints = new ArrayList<String>();
        this.algorithm = ObjectUtil.checkNotNull(algorithm, "algorithm");
    }
    
    public FingerprintTrustManagerFactoryBuilder fingerprints(final CharSequence... fingerprints) {
        return this.fingerprints((Iterable<? extends CharSequence>)Arrays.asList((Object[])ObjectUtil.checkNotNull((T[])fingerprints, "fingerprints")));
    }
    
    public FingerprintTrustManagerFactoryBuilder fingerprints(final Iterable<? extends CharSequence> fingerprints) {
        ObjectUtil.checkNotNull(fingerprints, "fingerprints");
        for (final CharSequence fingerprint : fingerprints) {
            ObjectUtil.checkNotNullWithIAE(fingerprint, "fingerprint");
            this.fingerprints.add(fingerprint.toString());
        }
        return this;
    }
    
    public FingerprintTrustManagerFactory build() {
        if (this.fingerprints.isEmpty()) {
            throw new IllegalStateException("No fingerprints provided");
        }
        return new FingerprintTrustManagerFactory(this.algorithm, FingerprintTrustManagerFactory.toFingerprintArray(this.fingerprints));
    }
}
