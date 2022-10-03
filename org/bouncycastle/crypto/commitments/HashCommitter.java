package org.bouncycastle.crypto.commitments;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Commitment;
import org.bouncycastle.crypto.ExtendedDigest;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Committer;

public class HashCommitter implements Committer
{
    private final Digest digest;
    private final int byteLength;
    private final SecureRandom random;
    
    public HashCommitter(final ExtendedDigest digest, final SecureRandom random) {
        this.digest = digest;
        this.byteLength = digest.getByteLength();
        this.random = random;
    }
    
    public Commitment commit(final byte[] array) {
        if (array.length > this.byteLength / 2) {
            throw new DataLengthException("Message to be committed to too large for digest.");
        }
        final byte[] array2 = new byte[this.byteLength - array.length];
        this.random.nextBytes(array2);
        return new Commitment(array2, this.calculateCommitment(array2, array));
    }
    
    public boolean isRevealed(final Commitment commitment, final byte[] array) {
        if (array.length + commitment.getSecret().length != this.byteLength) {
            throw new DataLengthException("Message and witness secret lengths do not match.");
        }
        return Arrays.constantTimeAreEqual(commitment.getCommitment(), this.calculateCommitment(commitment.getSecret(), array));
    }
    
    private byte[] calculateCommitment(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[this.digest.getDigestSize()];
        this.digest.update(array, 0, array.length);
        this.digest.update(array2, 0, array2.length);
        this.digest.doFinal(array3, 0);
        return array3;
    }
}
