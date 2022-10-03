package org.antlr.v4.runtime;

public class InterpreterRuleContext extends ParserRuleContext
{
    protected int ruleIndex;
    
    public InterpreterRuleContext() {
        this.ruleIndex = -1;
    }
    
    public InterpreterRuleContext(final ParserRuleContext parent, final int invokingStateNumber, final int ruleIndex) {
        super(parent, invokingStateNumber);
        this.ruleIndex = -1;
        this.ruleIndex = ruleIndex;
    }
    
    @Override
    public int getRuleIndex() {
        return this.ruleIndex;
    }
}
