package org.apache.commons.math3.geometry;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import java.io.Serializable;

public interface Space extends Serializable
{
    int getDimension();
    
    Space getSubSpace() throws MathUnsupportedOperationException;
}
