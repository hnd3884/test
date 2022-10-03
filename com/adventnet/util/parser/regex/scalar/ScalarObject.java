package com.adventnet.util.parser.regex.scalar;

import java.util.ArrayList;

public class ScalarObject
{
    private boolean referenceReq;
    private ArrayList regex;
    
    public ScalarObject() {
        this.referenceReq = false;
        this.regex = null;
        this.regex = new ArrayList();
    }
    
    public boolean getReferenceRequired() {
        return this.referenceReq;
    }
    
    public void setReferenceRequired(final boolean referenceReq) {
        this.referenceReq = referenceReq;
    }
    
    public ArrayList getRegExpressions() {
        return this.regex;
    }
    
    public void setRegExpressions(final ArrayList regex) {
        this.regex = regex;
    }
}
