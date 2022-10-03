package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;

public class GMSSStateAwareSigner implements StateAwareMessageSigner
{
    private final GMSSSigner gmssSigner;
    private GMSSPrivateKeyParameters key;
    
    public GMSSStateAwareSigner(final Digest digest) {
        if (!(digest instanceof Memoable)) {
            throw new IllegalArgumentException("digest must implement Memoable");
        }
        this.gmssSigner = new GMSSSigner(new GMSSDigestProvider() {
            final /* synthetic */ Memoable val$dig = ((Memoable)digest).copy();
            
            public Digest get() {
                return (Digest)this.val$dig.copy();
            }
        });
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                this.key = (GMSSPrivateKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
            }
            else {
                this.key = (GMSSPrivateKeyParameters)cipherParameters;
            }
        }
        this.gmssSigner.init(b, cipherParameters);
    }
    
    public byte[] generateSignature(final byte[] array) {
        if (this.key == null) {
            throw new IllegalStateException("signing key no longer usable");
        }
        final byte[] generateSignature = this.gmssSigner.generateSignature(array);
        this.key = this.key.nextKey();
        return generateSignature;
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2) {
        return this.gmssSigner.verifySignature(array, array2);
    }
    
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        final GMSSPrivateKeyParameters key = this.key;
        this.key = null;
        return key;
    }
}
