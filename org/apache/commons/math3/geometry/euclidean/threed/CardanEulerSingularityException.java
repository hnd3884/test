package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalStateException;

public class CardanEulerSingularityException extends MathIllegalStateException
{
    private static final long serialVersionUID = -1360952845582206770L;
    
    public CardanEulerSingularityException(final boolean isCardan) {
        super(isCardan ? LocalizedFormats.CARDAN_ANGLES_SINGULARITY : LocalizedFormats.EULER_ANGLES_SINGULARITY, new Object[0]);
    }
}
