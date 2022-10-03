package org.bouncycastle.math.ec.tools;

import java.util.Enumeration;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import java.util.Random;
import org.bouncycastle.util.Integers;
import java.util.ArrayList;
import org.bouncycastle.asn1.x9.X9ECParameters;
import java.util.Iterator;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import java.util.Collection;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import java.security.SecureRandom;
import java.math.BigInteger;

public class TraceOptimizer
{
    private static final BigInteger ONE;
    private static final SecureRandom R;
    
    public static void main(final String[] array) {
        final TreeSet set = new TreeSet(enumToList(ECNamedCurveTable.getNames()));
        set.addAll(enumToList(CustomNamedCurves.getNames()));
        for (final String s : set) {
            X9ECParameters x9ECParameters = CustomNamedCurves.getByName(s);
            if (x9ECParameters == null) {
                x9ECParameters = ECNamedCurveTable.getByName(s);
            }
            if (x9ECParameters != null && ECAlgorithms.isF2mCurve(x9ECParameters.getCurve())) {
                System.out.print(s + ":");
                implPrintNonZeroTraceBits(x9ECParameters);
            }
        }
    }
    
    public static void printNonZeroTraceBits(final X9ECParameters x9ECParameters) {
        if (!ECAlgorithms.isF2mCurve(x9ECParameters.getCurve())) {
            throw new IllegalArgumentException("Trace only defined over characteristic-2 fields");
        }
        implPrintNonZeroTraceBits(x9ECParameters);
    }
    
    public static void implPrintNonZeroTraceBits(final X9ECParameters x9ECParameters) {
        final ECCurve curve = x9ECParameters.getCurve();
        final int fieldSize = curve.getFieldSize();
        final ArrayList list = new ArrayList();
        for (int i = 0; i < fieldSize; ++i) {
            if (calculateTrace(curve.fromBigInteger(TraceOptimizer.ONE.shiftLeft(i))) != 0) {
                list.add(Integers.valueOf(i));
                System.out.print(" " + i);
            }
        }
        System.out.println();
        for (int j = 0; j < 1000; ++j) {
            final BigInteger bigInteger = new BigInteger(fieldSize, TraceOptimizer.R);
            final int calculateTrace = calculateTrace(curve.fromBigInteger(bigInteger));
            int n = 0;
            for (int k = 0; k < list.size(); ++k) {
                if (bigInteger.testBit((int)list.get(k))) {
                    n ^= 0x1;
                }
            }
            if (calculateTrace != n) {
                throw new IllegalStateException("Optimized-trace sanity check failed");
            }
        }
    }
    
    private static int calculateTrace(ECFieldElement square) {
        final int fieldSize = square.getFieldSize();
        ECFieldElement add = square;
        for (int i = 1; i < fieldSize; ++i) {
            square = square.square();
            add = add.add(square);
        }
        final BigInteger bigInteger = add.toBigInteger();
        if (bigInteger.bitLength() > 1) {
            throw new IllegalStateException();
        }
        return bigInteger.intValue();
    }
    
    private static ArrayList enumToList(final Enumeration enumeration) {
        final ArrayList list = new ArrayList();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
        R = new SecureRandom();
    }
}
