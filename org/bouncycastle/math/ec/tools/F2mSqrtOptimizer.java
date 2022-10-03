package org.bouncycastle.math.ec.tools;

import java.util.ArrayList;
import java.util.Enumeration;
import org.bouncycastle.math.ec.ECFieldElement;
import java.math.BigInteger;
import org.bouncycastle.asn1.x9.X9ECParameters;
import java.util.Iterator;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import java.util.Collection;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;

public class F2mSqrtOptimizer
{
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
                implPrintRootZ(x9ECParameters);
            }
        }
    }
    
    public static void printRootZ(final X9ECParameters x9ECParameters) {
        if (!ECAlgorithms.isF2mCurve(x9ECParameters.getCurve())) {
            throw new IllegalArgumentException("Sqrt optimization only defined over characteristic-2 fields");
        }
        implPrintRootZ(x9ECParameters);
    }
    
    private static void implPrintRootZ(final X9ECParameters x9ECParameters) {
        final ECFieldElement fromBigInteger = x9ECParameters.getCurve().fromBigInteger(BigInteger.valueOf(2L));
        final ECFieldElement sqrt = fromBigInteger.sqrt();
        System.out.println(sqrt.toBigInteger().toString(16).toUpperCase());
        if (!sqrt.square().equals(fromBigInteger)) {
            throw new IllegalStateException("Optimized-sqrt sanity check failed");
        }
    }
    
    private static ArrayList enumToList(final Enumeration enumeration) {
        final ArrayList list = new ArrayList();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }
}
