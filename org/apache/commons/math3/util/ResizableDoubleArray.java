package org.apache.commons.math3.util;

import java.util.Arrays;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.io.Serializable;

public class ResizableDoubleArray implements DoubleArray, Serializable
{
    @Deprecated
    public static final int ADDITIVE_MODE = 1;
    @Deprecated
    public static final int MULTIPLICATIVE_MODE = 0;
    private static final long serialVersionUID = -3485529955529426875L;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_EXPANSION_FACTOR = 2.0;
    private static final double DEFAULT_CONTRACTION_DELTA = 0.5;
    private double contractionCriterion;
    private double expansionFactor;
    private ExpansionMode expansionMode;
    private double[] internalArray;
    private int numElements;
    private int startIndex;
    
    public ResizableDoubleArray() {
        this(16);
    }
    
    public ResizableDoubleArray(final int initialCapacity) throws MathIllegalArgumentException {
        this(initialCapacity, 2.0);
    }
    
    public ResizableDoubleArray(final double[] initialArray) {
        this(16, 2.0, 2.5, ExpansionMode.MULTIPLICATIVE, initialArray);
    }
    
    @Deprecated
    public ResizableDoubleArray(final int initialCapacity, final float expansionFactor) throws MathIllegalArgumentException {
        this(initialCapacity, (double)expansionFactor);
    }
    
    public ResizableDoubleArray(final int initialCapacity, final double expansionFactor) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor, 0.5 + expansionFactor);
    }
    
    @Deprecated
    public ResizableDoubleArray(final int initialCapacity, final float expansionFactor, final float contractionCriteria) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor, (double)contractionCriteria);
    }
    
    public ResizableDoubleArray(final int initialCapacity, final double expansionFactor, final double contractionCriterion) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor, contractionCriterion, ExpansionMode.MULTIPLICATIVE, (double[])null);
    }
    
    @Deprecated
    public ResizableDoubleArray(final int initialCapacity, final float expansionFactor, final float contractionCriteria, final int expansionMode) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor, contractionCriteria, (expansionMode == 1) ? ExpansionMode.ADDITIVE : ExpansionMode.MULTIPLICATIVE, (double[])null);
        this.setExpansionMode(expansionMode);
    }
    
    public ResizableDoubleArray(final int initialCapacity, final double expansionFactor, final double contractionCriterion, final ExpansionMode expansionMode, final double... data) throws MathIllegalArgumentException {
        this.contractionCriterion = 2.5;
        this.expansionFactor = 2.0;
        this.expansionMode = ExpansionMode.MULTIPLICATIVE;
        this.numElements = 0;
        this.startIndex = 0;
        if (initialCapacity <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.INITIAL_CAPACITY_NOT_POSITIVE, initialCapacity);
        }
        this.checkContractExpand(contractionCriterion, expansionFactor);
        this.expansionFactor = expansionFactor;
        this.contractionCriterion = contractionCriterion;
        this.expansionMode = expansionMode;
        this.internalArray = new double[initialCapacity];
        this.numElements = 0;
        this.startIndex = 0;
        if (data != null && data.length > 0) {
            this.addElements(data);
        }
    }
    
    public ResizableDoubleArray(final ResizableDoubleArray original) throws NullArgumentException {
        this.contractionCriterion = 2.5;
        this.expansionFactor = 2.0;
        this.expansionMode = ExpansionMode.MULTIPLICATIVE;
        this.numElements = 0;
        this.startIndex = 0;
        MathUtils.checkNotNull(original);
        copy(original, this);
    }
    
    public synchronized void addElement(final double value) {
        if (this.internalArray.length <= this.startIndex + this.numElements) {
            this.expand();
        }
        this.internalArray[this.startIndex + this.numElements++] = value;
    }
    
    public synchronized void addElements(final double[] values) {
        final double[] tempArray = new double[this.numElements + values.length + 1];
        System.arraycopy(this.internalArray, this.startIndex, tempArray, 0, this.numElements);
        System.arraycopy(values, 0, tempArray, this.numElements, values.length);
        this.internalArray = tempArray;
        this.startIndex = 0;
        this.numElements += values.length;
    }
    
    public synchronized double addElementRolling(final double value) {
        final double discarded = this.internalArray[this.startIndex];
        if (this.startIndex + (this.numElements + 1) > this.internalArray.length) {
            this.expand();
        }
        ++this.startIndex;
        this.internalArray[this.startIndex + (this.numElements - 1)] = value;
        if (this.shouldContract()) {
            this.contract();
        }
        return discarded;
    }
    
    public synchronized double substituteMostRecentElement(final double value) throws MathIllegalStateException {
        if (this.numElements < 1) {
            throw new MathIllegalStateException(LocalizedFormats.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY, new Object[0]);
        }
        final int substIndex = this.startIndex + (this.numElements - 1);
        final double discarded = this.internalArray[substIndex];
        this.internalArray[substIndex] = value;
        return discarded;
    }
    
    @Deprecated
    protected void checkContractExpand(final float contraction, final float expansion) throws MathIllegalArgumentException {
        this.checkContractExpand(contraction, (double)expansion);
    }
    
    protected void checkContractExpand(final double contraction, final double expansion) throws NumberIsTooSmallException {
        if (contraction < expansion) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, true);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR, contraction, expansion);
            throw e;
        }
        if (contraction <= 1.0) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, false);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_ONE, contraction);
            throw e;
        }
        if (expansion <= 1.0) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, false);
            e.getContext().addMessage(LocalizedFormats.EXPANSION_FACTOR_SMALLER_THAN_ONE, expansion);
            throw e;
        }
    }
    
    public synchronized void clear() {
        this.numElements = 0;
        this.startIndex = 0;
    }
    
    public synchronized void contract() {
        final double[] tempArray = new double[this.numElements + 1];
        System.arraycopy(this.internalArray, this.startIndex, tempArray, 0, this.numElements);
        this.internalArray = tempArray;
        this.startIndex = 0;
    }
    
    public synchronized void discardFrontElements(final int i) throws MathIllegalArgumentException {
        this.discardExtremeElements(i, true);
    }
    
    public synchronized void discardMostRecentElements(final int i) throws MathIllegalArgumentException {
        this.discardExtremeElements(i, false);
    }
    
    private synchronized void discardExtremeElements(final int i, final boolean front) throws MathIllegalArgumentException {
        if (i > this.numElements) {
            throw new MathIllegalArgumentException(LocalizedFormats.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY, new Object[] { i, this.numElements });
        }
        if (i < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS, new Object[] { i });
        }
        this.numElements -= i;
        if (front) {
            this.startIndex += i;
        }
        if (this.shouldContract()) {
            this.contract();
        }
    }
    
    protected synchronized void expand() {
        int newSize = 0;
        if (this.expansionMode == ExpansionMode.MULTIPLICATIVE) {
            newSize = (int)FastMath.ceil(this.internalArray.length * this.expansionFactor);
        }
        else {
            newSize = (int)(this.internalArray.length + FastMath.round(this.expansionFactor));
        }
        final double[] tempArray = new double[newSize];
        System.arraycopy(this.internalArray, 0, tempArray, 0, this.internalArray.length);
        this.internalArray = tempArray;
    }
    
    private synchronized void expandTo(final int size) {
        final double[] tempArray = new double[size];
        System.arraycopy(this.internalArray, 0, tempArray, 0, this.internalArray.length);
        this.internalArray = tempArray;
    }
    
    @Deprecated
    public float getContractionCriteria() {
        return (float)this.getContractionCriterion();
    }
    
    public double getContractionCriterion() {
        return this.contractionCriterion;
    }
    
    public synchronized double getElement(final int index) {
        if (index >= this.numElements) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index >= 0) {
            return this.internalArray[this.startIndex + index];
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }
    
    public synchronized double[] getElements() {
        final double[] elementArray = new double[this.numElements];
        System.arraycopy(this.internalArray, this.startIndex, elementArray, 0, this.numElements);
        return elementArray;
    }
    
    @Deprecated
    public float getExpansionFactor() {
        return (float)this.expansionFactor;
    }
    
    @Deprecated
    public int getExpansionMode() {
        synchronized (this) {
            switch (this.expansionMode) {
                case MULTIPLICATIVE: {
                    return 0;
                }
                case ADDITIVE: {
                    return 1;
                }
                default: {
                    throw new MathInternalError();
                }
            }
        }
    }
    
    @Deprecated
    synchronized int getInternalLength() {
        return this.internalArray.length;
    }
    
    public int getCapacity() {
        return this.internalArray.length;
    }
    
    public synchronized int getNumElements() {
        return this.numElements;
    }
    
    @Deprecated
    public synchronized double[] getInternalValues() {
        return this.internalArray;
    }
    
    protected double[] getArrayRef() {
        return this.internalArray;
    }
    
    protected int getStartIndex() {
        return this.startIndex;
    }
    
    @Deprecated
    public void setContractionCriteria(final float contractionCriteria) throws MathIllegalArgumentException {
        this.checkContractExpand(contractionCriteria, this.getExpansionFactor());
        synchronized (this) {
            this.contractionCriterion = contractionCriteria;
        }
    }
    
    public double compute(final MathArrays.Function f) {
        final double[] array;
        final int start;
        final int num;
        synchronized (this) {
            array = this.internalArray;
            start = this.startIndex;
            num = this.numElements;
        }
        return f.evaluate(array, start, num);
    }
    
    public synchronized void setElement(final int index, final double value) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index + 1 > this.numElements) {
            this.numElements = index + 1;
        }
        if (this.startIndex + index >= this.internalArray.length) {
            this.expandTo(this.startIndex + (index + 1));
        }
        this.internalArray[this.startIndex + index] = value;
    }
    
    @Deprecated
    public void setExpansionFactor(final float expansionFactor) throws MathIllegalArgumentException {
        this.checkContractExpand(this.getContractionCriterion(), expansionFactor);
        synchronized (this) {
            this.expansionFactor = expansionFactor;
        }
    }
    
    @Deprecated
    public void setExpansionMode(final int expansionMode) throws MathIllegalArgumentException {
        if (expansionMode != 0 && expansionMode != 1) {
            throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_EXPANSION_MODE, new Object[] { expansionMode, 0, "MULTIPLICATIVE_MODE", 1, "ADDITIVE_MODE" });
        }
        synchronized (this) {
            if (expansionMode == 0) {
                this.setExpansionMode(ExpansionMode.MULTIPLICATIVE);
            }
            else if (expansionMode == 1) {
                this.setExpansionMode(ExpansionMode.ADDITIVE);
            }
        }
    }
    
    @Deprecated
    public void setExpansionMode(final ExpansionMode expansionMode) {
        synchronized (this) {
            this.expansionMode = expansionMode;
        }
    }
    
    @Deprecated
    protected void setInitialCapacity(final int initialCapacity) throws MathIllegalArgumentException {
    }
    
    public synchronized void setNumElements(final int i) throws MathIllegalArgumentException {
        if (i < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.INDEX_NOT_POSITIVE, new Object[] { i });
        }
        final int newSize = this.startIndex + i;
        if (newSize > this.internalArray.length) {
            this.expandTo(newSize);
        }
        this.numElements = i;
    }
    
    private synchronized boolean shouldContract() {
        if (this.expansionMode == ExpansionMode.MULTIPLICATIVE) {
            return this.internalArray.length / (float)this.numElements > this.contractionCriterion;
        }
        return this.internalArray.length - this.numElements > this.contractionCriterion;
    }
    
    @Deprecated
    public synchronized int start() {
        return this.startIndex;
    }
    
    public static void copy(final ResizableDoubleArray source, final ResizableDoubleArray dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        synchronized (source) {
            synchronized (dest) {
                dest.contractionCriterion = source.contractionCriterion;
                dest.expansionFactor = source.expansionFactor;
                dest.expansionMode = source.expansionMode;
                dest.internalArray = new double[source.internalArray.length];
                System.arraycopy(source.internalArray, 0, dest.internalArray, 0, dest.internalArray.length);
                dest.numElements = source.numElements;
                dest.startIndex = source.startIndex;
            }
        }
    }
    
    public synchronized ResizableDoubleArray copy() {
        final ResizableDoubleArray result = new ResizableDoubleArray();
        copy(this, result);
        return result;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ResizableDoubleArray)) {
            return false;
        }
        synchronized (this) {
            synchronized (object) {
                boolean result = true;
                final ResizableDoubleArray other = (ResizableDoubleArray)object;
                result = (result && other.contractionCriterion == this.contractionCriterion);
                result = (result && other.expansionFactor == this.expansionFactor);
                result = (result && other.expansionMode == this.expansionMode);
                result = (result && other.numElements == this.numElements);
                result = (result && other.startIndex == this.startIndex);
                return result && Arrays.equals(this.internalArray, other.internalArray);
            }
        }
    }
    
    @Override
    public synchronized int hashCode() {
        final int[] hashData = { Double.valueOf(this.expansionFactor).hashCode(), Double.valueOf(this.contractionCriterion).hashCode(), this.expansionMode.hashCode(), Arrays.hashCode(this.internalArray), this.numElements, this.startIndex };
        return Arrays.hashCode(hashData);
    }
    
    public enum ExpansionMode
    {
        MULTIPLICATIVE, 
        ADDITIVE;
    }
}
