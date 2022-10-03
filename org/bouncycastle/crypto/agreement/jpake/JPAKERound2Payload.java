package org.bouncycastle.crypto.agreement.jpake;

import org.bouncycastle.util.Arrays;
import java.math.BigInteger;

public class JPAKERound2Payload
{
    private final String participantId;
    private final BigInteger a;
    private final BigInteger[] knowledgeProofForX2s;
    
    public JPAKERound2Payload(final String participantId, final BigInteger a, final BigInteger[] array) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(a, "a");
        JPAKEUtil.validateNotNull(array, "knowledgeProofForX2s");
        this.participantId = participantId;
        this.a = a;
        this.knowledgeProofForX2s = Arrays.copyOf(array, array.length);
    }
    
    public String getParticipantId() {
        return this.participantId;
    }
    
    public BigInteger getA() {
        return this.a;
    }
    
    public BigInteger[] getKnowledgeProofForX2s() {
        return Arrays.copyOf(this.knowledgeProofForX2s, this.knowledgeProofForX2s.length);
    }
}
