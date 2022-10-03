package org.apache.axiom.om.impl.llom.factory;

import org.apache.axiom.core.NodeFactoryImpl;

public final class LLOMNodeFactory extends NodeFactoryImpl
{
    public static LLOMNodeFactory INSTANCE;
    
    static {
        LLOMNodeFactory.INSTANCE = new LLOMNodeFactory();
    }
    
    private LLOMNodeFactory() {
        super(LLOMNodeFactory.class.getClassLoader(), new String[] { "org.apache.axiom.om.impl.llom", "org.apache.axiom.soap.impl.llom", "org.apache.axiom.soap.impl.llom.soap11", "org.apache.axiom.soap.impl.llom.soap12" });
    }
}
