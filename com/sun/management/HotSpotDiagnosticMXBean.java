package com.sun.management;

import java.util.List;
import java.io.IOException;
import jdk.Exported;
import java.lang.management.PlatformManagedObject;

@Exported
public interface HotSpotDiagnosticMXBean extends PlatformManagedObject
{
    void dumpHeap(final String p0, final boolean p1) throws IOException;
    
    List<VMOption> getDiagnosticOptions();
    
    VMOption getVMOption(final String p0);
    
    void setVMOption(final String p0, final String p1);
}
