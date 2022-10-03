package org.bouncycastle.util.test;

import org.bouncycastle.util.Strings;

public class SimpleTestResult implements TestResult
{
    private static final String SEPARATOR;
    private boolean success;
    private String message;
    private Throwable exception;
    
    public SimpleTestResult(final boolean success, final String message) {
        this.success = success;
        this.message = message;
    }
    
    public SimpleTestResult(final boolean success, final String message, final Throwable exception) {
        this.success = success;
        this.message = message;
        this.exception = exception;
    }
    
    public static TestResult successful(final Test test, final String s) {
        return new SimpleTestResult(true, test.getName() + ": " + s);
    }
    
    public static TestResult failed(final Test test, final String s) {
        return new SimpleTestResult(false, test.getName() + ": " + s);
    }
    
    public static TestResult failed(final Test test, final String s, final Throwable t) {
        return new SimpleTestResult(false, test.getName() + ": " + s, t);
    }
    
    public static TestResult failed(final Test test, final String s, final Object o, final Object o2) {
        return failed(test, s + SimpleTestResult.SEPARATOR + "Expected: " + o + SimpleTestResult.SEPARATOR + "Found   : " + o2);
    }
    
    public static String failedMessage(final String s, final String s2, final String s3, final String s4) {
        final StringBuffer sb = new StringBuffer(s);
        sb.append(" failing ").append(s2);
        sb.append(SimpleTestResult.SEPARATOR).append("    expected: ").append(s3);
        sb.append(SimpleTestResult.SEPARATOR).append("    got     : ").append(s4);
        return sb.toString();
    }
    
    public boolean isSuccessful() {
        return this.success;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
    
    public Throwable getException() {
        return this.exception;
    }
    
    static {
        SEPARATOR = Strings.lineSeparator();
    }
}
