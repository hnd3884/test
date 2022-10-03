package org.bouncycastle.cert.crmf;

import org.bouncycastle.operator.RuntimeOperatorException;
import java.io.OutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PKMACBuilder
{
    private AlgorithmIdentifier owf;
    private int iterationCount;
    private AlgorithmIdentifier mac;
    private int saltLength;
    private SecureRandom random;
    private PKMACValuesCalculator calculator;
    private PBMParameter parameters;
    private int maxIterations;
    
    public PKMACBuilder(final PKMACValuesCalculator pkmacValuesCalculator) {
        this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, (ASN1Encodable)DERNull.INSTANCE), pkmacValuesCalculator);
    }
    
    public PKMACBuilder(final PKMACValuesCalculator calculator, final int maxIterations) {
        this.saltLength = 20;
        this.maxIterations = maxIterations;
        this.calculator = calculator;
    }
    
    private PKMACBuilder(final AlgorithmIdentifier owf, final int iterationCount, final AlgorithmIdentifier mac, final PKMACValuesCalculator calculator) {
        this.saltLength = 20;
        this.owf = owf;
        this.iterationCount = iterationCount;
        this.mac = mac;
        this.calculator = calculator;
    }
    
    public PKMACBuilder setSaltLength(final int saltLength) {
        if (saltLength < 8) {
            throw new IllegalArgumentException("salt length must be at least 8 bytes");
        }
        this.saltLength = saltLength;
        return this;
    }
    
    public PKMACBuilder setIterationCount(final int iterationCount) {
        if (iterationCount < 100) {
            throw new IllegalArgumentException("iteration count must be at least 100");
        }
        this.checkIterationCountCeiling(iterationCount);
        this.iterationCount = iterationCount;
        return this;
    }
    
    public PKMACBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public PKMACBuilder setParameters(final PBMParameter parameters) {
        this.checkIterationCountCeiling(parameters.getIterationCount().getValue().intValue());
        this.parameters = parameters;
        return this;
    }
    
    public MacCalculator build(final char[] array) throws CRMFException {
        if (this.parameters != null) {
            return this.genCalculator(this.parameters, array);
        }
        final byte[] array2 = new byte[this.saltLength];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(array2);
        return this.genCalculator(new PBMParameter(array2, this.owf, this.iterationCount, this.mac), array);
    }
    
    private void checkIterationCountCeiling(final int n) {
        if (this.maxIterations > 0 && n > this.maxIterations) {
            throw new IllegalArgumentException("iteration count exceeds limit (" + n + " > " + this.maxIterations + ")");
        }
    }
    
    private MacCalculator genCalculator(final PBMParameter pbmParameter, final char[] array) throws CRMFException {
        final byte[] utf8ByteArray = Strings.toUTF8ByteArray(array);
        final byte[] octets = pbmParameter.getSalt().getOctets();
        byte[] calculateDigest = new byte[utf8ByteArray.length + octets.length];
        System.arraycopy(utf8ByteArray, 0, calculateDigest, 0, utf8ByteArray.length);
        System.arraycopy(octets, 0, calculateDigest, utf8ByteArray.length, octets.length);
        this.calculator.setup(pbmParameter.getOwf(), pbmParameter.getMac());
        int intValue = pbmParameter.getIterationCount().getValue().intValue();
        do {
            calculateDigest = this.calculator.calculateDigest(calculateDigest);
        } while (--intValue > 0);
        return new MacCalculator() {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, (ASN1Encodable)pbmParameter);
            }
            
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), calculateDigest);
            }
            
            public OutputStream getOutputStream() {
                return this.bOut;
            }
            
            public byte[] getMac() {
                try {
                    return PKMACBuilder.this.calculator.calculateMac(calculateDigest, this.bOut.toByteArray());
                }
                catch (final CRMFException ex) {
                    throw new RuntimeOperatorException("exception calculating mac: " + ex.getMessage(), ex);
                }
            }
        };
    }
}
