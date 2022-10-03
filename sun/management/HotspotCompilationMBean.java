package sun.management;

import sun.management.counter.Counter;
import java.util.List;

public interface HotspotCompilationMBean
{
    int getCompilerThreadCount();
    
    List<CompilerThreadStat> getCompilerThreadStats();
    
    long getTotalCompileCount();
    
    long getBailoutCompileCount();
    
    long getInvalidatedCompileCount();
    
    MethodInfo getLastCompile();
    
    MethodInfo getFailedCompile();
    
    MethodInfo getInvalidatedCompile();
    
    long getCompiledMethodCodeSize();
    
    long getCompiledMethodSize();
    
    List<Counter> getInternalCompilerCounters();
}
