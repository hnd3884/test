package com.adventnet.ds.query;

import java.util.Objects;
import com.zoho.conf.AppResources;

public class Function extends Column
{
    private String functionName;
    private Object[] functionArgs;
    boolean isArgument;
    
    Function(final String functionName, final Object... args) {
        this.functionName = null;
        this.functionArgs = null;
        this.isArgument = false;
        if (functionName == null || functionName.trim().length() == 0) {
            throw new IllegalArgumentException("FunctionName cannot be null/empty");
        }
        this.functionName = functionName;
        this.functionArgs = args;
        for (final Object arg : args) {
            if (arg instanceof Function) {
                ((Function)arg).isArgument = true;
            }
            else if (arg instanceof Operation) {
                ((Operation)arg).isArgument = true;
            }
        }
    }
    
    public boolean isArgument() {
        return this.isArgument;
    }
    
    public String getFunctionName() {
        return this.functionName;
    }
    
    public Object[] getFunctionArguments() {
        return this.functionArgs;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.functionName);
        if (this.functionArgs != null) {
            sb.append("(");
            for (int index = 0; index < this.functionArgs.length; ++index) {
                if (index != 0) {
                    sb.append(", ");
                }
                sb.append(this.functionArgs[index]);
            }
            sb.append(")");
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.hashCode(this.functionName);
    }
    
    private int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Function)) {
            return false;
        }
        final Function func = (Function)obj;
        if (this.getColumnAlias() != null && !this.getColumnAlias().equals(func.getColumnAlias())) {
            return false;
        }
        if (!AppResources.getBoolean("expr.equals.ignore.arguments", Boolean.valueOf(true))) {
            if (this.getFunctionArguments().length != func.getFunctionArguments().length) {
                return false;
            }
            final Object[] presentObj = this.getFunctionArguments();
            final Object[] passedObj = func.getFunctionArguments();
            for (int i = 0; i < presentObj.length; ++i) {
                if ((presentObj[i] == null && passedObj[i] != null) || (presentObj[i] != null && passedObj[i] == null) || (presentObj[i] != null && passedObj[i] != null && !presentObj[i].equals(passedObj[i]))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static class ReservedParameter
    {
        private String paramValue;
        
        public ReservedParameter(final String paramValue) {
            this.paramValue = paramValue;
        }
        
        public String getParamValue() {
            return this.paramValue;
        }
        
        @Override
        public String toString() {
            return this.paramValue;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof ReservedParameter && Objects.equals(this.paramValue, ((ReservedParameter)obj).paramValue));
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.paramValue);
        }
    }
}
