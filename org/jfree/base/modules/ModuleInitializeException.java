package org.jfree.base.modules;

import org.jfree.util.StackableException;

public class ModuleInitializeException extends StackableException
{
    public ModuleInitializeException() {
    }
    
    public ModuleInitializeException(final String s) {
        super(s);
    }
    
    public ModuleInitializeException(final String s, final Exception e) {
        super(s, e);
    }
}
