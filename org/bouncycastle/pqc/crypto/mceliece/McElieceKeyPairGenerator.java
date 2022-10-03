package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.math.linearalgebra.Matrix;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2m;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class McElieceKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
    private McElieceKeyGenerationParameters mcElieceParams;
    private int m;
    private int n;
    private int t;
    private int fieldPoly;
    private SecureRandom random;
    private boolean initialized;
    
    public McElieceKeyPairGenerator() {
        this.initialized = false;
    }
    
    private void initializeDefault() {
        this.initialize(new McElieceKeyGenerationParameters(new SecureRandom(), new McElieceParameters()));
    }
    
    private void initialize(final KeyGenerationParameters keyGenerationParameters) {
        this.mcElieceParams = (McElieceKeyGenerationParameters)keyGenerationParameters;
        this.random = new SecureRandom();
        this.m = this.mcElieceParams.getParameters().getM();
        this.n = this.mcElieceParams.getParameters().getN();
        this.t = this.mcElieceParams.getParameters().getT();
        this.fieldPoly = this.mcElieceParams.getParameters().getFieldPoly();
        this.initialized = true;
    }
    
    private AsymmetricCipherKeyPair genKeyPair() {
        if (!this.initialized) {
            this.initializeDefault();
        }
        final GF2mField gf2mField = new GF2mField(this.m, this.fieldPoly);
        final PolynomialGF2mSmallM polynomialGF2mSmallM = new PolynomialGF2mSmallM(gf2mField, this.t, 'I', this.random);
        new PolynomialRingGF2m(gf2mField, polynomialGF2mSmallM).getSquareRootMatrix();
        final GoppaCode.MaMaPe computeSystematicForm = GoppaCode.computeSystematicForm(GoppaCode.createCanonicalCheckMatrix(gf2mField, polynomialGF2mSmallM), this.random);
        final GF2Matrix secondMatrix = computeSystematicForm.getSecondMatrix();
        final Permutation permutation = computeSystematicForm.getPermutation();
        final GF2Matrix gf2Matrix = (GF2Matrix)secondMatrix.computeTranspose();
        final GF2Matrix extendLeftCompactForm = gf2Matrix.extendLeftCompactForm();
        final int numRows = gf2Matrix.getNumRows();
        final GF2Matrix[] randomRegularMatrixAndItsInverse = GF2Matrix.createRandomRegularMatrixAndItsInverse(numRows, this.random);
        final Permutation permutation2 = new Permutation(this.n, this.random);
        return new AsymmetricCipherKeyPair(new McEliecePublicKeyParameters(this.n, this.t, (GF2Matrix)((GF2Matrix)randomRegularMatrixAndItsInverse[0].rightMultiply(extendLeftCompactForm)).rightMultiply(permutation2)), new McEliecePrivateKeyParameters(this.n, numRows, gf2mField, polynomialGF2mSmallM, permutation, permutation2, randomRegularMatrixAndItsInverse[1]));
    }
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }
}
