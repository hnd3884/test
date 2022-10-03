package org.antlr.v4.runtime.atn;

public class ATNDeserializationOptions
{
    private static final ATNDeserializationOptions defaultOptions;
    private boolean readOnly;
    private boolean verifyATN;
    private boolean generateRuleBypassTransitions;
    
    public ATNDeserializationOptions() {
        this.verifyATN = true;
        this.generateRuleBypassTransitions = false;
    }
    
    public ATNDeserializationOptions(final ATNDeserializationOptions options) {
        this.verifyATN = options.verifyATN;
        this.generateRuleBypassTransitions = options.generateRuleBypassTransitions;
    }
    
    public static ATNDeserializationOptions getDefaultOptions() {
        return ATNDeserializationOptions.defaultOptions;
    }
    
    public final boolean isReadOnly() {
        return this.readOnly;
    }
    
    public final void makeReadOnly() {
        this.readOnly = true;
    }
    
    public final boolean isVerifyATN() {
        return this.verifyATN;
    }
    
    public final void setVerifyATN(final boolean verifyATN) {
        this.throwIfReadOnly();
        this.verifyATN = verifyATN;
    }
    
    public final boolean isGenerateRuleBypassTransitions() {
        return this.generateRuleBypassTransitions;
    }
    
    public final void setGenerateRuleBypassTransitions(final boolean generateRuleBypassTransitions) {
        this.throwIfReadOnly();
        this.generateRuleBypassTransitions = generateRuleBypassTransitions;
    }
    
    protected void throwIfReadOnly() {
        if (this.isReadOnly()) {
            throw new IllegalStateException("The object is read only.");
        }
    }
    
    static {
        (defaultOptions = new ATNDeserializationOptions()).makeReadOnly();
    }
}
