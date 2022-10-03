package org.w3c.css.sac;

public interface SiblingSelector extends Selector
{
    public static final short ANY_NODE = 201;
    
    short getNodeType();
    
    Selector getSelector();
    
    SimpleSelector getSiblingSelector();
}
