package com.adventnet.iam.security;

import java.util.List;
import org.w3c.dom.Element;

public class ResponseRule
{
    private ResponseLogRule responseLogRule;
    
    public ResponseRule(final Element element) {
        this.responseLogRule = null;
        this.initResponseLogRule(element);
    }
    
    private void initResponseLogRule(final Element element) {
        final List<Element> list = RuleSetParser.getChildNodesByTagName(element, "log");
        if (list != null) {
            if (list.size() == 1) {
                final Element logElement = list.get(0);
                this.responseLogRule = new ResponseLogRule(logElement);
            }
            else if (list.size() != 0) {
                throw new RuntimeException("More than one response log rule is not allowed");
            }
        }
    }
    
    public ResponseLogRule getResponseLogRule() {
        return this.responseLogRule;
    }
    
    public void setResponseLogRule(final ResponseLogRule logRule) {
        this.responseLogRule = logRule;
    }
}
