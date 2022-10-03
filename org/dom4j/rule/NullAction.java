package org.dom4j.rule;

import org.dom4j.Node;

public class NullAction implements Action
{
    public static final NullAction SINGLETON;
    
    public void run(final Node node) throws Exception {
    }
    
    static {
        SINGLETON = new NullAction();
    }
}
