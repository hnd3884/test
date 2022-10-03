package org.owasp.esapi;

public class ExecuteResult
{
    private final int exitValue;
    private final String output;
    private final String errors;
    
    public ExecuteResult(final int exitValue, final String output, final String errors) {
        this.exitValue = exitValue;
        this.output = output;
        this.errors = errors;
    }
    
    public int getExitValue() {
        return this.exitValue;
    }
    
    public String getOutput() {
        return this.output;
    }
    
    public String getErrors() {
        return this.errors;
    }
    
    @Override
    public String toString() {
        return "ExecuteResult[exitValue=" + this.exitValue + ",output=" + this.output + ",errors=" + this.errors + "]";
    }
}
