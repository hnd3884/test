package org.bouncycastle.operator.bc;

import org.bouncycastle.operator.OperatorCreationException;
import java.util.Collections;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.util.HashMap;
import java.util.Map;

public class BcDefaultDigestProvider implements BcDigestProvider
{
    private static final Map lookup;
    public static final BcDigestProvider INSTANCE;
    
    private static Map createTable() {
        final HashMap hashMap = new HashMap();
        hashMap.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA1Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA224Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA256Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA384Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA512Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_224, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA3Digest(224);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_256, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA3Digest(256);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_384, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA3Digest(384);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_512, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA3Digest(512);
            }
        });
        hashMap.put(PKCSObjectIdentifiers.md5, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new MD5Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.md4, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new MD4Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.md2, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new MD2Digest();
            }
        });
        hashMap.put(CryptoProObjectIdentifiers.gostR3411, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new GOST3411Digest();
            }
        });
        hashMap.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new GOST3411_2012_256Digest();
            }
        });
        hashMap.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new GOST3411_2012_512Digest();
            }
        });
        hashMap.put(TeleTrusTObjectIdentifiers.ripemd128, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new RIPEMD128Digest();
            }
        });
        hashMap.put(TeleTrusTObjectIdentifiers.ripemd160, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new RIPEMD160Digest();
            }
        });
        hashMap.put(TeleTrusTObjectIdentifiers.ripemd256, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new RIPEMD256Digest();
            }
        });
        return Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
    
    private BcDefaultDigestProvider() {
    }
    
    public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        final BcDigestProvider bcDigestProvider = BcDefaultDigestProvider.lookup.get(algorithmIdentifier.getAlgorithm());
        if (bcDigestProvider == null) {
            throw new OperatorCreationException("cannot recognise digest");
        }
        return bcDigestProvider.get(algorithmIdentifier);
    }
    
    static {
        lookup = createTable();
        INSTANCE = new BcDefaultDigestProvider();
    }
}
