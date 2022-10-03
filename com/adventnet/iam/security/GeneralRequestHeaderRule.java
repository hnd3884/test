package com.adventnet.iam.security;

import org.w3c.dom.Element;

public class GeneralRequestHeaderRule extends HeaderRule
{
    static final String CONTENTLENGTH = "content-length";
    
    public GeneralRequestHeaderRule() {
    }
    
    GeneralRequestHeaderRule(final Element element) {
        super(element);
    }
}
