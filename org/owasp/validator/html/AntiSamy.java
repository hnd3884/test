package org.owasp.validator.html;

import java.io.File;
import java.io.Writer;
import java.io.Reader;
import org.owasp.validator.html.scan.AntiSamySAXScanner;
import org.owasp.validator.html.scan.AntiSamyDOMScanner;

public class AntiSamy
{
    public static final int DOM = 0;
    public static final int SAX = 1;
    private Policy policy;
    
    public AntiSamy() {
        this.policy = null;
    }
    
    public AntiSamy(final Policy policy) {
        this.policy = null;
        this.policy = policy;
    }
    
    public CleanResults scan(final String taintedHTML) throws ScanException, PolicyException {
        if (this.policy == null) {
            throw new PolicyException("No policy loaded");
        }
        return this.scan(taintedHTML, this.policy, 1);
    }
    
    public CleanResults scan(final String taintedHTML, final int scanType) throws ScanException, PolicyException {
        if (this.policy == null) {
            throw new PolicyException("No policy loaded");
        }
        return this.scan(taintedHTML, this.policy, scanType);
    }
    
    public CleanResults scan(final String taintedHTML, final Policy policy) throws ScanException, PolicyException {
        return new AntiSamyDOMScanner(policy).scan(taintedHTML);
    }
    
    public CleanResults scan(final String taintedHTML, final Policy policy, final int scanType) throws ScanException, PolicyException {
        if (scanType == 0) {
            return new AntiSamyDOMScanner(policy).scan(taintedHTML);
        }
        return new AntiSamySAXScanner(policy).scan(taintedHTML);
    }
    
    public CleanResults scan(final Reader reader, final Writer writer, final Policy policy) throws ScanException {
        return new AntiSamySAXScanner(policy).scan(reader, writer);
    }
    
    public CleanResults scan(final String taintedHTML, final String filename) throws ScanException, PolicyException {
        final Policy policy = Policy.getInstance(filename);
        return this.scan(taintedHTML, policy);
    }
    
    public CleanResults scan(final String taintedHTML, final File policyFile) throws ScanException, PolicyException {
        final Policy policy = Policy.getInstance(policyFile);
        return this.scan(taintedHTML, policy);
    }
}
