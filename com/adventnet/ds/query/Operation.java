package com.adventnet.ds.query;

import com.zoho.conf.AppResources;

public class Operation extends Column
{
    private operationType operation;
    private Object lhsArg;
    private Object rhsArg;
    boolean isArgument;
    
    Operation(final operationType oType, final Object lhsArgument, final Object rhsArgument) {
        this.operation = null;
        this.lhsArg = null;
        this.rhsArg = null;
        this.isArgument = false;
        if (lhsArgument == null || rhsArgument == null) {
            throw new IllegalArgumentException("Arguments in the operation cannot be null");
        }
        if (oType == null) {
            throw new IllegalArgumentException("operationType cannot be null");
        }
        this.operation = oType;
        this.lhsArg = lhsArgument;
        this.rhsArg = rhsArgument;
        if (this.lhsArg instanceof Function) {
            ((Function)this.lhsArg).isArgument = true;
        }
        else if (this.lhsArg instanceof Operation) {
            ((Operation)this.lhsArg).isArgument = true;
        }
        if (this.rhsArg instanceof Operation) {
            ((Operation)this.rhsArg).isArgument = true;
        }
        else if (this.rhsArg instanceof Function) {
            ((Function)this.rhsArg).isArgument = true;
        }
    }
    
    public boolean isArgument() {
        return this.isArgument;
    }
    
    public operationType getOperation() {
        return this.operation;
    }
    
    public static operationType getOperationFor(final String operStr) {
        if (operStr == null) {
            throw new IllegalArgumentException("operationString cannot be null");
        }
        try {
            return Enum.valueOf(operationType.class, operStr);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Operation String [" + operStr + "] is not a valid one. Valid ones are :: [ADD/SUBTRACT/MULTIPLY/DIVIDE/MODULO]", e);
        }
    }
    
    public Object getLHSArgument() {
        return this.lhsArg;
    }
    
    public Object getRHSArgument() {
        return this.rhsArg;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.lhsArg);
        sb.append(this.operation.toString());
        sb.append(this.rhsArg);
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.hashCode(this.operation);
    }
    
    private int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Operation)) {
            return false;
        }
        final Operation op = (Operation)obj;
        return AppResources.getBoolean("expr.equals.ignore.arguments", Boolean.valueOf(true)) || ((this.getLHSArgument() == null || this.getLHSArgument().equals(op.getLHSArgument())) && (this.getRHSArgument() == null || this.getRHSArgument().equals(op.getRHSArgument())) && (this.getOperation() == null || this.getOperation().equals(op.getOperation())));
    }
    
    public enum operationType
    {
        ADD, 
        SUBTRACT, 
        MULTIPLY, 
        DIVIDE, 
        MODULO;
    }
}
