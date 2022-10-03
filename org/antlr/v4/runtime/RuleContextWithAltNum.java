package org.antlr.v4.runtime;

public class RuleContextWithAltNum extends ParserRuleContext
{
    public int altNum;
    
    public RuleContextWithAltNum() {
        this.altNum = 0;
    }
    
    public RuleContextWithAltNum(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }
    
    @Override
    public int getAltNumber() {
        return this.altNum;
    }
    
    @Override
    public void setAltNumber(final int altNum) {
        this.altNum = altNum;
    }
}
