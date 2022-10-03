package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public abstract class RandomKey<T> extends AbstractListChromosome<Double> implements PermutationChromosome<T>
{
    private final List<Double> sortedRepresentation;
    private final List<Integer> baseSeqPermutation;
    
    public RandomKey(final List<Double> representation) throws InvalidRepresentationException {
        super(representation);
        final List<Double> sortedRepr = new ArrayList<Double>(this.getRepresentation());
        Collections.sort(sortedRepr);
        this.sortedRepresentation = Collections.unmodifiableList((List<? extends Double>)sortedRepr);
        this.baseSeqPermutation = Collections.unmodifiableList((List<? extends Integer>)decodeGeneric((List<? extends T>)baseSequence(this.getLength()), this.getRepresentation(), this.sortedRepresentation));
    }
    
    public RandomKey(final Double[] representation) throws InvalidRepresentationException {
        this(Arrays.asList(representation));
    }
    
    public List<T> decode(final List<T> sequence) {
        return decodeGeneric(sequence, this.getRepresentation(), this.sortedRepresentation);
    }
    
    private static <S> List<S> decodeGeneric(final List<S> sequence, final List<Double> representation, final List<Double> sortedRepr) throws DimensionMismatchException {
        final int l = sequence.size();
        if (representation.size() != l) {
            throw new DimensionMismatchException(representation.size(), l);
        }
        if (sortedRepr.size() != l) {
            throw new DimensionMismatchException(sortedRepr.size(), l);
        }
        final List<Double> reprCopy = new ArrayList<Double>(representation);
        final List<S> res = new ArrayList<S>(l);
        for (int i = 0; i < l; ++i) {
            final int index = reprCopy.indexOf(sortedRepr.get(i));
            res.add(sequence.get(index));
            reprCopy.set(index, null);
        }
        return res;
    }
    
    @Override
    protected boolean isSame(final Chromosome another) {
        if (!(another instanceof RandomKey)) {
            return false;
        }
        final RandomKey<?> anotherRk = (RandomKey<?>)another;
        if (this.getLength() != anotherRk.getLength()) {
            return false;
        }
        final List<Integer> thisPerm = this.baseSeqPermutation;
        final List<Integer> anotherPerm = anotherRk.baseSeqPermutation;
        for (int i = 0; i < this.getLength(); ++i) {
            if (thisPerm.get(i) != anotherPerm.get(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void checkValidity(final List<Double> chromosomeRepresentation) throws InvalidRepresentationException {
        for (final double val : chromosomeRepresentation) {
            if (val < 0.0 || val > 1.0) {
                throw new InvalidRepresentationException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, new Object[] { val, 0, 1 });
            }
        }
    }
    
    public static final List<Double> randomPermutation(final int l) {
        final List<Double> repr = new ArrayList<Double>(l);
        for (int i = 0; i < l; ++i) {
            repr.add(GeneticAlgorithm.getRandomGenerator().nextDouble());
        }
        return repr;
    }
    
    public static final List<Double> identityPermutation(final int l) {
        final List<Double> repr = new ArrayList<Double>(l);
        for (int i = 0; i < l; ++i) {
            repr.add(i / (double)l);
        }
        return repr;
    }
    
    public static <S> List<Double> comparatorPermutation(final List<S> data, final Comparator<S> comparator) {
        final List<S> sortedData = new ArrayList<S>((Collection<? extends S>)data);
        Collections.sort(sortedData, comparator);
        return inducedPermutation(data, sortedData);
    }
    
    public static <S> List<Double> inducedPermutation(final List<S> originalData, final List<S> permutedData) throws DimensionMismatchException, MathIllegalArgumentException {
        if (originalData.size() != permutedData.size()) {
            throw new DimensionMismatchException(permutedData.size(), originalData.size());
        }
        final int l = originalData.size();
        final List<S> origDataCopy = new ArrayList<S>((Collection<? extends S>)originalData);
        final Double[] res = new Double[l];
        for (int i = 0; i < l; ++i) {
            final int index = origDataCopy.indexOf(permutedData.get(i));
            if (index == -1) {
                throw new MathIllegalArgumentException(LocalizedFormats.DIFFERENT_ORIG_AND_PERMUTED_DATA, new Object[0]);
            }
            res[index] = i / (double)l;
            origDataCopy.set(index, null);
        }
        return Arrays.asList(res);
    }
    
    @Override
    public String toString() {
        return String.format("(f=%s pi=(%s))", this.getFitness(), this.baseSeqPermutation);
    }
    
    private static List<Integer> baseSequence(final int l) {
        final List<Integer> baseSequence = new ArrayList<Integer>(l);
        for (int i = 0; i < l; ++i) {
            baseSequence.add(i);
        }
        return baseSequence;
    }
}
