package org.apache.commons.math3.random;

import java.util.Collection;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

@Deprecated
public interface RandomData
{
    String nextHexString(final int p0) throws NotStrictlyPositiveException;
    
    int nextInt(final int p0, final int p1) throws NumberIsTooLargeException;
    
    long nextLong(final long p0, final long p1) throws NumberIsTooLargeException;
    
    String nextSecureHexString(final int p0) throws NotStrictlyPositiveException;
    
    int nextSecureInt(final int p0, final int p1) throws NumberIsTooLargeException;
    
    long nextSecureLong(final long p0, final long p1) throws NumberIsTooLargeException;
    
    long nextPoisson(final double p0) throws NotStrictlyPositiveException;
    
    double nextGaussian(final double p0, final double p1) throws NotStrictlyPositiveException;
    
    double nextExponential(final double p0) throws NotStrictlyPositiveException;
    
    double nextUniform(final double p0, final double p1) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException;
    
    double nextUniform(final double p0, final double p1, final boolean p2) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException;
    
    int[] nextPermutation(final int p0, final int p1) throws NumberIsTooLargeException, NotStrictlyPositiveException;
    
    Object[] nextSample(final Collection<?> p0, final int p1) throws NumberIsTooLargeException, NotStrictlyPositiveException;
}
