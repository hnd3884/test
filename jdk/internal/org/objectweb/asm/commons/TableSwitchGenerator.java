package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Label;

public interface TableSwitchGenerator
{
    void generateCase(final int p0, final Label p1);
    
    void generateDefault();
}
