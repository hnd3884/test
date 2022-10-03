package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.List;

public abstract class BinaryChromosome extends AbstractListChromosome<Integer>
{
    public BinaryChromosome(final List<Integer> representation) throws InvalidRepresentationException {
        super(representation);
    }
    
    public BinaryChromosome(final Integer[] representation) throws InvalidRepresentationException {
        super(representation);
    }
    
    @Override
    protected void checkValidity(final List<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
        for (final int i : chromosomeRepresentation) {
            if (i < 0 || i > 1) {
                throw new InvalidRepresentationException(LocalizedFormats.INVALID_BINARY_DIGIT, new Object[] { i });
            }
        }
    }
    
    public static List<Integer> randomBinaryRepresentation(final int length) {
        final List<Integer> rList = new ArrayList<Integer>(length);
        for (int j = 0; j < length; ++j) {
            rList.add(GeneticAlgorithm.getRandomGenerator().nextInt(2));
        }
        return rList;
    }
    
    @Override
    protected boolean isSame(final Chromosome another) {
        if (!(another instanceof BinaryChromosome)) {
            return false;
        }
        final BinaryChromosome anotherBc = (BinaryChromosome)another;
        if (this.getLength() != anotherBc.getLength()) {
            return false;
        }
        for (int i = 0; i < this.getRepresentation().size(); ++i) {
            if (!this.getRepresentation().get(i).equals(anotherBc.getRepresentation().get(i))) {
                return false;
            }
        }
        return true;
    }
}
