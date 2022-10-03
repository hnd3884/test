package org.apache.commons.math3.optim.linear;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.commons.math3.linear.MatrixUtils;
import java.io.ObjectOutputStream;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import java.io.Serializable;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.analysis.MultivariateFunction;

public class LinearObjectiveFunction implements MultivariateFunction, OptimizationData, Serializable
{
    private static final long serialVersionUID = -4531815507568396090L;
    private final transient RealVector coefficients;
    private final double constantTerm;
    
    public LinearObjectiveFunction(final double[] coefficients, final double constantTerm) {
        this(new ArrayRealVector(coefficients), constantTerm);
    }
    
    public LinearObjectiveFunction(final RealVector coefficients, final double constantTerm) {
        this.coefficients = coefficients;
        this.constantTerm = constantTerm;
    }
    
    public RealVector getCoefficients() {
        return this.coefficients;
    }
    
    public double getConstantTerm() {
        return this.constantTerm;
    }
    
    public double value(final double[] point) {
        return this.value(new ArrayRealVector(point, false));
    }
    
    public double value(final RealVector point) {
        return this.coefficients.dotProduct(point) + this.constantTerm;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LinearObjectiveFunction) {
            final LinearObjectiveFunction rhs = (LinearObjectiveFunction)other;
            return this.constantTerm == rhs.constantTerm && this.coefficients.equals(rhs.coefficients);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Double.valueOf(this.constantTerm).hashCode() ^ this.coefficients.hashCode();
    }
    
    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        MatrixUtils.serializeRealVector(this.coefficients, oos);
    }
    
    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        MatrixUtils.deserializeRealVector(this, "coefficients", ois);
    }
}
