package org.bouncycastle.crypto.agreement.jpake;

import org.bouncycastle.util.Arrays;
import java.math.BigInteger;

public class JPAKERound1Payload
{
    private final String participantId;
    private final BigInteger gx1;
    private final BigInteger gx2;
    private final BigInteger[] knowledgeProofForX1;
    private final BigInteger[] knowledgeProofForX2;
    
    public JPAKERound1Payload(final String participantId, final BigInteger gx1, final BigInteger gx2, final BigInteger[] array, final BigInteger[] array2) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(gx1, "gx1");
        JPAKEUtil.validateNotNull(gx2, "gx2");
        JPAKEUtil.validateNotNull(array, "knowledgeProofForX1");
        JPAKEUtil.validateNotNull(array2, "knowledgeProofForX2");
        this.participantId = participantId;
        this.gx1 = gx1;
        this.gx2 = gx2;
        this.knowledgeProofForX1 = Arrays.copyOf(array, array.length);
        this.knowledgeProofForX2 = Arrays.copyOf(array2, array2.length);
    }
    
    public String getParticipantId() {
        return this.participantId;
    }
    
    public BigInteger getGx1() {
        return this.gx1;
    }
    
    public BigInteger getGx2() {
        return this.gx2;
    }
    
    public BigInteger[] getKnowledgeProofForX1() {
        return Arrays.copyOf(this.knowledgeProofForX1, this.knowledgeProofForX1.length);
    }
    
    public BigInteger[] getKnowledgeProofForX2() {
        return Arrays.copyOf(this.knowledgeProofForX2, this.knowledgeProofForX2.length);
    }
}
