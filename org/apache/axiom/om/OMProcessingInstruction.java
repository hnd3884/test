package org.apache.axiom.om;

public interface OMProcessingInstruction extends OMNode
{
    void setTarget(final String p0);
    
    String getTarget();
    
    void setValue(final String p0);
    
    String getValue();
}
