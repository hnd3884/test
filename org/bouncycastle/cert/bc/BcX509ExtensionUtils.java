package org.bouncycastle.cert.bc;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import java.io.OutputStream;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import java.io.IOException;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.cert.X509ExtensionUtils;

public class BcX509ExtensionUtils extends X509ExtensionUtils
{
    public BcX509ExtensionUtils() {
        super(new SHA1DigestCalculator());
    }
    
    public BcX509ExtensionUtils(final DigestCalculator digestCalculator) {
        super(digestCalculator);
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter));
    }
    
    public SubjectKeyIdentifier createSubjectKeyIdentifier(final AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter));
    }
    
    private static class SHA1DigestCalculator implements DigestCalculator
    {
        private ByteArrayOutputStream bOut;
        
        private SHA1DigestCalculator() {
            this.bOut = new ByteArrayOutputStream();
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }
        
        public OutputStream getOutputStream() {
            return this.bOut;
        }
        
        public byte[] getDigest() {
            final byte[] byteArray = this.bOut.toByteArray();
            this.bOut.reset();
            final SHA1Digest sha1Digest = new SHA1Digest();
            ((Digest)sha1Digest).update(byteArray, 0, byteArray.length);
            final byte[] array = new byte[((Digest)sha1Digest).getDigestSize()];
            ((Digest)sha1Digest).doFinal(array, 0);
            return array;
        }
    }
}
