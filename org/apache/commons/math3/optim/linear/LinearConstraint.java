package org.apache.commons.math3.optim.linear;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.commons.math3.linear.MatrixUtils;
import java.io.ObjectOutputStream;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import java.io.Serializable;

public class LinearConstraint implements Serializable
{
    private static final long serialVersionUID = -764632794033034092L;
    private final transient RealVector coefficients;
    private final Relationship relationship;
    private final double value;
    
    public LinearConstraint(final double[] coefficients, final Relationship relationship, final double value) {
        this(new ArrayRealVector(coefficients), relationship, value);
    }
    
    public LinearConstraint(final RealVector coefficients, final Relationship relationship, final double value) {
        this.coefficients = coefficients;
        this.relationship = relationship;
        this.value = value;
    }
    
    public LinearConstraint(final double[] lhsCoefficients, final double lhsConstant, final Relationship relationship, final double[] rhsCoefficients, final double rhsConstant) {
        final double[] sub = new double[lhsCoefficients.length];
        for (int i = 0; i < sub.length; ++i) {
            sub[i] = lhsCoefficients[i] - rhsCoefficients[i];
        }
        this.coefficients = new ArrayRealVector(sub, false);
        this.relationship = relationship;
        this.value = rhsConstant - lhsConstant;
    }
    
    public LinearConstraint(final RealVector lhsCoefficients, final double lhsConstant, final Relationship relationship, final RealVector rhsCoefficients, final double rhsConstant) {
        this.coefficients = lhsCoefficients.subtract(rhsCoefficients);
        this.relationship = relationship;
        this.value = rhsConstant - lhsConstant;
    }
    
    public RealVector getCoefficients() {
        return this.coefficients;
    }
    
    public Relationship getRelationship() {
        return this.relationship;
    }
    
    public double getValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LinearConstraint) {
            final LinearConstraint rhs = (LinearConstraint)other;
            return this.relationship == rhs.relationship && this.value == rhs.value && this.coefficients.equals(rhs.coefficients);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.relationship.hashCode() ^ Double.valueOf(this.value).hashCode() ^ this.coefficients.hashCode();
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
