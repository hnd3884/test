package org.bouncycastle.crypto.agreement.jpake;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.digests.SHA256Digest;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;

public class JPAKEParticipant
{
    public static final int STATE_INITIALIZED = 0;
    public static final int STATE_ROUND_1_CREATED = 10;
    public static final int STATE_ROUND_1_VALIDATED = 20;
    public static final int STATE_ROUND_2_CREATED = 30;
    public static final int STATE_ROUND_2_VALIDATED = 40;
    public static final int STATE_KEY_CALCULATED = 50;
    public static final int STATE_ROUND_3_CREATED = 60;
    public static final int STATE_ROUND_3_VALIDATED = 70;
    private final String participantId;
    private char[] password;
    private final Digest digest;
    private final SecureRandom random;
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger g;
    private String partnerParticipantId;
    private BigInteger x1;
    private BigInteger x2;
    private BigInteger gx1;
    private BigInteger gx2;
    private BigInteger gx3;
    private BigInteger gx4;
    private BigInteger b;
    private int state;
    
    public JPAKEParticipant(final String s, final char[] array) {
        this(s, array, JPAKEPrimeOrderGroups.NIST_3072);
    }
    
    public JPAKEParticipant(final String s, final char[] array, final JPAKEPrimeOrderGroup jpakePrimeOrderGroup) {
        this(s, array, jpakePrimeOrderGroup, new SHA256Digest(), new SecureRandom());
    }
    
    public JPAKEParticipant(final String participantId, final char[] array, final JPAKEPrimeOrderGroup jpakePrimeOrderGroup, final Digest digest, final SecureRandom random) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(array, "password");
        JPAKEUtil.validateNotNull(jpakePrimeOrderGroup, "p");
        JPAKEUtil.validateNotNull(digest, "digest");
        JPAKEUtil.validateNotNull(random, "random");
        if (array.length == 0) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        this.participantId = participantId;
        this.password = Arrays.copyOf(array, array.length);
        this.p = jpakePrimeOrderGroup.getP();
        this.q = jpakePrimeOrderGroup.getQ();
        this.g = jpakePrimeOrderGroup.getG();
        this.digest = digest;
        this.random = random;
        this.state = 0;
    }
    
    public int getState() {
        return this.state;
    }
    
    public JPAKERound1Payload createRound1PayloadToSend() {
        if (this.state >= 10) {
            throw new IllegalStateException("Round1 payload already created for " + this.participantId);
        }
        this.x1 = JPAKEUtil.generateX1(this.q, this.random);
        this.x2 = JPAKEUtil.generateX2(this.q, this.random);
        this.gx1 = JPAKEUtil.calculateGx(this.p, this.g, this.x1);
        this.gx2 = JPAKEUtil.calculateGx(this.p, this.g, this.x2);
        final BigInteger[] calculateZeroKnowledgeProof = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx1, this.x1, this.participantId, this.digest, this.random);
        final BigInteger[] calculateZeroKnowledgeProof2 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx2, this.x2, this.participantId, this.digest, this.random);
        this.state = 10;
        return new JPAKERound1Payload(this.participantId, this.gx1, this.gx2, calculateZeroKnowledgeProof, calculateZeroKnowledgeProof2);
    }
    
    public void validateRound1PayloadReceived(final JPAKERound1Payload jpakeRound1Payload) throws CryptoException {
        if (this.state >= 20) {
            throw new IllegalStateException("Validation already attempted for round1 payload for" + this.participantId);
        }
        this.partnerParticipantId = jpakeRound1Payload.getParticipantId();
        this.gx3 = jpakeRound1Payload.getGx1();
        this.gx4 = jpakeRound1Payload.getGx2();
        final BigInteger[] knowledgeProofForX1 = jpakeRound1Payload.getKnowledgeProofForX1();
        final BigInteger[] knowledgeProofForX2 = jpakeRound1Payload.getKnowledgeProofForX2();
        JPAKEUtil.validateParticipantIdsDiffer(this.participantId, jpakeRound1Payload.getParticipantId());
        JPAKEUtil.validateGx4(this.gx4);
        JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx3, knowledgeProofForX1, jpakeRound1Payload.getParticipantId(), this.digest);
        JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx4, knowledgeProofForX2, jpakeRound1Payload.getParticipantId(), this.digest);
        this.state = 20;
    }
    
    public JPAKERound2Payload createRound2PayloadToSend() {
        if (this.state >= 30) {
            throw new IllegalStateException("Round2 payload already created for " + this.participantId);
        }
        if (this.state < 20) {
            throw new IllegalStateException("Round1 payload must be validated prior to creating Round2 payload for " + this.participantId);
        }
        final BigInteger calculateGA = JPAKEUtil.calculateGA(this.p, this.gx1, this.gx3, this.gx4);
        final BigInteger calculateX2s = JPAKEUtil.calculateX2s(this.q, this.x2, JPAKEUtil.calculateS(this.password));
        final BigInteger calculateA = JPAKEUtil.calculateA(this.p, this.q, calculateGA, calculateX2s);
        final BigInteger[] calculateZeroKnowledgeProof = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, calculateGA, calculateA, calculateX2s, this.participantId, this.digest, this.random);
        this.state = 30;
        return new JPAKERound2Payload(this.participantId, calculateA, calculateZeroKnowledgeProof);
    }
    
    public void validateRound2PayloadReceived(final JPAKERound2Payload jpakeRound2Payload) throws CryptoException {
        if (this.state >= 40) {
            throw new IllegalStateException("Validation already attempted for round2 payload for" + this.participantId);
        }
        if (this.state < 20) {
            throw new IllegalStateException("Round1 payload must be validated prior to validating Round2 payload for " + this.participantId);
        }
        final BigInteger calculateGA = JPAKEUtil.calculateGA(this.p, this.gx3, this.gx1, this.gx2);
        this.b = jpakeRound2Payload.getA();
        final BigInteger[] knowledgeProofForX2s = jpakeRound2Payload.getKnowledgeProofForX2s();
        JPAKEUtil.validateParticipantIdsDiffer(this.participantId, jpakeRound2Payload.getParticipantId());
        JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, jpakeRound2Payload.getParticipantId());
        JPAKEUtil.validateGa(calculateGA);
        JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, calculateGA, this.b, knowledgeProofForX2s, jpakeRound2Payload.getParticipantId(), this.digest);
        this.state = 40;
    }
    
    public BigInteger calculateKeyingMaterial() {
        if (this.state >= 50) {
            throw new IllegalStateException("Key already calculated for " + this.participantId);
        }
        if (this.state < 40) {
            throw new IllegalStateException("Round2 payload must be validated prior to creating key for " + this.participantId);
        }
        final BigInteger calculateS = JPAKEUtil.calculateS(this.password);
        Arrays.fill(this.password, '\0');
        this.password = null;
        final BigInteger calculateKeyingMaterial = JPAKEUtil.calculateKeyingMaterial(this.p, this.q, this.gx4, this.x2, calculateS, this.b);
        this.x1 = null;
        this.x2 = null;
        this.b = null;
        this.state = 50;
        return calculateKeyingMaterial;
    }
    
    public JPAKERound3Payload createRound3PayloadToSend(final BigInteger bigInteger) {
        if (this.state >= 60) {
            throw new IllegalStateException("Round3 payload already created for " + this.participantId);
        }
        if (this.state < 50) {
            throw new IllegalStateException("Keying material must be calculated prior to creating Round3 payload for " + this.participantId);
        }
        final BigInteger calculateMacTag = JPAKEUtil.calculateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, bigInteger, this.digest);
        this.state = 60;
        return new JPAKERound3Payload(this.participantId, calculateMacTag);
    }
    
    public void validateRound3PayloadReceived(final JPAKERound3Payload jpakeRound3Payload, final BigInteger bigInteger) throws CryptoException {
        if (this.state >= 70) {
            throw new IllegalStateException("Validation already attempted for round3 payload for" + this.participantId);
        }
        if (this.state < 50) {
            throw new IllegalStateException("Keying material must be calculated validated prior to validating Round3 payload for " + this.participantId);
        }
        JPAKEUtil.validateParticipantIdsDiffer(this.participantId, jpakeRound3Payload.getParticipantId());
        JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, jpakeRound3Payload.getParticipantId());
        JPAKEUtil.validateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, bigInteger, this.digest, jpakeRound3Payload.getMacTag());
        this.gx1 = null;
        this.gx2 = null;
        this.gx3 = null;
        this.gx4 = null;
        this.state = 70;
    }
}
